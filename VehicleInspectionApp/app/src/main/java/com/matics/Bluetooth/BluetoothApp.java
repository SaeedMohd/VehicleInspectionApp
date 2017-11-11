package com.matics.Bluetooth;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

import com.matics.MainActivity;
import com.matics.R;

public class BluetoothApp extends Application {

        //This character specifies that this is the end of the message --BOTH
    public static char endOfMessageFlag = '^';

    //Flags --Slave
    public static boolean isRequestReplyReceived;
    //public static boolean isHeartBeatReplied; --To be removed
    public static boolean isUpdatedOrdersReceived;
    public static ConnectToServerThread connectToServerThread; //--SLAVE
    public static CommsThread commsThread;

    public static boolean isOBD2LockingPreventionRequired=true;
    
    //public static UUID uuid = UUID.randomUUID();
    
    public static UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //All the Bluetooth communication threads are maintained by Bluetooth --BOTH
	public static SearchForMasterThread searchForMasterThread; //--SLAVE
    public static ConnectionManagerThread connectionManagerThread;  //--BOTH
    public static BluetoothDeviceReadings bluetoothDeviceReadings;
    public static BluetoothCommandsCommunicationThread bluetoothCommandsCommunicationThread;

    //Communication Handler --BOTH
    public static Handler UIupdater = null;
    
    public static boolean stillSearching=false;

    //private static BTDevice btDevice;
    public static boolean isConnectionEstablished;
    public static String lastKnownMasterMacAddress="";

	public static Context context;
    
    public static void startConnectionManager() {
    	if(BluetoothApp.connectionManagerThread == null){
        BluetoothApp.connectionManagerThread = new ConnectionManagerThread();
        BluetoothApp.connectionManagerThread.start();
    	}else{
    		////Log.i("Connection manager already started","Connection manager already working");
    	}
    }
   

//    public static void removeDisconnectedSlave(String uuidString) {
//        for (int x =0;x<slaveBTDevicesArray.size();x++){
//            if (slaveBTDevicesArray.get(x).getUuid().toString().equals(uuidString)){
//                slaveBTDevicesArray.remove(x);
//                ApplicationConfig.writeLog('v', "REMOVING Disconnected Slave", "was holding uuid: " + uuidString);
//            }
//        }
//    }
//

        //Writing message to server socket task
    //--Slave
//    private static class  WriteTask extends AsyncTask<String, Void, Void> {
//        protected Void doInBackground(String... args) {
//            try {
//                //ApplicationConfig.writeLog('v', "foat", "write task running");
//                connectToServerThread.commsThread.write(args[0]);
//            } catch (Exception e) {
//                //ApplicationConfig.writeLog('v',"MainActivity", e.getLocalizedMessage());
//            }
//            return null;
//        }
//    }

    //Writing messgae to slave socket task
    //--MASTER
//    private static class ServerWriteTask extends AsyncTask<String, Void, Void> {
//        protected Void doInBackground(String... args) {
//            try {
//                ApplicationConfig.writeLog('v', "foat", "write task running");
//                serverThread.commsThread.write(args[0]);
//            } catch (Exception e) {
//                //ApplicationConfig.writeLog('v',"MainActivity", e.getLocalizedMessage());
//            }
//            return null;
//        }
//    }

    //--BOTH
    public static boolean isBluetoothOn(Context context) {
        boolean isBluetoothOn;
        if (BluetoothAdapter.getDefaultAdapter() == null) {
            isBluetoothOn = false;
            //ApplicationConfig.showDebugToast(context, "Device does not support bluetooth", Toast.LENGTH_SHORT);
        } else {
            if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                isBluetoothOn = false;
                //ApplicationConfig.showDebugToast(context, "Bluetooth is Off", Toast.LENGTH_SHORT);
            } else {
                isBluetoothOn = true;
            }
        }
        return isBluetoothOn;
    }

    //--BOTH
    public static String getBluetoothDeviceName(){

        return BluetoothAdapter.getDefaultAdapter().getName();
    }


    //--BOTH
    public static BluetoothDevice getBluetoothDeviceFromBluetoothDeviceName(String bluetoothDeviceName, ArrayList<BluetoothDevice> pairedDevices) {
        BluetoothDevice requiredBluetoothDevice = null;
        for (BluetoothDevice btDevice : pairedDevices) {
            if (btDevice.getName().equals(bluetoothDeviceName)) {
                requiredBluetoothDevice = btDevice;
                return requiredBluetoothDevice;
            }
        }
        return requiredBluetoothDevice;
    }





    //--BOTH
    public static ArrayList<String> getBluetoothDevicesNamesFromBluetoothDevicesList(ArrayList<BluetoothDevice> bluetoothDevices){
        ArrayList<String> devicesNames = new ArrayList<String>();
        for ( BluetoothDevice bd : bluetoothDevices){
            devicesNames.add(bd.getName());
        }
        return devicesNames;
    }


    //--BOTH
    //---find all the previously paired devices---
    public static ArrayList<BluetoothDevice> QueryPairedDevices(BluetoothAdapter bluetoothAdapter) {
        ArrayList<BluetoothDevice> pairedDevices = new ArrayList<BluetoothDevice>();
        Set<BluetoothDevice> allPairedDevices =
                bluetoothAdapter.getBondedDevices();

        //---if there are paired devices---
        if (allPairedDevices.size() > 0) {
            //---loop through paired devices---

            for (BluetoothDevice device : allPairedDevices) {
                //---add the name and address to an array adapter
                // to show in a ListView---
                pairedDevices.add(device);

            }
        }
        return pairedDevices;
    }
    
    public static void setApplicationContext(Context context){
    	BluetoothApp.context = context;
    }

	public static void showNotification(Context context) {
		// TODO Auto-generated method stub
		try {
			Intent intent = new Intent(context,MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
			NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
			.setContentTitle("Vehical Health")
			.setContentText("Running")
			.setSmallIcon(R.drawable.app_icon);
		    builder.setContentIntent(pIntent);
		    builder.setOngoing(true);
		    builder.build().flags |= Notification.FLAG_AUTO_CANCEL;
		    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		    mNotificationManager.notify(100, builder.build());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public static void showFualNotification(Context context) {
		// TODO Auto-generated method stub
		try {
			Intent intent = new Intent(context,MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
			NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
			.setContentTitle("Vehical Health")
			.setContentText("Did you just add fuel?  If so please enter your vehicle's current milage to accurately calculate performance.")
			.setSmallIcon(R.drawable.ic_launcher);
		    builder.setContentIntent(pIntent);
		    builder.setOngoing(true);
		    builder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
		    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		    mNotificationManager.notify(300, builder.getNotification());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public static void showAlertNotification(Context context) {
		// TODO Auto-generated method stub
		try {
			Intent intent = new Intent(context,MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
			NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
			.setContentTitle("Vehical Health")
			.setContentText("Bluetooth is not Connected.")
			.setSmallIcon(R.drawable.ic_launcher);
		    builder.setContentIntent(pIntent);
		    builder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
		    NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		    mNotificationManager.notify(100, builder.getNotification());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	
		
	}
	
	public static void cancelNotification(Context context) {
		// TODO Auto-generated method stub
		try {
			if (context != null){
			NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.cancel(100);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}


}
