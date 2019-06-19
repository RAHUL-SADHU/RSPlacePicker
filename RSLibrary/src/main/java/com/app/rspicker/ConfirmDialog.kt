package com.app.rspicker

import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.app.rspicker.databinding.DialogConfirmBinding
import com.app.rspicker.utils.*
import java.io.IOException

class ConfirmDialog : DialogFragment() {

    private lateinit var mBinding: DialogConfirmBinding
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var bitmap: Bitmap? = null
    private lateinit var locationConfirmListener: LocationConfirmListener

    companion object {
        fun newInstance(latitude: Double, longitude: Double, bitmap: Bitmap): ConfirmDialog {
            val args = Bundle()
            args.putDouble(KEY_LATITUDE, latitude)
            args.putDouble(KEY_LONGITUDE, longitude)
            args.putParcelable(KEY_IMAGE_MAP, bitmap)
            val fragment = ConfirmDialog()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomDialog)
        latitude = arguments?.getDouble(KEY_LATITUDE) ?: 0.0
        longitude = arguments?.getDouble(KEY_LONGITUDE) ?: 0.0
        bitmap = arguments?.getParcelable(KEY_IMAGE_MAP)

    }

    fun setConfirmListener(locationConfirmListener: LocationConfirmListener) {
        this.locationConfirmListener = locationConfirmListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.dialog_confirm,
            container,
            false
        )

        val address = getAddress()
        mBinding.txtName.text = "${address?.featureName}"
        mBinding.txtAddress.text = "${address?.getAddressLine(0)}"

        val staticMapUrl = STATIC_MAP_URL
            .format(
                latitude,
                longitude,
                RSPlacePicker.androidApiKey
            )

        mBinding.imgMap.setImageBitmap(bitmap)


        mBinding.btnChangeLocation.setOnClickListener {
            dismiss()
        }

        mBinding.btnOk.setOnClickListener {
            address?.getAddressLine(0)
                ?.let { value -> locationConfirmListener.locationConfirm(value, latitude, longitude, staticMapUrl) }
            dismiss()
        }

        return mBinding.root
    }


    private fun getAddress(): Address? {
        var addresses: List<Address> = emptyList()

        try {
            addresses = Geocoder(requireContext()).getFromLocation(
                latitude,
                longitude,
                // In this sample, we get just a single address.
                1
            )
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                Utils.logInfo(javaClass.simpleName, address.locality + " " + address.featureName)
                return address
            }

        } catch (ioException: IOException) {
            // Catch network or other I/O problems.
            Utils.logError(javaClass.simpleName, ioException.localizedMessage)
        } catch (illegalArgumentException: IllegalArgumentException) {
            // Catch invalid latitude or longitude values.
            Utils.logError(javaClass.simpleName, illegalArgumentException.localizedMessage)
        }
        return null
    }

}