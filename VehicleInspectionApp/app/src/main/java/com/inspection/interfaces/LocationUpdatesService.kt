package com.inspection.interfaces

import android.app.AlarmManager
import android.app.IntentService
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.inspection.MainActivity
import com.inspection.Utils.ApplicationPrefs
import com.inspection.Utils.Constants
import org.jetbrains.anko.runOnUiThread
import java.util.*

class LocationUpdatesService : IntentService("LocationUpdatesService") {
    var urlString = ""
    override fun onCreate() {
        super.onCreate()
    }
    override fun onHandleIntent(intent: Intent?) {
        Log.v("ALARM ------------> ", "LocationUpdatesService")
        if (urlString.isNullOrEmpty()) {
            urlString += ApplicationPrefs.getInstance(MainActivity.activity).sessionID
            urlString += "&userId=" + ApplicationPrefs.getInstance(MainActivity.activity).loggedInUserID
            urlString += "&deviceId=" + ApplicationPrefs.getInstance(MainActivity.activity).deviceID
        }
        Volley.newRequestQueue(applicationContext).add(StringRequest(Request.Method.GET, Constants.logTracking + urlString,
                Response.Listener { response ->
                }, Response.ErrorListener {
        }))
        scheduleAlarm()
    }

    fun scheduleAlarm() {
        val intent = Intent(getApplicationContext(), LocationAlarmManager::class.java);
        val pIntent = PendingIntent.getBroadcast(this, LocationAlarmManager.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        var alarm = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager;
        alarm?.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis()+5000,
                pIntent)
    }
}
