package com.inspection.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.gson.Gson;
import com.inspection.Bluetooth.BluetoothApp;
import com.inspection.LivePhoneReadings;
import com.inspection.MainActivity;
import com.inspection.model.AccountDetailModel;
import com.inspection.model.UserAccountModel;
import com.inspection.model.UserProfileModel;
import com.inspection.model.VehicleProfileModel;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;

public class ApplicationPrefs {

    private static ApplicationPrefs applicationPrefsInstance = null;

    public boolean isFirstRun;

    private Context context;

    public static final String PREFS_NAME = "VehicleHealthPref";
    public static final String IS_FIRST_RUN = "IsFirstRun";
    public static final String ACCOUNT_DETAIL = "AccountDetail";
    public static final String USER_PROFILE = "UserProfile";
    public static final String SHOP_USER_PROFILE = "ShopUserProfile";
    public static final String USER_ACCOUNT = "UserAccount";
    public static final String VEHICLE_PROFILE = "VehicleProfile";
    public static final String VEHICLE_HEALTH_VALUE = "VehicleHealthValue";
    public static final String VEHICLE_HEALTH_MESSAGE = "VehicleHealthMessage";
    public static final String LOGGED_IN = "LoggedIn";
    public static final String LAST_UPDATE_DAY = "LastUpdateDay";
    public static final String GCM_REGISTRATION_ID = "registration_id";
    public static final String APP_VERSION = "appVersion";
    public static final String IS_CLEAR_TROUBLE_CODE_REQUIRED = "isClearTroubleCodeRequired";
    public static final String BT_NOTIFICATION = "bluetoothNotification";
    public static final String IS_BT_SNOOZE_REQUIRED = "isBluetoothSnoozeRequired";
    public static final String ENABLE_NOTIFICATION_TIME = "enableNotificationTime";
    public static final String AUTO_PAIR_WITH_OBD2 = "autoPairWithObd2";
    public static final String OBD_DEVICES_ARRAY = "obdDevicesArray";
    public static final String IS_PASSWORD_RESET = "isPasswordReset";
    public static final String EMAIL_FOR_PASSWORD_BEEN_RESET = "emailForUserPasswordBeenReset";
    public static final String LAST_CONNECTED_VIN_ID = "lastConnectedVinId";
    public static final String SHOP_IMAGE = "shopImage";
    public static final String IS_QUICKBLOX_USER_CREATED = "isQuickBloxUserCreated";
    public static final String IS_ANSWERING_A_CALL = "isAnsweringACall";
    public static final String LAST_SYNCH_DATE = "lastSynchDate";
    public static final String LAST_COMPETITOR_UPDATE = "lastCompetitorUpdate";
    public static final String COMPETITOR_LIST_OBJECT = "competitorListObject";
    public static final String COMPETITOR_GEOFENCE_ID = "competitor_geofence_campaign_id";
    public static final String COMPETITOR_GEOFENCE_CAMPAIGN_MINUTES = "competitor_geofence_campaign_minutes";
    public static final String USER_INSIDE_COMPETITOR_GEOFENCE = "userInsideCompetitorGeoFence";
    public static final String COMPETITOR_GEOFENCE_INCIDENT_ID = "competitorGeoFenceIncidentId";
    public static final String LAST_TIME_IN_COMPETITOR_LOCATION = "lastTimeInCompetitorLocation";
    public static final String LAST_GEOFENCE_ID = "lastGeoFenceId";
    public static final String IS_SAFETY_CHECK_PUSH_NOTIFICATIONS_ENABLED = "isSafetyCheckPushNotificationsEnabled";
    public static final String IS_SAFETY_CHECK_EMAIL_NOTIFICATIONS_ENABLED = "isSafetyCheckEmailNotificationsEnabled";
    public static final String IS_SAFETY_CHECK_SMS_NOTIFICATIONS_ENABLED = "isSafetyCheckSMSNotificationsEnabled";
    public static final String SafetyCheckProgramName = "safetyCheckProgramName";



    private final String TAG = "ApplicationPrefs::";

    // singletoin class - make sure only one instance ever exists
    private ApplicationPrefs(Context context) {
        this.context = context;
    }

    public static ApplicationPrefs getInstance(Context context) {
        if (applicationPrefsInstance == null) {
            applicationPrefsInstance = new ApplicationPrefs(context);
        }
        return applicationPrefsInstance;
    }

