package com.ynr.taximedriver.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.ynr.taximedriver.R;
import com.ynr.taximedriver.config.KeyString;
import com.ynr.taximedriver.gps.GPSTracker;
import com.ynr.taximedriver.home.driver.OnTripActivity;
import com.ynr.taximedriver.model.tripAcceptModel.DispatchTripAcceptRequestModel;
import com.ynr.taximedriver.model.tripAcceptModel.PassengerTripAcceptRequestModel;
import com.ynr.taximedriver.model.tripAcceptModel.TripAcceptResponseModel;
import com.ynr.taximedriver.model.tripModel.TripModel;
import com.ynr.taximedriver.rest.ApiInterface;
import com.ynr.taximedriver.rest.JsonApiClient;
import com.ynr.taximedriver.session.LoginSession;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.MyViewHolder>{
    public interface TripActionCallback {
        void onAcceptTrip();
    }
    Context context;
    List<TripModel> tripModelList;
    ProgressDialog progressDialog;
    TripActionCallback callback = null;
    // private Handler handler;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        CountDownTimer countDownTimer;
        TextView timer, pickupAddress, dropAddress, vehicleModel, date, distance, totalAmount, accept;
        ProgressBar progressBar;
        public MyViewHolder(View itemView) {
            super(itemView);
            timer = itemView.findViewById(R.id.timer);
            pickupAddress = itemView.findViewById(R.id.pickup_address);
            dropAddress = itemView.findViewById(R.id.btnDropLocation);
            vehicleModel = itemView.findViewById(R.id.tv_vehicleModel);
            date = itemView.findViewById(R.id.dateAndTime);
            distance = itemView.findViewById(R.id.distence);
            totalAmount = itemView.findViewById(R.id.total_amount);
            progressBar = itemView.findViewById(R.id.progress_bar);
            accept = itemView.findViewById(R.id.accept);
        }
    }

    public TripAdapter(Context context, List<TripModel> tripModels){
        this.context = context;
        this.tripModelList = tripModels;
    }

    public void setTripActionCallback(@NonNull TripActionCallback callback) {
        this.callback = callback;
    }

    @Override
    public @NotNull MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_row_item, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        holder.pickupAddress.setText(tripModelList.get(position).getPickupLocation().getAddress());
        holder.dropAddress.setText(tripModelList.get(position).getDropLocations().get(0).getAddress());
        holder.vehicleModel.setText(tripModelList.get(position).getVehicleCategory());
        holder.date.setText(tripModelList.get(position).getPickupDateTime().substring(0,10));
        holder.distance.setText(String.format(context.getString(R.string.numberOf_km), tripModelList.get(position).getDistance()));
        holder.totalAmount.setText(String.format(context.getString(R.string.total_cost_rs), tripModelList.get(position).getHireCost()));
        holder.accept.setOnClickListener(v -> {
            vibe.vibrate(50);
            if (tripModelList.get(position).getType().equals(KeyString.PASSENGER_TRIP)) {
                PassengerTripAcceptRequestModel model = new PassengerTripAcceptRequestModel();
                model.setId(tripModelList.get(position).getId());
                model.setDriverId(new LoginSession(context).getUserDetails().getContent().getId());
                model.setVehicleId(new LoginSession(context).getVehicle().getId());
                model.setPassengerId(tripModelList.get(position).getPassengerDetails().getId());
                model.setType(tripModelList.get(position).getType());
                model.setVehicleSubCategory(tripModelList.get(position).getVehicleSubCategory());
                model.setVehicleCategory(tripModelList.get(position).getVehicleCategory());
                model.setPickupLocation(tripModelList.get(position).getPickupLocation());
                model.setCustomerTelephoneNo(tripModelList.get(position).getPassengerDetails().getContactNumber());
                model.setCurrentLocationLatitude(new GPSTracker(context).getLatitude());
                model.setCurrentLocationLongitude(new GPSTracker(context).getLongitude());
                acceptLiveTrip(model, tripModelList.get(position));
            } else {
                DispatchTripAcceptRequestModel model = new DispatchTripAcceptRequestModel();
                model.setId(tripModelList.get(position).getId());
                model.setDispatcherId(tripModelList.get(position).getDispatcherId());
                LoginSession session = new LoginSession(context);
                model.setDriverId(session.getUserDetails().getContent().getId());
                model.setVehicleId(session.getVehicle().getId());
                model.setType(tripModelList.get(position).getType());
                model.setVehicleSubCategory(tripModelList.get(position).getVehicleSubCategory());
                model.setVehicleCategory(tripModelList.get(position).getVehicleCategory());
                model.setCustomerTelephoneNo(tripModelList.get(position).getMobileNumber());
                DispatchTripAcceptRequestModel.Location location = new DispatchTripAcceptRequestModel.Location();
                location.setAddress(tripModelList.get(position).getPickupLocation().getAddress());
                location.setLatitude(tripModelList.get(position).getPickupLocation().getLatitude());
                location.setLongitude(tripModelList.get(position).getPickupLocation().getLongitude());
                model.setPickupLocation(location);
                acceptDispatch(model, tripModelList.get(position));
            }
            if (callback != null) {
                callback.onAcceptTrip();
            }
        });

        if (tripModelList.get(position).getType().equals(KeyString.PASSENGER_TRIP)) {

        } else {

        }

        float progress = ((tripModelList.get(position).getExpireTime() - (int)System.currentTimeMillis()/1000)*100)/tripModelList.get(position).getValidTime();
        Log.i("TAG_PROGRESS", String.valueOf(progress));
        //progressBar.setProgress(Integer.parseInt(pro));
        if (holder.countDownTimer != null) holder.countDownTimer.cancel();
        holder.countDownTimer = new CountDownTimer((tripModelList.get(position).getExpireTime() * 1000L - (int) System.currentTimeMillis()), 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                try {
                    holder.progressBar.setProgress((int) (10000 - Math.round(((double) millisUntilFinished / (tripModelList.get(position).getValidTime() * 1000)) * 10000)));
                    holder.timer.setText((int) millisUntilFinished / 1000 + "S");
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    @Override
    public int getItemCount() {
        return tripModelList.size();
    }

    /**
     * passenger trip accept api call
     * start OnTripActivity
     * passing tripModel and pricingModel through intent
     * @param model
     * @param tripModel
     */
    private void acceptLiveTrip(PassengerTripAcceptRequestModel model, final TripModel tripModel) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        String s = new Gson().toJson(model);
        System.out.println(s);

        ApiInterface apiInterface = JsonApiClient.getApiClient().create(ApiInterface.class);
        Call<TripAcceptResponseModel> call = apiInterface.acceptLiveTrip(model);
        call.enqueue(new Callback<TripAcceptResponseModel>() {
            @Override
            public void onResponse(@NotNull Call<TripAcceptResponseModel> call, @NotNull Response<TripAcceptResponseModel> response) {
                progressDialog.dismiss();
                if (response.code() == 200) {
                    Intent intent = new Intent(context, OnTripActivity.class);
                    intent.putExtra(KeyString.TRIP_MODEL, new Gson().toJson(tripModel));
                    intent.putExtra(KeyString.TRIP_PRICE_MODEL, new Gson().toJson(response.body()));
                    context.startActivity(intent);
                }else if (response.code() == 208) {
                    Toast.makeText(context, "Trip Expired", Toast.LENGTH_SHORT).show();
                } else {
                    System.out.println(tripModel.getVehicleCategory());
                    Toast.makeText(context, "Response Code : " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<TripAcceptResponseModel> call, @NotNull Throwable t) {
                progressDialog.dismiss();
                System.out.println(t);
                Toast.makeText(context, "Something went wrong fuck...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * dispatch trip accept API call
     * start OnTripActivity
     * passing tripModel and pricingModel through intent
     * @param model
     * @param tripModel
     */
    private void acceptDispatch(DispatchTripAcceptRequestModel model, final TripModel tripModel) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        ApiInterface apiInterface = JsonApiClient.getApiClient().create(ApiInterface.class);
        Call<TripAcceptResponseModel> call = apiInterface.acceptDispatch(model);
        call.enqueue(new Callback<TripAcceptResponseModel>() {
            @Override
            public void onResponse(@NotNull Call<TripAcceptResponseModel> call, @NotNull Response<TripAcceptResponseModel> response) {
                progressDialog.dismiss();
                if (response.code() == 200) {
                    Intent intent = new Intent(context, OnTripActivity.class);
                    intent.putExtra(KeyString.TRIP_MODEL, new Gson().toJson(tripModel));
                    intent.putExtra(KeyString.TRIP_PRICE_MODEL, new Gson().toJson(response.body()));
                    context.startActivity(intent);
                }else if (response.code() == 208) {
                    Toast.makeText(context, "Trip Expired", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Something went wrong...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<TripAcceptResponseModel> call, @NotNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(context, "Something went wrong...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * round double value with two floating point
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
}
