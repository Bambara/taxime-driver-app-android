package com.ynr.taximedriver.register

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ynr.taximedriver.BuildConfig
import com.ynr.taximedriver.FileHelper
import com.ynr.taximedriver.R
import com.ynr.taximedriver.config.KeyString
import com.ynr.taximedriver.login.OTPVerificationActivity
import com.ynr.taximedriver.rest.ApiInterface
import com.ynr.taximedriver.rest.FileApiClient
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.top_tool_bar_layout.*
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

class RegisterActivity : AppCompatActivity() {
    var fragment: Fragment? = null
    private val GALLERY = 1
    private val CAMERA = 2
    private var direction: String? = null
    private var state = 0

    @JvmField
    var firstName: String? = null

    @JvmField
    var lastName: String? = null

    @JvmField
    var birthDay: String? = null

    @JvmField
    var accNumber: String? = null

    @JvmField
    var email: String? = null

    @JvmField
    var mobileNumber: String? = null
    var gender = "male"

    //    @JvmField
//    var addressLineOne: String? = null
//    @JvmField
//    var addressLineTwo: String? = null
    @JvmField
    var city: String? = null

    //    @JvmField
//    var postalCode: String? = null
    @JvmField
    var country: String? = null

    @JvmField
    var district: String? = null

    @JvmField
    var province: String? = null

    @JvmField
    var nicNumber: String? = null

    @JvmField
    var companyCode: String? = ""
//    @JvmField
//    var lifeInsuranceNo: String? = null
//    @JvmField
//    var lifeInsuranceExpiryDate: String? = null


    private var profileImagePath: Uri? = null
    private var licenceFrontImagePath: Uri? = null
    private var licenceBackImagePath: Uri? = null
    private var nicFrontImagePath: Uri? = null
    private var nicBackImagePath: Uri? = null
    private var progressDialog: ProgressDialog? = null


    private var latestCameraFileUri: Uri? = null

    private val loadingState = MutableLiveData<Boolean>()
    val loadingStateObservable: LiveData<Boolean> get() = loadingState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        val fragmentTransaction = supportFragmentManager.beginTransaction()
        val registerFormOneFragment = RegisterFormOneFragment()
        val bundle = Bundle()
        bundle.putString(KeyString.MOBILE_NUMBER, intent.getStringExtra(KeyString.MOBILE_NUMBER))
        registerFormOneFragment.arguments = bundle
        fragmentTransaction.replace(R.id.register_fragment, registerFormOneFragment)
        fragmentTransaction.commit()

        btnBack.setOnClickListener { onBackPressed() }
        txtToolBarTitle.text = getString(R.string.registration)


