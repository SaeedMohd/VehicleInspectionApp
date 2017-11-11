package com.matics.serverTasks

import java.util.ArrayList


import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.os.AsyncTask

import com.matics.Utils.Utility


class DeleteVehicleTask(mContext: Activity) : AsyncTask<String, Int, String>() {

    private val mContext: Context
    private val mProgressDialog: ProgressDialog?
    var isSuccess = "false"

    private val serverUrl = "http://jet-matics.com/JetComService/JetCom.svc/RemoveUserVehicleRelation?"

    init {
        this.mContext = mContext
        mProgressDialog = ProgressDialog(mContext)
        mProgressDialog.setMessage("Please wait...")
        mProgressDialog.setCanceledOnTouchOutside(false)
    }

    override fun onPreExecute() {
        super.onPreExecute()
        if (mProgressDialog != null && !mProgressDialog.isShowing) {
            mProgressDialog.show()
        }
    }

    override fun doInBackground(vararg params: String): String {

        return deleteVehicleRequest(params[0], params[1])
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)
        if (mProgressDialog != null && mProgressDialog.isShowing) {
            mProgressDialog.dismiss()
        }
        //return userAccountArrayList;
    }

    override fun onCancelled() {
        super.onCancelled()
        if (mProgressDialog != null && mProgressDialog.isShowing)
            mProgressDialog.dismiss()
    }

    private fun deleteVehicleRequest(vin: String, mobileUserProfileID: String): String {
        //		Utility.VehiclePofileData.clear();
        var result: String? = null

        try {
            val values = ContentValues()
            values.put("VIN", vin)
            values.put("MobileUserProfileId", mobileUserProfileID)


            result = Utility.postRequest(serverUrl, values)
            //			//------------------------------------Parsing
            //			JSONObject jObject = new JSONObject(result.toString());
            //			JSONObject ProfileResult = jObject.getJSONObject("GetAccountDetailResult");
            //			JSONArray j2 = ProfileResult.getJSONArray("AccountUsers");
            //			Gson gson = new Gson();
            //
            //			for(int i=0;i<j2.length();i++){
            //			UserAccountModel vm = gson.fromJson(j2.get(i).toString(), UserAccountModel.class);
            //			userAccountArrayList.add(vm);
            //			}
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result!!
    }

    companion object {
        var spinnerdata = ArrayList<String>()
    }
    //	public static final void showMessageDialog(Context contex, String title,
    //			String message) {
    //		if (message != null && message.trim().length() > 0) {
    //			Builder builder = new AlertDialog.Builder(contex);
    //			builder.setTitle(title);
    //			builder.setMessage(message);
    //			builder.setPositiveButton("OK",
    //					new DialogInterface.OnClickListener() {
    //						public void onClick(DialogInterface dialog, int id) {
    //							dialog.dismiss();
    //						}
    //					});
    //			builder.show();
    //		}
    //	}

}
