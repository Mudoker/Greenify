package com.example.greenify.activity.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenify.R;

public class DashboardAllEventAdapter extends RecyclerView.Adapter<DashboardAllEventAdapter.ViewHolder> {

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
    }

    @Override
    public int getItemCount() {
        return 5;
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
