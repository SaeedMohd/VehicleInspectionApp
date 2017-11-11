package com.matics.Bluetooth;

import android.util.Log;

import com.matics.MainActivity;

public class BluetoothCommandsCommunicationThread extends Thread {

	public String rpm, temp, speed, fuel;
	
	
	public BluetoothCommandsCommunicationThread() {

	}

	public void run() {

		// Initializing obd command
		while (true) {
			try {
				if (BluetoothApp.isConnectionEstablished) {
					BluetoothApp.commsThread
							.write("TEST"+BluetoothCommands.RPM+"^");
					Thread.sleep(500);
					BluetoothApp.commsThread
							.write("TEST"+BluetoothCommands.COOLANT_TEMP+"^");
					Thread.sleep(500);
					BluetoothApp.commsThread
							.write("TEST"+BluetoothCommands.VEHICLE_SPEED+"^");
					Thread.sleep(500);
					BluetoothApp.commsThread
							.write("TEST"+BluetoothCommands.FUEL_LEVEL+"^");
					Thread.sleep(10*1000);
				} else {
					Thread.sleep(10*1000);

				}
			} catch (Exception exp) {
				exp.printStackTrace();
				//Log.dMainActivity.TAG,
//						"" + exp.getMessage());
			}
		}

	}

}
