package com.inspection.GCM;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import com.inspection.MainActivity;
import com.inspection.inspection.R;
import com.inspection.Utils.Utility;
import com.inspection.Services.VideoCallingService;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    Bitmap thumbImageBitMap;
    NotificationCompat.Builder builder;
    private static final String TAG = "GcmIntentService";

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        //Log.dMainActivity.TAG, "Message received: " + messageType);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.toString());
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " +
                        extras.toString());
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {

                //Log.dTAG, "message resieved is: " + extras.keySet().toString());


                //GcmMessageModel gcmMessageModel = new Gson().fromJson(extras.getString("message"),GcmMessageModel.class);
                String type = extras.getString("type");
                if (type == null) {
                    return;
                }

                if (type.equals("ClearTroubleCode")) {

                    //Log.dMainActivity.TAG, "Setting clear trouble code to true");
//                    ApplicationPrefs.getInstance(BluetoothApp.context).setIsClearTroubleCodeRequired(true);

                } else if (type.equals("ScheduleAppointment")) {

                    //Log.dMainActivity.TAG, "Schedule Appointment received");
                    String appointmentTitle = extras.getString("title");
                    String appointmentDescription = extras.getString("message");
                    String appointmentDate = extras.getString("date");
                    String location = extras.getString("location");
                    int firstReminderType = Integer.parseInt(extras.getString("firstReminderType"));
                    String firstReminderDate = extras.getString("firstReminderDate");
                    int secondReminderType = -1;
                    String secondReminderDate = "";

                    if (extras.containsKey("secondReminderType") && extras.getString("secondReminderType").length() > 0) {
                        secondReminderType = Integer.parseInt(extras.getString("secondReminderType"));
                        secondReminderDate = extras.getString("secondReminderDate");
                    }

                    Utility.addCalendarEvent(appointmentTitle, appointmentDescription, appointmentDate, firstReminderType, firstReminderDate, secondReminderType, secondReminderDate, location);
                    appointmentDate = null;
                    appointmentDescription = null;
                    appointmentTitle = null;
                    location = null;
                } else if (type.equals("Notification")) {
                    String message = extras.getString("message");

                    if (extras.containsKey("image")) {
                        String thumbImageName = extras.getString("image");
                        //Log.dMainActivity.TAG, "" + thumbImageName);

                        thumbImageBitMap = getBitmapFromURL("" + thumbImageName);
                    }
                    // Post notification of received message.
                    sendNotification(message);
                } else if (type.equals("AddContact")) {
                    //Log.dMainActivity.TAG, "received");
                    String contactName = "";
                    String contactPhoneNumber = "";
                    String contactAddress = "";
                    String contactEmail = "";
                    String imageURL = "";

                    contactName = extras.getString("contactName");
                    contactPhoneNumber = extras.getString("contactPhoneNumber");
                    contactAddress = extras.getString("contactAddress");
                    contactEmail = extras.getString("contactEmail");
                    imageURL = extras.getString("image");

                    //Log.dMainActivity.TAG, "contactName = " + contactName);
                    //Log.dMainActivity.TAG, "contactPhoneNumber= " + contactPhoneNumber);
                    //Log.dMainActivity.TAG, "contactAddress = " + contactAddress);
                    //Log.dMainActivity.TAG, "contactEmail = " + contactEmail);
                    //Log.dMainActivity.TAG, "imageURL = " + imageURL);

                    if (!contactName.isEmpty() && !contactPhoneNumber.isEmpty()) {
                        //Log.dMainActivity.TAG, "let's add your contact");
                        Utility.addContact(contactName, contactPhoneNumber, contactAddress, contactEmail, imageURL);
                    }
                } else if (type.equals("CallRequested")) {
                    Handler mHandler = new Handler(getMainLooper());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!VideoCallingService.getInstance(MainActivity.mContext).isConnected()) {
                                VideoCallingService.getInstance(MainActivity.mContext).loginToVideoCallService();
                            }
                        }
                    });

//                    try {
//                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//                        r.play();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    // Post notification of received message.

                }else if (type.equals("EnableVideoRequest")){
                    VideoCallingService.getInstance(MainActivity.mContext).opponentVideoEnabled();
                }else if (type.equals("DisableVideoRequest")){
                    VideoCallingService.getInstance(MainActivity.mContext).opponentVideoDisabled();
                }

                //Log.dTAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();


        Bitmap remote_picture = null;


//     // Create the style object with BigPictureStyle subclass.
//     NotificationCompat.Builder  builder = new
//             NotificationCompat.Builder(this);
//     builder.setBigContentTitle("Vehicle Health Monitor");
//     builddr.setSummaryText(msg);


        remote_picture = BitmapFactory.decodeResource(getResources(), R.drawable.maintenance_facility_icon);
        if (thumbImageBitMap != null) {
            remote_picture = thumbImageBitMap;
        }


        // Add the big picture to the style.
        //notiStyle.bigPicture(remote_picture);

        // Creates an explicit intent for an ResultActivity to receive.
        Intent resultIntent = new Intent(this, MainActivity.class);

        // This ensures that the back button follows the recommended
        // convention for the back key.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Adds the back stack for the Intent (but not the Intent itself).
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack.
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0, PendingIntent.FLAG_UPDATE_CURRENT);



        Notification myNotification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(remote_picture)
                .setContentIntent(resultPendingIntent)
                .setContentTitle("Vehicle Health Monitor")
                .setVibrate(new long[20])
                .setCategory(Notification.CATEGORY_CALL)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentText(msg)
                .setAutoCancel(false)
                .build();
        mNotificationManager.notify(NOTIFICATION_ID, myNotification);
//        }
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }
}