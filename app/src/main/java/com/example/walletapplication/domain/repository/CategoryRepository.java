package com.example.walletapplication.domain.repository;

import com.example.walletapplication.domain.entity.Category;
import com.example.walletapplication.domain.entity.CategoryType;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface CategoryRepository {
    
    CompletableFuture<Long> insertCategory(Category category);
    
    CompletableFuture<Void> updateCategory(Category category);
    
    CompletableFuture<Void> deleteCategory(Long categoryId);
    
    CompletableFuture<Optional<Category>> getCategoryById(Long id);
    
    CompletableFuture<Optional<Category>> getCategoryByName(String name);
    
    CompletableFuture<List<Category>> getAllCategories();
    
    CompletableFuture<List<Category>> getCategoriesByType(CategoryType type);
    
    CompletableFuture<List<Category>> getDefaultCategories();
    
    CompletableFuture<List<Category>> getCustomCategories();
    
    CompletableFuture<Boolean> isCategoryNameExists(String name);
    
    CompletableFuture<Void> initializeDefaultCategories();
} 