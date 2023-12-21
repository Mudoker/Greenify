package com.example.greenify.activity.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.app.ActivityCompat
import com.example.greenify.R
import com.example.greenify.util.ApplicationUtils
import com.example.greenify.util.Environment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.JsonObject
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.viewport.viewport

class MapBoxActivity : AppCompatActivity() {
    //Map
    private lateinit var mapView: MapView
    private lateinit var mapBoxMap: MapboxMap
    private lateinit var mapMiniView: LinearLayoutCompat

    // Response Code
    private val systemResponse = Environment.getSystemResponse()

    // User location
    private var userLocation: Point =
        Point.fromLngLat(Environment.getCurrentLng(), Environment.getCurrentLat())

    // Permission
    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isPermissionGranted ->
        if (isPermissionGranted) {
            Toast.makeText(this@MapBoxActivity, "Permission Granted!", Toast.LENGTH_SHORT).show()
        } else {
            ApplicationUtils.requestPermissions(this@MapBoxActivity)
        }
    }

    // Listener
    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener { bearing ->
        mapView.mapboxMap.setCamera(CameraOptions.Builder().bearing(bearing).build())
    }

    private val onIndicatorPositionChangedListener =
        OnIndicatorPositionChangedListener { point ->
            userLocation = point

            mapView.mapboxMap.setCamera(
                CameraOptions.Builder()
                    .center(point)
                    .zoom(17.0)
                    .build()
            )

            navigateCamera(userLocation)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_box)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            activityResultLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            activityResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        // Minimise map mini view
        val btnMinimise: TextView = findViewById(R.id.map_btn_minimise)

        mapMiniView = findViewById(R.id.map_mini_layout)

        btnMinimise.setOnClickListener {
            mapMiniView.visibility = View.GONE
        }

        // Re-navigate
        val btnReNavigate: FloatingActionButton = findViewById(R.id.map_btn_my_location)

        btnReNavigate.setOnClickListener {
            navigateCamera(userLocation)
        }

        mapView = findViewById(R.id.mapView)

        mapBoxMap = mapView.mapboxMap

        mapBoxMap.loadStyle(Style.MAPBOX_STREETS) {
            if (it.isStyleLoaded()) {
                navigateCamera(userLocation)
            } else {
                Toast.makeText(this, systemResponse.mapLoadFail(), Toast.LENGTH_SHORT).show()
            }
        }

        with(mapView) {
            location.locationPuck = createDefault2DPuck(withBearing = true)
            location.enabled = true
            location.puckBearing = PuckBearing.COURSE

            location.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
            location.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
            viewport.transitionTo(
                targetState = viewport.makeFollowPuckViewportState(),
                transition = viewport.makeImmediateViewportTransition()
            )
        }
    }

    private fun navigateCamera(point: Point) {
        val cameraCenterCoordinates =
            Point.fromLngLat(point.longitude(), point.latitude())

        val cameraOptions = CameraOptions.Builder()
            .center(cameraCenterCoordinates)
            .zoom(17.0)
            .build()

        val cameraAnimationOptions = MapAnimationOptions.Builder().duration(1000).build()

        mapView.mapboxMap.flyTo(cameraOptions, cameraAnimationOptions)

        addPointAnnotationToMap(
            Point.fromLngLat(
                Environment.getCurrentLng(),
                Environment.getCurrentLat()
            )
        )

        addPointAnnotationToMap(
            Point.fromLngLat(
                106.6648324106366,
                10.785226863255026
            )
        )
    }

    // Adds a marker to the map with coordinates
    private fun addPointAnnotationToMap(point: Point) {
        // Create a bitmap from drawable resource
        val locationIconBitmap = createBitmapFromDrawableRes(
            this@MapBoxActivity,
            R.drawable.baseline_location_on_48
        )

        // If bitmap creation is successful
        locationIconBitmap?.let {
            val annotationApi = mapView.annotations

            val pointAnnotationManager = annotationApi.createPointAnnotationManager()

            val annotationIdJsonObject = JsonObject()
            annotationIdJsonObject.addProperty("id", 123)

            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(Point.fromLngLat(point.longitude(), point.latitude()))
                .withIconImage(it)
                .withData(annotationIdJsonObject)

            pointAnnotationManager.addClickListener { annotation ->
                val annotationLocation = annotation.getData()

                mapMiniView.visibility = View.VISIBLE

                Log.e("Clicked Annotation", annotationLocation.toString())
                return@addClickListener true
            }

            pointAnnotationManager.create(pointAnnotationOptions)
        }
    }

    // Converts a drawable resource to a bitmap
    private fun createBitmapFromDrawableRes(
        context: Context,
        @DrawableRes resourceId: Int
    ): Bitmap? {
        // Convert the drawable resource to a bitmap
        return convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))
    }

    // Converts a drawable to a bitmap
    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        // Check if null
        if (sourceDrawable == null) {
            return null
        }

        // If already a BitmapDrawable
        if (sourceDrawable is BitmapDrawable) {
            return sourceDrawable.bitmap
        } else {
            // Create a new bitmap
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)

            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)

            return bitmap
        }
    }
}