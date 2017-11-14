package com.inspection.serverTasks

import android.content.ContentValues
import android.content.Context

import com.inspection.R
import com.inspection.Utils.Utility

abstract class UpdateCompetitorPhoneTask(private val context: Context, private val phoneId: Int, private val totalTimeOfPhoneCall: Int, private val mobileUserProfileId: Int, private val phoneIncidentId: Int) : AsyncTaskParent() {

    override fun onPreExecute() {
        super.onPreExecute()

    }

    override fun doInBackground(vararg params: String): String {

        //http://www.jet-matics.com/JetComService/JetCom.svc/UpdateCompetitorPhone?=86&=25&MobileUserProfileID=13&=6

        val values = ContentValues()
        values.put("PhoneID", phoneId)
        values.put("TotalTimeofPhoneCall", totalTimeOfPhoneCall)
        values.put("PhoneIncidentID", phoneIncidentId)
        values.put("MobileUserProfileId", mobileUserProfileId)

        return Utility.postRequest(context.getString(R.string.updateCompetitorPhone), values)
    }

    override fun onPostExecute(s: String) {
        super.onPostExecute(s)
        onTaskCompleted(s)
    }
}
