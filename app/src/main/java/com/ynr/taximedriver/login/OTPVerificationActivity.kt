package com.ynr.taximedriver.login

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.ynr.taximedriver.R
import com.ynr.taximedriver.config.KeyString
import com.ynr.taximedriver.config.SoftKeyboard
import com.ynr.taximedriver.home.HomeActivity
import com.ynr.taximedriver.model.LoginModel
import com.ynr.taximedriver.other.DriverNotApprovedActivity
import com.ynr.taximedriver.profile.AddVehicleActivity
import com.ynr.taximedriver.rest.ApiInterface
import com.ynr.taximedriver.rest.JsonApiClient
import com.ynr.taximedriver.session.LoginSession
import kotlinx.android.synthetic.main.activity_otpverification.*
import kotlinx.android.synthetic.main.top_tool_bar_layout.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OTPVerificationActivity : AppCompatActivity() {

    var progressDialog: ProgressDialog? = null
    var profileImagePath: String? = null
    var firstName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otpverification)
        findViewById<View>(R.id.root_layout_otp_verification_activity).setOnTouchListener { v, event ->
            SoftKeyboard.hideSoftKeyboard(this@OTPVerificationActivity)
            false
        }

        profileImagePath = intent.getStringExtra(KeyString.PROFILE_IMAGE)
        Glide.with(this).load(profileImagePath).into(profileImage)

        firstName = intent.getStringExtra(KeyString.FIRST_NAME)
        txtName.setText(firstName)

        nextButton.setOnClickListener(View.OnClickListener { otpValidate(fetchData()) })
        pinBorderSetUp()

        txtToolBarTitle.text = "Confirm OTP"

        btnBack.setOnClickListener { onBackPressed() }
    }

    private fun fetchData(): LoginModel {
        val mobileNumber = intent.getStringExtra(KeyString.MOBILE_NUMBER)
        val model = LoginModel()
        try {
            val otp = pinOne!!.text.toString() + pinTwo!!.text.toString() + pinThree!!.text.toString() + pinFour!!.text.toString()
            model.pin = otp.toInt()
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "Invalid Pin", Toast.LENGTH_SHORT).show()
        }
        model.mobileNumber = mobileNumber
        return model
    }

    /**
     * otp validation api call
     * @param model
     */
    private fun otpValidate(model: LoginModel) {
        Log.i("TAG OTP", model.pin.toString())
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Loading...")
        progressDialog!!.show()
        val apiInterface = JsonApiClient.apiClient.create(ApiInterface::class.java)
        val call = apiInterface.signIn(model)
        call.enqueue(object : Callback<LoginModel?> {
            override fun onResponse(call: Call<LoginModel?>, response: Response<LoginModel?>) {
                progressDialog!!.dismiss()
                Log.i("TAG CODE", response.code().toString())
                if (response.code() == 200) {
                    if (response.body()?.message == "signedin") {
                        val session = LoginSession(applicationContext)
                        session.createLoginSession(response.body())
                        session.driverState = KeyString.OFFLINE
                        session.createVehicleSession(null)
                        session.driverApprove = true
                        session.driverEnable = true
                        session.dispatcherEnable = true
                        session.setVehicleEnable(true)
                        val intent = Intent(applicationContext, HomeActivity::class.java)
                        intent.putExtra(KeyString.PROFILE_IMAGE, profileImagePath)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Add new Flag to start new Activity
                        startActivity(intent)
                        finish()
                    } else if (response.body()?.message == "notapproved ") { val session = LoginSession(applicationContext)
                        session.createLoginSession(response.body())
                        session.driverState = KeyString.OFFLINE
                        session.createVehicleSession(null)
                        session.driverApprove = false
                        session.driverEnable = false
                        session.dispatcherEnable = false
                        session.setVehicleEnable(false)

                        val intent = Intent(this@OTPVerificationActivity, DriverNotApprovedActivity::class.java)
                        startActivity(intent)
                    }

//                    when(response.body()?.message) {
//                        "signedin" -> {
//                            val intent = Intent(applicationContext, HomeActivity::class.java)
//                            intent.putExtra(KeyString.PROFILE_IMAGE, profileImagePath)
//                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Add new Flag to start new Activity
//                            startActivity(intent)
//                            finish()
//                        }
//                        "notapproved" -> {
//                            val intent = Intent(applicationContext, DriverNotApprovedActivity::class.java)
//                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                            startActivity(intent)
//                            finish()
//                        }
//                    }


                } else if (response.code() == 202) {
                    val session = LoginSession(applicationContext)
                    session.createLoginSession(response.body())
                    session.driverState = KeyString.OFFLINE
                    session.createVehicleSession(null)
                    session.driverApprove = false
                    session.driverEnable = false
                    session.dispatcherEnable = false
                    session.setVehicleEnable(false)
                    //Intent intent = new Intent(getApplicationContext(), DriverNotApprovedActivity.class);
                    val intent = Intent(applicationContext, AddVehicleActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    val alertDialog = AlertDialog.Builder(this@OTPVerificationActivity).create()
                    alertDialog.setTitle("Invalid PIN Number")
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK"
                    ) { dialog, which -> dialog.dismiss() }
                    alertDialog.show()
                }
            }

            override fun onFailure(call: Call<LoginModel?>, t: Throwable) {
                progressDialog!!.dismiss()
                Log.i("TAG CODE", t.message!!)
                val alertDialog = AlertDialog.Builder(this@OTPVerificationActivity).create()
                alertDialog.setTitle("Something went wrong")
                alertDialog.setMessage("check your internet connection and try again")
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK"
                ) { dialog, which -> dialog.dismiss() }
                alertDialog.show()
            }
        })
    }

    /**
     * set border color in otp number input field
     */
    private fun pinBorderSetUp() {
        pinOne!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (!s.toString().isEmpty()) {
                    pinOne!!.background = getDrawable(R.drawable.ash_view_background_with_green_redious_boder)
                    pinTwo!!.requestFocus()
                } else {
                    pinOne!!.background = getDrawable(R.drawable.ash_view_background_with_white_redious_boder)
                }
            }
        })
        pinTwo!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (!s.toString().isEmpty()) {
                    pinTwo!!.background = getDrawable(R.drawable.ash_view_background_with_green_redious_boder)
                    pinThree!!.requestFocus()
                } else {
                    pinTwo!!.background = getDrawable(R.drawable.ash_view_background_with_white_redious_boder)
                    if (pinThree!!.text.toString().isEmpty()) {
                        pinOne!!.requestFocus()
                    }
                }
            }
        })
        pinThree!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (!s.toString().isEmpty()) {
                    pinThree!!.background = getDrawable(R.drawable.ash_view_background_with_green_redious_boder)
                    pinFour!!.requestFocus()
                } else {
                    pinThree!!.background = getDrawable(R.drawable.ash_view_background_with_white_redious_boder)
                    if (pinFour!!.text.toString().isEmpty()) {
                        pinTwo!!.requestFocus()
                    }
                }
            }
        })
        pinFour!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (!s.toString().isEmpty()) {
                    pinFour!!.background = getDrawable(R.drawable.ash_view_background_with_green_redious_boder)
                } else {
                    pinFour!!.background = getDrawable(R.drawable.ash_view_background_with_white_redious_boder)
                    pinThree!!.requestFocus()
                }
            }
        })
    }
}