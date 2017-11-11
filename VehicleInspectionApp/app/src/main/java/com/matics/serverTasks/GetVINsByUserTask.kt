package com.matics.serverTasks

import com.matics.Utils.Utility
import android.content.ContentValues
import android.content.Context

abstract class GetVINsByUserTask(private val context: Context, private val userName: String) : AsyncTaskParent() {
    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg params: String): String {
        return GetVINsByUser(userName)
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)
        onTaskCompleted(result)
    }

    /**
     * routine use to get-authentication for request credential
     */

    private fun GetVINsByUser(userName: String): String {

        var result: String? = null
        val getServerPath = "http://jet-matics.com/JetComService/JetCom.svc/GetVinsByUser?"
        try {
            val values = ContentValues()
            values.put("UserName", userName)
            result = Utility.postRequest(getServerPath, values)
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
