package com.example.greenify.activity.auth;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.greenify.R;
import com.example.greenify.util.ApplicationUtils;

public class AuthenticationActivity extends AppCompatActivity {

//    private final NotificationHelper notificationHelper = ApplicationUtils.getNotificationHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_authentication);
        // Hide system bars after setting content view
        Window window = getWindow();
        WindowInsetsController insetsController = window.getInsetsController();
        if (insetsController != null) {
            insetsController.hide(WindowInsets.Type.systemBars());
            insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
//        Button btnLogin = findViewById(R.id.auth_btn_login);
        TextView btnNeedHelp = findViewById(R.id.auth_btn_need_help);

        btnNeedHelp.setOnClickListener((v) -> ApplicationUtils.sendEmail(this));
//
//        btnLogin.setOnClickListener((v) -> notificationHelper.sendNotification(this, R.mipmap.app_logo, "101", "Hello World", "Today is good", R.color.dark_blue, AuthenticationActivity.this));
    }


}