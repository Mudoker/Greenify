package com.example.greenify.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenify.R;

public class ProfileRecentActivityAdapter extends RecyclerView.Adapter<ProfileRecentActivityAdapter.ViewHolder> {


    public ProfileRecentActivityAdapter(Context context) {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_profile_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // You can bind data or perform any other operations here if needed
    }

    @Override
    public int getItemCount() {
        return 2; // Number of items in the RecyclerView
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
