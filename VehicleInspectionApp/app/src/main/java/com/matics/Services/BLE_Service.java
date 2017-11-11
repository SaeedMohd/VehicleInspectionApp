package com.matics.Services;

import java.util.Timer;

import com.matics.Bluetooth.BluetoothApp;
import com.matics.MainActivity;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class BLE_Service extends Service{
	
   public static Context con;
   Timer connectTextTimer;
   public static boolean boolService=false;
   
    public class LocalBinder extends Binder {
    	BLE_Service getService() {
            return BLE_Service.this;
        }
    }

    public void onDestroy() {
    	BluetoothApp.cancelNotification(BLE_Service.this);
		super.onDestroy();
	}
    
    @Override
    public void onCreate() {
    	//Log.dMainActivity.TAG, "BLE_Service");
    	con=BLE_Service.this;    	
    	
    }

  
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.dMainActivity.TAG, "Received start id " + startId + ": " + intent);
        
        Thread thread = new Thread()
		{
		    @Override
		    public void run() {
		        try {
		        	
		        	BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		            
		                sleep(1000);
		                try {
		                	 mBluetoothAdapter.disable(); 
		                	 //Log.dMainActivity.TAG, "BLE_Service disable");
		                	 sleep(5000);
		                	 
		                	 mBluetoothAdapter.enable(); 
		                	 //Log.dMainActivity.TAG, "BLE_Service enable");
		                } catch (Exception e) {
		                    e.printStackTrace();
		                    //Log.dMainActivity.TAG, "BLE_Service thread interrupted");
		                }
		           
		        } catch (InterruptedException e) {
		            e.printStackTrace();
		        }
		    }
		};

		thread.start();  
        
       
        
        return START_NOT_STICKY;
    }

 

	@Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    private final IBinder mBinder = new LocalBinder();

	
}