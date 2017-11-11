package com.matics.serverTasks

import java.util.ArrayList
import org.json.JSONArray
import org.json.JSONObject

import com.matics.LivePhoneReadings
import com.matics.Bluetooth.BluetoothApp
import com.matics.MainActivity
import com.matics.adapter.DataBaseHelper
import com.matics.model.VehicleProfileModel
import com.matics.Utils.ApplicationPrefs
import com.matics.Utils.Utility

import android.content.ContentValues
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.widget.Toast

class GetProfileTask(private val mContext: Context) : AsyncTaskParent() {
    private var VIn = ""
    private var make = ""
    private var model = ""
    private var year = ""
    private var IsSuccess = ""

    internal var isnet1: Boolean? = null
    internal var Mydb: DataBaseHelper

    init {
        isnet1 = Utility.checkInternetConnection(mContext)
        Mydb = DataBaseHelper(mContext)
    }

    override fun onPreExecute() {
        super.onPreExecute()
    }


    override fun doInBackground(vararg params: String): String? {
        ExecuteCommands(params[0])
        return null
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)
        // DataBaseHelper DB = new DataBaseHelper(mContext);
        if (IsSuccess.equals("true", ignoreCase = true)) {
            if (existVIn()!!) {
                try {
                    // Mydb.addProfile(new
                    // VehicleProfileModel(VIn,"",Integer.parseInt(year),make,model,"",MainActivity.PHONE_ID,"true","","",MainActivity.mediaPrefs.getString(MainActivity.DeviceAddress,
                    // ""),MainActivity.finalvehicle_health));
                } catch (e: Exception) {
                    //Log.e("", "App in Background")
                }

            } else {
                // Mydb.updateprofile(new
                // VehicleProfileModel(VIn,"",Integer.parseInt(year),make,model,"",MainActivity.PHONE_ID,"true","","","",LivePhoneReadings.getVehicleHealthValue()),);
            }
        } else {
            //Log.e("", "In else")
            if (VIn.length == 17) {
                try {
                    if (existVIn()!!) {
                        // Mydb.addProfile(new
                        // VehicleProfileModel(VIn,"",0,"","","",MainActivity.PHONE_ID,"true","","","",LivePhoneReadings.getVehicleHealthValue()));
                    } else {
                        // Mydb.updateprofile(new
                        // VehicleProfileModel(VIn,"",Integer.parseInt("0"),make,model,"",MainActivity.PHONE_ID,"true","","","",LivePhoneReadings.getVehicleHealthValue()));
                    }

                } catch (e: Exception) {
                    //Log.e("", "App in Background")
                }

            }
        }
    }

    override fun onCancelled() {
        super.onCancelled()
    }

    private fun ExecuteCommands(VIN: String?): Any? {
        Utility.VehiclePofileData = ArrayList<VehicleProfileModel>()
        var result: String? = null
        this.VIn = VIN!!
        //Log.dMainActivity.TAG, "with VIN value = " + VIN!!)

        if (isnet1!!) {

            val getServerPath = "http://api.edmunds.com/v1/api/toolsrepository/vindecoder?"
            try {
                val values = ContentValues()

                if (VIN == null || VIN.equals("null", ignoreCase = true)
                        || VIN.equals("", ignoreCase = true)) {
                    values.put("vin", "NODATA".toString())
                } else {
                    values.put("vin", VIN.toString())
                }
                values.put("fmt", "json".toString())

                values.put("api_key", "e7yfyg8y7w9yr7rs3a2qr4ba".toString())

                result = Utility.postRequest(getServerPath, values)
                // ------------------------------------Parsing
                val jObject = JSONObject(result!!.toString())
                //Log.dMainActivity.TAG, "server repoinse is: " + result)
                if (jObject.isNull("styleHolder") == false) {
                    val j1 = jObject.getJSONArray("styleHolder")
                    val j2 = j1.getJSONObject(0)
                    year = j2.getString("year")
                    make = j2.getString("makeName")
                    model = j2.getString("modelName")
                    IsSuccess = "true"
                    //Log.e("VIN decoded", "year is :" + year)
                    //Log.e("VIN Decoded", "make is :" + make)
                    //Log.e("VIN Decoded", "make is :" + model)

                    if (ApplicationPrefs.getInstance(BluetoothApp.context).vehicleProfilePref != null) {
                        var vehicleProfileModel: VehicleProfileModel? = ApplicationPrefs.getInstance(BluetoothApp.context).vehicleProfilePref
                        vehicleProfileModel!!.make = make
                        vehicleProfileModel.model = model
                        vehicleProfileModel.year = year
                        vehicleProfileModel.vinRetrievable = true
                        vehicleProfileModel.vin = VIN
                        ApplicationPrefs.getInstance(BluetoothApp.context).setVehicleProfilePrefs(vehicleProfileModel)
                        var dbHelper: DataBaseHelper? = DataBaseHelper(BluetoothApp.context)
                        dbHelper!!.updateprofile(vehicleProfileModel)
                        dbHelper = null
                        vehicleProfileModel = null
                    } else {
                        var vehicleProfileModel: VehicleProfileModel? = VehicleProfileModel()
                        vehicleProfileModel!!.vin = VIN
                        vehicleProfileModel.btID = LivePhoneReadings.bluetoothId
                        vehicleProfileModel.make = make
                        vehicleProfileModel.model = model
                        vehicleProfileModel.vinRetrievable = true
                        vehicleProfileModel.year = year
                        ApplicationPrefs.getInstance(BluetoothApp.context).setVehicleProfilePrefs(vehicleProfileModel)
                        var dbHelper: DataBaseHelper? = DataBaseHelper(BluetoothApp.context)
                        dbHelper!!.updateprofile(vehicleProfileModel)
                        dbHelper = null
                        vehicleProfileModel = null
                    }
                } else if (jObject.isNull("error")) {
                    IsSuccess = "false"
                    Toast.makeText(BluetoothApp.context, "VIN is not correct, please enter a right VIN, or set Vehicle details manually", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // DataBaseHelper DB = new DataBaseHelper(mContext);
                // if(existVIn()){
                // try {
                // DB.addProfile(new
                // VehicleProfileModel(VIn,"",Integer.parseInt(year),make,model,"",MainActivity.PHONE_ID,"true","","",""));
                // } catch (Exception e1) {
                // //Log.e("", "App in Background");
                // }
                // }else{
                // DB.updateprofile(new
                // VehicleProfileModel(VIn,"",Integer.parseInt(year),make,model,"",MainActivity.PHONE_ID,"true","","",""));
                // }
            }

        }
        return null
    }

    fun existVIn(): Boolean? {
        var Flag: Boolean? = true
        try {

            val data = Mydb.getprofiledata()
            for (i in data.indices) {
                if (VIn.equals(data[i].VIN, ignoreCase = true)) {
                    Flag = false
                    break
                } else {
                    Flag = true
                }
            }
            return Flag
        } catch (e: Exception) {
            // TODO: handle exception
            e.printStackTrace()
            return Flag
        }

    }

    override fun onTaskCompleted(result: String) {

    }
}
