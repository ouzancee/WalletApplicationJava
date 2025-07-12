package com.example.walletapplication.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Backup representation of a transaction
 * Flattened structure for easy JSON serialization
 */
public class BackupTransaction {
    private final Long id;
    private final BigDecimal amount;
    private final String description;
    private final String category;
    private final LocalDateTime date;
    private final String type; // INCOME or EXPENSE as string
    
    // Expense specific fields
    private final String paymentMethod;
    private final String vendor;
    
    // Income specific fields
    private final String source;
    private final String incomeType;

    private BackupTransaction(Long id, BigDecimal amount, String description, String category, 
                             LocalDateTime date, String type, String paymentMethod, String vendor, 
                             String source, String incomeType) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.date = date;
        this.type = type;
        this.paymentMethod = paymentMethod;
        this.vendor = vendor;
        this.source = source;
        this.incomeType = incomeType;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getVendor() {
        return vendor;
    }

    public String getSource() {
        return source;
    }

    public String getIncomeType() {
        return incomeType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BackupTransaction that = (BackupTransaction) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(amount, that.amount) &&
               Objects.equals(description, that.description) &&
               Objects.equals(category, that.category) &&
               Objects.equals(date, that.date) &&
               Objects.equals(type, that.type) &&
               Objects.equals(paymentMethod, that.paymentMethod) &&
               Objects.equals(vendor, that.vendor) &&
               Objects.equals(source, that.source) &&
               Objects.equals(incomeType, that.incomeType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, description, category, date, type, paymentMethod, vendor, source, incomeType);
    }

    @Override
    public String toString() {
        return "BackupTransaction{" +
                "id=" + id +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", date=" + date +
                ", type='" + type + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", vendor='" + vendor + '\'' +
                ", source='" + source + '\'' +
                ", incomeType='" + incomeType + '\'' +
                '}';
    }

    public static class Builder {
        private Long id;
        private BigDecimal amount;
        private String description;
        private String category;
        private LocalDateTime date;
        private String type;
        private String paymentMethod;
        private String vendor;
        private String source;
        private String incomeType;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setAmount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setCategory(String category) {
            this.category = category;
            return this;
        }

        public Builder setDate(LocalDateTime date) {
            this.date = date;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public Builder setVendor(String vendor) {
            this.vendor = vendor;
            return this;
        }

        public Builder setSource(String source) {
            this.source = source;
            return this;
        }

        public Builder setIncomeType(String incomeType) {
            this.incomeType = incomeType;
            return this;
        }

        public BackupTransaction build() {
            return new BackupTransaction(id, amount, description, category, date, type, 
                                       paymentMethod, vendor, source, incomeType);
        }
    }
} 