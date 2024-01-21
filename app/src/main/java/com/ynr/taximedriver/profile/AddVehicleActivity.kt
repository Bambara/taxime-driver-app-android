package com.ynr.taximedriver.profile

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ynr.taximedriver.BuildConfig
import com.ynr.taximedriver.FileHelper
import com.ynr.taximedriver.R
import com.ynr.taximedriver.config.KeyString
import com.ynr.taximedriver.config.Permission
import com.ynr.taximedriver.config.SoftKeyboard
import com.ynr.taximedriver.other.DriverNotApprovedActivity
import com.ynr.taximedriver.rest.ApiInterface
import com.ynr.taximedriver.rest.FileApiClient
import com.ynr.taximedriver.session.LoginSession
import com.ynr.taximedriver.validation.Formvalidation
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_add_vehicale.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class AddVehicleActivity : AppCompatActivity() {
//    private val vehicleBrand: String? = null
//    private val vehicleModel: String? = null
//    private val vehicleColor: String? = null
//    private val weightLimit: String? = null
//    private val passengerCapacity: String? = null
//    private val vehicleRegisterNumber: String? = null
    private var vehicleBookImagePath: Uri? = null
    private var vehicleRevenueImagePath: Uri? = null
    private var vehicleInsurenceImagePath: Uri? = null
    private var vehicleFrontImagePath: Uri? = null
    private var vehicleBackImagePath: Uri? = null
//   / private val vehicleRevenueLicenceImagePath: Uri? = null
    private lateinit var brand: EditText
    private lateinit var model: EditText
    private lateinit var color: EditText
    private lateinit var weight: EditText
    private lateinit var passenger: EditText
    private lateinit var registerNumber: EditText
    private lateinit var vehicleRegistrationCertificateCard: FrameLayout
    private lateinit var vehicleRevenueLicenceCard: FrameLayout
    private lateinit var vehicleInsuranceCard: FrameLayout
    private lateinit var vehicleFrontCard: FrameLayout
    private lateinit var vehicleBackCard: FrameLayout
    private lateinit var submit: Button
    private lateinit var vehicleBookImage: ImageView
    private lateinit var vehicleRevenueLicenceImage: ImageView
    private lateinit var vehicleInsuranceImage: ImageView
    private lateinit var vehicleFrontImage: ImageView
    private lateinit var vehicleBackImage: ImageView
    private lateinit var vehicleBrandErrorImage: ImageView
    private lateinit var vehicleModelErrorImage: ImageView
    private lateinit var vehicleColorErrorImage: ImageView
    private lateinit var vehicleCapacityErrorImage: ImageView
    private lateinit var vehicleRegisterNoErrorimage: ImageView
    private lateinit var vehicleBookTextView: TextView
    private lateinit var vehicleRevenueLicenceTextView: TextView
    private lateinit var vehicleInsuranceTextView: TextView
    private lateinit var vehicleFrontTextView: TextView
    private lateinit var vehicleBackTextView: TextView
    var progressDialog: ProgressDialog? = null
    private val GALLERY = 1
    private val CAMERA = 2
    private var direction: String? = null

    private val loadingState = MutableLiveData<Boolean>()
    val loadingStateObservable: LiveData<Boolean> get() = loadingState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_vehicale)
        val myLayout = findViewById<View>(R.id.include2)
        val titleTextView = myLayout.findViewById<View>(R.id.txtToolBarTitle) as TextView
        titleTextView.text = getString(R.string.add_your_vehicle)
        val backButtonIV = myLayout.findViewById<View>(R.id.btnBack) as ImageView
        backButtonIV.setOnClickListener { onBackPressed() }
        findViewById<View>(R.id.rot_layout_add_vehicle_activity).setOnTouchListener { v, event ->
            SoftKeyboard.hideSoftKeyboard(this@AddVehicleActivity)
            false
        }
        brand = findViewById(R.id.vehicle_brand)
        model = findViewById(R.id.vehicle_model)
        color = findViewById(R.id.vehicle_color)
        passenger = findViewById(R.id.passenger_capacity)
        registerNumber = findViewById(R.id.vehicle_register_number)
        vehicleRegistrationCertificateCard = findViewById(R.id.vehicle_book)
        vehicleRevenueLicenceCard = findViewById(R.id.vehicle_revenue_licence)
        vehicleInsuranceCard = findViewById(R.id.vehicle_insurance)
        vehicleFrontCard = findViewById(R.id.vehicle_front)
        vehicleBackCard = findViewById(R.id.vehicle_back)
        vehicleBookImage = findViewById(R.id.vehicle_book_image)
        vehicleRevenueLicenceImage = findViewById(R.id.vehicle_revenue_licence_image)
        vehicleInsuranceImage = findViewById(R.id.vehicle_insurance_image)
        vehicleFrontImage = findViewById(R.id.vehicle_front_image)
        vehicleBackImage = findViewById(R.id.vehicle_back_image)
        vehicleBrandErrorImage = findViewById(R.id.vehicle_brand_error)
        vehicleModelErrorImage = findViewById(R.id.vehicle_model_error)
        vehicleColorErrorImage = findViewById(R.id.vehicle_color_error)
        vehicleCapacityErrorImage = findViewById(R.id.passenger_capacity_error)
        vehicleRegisterNoErrorimage = findViewById(R.id.vehicle_registration_no_error)
        vehicleBookTextView = findViewById(R.id.vehicle_book_textview)
        vehicleRevenueLicenceTextView = findViewById(R.id.vehicle_revenue_textview)
        vehicleInsuranceTextView = findViewById(R.id.vehicle_insurance_textview)
        vehicleFrontTextView = findViewById(R.id.vehicle_front_textview)
        vehicleBackTextView = findViewById(R.id.vehicle_back_textview)
        submit = findViewById(R.id.submit_button)
        vehicleRegistrationCertificateCard.setOnClickListener(View.OnClickListener { showPictureDialog(KeyString.VEHICLE_BOOK) })
        vehicleRevenueLicenceCard.setOnClickListener(View.OnClickListener { showPictureDialog(KeyString.VEHICLE_REVENUE_LICENCE) })
        vehicleInsuranceCard.setOnClickListener(View.OnClickListener { showPictureDialog(KeyString.VEHICLE_INSURANCE) })
        vehicleFrontCard.setOnClickListener(View.OnClickListener { showPictureDialog(KeyString.VEHICLE_FRONT) })
        vehicleBackCard.setOnClickListener(View.OnClickListener { showPictureDialog(KeyString.VEHICLE_BACK) })
        submit.setOnClickListener(View.OnClickListener {
            try {
                if (brand.text.toString().isEmpty()) {
                    vehicleBrandErrorImage.setImageResource(R.drawable.wrong_icon)
                } else if (model.text.toString().isEmpty()) {
                    vehicleModelErrorImage.setImageResource(R.drawable.wrong_icon)
                } else if (color.text.toString().isEmpty()) {
                    vehicleColorErrorImage.setImageResource(R.drawable.wrong_icon)
                } else if (passenger.text.toString().isEmpty()) {
                    vehicleCapacityErrorImage.setImageResource(R.drawable.wrong_icon)
                } else if (registerNumber.text.toString().isEmpty()) {
                    vehicleRegisterNoErrorimage.setImageResource(R.drawable.wrong_icon)
                } else if (vehicleBookImagePath == null
                    // || vehicleRevenueLicenceImagePath == null
                    || vehicleInsurenceImagePath == null
                    || vehicleFrontImagePath == null
                    || vehicleBackImagePath == null
                ) {
                    Toast.makeText(
                        applicationContext,
                        "Please add all the images!",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@OnClickListener
                } else {
                    loadingProgress.visibility = View.VISIBLE
                    GlobalScope.launch {
                        fetchData()
                    }
                }

//                GlobalScope.launch {
//                    fetchData()
//                }

//                    if (brand.getText().toString().isEmpty() || model.getText().toString().isEmpty() || color.getText().toString().isEmpty()
//                            || passenger.getText().toString().isEmpty()  || registerNumber.getText().toString().isEmpty()
//                            || vehicleBookImagePath.isEmpty() || vehicleRevenueImagePath.isEmpty() || vehicleInsurenceImagePath.isEmpty() || vehicleFrontImagePath.isEmpty()
//                            || vehicleBackImagePath.isEmpty()) {
//                        AlertDialog alertDialog = new AlertDialog.Builder(AddVehicleActivity.this).create();
//                        alertDialog.setTitle("Error");
//                        alertDialog.setMessage("All data are required");
//                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                                new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
//                                    }
//                                });
//                        alertDialog.show();
//                        return;
//                    } else {
//                        fetchData();
//                    }
            } catch (e: Exception) {
            }
        })
        setUpTextChangeValidators()


        loadingStateObservable.observe(this, androidx.lifecycle.Observer {
            if (it) {
                Toast.makeText(this, "Vehicle Added Successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(applicationContext, DriverNotApprovedActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        })
    }

    private fun setUpTextChangeValidators() {
        brand.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (Formvalidation.isName(editable.toString())) {
                    vehicleBrandErrorImage.setImageResource(R.drawable.right_icon)
                } else {
                    vehicleBrandErrorImage.setImageResource(R.drawable.wrong_icon)
                }
            }
        })
        model.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (Formvalidation.isName(editable.toString())) {
                    vehicleModelErrorImage.setImageResource(R.drawable.right_icon)
                } else {
                    vehicleModelErrorImage.setImageResource(R.drawable.wrong_icon)
                }
            }
        })
        color.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (Formvalidation.isName(editable.toString())) {
                    vehicleColorErrorImage.setImageResource(R.drawable.right_icon)
                } else {
                    vehicleColorErrorImage.setImageResource(R.drawable.wrong_icon)
                }
            }
        })
        passenger.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (Formvalidation.isName(editable.toString())) {
                    vehicleCapacityErrorImage.setImageResource(R.drawable.right_icon)
                } else {
                    vehicleCapacityErrorImage.setImageResource(R.drawable.wrong_icon)
                }
            }
        })
        registerNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (Formvalidation.isName(editable.toString())) {
                    vehicleRegisterNoErrorimage.setImageResource(R.drawable.right_icon)
                } else {
                    vehicleRegisterNoErrorimage.setImageResource(R.drawable.wrong_icon)
                }
            }
        })
    }

    /**
     * method for select camera or gallery
     * @param d
     */
    private fun showPictureDialog(d: String) {
        val permission = Permission(applicationContext, this@AddVehicleActivity)
        if (!permission.isCameraPermissionGranted || !permission.isStoragePermissionGranted) {
            permission.checkPermissions()
            return
        }
        val pictureDialog = AlertDialog.Builder(this@AddVehicleActivity)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf(
                "Select photo from gallery",
                "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary(d)
                1 -> takePhotoFromCamera(d)
            }
        }
        pictureDialog.show()
    }

    /**
     * add vehicle api call
     */
    private suspend fun fetchData() {
        val session = LoginSession(applicationContext)
        val apiInterface = FileApiClient.getApiClient().create(ApiInterface::class.java)

        val vehicleBookPicPart = prepareFilePart("vehicleBookPic", vehicleBookImagePath!!)
        val vehicleRevenuePicPart = prepareFilePart("vehicleRevenuePic", vehicleRevenueImagePath!!)
        val vehicleInsurancePicPart =
            prepareFilePart("vehicleInsurancePic", vehicleInsurenceImagePath!!)
        val vehicleFrontPicPart = prepareFilePart("vehicleFrontPic", vehicleFrontImagePath!!)
        val vehicleSideViewPicPart = prepareFilePart("vehicleSideViewPic", vehicleBackImagePath!!)


        apiInterface?.addVehicle(
            createPartFromString(session.userDetails.content.firstName),
            createPartFromString(session.userDetails.content.contactNo),
            createPartFromString(session.userDetails.content.email),
            createPartFromString(registerNumber.text.toString()),
            createPartFromString(color.text.toString()),
            createPartFromString(brand.text.toString()),
            vehicleBookPicPart,
            vehicleRevenuePicPart,
            vehicleInsurancePicPart,
            vehicleFrontPicPart,
            vehicleSideViewPicPart,
            createPartFromString(session.userDetails.content.id),
            createPartFromString(model.text.toString()),  //                createPartFromString(weight.getText().toString()),
            createPartFromString(passenger.text.toString())
        )?.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                loadingProgress.visibility = View.GONE
                Log.i("TAG_RESPONSE_CODE", response.code().toString())
                loadingState.value = response.code() == 200
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                progressDialog!!.dismiss()
                Log.i("TAG_RESPONSE_CODE", t.message!!)
                Toast.makeText(this@AddVehicleActivity, t.message, Toast.LENGTH_SHORT).show()
                loadingProgress.visibility = View.GONE
                loadingState.value = false
            }
        })
    }

    private fun createPartFromString(descriptionString: String): RequestBody {
        return RequestBody.create(
                MultipartBody.FORM, descriptionString)
    }

    private suspend fun prepareFilePart(partName: String, filePath: Uri): MultipartBody.Part {
        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
        // use the FileUtils to get the actual file by uri
        val path =  FileHelper.getFilePathFromURI(this, filePath)
        val file =  File(path!!)

        val compressedImageFile = Compressor.compress(this, file, Dispatchers.IO) {

        }

        // create RequestBody instance from file
        val requestFile = RequestBody.create(MediaType.parse("image/jpg"), compressedImageFile)

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    fun choosePhotoFromGallary(d: String?) {
        direction = d

        try {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY)
        }catch (e: java.lang.Exception){
            e.printStackTrace()
        }
//        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        startActivityForResult(galleryIntent, GALLERY)
    }

    fun takePhotoFromCamera(d: String?) {
        direction = d
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val folder = File("${getExternalFilesDir(Environment.DIRECTORY_DCIM)}")
        folder.mkdirs()

        val file = File(folder, System.currentTimeMillis().div(1000).toString() + ".jpg")
        if (file.exists())
            file.delete()
        file.createNewFile()

        val uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file)

        when (direction) {
            KeyString.VEHICLE_BOOK -> {
                vehicleBookImagePath = uri
            }
            KeyString.VEHICLE_REVENUE_LICENCE -> {
                vehicleRevenueImagePath = uri
            }
            KeyString.VEHICLE_INSURANCE -> {
                vehicleInsurenceImagePath = uri
            }
            KeyString.VEHICLE_FRONT -> {
                vehicleFrontImagePath = uri
            }
            KeyString.VEHICLE_BACK -> {
                vehicleBackImagePath = uri
            }
            else -> {
            }
        }


        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, CAMERA)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) {
            return
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                val contentURI = data.data

                try {
                    when (direction) {
                        KeyString.VEHICLE_BOOK -> {
                            vehicleBookImage.setImageURI(contentURI)
                            vehicleBookTextView.visibility = View.GONE
                            vehicleBookImagePath = contentURI
                        }
                        KeyString.VEHICLE_REVENUE_LICENCE -> {
                            vehicleRevenueLicenceImage.setImageURI(contentURI)
                            vehicleRevenueLicenceTextView.visibility = View.GONE
                            vehicleRevenueImagePath = contentURI
                        }
                        KeyString.VEHICLE_INSURANCE -> {
                            vehicleInsuranceImage.setImageURI(contentURI)
                            vehicleInsuranceTextView.visibility = View.GONE
                            vehicleInsurenceImagePath = contentURI
                        }
                        KeyString.VEHICLE_FRONT -> {
                            vehicleFrontImage.setImageURI(contentURI)
                            vehicleFrontTextView.visibility = View.GONE
                            vehicleFrontImagePath = contentURI
                        }
                        KeyString.VEHICLE_BACK -> {
                            vehicleBackImage.setImageURI(contentURI)
                            vehicleBackTextView.visibility = View.GONE
                            vehicleBackImagePath = contentURI
                        }
                        else -> {
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    //Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == CAMERA) {
            try {
                //Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                //String path = saveImage(bitmap);
//                val file = File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg")
//
//                //Uri of camera image
//                val uri = FileProvider.getUriForFile(this, this.applicationContext.packageName + ".provider", file)
//                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                when (direction) {
                    KeyString.VEHICLE_BOOK -> {
                        vehicleBookImage.setImageURI(vehicleBookImagePath)
                        vehicleBookTextView.visibility = View.GONE
                        //vehicleBookImagePath = saveImage(bitmap)
                    }
                    KeyString.VEHICLE_REVENUE_LICENCE -> {
                        vehicleRevenueLicenceImage.setImageURI(vehicleRevenueImagePath)
                        vehicleRevenueLicenceTextView.visibility = View.GONE
                        // vehicleRevenueImagePath = saveImage(bitmap)
                    }
                    KeyString.VEHICLE_INSURANCE -> {
                        vehicleInsuranceImage.setImageURI(vehicleInsurenceImagePath)
                        vehicleInsuranceTextView.visibility = View.GONE
                        // vehicleInsurenceImagePath = saveImage(bitmap)
                    }
                    KeyString.VEHICLE_FRONT -> {
                        vehicleFrontImage.setImageURI(vehicleFrontImagePath)
                        vehicleFrontTextView.visibility = View.GONE
                        //  vehicleFrontImagePath = saveImage(bitmap)
                    }
                    KeyString.VEHICLE_BACK -> {
                        vehicleBackImage.setImageURI(vehicleBackImagePath)
                        vehicleBackTextView.visibility = View.GONE
                        // vehicleBackImagePath = saveImage(bitmap)
                    }
                    else -> {
                    }
                }
            } catch (e: Exception) {
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
    fun saveImage(myBitmap: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        val size = ByteArrayOutputStream()
        var quality = 100
        myBitmap.compress(Bitmap.CompressFormat.JPEG, quality, size)
        val imageSizeKB = size.toByteArray().size / 1024
        if (imageSizeKB > 2000) {
            quality = if (imageSizeKB < 3000) 75 else if (imageSizeKB < 4000) 55 else if (imageSizeKB < 5000) 45 else if (imageSizeKB < 6000) 38 else if (imageSizeKB < 7000) 32 else if (imageSizeKB < 8000) 28 else if (imageSizeKB < 9000) 25 else if (imageSizeKB < 10000) 22 else 15
            myBitmap.compress(Bitmap.CompressFormat.JPEG, quality, bytes)
        } else {
            myBitmap.compress(Bitmap.CompressFormat.JPEG, quality, bytes)
        }
        val wallpaperDirectory = File(cacheDir.toString() + IMAGE_DIRECTORY)
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs()
        }
        try {
            val f = File(wallpaperDirectory, Calendar.getInstance().timeInMillis.toString() + ".jpg")
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(this, arrayOf(f.path), arrayOf("image/jpeg"), null)
            fo.close()
            Log.d("TAG", "File Saved::---&gt;" + f.absolutePath)
            return f.absolutePath
        } catch (e1: IOException) {
            e1.printStackTrace()
        }
        return ""
    }

    companion object {
        private const val IMAGE_DIRECTORY = "/demonuts"
    }
}