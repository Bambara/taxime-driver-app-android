package com.ynr.taximedriver.common

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.ynr.taximedriver.R
import kotlinx.android.synthetic.main.activity_location_picker.*
import java.io.IOException
import java.util.*

class LocationPickerActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener {
    companion object {
        private val TAG = LocationPickerActivity::class.java.simpleName
        private const val DEFAULT_ZOOM = 15
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val KEY_LOCATION = "location"
        const val SELECTED_PLACE = "selectedPlace"
        private const val AUTOCOMPLETE_REQUEST_CODE = 1
    }

    private var map: GoogleMap? = null
    private var cameraPosition: CameraPosition? = null

    // The entry point to the Places API.
    private lateinit var placesClient: PlacesClient

    // The entry point to the Fused Location Provider.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private val defaultLocation = LatLng(-33.8523341, 151.2106085)
    private var locationPermissionGranted = false

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private var lastKnownLocation: Location? = null
    private var selectedPlace: Place? = null
    private var selectedLocationMarker: Marker? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION)
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
        }
        setContentView(R.layout.activity_location_picker)
//        Places.initialize(applicationContext, MAPS_API_KEY)
        Places.initialize(
            applicationContext,
            applicationContext.packageManager.getApplicationInfo(
                applicationContext.packageName,
                PackageManager.GET_META_DATA
            ).metaData["MAPS_API_KEY"].toString()
        )
        placesClient = Places.createClient(this)
        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        // Build the map.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = getString(R.string.pick_a_location)
        constraintLayout12.visibility = View.GONE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        map?.let { map ->
            outState.putParcelable(KEY_CAMERA_POSITION, map.cameraPosition)
            outState.putParcelable(KEY_LOCATION, lastKnownLocation)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        map.setOnMapClickListener(this)
        getLocationPermission()
        updateLocationUI()
        getDeviceLocation()
        showCurrentPlace()
//        progressBar.visibility = View.GONE
    }

    override fun onMapClick(p0: LatLng) {
        p0.let {
            selectedLocationMarker?.remove()
            val markerOptions = MarkerOptions()
                .position(it)
            selectedLocationMarker = map?.addMarker(markerOptions)
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(it, DEFAULT_ZOOM.toFloat()))
            getAddressFromLocation(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.location_picker_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.searchLocation -> {
                val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
                // Start the autocomplete intent.
                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                        .setCountry("LK")
                        .build(this)
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)

            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                }
            }
        }
        updateLocationUI()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        selectedPlace = Autocomplete.getPlaceFromIntent(data)
                        finishWithSelectedLocation()
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.i(TAG, status.statusMessage.toString())
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    // User Actions
    fun useLocationBtnClicked (view: View) {
        finishWithSelectedLocation()
    }
    private fun getAddressFromLocation(latLang: LatLng) {
        val geoCoder = Geocoder(this, Locale.ENGLISH)
        try {
            val addresses: List<Address> =
                geoCoder.getFromLocation(latLang.latitude, latLang.longitude, 1) as List<Address>
            if (addresses.isNotEmpty()) {
                val fetchedAddress = addresses[0]
                val strAddress = StringBuilder()
                for (i in 0 until fetchedAddress.maxAddressLineIndex + 1) {
                    strAddress.append(fetchedAddress.getAddressLine(i)).append(" ")
                }
                addressTv.text = strAddress.toString()
                val builder = Place.builder()
                builder.latLng = latLang
                builder.address = strAddress.toString()
                builder.name = ""
                selectedPlace = builder.build()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    @SuppressLint("MissingPermission")
    private fun showCurrentPlace() {
        if (map == null) {
            return
        }
        if (locationPermissionGranted) {
            // Use fields to define the data types to return.
            val placeFields = listOf(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
            // Use the builder to create a FindCurrentPlaceRequest.
            val request = FindCurrentPlaceRequest.newInstance(placeFields)
            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            val placeResult = placesClient.findCurrentPlace(request)
            placeResult.addOnCompleteListener { task ->
                constraintLayout12.visibility = View.VISIBLE
                if (task.isSuccessful && task.result != null) {
                    val likelyPlaces = task.result
                    likelyPlaces?.placeLikelihoods?.let {
                        this.selectedPlace = it.first().place
                        if (selectedPlace?.name?.isEmpty() == false) {
                            addressTv.text = selectedPlace?.address
                        } else {
                            addressTv.text = selectedPlace?.name
                        }
                    }
                } else {
                    Log.e(TAG, "Exception: %s", task.exception)
                }
            }
        } else {
            // The user has not granted permission.
            Log.i(TAG, "The user did not grant location permission.")
            getLocationPermission()
        }
    }

    private fun finishWithSelectedLocation() {
        this.selectedPlace?.let {
            val data = Intent()
            data.putExtra(SELECTED_PLACE, it)
            setResult(RESULT_OK, data)
            finish()
        }
    }

    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    LatLng(lastKnownLocation!!.latitude,
                                            lastKnownLocation!!.longitude), DEFAULT_ZOOM.toFloat()))
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        map?.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat()))
                        map?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                map?.isMyLocationEnabled = true
                map?.uiSettings?.isMyLocationButtonEnabled = true
            } else {
                map?.isMyLocationEnabled = false
                map?.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
}
