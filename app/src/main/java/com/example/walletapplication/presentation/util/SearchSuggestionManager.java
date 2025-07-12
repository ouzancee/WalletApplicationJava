package com.example.walletapplication.presentation.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Manages search suggestions including recent searches and popular categories
 */
public class SearchSuggestionManager {
    
    private static final String PREFS_NAME = "search_suggestions";
    private static final String KEY_RECENT_SEARCHES = "recent_searches";
    private static final String KEY_POPULAR_CATEGORIES = "popular_categories";
    private static final int MAX_RECENT_SEARCHES = 10;
    private static final int MAX_POPULAR_CATEGORIES = 5;
    
    private final SharedPreferences sharedPreferences;
    private final Gson gson;
    
    // Popular categories based on typical usage patterns
    private final List<String> defaultPopularCategories = new ArrayList<String>() {{
        add("Yemek");
        add("Ulaşım");
        add("Alışveriş");
        add("Faturalar");
        add("Eğlence");
    }};
    
    public SearchSuggestionManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
    }
    
    /**
     * Adds a search query to recent searches
     * @param query Search query to add
     */
    public void addRecentSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            return;
        }
        
        query = query.trim();
        List<String> recentSearches = getRecentSearches();
        
        // Remove if already exists to avoid duplicates
        recentSearches.remove(query);
        
        // Add to beginning
        recentSearches.add(0, query);
        
        // Keep only max number of recent searches
        if (recentSearches.size() > MAX_RECENT_SEARCHES) {
            recentSearches = recentSearches.subList(0, MAX_RECENT_SEARCHES);
        }
        
        // Save to preferences
        saveRecentSearches(recentSearches);
    }
    
    /**
     * Gets recent search queries
     * @return List of recent searches
     */
    public List<String> getRecentSearches() {
        String json = sharedPreferences.getString(KEY_RECENT_SEARCHES, "[]");
        Type type = new TypeToken<List<String>>(){}.getType();
        List<String> recentSearches = gson.fromJson(json, type);
        return recentSearches != null ? recentSearches : new ArrayList<>();
    }
    
    /**
     * Clears all recent searches
     */
    public void clearRecentSearches() {
        sharedPreferences.edit().remove(KEY_RECENT_SEARCHES).apply();
    }
    
    /**
     * Adds a category to popular categories based on usage
     * @param category Category name
     */
    public void addPopularCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return;
        }
        
        category = category.trim();
        List<String> popularCategories = getPopularCategories();
        
        // Remove if already exists
        popularCategories.remove(category);
        
        // Add to beginning
        popularCategories.add(0, category);
        
        // Keep only max number of popular categories
        if (popularCategories.size() > MAX_POPULAR_CATEGORIES) {
            popularCategories = popularCategories.subList(0, MAX_POPULAR_CATEGORIES);
        }
        
        // Save to preferences
        savePopularCategories(popularCategories);
    }
    
    /**
     * Gets popular categories
     * @return List of popular categories
     */
    public List<String> getPopularCategories() {
        String json = sharedPreferences.getString(KEY_POPULAR_CATEGORIES, null);
        if (json == null) {
            // Return default popular categories if none saved
            return new ArrayList<>(defaultPopularCategories);
        }
        
        Type type = new TypeToken<List<String>>(){}.getType();
        List<String> popularCategories = gson.fromJson(json, type);
        return popularCategories != null ? popularCategories : new ArrayList<>(defaultPopularCategories);
    }
    
    /**
     * Gets search suggestions combining recent searches and popular categories
     * @param query Current query to filter suggestions
     * @return List of search suggestions
     */
    public List<SearchSuggestion> getSearchSuggestions(String query) {
        List<SearchSuggestion> suggestions = new ArrayList<>();
        String lowerQuery = query != null ? query.toLowerCase() : "";
        
        // Add recent searches that match the query
        List<String> recentSearches = getRecentSearches();
        for (String recentSearch : recentSearches) {
            if (recentSearch.toLowerCase().contains(lowerQuery)) {
                suggestions.add(new SearchSuggestion(recentSearch, SearchSuggestionType.RECENT_SEARCH));
            }
        }
        
        // Add popular categories that match the query
        List<String> popularCategories = getPopularCategories();
        for (String category : popularCategories) {
            if (category.toLowerCase().contains(lowerQuery)) {
                suggestions.add(new SearchSuggestion(category, SearchSuggestionType.POPULAR_CATEGORY));
            }
        }
        
        // Remove duplicates while preserving order
        Set<String> seen = new LinkedHashSet<>();
        List<SearchSuggestion> uniqueSuggestions = new ArrayList<>();
        for (SearchSuggestion suggestion : suggestions) {
            if (seen.add(suggestion.getText())) {
                uniqueSuggestions.add(suggestion);
            }
        }
        
        return uniqueSuggestions;
    }
    
    /**
     * Gets quick search suggestions for empty query
     * @return List of quick suggestions
     */
    public List<SearchSuggestion> getQuickSuggestions() {
        List<SearchSuggestion> suggestions = new ArrayList<>();
        
        // Add recent searches (limited)
        List<String> recentSearches = getRecentSearches();
        int recentCount = Math.min(recentSearches.size(), 3);
        for (int i = 0; i < recentCount; i++) {
            suggestions.add(new SearchSuggestion(recentSearches.get(i), SearchSuggestionType.RECENT_SEARCH));
        }
        
        // Add popular categories
        List<String> popularCategories = getPopularCategories();
        for (String category : popularCategories) {
            suggestions.add(new SearchSuggestion(category, SearchSuggestionType.POPULAR_CATEGORY));
        }
        
        return suggestions;
    }
    
    private void saveRecentSearches(List<String> recentSearches) {
        String json = gson.toJson(recentSearches);
        sharedPreferences.edit().putString(KEY_RECENT_SEARCHES, json).apply();
    }
    
    private void savePopularCategories(List<String> popularCategories) {
        String json = gson.toJson(popularCategories);
        sharedPreferences.edit().putString(KEY_POPULAR_CATEGORIES, json).apply();
    }
    
    /**
     * Search suggestion data class
     */
    public static class SearchSuggestion {
        private final String text;
        private final SearchSuggestionType type;
        
        public SearchSuggestion(String text, SearchSuggestionType type) {
            this.text = text;
            this.type = type;
        }
        
        public String getText() {
            return text;
        }
        
        public SearchSuggestionType getType() {
            return type;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SearchSuggestion that = (SearchSuggestion) o;
            return text.equals(that.text);
        }
        
        @Override
        public int hashCode() {
            return text.hashCode();
        }
    }
    
    /**
     * Types of search suggestions
     */
    public enum SearchSuggestionType {
        RECENT_SEARCH,
        POPULAR_CATEGORY
    }
} 