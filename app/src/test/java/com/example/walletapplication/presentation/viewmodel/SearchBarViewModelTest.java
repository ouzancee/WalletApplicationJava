package com.example.walletapplication.presentation.viewmodel;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.walletapplication.domain.entity.SearchFilters;
import com.example.walletapplication.domain.entity.SearchHistory;
import com.example.walletapplication.domain.entity.SearchSuggestion;
import com.example.walletapplication.domain.entity.SearchSuggestionType;
import com.example.walletapplication.domain.entity.Transaction;
import com.example.walletapplication.domain.entity.TransactionType;
import com.example.walletapplication.domain.search.SearchBarConfig;
import com.example.walletapplication.domain.search.SearchSuggestionProvider;
import com.example.walletapplication.domain.usecase.transaction.AdvancedSearchUseCase;
import com.example.walletapplication.presentation.util.SearchHandler;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SearchBarViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private AdvancedSearchUseCase mockAdvancedSearchUseCase;

    @Mock
    private SearchSuggestionProvider mockSuggestionProvider;

    @Mock
    private SearchHandler mockSearchHandler;

    @Mock
    private Observer<String> searchQueryObserver;

    @Mock
    private Observer<SearchFilters> activeFiltersObserver;

    @Mock
    private Observer<Boolean> isLoadingObserver;

    @Mock
    private Observer<String> errorObserver;

    @Mock
    private Observer<List<SearchSuggestion>> suggestionsObserver;

    @Mock
    private Observer<Boolean> isExpandedObserver;

    private SearchBarViewModel viewModel;

    @Before
    public void setUp() {
        // Set up default mock behaviors
        when(mockAdvancedSearchUseCase.search(any(AdvancedSearchUseCase.SearchCriteria.class)))
                .thenReturn(CompletableFuture.completedFuture(new ArrayList<>()));
        
        when(mockSuggestionProvider.getSuggestions(anyString()))
                .thenReturn(new MutableLiveData<>(new ArrayList<>()));
        
        when(mockSuggestionProvider.getRecentSearches())
                .thenReturn(new ArrayList<>());
        
        // Create ViewModel manually for unit testing (bypassing Hilt)
        viewModel = new SearchBarViewModel(mockAdvancedSearchUseCase, mockSearchHandler);
        
        // Set up observers
        viewModel.getSearchQuery().observeForever(searchQueryObserver);
        viewModel.getActiveFilters().observeForever(activeFiltersObserver);
        viewModel.getIsLoading().observeForever(isLoadingObserver);
        viewModel.getError().observeForever(errorObserver);
        viewModel.getSuggestions().observeForever(suggestionsObserver);
        viewModel.getIsExpanded().observeForever(isExpandedObserver);
        
        // Set up suggestion provider
        viewModel.setSuggestionProvider(mockSuggestionProvider);
    }

    @Test
    public void constructor_ShouldInitializeWithDefaultValues() {
        // Assert initial values
        assertEquals("", viewModel.getSearchQuery().getValue());
        assertNotNull(viewModel.getActiveFilters().getValue());
        assertFalse(viewModel.getActiveFilters().getValue().hasActiveFilters());
        assertFalse(viewModel.getIsLoading().getValue());
        assertNull(viewModel.getError().getValue());
        assertNotNull(viewModel.getSuggestions().getValue());
        assertTrue(viewModel.getSuggestions().getValue().isEmpty());
        assertFalse(viewModel.getIsExpanded().getValue());
    }

    @Test
    public void setConfig_ShouldAcceptValidConfig() {
        // Arrange
        SearchBarConfig config = SearchBarConfig.forTransactions();
        
        // Act
        viewModel.setConfig(config);
        
        // Assert - no exception should be thrown
        // Config is stored internally and used for debounce settings
    }

    @Test
    public void setSuggestionProvider_ShouldAcceptValidProvider() {
        // Arrange
        SearchSuggestionProvider provider = mock(SearchSuggestionProvider.class);
        
        // Act
        viewModel.setSuggestionProvider(provider);
        
        // Assert - no exception should be thrown
        // Provider is stored internally for suggestions
    }

    @Test
    public void search_WithValidQuery_ShouldUpdateSearchQuery() throws InterruptedException {
        // Arrange
        String query = "test query";
        when(mockAdvancedSearchUseCase.search(any(AdvancedSearchUseCase.SearchCriteria.class)))
                .thenReturn(CompletableFuture.completedFuture(new ArrayList<>()));
        
        // Act
        viewModel.search(query);
        
        // Wait for debounced search to execute
        Thread.sleep(350);
        
        // Assert
        verify(searchQueryObserver, atLeastOnce()).onChanged(query);
        verify(errorObserver, atLeastOnce()).onChanged(null);
    }

    @Test
    public void search_WithEmptyQuery_ShouldUpdateSearchQuery() {
        // Arrange
        String query = "";
        
        // Act
        viewModel.search(query);
        
        // Assert
        verify(searchQueryObserver, atLeastOnce()).onChanged(query);
    }

    @Test
    public void search_WithNullQuery_ShouldSetEmptyQuery() {
        // Act
        viewModel.search(null);
        
        // Assert
        verify(searchQueryObserver, atLeastOnce()).onChanged("");
    }

    @Test
    public void search_WithInvalidQuery_ShouldSetError() {
        // Arrange
        String invalidQuery = "   ;;;   ";
        
        // Act
        viewModel.search(invalidQuery);
        
        // Assert
        verify(errorObserver).onChanged("Invalid search query");
    }

    @Test
    public void searchImmediate_WithValidQuery_ShouldPerformSearchImmediately() {
        // Arrange
        String query = "immediate test";
        when(mockAdvancedSearchUseCase.search(any(AdvancedSearchUseCase.SearchCriteria.class)))
                .thenReturn(CompletableFuture.completedFuture(new ArrayList<>()));
        
        // Act
        viewModel.searchImmediate(query);
        
        // Assert
        verify(searchQueryObserver, atLeastOnce()).onChanged(query);
        verify(errorObserver, atLeastOnce()).onChanged(null);
        verify(mockSuggestionProvider).saveSearchQuery(query);
    }

    @Test
    public void searchImmediate_WithEmptyQuery_ShouldNotSaveToHistory() {
        // Arrange
        String query = "";
        
        // Act
        viewModel.searchImmediate(query);
        
        // Assert
        verify(searchQueryObserver, atLeastOnce()).onChanged(query);
        verify(mockSuggestionProvider, never()).saveSearchQuery(anyString());
    }

    @Test
    public void applyFilters_WithValidFilters_ShouldUpdateActiveFilters() {
        // Arrange
        SearchFilters filters = new SearchFilters.Builder()
                .setType(TransactionType.EXPENSE)
                .setCategory("Food")
                .build();
        when(mockAdvancedSearchUseCase.search(any(AdvancedSearchUseCase.SearchCriteria.class)))
                .thenReturn(CompletableFuture.completedFuture(new ArrayList<>()));
        
        // Set a search query first
        viewModel.searchImmediate("test");
        
        // Act
        viewModel.applyFilters(filters);
        
        // Assert
        verify(activeFiltersObserver, atLeastOnce()).onChanged(filters);
        verify(errorObserver, atLeastOnce()).onChanged(null);
    }

    @Test
    public void applyFilters_WithInvalidFilters_ShouldSetError() {
        // Arrange
        SearchFilters invalidFilters = new SearchFilters.Builder()
                .setMinAmount(new BigDecimal("100"))
                .setMaxAmount(new BigDecimal("50")) // Invalid: min > max
                .build();
        
        // Act
        viewModel.applyFilters(invalidFilters);
        
        // Assert
        verify(errorObserver).onChanged("Invalid filter parameters");
    }

    @Test
    public void applyFilters_WithNullFilters_ShouldSetError() {
        // Act
        viewModel.applyFilters(null);
        
        // Assert
        verify(errorObserver).onChanged("Invalid filter parameters");
    }

    @Test
    public void clearFilters_ShouldResetFiltersToEmpty() {
        // Arrange
        SearchFilters filters = new SearchFilters.Builder()
                .setType(TransactionType.EXPENSE)
                .build();
        viewModel.applyFilters(filters);
        
        // Act
        viewModel.clearFilters();
        
        // Assert
        verify(activeFiltersObserver, atLeastOnce()).onChanged(argThat(f -> !f.hasActiveFilters()));
    }

    @Test
    public void clearSearch_ShouldResetSearchState() {
        // Arrange
        viewModel.search("test query");
        
        // Act
        viewModel.clearSearch();
        
        // Assert
        verify(searchQueryObserver, atLeastOnce()).onChanged("");
        verify(errorObserver, atLeastOnce()).onChanged(null);
        verify(isLoadingObserver, atLeastOnce()).onChanged(false);
    }

    @Test
    public void expand_ShouldSetExpandedToTrue() {
        // Arrange
        when(mockSuggestionProvider.getRecentSearches())
                .thenReturn(Arrays.asList("recent1", "recent2"));
        
        // Act
        viewModel.expand();
        
        // Assert
        verify(isExpandedObserver).onChanged(true);
        verify(mockSuggestionProvider).getRecentSearches();
    }

    @Test
    public void collapse_ShouldSetExpandedToFalse() {
        // Act
        viewModel.collapse();
        
        // Assert
        verify(isExpandedObserver).onChanged(false);
        verify(suggestionsObserver, atLeastOnce()).onChanged(argThat(List::isEmpty));
    }

    @Test
    public void selectSuggestion_WithValidSuggestion_ShouldPerformSearch() {
        // Arrange
        SearchSuggestion suggestion = SearchSuggestion.createRecentSearch("suggestion text", 1);
        when(mockAdvancedSearchUseCase.search(any(AdvancedSearchUseCase.SearchCriteria.class)))
                .thenReturn(CompletableFuture.completedFuture(new ArrayList<>()));
        
        // Act
        viewModel.selectSuggestion(suggestion);
        
        // Assert
        verify(searchQueryObserver).onChanged("suggestion text");
        verify(mockSuggestionProvider).saveSearchQuery("suggestion text");
    }

    @Test
    public void selectSuggestion_WithNullSuggestion_ShouldNotPerformSearch() {
        // Act
        viewModel.selectSuggestion(null);
        
        // Assert
        verify(searchQueryObserver, never()).onChanged(anyString());
    }

    @Test
    public void selectHistoryItem_WithValidHistory_ShouldPerformSearch() {
        // Arrange
        SearchHistory historyItem = SearchHistory.create("history query", null);
        when(mockAdvancedSearchUseCase.search(any(AdvancedSearchUseCase.SearchCriteria.class)))
                .thenReturn(CompletableFuture.completedFuture(new ArrayList<>()));
        
        // Act
        viewModel.selectHistoryItem(historyItem);
        
        // Assert
        verify(searchQueryObserver, atLeastOnce()).onChanged("history query");
        verify(mockSuggestionProvider).saveSearchQuery("history query");
    }

    @Test
    public void selectHistoryItem_WithHistoryWithFilters_ShouldApplyFilters() {
        // Arrange
        SearchFilters filters = new SearchFilters.Builder()
                .setType(TransactionType.INCOME)
                .build();
        SearchHistory historyItem = SearchHistory.create("query", LocalDateTime.now(), filters);
        when(mockAdvancedSearchUseCase.search(any(AdvancedSearchUseCase.SearchCriteria.class)))
                .thenReturn(CompletableFuture.completedFuture(new ArrayList<>()));
        
        // Act
        viewModel.selectHistoryItem(historyItem);
        
        // Assert
        verify(searchQueryObserver, atLeastOnce()).onChanged("query");
        verify(activeFiltersObserver, atLeastOnce()).onChanged(filters);
    }

    @Test
    public void getActiveFilterCount_WithNoFilters_ShouldReturnZero() {
        // Act
        int count = viewModel.getActiveFilterCount();
        
        // Assert
        assertEquals(0, count);
    }

    @Test
    public void getActiveFilterCount_WithFilters_ShouldReturnCorrectCount() {
        // Arrange
        SearchFilters filters = new SearchFilters.Builder()
                .setType(TransactionType.EXPENSE)
                .setCategory("Food")
                .build();
        viewModel.applyFilters(filters);
        
        // Act
        int count = viewModel.getActiveFilterCount();
        
        // Assert
        assertEquals(2, count);
    }

    @Test
    public void hasActiveFilters_WithNoFilters_ShouldReturnFalse() {
        // Act
        boolean hasFilters = viewModel.hasActiveFilters();
        
        // Assert
        assertFalse(hasFilters);
    }

    @Test
    public void hasActiveFilters_WithFilters_ShouldReturnTrue() {
        // Arrange
        SearchFilters filters = new SearchFilters.Builder()
                .setType(TransactionType.EXPENSE)
                .build();
        viewModel.applyFilters(filters);
        
        // Act
        boolean hasFilters = viewModel.hasActiveFilters();
        
        // Assert
        assertTrue(hasFilters);
    }

    @Test
    public void search_WhenAdvancedSearchFails_ShouldSetError() throws InterruptedException {
        // Arrange
        String query = "test query";
        CompletableFuture<List<Transaction>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Search failed"));
        when(mockAdvancedSearchUseCase.search(any(AdvancedSearchUseCase.SearchCriteria.class)))
                .thenReturn(failedFuture);
        
        // Act
        viewModel.search(query);
        
        // Wait for debounced search to execute
        Thread.sleep(350);
        
        // Assert
        verify(errorObserver, timeout(1000)).onChanged(contains("Search failed"));
        verify(isLoadingObserver, timeout(1000)).onChanged(false);
    }

    @Test
    public void searchImmediate_WhenAdvancedSearchSucceeds_ShouldSetLoadingFalse() {
        // Arrange
        String query = "test query";
        when(mockAdvancedSearchUseCase.search(any(AdvancedSearchUseCase.SearchCriteria.class)))
                .thenReturn(CompletableFuture.completedFuture(new ArrayList<>()));
        
        // Act
        viewModel.searchImmediate(query);
        
        // Assert
        verify(isLoadingObserver, timeout(1000)).onChanged(true);
        verify(isLoadingObserver, timeout(1000)).onChanged(false);
    }

    @Test
    public void sanitizeQuery_ShouldRemoveHarmfulCharacters() {
        // Arrange
        String maliciousQuery = "test'; DROP TABLE users; --";
        
        // Act
        viewModel.search(maliciousQuery);
        
        // Assert - Should sanitize the query and not contain harmful characters
        verify(searchQueryObserver).onChanged("test DROP TABLE users --");
    }

    @Test
    public void sanitizeQuery_ShouldLimitLength() {
        // Arrange
        StringBuilder longQuery = new StringBuilder();
        for (int i = 0; i < 150; i++) {
            longQuery.append("a");
        }
        
        // Act
        viewModel.search(longQuery.toString());
        
        // Assert - Should limit to 100 characters
        verify(searchQueryObserver).onChanged(argThat(query -> query.length() <= 100));
    }

    @Test
    public void onCleared_ShouldCancelPendingSearches() {
        // Arrange
        viewModel.search("test query");
        
        // Act
        viewModel.onCleared();
        
        // Assert - No exception should be thrown
        // SearchHandler.cancelSearch() should be called internally
    }
}