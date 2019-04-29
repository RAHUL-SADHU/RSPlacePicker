package com.app.rspicker

import android.app.Activity
import android.content.Intent
import com.app.rspicker.utils.KEY_LOCATION
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException

class RSPlacePicker {


    fun setAndroidApiKey(androidKey: String): RSPlacePicker{
        androidApiKey = androidKey
        return this
    }


    @Throws(GooglePlayServicesNotAvailableException::class)
    fun build(activity: Activity): Intent {
        val intent = Intent()

        val result: Int =
                GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activity)

        if (ConnectionResult.SUCCESS != result) {
            throw GooglePlayServicesNotAvailableException(result)
        }

        intent.setClass(activity, MapsActivity::class.java)
        return intent
    }

    companion object {

        var androidApiKey: String = ""

        fun getLocation(intent: Intent): LocationModel {
            return intent.getParcelableExtra(KEY_LOCATION)
        }
    }
}