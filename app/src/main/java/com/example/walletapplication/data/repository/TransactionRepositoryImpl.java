package com.example.walletapplication.data.repository;

import com.example.walletapplication.data.local.dao.TransactionDao;
import com.example.walletapplication.data.local.entity.TransactionEntity;
import com.example.walletapplication.data.mapper.TransactionMapper;
import com.example.walletapplication.domain.entity.Transaction;
import com.example.walletapplication.domain.entity.TransactionType;
import com.example.walletapplication.domain.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class TransactionRepositoryImpl implements TransactionRepository {
    
    private final TransactionDao transactionDao;
    private final Executor executor;
    
    public TransactionRepositoryImpl(TransactionDao transactionDao, Executor executor) {
        this.transactionDao = transactionDao;
        this.executor = executor;
    }
    
    @Override
    public CompletableFuture<Long> insertTransaction(Transaction transaction) {
        return CompletableFuture.supplyAsync(() -> {
            TransactionEntity entity = TransactionMapper.toEntity(transaction);
            return transactionDao.insertTransaction(entity);
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> updateTransaction(Transaction transaction) {
        return CompletableFuture.runAsync(() -> {
            TransactionEntity entity = TransactionMapper.toEntity(transaction);
            transactionDao.updateTransaction(entity);
        }, executor);
    }
    
    @Override
    public CompletableFuture<Void> deleteTransaction(Long transactionId) {
        return CompletableFuture.runAsync(() -> {
            transactionDao.deleteTransactionById(transactionId);
        }, executor);
    }
    
    @Override
    public CompletableFuture<Optional<Transaction>> getTransactionById(Long id) {
        return CompletableFuture.supplyAsync(() -> {
            TransactionEntity entity = transactionDao.getTransactionById(id);
            if (entity != null) {
                return Optional.of(TransactionMapper.toDomain(entity));
            }
            return Optional.empty();
        }, executor);
    }
    
    @Override
    public CompletableFuture<List<Transaction>> getAllTransactions() {
        return CompletableFuture.supplyAsync(() -> {
            List<TransactionEntity> entities = transactionDao.getAllTransactions();
            return TransactionMapper.toDomainList(entities);
        }, executor);
    }
    
    @Override
    public CompletableFuture<List<Transaction>> getTransactionsByType(TransactionType type) {
        return CompletableFuture.supplyAsync(() -> {
            List<TransactionEntity> entities = transactionDao.getTransactionsByType(type);
            return TransactionMapper.toDomainList(entities);
        }, executor);
    }
    
    @Override
    public CompletableFuture<List<Transaction>> getTransactionsByCategory(String category) {
        return CompletableFuture.supplyAsync(() -> {
            List<TransactionEntity> entities = transactionDao.getTransactionsByCategory(category);
            return TransactionMapper.toDomainList(entities);
        }, executor);
    }
    
    @Override
    public CompletableFuture<List<Transaction>> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return CompletableFuture.supplyAsync(() -> {
            List<TransactionEntity> entities = transactionDao.getTransactionsByDateRange(startDate, endDate);
            return TransactionMapper.toDomainList(entities);
        }, executor);
    }
    
    @Override
    public CompletableFuture<List<Transaction>> getTransactionsByAmountRange(BigDecimal minAmount, BigDecimal maxAmount) {
        return CompletableFuture.supplyAsync(() -> {
            List<TransactionEntity> entities = transactionDao.getTransactionsByAmountRange(minAmount, maxAmount);
            return TransactionMapper.toDomainList(entities);
        }, executor);
    }
    
    @Override
    public CompletableFuture<List<Transaction>> searchTransactions(String query) {
        return CompletableFuture.supplyAsync(() -> {
            List<TransactionEntity> entities = transactionDao.searchTransactions(query);
            return TransactionMapper.toDomainList(entities);
        }, executor);
    }
    
    @Override
    public CompletableFuture<BigDecimal> getTotalIncomeByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return CompletableFuture.supplyAsync(() -> {
            BigDecimal result = transactionDao.getTotalIncomeByDateRange(startDate, endDate);
            return result != null ? result : BigDecimal.ZERO;
        }, executor);
    }
    
    @Override
    public CompletableFuture<BigDecimal> getTotalExpenseByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return CompletableFuture.supplyAsync(() -> {
            BigDecimal result = transactionDao.getTotalExpenseByDateRange(startDate, endDate);
            return result != null ? result : BigDecimal.ZERO;
        }, executor);
    }
    
    @Override
    public CompletableFuture<BigDecimal> getBalanceByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return CompletableFuture.supplyAsync(() -> {
            BigDecimal income = transactionDao.getTotalIncomeByDateRange(startDate, endDate);
            BigDecimal expense = transactionDao.getTotalExpenseByDateRange(startDate, endDate);
            
            income = income != null ? income : BigDecimal.ZERO;
            expense = expense != null ? expense : BigDecimal.ZERO;
            
            return income.subtract(expense);
        }, executor);
    }
    
    @Override
    public CompletableFuture<List<String>> getAllCategories() {
        return CompletableFuture.supplyAsync(() -> {
            return transactionDao.getAllCategories();
        }, executor);
    }
} 