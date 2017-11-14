package com.inspection.serverTasks

import android.content.ContentValues
import android.content.Context

import com.inspection.R
import com.inspection.Utils.Utility

abstract class GetSafetyCheckResultsScaleOptionsTask(private val context: Context, private val safetyCheckResultsScaleID: Int) : AsyncTaskParent() {
    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg params: String): String {
        return GetSafetyCheckResultsScaleOptionsTask(safetyCheckResultsScaleID)
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)
        onTaskCompleted(result)
    }

    /**
     * routine use to get-authentication for request credential
     */

    private fun GetSafetyCheckResultsScaleOptionsTask(safetyCheckResultsScaleID: Int): String {

        var result: String? = null

        try {
            val values = ContentValues()
            values.put("safetyCheckResultsScaleID", safetyCheckResultsScaleID)
            result = Utility.postRequest(context.getString(R.string.get_safety_check_results_scale_options), values)
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
