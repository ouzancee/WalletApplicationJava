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
import com.example.walletapplication.domain.entity.Transaction;
import com.example.walletapplication.domain.entity.TransactionType;
import com.example.walletapplication.presentation.util.TextHighlighter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

public class TransactionAdapter extends ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder> {

    private OnTransactionClickListener clickListener;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
    private String searchQuery = ""; // Current search query for highlighting

    public interface OnTransactionClickListener {
        void onTransactionClick(Transaction transaction);
        void onTransactionLongClick(Transaction transaction);
    }

    public TransactionAdapter() {
        super(DIFF_CALLBACK);
    }

    public void setOnTransactionClickListener(OnTransactionClickListener listener) {
        this.clickListener = listener;
    }

    // For compatibility with fragments
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.itemLongClickListener = listener;
    }

    /**
     * Sets the search query for highlighting text in search results
     * @param query Current search query
     */
    public void setSearchQuery(String query) {
        this.searchQuery = query != null ? query : "";
        notifyDataSetChanged(); // Refresh to apply highlighting
    }

    /**
     * Clears the search query and removes highlighting
     */
    public void clearSearchQuery() {
        setSearchQuery("");
    }

    private OnItemClickListener itemClickListener;
    private OnItemLongClickListener itemLongClickListener;

    public interface OnItemClickListener {
        void onItemClick(Transaction transaction);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(Transaction transaction);
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = getItem(position);
        holder.bind(transaction);
    }

    class TransactionViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivTransactionType;
        private TextView tvDescription;
        private TextView tvCategory;
        private TextView tvDate;
        private TextView tvAmount;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            ivTransactionType = itemView.findViewById(R.id.iv_transaction_type);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvAmount = itemView.findViewById(R.id.tv_amount);

            itemView.setOnClickListener(v -> {
                if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                    Transaction transaction = getItem(getAdapterPosition());
                    if (clickListener != null) {
                        clickListener.onTransactionClick(transaction);
                    }
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(transaction);
                    }
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                    Transaction transaction = getItem(getAdapterPosition());
                    boolean handled = false;
                    if (clickListener != null) {
                        clickListener.onTransactionLongClick(transaction);
                        handled = true;
                    }
                    if (itemLongClickListener != null) {
                        handled = itemLongClickListener.onItemLongClick(transaction);
                    }
                    return handled;
                }
                return false;
            });
        }

        public void bind(Transaction transaction) {
            // Apply highlighting if search query exists
            if (searchQuery.isEmpty()) {
                // No search query, show normal text
                tvDescription.setText(transaction.getDescription());
                tvCategory.setText(transaction.getCategory());
            } else {
                // Apply highlighting to description and category
                tvDescription.setText(TextHighlighter.highlightSubtle(
                    transaction.getDescription(), searchQuery));
                tvCategory.setText(TextHighlighter.highlightSubtle(
                    transaction.getCategory(), searchQuery));
            }
            
            tvDate.setText(transaction.getDate().format(dateFormatter));

            // Format amount with currency symbol
            String amountText = "₺" + decimalFormat.format(transaction.getAmount());
            tvAmount.setText(amountText);

            // Set colors and icons based on transaction type
            if (transaction.getType() == TransactionType.INCOME) {
                tvAmount.setTextColor(itemView.getContext().getColor(R.color.income_green));
                ivTransactionType.setImageResource(android.R.drawable.arrow_up_float);
                ivTransactionType.setColorFilter(itemView.getContext().getColor(R.color.income_green));
            } else {
                tvAmount.setTextColor(itemView.getContext().getColor(R.color.expense_red));
                ivTransactionType.setImageResource(android.R.drawable.arrow_down_float);
                ivTransactionType.setColorFilter(itemView.getContext().getColor(R.color.expense_red));
            }
        }
    }

    private static final DiffUtil.ItemCallback<Transaction> DIFF_CALLBACK = new DiffUtil.ItemCallback<Transaction>() {
        @Override
        public boolean areItemsTheSame(@NonNull Transaction oldItem, @NonNull Transaction newItem) {
            return oldItem.getId() != null && oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Transaction oldItem, @NonNull Transaction newItem) {
            return oldItem.equals(newItem);
        }
    };
} 