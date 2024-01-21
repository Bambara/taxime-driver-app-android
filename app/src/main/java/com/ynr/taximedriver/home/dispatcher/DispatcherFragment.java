package com.ynr.taximedriver.home.dispatcher;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.ynr.taximedriver.AppConstants;
import com.ynr.taximedriver.Helpers;
import com.ynr.taximedriver.R;
import com.ynr.taximedriver.config.KeyString;
import com.ynr.taximedriver.home.driver.DriverFragment;
import com.ynr.taximedriver.model.DispatchHistoryModel;
import com.ynr.taximedriver.model.PaymentResponseModel;
import com.ynr.taximedriver.rest.ApiInterface;
import com.ynr.taximedriver.rest.JsonApiClient;
import com.ynr.taximedriver.session.LoginSession;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DispatcherFragment extends Fragment {
    private Button switchDriverButton, btnWithdraw;
    private FrameLayout dispatchButton, historyButton;
    private TextView tripCount, earnings;
    ProgressDialog progressDialog;
    public DispatchHistoryModel dispatchHistory = null;

    public static DispatcherFragment newInstance() {
        DispatcherFragment fragment = new DispatcherFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View myFragmentView = inflater.inflate(R.layout.fragment_dispatcher, container, false);
        /**
         * set status bar colour
         */
        Window window = getActivity().getWindow();   // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);  // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);   // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.greenOne));

//        getDispatchHistory();

        tripCount = myFragmentView.findViewById(R.id.trip_count);
        earnings = myFragmentView.findViewById(R.id.earning_amount);
        dispatchButton = myFragmentView.findViewById(R.id.dispatch_button);
        dispatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchButton.animate().alpha(0.3f).setDuration(AppConstants.INSTANCE.getButtonAnimationDuration()).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        dispatchButton.animate().alpha(1f).setDuration(AppConstants.INSTANCE.getButtonAnimationDuration()).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                if (!new LoginSession(getContext()).getDispatcherEnable()) {
                                    Helpers.INSTANCE.showAlertDialog(getActivity(),
                                            getString(R.string.dispatcher_foundction_not_enabled),
                                            Helpers.AlertDialogType.WARNING,
                                            confirm -> null);
                                } else {
                                    Intent intent = new Intent(getContext(), DispatcherFormActivity.class);
                                    startActivity(intent);
                                }
                            }
                        }).start();
                    }
                }).start();
            }
        });

        switchDriverButton = myFragmentView.findViewById(R.id.driverButton);
        switchDriverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!new LoginSession(getContext()).getDriverState().equals(KeyString.ONLINE)) {
                    AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                    alertDialog.setTitle("Driver Offline");
                    alertDialog.setMessage("please change driver state as online");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } else {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment, DriverFragment.newInstance()).commit();
                }
            }
        });

        btnWithdraw = myFragmentView.findViewById(R.id.btnWithdraw);
        btnWithdraw.setOnClickListener(v -> {
            double amount = Double.parseDouble(earnings.getText().toString().split(" ")[1]);

            if (amount < 0) {
                withdrawEarnings(amount);
            } else {
                showAlertDialog("Earning Withdraw", "Earning amount is not enough to withdraw", getResources().getDrawable(R.drawable.ic_info));
            }
        });

        historyButton = myFragmentView.findViewById(R.id.history_button);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                historyButton.animate().alpha(0.3f).setDuration(AppConstants.INSTANCE.getButtonAnimationDuration()).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        historyButton.animate().alpha(1f).setDuration(AppConstants.INSTANCE.getButtonAnimationDuration()).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                if (dispatchHistory != null) {
                                    Intent intent = new Intent(getContext(), DispatchHistoryActivity.class);
                                    intent.putExtra(KeyString.DISPATCH_HISTORY_MODEL, new Gson().toJson(dispatchHistory));
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
                                }
                            }
                        }).start();
                    }
                }).start();
            }
        });


        myFragmentView.findViewById(R.id.btnBack2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        return myFragmentView;
    }

    private void showAlertDialog(String title, String message, Drawable icon) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle(title).
                setMessage(message).
                setCancelable(true).
                setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss()).
                setIcon(icon);

        androidx.appcompat.app.AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * get dispatch history API call
     */
    private void getDispatchHistory() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        ApiInterface apiInterface = JsonApiClient.getApiClient().create(ApiInterface.class);
        Call<DispatchHistoryModel> call = apiInterface.getDispatchHistory(new LoginSession(getContext()).getUserDetails().getContent().getId());
        call.enqueue(new Callback<DispatchHistoryModel>() {
            @Override
            public void onResponse(Call<DispatchHistoryModel> call, Response<DispatchHistoryModel> response) {
                progressDialog.dismiss();
                if (response.code() == 200) {
                    dispatchHistory = response.body();
                    tripCount.setText(dispatchHistory.getTotalDispatchesDone() + " " + getResources().getString(R.string.trips));
                    earnings.setText(getResources().getString(R.string.rs) + " " + round(dispatchHistory.getTotalDispatchEarnings(), 2));
                }
            }

            @Override
            public void onFailure(Call<DispatchHistoryModel> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    public void withdrawEarnings(double amount) {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Withdrawing...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        LoginSession loginSession = new LoginSession(getContext());

        /*RefundModel topUpModel = new RefundModel();
        topUpModel.setPartnerAccount(loginSession.getUserDetails().getContent().getNic());
        topUpModel.setAmount(round(dispatchHistory.getTotalDispatchEarnings(), 2));
        topUpModel.setMobileNo(loginSession.getUserDetails().getContent().getContactNo());
        topUpModel.setEmail(loginSession.getUserDetails().getContent().getEmail());
        topUpModel.setGlobalId(loginSession.getUserDetails().getContent().getNic());*/

        JSONObject json = new JSONObject();

        RequestBody body = null;
        try {

//            json.put("partnerAccount", "007107012077");
//            json.put("amount", 10);
//            json.put("mobileNo", "+94771129101");
//            json.put("email", "test@live.com");
//            json.put("globalId", "911850796V");

            //json.put("partnerAccount", loginSession.getUserDetails().getContent().getAccNumber());
            json.put("amount", amount);
            json.put("mobileNo", loginSession.getUserDetails().getMobileNumber());
            json.put("email", loginSession.getUserDetails().getContent().getEmail());
            json.put("globalId", loginSession.getUserDetails().getContent().getNic());

            body = RequestBody.create(
                    okhttp3.MediaType.parse(
                            "application/json; charset=utf-8"
                    ), json.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiInterface apiInterface = JsonApiClient.getPaymentApiClient().create(ApiInterface.class);
        Call<PaymentResponseModel> call = apiInterface.refundWallet(body);

        call.enqueue(new Callback<PaymentResponseModel>() {
            @Override
            public void onResponse(Call<PaymentResponseModel> call, Response<PaymentResponseModel> response) {
                progressDialog.dismiss();
                if (response.code() == 200) {

                    if (response.body().isSuccess() & response.body().getMessage().equals("Account topup successfully")) {

                        //Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();

                        progressDialog.dismiss();
                        getDispatchHistory();
                        showAlertDialog(response.body().getMessage(), getResources().getDrawable(R.drawable.ic_info));

                    } else if (!response.body().isSuccess() & response.body().getMessage().equals("Account topup failed")) {

                        //Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();

                        progressDialog.dismiss();

                        showAlertDialog(response.body().getMessage(), getResources().getDrawable(R.drawable.ic_error));

                    } else if (!response.body().isSuccess() & response.body().getMessage().equals("Account validation failed")) {

                        //Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();

                        progressDialog.dismiss();

                        showAlertDialog(response.body().getMessage(), getResources().getDrawable(R.drawable.ic_error));

                    } else if (!response.body().isSuccess() & response.body().getMessage().equals("Token validation failed")) {

                        //Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();

                        progressDialog.dismiss();

                        showAlertDialog(response.body().getMessage(), getResources().getDrawable(R.drawable.ic_error));
                    }

                } else {
                    //tvOther.setText(getResources().getString(R.string.something_went_wrong_please_try_again_later));
                }
            }

            @Override
            public void onFailure(Call<PaymentResponseModel> call, Throwable t) {
                progressDialog.dismiss();
                //error.setText(getResources().getString(R.string.something_went_wrong_please_try_again_later));
            }
        });
    }

    private void showAlertDialog(String message, Drawable icon) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Earning Withdraw").
                setMessage(message).
                setCancelable(true).
                setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss()).
                setIcon(icon);

        androidx.appcompat.app.AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onResume() {
        getDispatchHistory();
        super.onResume();
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

}
