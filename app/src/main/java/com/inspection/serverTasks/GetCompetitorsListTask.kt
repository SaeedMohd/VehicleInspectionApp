package com.inspection.serverTasks

import android.content.ContentValues
import android.content.Context

import com.inspection.R
import com.inspection.Utils.ApplicationPrefs
import com.inspection.Utils.Utility

import java.util.Date

/**
 * Created by devsherif on 6/21/16.
 */
abstract class GetCompetitorsListTask(private val context: Context) : AsyncTaskParent() {

    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg strings: String): String {

        val values = ContentValues()

        values.put("MobileUserProfileId", ApplicationPrefs.getInstance(context).userProfilePref.mobileUserProfileId)

        return Utility.postRequest(context.getString(R.string.getCompetitorsDetailsURL), values)

    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        if (result != null && result.contains("CompetitorGeoFence")) {
            ApplicationPrefs.getInstance(context).setLastCompetitorUpdate(Date().time)
            ApplicationPrefs.getInstance(context).competitorModelString = result
            onTaskCompleted("")
        }
    }

}
