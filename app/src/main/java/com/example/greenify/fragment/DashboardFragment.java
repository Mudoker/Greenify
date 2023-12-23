package com.example.greenify.fragment;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.greenify.R;
import com.example.greenify.activity.adapter.DashboardAllEventAdapter;
import com.example.greenify.activity.adapter.DashboardPopularEventAdapter;
import com.example.greenify.activity.event.EventFormActivity;
import com.example.greenify.activity.map.MapBoxActivity;
import com.example.greenify.util.ApplicationUtils;
import com.example.greenify.util.FirebaseAPIs;
import com.example.greenify.util.GridSpacingItemDecoration;

public class DashboardFragment extends Fragment {
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        swipeRefreshLayout = rootView.findViewById(R.id.dashboard_refresh);

        RecyclerView popularEventView = rootView.findViewById(R.id.dashboard_popular_event_recycler_view);
        RecyclerView allEventView = rootView.findViewById(R.id.dashboard_table_recycler_view);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2); // 2 columns
        allEventView.setLayoutManager(gridLayoutManager);
        allEventView.addItemDecoration(new GridSpacingItemDecoration(ApplicationUtils.dpToPx(requireContext(), 10)));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        popularEventView.setLayoutManager(layoutManager);

        DashboardPopularEventAdapter popularEventAdapter = new DashboardPopularEventAdapter(requireContext());
        DashboardAllEventAdapter allEventAdapter = new DashboardAllEventAdapter(requireContext());

        popularEventView.setAdapter(popularEventAdapter);
        allEventView.setAdapter(allEventAdapter);
        fetchDataEvent(allEventAdapter, popularEventAdapter);

        // Filter buttons
        Button btnAll = rootView.findViewById(R.id.btn_show_all);
        Button btnParks = rootView.findViewById(R.id.btn_show_park);
        Button btnBeaches = rootView.findViewById(R.id.btn_show_beach);
        Button btnStreets = rootView.findViewById(R.id.btn_show_street);

        View.OnClickListener commonClickListener = v -> {
            // Reset the background tint of all buttons to gray
            btnAll.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gray)));
            btnParks.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gray)));
            btnBeaches.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gray)));
            btnStreets.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.gray)));

            // Set clicked button to white
            ((Button) v).setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), android.R.color.white)));

            // Update based on the clicked button
            String selectedCategory;
            if (v.getId() == R.id.btn_show_all) {
                selectedCategory = "All";
            } else if (v.getId() == R.id.btn_show_park) {
                selectedCategory = "Park";
            } else if (v.getId() == R.id.btn_show_beach) {
                selectedCategory = "Beach";
            } else if (v.getId() == R.id.btn_show_street) {
                selectedCategory = "Street";
            } else {
                selectedCategory = "All"; // Default to "All"
            }

            allEventAdapter.setSelectedCategory(selectedCategory);
        };

        // Set up the refresh listener
        swipeRefreshLayout.setOnRefreshListener(() -> {
            btnAll.performClick();
            fetchDataEvent(allEventAdapter, popularEventAdapter);
        });

        // Set the click listener for all buttons
        btnAll.setOnClickListener(commonClickListener);
        btnParks.setOnClickListener(commonClickListener);
        btnBeaches.setOnClickListener(commonClickListener);
        btnStreets.setOnClickListener(commonClickListener);


        // Utility buttons
        ImageFilterView btnCreateEvent = rootView.findViewById(R.id.btn_event_create);
        ImageFilterView btnOpenMap = rootView.findViewById(R.id.btn_open_map);

        btnOpenMap.setOnClickListener((v) -> {
            Intent intent = new Intent(requireContext(), MapBoxActivity.class);
            startActivity(intent);
            requireActivity().finish();

        });
        btnCreateEvent.setOnClickListener((v) -> {
            Intent intent = new Intent(requireContext(), EventFormActivity.class);
            startActivity(intent);
        });
        return rootView;
    }

    private void fetchDataEvent(DashboardAllEventAdapter allEventAdapter, DashboardPopularEventAdapter popularEventAdapter) {
        FirebaseAPIs firebaseAPIs = new FirebaseAPIs();

        firebaseAPIs.getAllEventsData(eventModels -> {
            int previousAllSize = allEventAdapter.getItemCount();
            allEventAdapter.setEventList(eventModels);
            int newAllSize = allEventAdapter.getItemCount();

            // Notify only the items that were added
            for (int i = previousAllSize; i < newAllSize; i++) {
                allEventAdapter.notifyItemInserted(i);
            }

            int previousPopularSize = popularEventAdapter.getItemCount();
            popularEventAdapter.setEventList(eventModels);
            int newPopularSize = popularEventAdapter.getItemCount();

            // Notify only the items that were added
            for (int i = previousPopularSize; i < newPopularSize; i++) {
                popularEventAdapter.notifyItemInserted(i);
            }

            // Stop the swipe refresh animation
            swipeRefreshLayout.setRefreshing(false);
        }, e -> {
            Toast.makeText(requireContext(), "Failed To Fetch Data", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        });
    }
}