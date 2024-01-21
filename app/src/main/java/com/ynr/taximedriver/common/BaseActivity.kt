package com.ynr.taximedriver.common

import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    fun disableUserAction() {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
    fun enableUserAction() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}