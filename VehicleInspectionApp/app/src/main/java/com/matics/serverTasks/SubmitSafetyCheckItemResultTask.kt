package com.matics.serverTasks

import android.content.ContentValues
import android.content.Context

import com.matics.R
import com.matics.Utils.Utility

abstract class SubmitSafetyCheckItemResultTask(private val context: Context, private val safetyCheckReportsID: Int, private val safetyCheckItemsID: Int, private val safetyCheckResultID: Int, private val safetyCheckItemComment: String) : AsyncTaskParent() {
    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg params: String): String {
        var result: String? = null

        try {
            val values = ContentValues()
            values.put("safetyCheckReportsID", safetyCheckReportsID)
            values.put("safetyCheckItemsID", safetyCheckItemsID)
            values.put("safetyCheckResultID", safetyCheckResultID)
            values.put("safetyCheckItemComment", safetyCheckItemComment)

            result = Utility.postRequest(context.getString(R.string.submitSafetyCheckItemResult), values)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result!!
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)
        onTaskCompleted(result)
    }


    override fun onCancelled() {
        super.onCancelled()
        // if(mProgressDialog != null && mProgressDialog.isShowing())
        // mProgressDialog.dismiss();
    }
}
