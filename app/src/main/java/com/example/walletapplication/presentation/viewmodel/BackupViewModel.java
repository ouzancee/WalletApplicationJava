package com.example.walletapplication.presentation.viewmodel;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.walletapplication.R;
import com.example.walletapplication.domain.common.AppError;
import com.example.walletapplication.domain.common.Result;
import com.example.walletapplication.domain.entity.BackupData;
import com.example.walletapplication.domain.repository.BackupRepository;
import com.example.walletapplication.domain.usecase.backup.ExportDataUseCase;
import com.example.walletapplication.domain.usecase.backup.ImportDataUseCase;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;

/**
 * ViewModel for backup and restore operations
 */
@HiltViewModel
public class BackupViewModel extends ViewModel {
    
    private final ExportDataUseCase exportDataUseCase;
    private final ImportDataUseCase importDataUseCase;
    private final BackupRepository backupRepository;
    private final Context context;
    
    // UI State
    private final MutableLiveData<BackupUiState> _uiState = new MutableLiveData<>(new BackupUiState());
    public final LiveData<BackupUiState> uiState = _uiState;
    
    // Export state
    private final MutableLiveData<Boolean> _isExporting = new MutableLiveData<>(false);
    public final LiveData<Boolean> isExporting = _isExporting;
    
    // Import state
    private final MutableLiveData<Boolean> _isImporting = new MutableLiveData<>(false);
    public final LiveData<Boolean> isImporting = _isImporting;
    
    // Messages
    private final MutableLiveData<String> _message = new MutableLiveData<>();
    public final LiveData<String> message = _message;
    
    // Error messages
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public final LiveData<String> errorMessage = _errorMessage;
    
    // Available backup files
    private final MutableLiveData<List<String>> _availableBackupFiles = new MutableLiveData<>();
    public final LiveData<List<String>> availableBackupFiles = _availableBackupFiles;
    
    @Inject
    public BackupViewModel(ExportDataUseCase exportDataUseCase,
                          ImportDataUseCase importDataUseCase,
                          BackupRepository backupRepository,
                          @ApplicationContext Context context) {
        this.exportDataUseCase = exportDataUseCase;
        this.importDataUseCase = importDataUseCase;
        this.backupRepository = backupRepository;
        this.context = context;
        
        loadAvailableBackupFiles();
    }
    
    /**
     * Exports all data to a backup file
     */
    public void exportData() {
        _isExporting.setValue(true);
        _errorMessage.setValue(null);
        _message.setValue(null);
        
        exportDataUseCase.execute()
                .thenCompose(result -> {
                    if (result.isSuccess()) {
                        BackupData backupData = result.getDataOrNull();
                        String fileName = backupRepository.getDefaultBackupFileName();
                        return backupRepository.saveBackupToFile(backupData, fileName)
                                .thenApply(filePath -> Result.success(filePath));
                    } else {
                        return java.util.concurrent.CompletableFuture.completedFuture(
                                Result.error(result.getErrorOrNull())
                        );
                    }
                })
                .thenAccept(result -> {
                    // Use postValue for background thread updates
                    _isExporting.postValue(false);
                    if (result.isSuccess()) {
                        _message.postValue(context.getString(R.string.backup_export_success));
                        loadAvailableBackupFiles(); // Refresh the list
                    } else {
                        _errorMessage.postValue(getErrorMessage(result.getErrorOrNull()));
                    }
                })
                .exceptionally(throwable -> {
                    // Use postValue for background thread updates
                    _isExporting.postValue(false);
                    _errorMessage.postValue(context.getString(R.string.backup_export_failed));
                    return null;
                });
    }
    
    /**
     * Exports data to external storage (Downloads folder)
     */
    public void exportDataToExternalStorage() {
        _isExporting.setValue(true);
        _errorMessage.setValue(null);
        _message.setValue(null);
        
        exportDataUseCase.execute()
                .thenCompose(result -> {
                    if (result.isSuccess()) {
                        BackupData backupData = result.getDataOrNull();
                        String fileName = backupRepository.getDefaultBackupFileName();
                        return backupRepository.saveBackupToExternalStorage(backupData, fileName)
                                .thenApply(filePath -> Result.success(filePath));
                    } else {
                        return java.util.concurrent.CompletableFuture.completedFuture(
                                Result.error(result.getErrorOrNull())
                        );
                    }
                })
                .thenAccept(result -> {
                    // Use postValue for background thread updates
                    _isExporting.postValue(false);
                    if (result.isSuccess()) {
                        _message.postValue(context.getString(R.string.backup_file_saved) + ": " + result.getDataOrNull());
                    } else {
                        _errorMessage.postValue(getErrorMessage(result.getErrorOrNull()));
                    }
                })
                .exceptionally(throwable -> {
                    // Use postValue for background thread updates
                    _isExporting.postValue(false);
                    _errorMessage.postValue(context.getString(R.string.backup_export_failed) + ": " + throwable.getMessage());
                    return null;
                });
    }
    
