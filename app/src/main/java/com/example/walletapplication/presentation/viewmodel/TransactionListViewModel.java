package com.example.walletapplication.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.walletapplication.domain.entity.Transaction;
import com.example.walletapplication.domain.entity.TransactionType;
import com.example.walletapplication.domain.usecase.transaction.AdvancedSearchUseCase;
import com.example.walletapplication.domain.usecase.transaction.DeleteTransactionUseCase;
import com.example.walletapplication.domain.usecase.transaction.GetTransactionsUseCase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TransactionListViewModel extends ViewModel {
    
    private final GetTransactionsUseCase getTransactionsUseCase;
    private final DeleteTransactionUseCase deleteTransactionUseCase;
    private final AdvancedSearchUseCase advancedSearchUseCase;
    
    private final MutableLiveData<List<Transaction>> _transactions = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> _error = new MutableLiveData<>();
    private final MutableLiveData<String> _searchQuery = new MutableLiveData<>();
    private final MutableLiveData<TransactionType> _filterType = new MutableLiveData<>();
    private final MutableLiveData<String> _filterCategory = new MutableLiveData<>();
    
    public LiveData<List<Transaction>> getTransactions() {
        return _transactions;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return _isLoading;
    }
    
    public LiveData<String> getError() {
        return _error;
    }
    
    public LiveData<String> getSearchQuery() {
        return _searchQuery;
    }
    
    public LiveData<TransactionType> getFilterType() {
        return _filterType;
    }
    
    public LiveData<String> getFilterCategory() {
        return _filterCategory;
    }
    
    @Inject
    public TransactionListViewModel(GetTransactionsUseCase getTransactionsUseCase, 
                                   DeleteTransactionUseCase deleteTransactionUseCase,
                                   AdvancedSearchUseCase advancedSearchUseCase) {
        this.getTransactionsUseCase = getTransactionsUseCase;
        this.deleteTransactionUseCase = deleteTransactionUseCase;
        this.advancedSearchUseCase = advancedSearchUseCase;
        
        // Initialize with default values
        _isLoading.setValue(false);
        _searchQuery.setValue("");
        
        loadAllTransactions();
    }
    
    public void loadAllTransactions() {
        _isLoading.setValue(true);
        _error.setValue(null);
        
        getTransactionsUseCase.getAllTransactions()
            .thenAccept(transactions -> {
                _transactions.postValue(transactions);
                _isLoading.postValue(false);
            })
            .exceptionally(throwable -> {
                _error.postValue("Failed to load transactions: " + throwable.getMessage());
                _isLoading.postValue(false);
                return null;
            });
    }
    
    public void searchTransactions(String query) {
        _searchQuery.setValue(query);
        
        if (query == null || query.trim().isEmpty()) {
            loadAllTransactions();
            return;
        }
        
        _isLoading.setValue(true);
        _error.setValue(null);
        
        getTransactionsUseCase.searchTransactions(query)
            .thenAccept(transactions -> {
                _transactions.postValue(transactions);
                _isLoading.postValue(false);
            })
            .exceptionally(throwable -> {
                _error.postValue("Failed to search transactions: " + throwable.getMessage());
                _isLoading.postValue(false);
                return null;
            });
    }
    
    public void filterByType(TransactionType type) {
        _filterType.setValue(type);
        
        if (type == null) {
            loadAllTransactions();
            return;
        }
        
        _isLoading.setValue(true);
        _error.setValue(null);
        
        getTransactionsUseCase.getTransactionsByType(type)
            .thenAccept(transactions -> {
                _transactions.postValue(transactions);
                _isLoading.postValue(false);
            })
            .exceptionally(throwable -> {
                _error.postValue("Failed to filter transactions: " + throwable.getMessage());
                _isLoading.postValue(false);
                return null;
            });
    }
    
    public void filterByCategory(String category) {
        _filterCategory.setValue(category);
        
        if (category == null || category.trim().isEmpty()) {
            loadAllTransactions();
            return;
        }
        
        _isLoading.setValue(true);
        _error.setValue(null);
        
        getTransactionsUseCase.getTransactionsByCategory(category)
            .thenAccept(transactions -> {
                _transactions.postValue(transactions);
                _isLoading.postValue(false);
            })
            .exceptionally(throwable -> {
                _error.postValue("Failed to filter transactions: " + throwable.getMessage());
                _isLoading.postValue(false);
                return null;
            });
    }
    
    public void filterByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        _isLoading.setValue(true);
        _error.setValue(null);
        
        getTransactionsUseCase.getTransactionsByDateRange(startDate, endDate)
            .thenAccept(transactions -> {
                _transactions.postValue(transactions);
                _isLoading.postValue(false);
            })
            .exceptionally(throwable -> {
                _error.postValue("Failed to filter transactions: " + throwable.getMessage());
                _isLoading.postValue(false);
                return null;
            });
    }
    
    public void filterByAmountRange(BigDecimal minAmount, BigDecimal maxAmount) {
        _isLoading.setValue(true);
        _error.setValue(null);
        
        getTransactionsUseCase.getTransactionsByAmountRange(minAmount, maxAmount)
            .thenAccept(transactions -> {
                _transactions.postValue(transactions);
                _isLoading.postValue(false);
            })
            .exceptionally(throwable -> {
                _error.postValue("Failed to filter transactions: " + throwable.getMessage());
                _isLoading.postValue(false);
                return null;
            });
    }
    
    public void deleteTransaction(Long transactionId) {
        _isLoading.setValue(true);
        _error.setValue(null);
        
        deleteTransactionUseCase.execute(transactionId)
            .thenRun(() -> {
                _isLoading.postValue(false);
                // Reload transactions after deletion
                loadAllTransactions();
            })
            .exceptionally(throwable -> {
                _error.postValue("Failed to delete transaction: " + throwable.getMessage());
                _isLoading.postValue(false);
                return null;
            });
    }
    
    public void clearFilters() {
        _filterType.setValue(null);
        _filterCategory.setValue(null);
        _searchQuery.setValue("");
        loadAllTransactions();
    }
    
    public void refresh() {
        loadAllTransactions();
    }
    
    /**
     * Clears search query and reloads all transactions
     * Used when search field is cleared
     */
    public void clearSearchAndReload() {
        _searchQuery.setValue("");
        loadAllTransactions();
    }
    
    /**
     * Performs advanced search with multiple criteria
     * @param criteria Advanced search criteria
     */
    public void performAdvancedSearch(AdvancedSearchUseCase.SearchCriteria criteria) {
        _isLoading.setValue(true);
        _error.setValue(null);
        
        advancedSearchUseCase.search(criteria)
            .thenAccept(transactions -> {
                _transactions.postValue(transactions);
                _isLoading.postValue(false);
            })
            .exceptionally(throwable -> {
                _error.postValue("Advanced search failed: " + throwable.getMessage());
                _isLoading.postValue(false);
                return null;
            });
    }
    
    /**
     * Quick search using only text query
     * @param query Text query to search for
     */
    public void performQuickSearch(String query) {
        _isLoading.setValue(true);
        _error.setValue(null);
        
        advancedSearchUseCase.quickSearch(query)
            .thenAccept(transactions -> {
                _transactions.postValue(transactions);
                _isLoading.postValue(false);
            })
            .exceptionally(throwable -> {
                _error.postValue("Quick search failed: " + throwable.getMessage());
                _isLoading.postValue(false);
                return null;
            });
    }
    
    /**
     * Search by amount range
     * @param minAmount Minimum amount (can be null)
     * @param maxAmount Maximum amount (can be null)
     */
    public void searchByAmountRange(BigDecimal minAmount, BigDecimal maxAmount) {
        _isLoading.setValue(true);
        _error.setValue(null);
        
        advancedSearchUseCase.searchByAmountRange(minAmount, maxAmount)
            .thenAccept(transactions -> {
                _transactions.postValue(transactions);
                _isLoading.postValue(false);
            })
            .exceptionally(throwable -> {
                _error.postValue("Amount range search failed: " + throwable.getMessage());
                _isLoading.postValue(false);
                return null;
            });
    }
    
    /**
     * Search by date range
     * @param startDate Start date (can be null)
     * @param endDate End date (can be null)
     */
    public void searchByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        _isLoading.setValue(true);
        _error.setValue(null);
        
        advancedSearchUseCase.searchByDateRange(startDate, endDate)
            .thenAccept(transactions -> {
                _transactions.postValue(transactions);
                _isLoading.postValue(false);
            })
            .exceptionally(throwable -> {
                _error.postValue("Date range search failed: " + throwable.getMessage());
                _isLoading.postValue(false);
                return null;
            });
    }
} 