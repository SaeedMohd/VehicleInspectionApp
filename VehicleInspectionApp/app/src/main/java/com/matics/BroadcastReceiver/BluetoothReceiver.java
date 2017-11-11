package com.matics.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.matics.serverTasks.SendGPSAndBluetoothStatesTask;

public class BluetoothReceiver extends BroadcastReceiver {
    public BluetoothReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("BluetoothReceiver", "received.....");
        new SendGPSAndBluetoothStatesTask(context) {
            @Override
            public void onTaskCompleted(String result) {
                Log.v("SendGpsAndBluetooth", ""+result);
            }
        }.execute();
    }
}
