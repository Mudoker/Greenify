package com.example.greenify.activity.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.greenify.R;
import com.example.greenify.activity.event.EventDetailActivity;
import com.example.greenify.model.EventModel;
import com.example.greenify.util.FirebaseAPIs;
import com.example.greenify.util.FirebaseCallback;

import java.util.List;

public class DashboardPopularEventAdapter extends RecyclerView.Adapter<DashboardPopularEventAdapter.ViewHolder> {
    private List<EventModel> eventList;

    public DashboardPopularEventAdapter(Context context) {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dashboard_popular_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventModel eventModel = eventList.get(position);
        holder.itemView.findViewById(R.id.item_popular_event_image);
        holder.itemView.findViewById(R.id.item_popular_event_title);
        holder.itemView.findViewById(R.id.item_popular_event_host);

        holder.eventType.setText(eventModel.getCategory());
        holder.eventTitle.setText(eventModel.getTitle());

        FirebaseAPIs firebaseAPIs = new FirebaseAPIs();

        String[] hostName = {null};

        firebaseAPIs.getUserDataById(eventModel.getOwnerId().toString(), userModel -> {
            hostName[0] = userModel.getUsername();

            if (TextUtils.isEmpty(hostName[0])) {
                hostName[0] = userModel.getEmail();
            }
            holder.eventHost.setText(hostName[0]);
        }, e -> holder.eventHost.setText(R.string.undefined));


        firebaseAPIs.getMediaDownloadUrlFromFirebase(eventModel.getId(), new FirebaseCallback() {
            @Override
            public void onSuccess(boolean success) {
            }

            @Override
            public void onSuccess(Uri uri) {
                Glide.with(holder.itemView.getContext()).load(uri).into(holder.eventImage);
            }

            @Override
            public void onFailure(Exception e) {
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), EventDetailActivity.class);
            intent.putExtra("EVENT_MODEL", eventModel);
            intent.putExtra("HOST_NAME", hostName[0]);
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        if (eventList == null) {
            return 0;
        }
        return eventList.size();
    }

    public void setEventList(List<EventModel> eventModels) {
        // Set the eventList
        this.eventList = eventModels;

        // Sort the eventList based on createdDate
        eventList.sort((event1, event2) -> {
            return event2.getCreatedDate().compareTo(event1.getCreatedDate());
        });

        // Limit the eventList to 5 events
        if (eventList.size() > 5) {
            eventList = eventList.subList(0, 5);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView eventImage;
        TextView eventTitle;
        TextView eventHost;

        TextView eventType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.item_popular_event_image);
            eventTitle = itemView.findViewById(R.id.item_popular_event_title);
            eventHost = itemView.findViewById(R.id.item_popular_event_host);
            eventType = itemView.findViewById(R.id.item_popular_event_category);
        }
    }
}
