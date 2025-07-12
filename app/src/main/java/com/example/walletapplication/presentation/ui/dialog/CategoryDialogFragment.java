package com.example.walletapplication.presentation.ui.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.walletapplication.R;
import com.example.walletapplication.domain.entity.Category;
import com.example.walletapplication.domain.entity.CategoryType;
import com.example.walletapplication.presentation.viewmodel.CategoryViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CategoryDialogFragment extends DialogFragment {

    private static final String ARG_CATEGORY = "category";
    private static final String ARG_IS_EDIT_MODE = "is_edit_mode";

    private CategoryViewModel viewModel;
    private Category categoryToEdit;
    private boolean isEditMode;

    // UI Components
    private MaterialTextView tvDialogTitle;
    private TextInputLayout tilCategoryName, tilCategoryDisplayName;
    private TextInputEditText etCategoryName, etCategoryDisplayName;
    private MaterialButtonToggleGroup toggleCategoryType;
    private MaterialButton btnExpenseType, btnIncomeType, btnBothType;
    private GridLayout gridColorPicker;
    private MaterialButton btnCancel, btnSave;

    // Color selection
    private String selectedColor = "#FF9800"; // Default color
    private View selectedColorView;

    // Predefined colors
    private final String[] colors = {
            "#FF9800", "#F44336", "#4CAF50", "#2196F3", "#9C27B0", "#FF5722",
            "#795548", "#607D8B", "#E91E63", "#3F51B5", "#009688", "#FFC107"
    };

    public static CategoryDialogFragment newInstance() {
        return new CategoryDialogFragment();
    }

    public static CategoryDialogFragment newInstance(Category category) {
        CategoryDialogFragment fragment = new CategoryDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CATEGORY, category);
        args.putBoolean(ARG_IS_EDIT_MODE, true);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(CategoryViewModel.class);

        if (getArguments() != null) {
            categoryToEdit = (Category) getArguments().getSerializable(ARG_CATEGORY);
            isEditMode = getArguments().getBoolean(ARG_IS_EDIT_MODE, false);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_category, null);
        initializeViews(view);
        setupClickListeners();
        setupColorPicker();
        
        if (isEditMode && categoryToEdit != null) {
            populateFields();
        }

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext())
                .setView(view);

        return builder.create();
    }

    private void initializeViews(View view) {
        tvDialogTitle = view.findViewById(R.id.tv_dialog_title);
        tilCategoryName = view.findViewById(R.id.til_category_name);
        tilCategoryDisplayName = view.findViewById(R.id.til_category_display_name);
        etCategoryName = view.findViewById(R.id.et_category_name);
        etCategoryDisplayName = view.findViewById(R.id.et_category_display_name);
        toggleCategoryType = view.findViewById(R.id.toggle_category_type);
        btnExpenseType = view.findViewById(R.id.btn_expense_type);
        btnIncomeType = view.findViewById(R.id.btn_income_type);
        btnBothType = view.findViewById(R.id.btn_both_type);
        gridColorPicker = view.findViewById(R.id.grid_color_picker);
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnSave = view.findViewById(R.id.btn_save);

        // Set title based on mode
        tvDialogTitle.setText(isEditMode ? R.string.edit_category : R.string.add_category);

        // Add text watchers for real-time validation
        etCategoryName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilCategoryName.setError(null);
                tilCategoryName.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etCategoryDisplayName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilCategoryDisplayName.setError(null);
                tilCategoryDisplayName.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupClickListeners() {
        btnCancel.setOnClickListener(v -> dismiss());
        btnSave.setOnClickListener(v -> saveCategory());
    }

    private void setupColorPicker() {
        gridColorPicker.removeAllViews();
        
        for (int i = 0; i < colors.length; i++) {
            String color = colors[i];
            View colorView = createColorView(color);
            
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = (int) (48 * getResources().getDisplayMetrics().density);
            params.height = (int) (48 * getResources().getDisplayMetrics().density);
            params.setMargins(8, 8, 8, 8);
            params.columnSpec = GridLayout.spec(i % 6);
            params.rowSpec = GridLayout.spec(i / 6);
            
            colorView.setLayoutParams(params);
            gridColorPicker.addView(colorView);
            
            // Set default selection
            if (color.equals(selectedColor)) {
                selectColor(colorView, color);
            }
        }
    }

    private View createColorView(String color) {
        MaterialCardView cardView = new MaterialCardView(requireContext());
        cardView.setRadius(24);
        cardView.setCardElevation(2);
        cardView.setClickable(true);
        cardView.setFocusable(true);
        
        View colorView = new View(requireContext());
        colorView.setBackgroundColor(Color.parseColor(color));
        
        cardView.addView(colorView);
        
        cardView.setOnClickListener(v -> selectColor(cardView, color));
        
        return cardView;
    }

    private void selectColor(View colorView, String color) {
        // Remove selection from previous color
        if (selectedColorView != null) {
            selectedColorView.setSelected(false);
            ((MaterialCardView) selectedColorView).setStrokeWidth(0);
        }
        
        // Select new color
        selectedColor = color;
        selectedColorView = colorView;
        colorView.setSelected(true);
        ((MaterialCardView) colorView).setStrokeWidth(4);
        ((MaterialCardView) colorView).setStrokeColor(Color.parseColor("#000000"));
    }

    private void populateFields() {
        if (categoryToEdit != null) {
            etCategoryName.setText(categoryToEdit.getName());
            etCategoryDisplayName.setText(categoryToEdit.getDisplayName());
            
            // Set category type
            switch (categoryToEdit.getType()) {
                case INCOME:
                    btnIncomeType.setChecked(true);
                    break;
                case EXPENSE:
                    btnExpenseType.setChecked(true);
                    break;
                case BOTH:
                    btnBothType.setChecked(true);
                    break;
            }
            
            // Set color
            selectedColor = categoryToEdit.getColor();
            setupColorPicker(); // Refresh color picker with correct selection
        }
    }

    private void saveCategory() {
        if (!validateForm()) {
            return;
        }

        String name = etCategoryName.getText().toString().trim();
        String displayName = etCategoryDisplayName.getText().toString().trim();
        CategoryType type = getSelectedCategoryType();

        if (isEditMode && categoryToEdit != null) {
            // Update existing category
            Category updatedCategory = new Category.Builder()
                    .setId(categoryToEdit.getId())
                    .setName(name)
                    .setDisplayName(displayName)
                    .setType(type)
                    .setColor(selectedColor)
                    .setIconName(categoryToEdit.getIconName())
                    .setIsDefault(categoryToEdit.isDefault())
                    .setCreatedAt(categoryToEdit.getCreatedAt())
                    .build();
            
            viewModel.updateCategory(updatedCategory);
        } else {
            // Add new category
            viewModel.setCategoryName(name);
            viewModel.setCategoryDisplayName(displayName);
            viewModel.setCategoryType(type);
            viewModel.setCategoryColor(selectedColor);
            viewModel.addCategory();
        }

        dismiss();
    }

    private boolean validateForm() {
        boolean isValid = true;

        String name = etCategoryName.getText().toString().trim();
        if (name.isEmpty()) {
            tilCategoryName.setError(getString(R.string.error_category_name_required));
            tilCategoryName.setErrorEnabled(true);
            isValid = false;
        }

        String displayName = etCategoryDisplayName.getText().toString().trim();
        if (displayName.isEmpty()) {
            tilCategoryDisplayName.setError(getString(R.string.error_category_display_name_required));
            tilCategoryDisplayName.setErrorEnabled(true);
            isValid = false;
        }

        return isValid;
    }

    private CategoryType getSelectedCategoryType() {
        int checkedId = toggleCategoryType.getCheckedButtonId();
        if (checkedId == R.id.btn_income_type) {
            return CategoryType.INCOME;
        } else if (checkedId == R.id.btn_both_type) {
            return CategoryType.BOTH;
        } else {
            return CategoryType.EXPENSE; // Default
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Make dialog wider
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.9),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }
} 