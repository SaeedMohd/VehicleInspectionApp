package com.inspection.interfaces;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.inspection.Utils.Utility;

public class LocationUpdatesReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Utility.scheduleJob(context);

    }
}
