package com.example.walletapplication.domain.entity;

public enum CategoryType {
    INCOME("Gelir"),
    EXPENSE("Gider"),
    BOTH("Her İkisi");

    private final String displayName;

    CategoryType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 