package com.example.walletapplication.presentation.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.walletapplication.R;
import com.example.walletapplication.domain.entity.Category;
import com.example.walletapplication.domain.entity.CategoryType;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.function.Consumer;

public class CategoryAdapter extends ListAdapter<Category, CategoryAdapter.CategoryViewHolder> {

    private Consumer<Category> onEditClickListener;
    private Consumer<Category> onDeleteClickListener;

    public CategoryAdapter() {
        super(new CategoryDiffCallback());
    }

    public void setOnEditClickListener(Consumer<Category> listener) {
        this.onEditClickListener = listener;
    }

    public void setOnDeleteClickListener(Consumer<Category> listener) {
        this.onDeleteClickListener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = getItem(position);
        holder.bind(category);
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final View viewCategoryColor;
        private final ImageView ivCategoryIcon;
        private final MaterialTextView tvCategoryName;
        private final MaterialTextView tvCategoryType;
        private final MaterialTextView tvCategoryDefault;
        private final MaterialButton btnEditCategory;
        private final MaterialButton btnDeleteCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            viewCategoryColor = itemView.findViewById(R.id.view_category_color);
            ivCategoryIcon = itemView.findViewById(R.id.iv_category_icon);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
            tvCategoryType = itemView.findViewById(R.id.tv_category_type);
            tvCategoryDefault = itemView.findViewById(R.id.tv_category_default);
            btnEditCategory = itemView.findViewById(R.id.btn_edit_category);
            btnDeleteCategory = itemView.findViewById(R.id.btn_delete_category);
        }

        public void bind(Category category) {
            // Set category name
            tvCategoryName.setText(category.getDisplayName());

            // Set category type
            String typeText = getCategoryTypeText(category.getType());
            tvCategoryType.setText(typeText);

            // Set category color
            try {
                int color = Color.parseColor(category.getColor());
                viewCategoryColor.setBackgroundColor(color);
            } catch (IllegalArgumentException e) {
                // Fallback to default color if parsing fails
                viewCategoryColor.setBackgroundColor(itemView.getContext().getColor(R.color.primary_500));
            }

            // Set category icon (for now, use default icon)
            ivCategoryIcon.setImageResource(R.drawable.ic_category);

            // Show/hide default label
            if (category.isDefault()) {
                tvCategoryDefault.setVisibility(View.VISIBLE);
                btnDeleteCategory.setVisibility(View.GONE); // Default categories cannot be deleted
            } else {
                tvCategoryDefault.setVisibility(View.GONE);
                btnDeleteCategory.setVisibility(View.VISIBLE);
            }

            // Set click listeners
            btnEditCategory.setOnClickListener(v -> {
                if (onEditClickListener != null) {
                    onEditClickListener.accept(category);
                }
            });

            btnDeleteCategory.setOnClickListener(v -> {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.accept(category);
                }
            });
        }

        private String getCategoryTypeText(CategoryType type) {
            switch (type) {
                case INCOME:
                    return itemView.getContext().getString(R.string.income);
                case EXPENSE:
                    return itemView.getContext().getString(R.string.expense);
                case BOTH:
                    return itemView.getContext().getString(R.string.income) + "/" + 
                           itemView.getContext().getString(R.string.expense);
                default:
                    return "";
            }
        }
    }

    private static class CategoryDiffCallback extends DiffUtil.ItemCallback<Category> {
        @Override
        public boolean areItemsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
            return oldItem.getId() != null && oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                   oldItem.getDisplayName().equals(newItem.getDisplayName()) &&
                   oldItem.getType() == newItem.getType() &&
                   oldItem.getColor().equals(newItem.getColor()) &&
                   oldItem.isDefault() == newItem.isDefault();
        }
    }
} 