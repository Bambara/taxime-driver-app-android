package com.ynr.taximedriver.register;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ynr.taximedriver.R;
import com.ynr.taximedriver.config.KeyString;
import com.ynr.taximedriver.config.Permission;

public class RegisterFormThreeFragment extends Fragment {
    private FrameLayout licenceFront, licenceBack, nicFront, nicBack;
    public ImageView licenceFrontImage, licenceBackImage, nicFrontImage, nicBackImsge;
    public TextView errorText, txtLicenceFront, txtLicenceBack, txtNicFront, txtNicBack;
    public FrameLayout loadingIndicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View myFragmentView =  inflater.inflate(R.layout.fragment_register_form_three, container, false);

        errorText = myFragmentView.findViewById(R.id.error_text);
        txtLicenceFront = myFragmentView.findViewById(R.id.txtLicenceFront);
        txtLicenceBack = myFragmentView.findViewById(R.id.txtLicenceBack);
        txtNicFront = myFragmentView.findViewById(R.id.txtNicFront);
        txtNicBack = myFragmentView.findViewById(R.id.txtNicBack);
        licenceFrontImage = myFragmentView.findViewById(R.id.imgLicenceFront);
        licenceBackImage = myFragmentView.findViewById(R.id.imgLicenceBack);
        nicFrontImage = myFragmentView.findViewById(R.id.imgNicFront);
        nicBackImsge = myFragmentView.findViewById(R.id.imgNicBack);
        loadingIndicator = myFragmentView.findViewById(R.id.loadingIndicator);

        licenceFront = myFragmentView.findViewById(R.id.vehicle_book);
        licenceFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog(KeyString.DRIVING_LICENCE_FRONT);
            }
        });

        licenceBack = myFragmentView.findViewById(R.id.vehicle_revenue_licence);
        licenceBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog(KeyString.DRIVING_LICENCE_BACK);
            }
        });

        nicFront = myFragmentView.findViewById(R.id.vehicle_insurance);
        nicFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog(KeyString.NIC_FRONT);
            }
        });

        nicBack = myFragmentView.findViewById(R.id.nic_back);
        nicBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog(KeyString.NIC_BACK);
            }
        });

        return myFragmentView;
    }

    public void showLoading(Boolean show){
        if (show){
            loadingIndicator.setVisibility(View.VISIBLE);
        } else  {
            loadingIndicator.setVisibility(View.GONE);
        }
    }

    private void showPictureDialog(final String d){

        Permission permission = new Permission(getContext(), getActivity());
        if (!permission.isCameraPermissionGranted() || !permission.isStoragePermissionGranted()) {
            permission.checkPermissions();
            return;
        }
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {"Select photo from gallery", "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                ((RegisterActivity)getActivity()).choosePhotoFromGallary(d);
                                break;
                            case 1:
                                ((RegisterActivity)getActivity()).takePhotoFromCamera(d);
                                break;
                        }
                    }
                });
        pictureDialog.show();

    }

}
