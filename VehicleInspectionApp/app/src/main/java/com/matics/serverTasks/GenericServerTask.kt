package com.matics.serverTasks

import android.content.ContentValues
import android.content.Context

import com.matics.R
import com.matics.Utils.Utility

abstract class GenericServerTask(private val context: Context, private val url: String, private val parameters: Array<String>, private val values: Array<String>) : AsyncTaskParent() {
    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg params: String): String {
        var result: String? = null

        try {
            val contentValues = ContentValues()
            for (x in parameters.indices) {
                contentValues.put(parameters[x], values[x])
            }
            result = Utility.postRequest(url, contentValues)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result!!
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)
        onTaskCompleted(result)
    }

}
