package com.ynr.taximedriver.notification;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.ynr.taximedriver.R;

public class NotificationFragment extends Fragment {
    private ImageView backButton;
    //private RecyclerView hiresList;
    ProgressDialog progressDialog;
    private TextView error;
    private TextView textView40;

    public static NotificationFragment newInstance() {
        NotificationFragment fragment = new NotificationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View myFragmentView =  inflater.inflate(R.layout.fragment_notification, container, false);

        /*back button*/
        backButton = myFragmentView.findViewById(R.id.btnBack2);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        return myFragmentView;
    }
}
