package com.example.walletapplication.domain.usecase.transaction;

import com.example.walletapplication.domain.entity.Transaction;
import com.example.walletapplication.domain.entity.TransactionType;
import com.example.walletapplication.domain.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GetTransactionsUseCase {
    private final TransactionRepository transactionRepository;

    public GetTransactionsUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public CompletableFuture<List<Transaction>> getAllTransactions() {
        return transactionRepository.getAllTransactions();
    }

    public CompletableFuture<List<Transaction>> getTransactionsByType(TransactionType type) {
        if (type == null) {
            CompletableFuture<List<Transaction>> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalArgumentException("Transaction type cannot be null"));
            return future;
        }
        return transactionRepository.getTransactionsByType(type);
    }

    public CompletableFuture<List<Transaction>> getTransactionsByCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            CompletableFuture<List<Transaction>> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalArgumentException("Category cannot be null or empty"));
            return future;
        }
        return transactionRepository.getTransactionsByCategory(category);
    }

    public CompletableFuture<List<Transaction>> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            CompletableFuture<List<Transaction>> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalArgumentException("Start date and end date cannot be null"));
            return future;
        }
        if (startDate.isAfter(endDate)) {
            CompletableFuture<List<Transaction>> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalArgumentException("Start date cannot be after end date"));
            return future;
        }
        return transactionRepository.getTransactionsByDateRange(startDate, endDate);
    }

    public CompletableFuture<List<Transaction>> getTransactionsByAmountRange(BigDecimal minAmount, BigDecimal maxAmount) {
        if (minAmount == null || maxAmount == null) {
            CompletableFuture<List<Transaction>> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalArgumentException("Min and max amounts cannot be null"));
            return future;
        }
        if (minAmount.compareTo(maxAmount) > 0) {
            CompletableFuture<List<Transaction>> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalArgumentException("Min amount cannot be greater than max amount"));
            return future;
        }
        return transactionRepository.getTransactionsByAmountRange(minAmount, maxAmount);
    }

    public CompletableFuture<List<Transaction>> searchTransactions(String query) {
        if (query == null || query.trim().isEmpty()) {
            CompletableFuture<List<Transaction>> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalArgumentException("Search query cannot be null or empty"));
            return future;
        }
        return transactionRepository.searchTransactions(query.trim());
    }
} 