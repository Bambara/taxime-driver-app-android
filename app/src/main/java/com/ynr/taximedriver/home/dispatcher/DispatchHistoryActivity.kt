package com.ynr.taximedriver.home.dispatcher

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.gson.Gson
import com.ynr.taximedriver.R
import com.ynr.taximedriver.adapters.DispatchHistoryAdapter
import com.ynr.taximedriver.config.KeyString
import com.ynr.taximedriver.home.driver.DriverFragment
import com.ynr.taximedriver.model.DispatchHistoryModel
import com.ynr.taximedriver.session.LoginSession
import kotlinx.android.synthetic.main.activity_dispatch_history.*

class DispatchHistoryActivity : AppCompatActivity() {


    var dispatchHistory: DispatchHistoryModel? = null
    private var adapter: DispatchHistoryAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dispatch_history)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val bundle = intent.extras
        val json = bundle?.getString(KeyString.DISPATCH_HISTORY_MODEL)
        val gson = Gson()
        dispatchHistory = gson.fromJson(json, DispatchHistoryModel::class.java)

//        if (dispatchHistory!!.dispatchHistories.size > 0) {
//            my_hires_recycler_view.setLayoutManager(LinearLayoutManager(my_hires_recycler_view.getContext(), LinearLayoutManager.VERTICAL, false))
//            my_hires_recycler_view.setHasFixedSize(true)
//            adapter = DispatchHistoryAdapter(applicationContext, dispatchHistory!!.dispatchHistories)
//            my_hires_recycler_view.setAdapter(adapter)
//        } else {
//            error.setText(resources.getString(R.string.no_data_available))
//        }

        btnBack2.setOnClickListener(View.OnClickListener { onBackPressed() })

        fragmentViewPager.adapter = ScreenSlidePagerAdapter(this)

        driverButton.setOnClickListener {
            if (!(LoginSession(this@DispatchHistoryActivity).driverState == KeyString.ONLINE)) {
                val alertDialog: AlertDialog = AlertDialog.Builder(this@DispatchHistoryActivity).create()
                alertDialog.setTitle("Driver Offline")
                alertDialog.setMessage("please change driver state as online")
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK"
                ) { dialog, which -> dialog.dismiss() }
                alertDialog.show()
            } else {
                supportFragmentManager.beginTransaction().replace(R.id.fragment, DriverFragment.newInstance()).commit()
            }
        }


        btnOwnDispatch.setOnClickListener {
            fragmentViewPager.currentItem = 0
        }

        btnOwnNetwork.setOnClickListener {
            fragmentViewPager.currentItem = 1
        }


    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment = when(position) {
            0 -> DispatchDataFragment.newInstance(DispatchHistoryType.OWN_DISPATCH, dispatchHistory!!.dispatchHistories!!)
            1 -> DispatchDataFragment.newInstance(DispatchHistoryType.OWN_NETWORK, dispatchHistory!!.dispatchHistories!!)
            else -> DispatchDataFragment.newInstance(DispatchHistoryType.OWN_DISPATCH, dispatchHistory!!.dispatchHistories!!)
        }
    }


    /**
     * round double value with two floating point
     * @param value
     * @param places
     * @return
     */
    fun round(value: Double, places: Int): Double {
        var value = value
        require(places >= 0)
        val factor = Math.pow(10.0, places.toDouble()).toLong()
        value = value * factor
        val tmp = Math.round(value)
        return tmp.toDouble() / factor
    }
}