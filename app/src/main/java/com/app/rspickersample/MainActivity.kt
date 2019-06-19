package com.app.rspickersample

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.app.rspicker.LocationModel
import com.app.rspicker.RSPlacePicker
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val REQUEST_PLACE_PICKER = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_openPicker.setOnClickListener {
            val placePicker = RSPlacePicker().setAndroidApiKey("YOUR GOOGLE API KEY").build(this)
            startActivityForResult(placePicker, REQUEST_PLACE_PICKER)

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == REQUEST_PLACE_PICKER) && (resultCode == Activity.RESULT_OK)) {
            val location: LocationModel? = data?.let { RSPlacePicker.getLocation(it) }
            Toast.makeText(this, "address: ${location?.address} \nlatitude: ${location?.latitude} " +
                    "\nlongitude: ${location?.longitude} \nimageUrl: ${location?.mapImage}"
                    , Toast.LENGTH_LONG).show()
        }
    }
}
