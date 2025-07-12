package com.example.walletapplication.presentation.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.walletapplication.MainActivity;
import com.example.walletapplication.R;

/**
 * Manages all backup-related notifications including progress tracking
 */
public class BackupNotificationManager {
    
    // Notification Channel IDs
    private static final String CHANNEL_BACKUP_PROGRESS = "backup_progress_channel";
    private static final String CHANNEL_BACKUP_COMPLETE = "backup_complete_channel";
    private static final String CHANNEL_BACKUP_ERROR = "backup_error_channel";
    
    // Notification IDs
    public static final int NOTIFICATION_ID_EXPORT_PROGRESS = 1001;
    public static final int NOTIFICATION_ID_IMPORT_PROGRESS = 1002;
    public static final int NOTIFICATION_ID_EXPORT_COMPLETE = 1003;
    public static final int NOTIFICATION_ID_IMPORT_COMPLETE = 1004;
    public static final int NOTIFICATION_ID_ERROR = 1005;
    
    // Actions
    public static final String ACTION_CANCEL_BACKUP = "com.example.walletapplication.CANCEL_BACKUP";
    
    private final Context context;
    private final NotificationManagerCompat notificationManager;
    
    public BackupNotificationManager(Context context) {
        this.context = context;
        this.notificationManager = NotificationManagerCompat.from(context);
        createNotificationChannels();
    }
    
    /**
     * Creates notification channels for Android 8.0+ (API 26+)
     */
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Progress Channel
            NotificationChannel progressChannel = new NotificationChannel(
                    CHANNEL_BACKUP_PROGRESS,
                    "Yedekleme İşlemleri",
                    NotificationManager.IMPORTANCE_LOW
            );
            progressChannel.setDescription("Yedekleme ve geri yükleme işlemlerinin ilerlemesi");
            progressChannel.setShowBadge(false);
            progressChannel.enableVibration(false);
            progressChannel.setSound(null, null);
            
            // Completion Channel
            NotificationChannel completeChannel = new NotificationChannel(
                    CHANNEL_BACKUP_COMPLETE,
                    "Yedekleme Tamamlandı",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            completeChannel.setDescription("Yedekleme işlemleri tamamlandığında bildirim");
            completeChannel.setShowBadge(true);
            
            // Error Channel
            NotificationChannel errorChannel = new NotificationChannel(
                    CHANNEL_BACKUP_ERROR,
                    "Yedekleme Hataları",
                    NotificationManager.IMPORTANCE_HIGH
            );
            errorChannel.setDescription("Yedekleme işlemlerinde oluşan hatalar");
            errorChannel.setShowBadge(true);
            errorChannel.enableVibration(true);
            
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(progressChannel);
                manager.createNotificationChannel(completeChannel);
                manager.createNotificationChannel(errorChannel);
            }
        }
    }
    
    /**
     * Creates a foreground notification for export progress
     */
    public Notification createExportProgressNotification(int progress, int max, String status) {
        Intent cancelIntent = new Intent(ACTION_CANCEL_BACKUP);
        cancelIntent.putExtra("operation", "export");
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(
                context, 0, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent mainPendingIntent = PendingIntent.getActivity(
                context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        return new NotificationCompat.Builder(context, CHANNEL_BACKUP_PROGRESS)
                .setContentTitle("Veriler dışa aktarılıyor")
                .setContentText(status)
                .setSmallIcon(R.drawable.ic_money)
                .setProgress(max, progress, false)
                .setOngoing(true)
                .setContentIntent(mainPendingIntent)
                .addAction(R.drawable.ic_arrow_left, "İptal", cancelPendingIntent)
                .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }
    
    /**
     * Creates a foreground notification for import progress
     */
    public Notification createImportProgressNotification(int progress, int max, String status) {
        Intent cancelIntent = new Intent(ACTION_CANCEL_BACKUP);
        cancelIntent.putExtra("operation", "import");
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(
                context, 0, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent mainPendingIntent = PendingIntent.getActivity(
                context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        return new NotificationCompat.Builder(context, CHANNEL_BACKUP_PROGRESS)
                .setContentTitle("Veriler içe aktarılıyor")
                .setContentText(status)
                .setSmallIcon(R.drawable.ic_add)
                .setProgress(max, progress, false)
                .setOngoing(true)
                .setContentIntent(mainPendingIntent)
                .addAction(R.drawable.ic_arrow_left, "İptal", cancelPendingIntent)
                .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }
    
    /**
     * Shows export success notification
     */
    public void showExportSuccessNotification(String filePath, int transactionCount, int categoryCount) {
        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent mainPendingIntent = PendingIntent.getActivity(
                context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        String contentText = String.format("%d işlem ve %d kategori başarıyla dışa aktarıldı", 
                transactionCount, categoryCount);
        
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_BACKUP_COMPLETE)
                .setContentTitle("Dışa aktarma tamamlandı")
                .setContentText(contentText)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(contentText + "\n\nDosya konumu: " + filePath))
                .setSmallIcon(R.drawable.ic_balance)
                .setContentIntent(mainPendingIntent)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_STATUS)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();
        
        notificationManager.notify(NOTIFICATION_ID_EXPORT_COMPLETE, notification);
    }
    
    /**
     * Shows import success notification
     */
    public void showImportSuccessNotification(int transactionCount, int categoryCount) {
        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent mainPendingIntent = PendingIntent.getActivity(
                context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        String contentText = String.format("%d işlem ve %d kategori başarıyla içe aktarıldı", 
                transactionCount, categoryCount);
        
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_BACKUP_COMPLETE)
                .setContentTitle("İçe aktarma tamamlandı")
                .setContentText(contentText)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
                .setSmallIcon(R.drawable.ic_balance)
                .setContentIntent(mainPendingIntent)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_STATUS)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();
        
        notificationManager.notify(NOTIFICATION_ID_IMPORT_COMPLETE, notification);
    }
    
    /**
     * Shows error notification
     */
    public void showErrorNotification(String title, String message, String operation) {
        Intent mainIntent = new Intent(context, MainActivity.class);
        PendingIntent mainPendingIntent = PendingIntent.getActivity(
                context, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_BACKUP_ERROR)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message)
                        .setSummaryText("İşlem: " + operation))
                .setSmallIcon(R.drawable.ic_empty_state)
                .setContentIntent(mainPendingIntent)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_ERROR)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();
        
        notificationManager.notify(NOTIFICATION_ID_ERROR, notification);
    }
    
    /**
     * Updates progress notification
     */
    public void updateProgressNotification(int notificationId, int progress, int max, String status) {
        Notification notification;
        
        if (notificationId == NOTIFICATION_ID_EXPORT_PROGRESS) {
            notification = createExportProgressNotification(progress, max, status);
        } else if (notificationId == NOTIFICATION_ID_IMPORT_PROGRESS) {
            notification = createImportProgressNotification(progress, max, status);
        } else {
            return;
        }
        
        notificationManager.notify(notificationId, notification);
    }
    
    /**
     * Cancels a specific notification
     */
    public void cancelNotification(int notificationId) {
        notificationManager.cancel(notificationId);
    }
    
    /**
     * Cancels all backup-related notifications
     */
    public void cancelAllNotifications() {
        notificationManager.cancel(NOTIFICATION_ID_EXPORT_PROGRESS);
        notificationManager.cancel(NOTIFICATION_ID_IMPORT_PROGRESS);
        notificationManager.cancel(NOTIFICATION_ID_EXPORT_COMPLETE);
        notificationManager.cancel(NOTIFICATION_ID_IMPORT_COMPLETE);
        notificationManager.cancel(NOTIFICATION_ID_ERROR);
    }
} 