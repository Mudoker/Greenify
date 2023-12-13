package com.example.greenify;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.utils.widget.ImageFilterView;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.fragment_profile);
        // Hide system bars after setting content view
        Window window = getWindow();
        WindowInsetsController insetsController = window.getInsetsController();
        if (insetsController != null) {
            insetsController.hide(WindowInsets.Type.systemBars());
            insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_DEFAULT);
        }

        HorizontalScrollView horizontalScrollView = findViewById(R.id.profile_list_host_event);
        LinearLayout linearLayout = findViewById(R.id.profile_list_host_event_layout);

        // Create 7 CardViews
        for (int i = 0; i < 7; i++) {
            CardView cardView = new CardView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ApplicationUtils.dpToPx(this,200), // Width
            ApplicationUtils.dpToPx(this, 100)  // Height
            );
            layoutParams.setMargins(ApplicationUtils.dpToPx(this, 16), ApplicationUtils.dpToPx(this, 16), ApplicationUtils.dpToPx(this, 16), ApplicationUtils.dpToPx(this, 16));
            cardView.setLayoutParams(layoutParams);
            cardView.setRadius(ApplicationUtils.dpToPx(this, 20));
            cardView.setClickable(true);
            cardView.setCardBackgroundColor(getResources().getColor(R.color.dark_blue));

            ImageFilterView imageFilterView = new ImageFilterView(this);
            imageFilterView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            imageFilterView.setImageResource(R.drawable.ic_outreach);
            imageFilterView.setColorFilter(getResources().getColor(R.color.white));

            cardView.addView(imageFilterView);
            linearLayout.addView(cardView);
        }

        LinearLayout profileRecentActLayout = findViewById(R.id.profile_recent_act_layout);

        float scale = getResources().getDisplayMetrics().density;
        int imageSizeInDp = 100; // Replace with your desired size in dp
        int imageSizeInPixels = (int) (imageSizeInDp * scale + 0.5f);
        int cardCornerRadius = (int) (20 * scale + 0.5f);

        for (int i = 0; i < 10; i++) {
            CardView cardView = new CardView(this);

            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            cardParams.setMargins(0, ApplicationUtils.dpToPx(this, 5), 0, ApplicationUtils.dpToPx(this, 5)); // Adjust margins as needed
            cardView.setLayoutParams(cardParams);
            cardView.setCardBackgroundColor(getResources().getColor(R.color.dark_blue));
            cardView.setRadius(cardCornerRadius);

            // Create the content views programmatically
            ImageFilterView profileImage = new ImageFilterView(this);
            profileImage.setId(View.generateViewId()); // Generate a unique ID for each view

// Adjust the LayoutParams to include a margin of 10
            LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(
                    imageSizeInPixels,
                    imageSizeInPixels
            );
            imageLayoutParams.setMargins(ApplicationUtils.dpToPx(this,10), ApplicationUtils.dpToPx(this,10), ApplicationUtils.dpToPx(this,10), ApplicationUtils.dpToPx(this,10)); // Add margins (left, top, right, bottom)
            profileImage.setLayoutParams(imageLayoutParams);

            profileImage.setImageResource(R.drawable.ic_outreach);
            profileImage.setColorFilter(Color.WHITE);

            LinearLayout textLayout = new LinearLayout(this);

// Set layout parameters with margin and weight
            LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1.0f
            );
            textLayoutParams.setMargins(ApplicationUtils.dpToPx(this, 10), ApplicationUtils.dpToPx(this, 10), ApplicationUtils.dpToPx(this, 10), ApplicationUtils.dpToPx(this, 10)); // Add margin (top, start, bottom, end)
            textLayout.setLayoutParams(textLayoutParams);

            textLayout.setOrientation(LinearLayout.VERTICAL);
            textLayout.setGravity(Gravity.CENTER_VERTICAL); // Center vertically


            TextView titleTextView = new TextView(this);
            titleTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            titleTextView.setId(View.generateViewId());
            titleTextView.setText(R.string.event_title);
            titleTextView.setTextColor(getResources().getColor(R.color.white));
            titleTextView.setTextSize(30);
            titleTextView.setTypeface(titleTextView.getTypeface(), Typeface.BOLD);
            titleTextView.setAlpha(0.8f);
            titleTextView.setGravity(Gravity.CENTER_VERTICAL); // Center vertically

            TextView descriptionTextView = new TextView(this);
            descriptionTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            descriptionTextView.setId(View.generateViewId());
            descriptionTextView.setText(R.string.hello_worlddddd);
            descriptionTextView.setTextColor(getResources().getColor(R.color.white));
            descriptionTextView.setTextSize(16);
            descriptionTextView.setAlpha(0.8f);
            descriptionTextView.setGravity(Gravity.CENTER_VERTICAL); // Center vertically

            textLayout.addView(titleTextView);
            textLayout.addView(descriptionTextView);

            TextView doneTextView = new TextView(this);
            LinearLayout.LayoutParams doneTextLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            doneTextLayoutParams.setMarginEnd(ApplicationUtils.dpToPx(this, 20)); // Add margin to the end (right)
            doneTextView.setLayoutParams(doneTextLayoutParams);

            doneTextView.setId(View.generateViewId());
            doneTextView.setText(R.string.done);
            doneTextView.setTextColor(getResources().getColor(R.color.light_green_1));
            doneTextView.setTextSize(24);
            doneTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
            // Center vertically and align to end

            // Add content views to the CardView
            LinearLayout cardContentLayout = new LinearLayout(this);
            cardContentLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            cardContentLayout.setOrientation(LinearLayout.HORIZONTAL);
            cardContentLayout.addView(profileImage);
            cardContentLayout.addView(textLayout);
            cardContentLayout.addView(doneTextView);

            cardView.addView(cardContentLayout); // Add the content layout to the CardView

            profileRecentActLayout.addView(cardView); // Add the CardView to the parent layout
        }
    }
}