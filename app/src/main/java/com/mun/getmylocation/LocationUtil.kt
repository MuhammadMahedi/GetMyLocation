package com.mun.getmylocation
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import java.util.Locale

class LocationUtil(private val activity: Activity) {

    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
    private val locationRequest: LocationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        1000L
    ).build()

    private val locationSettingsRequest: LocationSettingsRequest = LocationSettingsRequest.Builder()
        .addLocationRequest(locationRequest)
        .build()

    companion object {
        const val REQUEST_CHECK_SETTINGS = 1001
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    // Check if GPS is enabled and ask the user to enable it if necessary
    fun checkLocationSettings() {
        val settingsClient: SettingsClient = LocationServices.getSettingsClient(activity)

        val task: Task<LocationSettingsResponse> = settingsClient.checkLocationSettings(locationSettingsRequest)

        task.addOnSuccessListener {
            // Location settings are satisfied, get the location
           // getLocation()
            requestLocationUpdates()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    sendEx.printStackTrace()
                }
            }
        }
    }

    // Function to get the current location
//    fun getLocation() {
//        if (ActivityCompat.checkSelfPermission(
//                activity,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//            && ActivityCompat.checkSelfPermission(
//                activity,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                activity,
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
//                LOCATION_PERMISSION_REQUEST_CODE
//            )
//            return
//        }
//
//        fusedLocationClient.lastLocation
//            .addOnSuccessListener { location: Location? ->
//                if (location != null) {
//                    val latitude = location.latitude
//                    val longitude = location.longitude
//                    val country = getCountryFromLocation(latitude, longitude)
//                    Toast.makeText(activity, "Lat: $latitude, Lon: $longitude, Country: $country", Toast.LENGTH_LONG).show()
//                } else {
//                    Toast.makeText(activity, "Unable to get location", Toast.LENGTH_LONG).show()
//                }
//            }


    // Request fresh location updates if getLastLocation() returns null
    fun requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                activity, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    val country = getCountryFromLocation(latitude, longitude)
                    Toast.makeText(activity, "Lat: $latitude, Lon: $longitude, Country: $country", Toast.LENGTH_SHORT).show()
                    // Stop location updates after getting the location
                    fusedLocationClient.removeLocationUpdates(this)

                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }


    // Helper function to get the country name from latitude and longitude
    private fun getCountryFromLocation(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(activity, Locale.getDefault())
        if(isInternetAvailable(activity)){
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
             return addresses?.get(0)?.countryName ?: "Country not found"
        }else return "null"
    }

    // Handle permission result in MainActivity or Fragment
    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //getLocation()
                requestLocationUpdates()
            } else {
                Toast.makeText(activity, "Permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Handle the result of the location settings resolution in MainActivity or Fragment
    fun onActivityResult(requestCode: Int, resultCode: Int) {
        if (requestCode == REQUEST_CHECK_SETTINGS && resultCode == Activity.RESULT_OK) {
            //getLocation()
            requestLocationUpdates()
        } else {
            Toast.makeText(activity, "GPS not enabled", Toast.LENGTH_SHORT).show()
        }
    }

    // Helper function to check internet availability
    private fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }
}
