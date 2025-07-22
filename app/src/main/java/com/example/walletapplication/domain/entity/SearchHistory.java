package com.example.walletapplication.domain.entity;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Data class representing search history for persistence.
 * Stores search queries along with applied filters and timestamps.
 */
public class SearchHistory {
    private final Long id;
    private final String query;
    private final LocalDateTime timestamp;
    private final SearchFilters appliedFilters;

    public SearchHistory(Long id, String query, LocalDateTime timestamp, SearchFilters appliedFilters) {
        this.id = id;
        this.query = query;
        this.timestamp = timestamp;
        this.appliedFilters = appliedFilters;
    }

    public Long getId() {
        return id;
    }

    public String getQuery() {
        return query;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public SearchFilters getAppliedFilters() {
        return appliedFilters;
    }

    /**
     * Checks if this search history entry is valid.
     * @return true if the entry has a valid query and timestamp
     */
    public boolean isValid() {
        return query != null && !query.trim().isEmpty() && timestamp != null;
    }

    /**
     * Gets a display-friendly representation of this search.
     * @return formatted search text with filter info if applicable
     */
    public String getDisplayText() {
        StringBuilder display = new StringBuilder(query);
        
        if (appliedFilters != null && appliedFilters.hasActiveFilters()) {
            display.append(" (").append(appliedFilters.getActiveFilterCount()).append(" filtre)");
        }
        
        return display.toString();
    }

    /**
     * Checks if this search has any filters applied.
     * @return true if filters were applied with this search
     */
    public boolean hasFilters() {
        return appliedFilters != null && appliedFilters.hasActiveFilters();
    }

    /**
     * Creates a new SearchHistory entry with current timestamp.
     * @param query the search query
     * @param appliedFilters the filters that were applied
     * @return SearchHistory instance
     */
    public static SearchHistory create(String query, SearchFilters appliedFilters) {
        return new SearchHistory(null, query, LocalDateTime.now(), appliedFilters);
    }

    /**
     * Creates a new SearchHistory entry with specified timestamp.
     * @param query the search query
     * @param timestamp when the search was performed
     * @param appliedFilters the filters that were applied
     * @return SearchHistory instance
     */
    public static SearchHistory create(String query, LocalDateTime timestamp, SearchFilters appliedFilters) {
        return new SearchHistory(null, query, timestamp, appliedFilters);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchHistory that = (SearchHistory) o;
        return Objects.equals(id, that.id) &&
               Objects.equals(query, that.query) &&
               Objects.equals(timestamp, that.timestamp) &&
               Objects.equals(appliedFilters, that.appliedFilters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, query, timestamp, appliedFilters);
    }

    @Override
    public String toString() {
        return "SearchHistory{" +
                "id=" + id +
                ", query='" + query + '\'' +
                ", timestamp=" + timestamp +
                ", appliedFilters=" + appliedFilters +
                '}';
    }

    /**
     * Builder class for creating SearchHistory instances.
     */
    public static class Builder {
        private Long id;
        private String query;
        private LocalDateTime timestamp;
        private SearchFilters appliedFilters;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setQuery(String query) {
            this.query = query;
            return this;
        }

        public Builder setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder setAppliedFilters(SearchFilters appliedFilters) {
            this.appliedFilters = appliedFilters;
            return this;
        }

        public SearchHistory build() {
            if (timestamp == null) {
                timestamp = LocalDateTime.now();
            }
            return new SearchHistory(id, query, timestamp, appliedFilters);
        }
    }
}