package com.matics.Utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.RingtoneManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.matics.Bluetooth.BluetoothApp;
import com.matics.LivePhoneReadings;
import com.matics.MainActivity;
import com.matics.R;
import com.matics.Services.VideoCallingService;
import com.matics.adapter.DataBaseHelper;
import com.matics.model.AccelerationModel;
import com.matics.Services.NotificationIntentService;
import com.matics.model.CompetitorModel;
import com.matics.serverTasks.GetCompetitorsListTask;
import com.matics.serverTasks.UpdateCompetitorGeoFenceTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class AccelerometerTracker extends Service implements SensorEventListener {
    private static AccelerometerTracker accelerometerTracker;
    //private  Context mContext;
    private NotificationManager mNotificationManager;
    private SensorManager mSensorManager;
    private Sensor accelerationSensor;
    private GPSTracker gpsTracker;
    private DataBaseHelper dbHelper;
    private DecimalFormat df = new DecimalFormat("###.##");
    final float alpha = 0.8f;
    float[] gravity;
    boolean isDisplayed = false;
    private Timer bluetoothTimer;
    private Date lastTimeQuickBloxChecked;
    private int quickBloxCheckIntervalLimit = 1000;
    private Timer gpsTimer;
    private boolean isGpsOn = false;
    private Context context;

    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.dMainActivity.TAG, "****************");
        context = this;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gravity = new float[3];
        ApplicationPrefs.getInstance(this).updateProfiles();
        dbHelper = new DataBaseHelper(this);
        gpsTracker = new GPSTracker(this);
        lastTimeQuickBloxChecked = new Date();
        BluetoothApp.setApplicationContext(this);
        if (gpsTimer == null){
            startTimerForGPS();
        }

