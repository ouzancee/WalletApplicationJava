package com.example.walletapplication.domain.repository;

import android.net.Uri;
import com.example.walletapplication.domain.entity.BackupData;

import java.util.concurrent.CompletableFuture;

/**
 * Repository interface for backup and restore operations
 */
public interface BackupRepository {
    
    /**
     * Saves backup data to a file
     * @param backupData The backup data to save
     * @param fileName The name of the backup file
     * @return CompletableFuture with the path of the saved file
     */
    CompletableFuture<String> saveBackupToFile(BackupData backupData, String fileName);
    
    /**
     * Loads backup data from a file
     * @param filePath The path of the backup file
     * @return CompletableFuture with the loaded backup data
     */
    CompletableFuture<BackupData> loadBackupFromFile(String filePath);
    
    /**
     * Loads backup data from a content URI
     * @param uri The content URI of the backup file
     * @return CompletableFuture with the loaded backup data
     */
    CompletableFuture<BackupData> loadBackupFromContentUri(Uri uri);
    
    /**
     * Saves backup data to external storage (Downloads folder)
     * @param backupData The backup data to save
     * @param fileName The name of the backup file
     * @return CompletableFuture with the path of the saved file
     */
    CompletableFuture<String> saveBackupToExternalStorage(BackupData backupData, String fileName);
    
    /**
     * Gets the default backup file name with timestamp
     * @return A default file name for backup
     */
    String getDefaultBackupFileName();
    
    /**
     * Validates if a file is a valid backup file
     * @param filePath The path of the file to validate
     * @return CompletableFuture with validation result
     */
    CompletableFuture<Boolean> isValidBackupFile(String filePath);
    
    /**
     * Validates if a content URI contains a valid backup file
     * @param uri The content URI to validate
     * @return CompletableFuture with validation result
     */
    CompletableFuture<Boolean> isValidBackupContentUri(Uri uri);
    
    /**
     * Gets available backup files from the default backup directory
     * @return CompletableFuture with list of backup file paths
     */
    CompletableFuture<java.util.List<String>> getAvailableBackupFiles();
} 