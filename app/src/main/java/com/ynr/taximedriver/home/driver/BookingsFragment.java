package com.ynr.taximedriver.home.driver;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ynr.taximedriver.R;
import com.ynr.taximedriver.adapters.TripAdapter;
import com.ynr.taximedriver.home.HomeActivity;
import com.ynr.taximedriver.model.tripModel.TripModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BookingsFragment extends Fragment implements TripAdapter.TripActionCallback {
    private RecyclerView recyclerView;
    List<TripModel> tripModelList = new ArrayList<>();
    TripAdapter adapter;

    public static BookingsFragment newInstance() {
        return new BookingsFragment();
    }
    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        tripModelList = ((HomeActivity)getActivity()).tripModelList;
        Log.i("TAG_BOOKING_FRAGMENT", "call onAttach method");
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("TAG_BOOKING_FRAGMENT", "call onCreate method");
    }
    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View myFragmentView =  inflater.inflate(R.layout.fragment_bookings, container, false);
        Log.i("TAG_BOOKING_FRAGMENT", "call onCreateView method");
        recyclerView = myFragmentView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.VERTICAL,false));
        recyclerView.setHasFixedSize(true);
        if (!tripModelList.isEmpty()) {
            adapter = new TripAdapter(getContext(), tripModelList);
            recyclerView.setAdapter(adapter);
            adapter.setTripActionCallback(this);
        }
        return myFragmentView;
    }

    public void updateList() {
        this.tripModelList = ((HomeActivity) getActivity()).tripModelList;
        adapter = new TripAdapter(getContext(), tripModelList);
        recyclerView.setAdapter(adapter);
        adapter.setTripActionCallback(this);
    }

    @Override
    public void onAcceptTrip() {
        ((HomeActivity)getActivity()).stopTone();
    }
}
