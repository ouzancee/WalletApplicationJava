package com.example.walletapplication.presentation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.walletapplication.R;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;

public class CategoryBreakdownAdapter extends ListAdapter<CategoryBreakdownAdapter.CategoryItem, CategoryBreakdownAdapter.CategoryViewHolder> {

    private final DecimalFormat currencyFormatter;

    public CategoryBreakdownAdapter() {
        super(new CategoryDiffCallback());
        
        // Setup currency formatter
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("tr", "TR"));
        symbols.setCurrencySymbol("₺");
        currencyFormatter = new DecimalFormat("#,##0.00", symbols);
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_breakdown, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryItem item = getItem(position);
        holder.bind(item);
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivCategoryIcon;
        private final TextView tvCategoryName;
        private final TextView tvTransactionCount;
        private final TextView tvCategoryAmount;
        private final TextView tvCategoryPercentage;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            
            ivCategoryIcon = itemView.findViewById(R.id.ivCategoryIcon);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvTransactionCount = itemView.findViewById(R.id.tvTransactionCount);
            tvCategoryAmount = itemView.findViewById(R.id.tvCategoryAmount);
            tvCategoryPercentage = itemView.findViewById(R.id.tvCategoryPercentage);
        }

        public void bind(CategoryItem item) {
            tvCategoryName.setText(item.getCategoryName());
            tvCategoryAmount.setText("₺" + currencyFormatter.format(item.getAmount()));
            tvCategoryPercentage.setText(String.format(Locale.getDefault(), "%.1f%%", item.getPercentage()));
            
            // Set transaction count text
            String transactionText = item.getTransactionCount() == 1 ? 
                "1 işlem" : item.getTransactionCount() + " işlem";
            tvTransactionCount.setText(transactionText);
            
            // Set category icon based on category name
            int iconRes = getCategoryIcon(item.getCategoryName());
            ivCategoryIcon.setImageResource(iconRes);
            
            // Set icon color based on category
            int colorRes = getCategoryColor(item.getCategoryName());
            ivCategoryIcon.setColorFilter(itemView.getContext().getColor(colorRes));
        }

        private int getCategoryIcon(String categoryName) {
            switch (categoryName.toLowerCase()) {
                case "yemek":
                case "food":
                    return R.drawable.ic_category;
                case "ulaşım":
                case "transport":
                    return R.drawable.ic_category;
                case "faturalar":
                case "utilities":
                    return R.drawable.ic_payment;
                case "eğlence":
                case "entertainment":
                    return R.drawable.ic_category;
                case "alışveriş":
                case "shopping":
                    return R.drawable.ic_store;
                case "sağlık":
                case "health":
                    return R.drawable.ic_category;
                case "eğitim":
                case "education":
                    return R.drawable.ic_category;
                case "maaş":
                case "salary":
                    return R.drawable.ic_income_type;
                case "iş":
                case "business":
                    return R.drawable.ic_source;
                case "yatırım":
                case "investment":
                    return R.drawable.ic_money;
                default:
                    return R.drawable.ic_category;
            }
        }

        private int getCategoryColor(String categoryName) {
            switch (categoryName.toLowerCase()) {
                case "yemek":
                case "food":
                    return R.color.red_700;
                case "ulaşım":
                case "transport":
                    return R.color.blue_500;
                case "faturalar":
                case "utilities":
                    return R.color.red_700;
                case "eğlence":
                case "entertainment":
                    return R.color.blue_500;
                case "alışveriş":
                case "shopping":
                    return R.color.red_700;
                case "sağlık":
                case "health":
                    return R.color.red_700;
                case "eğitim":
                case "education":
                    return R.color.blue_500;
                case "maaş":
                case "salary":
                    return R.color.green_700;
                case "iş":
                case "business":
                    return R.color.green_700;
                case "yatırım":
                case "investment":
                    return R.color.green_700;
                default:
                    return R.color.gray_500;
            }
        }
    }

    public static class CategoryItem {
        private final String categoryName;
        private final BigDecimal amount;
        private final double percentage;
        private final int transactionCount;

        public CategoryItem(String categoryName, BigDecimal amount, double percentage, int transactionCount) {
            this.categoryName = categoryName;
            this.amount = amount;
            this.percentage = percentage;
            this.transactionCount = transactionCount;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public double getPercentage() {
            return percentage;
        }

        public int getTransactionCount() {
            return transactionCount;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CategoryItem that = (CategoryItem) o;
            return Double.compare(that.percentage, percentage) == 0 &&
                    transactionCount == that.transactionCount &&
                    Objects.equals(categoryName, that.categoryName) &&
                    Objects.equals(amount, that.amount);
        }

        @Override
        public int hashCode() {
            return Objects.hash(categoryName, amount, percentage, transactionCount);
        }
    }

    private static class CategoryDiffCallback extends DiffUtil.ItemCallback<CategoryItem> {
        @Override
        public boolean areItemsTheSame(@NonNull CategoryItem oldItem, @NonNull CategoryItem newItem) {
            return Objects.equals(oldItem.getCategoryName(), newItem.getCategoryName());
        }

        @Override
        public boolean areContentsTheSame(@NonNull CategoryItem oldItem, @NonNull CategoryItem newItem) {
            return oldItem.equals(newItem);
        }
    }
} 