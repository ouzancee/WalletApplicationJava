package com.example.walletapplication.presentation.util;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for highlighting search terms in text
 */
public class TextHighlighter {
    
    private static final int DEFAULT_HIGHLIGHT_COLOR = Color.YELLOW;
    private static final int DEFAULT_TEXT_COLOR = Color.BLACK;
    
    /**
     * Highlights search query in text with default colors
     * @param text Original text
     * @param query Search query to highlight
     * @return SpannableString with highlighted text
     */
    public static SpannableString highlight(String text, String query) {
        return highlight(text, query, DEFAULT_HIGHLIGHT_COLOR, DEFAULT_TEXT_COLOR);
    }
    
    /**
     * Highlights search query in text with custom colors
     * @param text Original text
     * @param query Search query to highlight
     * @param highlightColor Background color for highlighted text
     * @param textColor Text color for highlighted text
     * @return SpannableString with highlighted text
     */
    public static SpannableString highlight(String text, String query, int highlightColor, int textColor) {
        if (text == null || query == null || query.trim().isEmpty()) {
            return new SpannableString(text != null ? text : "");
        }
        
        SpannableString spannableString = new SpannableString(text);
        
        // Create case-insensitive pattern
        Pattern pattern = Pattern.compile(Pattern.quote(query), Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        
        // Find all matches and apply highlighting
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            
            // Apply background color
            spannableString.setSpan(
                new BackgroundColorSpan(highlightColor),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            
            // Apply text color
            spannableString.setSpan(
                new ForegroundColorSpan(textColor),
                start,
                end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }
        
        return spannableString;
    }
    
    /**
     * Highlights multiple search terms in text
     * @param text Original text
     * @param queries Array of search queries to highlight
     * @return SpannableString with highlighted text
     */
    public static SpannableString highlightMultiple(String text, String[] queries) {
        return highlightMultiple(text, queries, DEFAULT_HIGHLIGHT_COLOR, DEFAULT_TEXT_COLOR);
    }
    
    /**
     * Highlights multiple search terms in text with custom colors
     * @param text Original text
     * @param queries Array of search queries to highlight
     * @param highlightColor Background color for highlighted text
     * @param textColor Text color for highlighted text
     * @return SpannableString with highlighted text
     */
    public static SpannableString highlightMultiple(String text, String[] queries, int highlightColor, int textColor) {
        if (text == null || queries == null || queries.length == 0) {
            return new SpannableString(text != null ? text : "");
        }
        
        SpannableString spannableString = new SpannableString(text);
        
        for (String query : queries) {
            if (query != null && !query.trim().isEmpty()) {
                // Create case-insensitive pattern
                Pattern pattern = Pattern.compile(Pattern.quote(query), Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(text);
                
                // Find all matches and apply highlighting
                while (matcher.find()) {
                    int start = matcher.start();
                    int end = matcher.end();
                    
                    // Apply background color
                    spannableString.setSpan(
                        new BackgroundColorSpan(highlightColor),
                        start,
                        end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                    
                    // Apply text color
                    spannableString.setSpan(
                        new ForegroundColorSpan(textColor),
                        start,
                        end,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    );
                }
            }
        }
        
        return spannableString;
    }
    
    /**
     * Highlights search query with subtle highlighting (light background)
     * @param text Original text
     * @param query Search query to highlight
     * @return SpannableString with subtly highlighted text
     */
    public static SpannableString highlightSubtle(String text, String query) {
        return highlight(text, query, Color.parseColor("#FFEB3B"), Color.parseColor("#333333"));
    }
    
    /**
     * Highlights search query with bold highlighting (dark background)
     * @param text Original text
     * @param query Search query to highlight
     * @return SpannableString with boldly highlighted text
     */
    public static SpannableString highlightBold(String text, String query) {
        return highlight(text, query, Color.parseColor("#FF9800"), Color.WHITE);
    }
    
    /**
     * Removes all highlighting from text
     * @param text SpannableString with highlighting
     * @return Plain string without highlighting
     */
    public static String removeHighlighting(SpannableString text) {
        return text != null ? text.toString() : "";
    }
    
    /**
     * Checks if text contains the search query (case-insensitive)
     * @param text Text to search in
     * @param query Search query
     * @return true if text contains query, false otherwise
     */
    public static boolean containsQuery(String text, String query) {
        if (text == null || query == null || query.trim().isEmpty()) {
            return false;
        }
        
        return text.toLowerCase().contains(query.toLowerCase());
    }
    
    /**
     * Gets the number of matches for a query in text
     * @param text Text to search in
     * @param query Search query
     * @return Number of matches found
     */
    public static int getMatchCount(String text, String query) {
        if (text == null || query == null || query.trim().isEmpty()) {
            return 0;
        }
        
        Pattern pattern = Pattern.compile(Pattern.quote(query), Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        
        return count;
    }
} 