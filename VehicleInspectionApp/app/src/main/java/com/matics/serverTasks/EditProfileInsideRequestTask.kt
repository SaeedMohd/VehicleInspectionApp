package com.matics.serverTasks

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.os.AsyncTask

import com.google.gson.Gson
import com.matics.model.UserProfileModel
import com.matics.Utils.ApplicationPrefs
import com.matics.Utils.Utility

import org.json.JSONObject

class EditProfileInsideRequestTask
// private final LoginData loginData = new LoginData();

(private val mContext: Context, private val webUrl: String) : AsyncTask<String, Int, Any>() {
    private val mProgressDialog: ProgressDialog?
    private var ErrorDetail: String? = null
    private var status: String? = null

    init {
        mProgressDialog = ProgressDialog(mContext)
        mProgressDialog.setMessage("Please Wait...")
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.setCancelable(false)
    }

    override fun onPreExecute() {
        super.onPreExecute()
        if (mProgressDialog != null && !mProgressDialog.isShowing) {
            mProgressDialog.show()
        }
    }

    override fun doInBackground(vararg params: String): Any? {
        return EditProfile(params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7], params[8], params[9], params[10], params[11],
                params[12], params[13], params[14], params[15], params[16], params[17], params[18])!!
    }

    override fun onPostExecute(result: Any?) {
        super.onPostExecute(result)

        if (mProgressDialog != null && mProgressDialog.isShowing) {
            mProgressDialog.dismiss()
        }
        if (result.toString().equals("")) {
            ErrorDetail="Profile Updated Succesfully"

        }
        else ErrorDetail="Error while updating profile"
        Utility.showMessageDialog(mContext, "", ErrorDetail)
        //		if (status.equalsIgnoreCase("true")) {
        ////			LoginActivity.storeStringUserDetails(LoginActivity.First_Name, value);
        ////			Intent intent = new Intent(mContext, MainActivity.class);
        ////            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);a
        ////            mContext.startActivity(intent);
        ////            ((Activity)mContext).finish();
        ////            LoginActivity.storeBooleanUserDetails("profile", true);.
        //
        ////			Toast.makeText(mContext, "Project Updated successfuly", duration)
        //
        //		}
        //		else{
        //
        //		}

    }

    /**
     * routine use to get-authentication for request credential
     */

    private fun EditProfile(firstName: String, lastName: String, phoneNumber: String, email: String, mobileUserProfileId: String, accountId: String, userName: String, password: String, email2: String, email3: String, phoneNumber2: String, phoneNumber3: String, address: String, city: String, street: String, zip: String, facebook: String, customerId: String, phoneId: String): Any? {

        var result: String? = null
        val getServerPath = "http://www.jet-matics.com/JetComService/JetCom.svc/UpdateProfile?"
        try {
            val values = ContentValues()
            // Saeed Mostafa - 03092017 - Resolving crash while editing user profile due to NullPointerException [Start]
            values.put("FirstName", if (firstName==null) "" else firstName)
            values.put("LastName", if (lastName==null) "" else lastName)
            values.put("PhoneNumber", if (phoneNumber==null) "" else phoneNumber)
            values.put("Email", if (email==null) "" else email)
            values.put("MobileUserProfileId", ApplicationPrefs.getInstance(mContext).userProfilePref.mobileUserProfileId)
            values.put("UserName", if (userName==null) "" else userName)
            values.put("Password", if (password==null) "" else password)
            values.put("Email2", if (email2==null) "" else email2)
            values.put("Email3", if (email3==null) "" else phoneNumber)
            values.put("Phone2", if (phoneNumber2==null) "" else phoneNumber2)
            values.put("Phone3", if (phoneNumber3==null) "" else phoneNumber3)
            values.put("Address", if (address==null) "" else address)
            values.put("City", if (city==null) "" else city )
            values.put("ST", if (street==null) "" else street)
            values.put("Zip", if (zip==null) "" else zip)
            values.put("Facebook", if (facebook==null) "" else facebook)
            values.put("CustomerID", if (customerId==null) "" else customerId)
            values.put("Phone_ID", if (phoneId==null) "" else phoneId)
            values.put("AccountID", if (accountId==null) "" else accountId)
//            values.put("AccountID", accountId)
            // Saeed Mostafa - 03092017 - Resolving crash while editing user profile due to NullPointerException [End]
            result = Utility.postRequest(getServerPath, values)

            val jObject = JSONObject(result!!.toString())
            val Loginresult = jObject.getJSONObject("UpdateProfileResult")
            val ObjError = Loginresult.getJSONObject("Error")
            status = ObjError.getString("IsSuccess")
            val UserProfileOBJ = Loginresult.getJSONObject("UserProfile")

            if (status!!.equals("true", ignoreCase = true)) {
                var userProfileModel: UserProfileModel? = Gson().fromJson(UserProfileOBJ.toString(), UserProfileModel::class.java)
                userProfileModel!!.firstName = userProfileModel.firstName
                userProfileModel.lastName = userProfileModel.lastName
                userProfileModel.userName = userProfileModel.userName
                userProfileModel.phoneNumber = userProfileModel.phoneNumber
                userProfileModel.phoneNumber2 = userProfileModel.phoneNumber2
                userProfileModel.phoneNumber3 = userProfileModel.phoneNumber3
                userProfileModel.email = userProfileModel.email
                userProfileModel.email2 = userProfileModel.email2
                userProfileModel.email3 = userProfileModel.email3
                userProfileModel.address = userProfileModel.address
                userProfileModel.city = userProfileModel.city
                userProfileModel.state = userProfileModel.state
                userProfileModel.zip = userProfileModel.zip
                userProfileModel.facebook = userProfileModel.facebook
                userProfileModel.deviceID = userProfileModel.deviceID
                userProfileModel.phone_ID = userProfileModel.phone_ID
                userProfileModel.accountID = userProfileModel.accountID
                ApplicationPrefs.getInstance(mContext).userProfilePref = userProfileModel
                userProfileModel = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return "Error"
        }
        return ""
    }

    override fun onCancelled() {
        super.onCancelled()
        // if(mProgressDialog != null && mProgressDialog.isShowing())
        // mProgressDialog.dismiss();
    }
}
