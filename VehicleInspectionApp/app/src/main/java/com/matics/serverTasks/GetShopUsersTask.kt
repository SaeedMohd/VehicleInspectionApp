package com.matics.serverTasks

import android.app.AlertDialog
import android.app.Fragment
import android.app.FragmentManager
import android.app.FragmentTransaction
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.widget.Toast

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.matics.MainActivity
import com.matics.R
import com.matics.Utils.ApplicationPrefs
import com.matics.Utils.Utility
import com.matics.fragments.VideoCallFragments.FragmentVideoCall
import com.matics.model.AccountDetailModel
import com.matics.model.UserProfileModel

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList

import com.matics.MainActivity.getActivity

/**
 * Created by devsherif on 6/21/16.
 */
abstract class GetShopUsersTask(internal var context: Context, accountID: Int) : AsyncTaskParent() {
    internal var accountID = -1

    init {
        //Log.dMainActivity.TAG, "Started")
        this.accountID = accountID

    }

    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg strings: String): String {
        val values = ContentValues()
        values.put("AccountID", accountID)

        //Log.dMainActivity.TAG, "calling url now")
        return Utility.postRequest(context.getString(R.string.getShopCustomersURL), values)
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)

        if (result.contains("IsSuccess\":true")) {
            onTaskCompleted(result)
        } else {
            onTaskCompleted("Failed")
        }
    }


}
