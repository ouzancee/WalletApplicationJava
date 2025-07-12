package com.example.walletapplication.domain.usecase;

import com.example.walletapplication.domain.entity.Transaction;
import com.example.walletapplication.domain.entity.TransactionType;
import com.example.walletapplication.domain.entity.Expense;
import com.example.walletapplication.domain.entity.Income;
import com.example.walletapplication.domain.repository.TransactionRepository;
import com.example.walletapplication.domain.usecase.transaction.AdvancedSearchUseCase;

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
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AdvancedSearchUseCaseTest {

    @Mock
    private TransactionRepository mockRepository;

    private AdvancedSearchUseCase advancedSearchUseCase;
    private List<Transaction> mockTransactions;

    @Before
    public void setUp() {
        advancedSearchUseCase = new AdvancedSearchUseCase(mockRepository);
        
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
                .setDescription("Salary payment")
                .setAmount(new BigDecimal("3000.00"))
                .setCategory("Salary")
                .setDate(LocalDateTime.now().minusDays(2))
                .build();

        Transaction transaction3 = new Expense.Builder()
                .setId(3L)
                .setDescription("Restaurant dinner")
                .setAmount(new BigDecimal("75.00"))
                .setCategory("Food")
                .setDate(LocalDateTime.now().minusDays(3))
                .build();

        mockTransactions = Arrays.asList(transaction1, transaction2, transaction3);
    }

    @Test
    public void search_EmptyCriteria_ShouldReturnAllTransactions() {
        // Arrange
        AdvancedSearchUseCase.SearchCriteria criteria = new AdvancedSearchUseCase.SearchCriteria();
        when(mockRepository.getAllTransactions())
                .thenReturn(CompletableFuture.completedFuture(mockTransactions));

        // Act
        CompletableFuture<List<Transaction>> result = advancedSearchUseCase.search(criteria);

        // Assert
        assertNotNull(result);
        List<Transaction> transactions = result.join();
        assertEquals(3, transactions.size());
        verify(mockRepository, times(1)).getAllTransactions();
    }

    @Test
    public void search_NullCriteria_ShouldReturnAllTransactions() {
        // Arrange
        when(mockRepository.getAllTransactions())
                .thenReturn(CompletableFuture.completedFuture(mockTransactions));

        // Act
        CompletableFuture<List<Transaction>> result = advancedSearchUseCase.search(null);

        // Assert
        assertNotNull(result);
        List<Transaction> transactions = result.join();
        assertEquals(3, transactions.size());
        verify(mockRepository, times(1)).getAllTransactions();
    }

    @Test
    public void search_WithTextQuery_ShouldFilterByText() {
        // Arrange
        AdvancedSearchUseCase.SearchCriteria criteria = new AdvancedSearchUseCase.SearchCriteria()
                .setTextQuery("grocery");
        when(mockRepository.getAllTransactions())
                .thenReturn(CompletableFuture.completedFuture(mockTransactions));

        // Act
        CompletableFuture<List<Transaction>> result = advancedSearchUseCase.search(criteria);

        // Assert
        assertNotNull(result);
        List<Transaction> transactions = result.join();
        assertEquals(1, transactions.size());
        assertTrue(transactions.get(0).getDescription().toLowerCase().contains("grocery"));
    }

    @Test
    public void search_WithCategory_ShouldFilterByCategory() {
        // Arrange
        AdvancedSearchUseCase.SearchCriteria criteria = new AdvancedSearchUseCase.SearchCriteria()
                .setCategory("Food");
        when(mockRepository.getAllTransactions())
                .thenReturn(CompletableFuture.completedFuture(mockTransactions));

        // Act
        CompletableFuture<List<Transaction>> result = advancedSearchUseCase.search(criteria);

        // Assert
        assertNotNull(result);
        List<Transaction> transactions = result.join();
        assertEquals(2, transactions.size());
        assertTrue(transactions.stream().allMatch(t -> t.getCategory().equals("Food")));
    }

    @Test
    public void search_WithType_ShouldFilterByType() {
        // Arrange
        AdvancedSearchUseCase.SearchCriteria criteria = new AdvancedSearchUseCase.SearchCriteria()
                .setType(TransactionType.EXPENSE);
        when(mockRepository.getAllTransactions())
                .thenReturn(CompletableFuture.completedFuture(mockTransactions));

        // Act
        CompletableFuture<List<Transaction>> result = advancedSearchUseCase.search(criteria);

        // Assert
        assertNotNull(result);
        List<Transaction> transactions = result.join();
        assertEquals(2, transactions.size());
        assertTrue(transactions.stream().allMatch(t -> t.getType() == TransactionType.EXPENSE));
    }

    @Test
    public void search_WithAmountRange_ShouldFilterByAmount() {
        // Arrange
        AdvancedSearchUseCase.SearchCriteria criteria = new AdvancedSearchUseCase.SearchCriteria()
                .setAmountRange(new BigDecimal("40.00"), new BigDecimal("80.00"));
        when(mockRepository.getAllTransactions())
                .thenReturn(CompletableFuture.completedFuture(mockTransactions));

        // Act
        CompletableFuture<List<Transaction>> result = advancedSearchUseCase.search(criteria);

        // Assert
        assertNotNull(result);
        List<Transaction> transactions = result.join();
        assertEquals(2, transactions.size());
        assertTrue(transactions.stream().allMatch(t -> 
            t.getAmount().compareTo(new BigDecimal("40.00")) >= 0 && 
            t.getAmount().compareTo(new BigDecimal("80.00")) <= 0));
    }

    @Test
    public void search_WithDateRange_ShouldFilterByDate() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now().minusDays(2);
        LocalDateTime endDate = LocalDateTime.now();
        AdvancedSearchUseCase.SearchCriteria criteria = new AdvancedSearchUseCase.SearchCriteria()
                .setDateRange(startDate, endDate);
        when(mockRepository.getAllTransactions())
                .thenReturn(CompletableFuture.completedFuture(mockTransactions));

        // Act
        CompletableFuture<List<Transaction>> result = advancedSearchUseCase.search(criteria);

        // Assert
        assertNotNull(result);
        List<Transaction> transactions = result.join();
        assertEquals(2, transactions.size());
        assertTrue(transactions.stream().allMatch(t -> 
            !t.getDate().isBefore(startDate) && !t.getDate().isAfter(endDate)));
    }

    @Test
    public void search_WithMultipleCriteria_ShouldApplyAllFilters() {
        // Arrange
        AdvancedSearchUseCase.SearchCriteria criteria = new AdvancedSearchUseCase.SearchCriteria()
                .setTextQuery("grocery")
                .setCategory("Food")
                .setType(TransactionType.EXPENSE)
                .setAmountRange(new BigDecimal("40.00"), new BigDecimal("60.00"));
        when(mockRepository.getAllTransactions())
                .thenReturn(CompletableFuture.completedFuture(mockTransactions));

        // Act
        CompletableFuture<List<Transaction>> result = advancedSearchUseCase.search(criteria);

        // Assert
        assertNotNull(result);
        List<Transaction> transactions = result.join();
        assertEquals(1, transactions.size());
        Transaction transaction = transactions.get(0);
        assertTrue(transaction.getDescription().toLowerCase().contains("grocery"));
        assertEquals("Food", transaction.getCategory());
        assertEquals(TransactionType.EXPENSE, transaction.getType());
        assertTrue(transaction.getAmount().compareTo(new BigDecimal("40.00")) >= 0);
        assertTrue(transaction.getAmount().compareTo(new BigDecimal("60.00")) <= 0);
    }

    @Test
    public void quickSearch_ValidQuery_ShouldReturnMatchingTransactions() {
        // Arrange
        String query = "salary";
        when(mockRepository.getAllTransactions())
                .thenReturn(CompletableFuture.completedFuture(mockTransactions));

        // Act
        CompletableFuture<List<Transaction>> result = advancedSearchUseCase.quickSearch(query);

        // Assert
        assertNotNull(result);
        List<Transaction> transactions = result.join();
        assertEquals(1, transactions.size());
        assertTrue(transactions.get(0).getDescription().toLowerCase().contains(query));
    }

    @Test
    public void searchByCategoryAndType_ValidParameters_ShouldReturnFilteredTransactions() {
        // Arrange
        String category = "Food";
        TransactionType type = TransactionType.EXPENSE;
        when(mockRepository.getAllTransactions())
                .thenReturn(CompletableFuture.completedFuture(mockTransactions));

        // Act
        CompletableFuture<List<Transaction>> result = advancedSearchUseCase.searchByCategoryAndType(category, type);

        // Assert
        assertNotNull(result);
        List<Transaction> transactions = result.join();
        assertEquals(2, transactions.size());
        assertTrue(transactions.stream().allMatch(t -> 
            t.getCategory().equals(category) && t.getType() == type));
    }

    @Test
    public void searchByAmountRange_ValidRange_ShouldReturnFilteredTransactions() {
        // Arrange
        BigDecimal minAmount = new BigDecimal("50.00");
        BigDecimal maxAmount = new BigDecimal("100.00");
        when(mockRepository.getAllTransactions())
                .thenReturn(CompletableFuture.completedFuture(mockTransactions));

        // Act
        CompletableFuture<List<Transaction>> result = advancedSearchUseCase.searchByAmountRange(minAmount, maxAmount);

        // Assert
        assertNotNull(result);
        List<Transaction> transactions = result.join();
        assertEquals(2, transactions.size());
        assertTrue(transactions.stream().allMatch(t -> 
            t.getAmount().compareTo(minAmount) >= 0 && t.getAmount().compareTo(maxAmount) <= 0));
    }

    @Test
    public void searchByDateRange_ValidRange_ShouldReturnFilteredTransactions() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now().minusDays(2);
        LocalDateTime endDate = LocalDateTime.now();
        when(mockRepository.getAllTransactions())
                .thenReturn(CompletableFuture.completedFuture(mockTransactions));

        // Act
        CompletableFuture<List<Transaction>> result = advancedSearchUseCase.searchByDateRange(startDate, endDate);

        // Assert
        assertNotNull(result);
        List<Transaction> transactions = result.join();
        assertEquals(2, transactions.size());
        assertTrue(transactions.stream().allMatch(t -> 
            !t.getDate().isBefore(startDate) && !t.getDate().isAfter(endDate)));
    }

    @Test
    public void search_NoMatchingResults_ShouldReturnEmptyList() {
        // Arrange
        AdvancedSearchUseCase.SearchCriteria criteria = new AdvancedSearchUseCase.SearchCriteria()
                .setTextQuery("nonexistent");
        when(mockRepository.getAllTransactions())
                .thenReturn(CompletableFuture.completedFuture(mockTransactions));

        // Act
        CompletableFuture<List<Transaction>> result = advancedSearchUseCase.search(criteria);

        // Assert
        assertNotNull(result);
        List<Transaction> transactions = result.join();
        assertTrue(transactions.isEmpty());
    }

    @Test
    public void search_RepositoryThrowsException_ShouldPropagateException() {
        // Arrange
        AdvancedSearchUseCase.SearchCriteria criteria = new AdvancedSearchUseCase.SearchCriteria()
                .setTextQuery("test");
        when(mockRepository.getAllTransactions())
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Database error")));

        // Act
        CompletableFuture<List<Transaction>> result = advancedSearchUseCase.search(criteria);

        // Assert
        assertThrows(RuntimeException.class, result::join);
    }
} 