package com.ynr.taximedriver.session;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;
import com.ynr.taximedriver.config.KeyString;
import com.ynr.taximedriver.login.LoginActivity;
import com.ynr.taximedriver.model.LoginModel;
import com.ynr.taximedriver.model.driverCheckInformationModel.DriverCheckResponseModel;
import com.ynr.taximedriver.model.vehicleModel.Content;

public class LoginSession {

    static SharedPreferences pref; // Shared Preferences
    Editor editor;  // Editor for Shared preferences
    Context _context;   // Context
    int PRIVATE_MODE = 0;   // Shared pref mode
    private static final String PREF_NAME = "Login";   // SharedPref file name
    private static final String _ID = "_id";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String PHONE_NO = "contactNumber";
    private static final String PROFILE_PICTURE = "profilePicture";
    private static final String GENDER = "gender";
    private static final String BIRTHDAY = "birthday";
    private static final String NIC = "nic";
    private static final String ADDRESS = "address";
    private static final String CITY = "city";
    private static final String DISTRICT = "district";
    private static final String PROVINCE = "province";
    private static final String COUNTRY = "country";
    private static final String CONTACT_NUMBER = "mobile";
    private static final String EMAIL = "email";
    private static final String ACCOUNT_NUMBER = "accountNumber";
    private static final String VEHICLE = "vehicle";
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String TOKEN = "token";
    private static final String DRIVER_CODE = "driverCode";
    private static final String COMPANY_CODE = "companyCode";
    private static final String DRIVER_STATE = "driverState";
    private static final String DISPATCHER = "dispatcher";
    private static final String DRIVER_APPROVE = "Driver Approve";
    private static final String DRIVER_ENABLE = "Driver Enable";
    private static final String VEHICLE_ENABLE = "Vehicle Enable";
    private static final String DISPATCHER_ENABLE = "Dispatcher Enable";
    private static final String SOCKET_ID = "Socket Id";
    private static final String CATEGORY_IMAGE = "categoryImage";

