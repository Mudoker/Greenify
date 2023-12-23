package com.example.greenify.activity.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenify.R;
import com.example.greenify.util.FirebaseAPIs;
import com.example.greenify.util.FirebaseCallback;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

public class MemberListAdapter extends RecyclerView.Adapter<MemberListAdapter.MemberViewHolder> {
    private List<String> memberList;
    private Context context;

    public MemberListAdapter(List<String> memberList) {
        this.memberList = memberList;
    }

    public MemberListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MemberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mem_list, parent, false);
        return new MemberViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberViewHolder holder, int position) {
        if (memberList == null) {
            return;
        }

        String memberId = memberList.get(position);

        FirebaseAPIs firebaseAPIs = new FirebaseAPIs();
        firebaseAPIs.getUserDataById(memberId, userModel -> {
            // Set data to the views
            holder.titleTextView.setText(userModel.getUsername());
            holder.descriptionTextView.setText(userModel.getEmail());
        }, e -> {

        });

        firebaseAPIs.getMediaDownloadUrlFromFirebase(memberId, new FirebaseCallback() {
            @Override
            public void onSuccess(boolean success) {
            }

            @Override
            public void onSuccess(Uri uri) {
                holder.userAva.setImageURI(uri);
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    @Override
    public int getItemCount() {
        if (memberList != null) {
            return memberList.size();
        }
        return 0;
    }

    public static class MemberViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        CardView cardView;
        ShapeableImageView userAva;

        public MemberViewHolder(@NonNull View itemView) {
            super(itemView);
            userAva = itemView.findViewById(R.id.profile_img_mem_list);
            titleTextView = itemView.findViewById(R.id.mem_list_txt_title);
            descriptionTextView = itemView.findViewById(R.id.mem_list_txt_description);
            cardView = itemView.findViewById(R.id.mem_list_card);
        }
    }
}
