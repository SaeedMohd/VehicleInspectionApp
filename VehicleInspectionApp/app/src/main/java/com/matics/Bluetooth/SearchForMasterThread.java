package com.matics.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import com.matics.MainActivity;
import com.matics.Utils.ApplicationPrefs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by devsherif on 4/16/14.
 */
public class SearchForMasterThread extends Thread {


    boolean discovering = false;
    BluetoothDevice newBtDeviceFound;
	ArrayList<BluetoothDevice> pairedDevices;
	BluetoothAdapter bluetoothAdapter;
	BluetoothDevice bluetoothDevice;
	private int connection_count = 0;

	public SearchForMasterThread(BluetoothAdapter bluetoothAdapter,
			BluetoothDevice btDevice) {
		// this.context = context;
		this.bluetoothAdapter = bluetoothAdapter;
		this.bluetoothDevice = btDevice;
		BluetoothApp.stillSearching = true;
	}

	public void run() {
		synchronized (this) {
            while (BluetoothApp.stillSearching && !discovering) {
                if (connection_count > 5) {
                    connection_count = 0;
                    BluetoothApp.stillSearching = false;
                    BluetoothApp.isOBD2LockingPreventionRequired = true;
                    break;
                }
                connection_count++;
                //Log.dMainActivity.TAG, "Connection count: " + connection_count);

                pairedDevices = BluetoothApp
                        .QueryPairedDevices(bluetoothAdapter);

                boolean isTherePairedOBD2Device = false;
                for (BluetoothDevice btd : pairedDevices) {
                    if (btd.getName().contains("obd") || btd.getName().contains("OBD") || btd.getName().contains("Obd")) {
                        isTherePairedOBD2Device = true;
                        break;
                    }

                    if (ApplicationPrefs.getInstance(BluetoothApp.context).getObd2Devices().contains(btd.getName())) {
                        isTherePairedOBD2Device = true;
                        break;
                    }

                }

                if (!isTherePairedOBD2Device) {
                    startDiscoveringAndPairing();
                }


                for (BluetoothDevice btd : pairedDevices) {
//                    //Log.i("SearchForMasterThread",
//                            "Trying to connect to Device: " + btd.getName());
                    connectToDevice(btd);
//                    //Log.i("SearchForMasterThread",
//                            "Completed trying to connect to Device: "
//                                    + btd.getName());
                    if (!BluetoothApp.stillSearching) {
                        break;
                    }
                }

                if (newBtDeviceFound != null) {
                    connectToDevice(newBtDeviceFound);
                    newBtDeviceFound = null;
                }

                try {
                    this.wait(30 * 1000);
                } catch (InterruptedException e) {
//                    //Log.i("SearchForMaster:Run",
//                            "wiat for 30 seconds interrupted somehow");
                    e.printStackTrace();
                }
            }
        }
	}

	public void connectToDevice(BluetoothDevice bluetoothDevice) {
//		//Log.i("SearchForMaster",
//				"trying to connect to device " + bluetoothDevice);

//		//Log.i("SearchForMaster", "Still Searching: "
//				+ BluetoothApp.stillSearching);

//		//Log.i("SearchForMaster",
//				"Checking if connect to server thread not null");
		// if (BluetoothApp.connectToServerThread != null) {
		// //Log.i("SearchForMaster::ConnectToDevice","Server thread not null");
		// try {
		// //---close the connection first---
		// synchronized (this) {
		// if(BluetoothApp.connectToServerThread.bluetoothSocket != null)
		// BluetoothApp.connectToServerThread.bluetoothSocket.close();
		// }
		// } catch (IOException e) {
		// //Log.i("MainActivity", e.getLocalizedMessage());
		// }
		// }

		// ---connect to the selected Bluetooth device---
		// if(BluetoothApp.connectToServerThread==null){
//		//Log.i("SearchForMaster", "Server thread is null");
		BluetoothApp.connectToServerThread = new ConnectToServerThread(
				bluetoothDevice, bluetoothAdapter);
		BluetoothApp.connectToServerThread.setName("Started At " + new Date());
		BluetoothApp.connectToServerThread.start();
		//Log.dMainActivity.TAG, "connect to server thread started");

		try {
			BluetoothApp.connectToServerThread.join(10 * 1000);
			
			this.wait(10);
				if (!BluetoothApp.isConnectionEstablished) {
//					//Log.i("SearchForMaster",
//							"Connection not established yet after waiting for 5 seconds");
					try {
						BluetoothApp.commsThread.inputStream.close();
						BluetoothApp.commsThread.outputStream.close();
						BluetoothApp.connectToServerThread.bluetoothSocket.close();
					} catch (NullPointerException nullExp) {
//						//Log.i("SearchForMasterThread",
//								"Connection already closed");
					} catch (IOException ioException) {
//						//Log.i("SearchForMasterThread",
//								ioException.getLocalizedMessage());
					}
					BluetoothApp.connectToServerThread = null;
				} else {
//					//Log.i("Breaking loop", "breaking loop");
					BluetoothApp.lastKnownMasterMacAddress = bluetoothDevice
							.getAddress();
					BluetoothApp.stillSearching = false;
					BluetoothApp.isConnectionEstablished = true;
				}
		} catch (InterruptedException interruptedExp) {
//			//Log.i("SearchForMasterThread",interruptedExp.getMessage());
		}
	}


    private void startDiscoveringAndPairing(){
        discovering = true;
        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        BluetoothApp.context.registerReceiver(mReceiver, filter);
        bluetoothAdapter.startDiscovery();




    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //discovery starts, we can show progress dialog or perform other tasks
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //discovery finishes, dismis progress dialog
                discovering = false;
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //bluetooth device found
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device!=null){
                    Toast.makeText(context, "Found device " + device.getName(), Toast.LENGTH_LONG).show();
                    if (device.getName()!= null && (device.getName().contains("obd") || device.getName().contains("OBD") || device.getName().contains("Obd"))) {
                        newBtDeviceFound = device;
                    }
                }
            }
        }
    };
}
