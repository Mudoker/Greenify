package com.example.greenify;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class ApplicationUtils {
    private static NotificationHelper notificationHelper;
    private static final int PERMISSION_REQUEST_CODE = 101;

    // Get permission from user
    public void requestPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    // Get notification singleton
    public static NotificationHelper getNotificationHelper() {
        if (notificationHelper == null) {
            notificationHelper = new NotificationHelper();
        }
        return notificationHelper;
    }

    public static void showDialog(Context context, String alertTitle, String alertMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(alertTitle);

        SpannableString spannableMessage = new SpannableString(alertMessage);

        int startIndex = alertMessage.indexOf(Environment.getSupportEmail());

        if (startIndex != -1) {
            int endIndex = startIndex + Environment.getSupportEmail().length();

            spannableMessage.setSpan(new ForegroundColorSpan(Color.BLUE), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }


        builder.setMessage(spannableMessage);

        // Set positive
        builder.setPositiveButton("OK", (dialog, which) -> {
            // Dismiss the dialog
            dialog.dismiss();
        });

        // Create and show AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
