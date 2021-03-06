package com.example.tptdl

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.tptdl.weatherAPI.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.*

class WeatherService : Service() {

    private val binder: IBinder = WeatherServiceBinder()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentWeather: WeatherState? = null

    override fun onCreate() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    /* Tries to get current location, if there is none, return null.
    Otherwise, it returns the current weather. */
    fun updateWeather() : WeatherState?  {

        val location = getCurrentLocation() ?: return null

        val weather = Weather()

        currentWeather = weather.fetchCurrent(location)

        return currentWeather
    }

    private fun getCurrentLocation() : Location? {

        if (!checkForPermissions()) return null

        val cancellationSource = CancellationTokenSource()

        return try {
            Tasks.await(fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY,cancellationSource.token))
        } catch (e: SecurityException) {
            println("Couldn't fetch current location: $e")
            null
        } catch (e: Exception) {
            println("Couldn't fetch current location: $e")
            null
        }
    }

    private fun checkForPermissions() : Boolean {
        if (ActivityCompat.checkSelfPermission(
                baseContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                baseContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        return true
    }

    inner class WeatherServiceBinder : Binder() {
        val service: WeatherService = this@WeatherService
    }
}