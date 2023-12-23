package com.example.greenify.activity.walkthru;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.text.HtmlCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.greenify.R;
import com.example.greenify.activity.adapter.WalkThruPagerAdapter;
import com.example.greenify.activity.main.MainActivity;
import com.example.greenify.activity.map.MapBoxActivity;
import com.example.greenify.util.ApplicationUtils;
import com.example.greenify.util.Environment;
import com.google.firebase.messaging.FirebaseMessaging;

public class WalkThruActivity extends AppCompatActivity {

    private ViewPager2 mWalkThruSliderViewPager;
    private LinearLayout mPageIndicatorLayout;

    private Button backButton;
    private Button nextButton;

    private TextView[] pageIndicators;

    private WalkThruPagerAdapter walkThruPagerAdapter;

    private int currentPageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SplashScreen.installSplashScreen(this);

        ApplicationUtils.configureWindowInsets(this);

        setContentView(R.layout.activity_walk_through);


        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();
                Environment.setDeviceToken(token);
                System.out.println("Token: " + token);
            } else {
                System.out.println("Failed");
            }
        });

        backButton = findViewById(R.id.walk_thru_btn_back);
        nextButton = findViewById(R.id.walk_thru_btn_next);

        Button skipButton = findViewById(R.id.walk_thru_btn_skip);

        backButton.setOnClickListener(v -> {
            if (currentPageIndex > 0) {
                mWalkThruSliderViewPager.setCurrentItem(currentPageIndex - 1, true);
            }
        });

        nextButton.setOnClickListener(v -> {
            ApplicationUtils.sendNotificationMessage("Hello 123", "Test 1", "123");

            if (currentPageIndex < walkThruPagerAdapter.getItemCount() - 1) {
                mWalkThruSliderViewPager.setCurrentItem(currentPageIndex + 1, true);
            } else {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });

        skipButton.setOnClickListener(v -> {
            startActivity(new Intent(this, MapBoxActivity.class));
            finish();
        });

        mWalkThruSliderViewPager = findViewById(R.id.walk_thru_slide_pager);

        mPageIndicatorLayout = findViewById(R.id.walk_thru_layout_indicator);

        walkThruPagerAdapter = new WalkThruPagerAdapter(this);

        mWalkThruSliderViewPager.setAdapter(walkThruPagerAdapter);

        mWalkThruSliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentPageIndex = position;
                updateUI();
            }
        });
    }

    private void updateUI() {
        backButton.setTextColor(currentPageIndex > 0 ? ContextCompat.getColor(this, R.color.light_green_1) : ContextCompat.getColor(this, R.color.gray));

        if (currentPageIndex == walkThruPagerAdapter.getItemCount() - 1) {
            nextButton.setText(R.string.login);
        } else {
            nextButton.setText(R.string.next);
        }

        if (currentPageIndex == 0) {
            mPageIndicatorLayout.removeAllViews();
        } else {
            createDots();
            setIndicator(currentPageIndex);
        }
    }

    private void createDots() {
        pageIndicators = new TextView[walkThruPagerAdapter.getItemCount()];
        mPageIndicatorLayout.removeAllViews();

        for (int i = 0; i < pageIndicators.length; i++) {
            pageIndicators[i] = new TextView(this);
            pageIndicators[i].setText(HtmlCompat.fromHtml("&#8226;", HtmlCompat.FROM_HTML_MODE_LEGACY));
            pageIndicators[i].setTextSize(30);
            pageIndicators[i].setTextColor(ContextCompat.getColor(this, R.color.gray));
            mPageIndicatorLayout.addView(pageIndicators[i]);
        }
    }

    private void setIndicator(int position) {
        for (int i = 0; i < pageIndicators.length; i++) {
            pageIndicators[i].setTextColor(ContextCompat.getColor(this, (i == position) ? R.color.light_green_1 : R.color.gray));
        }
    }
}
