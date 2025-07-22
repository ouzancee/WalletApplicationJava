package com.example.walletapplication.domain.search;

import com.example.walletapplication.domain.entity.SearchSuggestionType;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for SearchBarConfig class.
 */
public class SearchBarConfigTest {

    @Test
    public void testBuilderPattern() {
        SearchBarConfig config = new SearchBarConfig.Builder()
                .setPlaceholderText("Test placeholder")
                .addSearchContext(SearchBarConfig.SearchContext.TRANSACTIONS)
                .addSuggestionType(SearchSuggestionType.RECENT_SEARCH)
                .addFilterOption(SearchBarConfig.FilterOption.CATEGORY)
                .setDebounceDelayMs(500)
                .setMaxSuggestions(5)
                .build();

        assertEquals("Test placeholder", config.getPlaceholderText());
        assertEquals(1, config.getEnabledSearchContexts().size());
        assertEquals(SearchBarConfig.SearchContext.TRANSACTIONS, config.getEnabledSearchContexts().get(0));
        assertEquals(1, config.getEnabledSuggestionTypes().size());
        assertEquals(SearchSuggestionType.RECENT_SEARCH, config.getEnabledSuggestionTypes().get(0));
        assertEquals(1, config.getAvailableFilterOptions().size());
        assertEquals(SearchBarConfig.FilterOption.CATEGORY, config.getAvailableFilterOptions().get(0));
        assertEquals(500, config.getDebounceDelayMs());
        assertEquals(5, config.getMaxSuggestions());
    }

    @Test
    public void testValidationSuccess() {
        SearchBarConfig config = new SearchBarConfig.Builder()
                .setPlaceholderText("Valid placeholder")
                .addSearchContext(SearchBarConfig.SearchContext.TRANSACTIONS)
                .addSuggestionType(SearchSuggestionType.RECENT_SEARCH)
                .addFilterOption(SearchBarConfig.FilterOption.CATEGORY)
                .build();

        assertTrue(config.isValid());
    }

    @Test(expected = IllegalStateException.class)
    public void testValidationFailureEmptyPlaceholder() {
        new SearchBarConfig.Builder()
                .setPlaceholderText("")
                .addSearchContext(SearchBarConfig.SearchContext.TRANSACTIONS)
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void testValidationFailureNoSearchContext() {
        new SearchBarConfig.Builder()
                .setPlaceholderText("Valid placeholder")
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void testValidationFailureInvalidDebounceDelay() {
        new SearchBarConfig.Builder()
                .setPlaceholderText("Valid placeholder")
                .addSearchContext(SearchBarConfig.SearchContext.TRANSACTIONS)
                .setDebounceDelayMs(-1)
                .build();
    }

    @Test
    public void testForTransactionsFactory() {
        SearchBarConfig config = SearchBarConfig.forTransactions();

        assertEquals("İşlem ara...", config.getPlaceholderText());
        assertTrue(config.getEnabledSearchContexts().contains(SearchBarConfig.SearchContext.TRANSACTIONS));
        assertTrue(config.getEnabledSuggestionTypes().contains(SearchSuggestionType.RECENT_SEARCH));
        assertTrue(config.getEnabledSuggestionTypes().contains(SearchSuggestionType.TRANSACTION_DESCRIPTION));
        assertTrue(config.getAvailableFilterOptions().contains(SearchBarConfig.FilterOption.TRANSACTION_TYPE));
        assertTrue(config.getAvailableFilterOptions().contains(SearchBarConfig.FilterOption.CATEGORY));
        assertTrue(config.isFiltersEnabled());
        assertTrue(config.isSuggestionsEnabled());
        assertTrue(config.isValid());
    }

    @Test
    public void testForCategoriesFactory() {
        SearchBarConfig config = SearchBarConfig.forCategories();

        assertEquals("Kategori ara...", config.getPlaceholderText());
        assertTrue(config.getEnabledSearchContexts().contains(SearchBarConfig.SearchContext.CATEGORIES));
        assertTrue(config.getEnabledSuggestionTypes().contains(SearchSuggestionType.CATEGORY_NAME));
        assertTrue(config.getAvailableFilterOptions().contains(SearchBarConfig.FilterOption.CATEGORY));
        assertTrue(config.isValid());
    }

    @Test
    public void testForReportsFactory() {
        SearchBarConfig config = SearchBarConfig.forReports();

        assertEquals("Rapor ara...", config.getPlaceholderText());
        assertTrue(config.getEnabledSearchContexts().contains(SearchBarConfig.SearchContext.REPORTS));
        assertTrue(config.getEnabledSuggestionTypes().contains(SearchSuggestionType.DATE_SUGGESTION));
        assertTrue(config.getAvailableFilterOptions().contains(SearchBarConfig.FilterOption.DATE_RANGE));
        assertTrue(config.isValid());
    }

    @Test
    public void testMinimalFactory() {
        SearchBarConfig config = SearchBarConfig.minimal();

        assertEquals("Ara...", config.getPlaceholderText());
        assertTrue(config.getEnabledSearchContexts().contains(SearchBarConfig.SearchContext.ALL));
        assertFalse(config.isFiltersEnabled());
        assertFalse(config.isSuggestionsEnabled());
        assertFalse(config.isHistoryEnabled());
        assertFalse(config.isExpandable());
        assertTrue(config.isValid());
    }

    @Test
    public void testWithPlaceholderFactory() {
        SearchBarConfig config = SearchBarConfig.withPlaceholder("Custom placeholder", SearchBarConfig.SearchContext.TRANSACTIONS);

        assertEquals("Custom placeholder", config.getPlaceholderText());
        assertTrue(config.getEnabledSearchContexts().contains(SearchBarConfig.SearchContext.TRANSACTIONS));
        assertTrue(config.isValid());
    }

    @Test
    public void testToBuilder() {
        SearchBarConfig original = SearchBarConfig.forTransactions();
        SearchBarConfig modified = original.toBuilder()
                .setPlaceholderText("Modified placeholder")
                .setMaxSuggestions(15)
                .build();

        assertEquals("Modified placeholder", modified.getPlaceholderText());
        assertEquals(15, modified.getMaxSuggestions());
        // Other properties should remain the same
        assertEquals(original.getEnabledSearchContexts(), modified.getEnabledSearchContexts());
        assertEquals(original.getEnabledSuggestionTypes(), modified.getEnabledSuggestionTypes());
    }

    @Test
    public void testEqualsAndHashCode() {
        SearchBarConfig config1 = SearchBarConfig.forTransactions();
        SearchBarConfig config2 = SearchBarConfig.forTransactions();
        SearchBarConfig config3 = SearchBarConfig.forCategories();

        assertEquals(config1, config2);
        assertEquals(config1.hashCode(), config2.hashCode());
        assertNotEquals(config1, config3);
        assertNotEquals(config1.hashCode(), config3.hashCode());
    }

    @Test
    public void testToString() {
        SearchBarConfig config = SearchBarConfig.forTransactions();
        String toString = config.toString();

        assertTrue(toString.contains("SearchBarConfig"));
        assertTrue(toString.contains("İşlem ara..."));
        assertTrue(toString.contains("TRANSACTIONS"));
    }
}