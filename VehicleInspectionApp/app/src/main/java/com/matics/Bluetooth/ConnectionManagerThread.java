package com.matics.Bluetooth;

import com.matics.MainActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by devsherif on 4/21/14.
 */
public class ConnectionManagerThread extends Thread {

	static BluetoothAdapter bluetoothAdapter;
	// static ApplicationBase AppBase;
	public static boolean keepAlive;


	public ConnectionManagerThread() {
		keepAlive = true;
	}

	public void run() {
		//Log.dMainActivity.TAG, "Connection Manager Started");
		while (keepAlive) {
			synchronized (this) {
				try {
					
					
					
					if (BluetoothApp.isOBD2LockingPreventionRequired) {
						//Log.dMainActivity.TAG, "Phone is not moving, no need to connect");
						if(BluetoothApp.searchForMasterThread!=null){
							//Log.dMainActivity.TAG, "as phone is not moving, setting search for master thread class as null");
							BluetoothApp.searchForMasterThread=null;
						}
					}else{
						
						if(BluetoothApp.searchForMasterThread==null && !BluetoothApp.isConnectionEstablished){
							//Log.dMainActivity.TAG, "starting Setup Connection");
							setupCommunications();
						}else{
							if(!BluetoothApp.stillSearching){
								//Log.dMainActivity.TAG, "App finished searching , setting search for master as null");
								BluetoothApp.searchForMasterThread=null;
							}
						}
					}
			
//					} else if (!BluetoothApp.isConnectionEstablished
//							&& BluetoothApp.connectToServerThread == null) {
//						//Log.i("ConnectionManager::run",
//								"Start setup communications");
//						setupCommunications();
//					}
					this.wait(30 * 1000);
//					if (BluetoothApp.connectToServerThread != null
//							&& BluetoothApp.isConnectionEstablished) {
//						//Log.i("ConnectionManager::run",
//								"Connection established, set connect to server thread as null");
//						BluetoothApp.connectToServerThread = null;
//					}
				} catch (InterruptedException e) {
					e.printStackTrace();
					//Log.dMainActivity.TAG,
//							"Conneciton manager thread interrupted");
				}
			}
		}

	}

	public void setupCommunications() {
		// ApplicationConfig.showDebugToast(AppBase.AppContext,
		// "This is a slave device, Start searching for a Master",
		// Toast.LENGTH_SHORT);
		//Log.dMainActivity.TAG, "Let's start searching for a master");
		BluetoothApp.searchForMasterThread = new SearchForMasterThread(
				BluetoothAdapter.getDefaultAdapter(), null);
		BluetoothApp.searchForMasterThread.start();
	}

}