    // Constructor
    @SuppressLint("CommitPrefEdits")
    public LoginSession(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * stored dispatcher data and get dispatcher data in session
     */
    public void createDispatcherSession(DriverCheckResponseModel.DriverDispatch.Dispatcher model) {
        Gson gson = new Gson();
        String json = gson.toJson(model);
        editor.putString(DISPATCHER, json);
        editor.commit();
    }
    public DriverCheckResponseModel.DriverDispatch.Dispatcher getDispatcher() {
        Gson gson = new Gson();
        String json = pref.getString(DISPATCHER, null);
        return gson.fromJson(json, DriverCheckResponseModel.DriverDispatch.Dispatcher.class);
    }
    /**
     * set and get socket id
     */
    public void setSocketId (String id) {
        editor.putString(SOCKET_ID, id);
        editor.commit();
    }
    public String getSocketId() {
        return pref.getString(SOCKET_ID, null);
    }
    public void set_Id(String id) {
        editor.putString(_ID, id);
        editor.commit();
    }
    public String get_Id() {
        return pref.getString(_ID, null);
    }

    /**
     * set and get driver approved or not
     */
    public void setDriverApprove(boolean state) {
        editor.putBoolean(DRIVER_APPROVE, state);
        editor.commit();
    }
    public boolean getDriverApprove() {
        return pref.getBoolean(DRIVER_APPROVE, false);
    }

    /**
     * set and get driver enable and disable
     */
    public void setDriverEnable(boolean state) {
        editor.putBoolean(DRIVER_ENABLE, state);
        editor.commit();
    }
    public boolean getDriverEnable(){
        return pref.getBoolean(DRIVER_ENABLE,false);
    }

    /**
     * set nad get dispatcher enable and disable
     */
    public void setDispatcherEnable(boolean state) {
        editor.putBoolean(DISPATCHER_ENABLE, state);
        editor.commit();
    }
    public boolean getDispatcherEnable() {
        return pref.getBoolean(DISPATCHER_ENABLE, false);
    }

    /**
     * set nad get vehicle enable and disable
     */
    public void setVehicleEnable(boolean state) {
        editor.putBoolean(VEHICLE_ENABLE, state);
        editor.commit();
    }
    /**
     * store sub category images
     * @param categoryImage ""
     */
    public void createSubCategoryImageSession(DriverCheckResponseModel.CategoryImage categoryImage) {
        Gson gson = new Gson();
        String json = gson.toJson(categoryImage);
        editor.putString(CATEGORY_IMAGE, json);
        editor.commit();
    }

    /**
     * get sub category images from session
     * @return DriverCheckResponseModel.CategoryImage
     */
    public DriverCheckResponseModel.CategoryImage getCategoryImage() {
        Gson gson = new Gson();
        String json = pref.getString(CATEGORY_IMAGE, null);
        return gson.fromJson(json, DriverCheckResponseModel.CategoryImage.class);
    }

    /**
     * store vehicle data in session
     */
    public void createVehicleSession(Content content) {
        Gson gson = new Gson();
        String json = gson.toJson(content);
        editor.putString(VEHICLE, json);
        editor.commit();
    }

    /**
     * get vehicle data from session
     */
    public Content getVehicle() {
        Gson gson = new Gson();
        String json = pref.getString(VEHICLE, null);
        return gson.fromJson(json, Content.class);
    }

    /**
     * Create login session
     * */
    public void createLoginSession(LoginModel model){
        editor.putBoolean(IS_LOGIN, true);  // Storing login value as TRUE
        editor.putString(ID, model.getContent().getId());   // Storing name in pref
        editor.putString(FIRST_NAME, model.getContent().getFirstName());
        editor.putString(LAST_NAME, model.getContent().getLastName());
        editor.putString(PROFILE_PICTURE, model.getContent().getProfileImage());
        editor.putString(GENDER, model.getContent().getGender());
        editor.putString(NIC, model.getContent().getNic());
        editor.putString(TOKEN, model.getToken());
        editor.putString(ACCOUNT_NUMBER, model.getContent().getAccNumber());
        editor.putString(BIRTHDAY, model.getContent().getBirthday());
        editor.putString(ADDRESS, model.getContent().getAddress().getCity() + ", " + model.getContent().getAddress().getDistrict() + ", " + model.getContent().getAddress().getProvince() + ", " + model.getContent().getAddress().getCountry());
        editor.putString(CITY, model.getContent().getAddress().getCity());
        editor.putString(DISTRICT, model.getContent().getAddress().getDistrict());
        editor.putString(PROVINCE, model.getContent().getAddress().getProvince());
        editor.putString(COUNTRY, model.getContent().getAddress().getCountry());
        editor.putString(CONTACT_NUMBER, model.getContent().getContactNo());
        editor.putString(EMAIL, model.getContent().getEmail());
        editor.putString(DRIVER_CODE, model.getContent().getDriverCode());
        editor.putString(COMPANY_CODE, model.getContent().getCompanyCode());
        editor.commit();    // commit changes
    }

    /**
     * set driver state
     */
    public void setDriverState(String state) {
        editor.putString(DRIVER_STATE, state);
        editor.commit();
    }
    /**
     * get driver state
     */
    public String getDriverState() {
        return pref.getString(DRIVER_STATE, KeyString.OFFLINE);
    }

    public void updateProfileImage(String profilePicture){
        editor.putString(PROFILE_PICTURE, profilePicture);
        editor.commit();    // commit changes
    }
    /**
     * Get stored session data
     * */
    public LoginModel getUserDetails(){
        LoginModel loginModel = new LoginModel();
        LoginModel.User user = new LoginModel.User();
        user.setId(pref.getString(ID, null));
        user.setName(pref.getString(NAME, null));
        user.setUserProfilePic(pref.getString(PROFILE_PICTURE, null));
        user.setContactNumber(pref.getString(PHONE_NO, null));
        user.setBirthday(pref.getString(BIRTHDAY, null));
        user.setGender(pref.getString(GENDER, null));
        LoginModel.Content content = new LoginModel.Content();
        LoginModel.Content.Address address = new LoginModel.Content.Address();
        content.setFirstName(pref.getString(FIRST_NAME, null));
        content.setLastName(pref.getString(LAST_NAME, null));
        content.setId(pref.getString(ID, null));
        content.setProfileImage(pref.getString(PROFILE_PICTURE, null));
        content.setBirthday(pref.getString(BIRTHDAY, null));
        address.setAddress(pref.getString(ADDRESS, null));
        address.setCity(pref.getString(CITY, null));
        address.setDistrict(pref.getString(DISTRICT, null));
        address.setProvince(pref.getString(PROVINCE, null));
        address.setCountry(pref.getString(COUNTRY, null));
        content.setAddress(address);
        content.setContactNo(pref.getString(CONTACT_NUMBER, null));
        content.setAccNumber(pref.getString(ACCOUNT_NUMBER, null));
        content.setEmail(pref.getString(EMAIL, null));
        content.setDriverCode(pref.getString(DRIVER_CODE, "N/A"));
        content.setCompanyCode(pref.getString(COMPANY_CODE, "N/A"));
        content.setNic(pref.getString(NIC, null));
        content.setGender(pref.getString(GENDER, null));
        loginModel.setContent(content);
        loginModel.setToken(pref.getString(TOKEN, null));
        loginModel.setUser(user);
        return loginModel;
    }

    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        clearSession();
        Intent intent = new Intent(_context, LoginActivity.class);   // After logout redirect user to Login Activity
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        _context.startActivity(intent);  // Staring Login Activity
    }
    public void clearSession(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
