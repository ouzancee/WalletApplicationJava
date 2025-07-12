package com.example.walletapplication.domain.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Category implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final Long id;
    private final String name;
    private final String displayName;
    private final CategoryType type;
    private final String iconName;
    private final String color;
    private final boolean isDefault;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public Category(Long id, String name, String displayName, CategoryType type, 
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

    public CategoryType getType() {
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
        Category category = (Category) o;
        return isDefault == category.isDefault &&
               Objects.equals(id, category.id) &&
               Objects.equals(name, category.name) &&
               Objects.equals(displayName, category.displayName) &&
               type == category.type &&
               Objects.equals(iconName, category.iconName) &&
               Objects.equals(color, category.color) &&
               Objects.equals(createdAt, category.createdAt) &&
               Objects.equals(updatedAt, category.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, displayName, type, iconName, color, isDefault, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", type=" + type +
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
        private CategoryType type;
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

        public Builder setType(CategoryType type) {
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

        public Category build() {
            if (createdAt == null) {
                createdAt = LocalDateTime.now();
            }
            if (updatedAt == null) {
                updatedAt = LocalDateTime.now();
            }
            return new Category(id, name, displayName, type, iconName, color, isDefault, createdAt, updatedAt);
        }
    }
} 