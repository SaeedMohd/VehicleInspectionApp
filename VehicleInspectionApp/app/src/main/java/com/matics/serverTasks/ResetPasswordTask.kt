package com.matics.serverTasks

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.os.AsyncTask
import android.util.Log

import com.matics.MainActivity
import com.matics.Utils.ApplicationPrefs
import com.matics.Utils.Utility

/**
 * Created by devsherif on 6/21/16.
 */
abstract class ResetPasswordTask(internal var context: Activity, internal var emailAddress: String, internal var successfulResponseMessage: String) : AsyncTaskParent() {
    internal var resetPasswordUrl = "http://www.jet-matics.com/JetComService/JetCom.svc/ForgotPassword?"

    override fun doInBackground(vararg strings: String): String {
        var result = ""
        try {
            val values = ContentValues()
            values.put("EmailAddress", emailAddress)


            //Log.dMainActivity.TAG, "calling url now")
            result = Utility.postRequest(resetPasswordUrl, values)

            //Log.dMainActivity.TAG, "Result response= " + result)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        if (result != null) {
            if (result.contains("Successfully")) {
                ApplicationPrefs.getInstance(context).setBooleanPref(ApplicationPrefs.IS_PASSWORD_RESET, true)
                ApplicationPrefs.getInstance(context).emailUserBeenReset = emailAddress
                val confirmationDialog = AlertDialog.Builder(context)
                confirmationDialog.setMessage(successfulResponseMessage)
                confirmationDialog.setPositiveButton("OK", null)
                confirmationDialog.show()
            } else {
                val confirmationDialog = AlertDialog.Builder(context)
                confirmationDialog.setMessage("Username and email doesn't match")
                confirmationDialog.setPositiveButton("OK", null)
                confirmationDialog.show()
            }
        } else {
            val confirmationDialog = AlertDialog.Builder(context)
            confirmationDialog.setMessage("Unable to connect. Please make sure that internet connection is active")
            confirmationDialog.setPositiveButton("OK", null)
            confirmationDialog.show()
        }
    }
}
