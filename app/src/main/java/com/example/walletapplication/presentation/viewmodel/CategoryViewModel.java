package com.example.walletapplication.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.walletapplication.domain.entity.Category;
import com.example.walletapplication.domain.entity.CategoryType;
import com.example.walletapplication.domain.repository.CategoryRepository;
import com.example.walletapplication.domain.usecase.category.AddCategoryUseCase;
import com.example.walletapplication.domain.usecase.category.DeleteCategoryUseCase;
import com.example.walletapplication.domain.usecase.category.GetCategoriesByTypeUseCase;
import com.example.walletapplication.domain.usecase.category.UpdateCategoryUseCase;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CategoryViewModel extends ViewModel {
    
    private final AddCategoryUseCase addCategoryUseCase;
    private final UpdateCategoryUseCase updateCategoryUseCase;
    private final DeleteCategoryUseCase deleteCategoryUseCase;
    private final GetCategoriesByTypeUseCase getCategoriesByTypeUseCase;
    private final CategoryRepository categoryRepository;
    
    private final MutableLiveData<List<Category>> _categories = new MutableLiveData<>();
    private final MutableLiveData<List<Category>> _defaultCategories = new MutableLiveData<>();
    private final MutableLiveData<List<Category>> _customCategories = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> _error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _operationSuccess = new MutableLiveData<>();
    
    // Form fields for add/edit category
    private final MutableLiveData<String> _categoryName = new MutableLiveData<>();
    private final MutableLiveData<String> _categoryDisplayName = new MutableLiveData<>();
    private final MutableLiveData<CategoryType> _categoryType = new MutableLiveData<>();
    private final MutableLiveData<String> _categoryColor = new MutableLiveData<>();
    private final MutableLiveData<String> _categoryIcon = new MutableLiveData<>();
    
    @Inject
    public CategoryViewModel(
            AddCategoryUseCase addCategoryUseCase,
            UpdateCategoryUseCase updateCategoryUseCase,
            DeleteCategoryUseCase deleteCategoryUseCase,
            GetCategoriesByTypeUseCase getCategoriesByTypeUseCase,
            CategoryRepository categoryRepository
    ) {
        this.addCategoryUseCase = addCategoryUseCase;
        this.updateCategoryUseCase = updateCategoryUseCase;
        this.deleteCategoryUseCase = deleteCategoryUseCase;
        this.getCategoriesByTypeUseCase = getCategoriesByTypeUseCase;
        this.categoryRepository = categoryRepository;
        
        // Initialize default values
        _isLoading.setValue(false);
        _operationSuccess.setValue(false);
        _categoryType.setValue(CategoryType.EXPENSE);
        _categoryColor.setValue("#FF9800");
        _categoryIcon.setValue("ic_category");
        
        // Initialize default categories on first run
        initializeDefaultCategories();
        loadCategories();
    }
    
    // LiveData getters
    public LiveData<List<Category>> getCategories() {
        return _categories;
    }
    
    public LiveData<List<Category>> getDefaultCategories() {
        return _defaultCategories;
    }
    
    public LiveData<List<Category>> getCustomCategories() {
        return _customCategories;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return _isLoading;
    }
    
    public LiveData<String> getError() {
        return _error;
    }
    
    public LiveData<Boolean> getOperationSuccess() {
        return _operationSuccess;
    }
    
    public LiveData<String> getCategoryName() {
        return _categoryName;
    }
    
    public LiveData<String> getCategoryDisplayName() {
        return _categoryDisplayName;
    }
    
    public LiveData<CategoryType> getCategoryType() {
        return _categoryType;
    }
    
    public LiveData<String> getCategoryColor() {
        return _categoryColor;
    }
    
    public LiveData<String> getCategoryIcon() {
        return _categoryIcon;
    }
    
    // Setters for form fields
    public void setCategoryName(String name) {
        _categoryName.setValue(name);
    }
    
    public void setCategoryDisplayName(String displayName) {
        _categoryDisplayName.setValue(displayName);
    }
    
    public void setCategoryType(CategoryType type) {
        _categoryType.setValue(type);
    }
    
    public void setCategoryColor(String color) {
        _categoryColor.setValue(color);
    }
    
    public void setCategoryIcon(String icon) {
        _categoryIcon.setValue(icon);
    }
    
    // Operations
    public void loadCategories() {
        _isLoading.setValue(true);
        _error.setValue(null);
        
        getCategoriesByTypeUseCase.getAllCategories()
                .thenAccept(categories -> {
                    _categories.postValue(categories);
                    _isLoading.postValue(false);
                })
                .exceptionally(throwable -> {
                    _error.postValue("Failed to load categories: " + throwable.getMessage());
                    _isLoading.postValue(false);
                    return null;
                });
    }
    
    public void loadCategoriesByType(CategoryType type) {
        _isLoading.setValue(true);
        _error.setValue(null);
        
        getCategoriesByTypeUseCase.execute(type)
                .thenAccept(categories -> {
                    _categories.postValue(categories);
                    _isLoading.postValue(false);
                })
                .exceptionally(throwable -> {
                    _error.postValue("Failed to load categories: " + throwable.getMessage());
                    _isLoading.postValue(false);
                    return null;
                });
    }
    
    public void loadDefaultCategories() {
        getCategoriesByTypeUseCase.getDefaultCategories()
                .thenAccept(categories -> {
                    _defaultCategories.postValue(categories);
                })
                .exceptionally(throwable -> {
                    _error.postValue("Failed to load default categories: " + throwable.getMessage());
                    return null;
                });
    }
    
    public void loadCustomCategories() {
        getCategoriesByTypeUseCase.getCustomCategories()
                .thenAccept(categories -> {
                    _customCategories.postValue(categories);
                })
                .exceptionally(throwable -> {
                    _error.postValue("Failed to load custom categories: " + throwable.getMessage());
                    return null;
                });
    }
    
    public void addCategory() {
        if (!validateForm()) {
            return;
        }
        
        _isLoading.setValue(true);
        _error.setValue(null);
        
        Category category = new Category.Builder()
                .setName(_categoryName.getValue())
                .setDisplayName(_categoryDisplayName.getValue())
                .setType(_categoryType.getValue())
                .setColor(_categoryColor.getValue())
                .setIconName(_categoryIcon.getValue())
                .setIsDefault(false)
                .build();
        
        addCategoryUseCase.execute(category)
                .thenAccept(id -> {
                    _operationSuccess.postValue(true);
                    _isLoading.postValue(false);
                    clearForm();
                    loadCategories();
                })
                .exceptionally(throwable -> {
                    _error.postValue("Failed to add category: " + throwable.getMessage());
                    _isLoading.postValue(false);
                    return null;
                });
    }
    
    public void updateCategory(Category category) {
        _isLoading.setValue(true);
        _error.setValue(null);
        
        updateCategoryUseCase.execute(category)
                .thenAccept(result -> {
                    _operationSuccess.postValue(true);
                    _isLoading.postValue(false);
                    loadCategories();
                })
                .exceptionally(throwable -> {
                    _error.postValue("Failed to update category: " + throwable.getMessage());
                    _isLoading.postValue(false);
                    return null;
                });
    }
    
    public void deleteCategory(Long categoryId) {
        _isLoading.setValue(true);
        _error.setValue(null);
        
        deleteCategoryUseCase.execute(categoryId)
                .thenAccept(result -> {
                    _operationSuccess.postValue(true);
                    _isLoading.postValue(false);
                    loadCategories();
                })
                .exceptionally(throwable -> {
                    _error.postValue("Failed to delete category: " + throwable.getMessage());
                    _isLoading.postValue(false);
                    return null;
                });
    }
    
    private boolean validateForm() {
        if (_categoryName.getValue() == null || _categoryName.getValue().trim().isEmpty()) {
            _error.setValue("Category name is required");
            return false;
        }
        
        if (_categoryDisplayName.getValue() == null || _categoryDisplayName.getValue().trim().isEmpty()) {
            _error.setValue("Category display name is required");
            return false;
        }
        
        if (_categoryType.getValue() == null) {
            _error.setValue("Category type is required");
            return false;
        }
        
        return true;
    }
    
    private void clearForm() {
        _categoryName.setValue("");
        _categoryDisplayName.setValue("");
        _categoryType.setValue(CategoryType.EXPENSE);
        _categoryColor.setValue("#FF9800");
        _categoryIcon.setValue("ic_category");
    }
    
    private void initializeDefaultCategories() {
        categoryRepository.initializeDefaultCategories()
                .exceptionally(throwable -> {
                    _error.postValue("Failed to initialize default categories: " + throwable.getMessage());
                    return null;
                });
    }
} 