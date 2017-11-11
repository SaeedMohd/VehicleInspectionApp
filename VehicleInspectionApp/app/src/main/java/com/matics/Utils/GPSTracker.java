package com.matics.Utils;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import com.matics.Bluetooth.BluetoothApp;
import com.matics.LivePhoneReadings;
import com.matics.MainActivity;
import com.matics.adapter.DataBaseHelper;
import com.matics.model.VehicleProfileModel;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class GPSTracker extends Service implements LocationListener {

	private final Context mContext;

	// flag for GPS status
	boolean isGPSEnabled = false;

	// flag for network status
	boolean isNetworkEnabled = false;

	// flag for GPS status
	boolean canGetLocation = false;

	Location location; // location
	double latitude; // latitude
	double longitude; // longitude
	double GPSSpeed,GPSTime;
	
	boolean isTheSameGpsSpeed = false;
	int sameGpsSpeedCount=0;
	int mileageCounterIndicator = 0;
	int mileageDBUpdateCounter = 0;
	double accomulatedMileage;
	
	double firstLatitudeValue;
	double firstLongitudeValue;
	double secondLatitudeValue;
	double secondLongitudeValue;
	private DataBaseHelper dbHelper; 
	
	public boolean isGetLocationStarted = false;
	
	private DecimalFormat df  = new DecimalFormat("##.##");
	
	

	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5; // 10 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1000 * 2; // 1 minute
	
	// Declaring a Location Manager
	protected LocationManager locationManager;

	public GPSTracker(Context context) {
		this.mContext = context;
	}

	public Location getLocation() {
		try {
			
			locationManager = (LocationManager) mContext
					.getSystemService(LOCATION_SERVICE);
			// getting GPS status
			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
				//Log.dMainActivity.TAG,"nothing available, no gps no network");
			} else {
				this.canGetLocation = true;
				
				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {
					
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						//Log.dMainActivity.TAG, "GPS Enabled");
						if (locationManager != null) {
							location = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) {
								latitude = location.getLatitude();
								longitude = location.getLongitude();
								GPSSpeed =  location.getSpeed();
								GPSTime = location.getTime();
							}
						}
					
				}else{			
				if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					//Log.dMainActivity.TAG, "Network");
					if (locationManager != null) {
						location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
							GPSSpeed =  location.getSpeed();
							//Log.e("", "GPS speed" + location.getSpeed());
						}
					}
				}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return location;
	}
	
	/**
	 * Stop using GPS listener
	 * Calling this function will stop using GPS in your app
	 * */
	public void stopUsingGPS(){
		if(locationManager != null){
			locationManager.removeUpdates(GPSTracker.this);
		}		
	}
	
	public boolean isGPSEnabled(){
		if(locationManager!=null){
		isGPSEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		}
		return isGPSEnabled;
	}
	
	/**
	 * Function to get latitude
	 * */
	public double getLatitude(){
		if(location != null){
			latitude = location.getLatitude();
		}
		
		// return latitude
		return latitude;
	}
	
	/**
	 * Function to get longitude
	 * */
	public double getLongitude(){
		if(location != null){
			longitude = location.getLongitude();
		}
		
		// return longitude
		return longitude;
	}
	public double getGpsspeed(){
		if(location != null){
			GPSSpeed = location.getSpeed();
		}
		
		// return longitude
		return GPSSpeed;
	}
	public double getGpsTime(){
		if(location != null){
			GPSTime = location.getTime();
		}
		
		// return longitude
		return GPSTime;
	}
	
	/**
	 * Function to check GPS/wifi enabled
	 * @return boolean
	 * */
	public boolean canGetLocation() {
		return this.canGetLocation;
	}
	
	/**
	 * Function to show settings alert dialog
	 * On pressing Settings button will lauch Settings Options
	 * */
	public void showSettingsAlert(){
		
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("GPS is settings");
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
            	Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            	mContext.startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
       // alertDialog.show();
	}

	@Override
	public void onLocationChanged(Location location) {
		////Log.i("location changes", "gps speed = "+location.getSpeed());
		if(GPSSpeed==location.getSpeed()){
			if(sameGpsSpeedCount>3){
				LivePhoneReadings.setGps_speed(0);
			}else{
			sameGpsSpeedCount++;
			}
		}else{
			LivePhoneReadings.setGps_speed(location.getSpeed());	
		}
		GPSSpeed = location.getSpeed();
		
		LivePhoneReadings.setLatitude(""+location.getLatitude());
		LivePhoneReadings.setLongitude(""+location.getLongitude());
		LivePhoneReadings.setGps_time(location.getTime());
		
		if(BluetoothApp.isConnectionEstablished){
			if(mileageCounterIndicator==0){
				firstLatitudeValue = location.getLatitude();
				firstLongitudeValue = location.getLongitude();
			}
			
			mileageCounterIndicator++;
			mileageDBUpdateCounter++;
			
			if(mileageCounterIndicator==3){
				mileageCounterIndicator=0;
				secondLatitudeValue = location.getLatitude();
				secondLongitudeValue = location.getLongitude();
                try {
                    accomulatedMileage = Double.parseDouble(LivePhoneReadings.mileage) + distance(firstLatitudeValue, firstLongitudeValue, secondLatitudeValue, secondLongitudeValue, 'N');
                    LivePhoneReadings.mileage = "" + df.format(accomulatedMileage);
                }catch(NumberFormatException numberFormatException){
                    //Log.dMainActivity.TAG,"live phone readings mileage = "+LivePhoneReadings.mileage);
                    LivePhoneReadings.mileage = "0";
                    numberFormatException.printStackTrace();
                }
				//Log.dMainActivity.TAG,"Mileage accomulated = "+LivePhoneReadings.mileage);
				if(LivePhoneReadings.mileage.equals("NaN")){
					LivePhoneReadings.mileage="1";
				}
			}
			
			if(mileageDBUpdateCounter==5){
				mileageDBUpdateCounter=0;
				dbHelper = new DataBaseHelper(BluetoothApp.context);
				VehicleProfileModel vehicleProfileModel = dbHelper.getVehicleProfileDataWithVin(LivePhoneReadings.getVin());
						if(vehicleProfileModel!=null && vehicleProfileModel.getVIN().length()>0){
							vehicleProfileModel.setMileage(LivePhoneReadings.mileage);
							dbHelper.updateVehicleMileage(vehicleProfileModel);
						}else{
							ArrayList<VehicleProfileModel> vehicleProfileList = new ArrayList<VehicleProfileModel>();
							vehicleProfileList = dbHelper.getprofiledataforlist();
							for(VehicleProfileModel vpl : vehicleProfileList){
								if(vpl.getMake().equals(LivePhoneReadings.getMake()) && vpl.getModel().equals(LivePhoneReadings.getModel()) && (""+vpl.getYear()).equals(LivePhoneReadings.getYear())){
									//Log.dMainActivity.TAG,"Vehicle found, updating mileage now");
									vpl.setMileage(LivePhoneReadings.mileage);
									dbHelper.updateVehicleMileage(vpl);
									break;
								}
							}
						}
						vehicleProfileModel = null;
						dbHelper.close();
						dbHelper = null;
			}
			
			
		}
		
	}
	
	
	private double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
		  double theta = lon1 - lon2;
		  double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		  dist = Math.acos(dist);
		  dist = rad2deg(dist);
		  dist = dist * 60 * 1.1515;
		  if (unit == 'K') {
		    dist = dist * 1.609344;
		  } else if (unit == 'N') {
		  	dist = dist * 0.8684;
		    }
		  return (dist*1.5);
		}

		/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
		/*::  This function converts decimal degrees to radians             :*/
		/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
		private double deg2rad(double deg) {
		  return (deg * Math.PI / 180.0);
		}

		/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
		/*::  This function converts radians to decimal degrees             :*/
		/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
		private double rad2deg(double rad) {
		  return (rad * 180 / Math.PI);
		}
	
	public void accomulateDistance(){
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
	}

	@Override
	public IBinder onBind(Intent arg0) {
		
		return null;
	}

}
