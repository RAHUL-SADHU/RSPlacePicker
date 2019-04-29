package com.app.rspicker

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationModel(
        val latitude: Double,
        val longitude: Double,
        val mapImage: String
) : Parcelable