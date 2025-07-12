package com.example.walletapplication.domain.usecase.category;

import com.example.walletapplication.domain.repository.TransactionRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GetCategoriesUseCase {
    private final TransactionRepository transactionRepository;

    public GetCategoriesUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public CompletableFuture<List<String>> execute() {
        return transactionRepository.getAllCategories();
    }
} 