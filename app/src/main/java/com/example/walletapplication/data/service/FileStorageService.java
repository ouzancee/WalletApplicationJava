package com.example.walletapplication.data.service;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import dagger.hilt.android.qualifiers.ApplicationContext;

/**
 * Service for handling file storage operations
 */
public class FileStorageService {
    private static final String TAG = "FileStorageService";
    private static final String BACKUP_DIRECTORY = "WalletBackups";
    private static final String BACKUP_FILE_EXTENSION = ".json";
    private static final String MIME_TYPE = "application/json";
    
    private final Context context;
    private final Executor executor;
    
    public FileStorageService(@ApplicationContext Context context, Executor executor) {
        this.context = context;
        this.executor = executor;
    }
    
    /**
     * Saves content to internal storage
     * @param content The content to save
     * @param fileName The name of the file
     * @return CompletableFuture with the path of the saved file
     */
    public CompletableFuture<String> saveToInternalStorage(String content, String fileName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                File backupDir = new File(context.getFilesDir(), BACKUP_DIRECTORY);
                if (!backupDir.exists()) {
                    if (!backupDir.mkdirs()) {
                        throw new RuntimeException("Failed to create backup directory");
                    }
                }
                
                File file = new File(backupDir, fileName);
                
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(content.getBytes(StandardCharsets.UTF_8));
                    fos.flush();
                }
                
