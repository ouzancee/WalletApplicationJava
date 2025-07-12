package com.example.walletapplication.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Expense extends Transaction {
    private final String paymentMethod;
    private final String vendor;

    public Expense(Long id, BigDecimal amount, String description, String category, 
                   LocalDateTime date, String paymentMethod, String vendor) {
        super(id, amount, description, category, date, TransactionType.EXPENSE);
        this.paymentMethod = paymentMethod;
        this.vendor = vendor;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getVendor() {
        return vendor;
    }

    public static class Builder {
        private Long id;
        private BigDecimal amount;
        private String description;
        private String category;
        private LocalDateTime date;
        private String paymentMethod;
        private String vendor;

        public Builder() {
            // Constructor for Expense Builder
        }

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

        public Builder setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public Builder setVendor(String vendor) {
            this.vendor = vendor;
            return this;
        }

        public Expense build() {
            return new Expense(id, amount, description, category, date, paymentMethod, vendor);
        }
    }
} 