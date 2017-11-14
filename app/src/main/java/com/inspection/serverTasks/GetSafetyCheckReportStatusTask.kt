package com.inspection.serverTasks

import android.content.ContentValues
import android.content.Context

import com.inspection.R
import com.inspection.Utils.Utility

abstract class GetSafetyCheckReportStatusTask(private val context: Context, private val safetyCheckReportsID: Int) : AsyncTaskParent() {
    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg params: String): String {
        var result: String? = null

        try {
            val values = ContentValues()
            values.put("safetyCheckReportsID", safetyCheckReportsID)
            result = Utility.postRequest(context.getString(R.string.getSafetyCheckReportStatus), values)
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
