package com.example.walletapplication.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.walletapplication.domain.entity.Transaction;
import com.example.walletapplication.domain.usecase.report.GetMonthlyReportUseCase;
import com.example.walletapplication.domain.usecase.transaction.GetTransactionsUseCase;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainViewModel extends ViewModel {
    
    private final GetTransactionsUseCase getTransactionsUseCase;
    private final GetMonthlyReportUseCase getMonthlyReportUseCase;
    
    private final MutableLiveData<List<Transaction>> _recentTransactions = new MutableLiveData<>();
    private final MutableLiveData<BigDecimal> _currentBalance = new MutableLiveData<>();
    private final MutableLiveData<BigDecimal> _monthlyIncome = new MutableLiveData<>();
    private final MutableLiveData<BigDecimal> _monthlyExpense = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> _error = new MutableLiveData<>();
    
    public LiveData<List<Transaction>> getRecentTransactions() {
        return _recentTransactions;
    }
    
    public LiveData<BigDecimal> getCurrentBalance() {
        return _currentBalance;
    }
    
    public LiveData<BigDecimal> getMonthlyIncome() {
        return _monthlyIncome;
    }
    
    public LiveData<BigDecimal> getMonthlyExpense() {
        return _monthlyExpense;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return _isLoading;
    }
    
    public LiveData<String> getError() {
        return _error;
    }
    
    @Inject
    public MainViewModel(GetTransactionsUseCase getTransactionsUseCase, 
                        GetMonthlyReportUseCase getMonthlyReportUseCase) {
        this.getTransactionsUseCase = getTransactionsUseCase;
        this.getMonthlyReportUseCase = getMonthlyReportUseCase;
        
        // Initialize with default values
        _currentBalance.setValue(BigDecimal.ZERO);
        _monthlyIncome.setValue(BigDecimal.ZERO);
        _monthlyExpense.setValue(BigDecimal.ZERO);
        _isLoading.setValue(false);
        
        loadDashboardData();
    }
    
    public void loadDashboardData() {
        _isLoading.setValue(true);
        _error.setValue(null);
        
        // Load recent transactions
        getTransactionsUseCase.getAllTransactions()
            .thenAccept(transactions -> {
                _recentTransactions.postValue(transactions.subList(0, Math.min(5, transactions.size())));
            })
            .exceptionally(throwable -> {
                _error.postValue("Failed to load recent transactions: " + throwable.getMessage());
                return null;
            });
        
        // Load monthly report
        YearMonth currentMonth = YearMonth.now();
        getMonthlyReportUseCase.getMonthlyReport(currentMonth)
            .thenAccept(result -> {
                _isLoading.postValue(false);
                
                if (result.isSuccess()) {
                    GetMonthlyReportUseCase.MonthlyReport report = result.getDataOrNull();
                    if (report != null) {
                        _monthlyIncome.postValue(report.getTotalIncome());
                        _monthlyExpense.postValue(report.getTotalExpense());
                        _currentBalance.postValue(report.getBalance());
                    }
                } else {
                    _error.postValue("Aylık rapor yüklenirken hata oluştu");
                }
            })
            .exceptionally(throwable -> {
                _error.postValue("Beklenmeyen hata: " + throwable.getMessage());
                _isLoading.postValue(false);
                return null;
            });
    }
    
    public void refresh() {
        loadDashboardData();
    }
} 