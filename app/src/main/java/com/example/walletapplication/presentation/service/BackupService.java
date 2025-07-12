package com.example.walletapplication.presentation.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.walletapplication.domain.usecase.backup.ExportDataUseCase;
import com.example.walletapplication.domain.usecase.backup.ImportDataUseCase;
import com.example.walletapplication.domain.repository.BackupRepository;
import com.example.walletapplication.domain.entity.BackupData;
import com.example.walletapplication.domain.common.Result;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Foreground service for handling long-running backup operations
 */
@AndroidEntryPoint
public class BackupService extends Service implements BackupProgressTracker.ProgressCallback {
    
    private static final String TAG = "BackupService";
    
    // Service actions
    public static final String ACTION_START_EXPORT = "com.example.walletapplication.START_EXPORT";
    public static final String ACTION_START_IMPORT = "com.example.walletapplication.START_IMPORT";
    public static final String ACTION_START_EXPORT_EXTERNAL = "com.example.walletapplication.START_EXPORT_EXTERNAL";
    public static final String ACTION_START_IMPORT_URI = "com.example.walletapplication.START_IMPORT_URI";
    public static final String ACTION_CANCEL_OPERATION = "com.example.walletapplication.CANCEL_OPERATION";
    
    // Intent extras
    public static final String EXTRA_FILE_PATH = "file_path";
    public static final String EXTRA_CONTENT_URI = "content_uri";
    public static final String EXTRA_REPLACE_EXISTING = "replace_existing";
    
    // Dependencies
    @Inject
    ExportDataUseCase exportDataUseCase;
    
    @Inject
    ImportDataUseCase importDataUseCase;
    
    @Inject
    BackupRepository backupRepository;
    
    // Service components
    private BackupNotificationManager notificationManager;
    private BackupProgressTracker progressTracker;
    private CancelBroadcastReceiver cancelReceiver;
    
    // Service state
    private final AtomicBoolean isOperationRunning = new AtomicBoolean(false);
    private volatile String currentOperation = "";
    private CompletableFuture<Void> currentTask;
    
    // Binder for fragment communication
    private final BackupServiceBinder binder = new BackupServiceBinder();
    
    /**
     * Binder class for fragment communication
     */
    public class BackupServiceBinder extends Binder {
        public BackupService getService() {
            return BackupService.this;
        }
    }
    
    /**
     * Interface for operation callbacks
     */
    public interface OperationCallback {
        void onProgressUpdate(int progress, int max, String status, String eta);
        void onOperationComplete(boolean success, String message);
        void onError(String error);
    }
    
