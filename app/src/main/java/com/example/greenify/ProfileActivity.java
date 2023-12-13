package com.example.greenify;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.core.content.ContextCompat;

public class ProfileActivity extends AppCompatActivity {

    private final int CORNER_RADIUS = Environment.getCornerRadius();

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

        LinearLayout linearLayoutHostEvent = findViewById(R.id.profile_list_host_event_layout);

        createListOfEvent(linearLayoutHostEvent);


        LinearLayout profileRecentActLayout = findViewById(R.id.profile_recent_act_layout);

        createListOfRecentActivities(profileRecentActLayout);
    }

    private void createListOfEvent(LinearLayout parentLayout) {
        final int EVENT_CARD_WIDTH = ApplicationUtils.dpToPx(this, 180);
        final int EVENT_CARD_HEIGHT = ApplicationUtils.dpToPx(this, 80);

        // Margin value in dp
        final int MARGIN_VALUE = ApplicationUtils.dpToPx(this, 16);

        // Create CardViews
        for (int i = 0; i < 7; i++) {
            // Set up CardView layout
            CardView cardView = new CardView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    EVENT_CARD_WIDTH, // Width
                    EVENT_CARD_HEIGHT // Height
            );

            layoutParams.setMargins(MARGIN_VALUE, MARGIN_VALUE, MARGIN_VALUE, MARGIN_VALUE);

            cardView.setLayoutParams(layoutParams);

            cardView.setRadius(ApplicationUtils.dpToPx(this, CORNER_RADIUS));

            cardView.setClickable(true);

            cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.dark_blue));

            // Set up ImageFilterView
            ImageFilterView imageFilterView = new ImageFilterView(this);
            imageFilterView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));

            imageFilterView.setImageResource(R.drawable.ic_outreach);
            imageFilterView.setColorFilter(ContextCompat.getColor(this, R.color.white));

            // Add content layout to CardView
            cardView.addView(imageFilterView);

            // Add CardView to parent layout
            parentLayout.addView(cardView);
        }
    }

    private void createListOfRecentActivities(LinearLayout parentLayout) {
        // Constants for size, margin, font size, and alpha values
        final int RECENT_ACT_IMG_SIZE_PX = ApplicationUtils.dpToPx(this, 80);
        final int MARGIN_VALUE_CARD_VERTICAL = ApplicationUtils.dpToPx(this, 5);
        final int MARGIN_VALUE_CARD_HORIZONTAL = 0;
        final int MARGIN_VALUE_IMG = ApplicationUtils.dpToPx(this, 10);
        final int TITLE_FONT_SIZE = 26;
        final int DES_FONT_SIZE = 16;
        final int STATUS_FONT_SIZE = 22;
        final float TEXT_ALPHA_VALUE = 0.8f;

        // Create recent activity CardViews
        for (int i = 0; i < 10; i++) {
            // Set up CardView layout
            CardView cardView = new CardView(this);
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            cardParams.setMargins(MARGIN_VALUE_CARD_HORIZONTAL, MARGIN_VALUE_CARD_VERTICAL, MARGIN_VALUE_CARD_HORIZONTAL, MARGIN_VALUE_CARD_VERTICAL); // Adjust margins as needed

            cardView.setLayoutParams(cardParams);

            cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.dark_blue));

            cardView.setRadius(ApplicationUtils.dpToPx(this, CORNER_RADIUS));

            // Set up ImageFilterView
            ImageFilterView profileImage = new ImageFilterView(this);

            profileImage.setId(View.generateViewId()); // Generate a unique ID for each view

            LinearLayout.LayoutParams imageLayoutParams = new LinearLayout.LayoutParams(
                    RECENT_ACT_IMG_SIZE_PX,
                    RECENT_ACT_IMG_SIZE_PX
            );

            imageLayoutParams.setMargins(ApplicationUtils.dpToPx(this, 10), ApplicationUtils.dpToPx(this, 10), ApplicationUtils.dpToPx(this, 10), ApplicationUtils.dpToPx(this, 10)); // Add margins (left, top, right, bottom)
            profileImage.setLayoutParams(imageLayoutParams);

            profileImage.setImageResource(R.drawable.ic_outreach);
            profileImage.setColorFilter(Color.WHITE);

            LinearLayout textLayout = new LinearLayout(this);

            // Set up TextViews
            LinearLayout.LayoutParams textLayoutParams = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1.0f
            );

            textLayoutParams.setMargins(MARGIN_VALUE_IMG, MARGIN_VALUE_IMG, MARGIN_VALUE_IMG, MARGIN_VALUE_IMG);
            textLayout.setLayoutParams(textLayoutParams);

            textLayout.setOrientation(LinearLayout.VERTICAL);
            textLayout.setGravity(Gravity.CENTER_VERTICAL);

            TextView titleTextView = new TextView(this);
            titleTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            titleTextView.setId(View.generateViewId());
            titleTextView.setText(R.string.event_title);
            titleTextView.setTextColor(ContextCompat.getColor(this, R.color.white));
            titleTextView.setTextSize(TITLE_FONT_SIZE);
            titleTextView.setTypeface(titleTextView.getTypeface(), Typeface.BOLD);
            titleTextView.setAlpha(TEXT_ALPHA_VALUE);
            titleTextView.setGravity(Gravity.CENTER_VERTICAL); // Center vertically

            TextView descriptionTextView = new TextView(this);
            descriptionTextView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            descriptionTextView.setId(View.generateViewId());
            descriptionTextView.setText(R.string.hello_worlddddd);
            descriptionTextView.setTextColor(ContextCompat.getColor(this, R.color.white));
            descriptionTextView.setTextSize(DES_FONT_SIZE);
            descriptionTextView.setAlpha(TEXT_ALPHA_VALUE);
            descriptionTextView.setGravity(Gravity.CENTER_VERTICAL); // Center vertically

            textLayout.addView(titleTextView);
            textLayout.addView(descriptionTextView);

            TextView doneTextView = new TextView(this);
            LinearLayout.LayoutParams doneTextLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            doneTextLayoutParams.setMarginEnd(ApplicationUtils.dpToPx(this, 20));
            doneTextView.setLayoutParams(doneTextLayoutParams);

            doneTextView.setId(View.generateViewId());
            doneTextView.setText(R.string.done);
            doneTextView.setTextColor(ContextCompat.getColor(this, R.color.light_green_1));
            doneTextView.setTextSize(STATUS_FONT_SIZE);
            doneTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);

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

            // Add content layout to CardView
            cardView.addView(cardContentLayout);

            // Add CardView to parent layout
            parentLayout.addView(cardView);
        }
    }
}