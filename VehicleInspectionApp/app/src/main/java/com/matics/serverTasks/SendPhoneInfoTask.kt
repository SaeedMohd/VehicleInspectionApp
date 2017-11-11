package com.matics.serverTasks

import android.content.ContentValues
import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.util.Log

import com.matics.LivePhoneReadings
import com.matics.MainActivity
import com.matics.Utils.Utility

class SendPhoneInfoTask : AsyncTask<String, Int, Any> {

    //	private final Context mContext;
    //	private final String webUrl;

    private val ErrorDetail: String? = null
    private val status: String? = null

    // private final LoginData loginData = new LoginData();
    constructor() {

    }

    constructor(mContext: Context, url: String) {
        //		this.mContext = mContext;
        //		this.webUrl = url;
    }

    override fun onPreExecute() {
        super.onPreExecute()

    }

    override fun doInBackground(vararg params: String): Any {

        sendPhoneInfo(LivePhoneReadings.mobileUserProfileId, Build.MANUFACTURER, Build.MODEL, Build.DEVICE, Build.VERSION.CODENAME, Build.VERSION.SDK_INT)

        return ""
    }

    override fun onPostExecute(result: Any) {

    }

    /**
     * routine use to get-authentication for request credential
     */

    private fun sendPhoneInfo(userId: String, manufacturer: String, model: String, device: String, codeName: String, sdkInt: Int): Any? {
        var result: String? = null
        val getServerPath = "http://www.jet-matics.com/JetComService/JetCom.svc/AddMobileInformation?"
        try {
            val values = ContentValues()
            values.put("MobileUserProfileId", "" + userId)
            values.put("manufacturer", manufacturer)
            values.put("model", model)
            values.put("device", device)
            values.put("codename", codeName)
            values.put("sdk", "" + sdkInt)

            result = Utility.postRequest(getServerPath, values)
            //Log.dMainActivity.TAG, "response is: " + result!!)
            //			JSONObject jObject = new JSONObject(result.toString());
            //			JSONObject Loginresult = jObject.getJSONObject("CreateProfileResult");
            //			JSONObject ObjError = Loginresult.getJSONObject("Error");
            //			status = ObjError.getString("IsSuccess");
            //			JSONObject UserProfileOBJ = Loginresult.getJSONObject("UserProfile");
            //			if (status.equalsIgnoreCase("true")) {
            //				UserProfileModel userProfileModel = ApplicationPrefs.getInstance(mContext).getUserProfilePref();
            //				userProfileModel.setFirstName(Fname);
            //				userProfileModel.setLastName(Lname);
            //				userProfileModel.setEmail(Email);
            //				userProfileModel.setPhoneNumber(Phone);
            //				userProfileModel.setMobileUserProfileId(Integer.parseInt(UserProfileOBJ.getString("MobileUserProfileId")));
            //				ApplicationPrefs.getInstance(mContext).setUserProfilePref(userProfileModel);
            //				userProfileModel=null;
            //			}
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
