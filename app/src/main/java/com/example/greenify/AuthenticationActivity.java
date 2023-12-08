package com.example.greenify;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.splashscreen.SplashScreen;

public class AuthenticationActivity extends AppCompatActivity {

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SplashScreen.installSplashScreen(this);

        Window window = getWindow();
        WindowInsetsController insetsController = window.getInsetsController();
        if (insetsController != null) {
            // Hide system bars
            insetsController.hide(WindowInsets.Type.systemBars());
            // Set system bars behavior to default
            insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_DEFAULT);
        }

        setContentView(R.layout.activity_authentication);

        Button btnLogin = findViewById(R.id.auth_btn_login);

        btnLogin.setOnClickListener((v) -> {
            sendNotification();
        });
    }


    public boolean sendNotification() {
        Bitmap appLargeIconBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.greenify_app_logo);
        NotificationCompat.Builder notificationCompatBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);

        notificationCompatBuilder.setSmallIcon(R.drawable.greenify_app_logo);
        notificationCompatBuilder.setLargeIcon(appLargeIconBitmap);
        notificationCompatBuilder.setContentTitle("Hello");
        notificationCompatBuilder.setContentText("Cuccooooooo");
        notificationCompatBuilder.setAutoCancel(true);
        notificationCompatBuilder.setPriority(0);

        Intent intent = new Intent(getApplicationContext(), AuthenticationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_MUTABLE);

        notificationCompatBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        NotificationChannel notificationChannel = notificationManager.getNotificationChannel(CHANNEL_ID);

        if (notificationChannel == null) {
            int important = NotificationManager.IMPORTANCE_HIGH;
            notificationChannel = new NotificationChannel(CHANNEL_ID, "Hello", important);
            notificationChannel.setLightColor(getColor(R.color.dark_blue));
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(AuthenticationActivity.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(AuthenticationActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        notificationManager.notify(0, notificationCompatBuilder.build());
        return true;
    }
}