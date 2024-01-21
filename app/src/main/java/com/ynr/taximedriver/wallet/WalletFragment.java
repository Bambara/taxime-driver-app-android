package com.ynr.taximedriver.wallet;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.ynr.taximedriver.R;
import com.ynr.taximedriver.model.PaymentResponseModel;
import com.ynr.taximedriver.model.WalletDataModel;
import com.ynr.taximedriver.rest.ApiInterface;
import com.ynr.taximedriver.rest.JsonApiClient;
import com.ynr.taximedriver.session.LoginSession;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Objects;

import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletFragment extends Fragment {
    private TextView cashEarnings, cardEarnings, walletPoints, tvOther;
    ProgressDialog progressDialog;

    public static WalletFragment newInstance() {
        return new WalletFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View myFragmentView = inflater.inflate(R.layout.fragment_walet, container, false);

        cashEarnings = myFragmentView.findViewById(R.id.cashEarnings);
        cardEarnings = myFragmentView.findViewById(R.id.cardEarnings);
        walletPoints = myFragmentView.findViewById(R.id.wallet_points);
        tvOther = myFragmentView.findViewById(R.id.tvOther);
        ImageView backButton = myFragmentView.findViewById(R.id.btnBack2);
        Button btnRecharge = myFragmentView.findViewById(R.id.btnRecharge);
        Button btnExchangeCard = myFragmentView.findViewById(R.id.btnExchangeCard);
        //btnExchangeCard = myFragmentView.findViewById(R.id.btnExchangeCard);
        Button btnExchange_02 = myFragmentView.findViewById(R.id.btnExchange_02);


        backButton.setOnClickListener(v -> requireActivity().onBackPressed());


        btnRecharge.setOnClickListener(v -> {
            //topUpWallet();
            loadRecharge();
        });

        btnExchangeCard.setOnClickListener(v -> {
            double earnings = Double.parseDouble(this.cardEarnings.getText().toString().split(" ")[1]);
            if (earnings > 100) {
                withdrawCardEarnings(earnings);
            } else {
                showAlertDialog("Earning Withdraw", "Earning amount is not enough to withdraw. Minimum hold value is Rs: 100", getResources().getDrawable(R.drawable.ic_info));
            }
        });

        btnExchange_02.setOnClickListener(v -> {
            //topUpWallet();
            withdrawOtherEarnings(0);
        });

        getWalletData();

        return myFragmentView;
    }

    /**
     * get wallet data API call
     */
    private void getWalletData() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Updating...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        ApiInterface apiInterface = JsonApiClient.getApiClient().create(ApiInterface.class);
        Call<WalletDataModel> call = apiInterface.getWalletData(new LoginSession(getContext()).getUserDetails().getContent().getId());
//        Call<WalletDataModel> call = apiInterface.getWalletData("5ff99a6aebe130719c628488");
        call.enqueue(new Callback<WalletDataModel>() {
            @Override
            public void onResponse(@NonNull Call<WalletDataModel> call, @NonNull Response<WalletDataModel> response) {
                progressDialog.dismiss();
                if (response.code() == 200) {

                    assert response.body() != null;
                    try {
                        double cash = response.body().getContent().getTransactions().get(0).getTransAmount();
                        cashEarnings.setText("LKR: " + cash);
                    } catch (Exception e) {
                        cashEarnings.setText("LKR: " + 0.00);
                    }

                    try {
                        double card = response.body().getContent().getTransactions().get(1).getTransAmount();
                        cardEarnings.setText("LKR: " + card);
                    } catch (Exception e) {
                        cardEarnings.setText("LKR: " + 0.00);
                    }


                    BigDecimal TotalWalletPoints = BigDecimalRound(BigDecimal.valueOf(response.body().getContent().getTotalWalletPoints()), 2, false);
                    walletPoints.setText("LKR: " + TotalWalletPoints);

                    BigDecimal OtherEarnings = BigDecimalRound(BigDecimal.valueOf(response.body().getContent().getBonusAmount()), 2, false);
                    tvOther.setText("LKR: " + OtherEarnings);

                    /*try {
                        Log.i("Earnings", String.valueOf(response.body().getContent().getTransactions().get(0).getTransAmount()));
                        Log.i("Earnings", String.valueOf(response.body().getContent().getTransactions().get(1).getTransAmount()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                }  //error.setText(getResources().getString(R.string.something_went_wrong_please_try_again_later));

            }

            @Override
            public void onFailure(@NonNull Call<WalletDataModel> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                //error.setText(getResources().getString(R.string.something_went_wrong_please_try_again_later));
            }
        });
    }

    /**
     * top up wallet from account API call
     */
    public void topUpWallet(double amount) {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Recharging...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        LoginSession loginSession = new LoginSession(getContext());

        /*TopUpModel topUpModel = new TopUpModel();
         topUpModel.setPartnerAccount(loginSession.getUserDetails().getContent().getNic());
        topUpModel.setAmount(amount);
        topUpModel.setMobileNo(loginSession.getUserDetails().getContent().getContactNo());
        topUpModel.setEmail(loginSession.getUserDetails().getContent().getEmail());
        topUpModel.setGlobalId(loginSession.getUserDetails().getContent().getNic());

        topUpModel.setPartnerAccount("007107012077");
        topUpModel.setAmount(10);
        topUpModel.setMobileNo("+94771129101");
        topUpModel.setEmail("test@live.com");
        topUpModel.setGlobalId("911850796V");*/

        JSONObject json = new JSONObject();

        RequestBody body = null;
        try {

//            json.put("partnerAccount", "007107012077");
//            json.put("amount", 10);
//            json.put("mobileNo", "+94771129101");
//            json.put("email", "test@live.com");
//            json.put("globalId", "911850796V");

            json.put("partnerAccount", loginSession.getUserDetails().getContent().getAccNumber());
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
        Call<PaymentResponseModel> call = apiInterface.topUpWallet(body);

        try {
            Buffer buf = new Buffer();
            Objects.requireNonNull(call.request().body()).writeTo(buf);
            System.out.println("Request Body : " + buf.readUtf8());
        } catch (Exception ignored) {

        }

        call.enqueue(new Callback<PaymentResponseModel>() {
            @Override
            public void onResponse(Call<PaymentResponseModel> call, @NonNull Response<PaymentResponseModel> response) {
                progressDialog.dismiss();
                if (response.code() == 200) {

                    assert response.body() != null;
                    Toast.makeText(getContext(), response.body().isSuccess() + " : " + response.body().getMessage(), Toast.LENGTH_LONG).show();

                    if (response.body().isSuccess() & response.body().getMessage().contains("Account Topup Successfully")) {

                        //Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();

                        progressDialog.dismiss();
                        getWalletData();
                        showAlertDialog("Account Recharge", response.body().getMessage(), getResources().getDrawable(R.drawable.ic_info));


                    } else if (!response.body().isSuccess() & response.body().getMessage().contains("Account Topup Failed")) {

                        //Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();

                        progressDialog.dismiss();

                        showAlertDialog("Account Recharge", response.body().getMessage(), getResources().getDrawable(R.drawable.ic_error));

                    } else if (!response.body().isSuccess() & response.body().getMessage().contains("Account Validation Failed")) {

                        //Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();

                        progressDialog.dismiss();

                        showAlertDialog("Account Recharge", response.body().getMessage(), getResources().getDrawable(R.drawable.ic_error));

                    } else if (!response.body().isSuccess() & response.body().getMessage().contains("Token Validation Failed")) {

                        //Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();

                        progressDialog.dismiss();

                        showAlertDialog("Account Recharge", response.body().getMessage(), getResources().getDrawable(R.drawable.ic_error));
                    }

                }  //tvOther.setText(getResources().getString(R.string.something_went_wrong_please_try_again_later));

            }

            @Override
            public void onFailure(Call<PaymentResponseModel> call, Throwable t) {
                progressDialog.dismiss();
                //error.setText(getResources().getString(R.string.something_went_wrong_please_try_again_later));
            }


        });


    }

    public void withdrawCardEarnings(double amount) {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Withdrawing...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        LoginSession loginSession = new LoginSession(getContext());

        /*RefundModel topUpModel = new RefundModel();
        topUpModel.setPartnerAccount(loginSession.getUserDetails().getContent().getNic());
        topUpModel.setAmount(amount - 100);
        topUpModel.setMobileNo(loginSession.getUserDetails().getContent().getContactNo());
        topUpModel.setEmail(loginSession.getUserDetails().getContent().getEmail());
        topUpModel.setGlobalId(loginSession.getUserDetails().getContent().getNic());*/


        JSONObject json = new JSONObject();

        RequestBody body = null;
        try {

//            json.put("partnerAccount", "007107012077");
//            json.put("amount", amount);
//            json.put("mobileNo", "+94771129101");
//            json.put("email", "test@live.com");
//            json.put("globalId", "911850796V");

            json.put("partnerAccount", loginSession.getUserDetails().getContent().getAccNumber());
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
                        getWalletData();
                        showAlertDialog("Earning Withdraw", response.body().getMessage(), getResources().getDrawable(R.drawable.ic_info));

                    } else if (!response.body().isSuccess() & response.body().getMessage().equals("Account topup failed")) {

                        //Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();

                        progressDialog.dismiss();

                        showAlertDialog("Earning Withdraw", response.body().getMessage(), getResources().getDrawable(R.drawable.ic_error));

                    } else if (!response.body().isSuccess() & response.body().getMessage().equals("Account validation failed")) {

                        //Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();

                        progressDialog.dismiss();

                        showAlertDialog("Earning Withdraw", response.body().getMessage(), getResources().getDrawable(R.drawable.ic_error));

                    } else if (!response.body().isSuccess() & response.body().getMessage().equals("Token validation failed")) {

                        //Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();

                        progressDialog.dismiss();

                        showAlertDialog("Earning Withdraw", response.body().getMessage(), getResources().getDrawable(R.drawable.ic_error));
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

    public void withdrawOtherEarnings(double amount) {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Withdrawing...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        LoginSession loginSession = new LoginSession(getContext());

        /*RefundModel topUpModel = new RefundModel();
        topUpModel.setPartnerAccount(loginSession.getUserDetails().getContent().getNic());
        topUpModel.setAmount(amount);
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
                        getWalletData();
                        showAlertDialog("Bonus Withdraw", response.body().getMessage(), getResources().getDrawable(R.drawable.ic_info));

                    } else if (!response.body().isSuccess() & response.body().getMessage().equals("Account topup failed")) {

                        //Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();

                        progressDialog.dismiss();

                        showAlertDialog("Bonus Withdraw", response.body().getMessage(), getResources().getDrawable(R.drawable.ic_error));

                    } else if (!response.body().isSuccess() & response.body().getMessage().equals("Account validation failed")) {

                        //Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();

                        progressDialog.dismiss();

                        showAlertDialog("Bonus Withdraw", response.body().getMessage(), getResources().getDrawable(R.drawable.ic_error));

                    } else if (!response.body().isSuccess() & response.body().getMessage().equals("Token validation failed")) {

                        //Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();

                        progressDialog.dismiss();

                        showAlertDialog("Bonus Withdraw", response.body().getMessage(), getResources().getDrawable(R.drawable.ic_error));
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

    private void updateWalletData() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        ApiInterface apiInterface = JsonApiClient.getApiClient().create(ApiInterface.class);
        Call<WalletDataModel> call = apiInterface.getWalletData(new LoginSession(getContext()).getUserDetails().getContent().getId());
        call.enqueue(new Callback<WalletDataModel>() {
            @Override
            public void onResponse(Call<WalletDataModel> call, Response<WalletDataModel> response) {
                progressDialog.dismiss();
                if (response.code() == 200) {

                    getWalletData();

                } else {
                    //error.setText(getResources().getString(R.string.something_went_wrong_please_try_again_later));
                }
            }

            @Override
            public void onFailure(Call<WalletDataModel> call, Throwable t) {
                progressDialog.dismiss();
                //error.setText(getResources().getString(R.string.something_went_wrong_please_try_again_later));
            }
        });
    }

    private void showAlertDialog(String title, String message, Drawable icon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title).
                setMessage(message).
                setCancelable(true).
                setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss()).
                setIcon(icon);

        AlertDialog alert = builder.create();
        alert.show();
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

    /*
     * round up with bigdecimal
     * */
    public static BigDecimal BigDecimalRound(BigDecimal d, int scale, boolean roundUp) {
        int mode = (roundUp) ? BigDecimal.ROUND_UP : BigDecimal.ROUND_DOWN;
        return d.setScale(scale, mode);
    }

    private void loadRecharge() {
        RechargeChooseDialog rcd = new RechargeChooseDialog(getActivity(), this);
        rcd.show();
    }
}
