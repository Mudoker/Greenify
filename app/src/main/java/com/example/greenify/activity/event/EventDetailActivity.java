package com.example.greenify.activity.event;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.utils.widget.ImageFilterView;

import com.example.greenify.R;
import com.example.greenify.model.EventModel;
import com.example.greenify.model.UserModel;
import com.example.greenify.util.ApplicationUtils;
import com.example.greenify.util.FirebaseAPIs;
import com.example.greenify.util.FirebaseCallback;

import java.util.Objects;

public class EventDetailActivity extends AppCompatActivity {
    private EventModel eventModel;

    private final FirebaseAPIs firebaseAPIs = new FirebaseAPIs();

    private boolean isEdit;

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

                if (eventModel == null) {
                    Toast.makeText(this, "Error loading Data", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                hostName = ApplicationUtils.getCreatedDateAsString(eventModel.getCreatedDate());
                if (Objects.equals(eventModel.getOwnerId(), UserModel.getUserSingleTon().getId())) {
                    isEdit = true;
                }
            }
        }

        TextView eventCategory = findViewById(R.id.event_detail_category);
        TextView eventHost = findViewById(R.id.event_detail_host_name);
        TextView eventTitle = findViewById(R.id.event_detail_location);
        TextView eventDes = findViewById(R.id.event_detail_event_des);
        TextView eventStatus = findViewById(R.id.event_detail_txt_status_content);
        TextView eventParticipant = findViewById(R.id.event_detail_txt_participant_content);

        ImageFilterView btnShowMemList = findViewById(R.id.event_detail_btn_member_list);

        if (isEdit) {
            btnShowMemList.setVisibility(View.VISIBLE);
        }

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

        Button btnRegister = findViewById(R.id.event_detail_btn_register);

        if (Objects.equals(eventModel.getOwnerId(), UserModel.getUserSingleTon().getId())) {
            btnRegister.setText("Edit");
        }

        btnRegister.setOnClickListener((v) -> {
            if (isEdit) {
                Intent intent2 = new Intent(this, EventUpdateFormActivity.class);

                intent2.putExtra("EVENT_MODEL", eventModel);
                startActivity(intent2);
            } else {
                if (UserModel.getUserSingleTon().getJoinedEvents().contains(eventModel.getId())) {
                    try {
                        UserModel.getUserSingleTon().removeEnrolEvent(eventModel.getId());
                        eventModel.removeParticipants(UserModel.getUserSingleTon().getId());

                        btnRegister.setText("Unregister");
                        btnRegister.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));

                        firebaseAPIs.updateUserData(UserModel.getUserSingleTon(), new FirebaseCallback() {
                            @Override
                            public void onSuccess(boolean success) {
                            }

                            @Override
                            public void onSuccess(Uri uri) {
                            }

                            @Override
                            public void onFailure(Exception e) {
                            }
                        });

                        firebaseAPIs.updateEventData(eventModel, new FirebaseCallback() {
                            @Override
                            public void onSuccess(boolean success) {
                            }

                            @Override
                            public void onSuccess(Uri uri) {
                            }

                            @Override
                            public void onFailure(Exception e) {
                            }
                        });
                    } catch (Exception e) {
                        Log.d("EVENT UPDATE ERROR 3: ", String.valueOf(e));
                    }
                } else {
                    System.out.println("Trigger Register");
                    UserModel userModel = UserModel.getUserSingleTon();

                    userModel.addJoinedEvents(eventModel.getId());

                    System.out.println("Hosted events" + userModel.getHostedEvents());
                    System.out.println("Joined events" + userModel.getJoinedEvents());

                    eventModel.addParticipants(UserModel.getUserSingleTon().getId());

                    firebaseAPIs.updateEventData(eventModel, new FirebaseCallback() {
                        @Override
                        public void onSuccess(boolean success) {
                        }

                        @Override
                        public void onSuccess(Uri uri) {
                        }

                        @Override
                        public void onFailure(Exception e) {
                        }
                    });

                    firebaseAPIs.updateUserData(UserModel.getUserSingleTon(), new FirebaseCallback() {
                        @Override
                        public void onSuccess(boolean success) {
                        }

                        @Override
                        public void onSuccess(Uri uri) {
                        }

                        @Override
                        public void onFailure(Exception e) {
                        }
                    });

                    btnRegister.setText("Unregister");
                    btnRegister.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                }
            }
        });


        btnShowMemList.setOnClickListener(v -> {
            Intent intent1 = new Intent(this, EventMemberListActivity.class);
            intent1.putStringArrayListExtra("MEM_LIST", eventModel.getParticipants());
            startActivity(intent1);
        });

        btnCloseView.setOnClickListener(v -> {
            finish();
        });
    }
}