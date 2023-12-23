package com.example.greenify.activity.event;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.greenify.R;
import com.example.greenify.model.EventModel;
import com.example.greenify.util.ApplicationUtils;

public class EventDetailActivity extends AppCompatActivity {
    private EventModel eventModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        ApplicationUtils.configureWindowInsets(this);

        TextView btnCloseView = findViewById(R.id.btn_close_view);
        String hostName = "";

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("EVENT_MODEL")) {
                eventModel = (EventModel) intent.getSerializableExtra("EVENT_MODEL");
            } else {
                finish();
                return; // No need to continue if the first condition fails
            }

            if (intent.hasExtra("HOST_NAME")) {
                hostName = intent.getStringExtra("HOST_NAME");
            } else {
                hostName = "N/A";
            }
        } else {
            finish(); // Finish the activity if there is no intent
        }


        TextView eventCategory = findViewById(R.id.event_detail_category);
        TextView eventHost = findViewById(R.id.event_detail_host_name);
        TextView eventTitle = findViewById(R.id.event_detail_location);
        TextView eventDes = findViewById(R.id.event_detail_event_des);
        TextView eventStatus = findViewById(R.id.event_detail_txt_status_content);
        TextView eventParticipant = findViewById(R.id.event_detail_txt_participant_content);

        eventCategory.setText(eventModel.getCategory());
        eventHost.setText(hostName);
        eventDes.setText(eventModel.getDescription());
        eventTitle.setText(eventModel.getTitle());

        if (eventModel.getStatus()) {
            eventStatus.setText("On Going");
        } else {
            eventStatus.setText("Closed");
        }

        if (eventModel.getParticipants() == null || eventModel.getParticipants().isEmpty()) {
            eventParticipant.setText("0/20");
        } else {
            int size = eventModel.getParticipants().size();
            String participantNum = size + "/20";
            eventParticipant.setText(participantNum);
        }


        btnCloseView.setOnClickListener(v -> {
            finish();
        });
    }
}