    /**
     * Imports data from a backup file
     * @param filePath The path of the backup file
     * @param replaceExisting Whether to replace existing data
     */
    public void importData(String filePath, boolean replaceExisting) {
        _isImporting.setValue(true);
        _errorMessage.setValue(null);
        _message.setValue(null);
        
        // First validate the backup file
        backupRepository.isValidBackupFile(filePath)
                .thenCompose(isValid -> {
                    if (!isValid) {
                        return java.util.concurrent.CompletableFuture.completedFuture(
                                Result.error(AppError.validation("file", context.getString(R.string.error_backup_invalid_format)))
                        );
                    }
                    
                    // Load backup data
                    return backupRepository.loadBackupFromFile(filePath)
                            .thenCompose(backupData -> 
                                    importDataUseCase.execute(backupData, replaceExisting)
                                            .thenApply(importResult -> {
                                                if (importResult.isSuccess()) {
                                                    return Result.success(importResult.getDataOrNull());
                                                } else {
                                                    return Result.error(importResult.getErrorOrNull());
                                                }
                                            })
                            );
                })
                .thenAccept(result -> {
                    // Use postValue for background thread updates
                    _isImporting.postValue(false);
                    if (result.isSuccess()) {
                        ImportDataUseCase.ImportSummary summary = (ImportDataUseCase.ImportSummary) result.getDataOrNull();
                        String message = context.getString(R.string.backup_import_success) + 
                                "\n" + summary.getTransactionsImported() + " işlem, " + 
                                summary.getCategoriesImported() + " kategori içe aktarıldı";
                        _message.postValue(message);
                    } else {
                        _errorMessage.postValue(getErrorMessage(result.getErrorOrNull()));
                    }
                })
                .exceptionally(throwable -> {
                    // Use postValue for background thread updates
                    _isImporting.postValue(false);
                    _errorMessage.postValue(context.getString(R.string.backup_import_failed) + ": " + throwable.getMessage());
                    return null;
                });
    }
    
    /**
     * Imports data from a content URI (for file picker selections)
     * @param uri The content URI of the backup file
     * @param replaceExisting Whether to replace existing data
     */
    public void importDataFromContentUri(Uri uri, boolean replaceExisting) {
        _isImporting.setValue(true);
        _errorMessage.setValue(null);
        _message.setValue(null);
        
        // First validate the backup content URI
        backupRepository.isValidBackupContentUri(uri)
                .thenCompose(isValid -> {
                    if (!isValid) {
                        return java.util.concurrent.CompletableFuture.completedFuture(
                                Result.error(AppError.validation("file", context.getString(R.string.error_backup_invalid_format)))
                        );
                    }
                    
                    // Load backup data from content URI
                    return backupRepository.loadBackupFromContentUri(uri)
                            .thenCompose(backupData -> 
                                    importDataUseCase.execute(backupData, replaceExisting)
                                            .thenApply(importResult -> {
                                                if (importResult.isSuccess()) {
                                                    return Result.success(importResult.getDataOrNull());
                                                } else {
                                                    return Result.error(importResult.getErrorOrNull());
                                                }
                                            })
                            );
                })
                .thenAccept(result -> {
                    // Use postValue for background thread updates
                    _isImporting.postValue(false);
                    if (result.isSuccess()) {
                        ImportDataUseCase.ImportSummary summary = (ImportDataUseCase.ImportSummary) result.getDataOrNull();
                        String message = context.getString(R.string.backup_import_success) + 
                                "\n" + summary.getTransactionsImported() + " işlem, " + 
                                summary.getCategoriesImported() + " kategori içe aktarıldı";
                        _message.postValue(message);
                    } else {
                        _errorMessage.postValue(getErrorMessage(result.getErrorOrNull()));
                    }
                })
                .exceptionally(throwable -> {
                    // Use postValue for background thread updates
                    _isImporting.postValue(false);
                    _errorMessage.postValue(context.getString(R.string.backup_import_failed) + ": " + throwable.getMessage());
                    return null;
                });
    }
    
    /**
     * Loads available backup files
     */
    public void loadAvailableBackupFiles() {
        backupRepository.getAvailableBackupFiles()
                .thenAccept(files -> _availableBackupFiles.postValue(files))
                .exceptionally(throwable -> {
                    _availableBackupFiles.postValue(java.util.Collections.emptyList());
                    return null;
                });
    }
    
    /**
     * Clears messages
     */
    public void clearMessages() {
        _message.setValue(null);
        _errorMessage.setValue(null);
    }
    
    private String getErrorMessage(AppError error) {
        if (error == null) {
            return context.getString(R.string.error_unknown);
        }
        
        String userMessage = error.getUserMessage();
        if (userMessage != null && !userMessage.isEmpty()) {
            return userMessage;
        }
        
        return context.getString(R.string.error_unknown);
    }
    
    /**
     * UI State class for backup operations
     */
    public static class BackupUiState {
        private boolean isLoading = false;
        private boolean hasError = false;
        private String errorMessage = null;
        private String successMessage = null;
        
        public BackupUiState() {}
        
        public BackupUiState(boolean isLoading, boolean hasError, String errorMessage, String successMessage) {
            this.isLoading = isLoading;
            this.hasError = hasError;
            this.errorMessage = errorMessage;
            this.successMessage = successMessage;
        }
        
        public boolean isLoading() {
            return isLoading;
        }
        
        public boolean hasError() {
            return hasError;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
        
        public String getSuccessMessage() {
            return successMessage;
        }
        
        public BackupUiState withLoading(boolean loading) {
            return new BackupUiState(loading, this.hasError, this.errorMessage, this.successMessage);
        }
        
        public BackupUiState withError(String error) {
            return new BackupUiState(this.isLoading, error != null, error, this.successMessage);
        }
        
        public BackupUiState withSuccess(String success) {
            return new BackupUiState(this.isLoading, this.hasError, this.errorMessage, success);
        }
    }
} 