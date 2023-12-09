package com.example.greenify;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;

import androidx.core.splashscreen.SplashScreen;

public class AuthenticationActivity extends AppCompatActivity {

    private final NotificationHelper notificationHelper = NotificationHelper.getInstance();

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
            notificationHelper.sendNotification(this, R.mipmap.app_logo, "101", "Hello World", "Today is good", R.color.dark_blue, AuthenticationActivity.this);
        });
    }


}