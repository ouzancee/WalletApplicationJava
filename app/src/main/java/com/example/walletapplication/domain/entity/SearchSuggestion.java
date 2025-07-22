package com.example.walletapplication.domain.entity;

import java.util.Objects;

/**
 * Data class representing a search suggestion with type enumeration.
 * Used to provide contextual search suggestions to users.
 */
public class SearchSuggestion {
    private final String text;
    private final SearchSuggestionType type;
    private final String category;
    private final int frequency;

    public SearchSuggestion(String text, SearchSuggestionType type, String category, int frequency) {
        this.text = text;
        this.type = type;
        this.category = category;
        this.frequency = frequency;
    }

    public String getText() {
        return text;
    }

    public SearchSuggestionType getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public int getFrequency() {
        return frequency;
    }

    /**
     * Checks if this suggestion is valid for display.
     * @return true if the suggestion has valid text and type
     */
    public boolean isValid() {
        return text != null && !text.trim().isEmpty() && type != null;
    }

    /**
     * Gets the display text for this suggestion, including category if available.
     * @return formatted display text
     */
    public String getDisplayText() {
        if (category != null && !category.trim().isEmpty()) {
            return text + " (" + category + ")";
        }
        return text;
    }

    /**
     * Creates a suggestion for recent search.
     * @param searchText the search text
     * @param frequency how often this search was performed
     * @return SearchSuggestion instance
     */
    public static SearchSuggestion createRecentSearch(String searchText, int frequency) {
        return new SearchSuggestion(searchText, SearchSuggestionType.RECENT_SEARCH, null, frequency);
    }

    /**
     * Creates a suggestion for transaction description.
     * @param description the transaction description
     * @param category the associated category
     * @param frequency how often this description appears
     * @return SearchSuggestion instance
     */
    public static SearchSuggestion createTransactionDescription(String description, String category, int frequency) {
        return new SearchSuggestion(description, SearchSuggestionType.TRANSACTION_DESCRIPTION, category, frequency);
    }

    /**
     * Creates a suggestion for category name.
     * @param categoryName the category name
     * @param frequency how often this category is used
     * @return SearchSuggestion instance
     */
    public static SearchSuggestion createCategoryName(String categoryName, int frequency) {
        return new SearchSuggestion(categoryName, SearchSuggestionType.CATEGORY_NAME, null, frequency);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchSuggestion that = (SearchSuggestion) o;
        return frequency == that.frequency &&
               Objects.equals(text, that.text) &&
               type == that.type &&
               Objects.equals(category, that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, type, category, frequency);
    }

    @Override
    public String toString() {
        return "SearchSuggestion{" +
                "text='" + text + '\'' +
                ", type=" + type +
                ", category='" + category + '\'' +
                ", frequency=" + frequency +
                '}';
    }
}