package com.ynr.taximedriver.home.driver;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.ynr.taximedriver.R;
import com.ynr.taximedriver.background.LocationMonitoringService;
import com.ynr.taximedriver.config.KeyString;
import com.ynr.taximedriver.config.Permission;
import com.ynr.taximedriver.gps.GPSTracker;
import com.ynr.taximedriver.model.DriverState;
import com.ynr.taximedriver.model.Location;
import com.ynr.taximedriver.model.mapDistanceModel.MapDistanceModel;
import com.ynr.taximedriver.model.tripAcceptModel.TripAcceptResponseModel;
import com.ynr.taximedriver.model.tripCancelModel.DispatchTripCancelRequestModel;
import com.ynr.taximedriver.model.tripCancelModel.PassengerTripCancelRequestModel;
import com.ynr.taximedriver.model.tripModel.TripModel;
import com.ynr.taximedriver.rest.ApiInterface;
import com.ynr.taximedriver.rest.JsonApiClient;
import com.ynr.taximedriver.rest.mapApiClient;
import com.ynr.taximedriver.session.LoginSession;
import com.ynr.taximedriver.session.TripSession;
import com.ynr.taximedriver.socket.MySocket;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OnTripActivity extends AppCompatActivity implements OnMapReadyCallback {
    TripSession tripSession;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private GoogleMap gmap;
    private MapView mapView;
    LatLng latLng;
    private Polyline polyLine;
    private Marker pickupMarker;
    private ImageView showMore;
    private ConstraintLayout cancelBottomSheet, navigateButton, tripInfo;
    private TripModel tripModel;
    private TripAcceptResponseModel tripPriceModel;
    private TextView date;
    private TextView distance, tvPickupAddress, tvDropAddress;
    private TextView hireDistance;
    private TextView passengerDistance;
    private TextView cancelTrip;
    private Button button, cancelButton;
    protected PowerManager.WakeLock mWakeLock;
    private com.github.nkzawa.socketio.client.Socket mSocket;
    private double lastLongitude, lastLatitude, waitTimeMillis = 0.0, totalTimeMillis = 0.0, waitedDistance = 0.0;
    private Runnable distanceTimer;
    private final Handler handler = new Handler();
    double totalDistanceInMeters = 0.0, prevDistance = 0.0;
    ProgressDialog progressDialog;
    private double latitude, longitude;
    private String address;
    private boolean checker = false;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_trip);
        /* This code together with the one in onDestroy()
         * will make the screen be always on until this Activity gets destroyed. */
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();
        /**
         * set status bar colour
         */
        Window window = getWindow();   // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);  // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);   // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.transparentAsh));

        GPSTracker location = new GPSTracker(getApplicationContext());
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        address = location.getAddressLine(getApplicationContext());

        LocalBroadcastManager.getInstance(OnTripActivity.this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        latitude = intent.getDoubleExtra(KeyString.EXTRA_LATITUDE, 0);
                        longitude = intent.getDoubleExtra(KeyString.EXTRA_LONGITUDE, 0);
                        address = intent.getStringExtra(KeyString.EXTRA_ADDRESS);
                    }
                }, new IntentFilter(LocationMonitoringService.ACTION_LOCATION_BROADCAST)
        );

        tripSession = new TripSession(getApplicationContext());

        MySocket socket = (MySocket) getApplication();
        mSocket = socket.getmSocket();

        button = findViewById(R.id.button);
        button.setVisibility(View.VISIBLE);
        showMore = findViewById(R.id.iv_showMore);
        cancelButton = findViewById(R.id.cancel_button);
        tripInfo = findViewById(R.id.trip_info);
        cancelBottomSheet = findViewById(R.id.cancelBottomSheet);
        TextView yesButton = findViewById(R.id.btnYesCancel);
        TextView noButton = findViewById(R.id.btnNoCancel);


        if (!tripSession.isOnTrip()) {
            Bundle bundle = getIntent().getExtras();
            String json = bundle.getString(KeyString.TRIP_MODEL);
            String json1 = bundle.getString(KeyString.TRIP_PRICE_MODEL);
            Gson gson = new Gson();
            tripModel = gson.fromJson(json, TripModel.class);
            tripPriceModel = gson.fromJson(json1, TripAcceptResponseModel.class);

            LoginSession session = new LoginSession(getApplicationContext());
            session.setDriverState(KeyString.GOING_TO_PICKUP);
            DriverState state = new DriverState(mSocket.id(), session.getDriverState(), new LoginSession(getApplicationContext()).get_Id());
            try {
                mSocket.emit(KeyString.UPDATE_DRIVER_STATE_SOCKET, new JSONObject(new Gson().toJson(state, DriverState.class)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            tripModel = tripSession.getTripModel();
            tripPriceModel = tripSession.getTripPriceModel();
            lastLongitude = tripSession.getLastLongitude();
            lastLatitude = tripSession.getLastLatitude();
            waitTimeMillis = tripSession.getWaitTimeMillis();
            totalTimeMillis = tripSession.getTotalTimeMillis();
            totalDistanceInMeters = tripSession.getTotalDistance();
            prevDistance = tripSession.getPrevDistance();
            DriverState state = new DriverState(mSocket.id(), tripSession.getDriverState(), new LoginSession(getApplicationContext()).get_Id());
            new LoginSession(getApplicationContext()).setDriverState(tripSession.getDriverState());
            boolean checker = true;
            while (checker) {
                if (mSocket.connected()) {
                    try {
                        mSocket.emit(KeyString.UPDATE_DRIVER_STATE_SOCKET, new JSONObject(new Gson().toJson(state, DriverState.class)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    checker = false;
                }
            }
            if (tripSession.getDriverState().equals(KeyString.GOING_TO_PICKUP)) {
                button.setText(getResources().getString(R.string.start));
                button.setBackground(ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.green_view_background_with_white_redious_boder));
            } else {
                button.setText(getResources().getString(R.string.end));
                button.setBackground(ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.red_view_background_with_redious_conner));
                cancelButton.setVisibility(View.GONE);
                setTimer(tripSession.getLastLatitude(), tripSession.getLastLongitude());
            }
        }
        if (tripModel == null) return;
        setData(tripModel);
        cancelButton.setOnClickListener(v -> {
            tripInfo.setVisibility(View.GONE);
            navigateButton.setVisibility(View.GONE);
            cancelBottomSheet.setVisibility(View.VISIBLE);
        });
        yesButton.setOnClickListener(view -> {
            if (tripModel.getType().equals(KeyString.PASSENGER_TRIP)) {
                cancelPassengerTrip();
            } else {
                cancelDispatchTrip();
            }
        });
        noButton.setOnClickListener(view -> {
            cancelBottomSheet.setVisibility(View.GONE);
            tripInfo.setVisibility(View.VISIBLE);
            navigateButton.setVisibility(View.VISIBLE);
        });
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
        showMore.setOnClickListener(view -> {
            if (button.getText().toString().equals(getResources().getString(R.string.end))) {
                distance.setVisibility(View.GONE);
                date.setVisibility(View.GONE);
                hireDistance.setVisibility(View.GONE);
                passengerDistance.setVisibility(View.GONE);
                cancelButton.setVisibility(View.GONE);
                cancelTrip.setVisibility(View.GONE);
            } else {
                if (showMore.getTag().toString().equals("0")) {
                    distance.setVisibility(View.VISIBLE);
                    date.setVisibility(View.VISIBLE);
                    hireDistance.setVisibility(View.VISIBLE);
                    passengerDistance.setVisibility(View.VISIBLE);
                    cancelButton.setVisibility(View.VISIBLE);
                    cancelTrip.setVisibility(View.VISIBLE);
                    showMore.setTag(1);
                    showMore.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.down_arrow_icon));

                } else if (showMore.getTag().toString().equals("1")) {
                    distance.setVisibility(View.GONE);
                    date.setVisibility(View.GONE);
                    hireDistance.setVisibility(View.GONE);
                    passengerDistance.setVisibility(View.GONE);
                    cancelButton.setVisibility(View.GONE);
                    cancelTrip.setVisibility(View.GONE);
                    showMore.setTag(0);
                    showMore.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.up_arrow_icon));
                }
            }
        });
        navigateButton = findViewById(R.id.navigate_button);
        navigateButton.setOnClickListener(v -> {
            if (new LoginSession(getApplicationContext()).getDriverState().equals(KeyString.GOING_TO_PICKUP)) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + tripModel.getPickupLocation().getLatitude() + "," + tripModel.getPickupLocation().getLongitude());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            } else {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + tripModel.getDropLocations().get(0).getLatitude() + "," + tripModel.getDropLocations().get(0).getLongitude());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
        button.setOnClickListener(v -> {
            if (button.getText().toString().equals(getResources().getString(R.string.arrived))) {
                button.setBackground(ContextCompat.getDrawable(getApplicationContext(),
                        R.drawable.green_view_background_with_white_redious_boder));
                button.setText(getResources().getString(R.string.start));
                distance.setVisibility(View.GONE);
                date.setVisibility(View.GONE);
                hireDistance.setVisibility(View.GONE);
                passengerDistance.setVisibility(View.GONE);
                cancelTrip.setVisibility(View.GONE);
                new LoginSession(getApplicationContext()).setDriverState(KeyString.DRIVER_ARRIVED);
                polyLine.remove();
            } else if (button.getText().toString().equals(getResources().getString(R.string.start))) {
                final AlertDialog alertDialog = new AlertDialog.Builder(OnTripActivity.this).create();
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.trip_start_dialog, null, false);
                view.findViewById(R.id.confirm_button).setOnClickListener(v14 -> {
                    alertDialog.dismiss();
                    button.setBackground(ContextCompat.getDrawable(getApplicationContext(),
                            R.drawable.red_view_background_with_redious_conner));
                    button.setText(getResources().getString(R.string.end));
                    distance.setVisibility(View.GONE);
                    date.setVisibility(View.GONE);
                    hireDistance.setVisibility(View.GONE);
                    passengerDistance.setVisibility(View.GONE);
                    cancelButton.setVisibility(View.GONE);
                    cancelTrip.setVisibility(View.GONE);
                    GPSTracker location1 = new GPSTracker(getApplicationContext());
                    Location realPickupLocation = new Location();
                    realPickupLocation.setLatitude(location1.getLatitude());
                    realPickupLocation.setLongitude(location1.getLongitude());
                    realPickupLocation.setAddress(location1.getAddressLine(getApplicationContext()));
                    tripModel.setPickupLocation(realPickupLocation);
                    new LoginSession(getApplicationContext()).setDriverState(KeyString.ON_TRIP);
                    setTimer(latitude, longitude);
                    LatLng pickup = new LatLng(tripModel.getPickupLocation().getLatitude(),
                            tripModel.getPickupLocation().getLongitude());
                    LatLng drop = new LatLng(tripModel.getDropLocations().get(0).getLatitude(),
                            tripModel.getDropLocations().get(0).getLongitude());
                    mapRoute(pickup, drop, false);
                    pickupMarker.remove();
                    pickupMarker = gmap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                            .title(getString(R.string.pickup_location))
                            .position(pickup));
                });
                view.findViewById(R.id.cancel_button).setOnClickListener(v1 -> alertDialog.dismiss());
                alertDialog.setView(view);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            } else if (button.getText().toString().equals(getResources().getString(R.string.end))) {
                final AlertDialog alertDialog = new AlertDialog.Builder(OnTripActivity.this).create();
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.trip_end_dialog, null, false);
                view.findViewById(R.id.confirm_button).setOnClickListener(v13 -> {
                    alertDialog.dismiss();
                    new LoginSession(getApplicationContext()).setDriverState(KeyString.UPON_COMPLETION);
//                    new LoginSession(getApplicationContext()).setDriverState(KeyString.DROP_LOCATION_ARRIVED);
                    handler.removeCallbacks(distanceTimer);
                    Intent intent = new Intent(getApplicationContext(), TripBillingActivity.class);
                    intent.putExtra(KeyString.TRIP_MODEL, new Gson().toJson(tripModel));
                    intent.putExtra(KeyString.TRIP_PRICE_MODEL, new Gson().toJson(tripPriceModel));
                    intent.putExtra(KeyString.TOTAL_TIME, totalTimeMillis);
                    intent.putExtra(KeyString.WAIT_TIME, waitTimeMillis);
                    intent.putExtra(KeyString.DISTANCE, totalDistanceInMeters);

                    System.out.println(totalDistanceInMeters);
                    System.out.println(new Gson().toJson(tripModel));
                    System.out.println(new Gson().toJson(tripPriceModel));

                    startActivity(intent);
                    finish();
                });
                view.findViewById(R.id.cancel_button).setOnClickListener(v12 -> alertDialog.dismiss());
                alertDialog.setView(view);
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
            DriverState state = new DriverState(mSocket.id(), new LoginSession(getApplicationContext()).getDriverState(), new LoginSession(getApplicationContext()).get_Id());
