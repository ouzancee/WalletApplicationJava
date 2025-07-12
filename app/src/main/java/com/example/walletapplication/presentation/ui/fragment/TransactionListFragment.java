package com.example.walletapplication.presentation.ui.fragment;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.walletapplication.R;
import com.example.walletapplication.domain.entity.TransactionType;
import com.example.walletapplication.presentation.adapter.TransactionAdapter;
import com.example.walletapplication.presentation.base.BaseFragment;
import com.example.walletapplication.presentation.viewmodel.TransactionListViewModel;
import com.example.walletapplication.presentation.util.SearchHandler;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.util.Arrays;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TransactionListFragment extends BaseFragment {
    
    private TransactionListViewModel viewModel;
    private TransactionAdapter transactionAdapter;
    private SearchHandler searchHandler;
    
    // UI Components
    private TextInputLayout tilSearch, tilCategoryFilter;
    private TextInputEditText etSearch;
    private AutoCompleteTextView etCategoryFilter;
    private MaterialButtonToggleGroup toggleTypeFilter;
    private MaterialButton btnAll, btnIncome, btnExpense;
    private MaterialButton btnClearFilters;
    private RecyclerView rvTransactions;
    private MaterialTextView tvEmptyState;
    private FloatingActionButton fabAddTransaction;
    private CircularProgressIndicator progressBar;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_transaction_list;
    }

    @Override
    protected void initializeViews(View view) {
        viewModel = new ViewModelProvider(this).get(TransactionListViewModel.class);
        searchHandler = new SearchHandler();
        
        // Initialize UI components
        tilSearch = view.findViewById(R.id.til_search);
        tilCategoryFilter = view.findViewById(R.id.til_category_filter);
        etSearch = view.findViewById(R.id.et_search);
        etCategoryFilter = view.findViewById(R.id.et_category_filter);
        
        toggleTypeFilter = view.findViewById(R.id.toggle_type_filter);
        btnAll = view.findViewById(R.id.btn_all);
        btnIncome = view.findViewById(R.id.btn_income);
        btnExpense = view.findViewById(R.id.btn_expense);
        btnClearFilters = view.findViewById(R.id.btn_clear_filters);
        
        rvTransactions = view.findViewById(R.id.rv_transactions);
        tvEmptyState = view.findViewById(R.id.tv_empty_state);
        fabAddTransaction = view.findViewById(R.id.fab_add_transaction);
        progressBar = view.findViewById(R.id.progress_bar);
        
        setupRecyclerView();
        setupClickListeners();
        setupCategoryFilter();
        setTitle(getString(R.string.all_transactions));
    }

    private void setupRecyclerView() {
        transactionAdapter = new TransactionAdapter();
        rvTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTransactions.setAdapter(transactionAdapter);
        
        // Set item click listener
        transactionAdapter.setOnItemClickListener(transaction -> {
            // Navigate to transaction details (will be implemented later)
            showToast("Transaction clicked: " + transaction.getDescription());
        });
        
        // Set long click listener for delete
        transactionAdapter.setOnItemLongClickListener(transaction -> {
            showDeleteConfirmationDialog(transaction.getId());
            return true;
        });
    }

    private void setupClickListeners() {
        // FAB click - Navigate to add transaction
        fabAddTransaction.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_transactionList_to_addTransaction);
        });
        
        // Type filter toggle
        toggleTypeFilter.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btn_all) {
                    viewModel.filterByType(null);
                } else if (checkedId == R.id.btn_income) {
                    viewModel.filterByType(TransactionType.INCOME);
                } else if (checkedId == R.id.btn_expense) {
                    viewModel.filterByType(TransactionType.EXPENSE);
                }
            }
        });
        
        // Clear filters button
        btnClearFilters.setOnClickListener(v -> {
            clearAllFilters();
        });
        
        // Search text change listener with debouncing
        etSearch.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                searchHandler.search(query, searchQuery -> {
                    // Update adapter with current search query for highlighting
                    transactionAdapter.setSearchQuery(searchQuery);
                    
                    if (searchQuery.isEmpty()) {
                        viewModel.clearSearchAndReload();
                    } else {
                        viewModel.searchTransactions(searchQuery);
                    }
                });
            }
        });
        
        // Category filter change listener
        etCategoryFilter.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String category = s.toString().trim();
                if (category.isEmpty()) {
                    viewModel.filterByCategory(null);
                } else {
                    viewModel.filterByCategory(category);
                }
            }
        });
    }

    private void setupCategoryFilter() {
        List<String> categories = Arrays.asList(
            getString(R.string.category_food),
            getString(R.string.category_transport),
            getString(R.string.category_utilities),
            getString(R.string.category_entertainment),
            getString(R.string.category_shopping),
            getString(R.string.category_health),
            getString(R.string.category_education),
            getString(R.string.category_salary),
            getString(R.string.category_business),
            getString(R.string.category_investment),
            getString(R.string.category_other)
        );
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), 
            android.R.layout.simple_dropdown_item_1line, categories);
        etCategoryFilter.setAdapter(adapter);
    }

    private void clearAllFilters() {
        // Clear search
        etSearch.setText("");
        
        // Clear category filter
        etCategoryFilter.setText("");
        
        // Reset type filter to "All"
        btnAll.setChecked(true);
        
        // Clear search highlighting
        transactionAdapter.clearSearchQuery();
        
        // Clear filters in ViewModel
        viewModel.clearFilters();
    }

    private void showDeleteConfirmationDialog(Long transactionId) {
        if (getContext() == null) return;
        
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.delete_transaction))
                .setMessage(getString(R.string.delete_transaction_confirmation))
                .setPositiveButton(getString(R.string.delete), (dialog, which) -> {
                    viewModel.deleteTransaction(transactionId);
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    @Override
    protected void observeViewModel() {
        viewModel.getTransactions().observe(getViewLifecycleOwner(), transactions -> {
            if (transactions != null) {
                transactionAdapter.submitList(transactions);
                updateEmptyState(transactions.isEmpty());
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
        
        viewModel.getSearchQuery().observe(getViewLifecycleOwner(), query -> {
            // Update search hint or other UI elements if needed
        });
        
        viewModel.getFilterType().observe(getViewLifecycleOwner(), type -> {
            // Update filter UI state if needed
        });
        
        viewModel.getFilterCategory().observe(getViewLifecycleOwner(), category -> {
            // Update category filter UI state if needed
        });
    }

    private void updateEmptyState(boolean isEmpty) {
        if (isEmpty) {
            tvEmptyState.setVisibility(View.VISIBLE);
            rvTransactions.setVisibility(View.GONE);
            tvEmptyState.setText(getString(R.string.no_transactions_found));
        } else {
            tvEmptyState.setVisibility(View.GONE);
            rvTransactions.setVisibility(View.VISIBLE);
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

    // Simple TextWatcher implementation
    private abstract static class SimpleTextWatcher implements android.text.TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(android.text.Editable s) {}
    }
} 