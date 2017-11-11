package com.matics.serverTasks

import android.content.ContentValues

import com.matics.MainActivity
import com.matics.R
import com.matics.Utils.Utility

/**
 * Created by devsherif on 6/21/16.
 */
abstract class GetBeaconMessageTask(private val mobileUserProfileId: String, private val beaconId: String) : AsyncTaskParent() {

    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg strings: String): String? {

        val values = ContentValues()

        values.put("MobileUserProfileID", mobileUserProfileId)
        values.put("BeaconID", beaconId)

        Utility.postRequest(MainActivity.mContext.getString(R.string.get_beacon_service), values)

        return ""
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)
        onTaskCompleted(result)
    }

    companion object {

        var callRequestedType = "CallRequested"
    }

}
