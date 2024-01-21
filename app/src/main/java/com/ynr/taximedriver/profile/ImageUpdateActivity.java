package com.ynr.taximedriver.profile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.ynr.taximedriver.R;
import com.ynr.taximedriver.config.KeyString;
import com.ynr.taximedriver.config.Permission;
import com.ynr.taximedriver.rest.ApiInterface;
import com.ynr.taximedriver.rest.FileApiClient;
import com.ynr.taximedriver.session.LoginSession;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImageUpdateActivity extends AppCompatActivity {

    private static final String IMAGE_DIRECTORY = "/demonuts";
    private int GALLERY = 1, CAMERA = 2;
    private String direction = null;
    private CardView vehicleBookCard, vehicleInsuranceCard, vehicleFrontCard, vehicleSideViewCard;
    private ImageView vehicleBookImage, vehicleInsuranceImage, vehicleFrontImage, vehicleSideViewImage;
    private CardView licenceFront, licenceBack, nicFront, nicBack;
    public ImageView licenceFrontImage, licenceBackImage, nicFrontImage, nicBackImsge;
    private String vehicleBookImagePath = null, vehicleInsurenceImagePath = null, vehicleFrontImagePath = null, vehicleSideImagePath = null;
    private String profileImagePath = null, licenceFrontImagePath = null, licenceBackImagePath = null, nicFrontImagePath = null, nicBackImagePath = null;
    public CircleImageView profileImage;
    ProgressDialog progressDialog;
    Button submitButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_update);

        vehicleBookCard = findViewById(R.id.vehicle_book);
        vehicleInsuranceCard = findViewById(R.id.vehicle_insurance);
        vehicleFrontCard = findViewById(R.id.vehicle_front);
        vehicleSideViewCard = findViewById(R.id.vehicle_side_view);
        vehicleBookImage = findViewById(R.id.vehicle_book_image);
        vehicleInsuranceImage = findViewById(R.id.vehicle_insurance_image);
        vehicleFrontImage = findViewById(R.id.vehicle_front_image);
        vehicleSideViewImage = findViewById(R.id.vehicle_side_view_image);
        licenceFrontImage = findViewById(R.id.licence_front_image);
        licenceBackImage = findViewById(R.id.licence_back_image);
        nicFrontImage = findViewById(R.id.nic_front_image);
        nicBackImsge = findViewById(R.id.nic_back_image);
        licenceFront = findViewById(R.id.vehicle_book);
        licenceBack = findViewById(R.id.vehicle_revenue_licence);
        nicFront = findViewById(R.id.vehicle_insurance);
        nicBack = findViewById(R.id.nic_back);
        profileImage = findViewById(R.id.profileImage);
        submitButton = findViewById(R.id.submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profileImagePath != null && vehicleBookImagePath != null && vehicleInsurenceImagePath != null && vehicleFrontImagePath != null
                        && vehicleSideImagePath != null && licenceFrontImagePath != null && licenceBackImagePath != null && nicFrontImagePath != null && nicBackImagePath != null) {
                    driverImageUpdate();
                } else {
                    Toast.makeText(getApplicationContext(),"All images are required", Toast.LENGTH_SHORT).show();
                }
            }
        });
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog(KeyString.PROFILE_IMAGE);
            }
        });
        vehicleBookCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog(KeyString.VEHICLE_BOOK);
            }
        });
        vehicleInsuranceCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog(KeyString.VEHICLE_INSURANCE);
            }
        });
        vehicleFrontCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog(KeyString.VEHICLE_FRONT);
            }
        });
        vehicleSideViewCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog(KeyString.VEHICLE_SIDE_VIEW);
            }
        });
        licenceFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog(KeyString.DRIVING_LICENCE_FRONT);
            }
        });
        licenceBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog(KeyString.DRIVING_LICENCE_BACK);
            }
        });

        nicFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog(KeyString.NIC_FRONT);
            }
        });

        nicBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog(KeyString.NIC_BACK);
            }
        });
    }

    /**
     * driver image update api call
     */
    private void driverImageUpdate() {
        progressDialog = new ProgressDialog(ImageUpdateActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        ApiInterface apiInterface = FileApiClient.getApiClient().create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.driverImageUpdate(
                createPartFromString(new LoginSession(getApplicationContext()).getUserDetails().getContent().getId()),
                prepareFilePart("driverPic", profileImagePath),
                prepareFilePart("nicFrontPic", nicFrontImagePath),
                prepareFilePart("nicBackPic", nicBackImagePath),
                prepareFilePart("drivingLicenceFrontPic", licenceFrontImagePath),
                prepareFilePart("drivingLicenceBackPic", licenceBackImagePath)
        );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.code() == 200) {
                    vehicleImageUpdate();
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(ImageUpdateActivity.this).create();
                    alertDialog.setTitle("Something Went Wrong");
                    alertDialog.setMessage("Try again latter");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                AlertDialog alertDialog = new AlertDialog.Builder(ImageUpdateActivity.this).create();
                alertDialog.setTitle("Something Went Wrong");
                alertDialog.setMessage("Try again latter");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });
    }
    /**
     * vehicle image update api call
     */
    private void vehicleImageUpdate() {
        progressDialog = new ProgressDialog(ImageUpdateActivity.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        LoginSession session = new LoginSession(getApplicationContext());
        ApiInterface apiInterface = FileApiClient.getApiClient().create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.vehicleImageUpdate(
                createPartFromString(new LoginSession(getApplicationContext()).getVehicle().getId()),
                prepareFilePart("vehicleBookPic", vehicleBookImagePath),
                prepareFilePart("vehicleInsurancePic", vehicleInsurenceImagePath),
                prepareFilePart("vehicleFrontPic", vehicleFrontImagePath),
                prepareFilePart("vehicleSideViewPic", vehicleSideImagePath)
        );
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
               if (response.code() == 200) {
                   finish();
               } else {
                   AlertDialog alertDialog = new AlertDialog.Builder(ImageUpdateActivity.this).create();
                   alertDialog.setTitle("Something Went Wrong");
                   alertDialog.setMessage("Try again latter");
                   alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                           new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog, int which) {
                                   dialog.dismiss();
                               }
                           });
                   alertDialog.show();
               }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                AlertDialog alertDialog = new AlertDialog.Builder(ImageUpdateActivity.this).create();
                alertDialog.setTitle("Something Went Wrong");
                alertDialog.setMessage("Try again latter");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });
    }

    /**
     * method for select camera or gallery
     * @param d
     */
    private void showPictureDialog(final String d){
        Permission permission = new Permission(getApplicationContext(), ImageUpdateActivity.this);
        if (!permission.isCameraPermissionGranted() || !permission.isStoragePermissionGranted()) {
            permission.checkPermissions();
            return;
        }
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(ImageUpdateActivity.this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary(d);
                                break;
                            case 1:
                                takePhotoFromCamera(d);
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(
                okhttp3.MultipartBody.FORM, descriptionString);
    }

    @NonNull
    private MultipartBody.Part prepareFilePart(String partName, String filePath) {
        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
        // use the FileUtils to get the actual file by uri
        File file = new File(filePath);

        // create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpg"), file);

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    public void choosePhotoFromGallary(String d) {
        direction = d;
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }

    public void takePhotoFromCamera(String d) {
        direction = d;
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg");
        Uri uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    //String path = saveImage(bitmap);
                    //Toast.makeText(getApplicationContext(), "Image Saved!", Toast.LENGTH_SHORT).show();
                    switch (direction) {
                        case KeyString.VEHICLE_BOOK:
                            vehicleBookImage.setImageBitmap(bitmap);
                            vehicleBookImagePath = saveImage(bitmap);
                            break;
                        case KeyString.VEHICLE_INSURANCE:
                            vehicleInsuranceImage.setImageBitmap(bitmap);
                            vehicleInsurenceImagePath = saveImage(bitmap);
                            break;
                        case KeyString.VEHICLE_FRONT:
                            vehicleFrontImage.setImageBitmap(bitmap);
                            vehicleFrontImagePath = saveImage(bitmap);
                            break;
                        case KeyString.VEHICLE_SIDE_VIEW:
                            vehicleSideViewImage.setImageBitmap(bitmap);
                            vehicleSideImagePath = saveImage(bitmap);
                            break;
                        case KeyString.PROFILE_IMAGE:
                            profileImage.setImageBitmap(bitmap);
                            profileImagePath = saveImage(bitmap);
                            break;
                        case KeyString.DRIVING_LICENCE_FRONT:
                            licenceFrontImage.setImageBitmap(bitmap);
                            licenceFrontImagePath = saveImage(bitmap);
                            break;
                        case KeyString.DRIVING_LICENCE_BACK:
                            licenceBackImage.setImageBitmap(bitmap);
                            licenceBackImagePath = saveImage(bitmap);
                            break;
                        case KeyString.NIC_FRONT:
                            nicFrontImage.setImageBitmap(bitmap);
                            nicFrontImagePath = saveImage(bitmap);
                            break;
                        case KeyString.NIC_BACK:
                            nicBackImsge.setImageBitmap(bitmap);
                            nicBackImagePath = saveImage(bitmap);
                            break;
                        default:
                            break;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    //Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            try {
                //Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                //String path = saveImage(bitmap);
                File file = new File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg");

                //Uri of camera image
                Uri uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                switch (direction) {
                    case KeyString.VEHICLE_BOOK:
                        vehicleBookImage.setImageBitmap(bitmap);
                        vehicleBookImagePath = saveImage(bitmap);
                        break;
                    case KeyString.VEHICLE_INSURANCE:
                        vehicleInsuranceImage.setImageBitmap(bitmap);
                        vehicleInsurenceImagePath = saveImage(bitmap);
                        break;
                    case KeyString.VEHICLE_FRONT:
                        vehicleFrontImage.setImageBitmap(bitmap);
                        vehicleFrontImagePath = saveImage(bitmap);
                        break;
                    case KeyString.VEHICLE_SIDE_VIEW:
                        vehicleSideViewImage.setImageBitmap(bitmap);
                        vehicleSideImagePath = saveImage(bitmap);
                        break;
                    case KeyString.PROFILE_IMAGE:
                        profileImage.setImageBitmap(bitmap);
                        profileImagePath = saveImage(bitmap);
                        break;
                    case KeyString.DRIVING_LICENCE_FRONT:
                        licenceFrontImage.setImageBitmap(bitmap);
                        licenceFrontImagePath = saveImage(bitmap);
                        break;
                    case KeyString.DRIVING_LICENCE_BACK:
                        licenceBackImage.setImageBitmap(bitmap);
                        licenceBackImagePath = saveImage(bitmap);
                        break;
                    case KeyString.NIC_FRONT:
                        nicFrontImage.setImageBitmap(bitmap);
                        nicFrontImagePath = saveImage(bitmap);
                        break;
                    case KeyString.NIC_BACK:
                        nicBackImsge.setImageBitmap(bitmap);
                        nicBackImagePath = saveImage(bitmap);
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {

            }
            //Toast.makeText(getApplicationContext(), "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * reduce selected image size
     * save resized image
     * return resized image file path
     * @param myBitmap
     * @return
     */
    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ByteArrayOutputStream size = new ByteArrayOutputStream();

        int quality = 100;
        myBitmap.compress(Bitmap.CompressFormat.JPEG, quality, size);

        int imageSizeKB = size.toByteArray().length/1024;
        if (imageSizeKB > 2000) {
            quality = imageSizeKB < 3000 ? 75 : imageSizeKB < 4000 ? 55 : imageSizeKB < 5000 ? 45 :
                    imageSizeKB < 6000 ? 38 : imageSizeKB < 7000 ? 32 :
                            imageSizeKB < 8000 ? 28 : imageSizeKB < 9000 ? 25 :
                                    imageSizeKB < 10000 ? 22 : 15;
            myBitmap.compress(Bitmap.CompressFormat.JPEG, quality, bytes);
        } else {
            myBitmap.compress(Bitmap.CompressFormat.JPEG, quality, bytes);
        }

        File wallpaperDirectory = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance().getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this, new String[]{f.getPath()}, new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::---&gt;" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }
}
