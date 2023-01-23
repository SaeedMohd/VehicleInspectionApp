package com.inspection.interfaces

import android.Manifest
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationProvider
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.inspection.MainActivity
import com.inspection.MainActivity.Companion.activity
import com.inspection.Utils.ApplicationPrefs
import com.inspection.Utils.Constants
import com.inspection.Utils.Utility
import java.util.*

class LocationLogService : JobService() {
    private var urlString = ""
    private var locInfoString = ""
    private var fusedLocationProviderClient : FusedLocationProviderClient? = null
    override fun onStartJob(parameters: JobParameters?): Boolean {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(applicationContext)
        urlString = parameters!!.extras.getString("urlString").toString()
        if (checkLocationPermissions()) {
            try {
                val task = fusedLocationProviderClient!!.getLastLocation();
                task.addOnSuccessListener {
                    if (it != null) {
                        Log.v("AndroidClarified", it.getLatitude().toString() + " " + it.getLongitude());
                        logLocationtoDB(urlString+"&latitude=${it.getLatitude()}&longitude=${it.longitude}")
                    } else {
                        logLocationtoDB(urlString+"&latitude=Unable to get Location&longitude=Unable to get Location")
                    }
                }
                } catch (e: SecurityException) {

                }
        } else {
            if (!checkLocationPermissions()) {
                if (activity != null) {
                    requestPermissionAndContinue();
                }
            } else {
                try {
                    val task = fusedLocationProviderClient!!.getLastLocation();
                    task.addOnSuccessListener {
                        if (it != null) {
                            Log.v("AndroidClarified", it.getLatitude().toString() + " " + it.getLongitude());
                            logLocationtoDB(urlString+"&latitude=${it.getLatitude()}&longitude=${it.longitude}")
                        } else {
                            logLocationtoDB(urlString+"&latitude=Unable to get Location&longitude=Unable to get Location")
                        }
                    }
                } catch (e: SecurityException) {

                }
            }
        }
        startNewJob(parameters)
        return false
    }

    fun logLocationtoDB(urlString: String) {
        Log.v("ALARM ------------> ", "LocationLogService "+ locInfoString)
        Volley.newRequestQueue(applicationContext).add(StringRequest(Request.Method.GET, Constants.logTracking + urlString,
                Response.Listener { response ->
                    Log.v("ALARM ------------> ", locInfoString)
                }, Response.ErrorListener {
        }))
    }

    override fun onStopJob(parameters: JobParameters?): Boolean {
        return false
    }

    fun startNewJob(parameters: JobParameters?){
        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        val jobInfo = JobInfo.Builder(12, ComponentName(applicationContext, LocationLogService::class.java))
                // only add if network access is required
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setMinimumLatency(1000*60)
                .setOverrideDeadline(1000*60*3)
                .setPersisted(true)
                .setExtras(parameters!!.extras)
                .build()

        jobScheduler.schedule(jobInfo)
    }



    fun checkLocationPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermissionAndContinue() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity as MainActivity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 350);
        } else {
            try {
                val task = fusedLocationProviderClient!!.getLastLocation();
                task.addOnSuccessListener {
                    if (it != null) {
                        Log.v("AndroidClarified", it.getLatitude().toString() + " " + it.getLongitude());
                        logLocationtoDB(urlString+"&latitude=${it.getLatitude()}&longitude=${it.longitude}")
                    } else {
                        logLocationtoDB(urlString+"&latitude=Unable to get Location&longitude=Unable to get Location")
                    }
                }
            } catch (e: SecurityException) {

            }
        }
    }


}