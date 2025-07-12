package com.example.walletapplication.domain.usecase.transaction;

import com.example.walletapplication.domain.entity.Transaction;
import com.example.walletapplication.domain.repository.TransactionRepository;

import java.util.concurrent.CompletableFuture;

public class UpdateTransactionUseCase {
    private final TransactionRepository transactionRepository;

    public UpdateTransactionUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public CompletableFuture<Void> execute(Transaction transaction) {
        if (transaction == null) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalArgumentException("Transaction cannot be null"));
            return future;
        }
        
        if (transaction.getId() == null) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalArgumentException("Transaction ID cannot be null for update"));
            return future;
        }
        
        if (transaction.getAmount() == null || transaction.getAmount().signum() <= 0) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalArgumentException("Transaction amount must be positive"));
            return future;
        }
        
        if (transaction.getDescription() == null || transaction.getDescription().trim().isEmpty()) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalArgumentException("Transaction description cannot be empty"));
            return future;
        }
        
        if (transaction.getCategory() == null || transaction.getCategory().trim().isEmpty()) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalArgumentException("Transaction category cannot be empty"));
            return future;
        }
        
        if (transaction.getDate() == null) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalArgumentException("Transaction date cannot be null"));
            return future;
        }

        return transactionRepository.updateTransaction(transaction);
    }
} 