package com.example.walletapplication.presentation.service;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Tracks and manages progress for backup operations with real-time updates
 */
public class BackupProgressTracker {
    
    // Progress tracking
    private final AtomicInteger currentProgress = new AtomicInteger(0);
    private final AtomicInteger maxProgress = new AtomicInteger(100);
    private final AtomicLong startTime = new AtomicLong(0);
    private volatile String currentStatus = "";
    private volatile boolean isRunning = false;
    
    // Progress callback
    private ProgressCallback callback;
    private final Handler mainHandler;
    
    // Progress step definitions
    public static class ProgressSteps {
        // Export steps
        public static final int EXPORT_DATABASE_READ = 20;
        public static final int EXPORT_JSON_CONVERSION = 40;
        public static final int EXPORT_FILE_WRITE = 40;
        
        // Import steps  
        public static final int IMPORT_FILE_READ = 20;
        public static final int IMPORT_JSON_PARSING = 20;
        public static final int IMPORT_VALIDATION = 20;
        public static final int IMPORT_DATABASE_WRITE = 40;
    }
    
    /**
     * Callback interface for progress updates
     */
    public interface ProgressCallback {
        void onProgressUpdate(int progress, int max, String status, String eta);
        void onOperationComplete(boolean success, String message);
        void onError(String error);
    }
    
    public BackupProgressTracker() {
        this.mainHandler = new Handler(Looper.getMainLooper());
    }
    
    /**
     * Sets the progress callback
     */
    public void setCallback(ProgressCallback callback) {
        this.callback = callback;
    }
    
    /**
     * Starts tracking a new operation
     */
    public void startOperation(String initialStatus) {
        reset();
        isRunning = true;
        startTime.set(System.currentTimeMillis());
        currentStatus = initialStatus;
        notifyProgressUpdate();
    }
    
    /**
     * Updates progress with absolute value
     */
    public void updateProgress(int progress, String status) {
        if (!isRunning) return;
        
        currentProgress.set(Math.max(0, Math.min(progress, maxProgress.get())));
        currentStatus = status;
        notifyProgressUpdate();
    }
    
    /**
     * Increments progress by specified amount
     */
    public void incrementProgress(int increment, String status) {
        if (!isRunning) return;
        
        int newProgress = currentProgress.addAndGet(increment);
        newProgress = Math.max(0, Math.min(newProgress, maxProgress.get()));
        currentProgress.set(newProgress);
        currentStatus = status;
        notifyProgressUpdate();
    }
    
    /**
     * Updates progress for export database read step
     */
    public void updateExportDatabaseRead(int transactionCount, int categoryCount) {
        String status = String.format("Veritabanından veriler okunuyor... (%d işlem, %d kategori)", 
                transactionCount, categoryCount);
        updateProgress(ProgressSteps.EXPORT_DATABASE_READ, status);
    }
    
    /**
     * Updates progress for export JSON conversion step
     */
    public void updateExportJsonConversion(int processedItems, int totalItems) {
        int progress = ProgressSteps.EXPORT_DATABASE_READ + 
                ((ProgressSteps.EXPORT_JSON_CONVERSION * processedItems) / Math.max(1, totalItems));
        String status = String.format("JSON formatına dönüştürülüyor... (%d/%d)", processedItems, totalItems);
        updateProgress(progress, status);
    }
    
    /**
     * Updates progress for export file write step
     */
    public void updateExportFileWrite(long bytesWritten, long totalBytes) {
        int baseProgress = ProgressSteps.EXPORT_DATABASE_READ + ProgressSteps.EXPORT_JSON_CONVERSION;
        int writeProgress = totalBytes > 0 ? 
                (int) ((ProgressSteps.EXPORT_FILE_WRITE * bytesWritten) / totalBytes) : 0;
        
        String status = String.format("Dosyaya yazılıyor... (%s/%s)", 
                formatBytes(bytesWritten), formatBytes(totalBytes));
        updateProgress(baseProgress + writeProgress, status);
    }
    
    /**
     * Updates progress for import file read step
     */
    public void updateImportFileRead(long bytesRead, long totalBytes) {
        int progress = totalBytes > 0 ? 
                (int) ((ProgressSteps.IMPORT_FILE_READ * bytesRead) / totalBytes) : 0;
        String status = String.format("Dosya okunuyor... (%s/%s)", 
                formatBytes(bytesRead), formatBytes(totalBytes));
        updateProgress(progress, status);
    }
    
