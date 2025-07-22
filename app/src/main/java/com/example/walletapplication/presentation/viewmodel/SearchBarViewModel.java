package com.example.walletapplication.presentation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.walletapplication.domain.entity.SearchFilters;
import com.example.walletapplication.domain.entity.SearchHistory;
import com.example.walletapplication.domain.entity.SearchSuggestion;
import com.example.walletapplication.domain.search.SearchBarConfig;
import com.example.walletapplication.domain.search.SearchSuggestionProvider;
import com.example.walletapplication.domain.usecase.transaction.AdvancedSearchUseCase;
import com.example.walletapplication.presentation.util.SearchHandler;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel for SearchBar component that manages search state and business logic.
 * Provides debounced search functionality, filter management, and search suggestions.
 */
@HiltViewModel
public class SearchBarViewModel extends ViewModel {
    
    private final AdvancedSearchUseCase advancedSearchUseCase;
    private final SearchHandler searchHandler;
    private SearchSuggestionProvider suggestionProvider;
    private SearchBarConfig config;
    
    // Search state LiveData
    private final MutableLiveData<String> _searchQuery = new MutableLiveData<>();
    private final MutableLiveData<SearchFilters> _activeFilters = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> _error = new MutableLiveData<>();
    private final MutableLiveData<List<SearchSuggestion>> _suggestions = new MutableLiveData<>();
    private final MutableLiveData<List<SearchHistory>> _searchHistory = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _isExpanded = new MutableLiveData<>();
    
    // Public LiveData getters
    public LiveData<String> getSearchQuery() {
        return _searchQuery;
    }
    
    public LiveData<SearchFilters> getActiveFilters() {
        return _activeFilters;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return _isLoading;
    }
    
    public LiveData<String> getError() {
        return _error;
    }
    
    public LiveData<List<SearchSuggestion>> getSuggestions() {
        return _suggestions;
    }
    
    public LiveData<List<SearchHistory>> getSearchHistory() {
        return _searchHistory;
    }
    
    public LiveData<Boolean> getIsExpanded() {
        return _isExpanded;
    }
    
    @Inject
    public SearchBarViewModel(AdvancedSearchUseCase advancedSearchUseCase) {
        this.advancedSearchUseCase = advancedSearchUseCase;
        this.searchHandler = new SearchHandler(300); // 300ms debounce delay
        
        initializeDefaultValues();
    }
    
    /**
     * Constructor for testing purposes (without Hilt injection).
     * @param advancedSearchUseCase AdvancedSearchUseCase instance
     * @param searchHandler SearchHandler instance
     */
    public SearchBarViewModel(AdvancedSearchUseCase advancedSearchUseCase, SearchHandler searchHandler) {
        this.advancedSearchUseCase = advancedSearchUseCase;
        this.searchHandler = searchHandler;
        
        initializeDefaultValues();
    }
    
    /**
     * Initializes default values for LiveData properties.
     */
    private void initializeDefaultValues() {
        _searchQuery.setValue("");
        _activeFilters.setValue(SearchFilters.empty());
        _isLoading.setValue(false);
        _error.setValue(null);
        _suggestions.setValue(new ArrayList<>());
        _searchHistory.setValue(new ArrayList<>());
        _isExpanded.setValue(false);
    }
    
    /**
     * Sets the configuration for this SearchBar instance.
     * @param config SearchBarConfig containing settings and options
     */
    public void setConfig(SearchBarConfig config) {
        this.config = config;
        if (config != null) {
            // Update debounce delay if specified in config
            searchHandler.cancelSearch();
        }
    }
    
    /**
     * Sets the suggestion provider for this SearchBar.
     * @param provider SearchSuggestionProvider implementation
     */
    public void setSuggestionProvider(SearchSuggestionProvider provider) {
        this.suggestionProvider = provider;
    }
    
    /**
     * Performs debounced search with the given query.
     * @param query Search query text
     */
    public void search(String query) {
        String sanitizedQuery = sanitizeQuery(query);
        
        if (!isValidQuery(sanitizedQuery)) {
            _error.setValue("Invalid search query");
            return;
        }
        
        _searchQuery.setValue(sanitizedQuery);
        _error.setValue(null);
        
        // Use SearchHandler for debounced search
        searchHandler.search(sanitizedQuery, new SearchHandler.SearchCallback() {
            @Override
            public void onSearch(String query) {
                performSearch(query);
            }
        });
        
        // Update suggestions based on query
        updateSuggestions(sanitizedQuery);
    }
    
