package com.example.greenify.activity.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Button
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
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.viewport.viewport

class MapBoxActivity : AppCompatActivity() {
    //Map
    private lateinit var mapView: MapView
    private lateinit var mapBoxMap: MapboxMap
    private lateinit var mapMiniView: LinearLayoutCompat

    // Response Code
    private val systemResponse = Environment.getSystemResponse()

    // Annotation Id
    private var annotationId = "123"

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
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().bearing(bearing).build())
    }

    private val onIndicatorPositionChangedListener =
        OnIndicatorPositionChangedListener { point ->
            userLocation = point

            mapView.getMapboxMap().setCamera(
                CameraOptions.Builder()
                    .center(point)
                    .zoom(17.0)
                    .build()
            )
        }

    private val onMoveListener: OnMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            mapView.location.removeOnIndicatorBearingChangedListener(
                onIndicatorBearingChangedListener
            )
            mapView.location.removeOnIndicatorPositionChangedListener(
                onIndicatorPositionChangedListener
            )
            mapView.gestures.removeOnMoveListener(this)
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }

    // Update marker
    private lateinit var pointAnnotationManager: PointAnnotationManager

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

        // Add location
        val btnAddLocation: Button = findViewById(R.id.btn_add_location)

        btnAddLocation.setOnClickListener {
            addPointAnnotationToMap(
                Point.fromLngLat(
                    106.6648324106366,
                    10.785226863255026
                )
            )
        }

        // Update location
        val btnUpdateLocation: Button = findViewById(R.id.btn_update_location)
        btnUpdateLocation.setOnClickListener {
            updatePointAnnotationLocation(
                annotationId, Point.fromLngLat(
                    Environment.getCurrentLng(),
                    Environment.getCurrentLat()
                )
            )
        }

        mapView = findViewById(R.id.mapView)

        mapBoxMap = mapView.getMapboxMap()

        // CRUD Markers
        val annotationApi = mapView.annotations
        pointAnnotationManager = annotationApi.createPointAnnotationManager()

        mapBoxMap.loadStyleUri(Style.MAPBOX_STREETS) {
            if (it.isStyleLoaded) {
                navigateCamera(userLocation)
            } else {
                Toast.makeText(this, systemResponse.mapLoadFail(), Toast.LENGTH_SHORT).show()
            }
        }


        // User location puck
        with(mapView) {
            location.apply {
                enabled = true
                pulsingEnabled = true // Enable pulsing animation for the location puck
                pulsingColor = Color.BLUE // Set the pulsing color (optional)
                pulsingMaxRadius = 10f // Set the maximum radius of pulsing animation (optional)
                addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
                addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
            }

            gestures.addOnMoveListener(onMoveListener)

            viewport.transitionTo(
                targetState = viewport.makeFollowPuckViewportState(),
                transition = viewport.makeImmediateViewportTransition()
            )
        }

        // Request route
//        val btnRequestDirection: CardView = findViewById(R.id.map_direction)

    }

    private fun navigateCamera(point: Point) {
        val cameraCenterCoordinates =
            Point.fromLngLat(point.longitude(), point.latitude())

        val cameraOptions = CameraOptions.Builder()
            .center(cameraCenterCoordinates)
            .zoom(17.0)
            .build()

        val cameraAnimationOptions = MapAnimationOptions.Builder().duration(1000).build()

        mapView.getMapboxMap().flyTo(cameraOptions, cameraAnimationOptions)
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
            val annotationIdJsonObject = JsonObject()
            annotationIdJsonObject.addProperty("id", 123)

            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(Point.fromLngLat(point.longitude(), point.latitude()))
                .withIconImage(it)
                .withData(annotationIdJsonObject)

            pointAnnotationManager.addClickListener {
                mapMiniView.visibility = View.VISIBLE

                return@addClickListener true
            }

            pointAnnotationManager.create(pointAnnotationOptions)
        }
    }


    private fun updatePointAnnotationLocation(annotationId: String, newPoint: Point) {
        // Find the annotation with the given ID
        val annotationToDelete = pointAnnotationManager.annotations.firstOrNull {
            val annotationData = it.getData()?.asJsonObject
            val id = annotationData?.get("id")?.asString
            id == annotationId
        }

        // If the annotation is found, delete it
        annotationToDelete?.let {
            pointAnnotationManager.delete(it)
            addPointAnnotationToMap(newPoint)
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