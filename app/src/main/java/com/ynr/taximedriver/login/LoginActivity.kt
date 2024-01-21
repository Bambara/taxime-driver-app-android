package com.ynr.taximedriver.login

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ynr.taximedriver.R
import com.ynr.taximedriver.config.KeyString
import com.ynr.taximedriver.config.Permission
import com.ynr.taximedriver.home.HomeActivity
import com.ynr.taximedriver.model.LoginModel
import com.ynr.taximedriver.model.driverCheckInformationModel.DriverCheckRequestModel
import com.ynr.taximedriver.model.driverCheckInformationModel.DriverCheckResponseModel
import com.ynr.taximedriver.other.DriverNotApprovedActivity
import com.ynr.taximedriver.register.RegisterActivity
import com.ynr.taximedriver.rest.ApiInterface
import com.ynr.taximedriver.rest.JsonApiClient
import com.ynr.taximedriver.session.LoginSession
import com.ynr.taximedriver.validation.Formvalidation
import kotlinx.android.synthetic.main.activity_loging.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    var progressDialog: ProgressDialog? = null
    private var mLastClickTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loging)

        Permission(applicationContext, this@LoginActivity).checkPermissions()
        val session = LoginSession(applicationContext)

        if (session.isLoggedIn && session.driverApprove) {
            val intent = Intent(applicationContext, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } else if (session.isLoggedIn && !session.driverApprove) {
            checkDriver()
        }

        btnExchange.setOnClickListener(View.OnClickListener {
            if (Formvalidation.isMobileNumber(editTextPhone.text.toString())) {
                val model = LoginModel()
                var number = editTextPhone.text.toString()
                if (!number.startsWith("0")) {
                    number = "0$number"
                }
                model.mobileNumber = number
                otpRequest(model)
            } else {
                val alertDialog = AlertDialog.Builder(this@LoginActivity).create()
                alertDialog.setTitle(resources.getString(R.string.invalid_mobile_number))
                alertDialog.setMessage(resources.getString(R.string.enter_your_10_digit_mobile_number))
                alertDialog.setButton(
                    AlertDialog.BUTTON_NEUTRAL, "OK"
                ) { dialog, which -> dialog.dismiss() }
                alertDialog.show()
            }
        })
//        tv_TAC.movementMethod = LinkMovementMethod.getInstance()

        tv_TAC.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("https://taxime.lk/privacy-policy/"))
            startActivity(browserIntent)
        }

        editTextPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
            }
        })

    }

    private fun otpRequest(model: LoginModel) {
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Loading...")
        progressDialog!!.show()
        val apiInterface = JsonApiClient.apiClient.create(ApiInterface::class.java)
        val call = apiInterface.sendOtp(model)
        call.enqueue(object : Callback<LoginModel?> {
            override fun onResponse(call: Call<LoginModel?>, response: Response<LoginModel?>) {
                progressDialog!!.dismiss()
                try {
                    if (response.code() == 200) {
                        val intent = Intent(applicationContext, OTPVerificationActivity::class.java)
                        intent.putExtra(KeyString.FIRST_NAME, response.body()!!.content.firstName)
                        intent.putExtra(KeyString.MOBILE_NUMBER, model.mobileNumber)
                        intent.putExtra(KeyString.PROFILE_IMAGE, response.body()!!.content.profileImage)
                        startActivity(intent)
                    }
                    else if (response.code() == 204) {
                        val intent = Intent(applicationContext, RegisterActivity::class.java)
                        intent.putExtra(KeyString.MOBILE_NUMBER, model.mobileNumber)
                        startActivity(intent)
                    }
                    else {
                        val alertDialog = AlertDialog.Builder(this@LoginActivity).create()
                        alertDialog.setTitle(resources.getString(R.string.something_went_wrong))
                        alertDialog.setMessage(resources.getString(R.string.error_code) + " " + response.code())
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK"
                        ) { dialog, which -> dialog.dismiss() }
                        alertDialog.show()
                    }
                } catch (e: Exception) {
                    Log.i("TAG SENDOTP", e.toString())
                }
            }

            override fun onFailure(call: Call<LoginModel?>, t: Throwable) {
                progressDialog!!.dismiss()
                val alertDialog = AlertDialog.Builder(this@LoginActivity).create()
                alertDialog.setTitle(resources.getString(R.string.something_went_wrong))
                alertDialog.setMessage(resources.getString(R.string.error_code))
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK"
                ) { dialog, which -> dialog.dismiss() }
                alertDialog.show()
            }
        })
    }


    /**
     * check driver approved or not
     */
    private fun checkDriver() {
        val model = DriverCheckRequestModel()
        val session = LoginSession(applicationContext)
        model.driverId = session.userDetails.content.id
        model.vehicleId = null
        progressDialog = ProgressDialog(this@LoginActivity)
        progressDialog!!.setMessage("Loading...")
        progressDialog!!.show()
        val apiInterface = JsonApiClient.apiClient.create(ApiInterface::class.java)
        val call = apiInterface.driverCheck(model)
        call.enqueue(object : Callback<DriverCheckResponseModel?> {
            override fun onResponse(call: Call<DriverCheckResponseModel?>, response: Response<DriverCheckResponseModel?>) {
                progressDialog!!.dismiss()
                Log.i("TAG CHECK DRIVER CODE", response.code().toString())
                val session1 = LoginSession(applicationContext)

                if (response.code() == 206 && response.body()?.driverDispatch?.isNotEmpty() == true && response.body()!!.driverDispatch[0].isApproved) {
                    session1.driverEnable = response.body()!!.driverDispatch[0].isEnable
                    session1.dispatcherEnable = response.body()!!.driverDispatch[0].isDispatchEnable
                    session1.driverApprove = true
                    val intent = Intent(applicationContext, HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(applicationContext, DriverNotApprovedActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
            }

            override fun onFailure(call: Call<DriverCheckResponseModel?>, t: Throwable) {
                progressDialog!!.dismiss()
                Log.i("TAG CHECK DRIVER FAIL", t.message!!)
                val intent = Intent(applicationContext, DriverNotApprovedActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        })
    }
}