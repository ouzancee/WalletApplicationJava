package com.example.walletapplication.presentation.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.DefaultLifecycleObserver;

/**
 * Manages connection and communication between Fragment and BackupService
 */
public class BackupServiceConnection implements ServiceConnection, DefaultLifecycleObserver {
    
    private static final String TAG = "BackupServiceConnection";
    
    private final Context context;
    private BackupService backupService;
    private boolean isServiceBound = false;
    private ServiceConnectionCallback connectionCallback;
    
    /**
     * Callback interface for service connection events
     */
    public interface ServiceConnectionCallback {
        void onServiceConnected(BackupService service);
        void onServiceDisconnected();
        void onProgressUpdate(int progress, int max, String status, String eta);
        void onOperationComplete(boolean success, String message);
        void onError(String error);
    }
    
    public BackupServiceConnection(Context context) {
        this.context = context;
    }
    
    /**
     * Sets callback for service connection events
     */
    public void setCallback(ServiceConnectionCallback callback) {
        this.connectionCallback = callback;
    }
    
    /**
     * Binds to the backup service
     */
    public void bindService() {
        if (isServiceBound) {
            Log.d(TAG, "Service already bound");
            return;
        }
        
        Intent intent = new Intent(context, BackupService.class);
        boolean bindResult = context.bindService(intent, this, Context.BIND_AUTO_CREATE);
        
        Log.d(TAG, "Attempting to bind service: " + bindResult);
        
        if (!bindResult) {
            Log.e(TAG, "Failed to bind to BackupService");
        }
    }
    
    /**
     * Unbinds from the backup service
     */
    public void unbindService() {
        if (!isServiceBound) {
            Log.d(TAG, "Service not bound");
            return;
        }
        
        try {
            if (backupService != null) {
                backupService.setOperationCallback(null);
            }
            
            context.unbindService(this);
            isServiceBound = false;
            backupService = null;
            
            Log.d(TAG, "Service unbound successfully");
            
            if (connectionCallback != null) {
                connectionCallback.onServiceDisconnected();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error unbinding service", e);
        }
    }
    
    /**
     * Starts export operation to internal storage
     */
    public void startExportOperation() {
        if (!isServiceBound || backupService == null) {
            Log.w(TAG, "Service not bound, cannot start export operation");
            return;
        }
        
        if (backupService.isOperationRunning()) {
            Log.w(TAG, "Operation already running");
            return;
        }
        
        Intent intent = new Intent(context, BackupService.class);
        intent.setAction(BackupService.ACTION_START_EXPORT);
        context.startService(intent);
        
        Log.d(TAG, "Export operation started");
    }
    
    /**
     * Starts export operation to external storage
     */
    public void startExportExternalOperation() {
        if (!isServiceBound || backupService == null) {
            Log.w(TAG, "Service not bound, cannot start external export operation");
            return;
        }
        
        if (backupService.isOperationRunning()) {
            Log.w(TAG, "Operation already running");
            return;
        }
        
        Intent intent = new Intent(context, BackupService.class);
        intent.setAction(BackupService.ACTION_START_EXPORT_EXTERNAL);
        context.startService(intent);
        
        Log.d(TAG, "External export operation started");
    }
    
    /**
     * Starts import operation from file path
     */
    public void startImportOperation(String filePath, boolean replaceExisting) {
        if (!isServiceBound || backupService == null) {
            Log.w(TAG, "Service not bound, cannot start import operation");
            return;
        }
        
        if (backupService.isOperationRunning()) {
            Log.w(TAG, "Operation already running");
            return;
        }
        
        Intent intent = new Intent(context, BackupService.class);
        intent.setAction(BackupService.ACTION_START_IMPORT);
        intent.putExtra(BackupService.EXTRA_FILE_PATH, filePath);
        intent.putExtra(BackupService.EXTRA_REPLACE_EXISTING, replaceExisting);
        context.startService(intent);
        
        Log.d(TAG, "Import operation started from file: " + filePath);
    }
    
    /**
     * Starts import operation from content URI
     */
    public void startImportUriOperation(Uri uri, boolean replaceExisting) {
        if (!isServiceBound || backupService == null) {
            Log.w(TAG, "Service not bound, cannot start import URI operation");
            return;
        }
        
        if (backupService.isOperationRunning()) {
            Log.w(TAG, "Operation already running");
            return;
        }
        
        Intent intent = new Intent(context, BackupService.class);
        intent.setAction(BackupService.ACTION_START_IMPORT_URI);
        intent.putExtra(BackupService.EXTRA_CONTENT_URI, uri);
        intent.putExtra(BackupService.EXTRA_REPLACE_EXISTING, replaceExisting);
        context.startService(intent);
        
        Log.d(TAG, "Import URI operation started from: " + uri);
    }
    
    /**
     * Cancels the current operation
     */
    public void cancelCurrentOperation() {
        if (!isServiceBound || backupService == null) {
            Log.w(TAG, "Service not bound, cannot cancel operation");
            return;
        }
        
        Intent intent = new Intent(context, BackupService.class);
        intent.setAction(BackupService.ACTION_CANCEL_OPERATION);
        context.startService(intent);
        
        Log.d(TAG, "Cancel operation requested");
    }
    
    /**
     * Checks if an operation is currently running
     */
    public boolean isOperationRunning() {
        return isServiceBound && backupService != null && backupService.isOperationRunning();
    }
    
    /**
     * Gets the current operation type
     */
    public String getCurrentOperation() {
        if (isServiceBound && backupService != null) {
            return backupService.getCurrentOperation();
        }
        return "";
    }
    
    /**
     * Gets the current progress tracker
     */
    public BackupProgressTracker getProgressTracker() {
        if (isServiceBound && backupService != null) {
            return backupService.getProgressTracker();
        }
        return null;
    }
    
    // ServiceConnection implementation
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG, "Service connected: " + name.getClassName());
        
        BackupService.BackupServiceBinder binder = (BackupService.BackupServiceBinder) service;
        backupService = binder.getService();
        isServiceBound = true;
        
        // Set up operation callback
        backupService.setOperationCallback(new BackupService.OperationCallback() {
            @Override
            public void onProgressUpdate(int progress, int max, String status, String eta) {
                if (connectionCallback != null) {
                    connectionCallback.onProgressUpdate(progress, max, status, eta);
                }
            }
            
            @Override
            public void onOperationComplete(boolean success, String message) {
                if (connectionCallback != null) {
                    connectionCallback.onOperationComplete(success, message);
                }
            }
            
            @Override
            public void onError(String error) {
                if (connectionCallback != null) {
                    connectionCallback.onError(error);
                }
            }
        });
        
        if (connectionCallback != null) {
            connectionCallback.onServiceConnected(backupService);
        }
    }
    
    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "Service disconnected: " + name.getClassName());
        
        isServiceBound = false;
        backupService = null;
        
        if (connectionCallback != null) {
            connectionCallback.onServiceDisconnected();
        }
    }
    
    // Lifecycle observer methods for automatic binding/unbinding
    @Override
    public void onStart(LifecycleOwner owner) {
        Log.d(TAG, "Lifecycle: onStart - binding service");
        bindService();
    }
    
    @Override
    public void onStop(LifecycleOwner owner) {
        Log.d(TAG, "Lifecycle: onStop - unbinding service");
        unbindService();
    }
    
    // Public getters
    public boolean isServiceBound() {
        return isServiceBound;
    }
    
    public BackupService getBackupService() {
        return backupService;
    }
} 