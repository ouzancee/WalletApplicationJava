package com.example.walletapplication.domain.entity;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Backup representation of a category
 * Flattened structure for easy JSON serialization
 */
public class BackupCategory {
    private final Long id;
    private final String name;
    private final String displayName;
    private final String type; // INCOME, EXPENSE, or BOTH as string
    private final String iconName;
    private final String color;
    private final boolean isDefault;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private BackupCategory(Long id, String name, String displayName, String type, 
                          String iconName, String color, boolean isDefault, 
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.type = type;
        this.iconName = iconName;
        this.color = color;
        this.isDefault = isDefault;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getType() {
        return type;
    }

    public String getIconName() {
        return iconName;
    }

    public String getColor() {
        return color;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BackupCategory that = (BackupCategory) o;
        return isDefault == that.isDefault &&
               Objects.equals(id, that.id) &&
               Objects.equals(name, that.name) &&
               Objects.equals(displayName, that.displayName) &&
               Objects.equals(type, that.type) &&
               Objects.equals(iconName, that.iconName) &&
               Objects.equals(color, that.color) &&
               Objects.equals(createdAt, that.createdAt) &&
               Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, displayName, type, iconName, color, isDefault, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "BackupCategory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", type='" + type + '\'' +
                ", iconName='" + iconName + '\'' +
                ", color='" + color + '\'' +
                ", isDefault=" + isDefault +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    public static class Builder {
        private Long id;
        private String name;
        private String displayName;
        private String type;
        private String iconName;
        private String color;
        private boolean isDefault;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder setType(String type) {
            this.type = type;
            return this;
        }

        public Builder setIconName(String iconName) {
            this.iconName = iconName;
            return this;
        }

        public Builder setColor(String color) {
            this.color = color;
            return this;
        }

        public Builder setIsDefault(boolean isDefault) {
            this.isDefault = isDefault;
            return this;
        }

        public Builder setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public BackupCategory build() {
            return new BackupCategory(id, name, displayName, type, iconName, color, 
                                    isDefault, createdAt, updatedAt);
        }
    }
} 