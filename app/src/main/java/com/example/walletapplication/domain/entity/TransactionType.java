package com.example.walletapplication.domain.entity;

public enum TransactionType {
    INCOME("Gelir"),
    EXPENSE("Gider");

    private final String displayName;

    TransactionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
} 