//        VideoCallingService.getInstance(this).loginToVideoCallService();

        startListening();
        //Log.dMainActivity.TAG, "****************");
        return START_STICKY;
    }


    public void startListening() {
        mSensorManager.registerListener(this, accelerationSensor, 2 * 1000 * 1000);
        //mSensorManager.registerListener(this, accelerationSensor,SensorManager.SENSOR_DELAY_NORMAL);

    }

    public void stopListening() {
        mSensorManager.unregisterListener(this, accelerationSensor);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        float accelerometerX = event.values[0] - gravity[0];
        float accelerometerY = event.values[1] - gravity[1];
        float accelerometerZ = event.values[2] - gravity[2];

        LivePhoneReadings.accelerometerX = df.format(accelerometerX);
        LivePhoneReadings.accelerometerY = df.format(accelerometerY);
        LivePhoneReadings.accelerometerZ = df.format(accelerometerZ);

        if (accelerometerX > 2 || accelerometerY > 2 || accelerometerZ > 2) {
            if (ApplicationPrefs.getInstance(this).getIsLoggedInPref()) {


                if (new Date().getTime() - ApplicationPrefs.getInstance(this).getLastTimeCompetitorListUpdated() > (1000 * 60 * 60 * 24)) {
                    new GetCompetitorsListTask(this) {
                        @Override
                        public void onTaskCompleted(String result) {

                        }
                    }.execute();
                }

                if (new Date().getTime() - lastTimeQuickBloxChecked.getTime() > quickBloxCheckIntervalLimit) {
                    quickBloxCheckIntervalLimit = 20 * 1000;
                    lastTimeQuickBloxChecked = new Date();
                    //Log.dMainActivity.TAG, "checking if is connected or not");
                    if (BluetoothApp.context != null && !VideoCallingService.getInstance(this).isConnected()) {
                        lastTimeQuickBloxChecked = new Date();
                        VideoCallingService.getInstance(this).loginToVideoCallService();
                    }
                }
            }


            try {
                if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                    BluetoothApp.setApplicationContext(this);
                    ////Log.i("Accelerometer", "Phone moved, " + accelerometerX + ", " + accelerometerY + "," + accelerometerZ);
                    BluetoothApp.isOBD2LockingPreventionRequired = false;
                    if (ApplicationPrefs.getInstance(this).getIsLoggedInPref()) {
                        BluetoothApp.startConnectionManager();
                    }

                    if (BluetoothApp.isConnectionEstablished) {
                        AccelerationModel accelerationModel = new AccelerationModel();
                        accelerationModel.setMobileUserProfileID(Integer.parseInt(LivePhoneReadings.getMobileUserProfileId()));
                        accelerationModel.setVin(LivePhoneReadings.getVin());
                        accelerationModel.setAccelerationX(accelerometerX);
                        accelerationModel.setAccelerationX(accelerometerY);
                        accelerationModel.setAccelerationX(accelerometerZ);
                        accelerationModel.setLatitude(LivePhoneReadings.getLatitude());
                        accelerationModel.setLongitude(LivePhoneReadings.getLongitude());
                        dbHelper.addAcceleration(accelerationModel);
                        accelerationModel = null;

                        //Log.dMainActivity.TAG, "starting SynchData Thread");
                        if (!SynchDataWithServerThread.getInstance(this).isAlive()) {
                            //Log.dMainActivity.TAG, "it is not running, let's start it");
                            //SynchDataWithServerThread.isRunning = true;
                            SynchDataWithServerThread.getInstance(this).startSynchingThread(this);
                        }

                        gpsTracker.getLocation();

                    } else {
                        gpsTracker.stopUsingGPS();
                        //SynchDataWithServerThread.isRunning=false;
                        //SynchDataWithServerThread.getInstance(this).interrupt();

                        SynchDataWithServerThread.stopSynchingThread();
                    }

                } else {

                    if (!Utility.isAirplaneModeOn(this) && ApplicationPrefs.getInstance(this).isBluetoothNotificationOn() && ApplicationPrefs.getInstance(this).getEnableNotificationTime() < new Date().getTime()) {
                        if (!isDisplayed) {
                            isDisplayed = true;
                            waitSomeTimeBeforeCheckingBluetoothAgain();
                            showAlertDialogToEnableBluetooth();
                        }
                    }
                }

            } catch (Exception exp) {

            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    private void waitSomeTimeBeforeCheckingBluetoothAgain() {
        try {
            TimerTask task = new TimerTask() {
                public void run() {
                    isDisplayed = false;
                    MainActivity.isBluetoothEnableDenied = false;
                    //Log.dMainActivity.TAG, "bluetooth check is set to true again");
                }

            };
            if (bluetoothTimer == null) {
                bluetoothTimer = new Timer();
            }
            bluetoothTimer.schedule(task, 60 * 60 * 1000);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private void showAlertDialogToEnableBluetooth() {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 10, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent2 = new Intent(this, NotificationIntentService.class);
        intent2.putExtra(ApplicationPrefs.BT_NOTIFICATION, false);
        PendingIntent pIntent2 = PendingIntent.getService(this, 14, intent2, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent3 = new Intent(this, NotificationIntentService.class);
        intent3.putExtra(ApplicationPrefs.IS_BT_SNOOZE_REQUIRED, true);
        PendingIntent pIntent3 = PendingIntent.getService(this, 15, intent3, PendingIntent.FLAG_UPDATE_CURRENT);


        Notification myNotification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.app_icon)
                .setAutoCancel(true)
                //.setLargeIcon(remote_picture)
                .setContentIntent(pIntent)
                .setContentTitle("Vehicle Health Monitor")
                .setContentText("To keep Vehicle Health Monitor app working, Bluetooth should be enabled. Would you like to enable it now?")
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .addAction(0, "Enable Bluetooth", pIntent)
                //.addAction(0, "Don't Notify", pIntent2)
                .addAction(0, "Snooze Notification", pIntent3)
                .setStyle(new Notification.BigTextStyle().bigText("To keep Vehicle Health Monitor app working, Bluetooth should be enabled. Would you like to enable it now?")).build();
        mNotificationManager.notify(110, myNotification);
    }


    private void startTimerForGPS() {
        try {
            final Handler toastHandler = new Handler()
            {
                @Override
                public void handleMessage(Message msg)
                {
                    if (BluetoothApp.isConnectionEstablished) {
                        gpsTracker.getLocation();
                        updateCompetitorLogicWithLocation(gpsTracker.getLatitude(), gpsTracker.getLongitude());
                        //accelerometerTracker.startListening();
                    } else {
                        if (isGpsOn) {
                            updateCompetitorLogicWithLocation(gpsTracker.getLatitude(), gpsTracker.getLongitude());
                            gpsTracker.stopUsingGPS();
                            isGpsOn = false;
                        } else {
                            isGpsOn = true;
                            gpsTracker.getLocation();
                        }
                        // accelerometerTracker.stopListening();
                    }
                }
            };


            TimerTask task = new TimerTask() {
                public void run() {
                    try {
                        toastHandler.sendEmptyMessage(0);
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }

                }

            };
            if (gpsTimer == null) {
                gpsTimer = new Timer();
            }
            gpsTimer.schedule(task, 0, 10 * 1000);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }


    public void updateCompetitorLogicWithLocation(double latitude, double longitude) {
        float[] results = new float[1];
//        ApplicationPrefs.getInstance(mContext).setLastGeofenceId(-123);
//        Toast.makeText(context, "Let's check competitor locations and your location", Toast.LENGTH_SHORT).show();
        JSONObject object = null;
        JSONArray arr = null;
        try {
            object = new JSONObject(ApplicationPrefs.getInstance(context).getCompetitorModelString());
            arr = object.getJSONArray("GetCustLossPreventionCompListByMobileUserProfileIDResult");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList<CompetitorModel> competitorModels;
        try {
            competitorModels = new Gson().fromJson(arr.toString(), new TypeToken<ArrayList<CompetitorModel>>() {
            }.getType());
        }catch (NullPointerException nullExp){
            return;
        }
        if (competitorModels != null) {
            boolean isNowInsideCompetitor = false;
            outerLoop:
            for (CompetitorModel competitorModel : competitorModels) {
                ArrayList<CompetitorModel.CompetitorGeoFence> competitorGeoFences = competitorModel.getCompetitorGeoFence();
                for (CompetitorModel.CompetitorGeoFence competitorGeoFence : competitorGeoFences) {
                    Location.distanceBetween(latitude, longitude,
                            competitorGeoFence.getLatitude(), competitorGeoFence.getLongitude(), results);

                    if (results[0] < competitorGeoFence.getRadius()) {
                        isNowInsideCompetitor = true;
//                        Toast.makeText(this, "You are inside a competitor location", Toast.LENGTH_SHORT).show();
                        if (ApplicationPrefs.getInstance(this).getCompetitorGeofenceId() != competitorGeoFence.getGeoFenceID()) {
                            ApplicationPrefs.getInstance(this).setCompetitorGeofenceId(competitorGeoFence.getGeoFenceID());
                            if(ApplicationPrefs.getInstance(this).getLastGeoFenceId() != competitorGeoFence.getGeoFenceID()) {
//                                Toast.makeText(this, "This is a new GeoFence ID", Toast.LENGTH_SHORT).show();
                                ApplicationPrefs.getInstance(this).setCompetitorGeofenceId(competitorGeoFence.getGeoFenceID());
                                ApplicationPrefs.getInstance(this).setLastTimeInCompetitorLocation(new Date().getTime() - (5 * 60 * 60 * 1000));
                                ApplicationPrefs.getInstance(this).setCompetitorGeofenceIncidentMinutes(0);
                            }else{
//                                Toast.makeText(this, "This is the same geofence you were inside previously", Toast.LENGTH_SHORT).show();
                                ApplicationPrefs.getInstance(this).setCompetitorGeofenceId(competitorGeoFence.getGeoFenceID());
                                ApplicationPrefs.getInstance(this).setCompetitorGeofenceIncidentMinutes(ApplicationPrefs.getInstance(this).getCompetitorGeofenceMinutes() + 1);
                                ApplicationPrefs.getInstance(this).setIsUserInsideCompetitorGeoFence(true);
                            }
                        } else {
                            ApplicationPrefs.getInstance(this).setCompetitorGeofenceIncidentMinutes(ApplicationPrefs.getInstance(context).getCompetitorGeofenceMinutes() + 1);
//                            Toast.makeText(this, "You are inside this area since = "+ApplicationPrefs.getInstance(this).getCompetitorGeofenceMinutes()+" minutes", Toast.LENGTH_SHORT).show();
                            if (ApplicationPrefs.getInstance(this).getCompetitorGeofenceMinutes() >= competitorGeoFence.getGeoFenceMinutes() && !ApplicationPrefs.getInstance(this).isUserInsideCompetitorGeoFence()) {
//                                Toast.makeText(this, "Required minutes inside competitor area passed", Toast.LENGTH_SHORT).show();
                                ApplicationPrefs.getInstance(this).setIsUserInsideCompetitorGeoFence(true);
                                if (new Date().getTime() - ApplicationPrefs.getInstance(this).getLastTimeInCompetitorLocation() > (3 * 60 * 60 * 1000)) {
//                                    Toast.makeText(this, "It is the first time for you to be here since more than 3 hours, let's create instance", Toast.LENGTH_LONG).show();
                                    new UpdateCompetitorGeoFenceTask(this, competitorGeoFence.getGeoFenceID(), 0, ApplicationPrefs.getInstance(this).getUserProfilePref().getMobileUserProfileId(), -1) {
                                        @Override
                                        public void onTaskCompleted(String result) {
                                            if (result.contains("sucess")) {
                                                String incidentID = result.replace("{\"UpdateCompetitorGeoFenceResult\":{\"GeoIncidentID\":", "").replace(",\"Message\":\"Data inserted sucessfully.\"}}","");
                                                ApplicationPrefs.getInstance(context).setCompetitorGeofenceIncidentId(Integer.parseInt(incidentID));
//                                                Toast.makeText(context, "GeoFence incident is created with Incident id = "+incidentID, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }.execute();
                                }
                            }
                            break outerLoop;
                        }
                    }

                }
            }
            if (ApplicationPrefs.getInstance(this).isUserInsideCompetitorGeoFence() && !isNowInsideCompetitor) {
//                Toast.makeText(this, "now you are outside after being inside a competitor area", Toast.LENGTH_SHORT).show();
                ApplicationPrefs.getInstance(this).setLastTimeInCompetitorLocation(new Date().getTime());
                ApplicationPrefs.getInstance(this).setIsUserInsideCompetitorGeoFence(false);
                ApplicationPrefs.getInstance(this).setLastGeofenceId(ApplicationPrefs.getInstance(this).getCompetitorGeofenceId());
                new UpdateCompetitorGeoFenceTask(this, ApplicationPrefs.getInstance(this).getCompetitorGeofenceId(), ApplicationPrefs.getInstance(this).getCompetitorGeofenceMinutes(), ApplicationPrefs.getInstance(this).getUserProfilePref().getMobileUserProfileId(), ApplicationPrefs.getInstance(this).getCompetitorGeofenceIncidentId()) {
                    @Override
                    public void onTaskCompleted(String result) {
                        if (result.contains("sucess")) {
                            ApplicationPrefs.getInstance(context).setCompetitorGeofenceId(-1);
//                            Toast.makeText(context, "Competitor Location Campaign Updated with: " + ApplicationPrefs.getInstance(context).getCompetitorGeofenceMinutes() + " Minutes", Toast.LENGTH_SHORT).show();
                        }
                    }
                }.execute();
            }
        }
    }
}
