package com.example.walletapplication.data.repository;

import android.net.Uri;
import com.example.walletapplication.data.service.FileStorageService;
import com.example.walletapplication.domain.entity.BackupData;
import com.example.walletapplication.domain.repository.BackupRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Implementation of BackupRepository using file storage and JSON serialization
 */
public class BackupRepositoryImpl implements BackupRepository {
    private final FileStorageService fileStorageService;
    private final Gson gson;
    
    public BackupRepositoryImpl(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
        this.gson = createGson();
    }
    
    private Gson createGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                .registerTypeAdapter(BigDecimal.class, new BigDecimalSerializer())
                .registerTypeAdapter(BigDecimal.class, new BigDecimalDeserializer())
                .setPrettyPrinting()
                .create();
    }
    
    @Override
    public CompletableFuture<String> saveBackupToFile(BackupData backupData, String fileName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String jsonContent = gson.toJson(backupData);
                return fileStorageService.saveToInternalStorage(jsonContent, fileName).join();
            } catch (Exception e) {
                throw new RuntimeException("Failed to save backup to file", e);
            }
        });
    }
    
    @Override
    public CompletableFuture<BackupData> loadBackupFromFile(String filePath) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String jsonContent = fileStorageService.readFromFile(filePath).join();
                return gson.fromJson(jsonContent, BackupData.class);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load backup from file", e);
            }
        });
    }
    
    @Override
    public CompletableFuture<String> saveBackupToExternalStorage(BackupData backupData, String fileName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String jsonContent = gson.toJson(backupData);
                return fileStorageService.saveToExternalStorage(jsonContent, fileName).join();
            } catch (Exception e) {
                throw new RuntimeException("Failed to save backup to external storage", e);
            }
        });
    }
    
    @Override
    public String getDefaultBackupFileName() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        return "wallet_backup_" + now.format(formatter) + ".json";
    }
    
    @Override
    public CompletableFuture<Boolean> isValidBackupFile(String filePath) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Check if file exists and is valid JSON
                boolean isValidJson = fileStorageService.isValidJsonFile(filePath).join();
                if (!isValidJson) {
                    return false;
                }
                
                // Try to parse as BackupData
                String jsonContent = fileStorageService.readFromFile(filePath).join();
                BackupData backupData = gson.fromJson(jsonContent, BackupData.class);
                
                // Basic validation
                return backupData != null && 
                       backupData.getVersion() != null && 
                       backupData.getTransactions() != null && 
                       backupData.getCategories() != null;
                       
            } catch (Exception e) {
                return false;
            }
        });
    }
    
    @Override
    public CompletableFuture<List<String>> getAvailableBackupFiles() {
        return fileStorageService.getBackupFiles();
    }
    
    @Override
    public CompletableFuture<BackupData> loadBackupFromContentUri(Uri uri) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String jsonContent = fileStorageService.readFromContentUri(uri).join();
                return gson.fromJson(jsonContent, BackupData.class);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load backup from content URI", e);
            }
        });
    }
    
    @Override
    public CompletableFuture<Boolean> isValidBackupContentUri(Uri uri) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Check if URI is valid JSON
                boolean isValidJson = fileStorageService.isValidJsonContentUri(uri).join();
                if (!isValidJson) {
                    return false;
                }
                
                // Try to parse as BackupData
                String jsonContent = fileStorageService.readFromContentUri(uri).join();
                BackupData backupData = gson.fromJson(jsonContent, BackupData.class);
                
                // Basic validation
                return backupData != null && 
                       backupData.getVersion() != null && 
                       backupData.getTransactions() != null && 
                       backupData.getCategories() != null;
                       
            } catch (Exception e) {
                return false;
            }
        });
    }
    
    // Custom serializers for LocalDateTime
    private static class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        
        @Override
        public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext context) {
            return new JsonPrimitive(localDateTime.format(formatter));
        }
    }
    
    private static class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        
        @Override
        public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            return LocalDateTime.parse(json.getAsString(), formatter);
        }
    }
    
    // Custom serializers for BigDecimal
    private static class BigDecimalSerializer implements JsonSerializer<BigDecimal> {
        @Override
        public JsonElement serialize(BigDecimal bigDecimal, Type type, JsonSerializationContext context) {
            return new JsonPrimitive(bigDecimal.toString());
        }
    }
    
    private static class BigDecimalDeserializer implements JsonDeserializer<BigDecimal> {
        @Override
        public BigDecimal deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            return new BigDecimal(json.getAsString());
        }
    }
} 