    private OperationCallback operationCallback;
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "BackupService created");
        
        // Initialize components
        notificationManager = new BackupNotificationManager(this);
        progressTracker = new BackupProgressTracker();
        progressTracker.setCallback(this);
        
        // Register cancel broadcast receiver
        cancelReceiver = new CancelBroadcastReceiver();
        IntentFilter filter = new IntentFilter(BackupNotificationManager.ACTION_CANCEL_BACKUP);
        
        // For Android 14+ (API 34+), we need to specify RECEIVER_NOT_EXPORTED
        // since this receiver is only used internally within the app
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            registerReceiver(cancelReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(cancelReceiver, filter);
        }
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_NOT_STICKY;
        }
        
        String action = intent.getAction();
        Log.d(TAG, "Service started with action: " + action);
        
        if (isOperationRunning.get()) {
            Log.w(TAG, "Operation already running, ignoring new request");
            return START_NOT_STICKY;
        }
        
        switch (action != null ? action : "") {
            case ACTION_START_EXPORT:
                startExportOperation();
                break;
                
            case ACTION_START_EXPORT_EXTERNAL:
                startExportExternalOperation();
                break;
                
            case ACTION_START_IMPORT:
                String filePath = intent.getStringExtra(EXTRA_FILE_PATH);
                boolean replaceExisting = intent.getBooleanExtra(EXTRA_REPLACE_EXISTING, false);
                startImportOperation(filePath, replaceExisting);
                break;
                
            case ACTION_START_IMPORT_URI:
                Uri uri = intent.getParcelableExtra(EXTRA_CONTENT_URI);
                boolean replaceExistingUri = intent.getBooleanExtra(EXTRA_REPLACE_EXISTING, false);
                startImportUriOperation(uri, replaceExistingUri);
                break;
                
            case ACTION_CANCEL_OPERATION:
                cancelCurrentOperation();
                break;
                
            default:
                Log.w(TAG, "Unknown action: " + action);
                return START_NOT_STICKY;
        }
        
        return START_NOT_STICKY;
    }
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "Service bound");
        return binder;
    }
    
    @Override
    public void onDestroy() {
        Log.d(TAG, "BackupService destroyed");
        
        // Cancel any running operation
        cancelCurrentOperation();
        
        // Unregister receiver
        if (cancelReceiver != null) {
            unregisterReceiver(cancelReceiver);
        }
        
        // Cancel all notifications
        notificationManager.cancelAllNotifications();
        
        super.onDestroy();
    }
    
    /**
     * Starts export operation to internal storage
     */
    private void startExportOperation() {
        if (!isOperationRunning.compareAndSet(false, true)) {
            return;
        }
        
        currentOperation = "export";
        Log.d(TAG, "Starting export operation");
        
        // Start foreground service with notification
        startForeground(BackupNotificationManager.NOTIFICATION_ID_EXPORT_PROGRESS,
                notificationManager.createExportProgressNotification(0, 100, "Dışa aktarma başlatılıyor..."));
        
        progressTracker.startOperation("Dışa aktarma başlatılıyor...");
        
        currentTask = CompletableFuture.runAsync(() -> {
            try {
                // Step 1: Get data from database
                progressTracker.updateProgress(10, "Veritabanından veriler alınıyor...");
                
                Result<BackupData> result = exportDataUseCase.execute().join();
                if (result.isError()) {
                    throw new RuntimeException(result.getErrorOrNull().getMessage());
                }
                
                BackupData backupData = result.getDataOrNull();
                int transactionCount = backupData.getTransactions().size();
                int categoryCount = backupData.getCategories().size();
                
                progressTracker.updateExportDatabaseRead(transactionCount, categoryCount);

                Thread.sleep(3000);
                
                // Step 2: Convert to JSON
                progressTracker.updateProgress(60, "JSON formatına dönüştürülüyor...");

                Thread.sleep(3000);

                // Step 3: Save to file
                progressTracker.updateProgress(80, "Dosyaya kaydediliyor...");

                Thread.sleep(3000);

                String fileName = backupRepository.getDefaultBackupFileName();
                String filePath = backupRepository.saveBackupToFile(backupData, fileName).join();
                
                progressTracker.updateProgress(100, "Dışa aktarma tamamlandı");
                
                String successMessage = String.format("Yedekleme başarıyla tamamlandı!\n%d işlem ve %d kategori dışa aktarıldı.",
                        transactionCount, categoryCount);
                
                progressTracker.completeOperation(true, successMessage);
                
                // Show success notification
                notificationManager.showExportSuccessNotification(filePath, transactionCount, categoryCount);
                
            } catch (Exception e) {
                Log.e(TAG, "Export operation failed", e);
                progressTracker.reportError("Dışa aktarma başarısız: " + e.getMessage());
                notificationManager.showErrorNotification("Dışa aktarma başarısız", e.getMessage(), "Export");
            } finally {
                isOperationRunning.set(false);
                stopForeground(true);
                stopSelf();
            }
        });
    }
    
    /**
     * Starts export operation to external storage
     */
    private void startExportExternalOperation() {
        if (!isOperationRunning.compareAndSet(false, true)) {
            return;
        }
        
        currentOperation = "export_external";
        Log.d(TAG, "Starting external export operation");
        
        // Start foreground service with notification
        startForeground(BackupNotificationManager.NOTIFICATION_ID_EXPORT_PROGRESS,
                notificationManager.createExportProgressNotification(0, 100, "Harici depolama alanına aktarılıyor..."));
        
        progressTracker.startOperation("Harici depolama alanına aktarılıyor...");
        
        currentTask = CompletableFuture.runAsync(() -> {
            try {
                // Step 1: Get data from database
                progressTracker.updateProgress(10, "Veritabanından veriler alınıyor...");
                
                Result<BackupData> result = exportDataUseCase.execute().join();
                if (result.isError()) {
                    throw new RuntimeException(result.getErrorOrNull().getMessage());
                }
                
                BackupData backupData = result.getDataOrNull();
                int transactionCount = backupData.getTransactions().size();
                int categoryCount = backupData.getCategories().size();
                
                progressTracker.updateExportDatabaseRead(transactionCount, categoryCount);
                
                // Step 2: Convert to JSON
                progressTracker.updateProgress(60, "JSON formatına dönüştürülüyor...");
                
                // Step 3: Save to external storage
                progressTracker.updateProgress(80, "Harici depolama alanına kaydediliyor...");
                
                String fileName = backupRepository.getDefaultBackupFileName();
                String filePath = backupRepository.saveBackupToExternalStorage(backupData, fileName).join();
                
                progressTracker.updateProgress(100, "Harici depolama alanına aktarma tamamlandı");
                
                String successMessage = String.format("Yedekleme başarıyla tamamlandı!\n%d işlem ve %d kategori harici depolama alanına aktarıldı.",
                        transactionCount, categoryCount);
                
                progressTracker.completeOperation(true, successMessage);
                
                // Show success notification
                notificationManager.showExportSuccessNotification(filePath, transactionCount, categoryCount);
                
            } catch (Exception e) {
                Log.e(TAG, "External export operation failed", e);
                progressTracker.reportError("Harici depolama alanına aktarma başarısız: " + e.getMessage());
                notificationManager.showErrorNotification("Harici depolama aktarma başarısız", e.getMessage(), "External Export");
            } finally {
                isOperationRunning.set(false);
                stopForeground(true);
                stopSelf();
            }
        });
    }
    
    /**
     * Starts import operation from file path
     */
    private void startImportOperation(String filePath, boolean replaceExisting) {
        if (!isOperationRunning.compareAndSet(false, true)) {
            return;
        }
        
        currentOperation = "import";
        Log.d(TAG, "Starting import operation from: " + filePath);
        
        // Start foreground service with notification
        startForeground(BackupNotificationManager.NOTIFICATION_ID_IMPORT_PROGRESS,
                notificationManager.createImportProgressNotification(0, 100, "İçe aktarma başlatılıyor..."));
        
        progressTracker.startOperation("İçe aktarma başlatılıyor...");
        
        currentTask = CompletableFuture.runAsync(() -> {
            try {
                performImportOperation(filePath, null, replaceExisting);
            } catch (Exception e) {
                Log.e(TAG, "Import operation failed", e);
                progressTracker.reportError("İçe aktarma başarısız: " + e.getMessage());
                notificationManager.showErrorNotification("İçe aktarma başarısız", e.getMessage(), "Import");
            } finally {
                isOperationRunning.set(false);
                stopForeground(true);
                stopSelf();
            }
        });
    }
    
    /**
     * Starts import operation from content URI
     */
    private void startImportUriOperation(Uri uri, boolean replaceExisting) {
        if (!isOperationRunning.compareAndSet(false, true)) {
            return;
        }
        
        currentOperation = "import_uri";
        Log.d(TAG, "Starting import operation from URI: " + uri);
        
        // Start foreground service with notification
        startForeground(BackupNotificationManager.NOTIFICATION_ID_IMPORT_PROGRESS,
                notificationManager.createImportProgressNotification(0, 100, "İçe aktarma başlatılıyor..."));
        
        progressTracker.startOperation("İçe aktarma başlatılıyor...");
        
        currentTask = CompletableFuture.runAsync(() -> {
            try {
                performImportOperation(null, uri, replaceExisting);
            } catch (Exception e) {
                Log.e(TAG, "Import URI operation failed", e);
                progressTracker.reportError("İçe aktarma başarısız: " + e.getMessage());
                notificationManager.showErrorNotification("İçe aktarma başarısız", e.getMessage(), "Import URI");
            } finally {
                isOperationRunning.set(false);
                stopForeground(true);
                stopSelf();
            }
        });
    }
    
    /**
     * Performs the actual import operation
     */
    private void performImportOperation(String filePath, Uri uri, boolean replaceExisting) throws Exception {
        // Step 1: Validate file/URI
        progressTracker.updateProgress(10, "Dosya doğrulanıyor...");
        
        boolean isValid;
        if (filePath != null) {
            isValid = backupRepository.isValidBackupFile(filePath).join();
        } else {
            isValid = backupRepository.isValidBackupContentUri(uri).join();
        }
        
        if (!isValid) {
            throw new RuntimeException("Geçersiz yedek dosyası formatı");
        }
        
        // Step 2: Load backup data
        progressTracker.updateProgress(30, "Yedek veriler yükleniyor...");
        
        BackupData backupData;
        if (filePath != null) {
            backupData = backupRepository.loadBackupFromFile(filePath).join();
        } else {
            backupData = backupRepository.loadBackupFromContentUri(uri).join();
        }
        
        progressTracker.updateImportJsonParsing();
        
        // Step 3: Validate backup data
        int transactionCount = backupData.getTransactions().size();
        int categoryCount = backupData.getCategories().size();
        
        progressTracker.updateImportValidation(transactionCount, transactionCount + categoryCount);
        
        // Step 4: Import data
        progressTracker.updateProgress(70, "Veriler içe aktarılıyor...");
        
        Result<ImportDataUseCase.ImportSummary> result = importDataUseCase.execute(backupData, replaceExisting).join();
        if (result.isError()) {
            throw new RuntimeException(result.getErrorOrNull().getMessage());
        }
        
        ImportDataUseCase.ImportSummary summary = result.getDataOrNull();
        progressTracker.updateImportDatabaseWrite(summary.getTransactionsImported() + summary.getCategoriesImported(),
                transactionCount + categoryCount);
        
        progressTracker.updateProgress(100, "İçe aktarma tamamlandı");
        
        String successMessage = String.format("İçe aktarma başarıyla tamamlandı!\n%d işlem ve %d kategori içe aktarıldı.",
                summary.getTransactionsImported(), summary.getCategoriesImported());
        
        progressTracker.completeOperation(true, successMessage);
        
        // Show success notification
        notificationManager.showImportSuccessNotification(summary.getTransactionsImported(), summary.getCategoriesImported());
    }
    
    /**
     * Cancels the current operation
     */
    private void cancelCurrentOperation() {
        if (!isOperationRunning.get()) {
            return;
        }
        
        Log.d(TAG, "Cancelling current operation: " + currentOperation);
        
        if (currentTask != null && !currentTask.isDone()) {
            currentTask.cancel(true);
        }
        
        progressTracker.cancelOperation();
        isOperationRunning.set(false);
        
        stopForeground(true);
        stopSelf();
        
        notificationManager.showErrorNotification("İşlem iptal edildi", 
                "Yedekleme işlemi kullanıcı tarafından iptal edildi", currentOperation);
    }
    
    /**
     * Sets operation callback for fragment communication
     */
    public void setOperationCallback(OperationCallback callback) {
        this.operationCallback = callback;
    }
    
    // ProgressCallback implementation
    @Override
    public void onProgressUpdate(int progress, int max, String status, String eta) {
        // Update notification
        if (currentOperation.equals("export") || currentOperation.equals("export_external")) {
            notificationManager.updateProgressNotification(
                    BackupNotificationManager.NOTIFICATION_ID_EXPORT_PROGRESS, progress, max, status);
        } else if (currentOperation.startsWith("import")) {
            notificationManager.updateProgressNotification(
                    BackupNotificationManager.NOTIFICATION_ID_IMPORT_PROGRESS, progress, max, status);
        }
        
        // Notify fragment callback
        if (operationCallback != null) {
            operationCallback.onProgressUpdate(progress, max, status, eta);
        }
    }
    
    @Override
    public void onOperationComplete(boolean success, String message) {
        if (operationCallback != null) {
            operationCallback.onOperationComplete(success, message);
        }
    }
    
    @Override
    public void onError(String error) {
        if (operationCallback != null) {
            operationCallback.onError(error);
        }
    }
    
    /**
     * Broadcast receiver for cancel actions from notification
     */
    private class CancelBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BackupNotificationManager.ACTION_CANCEL_BACKUP.equals(intent.getAction())) {
                Log.d(TAG, "Cancel action received from notification");
                cancelCurrentOperation();
            }
        }
    }
    
    // Public methods for service status
    public boolean isOperationRunning() {
        return isOperationRunning.get();
    }
    
    public String getCurrentOperation() {
        return currentOperation;
    }
    
    public BackupProgressTracker getProgressTracker() {
        return progressTracker;
    }
} 