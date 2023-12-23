package com.example.greenify.activity.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.greenify.R;
import com.example.greenify.model.EventModel;
import com.example.greenify.util.EventModelDiffCallback;
import com.example.greenify.util.FirebaseAPIs;
import com.example.greenify.util.FirebaseCallback;

import java.util.ArrayList;
import java.util.List;

public class DashboardAllEventAdapter extends RecyclerView.Adapter<DashboardAllEventAdapter.ViewHolder> {
    private List<EventModel> eventList;
    private List<EventModel> filteredEventList = new ArrayList<>();
    private String selectedCategory = "All";

    public DashboardAllEventAdapter(Context context) {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dashboard_all_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (!filteredEventList.isEmpty() && position < filteredEventList.size()) {
            EventModel eventModel = filteredEventList.get(position);

            holder.eventTitle.setText(eventModel.getTitle());
            holder.eventType.setText(eventModel.getCategory());

            FirebaseAPIs firebaseAPIs = new FirebaseAPIs();
            firebaseAPIs.getUserDataById(eventModel.getOwnerId().toString(), userModel -> {
                String hostName = userModel.getUsername();

                if (TextUtils.isEmpty(hostName)) {
                    hostName = userModel.getEmail();
                }
                holder.hostName.setText(hostName);
            }, e -> holder.hostName.setText(R.string.undefined));

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

            holder.itemView.setVisibility(View.VISIBLE);
        } else {
            holder.itemView.setVisibility(View.GONE);
        }
    }

    public void setSelectedCategory(String category) {
        String previousCategory = selectedCategory;
        selectedCategory = category;

        // Reset filteredEventList to an empty list before filtering
        filteredEventList.clear();

        List<EventModel> newFilteredData = filterDataByCategory(category);
        List<EventModel> previousFilteredData = filterDataByCategory(previousCategory);

        Log.d("Filter", "New Filtered Data Size: " + newFilteredData.size());
        Log.d("Filter", "Previous Filtered Data Size: " + previousFilteredData.size());

        // Update filteredEventList before calculating the DiffResult
        filteredEventList.addAll(newFilteredData);

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new EventModelDiffCallback(previousFilteredData, newFilteredData));
        diffResult.dispatchUpdatesTo(this);
    }


    public List<EventModel> filterDataByCategory(String category) {
        List<EventModel> filteredList = new ArrayList<>();

        if ("All".equalsIgnoreCase(category)) {
            // If category is "All", create a new list to avoid reference issues
            filteredList.addAll(eventList);
        } else {
            // filter based on the category
            for (EventModel event : eventList) {
                if (category.equalsIgnoreCase(event.getCategory())) {
                    filteredList.add(event);
                }
            }
        }

        return filteredList;
    }


    @Override
    public int getItemCount() {
        return filteredEventList.size();
    }

    public void setEventList(List<EventModel> eventModels) {
        this.eventList = eventModels;

        // Reset filteredEventList to match the new eventList
        filteredEventList.clear();
        filteredEventList.addAll(eventModels);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageFilterView eventImage;

        TextView eventTitle, hostTitle, hostName, eventTypeTitle, eventType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.item_event_image);
            eventTitle = itemView.findViewById(R.id.item_all_event_title);
            eventTypeTitle = itemView.findViewById(R.id.item_all_event_category_title);
            hostName = itemView.findViewById(R.id.item_all_event_host);
            hostTitle = itemView.findViewById(R.id.item_all_event_host_title);
            eventType = itemView.findViewById(R.id.item_all_event_category_type);
        }
    }
}
