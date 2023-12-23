package com.example.greenify.activity.map

//import com.mapbox.navigation.dropin.NavigationView

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greenify.R
import com.example.greenify.activity.adapter.MapSearchAdapter
import com.example.greenify.activity.event.EventDetailActivity
import com.example.greenify.activity.main.MainActivity
import com.example.greenify.model.EventModel
import com.example.greenify.util.ApplicationUtils
import com.example.greenify.util.Environment
import com.example.greenify.util.FirebaseAPIs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.GeoPoint
import com.google.gson.JsonObject
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.android.core.location.LocationEngineResult
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.Bearing
import com.mapbox.api.directions.v5.models.RouteOptions
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
import com.mapbox.navigation.dropin.NavigationView
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineOptions
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineResources
import com.mapbox.turf.TurfMeasurement
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException


class MapBoxActivity : AppCompatActivity(), MapSearchAdapter.OnItemClickListener {
    //Map
    private lateinit var mapView: MapView
    private lateinit var mapBoxMap: MapboxMap
    private lateinit var mapMiniView: LinearLayoutCompat
    private var allEvents: List<EventModel> = emptyList()

    //Search
    private lateinit var mapSearchView: RecyclerView
    private lateinit var mapSearchAdapter: MapSearchAdapter
    private var selectedFilter: String = ""
    private var selectedPhrase: String = ""

    //Miniview
    private lateinit var mapMiniTitle: TextView;
    private lateinit var mapMiniAddress: TextView;
    private lateinit var mapMiniDistance: TextView;
    private lateinit var selectedEventDetail: EventModel;

    // Response Code
    private val systemResponse = Environment.getSystemResponse()

    private var firebaseAPIs = FirebaseAPIs()

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
            CameraOptions.Builder().center(userLocation).zoom(17.0).build()
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

        override fun onMoveEnd(detector: MoveGestureDetector) {
            mapBoxNavigation.registerRoutesObserver(routesObserver)
            mapBoxNavigation.registerLocationObserver(locationObserver)
        }
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

    private val offRouteObserver = OffRouteObserver {}

    //Navigation
    private lateinit var mapBoxNavigation: MapboxNavigation
    private lateinit var btnNavigate: CardView

    // Simulation
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

        mapMiniView = findViewById(R.id.map_mini_layout)

        // Minimise map mini view
        val btnMinimise: TextView = findViewById(R.id.map_btn_minimise)
        val btnEventDetail: CardView = findViewById(R.id.map_mini_detail_view)
        // Request route
        btnNavigate = findViewById(R.id.map_direction)

        btnNavigate.setOnClickListener {
            val geoPoint = getLocationFromAddress(selectedEventDetail.location)
            if (geoPoint != null) {
                getRouteToPoint(Point.fromLngLat(geoPoint.longitude, geoPoint.latitude))
            }
        }

        mapMiniTitle = findViewById(R.id.mapbox_mini_event_title)
        mapMiniAddress = findViewById(R.id.mapbox_mini_event_address)
        mapMiniDistance = findViewById(R.id.mapbox_mini_event_distance)
        mapSearchView = findViewById(R.id.map_search_content)

        btnEventDetail.setOnClickListener {
            val intent = Intent(
                this@MapBoxActivity, EventDetailActivity::class.java
            )
            intent.putExtra("EVENT_MODEL", selectedEventDetail)

            // Use a coroutine to perform the background task
            lifecycleScope.launch {
                try {
                    // Use suspend function to get user data
                    firebaseAPIs.getUserDataById(selectedEventDetail.ownerId.toString(), {
                        // Update the intent with user data
                        intent.putExtra("HOST_NAME", it.email)
                    }, {

                    })

                    // Start the activity after the background task is complete
                    startActivity(intent)
                } catch (e: Exception) {
                    // Handle exceptions
                    Log.e("Firebase Error", "Error getting user data by ID: ${e.message}")
                }
            }
        }

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


        // Open search
        val btnSearch: FloatingActionButton = findViewById(R.id.map_btn_open_search)
        val btnMinimiseSearch: TextView = findViewById(R.id.map_btn_minimise_search)
        val searchBar: LinearLayout = findViewById(R.id.map_search_bar)
        val btnAll: AppCompatButton = findViewById(R.id.map_filter_all)
        val btnParks: AppCompatButton = findViewById(R.id.map_filter_park)
        val btnBeaches: AppCompatButton = findViewById(R.id.map_filter_beach)
        val btnStreets: AppCompatButton = findViewById(R.id.map_filter_street)
        val searchField: EditText = findViewById(R.id.map_search_edt)

        val handler = Handler(Looper.getMainLooper())
        val searchJob: Job? = null

        searchField.doOnTextChanged { _, _, _, _ ->
            // Cancel any existing job to ensure only the latest text change is processed
            searchJob?.cancel()

            // Post a delayed message to the handler
            handler.postDelayed({
                selectedPhrase = searchField.text.toString()
                mapSearchAdapter.setSelectedFilter(selectedPhrase, selectedFilter)
            }, 1000) // (1 second) delay
        }

