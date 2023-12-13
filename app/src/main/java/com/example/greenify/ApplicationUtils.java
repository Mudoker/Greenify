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
    private static final int NOTIFICATIONS_PERMISSION_REQUEST_CODE = 101;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 102;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 103;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 104;

    // Get permissions from the user
    public void requestPermissions(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check if permission to post notifications is granted
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATIONS_PERMISSION_REQUEST_CODE);
            }

            // Check if permission to access local storage is granted
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
            }

            // Check if permission to access the camera is granted
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            }

            // Check if location permission is granted
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
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

    // Convert pixels to dp
    public static int pxToDp(Context context, int px) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(px / density);
    }

    // Convert dp to pixels
    public static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density + 0.5f);
    }

    // Convert pixels to sp
    public static int pxToSp(Context context, int px) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return Math.round(px / scaledDensity);
    }

    // Convert sp to pixels
    public static int spToPx(Context context, int sp) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return Math.round(sp * scaledDensity);
    }
}
