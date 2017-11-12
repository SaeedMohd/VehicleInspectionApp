package com.inspection.serverTasks

import android.bluetooth.BluetoothAdapter
import android.content.ContentValues
import android.content.Context
import android.location.LocationManager

import com.inspection.Utils.ApplicationPrefs
import com.inspection.Utils.Utility

/**
 * Created by devsherif on 6/21/16.
 */
abstract class SendGPSAndBluetoothStatesTask(private val context: Context) : AsyncTaskParent() {

    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg strings: String): String? {

        val values = ContentValues()

        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val isGpsEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        var isBluetoothEnabled = false

        if (BluetoothAdapter.getDefaultAdapter() != null && BluetoothAdapter.getDefaultAdapter().isEnabled) {
            isBluetoothEnabled = true
        }

        values.put("MobileUserProfileId", ApplicationPrefs.getInstance(context).userProfilePref.mobileUserProfileId)
        values.put("GPSStatus", if (isGpsEnabled) 1 else 0)
        values.put("BluetoothStatus", if (isBluetoothEnabled) 1 else 0)

        Utility.postRequest("http://jet-matics.com/JetComService/JetCom.svc/UpdateStatusOfGPSAndBluetooth?", values)

        return ""
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)
        onTaskCompleted(result)
    }

}
