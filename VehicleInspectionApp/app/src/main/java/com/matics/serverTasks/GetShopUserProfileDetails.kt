package com.matics.serverTasks

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast

import com.google.gson.Gson
import com.matics.MainActivity
import com.matics.Utils.ApplicationPrefs
import com.matics.Utils.Utility
import com.matics.model.AccountDetailModel
import com.matics.model.UserProfileModel

import org.json.JSONException
import org.json.JSONObject

/**
 * Created by devsherif on 6/21/16.
 */
abstract class GetShopUserProfileDetails(internal var context: Context, internal var checkPhoneID: Boolean, email: String,
                                         phoneId: String) : AsyncTaskParent() {
    internal var accountDetailModel: AccountDetailModel? = null
    internal var email = ""
    internal var phoneId = ""

    // String urlPath =
    // "http://www.jet-matics.com/JetComService/JetCom.svc/GetAccountDetailByPhoneAndMac?";
    internal var urlPath = "http://www.jet-matics.com/JetComService/JetCom.svc/GetAccountDetailByEmailAndPhoneID?"

    init {
        //Log.dMainActivity.TAG, "Started")
        this.email = email
        this.phoneId = phoneId

    }

    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg strings: String): String {
        val result = GetShopUserProfileDetails(email, phoneId)
        return result
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)

        onTaskCompleted(result)
    }

    override fun onCancelled() {
        super.onCancelled()
    }

    private fun GetShopUserProfileDetails(email: String,
                                          phoneId: String): String {
        //Log.dMainActivity.TAG, "Calling Web Service for shop user profile")
        var result = ""
        try {
            // phoneId = "6709b4c985a1e4a8";
            val values = ContentValues()
            values.put("email", email)
            values.put("Android_Phone_ID", phoneId)

            result = Utility.postRequest(urlPath, values)


            //Log.dMainActivity.TAG, "Result response= " + result)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

    fun checkAccountDetailsRetrievedFromCloud(result: String?) {
        //Log.dMainActivity.TAG, "result : " + result!!)
        if (result == null) {
            Toast.makeText(context, "Connection error. Please try again later", Toast.LENGTH_LONG).show()
            val failedLoginDialog = AlertDialog.Builder(context)
            failedLoginDialog.setMessage("Couldn't login, Please try again")
            failedLoginDialog.setPositiveButton("OK", null)
            failedLoginDialog.show()

            return
        }
        if (result.contains("Timeout") || result.contains("timeout") || result.contains("<HTML></HTML>")) {
            Toast.makeText(context, "Connection error. Please try again later", Toast.LENGTH_LONG).show()
            val failedLoginDialog = AlertDialog.Builder(context)
            failedLoginDialog.setMessage("Couldn't login, Please try again")
            failedLoginDialog.setPositiveButton("OK", null)
            failedLoginDialog.show()

            return
        } else if (result.contains("IsSuccess\":true")) {
            //Log.dMainActivity.TAG, "is success, lets set the shop user profile data")
            try {
                val jObject = JSONObject(result.toString())
                val profileResult = jObject
                        .getJSONObject("GetAccountDetailByEmailAndPhoneIDResult")
                val userAccountObject = profileResult
                        .getJSONObject("AccountUser")
                if (profileResult.getJSONObject("UserProfile") != null) {
                    //Log.dMainActivity.TAG, "let's save user profile")
                    val userProfileObject = profileResult
                            .getJSONObject("UserProfile")
                    val userProfileModel = Gson().fromJson(
                            userProfileObject.toString(),
                            UserProfileModel::class.java)

                    ApplicationPrefs.getInstance(context).shopUserProfile = userProfileModel
                }

            } catch (jsonExp: JSONException) {
                //Log.dMainActivity.TAG, jsonExp.message)

                return
            }

        } else {

        }
    }
}