    /**
     * Performs immediate search without debouncing.
     * @param query Search query text
     */
    public void searchImmediate(String query) {
        String sanitizedQuery = sanitizeQuery(query);
        
        if (!isValidQuery(sanitizedQuery)) {
            _error.setValue("Invalid search query");
            return;
        }
        
        _searchQuery.setValue(sanitizedQuery);
        _error.setValue(null);
        
        searchHandler.searchImmediate(sanitizedQuery, new SearchHandler.SearchCallback() {
            @Override
            public void onSearch(String query) {
                performSearch(query);
            }
        });
        
        // Save to search history if not empty
        if (!sanitizedQuery.trim().isEmpty()) {
            saveToSearchHistory(sanitizedQuery);
        }
    }
    
    /**
     * Applies search filters and performs search.
     * @param filters SearchFilters to apply
     */
    public void applyFilters(SearchFilters filters) {
        if (filters == null || !filters.isValid()) {
            _error.setValue("Invalid filter parameters");
            return;
        }
        
        _activeFilters.setValue(filters);
        _error.setValue(null);
        
        // Perform search with current query and new filters
        String currentQuery = _searchQuery.getValue();
        if (currentQuery != null) {
            performSearchWithFilters(currentQuery, filters);
        }
    }
    
    /**
     * Clears all active filters.
     */
    public void clearFilters() {
        _activeFilters.setValue(SearchFilters.empty());
        
        // Re-perform search without filters
        String currentQuery = _searchQuery.getValue();
        if (currentQuery != null && !currentQuery.trim().isEmpty()) {
            performSearch(currentQuery);
        }
    }
    
    /**
     * Clears the search query and resets state.
     */
    public void clearSearch() {
        searchHandler.cancelSearch();
        _searchQuery.setValue("");
        _suggestions.setValue(new ArrayList<>());
        _error.setValue(null);
        _isLoading.setValue(false);
    }
    
    /**
     * Expands the search interface.
     */
    public void expand() {
        _isExpanded.setValue(true);
        loadSearchHistory();
    }
    
    /**
     * Collapses the search interface.
     */
    public void collapse() {
        _isExpanded.setValue(false);
        _suggestions.setValue(new ArrayList<>());
    }
    
    /**
     * Selects a search suggestion and performs search.
     * @param suggestion Selected SearchSuggestion
     */
    public void selectSuggestion(SearchSuggestion suggestion) {
        if (suggestion != null && suggestion.isValid()) {
            searchImmediate(suggestion.getText());
        }
    }
    
    /**
     * Selects a search history item and performs search.
     * @param historyItem Selected SearchHistory
     */
    public void selectHistoryItem(SearchHistory historyItem) {
        if (historyItem != null && historyItem.isValid()) {
            _searchQuery.setValue(historyItem.getQuery());
            
            // Apply filters if they exist in history
            if (historyItem.hasFilters()) {
                _activeFilters.setValue(historyItem.getAppliedFilters());
            }
            
            searchImmediate(historyItem.getQuery());
        }
    }
    
    /**
     * Gets the current active filter count.
     * @return number of active filters
     */
    public int getActiveFilterCount() {
        SearchFilters filters = _activeFilters.getValue();
        return filters != null ? filters.getActiveFilterCount() : 0;
    }
    
    /**
     * Checks if any filters are currently active.
     * @return true if filters are active
     */
    public boolean hasActiveFilters() {
        SearchFilters filters = _activeFilters.getValue();
        return filters != null && filters.hasActiveFilters();
    }
    
