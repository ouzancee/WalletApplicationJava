package com.example.walletapplication.domain.usecase.transaction;

import com.example.walletapplication.domain.common.AppError;
import com.example.walletapplication.domain.common.Result;
import com.example.walletapplication.domain.entity.Transaction;
import com.example.walletapplication.domain.repository.TransactionRepository;

import java.util.concurrent.CompletableFuture;

public class AddTransactionUseCase {
    private final TransactionRepository transactionRepository;

    public AddTransactionUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public CompletableFuture<Result<Long>> execute(Transaction transaction) {
        // Validation
        Result<Transaction> validationResult = validateTransaction(transaction);
        if (validationResult.isError()) {
            return CompletableFuture.completedFuture(Result.error(validationResult.getErrorOrNull()));
        }

        // Execute transaction insertion
        return transactionRepository.insertTransaction(transaction)
            .thenApply(Result::success)
            .exceptionally(throwable -> {
                if (throwable.getCause() instanceof Exception) {
                    return Result.error(AppError.fromException((Exception) throwable.getCause()));
                } else {
                    return Result.error(AppError.unknown(throwable));
                }
            });
    }

    private Result<Transaction> validateTransaction(Transaction transaction) {
        if (transaction == null) {
            return Result.error(AppError.validation("transaction", "İşlem boş olamaz"));
        }
        
        if (transaction.getAmount() == null || transaction.getAmount().signum() <= 0) {
            return Result.error(AppError.validation("amount", "İşlem tutarı pozitif olmalıdır"));
        }
        
        if (transaction.getDescription() == null || transaction.getDescription().trim().isEmpty()) {
            return Result.error(AppError.validation("description", "İşlem açıklaması boş olamaz"));
        }
        
        if (transaction.getCategory() == null || transaction.getCategory().trim().isEmpty()) {
            return Result.error(AppError.validation("category", "İşlem kategorisi seçilmelidir"));
        }
        
        if (transaction.getDate() == null) {
            return Result.error(AppError.validation("date", "İşlem tarihi seçilmelidir"));
        }

        if (transaction.getType() == null) {
            return Result.error(AppError.validation("type", "İşlem türü seçilmelidir"));
        }

        return Result.success(transaction);
    }
} 