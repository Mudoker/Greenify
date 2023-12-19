package com.example.greenify.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApplicationUtils {
    // Push notification
    private static NotificationHelper notificationHelper;

    // User session
    private static SessionManager sessionManager;

    private static final Executor executor = Executors.newSingleThreadExecutor();

    //Constants
    private static final int NOTIFICATIONS_PERMISSION_REQUEST_CODE = 101;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 102;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 103;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 104;

    // Get permissions from the user
    public static void requestPermissions(Context context) {
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

    public static void sendEmail(Context context) {
        String emailAddress = Environment.getSupportEmail();

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.putExtra(Intent.EXTRA_EMAIL, emailAddress);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Technical Support Request");
        intent.putExtra(Intent.EXTRA_TEXT, "I need support. Please help me!!!");

        intent.setType("message/rfc822");

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(Intent.createChooser(intent, "Choose an app"));
        } else {
            ApplicationUtils.showDialog(context, "Technical Support", "Please contact " + Environment.getSupportEmail() + " for more information.");
        }
    }

    public static boolean validateEmail(String email) {
        Pattern pattern = Pattern.compile(Environment.getEmailRegex());
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static void configureWindowInsets(Activity activity) {
        Window window = activity.getWindow();
        WindowInsetsController insetsController = window.getInsetsController();
        if (insetsController != null) {
            insetsController.hide(WindowInsets.Type.systemBars());
            insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
    }

    public static void sendNotificationMessage(String title, String body) {
        FutureTask<Void> futureTask = new FutureTask<>(() -> {
            sendNotificationInBackground(title, body);
            return null;
        });

        executor.execute(futureTask);

        try {
            // Block until the task is complete
            futureTask.get();
        } catch (Exception e) {
            // Handle exceptions here
            Log.e("Error Noti Send", String.valueOf(e));
            e.printStackTrace();
        }
    }

    private static void sendNotificationInBackground(String title, String body) {
        String receiverToken = "dJlyFqfKTcuKJtrsSL-622:APA91bHJpD4YwWHYIVdfhx8HB0ODCtN-iGgWUymoHTJ8t9xLTcRElOw0LTGIp4JOypgWQcrS2nYPV4yW5lEtSt2k-MbWfW6XEIquUpK1fQ7ShU4Evg7c4okr8j9-rX-xnVY97_QaONCE";
        String serverToken = "AAAA9MJN6pA:APA91bHI4l-Empmev0WUbAIjYtjn_uNT7ufcogdHVNOzIggKYwxN9opnX4iY2ADm-EWAXGrA4UJmTM2KMOcuKeWIvWQbEWrOCpMqVDHJ47WYus62uCPobv7C38IABtmZIV0pAHkB0ZYD";

        try {
            OkHttpClient okHttpClient = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            JSONObject jsonObject = new JSONObject();
            JSONObject jsonObjectNotification = new JSONObject();

            jsonObjectNotification.put("title", title);
            jsonObjectNotification.put("body", body);
            jsonObject.put("to", receiverToken);
            jsonObject.put("notification", jsonObjectNotification);

            RequestBody requestBody = RequestBody.create(mediaType, jsonObject.toString());
            Request request = new Request.Builder().url("https://fcm.googleapis.com/fcm/send")
                    .post(requestBody)
                    .addHeader("Authorization", "key=" + serverToken)
                    .addHeader("Content-Type", "application/json")
                    .build();

            Response response = okHttpClient.newCall(request).execute();

            // Check if the response is successful (status code 200)
            if (!response.isSuccessful()) {
                Log.e("Error Noti Code", String.valueOf(response));
                throw new IOException("Unexpected response code: " + response);
            }
        } catch (IOException e) {
            // Handle IOException
            Log.e("Error Noti IOException", String.valueOf(e));
            e.printStackTrace();
        } catch (JSONException e) {
            // Handle JSONException
            Log.e("Error Noti JSONException", String.valueOf(e));
            e.printStackTrace();
        } catch (Exception e) {
            // Handle other exceptions
            Log.e("Error Noti Exception", String.valueOf(e));
            e.printStackTrace();
        }
    }
}
