package com.example.walletapplication.presentation.ui.fragment;

import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.walletapplication.R;
import com.example.walletapplication.domain.entity.Category;
import com.example.walletapplication.domain.entity.CategoryType;
import com.example.walletapplication.presentation.adapter.CategoryAdapter;
import com.example.walletapplication.presentation.base.BaseFragment;
import com.example.walletapplication.presentation.ui.dialog.CategoryDialogFragment;
import com.example.walletapplication.presentation.viewmodel.CategoryViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CategoryFragment extends BaseFragment {

    private CategoryViewModel viewModel;
    private CategoryAdapter defaultCategoriesAdapter;
    private CategoryAdapter customCategoriesAdapter;

    // UI Components
    private MaterialButtonToggleGroup toggleCategoryType;
    private MaterialButton btnAllCategories, btnExpenseCategories, btnIncomeCategories;
    private MaterialButton btnAddCategory;
    private RecyclerView rvDefaultCategories, rvCustomCategories;
    private MaterialTextView tvNoDefaultCategories, tvNoCustomCategories;
    private CircularProgressIndicator progressBar;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_category;
    }

    @Override
    protected void initializeViews(View view) {
        viewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        // Initialize UI components
        toggleCategoryType = view.findViewById(R.id.toggle_category_type);
        btnAllCategories = view.findViewById(R.id.btn_all_categories);
        btnExpenseCategories = view.findViewById(R.id.btn_expense_categories);
        btnIncomeCategories = view.findViewById(R.id.btn_income_categories);
        btnAddCategory = view.findViewById(R.id.btn_add_category);
        rvDefaultCategories = view.findViewById(R.id.rv_default_categories);
        rvCustomCategories = view.findViewById(R.id.rv_custom_categories);
        tvNoDefaultCategories = view.findViewById(R.id.tv_no_default_categories);
        tvNoCustomCategories = view.findViewById(R.id.tv_no_custom_categories);
        progressBar = view.findViewById(R.id.progress_bar);

        setupRecyclerViews();
        setupClickListeners();
        setTitle(getString(R.string.category_management));
    }

    private void setupRecyclerViews() {
        // Default categories adapter
        defaultCategoriesAdapter = new CategoryAdapter();
        rvDefaultCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        rvDefaultCategories.setAdapter(defaultCategoriesAdapter);

        // Custom categories adapter
        customCategoriesAdapter = new CategoryAdapter();
        rvCustomCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCustomCategories.setAdapter(customCategoriesAdapter);

        // Set click listeners for custom categories only
        customCategoriesAdapter.setOnEditClickListener(this::showEditCategoryDialog);
        customCategoriesAdapter.setOnDeleteClickListener(this::showDeleteCategoryDialog);
    }

    private void setupClickListeners() {
        // Category type filter
        toggleCategoryType.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btn_all_categories) {
                    viewModel.loadCategories();
                } else if (checkedId == R.id.btn_expense_categories) {
                    viewModel.loadCategoriesByType(CategoryType.EXPENSE);
                } else if (checkedId == R.id.btn_income_categories) {
                    viewModel.loadCategoriesByType(CategoryType.INCOME);
                }
            }
        });

        // Add category button
        btnAddCategory.setOnClickListener(v -> showAddCategoryDialog());
    }

    @Override
    protected void observeViewModel() {
        // All categories (for filtering)
        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                updateCategoryLists(categories);
            }
        });

        // Default categories
        viewModel.getDefaultCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                defaultCategoriesAdapter.submitList(categories);
                tvNoDefaultCategories.setVisibility(categories.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });

        // Custom categories
        viewModel.getCustomCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                customCategoriesAdapter.submitList(categories);
                tvNoCustomCategories.setVisibility(categories.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });

        // Loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        // Error messages
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                showError(error);
            }
        });

        // Operation success
        viewModel.getOperationSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                // Show success message
                showToast(getString(R.string.category_added));
                // Refresh data after successful operation
                refreshData();
            }
        });
    }

    private void updateCategoryLists(List<Category> allCategories) {
        // Separate default and custom categories
        List<Category> defaultCategories = allCategories.stream()
                .filter(Category::isDefault)
                .collect(java.util.stream.Collectors.toList());

        List<Category> customCategories = allCategories.stream()
                .filter(category -> !category.isDefault())
                .collect(java.util.stream.Collectors.toList());

        defaultCategoriesAdapter.submitList(defaultCategories);
        customCategoriesAdapter.submitList(customCategories);

        tvNoDefaultCategories.setVisibility(defaultCategories.isEmpty() ? View.VISIBLE : View.GONE);
        tvNoCustomCategories.setVisibility(customCategories.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showAddCategoryDialog() {
        CategoryDialogFragment dialog = CategoryDialogFragment.newInstance();
        dialog.show(getParentFragmentManager(), "add_category_dialog");
    }

    private void showEditCategoryDialog(Category category) {
        CategoryDialogFragment dialog = CategoryDialogFragment.newInstance(category);
        dialog.show(getParentFragmentManager(), "edit_category_dialog");
    }

    private void showDeleteCategoryDialog(Category category) {
        if (category.isDefault()) {
            showError(getString(R.string.default_categories_cannot_be_deleted));
            return;
        }

        new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.delete_category))
                .setMessage(getString(R.string.delete_category_confirmation))
                .setPositiveButton(getString(R.string.delete), (dialog, which) -> {
                    viewModel.deleteCategory(category.getId());
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .show();
    }

    private void refreshData() {
        // Check which filter is currently selected and refresh accordingly
        int checkedId = toggleCategoryType.getCheckedButtonId();
        if (checkedId == R.id.btn_all_categories) {
            viewModel.loadCategories();
        } else if (checkedId == R.id.btn_expense_categories) {
            viewModel.loadCategoriesByType(CategoryType.EXPENSE);
        } else if (checkedId == R.id.btn_income_categories) {
            viewModel.loadCategoriesByType(CategoryType.INCOME);
        }

        // Also refresh separate lists
        viewModel.loadDefaultCategories();
        viewModel.loadCustomCategories();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }
} 