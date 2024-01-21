package com.ynr.taximedriver.home.driver

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ynr.taximedriver.AppConstants
import com.ynr.taximedriver.R
import com.ynr.taximedriver.home.dispatcher.DispatcherFragment
import kotlinx.android.synthetic.main.fragment_driver.*

class DriverFragment : Fragment() {

    @SuppressLint("ResourceAsColor")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val myFragmentView = inflater.inflate(R.layout.fragment_driver, container, false)
//        /**
//         * set status bar colour
//         */
        val window = requireActivity().window // clear FLAG_TRANSLUCENT_STATUS flag:
        //window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);  // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        //window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);   // finally change the color
        window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.redOne)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.driver_fragment, BookingsFragment.newInstance()).commit()

        return myFragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        driverButton.setOnClickListener{
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment, DispatcherFragment.newInstance()).commit()
        }

        btnBack2.setOnClickListener{ requireActivity().onBackPressed() }

        booking_button.setOnClickListener {
            booking_button.animate().alpha(0.5f).setDuration(AppConstants.buttonAnimationDuration).withEndAction {
                booking_button.animate().alpha(1f).setDuration(AppConstants.buttonAnimationDuration).withEndAction {

                }.start()
            }.start()
        }
        pre_booking_button.setOnClickListener {
            pre_booking_button.animate().alpha(0.5f).setDuration(AppConstants.buttonAnimationDuration).withEndAction {
                pre_booking_button.animate().alpha(1f).setDuration(AppConstants.buttonAnimationDuration).withEndAction {

                }.start()
            }.start()
        }
        long_trip_button.setOnClickListener {
            long_trip_button.animate().alpha(0.5f).setDuration(AppConstants.buttonAnimationDuration).withEndAction {
                long_trip_button.animate().alpha(1f).setDuration(AppConstants.buttonAnimationDuration).withEndAction {

                }.start()
            }.start()
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(): DriverFragment {
            return DriverFragment()
        }
    }
}