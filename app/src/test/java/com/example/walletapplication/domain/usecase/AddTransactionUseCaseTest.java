package com.example.walletapplication.domain.usecase;

import com.example.walletapplication.domain.entity.Transaction;
import com.example.walletapplication.domain.entity.TransactionType;
import com.example.walletapplication.domain.entity.Expense;
import com.example.walletapplication.domain.repository.TransactionRepository;
import com.example.walletapplication.domain.common.Result;
import com.example.walletapplication.domain.usecase.transaction.AddTransactionUseCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AddTransactionUseCaseTest {

    @Mock
    private TransactionRepository mockRepository;

    private AddTransactionUseCase addTransactionUseCase;

    @Before
    public void setUp() {
        addTransactionUseCase = new AddTransactionUseCase(mockRepository);
    }

    @Test
    public void execute_ValidTransaction_ShouldReturnTransactionId() {
        // Arrange
        Transaction transaction = new Expense.Builder()
                .setDescription("Test transaction")
                .setAmount(new BigDecimal("100.00"))
                .setCategory("Food")
                .setDate(LocalDateTime.now())
                .build();

        Long expectedId = 1L;
        when(mockRepository.insertTransaction(any(Transaction.class)))
                .thenReturn(CompletableFuture.completedFuture(expectedId));

        // Act
        CompletableFuture<Result<Long>> result = addTransactionUseCase.execute(transaction);

        // Assert
        assertNotNull(result);
        Result<Long> resultValue = result.join();
        assertTrue(resultValue.isSuccess());
        assertEquals(expectedId, resultValue.getDataOrNull());
        verify(mockRepository, times(1)).insertTransaction(transaction);
    }

    @Test
    public void execute_NullTransaction_ShouldReturnError() {
        // Act
        CompletableFuture<Result<Long>> result = addTransactionUseCase.execute(null);
        
        // Assert
        assertNotNull(result);
        Result<Long> resultValue = result.join();
        assertTrue(resultValue.isError());
        verify(mockRepository, never()).insertTransaction(any());
    }

    @Test
    public void execute_TransactionWithNullDescription_ShouldReturnError() {
        // Arrange
        Transaction transaction = new Expense.Builder()
                .setDescription(null)
                .setAmount(new BigDecimal("100.00"))
                .setCategory("Food")
                .setDate(LocalDateTime.now())
                .build();

        // Act
        CompletableFuture<Result<Long>> result = addTransactionUseCase.execute(transaction);

        // Assert
        assertNotNull(result);
        Result<Long> resultValue = result.join();
        assertTrue(resultValue.isError());
        verify(mockRepository, never()).insertTransaction(any());
    }

    @Test
    public void execute_TransactionWithEmptyDescription_ShouldReturnError() {
        // Arrange
        Transaction transaction = new Expense.Builder()
                .setDescription("")
                .setAmount(new BigDecimal("100.00"))
                .setCategory("Food")
                .setDate(LocalDateTime.now())
                .build();

        // Act
        CompletableFuture<Result<Long>> result = addTransactionUseCase.execute(transaction);

        // Assert
        assertNotNull(result);
        Result<Long> resultValue = result.join();
        assertTrue(resultValue.isError());
        verify(mockRepository, never()).insertTransaction(any());
    }

    @Test
    public void execute_TransactionWithNullAmount_ShouldReturnError() {
        // Arrange
        Transaction transaction = new Expense.Builder()
                .setDescription("Test transaction")
                .setAmount(null)
                .setCategory("Food")
                .setDate(LocalDateTime.now())
                .build();

        // Act
        CompletableFuture<Result<Long>> result = addTransactionUseCase.execute(transaction);

        // Assert
        assertNotNull(result);
        Result<Long> resultValue = result.join();
        assertTrue(resultValue.isError());
        verify(mockRepository, never()).insertTransaction(any());
    }

    @Test
    public void execute_TransactionWithZeroAmount_ShouldReturnError() {
        // Arrange
        Transaction transaction = new Expense.Builder()
                .setDescription("Test transaction")
                .setAmount(BigDecimal.ZERO)
                .setCategory("Food")
                .setDate(LocalDateTime.now())
                .build();

        // Act
        CompletableFuture<Result<Long>> result = addTransactionUseCase.execute(transaction);

        // Assert
        assertNotNull(result);
        Result<Long> resultValue = result.join();
        assertTrue(resultValue.isError());
        verify(mockRepository, never()).insertTransaction(any());
    }

    @Test
    public void execute_TransactionWithNegativeAmount_ShouldReturnError() {
        // Arrange
        Transaction transaction = new Expense.Builder()
                .setDescription("Test transaction")
                .setAmount(new BigDecimal("-100.00"))
                .setCategory("Food")
                .setDate(LocalDateTime.now())
                .build();

        // Act
        CompletableFuture<Result<Long>> result = addTransactionUseCase.execute(transaction);

        // Assert
        assertNotNull(result);
        Result<Long> resultValue = result.join();
        assertTrue(resultValue.isError());
        verify(mockRepository, never()).insertTransaction(any());
    }

    @Test
    public void execute_TransactionWithNullType_ShouldReturnError() {
        // Arrange
        Transaction transaction = new Expense.Builder()
                .setDescription("Test transaction")
                .setAmount(new BigDecimal("100.00"))
                .setCategory("Food")
                .setDate(LocalDateTime.now())
                .build();

        // Act
        CompletableFuture<Result<Long>> result = addTransactionUseCase.execute(transaction);

        // Assert
        assertNotNull(result);
        Result<Long> resultValue = result.join();
        assertTrue(resultValue.isError());
        verify(mockRepository, never()).insertTransaction(any());
    }

    @Test
    public void execute_TransactionWithNullCategory_ShouldReturnError() {
        // Arrange
        Transaction transaction = new Expense.Builder()
                .setDescription("Test transaction")
                .setAmount(new BigDecimal("100.00"))
                .setCategory(null)
                .setDate(LocalDateTime.now())
                .build();

        // Act
        CompletableFuture<Result<Long>> result = addTransactionUseCase.execute(transaction);

        // Assert
        assertNotNull(result);
        Result<Long> resultValue = result.join();
        assertTrue(resultValue.isError());
        verify(mockRepository, never()).insertTransaction(any());
    }

    @Test
    public void execute_TransactionWithNullDate_ShouldReturnError() {
        // Arrange
        Transaction transaction = new Expense.Builder()
                .setDescription("Test transaction")
                .setAmount(new BigDecimal("100.00"))
                .setCategory("Food")
                .setDate(null)
                .build();

        // Act
        CompletableFuture<Result<Long>> result = addTransactionUseCase.execute(transaction);

        // Assert
        assertNotNull(result);
        Result<Long> resultValue = result.join();
        assertTrue(resultValue.isError());
        verify(mockRepository, never()).insertTransaction(any());
    }

    @Test
    public void execute_RepositoryThrowsException_ShouldReturnError() {
        // Arrange
        Transaction transaction = new Expense.Builder()
                .setDescription("Test transaction")
                .setAmount(new BigDecimal("100.00"))
                .setCategory("Food")
                .setDate(LocalDateTime.now())
                .build();

        when(mockRepository.insertTransaction(any(Transaction.class)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Database error")));

        // Act
        CompletableFuture<Result<Long>> result = addTransactionUseCase.execute(transaction);

        // Assert
        assertNotNull(result);
        Result<Long> resultValue = result.join();
        assertTrue(resultValue.isError());
        verify(mockRepository, times(1)).insertTransaction(transaction);
    }
} 