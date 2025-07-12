package com.example.walletapplication.domain.repository;

import com.example.walletapplication.domain.entity.Transaction;
import com.example.walletapplication.domain.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface TransactionRepository {
    
    CompletableFuture<Long> insertTransaction(Transaction transaction);
    
    CompletableFuture<Void> updateTransaction(Transaction transaction);
    
    CompletableFuture<Void> deleteTransaction(Long transactionId);
    
    CompletableFuture<Optional<Transaction>> getTransactionById(Long id);
    
    CompletableFuture<List<Transaction>> getAllTransactions();
    
    CompletableFuture<List<Transaction>> getTransactionsByType(TransactionType type);
    
    CompletableFuture<List<Transaction>> getTransactionsByCategory(String category);
    
    CompletableFuture<List<Transaction>> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    CompletableFuture<List<Transaction>> getTransactionsByAmountRange(BigDecimal minAmount, BigDecimal maxAmount);
    
    CompletableFuture<List<Transaction>> searchTransactions(String query);
    
    CompletableFuture<BigDecimal> getTotalIncomeByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    CompletableFuture<BigDecimal> getTotalExpenseByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    CompletableFuture<BigDecimal> getBalanceByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    CompletableFuture<List<String>> getAllCategories();
} 