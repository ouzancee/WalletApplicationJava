package com.example.walletapplication.domain.common;

import java.util.Objects;

/**
 * Sealed class representing different types of application errors
 */
public abstract class AppError {
    
    private final String message;
    private final String userMessage;
    private final Throwable cause;
    
    protected AppError(String message, String userMessage, Throwable cause) {
        this.message = message;
        this.userMessage = userMessage;
        this.cause = cause;
    }
    
    public String getMessage() {
        return message;
    }
    
    public String getUserMessage() {
        return userMessage != null ? userMessage : message;
    }
    
    public Throwable getCause() {
        return cause;
    }
    
    // Database Errors
    public static final class DatabaseError extends AppError {
        public DatabaseError(String message, Throwable cause) {
            super(message, "Veritabanı hatası oluştu", cause);
        }
        
        public DatabaseError(String message, String userMessage, Throwable cause) {
            super(message, userMessage, cause);
        }
    }
    
    // Network Errors
    public static final class NetworkError extends AppError {
        public NetworkError(String message, Throwable cause) {
            super(message, "Ağ bağlantısı hatası", cause);
        }
        
        public NetworkError(String message, String userMessage, Throwable cause) {
            super(message, userMessage, cause);
        }
    }
    
    // Validation Errors
    public static final class ValidationError extends AppError {
        private final String fieldName;
        
        public ValidationError(String fieldName, String message) {
            super(message, message, null);
            this.fieldName = fieldName;
        }
        
        public ValidationError(String fieldName, String message, String userMessage) {
            super(message, userMessage, null);
            this.fieldName = fieldName;
        }
        
        public String getFieldName() {
            return fieldName;
        }
    }
    
    // Business Logic Errors
    public static final class BusinessError extends AppError {
        private final String errorCode;
        
        public BusinessError(String errorCode, String message) {
            super(message, message, null);
            this.errorCode = errorCode;
        }
        
        public BusinessError(String errorCode, String message, String userMessage) {
            super(message, userMessage, null);
            this.errorCode = errorCode;
        }
        
        public String getErrorCode() {
            return errorCode;
        }
    }
    
    // Unknown/Unexpected Errors
    public static final class UnknownError extends AppError {
        public UnknownError(String message, Throwable cause) {
            super(message, "Beklenmeyen bir hata oluştu", cause);
        }
        
        public UnknownError(Throwable cause) {
            super(cause.getMessage(), "Beklenmeyen bir hata oluştu", cause);
        }
    }
    
    // Timeout Errors
    public static final class TimeoutError extends AppError {
        public TimeoutError(String message) {
            super(message, "İşlem zaman aşımına uğradı", null);
        }
        
        public TimeoutError(String message, Throwable cause) {
            super(message, "İşlem zaman aşımına uğradı", cause);
        }
    }
    
    // Permission Errors
    public static final class PermissionError extends AppError {
        public PermissionError(String message) {
            super(message, "Bu işlem için gerekli izinler bulunmuyor", null);
        }
        
        public PermissionError(String message, String userMessage) {
            super(message, userMessage, null);
        }
    }
    
    // Not Found Errors
    public static final class NotFoundError extends AppError {
        private final String resourceType;
        private final String resourceId;
        
        public NotFoundError(String resourceType, String resourceId) {
            super(resourceType + " bulunamadı: " + resourceId, resourceType + " bulunamadı", null);
            this.resourceType = resourceType;
            this.resourceId = resourceId;
        }
        
        public String getResourceType() {
            return resourceType;
        }
        
        public String getResourceId() {
            return resourceId;
        }
    }
    
    // Factory methods
    public static AppError database(String message, Throwable cause) {
        return new DatabaseError(message, cause);
    }
    
    public static AppError network(String message, Throwable cause) {
        return new NetworkError(message, cause);
    }
    
    public static AppError validation(String fieldName, String message) {
        return new ValidationError(fieldName, message);
    }
    
    public static AppError business(String errorCode, String message) {
        return new BusinessError(errorCode, message);
    }
    
    public static AppError unknown(Throwable cause) {
        return new UnknownError(cause);
    }
    
    public static AppError timeout(String message) {
        return new TimeoutError(message);
    }
    
    public static AppError permission(String message) {
        return new PermissionError(message);
    }
    
    public static AppError notFound(String resourceType, String resourceId) {
        return new NotFoundError(resourceType, resourceId);
    }
    
    /**
     * Creates an AppError from a generic exception
     */
    public static AppError fromException(Exception exception) {
        if (exception instanceof IllegalArgumentException) {
            return validation("", exception.getMessage());
        } else if (exception instanceof IllegalStateException) {
            return business("ILLEGAL_STATE", exception.getMessage());
        } else if (exception instanceof java.util.concurrent.TimeoutException) {
            return timeout(exception.getMessage());
        } else if (exception instanceof java.sql.SQLException) {
            return database(exception.getMessage(), exception);
        } else if (exception instanceof java.net.SocketTimeoutException || 
                   exception instanceof java.net.ConnectException) {
            return network(exception.getMessage(), exception);
        } else {
            return unknown(exception);
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AppError appError = (AppError) obj;
        return Objects.equals(message, appError.message) &&
               Objects.equals(userMessage, appError.userMessage);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(message, userMessage);
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
               "message='" + message + '\'' +
               ", userMessage='" + userMessage + '\'' +
               '}';
    }
} 