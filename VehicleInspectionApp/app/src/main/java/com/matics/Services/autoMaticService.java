package com.matics.Services;

import java.util.Timer;
import java.util.TimerTask;

import com.matics.FualDialog;
import com.matics.Bluetooth.BluetoothApp;
import com.matics.MainActivity;
import com.matics.command.CommandsResponseParser;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class autoMaticService extends Service{
	
   public static Context con;
   Timer connectTextTimer;
   public static boolean boolService=false;
 boolean temp=true;
   public static int currentFualLevel=-1;
    public class LocalBinder extends Binder {
    	autoMaticService getService() {
            return autoMaticService.this;
        }
    }

    public void onDestroy() {
    	BluetoothApp.cancelNotification(autoMaticService.this);
		super.onDestroy();
	}
    
    @Override
    public void onCreate() {
    	//Log.dMainActivity.TAG, "autoMaticService");
    	con=autoMaticService.this;    	
    	
    }

  
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.dMainActivity.TAG, "Received start id " + startId + ": " + intent);
        currentFualLevel=-1;
     temp=true;
        if(isBluetooth()){
        	//Log.dMainActivity.TAG, "Bluetooth On");
//        	((Activity) con).runOnUiThread(updateListAfterDeviceIsConnectedToMaster);
        	if (BluetoothApp.isConnectionEstablished) {
        		//Log.dMainActivity.TAG, "Bluetooth connect");
        		BluetoothApp.showNotification(autoMaticService.this);
        		
        		
			} 
			else {
				BluetoothApp.cancelNotification(autoMaticService.this);
				//Log.dMainActivity.TAG, "Bluetooth not connect");
				 BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();    
			}
        }
        else{
        	//Log.dMainActivity.TAG, "Bluetooth Off");
        	BluetoothApp.cancelNotification(autoMaticService.this);
        }
        
        startTimerForConnectedTextView();        
        
       
        
        return START_STICKY;
    }

    private void startTimerForConnectedTextView() {

		final Runnable updateListAfterDeviceIsConnectedToMaster = new Runnable() {
			public void run(){
	        	//Log.dMainActivity.TAG,"Check connected or not to update the view");
				if (BluetoothApp.isConnectionEstablished) {
					
	
				} 
				else {
	
				}
			}
    };

    TimerTask task = new TimerTask(){
        public void run() {
        	if(isBluetooth()){
            	//Log.dMainActivity.TAG, "Bluetooth On");
//            	((Activity) con).runOnUiThread(updateListAfterDeviceIsConnectedToMaster);
            	if (BluetoothApp.isConnectionEstablished) {
            		//Log.dMainActivity.TAG, "Bluetooth connect");
            	
            		BluetoothApp.showNotification(autoMaticService.this);
            		boolService=true;
            		if(currentFualLevel == -1){
            			try {
            				currentFualLevel= Integer.parseInt(CommandsResponseParser.fuelLevel);
						} catch (Exception e) {
							// TODO: handle exception
							currentFualLevel=-1;
						}
            			
            		}
            		else{
            			try {
            				int checkFual=Integer.parseInt(CommandsResponseParser.fuelLevel);
            				checkFual=checkFual+5;
            				if(currentFualLevel > checkFual){
//            					BluetoothApp.showFualNotification(autoMaticService.this);
                    			Intent intent1 = new Intent(con, FualDialog.class);
                    			intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                				con.startActivity(intent1);
            				}
						} catch (Exception e) {
							// TODO: handle exception
						}
            			
            		}
				} 
				else {
					BluetoothApp.cancelNotification(autoMaticService.this);
//					if(boolService){
////						startService(new Intent(autoMaticService.this,com.matics.MyService.class));
//						ConnectionManagerThread.connection_count = 0;
//						
//						Thread thread = new Thread()
//						{
//						    @Override
//						    public void run() {
//						        try {
//						            while(true) {
//						                sleep(1000);
//						                try {
//						                	//Log.e("", "Connection Count is: " +ConnectionManagerThread.connection_count);
//						                	if(ConnectionManagerThread.connection_count>10){
//						                		if (!BluetoothApp.isConnectionEstablished){
//						                			BluetoothApp.showAlertNotification(autoMaticService.this);
//						                		}
//						                		
//						                		boolService=false;
//						                		break;
//						                	}
//						                	
//						                } catch (Exception e) {
//						                    e.printStackTrace();
//						                    //Log.i("ConnectionManagerThread::run", "Conneciton manager thread interrupted");
//						                }
//						            }
//						        } catch (InterruptedException e) {
//						            e.printStackTrace();
//						        }
//						    }
//						};
//
//						thread.start();
//						
//
////						new ConnectionManagerDetectThread().start();
//					}
//					BluetoothApp.cancelNotification(autoMaticService.this);
//					//Log.i("OdbConnection_Service", "Bluetooth not connect");
//					 BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();    
//						
//						if (mBluetoothAdapter.isEnabled()) {
//							//Context context = getApplicationContext();
//							//context.bindService(serviceIntent, serviceConn, Context.BIND_AUTO_CREATE);
//							//Log.i("MainActivity::onCreate","Starting Connection Manager Thread");
//							new ConnectionManagerThread().start();
//						}
				}
            }
            else{
            	//Log.dMainActivity.TAG, "Bluetooth Off");
            	
            }
                
        }
    
    };

		
		connectTextTimer = new Timer();
		connectTextTimer.schedule(task, 0, 15 * 1000);
	}
    
    
	protected boolean isBluetooth() {
		// TODO Auto-generated method stub
    	BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    	if (mBluetoothAdapter == null) {
    		return false;
    	} else {
    	    if (!mBluetoothAdapter.isEnabled()) {
    	    	return false;
    	    }
    	}
		return true;
	}

	@Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    private final IBinder mBinder = new LocalBinder();

	
}