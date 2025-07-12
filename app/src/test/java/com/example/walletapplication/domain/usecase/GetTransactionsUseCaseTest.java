package com.example.walletapplication.domain.usecase;

import com.example.walletapplication.domain.entity.Transaction;
import com.example.walletapplication.domain.entity.TransactionType;
import com.example.walletapplication.domain.entity.Expense;
import com.example.walletapplication.domain.entity.Income;
import com.example.walletapplication.domain.repository.TransactionRepository;
import com.example.walletapplication.domain.usecase.transaction.GetTransactionsUseCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class GetTransactionsUseCaseTest {

    @Mock
    private TransactionRepository mockRepository;

    private GetTransactionsUseCase getTransactionsUseCase;
    private List<Transaction> mockTransactions;

    @Before
    public void setUp() {
        getTransactionsUseCase = new GetTransactionsUseCase(mockRepository);
        
        // Create mock transactions
        Transaction transaction1 = new Expense.Builder()
                .setId(1L)
                .setDescription("Grocery shopping")
                .setAmount(new BigDecimal("50.00"))
                .setCategory("Food")
                .setDate(LocalDateTime.now().minusDays(1))
                .build();

        Transaction transaction2 = new Income.Builder()
                .setId(2L)
                .setDescription("Salary")
                .setAmount(new BigDecimal("3000.00"))
                .setCategory("Salary")
                .setDate(LocalDateTime.now().minusDays(2))
                .build();

        mockTransactions = Arrays.asList(transaction1, transaction2);
    }

    @Test
    public void getAllTransactions_ShouldReturnAllTransactions() {
        // Arrange
        when(mockRepository.getAllTransactions())
                .thenReturn(CompletableFuture.completedFuture(mockTransactions));

        // Act
        CompletableFuture<List<Transaction>> result = getTransactionsUseCase.getAllTransactions();

        // Assert
        assertNotNull(result);
        List<Transaction> transactions = result.join();
        assertEquals(2, transactions.size());
        assertEquals(mockTransactions, transactions);
        verify(mockRepository, times(1)).getAllTransactions();
    }

    @Test
    public void getAllTransactions_EmptyList_ShouldReturnEmptyList() {
        // Arrange
        when(mockRepository.getAllTransactions())
                .thenReturn(CompletableFuture.completedFuture(Collections.emptyList()));

        // Act
        CompletableFuture<List<Transaction>> result = getTransactionsUseCase.getAllTransactions();

        // Assert
        assertNotNull(result);
        List<Transaction> transactions = result.join();
        assertTrue(transactions.isEmpty());
        verify(mockRepository, times(1)).getAllTransactions();
    }

    @Test
    public void getTransactionsByType_ValidType_ShouldReturnFilteredTransactions() {
        // Arrange
        List<Transaction> expenseTransactions = Arrays.asList(mockTransactions.get(0));
        when(mockRepository.getTransactionsByType(TransactionType.EXPENSE))
                .thenReturn(CompletableFuture.completedFuture(expenseTransactions));

        // Act
        CompletableFuture<List<Transaction>> result = getTransactionsUseCase.getTransactionsByType(TransactionType.EXPENSE);

        // Assert
        assertNotNull(result);
        List<Transaction> transactions = result.join();
        assertEquals(1, transactions.size());
        assertEquals(TransactionType.EXPENSE, transactions.get(0).getType());
        verify(mockRepository, times(1)).getTransactionsByType(TransactionType.EXPENSE);
    }

    @Test
    public void getTransactionsByType_NullType_ShouldThrowException() {
        // Act & Assert
        CompletableFuture<List<Transaction>> result = getTransactionsUseCase.getTransactionsByType(null);
        
        assertThrows(IllegalArgumentException.class, result::join);
        verify(mockRepository, never()).getTransactionsByType(any());
    }

    @Test
    public void getTransactionsByCategory_ValidCategory_ShouldReturnFilteredTransactions() {
        // Arrange
        List<Transaction> foodTransactions = Arrays.asList(mockTransactions.get(0));
        when(mockRepository.getTransactionsByCategory("Food"))
                .thenReturn(CompletableFuture.completedFuture(foodTransactions));

        // Act
        CompletableFuture<List<Transaction>> result = getTransactionsUseCase.getTransactionsByCategory("Food");

        // Assert
        assertNotNull(result);
        List<Transaction> transactions = result.join();
        assertEquals(1, transactions.size());
        assertEquals("Food", transactions.get(0).getCategory());
        verify(mockRepository, times(1)).getTransactionsByCategory("Food");
    }

    @Test
    public void getTransactionsByCategory_NullCategory_ShouldThrowException() {
        // Act & Assert
        CompletableFuture<List<Transaction>> result = getTransactionsUseCase.getTransactionsByCategory(null);
        
        assertThrows(IllegalArgumentException.class, result::join);
        verify(mockRepository, never()).getTransactionsByCategory(anyString());
    }

    @Test
    public void getTransactionsByCategory_EmptyCategory_ShouldThrowException() {
        // Act & Assert
        CompletableFuture<List<Transaction>> result = getTransactionsUseCase.getTransactionsByCategory("");
        
        assertThrows(IllegalArgumentException.class, result::join);
        verify(mockRepository, never()).getTransactionsByCategory(anyString());
    }

    @Test
    public void searchTransactions_ValidQuery_ShouldReturnMatchingTransactions() {
        // Arrange
        String searchQuery = "grocery";
        List<Transaction> searchResults = Arrays.asList(mockTransactions.get(0));
        when(mockRepository.searchTransactions(searchQuery))
                .thenReturn(CompletableFuture.completedFuture(searchResults));

        // Act
        CompletableFuture<List<Transaction>> result = getTransactionsUseCase.searchTransactions(searchQuery);

        // Assert
        assertNotNull(result);
        List<Transaction> transactions = result.join();
        assertEquals(1, transactions.size());
        assertTrue(transactions.get(0).getDescription().toLowerCase().contains(searchQuery.toLowerCase()));
        verify(mockRepository, times(1)).searchTransactions(searchQuery);
    }

    @Test
    public void searchTransactions_NullQuery_ShouldThrowException() {
        // Act & Assert
        CompletableFuture<List<Transaction>> result = getTransactionsUseCase.searchTransactions(null);
        
        assertThrows(IllegalArgumentException.class, result::join);
        verify(mockRepository, never()).searchTransactions(anyString());
    }

    @Test
    public void searchTransactions_EmptyQuery_ShouldThrowException() {
        // Act & Assert
        CompletableFuture<List<Transaction>> result = getTransactionsUseCase.searchTransactions("");
        
        assertThrows(IllegalArgumentException.class, result::join);
        verify(mockRepository, never()).searchTransactions(anyString());
    }

    @Test
    public void getTransactionsByDateRange_ValidRange_ShouldReturnFilteredTransactions() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now().minusDays(3);
        LocalDateTime endDate = LocalDateTime.now();
        when(mockRepository.getTransactionsByDateRange(startDate, endDate))
                .thenReturn(CompletableFuture.completedFuture(mockTransactions));

        // Act
        CompletableFuture<List<Transaction>> result = getTransactionsUseCase.getTransactionsByDateRange(startDate, endDate);

        // Assert
        assertNotNull(result);
        List<Transaction> transactions = result.join();
        assertEquals(2, transactions.size());
        verify(mockRepository, times(1)).getTransactionsByDateRange(startDate, endDate);
    }

    @Test
    public void getTransactionsByAmountRange_ValidRange_ShouldReturnFilteredTransactions() {
        // Arrange
        BigDecimal minAmount = new BigDecimal("0.00");
        BigDecimal maxAmount = new BigDecimal("100.00");
        List<Transaction> filteredTransactions = Arrays.asList(mockTransactions.get(0));
        when(mockRepository.getTransactionsByAmountRange(minAmount, maxAmount))
                .thenReturn(CompletableFuture.completedFuture(filteredTransactions));

        // Act
        CompletableFuture<List<Transaction>> result = getTransactionsUseCase.getTransactionsByAmountRange(minAmount, maxAmount);

        // Assert
        assertNotNull(result);
        List<Transaction> transactions = result.join();
        assertEquals(1, transactions.size());
        verify(mockRepository, times(1)).getTransactionsByAmountRange(minAmount, maxAmount);
    }

    @Test
    public void getAllTransactions_RepositoryThrowsException_ShouldPropagateException() {
        // Arrange
        when(mockRepository.getAllTransactions())
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Database error")));

        // Act
        CompletableFuture<List<Transaction>> result = getTransactionsUseCase.getAllTransactions();

        // Assert
        assertThrows(RuntimeException.class, result::join);
        verify(mockRepository, times(1)).getAllTransactions();
    }
} 