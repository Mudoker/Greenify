package com.example.greenify.activity.main;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.greenify.R;
import com.example.greenify.fragment.AboutUsFragment;
import com.example.greenify.fragment.DashboardFragment;
import com.example.greenify.fragment.NotificationFragment;
import com.example.greenify.fragment.ProfileFragment;
import com.example.greenify.fragment.SettingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private int selectedItemId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        // Hide system bars after setting content view
        Window window = getWindow();
        WindowInsetsController insetsController = window.getInsetsController();
        if (insetsController != null) {
            insetsController.hide(WindowInsets.Type.systemBars());
            insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_DEFAULT);
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (selectedItemId == itemId) {
                return false;
            }

            selectedItemId = itemId;

            if (selectedItemId == R.id.bottom_nav_dashboard) {
                transactFragment(new DashboardFragment());
            } else if (selectedItemId == R.id.bottom_nav_about_us) {
                transactFragment(new AboutUsFragment());
            } else if (selectedItemId == R.id.bottom_nav_account) {
                transactFragment(new ProfileFragment());
            } else if (selectedItemId == R.id.bottom_nav_setting) {
                transactFragment(new SettingFragment());
            } else if (selectedItemId == R.id.bottom_nav_noti) {
                transactFragment(new NotificationFragment());
            } else {
                return false;
            }

            return true;
        });

        transactFragment(new DashboardFragment());
        selectedItemId = R.id.bottom_nav_dashboard;
    }

    public void transactFragment(Fragment fragment) {
        try {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.main_frame_layout, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}