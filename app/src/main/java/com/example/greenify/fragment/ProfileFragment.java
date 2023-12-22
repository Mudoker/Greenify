package com.example.greenify.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenify.R;
import com.example.greenify.activity.adapter.ProfileHostEventAdapter;

public class ProfileFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        RecyclerView profileRecyclerView = rootView.findViewById(R.id.profile_list_host_event);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        profileRecyclerView.setLayoutManager(layoutManager);

        ProfileHostEventAdapter adapter = new ProfileHostEventAdapter(requireContext());
        profileRecyclerView.setAdapter(adapter);

        profileRecyclerView.setVisibility(View.VISIBLE);
        // Inflate the layout for this fragment
        return rootView;
    }
}