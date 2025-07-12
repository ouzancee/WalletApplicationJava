package com.example.walletapplication.domain.usecase.backup;

import android.content.Context;
import android.os.Build;

import com.example.walletapplication.data.mapper.BackupMapper;
import com.example.walletapplication.domain.common.AppError;
import com.example.walletapplication.domain.common.Result;
import com.example.walletapplication.domain.entity.BackupCategory;
import com.example.walletapplication.domain.entity.BackupData;
import com.example.walletapplication.domain.entity.BackupMetadata;
import com.example.walletapplication.domain.entity.BackupTransaction;
import com.example.walletapplication.domain.entity.Category;
import com.example.walletapplication.domain.entity.Transaction;
import com.example.walletapplication.domain.repository.CategoryRepository;
import com.example.walletapplication.domain.repository.TransactionRepository;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import dagger.hilt.android.qualifiers.ApplicationContext;

/**
 * Use case for exporting all application data to backup format
 */
public class ExportDataUseCase {
    private static final String BACKUP_VERSION = "1.0";
    
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final Context context;

    public ExportDataUseCase(TransactionRepository transactionRepository, 
                           CategoryRepository categoryRepository,
                           @ApplicationContext Context context) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.context = context;
    }

    /**
     * Exports all application data to a BackupData object
     * @return CompletableFuture with Result containing BackupData
     */
    public CompletableFuture<Result<BackupData>> execute() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Get all transactions
                List<Transaction> transactions = transactionRepository.getAllTransactions().join();
                List<BackupTransaction> backupTransactions = BackupMapper.toBackupTransactionList(transactions);
                
                // Get all categories
                List<Category> categories = categoryRepository.getAllCategories().join();
                List<BackupCategory> backupCategories = BackupMapper.toBackupCategoryList(categories);
                
                // Create metadata
                BackupMetadata metadata = createMetadata(backupTransactions.size(), backupCategories.size());
                
                // Create backup data
                BackupData backupData = new BackupData.Builder()
                    .setVersion(BACKUP_VERSION)
                    .setCreatedAt(LocalDateTime.now())
                    .setMetadata(metadata)
                    .setTransactions(backupTransactions)
                    .setCategories(backupCategories)
                    .build();
                
                return Result.success(backupData);
                
            } catch (Exception e) {
                return Result.error(AppError.fromException(e));
            }
        });
    }

    private BackupMetadata createMetadata(int transactionCount, int categoryCount) {
        String appVersion = getAppVersion();
        String deviceModel = Build.MODEL;
        String deviceId = getDeviceId();
        LocalDateTime exportedAt = LocalDateTime.now();
        String checksum = generateChecksum(transactionCount, categoryCount);
        
        return new BackupMetadata.Builder()
            .setAppVersion(appVersion)
            .setDeviceModel(deviceModel)
            .setDeviceId(deviceId)
            .setExportedAt(exportedAt)
            .setTransactionCount(transactionCount)
            .setCategoryCount(categoryCount)
            .setChecksum(checksum)
            .build();
    }
    
    private String getAppVersion() {
        try {
            return context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .versionName;
        } catch (Exception e) {
            return "1.0";
        }
    }
    
    private String getDeviceId() {
        // Simple device identifier for backup tracking
        return Build.MANUFACTURER + "_" + Build.MODEL + "_" + Build.VERSION.SDK_INT;
    }
    
    private String generateChecksum(int transactionCount, int categoryCount) {
        try {
            String data = transactionCount + "_" + categoryCount + "_" + System.currentTimeMillis();
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(data.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return "no_checksum";
        }
    }
} 