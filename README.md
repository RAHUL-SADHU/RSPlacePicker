# RSPlacePicker
Pick a location and get latitude, longitude and map imageUrl

# Download 
Add Jitpack in your root build.gradle at the end of repositories:
```
allprojects {
  repositories {
	...
	maven { url 'https://jitpack.io' }
    }
}

```
Step 2. Add the dependency
```
dependencies {
   implementation 'com.github.RAHUL-SADHU:RSPlacePicker:1.0.0_alpha01'
 }

```

## Setup
1. Add Google Play Services to your project - [How to](https://developers.google.com/android/guides/setup).
2. Sign up for API keys - [How to](https://developers.google.com/places/android-sdk/signup)
3. Add the Android API key to your AndroidManifest file as in the [sample project](https://github.com/RAHUL-SADHU/RSPlacePicker/blob/master/app/src/main/AndroidManifest.xml).

### Add code your activity
```
 val placePicker = RSPlacePicker()
                   .setAndroidApiKey("AIzaSyDP7GmYr0Om-EuTqFUkqhMRQmxlbCG6-3I")
		   .build(this)
 startActivityForResult(placePicker, REQUEST_PLACE_PICKER)
 
 override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == REQUEST_PLACE_PICKER) && (resultCode == Activity.RESULT_OK)) {
            val location: LocationModel? = data?.let { RSPlacePicker.getLocation(it) }
            Toast.makeText(this, "latitude: ${location?.latitude} longitude: ${location?.longitude} 
	    imageUrl:${location?.mapImage}", Toast.LENGTH_LONG).show()
        }
  }
 
```