                Log.d(TAG, "File saved to internal storage: " + file.getAbsolutePath());
                return file.getAbsolutePath();
                
            } catch (IOException e) {
                Log.e(TAG, "Failed to save file to internal storage", e);
                throw new RuntimeException("Failed to save file to internal storage: " + e.getMessage(), e);
            }
        }, executor);
    }
    
    /**
     * Saves content to external storage (Downloads folder)
     * Uses MediaStore API for Android 10+ (API 29+) and legacy approach for older versions
     * @param content The content to save
     * @param fileName The name of the file
     * @return CompletableFuture with the path of the saved file
     */
    public CompletableFuture<String> saveToExternalStorage(String content, String fileName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Android 10+ (API 29+) - Use MediaStore API
                    return saveToExternalStorageScoped(content, fileName);
                } else {
                    // Android 9 and below - Use legacy approach
                    return saveToExternalStorageLegacy(content, fileName);
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to save file to external storage", e);
                throw new RuntimeException("Failed to save file to external storage: " + e.getMessage(), e);
            }
        }, executor);
    }
    
    /**
     * Saves content to external storage using MediaStore API (Android 10+)
     */
    private String saveToExternalStorageScoped(String content, String fileName) throws IOException {
        ContentResolver resolver = context.getContentResolver();
        
        // Create content values for the file
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, MIME_TYPE);
        values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/" + BACKUP_DIRECTORY);
        
        // Insert the file into MediaStore
        Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
        if (uri == null) {
            throw new IOException("Failed to create file in MediaStore");
        }
        
        // Write content to the file
        try (OutputStream outputStream = resolver.openOutputStream(uri)) {
            if (outputStream == null) {
                throw new IOException("Failed to open output stream");
            }
            outputStream.write(content.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        }
        
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) 
                + "/" + BACKUP_DIRECTORY + "/" + fileName;
        
        Log.d(TAG, "File saved to external storage (scoped): " + filePath);
        return filePath;
    }
    
    /**
     * Saves content to external storage using legacy approach (Android 9 and below)
     */
    private String saveToExternalStorageLegacy(String content, String fileName) throws IOException {
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (downloadsDir == null) {
            throw new IOException("External storage not available");
        }
        
        File backupDir = new File(downloadsDir, BACKUP_DIRECTORY);
        if (!backupDir.exists()) {
            if (!backupDir.mkdirs()) {
                throw new IOException("Failed to create backup directory: " + backupDir.getAbsolutePath());
            }
        }
        
        File file = new File(backupDir, fileName);
        
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(content.getBytes(StandardCharsets.UTF_8));
            fos.flush();
        }
        
        Log.d(TAG, "File saved to external storage (legacy): " + file.getAbsolutePath());
        return file.getAbsolutePath();
    }
    
    /**
     * Reads content from a file
     * @param filePath The path of the file to read
     * @return CompletableFuture with the file content
     */
    public CompletableFuture<String> readFromFile(String filePath) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                File file = new File(filePath);
                if (!file.exists()) {
                    Log.e(TAG, "File not found: " + filePath);
                    throw new RuntimeException("File not found: " + filePath);
                }
                
                byte[] bytes = new byte[(int) file.length()];
                
                try (FileInputStream fis = new FileInputStream(file)) {
                    int bytesRead = fis.read(bytes);
                    if (bytesRead != bytes.length) {
                        Log.w(TAG, "Warning: Expected " + bytes.length + " bytes, but read " + bytesRead);
                    }
                }
                
                return new String(bytes, StandardCharsets.UTF_8);
                
            } catch (IOException e) {
                Log.e(TAG, "Failed to read file: " + filePath, e);
                throw new RuntimeException("Failed to read file: " + filePath + " - " + e.getMessage(), e);
            }
        }, executor);
    }
    
    /**
     * Checks if a file exists
     * @param filePath The path of the file to check
     * @return CompletableFuture with existence result
     */
    public CompletableFuture<Boolean> fileExists(String filePath) {
        return CompletableFuture.supplyAsync(() -> {
            File file = new File(filePath);
            boolean exists = file.exists() && file.isFile();
            Log.d(TAG, "File exists check for " + filePath + ": " + exists);
            return exists;
        }, executor);
    }
    
    /**
     * Gets all backup files from internal storage
     * @return CompletableFuture with list of backup file paths
     */
    public CompletableFuture<List<String>> getBackupFiles() {
        return CompletableFuture.supplyAsync(() -> {
            List<String> backupFiles = new ArrayList<>();
            
            File backupDir = new File(context.getFilesDir(), BACKUP_DIRECTORY);
            if (backupDir.exists() && backupDir.isDirectory()) {
                File[] files = backupDir.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile() && file.getName().endsWith(BACKUP_FILE_EXTENSION)) {
                            backupFiles.add(file.getAbsolutePath());
                        }
                    }
                }
            }
            
            Log.d(TAG, "Found " + backupFiles.size() + " backup files");
            return backupFiles;
        }, executor);
    }
    
    /**
     * Deletes a file
     * @param filePath The path of the file to delete
     * @return CompletableFuture with deletion result
     */
    public CompletableFuture<Boolean> deleteFile(String filePath) {
        return CompletableFuture.supplyAsync(() -> {
            File file = new File(filePath);
            boolean deleted = file.exists() && file.delete();
            Log.d(TAG, "File deletion for " + filePath + ": " + deleted);
            return deleted;
        }, executor);
    }
    
    /**
     * Gets the backup directory path
     * @return The backup directory path
     */
    public String getBackupDirectoryPath() {
        return new File(context.getFilesDir(), BACKUP_DIRECTORY).getAbsolutePath();
    }
    
    /**
     * Gets the external backup directory path
     * @return The external backup directory path
     */
    public String getExternalBackupDirectoryPath() {
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        return new File(downloadsDir, BACKUP_DIRECTORY).getAbsolutePath();
    }
    
    /**
     * Validates if a file is a valid JSON file
     * @param filePath The path of the file to validate
     * @return CompletableFuture with validation result
     */
    public CompletableFuture<Boolean> isValidJsonFile(String filePath) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                File file = new File(filePath);
                if (!file.exists() || !file.isFile()) {
                    Log.d(TAG, "JSON validation failed: file does not exist or is not a file");
                    return false;
                }
                
                // Check file extension
                if (!file.getName().endsWith(BACKUP_FILE_EXTENSION)) {
                    Log.d(TAG, "JSON validation failed: incorrect file extension");
                    return false;
                }
                
                // Try to read the file to ensure it's readable
                String content = readFromFile(filePath).join();
                boolean isValid = content != null && !content.trim().isEmpty();
                Log.d(TAG, "JSON validation result for " + filePath + ": " + isValid);
                return isValid;
                
            } catch (Exception e) {
                Log.e(TAG, "JSON validation failed for " + filePath, e);
                return false;
            }
        }, executor);
    }
    
    /**
     * Reads content from a content URI (for files selected via file picker)
     * @param uri The content URI to read from
     * @return CompletableFuture with the file content
     */
    public CompletableFuture<String> readFromContentUri(Uri uri) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ContentResolver resolver = context.getContentResolver();
                
                try (InputStream inputStream = resolver.openInputStream(uri);
                     BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                    
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line).append('\n');
                    }
                    
                    String content = stringBuilder.toString();
                    Log.d(TAG, "Successfully read content from URI: " + uri.toString() + " (length: " + content.length() + ")");
                    return content;
                }
                
            } catch (IOException e) {
                Log.e(TAG, "Failed to read from content URI: " + uri.toString(), e);
                throw new RuntimeException("Failed to read from content URI: " + uri.toString() + " - " + e.getMessage(), e);
            }
        }, executor);
    }
    
    /**
     * Validates if a content URI contains a valid JSON backup file
     * @param uri The content URI to validate
     * @return CompletableFuture with validation result
     */
    public CompletableFuture<Boolean> isValidJsonContentUri(Uri uri) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Try to read the content to ensure it's readable
                String content = readFromContentUri(uri).join();
                boolean isValid = content != null && !content.trim().isEmpty();
                
                // Additional validation: check if it looks like JSON
                if (isValid) {
                    String trimmedContent = content.trim();
                    isValid = trimmedContent.startsWith("{") && trimmedContent.endsWith("}");
                }
                
                Log.d(TAG, "Content URI validation result for " + uri.toString() + ": " + isValid);
                return isValid;
                
            } catch (Exception e) {
                Log.e(TAG, "Content URI validation failed for " + uri.toString(), e);
                return false;
            }
        }, executor);
    }
} 