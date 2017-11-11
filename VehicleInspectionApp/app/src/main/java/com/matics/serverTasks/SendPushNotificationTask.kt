package com.matics.serverTasks

import android.content.ContentValues

import com.matics.Utils.Utility

/**
 * Created by devsherif on 6/21/16.
 */
abstract class SendPushNotificationTask(private val mobileUserProfileId: String, private val message: String, private val imageUrl: String, private val notificationType: String, private val notificationTitle: String, private val dateString: String) : AsyncTaskParent() {

    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg strings: String): String? {

        val values = ContentValues()

        values.put("MobileUserProfileId", mobileUserProfileId)
        values.put("message", message)
        values.put("image", imageUrl)
        values.put("type", notificationType)
        values.put("title", notificationTitle)
        values.put("date", dateString)

        Utility.postRequest("http://www.jet-matics.com/JetComService/JetCom.svc/SendNotification?", values)

        return ""
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        onTaskCompleted(result!!)
    }

    companion object {

        var callRequestedType = "CallRequested"
    }

}
