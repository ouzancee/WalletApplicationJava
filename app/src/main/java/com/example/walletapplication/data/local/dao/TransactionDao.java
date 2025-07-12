package com.example.walletapplication.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.walletapplication.data.local.entity.TransactionEntity;
import com.example.walletapplication.domain.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Dao
public interface TransactionDao {
    
    @Insert
    long insertTransaction(TransactionEntity transaction);
    
    @Update
    void updateTransaction(TransactionEntity transaction);
    
    @Query("DELETE FROM transactions WHERE id = :transactionId")
    void deleteTransactionById(long transactionId);
    
    @Delete
    void deleteTransaction(TransactionEntity transaction);
    
    @Query("SELECT * FROM transactions WHERE id = :id")
    TransactionEntity getTransactionById(long id);
    
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    List<TransactionEntity> getAllTransactions();
    
    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    List<TransactionEntity> getTransactionsByType(TransactionType type);
    
    @Query("SELECT * FROM transactions WHERE category = :category ORDER BY date DESC")
    List<TransactionEntity> getTransactionsByCategory(String category);
    
    @Query("SELECT * FROM transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    List<TransactionEntity> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT * FROM transactions WHERE amount BETWEEN :minAmount AND :maxAmount ORDER BY date DESC")
    List<TransactionEntity> getTransactionsByAmountRange(BigDecimal minAmount, BigDecimal maxAmount);
    
    @Query("SELECT * FROM transactions WHERE description LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' ORDER BY date DESC")
    List<TransactionEntity> searchTransactions(String query);
    
    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'INCOME' AND date BETWEEN :startDate AND :endDate")
    BigDecimal getTotalIncomeByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'EXPENSE' AND date BETWEEN :startDate AND :endDate")
    BigDecimal getTotalExpenseByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT DISTINCT category FROM transactions ORDER BY category ASC")
    List<String> getAllCategories();
    
    @Query("SELECT COUNT(*) FROM transactions")
    int getTransactionCount();
} 