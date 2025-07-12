package com.example.walletapplication.presentation.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.walletapplication.R;
import com.example.walletapplication.presentation.base.BaseFragment;
import com.example.walletapplication.presentation.viewmodel.ReportsViewModel;
import com.example.walletapplication.presentation.adapter.CategoryBreakdownAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ReportsFragment extends BaseFragment {

    private ReportsViewModel viewModel;
    private DecimalFormat currencyFormatter;
    
    // UI Components
    private MaterialButtonToggleGroup toggleReportType;
    private Button btnMonthly, btnYearly;
    private MaterialButton btnPrevious, btnNext;
    private TextView tvPeriod;
    private ProgressBar progressBar;
    
    // Statistics Cards
    private TextView tvTotalIncome, tvTotalExpense, tvNetBalance, tvTransactionCount;
    
    // Chart
    private FrameLayout chartContainer;
    private TextView tvChartPlaceholder;
    
    // Category Breakdown
    private RecyclerView rvCategoryBreakdown;
    private LinearLayout layoutEmptyCategories;
    private CategoryBreakdownAdapter categoryAdapter;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_reports;
    }

    @Override
    protected void initializeViews(View view) {
        viewModel = new ViewModelProvider(this).get(ReportsViewModel.class);
        
        initViews(view);
        setupCurrencyFormatter();
        setupRecyclerView();
        setupClickListeners();
    }

    private void initViews(View view) {
        // Toggle and navigation
        toggleReportType = view.findViewById(R.id.toggleReportType);
        btnMonthly = view.findViewById(R.id.btnMonthly);
        btnYearly = view.findViewById(R.id.btnYearly);
        btnPrevious = view.findViewById(R.id.btnPrevious);
        btnNext = view.findViewById(R.id.btnNext);
        tvPeriod = view.findViewById(R.id.tvPeriod);
        progressBar = view.findViewById(R.id.progressBar);
        
        // Statistics
        tvTotalIncome = view.findViewById(R.id.tvTotalIncome);
        tvTotalExpense = view.findViewById(R.id.tvTotalExpense);
        tvNetBalance = view.findViewById(R.id.tvNetBalance);
        tvTransactionCount = view.findViewById(R.id.tvTransactionCount);
        
        // Chart
        chartContainer = view.findViewById(R.id.chartContainer);
        tvChartPlaceholder = view.findViewById(R.id.tvChartPlaceholder);
        
        // Category breakdown
        rvCategoryBreakdown = view.findViewById(R.id.rvCategoryBreakdown);
        layoutEmptyCategories = view.findViewById(R.id.layoutEmptyCategories);
        
        // Set initial selection
        toggleReportType.check(R.id.btnMonthly);
    }

    private void setupCurrencyFormatter() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("tr", "TR"));
        symbols.setCurrencySymbol("₺");
        currencyFormatter = new DecimalFormat("#,##0.00", symbols);
    }

    private void setupRecyclerView() {
        categoryAdapter = new CategoryBreakdownAdapter();
        rvCategoryBreakdown.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCategoryBreakdown.setAdapter(categoryAdapter);
    }

    private void setupClickListeners() {
        // Report type toggle
        toggleReportType.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnMonthly) {
                    viewModel.setReportType(ReportsViewModel.ReportType.MONTHLY);
                } else if (checkedId == R.id.btnYearly) {
                    viewModel.setReportType(ReportsViewModel.ReportType.YEARLY);
                }
            }
        });

        // Navigation buttons
        btnPrevious.setOnClickListener(v -> {
            ReportsViewModel.ReportType reportType = viewModel.getReportType().getValue();
            if (reportType == ReportsViewModel.ReportType.MONTHLY) {
                viewModel.selectPreviousMonth();
            } else if (reportType == ReportsViewModel.ReportType.YEARLY) {
                viewModel.selectPreviousYear();
            }
        });

        btnNext.setOnClickListener(v -> {
            ReportsViewModel.ReportType reportType = viewModel.getReportType().getValue();
            if (reportType == ReportsViewModel.ReportType.MONTHLY) {
                viewModel.selectNextMonth();
            } else if (reportType == ReportsViewModel.ReportType.YEARLY) {
                viewModel.selectNextYear();
            }
        });
    }

    @Override
    protected void observeViewModel() {
        // Loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // Error handling
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                if (error.contains("Beklenmeyen hata") || error.contains("ağ") || error.contains("veritabanı")) {
                    // Show retry option for network/database errors
                    showRetrySnackbar(error, () -> viewModel.refresh());
                } else {
                    showErrorSnackbar(error);
                }
            }
        });

        // Report type
        viewModel.getReportType().observe(getViewLifecycleOwner(), reportType -> {
            if (reportType == ReportsViewModel.ReportType.MONTHLY) {
                toggleReportType.check(R.id.btnMonthly);
            } else if (reportType == ReportsViewModel.ReportType.YEARLY) {
                toggleReportType.check(R.id.btnYearly);
            }
        });

        // Period display
        viewModel.getSelectedMonth().observe(getViewLifecycleOwner(), yearMonth -> {
            if (yearMonth != null && viewModel.getReportType().getValue() == ReportsViewModel.ReportType.MONTHLY) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("tr", "TR"));
                tvPeriod.setText(yearMonth.format(formatter));
            }
        });

        viewModel.getSelectedYear().observe(getViewLifecycleOwner(), year -> {
            if (year != null && viewModel.getReportType().getValue() == ReportsViewModel.ReportType.YEARLY) {
                tvPeriod.setText(String.valueOf(year));
            }
        });

        // Statistics
        viewModel.getTotalIncome().observe(getViewLifecycleOwner(), income -> {
            if (income != null) {
                tvTotalIncome.setText("₺" + currencyFormatter.format(income));
            }
        });

        viewModel.getTotalExpense().observe(getViewLifecycleOwner(), expense -> {
            if (expense != null) {
                tvTotalExpense.setText("₺" + currencyFormatter.format(expense));
            }
        });

        viewModel.getNetBalance().observe(getViewLifecycleOwner(), balance -> {
            if (balance != null) {
                tvNetBalance.setText("₺" + currencyFormatter.format(balance));
                updateBalanceColor(balance);
            }
        });

        viewModel.getTransactionCount().observe(getViewLifecycleOwner(), count -> {
            if (count != null) {
                tvTransactionCount.setText(String.valueOf(count));
            }
        });

        // Category breakdown
        viewModel.getCategoryExpenseData().observe(getViewLifecycleOwner(), categoryData -> {
            if (categoryData != null && !categoryData.isEmpty()) {
                updateCategoryBreakdown(categoryData);
                layoutEmptyCategories.setVisibility(View.GONE);
                rvCategoryBreakdown.setVisibility(View.VISIBLE);
            } else {
                layoutEmptyCategories.setVisibility(View.VISIBLE);
                rvCategoryBreakdown.setVisibility(View.GONE);
            }
        });
    }

    private void updateBalanceColor(BigDecimal balance) {
        int color;
        if (balance.compareTo(BigDecimal.ZERO) > 0) {
            color = getResources().getColor(R.color.green_700, null);
        } else if (balance.compareTo(BigDecimal.ZERO) < 0) {
            color = getResources().getColor(R.color.red_700, null);
        } else {
            color = getResources().getColor(R.color.blue_500, null);
        }
        tvNetBalance.setTextColor(color);
    }

    private void updateCategoryBreakdown(Map<String, BigDecimal> categoryData) {
        List<CategoryBreakdownAdapter.CategoryItem> items = new ArrayList<>();
        BigDecimal total = categoryData.values().stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        for (Map.Entry<String, BigDecimal> entry : categoryData.entrySet()) {
            String category = entry.getKey();
            BigDecimal amount = entry.getValue();
            
            // Calculate percentage
            double percentage = 0;
            if (total.compareTo(BigDecimal.ZERO) > 0) {
                percentage = amount.divide(total, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).doubleValue();
            }

            items.add(new CategoryBreakdownAdapter.CategoryItem(
                category,
                amount,
                percentage,
                1 // Transaction count - we'll improve this later
            ));
        }

        categoryAdapter.submitList(items);
    }
} 