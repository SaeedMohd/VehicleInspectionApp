package com.inspection.interfaces

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.inspection.FormsActivity
import com.inspection.MainActivity.Companion.activity
import com.inspection.Utils.ApplicationPrefs
import com.inspection.Utils.Constants
import com.inspection.Utils.Utility

class LocationJobScheduler : JobService() {
    override fun onStartJob(parameters: JobParameters?): Boolean {
        val service = Intent(applicationContext, LocationLogService::class.java)
        service.setPackage("com.inspection.interfaces.LocationLogService")
        Log.v("ALARM ------------> ", "LocationLogService Called")
        applicationContext.startService(service)
        Utility.scheduleJob(applicationContext)
        return true
    }


    override fun onStopJob(parameters: JobParameters?): Boolean {
        return true
    }

}