package com.ynr.taximedriver.home.road_pickup;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;
import com.ynr.taximedriver.R;
import com.ynr.taximedriver.background.LocationMonitoringService;
import com.ynr.taximedriver.config.KeyString;
import com.ynr.taximedriver.gps.GPSTracker;
import com.ynr.taximedriver.model.DriverState;
import com.ynr.taximedriver.model.roadPickupModel.EndRoadPickupTripModel;
import com.ynr.taximedriver.model.roadPickupModel.StartRoadPickupTripResponceModel;
import com.ynr.taximedriver.rest.ApiInterface;
import com.ynr.taximedriver.rest.JsonApiClient;
import com.ynr.taximedriver.session.LoginSession;
import com.ynr.taximedriver.session.TripSession;
import com.ynr.taximedriver.socket.MySocket;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoadPickupPriceCalculatorActivity extends AppCompatActivity {
    public static final String START = "start";
    public static final String END = "end";
    public static final String BACK = "back";
    private TextView totalCost, waitingCost, waitingTime, tvDistance;
    private Button startEndButton;
    private final Handler handler = new Handler();
    private Runnable waitTimer, distanceTimer;
    //int longitude = -1, latitude = -1, counter = 0;
    private double lastLongitude;
    private double lastLatitude;
    private final double waitTimeMilliss = 0;
    private final double totalTimeMilliss = 0;
    double totalDistance = 0.0, prevDistance = 0.0, totalFaree = 0.0, waitingFaree = 0.0, waitedDistancee = 0.0;
    private String buttonState = START;
    private StartRoadPickupTripResponceModel dataModel;
    private ProgressDialog progressDialog;
    private com.github.nkzawa.socketio.client.Socket mSocket;
    protected PowerManager.WakeLock mWakeLock;
    private double latitude, longitude;
    private String address;
    //    private EndRoadPickupTripModel.Location pickupLocation = new EndRoadPickupTripModel.Location();
    EndRoadPickupTripModel endTripModel;

    // Our handler for received Intents. This will be called whenever an Intent
// with an action named "custom-event-name" is broadcasted.
//    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            // Get extra data included in the Intent
////            location.setLatitude(intent.getDoubleExtra(KeyString.EXTRA_LATITUDE, 0));
////            location.setLongitude(intent.getDoubleExtra(KeyString.EXTRA_LONGITUDE, 0));
////            location.setAddress(intent.getStringExtra(KeyString.EXTRA_ADDRESS));
//        }
//    };

    @SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_road_pickup_price_calculater);

        /* This code together with the one in onDestroy()
         * will make the screen be always on until this Activity gets destroyed. */
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();

        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "custom-event-name".
