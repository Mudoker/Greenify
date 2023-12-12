package com.example.greenify;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.TextView;

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
            insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_DEFAULT);
        }
//        Button btnLogin = findViewById(R.id.auth_btn_login);
        TextView btnNeedHelp = findViewById(R.id.auth_btn_need_help);

        btnNeedHelp.setOnClickListener((v) -> ApplicationUtils.showDialog(this, "Technical Support", "Please contact " + Environment.getSupportEmail() + " for more information."));
//
//        btnLogin.setOnClickListener((v) -> notificationHelper.sendNotification(this, R.mipmap.app_logo, "101", "Hello World", "Today is good", R.color.dark_blue, AuthenticationActivity.this));
    }


}