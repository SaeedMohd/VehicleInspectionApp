package com.matics;

import java.util.Iterator;
import java.util.List;

import android.R.bool;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {

	Context mcon;
//	MediaPlayer player = new MediaPlayer();
	Intent mintent;
	boolean boolConnection=false;
	@Override
	public IBinder onBind(Intent intent) {
		mintent = intent;
		return null;
	}

	@Override
	public void onCreate() {
		//Log.e("", "Service Created");
		mcon = MyService.this;
		boolConnection=false;
	}
	@Override
	public void onDestroy() {
	}
	@Override
	public void onStart(Intent intent, int startid) {
		// Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
		//Log.e("", "onStart");
		boolConnection=false;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// **Your code **
//		boolConnection=true;
//		//-----------------------------Uploading online data file
//		final Timer timer4 = new Timer();
//		timer4.schedule(new TimerTask() {
//			public void run() {
//			    try {
//			    	if(ConnectionManagerThread.connection_count>30){
//			    		if(boolConnection){
//			    			boolConnection=false;
//			    			if(!BluetoothApp.isConnectionEstablished){
//		        				Intent intent_mainIntent = new Intent(mcon, MainActivity.class);
//								intent_mainIntent.putExtra("FROM","Service");
//								intent_mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//								intent_mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//								mcon.startActivity(intent_mainIntent);
//		        			}
//				    		
////				    		//Log.e("", "Process Name is :" + getForegroundApp().processName);
////				    		if(!ispen("Vehicle Health").equalsIgnoreCase("Vehicle Health")){
////				    			//Log.e("", "Process Name is :" + getForegroundApp().processName);
////				        		try {
////				        			if(!CommsThread.isConnected){
////				        				Intent intent_mainIntent = new Intent(mcon, MainActivity.class);
////										intent_mainIntent.putExtra("FROM","Service");
////										intent_mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////										intent_mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////										mcon.startActivity(intent_mainIntent);
////				        			}
////				        			
////									
////				    			} catch (Exception e) {
////				    				// TODO: handle exception
////				    				e.printStackTrace();
////				    			}
////				        	}
//				    		timer4.cancel();
//							stopSelf();
//							mcon.stopService(new Intent(mcon, MyService.class));
//							ConnectionManagerThread.connection_count = 0;
//			    		}
//			    		
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}, 0, 1000);
//		
		return START_NOT_STICKY;
	}
	private RunningAppProcessInfo getForegroundApp() {
	    RunningAppProcessInfo result=null, info=null;

	    ActivityManager  mActivityManager = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
	    List <RunningAppProcessInfo> l = mActivityManager.getRunningAppProcesses();
	    Iterator <RunningAppProcessInfo> i = l.iterator();
	    while(i.hasNext()){
	        info = i.next();
	        if(info.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND && !isRunningService(info.processName)){
	            result=info;
	            //Log.e("", "processname is :" + info.processName);
	            break;
	        }
	    }
	    return result;
	}
	public boolean isRunningService(String processname){
	    if(processname==null || processname.isEmpty())
	        return false;

	    RunningServiceInfo service;
	    ActivityManager    mActivityManager = (ActivityManager)mcon.getSystemService(Context.ACTIVITY_SERVICE);
	    List <RunningServiceInfo> l = mActivityManager.getRunningServices(9999);
	    Iterator <RunningServiceInfo> i = l.iterator();
	    while(i.hasNext()){
	        service = i.next();
	        if(service.process.equals(processname))
	            return true;
	    }

	    return false;
	}
	
	public bool isoepn(){
		Boolean flag = false;
		ActivityManager am = (ActivityManager)mcon.getSystemService(ACTIVITY_SERVICE);
		RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
		
		String foregroundTaskPackageName = foregroundTaskInfo .topActivity.getPackageName();
		PackageManager pm = mcon.getPackageManager();
		PackageInfo foregroundAppPackageInfo = null;
		try {
			foregroundAppPackageInfo = pm.getPackageInfo(foregroundTaskPackageName, 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String foregroundTaskAppName = foregroundAppPackageInfo.applicationInfo.loadLabel(pm).toString();
		//Log.e("", "Opend app is:");
		
		return null;
	}
	
	public String ispen(String name){

		ActivityManager am = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE);
		RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
		
		String foregroundTaskPackageName = foregroundTaskInfo .topActivity.getPackageName();
		PackageManager pm = this.getPackageManager();
		PackageInfo foregroundAppPackageInfo = null;
		try {
			foregroundAppPackageInfo = pm.getPackageInfo(foregroundTaskPackageName, 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String foregroundTaskAppName = foregroundAppPackageInfo.applicationInfo.loadLabel(pm).toString();
		//Log.e("", "Opend app is:"+foregroundTaskAppName);
		return foregroundTaskAppName;
		
	}
	
	
}
