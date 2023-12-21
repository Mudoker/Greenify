package com.example.greenify.activity.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.example.greenify.R
import com.example.greenify.util.Environment
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager

class MapBoxActivity : AppCompatActivity() {
    //Map

    private lateinit var mapView: MapView
    private lateinit var mapBoxMap: MapboxMap

    private val systemResponse = Environment.getSystemResponse()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_box)

        mapView = findViewById(R.id.mapView)

        mapBoxMap = mapView.mapboxMap

        mapBoxMap.loadStyle(Style.MAPBOX_STREETS) {
            if (it.isStyleLoaded()) {
                navigateCamera()
            } else {
                Toast.makeText(this, systemResponse.mapLoadFail(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateCamera() {
        val cameraCenterCoordinates =
            Point.fromLngLat(Environment.getCurrentLng(), Environment.getCurrentLat())

        val cameraOptions = CameraOptions.Builder()
            .center(cameraCenterCoordinates)
            .zoom(17.0)
            .build()

        val cameraAnimationOptions = MapAnimationOptions.Builder().duration(15000).build()

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

            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(Point.fromLngLat(point.longitude(), point.latitude()))
                .withIconImage(it)

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