package com.inspection.Utils;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.UUID;

@SuppressLint("NewApi")
public class MyService extends Service {
	private static final String TAG = "MyService";
	MediaPlayer player;
	static Timer time;
	public static Boolean Enable=false;
	public static BluetoothAdapter btAdapter;
	public static String devString;
	public static BluetoothDevice bluetoothDevice;
	public static InputStream in = null;
	public static OutputStream out = null;
	public static Context mcontext;
	public static BluetoothSocket sock = null;
	static SharedPreferences prefs;
	static Timer time1,timer4;
	static Timer time2;
	static GPSTracker gps;
	static double latitude,longitude,Gpsspeed,Gpstime;
	static String Upload_period;
	protected static final UUID MY_UUID = UUID
			.fromString("00001101-0000-1000-8000-00805F9B34FB");
	public static boolean isFirsttime = true,isDialog=true,spinnerstatus=true;
	public static String VIN = "VIN";
	 public static SharedPreferences mediaPrefs = null;
	    public static String Address = "Address";
	    public static String AccountFullName = "AccountFullName";
	    public static String ContactID = "ContactID";
	    public static String AccountID = "AccountId";
	    public static String LastUpload = "LastUpload";    
	    public static String TimeInterval = "TimeInterval";
	    public static String SpinnerPosition= "SpinnerPosition";
	    public static String refereLink= "refereLink";
	    public static String Fb_Link= "Fb_Link";
	    public static String tw_Link= "tw_Link";
	    public static String appointment_link= "appointment_link";
	    public static String GCM_DeviceID= "GCM_DeviceID";
	    public static String First_Name= "First_Name";
	    public static String Last_Name= "Last_Name";
	    public static String Phone_Number= "Phone_Number";
	    public static String Email= "Email";
	    public static String Image_Url= "Image";
	    public static String phone= "Phone";
	    public static String State= "State";
	    public static String Zip= "Zip";
	    public static String MobileUserProfileId= "MobileUserProfileId";
	    public static String City= "City";
	    public static String Android_Phone_ID= "Android_Phone_ID";	 
	    public static String DeviceAddress= "DeviceAddress";
	    public static final int NOTIFY_ME_ID=1337;
	    public static String LogEnable= "LogEnable";
	    public static NotificationManager mNotificationManager;
	    public static ArrayList<String>listnew ;
	    public static Boolean isProceed=true;
	    public static Boolean isAvailable=false;
	    final String uploadFilePath = "/storage/emulated/0/";
	    final String uploadFileName = "matics.txt";
	    int serverResponseCode = 0;
	    Boolean isfirst;
	    boolean boolBluetooth;
	
