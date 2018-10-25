package com.inspection.GCM;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import com.inspection.R;
import com.inspection.Utils.ApplicationPrefs;

import java.io.IOException;

/**
 * Created by devsherif on 1/26/15.
 */
public class GcmRegistration {

    private final String TAG = "GcmRegistration::";
    public static final String EXTRA_MESSAGE = "message";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private final String GCM_URL = "http://www.jet-matics.com/JetComService/JetCom.svc/GCMMobileUserProfile?GCM_Id=%s&MobileUserProfileId=%s&Phone_ID=%s";

    private Context context;
//    private GoogleCloudMessaging gcm;
    private String regid;




    public GcmRegistration(Context context){
        this.context = context;
        if (checkPlayServices()) {
//            gcm = GoogleCloudMessaging.getInstance(context);
//            regid = getRegistrationId(context);
//            //Log.dMainActivity.TAG, "reg id saved is: " + regid);
//            //if (regid.isEmpty()) {
//                //Log.dMainActivity.TAG, "regid is empty. start registering with GCM");
//                new RegisterInBackground().execute();
//            // }
        }
        //Log.dMainActivity.TAG, "Initializing GcmBroadcastReceiver");
        if(!GcmBroadcastReceiver.isStarted) {
            new GcmBroadcastReceiver();
        }
    }



    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity)context,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                //Log.dMainActivity.TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    private String getRegistrationId(Context context) {
        String registrationId = ApplicationPrefs.getInstance(context).getRegistrationId();
        if (registrationId.isEmpty()) {
            //Log.dMainActivity.TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = ApplicationPrefs.getInstance(context).getGcmRegisteredAppVersion();
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            //new UnRegisterGcmId().execute();
            //Log.dMainActivity.TAG,"app is updated, GCM Registration Id should get updated");
            return "";
        }
        return registrationId;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }



    private class RegisterInBackground extends AsyncTask<String, String, String> {

        protected String doInBackground(String... params) {
            String msg = "";
            //Log.dMainActivity.TAG,"starting.....");
//            try {
//                //if (gcm == null) {
//                    gcm = GoogleCloudMessaging.getInstance(context);
//                //}
//                regid = gcm.register(context.getString(R.string.push_notifications_project_name));
//                msg = "Device registered, registration ID=" + regid;
//                //Log.dMainActivity.TAG,msg);
//                // You should send the registration ID to your server over HTTP,
//                // so it can use GCM/HTTP or CCS to send messages to your app.
//                // The request to your server should be authenticated if your app
//                // is using accounts.
//
//
////                sendRegistrationIdToBackend();
//
//
//                // For this demo: we don't need to send it because the device
//                // will send upstream messages to a server that echo back the
//                // message using the 'from' address in the message.
//
//                // Persist the regID - no need to register again.
//                storeRegistrationId(context, regid);
//            } catch (IOException ex) {
//                msg = "Error :" + ex.getMessage();
//                //Log.dMainActivity.TAG,msg);
//                // If there is an error, don't just keep trying to register.
//                // Require the user to click a button again, or perform
//                // exponential back-off.
//            }
            return msg;
        }
    }





    private void storeRegistrationId(Context context, String regId) {
        int appVersion = getAppVersion(context);
        //Log.dMainActivity.TAG, "Saving regId on app version " + appVersion);
        ApplicationPrefs.getInstance(context).setGcmRegistrationId(regId);
        ApplicationPrefs.getInstance(context).setGcmRegisteredAppVersion(appVersion);
    }

}
