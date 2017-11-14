package com.inspection.serverTasks

import android.content.ContentValues
import android.content.Context

import com.inspection.R
import com.inspection.Utils.Utility

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
