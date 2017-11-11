package com.matics.serverTasks

import java.util.ArrayList

import org.json.JSONArray
import org.json.JSONObject

import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.os.AsyncTask
import android.util.Log

import com.google.gson.Gson
import com.matics.MainActivity
import com.matics.command.CommandsResponseParser
import com.matics.model.UserAccountModel
import com.matics.Utils.Utility


class GetAccountDetailTask(mContext: Activity) : AsyncTask<String, Int, ArrayList<UserAccountModel>>() {

    private val mContext: Context
    private val mProgressDialog: ProgressDialog?
    var isSuccess = "false"

    enum class SearchParam {
        NAME, CITY, ZIP, FIVE_DIGITS
    }

    var searchParam: SearchParam? = null
    private val getShopsByName = "http://www.jet-matics.com/JetComService/JetCom.svc/GetAccountDetail?"
    private val getShopsByCity = "http://www.jet-matics.com/JetComService/JetCom.svc/GetAccountDetailByAccountCity?"
    private val getShopsBy5Digits = "http://www.jet-matics.com/JetComService/JetCom.svc/GetAccountDetailBy5DigitAccountID?"
    private val getShopsByZip = "http://www.jet-matics.com/JetComService/JetCom.svc/GetAccountDetailByZip?"

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

    override fun doInBackground(vararg params: String): ArrayList<UserAccountModel> {
        val userAccountArrayList = GetAccountDetail(params[0])
        return userAccountArrayList
    }

    override fun onPostExecute(userAccountArrayList: ArrayList<UserAccountModel>) {
        super.onPostExecute(userAccountArrayList)
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

    private fun GetAccountDetail(Name: String): ArrayList<UserAccountModel> {
        //		Utility.VehiclePofileData.clear();
        var result: String? = null
        val userAccountArrayList = ArrayList<UserAccountModel>()

        try {
            val values = ContentValues()
            when (searchParam) {
                GetAccountDetailTask.SearchParam.NAME -> {
                    values.put("AccountName", Name.toString())
                    values.put("BtId", CommandsResponseParser.bluetoothId.toString())
                    result = Utility.postRequest(getShopsByName, values)
                }
                GetAccountDetailTask.SearchParam.CITY -> {
                    values.put("City", Name.toString())
                    result = Utility.postRequest(getShopsByCity, values)
                }

                GetAccountDetailTask.SearchParam.ZIP -> {
                    values.put("Zip", Name.toString())
                    result = Utility.postRequest(getShopsByZip, values)
                }

                GetAccountDetailTask.SearchParam.FIVE_DIGITS -> {
                    values.put("AccountID", Name.toString())
                    result = Utility.postRequest(getShopsBy5Digits, values)
                }
            }


            //------------------------------------Parsing

            val jObject = JSONObject(result!!.toString())
            var ProfileResult = JSONObject()
            when (searchParam) {
                GetAccountDetailTask.SearchParam.NAME -> ProfileResult = jObject.getJSONObject("GetAccountDetailResult")
                GetAccountDetailTask.SearchParam.CITY -> ProfileResult = jObject.getJSONObject("GetAccountDetailByAccountCityResult")

                GetAccountDetailTask.SearchParam.ZIP -> ProfileResult = jObject.getJSONObject("GetAccountDetailByZipResult")

                GetAccountDetailTask.SearchParam.FIVE_DIGITS -> ProfileResult = jObject.getJSONObject("GetAccountDetailBy5DigitAccountIDResult")
            }


            val j2 = ProfileResult.getJSONArray("AccountUsers")
            val gson = Gson()

            for (i in 0..j2.length() - 1) {
                val vm = gson.fromJson(j2.get(i).toString(), UserAccountModel::class.java)
                userAccountArrayList.add(vm)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //Log.dMainActivity.TAG, "size= " + userAccountArrayList.size)
        return userAccountArrayList
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
