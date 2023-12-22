package com.example.greenify.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.greenify.R;
import com.example.greenify.activity.adapter.DashboardAllEventAdapter;
import com.example.greenify.activity.adapter.DashboardPopularEventAdapter;
import com.example.greenify.util.ApplicationUtils;
import com.example.greenify.util.GridSpacingItemDecoration;

public class DashboardFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
// Inflate the layout
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        RecyclerView profileRecyclerView = rootView.findViewById(R.id.dashboard_popular_event_recycler_view);
        RecyclerView tableRecyclerView = rootView.findViewById(R.id.dashboard_table_recycler_view);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2); // 2 columns
        tableRecyclerView.setLayoutManager(gridLayoutManager);
        tableRecyclerView.addItemDecoration(new GridSpacingItemDecoration(ApplicationUtils.dpToPx(requireContext(), 10)));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        profileRecyclerView.setLayoutManager(layoutManager);

        DashboardPopularEventAdapter adapter = new DashboardPopularEventAdapter(requireContext());
        DashboardAllEventAdapter adapter2 = new DashboardAllEventAdapter(requireContext());

        profileRecyclerView.setAdapter(adapter);
        tableRecyclerView.setAdapter(adapter2);

        // Inflate the layout for this fragment
        return rootView;
    }
}