package com.example.walletapplication.domain.search;

import androidx.lifecycle.LiveData;
import com.example.walletapplication.domain.entity.SearchSuggestion;

import java.util.List;

/**
 * Interface for providing search suggestions from different sources.
 * Implementations should provide context-specific suggestions based on the search query.
 */
public interface SearchSuggestionProvider {
    
    /**
     * Gets search suggestions based on the provided query.
     * @param query the search query to generate suggestions for
     * @return LiveData containing list of search suggestions
     */
    LiveData<List<SearchSuggestion>> getSuggestions(String query);
    
    /**
     * Gets quick suggestions when no query is provided (e.g., when SearchBar gains focus).
     * @return LiveData containing list of quick suggestions
     */
    LiveData<List<SearchSuggestion>> getQuickSuggestions();
    
    /**
     * Saves a search query to the provider's history/analytics.
     * @param query the search query to save
     */
    void saveSearchQuery(String query);
    
    /**
     * Gets recent search queries from this provider.
     * @return list of recent search queries
     */
    List<String> getRecentSearches();
    
    /**
     * Clears the search history for this provider.
     */
    void clearSearchHistory();
    
    /**
     * Gets the maximum number of suggestions this provider should return.
     * @return maximum number of suggestions
     */
    default int getMaxSuggestions() {
        return 10;
    }
    
    /**
     * Gets the priority of this provider (higher priority providers are queried first).
     * @return provider priority (higher values = higher priority)
     */
    default int getPriority() {
        return 0;
    }
    
    /**
     * Checks if this provider supports the given search context.
     * @param context the search context (e.g., "transactions", "categories", "reports")
     * @return true if this provider supports the context
     */
    default boolean supportsContext(String context) {
        return true;
    }
}