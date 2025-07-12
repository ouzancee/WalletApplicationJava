package com.example.walletapplication.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public abstract class Transaction {
    private final Long id;
    private final BigDecimal amount;
    private final String description;
    private final String category;
    private final LocalDateTime date;
    private final TransactionType type;

    protected Transaction(Long id, BigDecimal amount, String description, 
                         String category, LocalDateTime date, TransactionType type) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.date = date;
        this.type = type;
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

    public TransactionType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(amount, that.amount) &&
               Objects.equals(description, that.description) &&
               Objects.equals(category, that.category) &&
               Objects.equals(date, that.date) &&
               type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, description, category, date, type);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", date=" + date +
                ", type=" + type +
                '}';
    }
}