package com.example.walletapplication.domain.usecase.category;

import com.example.walletapplication.domain.entity.Category;
import com.example.walletapplication.domain.entity.CategoryType;
import com.example.walletapplication.domain.repository.CategoryRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GetCategoriesByTypeUseCase {
    private final CategoryRepository categoryRepository;

    public GetCategoriesByTypeUseCase(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public CompletableFuture<List<Category>> execute(CategoryType type) {
        // Validation
        if (type == null) {
            CompletableFuture<List<Category>> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalArgumentException("Category type cannot be null"));
            return future;
        }

        return categoryRepository.getCategoriesByType(type);
    }

    public CompletableFuture<List<Category>> getAllCategories() {
        return categoryRepository.getAllCategories();
    }

    public CompletableFuture<List<Category>> getDefaultCategories() {
        return categoryRepository.getDefaultCategories();
    }

    public CompletableFuture<List<Category>> getCustomCategories() {
        return categoryRepository.getCustomCategories();
    }
} 