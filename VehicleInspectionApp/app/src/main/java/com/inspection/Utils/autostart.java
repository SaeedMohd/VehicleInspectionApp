package com.inspection.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.inspection.CrashReport.CustomUncaughtExceptionHandler;
import com.inspection.Services.autoMaticService;

public class autostart extends BroadcastReceiver 
{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
		 SharedPreferences	prefs = PreferenceManager.getDefaultSharedPreferences(context);
		 if(prefs.getBoolean("Vehical_Health", true)){
			
				 Intent intent1 = new Intent(context,autoMaticService.class);
				 context.startService(intent1);
			        //Log.dMainActivity.TAG, "started");
		 }
	
	}
}