package com.app.rspicker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.app.rspicker.databinding.ActivityMapsBinding
import com.app.rspicker.utils.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.IOException
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnCameraMoveListener,
    GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraIdleListener, LocationConfirmListener,
    EasyPermissions.PermissionCallbacks {


    private lateinit var mMap: GoogleMap
    private lateinit var mBinding: ActivityMapsBinding
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private val mFusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }
    private var isSelectAutoComplete = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_maps)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        Places.initialize(applicationContext, RSPlacePicker.androidApiKey)
        mBinding.txtSearch.setOnClickListener {
            openAutoCompletePlace()
        }

        mBinding.btnSend.setOnClickListener {
            mMap.snapshot {
                isSelectAutoComplete = false
                val confirmDialog = ConfirmDialog.newInstance(latitude, longitude, it)
                confirmDialog.setConfirmListener(this)
                Utils.showDialog(supportFragmentManager, confirmDialog)
            }

        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnCameraMoveListener(this)
        mMap.setOnCameraIdleListener(this)
        mMap.setOnCameraMoveStartedListener(this)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            enableCurrentLocation()

        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.permission_location),
                RC_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }

    }

    override fun onCameraMove() {
        val cameraPosition = mMap.cameraPosition
        latitude = cameraPosition.target.latitude
        longitude = cameraPosition.target.longitude
        Utils.logInfo(javaClass.simpleName, "$latitude $longitude")
    }

    override fun onCameraMoveStarted(p0: Int) {
        Utils.logInfo("onCameraMoveStarted", "onCameraMoveStarted")
        val params = mBinding.imgMarker.layoutParams as ConstraintLayout.LayoutParams
        params.setMargins(0, 0, 0, 22)
        mBinding.imgMarker.layoutParams = params
    }

    override fun onCameraIdle() {
        Utils.logInfo(javaClass.simpleName, "CameraIdle")
        val params = mBinding.imgMarker.layoutParams as ConstraintLayout.LayoutParams
        params.setMargins(0, 0, 0, 0)
        mBinding.imgMarker.layoutParams = params
        if (isSelectAutoComplete) {
            isSelectAutoComplete = false
        } else {
            mBinding.txtSearch.text = getString(R.string.search_location)
        }

        /* if(isSelectAutoComplete){
             mBinding.btnSend.callOnClick()
         }*/
        //getAddress()

    }

    private fun moveCamera(latitude: Double?, longitude: Double?) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latitude?.let {
            longitude?.let { it1 ->
                LatLng(
                    it,
                    it1
                )
            }
        }, DEFAULT_ZOOM))
    }

    private fun getLastLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mFusedLocationProviderClient.lastLocation
                .addOnSuccessListener {
                    if (it != null) {
                        Log.e("###", " LastKnowLocation ${it.latitude}   ${it.longitude}")
                        moveCamera(it.latitude, it.longitude)
                    }
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            enableCurrentLocation()
        } else if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                RESULT_OK -> {
                    val place = data?.let { Autocomplete.getPlaceFromIntent(it) }
                    Log.i(javaClass.simpleName, "Place: " + place?.name + ", " + place?.id)
                    moveCamera(place?.latLng?.latitude, place?.latLng?.longitude)
                    latitude = place?.latLng?.latitude ?: 0.0
                    longitude = place?.latLng?.longitude ?: 0.0
                    mBinding.txtSearch.text = place?.name
                    isSelectAutoComplete = true
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    // TODO: Handle the error.
                    val status = data?.let { Autocomplete.getStatusFromIntent(it) }
                    Log.e(javaClass.simpleName, status?.statusMessage)
                }
                RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        enableCurrentLocation()
    }

    private fun enableCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            getLastLocation()
        }
    }

    private fun openAutoCompletePlace() {
        val fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.OVERLAY, fields
        )
            .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    override fun locationConfirm(address: String, latitude: Double, longitude: Double, imageUrl: String) {
        val data = Intent()
        val locationModel = LocationModel(address, latitude, longitude, imageUrl)
        data.putExtra(KEY_LOCATION, locationModel)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    private fun getAddress() {
        var addresses: List<Address> = emptyList()

        try {
            addresses = Geocoder(this).getFromLocation(
                latitude,
                longitude,
                // In this sample, we get just a single address.
                1
            )
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                Utils.logInfo(javaClass.simpleName, address.locality + " " + address.featureName)
            }

        } catch (ioException: IOException) {
            // Catch network or other I/O problems.
            Utils.logError(javaClass.simpleName, ioException.localizedMessage)
        } catch (illegalArgumentException: IllegalArgumentException) {
            // Catch invalid latitude or longitude values.
            Utils.logError(javaClass.simpleName, illegalArgumentException.localizedMessage)
        }
    }
}
