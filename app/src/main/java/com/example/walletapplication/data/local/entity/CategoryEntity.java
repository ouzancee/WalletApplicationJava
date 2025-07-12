package com.example.walletapplication.data.local.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.walletapplication.data.local.converter.DateConverter;
import com.example.walletapplication.domain.entity.CategoryType;

import java.time.LocalDateTime;

@Entity(tableName = "categories", 
        indices = {@Index(value = "name", unique = true)})
@TypeConverters({DateConverter.class})
public class CategoryEntity {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    
    private String name;
    private String displayName;
    private CategoryType type;
    private String iconName;
    private String color;
    private boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CategoryEntity() {
    }

    @Ignore
    public CategoryEntity(String name, String displayName, CategoryType type, 
                         String iconName, String color, boolean isDefault, 
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.name = name;
        this.displayName = displayName;
        this.type = type;
        this.iconName = iconName;
        this.color = color;
        this.isDefault = isDefault;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public CategoryType getType() {
        return type;
    }

    public void setType(CategoryType type) {
        this.type = type;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 