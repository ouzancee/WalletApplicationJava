package com.example.walletapplication.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.walletapplication.domain.common.AppError;
import com.example.walletapplication.domain.entity.Transaction;
import com.example.walletapplication.domain.entity.TransactionType;
import com.example.walletapplication.domain.usecase.report.GetMonthlyReportUseCase;
import com.example.walletapplication.domain.usecase.transaction.GetTransactionsUseCase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ReportsViewModel extends ViewModel {
    
    private final GetMonthlyReportUseCase getMonthlyReportUseCase;
    private final GetTransactionsUseCase getTransactionsUseCase;
    
    // Report data
    private final MutableLiveData<GetMonthlyReportUseCase.MonthlyReport> _monthlyReport = new MutableLiveData<>();
    private final MutableLiveData<List<GetMonthlyReportUseCase.MonthlyReport>> _yearlyReports = new MutableLiveData<>();
    
    // Chart data
    private final MutableLiveData<List<ChartData>> _incomeChartData = new MutableLiveData<>();
    private final MutableLiveData<List<ChartData>> _expenseChartData = new MutableLiveData<>();
    private final MutableLiveData<Map<String, BigDecimal>> _categoryExpenseData = new MutableLiveData<>();
    private final MutableLiveData<Map<String, BigDecimal>> _categoryIncomeData = new MutableLiveData<>();
    
    // UI state
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> _error = new MutableLiveData<>();
    private final MutableLiveData<YearMonth> _selectedMonth = new MutableLiveData<>();
    private final MutableLiveData<Integer> _selectedYear = new MutableLiveData<>();
    private final MutableLiveData<ReportType> _reportType = new MutableLiveData<>();
    
    // Report statistics
    private final MutableLiveData<BigDecimal> _totalIncome = new MutableLiveData<>();
    private final MutableLiveData<BigDecimal> _totalExpense = new MutableLiveData<>();
    private final MutableLiveData<BigDecimal> _netBalance = new MutableLiveData<>();
    private final MutableLiveData<Integer> _transactionCount = new MutableLiveData<>();
    
    @Inject
    public ReportsViewModel(GetMonthlyReportUseCase getMonthlyReportUseCase, 
                           GetTransactionsUseCase getTransactionsUseCase) {
        this.getMonthlyReportUseCase = getMonthlyReportUseCase;
        this.getTransactionsUseCase = getTransactionsUseCase;
        
        // Initialize with default values
        _isLoading.setValue(false);
        _selectedMonth.setValue(YearMonth.now());
        _selectedYear.setValue(YearMonth.now().getYear());
        _reportType.setValue(ReportType.MONTHLY);
        _totalIncome.setValue(BigDecimal.ZERO);
        _totalExpense.setValue(BigDecimal.ZERO);
        _netBalance.setValue(BigDecimal.ZERO);
        _transactionCount.setValue(0);
        
        // Load initial data
        loadMonthlyReport(YearMonth.now());
    }
    
    // LiveData getters
    public LiveData<GetMonthlyReportUseCase.MonthlyReport> getMonthlyReport() {
        return _monthlyReport;
    }
    
    public LiveData<List<GetMonthlyReportUseCase.MonthlyReport>> getYearlyReports() {
        return _yearlyReports;
    }
    
    public LiveData<List<ChartData>> getIncomeChartData() {
        return _incomeChartData;
    }
    
    public LiveData<List<ChartData>> getExpenseChartData() {
        return _expenseChartData;
    }
    
    public LiveData<Map<String, BigDecimal>> getCategoryExpenseData() {
        return _categoryExpenseData;
    }
    
    public LiveData<Map<String, BigDecimal>> getCategoryIncomeData() {
        return _categoryIncomeData;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return _isLoading;
    }
    
    public LiveData<String> getError() {
        return _error;
    }
    
    public LiveData<YearMonth> getSelectedMonth() {
        return _selectedMonth;
    }
    
    public LiveData<Integer> getSelectedYear() {
        return _selectedYear;
    }
    
    public LiveData<ReportType> getReportType() {
        return _reportType;
    }
    
    public LiveData<BigDecimal> getTotalIncome() {
        return _totalIncome;
    }
    
    public LiveData<BigDecimal> getTotalExpense() {
        return _totalExpense;
    }
    
    public LiveData<BigDecimal> getNetBalance() {
        return _netBalance;
    }
    
    public LiveData<Integer> getTransactionCount() {
        return _transactionCount;
    }
    
    // Public methods
    public void loadMonthlyReport(YearMonth yearMonth) {
        _selectedMonth.setValue(yearMonth);
        _isLoading.setValue(true);
        _error.setValue(null);
        
        getMonthlyReportUseCase.getMonthlyReport(yearMonth)
            .thenAccept(result -> {
                _isLoading.postValue(false);
                
                if (result.isSuccess()) {
                    GetMonthlyReportUseCase.MonthlyReport report = result.getDataOrNull();
                    if (report != null) {
                        _monthlyReport.postValue(report);
                        _totalIncome.postValue(report.getTotalIncome());
                        _totalExpense.postValue(report.getTotalExpense());
                        _netBalance.postValue(report.getBalance());
                        _transactionCount.postValue(report.getTransactions().size());
                        
                        // Generate chart data
                        generateCategoryData(report.getTransactions());
                    }
                } else {
                    AppError error = result.getErrorOrNull();
                    if (error != null) {
                        _error.postValue(error.getUserMessage());
                    } else {
                        _error.postValue("Aylık rapor yüklenirken hata oluştu");
                    }
                }
            })
            .exceptionally(throwable -> {
                _error.postValue("Beklenmeyen hata: " + throwable.getMessage());
                _isLoading.postValue(false);
                return null;
            });
    }
    
    public void loadYearlyReport(int year) {
        _selectedYear.setValue(year);
        _isLoading.setValue(true);
        _error.setValue(null);
        
        List<GetMonthlyReportUseCase.MonthlyReport> yearlyReports = new ArrayList<>();
        List<ChartData> incomeData = new ArrayList<>();
        List<ChartData> expenseData = new ArrayList<>();
        
        // Load reports for all 12 months
        for (int month = 1; month <= 12; month++) {
            YearMonth yearMonth = YearMonth.of(year, month);
            
            getMonthlyReportUseCase.getMonthlyReport(yearMonth)
                .thenAccept(result -> {
                    if (result.isSuccess()) {
                        GetMonthlyReportUseCase.MonthlyReport report = result.getDataOrNull();
                        if (report != null) {
                            yearlyReports.add(report);
                            
                            // Add to chart data
                            incomeData.add(new ChartData(
                                yearMonth.getMonth().toString().substring(0, 3),
                                report.getTotalIncome()
                            ));
                            expenseData.add(new ChartData(
                                yearMonth.getMonth().toString().substring(0, 3),
                                report.getTotalExpense()
                            ));
                            
                            // If all months loaded
                            if (yearlyReports.size() == 12) {
                                _yearlyReports.postValue(yearlyReports);
                                _incomeChartData.postValue(incomeData);
                                _expenseChartData.postValue(expenseData);
                                
                                // Calculate yearly totals
                                BigDecimal totalIncome = yearlyReports.stream()
                                    .map(GetMonthlyReportUseCase.MonthlyReport::getTotalIncome)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                                BigDecimal totalExpense = yearlyReports.stream()
                                    .map(GetMonthlyReportUseCase.MonthlyReport::getTotalExpense)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                                int totalTransactions = yearlyReports.stream()
                                    .mapToInt(monthlyReport -> monthlyReport.getTransactions().size())
                                    .sum();
                                
                                _totalIncome.postValue(totalIncome);
                                _totalExpense.postValue(totalExpense);
                                _netBalance.postValue(totalIncome.subtract(totalExpense));
                                _transactionCount.postValue(totalTransactions);
                                
                                _isLoading.postValue(false);
                            }
                        }
                    } else {
                        _error.postValue("Yıllık rapor yüklenirken hata oluştu");
                        _isLoading.postValue(false);
                    }
                })
                .exceptionally(throwable -> {
                    _error.postValue("Beklenmeyen hata: " + throwable.getMessage());
                    _isLoading.postValue(false);
                    return null;
                });
        }
    }
    
    public void setReportType(ReportType reportType) {
        _reportType.setValue(reportType);
        
        if (reportType == ReportType.MONTHLY) {
            loadMonthlyReport(_selectedMonth.getValue());
        } else if (reportType == ReportType.YEARLY) {
            loadYearlyReport(_selectedYear.getValue());
        }
    }
    
    public void selectPreviousMonth() {
        YearMonth current = _selectedMonth.getValue();
        if (current != null) {
            loadMonthlyReport(current.minusMonths(1));
        }
    }
    
    public void selectNextMonth() {
        YearMonth current = _selectedMonth.getValue();
        if (current != null) {
            loadMonthlyReport(current.plusMonths(1));
        }
    }
    
    public void selectPreviousYear() {
        Integer current = _selectedYear.getValue();
        if (current != null) {
            loadYearlyReport(current - 1);
        }
    }
    
    public void selectNextYear() {
        Integer current = _selectedYear.getValue();
        if (current != null) {
            loadYearlyReport(current + 1);
        }
    }
    
    public void refresh() {
        ReportType currentType = _reportType.getValue();
        if (currentType == ReportType.MONTHLY) {
            loadMonthlyReport(_selectedMonth.getValue());
        } else if (currentType == ReportType.YEARLY) {
            loadYearlyReport(_selectedYear.getValue());
        }
    }
    
    private void generateCategoryData(List<Transaction> transactions) {
        Map<String, BigDecimal> expenseByCategory = new HashMap<>();
        Map<String, BigDecimal> incomeByCategory = new HashMap<>();
        
        for (Transaction transaction : transactions) {
            String category = transaction.getCategory();
            BigDecimal amount = transaction.getAmount();
            
            if (transaction.getType() == TransactionType.EXPENSE) {
                expenseByCategory.put(category, 
                    expenseByCategory.getOrDefault(category, BigDecimal.ZERO).add(amount));
            } else if (transaction.getType() == TransactionType.INCOME) {
                incomeByCategory.put(category,
                    incomeByCategory.getOrDefault(category, BigDecimal.ZERO).add(amount));
            }
        }
        
        _categoryExpenseData.postValue(expenseByCategory);
        _categoryIncomeData.postValue(incomeByCategory);
    }
    
    // Enum for report types
    public enum ReportType {
        MONTHLY, YEARLY
    }
    
    // Data class for chart data
    public static class ChartData {
        private final String label;
        private final BigDecimal value;
        
        public ChartData(String label, BigDecimal value) {
            this.label = label;
            this.value = value;
        }
        
        public String getLabel() {
            return label;
        }
        
        public BigDecimal getValue() {
            return value;
        }
    }
} 