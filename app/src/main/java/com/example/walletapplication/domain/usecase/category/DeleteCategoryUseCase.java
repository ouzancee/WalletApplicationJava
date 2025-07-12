package com.example.walletapplication.domain.usecase.category;

import com.example.walletapplication.domain.repository.CategoryRepository;

import java.util.concurrent.CompletableFuture;

public class DeleteCategoryUseCase {
    private final CategoryRepository categoryRepository;

    public DeleteCategoryUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CompletableFuture<Void> execute(Long categoryId) {
        // Validation
        if (categoryId == null) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalArgumentException("Category ID cannot be null"));
            return future;
        }

        // Check if category exists and is not default
        return categoryRepository.getCategoryById(categoryId)
                .thenCompose(category -> {
                    if (!category.isPresent()) {
                        CompletableFuture<Void> future = new CompletableFuture<>();
                        future.completeExceptionally(new IllegalArgumentException("Category not found"));
                        return future;
                    }

                    // Don't allow deleting default categories
                    if (category.get().isDefault()) {
                        CompletableFuture<Void> future = new CompletableFuture<>();
                        future.completeExceptionally(new IllegalArgumentException("Default categories cannot be deleted"));
                        return future;
                    }

                    return categoryRepository.deleteCategory(categoryId);
                });
    }
} 