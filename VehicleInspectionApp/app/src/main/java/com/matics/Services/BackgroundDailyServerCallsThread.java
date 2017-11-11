package com.matics.Services;

import android.content.Context;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.matics.LivePhoneReadings;
import com.matics.Bluetooth.BluetoothApp;
import com.matics.adapter.DataBaseHelper;
import com.matics.model.VehicleProfileModel;
import com.matics.serverTasks.GetVINsByUserTask;
import com.matics.Utils.ApplicationPrefs;

public class BackgroundDailyServerCallsThread extends Thread {

    Context activity;

	public BackgroundDailyServerCallsThread(Context activity) {
        this.activity = activity;
	}

	@Override
	public void run() {
		super.run();

        updateVehicleHealth();
        getVinsByUser();

		new UpdateVinsMaintenanceHistoryTask().execute(ApplicationPrefs.getInstance(BluetoothApp.context).getUserProfilePref().getUserName());

	}


	private void updateVehicleHealth(){
        if (LivePhoneReadings.getVin().trim().length() > 0) {
            UpdateVehicleHealthStatusTask vehicleHealthStatusTask = new UpdateVehicleHealthStatusTask();
            try {
                vehicleHealthStatusTask.execute(LivePhoneReadings.getVin())
                        .get(40, TimeUnit.SECONDS);

            } catch (TimeoutException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private void getVinsByUser(){

            new GetVINsByUserTask(activity, ApplicationPrefs.getInstance(BluetoothApp.context).getUserProfilePref().getUserName()) {
                @Override
                public void onTaskCompleted(String result) {
                    try {
                        JSONObject aObject = new JSONObject(result.toString());
                        JSONObject jObject = aObject.getJSONObject("GetVinsByUserResult");
                        JSONArray vehicleProfilesArray = jObject
                                .getJSONArray("VehicleProfiles");
                        ArrayList<VehicleProfileModel> vehicleProfileModelArrayList = new ArrayList<VehicleProfileModel>();
                        VehicleProfileModel vehicleProfileModel;
                        for (int x = 0; x < vehicleProfilesArray.length(); x++) {
                            vehicleProfileModel = new Gson().fromJson(vehicleProfilesArray
                                    .get(x).toString(), VehicleProfileModel.class);
                            if (vehicleProfileModel.getVIN().equals("") && vehicleProfileModel.getModel().equals("") && vehicleProfileModel.getMake().equals("") && vehicleProfileModel.getYear().equals("0")) {
                                vehicleProfileModel = null;
                                continue;
                            }
                            vehicleProfileModelArrayList.add(vehicleProfileModel);
                            vehicleProfileModel = null;
                        }
                        if (vehicleProfileModelArrayList.size() > 0) {
                            DataBaseHelper dbHelper = new DataBaseHelper(
                                    activity);
                            dbHelper.clearProfileTable();
                            VehicleProfileModel vehicleProfileModel1;
                            for (int x = 0; x < vehicleProfileModelArrayList.size(); x++) {
                                vehicleProfileModel1 = vehicleProfileModelArrayList.get(x);
                                dbHelper.addProfile(vehicleProfileModel1);
                            }
                        }
                    } catch (JSONException e1) {
                        // Toast.makeText(getActivity(), "Error parcing Server data",
                        // Toast.LENGTH_SHORT).show();
                        e1.printStackTrace();
                    }
                }
            }.execute();


    }

}
