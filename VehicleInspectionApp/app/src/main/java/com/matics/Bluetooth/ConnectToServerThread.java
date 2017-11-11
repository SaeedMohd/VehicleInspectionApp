package com.matics.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.matics.Utils.ApplicationPrefs;

import java.io.IOException;

public class ConnectToServerThread extends Thread {

	public BluetoothSocket bluetoothSocket;
	private BluetoothAdapter bluetoothAdapter;
	private BluetoothDevice bluetoothDevice;
	private BluetoothSocket tmp;

	public ConnectToServerThread(BluetoothDevice device,
			BluetoothAdapter btAdapter) {
		// BluetoothSocket tmp = null;
		bluetoothAdapter = btAdapter;
		bluetoothDevice = device;

	}

	public void run() {
		// ---cancel discovery because it will slow down the
		// connection---
		//bluetoothAdapter.cancelDiscovery();

		try {

			tmp = bluetoothDevice.createRfcommSocketToServiceRecord(BluetoothApp.uuid);
			bluetoothSocket = tmp;
			// ---connect the device through the socket. This will
			// block until it succeeds or throws an exception---

//			//Log.i("Bluetooth", "Connecting with uuid: " + BluetoothApp.uuid.toString()
//					+ ", to device Name:" + bluetoothDevice.getAddress());

			bluetoothSocket.connect();
			// //Log.i("Bluetooth", "connected with uuid: " +
			// uuidToTry.toString());
			BluetoothApp.isConnectionEstablished = true;

			// ---create a thread for the communication channel---

            //Saving Bluetooth device as OBD2 device
//			//Log.i("Device Naaaaame","Device name is: "+bluetoothDevice.getName());
            ApplicationPrefs.getInstance(BluetoothApp.context).addObd2Device(bluetoothDevice.getName());

			BluetoothApp.commsThread = new CommsThread(bluetoothSocket,
					BluetoothApp.uuid, bluetoothDevice.getAddress());
			BluetoothApp.commsThread.start();

		} catch (IOException connectException) {
			// ---unable to connect; close the socket and get out---
//			//Log.i("ConnectToServerThread::run::IOException", "IOException happened when trying to connecto using UUID: " + BluetoothApp.uuid
//					+ " try the next uuid");
//			//Log.i("ConnectToServerThread::run::IOException", connectException.getMessage());

			cancel();
			// return;
		}

	}

	public void cancel() {
		try {
			synchronized (this) {
				bluetoothSocket.close();
				BluetoothApp.isConnectionEstablished=false;
				if (BluetoothApp.commsThread != null) {
					BluetoothApp.commsThread.cancel();
				}
			}
		} catch (Exception e) {
//			//Log.i("ConnectToServerThread::cancel::IOException", e.getLocalizedMessage());
			BluetoothApp.isConnectionEstablished=false;
			if (BluetoothApp.commsThread != null) {
				BluetoothApp.commsThread.cancel();
			}
		}
	}
}