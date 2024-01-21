package com.ynr.taximedriver.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ynr.taximedriver.R;
import com.ynr.taximedriver.other.VehicleSelecterPopup;
import com.ynr.taximedriver.session.LoginSession;

import java.util.Objects;

public class ProfileFragment extends Fragment {
    private TextView name, birthDay, email, mobileNumber, address, referalCode, companyCode, driverRate;
    private Button vehicleInfoButton, logoutButton;
    private ImageView btnEditProfile,profileImage, backButton;

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View myFragmentView =  inflater.inflate(R.layout.fragment_profile, container, false);
        /*back button*/
        backButton = myFragmentView.findViewById(R.id.btnBack2);
        btnEditProfile = myFragmentView.findViewById(R.id.btnEditProfile);
        backButton.setOnClickListener(v -> getActivity().onBackPressed());
        /**
         * set status bar colour
         */
        Window window = this.getActivity().getWindow();   // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);  // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);   // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.ashOne));

        LoginSession session = new LoginSession(getContext());

        name = myFragmentView.findViewById(R.id.driver_name);
        driverRate = myFragmentView.findViewById(R.id.tv_driverRate);
        birthDay = myFragmentView.findViewById(R.id.birthday);
        email = myFragmentView.findViewById(R.id.passenger_name);
        mobileNumber = myFragmentView.findViewById(R.id.mobile_number);
        address = myFragmentView.findViewById(R.id.address);
        vehicleInfoButton = myFragmentView.findViewById(R.id.vehicle_info_button);
        profileImage = myFragmentView.findViewById(R.id.profileImage);
        referalCode = myFragmentView.findViewById(R.id.tv_referalCode);
        companyCode = myFragmentView.findViewById(R.id.tv_companyCode);
        logoutButton = myFragmentView.findViewById(R.id.logout_button);
        driverRate.setText("0.0");
        referalCode.setText(session.getUserDetails().getContent().getDriverCode());
        companyCode.setText(session.getUserDetails().getContent().getCompanyCode());
        vehicleInfoButton.setOnClickListener(v -> new VehicleSelecterPopup(getContext(), getActivity()));
        logoutButton.setOnClickListener(v -> new LoginSession(getContext()).logoutUser());
        btnEditProfile.setOnClickListener(view -> goToEditProfile());
        return myFragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        populateCacheDate();
    }

    private void populateCacheDate() {
        LoginSession session = new LoginSession(getContext());
        name.setText(String.format(getString(R.string.full_name),session.getUserDetails().getContent().getFirstName(),session.getUserDetails().getContent().getLastName()));
        birthDay.setText(session.getUserDetails().getContent().getBirthday().substring(0, 10));
        email.setText(session.getUserDetails().getContent().getEmail());
        mobileNumber.setText(session.getUserDetails().getContent().getContactNo());
        address.setText(session.getUserDetails().getContent().getAddress().getAddress());
        Glide.with(Objects.requireNonNull(getContext())).load(new LoginSession(getContext()).getUserDetails().getContent().getProfileImage())
                .apply(new RequestOptions().error(R.drawable.ic_user).placeholder(R.drawable.ic_user)).into(profileImage);
        profileImage.setOnClickListener(view -> {
            goToEditProfile();
        });
    }
    private void goToEditProfile() {
        Intent intent = new Intent(getContext(), EditProfileActivity.class);
        startActivity(intent);
    }
}
