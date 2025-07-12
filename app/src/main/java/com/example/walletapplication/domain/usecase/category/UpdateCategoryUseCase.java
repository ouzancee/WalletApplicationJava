package com.example.walletapplication.domain.usecase.category;

import com.example.walletapplication.domain.entity.Category;
import com.example.walletapplication.domain.repository.CategoryRepository;

import java.util.concurrent.CompletableFuture;

public class UpdateCategoryUseCase {
    private final CategoryRepository categoryRepository;

    public UpdateCategoryUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CompletableFuture<Void> execute(Category category) {
        // Validation
        if (category == null) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalArgumentException("Category cannot be null"));
            return future;
        }

        if (category.getId() == null) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalArgumentException("Category ID cannot be null"));
            return future;
        }

        if (category.getName() == null || category.getName().trim().isEmpty()) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalArgumentException("Category name cannot be empty"));
            return future;
        }

        if (category.getDisplayName() == null || category.getDisplayName().trim().isEmpty()) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalArgumentException("Category display name cannot be empty"));
            return future;
        }

        if (category.getType() == null) {
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalArgumentException("Category type cannot be null"));
            return future;
        }

        // Check if category exists
        return categoryRepository.getCategoryById(category.getId())
                .thenCompose(existingCategory -> {
                    if (!existingCategory.isPresent()) {
                        CompletableFuture<Void> future = new CompletableFuture<>();
                        future.completeExceptionally(new IllegalArgumentException("Category not found"));
                        return future;
                    }

                    // Don't allow updating default categories
                    if (existingCategory.get().isDefault()) {
                        CompletableFuture<Void> future = new CompletableFuture<>();
                        future.completeExceptionally(new IllegalArgumentException("Default categories cannot be updated"));
                        return future;
                    }

                    return categoryRepository.updateCategory(category);
                });
    }
} 