    /**
     * Updates progress for import JSON parsing step
     */
    public void updateImportJsonParsing() {
        int progress = ProgressSteps.IMPORT_FILE_READ + ProgressSteps.IMPORT_JSON_PARSING;
        updateProgress(progress, "JSON verisi ayrıştırılıyor...");
    }
    
    /**
     * Updates progress for import validation step
     */
    public void updateImportValidation(int validatedItems, int totalItems) {
        int baseProgress = ProgressSteps.IMPORT_FILE_READ + ProgressSteps.IMPORT_JSON_PARSING;
        int validationProgress = totalItems > 0 ? 
                (int) ((ProgressSteps.IMPORT_VALIDATION * validatedItems) / totalItems) : 0;
        
        String status = String.format("Veriler doğrulanıyor... (%d/%d)", validatedItems, totalItems);
        updateProgress(baseProgress + validationProgress, status);
    }
    
    /**
     * Updates progress for import database write step
     */
    public void updateImportDatabaseWrite(int writtenItems, int totalItems) {
        int baseProgress = ProgressSteps.IMPORT_FILE_READ + ProgressSteps.IMPORT_JSON_PARSING + 
                ProgressSteps.IMPORT_VALIDATION;
        int writeProgress = totalItems > 0 ? 
                (int) ((ProgressSteps.IMPORT_DATABASE_WRITE * writtenItems) / totalItems) : 0;
        
        String status = String.format("Veritabanına yazılıyor... (%d/%d)", writtenItems, totalItems);
        updateProgress(baseProgress + writeProgress, status);
    }
    
    /**
     * Completes the operation
     */
    public void completeOperation(boolean success, String message) {
        if (!isRunning) return;
        
        isRunning = false;
        if (success) {
            currentProgress.set(maxProgress.get());
        }
        
        mainHandler.post(() -> {
            if (callback != null) {
                callback.onOperationComplete(success, message);
            }
        });
    }
    
    /**
     * Reports an error
     */
    public void reportError(String error) {
        if (!isRunning) return;
        
        isRunning = false;
        mainHandler.post(() -> {
            if (callback != null) {
                callback.onError(error);
            }
        });
    }
    
    /**
     * Cancels the current operation
     */
    public void cancelOperation() {
        isRunning = false;
        reset();
    }
    
    /**
     * Resets the progress tracker
     */
    private void reset() {
        currentProgress.set(0);
        maxProgress.set(100);
        startTime.set(0);
        currentStatus = "";
    }
    
    /**
     * Notifies the callback about progress update
     */
    private void notifyProgressUpdate() {
        if (callback == null) return;
        
        final int progress = currentProgress.get();
        final int max = maxProgress.get();
        final String status = currentStatus;
        final String eta = calculateETA();
        
        mainHandler.post(() -> callback.onProgressUpdate(progress, max, status, eta));
    }
    
    /**
     * Calculates estimated time remaining
     */
    private String calculateETA() {
        if (!isRunning || startTime.get() == 0) {
            return "Hesaplanıyor...";
        }
        
        int progress = currentProgress.get();
        if (progress <= 0) {
            return "Hesaplanıyor...";
        }
        
        long elapsedTime = System.currentTimeMillis() - startTime.get();
        long estimatedTotalTime = (elapsedTime * maxProgress.get()) / progress;
        long remainingTime = estimatedTotalTime - elapsedTime;
        
        if (remainingTime <= 0) {
            return "Neredeyse bitti...";
        }
        
        return formatDuration(remainingTime);
    }
    
    /**
     * Formats bytes for display
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }
    
    /**
     * Formats duration for display
     */
    private String formatDuration(long milliseconds) {
        long seconds = milliseconds / 1000;
        
        if (seconds < 60) {
            return seconds + " saniye";
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            long remainingSeconds = seconds % 60;
            return String.format("%d:%02d dakika", minutes, remainingSeconds);
        } else {
            long hours = seconds / 3600;
            long minutes = (seconds % 3600) / 60;
            return String.format("%d:%02d saat", hours, minutes);
        }
    }
    
    // Getters
    public int getCurrentProgress() {
        return currentProgress.get();
    }
    
    public int getMaxProgress() {
        return maxProgress.get();
    }
    
    public String getCurrentStatus() {
        return currentStatus;
    }
    
    public boolean isRunning() {
        return isRunning;
    }
} 