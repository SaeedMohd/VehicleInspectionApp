package com.matics;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.provider.Settings.Secure;

public class LivePhoneReadings {

	public static String phoneId = "";
	public static String latitude="0";
	public static String longitude="0";
	public static String gps_speed="0";
	public static String gps_time="0";
	public static String accelerometerX="0";
	public static String accelerometerY="0";
	public static String accelerometerZ="0";
	public static String accountId="0";
	public static String year="";
	public static String make="";
	public static String model="";
	public static String mileage="0";
	public static boolean vinRetrievable=false;
    public static int vinId=0;
	public static String bluetoothId = "";
	public static String deviceTime;
	public static String vin="";
	public static String mobileUserProfileId="";
	public static int vehicleHealthValue=0;
	public static String vehicleHealthMessage="";
	
	
	public static String getVin() {
		return vin;
	}
	public static void setVin(String vin) {
		LivePhoneReadings.vin = vin;
	}
	public static String getPhoneId(Context context) {
		return Secure.getString(context.getContentResolver(),
				Secure.ANDROID_ID);
	}
	public static void setPhoneId(String phoneId) {
		LivePhoneReadings.phoneId = phoneId;
	}
	public static String getLatitude() {
		return latitude;
	}
	public static void setLatitude(String latitude) {
		LivePhoneReadings.latitude = latitude;
	}
	public static String getLongitude() {
		return longitude;
	}
	public static void setLongitude(String longitude) {
		LivePhoneReadings.longitude = longitude;
	}
	public static String getGps_speed() {
		return gps_speed;
	}
	public static void setGps_speed(float gps_speed) {
		DecimalFormat myFormatter = new DecimalFormat("###.##");
		LivePhoneReadings.gps_speed = ""+myFormatter.format(gps_speed*3.6f);
	}
	public static String getAccountId() {
		return accountId;
	}
	public static void setAccountId(String accountId) {
		LivePhoneReadings.accountId = accountId;
	}
	public static String getYear() {
		return year;
	}
	public static void setYear(String year) {
		LivePhoneReadings.year = year;
	}
	public static String getMake() {
		return make;
	}
	public static void setMake(String make) {
		LivePhoneReadings.make = make;
	}
	public static String getModel() {
		return model;
	}
	public static void setModel(String model) {
		LivePhoneReadings.model = model;
	}
	public static String getMileage() {
		return mileage;
	}
	public static void setMileage(String mileage) {
		LivePhoneReadings.mileage = mileage;
	}
	public static String getDeviceTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");// date formate
		String currentDateandTime = sdf.format(new Date());
		return currentDateandTime.toString();
	}
	public static void setDeviceTime(String deviceTime) {
		LivePhoneReadings.deviceTime = deviceTime;
	}
	public static String getGps_time() {
		return gps_time;
	}
	public static void setGps_time(long gps_time) {
		LivePhoneReadings.gps_time = ""+gps_time;
	}
	public static String getBluetoothId() {
		return bluetoothId;
	}
	public static void setBluetoothId(String bluetoothId) {
		LivePhoneReadings.bluetoothId = bluetoothId;
	}
	public static String getAccelerometerX() {
		return accelerometerX;
	}
	public static void setAccelerometerX(String accelerometerX) {
		LivePhoneReadings.accelerometerX = accelerometerX;
	}
	public static String getAccelerometerY() {
		return accelerometerY;
	}
	public static void setAccelerometerY(String accelerometerY) {
		LivePhoneReadings.accelerometerY = accelerometerY;
	}
	public static String getAccelerometerZ() {
		return accelerometerZ;
	}
	public static void setAccelerometerZ(String accelerometerZ) {
		LivePhoneReadings.accelerometerZ = accelerometerZ;
	}
	public static String getMobileUserProfileId() {
		return mobileUserProfileId;
	}
	public static void setMobileUserProfileId(String mobileUserProfileId) {
		LivePhoneReadings.mobileUserProfileId = mobileUserProfileId;
	}
	public static String getPhoneId() {
		return phoneId;
	}
	public static void setGps_speed(String gps_speed) {
		LivePhoneReadings.gps_speed = gps_speed;
	}
	public static void setGps_time(String gps_time) {
		LivePhoneReadings.gps_time = gps_time;
	}
	public static int getVehicleHealthValue() {
		return vehicleHealthValue;
	}
	public static void setVehicleHealthValue(int vehicleHealthValue) {
		LivePhoneReadings.vehicleHealthValue = vehicleHealthValue;
	}
	public static String getVehicleHealthMessage() {
		return vehicleHealthMessage;
	}
	public static void setVehicleHealthMessage(String vehicleHealthMessage) {
		LivePhoneReadings.vehicleHealthMessage = vehicleHealthMessage;
	}

    public static int getVinId() {
        return vinId;
    }

    public static void setVinId(int vinId) {
        LivePhoneReadings.vinId = vinId;
    }

	public static boolean getVinRetrievable() {
		return vinRetrievable;
	}

	public static void setVinRetrievable(boolean vinRetrievable) {
		LivePhoneReadings.vinRetrievable = vinRetrievable;
	}
}
