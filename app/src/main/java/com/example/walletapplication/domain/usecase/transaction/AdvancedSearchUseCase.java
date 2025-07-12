package com.example.walletapplication.domain.usecase.transaction;

import com.example.walletapplication.domain.entity.Transaction;
import com.example.walletapplication.domain.entity.TransactionType;
import com.example.walletapplication.domain.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Advanced search use case that allows combining multiple search criteria
 * Supports text search, category, type, amount range, and date range filters
 */
public class AdvancedSearchUseCase {
    
    private final TransactionRepository transactionRepository;
    
    public AdvancedSearchUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }
    
    /**
     * Search criteria container
     */
    public static class SearchCriteria {
        private String textQuery;
        private String category;
        private TransactionType type;
        private BigDecimal minAmount;
        private BigDecimal maxAmount;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        
        public SearchCriteria() {}
        
        public SearchCriteria setTextQuery(String textQuery) {
            this.textQuery = textQuery;
            return this;
        }
        
        public SearchCriteria setCategory(String category) {
            this.category = category;
            return this;
        }
        
        public SearchCriteria setType(TransactionType type) {
            this.type = type;
            return this;
        }
        
        public SearchCriteria setAmountRange(BigDecimal minAmount, BigDecimal maxAmount) {
            this.minAmount = minAmount;
            this.maxAmount = maxAmount;
            return this;
        }
        
        public SearchCriteria setDateRange(LocalDateTime startDate, LocalDateTime endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
            return this;
        }
        
        public String getTextQuery() { return textQuery; }
        public String getCategory() { return category; }
        public TransactionType getType() { return type; }
        public BigDecimal getMinAmount() { return minAmount; }
        public BigDecimal getMaxAmount() { return maxAmount; }
        public LocalDateTime getStartDate() { return startDate; }
        public LocalDateTime getEndDate() { return endDate; }
        
        public boolean isEmpty() {
            return (textQuery == null || textQuery.trim().isEmpty()) &&
                   (category == null || category.trim().isEmpty()) &&
                   type == null &&
                   minAmount == null &&
                   maxAmount == null &&
                   startDate == null &&
                   endDate == null;
        }
        
        public boolean hasTextQuery() {
            return textQuery != null && !textQuery.trim().isEmpty();
        }
        
        public boolean hasCategory() {
            return category != null && !category.trim().isEmpty();
        }
        
        public boolean hasType() {
            return type != null;
        }
        
        public boolean hasAmountRange() {
            return minAmount != null || maxAmount != null;
        }
        
        public boolean hasDateRange() {
            return startDate != null || endDate != null;
        }
    }
    
    /**
     * Performs advanced search with multiple criteria
     * @param criteria Search criteria
     * @return CompletableFuture with filtered transactions
     */
    public CompletableFuture<List<Transaction>> search(SearchCriteria criteria) {
        if (criteria == null || criteria.isEmpty()) {
            return transactionRepository.getAllTransactions();
        }
        
        // Start with all transactions and apply filters progressively
        return transactionRepository.getAllTransactions()
            .thenApply(transactions -> {
                List<Transaction> filteredTransactions = transactions;
                
                // Apply text search filter
                if (criteria.hasTextQuery()) {
                    final String query = criteria.getTextQuery().toLowerCase().trim();
                    filteredTransactions = filteredTransactions.stream()
                        .filter(transaction -> 
                            transaction.getDescription().toLowerCase().contains(query) ||
                            transaction.getCategory().toLowerCase().contains(query)
                        )
                        .collect(Collectors.toList());
                }
                
                // Apply category filter
                if (criteria.hasCategory()) {
                    final String category = criteria.getCategory();
                    filteredTransactions = filteredTransactions.stream()
                        .filter(transaction -> transaction.getCategory().equalsIgnoreCase(category))
                        .collect(Collectors.toList());
                }
                
                // Apply type filter
                if (criteria.hasType()) {
                    final TransactionType type = criteria.getType();
                    filteredTransactions = filteredTransactions.stream()
                        .filter(transaction -> transaction.getType() == type)
                        .collect(Collectors.toList());
                }
                
                // Apply amount range filter
                if (criteria.hasAmountRange()) {
                    filteredTransactions = filteredTransactions.stream()
                        .filter(transaction -> {
                            BigDecimal amount = transaction.getAmount();
                            boolean minOk = criteria.getMinAmount() == null || 
                                          amount.compareTo(criteria.getMinAmount()) >= 0;
                            boolean maxOk = criteria.getMaxAmount() == null || 
                                          amount.compareTo(criteria.getMaxAmount()) <= 0;
                            return minOk && maxOk;
                        })
                        .collect(Collectors.toList());
                }
                
                // Apply date range filter
                if (criteria.hasDateRange()) {
                    filteredTransactions = filteredTransactions.stream()
                        .filter(transaction -> {
                            LocalDateTime date = transaction.getDate();
                            boolean startOk = criteria.getStartDate() == null || 
                                            !date.isBefore(criteria.getStartDate());
                            boolean endOk = criteria.getEndDate() == null || 
                                          !date.isAfter(criteria.getEndDate());
                            return startOk && endOk;
                        })
                        .collect(Collectors.toList());
                }
                
                return filteredTransactions;
            });
    }
    
    /**
     * Quick search with text query only
     * @param query Text to search for
     * @return CompletableFuture with matching transactions
     */
    public CompletableFuture<List<Transaction>> quickSearch(String query) {
        SearchCriteria criteria = new SearchCriteria().setTextQuery(query);
        return search(criteria);
    }
    
    /**
     * Search by category and type combination
     * @param category Category to filter by
     * @param type Transaction type to filter by
     * @return CompletableFuture with matching transactions
     */
    public CompletableFuture<List<Transaction>> searchByCategoryAndType(String category, TransactionType type) {
        SearchCriteria criteria = new SearchCriteria()
            .setCategory(category)
            .setType(type);
        return search(criteria);
    }
    
    /**
     * Search by amount range
     * @param minAmount Minimum amount (inclusive)
     * @param maxAmount Maximum amount (inclusive)
     * @return CompletableFuture with matching transactions
     */
    public CompletableFuture<List<Transaction>> searchByAmountRange(BigDecimal minAmount, BigDecimal maxAmount) {
        SearchCriteria criteria = new SearchCriteria()
            .setAmountRange(minAmount, maxAmount);
        return search(criteria);
    }
    
    /**
     * Search by date range
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return CompletableFuture with matching transactions
     */
    public CompletableFuture<List<Transaction>> searchByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        SearchCriteria criteria = new SearchCriteria()
            .setDateRange(startDate, endDate);
        return search(criteria);
    }
} 