package com.example.walletapplication.domain.usecase.transaction;

import com.example.walletapplication.domain.repository.TransactionRepository;

import java.util.concurrent.CompletableFuture;

public class DeleteTransactionUseCase {
    private final TransactionRepository transactionRepository;

    public DeleteTransactionUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public CompletableFuture<Void> execute(Long transactionId) {
        if (transactionId == null) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalArgumentException("Transaction ID cannot be null"));
            return future;
        }
        
        if (transactionId <= 0) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalArgumentException("Transaction ID must be positive"));
            return future;
        }

        return transactionRepository.deleteTransaction(transactionId);
    }
} 