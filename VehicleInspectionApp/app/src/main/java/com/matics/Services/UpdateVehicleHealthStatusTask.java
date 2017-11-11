package com.matics.Services;

import java.util.Calendar;


import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.matics.Bluetooth.BluetoothApp;
import com.matics.MainActivity;
import com.matics.adapter.DataBaseHelper;
import com.matics.model.VehicleProfileModel;
import com.matics.Utils.ApplicationPrefs;
import com.matics.Utils.Utility;

import android.content.ContentValues;
import android.os.AsyncTask;
import android.util.Log;

public class UpdateVehicleHealthStatusTask extends AsyncTask<String, Integer, String> {

	
	private String ErrorDetail,status;
	// private final LoginData loginData = new LoginData();

	public UpdateVehicleHealthStatusTask() {
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();

	}

	@Override
	protected String doInBackground(String... params) {
		return updateVehicleHealthDataWithVin(params[0]);
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		try{
			if(result!=null && result.trim().length()>0) {
				JSONObject jObject = new JSONObject(result.toString());
				JSONObject j1 = jObject.getJSONObject("GetVehicleHealthDataResult");
				VehicleHealthDataResult vehicleHealthDataResult = new Gson().fromJson(j1.toString(), VehicleHealthDataResult.class);
				if (vehicleHealthDataResult.getVehicleHealth() > 0) {
					ApplicationPrefs.getInstance(BluetoothApp.context).setVehicleHealthValuePref(vehicleHealthDataResult.VehicleHealth);
					ApplicationPrefs.getInstance(BluetoothApp.context).setVehicleHealthMessagePref(vehicleHealthDataResult.Reason);
					ApplicationPrefs.getInstance(BluetoothApp.context).setLastUpdatePref(Calendar.DAY_OF_MONTH);

					VehicleProfileModel vehicleProfileModel = ApplicationPrefs.getInstance(BluetoothApp.context).getVehicleProfilePref();

					if (vehicleProfileModel != null) {
						vehicleProfileModel.setVehicleHealth(vehicleHealthDataResult.VehicleHealth);
						vehicleProfileModel.setReason(vehicleHealthDataResult.Reason);
						vehicleProfileModel.setLastupdate("" + Calendar.DAY_OF_MONTH);

						DataBaseHelper dbHelper = new DataBaseHelper(BluetoothApp.context);
						dbHelper.updateprofile(vehicleProfileModel);

						//Log.dMainActivity.TAG, "Vehicle Health Prefs is set successfully");
						//Log.dMainActivity.TAG, "reason: " + vehicleHealthDataResult.Reason);
						//Log.dMainActivity.TAG, "Health: " + vehicleHealthDataResult.VehicleHealth);
					} else {
						//Log.dMainActivity.TAG, "No vehicle profile");
					}
				}
			}
		}catch(JSONException jsonException){
			
		}
	}


	private String updateVehicleHealthDataWithVin(String vin) {
		String result = null;
        String getServerPath = "http://jet-matics.com/JetComService/JetCom.svc/GetVehicleHealthData?";
        try {
        	ContentValues values = new ContentValues();
			values.put("VIN", vin);
			
			
			result = Utility.postRequest(getServerPath, values);
			
        }
        catch (Exception e) {
            e.printStackTrace();
        }
		return result;
	}
	protected void onCancelled() {
		super.onCancelled();
		// if(mProgressDialog != null && mProgressDialog.isShowing())
		// mProgressDialog.dismiss();
	}
	
	
	
	class VehicleHealthDataResult{
		String Reason;
		int VehicleHealth;
		public String getReason() {
			return Reason;
		}
		public void setReason(String reason) {
			Reason = reason;
		}
		public int getVehicleHealth() {
			return VehicleHealth;
		}
		public void setVehicleHealth(int vehicleHealth) {
			VehicleHealth = vehicleHealth;
		}
		
	}
}
