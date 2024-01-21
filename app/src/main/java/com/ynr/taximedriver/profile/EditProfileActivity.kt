package com.ynr.taximedriver.profile

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ynr.taximedriver.R
import com.ynr.taximedriver.common.BaseActivity
import com.ynr.taximedriver.model.LoginModel
import com.ynr.taximedriver.model.UpdateDriverRequest
import com.ynr.taximedriver.rest.ApiInterface
import com.ynr.taximedriver.rest.FileApiClient
import com.ynr.taximedriver.rest.JsonApiClient.apiClient
import com.ynr.taximedriver.session.LoginSession
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.top_tool_bar_layout.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@Suppress("SameParameterValue")
class EditProfileActivity : BaseActivity() {

    private var permission: String? = null
    private lateinit var session: LoginSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        session = LoginSession(this)
        supportActionBar?.hide()
        Glide.with(applicationContext).load(session.userDetails.content.profileImage)
                .apply(RequestOptions().error(R.drawable.ic_user).placeholder(R.drawable.ic_user)).into(profile_image)
        txtToolBarTitle.text = getString(R.string.edit_profile)
        etFirstName.setText(session.userDetails.content.firstName)
        etLastName.setText(session.userDetails.content.lastName)
        etNic.setText(session.userDetails.content.nic)
        etBirthDate.setText(session.userDetails.content.birthday.substring(0, 10))
        etAccountNumber.setText(session.userDetails.content.accNumber)
        etEmail.setText(session.userDetails.content.email)
        etMobileNumber.setText(session.userDetails.content.contactNo)
        etCity.setText(session.userDetails.content.address.city)
        etDistrict.setText(session.userDetails.content.address.district)
        etProvince.setText(session.userDetails.content.address.province)
        etCountry.setText(session.userDetails.content.address.country)
        if (session.userDetails.content.gender == "male") {
            maleButton.isChecked = true
        } else {
            femaleButton.isChecked = true
        }
        btnSave.setOnClickListener {
            showPictureDialog()
        }
        btnBack.setOnClickListener {
            onBackPressed()
        }
        @Suppress("ClickableViewAccessibility")
        etBirthDate.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val c = Calendar.getInstance()
                val mYear = c[Calendar.YEAR]
                val mMonth = c[Calendar.MONTH]
                val mDay = c[Calendar.DAY_OF_MONTH]
                val datePickerDialog = DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
                    val date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse("$dayOfMonth-$monthOfYear-$year")
                    date?.let {
                        if (it.before(Date())) {
                            val bDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
                            etBirthDate.setText(bDate)
                        }
                    }
                }, mYear, mMonth, mDay)
                datePickerDialog.show()
                return@setOnTouchListener true
            }
            return@setOnTouchListener false
        }
        profile_image.setOnClickListener {
            showPictureDialog()
        }
        btnSave.setOnClickListener {
            driverProfileUpdate()
        }
    }

    private fun isFormDataValid(): Boolean {
        var isValid = true
        val nonEmptyFields = arrayOf(
            etFirstName, etLastName, etNic, etBirthDate, etMobileNumber, etAccountNumber, etCity,
            etDistrict, etProvince, etCountry
        )
        nonEmptyFields.forEach {
            if (it.text.isEmpty()) {
                it.error = "Required field"
                isValid = false
            }
        }
        val pattern = Patterns.EMAIL_ADDRESS
        if (!pattern.matcher(etEmail.text).matches()) {
            etEmail.error = "Invalid email"
            isValid = false
        }
        return isValid
    }

    /**
     * driver profile update api call
     */
    private fun driverProfileUpdate() {
        if (!isFormDataValid()) return
        val req = UpdateDriverRequest(
            session.userDetails.content.id,
            etFirstName.text.toString(),
            etLastName.text.toString(),
            etEmail.text.toString(),
            etNic.text.toString(),
            etBirthDate.text.toString(),
            etAccountNumber.text.toString(),
            etMobileNumber.text.toString(),
            if (maleButton.isChecked) "male" else "female",
            etCity.text.toString(),
            etDistrict.text.toString(),
            etProvince.text.toString(),
            etCountry.text.toString()
        )
        progressIndicator.visibility = View.VISIBLE
        disableUserAction()
        val apiInterface = apiClient.create(ApiInterface::class.java)
        val call = apiInterface.updateDriverProfile(req)
        call.enqueue(object : Callback<LoginModel> {
            override fun onResponse(call: Call<LoginModel>, response: Response<LoginModel>) {
                progressIndicator.visibility = View.GONE
                enableUserAction()
                if (response.code() == 200) {
                    session.createLoginSession(response.body())
                } else {
                    val alertDialog = AlertDialog.Builder(this@EditProfileActivity).create()
                    alertDialog.setTitle("Something Went Wrong")
                    alertDialog.setMessage("Try again latter")
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK"
                    ) { dialog, _ -> dialog.dismiss() }
                    alertDialog.show()
                }
            }

            override fun onFailure(call: Call<LoginModel?>, t: Throwable) {
                progressIndicator.visibility = View.GONE
                enableUserAction()
                val alertDialog = AlertDialog.Builder(this@EditProfileActivity).create()
                alertDialog.setTitle("Something Went Wrong")
                alertDialog.setMessage("Try again latter")
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK"
                ) { dialog, _ -> dialog.dismiss() }
                alertDialog.show()
            }
        })
    }

    /**
     * driver image update api call
     */
    private fun driverImageUpdate(saveImage: MultipartBody.Part?) {
        progressBarProfilePic.visibility = View.VISIBLE
        val apiInterface = FileApiClient.getApiClient().create(ApiInterface::class.java)
        val call = apiInterface.driverProfileImageUpdate(
                createPartFromString(LoginSession(applicationContext).userDetails.content.id),
                saveImage
        )
        call.enqueue(object : Callback<Map<String, Any>> {
            override fun onResponse(call: Call<Map<String, Any>>, response: Response<Map<String, Any>>) {
                progressBarProfilePic.visibility = View.GONE
                if (response.code() == 200) {
                    session.updateProfileImage(response.body()?.get("url") as String)
                } else {
                    val alertDialog = AlertDialog.Builder(this@EditProfileActivity).create()
                    alertDialog.setTitle("Something Went Wrong")
                    alertDialog.setMessage("Try again latter")
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK"
                    ) { dialog, _ -> dialog.dismiss() }
                    alertDialog.show()
                }
            }

            override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                progressBarProfilePic.visibility = View.GONE
                val alertDialog = AlertDialog.Builder(this@EditProfileActivity).create()
                alertDialog.setTitle("Something Went Wrong")
                alertDialog.setMessage("Try again latter")
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK"
                ) { dialog, _ -> dialog.dismiss() }
                alertDialog.show()
            }
        })
    }

    private fun createPartFromString(descriptionString: String): RequestBody {
        return RequestBody.create(
                MultipartBody.FORM, descriptionString)
    }

    /**
     * reduce selected image size
     * save resized image
     * return resized image file path
     */
    private fun saveImage(myBitmap: Bitmap): MultipartBody.Part? {
        //create a file to write bitmap data
        val f = File(applicationContext.cacheDir, "ProfilePic.jpg")
        f.createNewFile()
        //Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos)
        val bitmapData = bos.toByteArray()
        //write the bytes in file
        val fos: FileOutputStream
        try {
            fos = FileOutputStream(f)
            fos.write(bitmapData)
            fos.flush()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val reqFile = RequestBody.create(MediaType.parse("image/jpg"), f)
        return MultipartBody.Part.createFormData("driverPic", f.name, reqFile)
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf(
                "Select photo from gallery",
                "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems
        ) { _, which ->
            when (which) {
                0 -> choosePhotoFromGallery()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    private fun choosePhotoFromGallery() {
        if (checkPermissionStorage()) {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, GALLERY)
        } else {
            requestPermissionStorage()
        }
    }

    private fun takePhotoFromCamera() {
        if (checkPermissionCam()) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA)
        } else {
            requestPermissionCam()
        }
    }

    private fun checkPermissionCam(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkPermissionStorage(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissionCam() {
        permission = "cam"
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),
                PERMISSION_REQUEST_CODE)
    }

    private fun requestPermissionStorage() {
        permission = "storage"
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "Permission Granted", Toast.LENGTH_SHORT).show()
                showPictureDialog()
                // main logic
            } else {
                Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        showMessageOKCancel("You need to allow access permissions")
                        { _, _ ->
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (permission == "cam") {
                                    requestPermissionCam()
                                } else if (permission == "storage") {
                                    requestPermissionStorage()
                                }
                            }
                        }
                    }
                }
            }
        }
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
                    contentURI?.let {
                        val bitmap = if (Build.VERSION.SDK_INT < 28) {
                            @Suppress("DEPRECATION")
                            MediaStore.Images.Media.getBitmap(
                                    this.contentResolver,
                                    it
                            )
                        } else {
                            val source = ImageDecoder.createSource(this.contentResolver, it)
                            ImageDecoder.decodeBitmap(source)
                        }
                        profile_image.setImageBitmap(bitmap)
                        driverImageUpdate(saveImage(bitmap))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else if (requestCode == CAMERA) {
            try {
                val bitmap = (data?.extras?.get("data") as Bitmap)
                profile_image.setImageBitmap(bitmap)
                driverImageUpdate(saveImage(bitmap))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showMessageOKCancel(message: String, okListener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show()
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 200
        private const val GALLERY = 1
        private const val CAMERA = 2
    }
}