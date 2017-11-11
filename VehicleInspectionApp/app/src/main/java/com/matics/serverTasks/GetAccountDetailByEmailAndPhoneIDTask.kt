package com.matics.serverTasks

import android.app.Activity
import android.app.AlertDialog
import android.app.AlertDialog.Builder
import android.app.Fragment
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast

import com.google.gson.Gson
import com.matics.MainActivity
import com.matics.R
import com.matics.Utils.ApplicationPrefs
import com.matics.Utils.OnTaskCompleted
import com.matics.Utils.Utility
import com.matics.adapter.DataBaseHelper
import com.matics.fragments.FragmentVehicleFacility
import com.matics.model.AccountDetailModel
import com.matics.model.UserAccountModel
import com.matics.model.UserProfileModel
import com.matics.model.VehicleProfileModel

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList

abstract class GetAccountDetailByEmailAndPhoneIDTask(internal var context: Context, internal var checkPhoneID: Boolean, email: String,
                                                     phoneId: String) : AsyncTaskParent() {
    internal var accountDetailModel: AccountDetailModel? = null
    lateinit var userProfileModel: UserProfileModel
        internal set
    var vehicleProfileModel: VehicleProfileModel? = null
        internal set
    lateinit var userAccountModel: UserAccountModel
        internal set
    internal var email = ""
    internal var phoneId = ""

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
        val result = GetAccountDetailByEmailAndPhoneId(email, phoneId)
        return result
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)


        checkAccountDetailsRetrievedFromCloud(result)
        onTaskCompleted(result)
    }

    override fun onCancelled() {
        super.onCancelled()
    }

    private fun GetAccountDetailByEmailAndPhoneId(email: String,
                                                  phoneId: String): String {
        //Log.dMainActivity.TAG, "Calling Web Service")
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
            //Log.dMainActivity.TAG, "is success, lets set the user profile data")
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
                    userProfileModel = Gson().fromJson(
                            userProfileObject.toString(),
                            UserProfileModel::class.java)


                    userAccountModel = Gson().fromJson(
                            userAccountObject.toString(),
                            UserAccountModel::class.java)


                    accountDetailModel = AccountDetailModel(
                            userAccountModel, userProfileModel, null)

                    val vehicleProfileListJsonArray = profileResult.getJSONArray("VehicleProfileList")
                    //Log.dMainActivity.TAG, "Its count = " + vehicleProfileListJsonArray.length())
                    val vehicleProfileModelArrayList = ArrayList<VehicleProfileModel>()
                    vehicleProfileModelArrayList.clear()
                    if (vehicleProfileListJsonArray.length() > 0) {

                        for (x in 0..vehicleProfileListJsonArray.length() - 1) {
                            vehicleProfileModel = Gson().fromJson(vehicleProfileListJsonArray.get(x).toString(), VehicleProfileModel::class.java)
                            if (vehicleProfileModel!!.vin == "" && vehicleProfileModel!!.model == "" && vehicleProfileModel!!.make == "" && vehicleProfileModel!!.year == "0") {
                                vehicleProfileModel = null
                                continue
                            }
                            vehicleProfileModelArrayList.add(vehicleProfileModel!!)
                        }
                        //Log.dMainActivity.TAG, "Its count = " + vehicleProfileModelArrayList.size)
                        val dbHelper = DataBaseHelper(context)
                        dbHelper.clearProfileTable()
                        var vehicleProfileModel1: VehicleProfileModel
                        for (x in vehicleProfileModelArrayList.indices) {
                            vehicleProfileModel1 = vehicleProfileModelArrayList[x]
                            dbHelper.addProfile(vehicleProfileModel1)
                        }

                    }


                }

                if (accountDetailModel != null) {
                    if (accountDetailModel!!.userProfileModel != null) {

                        ApplicationPrefs.getInstance(context).userProfilePref = accountDetailModel!!.userProfileModel
                        if (accountDetailModel!!.userAccountModel != null) {
                            ApplicationPrefs.getInstance(context).setUserAccountPrefs(accountDetailModel!!.userAccountModel)
                        }
                    }
                }
            } catch (jsonExp: JSONException) {
                //Log.dMainActivity.TAG, jsonExp.message)
            }

            return
        }
    }
}

