package com.example.walletapplication.domain.search;

import com.example.walletapplication.domain.entity.SearchSuggestionType;
import com.example.walletapplication.domain.entity.TransactionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Configuration class for SearchBar component that defines behavior and options
 * for different search contexts throughout the application.
 */
public class SearchBarConfig {
    
    // Default configuration values
    private static final String DEFAULT_PLACEHOLDER = "Ara...";
    private static final int DEFAULT_DEBOUNCE_DELAY = 300;
    private static final int DEFAULT_MAX_SUGGESTIONS = 10;
    private static final int DEFAULT_MAX_HISTORY = 50;
    private static final boolean DEFAULT_FILTERS_ENABLED = true;
    private static final boolean DEFAULT_SUGGESTIONS_ENABLED = true;
    private static final boolean DEFAULT_HISTORY_ENABLED = true;
    
    // Configuration properties
    private final String placeholderText;
    private final List<SearchContext> enabledSearchContexts;
    private final List<SearchSuggestionType> enabledSuggestionTypes;
    private final List<FilterOption> availableFilterOptions;
    private final int debounceDelayMs;
    private final int maxSuggestions;
    private final int maxHistoryEntries;
    private final boolean filtersEnabled;
    private final boolean suggestionsEnabled;
    private final boolean historyEnabled;
    private final boolean expandable;
    private final String searchHint;
    
    private SearchBarConfig(Builder builder) {
        this.placeholderText = builder.placeholderText;
        this.enabledSearchContexts = Collections.unmodifiableList(new ArrayList<>(builder.enabledSearchContexts));
        this.enabledSuggestionTypes = Collections.unmodifiableList(new ArrayList<>(builder.enabledSuggestionTypes));
        this.availableFilterOptions = Collections.unmodifiableList(new ArrayList<>(builder.availableFilterOptions));
        this.debounceDelayMs = builder.debounceDelayMs;
        this.maxSuggestions = builder.maxSuggestions;
        this.maxHistoryEntries = builder.maxHistoryEntries;
        this.filtersEnabled = builder.filtersEnabled;
        this.suggestionsEnabled = builder.suggestionsEnabled;
        this.historyEnabled = builder.historyEnabled;
        this.expandable = builder.expandable;
        this.searchHint = builder.searchHint;
    }
    
    // Getters
    public String getPlaceholderText() {
        return placeholderText;
    }
    
    public List<SearchContext> getEnabledSearchContexts() {
        return enabledSearchContexts;
    }
    
    public List<SearchSuggestionType> getEnabledSuggestionTypes() {
        return enabledSuggestionTypes;
    }
    
    public List<FilterOption> getAvailableFilterOptions() {
        return availableFilterOptions;
    }
    
    public int getDebounceDelayMs() {
        return debounceDelayMs;
    }
    
    public int getMaxSuggestions() {
        return maxSuggestions;
    }
    
    public int getMaxHistoryEntries() {
        return maxHistoryEntries;
    }
    
    public boolean isFiltersEnabled() {
        return filtersEnabled;
    }
    
    public boolean isSuggestionsEnabled() {
        return suggestionsEnabled;
    }
    
    public boolean isHistoryEnabled() {
        return historyEnabled;
    }
    
    public boolean isExpandable() {
        return expandable;
    }
    
    public String getSearchHint() {
        return searchHint;
    }
    
