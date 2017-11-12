package com.inspection.serverTasks

import android.content.ContentValues
import android.content.Context

import com.inspection.inspection.R
import com.inspection.Utils.Utility

abstract class GetOpenSafetyCheckReportsTask(private val context: Context, private val mobileUserProfileID: Int, private val vehicleID: Int) : AsyncTaskParent() {
    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg params: String): String {
        var result: String? = null

        try {
            val values = ContentValues()
            values.put("mobileUserProfileID", mobileUserProfileID)
            values.put("vehicleID", vehicleID)
            result = Utility.postRequest(context.getString(R.string.getOpenSafetyCheckReports), values)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result!!
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)
        onTaskCompleted(result)
    }

}
