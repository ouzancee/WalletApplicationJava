package com.example.walletapplication.presentation.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.walletapplication.R;
import com.example.walletapplication.presentation.adapter.BackupFilesAdapter;
import com.example.walletapplication.presentation.base.BaseFragment;
import com.example.walletapplication.presentation.service.BackupService;
import com.example.walletapplication.presentation.service.BackupServiceConnection;
import com.example.walletapplication.presentation.util.PermissionUtil;
import com.example.walletapplication.presentation.viewmodel.BackupViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BackupFragment extends BaseFragment {
    
    private static final String TAG = "BackupFragment";
    
    private BackupViewModel backupViewModel;
    
    // UI Components
    private MaterialButton btnExportInternal;
    private MaterialButton btnExportExternal;
    private MaterialButton btnSelectFile;
    private CheckBox checkboxReplaceExisting;
    private LinearLayout exportProgressLayout;
    private LinearLayout importProgressLayout;
    private LinearLayout messageLayout;
    private TextView textViewMessage;
    private RecyclerView recyclerViewBackupFiles;
    private TextView textViewNoBackupFiles;
    
    // Adapter
    private BackupFilesAdapter backupFilesAdapter;
    
    // File picker
    private ActivityResultLauncher<Intent> filePickerLauncher;
    
    // Permission manager
    private PermissionUtil.PermissionManager permissionManager;
    
    // Service connection
    private BackupServiceConnection serviceConnection;
    
    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_backup;
    }
    
    @Override
    protected void initializeViews(View view) {
        backupViewModel = new ViewModelProvider(this).get(BackupViewModel.class);
        
        // Initialize UI components
        initializeViewComponents(view);
        setupRecyclerView();
        setupFilePickerLauncher();
        setupPermissionManager();
        setupServiceConnection();
        setupClickListeners();
        
        // Set title
        setTitle(getString(R.string.backup_title));
        
        Log.d(TAG, "BackupFragment initialized");
    }
    
    private void initializeViewComponents(View view) {
        btnExportInternal = view.findViewById(R.id.btnExportInternal);
        btnExportExternal = view.findViewById(R.id.btnExportExternal);
        btnSelectFile = view.findViewById(R.id.btnSelectFile);
        checkboxReplaceExisting = view.findViewById(R.id.checkboxReplaceExisting);
        exportProgressLayout = view.findViewById(R.id.exportProgressLayout);
        importProgressLayout = view.findViewById(R.id.importProgressLayout);
        messageLayout = view.findViewById(R.id.messageLayout);
        textViewMessage = view.findViewById(R.id.textViewMessage);
        recyclerViewBackupFiles = view.findViewById(R.id.recyclerViewBackupFiles);
        textViewNoBackupFiles = view.findViewById(R.id.textViewNoBackupFiles);
    }
    
    private void setupRecyclerView() {
        backupFilesAdapter = new BackupFilesAdapter(new ArrayList<>(), this::onBackupFileSelected);
        recyclerViewBackupFiles.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewBackupFiles.setAdapter(backupFilesAdapter);
    }
    
    private void setupFilePickerLauncher() {
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            Log.d(TAG, "File picker returned URI: " + uri.toString());
                            boolean replaceExisting = checkboxReplaceExisting.isChecked();
                            startImportFromUri(uri, replaceExisting);
                        }
                    }
                }
        );
    }
    
    private void setupPermissionManager() {
        permissionManager = new PermissionUtil.PermissionManager(this);
    }
    
    private void setupServiceConnection() {
        serviceConnection = new BackupServiceConnection(requireContext());
        
        // Set up service callbacks
        serviceConnection.setCallback(new BackupServiceConnection.ServiceConnectionCallback() {
            @Override
            public void onServiceConnected(BackupService service) {
                Log.d(TAG, "Service connected successfully");
                // Service is now available for operations
            }
            
            @Override
            public void onServiceDisconnected() {
                Log.d(TAG, "Service disconnected");
                // Reset UI state if needed
                updateExportUI(false);
                updateImportUI(false);
            }
            
            @Override
            public void onProgressUpdate(int progress, int max, String status, String eta) {
                Log.d(TAG, "Progress update: " + progress + "/" + max + " - " + status);
                // Progress updates are now handled by notifications
                // but we can also update UI here if needed
            }
            
            @Override
            public void onOperationComplete(boolean success, String message) {
                Log.d(TAG, "Operation complete: " + success + " - " + message);
                
                // Update UI
                updateExportUI(false);
                updateImportUI(false);
                
                // Show completion message
                showMessage(message, !success);
                
                // Refresh backup files list
                if (success && backupViewModel != null) {
                    backupViewModel.loadAvailableBackupFiles();
                }
            }
            
            @Override
            public void onError(String error) {
                Log.e(TAG, "Operation error: " + error);
                
                // Update UI
                updateExportUI(false);
                updateImportUI(false);
                
                // Show error message
                showMessage(error, true);
            }
        });
        
        // Register for lifecycle events
        getLifecycle().addObserver(serviceConnection);
    }
    
    private void setupClickListeners() {
        btnExportInternal.setOnClickListener(v -> {
            Log.d(TAG, "Internal export button clicked");
            hideAllMessages();
            startInternalExport();
        });
        
        btnExportExternal.setOnClickListener(v -> {
            Log.d(TAG, "External export button clicked");
            hideAllMessages();
            requestStoragePermissionAndExport();
        });
        
        btnSelectFile.setOnClickListener(v -> {
            Log.d(TAG, "Select file button clicked");
            hideAllMessages();
            openFilePicker();
        });
    }
    
    /**
     * Starts internal export operation via service
     */
    private void startInternalExport() {
        if (serviceConnection == null || !serviceConnection.isServiceBound()) {
            showMessage("Servis bağlantısı kurulamadı. Lütfen tekrar deneyin.", true);
            return;
        }
        
        if (serviceConnection.isOperationRunning()) {
            showMessage("Başka bir işlem devam ediyor. Lütfen bekleyin.", true);
            return;
        }
        
        Log.d(TAG, "Starting internal export via service");
        updateExportUI(true);
        serviceConnection.startExportOperation();
    }
    
    /**
     * Starts external export operation via service
     */
    private void startExternalExport() {
        if (serviceConnection == null || !serviceConnection.isServiceBound()) {
            showMessage("Servis bağlantısı kurulamadı. Lütfen tekrar deneyin.", true);
            return;
        }
        
        if (serviceConnection.isOperationRunning()) {
            showMessage("Başka bir işlem devam ediyor. Lütfen bekleyin.", true);
            return;
        }
        
        Log.d(TAG, "Starting external export via service");
        updateExportUI(true);
        serviceConnection.startExportExternalOperation();
    }
    
    /**
     * Starts import operation via service
     */
    private void startImportFromUri(Uri uri, boolean replaceExisting) {
        if (serviceConnection == null || !serviceConnection.isServiceBound()) {
            showMessage("Servis bağlantısı kurulamadı. Lütfen tekrar deneyin.", true);
            return;
        }
        
        if (serviceConnection.isOperationRunning()) {
            showMessage("Başka bir işlem devam ediyor. Lütfen bekleyin.", true);
            return;
        }
        
        Log.d(TAG, "Starting import from URI via service: " + uri);
        updateImportUI(true);
        serviceConnection.startImportUriOperation(uri, replaceExisting);
    }
    
    /**
     * Starts import operation from file path via service
     */
    private void startImportFromFilePath(String filePath, boolean replaceExisting) {
        if (serviceConnection == null || !serviceConnection.isServiceBound()) {
            showMessage("Servis bağlantısı kurulamadı. Lütfen tekrar deneyin.", true);
            return;
        }
        
        if (serviceConnection.isOperationRunning()) {
            showMessage("Başka bir işlem devam ediyor. Lütfen bekleyin.", true);
            return;
        }
        
        Log.d(TAG, "Starting import from file path via service: " + filePath);
        updateImportUI(true);
        serviceConnection.startImportOperation(filePath, replaceExisting);
    }
    
    /**
     * Requests storage permissions and exports data to external storage
     */
    private void requestStoragePermissionAndExport() {
        permissionManager.requestStoragePermissions(new PermissionUtil.PermissionCallback() {
            @Override
            public void onPermissionsGranted() {
                Log.d(TAG, "Storage permissions granted, proceeding with export");
                // Permissions granted or not needed, proceed with export
                startExternalExport();
            }
            
            @Override
            public void onPermissionsDenied(List<String> deniedPermissions) {
                Log.d(TAG, "Storage permissions denied: " + deniedPermissions);
                // Show user-friendly error message
                String permissionsText = PermissionUtil.getPermissionsDescription(
                        deniedPermissions.toArray(new String[0])
                );
                showMessage("Gerekli izinler verilmedi: " + permissionsText, true);
            }
            
            @Override
            public void onPermissionsPermanentlyDenied(List<String> permanentlyDeniedPermissions) {
                Log.d(TAG, "Storage permissions permanently denied: " + permanentlyDeniedPermissions);
                // Show message about going to settings
                String permissionsText = PermissionUtil.getPermissionsDescription(
                        permanentlyDeniedPermissions.toArray(new String[0])
                );
                showMessage("İzinler kalıcı olarak reddedildi: " + permissionsText + 
                           ". Lütfen ayarlardan izinleri etkinleştirin.", true);
            }
        });
    }
    
    @Override
    protected void observeViewModel() {
        // Only observe backup files list from ViewModel
        // Export/Import operations are now handled by service
        backupViewModel.availableBackupFiles.observe(getViewLifecycleOwner(), files -> {
            Log.d(TAG, "Available backup files updated: " + (files != null ? files.size() : 0) + " files");
            updateBackupFilesList(files);
        });
    }
    
    private void updateExportUI(boolean isExporting) {
        exportProgressLayout.setVisibility(isExporting ? View.VISIBLE : View.GONE);
        btnExportInternal.setEnabled(!isExporting);
        btnExportExternal.setEnabled(!isExporting);
        
        // Ensure import is not affected
        if (!isExporting) {
            btnSelectFile.setEnabled(true);
        }
    }
    
    private void updateImportUI(boolean isImporting) {
        importProgressLayout.setVisibility(isImporting ? View.VISIBLE : View.GONE);
        btnSelectFile.setEnabled(!isImporting);
        
        // Ensure export is not affected
        if (!isImporting) {
            btnExportInternal.setEnabled(true);
            btnExportExternal.setEnabled(true);
        }
    }
    
    private void updateBackupFilesList(List<String> files) {
        if (files != null && !files.isEmpty()) {
            backupFilesAdapter.updateFiles(files);
            recyclerViewBackupFiles.setVisibility(View.VISIBLE);
            textViewNoBackupFiles.setVisibility(View.GONE);
        } else {
            recyclerViewBackupFiles.setVisibility(View.GONE);
            textViewNoBackupFiles.setVisibility(View.VISIBLE);
        }
    }
    
    private void hideAllMessages() {
        messageLayout.setVisibility(View.GONE);
    }
    
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/json");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        
        // Add extra MIME types to support various file providers
        String[] mimeTypes = {"application/json", "text/plain", "*/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        
        try {
            filePickerLauncher.launch(Intent.createChooser(intent, getString(R.string.backup_select_file)));
        } catch (Exception e) {
            Log.e(TAG, "Failed to open file picker", e);
            Toast.makeText(getContext(), "Dosya seçici açılamadı", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void onBackupFileSelected(String filePath) {
        Log.d(TAG, "Backup file selected: " + filePath);
        hideAllMessages();
        boolean replaceExisting = checkboxReplaceExisting.isChecked();
        startImportFromFilePath(filePath, replaceExisting);
    }
    
    private void showMessage(String message, boolean isError) {
        if (getContext() == null) return;
        
        Log.d(TAG, "Showing message: " + message + " (isError: " + isError + ")");
        
        textViewMessage.setText(message);
        textViewMessage.setTextColor(getResources().getColor(
                isError ? android.R.color.holo_red_dark : android.R.color.holo_green_dark,
                getContext().getTheme()
        ));
        messageLayout.setVisibility(View.VISIBLE);
        
        // Auto-hide message after 5 seconds
        textViewMessage.postDelayed(() -> {
            if (messageLayout != null) {
                messageLayout.setVisibility(View.GONE);
            }
        }, 5000);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Fragment resumed, refreshing backup files");
        // Refresh backup files when returning to fragment
        if (backupViewModel != null) {
            backupViewModel.loadAvailableBackupFiles();
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "Fragment paused");
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Fragment destroyed");
    }
} 