    /**
     * Validates the configuration parameters.
     * @return true if configuration is valid, false otherwise
     */
    public boolean isValid() {
        // Check required fields
        if (placeholderText == null || placeholderText.trim().isEmpty()) {
            return false;
        }
        
        // Check numeric constraints
        if (debounceDelayMs < 0 || debounceDelayMs > 5000) {
            return false;
        }
        
        if (maxSuggestions < 1 || maxSuggestions > 50) {
            return false;
        }
        
        if (maxHistoryEntries < 1 || maxHistoryEntries > 1000) {
            return false;
        }
        
        // Check that at least one search context is enabled
        if (enabledSearchContexts.isEmpty()) {
            return false;
        }
        
        // If suggestions are enabled, at least one suggestion type should be enabled
        if (suggestionsEnabled && enabledSuggestionTypes.isEmpty()) {
            return false;
        }
        
        // If filters are enabled, at least one filter option should be available
        if (filtersEnabled && availableFilterOptions.isEmpty()) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Enumeration of different search contexts in the application.
     */
    public enum SearchContext {
        TRANSACTIONS("İşlemler"),
        CATEGORIES("Kategoriler"), 
        REPORTS("Raporlar"),
        ALL("Tümü");
        
        private final String displayName;
        
        SearchContext(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * Enumeration of available filter options.
     */
    public enum FilterOption {
        TRANSACTION_TYPE("İşlem Türü"),
        CATEGORY("Kategori"),
        AMOUNT_RANGE("Tutar Aralığı"),
        DATE_RANGE("Tarih Aralığı"),
        DESCRIPTION("Açıklama");
        
        private final String displayName;
        
        FilterOption(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchBarConfig that = (SearchBarConfig) o;
        return debounceDelayMs == that.debounceDelayMs &&
               maxSuggestions == that.maxSuggestions &&
               maxHistoryEntries == that.maxHistoryEntries &&
               filtersEnabled == that.filtersEnabled &&
               suggestionsEnabled == that.suggestionsEnabled &&
               historyEnabled == that.historyEnabled &&
               expandable == that.expandable &&
               Objects.equals(placeholderText, that.placeholderText) &&
               Objects.equals(enabledSearchContexts, that.enabledSearchContexts) &&
               Objects.equals(enabledSuggestionTypes, that.enabledSuggestionTypes) &&
               Objects.equals(availableFilterOptions, that.availableFilterOptions) &&
               Objects.equals(searchHint, that.searchHint);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(placeholderText, enabledSearchContexts, enabledSuggestionTypes,
                availableFilterOptions, debounceDelayMs, maxSuggestions, maxHistoryEntries,
                filtersEnabled, suggestionsEnabled, historyEnabled, expandable, searchHint);
    }
    
    @Override
    public String toString() {
        return "SearchBarConfig{" +
                "placeholderText='" + placeholderText + '\'' +
                ", enabledSearchContexts=" + enabledSearchContexts +
                ", enabledSuggestionTypes=" + enabledSuggestionTypes +
                ", availableFilterOptions=" + availableFilterOptions +
                ", debounceDelayMs=" + debounceDelayMs +
                ", maxSuggestions=" + maxSuggestions +
                ", maxHistoryEntries=" + maxHistoryEntries +
                ", filtersEnabled=" + filtersEnabled +
                ", suggestionsEnabled=" + suggestionsEnabled +
                ", historyEnabled=" + historyEnabled +
                ", expandable=" + expandable +
                ", searchHint='" + searchHint + '\'' +
                '}';
    }    

    /**
     * Builder class for creating SearchBarConfig instances with fluent API.
     */
    public static class Builder {
        private String placeholderText = DEFAULT_PLACEHOLDER;
        private List<SearchContext> enabledSearchContexts = new ArrayList<>();
        private List<SearchSuggestionType> enabledSuggestionTypes = new ArrayList<>();
        private List<FilterOption> availableFilterOptions = new ArrayList<>();
        private int debounceDelayMs = DEFAULT_DEBOUNCE_DELAY;
        private int maxSuggestions = DEFAULT_MAX_SUGGESTIONS;
        private int maxHistoryEntries = DEFAULT_MAX_HISTORY;
        private boolean filtersEnabled = DEFAULT_FILTERS_ENABLED;
        private boolean suggestionsEnabled = DEFAULT_SUGGESTIONS_ENABLED;
        private boolean historyEnabled = DEFAULT_HISTORY_ENABLED;
        private boolean expandable = true;
        private String searchHint = "";
        
        public Builder setPlaceholderText(String placeholderText) {
            this.placeholderText = placeholderText;
            return this;
        }
        
        public Builder addSearchContext(SearchContext context) {
            if (!this.enabledSearchContexts.contains(context)) {
                this.enabledSearchContexts.add(context);
            }
            return this;
        }
        
        public Builder setSearchContexts(SearchContext... contexts) {
            this.enabledSearchContexts.clear();
            this.enabledSearchContexts.addAll(Arrays.asList(contexts));
            return this;
        }
        
        public Builder addSuggestionType(SearchSuggestionType type) {
            if (!this.enabledSuggestionTypes.contains(type)) {
                this.enabledSuggestionTypes.add(type);
            }
            return this;
        }
        
        public Builder setSuggestionTypes(SearchSuggestionType... types) {
            this.enabledSuggestionTypes.clear();
            this.enabledSuggestionTypes.addAll(Arrays.asList(types));
            return this;
        }
        
        public Builder addFilterOption(FilterOption option) {
            if (!this.availableFilterOptions.contains(option)) {
                this.availableFilterOptions.add(option);
            }
            return this;
        }
        
        public Builder setFilterOptions(FilterOption... options) {
            this.availableFilterOptions.clear();
            this.availableFilterOptions.addAll(Arrays.asList(options));
            return this;
        }
        
        public Builder setDebounceDelayMs(int delayMs) {
            this.debounceDelayMs = delayMs;
            return this;
        }
        
        public Builder setMaxSuggestions(int maxSuggestions) {
            this.maxSuggestions = maxSuggestions;
            return this;
        }
        
        public Builder setMaxHistoryEntries(int maxHistoryEntries) {
            this.maxHistoryEntries = maxHistoryEntries;
            return this;
        }
        
        public Builder setFiltersEnabled(boolean enabled) {
            this.filtersEnabled = enabled;
            return this;
        }
        
        public Builder setSuggestionsEnabled(boolean enabled) {
            this.suggestionsEnabled = enabled;
            return this;
        }
        
        public Builder setHistoryEnabled(boolean enabled) {
            this.historyEnabled = enabled;
            return this;
        }
        
        public Builder setExpandable(boolean expandable) {
            this.expandable = expandable;
            return this;
        }
        
        public Builder setSearchHint(String hint) {
            this.searchHint = hint;
            return this;
        }
        
        /**
         * Builds the SearchBarConfig instance.
         * @return configured SearchBarConfig instance
         * @throws IllegalStateException if configuration is invalid
         */
        public SearchBarConfig build() {
            SearchBarConfig config = new SearchBarConfig(this);
            if (!config.isValid()) {
                throw new IllegalStateException("Invalid SearchBarConfig: " + config.toString());
            }
            return config;
        }
    }
    
    // Factory methods for common configurations
    
    /**
     * Creates a default SearchBarConfig for transaction search.
     * @return SearchBarConfig configured for transaction search
     */
    public static SearchBarConfig forTransactions() {
        return new Builder()
                .setPlaceholderText("İşlem ara...")
                .addSearchContext(SearchContext.TRANSACTIONS)
                .setSuggestionTypes(
                        SearchSuggestionType.RECENT_SEARCH,
                        SearchSuggestionType.TRANSACTION_DESCRIPTION,
                        SearchSuggestionType.CATEGORY_NAME,
                        SearchSuggestionType.AMOUNT_SUGGESTION
                )
                .setFilterOptions(
                        FilterOption.TRANSACTION_TYPE,
                        FilterOption.CATEGORY,
                        FilterOption.AMOUNT_RANGE,
                        FilterOption.DATE_RANGE,
                        FilterOption.DESCRIPTION
                )
                .setSearchHint("İşlem açıklaması, kategori veya tutar giriniz")
                .build();
    }
    
    /**
     * Creates a SearchBarConfig for category search.
     * @return SearchBarConfig configured for category search
     */
    public static SearchBarConfig forCategories() {
        return new Builder()
                .setPlaceholderText("Kategori ara...")
                .addSearchContext(SearchContext.CATEGORIES)
                .setSuggestionTypes(
                        SearchSuggestionType.RECENT_SEARCH,
                        SearchSuggestionType.CATEGORY_NAME
                )
                .setFilterOptions(
                        FilterOption.CATEGORY
                )
                .setSearchHint("Kategori adı giriniz")
                .build();
    }
    
    /**
     * Creates a SearchBarConfig for report search.
     * @return SearchBarConfig configured for report search
     */
    public static SearchBarConfig forReports() {
        return new Builder()
                .setPlaceholderText("Rapor ara...")
                .addSearchContext(SearchContext.REPORTS)
                .setSuggestionTypes(
                        SearchSuggestionType.RECENT_SEARCH,
                        SearchSuggestionType.DATE_SUGGESTION
                )
                .setFilterOptions(
                        FilterOption.DATE_RANGE,
                        FilterOption.TRANSACTION_TYPE,
                        FilterOption.CATEGORY
                )
                .setSearchHint("Rapor verisi için tarih aralığı veya kategori giriniz")
                .build();
    }
    
    /**
     * Creates a SearchBarConfig for global search across all contexts.
     * @return SearchBarConfig configured for global search
     */
    public static SearchBarConfig forGlobalSearch() {
        return new Builder()
                .setPlaceholderText("Tümünde ara...")
                .setSearchContexts(
                        SearchContext.TRANSACTIONS,
                        SearchContext.CATEGORIES,
                        SearchContext.REPORTS
                )
                .setSuggestionTypes(
                        SearchSuggestionType.RECENT_SEARCH,
                        SearchSuggestionType.TRANSACTION_DESCRIPTION,
                        SearchSuggestionType.CATEGORY_NAME,
                        SearchSuggestionType.AMOUNT_SUGGESTION,
                        SearchSuggestionType.DATE_SUGGESTION
                )
                .setFilterOptions(
                        FilterOption.TRANSACTION_TYPE,
                        FilterOption.CATEGORY,
                        FilterOption.AMOUNT_RANGE,
                        FilterOption.DATE_RANGE,
                        FilterOption.DESCRIPTION
                )
                .setSearchHint("İşlem, kategori veya rapor verisi arayınız")
                .build();
    }
    
    /**
     * Creates a minimal SearchBarConfig with basic search functionality only.
     * @return SearchBarConfig with minimal features
     */
    public static SearchBarConfig minimal() {
        return new Builder()
                .setPlaceholderText("Ara...")
                .addSearchContext(SearchContext.ALL)
                .setFiltersEnabled(false)
                .setSuggestionsEnabled(false)
                .setHistoryEnabled(false)
                .setExpandable(false)
                .build();
    }
    
    /**
     * Creates a SearchBarConfig with custom placeholder and single search context.
     * @param placeholder the placeholder text to display
     * @param context the search context to enable
     * @return SearchBarConfig with specified placeholder and context
     */
    public static SearchBarConfig withPlaceholder(String placeholder, SearchContext context) {
        return new Builder()
                .setPlaceholderText(placeholder)
                .addSearchContext(context)
                .setSuggestionTypes(SearchSuggestionType.RECENT_SEARCH)
                .addFilterOption(FilterOption.DESCRIPTION)
                .build();
    }
    
    /**
     * Creates a copy of this configuration with modifications applied via builder.
     * @return Builder initialized with current configuration values
     */
    public Builder toBuilder() {
        return new Builder()
                .setPlaceholderText(this.placeholderText)
                .setSearchContexts(this.enabledSearchContexts.toArray(new SearchContext[0]))
                .setSuggestionTypes(this.enabledSuggestionTypes.toArray(new SearchSuggestionType[0]))
                .setFilterOptions(this.availableFilterOptions.toArray(new FilterOption[0]))
                .setDebounceDelayMs(this.debounceDelayMs)
                .setMaxSuggestions(this.maxSuggestions)
                .setMaxHistoryEntries(this.maxHistoryEntries)
                .setFiltersEnabled(this.filtersEnabled)
                .setSuggestionsEnabled(this.suggestionsEnabled)
                .setHistoryEnabled(this.historyEnabled)
                .setExpandable(this.expandable)
                .setSearchHint(this.searchHint);
    }
}