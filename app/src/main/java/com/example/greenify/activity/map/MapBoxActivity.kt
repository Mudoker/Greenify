package com.example.greenify.activity.map

//import com.mapbox.navigation.dropin.NavigationView
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import com.example.greenify.R
import com.example.greenify.util.ApplicationUtils
import com.example.greenify.util.Environment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.JsonObject
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.Bearing
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.api.geocoding.v5.GeocodingCriteria
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
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
import com.mapbox.maps.plugin.locationcomponent.LocationComponentConstants
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.viewport.viewport
import com.mapbox.navigation.base.extensions.applyDefaultNavigationOptions
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.base.route.NavigationRoute
import com.mapbox.navigation.base.route.NavigationRouterCallback
import com.mapbox.navigation.base.route.RouterFailure
import com.mapbox.navigation.base.route.RouterOrigin
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.directions.session.RoutesObserver
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.core.trip.session.OffRouteObserver
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import com.mapbox.navigation.dropin.NavigationView
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineOptions
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineResources
import com.mapbox.turf.TurfMeasurement
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

    // Location
    private val navigationLocationProvider: NavigationLocationProvider =
        NavigationLocationProvider()

    private lateinit var mapBoxRouteLineApi: MapboxRouteLineApi

    private lateinit var mapBoxRouteLineView: MapboxRouteLineView

    // Permission
    private val activityResultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isPermissionGranted ->
        if (isPermissionGranted) {
            try {
                val locationEngine: LocationEngine =
                    LocationEngineProvider.getBestLocationEngine(this)
                locationEngine.getLastLocation(object :
                    LocationEngineCallback<LocationEngineResult> {
                    override fun onSuccess(result: LocationEngineResult) {
                        val lastLocation = result.lastLocation
                        if (lastLocation != null) {
                            userLocation =
                                Point.fromLngLat(lastLocation.longitude, lastLocation.latitude)
                        }
                    }

                    override fun onFailure(exception: Exception) {
                        // Handle the failure
                    }
                })
            } catch (securityException: SecurityException) {
                ApplicationUtils.requestPermissions(this@MapBoxActivity)
            }

            Toast.makeText(this@MapBoxActivity, "Permission Granted!", Toast.LENGTH_SHORT).show()
        } else {
            ApplicationUtils.requestPermissions(this@MapBoxActivity)
        }
    }

    // Listener
    private var onFocusLocation = true

    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener { bearing ->
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().bearing(bearing).build())
    }

    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener { point ->
        userLocation = point

        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder().center(point).zoom(17.0).build()
        )
    }

    private val onMoveListener: OnMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onFocusLocation = false
            // Stop sticking on the user location
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

    private val locationObserver: LocationObserver = object : LocationObserver {
        override fun onNewLocationMatcherResult(locationMatcherResult: LocationMatcherResult) {
            val location: Location = locationMatcherResult.enhancedLocation
            navigationLocationProvider.changePosition(location, locationMatcherResult.keyPoints)

            if (onFocusLocation) {
                navigateCamera(
                    Point.fromLngLat(location.longitude, location.latitude),
                    location.bearing.toDouble()
                )
            }
        }

        override fun onNewRawLocation(rawLocation: Location) {
        }

    }

    private val routesObserver: RoutesObserver = RoutesObserver { result ->
        mapBoxRouteLineApi.setNavigationRoutes(
            result.navigationRoutes
        ) { routeLineErrorRouteSetValueExpected ->
            val style = mapBoxMap.getStyle()

            if (style != null) {
                mapBoxRouteLineView.renderRouteDrawData(
                    style, routeLineErrorRouteSetValueExpected
                )
            }
        }
    }

    private val routeProgressObserver =
        RouteProgressObserver { routeProgress ->
            routeProgress.currentState.let { currentState ->
                val state = currentState
            }
        }

    private val offRouteObserver = OffRouteObserver {
        // do something when the off route state changes
    }

    //Navigation
    private lateinit var mapBoxNavigation: MapboxNavigation
    private lateinit var btnNavigate: CardView

    // Simulation
    private lateinit var btnSimulateRoute: CardView
    private lateinit var simulationView: NavigationView

    // Update marker
    private lateinit var pointAnnotationManager: PointAnnotationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_box)

        ApplicationUtils.configureWindowInsets(this)

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            activityResultLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
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
            navigateCamera(userLocation, 0.0)
        }

        mapView = findViewById(R.id.mapView)

        mapBoxMap = mapView.getMapboxMap()

        // CRUD Markers
        val annotationApi = mapView.annotations
        pointAnnotationManager = annotationApi.createPointAnnotationManager()

        mapBoxMap.loadStyleUri(Style.MAPBOX_STREETS) {
            if (it.isStyleLoaded) {
                navigateCamera(userLocation, 0.0)
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

        // Routing
        val mapBoxRouteLineOptions: MapboxRouteLineOptions = MapboxRouteLineOptions.Builder(this)
            .withRouteLineResources(RouteLineResources.Builder().build())
            .withRouteLineBelowLayerId(LocationComponentConstants.LOCATION_INDICATOR_LAYER).build()

        mapBoxRouteLineView = MapboxRouteLineView(mapBoxRouteLineOptions)
        mapBoxRouteLineApi = MapboxRouteLineApi(mapBoxRouteLineOptions)

        val navigationOptions: NavigationOptions =
            NavigationOptions.Builder(this).accessToken(getString(R.string.mapbox_access_token))
                .build()

        MapboxNavigationApp.setup(navigationOptions)
        mapBoxNavigation = MapboxNavigation(navigationOptions)
        mapBoxNavigation.registerRoutesObserver(routesObserver)
        mapBoxNavigation.registerLocationObserver(locationObserver)
        mapBoxNavigation.registerRouteProgressObserver(routeProgressObserver)
        mapBoxNavigation.unregisterOffRouteObserver(offRouteObserver)

        mapBoxNavigation.startTripSession()

        // Set up navigation sdk
        if (!MapboxNavigationApp.isSetup()) {
            MapboxNavigationApp.setup {
                NavigationOptions.Builder(this).accessToken(getString(R.string.mapbox_access_token))
                    .build()
            }
        }

        // Request route
        btnNavigate = findViewById(R.id.map_direction)

        btnNavigate.setOnClickListener {
            getRouteToPoint(Point.fromLngLat(106.69805298180039, 10.772695965238423))
        }

        // Open search
        val btnSearch: FloatingActionButton = findViewById(R.id.map_btn_open_search)
        val btnMinimiseSearch: TextView = findViewById(R.id.map_btn_minimise_search)
        val searchBar: LinearLayout = findViewById(R.id.map_search_bar)

        btnSearch.setOnClickListener {
            searchBar.visibility = View.VISIBLE
            btnSearch.visibility = View.GONE
        }

        btnMinimiseSearch.setOnClickListener {
            searchBar.visibility = View.GONE
            btnSearch.visibility = View.VISIBLE
        }
    }

    // Get route
    private fun getRouteToPoint(destination: Point) {
        val routeOptionsBuilder: RouteOptions.Builder = RouteOptions.builder()
        routeOptionsBuilder.coordinatesList(listOf(userLocation, destination))
        routeOptionsBuilder.alternatives(false)
        routeOptionsBuilder.profile(DirectionsCriteria.PROFILE_DRIVING)
        routeOptionsBuilder.bearingsList(
            listOf(
                Bearing.builder().degrees(45.0).build(), null
            )
        )

        routeOptionsBuilder.applyDefaultNavigationOptions()

        // Assuming mapBoxNavigation is your Mapbox Navigation instance
        mapBoxNavigation.requestRoutes(
            routeOptionsBuilder.build(),
            object : NavigationRouterCallback {
                override fun onFailure(reasons: List<RouterFailure>, routeOptions: RouteOptions) {
                    ApplicationUtils.showDialog(
                        this@MapBoxActivity,
                        "System Error",
                        "Failed to Get routes"
                    )
                }

                override fun onRoutesReady(
                    routes: List<NavigationRoute>,
                    routerOrigin: RouterOrigin
                ) {
                    mapBoxNavigation.setNavigationRoutes(routes)
//                    val navigationRoute = routes.first()

                    try {
                        simulationView.visibility = View.VISIBLE
                        simulationView.customizeViewOptions {
                            enableMapLongClickIntercept = false
                        }

                        // This allows simulating your location
                        simulationView.api.routeReplayEnabled(true)
                        simulationView.api.startActiveGuidance(routes)
                    } catch (e: Exception) {
                        Log.e("Simulation error", e.toString())
                        e.printStackTrace() // Handle the exception appropriately, for example, log it
                    }

                }

                override fun onCanceled(routeOptions: RouteOptions, routerOrigin: RouterOrigin) {
                }
            })

    }

    // Calculate distance between two points
    private fun calculateDistance(origin: Point, destination: Point): Double {
        return TurfMeasurement.distance(userLocation, destination)
    }

    // Convert from Point to address
    private fun getReverseGeoCoding(point: Point): String {
        var resultAddress = ""
        val reverseGeocode = MapboxGeocoding.builder()
            .accessToken(getString(R.string.mapbox_access_token))
            .query(point)
            .geocodingTypes(GeocodingCriteria.TYPE_POI)
            .build()

        reverseGeocode.enqueueCall(object : Callback<GeocodingResponse> {
            override fun onResponse(
                call: Call<GeocodingResponse>,
                response: Response<GeocodingResponse>
            ) {

                if (response.isSuccessful) {
                    val results = response.body()?.features()

                    if (!results.isNullOrEmpty()) {
                        val firstResultPlaceName = results[0].placeName()
                        if (firstResultPlaceName != null) {
                            resultAddress = firstResultPlaceName
                            Log.e("Route Destination: ", firstResultPlaceName)
                        }
                    } else {
                        Log.e(
                            "Route Destination: ",
                            "No address found. Response: ${response.body()}"
                        )
                    }
                } else {
                    Log.e(
                        "Route Destination: ",
                        "Request failed with code ${response.code()}. Response: ${
                            response.errorBody()?.string()
                        }"
                    )
                }
            }

            override fun onFailure(
                call: Call<GeocodingResponse>,
                throwable: Throwable
            ) {
                throwable.printStackTrace()
            }
        })

        return resultAddress
    }

    // Convert address to point
    private fun getGeocoding(stringQuery: String): Point {
        var resultPoint: Point = Point.fromLngLat(0.0, 0.0)

        val geocode = MapboxGeocoding.builder()
            .accessToken(getString(R.string.mapbox_access_token))
            .query(stringQuery)
            .geocodingTypes(GeocodingCriteria.TYPE_POI)
            .build()

        geocode.enqueueCall(object : Callback<GeocodingResponse> {
            override fun onResponse(
                call: Call<GeocodingResponse>,
                response: Response<GeocodingResponse>
            ) {
                if (response.isSuccessful) {
                    val results = response.body()?.features()

                    if (!results.isNullOrEmpty()) {
                        val firstResultPoint = results[0].center()
                        if (firstResultPoint != null) {
                            resultPoint = firstResultPoint
                            Log.e("Route Destination: ", firstResultPoint.toString())
                        }
                    } else {
                        Log.e(
                            "Route Destination: ",
                            "No coordinates found for $stringQuery. Response: ${response.body()}"
                        )
                    }
                } else {
                    Log.e(
                        "Route Destination: ",
                        "Request failed with code ${response.code()}. Response: ${
                            response.errorBody()?.string()
                        }"
                    )
                }
            }

            override fun onFailure(
                call: Call<GeocodingResponse>,
                throwable: Throwable
            ) {
                throwable.printStackTrace()
            }
        })

        return resultPoint
    }

    private fun navigateCamera(point: Point, bearing: Double) {
        val cameraCenterCoordinates = Point.fromLngLat(point.longitude(), point.latitude())

        val cameraOptions =
            CameraOptions.Builder().center(cameraCenterCoordinates).zoom(17.0).bearing(bearing)
                .padding(EdgeInsets(1000.0, 0.0, 0.0, 0.0)).build()

        val cameraAnimationOptions = MapAnimationOptions.Builder().duration(1000).build()

        mapView.getMapboxMap().flyTo(cameraOptions, cameraAnimationOptions)
    }

    // Adds a marker to the map with coordinates
    private fun addPointAnnotationToMap(point: Point) {
        // Create a bitmap from drawable resource
        val locationIconBitmap = createBitmapFromDrawableRes(
            this@MapBoxActivity, R.drawable.baseline_location_on_48
        )

        // If bitmap creation is successful
        locationIconBitmap?.let {
            val annotationIdJsonObject = JsonObject()
            annotationIdJsonObject.addProperty("id", 123)

            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions().withPoint(
                Point.fromLngLat(
                    point.longitude(), point.latitude()
                )
            ).withIconImage(it).withData(annotationIdJsonObject)

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
        context: Context, @DrawableRes resourceId: Int
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
                drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)

            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)

            return bitmap
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mapBoxNavigation.unregisterRoutesObserver(routesObserver)
        mapBoxNavigation.unregisterLocationObserver(locationObserver)
        mapBoxNavigation.onDestroy()
    }
}