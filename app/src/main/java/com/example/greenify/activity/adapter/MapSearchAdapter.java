package com.example.greenify.activity.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.greenify.R;
import com.example.greenify.model.EventModel;
import com.example.greenify.util.EventModelDiffCallback;
import com.example.greenify.util.FirebaseAPIs;
import com.example.greenify.util.FirebaseCallback;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class MapSearchAdapter extends RecyclerView.Adapter<MapSearchAdapter.ViewHolder> {
    private List<EventModel> eventModelList;
    private List<EventModel> filteredEventList = new ArrayList<>();

    private String selectedCategory = "All";
    private String selectedSearchPhrase = "All";

    private OnItemClickListener onItemClickListener;


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public MapSearchAdapter(List<EventModel> eventModelList) {
        this.eventModelList = eventModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_map_search_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MapSearchAdapter.ViewHolder holder, int position) {
        if (!filteredEventList.isEmpty() && position < filteredEventList.size()) {
            EventModel eventModel = filteredEventList.get(position);

            holder.eventTitle.setText(eventModel.getTitle());
            holder.eventLocation.setText(eventModel.getLocation());

            new Thread(() -> {
                FirebaseAPIs firebaseAPIs = new FirebaseAPIs();

                // Get media download URL in the background
                firebaseAPIs.getMediaDownloadUrlFromFirebase(eventModel.getId(), new FirebaseCallback() {
                    @Override
                    public void onSuccess(boolean success) {
                    }

                    @Override
                    public void onSuccess(Uri uri) {
                        // Load image using Glide in the UI thread
                        holder.itemView.post(() -> {
                            Glide.with(holder.itemView.getContext()).load(uri).into(holder.eventImage);
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                    }
                });
            }).start();
        } else {
            holder.itemView.setVisibility(View.GONE);
        }
    }

    public void setEventList(List<EventModel> eventModels) {
        this.eventModelList = eventModels;

        // Reset filteredEventList to match the new eventList
        filteredEventList.clear();
        filteredEventList.addAll(eventModels);
    }

    @Override
    public int getItemCount() {
        return filteredEventList.size();
    }

    public void setSelectedFilter(String phrase, String category) {
        String previousPhrase = selectedSearchPhrase;
        String previousCategory = selectedCategory;

        selectedSearchPhrase = phrase;
        selectedCategory = category;

        // Reset filteredEventList to an empty list before filtering
        filteredEventList.clear();

        List<EventModel> newFilteredData = filterData(phrase, category);
        List<EventModel> previousFilteredData = filterData(previousPhrase, previousCategory);

        // Update filteredEventList before calculating the DiffResult
        filteredEventList.addAll(newFilteredData);

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new EventModelDiffCallback(previousFilteredData, newFilteredData));
        diffResult.dispatchUpdatesTo(this);
    }

    public List<EventModel> filterData(String phrase, String category) {
        List<EventModel> filteredList = new ArrayList<>();

        if ("All".equalsIgnoreCase(category)) {
            // If category is "All", create a new list to avoid reference issues
            filteredList.addAll(eventModelList);
        } else {
            // Filter based on the category
            for (EventModel event : eventModelList) {
                if (category.equalsIgnoreCase(event.getCategory())) {
                    filteredList.add(event);
                }
            }
        }

        // Additional filter based on the title containing the given phrase
        List<EventModel> finalFilteredList = new ArrayList<>();
        if (phrase != null && !phrase.isEmpty()) {
            for (EventModel event : filteredList) {
                if (event.getTitle().toLowerCase().contains(phrase.toLowerCase())) {
                    finalFilteredList.add(event);
                }
            }
        } else {
            finalFilteredList.addAll(filteredList);
        }

        return finalFilteredList;
    }

    public interface OnItemClickListener {
        void onItemClick(String selectedEventLocation);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ShapeableImageView eventImage;
        private final TextView eventTitle;

        private final TextView eventLocation;

        private String selectedEventLocation = "";

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            eventImage = itemView.findViewById(R.id.item_map_search_event_img);
            eventTitle = itemView.findViewById(R.id.item_map_search_event_title);
            eventLocation = itemView.findViewById(R.id.item_map_search_event_address);
            itemView.setOnClickListener((v) -> {
                selectedEventLocation = eventLocation.getText().toString();
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(selectedEventLocation);
                }
            });
        }
    }
}
