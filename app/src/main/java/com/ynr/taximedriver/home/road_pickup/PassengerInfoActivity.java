package com.ynr.taximedriver.home.road_pickup;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ynr.taximedriver.R;
import com.ynr.taximedriver.config.KeyString;
import com.ynr.taximedriver.config.SoftKeyboard;
import com.ynr.taximedriver.gps.GPSTracker;
import com.ynr.taximedriver.model.roadPickupModel.StartRoadPickupTripRequestModel;
import com.ynr.taximedriver.model.roadPickupModel.StartRoadPickupTripResponceModel;
import com.ynr.taximedriver.rest.ApiInterface;
import com.ynr.taximedriver.rest.JsonApiClient;
import com.ynr.taximedriver.session.LoginSession;
import com.ynr.taximedriver.validation.Formvalidation;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassengerInfoActivity extends AppCompatActivity {
    private Button continueButton;
    private TextView passengerName, mobileNumber, email;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_info);

        try {
            findViewById(R.id.root_layout_passenger_info_activity).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    SoftKeyboard.hideSoftKeyboard(PassengerInfoActivity.this);
                    return false;
                }
            });
        } catch (Exception e) {

        }

        mobileNumber = findViewById(R.id.mobile_number);
        passengerName = findViewById(R.id.passenger_name);
        email = findViewById(R.id.email);

        continueButton = findViewById(R.id.continue_button);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Formvalidation.isEmail(email.getText().toString()) && !email.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Invalid Email Address", Toast.LENGTH_SHORT).show();
                } else if (!Formvalidation.isMobileNumber(mobileNumber.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Invalid Mobile Number", Toast.LENGTH_SHORT).show();
                } else {
                    StartRoadPickupTripRequestModel model = new StartRoadPickupTripRequestModel();
                    model.setFirstName(passengerName.getText().toString());
                    model.setEmail(email.getText().toString());
                    model.setMobile(mobileNumber.getText().toString());
                    model.setVehicleCategory(new LoginSession(getApplicationContext()).getVehicle().getVehicleCategory());
                    model.setVehicleSubCategory(new LoginSession(getApplicationContext()).getVehicle().getVehicleSubCategory());
                    GPSTracker location = new GPSTracker(getApplicationContext());
                    StartRoadPickupTripRequestModel.Location pickupLocation = new StartRoadPickupTripRequestModel.Location();
                    pickupLocation.setAddress(location.getAddressLine(getApplicationContext()));
                    pickupLocation.setLongitude(location.getLongitude());
                    pickupLocation.setLatitude(location.getLatitude());

//                    pickupLocation.setAddress("Unnamed Road, Gampaha, Sri Lanka");
//                    pickupLocation.setLongitude(79.98330386);
//                    pickupLocation.setLatitude(7.08578872);

                    model.setPickupLocation(pickupLocation);
                    model.setDriverId(new LoginSession(getApplicationContext()).getUserDetails().getContent().getId());
                    model.setVehicleId(new LoginSession(getApplicationContext()).getVehicle().getId());

                    tripStart(model);
                }
            }
        });
    }

    private void tripStart(StartRoadPickupTripRequestModel model) {
        progressDialog = new ProgressDialog(PassengerInfoActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        ApiInterface apiInterface = JsonApiClient.getApiClient().create(ApiInterface.class);
        Call<StartRoadPickupTripResponceModel> call = apiInterface.startRoadPickup(model);
        call.enqueue(new Callback<StartRoadPickupTripResponceModel>() {
            @Override
            public void onResponse(Call<StartRoadPickupTripResponceModel> call, Response<StartRoadPickupTripResponceModel> response) {
                progressDialog.dismiss();
                Log.i("TAG_RESPONSE_CODE", String.valueOf(response.code()));
                if (response.code() == 200) {
                    Intent intent = new Intent(getApplicationContext(), RoadPickupPriceCalculatorActivity.class);
                    intent.putExtra(KeyString.ROAD_PICUP_MODEL, new Gson().toJson(response.body()));
                    startActivity(intent);
                    finish();
                } else if (response.code() == 206 || response.code() == 202){
                    AlertDialog alertDialog = new AlertDialog.Builder(PassengerInfoActivity.this).create();
                    alertDialog.setTitle("Service Not Available");
                    alertDialog.setMessage("Service not available in this aria");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } else if (response.code() == 203) {
                    AlertDialog alertDialog = new AlertDialog.Builder(PassengerInfoActivity.this).create();
                    alertDialog.setTitle("Something Went Wrong");
                    alertDialog.setMessage("can't identify your current district. try again later");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(PassengerInfoActivity.this).create();
                    alertDialog.setTitle("Something Went Wrong");
                    alertDialog.setMessage("try again later");
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
            public void onFailure(Call<StartRoadPickupTripResponceModel> call, Throwable t) {
                progressDialog.dismiss();
                Log.i("TAG_ONFAILURE", t.getMessage());
                AlertDialog alertDialog = new AlertDialog.Builder(PassengerInfoActivity.this).create();
                alertDialog.setTitle("Something Went Wrong");
                alertDialog.setMessage("Try again later");
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

}
