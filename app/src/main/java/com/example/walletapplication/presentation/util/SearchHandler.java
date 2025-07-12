package com.example.walletapplication.presentation.util;

import android.os.Handler;
import android.os.Looper;

/**
 * Utility class for handling search with debouncing mechanism
 * Prevents excessive API calls by delaying search execution
 */
public class SearchHandler {
    
    private final Handler handler;
    private final long delayMs;
    private Runnable searchRunnable;
    
    public interface SearchCallback {
        void onSearch(String query);
    }
    
    /**
     * Constructor with default delay of 300ms
     */
    public SearchHandler() {
        this(300);
    }
    
    /**
     * Constructor with custom delay
     * @param delayMs Delay in milliseconds before executing search
     */
    public SearchHandler(long delayMs) {
        this.handler = new Handler(Looper.getMainLooper());
        this.delayMs = delayMs;
    }
    
    /**
     * Schedules a search with debouncing
     * @param query Search query
     * @param callback Callback to execute when search is triggered
     */
    public void search(String query, SearchCallback callback) {
        // Cancel previous search if exists
        if (searchRunnable != null) {
            handler.removeCallbacks(searchRunnable);
        }
        
        // Create new search runnable
        searchRunnable = () -> {
            if (callback != null) {
                callback.onSearch(query);
            }
        };
        
        // Schedule search with delay
        handler.postDelayed(searchRunnable, delayMs);
    }
    
    /**
     * Cancels any pending search
     */
    public void cancelSearch() {
        if (searchRunnable != null) {
            handler.removeCallbacks(searchRunnable);
            searchRunnable = null;
        }
    }
    
    /**
     * Executes search immediately without delay
     * @param query Search query
     * @param callback Callback to execute
     */
    public void searchImmediate(String query, SearchCallback callback) {
        cancelSearch();
        if (callback != null) {
            callback.onSearch(query);
        }
    }
} 