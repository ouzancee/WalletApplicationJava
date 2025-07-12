package com.example.walletapplication.presentation.viewmodel;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.walletapplication.R;
import com.example.walletapplication.domain.common.AppError;
import com.example.walletapplication.domain.entity.Category;
import com.example.walletapplication.domain.entity.CategoryType;
import com.example.walletapplication.domain.entity.Expense;
import com.example.walletapplication.domain.entity.Income;
import com.example.walletapplication.domain.entity.Transaction;
import com.example.walletapplication.domain.entity.TransactionType;
import com.example.walletapplication.domain.repository.CategoryRepository;
import com.example.walletapplication.domain.usecase.transaction.AddTransactionUseCase;
import com.example.walletapplication.domain.usecase.category.GetCategoriesUseCase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;

@HiltViewModel
public class AddTransactionViewModel extends ViewModel {
    
    private final AddTransactionUseCase addTransactionUseCase;
    private final CategoryRepository categoryRepository;
    private final Context context;
    
    private final MutableLiveData<List<String>> _categories = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> _error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _transactionAdded = new MutableLiveData<>();
    
    // Form fields
    private final MutableLiveData<String> _amount = new MutableLiveData<>();
    private final MutableLiveData<String> _description = new MutableLiveData<>();
    private final MutableLiveData<String> _category = new MutableLiveData<>();
    private final MutableLiveData<TransactionType> _transactionType = new MutableLiveData<>();
    private final MutableLiveData<LocalDateTime> _date = new MutableLiveData<>();
    
    // Expense specific fields
    private final MutableLiveData<String> _paymentMethod = new MutableLiveData<>();
    private final MutableLiveData<String> _vendor = new MutableLiveData<>();
    
    // Income specific fields
    private final MutableLiveData<String> _source = new MutableLiveData<>();
    private final MutableLiveData<String> _incomeType = new MutableLiveData<>();
    
    public LiveData<List<String>> getCategories() {
        return _categories;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return _isLoading;
    }
    
    public LiveData<String> getError() {
        return _error;
    }
    
    public LiveData<Boolean> getTransactionAdded() {
        return _transactionAdded;
    }
    
    public LiveData<String> getAmount() {
        return _amount;
    }
    
    public LiveData<String> getDescription() {
        return _description;
    }
    
    public LiveData<String> getCategory() {
        return _category;
    }
    
    public LiveData<TransactionType> getTransactionType() {
        return _transactionType;
    }
    
    public LiveData<LocalDateTime> getDate() {
        return _date;
    }
    
    public LiveData<String> getPaymentMethod() {
        return _paymentMethod;
    }
    
    public LiveData<String> getVendor() {
        return _vendor;
    }
    
    public LiveData<String> getSource() {
        return _source;
    }
    
    public LiveData<String> getIncomeType() {
        return _incomeType;
    }
    
    @Inject
    public AddTransactionViewModel(AddTransactionUseCase addTransactionUseCase, 
                                  CategoryRepository categoryRepository,
                                  @ApplicationContext Context context) {
        this.addTransactionUseCase = addTransactionUseCase;
        this.categoryRepository = categoryRepository;
        this.context = context;
        
        // Initialize with default values
        _isLoading.setValue(false);
        _transactionAdded.setValue(false);
        _transactionType.setValue(TransactionType.EXPENSE);
        _date.setValue(LocalDateTime.now());
        
        loadCategories();
    }
    
    public void setAmount(String amount) {
        _amount.setValue(amount);
    }
    
    public void setDescription(String description) {
        _description.setValue(description);
    }
    
    public void setCategory(String category) {
        _category.setValue(category);
    }
    
    public void setTransactionType(TransactionType type) {
        _transactionType.setValue(type);
        // Reload categories based on transaction type
        loadCategoriesByType(type);
    }
    
    public void setDate(LocalDateTime date) {
        _date.setValue(date);
    }
    
    public void setPaymentMethod(String paymentMethod) {
        _paymentMethod.setValue(paymentMethod);
    }
    
    public void setVendor(String vendor) {
        _vendor.setValue(vendor);
    }
    
    public void setSource(String source) {
        _source.setValue(source);
    }
    
    public void setIncomeType(String incomeType) {
        _incomeType.setValue(incomeType);
    }
    
