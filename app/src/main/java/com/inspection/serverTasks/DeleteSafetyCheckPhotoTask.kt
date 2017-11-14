package com.inspection.serverTasks

import android.content.ContentValues
import android.content.Context

import com.inspection.R
import com.inspection.Utils.Utility

abstract class DeleteSafetyCheckPhotoTask(private val context: Context, private val safetyCheckReportsID: Int, private val safetyCheckItemsID: Int, private val safetyCheckItemPhotoName: String) : AsyncTaskParent() {
    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg params: String): String {
        var result: String? = null

        try {
            val values = ContentValues()
            values.put("safetyCheckReportsID", safetyCheckReportsID)
            values.put("safetyCheckItemsID", safetyCheckItemsID)
            values.put("safetyCheckItemPhotoName", safetyCheckItemPhotoName)
            result = Utility.postRequest(context.getString(R.string.deleteSafetyCheckItemPhoto), values)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result!!
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)
        onTaskCompleted(result)
    }

    /**
     * routine use to get-authentication for request credential
     */


    override fun onCancelled() {
        super.onCancelled()
        // if(mProgressDialog != null && mProgressDialog.isShowing())
        // mProgressDialog.dismiss();
    }
}
