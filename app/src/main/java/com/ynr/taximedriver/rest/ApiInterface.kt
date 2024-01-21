package com.ynr.taximedriver.rest

import com.ynr.taximedriver.BuildConfig.MAPS_API_KEY
import com.ynr.taximedriver.model.*
import com.ynr.taximedriver.model.dispatchEstimatedCostModel.EstimatedCostRequestModel
import com.ynr.taximedriver.model.dispatchEstimatedCostModel.EstimatedCostResponseModel
import com.ynr.taximedriver.model.dispatchModel.DispatchModel
import com.ynr.taximedriver.model.driverCheckInformationModel.DriverCheckRequestModel
import com.ynr.taximedriver.model.driverCheckInformationModel.DriverCheckResponseModel
import com.ynr.taximedriver.model.driverConnectModel.DriverConnectedModel
import com.ynr.taximedriver.model.mapDistanceModel.MapDistanceModel
import com.ynr.taximedriver.model.roadPickupModel.EndRoadPickupTripModel
import com.ynr.taximedriver.model.roadPickupModel.StartRoadPickupTripRequestModel
import com.ynr.taximedriver.model.roadPickupModel.StartRoadPickupTripResponceModel
import com.ynr.taximedriver.model.tripAcceptModel.DispatchTripAcceptRequestModel
import com.ynr.taximedriver.model.tripAcceptModel.PassengerTripAcceptRequestModel
import com.ynr.taximedriver.model.tripAcceptModel.TripAcceptResponseModel
import com.ynr.taximedriver.model.tripCancelModel.DispatchTripCancelRequestModel
import com.ynr.taximedriver.model.tripCancelModel.PassengerTripCancelRequestModel
import com.ynr.taximedriver.model.vehicalCategoryModel.VehicleCategoryRequestModel
import com.ynr.taximedriver.model.vehicalCategoryModel.VehicleCategoryResponseModel
import com.ynr.taximedriver.model.vehicleModel.VehicleModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {
    @Multipart
    @POST("/driver/driverregister")
    fun driverRegister(
        @Part("firstName") firstName: RequestBody?,
        @Part("lastName") lastName: RequestBody?,
        @Part("email") email: RequestBody?,
        @Part("nic") nicNumber: RequestBody?,
        @Part("birthday") birthday: RequestBody?,
        @Part("accNumber") accNumber: RequestBody?,
        @Part("mobile") mobileNumber: RequestBody?,
        @Part("gender") gender: RequestBody?,
//            @Part("address") addressLineOne: RequestBody?,
//            @Part("street") addressLineTwo: RequestBody?,
        @Part("city") city: RequestBody?,
//            @Part("zipcode") postalCode: RequestBody?,
        @Part("country") country: RequestBody?,
        @Part("province") province: RequestBody?,
        @Part("district") district: RequestBody?,
        @Part("companyCode") companyCode: RequestBody?,
        @Part nicFrontImage: MultipartBody.Part?,
        @Part nicBackImage: MultipartBody.Part?,
        @Part licenceFrontImage: MultipartBody.Part?,
        @Part licenceBackImage: MultipartBody.Part?,
//            @Part("lifeInsuranceNo") lifeInsuranceNo: RequestBody?,
//            @Part("lifeInsuranceExpiryDate") lifeInsuranceExpiryDate: RequestBody?,
        @Part profileImage: MultipartBody.Part?,
        @Part(" lifeInsuranceAmount") lifeInsuranceAmount: RequestBody?
    ): Call<ResponseBody?>?

    @POST("/driver/update")
    fun updateDriverProfile(@Body loginModel: UpdateDriverRequest): Call<LoginModel>

    @Multipart
    @POST("/driver/update_profile_image")
    fun driverProfileImageUpdate(
        @Part("driverId") driverId: RequestBody,
        @Part profileImage: MultipartBody.Part?
    ): Call<Map<String, Any>>

    @Multipart
    @POST("/driver/updatedriverimagesbyid")
    fun driverImageUpdate(
        @Part("driverId") driverId: RequestBody?,
        @Part profileImage: MultipartBody.Part?,
        @Part nicFrontImage: MultipartBody.Part?,
        @Part nicBackImage: MultipartBody.Part?,
        @Part licenceFrontImage: MultipartBody.Part?,
        @Part licenceBackImage: MultipartBody.Part?
    ): Call<ResponseBody?>

    @Multipart
    @POST("/driver/addvehicle")
    fun addVehicle(
        @Part("ownerContactName") ownerContactName: RequestBody?,
        @Part("ownerContactNumber") ownerContactNumber: RequestBody?,
        @Part("ownerContactEmail") ownerContactEmail: RequestBody?,
        @Part("vehicleRegistrationNo") vehicleRegistrationNo: RequestBody?,
        @Part("vehicleColor") vehicleColor: RequestBody?,
        @Part("vehicleBrandName") vehicleBrandName: RequestBody?,
        @Part vehicleBookPic: MultipartBody.Part?,
        @Part vehicleRevenuePic: MultipartBody.Part?,
        @Part vehicleInsurancePic: MultipartBody.Part?,
        @Part vehicleFrontPic: MultipartBody.Part?,
        @Part vehicleSideViewPic: MultipartBody.Part?,
        @Part("driverId") driverId: RequestBody?,
        @Part("vehicleModel") vehicleModel: RequestBody?,
//
        @Part("passengerCapacity") passengerCapacity: RequestBody?
    ): Call<ResponseBody?>

    @Multipart
    @POST("/vehicle/updatevehicleimagesbyid")
    fun vehicleImageUpdate(
        @Part("vehicleId") vehicleId: RequestBody?,
        @Part vehicleBookPic: MultipartBody.Part?,
        @Part vehicleInsurancePic: MultipartBody.Part?,
        @Part vehicleFrontPic: MultipartBody.Part?,
        @Part vehicleSideViewPic: MultipartBody.Part?
    ): Call<ResponseBody?>

    @POST("/driver/driverlogin")
    fun signIn(@Body loginModel: LoginModel?): Call<LoginModel?>

    @POST("/driver/sendotp")
    fun sendOtp(@Body loginModel: LoginModel?): Call<LoginModel?>

    @GET("api/directions/json?key=${MAPS_API_KEY}")
    fun getDistanceDuration(
        @Query("units") units: String?,
        @Query("origin") origin: String?,
        @Query("destination") destination: String?,
        @Query("mode") mode: String?
    ): Call<MapDistanceModel?>

    @POST("/vehicleCategory/getCategoryAllDataTimeAndLocationBased")
    fun getVehicleCategory(@Body model: VehicleCategoryRequestModel?): Call<VehicleCategoryResponseModel?>

    @POST("/dispatch/addDispatch")
    fun addDispatch(@Body dispatchModel: DispatchModel?): Call<ResponseBody?>

    @POST("/driver/getvehicledetails")
    fun getVehicle(@Body driverConnectedModel: DriverConnectedModel?): Call<VehicleModel?>

    @POST("/roadPickup/startRoadPickupTrip")
    fun startRoadPickup(@Body startRoadPickupTripRequestModel: StartRoadPickupTripRequestModel?): Call<StartRoadPickupTripResponceModel?>

    @POST("/roadPickup/endRoadPickupTrip")
    fun endRoadPickup(@Body endRoadPickupTripModel: EndRoadPickupTripModel?): Call<ResponseBody?>

    @POST("/driver/checkinformation")
    fun driverCheck(@Body driverCheckRequestModel: DriverCheckRequestModel?): Call<DriverCheckResponseModel?>

    @POST("/driver/selectvehicle")
    fun selectVehicle(@Body driverCheckRequestModel: DriverCheckRequestModel?): Call<ResponseBody?>

    @POST("/dispatch/getEstimatedCost")
    fun getDispatchEstimatedCost(@Body estimatedCostRequestModel: EstimatedCostRequestModel?): Call<EstimatedCostResponseModel?>

    @POST("/dispatch/acceptdispatch")
    fun acceptDispatch(@Body tripAcceptRequestModel: DispatchTripAcceptRequestModel?): Call<TripAcceptResponseModel?>

    @POST("/trip/acceptlivetrip")
    fun acceptLiveTrip(@Body tripAcceptRequestModel: PassengerTripAcceptRequestModel?): Call<TripAcceptResponseModel?>

    @POST("/dispatch/enddispatch")
    fun endDispatchTrip(@Body dispatchTripEndRequestModel: DispatchTripEndRequestModel?): Call<ResponseBody?>

    @POST("/trip/endlivetrip")
    fun endPassengerTrip(@Body passengerTripEndRequestModel: PassengerTripEndRequestModel?): Call<ResponseBody?>

    @POST("/dispatch/canceldispatch")
    fun cancelDispatchTrip(@Body dispatchTripCancelRequestModel: DispatchTripCancelRequestModel?): Call<ResponseBody?>

    @POST("/trip/cancelbydriver")
    fun cancelPassengerTrip(@Body passengerTripCancelRequestModel: PassengerTripCancelRequestModel?): Call<ResponseBody?>

    @GET("/driver/getLatestAndroidVersion")
    fun appVersionCheck(): Call<AndroidAppVersionModel?>

    @GET("/driver/getTrips/{driverid}")
    fun getDriverTrips(@Path("driverid") driverId: String?): Call<HiresListModel?>

    @GET("/driver/getDispatches/{driverid}")
    fun getDispatchHistory(@Path("driverid") driverId: String?): Call<DispatchHistoryModel?>

    @GET("/driver/getTripStatDataEd/{driverid}")
    fun getWalletData(@Path("driverid") driverId: String?): Call<WalletDataModel?>

    @POST("/driver/update_push_token")
    fun updateNotificationToken(@Body parms: RequestBody): Call<VehicleCategoryResponseModel?>

    @POST("/payment/topup")
    fun topUpWallet(@Body params: RequestBody?): Call<PaymentResponseModel?>

    @POST("/payment/refund")
    fun refundWallet(@Body params: RequestBody?): Call<PaymentResponseModel?>

    @POST("/trip/gettripdetailsbyid")
    fun getTripDetailsById(@Body estimatedCostRequestModel: EstimatedCostRequestModel?): Call<EstimatedCostResponseModel?>
}