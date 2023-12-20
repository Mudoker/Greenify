package com.example.greenify.activity.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.greenify.R;
import com.example.greenify.util.ApplicationUtils;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapboxMap mapboxMap;
    private MapView mapView;
    private Location currentUserLocation;
    private MapThread mapThread;

    private boolean isMapRunning;

    private static final int LOCATION_UPDATE_INTERVAL = 150; // Update interval in milliseconds

    // Request permission
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            getUserLocation();
            enableLocationComponent(mapboxMap.getStyle());
            Toast.makeText(MapActivity.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
        } else {
            ApplicationUtils.requestPermissions(MapActivity.this);
        }
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init mapbox
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_map);

        mapView = findViewById(R.id.map_mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, this::enableLocationComponent);
        mapboxMap.getUiSettings().setZoomGesturesEnabled(true);
        mapboxMap.getUiSettings().setScrollGesturesEnabled(true);
        mapboxMap.getUiSettings().setRotateGesturesEnabled(true);
    }

    private void enableLocationComponent(Style style) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            return;
        }

        LocationComponent locationComponent = mapboxMap.getLocationComponent();

        try {
            locationComponent.activateLocationComponent(this, style);
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setCameraMode(CameraMode.TRACKING_GPS);

            LocationComponentOptions locationComponentOptions =
                    LocationComponentOptions.builder(this)
                            .elevation(5f)
                            .accuracyAlpha(0.6f)
                            .pulseEnabled(true)
                            .pulseMaxRadius(20)
                            .foregroundDrawable(R.drawable.baseline_location_on_48)
                            .build();

            locationComponent.applyStyle(locationComponentOptions);
            getUserLocation(); // Fetch initial user location
            startMapThread(); // Start updating the map based on user location
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getUserLocation() {
        UserLocationListener userLocationListener = new UserLocationListener();
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        ApplicationUtils.checkLocationPermission(this);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_INTERVAL, 3f, userLocationListener);
    }

    private void startMapThread() {
        if (mapThread == null) {
            mapThread = new MapThread();
            mapThread.start();
        }
    }

    private class MapThread extends Thread {
        @Override
        public void run() {
            while (isMapRunning) {
                try {
                    if (currentUserLocation != null) {
                        runOnUiThread(() -> updateMap(currentUserLocation));
                    }

                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateMap(Location location) {
        if (location.distanceTo(currentUserLocation) != 0) {
            currentUserLocation = location;
            mapboxMap.clear();

            CameraPosition position = new CameraPosition.Builder()
                    .target(new LatLng(currentUserLocation.getLatitude(), currentUserLocation.getLongitude()))
                    .zoom(17)
                    .build();

            Log.e("New Position", String.valueOf(currentUserLocation));
            mapboxMap.setCameraPosition(position);
        }
    }

    public class UserLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            currentUserLocation = location;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}