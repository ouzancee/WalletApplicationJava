package com.example.walletapplication.domain.common;

import java.util.function.Function;

/**
 * A generic wrapper for operation results that can either be successful or contain an error.
 * This follows the Result pattern for better error handling.
 */
public abstract class Result<T> {
    
    private Result() {} // Prevent external instantiation
    
    /**
     * Represents a successful result containing data
     */
    public static final class Success<T> extends Result<T> {
        private final T data;
        
        public Success(T data) {
            this.data = data;
        }
        
        public T getData() {
            return data;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Success<?> success = (Success<?>) obj;
            return data != null ? data.equals(success.data) : success.data == null;
        }
        
        @Override
        public int hashCode() {
            return data != null ? data.hashCode() : 0;
        }
        
        @Override
        public String toString() {
            return "Success{data=" + data + "}";
        }
    }
    
    /**
     * Represents a failed result containing an error
     */
    public static final class Error<T> extends Result<T> {
        private final AppError error;
        
        public Error(AppError error) {
            this.error = error;
        }
        
        public AppError getError() {
            return error;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Error<?> error1 = (Error<?>) obj;
            return error != null ? error.equals(error1.error) : error1.error == null;
        }
        
        @Override
        public int hashCode() {
            return error != null ? error.hashCode() : 0;
        }
        
        @Override
        public String toString() {
            return "Error{error=" + error + "}";
        }
    }
    
    // Utility methods
    
    /**
     * Creates a successful result
     */
    public static <T> Result<T> success(T data) {
        return new Success<>(data);
    }
    
    /**
     * Creates an error result
     */
    public static <T> Result<T> error(AppError error) {
        return new Error<>(error);
    }
    
    /**
     * Creates an error result from exception
     */
    public static <T> Result<T> error(Exception exception) {
        return new Error<>(AppError.fromException(exception));
    }
    
    /**
     * Checks if the result is successful
     */
    public boolean isSuccess() {
        return this instanceof Success;
    }
    
    /**
     * Checks if the result is an error
     */
    public boolean isError() {
        return this instanceof Error;
    }
    
    /**
     * Gets the data if successful, null otherwise
     */
    public T getDataOrNull() {
        if (this instanceof Success) {
            return ((Success<T>) this).getData();
        }
        return null;
    }
    
    /**
     * Gets the error if failed, null otherwise
     */
    public AppError getErrorOrNull() {
        if (this instanceof Error) {
            return ((Error<T>) this).getError();
        }
        return null;
    }
    
    /**
     * Maps the data if successful
     */
    public <R> Result<R> map(Function<T, R> mapper) {
        if (this instanceof Success) {
            try {
                T data = ((Success<T>) this).getData();
                R mappedData = mapper.apply(data);
                return success(mappedData);
            } catch (Exception e) {
                return error(e);
            }
        } else {
            return error(((Error<T>) this).getError());
        }
    }
    
    /**
     * Flat maps the result
     */
    public <R> Result<R> flatMap(Function<T, Result<R>> mapper) {
        if (this instanceof Success) {
            try {
                T data = ((Success<T>) this).getData();
                return mapper.apply(data);
            } catch (Exception e) {
                return error(e);
            }
        } else {
            return error(((Error<T>) this).getError());
        }
    }
    
    /**
     * Executes action if successful
     */
    public Result<T> onSuccess(java.util.function.Consumer<T> action) {
        if (this instanceof Success) {
            T data = ((Success<T>) this).getData();
            action.accept(data);
        }
        return this;
    }
    
    /**
     * Executes action if error
     */
    public Result<T> onError(java.util.function.Consumer<AppError> action) {
        if (this instanceof Error) {
            AppError error = ((Error<T>) this).getError();
            action.accept(error);
        }
        return this;
    }
} 