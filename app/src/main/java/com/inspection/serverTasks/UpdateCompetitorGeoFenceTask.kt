package com.inspection.serverTasks

import android.content.ContentValues
import android.content.Context
import com.inspection.R
import com.inspection.Utils.Utility

abstract class UpdateCompetitorGeoFenceTask(private val context: Context, private val geoFenceId: Int, private val totalTimeInGeoFence: Int, private val mobileUserProfileId: Int, private val geoFenceIncidentId: Int) : AsyncTaskParent() {

    override fun onPreExecute() {
        super.onPreExecute()

    }

    override fun doInBackground(vararg params: String): String {

        val values = ContentValues()
        values.put("GeoFenceID", geoFenceId)
        values.put("TotalTimeInGeoFence", totalTimeInGeoFence)
        values.put("GeoIncidentID", geoFenceIncidentId)
        values.put("MobileUserProfileId", mobileUserProfileId)

        return Utility.postRequest(context.getString(R.string.updateCompetitorGeoFence), values)
    }

    override fun onPostExecute(s: String) {
        super.onPostExecute(s)
        onTaskCompleted(s)
    }
}
