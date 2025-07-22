package com.example.walletapplication.domain.search;

import com.example.walletapplication.domain.entity.SearchFilters;

/**
 * Interface for handling SearchBar events.
 * Implementations should handle search queries, filter changes, and search state changes.
 */
public interface SearchBarListener {
    
    /**
     * Called when the search query changes (typically with debouncing).
     * @param query the current search query
     */
    void onSearchQueryChanged(String query);
    
    /**
     * Called when the user submits a search (e.g., pressing enter or search button).
     * @param query the submitted search query
     */
    void onSearchSubmitted(String query);
    
    /**
     * Called when search filters are changed.
     * @param filters the updated search filters
     */
    void onFiltersChanged(SearchFilters filters);
    
    /**
     * Called when the search is cleared (query and filters reset).
     */
    void onSearchCleared();
    
    /**
     * Called when the SearchBar gains focus and becomes active.
     */
    default void onSearchFocused() {
        // Default empty implementation
    }
    
    /**
     * Called when the SearchBar loses focus.
     */
    default void onSearchUnfocused() {
        // Default empty implementation
    }
    
    /**
     * Called when a search suggestion is selected.
     * @param suggestion the selected suggestion text
     */
    default void onSuggestionSelected(String suggestion) {
        // Default implementation delegates to search query changed
        onSearchQueryChanged(suggestion);
    }
}