//        LocalBroadcastManager.getInstance(RoadPickupPriceCalculaterActivity.this).registerReceiver(mMessageReceiver, new IntentFilter(LocationMonitoringService.ACTION_LOCATION_BROADCAST));
//
        GPSTracker gpsTracker = new GPSTracker(getApplicationContext());
        address = gpsTracker.getAddressLine(getApplicationContext());
        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();

        LocalBroadcastManager.getInstance(RoadPickupPriceCalculatorActivity.this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
//                        latitude = intent.getDoubleExtra(KeyString.EXTRA_LATITUDE, 0);
//                        longitude = intent.getDoubleExtra(KeyString.EXTRA_LONGITUDE, 0);
//                        address = intent.getStringExtra(KeyString.EXTRA_ADDRESS);
                        latitude = intent.getDoubleExtra(KeyString.EXTRA_LATITUDE, 0);
                        longitude = intent.getDoubleExtra(KeyString.EXTRA_LONGITUDE, 0);
                        address = intent.getStringExtra(KeyString.EXTRA_ADDRESS);
                    }
                }, new IntentFilter(LocationMonitoringService.ACTION_LOCATION_BROADCAST)
        );

        MySocket socket = (MySocket) getApplication();
        mSocket = socket.getmSocket();

        totalCost = findViewById(R.id.tvTotalCost);
        waitingCost = findViewById(R.id.waiting_cost);
        waitingTime = findViewById(R.id.wait_time);
        tvDistance = findViewById(R.id.tvTripDistance);
        startEndButton = findViewById(R.id.start_end_button);

        TripSession tripSession = new TripSession(getApplicationContext());
        endTripModel = tripSession.getRoadPickup();
        if (endTripModel != null) {
            setButtonStateToOngoing();
        } else {
            Bundle bundle = getIntent().getExtras();
            String json = bundle.getString(KeyString.ROAD_PICUP_MODEL);
            Gson gson = new Gson();
            dataModel = gson.fromJson(json, StartRoadPickupTripResponceModel.class);
        }

        startEndButton.setOnClickListener(v -> {
            LoginSession session = new LoginSession(getApplicationContext());
            switch (buttonState) {
                case START: {
                    endTripModel = new EndRoadPickupTripModel();
                    endTripModel.setId(dataModel.getTripId());
                    endTripModel.setAdminCommission(dataModel.getContent().get(0).getDistrictPrice());
                    endTripModel.setPickupData(dataModel);
                    endTripModel.setDriverId(new LoginSession(getApplicationContext()).getUserDetails().getContent().getId());
                    endTripModel.setPaymentMethod("cash");
                    AlertDialog alertDialog = new AlertDialog.Builder(RoadPickupPriceCalculatorActivity.this).create();
                    alertDialog.setTitle("Confirm trip START");
                    alertDialog.setMessage(" ");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Yes",
                            (dialog, which) -> {
                                setCalculation(latitude, longitude);
                                buttonState = END;
                                v.setBackgroundResource(R.drawable.red_view_background_with_low_redious_conner);
                                startEndButton.setText(R.string.end_trip);
                                session.setDriverState(KeyString.ON_TRIP);
                                GPSTracker location = new GPSTracker(getApplicationContext());
                                EndRoadPickupTripModel.Location pickupLocation = new EndRoadPickupTripModel.Location();
                                pickupLocation.setAddress(location.getAddressLine(getApplicationContext()));
                                pickupLocation.setLatitude(location.getLatitude());
                                pickupLocation.setLongitude(location.getLongitude());
                                endTripModel.setPickupLocation(pickupLocation);
                                dialog.dismiss();
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "No",
                            (dialog, which) -> dialog.dismiss());
                    alertDialog.show();
                    break;
                }
                case END: {
                    AlertDialog alertDialog = new AlertDialog.Builder(RoadPickupPriceCalculatorActivity.this).create();
                    alertDialog.setTitle("Confirm trip END");
                    alertDialog.setMessage(" ");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Yes",
                            (dialog, which) -> {
                                handler.removeCallbacks(distanceTimer);
                                tripEndApiCall();
                                tripSession.setDriverState(KeyString.ONLINE);
                                tripSession.clearRoadPickup();
                                dialog.dismiss();
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "No",
                            (dialog, which) -> dialog.dismiss());
                    alertDialog.show();
                    break;
                }
                case BACK:
                    onBackPressed();
                    break;
            }
            DriverState state = new DriverState(mSocket.id(), session.getDriverState(), new LoginSession(getApplicationContext()).get_Id());
            try {
                mSocket.emit(KeyString.UPDATE_DRIVER_STATE_SOCKET, new JSONObject(new Gson().toJson(state, DriverState.class)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onDestroy() {
        this.mWakeLock.release();
        if (buttonState.equals(END)) {
            new TripSession(getApplicationContext()).setRoadPickup(endTripModel);
        }
        super.onDestroy();
    }

    private void setButtonStateToOngoing() {
        buttonState = END;
        final LoginSession session = new LoginSession(getApplicationContext());
        setCalculation(latitude, longitude);
        startEndButton.setBackgroundResource(R.drawable.red_view_background_with_low_redious_conner);
        startEndButton.setText(R.string.end_trip);
        session.setDriverState(KeyString.ON_TRIP);
    }
    /**
     * road pickup distance, time and cost calculator
     *
     * @param pickupLongitude -
     * @param pickupLatitude -
     */
    private void setCalculation(final double pickupLatitude, final double pickupLongitude) {
        lastLongitude = pickupLongitude;
        lastLatitude = pickupLatitude;
        distanceTimer = new Runnable() {
            @Override
            public void run() {
                endTripModel.setTripTime(endTripModel.getTripTime() + 1);
                double distance = getDistance(lastLatitude, lastLongitude, latitude, longitude);
                Log.i("Distance", distance +"");
                if (distance < 0.005) {
                    endTripModel.setWaitTime(endTripModel.getWaitTime() + 1);
                    runOnUiThread(() -> waitingTime.setText((int) (endTripModel.getWaitTime() / 60) + "Min " + (int) endTripModel.getWaitTime() % 60 + "S"));
                    double waitingFare = (endTripModel.getWaitTime() / 60) * endTripModel.getPickupData().getContent().get(0).getNormalWaitingChargePerMinute();
                    endTripModel.setWaitingCost(waitingFare);
                    runOnUiThread(() -> waitingCost.setText(new DecimalFormat("00.00").format(waitingFare) + " Rs"));
                }
                if (distance < 10000) {
                    endTripModel.setDistance(endTripModel.getDistance() + distance + (distance * 0.012));
                    Log.i("TotalDistance", endTripModel.getDistance() +"");
                    lastLatitude = latitude;
                    lastLongitude = longitude;
                }
                runOnUiThread(() -> tvDistance.setText(round(endTripModel.getDistance() / 1000, 1) + "Km"));
                double totalFare = 0.0;
                if ((endTripModel.getDistance() / 1000) <= endTripModel.getPickupData().getContent().get(0).getMinimumKM()) {
                    totalFare = endTripModel.getPickupData().getContent().get(0).getBaseFare()
                            + endTripModel.getPickupData().getContent().get(0).getMinimumFare()
                            + endTripModel.getWaitingCost();
                } else if ((endTripModel.getDistance() / 1000) <= endTripModel.getPickupData().getContent().get(0).getBelowAboveKMRange()) {
                    totalFare = endTripModel.getPickupData().getContent().get(0).getBaseFare()
                            + endTripModel.getPickupData().getContent().get(0).getMinimumFare()
                            + endTripModel.getPickupData().getContent().get(0).getBelowKMFare() * ((endTripModel.getDistance() / 1000) - endTripModel.getPickupData().getContent().get(0).getMinimumKM())
                            + endTripModel.getWaitingCost();
                    endTripModel.setTotalCost(totalFare);
                } else if ((endTripModel.getDistance() / 1000) > endTripModel.getPickupData().getContent().get(0).getBelowAboveKMRange()) {
                    totalFare = endTripModel.getPickupData().getContent().get(0).getBaseFare()
                            + endTripModel.getPickupData().getContent().get(0).getMinimumFare()
                            + endTripModel.getPickupData().getContent().get(0).getBelowKMFare() * (endTripModel.getPickupData().getContent().get(0).getBelowAboveKMRange() - endTripModel.getPickupData().getContent().get(0).getMinimumKM())
                            + endTripModel.getPickupData().getContent().get(0).getAboveKMFare() * ((endTripModel.getDistance() / 1000) - endTripModel.getPickupData().getContent().get(0).getBelowAboveKMRange())
                            + endTripModel.getWaitingCost();
                }
                endTripModel.setTotalCost(totalFare);
                runOnUiThread(() -> totalCost.setText(new DecimalFormat("00.00").format(endTripModel.getTotalCost()) + " Rs"));
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(distanceTimer, 1000);
    }

    /**
     * round double value with two floating point
     *
     * @param value -
     * @param places -
     * @return -
     */
    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    /**
     * calculate air distance using given two location
     *
     * @param prevLatitude -
     * @param preLongitude -
     * @param newLatitude -
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

    /**
     * road pickup end api call
     */
    private void tripEndApiCall() {
        EndRoadPickupTripModel.Location dropLocation = new EndRoadPickupTripModel.Location();
        dropLocation.setLatitude(latitude);
        dropLocation.setLongitude(longitude);
        endTripModel.setDropLocation(dropLocation);
        progressDialog = new ProgressDialog(RoadPickupPriceCalculatorActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        ApiInterface apiInterface = JsonApiClient.getApiClient().create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.endRoadPickup(endTripModel);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                progressDialog.dismiss();
                Log.i("TAG_RESPONSE_CODE", String.valueOf(response.code()));
                if (response.code() == 200) {
                    buttonState = BACK;
                    startEndButton.setBackgroundResource(R.drawable.green_view_background_with_low_redious_conner);
                    handler.removeCallbacks(distanceTimer);
                    startEndButton.setText(R.string.back_to_home);
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(RoadPickupPriceCalculatorActivity.this).create();
                    alertDialog.setTitle("Something Went Wrong...");
                    alertDialog.setMessage("Place Check your Internet Connection and End again");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            (dialog, which) -> dialog.dismiss());
                    alertDialog.show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                progressDialog.dismiss();
                Log.i("TAG_ONFAILURE", t.getMessage());
                AlertDialog alertDialog = new AlertDialog.Builder(RoadPickupPriceCalculatorActivity.this).create();
                alertDialog.setTitle("Something Went Wrong...");
                alertDialog.setMessage("Place Check your Internet Connection and End again");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        (dialog, which) -> dialog.dismiss());
                alertDialog.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!buttonState.equals(END)) {
            super.onBackPressed();
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(RoadPickupPriceCalculatorActivity.this).create();
            alertDialog.setTitle("Something Went Wrong...");
            alertDialog.setMessage("End Your Current Trip and Back again");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    (dialog, which) -> dialog.dismiss());
            alertDialog.show();
        }
    }
}
