package com.ynr.taximedriver.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ynr.taximedriver.R;
import com.ynr.taximedriver.config.KeyString;
import com.ynr.taximedriver.config.SoftKeyboard;
import com.ynr.taximedriver.model.LoginModel;
import com.ynr.taximedriver.rest.ApiInterface;
import com.ynr.taximedriver.rest.JsonApiClient;
import com.ynr.taximedriver.validation.Formvalidation;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MobileNumberActivity extends AppCompatActivity {
    private EditText mobileNumber;
    private TextView textError;
    private Button nextButton;
    private ImageView mobileNumberError;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_number);

        findViewById(R.id.root_layout_mobile_number_activity).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                SoftKeyboard.hideSoftKeyboard(MobileNumberActivity.this);
                return false;
            }
        });

        textError = findViewById(R.id.text_error);
        mobileNumberError = findViewById(R.id.mobile_number_error);
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
                if (Formvalidation.isMobileNumber(mobileNumber.getText().toString())) {
                    mobileNumberError.setImageResource(R.drawable.right_icon);
                } else {
                    mobileNumberError.setImageResource(R.drawable.wrong_icon);
                }
                textError.setText("");
            }
        });
        nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mobileNumber.getText().toString().length() == 10) {
                    LoginModel model = new LoginModel();
                    model.setMobileNumber(mobileNumber.getText().toString());
                    otpRequiest(model);
                }
            }
        });
    }

    private void otpRequiest(LoginModel model) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        ApiInterface apiInterface = JsonApiClient.getApiClient().create(ApiInterface.class);
        Call<LoginModel> call = apiInterface.sendOtp(model);
        call.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                progressDialog.dismiss();
                try {
                    if (response.code() == 200) {
                        Intent intent = new Intent(getApplicationContext(), OTPVerificationActivity.class);
                        intent.putExtra(KeyString.FIRST_NAME, response.body().getContent().getFirstName());
                        intent.putExtra(KeyString.MOBILE_NUMBER, mobileNumber.getText().toString());
                        intent.putExtra(KeyString.PROFILE_IMAGE, KeyString.BASE_URL + ":" + KeyString.BASE_SOCKET + response.body().getContent().getProfileImage());
                        startActivity(intent);
                    } else if (response.code() == 204) {
                        textError.setText("Driver not found");
                    } else {
                        textError.setText("Something went wrong");
                    }
                } catch (Exception e) {
                    Log.i("TAG SENDOTP", e.toString());
                }
            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {
                progressDialog.dismiss();
                textError.setText(R.string.request_fail);
            }
        });
    }
}
