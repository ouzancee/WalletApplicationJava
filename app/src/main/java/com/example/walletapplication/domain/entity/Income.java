package com.example.walletapplication.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Income extends Transaction {
    private final String source;
    private final String incomeType;

    public Income(Long id, BigDecimal amount, String description, String category, 
                  LocalDateTime date, String source, String incomeType) {
        super(id, amount, description, category, date, TransactionType.INCOME);
        this.source = source;
        this.incomeType = incomeType;
    }

    public String getSource() {
        return source;
    }

    public String getIncomeType() {
        return incomeType;
    }

    public static class Builder {
        private Long id;
        private BigDecimal amount;
        private String description;
        private String category;
        private LocalDateTime date;
        private String source;
        private String incomeType;

        public Builder() {
            // Constructor for Income Builder
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

        public Builder setSource(String source) {
            this.source = source;
            return this;
        }

        public Builder setIncomeType(String incomeType) {
            this.incomeType = incomeType;
            return this;
        }

        public Income build() {
            return new Income(id, amount, description, category, date, source, incomeType);
        }
    }
} 