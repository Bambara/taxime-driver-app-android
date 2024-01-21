package com.ynr.taximedriver.register;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ynr.taximedriver.R;
import com.ynr.taximedriver.config.SoftKeyboard;

import java.util.Calendar;

public class RegisterFormTwoFragment extends Fragment implements View.OnClickListener {
    private EditText city, country, province, district, accNumber;
    private ImageView countryError, provinceError, districtError, cityError;
    private int mYear, mMonth, mDay;
    private TextView lifeInsuranceExpiryDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View myFragmentView =  inflater.inflate(R.layout.fragment_register_form_two, container, false);

        myFragmentView.findViewById(R.id.root_layout_register_form_two_fragment).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                SoftKeyboard.hideSoftKeyboard(getActivity());
                return false;
            }
        });

        country = myFragmentView.findViewById(R.id.country);
        province = myFragmentView.findViewById(R.id.province);
        district = myFragmentView.findViewById(R.id.district);
        city = myFragmentView.findViewById(R.id.city);
        accNumber = myFragmentView.findViewById(R.id.accNumber);

        countryError = myFragmentView.findViewById(R.id.country_error);
        provinceError = myFragmentView.findViewById(R.id.province_error);
        districtError = myFragmentView.findViewById(R.id.district_error);
        cityError = myFragmentView.findViewById(R.id.city_error);

        validate("listener");
        return myFragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUpTextChangeValidators();
    }

    private void setUpTextChangeValidators() {

        country.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if (country.getText().toString().isEmpty()) {
                    countryError.setImageResource(R.drawable.wrong_icon);
                } else {
                    countryError.setImageResource(R.drawable.right_icon);
                }
            }
        });

        province.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if (province.getText().toString().isEmpty()) {
                    provinceError.setImageResource(R.drawable.wrong_icon);
                } else {
                    provinceError.setImageResource(R.drawable.right_icon);
                }
            }
        });

        district.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                if (district.getText().toString().isEmpty()) {
                    districtError.setImageResource(R.drawable.wrong_icon);
                } else {
                    districtError.setImageResource(R.drawable.right_icon);
                }
            }
        });

        city.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (city.getText().toString().isEmpty()) {
                    cityError.setImageResource(R.drawable.wrong_icon);
                } else {
                    cityError.setImageResource(R.drawable.right_icon);
                }
            }
        });
    }

    private boolean validate(String to) {
        boolean state = false;

        if ( to == "all"
                && !country.getText().toString().isEmpty()
                && !province.getText().toString().isEmpty()
                && !district.getText().toString().isEmpty()
                && !city.getText().toString().isEmpty() ) {
            state = true;
        } else if ( to == "all" ) {
            if (country.getText().toString().isEmpty()) {
                countryError.setImageResource(R.drawable.wrong_icon);
            }

            if (province.getText().toString().isEmpty()) {
                provinceError.setImageResource(R.drawable.wrong_icon);
            }

            if (district.getText().toString().isEmpty()) {
                districtError.setImageResource(R.drawable.wrong_icon);
            }

            if (city.getText().toString().isEmpty()) {
                cityError.setImageResource(R.drawable.wrong_icon);
            }
        }

        return state;
    }

    public boolean setData() {
        boolean state = false;
        if (validate("all")) {
            //todo save city, postal code and district
            ((RegisterActivity)getActivity()).city = city.getText().toString();
            //((RegisterActivity)getActivity()).postalCode = postalCode.getText().toString();
            ((RegisterActivity)getActivity()).country = country.getText().toString();
            ((RegisterActivity)getActivity()).province = province.getText().toString();
            ((RegisterActivity) getActivity()).district = district.getText().toString();
            ((RegisterActivity) getActivity()).accNumber = accNumber.getText().toString();
            state = true;
        }
        return state;
    }

    @Override
    public void onClick(View v) {

        if (v == lifeInsuranceExpiryDate) {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    lifeInsuranceExpiryDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    //date = String.valueOf(dayOfMonth) + "/" + String.valueOf(monthOfYear + 1) + "/" + String.valueOf(year);
                }
            }, mYear, mMonth, mDay);
            datePickerDialog.show();
        }
    }

}
