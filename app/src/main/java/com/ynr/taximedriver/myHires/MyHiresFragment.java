package com.ynr.taximedriver.myHires;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ynr.taximedriver.R;
import com.ynr.taximedriver.adapters.MyHiresListAdapter;
import com.ynr.taximedriver.model.HiresListModel;
import com.ynr.taximedriver.rest.ApiInterface;
import com.ynr.taximedriver.rest.JsonApiClient;
import com.ynr.taximedriver.session.LoginSession;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyHiresFragment extends Fragment {
    private ImageView backButton;
    private RecyclerView hiresList;
    ProgressDialog progressDialog;
    private MyHiresListAdapter adapter;
    private TextView error;

    public static MyHiresFragment newInstance() {
        MyHiresFragment fragment = new MyHiresFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View myFragmentView = inflater.inflate(R.layout.fragment_my_hires, container, false);

        backButton = myFragmentView.findViewById(R.id.btnBack2);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        hiresList = myFragmentView.findViewById(R.id.my_hires_recycler_view);
        error = myFragmentView.findViewById(R.id.error);
        getTripListApiCall();

        return myFragmentView;
    }

    private void getTripListApiCall() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        ApiInterface apiInterface = JsonApiClient.getApiClient().create(ApiInterface.class);
        System.out.println("Driver Id 02 : " + new LoginSession(getContext()).getUserDetails().getContent().getId());
        Call<HiresListModel> call = apiInterface.getDriverTrips(new LoginSession(getContext()).getUserDetails().getContent().getId());
        call.enqueue(new Callback<HiresListModel>() {
            @Override
            public void onResponse(Call<HiresListModel> call, Response<HiresListModel> response) {
                System.out.println("Hire List : ");
                progressDialog.dismiss();

                if (response.code() == 200) {
                    if (response.body().getDriverWallet().size() > 0) {
                        System.out.println("Hire List : " + response.body());
                        hiresList.setLayoutManager(new LinearLayoutManager(hiresList.getContext(), LinearLayoutManager.VERTICAL, false));
                        hiresList.setHasFixedSize(true);
                        adapter = new MyHiresListAdapter(getContext(), response.body().getDriverWallet());
                        hiresList.setAdapter(adapter);
                    } else {
                        error.setText(getResources().getString(R.string.no_data_available));
                    }
                } else {
                    error.setText(getResources().getString(R.string.something_went_wrong_please_try_again_later));
                }
            }
            @Override
            public void onFailure(Call<HiresListModel> call, Throwable t) {
                System.out.println("Error : " + t);
                progressDialog.dismiss();
                error.setText(getResources().getString(R.string.something_went_wrong_please_try_again_later));
            }
        });
    }
}
