package com.ynr.taximedriver.home.driver;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ynr.taximedriver.R;

public class PreBookingFragment extends Fragment {

    public static BookingsFragment newInstance() {
        BookingsFragment fragment = new BookingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View myFragmentView =  inflater.inflate(R.layout.fragment_pre_booking, container, false);


        return myFragmentView;
    }
}
