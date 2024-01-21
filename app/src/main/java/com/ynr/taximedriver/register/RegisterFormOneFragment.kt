package com.ynr.taximedriver.register

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ynr.taximedriver.R
import com.ynr.taximedriver.config.KeyString
import com.ynr.taximedriver.config.Permission
import com.ynr.taximedriver.config.SoftKeyboard
import com.ynr.taximedriver.validation.Formvalidation
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class RegisterFormOneFragment : Fragment(), View.OnClickListener {
    lateinit var profileImage: CircleImageView
    lateinit var selectProfileImage: ImageView
    private lateinit var firstName: EditText
    private lateinit var lastName: EditText
    private lateinit var email: EditText
    private lateinit var mobileNumber: EditText
    private lateinit var nicNumber: EditText
    private lateinit var companyCode: EditText
    private lateinit var firstNameError: ImageView
    private lateinit var lastNameError: ImageView
    private lateinit var mobileNumberError: ImageView
    private lateinit var emailError: ImageView
    private lateinit var dobError: ImageView
    private lateinit var nicNumberError: ImageView
    private lateinit var profileImageError: ImageView
    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    private lateinit var birthDay: TextView
    private var selectedPhoneNumber: String? = null
    var isProfileImageSelected: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedPhoneNumber = arguments?.getString(KeyString.MOBILE_NUMBER)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val myFragmentView = inflater.inflate(R.layout.fragment_register_form_one, container, false)

        myFragmentView.findViewById<View>(R.id.root_layout_register_form_one_activity).setOnTouchListener { v, event ->
            SoftKeyboard.hideSoftKeyboard(activity)
            false
        }
        firstName = myFragmentView.findViewById(R.id.first_name)
        lastName = myFragmentView.findViewById(R.id.last_name)
        birthDay = myFragmentView.findViewById(R.id.birthday)
        nicNumber = myFragmentView.findViewById(R.id.nic_number)
        birthDay.setOnClickListener(this)
        email = myFragmentView.findViewById(R.id.email)
        mobileNumber = myFragmentView.findViewById(R.id.mobile_number)

        if (selectedPhoneNumber != null && selectedPhoneNumber != "") {
            mobileNumber.setText(selectedPhoneNumber)
        }

        firstNameError = myFragmentView.findViewById(R.id.first_name_error)
        lastNameError = myFragmentView.findViewById(R.id.last_name_error)
        dobError = myFragmentView.findViewById(R.id.dob_error)
        emailError = myFragmentView.findViewById(R.id.email_error)
        nicNumberError = myFragmentView.findViewById(R.id.nic_number_error)
        companyCode = myFragmentView.findViewById(R.id.company_code)
        mobileNumberError = myFragmentView.findViewById(R.id.mobile_number_error)
        profileImageError = myFragmentView.findViewById(R.id.profileImageError)
        selectProfileImage = myFragmentView.findViewById(R.id.selectProfileImage)

        selectProfileImage.setOnClickListener { showPictureDialog(KeyString.PROFILE_IMAGE) }
        validate("listener")

        return myFragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpTextChangeValidators()
    }

    private fun setUpTextChangeValidators(){
        firstName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (Formvalidation.isName(s.toString())) {
                    firstNameError.setImageResource(R.drawable.right_icon)
                } else {
                    firstNameError.setImageResource(R.drawable.wrong_icon)
                }
            }
        })

        lastName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (Formvalidation.isName(s.toString())) {
                    lastNameError.setImageResource(R.drawable.right_icon)
                } else {
                    lastNameError.setImageResource(R.drawable.wrong_icon)
                }
            }
        })

        birthDay.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (!birthDay.text.toString().isEmpty()) {
                    dobError.setImageResource(R.drawable.right_icon)
                } else {
                    dobError.setImageResource(R.drawable.wrong_icon)
                }
            }
        })

        email.addTextChangedListener( object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (Formvalidation.isEmail(s.toString())) {
                    emailError.setImageResource(R.drawable.right_icon)
                } else {
                    emailError.setImageResource(R.drawable.wrong_icon)
                }
            }
        })

        mobileNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (Formvalidation.isMobileNumber(s.toString())) {
                    mobileNumberError.setImageResource(R.drawable.right_icon)
                } else {
                    mobileNumberError.setImageResource(R.drawable.wrong_icon)
                }
            }
        })

        nicNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (Formvalidation.isNIC(s.toString())) {
                    nicNumberError.setImageResource(R.drawable.right_icon)
                } else {
                    nicNumberError.setImageResource(R.drawable.wrong_icon)
                }
            }
        })
    }

    private fun validate(to: String): Boolean {
        var state = false

        if (to == "all" && Formvalidation.isName(firstName.text.toString())
                && Formvalidation.isName(lastName.text.toString())
                && Formvalidation.isMobileNumber(mobileNumber.text.toString())
                && Formvalidation.isEmail(email.text.toString())
                && !birthDay.text.toString().isEmpty()
                && !nicNumber.text.toString().isEmpty()
                && isProfileImageSelected
        ) {
            state = true
        } else if (to == "all") {
            if (!Formvalidation.isName(firstName.text.toString())) {
                firstNameError.setImageResource(R.drawable.wrong_icon)
            }
            if (!Formvalidation.isName(lastName.text.toString())) {
                lastNameError.setImageResource(R.drawable.wrong_icon)
            }
            if (!Formvalidation.isEmail(email.text.toString())) {
                emailError.setImageResource(R.drawable.wrong_icon)
            }
            if (!Formvalidation.isMobileNumber(mobileNumber.text.toString())) {
                mobileNumberError.setImageResource(R.drawable.wrong_icon)
            }
            if (birthDay.text.toString().isEmpty()) {
                dobError.setImageResource(R.drawable.wrong_icon)
            }
            if (nicNumber.text.toString().isEmpty()){
                nicNumberError.setImageResource(R.drawable.wrong_icon)
            }
            if (!isProfileImageSelected) {
                profileImageError.setImageResource(R.drawable.wrong_icon)
            }
        }
        return state
    }

    private fun showPictureDialog(d: String) {
        val permission = Permission(context, activity)
        if (!permission.isCameraPermissionGranted || !permission.isStoragePermissionGranted) {
            permission.checkPermissions()
            return
        }
        val pictureDialog = AlertDialog.Builder(context)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf(
                "Select photo from gallery",
                "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> (activity as RegisterActivity?)!!.choosePhotoFromGallary(d)
                1 -> (activity as RegisterActivity?)!!.takePhotoFromCamera(d)
            }
        }
        pictureDialog.show()
    }

    fun setData(): Boolean {
        var state = false
        if (validate("all")) {
            (activity as RegisterActivity?)!!.firstName = firstName.text.toString()
            (activity as RegisterActivity?)!!.lastName = lastName.text.toString()
            (activity as RegisterActivity?)!!.birthDay = birthDay.text.toString()
            (activity as RegisterActivity?)!!.email = email.text.toString()
            (activity as RegisterActivity?)!!.mobileNumber = mobileNumber.text.toString()
            (activity as RegisterActivity?)!!.nicNumber = nicNumber.text.toString()
            (activity as RegisterActivity?)!!.companyCode = companyCode.text.toString()
            //((RegisterActivity)getActivity()).gender = onRadioButtonClicked(v);
            state = true
        }
        return state
    }

    fun onRadioButtonClicked(view: View): String? {
        // Is the button now checked?
        val checked = (view as RadioButton).isChecked
        var state: String? = null
        when (view.getId()) {
            R.id.male -> if (checked) state = KeyString.MALE
            R.id.female -> if (checked) state = KeyString.FEMALE
        }
        return state
    }

    override fun onClick(v: View) {
        if (v === birthDay) {

            // Get Current Date
            val c = Calendar.getInstance()
            mYear = c[Calendar.YEAR]
            mMonth = c[Calendar.MONTH]
            mDay = c[Calendar.DAY_OF_MONTH]
            val datePickerDialog = DatePickerDialog(requireContext(), { view, year, monthOfYear, dayOfMonth ->
                birthDay.text = (monthOfYear + 1).toString() + "-" + dayOfMonth + "-" + year
                //date = String.valueOf(dayOfMonth) + "/" + String.valueOf(monthOfYear + 1) + "/" + String.valueOf(year);
            }, mYear, mMonth, mDay)
            datePickerDialog.show()
        }
        //        if (v == btnTimePicker) {
//
//            // Get Current Time
//            final Calendar c = Calendar.getInstance();
//            mHour = c.get(Calendar.HOUR_OF_DAY);
//            mMinute = c.get(Calendar.MINUTE);
//
//            // Launch Time Picker Dialog
//            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
//                    new TimePickerDialog.OnTimeSetListener() {
//
//                        @Override
//                        public void onTimeSet(TimePicker view, int hourOfDay,
//                                              int minute) {
//
//                            txtTime.setText(hourOfDay + ":" + minute);
//                        }
//                    }, mHour, mMinute, false);
//            timePickerDialog.show();
//        }
    }
}