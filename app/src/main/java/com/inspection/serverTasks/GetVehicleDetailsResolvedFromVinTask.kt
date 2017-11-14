package com.inspection.serverTasks

import android.content.ContentValues
import android.content.Context
import android.util.Log

import com.inspection.Utils.Utility
import com.inspection.model.VehicleProfileModel

import org.json.JSONObject

import java.util.ArrayList

abstract class GetVehicleDetailsResolvedFromVinTask(private val mContext: Context, private val vin: String) : AsyncTaskParent() {
    private var IsSuccess = ""

    internal var isnet1: Boolean? = null

    init {
        isnet1 = Utility.checkInternetConnection(mContext)
    }

    override fun doInBackground(vararg params: String): String? {
        return ExecuteCommands()
    }

    override fun onPostExecute(result: String) {
        Log.v("responseeee of result", result)
        onTaskCompleted(result)
    }

    override fun onCancelled() {
        super.onCancelled()
    }

    private fun ExecuteCommands(): String? {
        Utility.VehiclePofileData = ArrayList<VehicleProfileModel>()
        var result: String? = null

        val getServerPath = "http://api.edmunds.com/api/v1/vehicle/vin/$vin/configuration?"
        try {
            val values = ContentValues()

            values.put("api_key", "e7yfyg8y7w9yr7rs3a2qr4ba".toString())

            result = Utility.postRequest(getServerPath, values)
            // ------------------------------------Parsing
            val jObject = JSONObject(result!!.toString())
            //Log.dMainActivity.TAG, "server repoinse is: " + result)
            if (!jObject.has("error")) {
                val year = jObject.get("year")
                val makeJson = jObject.getJSONObject("make")
                val make = makeJson.get("name")
                val modelJson = jObject.getJSONObject("model")
                val model = modelJson.get("name")
                Log.v("i am hereeeeeeeeeeee", "$year!-!$make!-!$model!-!$vin")
                return "$year!-!$make!-!$model!-!$vin"
            } else if (jObject.has("error")) {
                IsSuccess = "false"
                Log.v("this is the car details", "errrrrrrrrrrrrrrrrror")
                return "failed"
            }
        } catch (e: Exception) {
            Log.v("errrrrrrorrrr", "errrrrrrrrror")
            IsSuccess = "false"
            return "failed"
        }

        return "failed"
    }


}
