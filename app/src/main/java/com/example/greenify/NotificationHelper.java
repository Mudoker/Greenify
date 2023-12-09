package com.example.greenify;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class NotificationHelper {
    // Singleton
    private static NotificationHelper notificationHelper;

    // Code
    private static final int PERMISSION_REQUEST_CODE = 101;

    // Get singleton
    public static NotificationHelper getInstance() {
        if (notificationHelper == null) {
            notificationHelper = new NotificationHelper();
        }
        return notificationHelper;
    }

    // Get permission from user
    public boolean requestPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    // Push notification
    public void sendNotification(Context context, int smallIcon, String channelId, String contentTitle, String contentText, int colorCode, AppCompatActivity intentDestination) {
        NotificationCompat.Builder notificationCompatBuilder = new NotificationCompat.Builder(context, channelId);

        notificationCompatBuilder.setSmallIcon(smallIcon);
        notificationCompatBuilder.setContentTitle(contentTitle);
        notificationCompatBuilder.setContentText(contentText);
        notificationCompatBuilder.setAutoCancel(true);
        notificationCompatBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);

        Intent intent = new Intent(context, intentDestination.getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent;
        pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        notificationCompatBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelId);

        if (notificationChannel == null) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            notificationChannel = new NotificationChannel(channelId, "Hello", importance);
            notificationChannel.setLightColor(colorCode);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(0, notificationCompatBuilder.build());
    }
}
