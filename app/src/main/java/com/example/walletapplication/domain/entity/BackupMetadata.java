package com.example.walletapplication.domain.entity;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Metadata information for backup data
 * Contains information about the backup creation and statistics
 */
public class BackupMetadata {
    private final String appVersion;
    private final String deviceModel;
    private final String deviceId;
    private final LocalDateTime exportedAt;
    private final int transactionCount;
    private final int categoryCount;
    private final String checksum;

    private BackupMetadata(String appVersion, String deviceModel, String deviceId, 
                          LocalDateTime exportedAt, int transactionCount, int categoryCount, 
                          String checksum) {
        this.appVersion = appVersion;
        this.deviceModel = deviceModel;
        this.deviceId = deviceId;
        this.exportedAt = exportedAt;
        this.transactionCount = transactionCount;
        this.categoryCount = categoryCount;
        this.checksum = checksum;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public LocalDateTime getExportedAt() {
        return exportedAt;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public int getCategoryCount() {
        return categoryCount;
    }

    public String getChecksum() {
        return checksum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BackupMetadata that = (BackupMetadata) o;
        return transactionCount == that.transactionCount &&
               categoryCount == that.categoryCount &&
               Objects.equals(appVersion, that.appVersion) &&
               Objects.equals(deviceModel, that.deviceModel) &&
               Objects.equals(deviceId, that.deviceId) &&
               Objects.equals(exportedAt, that.exportedAt) &&
               Objects.equals(checksum, that.checksum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appVersion, deviceModel, deviceId, exportedAt, transactionCount, categoryCount, checksum);
    }

    @Override
    public String toString() {
        return "BackupMetadata{" +
                "appVersion='" + appVersion + '\'' +
                ", deviceModel='" + deviceModel + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", exportedAt=" + exportedAt +
                ", transactionCount=" + transactionCount +
                ", categoryCount=" + categoryCount +
                ", checksum='" + checksum + '\'' +
                '}';
    }

    public static class Builder {
        private String appVersion;
        private String deviceModel;
        private String deviceId;
        private LocalDateTime exportedAt;
        private int transactionCount;
        private int categoryCount;
        private String checksum;

        public Builder setAppVersion(String appVersion) {
            this.appVersion = appVersion;
            return this;
        }

        public Builder setDeviceModel(String deviceModel) {
            this.deviceModel = deviceModel;
            return this;
        }

        public Builder setDeviceId(String deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        public Builder setExportedAt(LocalDateTime exportedAt) {
            this.exportedAt = exportedAt;
            return this;
        }

        public Builder setTransactionCount(int transactionCount) {
            this.transactionCount = transactionCount;
            return this;
        }

        public Builder setCategoryCount(int categoryCount) {
            this.categoryCount = categoryCount;
            return this;
        }

        public Builder setChecksum(String checksum) {
            this.checksum = checksum;
            return this;
        }

        public BackupMetadata build() {
            if (exportedAt == null) {
                exportedAt = LocalDateTime.now();
            }
            return new BackupMetadata(appVersion, deviceModel, deviceId, exportedAt, 
                                    transactionCount, categoryCount, checksum);
        }
    }
} 