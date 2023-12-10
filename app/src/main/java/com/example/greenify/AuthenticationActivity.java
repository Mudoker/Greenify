package com.example.greenify;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class AuthenticationActivity extends AppCompatActivity {

    private final NotificationHelper notificationHelper = ApplicationUtils.getNotificationHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about_us);

//        Button btnLogin = findViewById(R.id.auth_btn_login);
//        TextView btnNeedHelp = findViewById(R.id.auth_btn_need_help);
//
//        btnNeedHelp.setOnClickListener((v) -> ApplicationUtils.showDialog(this, "Technical Support", "Please contact " + Environment.getSupportEmail() + " for more information."));
//
//        btnLogin.setOnClickListener((v) -> notificationHelper.sendNotification(this, R.mipmap.app_logo, "101", "Hello World", "Today is good", R.color.dark_blue, AuthenticationActivity.this));
    }


}