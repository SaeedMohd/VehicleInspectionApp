package com.inspection.interfaces

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.inspection.MainActivity.Companion.activity
import com.inspection.Utils.ApplicationPrefs
import com.inspection.Utils.Constants
import com.inspection.Utils.Utility

class LocationLogService : JobService() {
    var urlString = ""

    override fun onStartJob(parameters: JobParameters?): Boolean {
        Log.v("ALARM ------------> ", "LocationLogService Before Volley")
        if (urlString.isNullOrEmpty()) {
            urlString += ApplicationPrefs.getInstance(activity).sessionID
            urlString += "&userId=" + ApplicationPrefs.getInstance(activity).loggedInUserID
            urlString += "&deviceId=" + ApplicationPrefs.getInstance(activity).deviceID
        }
        Volley.newRequestQueue(applicationContext).add(StringRequest(Request.Method.GET, Constants.logTracking + urlString,
                Response.Listener { response ->
                    Log.v("ALARM ------------> ", "LocationLogService Started")
                }, Response.ErrorListener {
        }))
//        Utility.scheduleJob(getApplicationContext())
        return true
    }

    // called when prematurely stopped
    override fun onStopJob(parameters: JobParameters?): Boolean {
        // if the job is prematurely cancelled, do cleanup work here
        // return true to restart the job
        return true
    }

}