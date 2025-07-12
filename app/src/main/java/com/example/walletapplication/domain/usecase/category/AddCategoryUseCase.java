package com.example.walletapplication.domain.usecase.category;

import com.example.walletapplication.domain.entity.Category;
import com.example.walletapplication.domain.repository.CategoryRepository;

import java.util.concurrent.CompletableFuture;

public class AddCategoryUseCase {
    private final CategoryRepository categoryRepository;

    public AddCategoryUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CompletableFuture<Long> execute(Category category) {
        // Validation
        if (category == null) {
            CompletableFuture<Long> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalArgumentException("Category cannot be null"));
            return future;
        }

        if (category.getName() == null || category.getName().trim().isEmpty()) {
            CompletableFuture<Long> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalArgumentException("Category name cannot be empty"));
            return future;
        }

        if (category.getDisplayName() == null || category.getDisplayName().trim().isEmpty()) {
            CompletableFuture<Long> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalArgumentException("Category display name cannot be empty"));
            return future;
        }

        if (category.getType() == null) {
            CompletableFuture<Long> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalArgumentException("Category type cannot be null"));
            return future;
        }

        // Check if category name already exists
        return categoryRepository.isCategoryNameExists(category.getName())
                .thenCompose(exists -> {
                    if (exists) {
                        CompletableFuture<Long> future = new CompletableFuture<>();
                        future.completeExceptionally(new IllegalArgumentException("Category with this name already exists"));
                        return future;
                    }
                    return categoryRepository.insertCategory(category);
                });
    }
} 