package com.example.walletapplication.data.repository;

import com.example.walletapplication.data.local.dao.CategoryDao;
import com.example.walletapplication.data.local.entity.CategoryEntity;
import com.example.walletapplication.data.mapper.CategoryMapper;
import com.example.walletapplication.domain.entity.Category;
import com.example.walletapplication.domain.entity.CategoryType;
import com.example.walletapplication.domain.repository.CategoryRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class CategoryRepositoryImpl implements CategoryRepository {
    
    private final CategoryDao categoryDao;
    private final Executor executor;
    
    public CategoryRepositoryImpl(CategoryDao categoryDao, Executor executor) {
        this.categoryDao = categoryDao;
        this.executor = executor;
    }
    
    @Override
    public CompletableFuture<Long> insertCategory(Category category) {
        return CompletableFuture.supplyAsync(() -> {
            CategoryEntity entity = CategoryMapper.toEntity(category);
            return categoryDao.insertCategory(entity);
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> updateCategory(Category category) {
        return CompletableFuture.runAsync(() -> {
            CategoryEntity entity = CategoryMapper.toEntity(category);
            entity.setUpdatedAt(LocalDateTime.now());
            categoryDao.updateCategory(entity);
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> deleteCategory(Long categoryId) {
        return CompletableFuture.runAsync(() -> {
            categoryDao.deleteCategoryById(categoryId);
        }, executor);
    }
    
    @Override
    public CompletableFuture<Optional<Category>> getCategoryById(Long id) {
        return CompletableFuture.supplyAsync(() -> {
            CategoryEntity entity = categoryDao.getCategoryById(id);
            if (entity != null) {
                return Optional.of(CategoryMapper.toDomain(entity));
            }
            return Optional.empty();
        }, executor);
    }
    
    @Override
    public CompletableFuture<Optional<Category>> getCategoryByName(String name) {
        return CompletableFuture.supplyAsync(() -> {
            CategoryEntity entity = categoryDao.getCategoryByName(name);
            if (entity != null) {
                return Optional.of(CategoryMapper.toDomain(entity));
            }
            return Optional.empty();
        }, executor);
    }
    
    @Override
    public CompletableFuture<List<Category>> getAllCategories() {
        return CompletableFuture.supplyAsync(() -> {
            List<CategoryEntity> entities = categoryDao.getAllCategories();
            return CategoryMapper.toDomainList(entities);
        }, executor);
    }
    
    @Override
    public CompletableFuture<List<Category>> getCategoriesByType(CategoryType type) {
        return CompletableFuture.supplyAsync(() -> {
            List<CategoryEntity> entities = categoryDao.getCategoriesByType(type);
            return CategoryMapper.toDomainList(entities);
        }, executor);
    }
    
    @Override
    public CompletableFuture<List<Category>> getDefaultCategories() {
        return CompletableFuture.supplyAsync(() -> {
            List<CategoryEntity> entities = categoryDao.getDefaultCategories();
            return CategoryMapper.toDomainList(entities);
        }, executor);
    }
    
    @Override
    public CompletableFuture<List<Category>> getCustomCategories() {
        return CompletableFuture.supplyAsync(() -> {
            List<CategoryEntity> entities = categoryDao.getCustomCategories();
            return CategoryMapper.toDomainList(entities);
        }, executor);
    }
    
    @Override
    public CompletableFuture<Boolean> isCategoryNameExists(String name) {
        return CompletableFuture.supplyAsync(() -> {
            return categoryDao.isCategoryNameExists(name);
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> initializeDefaultCategories() {
        return CompletableFuture.runAsync(() -> {
            // Check if default categories already exist
            int defaultCategoryCount = categoryDao.getDefaultCategoryCount();
            if (defaultCategoryCount > 0) {
                return; // Already initialized
            }
            
            // Create default categories
            List<CategoryEntity> defaultCategories = createDefaultCategories();
            categoryDao.insertCategories(defaultCategories);
        }, executor);
    }
    
    private List<CategoryEntity> createDefaultCategories() {
        LocalDateTime now = LocalDateTime.now();
        List<CategoryEntity> categories = new java.util.ArrayList<>();
        
        // Expense categories
        categories.add(new CategoryEntity("food", "Yemek", CategoryType.EXPENSE, 
                "ic_food", "#FF9800", true, now, now));
        categories.add(new CategoryEntity("transport", "Ulaşım", CategoryType.EXPENSE, 
                "ic_transport", "#2196F3", true, now, now));
        categories.add(new CategoryEntity("utilities", "Faturalar", CategoryType.EXPENSE, 
                "ic_utilities", "#F44336", true, now, now));
        categories.add(new CategoryEntity("entertainment", "Eğlence", CategoryType.EXPENSE, 
                "ic_entertainment", "#E91E63", true, now, now));
        categories.add(new CategoryEntity("shopping", "Alışveriş", CategoryType.EXPENSE, 
                "ic_shopping", "#9C27B0", true, now, now));
        categories.add(new CategoryEntity("health", "Sağlık", CategoryType.EXPENSE, 
                "ic_health", "#4CAF50", true, now, now));
        categories.add(new CategoryEntity("education", "Eğitim", CategoryType.EXPENSE, 
                "ic_education", "#3F51B5", true, now, now));
        
        // Income categories
        categories.add(new CategoryEntity("salary", "Maaş", CategoryType.INCOME, 
                "ic_salary", "#4CAF50", true, now, now));
        categories.add(new CategoryEntity("business", "İş", CategoryType.INCOME, 
                "ic_business", "#FF9800", true, now, now));
        categories.add(new CategoryEntity("investment", "Yatırım", CategoryType.INCOME, 
                "ic_investment", "#2196F3", true, now, now));
        
        // Both categories
        categories.add(new CategoryEntity("other", "Diğer", CategoryType.BOTH, 
                "ic_other", "#607D8B", true, now, now));
        
        return categories;
    }
} 