package com.example.walletapplication.data.mapper;

import com.example.walletapplication.data.local.entity.CategoryEntity;
import com.example.walletapplication.domain.entity.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryMapper {
    
    public static CategoryEntity toEntity(Category category) {
        CategoryEntity entity = new CategoryEntity();
        entity.setId(category.getId());
        entity.setName(category.getName());
        entity.setDisplayName(category.getDisplayName());
        entity.setType(category.getType());
        entity.setIconName(category.getIconName());
        entity.setColor(category.getColor());
        entity.setDefault(category.isDefault());
        entity.setCreatedAt(category.getCreatedAt());
        entity.setUpdatedAt(category.getUpdatedAt());
        return entity;
    }
    
    public static Category toDomain(CategoryEntity entity) {
        return new Category.Builder()
                .setId(entity.getId())
                .setName(entity.getName())
                .setDisplayName(entity.getDisplayName())
                .setType(entity.getType())
                .setIconName(entity.getIconName())
                .setColor(entity.getColor())
                .setIsDefault(entity.isDefault())
                .setCreatedAt(entity.getCreatedAt())
                .setUpdatedAt(entity.getUpdatedAt())
                .build();
    }
    
    public static List<Category> toDomainList(List<CategoryEntity> entities) {
        List<Category> categories = new ArrayList<>();
        for (CategoryEntity entity : entities) {
            categories.add(toDomain(entity));
        }
        return categories;
    }
    
    public static List<CategoryEntity> toEntityList(List<Category> categories) {
        List<CategoryEntity> entities = new ArrayList<>();
        for (Category category : categories) {
            entities.add(toEntity(category));
        }
        return entities;
    }
} 