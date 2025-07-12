package com.example.walletapplication.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.walletapplication.data.local.entity.CategoryEntity;
import com.example.walletapplication.domain.entity.CategoryType;

import java.util.List;

@Dao
public interface CategoryDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertCategory(CategoryEntity category);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCategories(List<CategoryEntity> categories);
    
    @Update
    void updateCategory(CategoryEntity category);
    
    @Query("DELETE FROM categories WHERE id = :categoryId")
    void deleteCategoryById(long categoryId);
    
    @Delete
    void deleteCategory(CategoryEntity category);
    
    @Query("SELECT * FROM categories WHERE id = :id")
    CategoryEntity getCategoryById(long id);
    
    @Query("SELECT * FROM categories WHERE name = :name LIMIT 1")
    CategoryEntity getCategoryByName(String name);
    
    @Query("SELECT * FROM categories ORDER BY isDefault DESC, displayName ASC")
    List<CategoryEntity> getAllCategories();
    
    @Query("SELECT * FROM categories WHERE type = :type OR type = 'BOTH' ORDER BY isDefault DESC, displayName ASC")
    List<CategoryEntity> getCategoriesByType(CategoryType type);
    
    @Query("SELECT * FROM categories WHERE isDefault = 1 ORDER BY displayName ASC")
    List<CategoryEntity> getDefaultCategories();
    
    @Query("SELECT * FROM categories WHERE isDefault = 0 ORDER BY displayName ASC")
    List<CategoryEntity> getCustomCategories();
    
    @Query("SELECT COUNT(*) > 0 FROM categories WHERE name = :name")
    boolean isCategoryNameExists(String name);
    
    @Query("SELECT COUNT(*) FROM categories WHERE isDefault = 1")
    int getDefaultCategoryCount();
    
    @Query("SELECT COUNT(*) FROM categories")
    int getCategoryCount();
} 