    /**
     * Performs the actual search operation.
     * @param query Search query
     */
    private void performSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            return;
        }
        
        _isLoading.setValue(true);
        _error.setValue(null);
        
        // Create search criteria
        AdvancedSearchUseCase.SearchCriteria criteria = new AdvancedSearchUseCase.SearchCriteria()
                .setTextQuery(query);
        
        // Apply active filters to criteria
        SearchFilters activeFilters = _activeFilters.getValue();
        if (activeFilters != null && activeFilters.hasActiveFilters()) {
            applyCriteriaFromFilters(criteria, activeFilters);
        }
        
        // Perform search using AdvancedSearchUseCase
        advancedSearchUseCase.search(criteria)
                .thenAccept(results -> {
                    _isLoading.postValue(false);
                    // Results are handled by the listener/fragment
                })
                .exceptionally(throwable -> {
                    _error.postValue("Search failed: " + throwable.getMessage());
                    _isLoading.postValue(false);
                    return null;
                });
    }
    
    /**
     * Performs search with specific filters.
     * @param query Search query
     * @param filters SearchFilters to apply
     */
    private void performSearchWithFilters(String query, SearchFilters filters) {
        _isLoading.setValue(true);
        _error.setValue(null);
        
        // Create search criteria with filters
        AdvancedSearchUseCase.SearchCriteria criteria = new AdvancedSearchUseCase.SearchCriteria()
                .setTextQuery(query);
        
        applyCriteriaFromFilters(criteria, filters);
        
        // Perform search
        advancedSearchUseCase.search(criteria)
                .thenAccept(results -> {
                    _isLoading.postValue(false);
                    // Results are handled by the listener/fragment
                })
                .exceptionally(throwable -> {
                    _error.postValue("Search with filters failed: " + throwable.getMessage());
                    _isLoading.postValue(false);
                    return null;
                });
    }
    
    /**
     * Applies SearchFilters to AdvancedSearchUseCase.SearchCriteria.
     * @param criteria SearchCriteria to modify
     * @param filters SearchFilters to apply
     */
    private void applyCriteriaFromFilters(AdvancedSearchUseCase.SearchCriteria criteria, SearchFilters filters) {
        if (filters.getType() != null) {
            criteria.setType(filters.getType());
        }
        if (filters.getCategory() != null && !filters.getCategory().trim().isEmpty()) {
            criteria.setCategory(filters.getCategory());
        }
        if (filters.getMinAmount() != null || filters.getMaxAmount() != null) {
            criteria.setAmountRange(filters.getMinAmount(), filters.getMaxAmount());
        }
        if (filters.getStartDate() != null || filters.getEndDate() != null) {
            criteria.setDateRange(filters.getStartDate(), filters.getEndDate());
        }
    }
    
    /**
     * Updates search suggestions based on query.
     * @param query Current search query
     */
    private void updateSuggestions(String query) {
        if (suggestionProvider == null) {
            return;
        }
        
        if (query == null || query.trim().isEmpty()) {
            // Show recent searches when query is empty
            loadSearchHistory();
            return;
        }
        
        // Get suggestions from provider
        suggestionProvider.getSuggestions(query).observeForever(suggestions -> {
            if (suggestions != null) {
                _suggestions.setValue(suggestions);
            }
        });
    }
    
    /**
     * Loads search history from provider.
     */
    private void loadSearchHistory() {
        if (suggestionProvider == null) {
            return;
        }
        
        List<String> recentSearches = suggestionProvider.getRecentSearches();
        List<SearchHistory> historyList = new ArrayList<>();
        
        for (String search : recentSearches) {
            historyList.add(SearchHistory.create(search, null));
        }
        
        _searchHistory.setValue(historyList);
    }
    
    /**
     * Saves search query to history.
     * @param query Search query to save
     */
    private void saveToSearchHistory(String query) {
        if (suggestionProvider != null && query != null && !query.trim().isEmpty()) {
            suggestionProvider.saveSearchQuery(query);
        }
    }
    
    /**
     * Sanitizes search query by trimming and removing invalid characters.
     * @param query Raw search query
     * @return Sanitized query
     */
    private String sanitizeQuery(String query) {
        if (query == null) {
            return "";
        }
        
        // Trim whitespace
        String sanitized = query.trim();
        
        // Remove potentially harmful characters (basic SQL injection prevention)
        sanitized = sanitized.replaceAll("[';\"\\\\]", "");
        
        // Limit length to prevent excessive queries
        if (sanitized.length() > 100) {
            sanitized = sanitized.substring(0, 100);
        }
        
        return sanitized;
    }
    
    /**
     * Validates search query.
     * @param query Query to validate
     * @return true if query is valid
     */
    private boolean isValidQuery(String query) {
        if (query == null) {
            return false;
        }
        
        // Allow empty queries (for clearing search)
        if (query.trim().isEmpty()) {
            return true;
        }
        
        // Check minimum length
        if (query.trim().length() < 1) {
            return false;
        }
        
        // Check for only whitespace or special characters
        if (query.matches("^[\\s\\p{Punct}]*$")) {
            return false;
        }
        
        return true;
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        // Cancel any pending searches
        searchHandler.cancelSearch();
    }
}