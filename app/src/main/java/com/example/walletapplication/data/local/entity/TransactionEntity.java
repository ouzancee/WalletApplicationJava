package com.example.walletapplication.data.local.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.walletapplication.data.local.converter.DateConverter;
import com.example.walletapplication.data.local.converter.BigDecimalConverter;
import com.example.walletapplication.domain.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity(tableName = "transactions")
@TypeConverters({DateConverter.class, BigDecimalConverter.class})
public class TransactionEntity {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    
    private BigDecimal amount;
    private String description;
    private String category;
    private LocalDateTime date;
    private TransactionType type;
    
    // Expense specific fields
    private String paymentMethod;
    private String vendor;
    
    // Income specific fields
    private String source;
    private String incomeType;

    public TransactionEntity() {
    }

    @Ignore
    public TransactionEntity(BigDecimal amount, String description, String category, 
                           LocalDateTime date, TransactionType type, String paymentMethod, 
                           String vendor, String source, String incomeType) {
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

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getIncomeType() {
        return incomeType;
    }

    public void setIncomeType(String incomeType) {
        this.incomeType = incomeType;
    }
} 