	public IBinder onBind(Intent intent) {
		return null;
	}
	public void onCreate() {
//		Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
//		MyService.mediaPrefs = this.getSharedPreferences("matics", 1);
//		
//		isFirsttime = true;
//		//Log.e(TAG, "onCreate"+MyService.mediaPrefs.getBoolean("Bluetooth", true));
//		prefs = PreferenceManager.getDefaultSharedPreferences(this);
//		if(MyService.mediaPrefs.getBoolean("Bluetooth", true)){
//			btAdapter = BluetoothAdapter.getDefaultAdapter();
//			btAdapter.startDiscovery();
//		}
//		//Log.e("Bluetooth", ""+MyService.mediaPrefs.getBoolean("Bluetooth", true));
//		try {
//			 WeakReference<TextView> text = new WeakReference<TextView>(FragmentConnection.Connect);
//			 text.get().setText("Connected");
//			 text.get().setTextColor(Color.GREEN);
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		stopService(new Intent(MyService.this, autoMaticService.class));
//		
//		IntentFilter filter1 = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
//	    IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
//	    IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
//	    registerReceiver(BTReceiver, filter1);
//	    registerReceiver(BTReceiver, filter2);
//	    registerReceiver(BTReceiver, filter3);
//		
////		registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
//	}
//	public void onDestroy() {
//		
//		try {
//			MainActivity.storeBooleanUserDetails("VehicalHealth", false);
//			Toast.makeText(this, "Matics Service Stopped", Toast.LENGTH_LONG).show();
//			//Log.e(TAG, "onDestroy");
////			time1.cancel();
//			time.cancel();
//			time2.cancel();
//			sock=null;
////			time1=null;
//			time=null;
//			time2=null;
//			Handler h = new Handler(MyService.this.getMainLooper());
//			h.post(new Runnable() {
//			    public void run() {
//			    	 WeakReference<TextView> text = new WeakReference<TextView>(FragmentConnection.Connect);
//					 text.get().setText("Not Connected");
//					 text.get().setTextColor(Color.parseColor("#000000"));
//			    }
//			});
//			
////			 unregisterReceiver(bReceiver);
//			 unregisterReceiver(BTReceiver);
//			 
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
//		
//		 stopSelf();
////		player.stop();
//	}
//	public void onStart(Intent intent, int startid) {
////		Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
//		//Log.e(TAG, "onStart");
//		SendNotification();
//		
//		LoadPreference();
//		if(MyService.mediaPrefs.getBoolean("Bluetooth", true)){
//			btAdapter = BluetoothAdapter.getDefaultAdapter();
//			btAdapter.startDiscovery();
//		}
//		
//		devString = MyService.mediaPrefs.getString(MyService.DeviceAddress, "");
//		Enable = MyService.mediaPrefs.getBoolean(MyService.LogEnable, true);
//		mcontext = this;
//		listnew = new ArrayList<String>();
//		final Handler handler = new Handler();
//			 time1 = new Timer();
//			time1.schedule(new TimerTask() {
//				public void run() {
//					listnew.clear();
//					handler.post(new Runnable() {
//						public void run() {
//							
//							
//							if (btAdapter.isDiscovering()) {
//					// the button is pressed when it discovers, so cancel the discovery
//								btAdapter.cancelDiscovery();
//							   }
//							   else {
//								   btAdapter.startDiscovery();
//								   registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));	
//								} 
//							try {
////								unregisterReceiver(BTReceiver);
//							} catch (Exception e) {
//								//Log.e(TAG, "Error in UnRegister");
//							}
//							
//							
								
//							    if(isProceed){
//							    	//Log.e("", "Available");
//							    	try {
//										if(sock.isConnected()){
//											//Log.e("", "Socket Already Connected.. No need toConnect");
////											connection();
//										}
//										else{
//											resetConnection();
//											try {
//												if (devString == null || "".equals(devString)) {
////													startActivity(new Intent(.this, SettingActivity.class));
////													devString ="38:59:F9:FD:74:0F";
//													return;
//												}
//											} catch (Exception e) {
//												//Log.e("", "Error :>>" + e.getMessage());
//												writefile(e.getMessage());
//											}
//											btAdapter = BluetoothAdapter.getDefaultAdapter();
//											btAdapter.startDiscovery();
//											bluetoothDevice = btAdapter.getRemoteDevice(devString);
//											BluetoothSocket tmp = null;
//											try {
//												tmp = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
////												tmp = createBluetoothSocket(bluetoothDevice);
//											} catch (IOException e) {
//												e.printStackTrace();
//											}
//											  btAdapter.cancelDiscovery();
//											if (tmp != null) {
//												sock = tmp;
//												
//												Handler h = new Handler(MyService.this.getMainLooper());
//												h.post(new Runnable() {
//												    public void run() {
//												    	try {
//															sock.connect();
//															 WeakReference<TextView> text = new WeakReference<TextView>(FragmentConnection.Connect);
//															 text.get().setText("Connected.");
//															 text.get().setTextColor(Color.GREEN);
//														} catch (IOException e) {
//															// TODO Auto-generated catch block
//															e.printStackTrace();
//														}
//														
//												    }
//												});
//												
//												try {
//													if (in == null) {
//														in = sock.getInputStream();
//													}
//													if (out == null) {
//														out =sock.getOutputStream();
//													}
//												} 
//												catch (Exception e) {
//													e.printStackTrace();
//												}
//											}
//										}
//									}
//							    	catch (Exception e) {
//										e.printStackTrace();
//										connection();
//									}
//							    }
//							    else{
//							    	//Log.e("", "Not Available");
//							    	if(isAvailable){
//							    		isProceed=true;
//							    	}else{
//							    		//Log.e("", "Not");
//							    	}
//							    }
//					//----------------Uploading offline file
//								String path11 = Environment.getExternalStorageDirectory().getPath()+"";
//								File file = new File(path11, "/" + "matics.txt");
//								if(file.exists()){
//									Boolean isnet = Utility.checkInternetConnection(MyService.this);
//								    try {
//										if(isnet){
//												uploadFile(Environment.getExternalStorageDirectory()+"/matics.txt","matics.txt",2);
//											}
//										else{
//											isfirst=true;
//										}
//									} catch (Exception e) {
//										e.printStackTrace();
//										//Log.e("", "matics not found");
//									}
//								}
//								
//								
//						}
//					});
//				}
//			}, 0,5000);
		
		
		
		//-----------------------------Uploading online data file
//				timer4 = new Timer();
//				timer4.schedule(new TimerTask() {
//					public void run() {
//					    Boolean isnet1 = Utility.checkInternetConnection(MyService.this);
//					    try {
//							if(isnet1){
//									uploadFile(Environment.getExternalStorageDirectory()+"/Matics/online.txt","online.txt",1);
//								}
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//					}
//				}, 0, 60000);
//		
//		getdata();
//		UploadData(mcontext, 10000, mcontext);
	}
//	private void SendNotification() {
//		// TODO Auto-generated method stub
//		Intent intent = new Intent(this,MainActivity.class);
////      intent.setClassName(packageName_Lyve, packageName_Lyve + "." + CLASSNAME_Lyve);
//		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
//		NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
//		.setContentTitle("Vehical Health is Running")
//		.setSmallIcon(R.drawable.ic_launcher);
//	      builder.setContentIntent(pIntent);
//	      builder.setOngoing(true);
//	      builder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
//       mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//       mNotificationManager.notify(Integer.valueOf(NOTIFY_ME_ID), builder.getNotification());
//    }
//	private void LoadPreference() {
//		try {
//			MyService.mediaPrefs.edit().putString(MyService.AccountFullName, MainActivity.mediaPrefs.getString(MyService.AccountFullName, "")).commit();
//			MyService.mediaPrefs.edit().putString(MyService.Address, MainActivity.mediaPrefs.getString(MyService.Address, "")).commit();
//			MyService.mediaPrefs.edit().putString(MyService.AccountID, MainActivity.mediaPrefs.getString(MyService.AccountID, "")).commit();
//			MyService.mediaPrefs.edit().putString(MyService.TimeInterval, MainActivity.mediaPrefs.getString(MyService.TimeInterval, "")).commit();
//			MyService.mediaPrefs.edit().putString(MyService.refereLink, MainActivity.mediaPrefs.getString(MyService.refereLink, "")).commit();
//			MyService.mediaPrefs.edit().putString(MyService.Fb_Link, MainActivity.mediaPrefs.getString(MyService.Fb_Link, "")).commit();
//			MyService.mediaPrefs.edit().putString(MyService.tw_Link, MainActivity.mediaPrefs.getString(MyService.tw_Link, "")).commit();
//			MyService.mediaPrefs.edit().putString(MyService.appointment_link, MainActivity.mediaPrefs.getString(MyService.appointment_link, "")).commit();
//			MyService.mediaPrefs.edit().putString(MyService.City, MainActivity.mediaPrefs.getString(MyService.City, "")).commit();
//			MyService.mediaPrefs.edit().putString(MyService.phone, MainActivity.mediaPrefs.getString(MyService.AccountFullName, "")).commit();
//			MyService.mediaPrefs.edit().putString(MyService.Zip, MainActivity.mediaPrefs.getString(MyService.Zip, "")).commit();
//			MyService.mediaPrefs.edit().putString(MyService.Image_Url, MainActivity.mediaPrefs.getString(MyService.Image_Url, "")).commit();
//			MyService.mediaPrefs.edit().putString(MyService.State, MainActivity.mediaPrefs.getString(MyService.State, "")).commit();
//			MyService.mediaPrefs.edit().putString(MyService.Android_Phone_ID, MainActivity.mediaPrefs.getString(MyService.Android_Phone_ID, "")).commit();
//			MyService.mediaPrefs.edit().putString(MyService.DeviceAddress, MainActivity.mediaPrefs.getString(MyService.DeviceAddress, "")).commit();
//			MyService.storeBooleanUserDetails(MyService.LogEnable, MainActivity.mediaPrefs.getBoolean(MyService.LogEnable, true));
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
//	}
//
//	public static void getdata(){
//		if(sock==null){
//			//Log.e("", "Socket is  closed");
//			writefile("Socket is  closed");
//		}else{
//			//Log.e("", "Socket is  Open");
//			writefile("Socket is Open");
//		}
//		try {
////			if (sock != null) {
//			
//				time = new Timer();
//				final Handler handler = new Handler();
//				time.schedule(new TimerTask() {
//					public void run() {
//						handler.post(new Runnable() {
//							@SuppressLint("NewApi")
//							@Override
//							public void run() {
////								
//								try {
//									if(sock != null){
//										if(!sock.isConnected()){
//											resetConnection();
//											connection();
//										}
//										//Log.e("", "Sock is :" + sock);
////										if(sock.isConnected() && in!=null && out!=null && isProceed){
//										if(sock.isConnected()){
//											if (isFirsttime) {
////												BLECommandTask task = new BLECommandTask(mcontext,null, getAllCommands(),null,null,null,null,null,getDecodeCommands());
////												task.execute();
//											} 
//											else {
////												BLECommandTask task = new BLECommandTask(mcontext,null, getCommands(),null,null,null,null,null,getDecodeCommands());
////												task.execute();
//											}
//										}
//										else{
//											//Log.e("", "Socket connection is not Established");
//											writefile("Socket connection is not Established");
//										}
//									}
//									
//								} catch (Exception e) {
//									// TODO: handle exception
//									e.printStackTrace();
//									//Log.e("", "in getdata Error");
//								}
//							}
//						});
//					}
//				}, 0, 5000);
//		
//				
//	
//	} catch (Exception e) {
//		// TODO: handle exception
////		resetConnection();
////		connection();
//		//Log.e("", "Error :" + e.getMessage());
//		Toast ta = Toast.makeText(mcontext, "Connection Disconnected", Toast.LENGTH_LONG);
//		ta.show();
//	}
//	}
//	public static void resetConnection() {
//		if (in != null) {
//			try {
//				in.close();
//			} catch (Exception e) {
//			}
//			in = null;
//		}
//		if (out != null) {
//			try {
//				out.close();
//			} catch (Exception e) {
//			}
//			out = null;
//		}
//		if (sock != null) {
//			try {
//				sock.close();
//			} catch (Exception e) {
//			}
//			sock = null;
//		}
//	}
//
//    public static void connection(){
//    	devString = MyService.mediaPrefs.getString(MyService.DeviceAddress, "");
//		Enable = MyService.mediaPrefs.getBoolean(MyService.LogEnable, true);
//		try {
//			if (devString == null || "".equals(devString)) {
//				//
////				startActivity(new Intent(MainActivity.this, SettingActivity.class));
//				devString ="38:59:F9:FD:74:0F";
//				return;
//			}else{
////				devString ="38:59:F9:FD:74:0F";
//			}
//		} catch (Exception e) {
//			//Log.e("", "Error :>" + e.getMessage());
//		}
//		resetConnection();
//		btAdapter = BluetoothAdapter.getDefaultAdapter();
//		bluetoothDevice = btAdapter.getRemoteDevice(devString);
//		BluetoothSocket tmp = null;
//		try {
//			tmp = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
////			tmp = createBluetoothSocket(bluetoothDevice);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		 btAdapter.cancelDiscovery();
//		if (tmp != null) {
//			sock = tmp;
//			try {
//				sock.connect();
//				try {
//					if (in == null) {
//						in = tmp.getInputStream();
//					}
//					if (out == null) {
//						out = tmp.getOutputStream();
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			} catch (IOException e) {
////				resetConnection();
//				writefile(e.getMessage());
////				connection();
//				// sock.close();
//			}
//		}
//    }
//    public static ArrayList<ObdCommand> getCommands()
//  	{
//  		ArrayList<ObdCommand> cmds = new ArrayList<ObdCommand>();
//  		cmds.add(new MassAirflowObdCommand("0110", "Mass Airflow", "g/s"));
//  		cmds.add(new TempObdCommand("010F", "Air Intake Temp", "C"));
//  		cmds.add(new IntObdCommand("010B", "Intake Manifold Press", "kPa"));
//  		cmds.add(new IntObdCommand("0133", "Barometric Press", "kPa"));
//  		cmds.add(new TempObdCommand("0146", "Ambient Air Temp", "C"));
//  		cmds.add(new IntObdCommand("010D", "Vehicle Speed", "km/h"));		
//  		cmds.add(new ThrottleObdCommand("0111", "Throttle Position", "%"));
//  		cmds.add(new EngineRPMObdCommand("010C", "Engine RPM", "RPM"));
//  		cmds.add(new FuelPressureObdCommand("010A", "Fuel Press", "kPa"));
//  		cmds.add(new TempObdCommand("0105", "Coolant Temp", "C"));
//  		cmds.add(new ThrottleObdCommand("0104", "Engine Load", "%"));
//  		cmds.add(new TotalDistanceObdCommand("0121", "Total miles", ""));// Distance traveled since codes cleared
//  		cmds.add(new VinObdCommand("0902", "VIN", "code"));
////  		cmds.add(new DtcNumberObdCommand("0101", "Trouble Code Status", ""));
//  		cmds.add(new TroubleCodesObdCommand7("03", "Trouble Codes", ""));
//  		
//  		//-----------------------------------New Without DeCode-----------------------
//  		cmds.add(new FuelObdCommand("012F", "Fuel Level Input", "%"));
//  		cmds.add(new FuelObdCommand("0103", "Fuel status", ""));
//  		cmds.add(new TimingAdvanceObdCommand("0103", "Timing Advance", ""));
//  		cmds.add(new AirFlowRateObdCommand("0110", "Air Flow Rate", ""));
//  		cmds.add(new OBDStarndardObdCommand("011C", "OBD Standard", ""));
//  		cmds.add(new OxySenserPresentObdCommand("011D", "Oxygen sensors present", ""));
//  		cmds.add(new RunTimeObdCommand("011F", "Run time since engine start", "seconds"));
//  		cmds.add(new FuelRailPressureObdCommand("0123", "Fuel Rail Pressure", "kPa"));
//  		cmds.add(new CommandedPurgeObdCommand("012E", "Commanded evaporative purge", "%"));
//  		cmds.add(new VaporPressureObdCommand("0132", "Evap. System Vapor Pressure", "Pa"));
//  		cmds.add(new ControlModuleVoltageObdCommand("0142", "Control module voltage", "V"));
//  		cmds.add(new AbsoluteLoadValueObdCommand("0143", "Absolute load value", "%"));
//  		cmds.add(new EquivalenceRatioObdCommand("0144", "Command equivalence ratio", ""));
//  		cmds.add(new FuelTypeObdCommand("0151", "Fuel Type", "V"));
//  		cmds.add(new EthanolFuelObdCommand("0152", "Ethanol fuel", "%"));
//  		
//  		cmds.add(new EthanolFuelObdCommand("0101", "Monitor_status", ""));
//  		cmds.add(new EthanolFuelObdCommand("0106", "Short_term_fuel_B1", ""));
//  		cmds.add(new EthanolFuelObdCommand("0107", "Long_term_fuel_B1", ""));
//  		cmds.add(new EthanolFuelObdCommand("0108", "Short_term_fuel_B2", ""));
//  		cmds.add(new EthanolFuelObdCommand("0109", "Long_term_fuel_B2", ""));
//  		cmds.add(new EthanolFuelObdCommand("0113", "Oxy_sensor_present", ""));
//  		cmds.add(new EthanolFuelObdCommand("0114", "Oxy_sensor_voltage_B1S1", ""));
//  		cmds.add(new EthanolFuelObdCommand("0115", "Oxy_sensor_voltage_B1S2", ""));
//  		cmds.add(new EthanolFuelObdCommand("0116", "Oxy_sensor_voltage_B1S3", ""));
//  		cmds.add(new EthanolFuelObdCommand("0117", "Oxy_sensor_voltage_B1S4", ""));
//  		cmds.add(new EthanolFuelObdCommand("0118", "Oxy_sensor_voltage_B2S1", ""));
//  		cmds.add(new EthanolFuelObdCommand("0119", "Oxy_sensor_voltage_B2S2", ""));
//  		cmds.add(new EthanolFuelObdCommand("011A", "Oxy_sensor_voltage_B2S3", ""));
//  		cmds.add(new EthanolFuelObdCommand("011B", "Oxy_sensor_voltage_B2S4", ""));
//  		cmds.add(new EthanolFuelObdCommand("011E", "Auxiliary_input_status", ""));
//  		cmds.add(new EthanolFuelObdCommand("0120", "PID_supported", ""));
//  		cmds.add(new EthanolFuelObdCommand("0124", "Equivalence_Ratio_O2S1_V", ""));
//  		cmds.add(new EthanolFuelObdCommand("0125", "Equivalence_Ratio_O2S2_V", ""));
//  		cmds.add(new EthanolFuelObdCommand("0126", "Equivalence_Ratio_O2S3_V", ""));
//  		cmds.add(new EthanolFuelObdCommand("0127", "Equivalence_Ratio_O2S4_V", ""));
//  		cmds.add(new EthanolFuelObdCommand("0128", "Equivalence_Ratio_O2S5_V", ""));
//  		cmds.add(new EthanolFuelObdCommand("0129", "Equivalence_Ratio_O2S6_V", ""));
//  		cmds.add(new EthanolFuelObdCommand("012A", "Equivalence_Ratio_O2S7_V", ""));
//  		cmds.add(new EthanolFuelObdCommand("012B", "Equivalence_Ratio_O2S8_V", ""));
//  		cmds.add(new EthanolFuelObdCommand("012C", "Commanded_EGR", ""));
//  		cmds.add(new EthanolFuelObdCommand("012D", "EGR_Error", ""));
//  		cmds.add(new EthanolFuelObdCommand("0130", "No_WarmUps", ""));
//  		cmds.add(new EthanolFuelObdCommand("0131", "Dist_Traveled", ""));
//  		cmds.add(new EthanolFuelObdCommand("0134", "Equivalence_Ratio_O2S1_C", ""));
//  		cmds.add(new EthanolFuelObdCommand("0135", "Equivalence_Ratio_O2S2_C", ""));
//  		cmds.add(new EthanolFuelObdCommand("0136", "Equivalence_Ratio_O2S3_C", ""));
//  		cmds.add(new EthanolFuelObdCommand("0137", "Equivalence_Ratio_O2S4_C", ""));
//  		cmds.add(new EthanolFuelObdCommand("0138", "Equivalence_Ratio_O2S5_C", ""));
//  		cmds.add(new EthanolFuelObdCommand("0139", "Equivalence_Ratio_O2S6_C", ""));
//  		cmds.add(new EthanolFuelObdCommand("013A", "Equivalence_Ratio_O2S7_C", ""));
//  		cmds.add(new EthanolFuelObdCommand("013B", "Equivalence_Ratio_O2S8_C", ""));
//  		cmds.add(new EthanolFuelObdCommand("013C", "Catalyst_Temp_B1S1", ""));
//  		cmds.add(new EthanolFuelObdCommand("013D", "Catalyst_Temp_B2S1", ""));
//  		cmds.add(new EthanolFuelObdCommand("013E", "Catalyst_Temp_B1S2", ""));
//  		cmds.add(new EthanolFuelObdCommand("013F", "Catalyst_Temp_B2S2", ""));
//  		cmds.add(new EthanolFuelObdCommand("0140", "PID_supported_41", ""));
//  		cmds.add(new EthanolFuelObdCommand("0141", "Monitor_status_Cycle", ""));
//  		cmds.add(new EthanolFuelObdCommand("0145", "Rela_Throttle_position", ""));
//  		cmds.add(new EthanolFuelObdCommand("0147", "Absolute_Throttle_position_B", ""));
//  		cmds.add(new EthanolFuelObdCommand("0148", "Absolute_Throttle_position_C", ""));
//  		cmds.add(new EthanolFuelObdCommand("0149", "Accelerator_pedal_position_D", ""));
//  		cmds.add(new EthanolFuelObdCommand("014A", "Accelerator_pedal_position_E", ""));
//  		cmds.add(new EthanolFuelObdCommand("014B", "Accelerator_pedal_position_F", ""));
//  		cmds.add(new EthanolFuelObdCommand("014C", "Commanded_throttle_actuator", ""));
//  		
//  		cmds.add(new EthanolFuelObdCommand("014D", "Time_MIL", ""));
//  		cmds.add(new EthanolFuelObdCommand("014E", "Time_trouble_code", ""));
//  		cmds.add(new EthanolFuelObdCommand("0153", "Evap_system_Vapour_Pressure", ""));
//  		cmds.add(new EthanolFuelObdCommand("0202", "Freeze_frame_trouble_code", ""));
//  		cmds.add(new EthanolFuelObdCommand("04", "Clear_trouble_code", ""));
//  		cmds.add(new EthanolFuelObdCommand("050100", "OBD_Monitor_ID_Supported", ""));
//  		cmds.add(new EthanolFuelObdCommand("050101", "O2_sensor_Monitor_B1S1_Learn", ""));
//  		cmds.add(new EthanolFuelObdCommand("050102", "O2_sensor_Monitor_B1S2_Learn", ""));
//  		cmds.add(new EthanolFuelObdCommand("050103", "O2_sensor_Monitor_B1S3_Learn", ""));
//  		cmds.add(new EthanolFuelObdCommand("050104", "O2_sensor_Monitor_B1S4_Learn", ""));
//  		
//  		cmds.add(new EthanolFuelObdCommand("050105", "O2_sensor_Monitor_B2S1_Learn", ""));
//  		cmds.add(new EthanolFuelObdCommand("050106", "O2_sensor_Monitor_B2S2_Learn", ""));
//  		cmds.add(new EthanolFuelObdCommand("050107", "O2_sensor_Monitor_B2S3_Learn", ""));
//  		cmds.add(new EthanolFuelObdCommand("050108", "O2_sensor_Monitor_B2S4_Learn", ""));
//  		cmds.add(new EthanolFuelObdCommand("050109", "O2_sensor_Monitor_B3S1_Learn", ""));
//  		cmds.add(new EthanolFuelObdCommand("05010A", "O2_sensor_Monitor_B3S2_Learn", ""));
//  		cmds.add(new EthanolFuelObdCommand("05010B", "O2_sensor_Monitor_B3S3_Learn", ""));
//  		cmds.add(new EthanolFuelObdCommand("05010C", "O2_sensor_Monitor_B3S4_Learn", ""));
//  		cmds.add(new EthanolFuelObdCommand("05010D", "O2_sensor_Monitor_B4S1_Learn", ""));
//  		cmds.add(new EthanolFuelObdCommand("05010E", "O2_sensor_Monitor_B4S2_Learn", ""));
//  		cmds.add(new EthanolFuelObdCommand("05010F", "O2_sensor_Monitor_B4S3_Learn", ""));
//  		cmds.add(new EthanolFuelObdCommand("050110", "O2_sensor_Monitor_B4S4_Learn", ""));
//  		cmds.add(new EthanolFuelObdCommand("050201", "O2_sensor_Monitor_B1S1_Rich", ""));
//  		cmds.add(new EthanolFuelObdCommand("050202", "O2_sensor_Monitor_B1S2_Rich", ""));
//  		cmds.add(new EthanolFuelObdCommand("050203", "O2_sensor_Monitor_B1S3_Rich", ""));
//  		cmds.add(new EthanolFuelObdCommand("050204", "O2_sensor_Monitor_B1S4_Rich", ""));
//  		cmds.add(new EthanolFuelObdCommand("050205", "O2_sensor_Monitor_B2S1_Rich", ""));
//  		cmds.add(new EthanolFuelObdCommand("050206", "O2_sensor_Monitor_B2S2_Rich", ""));
//  		cmds.add(new EthanolFuelObdCommand("050207", "O2_sensor_Monitor_B2S3_Rich", ""));
//  		cmds.add(new EthanolFuelObdCommand("050208", "O2_sensor_Monitor_B2S4_Rich", ""));
//  		cmds.add(new EthanolFuelObdCommand("050209", "O2_sensor_Monitor_B3S1_Rich", ""));
//  		cmds.add(new EthanolFuelObdCommand("05020A", "O2_sensor_Monitor_B3S2_Rich", ""));
//  		cmds.add(new EthanolFuelObdCommand("05020B", "O2_sensor_Monitor_B3S3_Rich", ""));
//  		cmds.add(new EthanolFuelObdCommand("05020C", "O2_sensor_Monitor_B3S4_Rich", ""));
//  		cmds.add(new EthanolFuelObdCommand("05020D", "O2_sensor_Monitor_B4S1_Rich", ""));
//  		cmds.add(new EthanolFuelObdCommand("05020E", "O2_sensor_Monitor_B4S2_Rich", ""));
//  		cmds.add(new EthanolFuelObdCommand("05020F", "O2_sensor_Monitor_B4S3_Rich", ""));
//  		cmds.add(new EthanolFuelObdCommand("050210", "O2_sensor_Monitor_B4S4_Rich", ""));
//  		cmds.add(new EthanolFuelObdCommand("0900", "Mode_9_supported", ""));
//  		cmds.add(new EthanolFuelObdCommand("0122", "Fuel_Rail_Pressure_D", ""));
//  		
//  		
//  		return cmds;
//  	}
//     
//    //---------------------Decoded Commands which will be displayed in listview in setting Screen 
//    public static ArrayList<ObdCommand> getDecodeCommands()
//  	{
//  		ArrayList<ObdCommand> cmds = new ArrayList<ObdCommand>();
//  		cmds.add(new EngineRPMObdCommandDecode("010C", "Engine RPM", "RPM"));
//  		cmds.add(new TempObdCommandDecode("0105", "Coolant Temp", "C"));
//  		cmds.add(new IntObdCommandDecode("010D", "Vehicle Speed", ""));
//  		cmds.add(new FuelObdCommandDecode("012F", "Fuel Level Input", "%"));
//  		return cmds;
//  	}
//    //-------------------Static commands for initialization. which will be called only once whicle running Application
//  	public static ArrayList<ObdCommand> getStaticCommands()
//  	{
//  		ArrayList<ObdCommand> cmds = new ArrayList<ObdCommand>();
//  		cmds.add(new ObdCommand("ATZ", "Initialize", ""));
//  		cmds.add(new ObdCommand("ATSP0", "Protocol Initialize", "C"));
//  		cmds.add(new ObdCommand("ate0", "Protocol search order)", "C"));
//  		return cmds;
//  	}
//  //----------------Combining All command Array into one Array	
//  	public static ArrayList<ObdCommand> getAllCommands()
//  	{
//  		ArrayList<ObdCommand> cmds = new ArrayList<ObdCommand>();
//  		cmds.addAll(getStaticCommands());
//  		cmds.addAll(getCommands());
//  		return cmds;
//  	}
//	
//	public static void UploadData(final Context context,long time,final Context mContext) {
//		Upload_period = prefs.getString(SettingActivity.UPDATE_PERIOD_KEY, null);
//		final Handler handler = new Handler();
//		 // Instantiate Timer Object
//		time2 = new Timer();
//		
//	
//		
//		time2.schedule(new TimerTask() {
//			public void run() {
//				handler.post(new Runnable() {
//					@Override
//					public void run() {
//						// TODO Auto-generated method stub
//						try {
//								if(sock.isConnected()){
//									if(fieldValidation()){
//										//Log.e("", "in upload");
//										gps = new GPSTracker(mContext);
//										if(gps.canGetLocation()){
//								        	 latitude = gps.getLatitude();
//								        	 longitude = gps.getLongitude();
//								        	 Gpsspeed = gps.getGpsspeed();
//								        	 Gpstime = gps.getGpsTime();
//								        }else{
//								        	gps.showSettingsAlert();
//								        }
//										//Log.e("", "GPS Speed:" +Gpsspeed);
////										UploadDataTask task = new UploadDataTask(context, BLECommandTask.results_value);
////										task.execute(String.valueOf(latitude),String.valueOf(longitude),String.valueOf(Gpsspeed),String.valueOf(Gpstime));
//									}
//								}
//								else{
//									//Log.e("", "Socket null found");
//									writefile("Socket null found");
//								}
//							
//							
//							
//							
//						} 
//						catch (Exception e) {
//							//Log.e("", "Error :>>>" + e.getMessage());
////							writefile("Data not uploaded");
//							
//						}
//					}
//				});
//			}
//		}, 0, time);
//	}
//	
//	//----------Storing Sharepreference 
//	 public static void storeStringUserDetails(String namePref, String value) {
//		 // Storing String Preferences
//		 try {
//			 SharedPreferences.Editor prefEditor = MyService.mediaPrefs.edit();
//		        prefEditor.putString(namePref, value);
//		        prefEditor.commit();
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//	       
//	    }
//	    public static void storeIntUserDetails(String namePref, int value) {  // Storing Integer Preference
//	    	try { SharedPreferences.Editor prefEditor = MyService.mediaPrefs.edit();
//	        prefEditor.putInt(namePref, value);
//	        prefEditor.commit();
//				
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
//	       
//	    }
//	    public static void storeBooleanUserDetails(String namePref, boolean value) { // Storing Boolean Preference
//	    	try {
//	    		 SharedPreferences.Editor prefEditor = MyService.mediaPrefs.edit();
//	 	        prefEditor.putBoolean(namePref, value);
//	 	        prefEditor.commit();
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
//	       
//	    }
//	    
//	   //----------Method for write text in text file which is stored in sdcard 
//	    public static void writefile(String data){
//	    	if(Enable){ 
//	    		try {
//		    		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss EEEE");
//		    		String currentDateandTime = sdf.format(new Date());
//					File f1 = new File(Environment.getExternalStorageDirectory().getPath()+ "/Matics/android.txt");
//					BufferedWriter out = new BufferedWriter(new FileWriter(f1, true));
//					out.write("\n\n"+"["+currentDateandTime+"]  "+data + "\n\n");
//					out.close();
//				} catch (IOException ioe) {
//					ioe.printStackTrace();
//				}
//	    	}
//	    }
//	  //--------------------- Field validation to check any field is not null  
//	    private static boolean fieldValidation() {
//			boolean flag = true;
////			if (BLECommandTask.results_value.get("0110").equalsIgnoreCase("")||BLECommandTask.results_value.get("0110").equalsIgnoreCase("NODATA")||BLECommandTask.results_value.get("0110")==null ||BLECommandTask.results_value.get("0110").contains("UNABLE")) {
////				flag = false;
////			} else if (BLECommandTask.results_value.get("010F").equalsIgnoreCase("")||BLECommandTask.results_value.get("010F").equalsIgnoreCase("NODATA")||BLECommandTask.results_value.get("010F")==null||BLECommandTask.results_value.get("010F").contains("UNABLE")) {
////				flag = false;
////			} 
////			else if (BLECommandTask.results_value.get("010B").equalsIgnoreCase("")||BLECommandTask.results_value.get("010B").equalsIgnoreCase("NODATA")||BLECommandTask.results_value.get("010B")==null||BLECommandTask.results_value.get("010B").contains("UNABLE")) {
////				flag = false;
////			} 
////			else if (BLECommandTask.results_value.get("0133").equalsIgnoreCase("")||BLECommandTask.results_value.get("0133").equalsIgnoreCase("NODATA")||BLECommandTask.results_value.get("0133")==null||BLECommandTask.results_value.get("0133").contains("UNABLE")) {
////				flag = false;
////			} 
////			else if (BLECommandTask.results_value.get("0146").equalsIgnoreCase("")||BLECommandTask.results_value.get("0146").equalsIgnoreCase("NODATA")||BLECommandTask.results_value.get("0146")==null||BLECommandTask.results_value.get("0146").contains("UNABLE")) {
////				flag = false;
////			} 
////			else if (BLECommandTask.results_value.get("010D").equalsIgnoreCase("")||BLECommandTask.results_value.get("010D").equalsIgnoreCase("NODATA")||BLECommandTask.results_value.get("010D")==null||BLECommandTask.results_value.get("010D").contains("UNABLE")) {
////				flag = false;
////			} 
////			else if (BLECommandTask.results_value.get("010D").equalsIgnoreCase("")||BLECommandTask.results_value.get("010D").equalsIgnoreCase("NODATA")||BLECommandTask.results_value.get("010D")==null||BLECommandTask.results_value.get("010D").contains("UNABLE")) {
////				flag = false;
////			} 
////			else if (BLECommandTask.results_value.get("0111").equalsIgnoreCase("")||BLECommandTask.results_value.get("0111").equalsIgnoreCase("NODATA")||BLECommandTask.results_value.get("0111")==null||BLECommandTask.results_value.get("0111").contains("UNABLE")) {
////				flag = false;
////			} 
////			else if (BLECommandTask.results_value.get("010C").equalsIgnoreCase("")||BLECommandTask.results_value.get("010C").equalsIgnoreCase("NODATA")||BLECommandTask.results_value.get("010C")==null||BLECommandTask.results_value.get("010C").contains("UNABLE")) {
////				flag = false;
////			} 
////			else if (BLECommandTask.results_value.get("010A").equalsIgnoreCase("")||BLECommandTask.results_value.get("010A").equalsIgnoreCase("NODATA")||BLECommandTask.results_value.get("010A")==null||BLECommandTask.results_value.get("010A").contains("UNABLE")) {
////				flag = false;
////			} 
////			else if (BLECommandTask.results_value.get("0105").equalsIgnoreCase("")||BLECommandTask.results_value.get("0105").equalsIgnoreCase("NODATA")||BLECommandTask.results_value.get("0105")==null||BLECommandTask.results_value.get("0105").contains("UNABLE")) {
////				flag = false;
////			} 
////			else if (BLECommandTask.results_value.get("0104").equalsIgnoreCase("")||BLECommandTask.results_value.get("0104").equalsIgnoreCase("NODATA")||BLECommandTask.results_value.get("0104")==null||BLECommandTask.results_value.get("0104").contains("UNABLE")) {
////				flag = false;
////			} 
////			else if (BLECommandTask.results_value.get("0902").equalsIgnoreCase("")||BLECommandTask.results_value.get("0902").equalsIgnoreCase("NODATA")||BLECommandTask.results_value.get("0902")==null||BLECommandTask.results_value.get("0902").contains("UNABLE")) {
////				flag = false;
////			} 
//			return flag;
//		}
//	    
//	    //The BroadcastReceiver that listens for bluetooth broadcasts
//	    private final BroadcastReceiver BTReceiver = new BroadcastReceiver() {
//	    public void onReceive(Context context, Intent intent) {
//	        String action = intent.getAction();
//	        if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
//	            Toast.makeText(getApplicationContext(), "BT Connected", Toast.LENGTH_LONG).show();
//	            isProceed=true;
//	            //------------Refreshing  pager and TabIndicator after connect the Socket
////	            try {
////	            	MainActivity.adapter.notifyDataSetChanged();
////            		MainActivity.pager.setAdapter(MainActivity.adapter);
////            		MainActivity.pager.setCurrentItem(1); 
////            		WeakReference<TextView> text = new WeakReference<TextView>(FragmentConnection.Connect);
////					 text.get().setText("Connected.");
////					 text.get().setTextColor(Color.GREEN);
////				} 
////	            catch (Exception e) {
////					e.printStackTrace();
////					//Log.e("", "Error is :" + e.getMessage());
////				} 
//	        }
//	        else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
//	            Toast.makeText(getApplicationContext(), "BT Disconnected", Toast.LENGTH_LONG).show();
//	            MyService.mNotificationManager.cancel(MyService.NOTIFY_ME_ID);
////	            try {
////	            	WeakReference<TextView> text = new WeakReference<TextView>(FragmentConnection.Connect);
////					 text.get().setText("Not Connected.");
////					 text.get().setTextColor(Color.BLACK);
////				} catch (Exception e) {
////					// TODO: handle exception
////				}
////	            
////	            if(MyService.mediaPrefs.getBoolean("Bluetooth", true)){
//	            	  startService(new Intent(MyService.this, autoMaticService.class));
////	            }
////	            else{
//////	            	 startService(new Intent(MyService.this, autoWifiService.class));
////	            }
//	            
//	            
//	          
//	            stopSelf();
//	            try {
//	            	if(sock != null)
//	            		sock.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
////	            resetConnection();
//	            isProceed=false;
//	        }
//	    }
//	    };
//	  //-----Check if Bluetooth is in Range or not   
//	    final BroadcastReceiver bReceiver = new BroadcastReceiver() {
//	         public void onReceive(Context context, Intent intent) {
//	        	 //Log.e("", "Available Reciever");
//	             String action = intent.getAction();
//	             if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//	                  BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//	                  //Log.e("", "Available Device is :" + device.getAddress());
//	                  if(device.getAddress().equalsIgnoreCase(devString)){
////	                	  listnew.add(device.getAddress());
//	                	  isProceed=true;
//	                	  unregisterReceiver(bReceiver);
//	                  }else{
//	                	  isProceed=false;
//	                  }
//	             }
//	         }
//	     };
//	   //--------------------  Method for Uploading Data 
//	     public int uploadFile(String sourceFileUri,String Name,int status) {
//	    	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); // solution for Networkonmainthread exception
//	    	StrictMode.setThreadPolicy(policy); // solution for Networkonmainthread exception
// 	        String fileName = sourceFileUri;
// 	        HttpURLConnection conn = null;
// 	        DataOutputStream dos = null;  
// 	        int bytesRead, bytesAvailable, bufferSize;
// 	        byte[] buffer;
// 	        int maxBufferSize = 1 * 1024 * 1024; 
// 	        File sourceFile = new File(sourceFileUri); 
// 	        if (!sourceFile.isFile()) {
// 	             //Log.e("uploadFile", "Source File not exist :"+uploadFilePath + "" + uploadFileName);
// 	             return 0;
// 	        }
// 	        else
// 	        {
// 	             try { 
// 	           	  String[] name = fileName.split("/");
// 	                 FileInputStream fileInputStream = new FileInputStream(sourceFile);
// 	                 URL url = new URL("http://www.jet-matics.com/JetComService/JetCom.svc/UploadDocument?fname="+Name);
// 	                 //Log.e("", "request is : http://www.jet-matics.com/JetComService/JetCom.svc/UploadDocument?fname="+Name);
// 	                 // Open a HTTP  connection to  the URL
// 	                 conn = (HttpURLConnection) url.openConnection(); 
// 	                 conn.setDoInput(true); // Allow Inputs
// 	                 conn.setDoOutput(true); // Allow Outputs
// 	                 conn.setUseCaches(false); // Don't use a Cached Copy
// 	                 conn.setRequestMethod("POST");
// 	                 conn.setRequestProperty("Connection", "Keep-Alive");
// 	                 conn.setRequestProperty("ENCTYPE", "multipart/form-data");
//// 	                 conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
// 	                 conn.setRequestProperty("uploaded_file", Name); 
// 	                 dos = new DataOutputStream(conn.getOutputStream());
// 	                 // create a buffer of  maximum size
// 	                 bytesAvailable = fileInputStream.available(); 
// 	                 bufferSize = Math.min(bytesAvailable, maxBufferSize);
// 	                 buffer = new byte[bufferSize];
// 	                 bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
// 	                 while (bytesRead > 0) {
// 	                   dos.write(buffer, 0, bufferSize);
// 	                   bytesAvailable = fileInputStream.available();
// 	                   bufferSize = Math.min(bytesAvailable, maxBufferSize);
// 	                   bytesRead = fileInputStream.read(buffer, 0, bufferSize);   
// 	                  }
// 	                 serverResponseCode = conn.getResponseCode();
// 	                 String serverResponseMessage = conn.getResponseMessage();
// 	                 //Log.i("uploadFile", "HTTP Response is : "+ serverResponseMessage + ": " + serverResponseCode);
// 	                 if(serverResponseCode == 200){
// 	                	 if(status==2){
// 	                		 //-----------Delete Offline file after uploading
// 	                		 //----------if Response will get then file will deleted from sdcard
// 	                		  try{
//	    	                    	  	String path11 = Environment.getExternalStorageDirectory().getPath()+"";
//	    	                			File file = new File(path11, "/" + "matics.txt");
//	    	                			file.delete();
//	    						} 
// 	                		  catch (Exception e) {
//	    							e.printStackTrace();
//	    							//Log.e("", "Error in Deleting File");
//	    						}
//	    	                      isfirst=false; 
// 	                	 }
// 	                	 //-----------Delete Online Data Storing file after uploading
// 	                	 else if (status==1) {
// 	                		 try{
// 	                			 		String path11 = Environment.getExternalStorageDirectory().getPath()+"";
//	    	                			File file = new File(path11, "/" + "online.txt");
//	    	                			file.delete();
//	    						} 
// 	                		 catch (Exception e) {
//	    							e.printStackTrace();
//	    							//Log.e("", "Error in Deleting File");
//	    						}
//							}
// 	                 }   
// 	                 fileInputStream.close();
// 	                 dos.flush();
// 	                 dos.close();
// 	            } 
// 	             catch (MalformedURLException ex) {
// 	                ex.printStackTrace();
// 	                //Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
// 	            } 
// 	             catch (Exception e) {
// 	                e.printStackTrace();
// 	                //Log.e("Upload file to server Exception", "Exception : "+ e.getMessage(), e);
// 	            }
// 	            return serverResponseCode; 
// 	         }  // End else block 
// 	       }
//	     private static BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
//	         if(Build.VERSION.SDK_INT >= 10){
//	             try {
//	                 final Method  m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
//	                 return (BluetoothSocket) m.invoke(device, MY_UUID);
//	             } catch (Exception e) {
//	                 //Log.e(TAG, "Could not create Insecure RFComm Connection",e);
//	             }
//	         }
//	         return  device.createRfcommSocketToServiceRecord(MY_UUID);
//	     }
}
