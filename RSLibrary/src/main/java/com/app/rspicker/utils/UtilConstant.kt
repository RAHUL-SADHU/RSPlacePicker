package com.app.rspicker.utils

const val DEFAULT_ZOOM: Float = 15f
const val RC_LOCATION_PERMISSION = 100
const val AUTOCOMPLETE_REQUEST_CODE = 200

const val KEY_LATITUDE = "latitude"
const val KEY_LONGITUDE = "longitude"
const val KEY_IMAGE_MAP = "imageMap"
const val KEY_LOCATION = "location"

const val PLACE_IMG_WIDTH = 640
const val PLACE_IMG_HEIGHT = 320


const val STATIC_MAP_URL = "https://maps.googleapis.com/maps/api/staticmap?" +
        "size=${PLACE_IMG_WIDTH}x$PLACE_IMG_HEIGHT" +
        "&markers=color:red|%.6f,%.6f" +
        "&key=%s"
