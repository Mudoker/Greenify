package com.example.greenify.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class NotificationHelper {
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
