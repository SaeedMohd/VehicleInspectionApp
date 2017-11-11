package com.matics.Services;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.matics.Bluetooth.BluetoothApp;
import com.matics.MainActivity;
import com.matics.R;
import com.matics.Utils.ApplicationPrefs;
import com.matics.fragments.VideoCallFragments.IncomingCallFragment;

import java.util.HashMap;

/**
 * Created by devsherif on 3/10/15.
 */
public class NotificationIntentService extends IntentService {

    public NotificationIntentService() {
        super("NotificationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Log.dMainActivity.TAG, "heeeeeeeeeeere");
        if(intent.hasExtra(ApplicationPrefs.BT_NOTIFICATION)){
            ApplicationPrefs.getInstance(BluetoothApp.context).setBluetoothNotification(intent.getBooleanExtra(ApplicationPrefs.BT_NOTIFICATION, true));
            //Toast.makeText(BluetoothApp.context, "Bluetooth Notification disabled", Toast.LENGTH_LONG).show();
            //Log.dMainActivity.TAG, "we have it");
            NotificationManager mNotificationManager = (NotificationManager)
                    this.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(110);

        }

        if(intent.hasExtra(ApplicationPrefs.IS_BT_SNOOZE_REQUIRED)){
            ApplicationPrefs.getInstance(BluetoothApp.context).setBluetoothSnoozeRequired(true);
                    Intent bluetoothNotificationIntent = new Intent(this, MainActivity.class);
        bluetoothNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(bluetoothNotificationIntent);
        }


        if(intent.hasExtra(MainActivity.ANSWER_VIDEO_CALL)){
            Intent mainActivity = new Intent(this, MainActivity.class);
            mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ApplicationPrefs.getInstance(this).setIsAnsweringACall(true);
            startActivity(mainActivity);

            VideoCallingService.getInstance(this).callAcceptedGoToVideoCall();

            NotificationManager mNotificationManager = (NotificationManager)
                    this.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(1);
        }

        if(intent.hasExtra(MainActivity.INCOMING_CALL)){
            Intent mainActivity = new Intent(this, MainActivity.class);
            mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ApplicationPrefs.getInstance(this).setIsAnsweringACall(true);
            startActivity(mainActivity);

            Fragment fragmentVideoCall = new IncomingCallFragment();
            FragmentTransaction incomingCall = MainActivity.mContext.getFragmentManager().beginTransaction();
            incomingCall.add(R.id.fragment, fragmentVideoCall);//.addToBackStack("");
            incomingCall.commit();


//            VideoCallingService.getInstance(this).callAcceptedGoToVideoCall();

            NotificationManager mNotificationManager = (NotificationManager)
                    this.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(1);
        }


        if(intent.hasExtra(MainActivity.CANCEL_VIDEO_CALL)){

            VideoCallingService.getInstance(this).getCurrentSession().rejectCall(new HashMap<String, String>());

            //Log.dMainActivity.TAG, "rejecteddd");

            NotificationManager mNotificationManager = (NotificationManager)
                    this.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(1);
        }


    }
}
