package com.ynr.taximedriver.home

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.nkzawa.socketio.client.Socket
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.ynr.taximedriver.R
import com.ynr.taximedriver.config.KeyString
import com.ynr.taximedriver.config.Permission
import com.ynr.taximedriver.gps.GPSTracker
import com.ynr.taximedriver.home.dispatcher.DispatcherFragment
import com.ynr.taximedriver.home.driver.DriverFragment
import com.ynr.taximedriver.home.road_pickup.PassengerInfoActivity
import com.ynr.taximedriver.model.DriverState
import com.ynr.taximedriver.model.vehicalCategoryModel.VehicleCategoryResponseModel
import com.ynr.taximedriver.other.SocketReconnect
import com.ynr.taximedriver.other.VehicleSelecterPopup
import com.ynr.taximedriver.rest.ApiInterface
import com.ynr.taximedriver.rest.JsonApiClient
import com.ynr.taximedriver.session.LoginSession
import com.ynr.taximedriver.socket.MySocket
import kotlinx.android.synthetic.main.fragment_home.*
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment(), OnMapReadyCallback {
    private var gmap: GoogleMap? = null
    private var location: GPSTracker? = null
    private var latLng: LatLng? = null
    private var mSocket: Socket? = null


    private lateinit var session: LoginSession

    val states = arrayOf(intArrayOf(-android.R.attr.state_checked), intArrayOf(android.R.attr.state_checked))

    val thumbColors = intArrayOf(
        Color.RED,
        Color.GREEN
    )

    val trackColors = intArrayOf(
        Color.RED,
        Color.GREEN
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(requireContext())
        val socket = requireActivity().application as MySocket
        mSocket = socket.getmSocket()
    }

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        session = LoginSession(requireContext())
        /**
         * set status bar colour
         */
        val window = this.requireActivity().window // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS) // finally change the color
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.greenOne)
        if (session.isLoggedIn) {
            val userdata = session.userDetails.content
            userdata.firstName?.let {
                txtDriverName.text = it
            }
            userdata.profileImage?.let {
                Glide.with(this).load(userdata.profileImage).apply(RequestOptions().error(R.drawable.ic_user).placeholder(R.drawable.ic_user))
                    .into(imgDriveProfile)
            }

        }

        socket_state.setText(R.string.connecting)
        location = GPSTracker(context)
        latLng = LatLng(location!!.latitude, location!!.longitude)
        val mapViewBundle: Bundle? = savedInstanceState?.getBundle(MAP_VIEW_BUNDLE_KEY)
        // tripCount = myFragmentView.findViewById(R.id.trip_count)
        mapView.onCreate(mapViewBundle)
        mapView?.getMapAsync(this)
        //onlineSwitch = myFragmentView.findViewById(R.id.onlineSwitch)
        onlineSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->

            val permission = Permission(context, activity)
            if (!permission.isLocationPermissionGranted) {
                permission.checkPermissions()
                buttonView.isChecked = false
                return@OnCheckedChangeListener
            }
            if (socket_state.text != getString(R.string.connected)) {
                buttonView.isChecked = false
                return@OnCheckedChangeListener
            }
            if (session.driverState == KeyString.OFFLINE && session.vehicle == null) {
                val alertDialog = AlertDialog.Builder(context).create()
                alertDialog.setTitle("No Vehicle")
                alertDialog.setMessage("You have no selected vehicle")
                alertDialog.setButton(
                    AlertDialog.BUTTON_NEUTRAL, "OK"
                ) { dialog, which -> dialog.dismiss() }
                alertDialog.setButton(
                    AlertDialog.BUTTON_POSITIVE, "Select Vehicle"
                ) { dialog, which -> VehicleSelecterPopup(requireContext(), requireActivity()) }
                alertDialog.show()
                buttonView.isChecked = false
                return@OnCheckedChangeListener
            } else if (!LoginSession(context).driverEnable) {
                val alertDialog = AlertDialog.Builder(context).create()
                alertDialog.setTitle("Driver Disable")
                alertDialog.setMessage("your account temporally disable contact snap team")
                alertDialog.setButton(
                    AlertDialog.BUTTON_NEUTRAL, "OK"
                ) { dialog, _ -> dialog.dismiss() }
                alertDialog.show()
                buttonView.isChecked = false
                return@OnCheckedChangeListener
            }
            if (isChecked) {
                onlineSwitch.backDrawable = resources.getDrawable(R.drawable.switch_online_background)
                onlineSwitch.thumbDrawable = resources.getDrawable(R.drawable.ic_switch_thumb)
                session.driverState = KeyString.ONLINE
            } else {
                onlineSwitch.backDrawable = resources.getDrawable(R.drawable.switch_offline_background)
                onlineSwitch.thumbDrawable = resources.getDrawable(R.drawable.switch_offline_thumb)
                session.driverState = KeyString.OFFLINE
            }
            val state = DriverState(mSocket!!.id(), session.driverState, LoginSession(context)._Id)
