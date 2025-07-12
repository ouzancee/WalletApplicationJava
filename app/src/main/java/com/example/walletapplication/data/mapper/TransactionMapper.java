package com.example.walletapplication.data.mapper;

import com.example.walletapplication.data.local.entity.TransactionEntity;
import com.example.walletapplication.domain.entity.Expense;
import com.example.walletapplication.domain.entity.Income;
import com.example.walletapplication.domain.entity.Transaction;
import com.example.walletapplication.domain.entity.TransactionType;

import java.util.ArrayList;
import java.util.List;

public class TransactionMapper {
    
    public static TransactionEntity toEntity(Transaction transaction) {
        TransactionEntity entity = new TransactionEntity();
        entity.setId(transaction.getId());
        entity.setAmount(transaction.getAmount());
        entity.setDescription(transaction.getDescription());
        entity.setCategory(transaction.getCategory());
        entity.setDate(transaction.getDate());
        entity.setType(transaction.getType());
        
        if (transaction instanceof Expense) {
            Expense expense = (Expense) transaction;
            entity.setPaymentMethod(expense.getPaymentMethod());
            entity.setVendor(expense.getVendor());
        } else if (transaction instanceof Income) {
            Income income = (Income) transaction;
            entity.setSource(income.getSource());
            entity.setIncomeType(income.getIncomeType());
        }
        
        return entity;
    }
    
    public static Transaction toDomain(TransactionEntity entity) {
        if (entity.getType() == TransactionType.EXPENSE) {
            return new Expense.Builder()
                    .setId(entity.getId())
                    .setAmount(entity.getAmount())
                    .setDescription(entity.getDescription())
                    .setCategory(entity.getCategory())
                    .setDate(entity.getDate())
                    .setPaymentMethod(entity.getPaymentMethod())
                    .setVendor(entity.getVendor())
                    .build();
        } else {
            return new Income.Builder()
                    .setId(entity.getId())
                    .setAmount(entity.getAmount())
                    .setDescription(entity.getDescription())
                    .setCategory(entity.getCategory())
                    .setDate(entity.getDate())
                    .setSource(entity.getSource())
                    .setIncomeType(entity.getIncomeType())
                    .build();
        }
    }
    
    public static List<Transaction> toDomainList(List<TransactionEntity> entities) {
        List<Transaction> transactions = new ArrayList<>();
        for (TransactionEntity entity : entities) {
            transactions.add(toDomain(entity));
        }
        return transactions;
    }
    
    public static List<TransactionEntity> toEntityList(List<Transaction> transactions) {
        List<TransactionEntity> entities = new ArrayList<>();
        for (Transaction transaction : transactions) {
            entities.add(toEntity(transaction));
        }
        return entities;
    }
} 