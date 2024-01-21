package com.ynr.taximedriver.home.dispatcher;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.model.Place;
import com.ynr.taximedriver.R;
import com.ynr.taximedriver.adapters.CategoryAdapter;
import com.ynr.taximedriver.common.LocationPickerActivity;
import com.ynr.taximedriver.config.KeyString;
import com.ynr.taximedriver.config.Permission;
import com.ynr.taximedriver.config.SoftKeyboard;
import com.ynr.taximedriver.gps.GPSTracker;
import com.ynr.taximedriver.model.dispatchModel.DispatchModel;
import com.ynr.taximedriver.model.dispatchModel.Location;
import com.ynr.taximedriver.model.mapDistanceModel.MapDistanceModel;
import com.ynr.taximedriver.model.vehicalCategoryModel.VehicleCategoryRequestModel;
import com.ynr.taximedriver.model.vehicalCategoryModel.VehicleCategoryResponseModel;
import com.ynr.taximedriver.rest.ApiInterface;
import com.ynr.taximedriver.rest.JsonApiClient;
import com.ynr.taximedriver.rest.mapApiClient;
import com.ynr.taximedriver.session.LoginSession;
import com.ynr.taximedriver.validation.Formvalidation;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DispatcherFormActivity extends AppCompatActivity implements OnMapReadyCallback, CategoryAdapter.SingleClickListener {
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private ProgressDialog progressDialog;
    private GoogleMap gMap;
    private MapView mapView;
    LatLng latLng;
    Polyline line;
    Marker pickupMarker;
    Marker dropMarker;
    CategoryAdapter categoryAdapter;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    private int mYear, mMonth, mDay;
    private EditText name, mobileNumber;
    private ImageView passengerMinusButton, passengerPlusButton, costMinusButton, costPlusButton;
    private TextView numberOfPassenger, unitCost, totalCost, dateAndTime, distance, notes, categoryName, dispatcherId, tvDistance;
    private int hr, min;
    private ScrollView scrollView;
    private DispatchModel dispatchModel;
    private VehicleCategoryResponseModel vehicleCategoryList;
    private VehicleCategoryResponseModel.Content selectedVehicleCategory;
    private Button orderButton;
    private View mapOverLayer, bottomSection;
    private int selectedCategoryPosition = 0;
    private int passengerCount = 1;
    private static final int PICKUP_LOCATION_REQUEST_CODE = 100;
    private static final int DROP_LOCATION_REQUEST_CODE = 200;
    private Place pickupLocation = null;
    private Place dropLocation = null;
    private Button btnPickupLocation, btnDropLocation;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatcher_form);
        findViewById(R.id.root_layout).setOnTouchListener((v, event) -> {
            SoftKeyboard.hideSoftKeyboard(DispatcherFormActivity.this);
            return false;
        });

        dispatchModel = new DispatchModel(); //dispatch data model
        dispatchModel.setNumberOfPassengers(1);
        dispatchModel.setType(KeyString.TYPE);
        dispatchModel.setDispatcherId(new LoginSession(getApplicationContext()).getUserDetails().getContent().getId());
        dispatcherId = findViewById(R.id.tvDispatcherId);
        tvDistance = findViewById(R.id.tvTripDistanceValue);
        dispatcherId.setText(String.format(getString(R.string.dispatcher_id), dispatchModel.getDispatcherId()));
        distance = findViewById(R.id.tvTripDistance);
        bottomSection = findViewById(R.id.bottomSection);
        totalCost = findViewById(R.id.tvTotalCost);
        notes = findViewById(R.id.notes);
        dateAndTime = findViewById(R.id.dateAndTime);
        scrollView = findViewById(R.id.custom_scrollview);
        orderButton = findViewById(R.id.order_button);
        orderButton.setOnClickListener(view -> {
            dispatchModel.setNotes(notes.getText().toString());
            if (formValidate())
                addDispatch();
            else
                Toast.makeText(getApplicationContext(), "Fill all the required fields", Toast.LENGTH_SHORT).show();
        });

        GPSTracker location = new GPSTracker(getApplicationContext());
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);
        btnPickupLocation = findViewById(R.id.btnPickupLocation);
        btnDropLocation = findViewById(R.id.btnDropLocation);
        mapOverLayer = findViewById(R.id.map_over_layer);
        mapOverLayer.setOnTouchListener((v, event) -> {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    // Disallow ScrollView to intercept touch events.
                    scrollView.requestDisallowInterceptTouchEvent(true);
                    // Disable touch on transparent view
                    return false;
                case MotionEvent.ACTION_UP:
                    // Allow ScrollView to intercept touch events.
                    scrollView.requestDisallowInterceptTouchEvent(false);
                    return true;
                default:
                    return true;
            }
        });
        ImageView backButton = findViewById(R.id.btnBack2);
        backButton.setOnClickListener(view -> onBackPressed());
        formEditTextChecker();
        setTime();
        setDate();
        getCategory();
        setNumberOfPassenger();
    }

    public void pickupLocationOnClick(View view) {
        openLocationPickerForRequestCode(PICKUP_LOCATION_REQUEST_CODE);
    }

    public void dropLocationOnClick(View view) {
        openLocationPickerForRequestCode(DROP_LOCATION_REQUEST_CODE);
    }

    private void openLocationPickerForRequestCode(int requestCode) {
        Intent intent = new Intent(this, LocationPickerActivity.class);
        startActivityForResult(intent, requestCode);
    }

    /**
     * popup method
     */
    private void popup(String message, String buttonText, boolean state, final int response) {

        // Get the widgets reference from XML layout
        LinearLayout linearLayout = findViewById(R.id.root_layout);
        // Inflate the custom layout/view
        View customView = View.inflate(this, R.layout.singale_button_popup, null);
        /*
            public PopupWindow (View contentView, int width, int height)
                Create a new non focusable popup window which can display the contentView.
                The dimension of the window must be passed to this constructor.

                The popup does not provide any background. This should be handled by
                the content view.

            Parameters
                contentView : the popup's content
                width : the popup's width
                height : the popup's height
        */
        // Initialize a new instance of popup window
        final PopupWindow mPopupWindow = new PopupWindow(customView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mPopupWindow.setOutsideTouchable(false);
        // Set an elevation value for popup window
        // Call requires API level 21
        if (Build.VERSION.SDK_INT >= 21) {
            mPopupWindow.setElevation(5.0f);
        }
        // Get a reference for the custom view close button
        TextView popupMessage = customView.findViewById(R.id.popup_message);
        ImageView popupImage = customView.findViewById(R.id.popup_image);
        Button popupButton = customView.findViewById(R.id.popup_button);
        popupMessage.setText(message);
        if (state) popupImage.setImageResource(R.drawable.right_icon);
        else popupImage.setImageResource(R.drawable.wrong_icon);

        popupButton.setText(buttonText);
        popupButton.setOnClickListener(view -> {
            mPopupWindow.dismiss();
        });
        mPopupWindow.showAtLocation(linearLayout, Gravity.CENTER, 0, 0);
    }

    /**
     * dispatch api call
     */
    private void addDispatch() {
        progressDialog = new ProgressDialog(DispatcherFormActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        ApiInterface apiInterface = JsonApiClient.getApiClient().create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.addDispatch(dispatchModel);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.code() == 200) {
                    popup(getResources().getString(R.string.almost_done), getResources().getString(R.string.done), true, 200);
                } else if (response.code() == 202) {
                    popup(getResources().getString(R.string.no_online_drivers_in_pick), getResources().getString(R.string.cancel), false, 202);
                } else if (response.code() == 203) {
                    popup(getResources().getString(R.string.no_online_drivers), getResources().getString(R.string.cancel), false, 203);
                } else {
                    popup(getResources().getString(R.string.unexpected_error), getResources().getString(R.string.cancel), false, 0);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                progressDialog.dismiss();
                popup(getResources().getString(R.string.unexpected_error), getResources().getString(R.string.cancel), false, 0);
            }
        });
    }

    /**
     * dispatch form validation method
     */
    private boolean formValidate() {
        boolean state = true;
        if (dispatchModel.getCustomerName() == null || !Formvalidation.isName(dispatchModel.getCustomerName())) {
            name.setError("Required field");
            state = false;
        }
        if (dispatchModel.getMobileNumber() == null || !Formvalidation.isMobileNumber(dispatchModel.getMobileNumber())) {
            mobileNumber.setError("Required field");
            state = false;
        }
        if (dispatchModel.getPickupLocation() == null) {
            state = false;
        }
        if (dispatchModel.getDropLocations() == null) {
            state = false;
        }
        if (dispatchModel.getVehicleCategory() == null && dispatchModel.getVehicleSubCategory() == null) {
            state = false;
        }
        return state;
    }

    /**
     * method for get all category using pickup time and location
     */
    private void getCategory() {
        progressDialog = new ProgressDialog(DispatcherFormActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        GPSTracker gpsTracker = new GPSTracker(getApplicationContext());

        VehicleCategoryRequestModel requestModel = new VehicleCategoryRequestModel();
        if (dispatchModel.getPickupLocation() == null) {
            requestModel.setAddress(gpsTracker.getAddressLine(getApplicationContext()));
            requestModel.setLatitude(gpsTracker.getLatitude());
            requestModel.setLongitude(gpsTracker.getLongitude());
        } else {
            requestModel.setAddress(dispatchModel.getPickupLocation().getAddress());
            requestModel.setLatitude(dispatchModel.getPickupLocation().getLatitude());
            requestModel.setLongitude(dispatchModel.getPickupLocation().getLongitude());
            requestModel.setDate(dispatchModel.getPickupDate());
            requestModel.setTime(dispatchModel.getPickupTime());
        }

        ApiInterface apiInterface = JsonApiClient.getApiClient().create(ApiInterface.class);
        Call<VehicleCategoryResponseModel> call = apiInterface.getVehicleCategory(requestModel);
        call.enqueue(new Callback<VehicleCategoryResponseModel>() {
            @Override
            public void onResponse(@NotNull Call<VehicleCategoryResponseModel> call, @NotNull Response<VehicleCategoryResponseModel> response) {
                progressDialog.dismiss();
                Log.i("TAG_RESPONSE_CODE", String.valueOf(response.code()));
                if (response.code() == 200) {
                    vehicleCategoryList = response.body();
                    assert response.body() != null;
                    if (response.body().getContent().size() == 0) {
                        popup(getResources().getString(R.string.unexpected_error), getResources().getString(R.string.cancel), false, 0);
                        return;
                    }
                    setCategory(response.body());
                } else {
                    popup(getResources().getString(R.string.unexpected_error), getResources().getString(R.string.cancel), false, 0);
                }

            }

            @Override
            public void onFailure(@NotNull Call<VehicleCategoryResponseModel> call, @NotNull Throwable t) {
                progressDialog.dismiss();
                Log.i("TAG_ONFAILURE", t.getMessage());
                popup(getResources().getString(R.string.unexpected_error), getResources().getString(R.string.cancel), false, 0);
            }
        });
    }

    /**
     * set category and subcategory view
     */
    private void setCategory(VehicleCategoryResponseModel category) {

        recyclerView = findViewById(R.id.category_recyclerview);
        categoryName = findViewById(R.id.tv_selected_category_name);
        linearLayoutManager = new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        category.setUnitCost(category.getContent().get(0).getLowerBidLimit());
        categoryAdapter = new CategoryAdapter(getApplicationContext(), category, 0);
        recyclerView.setAdapter(categoryAdapter);
        categoryAdapter.setOnItemClickListener(this);
        categoryName.setText(category.getContent().get(0).getCategoryTag());
        dispatchModel.setVehicleCategory(category.getContent().get(0).getCategoryTag());
        dispatchModel.setVehicleSubCategory(category.getContent().get(0).getSubCategoryName());
        dispatchModel.setUnitCost(category.getContent().get(0).getLowerBidLimit());
        selectedVehicleCategory = vehicleCategoryList.getContent().get(0);
        setUnitCost();
        priceCalculation();
        dispatchModel.setValidTime(category.getContent().get(0).getValidTime());
        dispatchModel.setOperationRadius(category.getContent().get(0).getPriceSelection().get(0).getTimeBase().get(0).getRadius());
        if (passengerCount > selectedVehicleCategory.getSeatCount()) {
            passengerCount = selectedVehicleCategory.getSeatCount();
            numberOfPassenger.setText(String.valueOf(passengerCount));
        }
    }

    @Override
    public void onItemClickListener(int position, List<VehicleCategoryResponseModel.Content> categories) {
        categoryName.setText(categories.get(position).getCategoryTag());
        dispatchModel.setVehicleCategory(categories.get(position).getCategoryTag());
        dispatchModel.setVehicleSubCategory(categories.get(position).getSubCategoryName());
        dispatchModel.setUnitCost(categories.get(position).getLowerBidLimit());
        dispatchModel.setValidTime(categories.get(position).getValidTime());
        dispatchModel.setOperationRadius(categories.get(position).getPriceSelection().get(0).getTimeBase().get(0).getRadius());
        selectedVehicleCategory = vehicleCategoryList.getContent().get(position);
        if (passengerCount > selectedVehicleCategory.getSeatCount()) {
            passengerCount = selectedVehicleCategory.getSeatCount();
            numberOfPassenger.setText(String.valueOf(passengerCount));
        }
        unitCost.setText(String.format(getString(R.string.price_per_km), selectedVehicleCategory.getLowerBidLimit()));
        selectedCategoryPosition = position;
        //recyclerView.smoothScrollToPosition(linearLayoutManager.findFirstVisibleItemPosition() + 1);
        recyclerView.smoothScrollToPosition(position);
        priceCalculation();
    }
    /**
     * set markers for the selected vehicle category
     */
//    private void setVehicleIconOnMap() {
//        if (vehicleMarkers.isEmpty()) {
//            if (gmap != null && onlineDriversList != null && selectedVehicleCategory != null) {
//                Bitmap bitmap;
//                for (i in onlineDriversList!!.indices) {
//                    var j = 0
//                    while (j < categoryIconBitmapList.size) {
//                        if (categoryIconBitmapList[j].tag == onlineDriversList!![i].vehicleSubCategory) {
//                            bitmap = categoryIconBitmapList[j].bitmap
//                            j = 1000
//                        }
//                        j++
//                    }
//                    if (bitmap != null) {
//                        vehicleMarkers.add(0, gmap!!.addMarker(MarkerOptions().position(LatLng(onlineDriversList!![i].currentLocation.latitude, onlineDriversList!![i].currentLocation.longitude))
//                                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
//                                .visible(false)
//                                .title(onlineDriversList!![i].vehicleSubCategory)
//                                .snippet(onlineDriversList!![i].driverId)))
//                    }
//                }
//                for (k in vehicleMarkers.indices) {
//                    if (vehicleMarkers[k].title == selectedVehicleCategory!!.subCategoryName) {
//                        vehicleMarkers[k].isVisible = true
//                    }
//                }
//            }
//        } else {
//            if (gmap != null && onlineDriversList != null && selectedVehicleCategory != null) {
//                for (i in vehicleMarkers.indices) {
//                    var count = 0
//                    var j = 0
//                    while (j < onlineDriversList!!.size) {
//
//                        if (vehicleMarkers.size > i) {
//                            if (vehicleMarkers[i].snippet == onlineDriversList!![j].driverId) {
//                                vehicleMarkers[i].position = LatLng(onlineDriversList!![j].currentLocation.latitude, onlineDriversList!![j].currentLocation.longitude)
//                                count = 1
//                            }
//                        }
//
//                        j++
//                    }
//                    if (count == 0 ) {
//                        if (vehicleMarkers.size > i) {
//                            vehicleMarkers[i].isVisible = false
//                            vehicleMarkers.removeAt(i)
//                        }
//                    }
//                }
//                for (i in onlineDriversList!!.indices) {
//                    var bitmap: Bitmap? = null
//                    run {
//                        var j = 0
//                        while (j < categoryIconBitmapList.size) {
//                            if (categoryIconBitmapList[j].tag == onlineDriversList!![i].vehicleSubCategory) {
//                                bitmap = categoryIconBitmapList[j].bitmap
//                                j = 1000
//                            }
//                            j++
//                        }
//                    }
//                    var count = 0
//                    for (j in vehicleMarkers.indices) {
//                        if (onlineDriversList!![i].driverId == vehicleMarkers[j].snippet) {
//                            count = 1
//                        }
//                    }
//                    if (count == 0 && bitmap != null) {
//                        vehicleMarkers.add(0, gmap!!.addMarker(MarkerOptions().position(LatLng(onlineDriversList!![i].currentLocation.latitude, onlineDriversList!![i].currentLocation.longitude))
//                                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
//                                .visible(false)
//                                .title(onlineDriversList!![i].vehicleSubCategory)
//                                .snippet(onlineDriversList!![i].driverId)))
//                    }
//                }
//                for (k in vehicleMarkers.indices) {
//                    vehicleMarkers[k].isVisible = vehicleMarkers[k].title == selectedVehicleCategory!!.subCategoryName
//                }
//            }
//        }
//    }

    /**
     * map path api call
     */
    private void mapRoute() {
        if (pickupLocation == null || dropLocation == null) {
            return;
        }
        bottomSection.setVisibility(View.VISIBLE);
        LatLng origin = pickupLocation.getLatLng();
        LatLng dest = dropLocation.getLatLng();
        ApiInterface apiInterface = mapApiClient.getApiClient().create(ApiInterface.class);
        assert origin != null;
        assert dest != null;
        Call<MapDistanceModel> call = apiInterface.getDistanceDuration("metric", origin.latitude + "," + origin.longitude, dest.latitude + "," + dest.longitude, "driving");
        call.enqueue(new Callback<MapDistanceModel>() {
            @Override
            public void onResponse(@NotNull Call<MapDistanceModel> call, @NotNull Response<MapDistanceModel> response) {
                try {
                    //Remove previous line from map
                    if (line != null) {
                        line.remove();
                    }
                    assert response.body() != null;
                    String encodedString = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
                    List<LatLng> list = decodePoly(encodedString);
                    line = gMap.addPolyline(new PolylineOptions().addAll(list).width(10).color(Color.RED).geodesic(true));
                    Location pickupLocation = new Location(response.body().getRoutes().get(0).getLegs().get(0).getPickupAddress(),
                            response.body().getRoutes().get(0).getLegs().get(0).getPickupLocation().getLat(),
                            response.body().getRoutes().get(0).getLegs().get(0).getPickupLocation().getLng());
                    dispatchModel.setPickupLocation(pickupLocation);
                    Location dropLocation = new Location(response.body().getRoutes().get(0).getLegs().get(0).getDropAddress(),
                            response.body().getRoutes().get(0).getLegs().get(0).getDropLocation().getLat(),
                            response.body().getRoutes().get(0).getLegs().get(0).getDropLocation().getLng());
                    List<Location> dropLocationList = new ArrayList<>();
                    dropLocationList.add(dropLocation);
                    dispatchModel.setDropLocations(dropLocationList);
                    dispatchModel.setDistance(response.body().getRoutes().get(0).getLegs().get(0).getDistance().getValue() / 1000);
                    tvDistance.setText(String.format(getString(R.string.numberOf_km), dispatchModel.getDistance()));
                    getCategory();
                    LatLngBounds.Builder builder = LatLngBounds.builder();
                    for (LatLng latLng : list) {
                        builder.include(latLng);
                    }
                    if (pickupMarker != null) {
                        pickupMarker.remove();
                    }
                    if (dropMarker != null) {
                        dropMarker.remove();
                    }
                    int height = 160;
                    int width = 92;
                    Bitmap b = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                            R.drawable.pickup_location_marker_pointed);
                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                    pickupMarker = gMap.addMarker(new MarkerOptions()
                            .position(origin)
                            .title("Pickup Location")
                            .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                    dropMarker = gMap.addMarker(new MarkerOptions()
                            .position(dest)
                            .title("Drop Location"));
                    gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(
                            builder.build(), 50));
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
     * method for set time
     */
    private void setTime() {
        int hours = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minutes = Calendar.getInstance().get(Calendar.MINUTE);
        Serializable sHr = hours < 10 ? "0" + hours : hours;
        Serializable sMin = minutes < 10 ? "0" + minutes : minutes;
        dispatchModel.setPickupTime(sHr + ":" + sMin + ":00");
        updateTime(hours, minutes);
    }

    private final TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            // TODO Auto-generated method stub
            hr = hourOfDay;
            min = minutes;
            Serializable sHr = hourOfDay < 10 ? "0" + hourOfDay : hourOfDay;
            Serializable sMin = minutes < 10 ? "0" + minutes : minutes;
            dispatchModel.setPickupTime(sHr + ":" + sMin + ":00");
            if (dispatchModel.getPickupLocation() != null) {
                getCategory();
            }
            updateTime(hr, min);
            Log.i("TAG_TIME", dispatchModel.getPickupTime());
        }
    };

    private void updateTime(int hours, int mins) {
        String timeSet;
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12) timeSet = "PM";
        else timeSet = "AM";
        String minutes;
        if (mins < 10) minutes = "0" + mins;
        else minutes = String.valueOf(mins);
        String aTime = String.valueOf(hours) + ':' + minutes + " " + timeSet;
        dateAndTime.setText(aTime);
    }

    /**
     * method for the set calenderView
     */
    private void setDate() {


        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        Serializable sMonth = (mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1);
        Serializable sDay = mDay < 10 ? "0" + mDay : mDay;

        dateAndTime.setText(mYear + "-" + sMonth + "-" + sDay);
        dispatchModel.setPickupDate(mYear + "-" + sMonth + "-" + sDay);

        dateAndTime.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(DispatcherFormActivity.this, (view1, year, monthOfYear, dayOfMonth) -> {
                Serializable sMonth1 = (monthOfYear + 1) < 10 ? "0" + (monthOfYear + 1) : (monthOfYear + 1);
                Serializable sDay1 = dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth;
                dateAndTime.setText(year + "-" + sMonth1 + "-" + sDay1);
                dispatchModel.setPickupDate(year + "-" + sMonth1 + "-" + sDay1);
                new TimePickerDialog(DispatcherFormActivity.this, timePickerListener, hr, min, false).show();
            }, mYear, mMonth, mDay);
            datePickerDialog.show();
        });
    }

    /**
     * validate the text input when text changed
     */
    private void formEditTextChecker() {
        name = findViewById(R.id.txtName);
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                dispatchModel.setCustomerName(s.toString());
                if (!Formvalidation.isName(s.toString())) {
                    name.setError("Required field");
                }
            }
        });
        mobileNumber = findViewById(R.id.mobile_number);
        mobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                dispatchModel.setMobileNumber(s.toString());
                if (!Formvalidation.isMobileNumber(s.toString())) {
                    mobileNumber.setError("Invalid number");
                }
            }
        });
    }

    /**
     * method for the set number of passengers
     */
    private void setNumberOfPassenger() {
        final Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        passengerMinusButton = findViewById(R.id.btnPassengerMinus);
        passengerPlusButton = findViewById(R.id.btnPassengerPlus);
        numberOfPassenger = findViewById(R.id.tvPassengerCount);
        numberOfPassenger.setText(String.valueOf(passengerCount));
        passengerPlusButton.setOnClickListener(v -> {
            vibe.vibrate(40);
            if (passengerCount < selectedVehicleCategory.getSeatCount()) {
                passengerCount++;
                numberOfPassenger.setText(String.valueOf(passengerCount));
                dispatchModel.setNumberOfPassengers(passengerCount);
            }
        });
        passengerMinusButton.setOnClickListener(v -> {
            vibe.vibrate(40);
            if (passengerCount > 1) {
                passengerCount--;
                numberOfPassenger.setText(String.valueOf(passengerCount));
                dispatchModel.setNumberOfPassengers(passengerCount);
            }
        });
    }
    /**
     * method for the set unit cost
     */
    private void setUnitCost() {
        final Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        costMinusButton = findViewById(R.id.btnCostMinus);
        costPlusButton = findViewById(R.id.btnCostPlus);
        unitCost = findViewById(R.id.tvHireCostValue);
        unitCost.setText(String.format(getString(R.string.price_per_km),dispatchModel.getUnitCost()));

        costPlusButton.setOnClickListener(v -> {
            vibe.vibrate(40);
            int count = dispatchModel.getUnitCost();
            if (count < selectedVehicleCategory.getUpperBidLimit())
                count++;
            dispatchModel.setUnitCost(count);
            unitCost.setText(String.format(getString(R.string.price_per_km), count));
            priceCalculation();
        });
        costMinusButton.setOnClickListener(v -> {
            vibe.vibrate(40);
            int count = dispatchModel.getUnitCost();
            if (count > selectedVehicleCategory.getLowerBidLimit())
                count--;
            dispatchModel.setUnitCost(count);
            unitCost.setText(String.format(getString(R.string.price_per_km),count));
            priceCalculation();
        });
    }

    /**
     * price calculation method - new price calculation by ghost
     */
    private void priceCalculation() {

        double totalCost = 0;

        if (vehicleCategoryList != null || dispatchModel.getDistance() != 0.0f) {//return 0;

            /*check vehicle category price */
            if (selectedVehicleCategory.getPriceSelection() != null) {

                if (dispatchModel.getDistance() <= selectedVehicleCategory.getPriceSelection().get(0).getTimeBase().get(0).getMinimumKM()) {

                    totalCost = (int) (selectedVehicleCategory.getPriceSelection().get(0).getTimeBase().get(0).getBaseFare()
                            + selectedVehicleCategory.getPriceSelection().get(0).getTimeBase().get(0).getMinimumFare());

                } else if (selectedVehicleCategory.getPriceSelection().get(0).getTimeBase().get(0).getBelowAboveKMRange() > 0) {

                    if (dispatchModel.getDistance() <= selectedVehicleCategory.getPriceSelection().get(0).getTimeBase().get(0).getBelowAboveKMRange()) {

                        totalCost = (int) (selectedVehicleCategory.getPriceSelection().get(0).getTimeBase().get(0).getBaseFare()
                                + selectedVehicleCategory.getPriceSelection().get(0).getTimeBase().get(0).getMinimumFare()
                                + (dispatchModel.getDistance() - selectedVehicleCategory.getPriceSelection().get(0).getTimeBase().get(0).getMinimumKM()) * dispatchModel.getUnitCost());

                    } else if (dispatchModel.getDistance() > selectedVehicleCategory.getPriceSelection().get(0).getTimeBase().get(0).getBelowAboveKMRange()) {

                        totalCost = (int) (selectedVehicleCategory.getPriceSelection().get(0).getTimeBase().get(0).getBaseFare()
                                + selectedVehicleCategory.getPriceSelection().get(0).getTimeBase().get(0).getMinimumFare()
                                + (selectedVehicleCategory.getPriceSelection().get(0).getTimeBase().get(0).getBelowAboveKMRange() - selectedVehicleCategory.getPriceSelection().get(0).getTimeBase().get(0).getMinimumKM()) * dispatchModel.getUnitCost()
                                + (dispatchModel.getDistance() - selectedVehicleCategory.getPriceSelection().get(0).getTimeBase().get(0).getBelowAboveKMRange()) * selectedVehicleCategory.getPriceSelection().get(0).getTimeBase().get(0).getAboveKMFare());
                    }
                } else {
                    totalCost = (int) (selectedVehicleCategory.getPriceSelection().get(0).getTimeBase().get(0).getBaseFare()
                            + selectedVehicleCategory.getPriceSelection().get(0).getTimeBase().get(0).getMinimumFare()
                            + (dispatchModel.getDistance() - selectedVehicleCategory.getPriceSelection().get(0).getTimeBase().get(0).getMinimumKM()) * dispatchModel.getUnitCost());

                }
                dispatchModel.setTotalCost(totalCost);
            }
        }
        this.totalCost.setText(String.format(getString(R.string.total_cost_rs), totalCost));
    }

    /**
     * round double value with two floating point
     *
     * @param value  -
     * @param places -
     * @return Double
     */
    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Permission permission = new Permission(getApplicationContext(), this);
        gMap = googleMap;
        gMap.setMinZoomPreference(1);
        gMap.setMaxZoomPreference(20);
        if (!permission.isLocationPermissionGranted()) {
            permission.checkPermissions();
            return;
        }
        gMap.setMyLocationEnabled(true);
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
        gMap.setOnMyLocationButtonClickListener(() -> {
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
            return false;
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
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == PICKUP_LOCATION_REQUEST_CODE) {
                pickupLocation = data.getParcelableExtra(LocationPickerActivity.SELECTED_PLACE);
                if (Objects.requireNonNull(pickupLocation.getName()).isEmpty()) {
                    btnPickupLocation.setText(pickupLocation.getAddress());
                } else {
                    btnPickupLocation.setText(pickupLocation.getName());
                }
                mapRoute();
            } else if (requestCode == DROP_LOCATION_REQUEST_CODE) {
                dropLocation = data.getParcelableExtra(LocationPickerActivity.SELECTED_PLACE);
                if (Objects.requireNonNull(dropLocation.getName()).isEmpty()) {
                    btnDropLocation.setText(dropLocation.getAddress());
                } else {
                    btnDropLocation.setText(dropLocation.getName());
                }
                mapRoute();
            }
        }
    }
}
