package com.example.walletapplication.domain.usecase.backup;

import android.content.Context;

import com.example.walletapplication.R;
import com.example.walletapplication.data.mapper.BackupMapper;
import com.example.walletapplication.domain.common.AppError;
import com.example.walletapplication.domain.common.Result;
import com.example.walletapplication.domain.entity.BackupCategory;
import com.example.walletapplication.domain.entity.BackupData;
import com.example.walletapplication.domain.entity.BackupTransaction;
import com.example.walletapplication.domain.entity.Category;
import com.example.walletapplication.domain.entity.Transaction;
import com.example.walletapplication.domain.repository.CategoryRepository;
import com.example.walletapplication.domain.repository.TransactionRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import dagger.hilt.android.qualifiers.ApplicationContext;

/**
 * Use case for importing backup data into the application
 */
public class ImportDataUseCase {
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final Context context;

    public ImportDataUseCase(TransactionRepository transactionRepository, 
                           CategoryRepository categoryRepository,
                           @ApplicationContext Context context) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
        this.context = context;
    }

    /**
     * Imports backup data into the application
     * @param backupData The backup data to import
     * @param replaceExisting Whether to replace existing data or merge
     * @return CompletableFuture with Result containing import summary
     */
    public CompletableFuture<Result<ImportSummary>> execute(BackupData backupData, boolean replaceExisting) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Validate backup data
                Result<Void> validationResult = validateBackupData(backupData);
                if (validationResult.isError()) {
                    return Result.error(validationResult.getErrorOrNull());
                }

                ImportSummary summary = new ImportSummary();
                
                // Import categories first (transactions depend on categories)
                Result<Integer> categoryResult = importCategories(backupData.getCategories(), replaceExisting);
                if (categoryResult.isError()) {
                    return Result.error(categoryResult.getErrorOrNull());
                }
                summary.setCategoriesImported(categoryResult.getDataOrNull());
                
                // Import transactions
                Result<Integer> transactionResult = importTransactions(backupData.getTransactions(), replaceExisting);
                if (transactionResult.isError()) {
                    return Result.error(transactionResult.getErrorOrNull());
                }
                summary.setTransactionsImported(transactionResult.getDataOrNull());
                
                return Result.success(summary);
                
            } catch (Exception e) {
                return Result.error(AppError.fromException(e));
            }
        });
    }

    private Result<Void> validateBackupData(BackupData backupData) {
        if (backupData == null) {
            return Result.error(AppError.validation("backupData", context.getString(R.string.error_backup_data_null)));
        }
        
        if (backupData.getVersion() == null || backupData.getVersion().isEmpty()) {
            return Result.error(AppError.validation("version", context.getString(R.string.error_backup_version_invalid)));
        }
        
        if (backupData.getTransactions() == null || backupData.getCategories() == null) {
            return Result.error(AppError.validation("data", context.getString(R.string.error_backup_data_incomplete)));
        }
        
        // Validate backup version compatibility
        if (!isVersionCompatible(backupData.getVersion())) {
            return Result.error(AppError.validation("version", context.getString(R.string.error_backup_version_incompatible)));
        }
        
        return Result.success(null);
    }
    
    private boolean isVersionCompatible(String version) {
        // For now, we only support version 1.0
        return "1.0".equals(version);
    }
    
    private Result<Integer> importCategories(List<BackupCategory> backupCategories, boolean replaceExisting) {
        try {
            int importedCount = 0;
            
            for (BackupCategory backupCategory : backupCategories) {
                Category category = BackupMapper.fromBackupCategory(backupCategory);
                
                // Check if category already exists
                boolean exists = categoryRepository.isCategoryNameExists(category.getName()).join();
                
                if (exists && !replaceExisting) {
                    // Skip existing category if not replacing
                    continue;
                }
                
                if (exists && replaceExisting) {
                    // Update existing category
                    categoryRepository.updateCategory(category).join();
                } else {
                    // Insert new category (set ID to null for new insert)
                    Category newCategory = new Category.Builder()
                            .setId(null) // Let database generate new ID
                            .setName(category.getName())
                            .setDisplayName(category.getDisplayName())
                            .setType(category.getType())
                            .setIconName(category.getIconName())
                            .setColor(category.getColor())
                            .setIsDefault(category.isDefault())
                            .setCreatedAt(category.getCreatedAt())
                            .setUpdatedAt(category.getUpdatedAt())
                            .build();
                    
                    categoryRepository.insertCategory(newCategory).join();
                }
                
                importedCount++;
            }
            
            return Result.success(importedCount);
            
        } catch (Exception e) {
            return Result.error(AppError.fromException(e));
        }
    }
    
    private Result<Integer> importTransactions(List<BackupTransaction> backupTransactions, boolean replaceExisting) {
        try {
            int importedCount = 0;
            
            for (BackupTransaction backupTransaction : backupTransactions) {
                Transaction transaction = BackupMapper.fromBackupTransaction(backupTransaction);
                
                // For transactions, we always create new entries (set ID to null)
                // This prevents ID conflicts and allows importing same data multiple times
                Transaction newTransaction = createTransactionWithoutId(transaction);
                
                transactionRepository.insertTransaction(newTransaction).join();
                importedCount++;
            }
            
            return Result.success(importedCount);
            
        } catch (Exception e) {
            return Result.error(AppError.fromException(e));
        }
    }
    
    private Transaction createTransactionWithoutId(Transaction transaction) {
        if (transaction instanceof com.example.walletapplication.domain.entity.Expense) {
            com.example.walletapplication.domain.entity.Expense expense = 
                (com.example.walletapplication.domain.entity.Expense) transaction;
            return new com.example.walletapplication.domain.entity.Expense.Builder()
                    .setId(null) // Let database generate new ID
                    .setAmount(expense.getAmount())
                    .setDescription(expense.getDescription())
                    .setCategory(expense.getCategory())
                    .setDate(expense.getDate())
                    .setPaymentMethod(expense.getPaymentMethod())
                    .setVendor(expense.getVendor())
                    .build();
        } else {
            com.example.walletapplication.domain.entity.Income income = 
                (com.example.walletapplication.domain.entity.Income) transaction;
            return new com.example.walletapplication.domain.entity.Income.Builder()
                    .setId(null) // Let database generate new ID
                    .setAmount(income.getAmount())
                    .setDescription(income.getDescription())
                    .setCategory(income.getCategory())
                    .setDate(income.getDate())
                    .setSource(income.getSource())
                    .setIncomeType(income.getIncomeType())
                    .build();
        }
    }
    
    /**
     * Summary of import operation
     */
    public static class ImportSummary {
        private int transactionsImported;
        private int categoriesImported;
        
        public ImportSummary() {
        }
        
        public int getTransactionsImported() {
            return transactionsImported;
        }
        
        public void setTransactionsImported(int transactionsImported) {
            this.transactionsImported = transactionsImported;
        }
        
        public int getCategoriesImported() {
            return categoriesImported;
        }
        
        public void setCategoriesImported(int categoriesImported) {
            this.categoriesImported = categoriesImported;
        }
        
        @Override
        public String toString() {
            return "ImportSummary{" +
                    "transactionsImported=" + transactionsImported +
                    ", categoriesImported=" + categoriesImported +
                    '}';
        }
    }
} 