        nextButton.setOnClickListener {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            Log.i("TAG STATE", state.toString())
            when (state) {
                0 -> {
                    val fragment = supportFragmentManager.findFragmentById(R.id.register_fragment) as RegisterFormOneFragment?
                    if (fragment!!.setData()) {
                        fragmentTransaction.replace(R.id.register_fragment, RegisterFormTwoFragment())
                        state = 1
                    } else if (profileImagePath == null) {
                        Toast.makeText(applicationContext, "Select Your Profile Image", Toast.LENGTH_SHORT).show()
                    }
                }
                1 -> {
                    val fragment1 = supportFragmentManager.findFragmentById(R.id.register_fragment) as RegisterFormTwoFragment?
                    if (fragment1!!.setData()) {
                        fragmentTransaction.replace(R.id.register_fragment, RegisterFormThreeFragment())
                        state = 2
                    }
                }
                2 -> {
                    val fragment2 = supportFragmentManager.findFragmentById(R.id.register_fragment) as RegisterFormThreeFragment?
                    if (licenceFrontImagePath == null || licenceBackImagePath == null || nicFrontImagePath == null || nicBackImagePath == null) {
                        fragment2!!.errorText.setText(R.string.select_all_images)
                    } else {
                        fragment2!!.errorText.text = ""

                        fragment2.showLoading(true)

                        GlobalScope.launch { fetchData() }

                    }
                }
            }
            fragmentTransaction.commit()
        }
    }

    /**
     * driver registration api call
     */
    private suspend fun fetchData() {
        val apiInterface = FileApiClient.getApiClient().create(ApiInterface::class.java)

        val nicFrontPicPart = prepareFilePart("nicFrontPic", nicFrontImagePath!!)
        val nicBackPicPart = prepareFilePart("nicBackPic", nicBackImagePath!!)
        val drivingLicenceFrontPicPart = prepareFilePart("drivingLicenceFrontPic", licenceFrontImagePath!!)
        val drivingLicenceBackPicPart = prepareFilePart("drivingLicenceBackPic", licenceBackImagePath!!)

        val call = if (profileImagePath != null) {

            val driverPicPart = prepareFilePart("driverPic", profileImagePath!!)

            apiInterface.driverRegister(
                createPartFromString(firstName),
                createPartFromString(lastName),
                createPartFromString(email),
                createPartFromString(nicNumber),
                createPartFromString(birthDay),
                createPartFromString(accNumber),
                createPartFromString(mobileNumber),
                createPartFromString(gender),
                createPartFromString(city),
                createPartFromString(country),
                createPartFromString(province),
                createPartFromString(district),
                createPartFromString(companyCode),
                nicFrontPicPart,
                nicBackPicPart,
                drivingLicenceFrontPicPart,
                drivingLicenceBackPicPart,
                driverPicPart,
                createPartFromString("0")
            )
        } else {
            apiInterface.driverRegister(
                createPartFromString(firstName),
                createPartFromString(lastName),
                createPartFromString(email),
                createPartFromString(nicNumber),
                createPartFromString(birthDay),
                createPartFromString(accNumber),
                createPartFromString(mobileNumber),
                createPartFromString(gender),
                createPartFromString(city),
                createPartFromString(country),
                createPartFromString(province),
                createPartFromString(district),
                createPartFromString(companyCode),
                nicFrontPicPart,
                nicBackPicPart,
                drivingLicenceFrontPicPart,
                drivingLicenceBackPicPart,
                null,
                createPartFromString("0")
            )
        }

        call?.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {

                runOnUiThread {
                    val fragment2 = supportFragmentManager.findFragmentById(R.id.register_fragment) as RegisterFormThreeFragment?
                    fragment2?.showLoading(false)
                }
                if (response.code() == 200) {
                    runOnUiThread {
                        val intent = Intent(applicationContext, OTPVerificationActivity::class.java)
                        intent.putExtra(KeyString.MOBILE_NUMBER, mobileNumber)
                        intent.putExtra(KeyString.PROFILE_IMAGE, profileImagePath)
                        intent.putExtra(KeyString.FIRST_NAME, firstName)
                        startActivity(intent)
                        finish()
                    }
                } else if (response.code() == 208) {
                    runOnUiThread {
                        val fragment2 = supportFragmentManager.findFragmentById(R.id.register_fragment) as RegisterFormThreeFragment?
                        fragment2!!.errorText.setText(R.string.alredy_registered)
                    }
                    //Toast.makeText(getApplicationContext(),"Registration Fail", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 203) {
                    runOnUiThread {
                        val fragment2 =
                            supportFragmentManager.findFragmentById(R.id.register_fragment) as RegisterFormThreeFragment?
                        fragment2!!.errorText.setText(R.string.companyCode_not_found)
                    }
                } else if (response.code() == 413) {
                    runOnUiThread {
                        val fragment2 =
                            supportFragmentManager.findFragmentById(R.id.register_fragment) as RegisterFormThreeFragment?
                        fragment2!!.errorText.setText(R.string.image_size_too_large)
                    }
                } else {
                    runOnUiThread {
                        val fragment2 =
                            supportFragmentManager.findFragmentById(R.id.register_fragment) as RegisterFormThreeFragment?
                        fragment2!!.errorText.setText(R.string.something_went_wrong)
                    }
                }
                Log.i("TAG RESPONSE CODE", response.code().toString())
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                runOnUiThread {
                    val fragment2 = supportFragmentManager.findFragmentById(R.id.register_fragment) as RegisterFormThreeFragment?
                    fragment2?.showLoading(false)
                }
                Log.i("TAG ON FAILURE", t.message!!)
                //Toast.makeText(getApplicationContext(),"Request fail", Toast.LENGTH_SHORT).show();
                runOnUiThread {
                    val fragment2 = supportFragmentManager.findFragmentById(R.id.register_fragment) as RegisterFormThreeFragment?
                    fragment2!!.errorText.setText(R.string.try_again)
                }
            }
        })
    }

    private fun createPartFromString(descriptionString: String?): RequestBody {
        return RequestBody.create(MultipartBody.FORM, descriptionString)
    }

    private suspend fun prepareFilePart(partName: String, filePath: Uri): MultipartBody.Part {
        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
        // use the FileUtils to get the actual file by uri
        val path = FileHelper.getFilePathFromURI(this, filePath)
        val file = File(path!!)

        val compressedImageFile = Compressor.compress(this, file, Dispatchers.IO) {

        }

        // create RequestBody instance from file
        val requestFile = RequestBody.create(MediaType.parse("image/jpg"), file)

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    fun choosePhotoFromGallary(d: String?) {
        direction = d
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY)
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
            KeyString.PROFILE_IMAGE -> {
                profileImagePath = uri
            }
            KeyString.DRIVING_LICENCE_FRONT -> {
                licenceFrontImagePath = uri
            }
            KeyString.DRIVING_LICENCE_BACK -> {
                licenceBackImagePath = uri
            }
            KeyString.NIC_FRONT -> {
                nicFrontImagePath = uri
            }
            KeyString.NIC_BACK -> {
                nicBackImagePath = uri
            }
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, CAMERA)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_CANCELED) {
            return
        }
        if (requestCode == GALLERY) {

            if (data != null) {
                val contentURI = data.data
                try {
                    when (direction) {
                        KeyString.PROFILE_IMAGE -> {
                            val fragment =
                                supportFragmentManager.findFragmentById(R.id.register_fragment) as RegisterFormOneFragment?
                            fragment!!.selectProfileImage.setImageURI(contentURI)
//                            fragment.selectProfileImage.visibility = View.GONE
                            fragment.isProfileImageSelected = true
                            profileImagePath = contentURI

                            System.out.println("ghost uri 3")
                            System.out.println(profileImagePath)
                        }
                        KeyString.DRIVING_LICENCE_FRONT -> {
                            val fragment1 = supportFragmentManager.findFragmentById(R.id.register_fragment) as RegisterFormThreeFragment?
                            fragment1?.licenceFrontImage?.setImageURI(contentURI)
                            fragment1?.txtLicenceFront?.visibility = View.GONE
                            licenceFrontImagePath = contentURI
                        }
                        KeyString.DRIVING_LICENCE_BACK -> {
                            val fragment2 = supportFragmentManager.findFragmentById(R.id.register_fragment) as RegisterFormThreeFragment?
                            fragment2?.licenceBackImage?.setImageURI(contentURI)
                            fragment2?.txtLicenceBack?.visibility = View.GONE
                            licenceBackImagePath = contentURI
                        }
                        KeyString.NIC_FRONT -> {
                            val fragment3 = supportFragmentManager.findFragmentById(R.id.register_fragment) as RegisterFormThreeFragment?
                            fragment3?.nicFrontImage?.setImageURI(contentURI)
                            fragment3?.txtNicFront?.visibility = View.GONE
                            nicFrontImagePath = contentURI
                        }
                        KeyString.NIC_BACK -> {
                            val fragment4 = supportFragmentManager.findFragmentById(R.id.register_fragment) as RegisterFormThreeFragment?
                            fragment4?.nicBackImsge?.setImageURI(contentURI)
                            fragment4?.txtNicBack?.visibility = View.GONE
                            nicBackImagePath = contentURI
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        } else if (requestCode == CAMERA) {

            try {

                when (direction) {
                    KeyString.PROFILE_IMAGE -> {
                        val fragment = supportFragmentManager.findFragmentById(R.id.register_fragment) as RegisterFormOneFragment?
                        fragment!!.selectProfileImage.setImageURI(profileImagePath)
                        fragment.isProfileImageSelected = true
                    }
                    KeyString.DRIVING_LICENCE_FRONT -> {
                        val fragment1 = supportFragmentManager.findFragmentById(R.id.register_fragment) as RegisterFormThreeFragment?
                        fragment1!!.licenceFrontImage.setImageURI(licenceFrontImagePath)
                        fragment1.txtLicenceFront.visibility = View.GONE
                    }
                    KeyString.DRIVING_LICENCE_BACK -> {
                        val fragment2 = supportFragmentManager.findFragmentById(R.id.register_fragment) as RegisterFormThreeFragment?
                        fragment2!!.licenceBackImage.setImageURI(licenceBackImagePath)
                        fragment2.txtLicenceBack?.visibility = View.GONE
                    }
                    KeyString.NIC_FRONT -> {
                        val fragment3 = supportFragmentManager.findFragmentById(R.id.register_fragment) as RegisterFormThreeFragment?
                        fragment3!!.nicFrontImage.setImageURI(nicFrontImagePath)
                        fragment3.txtNicFront?.visibility = View.GONE
                    }
                    KeyString.NIC_BACK -> {
                        val fragment4 = supportFragmentManager.findFragmentById(R.id.register_fragment) as RegisterFormThreeFragment?
                        fragment4!!.nicBackImsge.setImageURI(nicBackImagePath)
                        fragment4.txtNicBack?.visibility = View.GONE
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            //}
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
//    fun saveImage(myBitmap: Bitmap): String {
//        val bytes = ByteArrayOutputStream()
//        val size = ByteArrayOutputStream()
//        var quality = 100
//        myBitmap.compress(Bitmap.CompressFormat.JPEG, quality, size)
//        val imageSizeKB = size.toByteArray().size / 1024
//        if (imageSizeKB > 2000) {
//            quality = if (imageSizeKB < 3000) 75 else if (imageSizeKB < 4000) 55 else if (imageSizeKB < 5000) 45 else if (imageSizeKB < 6000) 38 else if (imageSizeKB < 7000) 32 else if (imageSizeKB < 8000) 28 else if (imageSizeKB < 9000) 25 else if (imageSizeKB < 10000) 22 else 15
//            myBitmap.compress(Bitmap.CompressFormat.JPEG, quality, bytes)
//        } else {
//            myBitmap.compress(Bitmap.CompressFormat.JPEG, quality, bytes)
//        }
//        val wallpaperDirectory = File(Environment.getExternalStorageDirectory().toString() + IMAGE_DIRECTORY)
//        // have the object build the directory structure, if needed.
//        if (!wallpaperDirectory.exists()) {
//            wallpaperDirectory.mkdirs()
//        }
//        try {
//            val f = File(wallpaperDirectory, Calendar.getInstance().timeInMillis.toString() + ".jpg")
//            f.createNewFile()
//            val fo = FileOutputStream(f)
//            fo.write(bytes.toByteArray())
//            MediaScannerConnection.scanFile(this, arrayOf(f.path), arrayOf("image/jpeg"), null)
//            fo.close()
//            Log.d("TAG", "File Saved::---&gt;" + f.absolutePath)
//            return f.absolutePath
//        } catch (e1: IOException) {
//            e1.printStackTrace()
//        }
//        return ""
//    }

    fun saveImage(myBitmap: Bitmap?): String {
        val bytes = ByteArrayOutputStream()
        myBitmap!!.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(this.externalCacheDir.toString())
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs()
        }
        try {
            val f = File(
                wallpaperDirectory, Calendar.getInstance()
                    .timeInMillis.toString() + ".jpg"
            )
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