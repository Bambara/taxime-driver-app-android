package com.ynr.taximedriver.home.driver;

import static android.content.Intent.FLAG_ACTIVITY_MULTIPLE_TASK;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.gson.Gson;
import com.ynr.taximedriver.R;
import com.ynr.taximedriver.config.KeyString;
import com.ynr.taximedriver.gps.GPSTracker;
import com.ynr.taximedriver.home.HomeActivity;
import com.ynr.taximedriver.model.DispatchTripEndRequestModel;
import com.ynr.taximedriver.model.DriverState;
import com.ynr.taximedriver.model.Location;
import com.ynr.taximedriver.model.PassengerTripEndRequestModel;
import com.ynr.taximedriver.model.PaymentStatus;
import com.ynr.taximedriver.model.tripAcceptModel.TripAcceptResponseModel;
import com.ynr.taximedriver.model.tripModel.TripModel;
import com.ynr.taximedriver.rest.ApiInterface;
import com.ynr.taximedriver.rest.JsonApiClient;
import com.ynr.taximedriver.session.LoginSession;
import com.ynr.taximedriver.socket.MySocket;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TripBillingActivity extends AppCompatActivity {
    private CardView cv_totalFare;
    private TextView totalFare, discount, fare, distance, tripTime, waitTime, waitCost, cancelFee, tvPayStatusValue;
    private ImageView iv_paymentMethod;
    private Button paymentButton;
    private TripModel tripModel;
    private TripAcceptResponseModel tripPriceModel;
    private double waitTimeMillis = 0.0, totalTimeMillis = 0.0, totalDistanceInMeters = 0.0;
    ProgressDialog progressDialog;

    private String paymentMethod = "cash";

    private com.github.nkzawa.socketio.client.Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_billing);

        Bundle bundle = getIntent().getExtras();
        String json = bundle.getString(KeyString.TRIP_MODEL);
        String json1 = bundle.getString(KeyString.TRIP_PRICE_MODEL);
        waitTimeMillis = bundle.getDouble(KeyString.WAIT_TIME);
        totalTimeMillis = bundle.getDouble(KeyString.TOTAL_TIME);
        totalDistanceInMeters = bundle.getDouble(KeyString.DISTANCE);
        Gson gson = new Gson();
        tripModel = gson.fromJson(json, TripModel.class);
        tripPriceModel = gson.fromJson(json1, TripAcceptResponseModel.class);

        cv_totalFare = findViewById(R.id.cv_totalFare);
        iv_paymentMethod = findViewById(R.id.iv_paymentMethod);
        totalFare = findViewById(R.id.total_fare);
        discount = findViewById(R.id.discount);
        fare = findViewById(R.id.fare);
        distance = findViewById(R.id.tvTripDistance);
        tripTime = findViewById(R.id.trip_time);
        waitTime = findViewById(R.id.wait_time);
        waitCost = findViewById(R.id.wait_cost);
        cancelFee = findViewById(R.id.cancel_fee);
        paymentButton = findViewById(R.id.payment_button);
        tvPayStatusValue = findViewById(R.id.tvPayStatusValue);


        MySocket socket = (MySocket) getApplication();
        mSocket = socket.getmSocket();

        tvPayStatusValue.setText("Pending");

        priceCalculation();
        getTripPaymentStatus();
    }


    private void getTripPaymentStatus() {
        mSocket.on("get_trip_payment_status", args -> {

            runOnUiThread(() -> {
                PaymentStatus payStatus = new Gson().fromJson(args[0].toString(), PaymentStatus.class);

                if (payStatus.getPayStatus().equals("payed")) {
                    tvPayStatusValue.setText("Payed");
                }

                if (payStatus.getPayMethod().equals("cash")) {
                    iv_paymentMethod.setImageDrawable(getResources().getDrawable(R.drawable.ic_cash_method));
                    cv_totalFare.setBackground(getResources().getDrawable(R.drawable.trip_bill_blue_bg));
                    paymentButton.setBackground(getResources().getDrawable(R.drawable.trip_bill_blue_bg));
                } else if (payStatus.getPayMethod().equals("card")) {
                    iv_paymentMethod.setImageDrawable(getResources().getDrawable(R.drawable.ic_card_payment));
                    cv_totalFare.setBackground(getResources().getDrawable(R.drawable.dispatcher_vehicle_type_bg));
                    paymentButton.setBackground(getResources().getDrawable(R.drawable.dispatcher_vehicle_type_bg));
                }

                System.out.println("Pay Status " + payStatus.getPayMethod());
                System.out.println("Pay Status " + payStatus.getPayStatus());
            });


        });
    }


    /**
     * hire cost calculation and set thees value in UI
     *
     * @return
     */
    private double priceCalculation() {

        double tripDistance, waitCost = 0.0, tripFare = 0.0, totalFare = 0.0;

//        if (Math.abs((tripModel.getDistance() * 1000) - totalDistance) > 1000) {
//
//            tripDistance = totalDistance/1000;
//        } else {
//
        tripDistance = totalDistanceInMeters / 1000;//tripModel.getDistance();
//        }

        if ((waitTimeMillis / 60000) > 5) {

            waitCost = ((waitTimeMillis / 60000) - 5) * tripPriceModel.getContent().get(0).getNormalWaitingChargePerMinute();
        }

        if (tripDistance <= tripPriceModel.getContent().get(0).getMinimumKM()) {

            tripFare = tripPriceModel.getContent().get(0).getBaseFare() + tripPriceModel.getContent().get(0).getMinimumFare();
            totalFare = tripFare + waitCost;

        } else if (tripPriceModel.getContent().get(0).getBelowAboveKMRange() > 0) {

            if (tripDistance <= tripPriceModel.getContent().get(0).getBelowAboveKMRange()) {

                tripFare = tripPriceModel.getContent().get(0).getBaseFare() + tripPriceModel.getContent().get(0).getMinimumFare()
                        + (tripDistance - tripPriceModel.getContent().get(0).getMinimumKM()) * tripModel.getBidValue();
                totalFare = tripFare + waitCost;

            } else if (tripDistance > tripPriceModel.getContent().get(0).getBelowAboveKMRange()) {

                tripFare = tripPriceModel.getContent().get(0).getBaseFare() + tripPriceModel.getContent().get(0).getMinimumFare()
                        + (tripPriceModel.getContent().get(0).getBelowAboveKMRange() - tripPriceModel.getContent().get(0).getMinimumKM()) * tripModel.getBidValue()
                        + (tripDistance - tripPriceModel.getContent().get(0).getBelowAboveKMRange()) * tripPriceModel.getContent().get(0).getAboveKMFare();
                totalFare = tripFare + waitCost;

            }
        } else {

            tripFare = tripPriceModel.getContent().get(0).getBaseFare() + tripPriceModel.getContent().get(0).getMinimumFare()
                    + (tripDistance - tripPriceModel.getContent().get(0).getMinimumKM()) * tripModel.getBidValue();
            totalFare = tripFare + waitCost;
        }

        this.totalFare.setText(getResources().getString(R.string.rs) + " " + round(totalFare, 2));
        discount.setText(getResources().getString(R.string.no_discount));
        fare.setText(getResources().getString(R.string.rs) + " " + round(tripFare, 2));
        distance.setText(round(tripDistance, 1) + " " + getResources().getString(R.string.km));
        tripTime.setText((int) (totalTimeMillis / 60000) + " " + getResources().getString(R.string.min));
        waitTime.setText((int) (waitTimeMillis / 60000) + " " + getResources().getString(R.string.min));
        this.waitCost.setText(getResources().getString(R.string.rs) + " " + round(waitCost, 2));

        final DispatchTripEndRequestModel dDataModel = new DispatchTripEndRequestModel();
        final PassengerTripEndRequestModel pDataModel = new PassengerTripEndRequestModel();
        if (tripModel.getType().equals(KeyString.ADMIN_DISPATCH) || tripModel.getType().equals(KeyString.USER_DISPATCH) || tripModel.getType().equals(KeyString.DRIVER_DISPATCH)) {
            dDataModel.setDispatchId(tripModel.getId());
            dDataModel.setType(tripModel.getType());
            dDataModel.setDistance(tripDistance);
            dDataModel.setWaitingCost(waitCost);
            dDataModel.setWaitTime((int) (waitTimeMillis / 60000));
            dDataModel.setEstimatedCost(tripModel.getHireCost());
            dDataModel.setTotalCost(totalFare);
            dDataModel.setDispatcherId(tripModel.getDispatcherId());
            dDataModel.setAdminCommission(tripPriceModel.getContent().get(0).getDistrictPrice());
            dDataModel.setDriverId(new LoginSession(getApplicationContext()).getUserDetails().getContent().getId());
            dDataModel.setPaymentMethod("cash");
            dDataModel.setRealPickupLocation(tripModel.getPickupLocation());

            GPSTracker location = new GPSTracker(getApplicationContext());
            Location realDropLocation = new Location();
            realDropLocation.setLatitude(location.getLatitude());
            realDropLocation.setLongitude(location.getLongitude());
            realDropLocation.setAddress(location.getAddressLine(getApplicationContext()));

            dDataModel.setRealDropLocation(realDropLocation);
            dDataModel.setDestinations(tripModel.getDropLocations());
            dDataModel.setTripTime(String.valueOf(totalTimeMillis / 60000));


            try {
                //PayStatus OBJ
                PaymentStatus paymentStatus = new PaymentStatus();
                paymentStatus.setTripType("DISPATCH_TRIP");
                paymentStatus.setDriverSocketId(mSocket.id());
                paymentStatus.setPayStatus("pending");
                paymentStatus.setPayMethod("cash");
                paymentStatus.setPassengerId(tripModel.getPassengerDetails().getId());
                paymentStatus.setDispatchTripEndRequestModel(dDataModel);
                mSocket.emit(KeyString.UPDATE_PAY_STATUS, new JSONObject(new Gson().toJson(paymentStatus, PaymentStatus.class)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (tripModel.getType().equals(KeyString.PASSENGER_TRIP)) {
            pDataModel.setTripId(tripModel.getId());
            pDataModel.setPassengerId(tripModel.getPassengerDetails().getId());
            pDataModel.setAdminCommission(tripPriceModel.getContent().get(0).getDistrictPrice());
            pDataModel.setDriverId(new LoginSession(getApplicationContext()).getUserDetails().getContent().getId());
            pDataModel.setPaymentMethod("cash");
            pDataModel.setType(tripModel.getType());
            pDataModel.setDistance(tripDistance);
            pDataModel.setWaitingCost(waitCost);
            pDataModel.setWaitTime((int) (waitTimeMillis / 60000));
            pDataModel.setEstimatedCost(tripModel.getHireCost());
            pDataModel.setTotalCost(totalFare);
            pDataModel.setRealPickupLocation(tripModel.getPickupLocation());

            GPSTracker location = new GPSTracker(getApplicationContext());
            Location realDropLocation = new Location();
            realDropLocation.setAddress(location.getAddressLine(getApplicationContext()));
            realDropLocation.setLatitude(location.getLatitude());
            realDropLocation.setLongitude(location.getLongitude());

            pDataModel.setRealDropLocation(realDropLocation);
            pDataModel.setDestinations(tripModel.getDropLocations());
            pDataModel.setTripTime(String.valueOf(totalTimeMillis / 60000));

            try {
                PaymentStatus paymentStatus = new PaymentStatus();
                paymentStatus.setTripType("TRIP");
                paymentStatus.setDriverSocketId(mSocket.id());
                paymentStatus.setPayStatus("pending");
                paymentStatus.setPayMethod("cash");
                paymentStatus.setPassengerId(tripModel.getPassengerDetails().getId());
                paymentStatus.setPassengerTripEndRequestModel(pDataModel);

                mSocket.emit(KeyString.UPDATE_PAY_STATUS, new JSONObject(new Gson().toJson(paymentStatus, PaymentStatus.class)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        paymentButton.setOnClickListener(v -> {
            if (tripModel.getType().equals(KeyString.ADMIN_DISPATCH) || tripModel.getType().equals(KeyString.USER_DISPATCH) || tripModel.getType().equals(KeyString.DRIVER_DISPATCH)) {

                if (tvPayStatusValue.getText().equals("Pending") || tvPayStatusValue.getText().equals("Not Payed")) {
                    AlertDialog alertDialog = new AlertDialog.Builder(TripBillingActivity.this).create();
                    alertDialog.setTitle("Payment Warning");
                    alertDialog.setMessage("Trip payment finish not yet ! Do you want proceed collect payment ?");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                            (dialog, which) -> dispatchTripEnd(dDataModel));

                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                            (dialog, which) -> dialog.dismiss());
                    alertDialog.show();
                } else {
                    dispatchTripEnd(dDataModel);
                }


            } else if (tripModel.getType().equals(KeyString.PASSENGER_TRIP)) {

                if (tvPayStatusValue.getText().equals("Pending") || tvPayStatusValue.getText().equals("Not Payed")) {
                    AlertDialog alertDialog = new AlertDialog.Builder(TripBillingActivity.this).create();
                    alertDialog.setTitle("Payment Warning");
                    alertDialog.setMessage("Trip payment finish not yet ! Do you want proceed collect payment ?");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                            (dialog, which) -> passengerTripEnd(pDataModel));

                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                            (dialog, which) -> dialog.dismiss());
                    alertDialog.show();
                } else {
                    passengerTripEnd(pDataModel);
                }


            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(TripBillingActivity.this).create();
                alertDialog.setTitle("ERROR");
                alertDialog.setMessage("trip type does not match");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        (dialog, which) -> dialog.dismiss());
                alertDialog.show();
            }
        });

        iv_paymentMethod.setOnClickListener(view -> {
            if (paymentMethod.equals("cash")) {
                iv_paymentMethod.setImageDrawable(getResources().getDrawable(R.drawable.ic_card_payment));
                cv_totalFare.setBackground(getResources().getDrawable(R.drawable.trip_bill_blue_bg));
                paymentButton.setBackground(getResources().getDrawable(R.drawable.trip_bill_blue_bg));
                paymentMethod = "card";
            } else if (paymentMethod.equals("card")) {
                iv_paymentMethod.setImageDrawable(getResources().getDrawable(R.drawable.ic_cash_method));
                cv_totalFare.setBackground(getResources().getDrawable(R.drawable.dispatcher_vehicle_type_bg));
                paymentButton.setBackground(getResources().getDrawable(R.drawable.dispatcher_vehicle_type_bg));
                paymentMethod = "cash";
            }
        });

//        DriverState state = new DriverState(mSocket.id(), new LoginSession(getApplicationContext()).getDriverState(), new LoginSession(getApplicationContext()).get_Id());
/*        PaymentStatus paymentStatus = new PaymentStatus(tripModel.getId(), mSocket.id(), "pending", "cash");
        try {
//            mSocket.emit(KeyString.UPDATE_DRIVER_STATE_SOCKET, new JSONObject(new Gson().toJson(state, DriverState.class)));
            mSocket.emit(KeyString.UPDATE_PAY_STATUS, new JSONObject(new Gson().toJson(paymentStatus, PaymentStatus.class)));
//                mSocket.emit(KeyString.UPDATE_PAY_STATUS, new JSONObject("{id:'" + tripModel.getId() + "',payStatus:pending}"));
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        return totalFare;
    }

    /**
     * calculate distance between two geo location
     *
     * @param prevLatitude
     * @param preLongitude
     * @param newLatitude
     * @param newLongitude
     * @return
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
     * round double value with two floating point
     *
     * @param value
     * @param places
     * @return
     */
    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    /**
     * dispatch trip end API call
     *
     * @param model
     */
    private void dispatchTripEnd(DispatchTripEndRequestModel model) {
        progressDialog = new ProgressDialog(TripBillingActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        ApiInterface apiInterface = JsonApiClient.getApiClient().create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.endDispatchTrip(model);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                Log.i("TAG CHECK DRIVER CODE", String.valueOf(response.code()));
                if (response.code() == 200) {
                    Intent homeIntent = new Intent(TripBillingActivity.this, HomeActivity.class);
                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_MULTIPLE_TASK);
                    new LoginSession(getApplicationContext()).setDriverState(KeyString.ONLINE);
                    DriverState state = new DriverState(mSocket.id(), new LoginSession(getApplicationContext()).getDriverState(), new LoginSession(getApplicationContext()).get_Id());
                    try {
                        mSocket.emit(KeyString.UPDATE_DRIVER_STATE_SOCKET, new JSONObject(new Gson().toJson(state, DriverState.class)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(homeIntent);
                    finish();
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(TripBillingActivity.this).create();
                    alertDialog.setTitle("Something Went Wrong");
                    alertDialog.setMessage("please try again");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Log.i("TAG CHECK DRIVER FAIL", t.getMessage());
                AlertDialog alertDialog = new AlertDialog.Builder(TripBillingActivity.this).create();
                alertDialog.setTitle("Something Went Wrong");
                alertDialog.setMessage("please try again");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });
    }

    /**
     * passenger trip end api call
     *
     * @param model
     */
    private void passengerTripEnd(PassengerTripEndRequestModel model) {
        progressDialog = new ProgressDialog(TripBillingActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        ApiInterface apiInterface = JsonApiClient.getApiClient().create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.endPassengerTrip(model);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                Log.i("TAG CHECK DRIVER CODE", String.valueOf(response.code()));
                if (response.code() == 200) {
                    new LoginSession(getApplicationContext()).setDriverState(KeyString.ONLINE);
                    DriverState state = new DriverState(mSocket.id(), new LoginSession(getApplicationContext()).getDriverState(), new LoginSession(getApplicationContext()).get_Id());
                    try {
                        mSocket.emit(KeyString.UPDATE_DRIVER_STATE_SOCKET, new JSONObject(new Gson().toJson(state, DriverState.class)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    finish();
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(TripBillingActivity.this).create();
                    alertDialog.setTitle("Something Went Wrong");
                    alertDialog.setMessage("please try again \ncode : " + response.code());
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                Log.i("TAG CHECK DRIVER FAIL", t.getMessage());
                AlertDialog alertDialog = new AlertDialog.Builder(TripBillingActivity.this).create();
                alertDialog.setTitle("Something Went Wrong");
                alertDialog.setMessage("please try again");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
