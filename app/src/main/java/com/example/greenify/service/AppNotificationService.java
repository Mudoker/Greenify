package com.example.greenify.service;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.greenify.R;
import com.example.greenify.activity.main.MainActivity;
import com.example.greenify.util.ApplicationUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

public class AppNotificationService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        try {
            getFirebaseMessage(Objects.requireNonNull(remoteMessage.getNotification()).getTitle(), remoteMessage.getNotification().getBody());
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
        Log.d("On Message_1", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("On Message_2", "Message data payload: " + remoteMessage.getData());
        }
    }

    public void getFirebaseMessage(String title, String msg) throws IllegalAccessException, InstantiationException {
        Context appContext = getApplicationContext();
        try {
            new Handler(Looper.getMainLooper()).post(() -> {
                try {
                    ApplicationUtils.getNotificationHelper().sendNotification(
                            appContext,
                            R.drawable.greenify_app_logo,
                            "111",
                            title,
                            msg,
                            R.color.dark_blue,
                            new MainActivity()
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