//            PaymentStatus paymentStatus = new PaymentStatus(tripModel.getId(), mSocket.id(), "pending", "cash");
            try {
                mSocket.emit(KeyString.UPDATE_DRIVER_STATE_SOCKET, new JSONObject(new Gson().toJson(state, DriverState.class)));
//                mSocket.emit(KeyString.UPDATE_PAY_STATUS, new JSONObject(new Gson().toJson(paymentStatus, PaymentStatus.class)));
//                mSocket.emit(KeyString.UPDATE_PAY_STATUS, new JSONObject("{id:'" + tripModel.getId() + "',payStatus:pending}"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        ImageView callButton = findViewById(R.id.call_button);
        callButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_CALL);
            if (tripModel.getType().equals(KeyString.PASSENGER_TRIP)) {
                intent.setData(Uri.parse("tel:" + tripModel.getPassengerDetails().getContactNumber()));
            } else {
                intent.setData(Uri.parse("tel:" + tripModel.getMobileNumber()));
            }
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                new Permission(getApplicationContext(), OnTripActivity.this).checkPermissions();
                return;
            }
            startActivity(intent);
        });

        tripCancelListner();
    }

    /**
     * map path api call
     */
    private void mapRoute(LatLng origin, LatLng dest, boolean isPassengerDistance) {
        ApiInterface apiInterface = mapApiClient.getApiClient().create(ApiInterface.class);
        assert origin != null;
        assert dest != null;
        Call<MapDistanceModel> call = apiInterface.getDistanceDuration("metric", origin.latitude + "," + origin.longitude, dest.latitude + "," + dest.longitude, "driving");
        call.enqueue(new Callback<MapDistanceModel>() {
            @Override
            public void onResponse(@NotNull Call<MapDistanceModel> call, @NotNull Response<MapDistanceModel> response) {
                try {
                    //Remove previous line from map
                    if (polyLine != null) {
                        polyLine.remove();
                    }
                    assert response.body() != null;
                    String encodedString = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
                    List<LatLng> list = decodePoly(encodedString);
                    polyLine = gmap.addPolyline(new PolylineOptions().addAll(list).width(10).color(Color.BLACK).geodesic(true));
                    if (isPassengerDistance) {
                        /*set distance to passenger*/
                        double distance = response.body().getRoutes().get(0).getLegs().get(0).getDistance().getValue() / 1000;
                        date.setText(
                                String.format(getString(R.string.numberOf_km), distance)
                        );
                    }

//                    pickupMarker = gMap.addMarker(new MarkerOptions()
//                            .position(origin)
//                            .title("Pickup Location")
//                            .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.pickup_location_marker)));
//                    dropMarker = gMap.addMarker(new MarkerOptions()
//                            .position(dest)
//                            .title("Drop Location"));
//                    gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(
//                            builder.build(), 50));
                } catch (Exception e) {
                    Log.d("onResponse", "There is an error");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NotNull Call<MapDistanceModel> call, @NotNull Throwable t) {
                Log.d("onFailure", t.toString());
            }
        });

    }


//    public void drawPath(String result) {
//        if (polyLine != null) {
//            gmap.clear();
//        }
//        gmap.addMarker(new MarkerOptions().position(endLatLng).icon(
//                BitmapDescriptorFactory.fromResource(R.drawable.vehicle_icon)));
//        gmap.addMarker(new MarkerOptions().position(startLatLng).icon(
//                BitmapDescriptorFactory.fromResource(R.drawable.vehicle_icon)));
//        try {
//            // Tranform the string into a json object
//            final JSONObject json = new JSONObject(result);
//            JSONArray routeArray = json.getJSONArray("routes");
//            JSONObject routes = routeArray.getJSONObject(0);
//            JSONObject overviewPolylines = routes
//                    .getJSONObject("overview_polyline");
//            String encodedString = overviewPolylines.getString("points");
//            List < LatLng > list = decodePoly(encodedString);
//
//            PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
//            for (int z = 0; z < list.size(); z++) {
//                LatLng point = list.get(z);
//                options.add(point);
//            }
//            polyLine = gmap.addPolyline(options);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private List<LatLng> decodePoly(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    /**
     * listener for trip cancel by passenger
     */
    private void tripCancelListner() {
        mSocket.on(KeyString.PASSENGER_CANCEL_TRIP_SOCKET, args -> {
            if (!checker) {
                checker = true;
                runOnUiThread(() -> {
                    AlertDialog alertDialog = new AlertDialog.Builder(OnTripActivity.this).create();
                    alertDialog.setTitle(getResources().getString(R.string.cancel_trip));
                    alertDialog.setMessage(getResources().getString(R.string.trip_cancel_by_passenger));
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            (dialog, which) -> {
                                dialog.dismiss();
                                new LoginSession(getApplicationContext()).setDriverState(KeyString.ONLINE);
                                DriverState state = new DriverState(mSocket.id(), new LoginSession(getApplicationContext()).getDriverState(), new LoginSession(getApplicationContext()).get_Id());
                                boolean checker = true;
                                while (checker) {
                                    if (mSocket.connected()) {
                                        try {
                                            mSocket.emit(KeyString.UPDATE_DRIVER_STATE_SOCKET, new JSONObject(new Gson().toJson(state, DriverState.class)));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        checker = false;
                                    }
                                }
                                //onBackPressed();
                                finish();
                            });
                    if (!OnTripActivity.this.isFinishing()) {
                        alertDialog.show();
                    }
                });
            }
        });
    }

    /**
     * trip info UI data attachment method
     *
     * @param model -
     */
    private void setData(TripModel model) {
        TextView name = findViewById(R.id.txtName);
        TextView cost = findViewById(R.id.cost);
        date = findViewById(R.id.time);
        distance = findViewById(R.id.tvTripDistance);
        tvPickupAddress = findViewById(R.id.tvPickupAddress);
        tvDropAddress = findViewById(R.id.tvDropAddress);
        hireDistance = findViewById(R.id.tv_hire_Distance);
        passengerDistance = findViewById(R.id.tv_passengerDistance);
        cancelTrip = findViewById(R.id.tv_cancelTrip);
        name.setText(model.getCustomerName());
        /*set hirecost*/
        cost.setText(String.format(getString(R.string.total_cost_rs), model.getHireCost()));
        /*hire distance set*/
        distance.setText(String.format(getString(R.string.numberOf_km), model.getDistance()));
        tvPickupAddress.setText(model.getPickupLocation().getAddress());
        tvDropAddress.setText(model.getDropLocations().get(0).getAddress());
    }

    /**
     * method for calculate waiting time and distance
     *
     * @param pickupLongitude -
     * @param pickupLatitude  -
     */
    private void setTimer(final double pickupLatitude, final double pickupLongitude) {
        lastLongitude = pickupLongitude;
        lastLatitude = pickupLatitude;
        distanceTimer = new Runnable() {
            @Override
            public void run() {
                totalTimeMillis = totalTimeMillis + 1000;
                Log.i("SEN Total time", totalTimeMillis + "");
                double distanceFromLastLocation = getDistance(lastLatitude, lastLongitude, latitude, longitude);
                Log.i("SEN Distance from last", distanceFromLastLocation + "");
                if (distanceFromLastLocation < 1) {
                    waitTimeMillis = waitTimeMillis + 1000;
                    Log.i("SEN wait time", waitTimeMillis + "");
                } else {
                    totalDistanceInMeters = prevDistance + distanceFromLastLocation;
                    waitedDistance = totalDistanceInMeters + totalDistanceInMeters * 0.012;
                    lastLatitude = latitude;
                    lastLongitude = longitude;
                    prevDistance = totalDistanceInMeters;
                    Log.i("SEN Distance", totalDistanceInMeters + "");
                }
                Log.i("SEN", "------------------------");
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(distanceTimer);
    }

    /**
     * passenger trip cancellation method
     */
    private void cancelPassengerTrip() {
        progressDialog = new ProgressDialog(OnTripActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        PassengerTripCancelRequestModel dataModel = new PassengerTripCancelRequestModel();
        dataModel.setCancelCost(tripPriceModel.getContent().get(0).getTripCancelationFee());
        dataModel.setType(tripModel.getType());
        dataModel.setCancelReason(" ");
        dataModel.setDriverId(new LoginSession(getApplicationContext()).getUserDetails().getContent().getId());
        dataModel.setTripId(tripModel.getId());
        dataModel.setPickupLocation(tripModel.getPickupLocation());
        dataModel.setDropLocations(tripModel.getDropLocations());
        dataModel.setPassengerId(tripModel.getPassengerDetails().getId());
        dataModel.setEstimatedCost(tripModel.getHireCost());

        ApiInterface apiInterface = JsonApiClient.getApiClient().create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.cancelPassengerTrip(dataModel);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                progressDialog.dismiss();
                Log.i("TAG CHECK DRIVER CODE", String.valueOf(response.code()));
                if (response.code() == 200) {
                    new LoginSession(getApplicationContext()).setDriverState(KeyString.ONLINE);
                    DriverState state = new DriverState(mSocket.id(), new LoginSession(getApplicationContext()).getDriverState(), new LoginSession(getApplicationContext()).get_Id());
                    boolean checker = true;
                    while (checker) {
                        if (mSocket.connected()) {
                            try {
                                mSocket.emit(KeyString.UPDATE_DRIVER_STATE_SOCKET, new JSONObject(new Gson().toJson(state, DriverState.class)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            checker = false;
                        }
                    }
                    //onBackPressed();
                    cancelBottomSheet.setVisibility(View.GONE);
                    finish();
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(OnTripActivity.this).create();
                    alertDialog.setTitle("Something Went Wrong");
                    alertDialog.setMessage("please try again");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            (dialog, which) -> dialog.dismiss());
                    alertDialog.show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                progressDialog.dismiss();
                Log.i("TAG CHECK DRIVER FAIL", t.getMessage());
                AlertDialog alertDialog = new AlertDialog.Builder(OnTripActivity.this).create();
                alertDialog.setTitle("Something Went Wrong");
                alertDialog.setMessage("please try again");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        (dialog, which) -> dialog.dismiss());
                alertDialog.show();
            }
        });
    }

    /**
     * dispatch trip cancellation method
     */
    private void cancelDispatchTrip() {
        progressDialog = new ProgressDialog(OnTripActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        DispatchTripCancelRequestModel dataModel = new DispatchTripCancelRequestModel();
        dataModel.setDiverId(new LoginSession(getApplicationContext()).getUserDetails().getContent().getId());
        dataModel.setVehicleId(new LoginSession(getApplicationContext()).getVehicle().getId());
        dataModel.setType(tripModel.getType());
        dataModel.setDispatchId(tripModel.getId());

        ApiInterface apiInterface = JsonApiClient.getApiClient().create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.cancelDispatchTrip(dataModel);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                progressDialog.dismiss();
                Log.i("TAG CHECK DRIVER CODE", String.valueOf(response.code()));
                if (response.code() == 200) {
                    new LoginSession(getApplicationContext()).setDriverState(KeyString.ONLINE);
                    DriverState state = new DriverState(mSocket.id(), new LoginSession(getApplicationContext()).getDriverState(), new LoginSession(getApplicationContext()).get_Id());
                    boolean checker = true;
                    while (checker) {
                        if (mSocket.connected()) {
                            try {
                                mSocket.emit(KeyString.UPDATE_DRIVER_STATE_SOCKET, new JSONObject(new Gson().toJson(state, DriverState.class)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            checker = false;
                        }
                    }
                    //onBackPressed();
                    cancelBottomSheet.setVisibility(View.GONE);
                    finish();
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(OnTripActivity.this).create();
                    alertDialog.setTitle("Something Went Wrong");
                    alertDialog.setMessage("please try again");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            (dialog, which) -> dialog.dismiss());
                    alertDialog.show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                progressDialog.dismiss();
                Log.i("TAG CHECK DRIVER FAIL", t.getMessage());
                AlertDialog alertDialog = new AlertDialog.Builder(OnTripActivity.this).create();
                alertDialog.setTitle("Something Went Wrong");
                alertDialog.setMessage("please try again");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        (dialog, which) -> dialog.dismiss());
                alertDialog.show();
            }
        });
    }

    /**
     * calculate distance between two geo location
     *
     * @param prevLatitude -
     * @param preLongitude -
     * @param newLatitude  -
     * @param newLongitude -
     * @return -
     */
    private Double getDistance(double prevLatitude, double preLongitude, double newLatitude, double newLongitude) {

        android.location.Location loc1 = new android.location.Location("");
        loc1.setLatitude(prevLatitude);
        loc1.setLongitude(preLongitude);

        android.location.Location loc2 = new android.location.Location("");
        loc2.setLatitude(newLatitude);
        loc2.setLongitude(newLongitude);

        double distanceInMeters = loc1.distanceTo(loc2);
        return Math.abs(distanceInMeters);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        final Permission permission = new Permission(getApplicationContext(), OnTripActivity.this);
        gmap = googleMap;
        LatLng pickupLocation = new LatLng(tripModel.getPickupLocation().getLatitude(), tripModel.getPickupLocation().getLongitude());
        LatLng dropLocation = new LatLng(tripModel.getDropLocations().get(0).getLatitude(), tripModel.getDropLocations().get(0).getLongitude());
        mapRoute(latLng, pickupLocation, true);
        runOnUiThread(() -> {
            gmap.setMinZoomPreference(1);
            gmap.setMaxZoomPreference(20);
            if (!permission.isLocationPermissionGranted()) {
                permission.checkPermissions();
                return;
            }
            gmap.setMyLocationEnabled(true);
            pickupMarker = gmap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .title(getString(R.string.pickup_location))
                    .position(new LatLng(pickupLocation.latitude, pickupLocation.longitude)));
            gmap.addMarker(new MarkerOptions()
                    .title(getString(R.string.drop_location))
                    .position(new LatLng(dropLocation.latitude, dropLocation.longitude)));
            gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
            gmap.setOnMyLocationButtonClickListener(() -> {
                gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
                return false;
            });
        });
    }

    @Override
    public void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }
        mapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onBackPressed() {
        if (checker) {
            new LoginSession(getApplicationContext()).setDriverState(KeyString.ONLINE);
            DriverState state = new DriverState(mSocket.id(), new LoginSession(getApplicationContext()).getDriverState(), new LoginSession(getApplicationContext()).get_Id());
            boolean checker = true;
            while (checker) {
                if (mSocket.connected()) {
                    try {
                        mSocket.emit(KeyString.UPDATE_DRIVER_STATE_SOCKET, new JSONObject(new Gson().toJson(state, DriverState.class)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    checker = false;
                }
            }
            finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        if (checker) {
            new LoginSession(getApplicationContext()).setDriverState(KeyString.ONLINE);
            DriverState state = new DriverState(mSocket.id(), new LoginSession(getApplicationContext()).getDriverState(), new LoginSession(getApplicationContext()).get_Id());
            boolean checker = true;
            while (checker) {
                if (mSocket.connected()) {
                    try {
                        mSocket.emit(KeyString.UPDATE_DRIVER_STATE_SOCKET, new JSONObject(new Gson().toJson(state, DriverState.class)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    checker = false;
                }
            }
            //onBackPressed();
            finish();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        this.mWakeLock.release();
        mapView.onDestroy();
        if (new LoginSession(getApplicationContext()).getDriverState().equals(KeyString.GOING_TO_PICKUP) || new LoginSession(getApplicationContext()).getDriverState().equals(KeyString.ON_TRIP)) {
            TripSession session = new TripSession(getApplicationContext());
            session.setTripModel(tripModel);
            session.setTripPriceModel(tripPriceModel);
            session.setDriverState(new LoginSession(getApplicationContext()).getDriverState());
            session.setLastActivity(getClass().getName());
            session.setLastLatitude(lastLatitude);
            session.setLastLongitude(lastLongitude);
            session.setPrevDistance(prevDistance);
            session.setTotalDistance(totalDistanceInMeters);
            session.setTotalTimeMillis(totalTimeMillis);
            session.setWaitTimeMillis(waitTimeMillis);
        } else {
            new TripSession(getApplicationContext()).clearTripSession();
        }
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
