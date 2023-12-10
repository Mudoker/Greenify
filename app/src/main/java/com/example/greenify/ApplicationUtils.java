package com.example.greenify;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

public class ApplicationUtils {
    private static NotificationHelper notificationHelper;

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
