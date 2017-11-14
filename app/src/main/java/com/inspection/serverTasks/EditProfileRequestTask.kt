package com.inspection.serverTasks


import org.json.JSONObject

import com.inspection.model.UserProfileModel
import com.inspection.Utils.ApplicationPrefs
import com.inspection.Utils.Utility

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.os.AsyncTask

class EditProfileRequestTask
// private final LoginData loginData = new LoginData();

(private val mContext: Context, private val webUrl: String) : AsyncTask<String, Int, Any>() {
    private val mProgressDialog: ProgressDialog?
    private val ErrorDetail: String? = null
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

    override fun doInBackground(vararg params: String): Any {
        return EditProfile(params[0], params[1], params[2], params[3])!!
    }

    override fun onPostExecute(result: Any) {
        super.onPostExecute(result)

        if (mProgressDialog != null && mProgressDialog.isShowing) {
            mProgressDialog.dismiss()
        }
        //		Utility.showMessageDialog(mContext, "", ErrorDetail);
        if (status!!.equals("true", ignoreCase = true)) {
            ApplicationPrefs.getInstance(mContext).setLoggedInPrefs(true)
//            val fragmentUser = FragmentVehicleFacility()
//            val fragmentManagerUser = (mContext as Activity).fragmentManager
//            val ftUser = fragmentManagerUser.beginTransaction()
//            ftUser.replace(R.id.tab_frames, fragmentUser)
//            ftUser.commit()

        }

    }

    /**
     * routine use to get-authentication for request credential
     */

    private fun EditProfile(Fname: String, Lname: String, Email: String, Phone: String): Any? {
        var result: String? = null
        val getServerPath = "http://www.jet-matics.com/JetComService/JetCom.svc/CreateProfile?"
        try {
            val values = ContentValues()
            values.put("FirstName", Fname.toString())
            values.put("LastName", Lname.toString())
            values.put("PhoneNumber", Phone.toString())
            values.put("Email", Email.toString())
            //nameValuePairs.add(new BasicNameValuePair("DeviceID", String.valueOf(MainActivity.mediaPrefs.getString(MainActivity.GCM_DeviceID, null))));
            //nameValuePairs.add(new BasicNameValuePair("AccountId", String.valueOf(MainActivity.mediaPrefs.getString(MainActivity.AccountID, null))));

            result = Utility.postRequest(getServerPath, values)
            val jObject = JSONObject(result!!.toString())
            val Loginresult = jObject.getJSONObject("CreateProfileResult")
            val ObjError = Loginresult.getJSONObject("Error")
            status = ObjError.getString("IsSuccess")
            val UserProfileOBJ = Loginresult.getJSONObject("UserProfile")
            if (status!!.equals("true", ignoreCase = true)) {
                var userProfileModel: UserProfileModel? = ApplicationPrefs.getInstance(mContext).userProfilePref
                userProfileModel!!.firstName = Fname
                userProfileModel.lastName = Lname
                userProfileModel.email = Email
                userProfileModel.phoneNumber = Phone
                userProfileModel.mobileUserProfileId = Integer.parseInt(UserProfileOBJ.getString("MobileUserProfileId"))
                ApplicationPrefs.getInstance(mContext).userProfilePref = userProfileModel
                userProfileModel = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    override fun onCancelled() {
        super.onCancelled()
        // if(mProgressDialog != null && mProgressDialog.isShowing())
        // mProgressDialog.dismiss();
    }
}