//            if (!mSocket!!.connected()) SocketReconnect(context, mSocket)
            try {
                mSocket!!.emit(KeyString.UPDATE_DRIVER_STATE_SOCKET, JSONObject(Gson().toJson(state, DriverState::class.java)))
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            println("Driver State 01" + LoginSession(context).driverState)
        })
        if (LoginSession(context).driverState != KeyString.OFFLINE) {
            onlineSwitch.isChecked = true
            onlineSwitch.backDrawable = resources.getDrawable(R.drawable.switch_online_background)
            onlineSwitch.thumbDrawable = resources.getDrawable(R.drawable.ic_switch_thumb)
        }


        dispatcherButton.setOnClickListener {
            requireFragmentManager().beginTransaction()
                .replace(R.id.fragment, DispatcherFragment.newInstance()).commit()
        }
        driverButton.setOnClickListener {
            if (LoginSession(context).driverState != KeyString.ONLINE) {
                val alertDialog = AlertDialog.Builder(context).create()
                alertDialog.setTitle("Driver Offline")
                alertDialog.setMessage("please change driver state as online")
                alertDialog.setButton(
                    AlertDialog.BUTTON_NEUTRAL, "OK"
                ) { dialog, _ -> dialog.dismiss() }
                alertDialog.show()
            } else {
                requireFragmentManager().beginTransaction()
                    .replace(R.id.fragment, DriverFragment.newInstance()).commit()
            }
        }
        roadPickupButton.setOnClickListener(View.OnClickListener {
            if (LoginSession(context).vehicle == null) {
                val alertDialog = AlertDialog.Builder(context).create()
                alertDialog.setTitle("No Vehicle")
                alertDialog.setMessage("You have no available vehicle")
                alertDialog.setButton(
                    AlertDialog.BUTTON_NEUTRAL, "OK"
                ) { dialog, which -> dialog.dismiss() }
                alertDialog.setButton(
                    AlertDialog.BUTTON_POSITIVE, "Select Vehicle"
                ) { dialog, which -> VehicleSelecterPopup(requireContext(), requireActivity()) }
                alertDialog.show()
                return@OnClickListener
            } else if (LoginSession(context).driverState != KeyString.ONLINE) {
                val alertDialog = AlertDialog.Builder(context).create()
                alertDialog.setTitle("Driver Offline")
                alertDialog.setMessage("please change driver state as online")
                alertDialog.setButton(
                    AlertDialog.BUTTON_NEUTRAL, "OK"
                ) { dialog, _ -> dialog.dismiss() }
                alertDialog.show()
                return@OnClickListener
            }
            val intent = Intent(context, PassengerInfoActivity::class.java)
            startActivity(intent)
        })

        retrieveNotificationToken()

        sw_map.setOnCheckedChangeListener { buttonView, isChecked ->
            mapEnable(isChecked)
        }

        DrawableCompat.setTintList(DrawableCompat.wrap(sw_map.trackDrawable), ColorStateList(states, thumbColors))
    }

    fun setConnectionState(state: String) {
        socket_state.text = state
    }

    fun getConenctionState(): String {
        return socket_state.text.toString()
    }

    /**
     * navigate driver fragment
     * call home activity onResume
     */
    fun navigateDriver() {
        requireFragmentManager().beginTransaction()
            .replace(R.id.fragment, DriverFragment.newInstance()).commit()
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        val permission = Permission(context, activity)
        gmap = googleMap
        requireActivity().runOnUiThread(Runnable {
            gmap!!.setMinZoomPreference(1f)
            gmap!!.setMaxZoomPreference(20f)
            if (!permission.isLocationPermissionGranted) {
                permission.checkPermissions()
                return@Runnable
            }
            //gmap.setTrafficEnabled(true);
            gmap!!.isMyLocationEnabled = true
            latLng?.let { CameraUpdateFactory.newLatLngZoom(it, 16.0f) }
                ?.let { gmap!!.animateCamera(it) }
            gmap!!.setOnMyLocationButtonClickListener {
                latLng?.let { CameraUpdateFactory.newLatLngZoom(it, 16.0f) }
                    ?.let { gmap!!.moveCamera(it) }
                false
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle)
        }
        mapView?.onSaveInstanceState(mapViewBundle)
    }

    // Get new FCM registration token
    private fun retrieveNotificationToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FirebaseToken", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val token = task.result
                token?.let { updateNotificationToken(it) }
            })
    }

    private fun updateNotificationToken(token: String) {

        val json = JSONObject()
        json.put("token", token)
        json.put("mobile", session.userDetails.content.contactNo)

        val body = RequestBody.create(
            okhttp3.MediaType.parse(
                "application/json; charset=utf-8"
            ), json.toString()
        )

        val apiInterface = JsonApiClient.apiClient.create(ApiInterface::class.java)
        val call = apiInterface.updateNotificationToken(body)
        call.enqueue(object : Callback<VehicleCategoryResponseModel?> {
            override fun onResponse(call: Call<VehicleCategoryResponseModel?>, response: Response<VehicleCategoryResponseModel?>) {
                Log.i("FirebaseMessaging", response.code().toString())
            }

            override fun onFailure(call: Call<VehicleCategoryResponseModel?>, t: Throwable) {
                t.message?.let { Log.d("FirebaseMessaging", it) }

            }
        })

    }

    private fun mapEnable(isChecked: Boolean) {

        /*val states = arrayOf(intArrayOf(-android.R.attr.state_checked), intArrayOf(android.R.attr.state_checked))

        val thumbColors = intArrayOf(
            Color.RED,
            Color.GREEN
        )

        val trackColors = intArrayOf(
            Color.RED,
            Color.GREEN
        )*/


        if (isChecked) {
            mapView.visibility = View.VISIBLE
            DrawableCompat.setTintList(DrawableCompat.wrap(sw_map.thumbDrawable), ColorStateList(states, trackColors))
        } else {
            mapView.visibility = View.GONE
            DrawableCompat.setTintList(DrawableCompat.wrap(sw_map.trackDrawable), ColorStateList(states, thumbColors))
        }
    }

    override fun onResume() {
        mapView?.onResume()
        mapEnable(sw_map.isChecked)
        super.onResume()
    }

    override fun onStart() {
        mapView?.onStart()
        mapEnable(sw_map.isChecked)
        super.onStart()
    }

    override fun onStop() {
        mapView?.onStop()
        super.onStop()
    }

    override fun onPause() {
        mapView?.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapView?.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        mapView?.onLowMemory()
        super.onLowMemory()
    }

    companion object {
        private const val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"

        @JvmStatic
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}