package com.example.walletapplication.domain.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Represents complete backup data for the wallet application
 * Contains all transactions and categories that can be exported/imported
 */
public class BackupData {
    private final String version;
    private final LocalDateTime createdAt;
    private final BackupMetadata metadata;
    private final List<BackupTransaction> transactions;
    private final List<BackupCategory> categories;

    private BackupData(String version, LocalDateTime createdAt, BackupMetadata metadata, 
                      List<BackupTransaction> transactions, List<BackupCategory> categories) {
        this.version = version;
        this.createdAt = createdAt;
        this.metadata = metadata;
        this.transactions = transactions;
        this.categories = categories;
    }

    public String getVersion() {
        return version;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public BackupMetadata getMetadata() {
        return metadata;
    }

    public List<BackupTransaction> getTransactions() {
        return transactions;
    }

    public List<BackupCategory> getCategories() {
        return categories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BackupData that = (BackupData) o;
        return Objects.equals(version, that.version) &&
               Objects.equals(createdAt, that.createdAt) &&
               Objects.equals(metadata, that.metadata) &&
               Objects.equals(transactions, that.transactions) &&
               Objects.equals(categories, that.categories);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, createdAt, metadata, transactions, categories);
    }

    @Override
    public String toString() {
        return "BackupData{" +
                "version='" + version + '\'' +
                ", createdAt=" + createdAt +
                ", metadata=" + metadata +
                ", transactions=" + (transactions != null ? transactions.size() : 0) + " items" +
                ", categories=" + (categories != null ? categories.size() : 0) + " items" +
                '}';
    }

    public static class Builder {
        private String version;
        private LocalDateTime createdAt;
        private BackupMetadata metadata;
        private List<BackupTransaction> transactions;
        private List<BackupCategory> categories;

        public Builder setVersion(String version) {
            this.version = version;
            return this;
        }

        public Builder setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder setMetadata(BackupMetadata metadata) {
            this.metadata = metadata;
            return this;
        }

        public Builder setTransactions(List<BackupTransaction> transactions) {
            this.transactions = transactions;
            return this;
        }

        public Builder setCategories(List<BackupCategory> categories) {
            this.categories = categories;
            return this;
        }

        public BackupData build() {
            if (createdAt == null) {
                createdAt = LocalDateTime.now();
            }
            return new BackupData(version, createdAt, metadata, transactions, categories);
        }
    }
} 