package com.ynr.taximedriver.other

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ynr.taximedriver.R
import com.ynr.taximedriver.session.LoginSession
import kotlinx.android.synthetic.main.activity_driver_not_approved.*

class DriverNotApprovedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_not_approved)

        txtUserName.text = "Partner"

        val userData =  LoginSession(this).userDetails

        userData.content?.firstName?.let {
            txtUserName.text = it
        }
    }
}