    // reads all the prefs out of the shared prefs
    public boolean IsFirstRun() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PREFS_NAME, 0);
        isFirstRun = sharedPreferences.getBoolean(IS_FIRST_RUN, true);
        return isFirstRun;
    }

    public void setFirstRun(boolean isFirstRun) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(IS_FIRST_RUN, isFirstRun);
        editor.commit();
    }

    public void setShopImage(Bitmap shopImage) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        ByteArrayOutputStream output = new ByteArrayOutputStream(shopImage.getByteCount());
        shopImage.compress(Bitmap.CompressFormat.PNG, 100, output);
        byte[] imageBytes = output.toByteArray();
        String imageString = new String(imageBytes);

        //editor.putString(SHOP_IMAGE, imageString);
        //editor.commit();
    }

    public Bitmap getShopImage() {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        String imageString = settings.getString(SHOP_IMAGE, "0");
        Bitmap shopBitmap = null;
        if (!imageString.equals("0")) {
            byte[] imageBytes = imageString.getBytes();
            shopBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        }
        return shopBitmap;
    }

    public void setAccountDetailPref(AccountDetailModel accountDetailModel) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        if (accountDetailModel != null) {
            editor.putString(ACCOUNT_DETAIL, new Gson().toJson(accountDetailModel));
            editor.putString(USER_PROFILE,
                    new Gson().toJson(accountDetailModel.getUserProfileModel()));
            editor.putString(USER_ACCOUNT,
                    new Gson().toJson(accountDetailModel.getUserAccountModel()));
            editor.putString(VEHICLE_PROFILE,
                    new Gson().toJson(accountDetailModel.getVehicleProfileModel()));
            editor.putBoolean(LOGGED_IN, true);
        } else {
            editor.putString(ACCOUNT_DETAIL, "");
            editor.putString(USER_PROFILE, "");
            editor.putString(USER_ACCOUNT, "");
            editor.putString(VEHICLE_PROFILE, "");
            editor.putBoolean(LOGGED_IN, false);
        }
        editor.commit();
    }

    public AccountDetailModel getAccountDetailPref() {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        AccountDetailModel accountDetailModel = new Gson().fromJson(
                settings.getString(ACCOUNT_DETAIL, ""),
                AccountDetailModel.class);
        return accountDetailModel;
    }

    public void setUserProfilePref(UserProfileModel userProfileModel) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(USER_PROFILE, new Gson().toJson(userProfileModel));
        editor.commit();
        LivePhoneReadings.setMobileUserProfileId(""
                + userProfileModel.getMobileUserProfileId());
    }

    public UserProfileModel getUserProfilePref() {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        UserProfileModel userProfileModel = new Gson().fromJson(
                settings.getString(USER_PROFILE, ""), UserProfileModel.class);
        return userProfileModel;
    }


    public void setShopUserProfile(UserProfileModel userProfileModel) {
        Log.v(MainActivity.TAG, "Saving user profile for shop");
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(SHOP_USER_PROFILE, new Gson().toJson(userProfileModel));
        editor.commit();
    }

    public UserProfileModel getShopUserProfile() {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        UserProfileModel userProfileModel = new Gson().fromJson(
                settings.getString(SHOP_USER_PROFILE, ""), UserProfileModel.class);
        return userProfileModel;
    }




    public void setUserAccountPrefs(UserAccountModel userAccountModel) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(USER_ACCOUNT, new Gson().toJson(userAccountModel).toString());
        editor.commit();
        LivePhoneReadings.setAccountId("" + userAccountModel.getAccountId());
    }

    public UserAccountModel getUserAccountPref() {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        UserAccountModel userAccountModel = new Gson().fromJson(
                settings.getString(USER_ACCOUNT, ""), UserAccountModel.class);
//		//Log.i("PREFFFFFFFFFFFFFFFFS user account","getting user account model from pref");
//		//Log.i("prefs account id", "account id = "+userAccountModel.getAccountID());
//		//Log.i("prefs account id", "account full name = "+userAccountModel.getAccountFullName());
        return userAccountModel;
    }

    public void setVehicleProfilePrefs(VehicleProfileModel vehicleProfileModel) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        //Log.dMainActivity.TAG, "5");
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(VEHICLE_PROFILE, new Gson().toJson(vehicleProfileModel).toString());
        //Log.dMainActivity.TAG, "6");
        editor.commit();
        LivePhoneReadings.setYear("" + vehicleProfileModel.getYear());
        LivePhoneReadings.setMake(vehicleProfileModel.getMake());
        LivePhoneReadings.setModel(vehicleProfileModel.getModel());
        LivePhoneReadings.setMileage(vehicleProfileModel.getMileage());
        LivePhoneReadings.setVinRetrievable(vehicleProfileModel.getVinRetrievable());
        //Log.dMainActivity.TAG, "7");
        if (vehicleProfileModel.getVIN().equals("")) {
            vehicleProfileModel.setVIN(vehicleProfileModel.getMake() + " " + vehicleProfileModel.getModel() + " " + vehicleProfileModel.getYear() + " " + vehicleProfileModel.getBtID());
            //Log.dMainActivity.TAG, "8");
        } else {
            LivePhoneReadings.setVin(vehicleProfileModel.getVIN());
            //Log.dMainActivity.TAG, "9");
            //Log.dMainActivity.TAG, "VIN = "+LivePhoneReadings.getVin());
        }

        if (!vehicleProfileModel.getVINID().isEmpty()) {
            LivePhoneReadings.setVinId(Integer.parseInt(vehicleProfileModel.getVINID()));
        }

        ApplicationPrefs.getInstance(BluetoothApp.context).setLastConnectedVinId(LivePhoneReadings.vinId);
    }

    public void clearVehicleProfilePrefs() {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(VEHICLE_PROFILE, "");
        editor.commit();



        ApplicationPrefs.getInstance(BluetoothApp.context).setLastConnectedVinId(0);
    }

    public VehicleProfileModel getVehicleProfilePref() {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        VehicleProfileModel vehicleProfileModel = new Gson().fromJson(
                settings.getString(VEHICLE_PROFILE, ""),
                VehicleProfileModel.class);
        return vehicleProfileModel;
    }

    public void setLoggedInPrefs(boolean isLoggedIn) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(LOGGED_IN, isLoggedIn);
        editor.commit();
    }

    public boolean getIsLoggedInPref() {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getBoolean(LOGGED_IN, false);
    }

    public void setIsQuickBloxUserCreate(boolean isQuickBloxUserCreate) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(IS_QUICKBLOX_USER_CREATED, isQuickBloxUserCreate);
        editor.commit();
    }

    public boolean getIsQuickBloxUserCreate() {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getBoolean(IS_QUICKBLOX_USER_CREATED, false);
    }


    // generic method for saving a bool to preferences
    public void setBooleanPref(String pref, boolean val) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(pref, val);
        editor.commit();
    }

    public boolean getBooleanPref(String pref) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getBoolean(pref, false);
    }

    // generic method for saving a bool to preferences
    public void setStringPref(String pref, String val) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(pref, val);
        editor.commit();
    }

    // generic method for saving a bool to preferences
    public void setDatePref(String pref, Date date) {
        setStringPref(pref, null);
    }

    // generic method for saving a int to preferences
    public void setIntPref(String pref, Integer val) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(pref, val);
        editor.commit();
    }

    public void updateProfiles() {
        UserAccountModel userAccountModel = getUserAccountPref();
        UserProfileModel userProfileModel = getUserProfilePref();
        VehicleProfileModel vehicleProfileModel = getVehicleProfilePref();

        if (vehicleProfileModel != null) {
            LivePhoneReadings.setYear("" + vehicleProfileModel.getYear());
            LivePhoneReadings.setMake(vehicleProfileModel.getMake());
            LivePhoneReadings.setModel(vehicleProfileModel.getModel());
            LivePhoneReadings.setMileage(vehicleProfileModel.getMileage());
            LivePhoneReadings.setVin(vehicleProfileModel.getVIN());
            if (!vehicleProfileModel.getVINID().isEmpty()) {
                LivePhoneReadings.setVinId(Integer.parseInt(vehicleProfileModel.getVINID()));
            }
        }

        if (userProfileModel != null) {
            LivePhoneReadings.setMobileUserProfileId(""
                    + userProfileModel.getMobileUserProfileId());
        }

        if (userAccountModel != null) {
            LivePhoneReadings.setAccountId("" + userAccountModel.getAccountId());
        }


        userAccountModel = null;
        userProfileModel = null;
        vehicleProfileModel = null;
    }

    public void setVehicleHealthValuePref(int vehicleHealthValue) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(VEHICLE_HEALTH_VALUE, vehicleHealthValue);
        editor.commit();
        LivePhoneReadings.setVehicleHealthValue(vehicleHealthValue);
    }

    public void setVehicleHealthMessagePref(String vehicleHealthMessage) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(VEHICLE_HEALTH_MESSAGE, vehicleHealthMessage);
        editor.commit();
        LivePhoneReadings.setVehicleHealthMessage(vehicleHealthMessage);
    }

    public int getVehicleHealthValuePref() {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getInt(VEHICLE_HEALTH_VALUE, 100);
    }

    public String getVehicleHealthMessagePref() {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getString(VEHICLE_HEALTH_MESSAGE, "");
    }

    public void setLastUpdatePref(int lastUpdateDate) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(LAST_UPDATE_DAY, lastUpdateDate);
        editor.commit();
    }

    public int getLastUpdatePref() {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getInt(LAST_UPDATE_DAY, 0);
    }

    public boolean isAppRequireToUpdateDailtyProfiles() {
        return getLastUpdatePref() != Calendar.DAY_OF_MONTH;
    }

    public void setGcmRegisteredAppVersion(int appVersion) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(APP_VERSION, appVersion);
        editor.commit();
    }

    public int getGcmRegisteredAppVersion() {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getInt(APP_VERSION, 0);
    }


    public void setGcmRegistrationId(String registrationId) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(GCM_REGISTRATION_ID, registrationId);
        editor.commit();
    }

    public String getRegistrationId() {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getString(GCM_REGISTRATION_ID, "");
    }

    public void setIsClearTroubleCodeRequired(boolean isClearTroubleCodeRequired) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(IS_CLEAR_TROUBLE_CODE_REQUIRED, isClearTroubleCodeRequired);
        editor.commit();
    }

    public boolean isClearTroubleCodeRequired() {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getBoolean(IS_CLEAR_TROUBLE_CODE_REQUIRED, false);
    }

    public void setBluetoothNotification(boolean isNotifyBluetoothRequired) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(BT_NOTIFICATION, isNotifyBluetoothRequired);
        //Log.dMainActivity.TAG, "Bluetooth Notification is set to: " + isNotifyBluetoothRequired);
        editor.commit();
    }

    public boolean isBluetoothNotificationOn() {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getBoolean(BT_NOTIFICATION, true);
    }


    public void setBluetoothSnoozeRequired(boolean isBluetoothNotificationSnoozeRequired) {
        //SAEED
        if (context !=null) {
            SharedPreferences settings = context
                    .getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(IS_BT_SNOOZE_REQUIRED, isBluetoothNotificationSnoozeRequired);
            //Log.dMainActivity.TAG, "set Is Bluetooth notification Snooze to: " + isBluetoothNotificationSnoozeRequired);
            editor.commit();
        }
    }

    public boolean isBluetoothSnoozeRequired() {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getBoolean(IS_BT_SNOOZE_REQUIRED, false);
    }

    public void setEnableNotificationTime(long enableNotificationTime) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(ENABLE_NOTIFICATION_TIME, enableNotificationTime);
        editor.commit();
    }

    public long getEnableNotificationTime() {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getLong(ENABLE_NOTIFICATION_TIME, 0);
    }

    public void setAutoPairWithObd2(boolean isAutoPairObd2) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(AUTO_PAIR_WITH_OBD2, isAutoPairObd2);
        editor.commit();
    }

    public boolean getAutoPairWithObd2() {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getBoolean(AUTO_PAIR_WITH_OBD2, false);
    }

    public void addObd2Device(String obdName) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        String obdsArrayString = settings.getString(OBD_DEVICES_ARRAY, "");
        if (!obdsArrayString.contains(obdName)) {
            obdsArrayString += obdName + ",";
        }
        editor.putString(OBD_DEVICES_ARRAY, obdsArrayString);
        editor.commit();
    }

    public String getObd2Devices() {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getString(OBD_DEVICES_ARRAY, "");
    }

    public int getLastConnectedVinId() {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getInt(LAST_CONNECTED_VIN_ID, -1);
    }

    public void setLastConnectedVinId(int vinId) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(LAST_CONNECTED_VIN_ID, vinId);
        //Log.dMainActivity.TAG, "is set to " + vinId);
        editor.commit();
    }

    public double getLastSynchDate() {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getLong(LAST_SYNCH_DATE, 0);
    }

    public void setLastSynchDate(long lastSynchDate) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(LAST_SYNCH_DATE, lastSynchDate);
        editor.commit();
    }

    public double getLastTimeCompetitorListUpdated() {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getLong(LAST_COMPETITOR_UPDATE, 0);
    }

    public void setLastCompetitorUpdate(long lastCompetitorUpdate) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(LAST_COMPETITOR_UPDATE, lastCompetitorUpdate);
        editor.commit();
    }


    public boolean getIsAnsweringACall() {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getBoolean(IS_ANSWERING_A_CALL, false);
    }

    public void setIsAnsweringACall(boolean isAnsweringACall) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(IS_ANSWERING_A_CALL, isAnsweringACall);
        editor.commit();
    }


    public void setEmailUserBeenReset(String email) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(EMAIL_FOR_PASSWORD_BEEN_RESET, email);
        editor.commit();
    }

    public String getEmailUserBeenReset() {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getString(EMAIL_FOR_PASSWORD_BEEN_RESET, "");
    }


    public void setCompetitorModelString(String competitorModelString) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(COMPETITOR_LIST_OBJECT, competitorModelString);
        editor.commit();
    }

    public String getCompetitorModelString() {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getString(COMPETITOR_LIST_OBJECT, "");
    }


    public void setCompetitorGeofenceId(int competitorGeofenceCampaignId) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(COMPETITOR_GEOFENCE_ID, competitorGeofenceCampaignId);
        editor.commit();
    }

    public int getCompetitorGeofenceId() {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getInt(COMPETITOR_GEOFENCE_ID, -1);
    }

    public void setCompetitorGeofenceIncidentMinutes(int competitorGeofenceCampaignId) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(COMPETITOR_GEOFENCE_CAMPAIGN_MINUTES, competitorGeofenceCampaignId);
        editor.commit();
    }

    public int getCompetitorGeofenceMinutes() {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getInt(COMPETITOR_GEOFENCE_CAMPAIGN_MINUTES, -1);
    }

    public boolean isUserInsideCompetitorGeoFence() {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getBoolean(USER_INSIDE_COMPETITOR_GEOFENCE, false);
    }

    public void setIsUserInsideCompetitorGeoFence(boolean userInsideCompetitorGeoFence) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(USER_INSIDE_COMPETITOR_GEOFENCE, userInsideCompetitorGeoFence);
        editor.commit();
    }


    public void setCompetitorGeofenceIncidentId(int competitorGeofenceIncidentId) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(COMPETITOR_GEOFENCE_INCIDENT_ID, competitorGeofenceIncidentId);
        editor.commit();
    }

    public int getCompetitorGeofenceIncidentId() {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getInt(COMPETITOR_GEOFENCE_INCIDENT_ID, -1);
    }


    public double getLastTimeInCompetitorLocation() {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getLong(LAST_TIME_IN_COMPETITOR_LOCATION, -1);
    }

    public void setLastTimeInCompetitorLocation(long lastTimeInCompetitorLocation) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(LAST_TIME_IN_COMPETITOR_LOCATION, lastTimeInCompetitorLocation);
        editor.commit();
    }


    public int getLastGeoFenceId() {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getInt(LAST_GEOFENCE_ID, -1);
    }

    public void setLastGeofenceId(int lastGeofenceId) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(LAST_GEOFENCE_ID, lastGeofenceId);
        editor.commit();
    }

    public boolean isReceivePushNotificationsForSafetyCheck() {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getBoolean(IS_SAFETY_CHECK_PUSH_NOTIFICATIONS_ENABLED, false);
    }

    public void setIsReceivePushNotificationsForSafetyCheck(boolean receivePushNotificationForSafetyCheck) {
        Log.v("CheckBox Clicked","PUSH to "+receivePushNotificationForSafetyCheck);
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(IS_SAFETY_CHECK_PUSH_NOTIFICATIONS_ENABLED, receivePushNotificationForSafetyCheck);
        editor.commit();
    }

    public boolean isReceiveEmailNotificationsForSafetyCheck() {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getBoolean(IS_SAFETY_CHECK_EMAIL_NOTIFICATIONS_ENABLED, false);
    }

    public void setIsReceiveEmailNotificationsForSafetyCheck(boolean receiveEmailNotificationForSafetyCheck) {
        Log.v("CheckBox Clicked","EMAIL to "+receiveEmailNotificationForSafetyCheck);
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(IS_SAFETY_CHECK_EMAIL_NOTIFICATIONS_ENABLED, receiveEmailNotificationForSafetyCheck);
        editor.commit();
    }

    public boolean isReceiveSMSNotificationsForSafetyCheck() {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getBoolean(IS_SAFETY_CHECK_SMS_NOTIFICATIONS_ENABLED, false);
    }

    public void setIsReceiveSMSNotificationsForSafetyCheck(boolean receiveSMSNotificationForSafetyCheck) {
        Log.v("CheckBox Clicked","SMS to "+receiveSMSNotificationForSafetyCheck);
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(IS_SAFETY_CHECK_SMS_NOTIFICATIONS_ENABLED, receiveSMSNotificationForSafetyCheck);
        editor.commit();
    }



    public void setSafetyCheckProgramName(String safetyCheckProgramName) {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(SafetyCheckProgramName, safetyCheckProgramName);
        editor.commit();
    }

    public String getSafetyCheckProgramName() {
        SharedPreferences settings = context
                .getSharedPreferences(PREFS_NAME, 0);
        return settings.getString(SafetyCheckProgramName, "");
    }

}



