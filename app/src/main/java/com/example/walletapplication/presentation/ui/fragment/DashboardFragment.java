package com.example.walletapplication.presentation.ui.fragment;

import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.walletapplication.R;
import com.example.walletapplication.presentation.adapter.TransactionAdapter;
import com.example.walletapplication.presentation.base.BaseFragment;
import com.example.walletapplication.presentation.viewmodel.MainViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textview.MaterialTextView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DashboardFragment extends BaseFragment {
    
    private MainViewModel viewModel;
    private TransactionAdapter transactionAdapter;
    
    // UI Components
    private MaterialCardView cardBalance, cardMonthlyIncome, cardMonthlyExpense;
    private MaterialTextView tvCurrentBalance, tvMonthlyIncome, tvMonthlyExpense;
    private MaterialTextView tvRecentTransactionsTitle;
    private RecyclerView rvRecentTransactions;
    private MaterialButton btnViewAllTransactions, btnReports, btnBackup, btnCategories;
    private FloatingActionButton fabAddTransaction;
    private CircularProgressIndicator progressBar;
    
    private NumberFormat currencyFormatter;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_dashboard;
    }

    @Override
    protected void initializeViews(View view) {
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        
        // Initialize currency formatter
        currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("tr", "TR"));
        
        // Initialize UI components
        cardBalance = view.findViewById(R.id.card_balance);
        cardMonthlyIncome = view.findViewById(R.id.card_monthly_income);
        cardMonthlyExpense = view.findViewById(R.id.card_monthly_expense);
        
        tvCurrentBalance = view.findViewById(R.id.tv_current_balance);
        tvMonthlyIncome = view.findViewById(R.id.tv_monthly_income);
        tvMonthlyExpense = view.findViewById(R.id.tv_monthly_expense);
        
        tvRecentTransactionsTitle = view.findViewById(R.id.tv_recent_transactions_title);
        rvRecentTransactions = view.findViewById(R.id.rv_recent_transactions);
        btnViewAllTransactions = view.findViewById(R.id.btn_view_all_transactions);
        btnReports = view.findViewById(R.id.btn_reports);
        btnBackup = view.findViewById(R.id.btn_backup);
        btnCategories = view.findViewById(R.id.btn_categories);
        fabAddTransaction = view.findViewById(R.id.fab_add_transaction);
        progressBar = view.findViewById(R.id.progress_bar);
        
        setupRecyclerView();
        setupClickListeners();
        setTitle(getString(R.string.app_name));
    }

    private void setupRecyclerView() {
        transactionAdapter = new TransactionAdapter();
        rvRecentTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRecentTransactions.setAdapter(transactionAdapter);
        
        // Set item click listener
        transactionAdapter.setOnItemClickListener(transaction -> {
            // Navigate to transaction details (will be implemented later)
            showToast("Transaction clicked: " + transaction.getDescription());
        });
    }

    private void setupClickListeners() {
        // FAB click - Navigate to add transaction
        fabAddTransaction.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_dashboard_to_addTransaction);
        });
        
        // View all transactions button
        btnViewAllTransactions.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_dashboard_to_transactionList);
        });
        
        // Reports button
        btnReports.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_dashboard_to_reports);
        });
        
        // Backup button
        btnBackup.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_dashboard_to_backup);
        });
        
        // Categories button
        btnCategories.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_dashboard_to_categories);
        });
        
        // Balance card click - Navigate to reports
        cardBalance.setOnClickListener(v -> {
            // Navigate to reports (will be implemented later)
            showToast("Balance card clicked");
        });
        
        // Income card click - Filter by income
        cardMonthlyIncome.setOnClickListener(v -> {
            // Navigate to transaction list with income filter
            Navigation.findNavController(v).navigate(R.id.action_dashboard_to_transactionList);
        });
        
        // Expense card click - Filter by expense
        cardMonthlyExpense.setOnClickListener(v -> {
            // Navigate to transaction list with expense filter
            Navigation.findNavController(v).navigate(R.id.action_dashboard_to_transactionList);
        });
    }

    @Override
    protected void observeViewModel() {
        viewModel.getCurrentBalance().observe(getViewLifecycleOwner(), balance -> {
            if (balance != null) {
                tvCurrentBalance.setText(currencyFormatter.format(balance));
                updateBalanceCardColor(balance);
            }
        });
        
        viewModel.getMonthlyIncome().observe(getViewLifecycleOwner(), income -> {
            if (income != null) {
                tvMonthlyIncome.setText(currencyFormatter.format(income));
            }
        });
        
        viewModel.getMonthlyExpense().observe(getViewLifecycleOwner(), expense -> {
            if (expense != null) {
                tvMonthlyExpense.setText(currencyFormatter.format(expense));
            }
        });
        
        viewModel.getRecentTransactions().observe(getViewLifecycleOwner(), transactions -> {
            if (transactions != null) {
                transactionAdapter.submitList(transactions);
                updateRecentTransactionsVisibility(transactions.isEmpty());
            }
        });
        
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
        
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                showError(error);
            }
        });
    }

    private void updateBalanceCardColor(BigDecimal balance) {
        if (getContext() == null) return;
        
        int colorResId;
        if (balance.compareTo(BigDecimal.ZERO) > 0) {
            colorResId = R.color.green_500; // Positive balance - green
        } else if (balance.compareTo(BigDecimal.ZERO) < 0) {
            colorResId = R.color.red_500; // Negative balance - red
        } else {
            colorResId = R.color.blue_500; // Zero balance - blue
        }
        
        cardBalance.setCardBackgroundColor(getContext().getColor(colorResId));
    }

    private void updateRecentTransactionsVisibility(boolean isEmpty) {
        if (isEmpty) {
            tvRecentTransactionsTitle.setText(getString(R.string.no_transactions));
        } else {
            tvRecentTransactionsTitle.setText(getString(R.string.recent_transactions));
        }
    }

    public void refreshData() {
        if (viewModel != null) {
            viewModel.refresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }
} 