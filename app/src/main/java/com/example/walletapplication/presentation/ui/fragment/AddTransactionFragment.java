package com.example.walletapplication.presentation.ui.fragment;

import android.app.DatePickerDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.walletapplication.R;
import com.example.walletapplication.domain.entity.TransactionType;
import com.example.walletapplication.presentation.base.BaseFragment;
import com.example.walletapplication.presentation.viewmodel.AddTransactionViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AddTransactionFragment extends BaseFragment {

    private AddTransactionViewModel viewModel;
    
    // UI Components
    private MaterialButtonToggleGroup toggleTransactionType;
    private MaterialButton btnExpense, btnIncome;
    private MaterialCardView cardExpenseFields, cardIncomeFields;
    
    private TextInputLayout tilAmount, tilDescription, tilCategory, tilDate;
    private TextInputLayout tilPaymentMethod, tilVendor, tilSource, tilIncomeType;
    
    private TextInputEditText etAmount, etDescription, etDate;
    private TextInputEditText etPaymentMethod, etVendor, etSource, etIncomeType;
    private AutoCompleteTextView etCategory;
    
    private MaterialButton btnCancel, btnSave;
    private CircularProgressIndicator progressBar;
    
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_add_transaction;
    }

    @Override
    protected void initializeViews(View view) {
        viewModel = new ViewModelProvider(this).get(AddTransactionViewModel.class);
        
        // Initialize UI components
        toggleTransactionType = view.findViewById(R.id.toggle_transaction_type);
        btnExpense = view.findViewById(R.id.btn_expense);
        btnIncome = view.findViewById(R.id.btn_income);
        cardExpenseFields = view.findViewById(R.id.card_expense_fields);
        cardIncomeFields = view.findViewById(R.id.card_income_fields);
        
        tilAmount = view.findViewById(R.id.til_amount);
        tilDescription = view.findViewById(R.id.til_description);
        tilCategory = view.findViewById(R.id.til_category);
        tilDate = view.findViewById(R.id.til_date);
        tilPaymentMethod = view.findViewById(R.id.til_payment_method);
        tilVendor = view.findViewById(R.id.til_vendor);
        tilSource = view.findViewById(R.id.til_source);
        tilIncomeType = view.findViewById(R.id.til_income_type);
        
        etAmount = view.findViewById(R.id.et_amount);
        etDescription = view.findViewById(R.id.et_description);
        etCategory = view.findViewById(R.id.et_category);
        etDate = view.findViewById(R.id.et_date);
        etPaymentMethod = view.findViewById(R.id.et_payment_method);
        etVendor = view.findViewById(R.id.et_vendor);
        etSource = view.findViewById(R.id.et_source);
        etIncomeType = view.findViewById(R.id.et_income_type);
        
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnSave = view.findViewById(R.id.btn_save);
        progressBar = view.findViewById(R.id.progress_bar);
        
        setupClickListeners();
        setupInitialState();
        setTitle(getString(R.string.add_transaction));
    }

    private void setupClickListeners() {
        // Transaction type toggle
        toggleTransactionType.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                TransactionType type = checkedId == R.id.btn_expense ? 
                    TransactionType.EXPENSE : TransactionType.INCOME;
                viewModel.setTransactionType(type);
                updateFieldsVisibility(type);
            }
        });
        
        // Date picker
        etDate.setOnClickListener(v -> showDatePicker());
        
        // Buttons
        btnCancel.setOnClickListener(v -> {
            Navigation.findNavController(v).navigateUp();
        });
        btnSave.setOnClickListener(v -> saveTransaction());
        
        // Text change listeners
        etAmount.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setAmount(s.toString());
                clearError(tilAmount);
            }
        });
        
        etDescription.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setDescription(s.toString());
                clearError(tilDescription);
            }
        });
        
        etCategory.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setCategory(s.toString());
                clearError(tilCategory);
            }
        });
        
        etPaymentMethod.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setPaymentMethod(s.toString());
            }
        });
        
        etVendor.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setVendor(s.toString());
            }
        });
        
        etSource.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setSource(s.toString());
            }
        });
        
        etIncomeType.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setIncomeType(s.toString());
            }
        });
    }

    private void setupInitialState() {
        // Default to expense
        btnExpense.setChecked(true);
        viewModel.setTransactionType(TransactionType.EXPENSE);
        updateFieldsVisibility(TransactionType.EXPENSE);
        
        // Set current date
        LocalDateTime now = LocalDateTime.now();
        etDate.setText(now.format(dateFormatter));
        viewModel.setDate(now);
    }

    private void updateFieldsVisibility(TransactionType type) {
        if (type == TransactionType.EXPENSE) {
            cardExpenseFields.setVisibility(View.VISIBLE);
            cardIncomeFields.setVisibility(View.GONE);
        } else {
            cardExpenseFields.setVisibility(View.GONE);
            cardIncomeFields.setVisibility(View.VISIBLE);
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            getContext(),
            (view, year, month, dayOfMonth) -> {
                LocalDateTime selectedDate = LocalDateTime.of(year, month + 1, dayOfMonth, 0, 0);
                etDate.setText(selectedDate.format(dateFormatter));
                viewModel.setDate(selectedDate);
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        datePickerDialog.show();
    }

    private void saveTransaction() {
        clearAllErrors();
        viewModel.addTransaction();
    }

    private void clearAllErrors() {
        clearError(tilAmount);
        clearError(tilDescription);
        clearError(tilCategory);
        clearError(tilDate);
    }

    private void clearError(TextInputLayout textInputLayout) {
        textInputLayout.setError(null);
        textInputLayout.setErrorEnabled(false);
    }

    private void showError(TextInputLayout textInputLayout, String error) {
        textInputLayout.setError(error);
        textInputLayout.setErrorEnabled(true);
    }

    @Override
    protected void observeViewModel() {
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                showErrorBasedOnMessage(error);
            }
        });
        
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            btnSave.setEnabled(!isLoading);
        });
        
        viewModel.getTransactionAdded().observe(getViewLifecycleOwner(), isAdded -> {
            if (isAdded) {
                showSuccessSnackbar(getString(R.string.transaction_added));
                Navigation.findNavController(requireView()).navigateUp();
            }
        });
        
        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null && !categories.isEmpty()) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), 
                    android.R.layout.simple_dropdown_item_1line, categories);
                etCategory.setAdapter(adapter);
            }
        });
    }

    private void showErrorBasedOnMessage(String error) {
        if (error.contains("amount") || error.contains("tutar")) {
            showError(tilAmount, error);
        } else if (error.contains("description") || error.contains("açıklama")) {
            showError(tilDescription, error);
        } else if (error.contains("category") || error.contains("kategori")) {
            showError(tilCategory, error);
        } else if (error.contains("date") || error.contains("tarih")) {
            showError(tilDate, error);
        } else if (error.contains("Beklenmeyen hata") || error.contains("ağ") || error.contains("veritabanı")) {
            // Show retry option for network/database errors
            showRetrySnackbar(error, () -> viewModel.addTransaction());
        } else {
            showErrorSnackbar(error);
        }
    }

    // Simple TextWatcher implementation
    private abstract static class SimpleTextWatcher implements android.text.TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(android.text.Editable s) {}
    }
} 