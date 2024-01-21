package com.ynr.taximedriver.other

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.nkzawa.socketio.client.Socket
import com.ynr.taximedriver.R
import com.ynr.taximedriver.adapters.VehicleSelectAdapter
import com.ynr.taximedriver.model.driverCheckInformationModel.DriverCheckRequestModel
import com.ynr.taximedriver.model.driverCheckInformationModel.DriverCheckResponseModel
import com.ynr.taximedriver.model.driverConnectModel.DriverConnectedModel
import com.ynr.taximedriver.model.vehicleModel.Content
import com.ynr.taximedriver.model.vehicleModel.VehicleModel
import com.ynr.taximedriver.profile.AddVehicleActivity
import com.ynr.taximedriver.rest.ApiInterface
import com.ynr.taximedriver.rest.JsonApiClient
import com.ynr.taximedriver.session.LoginSession
import com.ynr.taximedriver.socket.MySocket
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VehicleSelecterPopup(var context: Context, var activity: Activity) : VehicleSelectAdapter.SingleClickListener {
    var progressDialog: ProgressDialog? = null
    var vehicleSelectAdapter: VehicleSelectAdapter? = null
    private var vehicle: Content?
    private var mSocket: Socket? = null

    /**
     * getVehicle api call
     */
    private fun getVehicle() {
        progressDialog = ProgressDialog(activity)
        progressDialog!!.setMessage("Loading...")
        progressDialog!!.show()
        val model = DriverConnectedModel()
        model.driverId = LoginSession(context).userDetails.content.id
        Log.i("TAG_DRIVER_ID", model.driverId)
        val apiInterface = JsonApiClient.apiClient.create(ApiInterface::class.java)
        val call = apiInterface.getVehicle(model)
        call.enqueue(object : Callback<VehicleModel?> {
            override fun onResponse(call: Call<VehicleModel?>, response: Response<VehicleModel?>) {
                progressDialog!!.dismiss()
                if (response.code() == 200) {
                    vehiclePopup(response.body())
                } else if (response.code() == 204) {
                    val alertDialog = AlertDialog.Builder(activity).create()
                    alertDialog.setTitle("No Vehicle")
                    alertDialog.setMessage("You have no available vehicle")
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK"
                    ) { dialog, which -> dialog.dismiss() }
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add Vehicle"
                    ) { dialog, which ->
                        val intent = Intent(context, AddVehicleActivity::class.java)
                        context.startActivity(intent)
                        dialog.dismiss()
                    }
                    alertDialog.show()
                } else {
                    val alertDialog = AlertDialog.Builder(activity).create()
                    alertDialog.setTitle("Something Went Wrong...")
                    alertDialog.setMessage("Place try again")
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK"
                    ) { dialog, which -> dialog.dismiss() }
                    alertDialog.show()
                }
            }

            override fun onFailure(call: Call<VehicleModel?>, t: Throwable) {
                progressDialog!!.dismiss()
                val alertDialog = AlertDialog.Builder(activity).create()
                alertDialog.setTitle("Something Went Wrong...")
                alertDialog.setMessage("Place check your internet connection and try again")
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK"
                ) { dialog, which -> dialog.dismiss() }
                alertDialog.show()
            }
        })
        progressDialog!!.dismiss()
    }

    /**
     * popup method
     * @param vehicleModel
     */
    private fun vehiclePopup(vehicleModel: VehicleModel?) {

        // Get the widgets reference from XML layout
        val layout = activity.findViewById<LinearLayout>(R.id.root_layout_home_activity)

        // Initialize a new instance of LayoutInflater service
        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        // Inflate the custom layout/view
        val customView = inflater.inflate(R.layout.vehicle_selector_popup, null)

        /*
            public PopupWindow (View contentView, int width, int height)
                Create a new non focusable popup window which can display the contentView.
                The dimension of the window must be passed to this constructor.

                The popup does not provide any background. This should be handled by
                the content view.

            Parameters
                contentView : the popup's content
                width : the popup's width
                height : the popup's height
        */
        // Initialize a new instance of popup window
        val mPopupWindow = PopupWindow(customView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        mPopupWindow.isOutsideTouchable = false

        // Set an elevation value for popup window
        // Call requires API level 21
        if (Build.VERSION.SDK_INT >= 21) {
            mPopupWindow.elevation = 5.0f
        }
        var selected = -1
        val session = LoginSession(context)
        try {
            if (session.vehicle.id != null) {
                for (i in vehicleModel!!.content.indices) {
                    if (session.vehicle.id == vehicleModel.content[i].id) selected = i
                }
            }
        } catch (e: Exception) {
        }
        val cancelImage = customView.findViewById<ImageView>(R.id.cancel_image)
        val recyclerView: RecyclerView = customView.findViewById(R.id.vehicle_recyclerview)
        val addVehicleButton = customView.findViewById<ConstraintLayout>(R.id.add_vehicle_button)
        val okButton = customView.findViewById<Button>(R.id.ok_button)
        val cancelButton = customView.findViewById<Button>(R.id.cancel_button)
        val linearLayoutManager = LinearLayoutManager(recyclerView.context, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.setHasFixedSize(true)
        vehicleSelectAdapter = VehicleSelectAdapter(context, vehicleModel!!.content, selected)
        recyclerView.adapter = vehicleSelectAdapter
        vehicleSelectAdapter!!.setOnItemClickListener(this)
        cancelImage.setOnClickListener { mPopupWindow.dismiss() }
        cancelButton.setOnClickListener { mPopupWindow.dismiss() }
        okButton.setOnClickListener { //                MySocket mySocket = (MySocket)activity.getApplication();
//                new LoginSession(activity).createVehicleSession(vehicle);
//                SocketReconnect.driverConnect(context, mySocket.getmSocket());
//                mPopupWindow.dismiss();
            if (vehicle == null) {
                Toast.makeText(context, "Please select a vehicle", Toast.LENGTH_SHORT).show()
            } else {
                selectVehicleApiCall(mPopupWindow, vehicle!!)
            }
        }
        addVehicleButton.setOnClickListener {
            try {
                val intent = Intent(context, AddVehicleActivity::class.java)
                customView.context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mPopupWindow.dismiss()
        }

        /*
            public void showAtLocation (View parent, int gravity, int x, int y)
                Display the content view in a popup window at the specified location. If the
                popup window cannot fit on screen, it will be clipped.
                Learn WindowManager.LayoutParams for more information on how gravity and the x
                and y parameters are related. Specifying a gravity of NO_GRAVITY is similar
                to specifying Gravity.LEFT | Gravity.TOP.

            Parameters
                parent : a parent view to get the getWindowToken() token from
                gravity : the gravity which controls the placement of the popup window
                x : the popup's x location offset
                y : the popup's y location offset
        */


        // Finally, show the popup window at the center location of root relative layout
         mPopupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0)
    }

    override fun onItemClickListener(position: Int, vehicleList: List<Content>) {
        vehicleSelectAdapter!!.selectedItem()
        vehicle = vehicleList[position]
    }

    /**
     * API call for the select vehicle
     * @param mPopupWindow
     * @param vehicle
     */
    private fun selectVehicleApiCall(mPopupWindow: PopupWindow, vehicle: Content) {
        val model = DriverCheckRequestModel()
        val session = LoginSession(context)
        model.driverId = session.userDetails.content.id
        model.vehicleId = vehicle.id
        progressDialog = ProgressDialog(activity)
        progressDialog!!.setMessage("Loading...")
        progressDialog!!.show()
        val apiInterface = JsonApiClient.apiClient.create(ApiInterface::class.java)
        val call = apiInterface.selectVehicle(model)
        call.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                progressDialog!!.dismiss()
                Log.i("TAG CHECK DRIVER CODE", response.code().toString())
                if (response.code() == 200) {
                    val socket = activity.application as MySocket
                    mSocket = socket.getmSocket()
                    LoginSession(activity).createVehicleSession(vehicle)
                    //SocketReconnect.driverConnect(context, mySocket.getmSocket());
                    var state = true
                    while (state) {
                        if (mSocket?.connected() == true) {
                            SocketReconnect.driverConnect(context, mSocket)
                            state = false
                            Log.i("TAG_DRIVER_CONNECT", "true")
                        } else {
                            Log.i("TAG_DRIVER_CONNECT", "false")
                        }
                    }
                    checkDriver(context)
                    mPopupWindow.dismiss()
                } else {
                    val alertDialog = AlertDialog.Builder(activity).create()
                    alertDialog.setTitle("Something Went Wrong...")
                    alertDialog.setMessage("Place try again")
                    alertDialog.setCanceledOnTouchOutside(false)
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK"
                    ) { dialog, which -> dialog.dismiss() }
                    alertDialog.show()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                progressDialog!!.dismiss()
                Log.i("TAG CHECK DRIVER FAIL", t.message!!)
                val alertDialog = AlertDialog.Builder(activity).create()
                alertDialog.setTitle("Something Went Wrong...")
                alertDialog.setMessage("Place try again")
                alertDialog.setCanceledOnTouchOutside(false)
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK"
                ) { dialog, which -> dialog.dismiss() }
                alertDialog.show()
            }
        })
    }

    companion object {
        /**
         * method for check driver detail when app starting time
         * @param context
         */
        fun checkDriver(context: Context?) {
            val model = DriverCheckRequestModel()
            val session = LoginSession(context)
            model.driverId = session.userDetails.content.id
            model.vehicleId = session.vehicle.id

//        progressDialog = new ProgressDialog(context);
//        progressDialog.setMessage("Loading...");
//        progressDialog.show();
            val apiInterface = JsonApiClient.apiClient.create(ApiInterface::class.java)
            val call = apiInterface.driverCheck(model)
            call.enqueue(object : Callback<DriverCheckResponseModel?> {
                override fun onResponse(call: Call<DriverCheckResponseModel?>, response: Response<DriverCheckResponseModel?>) {
                    //progressDialog.dismiss();
                    Log.i("TAG CHECK DRIVER CODE", response.code().toString())
                    val session1 = LoginSession(context)
                    if (response.code() == 200) {
                        session1.driverEnable = response.body()!!.driverDispatch[0].isEnable
                        session1.dispatcherEnable = response.body()!!.driverDispatch[0].isDispatchEnable
                        session1.setVehicleEnable(response.body()!!.vehicle.isEnable)
                        session1.createSubCategoryImageSession(response.body()!!.subCategoryImage)
                        if (response.body()!!.driverDispatch[0].isDispatchEnable) {
                            session1.createDispatcherSession(response.body()!!.driverDispatch[0].dispatcher[0])
                        } else {
                            session1.createDispatcherSession(null)
                        }
                    } else if (response.code() == 203) {
                        session1.driverEnable = response.body()!!.driverDispatch[0].isEnable
                        session1.dispatcherEnable = response.body()!!.driverDispatch[0].isDispatchEnable
                        session1.setVehicleEnable(false)
                    }
                }

                override fun onFailure(call: Call<DriverCheckResponseModel?>, t: Throwable) {
                    //progressDialog.dismiss();
                    Log.i("TAG CHECK DRIVER FAIL", t.message!!)
                }
            })
        }
    }

    init {
        vehicle = LoginSession(context).vehicle
        getVehicle()
    }
}