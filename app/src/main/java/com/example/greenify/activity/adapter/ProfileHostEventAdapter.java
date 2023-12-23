package com.example.greenify.activity.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.greenify.R;
import com.example.greenify.model.UserModel;
import com.example.greenify.util.FirebaseAPIs;
import com.example.greenify.util.FirebaseCallback;

import java.util.List;

public class ProfileHostEventAdapter extends RecyclerView.Adapter<ProfileHostEventAdapter.ViewHolder> {
    private List<String> eventList;
    private MapSearchAdapter.OnItemClickListener onItemClickListener;

    private String selectedEvent;

    public interface OnItemClickListener {
        void onItemClick(String selectedEvent);
    }

    public void setOnItemClickListener(MapSearchAdapter.OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public ProfileHostEventAdapter(List<String> eventList) {
        this.eventList = eventList;
    }

    public void setEventList(List<String> eventModels) {
        this.eventList = eventModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.findViewById(R.id.item_host_event_img);

        FirebaseAPIs firebaseAPIs = new FirebaseAPIs();

        String eventId = UserModel.getUserSingleTon().getHostedEvents().get(position);

        holder.imageFilterView.setOnClickListener((v) -> {
            selectedEvent = eventId;
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(eventId);
            }
        });
        firebaseAPIs.getMediaDownloadUrlFromFirebase(eventId, new FirebaseCallback() {
            @Override
            public void onSuccess(boolean success) {
            }

            @Override
            public void onSuccess(Uri uri) {
                Glide.with(holder.itemView.getContext()).load(uri).into(holder.imageFilterView);
            }

            @Override
            public void onFailure(Exception e) {
            }
        });


    }

    @Override
    public int getItemCount() {
        if (eventList != null) {
            return eventList.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // You can access your item views here if needed
        public CardView cardView;
        public ImageFilterView imageFilterView;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.item_host_event_card_view);
            imageFilterView = itemView.findViewById(R.id.item_host_event_img);
        }
    }
}
