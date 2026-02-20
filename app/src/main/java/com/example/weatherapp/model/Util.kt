package com.example.weatherapp.model

import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object NetworkUtil {

    fun isInternetAvailable(context: Context): Boolean {
        val conntectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = conntectivityManager.activeNetwork ?: return false

        val capabilities = conntectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

    }

}

object LocationUtil {
    fun isLocationEnabled(context: Context): Boolean {
        //// Sistemden konum yöneticisini aldık.
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Cihazda GPS veya Network (baz istasyonu/wifi) üzerinden konum bulma açık mı?
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}