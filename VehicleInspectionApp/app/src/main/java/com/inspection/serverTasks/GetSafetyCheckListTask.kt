package com.inspection.serverTasks

import android.content.ContentValues
import android.content.Context

import com.inspection.inspection.R
import com.inspection.Utils.Utility

abstract class GetSafetyCheckListTask(private val context: Context, private val safetyCheckReportID: Int) : AsyncTaskParent() {
    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg params: String): String {
        return GetSafetyCheckList(safetyCheckReportID)
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)
        onTaskCompleted(result)
    }

    /**
     * routine use to get-authentication for request credential
     */

    private fun GetSafetyCheckList(safetyCheckReportID: Int): String {

        var result: String? = null

        try {
            val values = ContentValues()
            values.put("safetyCheckReportId", safetyCheckReportID)
            result = Utility.postRequest(context.getString(R.string.safety_check_list), values)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result!!
    }

    override fun onCancelled() {
        super.onCancelled()
        // if(mProgressDialog != null && mProgressDialog.isShowing())
        // mProgressDialog.dismiss();
    }
}
