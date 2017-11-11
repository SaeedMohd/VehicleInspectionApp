package com.matics.serverTasks

import org.json.JSONObject

import android.app.Activity
import android.app.AlertDialog
import android.app.Fragment
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.app.AlertDialog.Builder
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.os.AsyncTask
import android.util.Log

import com.google.gson.Gson
import com.matics.MainActivity
import com.matics.Utils.OnTaskCompleted
import com.matics.fragments.FragmentVehicleFacility
import com.matics.R
import com.matics.model.AccountDetailModel
import com.matics.model.UserAccountModel
import com.matics.model.UserProfileModel
import com.matics.model.VehicleProfileModel
import com.matics.Utils.ApplicationPrefs
import com.matics.Utils.Utility

class GetAccountDetailByPhoneAndMacTask(internal var context: Context, phoneId: String,
                                        bluetoothId: String, private val listener: OnTaskCompleted) : AsyncTask<String, Int, AccountDetailModel>() {
    internal var accountDetailModel: AccountDetailModel? = null
    internal var phoneId = ""
    internal var bluetoothId = ""

    internal var urlPath = "http://www.jet-matics.com/JetComService/JetCom.svc/GetAccountDetailByPhoneAndMac?"

    init {
        //Log.dMainActivity.TAG, "Started")
        this.phoneId = phoneId
        this.bluetoothId = bluetoothId
    }

    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg params: String): AccountDetailModel {
        val accountDetailModel = GetAccountDetailByPhoneAndMac(
                phoneId, bluetoothId)
        return accountDetailModel
    }

    override fun onPostExecute(result: AccountDetailModel) {
        super.onPostExecute(result)
        //checkAccountDetailsRetrievedFromCloud(result);
    }

    override fun onCancelled() {
        super.onCancelled()
    }

    private fun GetAccountDetailByPhoneAndMac(phoneId: String,
                                              bluetoothId: String): AccountDetailModel {
        //Log.dMainActivity.TAG, "Calling Web Service")
        var result = ""
        try {
            //phoneId = "6709b4c985a1e4a8";
            val values = ContentValues()
            values.put("Android_Phone_ID", phoneId)
            values.put("BtID", bluetoothId)

            result = Utility.postRequest(urlPath, values)

            //Log.dMainActivity.TAG, "Result response= " + result)

            val jObject = JSONObject(result.toString())
            val profileResult = jObject
                    .getJSONObject("GetAccountDetailByPhoneAndMacResult")
            val userAccountObject = profileResult
                    .getJSONObject("AccountUser")
            val userProfileObject = profileResult
                    .getJSONObject("UserProfile")
            val vehicleProfileObject = profileResult
                    .getJSONObject("VehicleProfile")

            //Log.dMainActivity.TAG, "userprofile object = " + userProfileObject.toString())

            val userProfileModel = Gson().fromJson(
                    userProfileObject.toString(), UserProfileModel::class.java)
            val userAccountModel = Gson().fromJson(
                    userAccountObject.toString(), UserAccountModel::class.java)
            val vehicleProfileModel = Gson().fromJson(
                    vehicleProfileObject.toString(), VehicleProfileModel::class.java)

            accountDetailModel = AccountDetailModel(userAccountModel,
                    userProfileModel, vehicleProfileModel)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return accountDetailModel!!
    }

    private fun checkAccountDetailsRetrievedFromCloud(
            accountDetailModel: AccountDetailModel?) {
        if (accountDetailModel != null) {
            val messageStringBuilder = StringBuilder()
            if (accountDetailModel.userProfileModel != null) {
            }
            if (accountDetailModel.userProfileModel.firstName != null && accountDetailModel.userProfileModel.firstName
                    .trim { it <= ' ' } != "") {
                messageStringBuilder.append("Name: "
                        + accountDetailModel.userProfileModel
                        .firstName.trim { it <= ' ' }
                        + " "
                        + accountDetailModel.userProfileModel
                        .lastName.trim { it <= ' ' } + "\n")
            }

            if (accountDetailModel.userAccountModel.address != null && accountDetailModel.userAccountModel.address
                    .trim { it <= ' ' } != "") {
                messageStringBuilder.append("Address: "
                        + accountDetailModel.userAccountModel.address
                        .trim { it <= ' ' } + "\n")
            }

            if (accountDetailModel.userAccountModel.city != null && accountDetailModel.userAccountModel.city
                    .trim { it <= ' ' } != "") {
                messageStringBuilder.append("City: "
                        + accountDetailModel.userAccountModel.city
                        .trim { it <= ' ' } + "\n")
            }

            if (accountDetailModel.userAccountModel.phone != null && accountDetailModel.userAccountModel.phone
                    .trim { it <= ' ' } != "") {
                messageStringBuilder.append("Phone: "
                        + accountDetailModel.userAccountModel.phone
                        .trim { it <= ' ' } + "\n")
            }

            if (accountDetailModel.userProfileModel.email != null && accountDetailModel.userProfileModel.email
                    .trim { it <= ' ' } != "") {
                messageStringBuilder.append("Email: "
                        + accountDetailModel.userProfileModel.email
                        .trim { it <= ' ' } + "\n")
            }

            showMessageDialog(context, "Is that you?", messageStringBuilder.toString(), accountDetailModel)

        }
    }

    fun showMessageDialog(contex: Context, title: String, message: String?,
                          accountDetailModel: AccountDetailModel) {
        //Log.dMainActivity.TAG,
//                "Showing dialog now")
        if (message != null && message.trim { it <= ' ' }.length > 0) {
            val builder = AlertDialog.Builder(contex)
            builder.setTitle(title)
            builder.setMessage(message)
            builder.setPositiveButton("Yes"
            ) { dialog, id ->
                ApplicationPrefs.getInstance(context).accountDetailPref = accountDetailModel

                val fragment = FragmentVehicleFacility()
                val fragmentManager = (context as Activity)
                        .fragmentManager
                val ft = fragmentManager
                        .beginTransaction()
                ft.replace(R.id.tab_frames, fragment)
                ft.commit()
            }
            builder.setNegativeButton("No"
            ) { dialog, id -> dialog.dismiss() }
            builder.show()

        }
    }

}
