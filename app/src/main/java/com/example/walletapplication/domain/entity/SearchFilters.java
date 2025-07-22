package com.example.walletapplication.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Data class representing search filters for the SearchBar component.
 * Provides validation logic and methods to check filter state.
 */
public class SearchFilters {
    private final TransactionType type;
    private final String category;
    private final BigDecimal minAmount;
    private final BigDecimal maxAmount;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public SearchFilters(TransactionType type, String category, BigDecimal minAmount, 
                        BigDecimal maxAmount, LocalDateTime startDate, LocalDateTime endDate) {
        this.type = type;
        this.category = category;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public TransactionType getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    /**
     * Checks if any filters are currently active.
     * @return true if at least one filter is set, false otherwise
     */
    public boolean hasActiveFilters() {
        return type != null || 
               (category != null && !category.trim().isEmpty()) ||
               minAmount != null || 
               maxAmount != null || 
               startDate != null || 
               endDate != null;
    }

    /**
     * Gets the count of active filters.
     * @return number of active filters
     */
    public int getActiveFilterCount() {
        int count = 0;
        if (type != null) count++;
        if (category != null && !category.trim().isEmpty()) count++;
        if (minAmount != null) count++;
        if (maxAmount != null) count++;
        if (startDate != null) count++;
        if (endDate != null) count++;
        return count;
    }

    /**
     * Validates the filter parameters.
     * @return true if all filters are valid, false otherwise
     */
    public boolean isValid() {
        // Validate amount range
        if (minAmount != null && maxAmount != null) {
            if (minAmount.compareTo(maxAmount) > 0) {
                return false;
            }
        }
        
        // Validate amounts are not negative
        if (minAmount != null && minAmount.compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }
        if (maxAmount != null && maxAmount.compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }
        
        // Validate date range
        if (startDate != null && endDate != null) {
            if (startDate.isAfter(endDate)) {
                return false;
            }
        }
        
        // Validate dates are not in the future
        LocalDateTime now = LocalDateTime.now();
        if (startDate != null && startDate.isAfter(now)) {
            return false;
        }
        if (endDate != null && endDate.isAfter(now)) {
            return false;
        }
        
        return true;
    }

    /**
     * Creates a new SearchFilters instance with all filters cleared.
     * @return empty SearchFilters instance
     */
    public static SearchFilters empty() {
        return new SearchFilters(null, null, null, null, null, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchFilters that = (SearchFilters) o;
        return type == that.type &&
               Objects.equals(category, that.category) &&
               Objects.equals(minAmount, that.minAmount) &&
               Objects.equals(maxAmount, that.maxAmount) &&
               Objects.equals(startDate, that.startDate) &&
               Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, category, minAmount, maxAmount, startDate, endDate);
    }

    @Override
    public String toString() {
        return "SearchFilters{" +
                "type=" + type +
                ", category='" + category + '\'' +
                ", minAmount=" + minAmount +
                ", maxAmount=" + maxAmount +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", hasActiveFilters=" + hasActiveFilters() +
                '}';
    }

    /**
     * Builder class for creating SearchFilters instances.
     */
    public static class Builder {
        private TransactionType type;
        private String category;
        private BigDecimal minAmount;
        private BigDecimal maxAmount;
        private LocalDateTime startDate;
        private LocalDateTime endDate;

        public Builder setType(TransactionType type) {
            this.type = type;
            return this;
        }

        public Builder setCategory(String category) {
            this.category = category;
            return this;
        }

        public Builder setMinAmount(BigDecimal minAmount) {
            this.minAmount = minAmount;
            return this;
        }

        public Builder setMaxAmount(BigDecimal maxAmount) {
            this.maxAmount = maxAmount;
            return this;
        }

        public Builder setStartDate(LocalDateTime startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder setEndDate(LocalDateTime endDate) {
            this.endDate = endDate;
            return this;
        }

        public SearchFilters build() {
            return new SearchFilters(type, category, minAmount, maxAmount, startDate, endDate);
        }
    }
}