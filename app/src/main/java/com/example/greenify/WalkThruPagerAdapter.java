package com.example.greenify;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WalkThruPagerAdapter extends RecyclerView.Adapter<WalkThruPagerAdapter.WalkThruViewHolder> {

    private final Context mContext;
    private final int[] mImageList = {R.drawable.volunteering, R.drawable.planting, R.drawable.cleaning, R.drawable.learning};
    private final int[] mTitleList = {R.string.walkthru_title_volunteering_title, R.string.walkthru_title_planting_title, R.string.walkthru_title_collecting_title, R.string.walkthru_title_learning_title};
    private final int[] mDescriptionList = {R.string.walkthru_title_volunteering_des, R.string.walkthru_title_planting_des, R.string.walkthru_title_collecting_des, R.string.walkthru_title_learning_des};

    public WalkThruPagerAdapter(Context context) {
        this.mContext = context;
    }

    @NonNull
    @Override
    public WalkThruViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_walk_through_slider, parent, false);
        return new WalkThruViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WalkThruViewHolder holder, int position) {
        holder.imageView.setImageResource(mImageList[position]);
        holder.title.setText(mContext.getString(mTitleList[position]));
        holder.description.setText(mContext.getString(mDescriptionList[position]));
    }

    @Override
    public int getItemCount() {
        return mTitleList.length;
    }

    static class WalkThruViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title;
        TextView description;

        WalkThruViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.walk_thru_slider_img);
            title = itemView.findViewById(R.id.walk_thru_slider_title);
            description = itemView.findViewById(R.id.walk_thru_slider_des);
        }
    }
}
