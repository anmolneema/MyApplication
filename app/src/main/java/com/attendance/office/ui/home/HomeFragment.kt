package com.attendance.office.ui.home


import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.GradientDrawable
import android.location.*
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.attendance.office.AppConstant
import com.attendance.office.AppUtil
import com.attendance.office.MainActivity
import com.attendance.office.R
import com.attendance.office.repository.roomdb.employee.Info
import com.attendance.office.tensorflow.lite.examples.detection.DetectorActivity
import com.attendance.office.ui.info.FetchInfoModal
import com.attendance.office.volley.detail.GetEmployeeMarkedDetails
import com.attendance.office.volley.markinout.Data
import com.attendance.office.volley.markinout.EmployeeMarkedDetail
import com.attendance.office.volley.markinout.MarkInOut
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationListener
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment(), OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    LocationListener, ResultCallback<LocationSettingsResult>, GoogleMap.OnMapLoadedCallback,
    GetEmployeeMarkedDetails.MarkedDetails, MarkInOut.MarkInOutDetail {
    private var isDetailFetched: Boolean = false
    private var progressBar: LinearLayout? = null
    private var infoEmpModal: FetchInfoModal? = null
    private var markInId: String? = ""
    private var markInPlace: String? = ""
    private var markOutPlace: String? = ""
    private var attendaceDate: String = ""
    private var signIN: String = ""
    private var signOut: String = ""
    private var hours: String = ""
    private var fetchEmpId: String = ""
    private var name: String = ""
    private var isMapLoaded: Boolean = false
    private var isRefereshMap: Boolean = false
    private var timer: CountDownTimer? = null
    private var mapTimer: CountDownTimer? = null
    private var mRequestingLocationUpdates: Boolean = false
    private var geoAddress: String = ""
    private var latLng: LatLng = LatLng(0.0, 0.0)
    private var mDialog: Dialog? = null
    private var mConfirmDialog: Dialog? = null
    private var mapView: MapView? = null
    private var googleMap: GoogleMap? = null
    var buttonGradientDrawable: GradientDrawable? = null

    var mGoogleApiClient: GoogleApiClient? = null
    var mLastLocation: Location? = null
    var mLocationRequest: LocationRequest? = null
    val REQUEST_CHECK_SETTINGS = 0x1
    var alert: AlertDialog? = null
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var mLocationSettingsRequest: LocationSettingsRequest? = null
    private var count: Int = 20;
    var timeStamp: String = ""
    var timeStampUpload: String = ""
    var rlHomeLayout: RelativeLayout? = null

    companion object {
        var mapFragment: SupportMapFragment? = null
        val TAG: String = MapFragment::class.java.simpleName
        fun newInstance() = MapFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater?.inflate(R.layout.fragment_home, container, false)
//        mMap = view?.findViewById(R.id.map_frag) as MapView
//        mMap?.onCreate(savedInstanceState)
//        mMap?.getMapAsync(this)
//        mapFragment = fragmentManager?.findFragmentById(R.id.map_frag) as SupportMapFragment?
//        mapFragment?.getMapAsync(this)
//        mapView = view.findViewById<View>(R.id.map_frag) as MapView
//        mapView!!.onCreate(savedInstanceState)
        mapFragment =
            childFragmentManager!!.findFragmentById(R.id.map_frag) as SupportMapFragment
        GetEmployeeMarkedDetails.setCallBack(this)
        MarkInOut.setCallBack(this)
        buildGoogleApiClient()
        createLocationRequest()
        buildLocationSettingsRequest()
        checkLocationSettings()
        initView(view);
        return view
    }

    private fun getMarkInReport() {
        GlobalScope.launch {
            GetEmployeeMarkedDetails.getMarkedDetails(
                ((activity as MainActivity).empId),
                requireContext()
            )
        }
    }

    private fun buildLocationSettingsRequest() {

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest!!)
        mLocationSettingsRequest = builder.build()
    }

    private fun createLocationRequest() {
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 1000
        mLocationRequest!!.fastestInterval = 1000
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

    }

    private fun initView(view: View?) {
        ((activity as MainActivity).isHomeFragment) = true
        var myLocation: Button = view?.findViewById(R.id.myLocation) as Button
        myLocation.setOnClickListener(View.OnClickListener {
            animateMap();
        })
        var markIn: Button = view?.findViewById(R.id.markIn) as Button
        var markOut: Button = view?.findViewById(R.id.markOut) as Button
        markIn.setOnClickListener(View.OnClickListener {
            if (isMapLoaded) {
                openDialog(AppConstant.MARKIN)
            } else {
                Toast.makeText(context, AppConstant.CAPTURING_LAT_LONG, Toast.LENGTH_SHORT).show()
            }
        })
        markOut.setOnClickListener(View.OnClickListener {
            if (isMapLoaded) {
                openDialog(AppConstant.MARKOUT)
            } else {
                Toast.makeText(context, AppConstant.CAPTURING_LAT_LONG, Toast.LENGTH_SHORT).show()
            }
        })
        rlHomeLayout = view?.findViewById(R.id.rlHomeLayout)
        progressBar = view?.findViewById(R.id.progressBar)
    }


    private fun openDialog(checkIn: String) {
        try {
            getMarkInReport()
            count = 20;
            mDialog = Dialog(requireContext())
            mDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            mDialog!!.setContentView(R.layout.dialog_in_out)
            val confirm: TextView
            val cancel: TextView
            val lat: TextView
            val long: TextView
            val dearUser: TextView
            val address: TextView
            val tvTime: TextView
            val tvNoAddress: TextView
            val tvDialogDs: TextView
            val llOk: LinearLayout
            val llConfirm: LinearLayout
            val btnOK: TextView
            val ivMark: ImageView
            confirm = mDialog!!.findViewById(R.id.btnConfirm)
            cancel = mDialog!!.findViewById(R.id.btnCancel)
            lat = mDialog!!.findViewById(R.id.latlong)
            tvNoAddress = mDialog!!.findViewById(R.id.tv_no_latlong)
            dearUser = mDialog!!.findViewById(R.id.dearUser)
            address = mDialog!!.findViewById(R.id.address)
            tvTime = mDialog!!.findViewById(R.id.tvTime)
            tvDialogDs = mDialog!!.findViewById(R.id.tv_dialog_disappear)
            llOk = mDialog!!.findViewById(R.id.llOk)
            llConfirm = mDialog!!.findViewById(R.id.llConfirm)
            btnOK = mDialog!!.findViewById(R.id.btnOK)
            ivMark = mDialog!!.findViewById(R.id.iv_mark)
            if (latLng == null) {
                lat.visibility == View.GONE
                dearUser.visibility == View.GONE
                address.visibility == View.GONE
                tvTime.visibility == View.GONE
                tvDialogDs.visibility == View.GONE
                llConfirm.visibility == View.GONE
                llOk.visibility == View.VISIBLE
                tvNoAddress.visibility == View.VISIBLE
            } else {
                startTimer(mDialog!!, tvTime);
                lat.visibility == View.VISIBLE
                dearUser.visibility == View.VISIBLE
                address.visibility == View.VISIBLE
                tvTime.visibility == View.VISIBLE
                tvDialogDs.visibility == View.VISIBLE
                llConfirm.visibility == View.VISIBLE
                llOk.visibility == View.GONE
                tvNoAddress.visibility == View.GONE
            }
            if (checkIn.equals(AppConstant.MARKIN, true)) {
                ivMark.setImageDrawable(resources.getDrawable(R.drawable.check))
                dearUser.setText("Dear " + (activity as MainActivity).empName + " (" + (activity as MainActivity).empId + ") you are marking in from:")
            } else {
                ivMark.setImageDrawable(resources.getDrawable(R.drawable.check_out))
                dearUser.setText("Dear " + (activity as MainActivity).empName + " (" + (activity as MainActivity).empId + ") you are marking out from:")
            }
            if (latLng != null) {
                geoAddress = getAddress(latLng!!)
                lat.setText("Location: " + latLng!!.latitude + "," + latLng!!.longitude)
            } else {
                lat.setText("Location: -,-")
            }
            address.setText("Address: " + geoAddress)
            confirm.setOnClickListener(View.OnClickListener {

                stopTimer()
                mDialog!!.cancel()
                openConfirmDialog(checkIn)

            })
            cancel.setOnClickListener(View.OnClickListener {
                stopTimer()
                mDialog!!.cancel()


            })
            btnOK.setOnClickListener(View.OnClickListener {
                stopTimer()
                mDialog!!.cancel()
            })
            if (!mDialog!!.isShowing)
                mDialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopTimer() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    private fun startTimer(mDialog: Dialog, tvTime: TextView) {
        timer = object : CountDownTimer(20000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tvTime.setText(count!!.toString())
                count--;
            }

            override fun onFinish() {
                if (mDialog != null)
                    mDialog.dismiss()
            }
        }.start()
    }

    private fun openConfirmDialog(checkIn: String) {
        try {
            timeStamp = getTimeFromDate()
            timeStampUpload = getTimeUploadFromDate()
            mConfirmDialog = Dialog(requireContext())
            mConfirmDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            mConfirmDialog!!.setContentView(R.layout.dialog_in_out_time)
            val confirm: TextView
            val tvDate: TextView
            val tvLat: TextView
            val tvGreeting: TextView
            val tvAddress: TextView
            val tvEmpName: TextView
            val tvLatValue: TextView
            val tvMarAddress: TextView
            confirm = mConfirmDialog!!.findViewById(R.id.mark_ok)
            tvDate = mConfirmDialog!!.findViewById(R.id.tv_date_time)
            tvLat = mConfirmDialog!!.findViewById(R.id.tv_lat_long)
            tvLatValue = mConfirmDialog!!.findViewById(R.id.tv_latlong_value)
            tvMarAddress = mConfirmDialog!!.findViewById(R.id.tv_mar_address)
            tvGreeting = mConfirmDialog!!.findViewById(R.id.tv_greeting)
            tvAddress = mConfirmDialog!!.findViewById(R.id.tv_address_value)
            tvEmpName = mConfirmDialog!!.findViewById(R.id.tv_empl_name)
            tvEmpName.setText("Dear " + (activity as MainActivity).empName + " (" + (activity as MainActivity).empId + ") ,")
            if (checkIn.equals(AppConstant.MARKIN, true)) {
                tvGreeting.setText("Thank you, you have marked in successfully")
                tvLat.setText("MarkIn location:")
                tvMarAddress.setText("MarkIn address:")
            } else {
                tvGreeting.setText("Thank you, you have marked out successfully")
                tvLat.setText("MarkOut location:")
                tvMarAddress.setText("MarkOut address:")
            }
            if (latLng != null) {
                geoAddress = getAddress(latLng!!)
                tvLatValue.setText("Location: " + latLng!!.latitude + "," + latLng!!.longitude)
            } else {
                tvLatValue.setText("Location: -,-")
            }
            tvAddress.setText("Address: " + geoAddress)
            //getDate ad time Wed, 14 Jul 2021, 20:15:47

            tvDate.setText(timeStamp)
            confirm.setOnClickListener(View.OnClickListener {
                AppUtil.showProgress(progressBar)
                if (!isDetailFetched) {
                    val handler: Handler = Handler()
                    handler.postDelayed(object : Runnable {
                        override fun run() {
                            mConfirmDialog!!.cancel()
                            confirmButton(checkIn)
                        }

                    }, 2000);
                } else {
                    mConfirmDialog!!.cancel()
                    confirmButton(checkIn)
                }
            })

            if (!mConfirmDialog!!.isShowing)
                mConfirmDialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getTimeFromDate(): String {
        var dateStr = ""
        try {
            val df: SimpleDateFormat = SimpleDateFormat("EEE, d MMM yyyy, hh:mm:ss a")
            dateStr = df.format(Calendar.getInstance().time)
        } catch (e: ParseException) {
            e.printStackTrace();
        }

        return dateStr
    }

    private fun getTimeUploadFromDate(): String {
        var dateStr = ""
        try {
            val df: SimpleDateFormat = SimpleDateFormat("yyyy-MM-d hh:mm:ss a")
            dateStr = df.format(Calendar.getInstance().time)
            Log.d("TAG", "Parsed date" + dateStr)
        } catch (e: ParseException) {
            e.printStackTrace();
        }

        return dateStr
    }


    private fun confirmButton(checkIn: String) {
        try {
            if (((activity as MainActivity)).db != null) {
                GlobalScope.launch {
                    val info: Info = Info()
                    info.setLat(latLng!!.latitude.toString())
                    info.setLon(latLng!!.longitude.toString())
                    info.setTime(timeStampUpload)
                    info.setAddress(geoAddress)
                    info.setMark(checkIn)
                    MarkInOut.getMarkIODetail(
                        (activity as MainActivity).empId,
                        info, requireContext()
                    )
                }
            }
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
    }


    private fun animateMap() {
        try {
            latLng = getCurrentLocation()
            Log.d(TAG, "Map showing animate")
            if (latLng != null && (latLng.latitude != 0.0 || latLng.longitude != 0.0) && googleMap != null) {
                googleMap!!.clear()
                setMapListener(googleMap!!)
                Log.d(TAG, "Map showing animate twice")
                val addMarker = googleMap!!.addMarker(
                    MarkerOptions().position(latLng!!).title("I am Here").icon(
                        BitmapFromVector(
                            requireContext(),
                            R.drawable.boy
                        )
                    )
                )
                addMarker.showInfoWindow()
                // For zooming automatically to the location of the marker
                val cameraPosition = CameraPosition.Builder().target(latLng).zoom(16f).build()
                googleMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                stopMapTimer()
            }
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
        //move map camera
    }

    private fun setMapListener(googleMap: GoogleMap) {
        googleMap!!.setOnMapLoadedCallback(this)
    }


    private fun stopMapTimer() {
        Log.d("new", ": request location stop")
        if (mapTimer != null) {

            mapTimer!!.cancel()
            mapTimer = null
        }

    }

    private fun refereshMap() {
        startMapTimer(2000, false)
    }


    override fun onResume() {
        super.onResume()
        //  checkGps()
        if (mGoogleApiClient != null && mGoogleApiClient!!.isConnected() && mRequestingLocationUpdates!!) {
            startLocationUpdates();
        }
    }

    override fun onPause() {

        super.onPause()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {

        super.onStop()
    }

    override fun onDestroy() {

        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }

    override fun onMapReady(mMap: GoogleMap?) {

        try {
            if (googleMap != null) {
                googleMap!!.clear()
            }
            googleMap = mMap
            latLng = getCurrentLocation()
            // For showing a move to my location button
            //googleMap!!.setMyLocationEnabled(true)
            // For dropping a marker at a point on the Map
            if (latLng != null) {
                val addMarker = googleMap!!.addMarker(
                    MarkerOptions().position(latLng!!).title("I am Here").icon(
                        BitmapFromVector(
                            requireContext(),
                            R.drawable.boy
                        )
                    )
                )
                addMarker.showInfoWindow()
                // For zooming automatically to the location of the marker
                val cameraPosition = CameraPosition.Builder().target(latLng).zoom(16f).build()
                googleMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getAddress(latLng: LatLng): String {
        var geoAddress = ""
        try {

            val geocoder = Geocoder(requireContext(), Locale.getDefault())
            val listAddresses: List<Address>? = geocoder.getFromLocation(
                latLng!!.latitude,
                latLng!!.longitude, 1
            )
            if (null != listAddresses && listAddresses.size > 0) {
                var address: String? = listAddresses.get(0).getAddressLine(0)
                val state: String = listAddresses[0].getAdminArea()
                val country: String = listAddresses[0].getCountryName()
                val subLocality: String = listAddresses[0].getSubLocality()
                val postalCode: String = listAddresses.get(0).getPostalCode()
                geoAddress = address!!
            }

        } catch (e: Exception) {

        }
        return geoAddress
    }

    /*req*/


    @Synchronized
    protected fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(requireActivity())
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        mGoogleApiClient!!.connect()
    }


    protected fun checkLocationSettings() {
        val result: PendingResult<LocationSettingsResult> =
            LocationServices.SettingsApi.checkLocationSettings(
                mGoogleApiClient,
                mLocationSettingsRequest
            )
        result.setResultCallback(this)
    }


    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected fun startLocationUpdates() {

        LocationServices.FusedLocationApi.requestLocationUpdates(
            mGoogleApiClient,
            mLocationRequest,
            this
        ).setResultCallback(object : ResultCallback<Status?>, OnMapReadyCallback {
            override fun onResult(p0: Status) {
                mRequestingLocationUpdates = true
                mapFragment!!.getMapAsync(this)
                //onMapReady(googleMap)
            }

            override fun onMapReady(mMap: GoogleMap?) {
                try {
                    Log.d(TAG, "Map showing")
                    googleMap = mMap
                    latLng = getCurrentLocation()
                    // For showing a move to my location button
                    //googleMap!!.setMyLocationEnabled(true)
                    // For dropping a marker at a point on the Map
                    if (latLng != null && (latLng.latitude != 0.0 || latLng.longitude != 0.0) && googleMap != null) {
                        setMapListener(googleMap!!)
                        googleMap!!.clear()
                        Log.d(TAG, "Map showing twice")
                        val addMarker = googleMap!!.addMarker(
                            MarkerOptions().position(latLng!!).title("I am Here").icon(
                                BitmapFromVector(
                                    requireContext(),
                                    R.drawable.boy
                                )
                            )
                        )
                        addMarker.showInfoWindow()
                        // For zooming automatically to the location of the marker
                        val cameraPosition =
                            CameraPosition.Builder().target(latLng).zoom(16f).build()
                        googleMap!!.animateCamera(
                            CameraUpdateFactory.newCameraPosition(
                                cameraPosition
                            )
                        )
                    } else {
                        // startLocationUpdates()

                        startMapTimer(5000, true);

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }


        })
    }

    private fun startMapTimer(refereshTime: Long, isFrom: Boolean) {
        mapTimer = object : CountDownTimer(refereshTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.d("request ", ": request location")
                if (isFrom)
                    animateMap()
            }

            override fun onFinish() {
                animateMap()
            }
        }.start()
    }

    override fun onResult(locationSettingsResult: LocationSettingsResult) {
        val status: Status = locationSettingsResult.status
        when (status.getStatusCode()) {
            LocationSettingsStatusCodes.SUCCESS -> {
                Log.i(TAG, "All location settings are satisfied.")
                startLocationUpdates()
            }
            LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                Log.i(
                    TAG, "Location settings are not satisfied. Show the user a dialog to" +
                            "upgrade location settings "
                )
                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().
                    // status.startResolutionForResult(requireActivity(), REQUEST_CHECK_SETTINGS)
                    startIntentSenderForResult(
                        status.getResolution().getIntentSender(),
                        REQUEST_CHECK_SETTINGS,
                        null,
                        0,
                        0,
                        0,
                        null
                    );
                } catch (e: SendIntentException) {
                    Log.i(TAG, "PendingIntent unable to execute request.")
                }
            }
            LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.i(
                TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                        "not created."
            )
        }
    }

    override fun onConnected(bundle: Bundle?) {

        if (mLastLocation == null) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)
        }
    }

    override fun onConnectionSuspended(i: Int) {}

    override fun onLocationChanged(location: Location) {
        mLastLocation = location
        if (mLastLocation != null)
            Log.d("check mao", "new  mao" + mLastLocation)
    }


    public fun getCurrentLocation(): LatLng {
        try {

            if (mLastLocation != null) {
                latLng = LatLng(mLastLocation!!.latitude, mLastLocation!!.longitude)
            } else {
                val locationManager =
                    requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val provider = locationManager.getBestProvider(Criteria(), true)
//                if (ActivityCompat.checkSelfPermission(
//                        applicationContext,
//                        Manifest.permission.ACCESS_FINE_LOCATION
//                    ) != PackageManager.PERMISSION_GRANTED &&
//                    ActivityCompat.checkSelfPermission(
//                        applicationContext,
//                        Manifest.permission.ACCESS_COARSE_LOCATION
//                    )
//                    != PackageManager.PERMISSION_GRANTED
//                ) {
//
//                }
                val locations = locationManager.getLastKnownLocation(provider!!)
                val providerList = locationManager.allProviders
                if (null != locations && null != providerList && providerList.size > 0) {
                    longitude = locations.longitude
                    latitude = locations.latitude
                    latLng = LatLng(latitude!!, longitude!!)
                }
            }

        } catch (e: Exception) {

        }
        return latLng!!
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }

    private fun BitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        // below line is use to generate a drawable.
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)

        // below line is use to set bounds to our vector drawable.
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )

        // below line is use to create a bitmap for our
        // drawable which we have added.
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        // below line is use to add bitmap in our canvas.
        val canvas = Canvas(bitmap)

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas)

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> when (resultCode) {
                Activity.RESULT_OK -> {
                    Log.i(TAG, "User agreed to make required location settings changes.")
                    isMapLoaded = false
                    startLocationUpdates()
                }
                Activity.RESULT_CANCELED -> isMapLoaded = false
            }
        }
    }

    override fun onMapLoaded() {
        Log.d("onMapLoaded", "map" + isMapLoaded)
        isMapLoaded = true
        if (!isRefereshMap) {
            isRefereshMap = true
            refereshMap()
        }
    }

    override fun getAllMarkedDetails(json: List<Data>) {
        if (json != null && json.size > 0) {
            json.forEach {
                attendaceDate = it.create_at.toString()!!
                signIN = it.in_time.toString()!!
                signOut = it.out_time.toString()
                if(it.working_hour!=null) {
                    hours = it.working_hour.toString()
                }else{
                    hours = "00:00"
                }
               // fetchEmpId = it.emp_id.toString()
              //  name = it.name.toString()
                markInId = it.id.toString()
                if(it.log!=null && it.log.size>0){
                    for(i in 0..(it.log.size-1)){
                        if(AppConstant.PUNCH_IN.equals(it.log[i].type.toString(), true)){
                            markInPlace = it.log[i].address
                            //  markOutPlace =""
                        }else{
                            //  markInPlace =""
                            markOutPlace =  it.log[i].address
                        }
                    }
                }
                infoEmpModal = FetchInfoModal(
                    hours,
                    attendaceDate,
                    fetchEmpId,
                    markInId,
                    name,
                    signIN,
                    signOut,
                    markInPlace,
                    markOutPlace
                )

            }
        } else {
            infoEmpModal = FetchInfoModal(
                hours,
                attendaceDate,
                fetchEmpId,
                markInId,
                name,
                signIN,
                signOut,
                markInPlace,
                markOutPlace
            )
        }
        isDetailFetched = true;

    }

    override fun getAllMarkedError(failure: String) {
        Snackbar.make(rlHomeLayout!!, failure, Snackbar.LENGTH_SHORT).show()
    }

    override fun getMarkInOutDetails(json: String, info: Info) {
        AppUtil.cancelProgress(progressBar)
        if (info.getMark() != null && info.getMark().equals(AppConstant.MARKIN, true)) {
            Snackbar.make(rlHomeLayout!!, AppConstant.MARKED_IN_SUCCESSFULLY, Snackbar.LENGTH_SHORT)
                .show()
        } else {
            Snackbar.make(
                rlHomeLayout!!,
                AppConstant.MARKED_OUT_SUCCESSFULLY,
                Snackbar.LENGTH_SHORT
            )
                .show()
        }
        GlobalScope.launch {
            var id = (activity as MainActivity).db!!.infoDao().insert(info)
            Log.d("cehck in", "id " + id)
        }


    }

    override fun getMarkInOutError(json: String) {
        AppUtil.cancelProgress(progressBar)
        Snackbar.make(rlHomeLayout!!, json, Snackbar.LENGTH_SHORT).show()
    }
}
