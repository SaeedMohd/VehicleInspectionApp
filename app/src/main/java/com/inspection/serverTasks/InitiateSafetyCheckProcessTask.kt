package com.inspection.serverTasks

import android.content.ContentValues
import android.content.Context

import com.inspection.inspection.R
import com.inspection.Utils.Utility

abstract class InitiateSafetyCheckProcessTask(private val context: Context, private val mobileUserProfileID: Int, private val vehicleID: Int, private val questionSetID: Int) : AsyncTaskParent() {
    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg params: String): String {
        return GetSafetyCheckList()
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)
        if (result == "-1") {
            onTaskCompleted("Failed")
        } else {
            onTaskCompleted(result)
        }

    }

    /**
     * routine use to get-authentication for request credential
     */

    private fun GetSafetyCheckList(): String {

        var result: String? = null

        try {
            val values = ContentValues()
            values.put("mobileUserProfileID", mobileUserProfileID)
            values.put("vehicleID", vehicleID)
            values.put("questionSetID", questionSetID)
            result = Utility.postRequest(context.getString(R.string.initiate_safety_check_process), values)
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