    public void addTransaction() {
        if (!validateForm()) {
            return;
        }
        
        _isLoading.setValue(true);
        _error.setValue(null);
        
        try {
            BigDecimal amount = new BigDecimal(_amount.getValue());
            String description = _description.getValue();
            String category = _category.getValue();
            LocalDateTime date = _date.getValue();
            TransactionType type = _transactionType.getValue();
            
            Transaction transaction;
            
            if (type == TransactionType.EXPENSE) {
                transaction = new Expense.Builder()
                    .setAmount(amount)
                    .setDescription(description)
                    .setCategory(category)
                    .setDate(date)
                    .setPaymentMethod(_paymentMethod.getValue())
                    .setVendor(_vendor.getValue())
                    .build();
            } else {
                transaction = new Income.Builder()
                    .setAmount(amount)
                    .setDescription(description)
                    .setCategory(category)
                    .setDate(date)
                    .setSource(_source.getValue())
                    .setIncomeType(_incomeType.getValue())
                    .build();
            }
            
            addTransactionUseCase.execute(transaction)
                .thenAccept(result -> {
                    _isLoading.postValue(false);
                    
                    if (result.isSuccess()) {
                        _transactionAdded.postValue(true);
                        clearForm();
                    } else {
                        AppError error = result.getErrorOrNull();
                        if (error != null) {
                            _error.postValue(error.getUserMessage());
                        } else {
                            _error.postValue(context.getString(R.string.error_saving_transaction));
                        }
                    }
                })
                .exceptionally(throwable -> {
                    _error.postValue(context.getString(R.string.error_unexpected) + ": " + throwable.getMessage());
                    _isLoading.postValue(false);
                    return null;
                });
                
        } catch (NumberFormatException e) {
            _error.setValue(context.getString(R.string.error_invalid_amount_format));
            _isLoading.setValue(false);
        }
    }
    
    private boolean validateForm() {
        if (_amount.getValue() == null || _amount.getValue().trim().isEmpty()) {
            _error.setValue(context.getString(R.string.error_amount_required));
            return false;
        }
        
        if (_description.getValue() == null || _description.getValue().trim().isEmpty()) {
            _error.setValue(context.getString(R.string.error_description_required));
            return false;
        }
        
        if (_category.getValue() == null || _category.getValue().trim().isEmpty()) {
            _error.setValue(context.getString(R.string.error_category_required));
            return false;
        }
        
        if (_date.getValue() == null) {
            _error.setValue(context.getString(R.string.error_date_required));
            return false;
        }
        
        return true;
    }
    
    private void clearForm() {
        _amount.setValue("");
        _description.setValue("");
        _category.setValue("");
        _paymentMethod.setValue("");
        _vendor.setValue("");
        _source.setValue("");
        _incomeType.setValue("");
        _date.setValue(LocalDateTime.now());
    }
    
    private void loadCategories() {
        categoryRepository.getAllCategories()
            .thenAccept(categories -> {
                // Convert Category entities to display names
                List<String> categoryNames = categories.stream()
                    .map(Category::getDisplayName)
                    .collect(Collectors.toList());
                _categories.postValue(categoryNames);
            })
            .exceptionally(throwable -> {
                _error.postValue(context.getString(R.string.error_failed_to_load_categories) + ": " + throwable.getMessage());
                return null;
            });
    }
    
    private void loadCategoriesByType(TransactionType type) {
        CategoryType categoryType = convertToCategoryType(type);
        categoryRepository.getCategoriesByType(categoryType)
            .thenAccept(categories -> {
                // Convert Category entities to display names
                List<String> categoryNames = categories.stream()
                    .map(Category::getDisplayName)
                    .collect(Collectors.toList());
                _categories.postValue(categoryNames);
            })
            .exceptionally(throwable -> {
                _error.postValue(context.getString(R.string.error_failed_to_load_categories) + ": " + throwable.getMessage());
                return null;
            });
    }
    
    private CategoryType convertToCategoryType(TransactionType transactionType) {
        switch (transactionType) {
            case INCOME:
                return CategoryType.INCOME;
            case EXPENSE:
                return CategoryType.EXPENSE;
            default:
                return CategoryType.EXPENSE; // Default to expense
        }
    }
} 