        // Create a common click listener for all buttons
        val commonClickListener = View.OnClickListener { v: View ->
            // Reset the background tint of all buttons to gray
            btnAll.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    this@MapBoxActivity,
                    R.color.gray
                )
            )
            btnParks.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    this@MapBoxActivity,
                    R.color.gray
                )
            )
            btnBeaches.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    this@MapBoxActivity,
                    R.color.gray
                )
            )
            btnStreets.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    this@MapBoxActivity,
                    R.color.gray
                )
            )

            // Set the background tint of the clicked button to white
            (v as Button).backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    this@MapBoxActivity,
                    android.R.color.white
                )
            )

            // Update the selected category based on the clicked button
            selectedFilter = if (v.getId() == R.id.map_filter_all) {
                "All"
            } else if (v.getId() == R.id.map_filter_park) {
                "Park"
            } else if (v.getId() == R.id.map_filter_beach) {
                "Beach"
            } else if (v.getId() == R.id.map_filter_street) {
                "Street"
            } else {
                "All" // Default to "All" if an unknown button is clicked
            }
            mapSearchAdapter.setSelectedFilter(selectedPhrase, selectedFilter)
        }

        // Set the click listener for all buttons
        btnAll.setOnClickListener(commonClickListener)
        btnParks.setOnClickListener(commonClickListener)
        btnBeaches.setOnClickListener(commonClickListener)
        btnStreets.setOnClickListener(commonClickListener)


        mapBoxMap.loadStyleUri(Style.MAPBOX_STREETS) {
            if (it.isStyleLoaded) {
                navigateCamera(userLocation, 0.0)
                lifecycleScope.launch {
                    firebaseAPIs.getAllEventsData({ eventList ->
                        try {
                            allEvents = eventList
                            mapSearchAdapter = MapSearchAdapter(eventList)
                            mapSearchAdapter.setOnItemClickListener(this@MapBoxActivity)

                            val linearLayoutManager = LinearLayoutManager(this@MapBoxActivity)

                            val itemDecoration =
                                DividerItemDecoration(
                                    this@MapBoxActivity,
                                    DividerItemDecoration.VERTICAL
                                )

                            mapSearchView.layoutManager = linearLayoutManager
                            mapSearchView.adapter = mapSearchAdapter
                            mapSearchView.addItemDecoration(itemDecoration)

                            eventList.forEach { event ->
                                val geoPoint = getLocationFromAddress(event.location)
                                if (geoPoint != null) {
                                    addPointAnnotationToMap(
                                        Point.fromLngLat(
                                            geoPoint.longitude, geoPoint.latitude
                                        ), event.id.toString()
                                    )
                                }
                            }

                            btnAll.performClick()
                        } catch (e: Exception) {
                            Log.e("Error converting", e.toString())
                            allEvents = emptyList()
                        }
                    }, { e ->
                        e.printStackTrace()
                        allEvents = emptyList()
                    })
                }

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
        mapBoxNavigation.unregisterOffRouteObserver(offRouteObserver)

        mapBoxNavigation.startTripSession()

        // Set up navigation sdk
        if (!MapboxNavigationApp.isSetup()) {
            MapboxNavigationApp.setup {
                NavigationOptions.Builder(this).accessToken(getString(R.string.mapbox_access_token))
                    .build()
            }
        }

        btnSearch.setOnClickListener {
            searchBar.visibility = View.VISIBLE
            btnSearch.visibility = View.GONE
            mapSearchView.visibility = View.VISIBLE
        }

        btnMinimiseSearch.setOnClickListener {
            searchBar.visibility = View.GONE
            mapSearchView.visibility = View.GONE
            btnSearch.visibility = View.VISIBLE
        }

        // Close view
        val btnCloseView: TextView = findViewById(R.id.btn_close_view)
        btnCloseView.setOnClickListener {
            intent = Intent(this@MapBoxActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun getLocationFromAddress(strAddress: String?): GeoPoint? {
        val coder = Geocoder(this)
        val address: List<Address>?
        try {
            address = coder.getFromLocationName(strAddress!!, 5)
            if (address == null) {
                return null
            }
            val location: Address = address[0]
            location.latitude
            location.longitude
            Log.e("Location Raw", location.toString())
            return GeoPoint(
                (location.latitude), (location.longitude)
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
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
                        this@MapBoxActivity, "System Error", "Failed to Get routes"
                    )
                }

                override fun onRoutesReady(
                    routes: List<NavigationRoute>, routerOrigin: RouterOrigin
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
    private fun calculateDistance(destination: Point): Double {
        val distance = TurfMeasurement.distance(userLocation, destination)
        return String.format("%.2f", distance).toDouble()
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
    private fun addPointAnnotationToMap(point: Point, id: String) {
        Log.e("On create annotation", id)

        // Create a bitmap from drawable resource
        val locationIconBitmap = createBitmapFromDrawableRes(
            this@MapBoxActivity, R.drawable.baseline_location_on_48
        )

        // CRUD Markers
        val annotationApi = mapView.annotations
        pointAnnotationManager = annotationApi.createPointAnnotationManager()

        // If bitmap creation is successful
        locationIconBitmap?.let {
            val annotationIdJsonObject = JsonObject()
            annotationIdJsonObject.addProperty("id", id)

            val pointAnnotationOptions = PointAnnotationOptions().withPoint(
                Point.fromLngLat(
                    point.longitude(), point.latitude()
                )
            ).withIconImage(it).withData(annotationIdJsonObject)

            pointAnnotationManager.addClickListener {
                mapMiniView.visibility = View.VISIBLE
                val foundEvent = allEvents.find { event -> event.id.toString() == id }

                if (foundEvent != null) {
                    mapMiniTitle.text = foundEvent.title
                    mapMiniAddress.text = foundEvent.location

                    val distanceText = calculateDistance(point).toString() + " km"
                    mapMiniDistance.text = distanceText
                    selectedEventDetail = foundEvent
                }
                return@addClickListener true
            }

            pointAnnotationManager.create(pointAnnotationOptions)
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

    override fun onItemClick(selectedEventLocation: String?) {
        val geoPoint = getLocationFromAddress(selectedEventLocation)
        if (geoPoint != null) {
            getRouteToPoint(Point.fromLngLat(geoPoint.longitude, geoPoint.latitude))
        }
    }
}