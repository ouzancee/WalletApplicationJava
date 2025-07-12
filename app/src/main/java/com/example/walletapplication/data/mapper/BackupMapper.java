package com.example.walletapplication.data.mapper;

import com.example.walletapplication.domain.entity.BackupCategory;
import com.example.walletapplication.domain.entity.BackupTransaction;
import com.example.walletapplication.domain.entity.Category;
import com.example.walletapplication.domain.entity.CategoryType;
import com.example.walletapplication.domain.entity.Expense;
import com.example.walletapplication.domain.entity.Income;
import com.example.walletapplication.domain.entity.Transaction;
import com.example.walletapplication.domain.entity.TransactionType;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper class for converting between domain entities and backup entities
 */
public class BackupMapper {
    
    // Transaction mapping
    public static BackupTransaction toBackupTransaction(Transaction transaction) {
        BackupTransaction.Builder builder = new BackupTransaction.Builder()
                .setId(transaction.getId())
                .setAmount(transaction.getAmount())
                .setDescription(transaction.getDescription())
                .setCategory(transaction.getCategory())
                .setDate(transaction.getDate())
                .setType(transaction.getType().name());
        
        if (transaction instanceof Expense) {
            Expense expense = (Expense) transaction;
            builder.setPaymentMethod(expense.getPaymentMethod())
                   .setVendor(expense.getVendor());
        } else if (transaction instanceof Income) {
            Income income = (Income) transaction;
            builder.setSource(income.getSource())
                   .setIncomeType(income.getIncomeType());
        }
        
        return builder.build();
    }
    
    public static Transaction fromBackupTransaction(BackupTransaction backupTransaction) {
        TransactionType type = TransactionType.valueOf(backupTransaction.getType());
        
        if (type == TransactionType.EXPENSE) {
            return new Expense.Builder()
                    .setId(backupTransaction.getId())
                    .setAmount(backupTransaction.getAmount())
                    .setDescription(backupTransaction.getDescription())
                    .setCategory(backupTransaction.getCategory())
                    .setDate(backupTransaction.getDate())
                    .setPaymentMethod(backupTransaction.getPaymentMethod())
                    .setVendor(backupTransaction.getVendor())
                    .build();
        } else {
            return new Income.Builder()
                    .setId(backupTransaction.getId())
                    .setAmount(backupTransaction.getAmount())
                    .setDescription(backupTransaction.getDescription())
                    .setCategory(backupTransaction.getCategory())
                    .setDate(backupTransaction.getDate())
                    .setSource(backupTransaction.getSource())
                    .setIncomeType(backupTransaction.getIncomeType())
                    .build();
        }
    }
    
    // Category mapping
    public static BackupCategory toBackupCategory(Category category) {
        return new BackupCategory.Builder()
                .setId(category.getId())
                .setName(category.getName())
                .setDisplayName(category.getDisplayName())
                .setType(category.getType().name())
                .setIconName(category.getIconName())
                .setColor(category.getColor())
                .setIsDefault(category.isDefault())
                .setCreatedAt(category.getCreatedAt())
                .setUpdatedAt(category.getUpdatedAt())
                .build();
    }
    
    public static Category fromBackupCategory(BackupCategory backupCategory) {
        CategoryType type = CategoryType.valueOf(backupCategory.getType());
        
        return new Category.Builder()
                .setId(backupCategory.getId())
                .setName(backupCategory.getName())
                .setDisplayName(backupCategory.getDisplayName())
                .setType(type)
                .setIconName(backupCategory.getIconName())
                .setColor(backupCategory.getColor())
                .setIsDefault(backupCategory.isDefault())
                .setCreatedAt(backupCategory.getCreatedAt())
                .setUpdatedAt(backupCategory.getUpdatedAt())
                .build();
    }
    
    // List mapping
    public static List<BackupTransaction> toBackupTransactionList(List<Transaction> transactions) {
        List<BackupTransaction> backupTransactions = new ArrayList<>();
        for (Transaction transaction : transactions) {
            backupTransactions.add(toBackupTransaction(transaction));
        }
        return backupTransactions;
    }
    
    public static List<Transaction> fromBackupTransactionList(List<BackupTransaction> backupTransactions) {
        List<Transaction> transactions = new ArrayList<>();
        for (BackupTransaction backupTransaction : backupTransactions) {
            transactions.add(fromBackupTransaction(backupTransaction));
        }
        return transactions;
    }
    
    public static List<BackupCategory> toBackupCategoryList(List<Category> categories) {
        List<BackupCategory> backupCategories = new ArrayList<>();
        for (Category category : categories) {
            backupCategories.add(toBackupCategory(category));
        }
        return backupCategories;
    }
    
    public static List<Category> fromBackupCategoryList(List<BackupCategory> backupCategories) {
        List<Category> categories = new ArrayList<>();
        for (BackupCategory backupCategory : backupCategories) {
            categories.add(fromBackupCategory(backupCategory));
        }
        return categories;
    }
} 