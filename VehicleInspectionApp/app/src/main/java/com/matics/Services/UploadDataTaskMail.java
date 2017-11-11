package com.matics.Services;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.matics.fragments.FragmentConnection;
import com.matics.LivePhoneReadings;
import com.matics.MainActivity;
import com.matics.adapter.DataBaseHelper;
import com.matics.command.ObdCommand;
import com.matics.model.VehicleProfileModel;
import com.matics.serverTasks.GetProfileTask;
import com.matics.Utils.GPSTracker;
import com.matics.Utils.MyService;
import com.matics.Utils.Utility;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.ListView;


public class UploadDataTaskMail extends AsyncTask<String, Integer, Object> {

	private final Context mContext;
	protected boolean stop = false;
	protected ArrayList<ObdCommand> cmds = null;
	protected Map<String, String> results;
	protected int updateCycle = 4000;
	public static String result;
	ListView listview_main;
	SimpleDateFormat sdf;
	String DeviceTime,make,model,mileage;
	int year;
	DataBaseHelper myDbHelper;
	protected Location currentLocation = null;
	protected LocationManager locationManager = null;
	double LAtitude,Longitude;
	GPSTracker gps;
	final String uploadFilePath = "/storage/emulated/0/";
    final String uploadFileName = "matics.txt";
    int serverResponseCode = 0;
    Boolean isfirst;
    String w_Account_id,w_mobile_pro_id="0",w_tblcd,W_VIN, VehicalIN;
    List<VehicleProfileModel>data =null;
	public static Boolean apiFlag = true;

	public UploadDataTaskMail(Context mContext,Map<String, String> result) {
		this.mContext = mContext;
		MyService.mediaPrefs= mContext.getSharedPreferences("matics", 1);
		this.results = result;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Object doInBackground(String... params) {
		
//		ExecuteCommands(params[0], params[1], params[2],params[3], params[4]);
		
		Boolean isnet1 = Utility.checkInternetConnection(MainActivity.mContext);
	    try {
			if(isnet1){
				//Log.dMainActivity.TAG, "available");
				
//				Upload file in server			
				
				File fileMatics = new File(Environment.getExternalStorageDirectory()+"/Matics/matics.txt");
				if(fileMatics.exists()){
					System.out.println(fileMatics.length());
					
					if(fileMatics.length() != 0){
						uploadFile(Environment.getExternalStorageDirectory()+"/Matics/matics.txt","matics.txt",2);
					}
				}
				
				
					
					uploadFile(Environment.getExternalStorageDirectory()+"/Matics/online.txt","online.txt",1);
//					uploadFile(Environment.getExternalStorageDirectory()+"/matics_motoe.txt","matics_motoe.txt",1);
//					uploadFile(Environment.getExternalStorageDirectory()+"/matics_motog.txt","matics_motog.txt",1);
//					uploadFile(Environment.getExternalStorageDirectory()+"/matics_samsung.txt","matics_samsung.txt",1);
			}
			else{
				//Log.dMainActivity.TAG, "Not available");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(Object result) {
		super.onPostExecute(result);
		if(apiFlag){
			Boolean isnet1 = Utility.checkInternetConnection(MainActivity.mContext);
			if(isnet1){
				try {
					myDbHelper = new DataBaseHelper(mContext);
					if(VehicalIN.length()==17){
						GetProfileTask task = new GetProfileTask(mContext);
						task.execute(VehicalIN);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				apiFlag=false;
			}
			
		}
		
	}
	@Override
	protected void onCancelled() {
		super.onCancelled();
	}
	private Object ExecuteCommands(String lat,String Lon,String Gpsspeed,String GPStime, String btId) {
		String result = null;
		String getServerPath = MainActivity.Upload_Url;
		myDbHelper = new DataBaseHelper(mContext);
		try {
			//data = myDbHelper.getVehicleProfileDataWithVin(results.get("0902"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		String fault = null;
		ContentValues values = new ContentValues();
		try {
			sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");// date formate
			String currentDateandTime = sdf.format(new Date());
			DeviceTime = currentDateandTime.toString();
			//Log.e("", "VIN is:" + results.get("0902"));
			
	
//			MainActivity.storeStringUserDetails(MainActivity.LastUpload,DeviceTime);
			String PHONE_ID = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);
			values.put("Android_Phone_ID", PHONE_ID);
//			values.put("Android_Phone_ID", String.valueOf("ce6acb5414f4be96")));
			values.put("Throttle_Positions", String.valueOf(results.get("0111")));
			values.put("Engine_RPM", String.valueOf(results.get("010C")));
			values.put("Engine_Load",  String.valueOf(results.get("0104")));
			values.put("Barometric_Press",  String.valueOf(results.get("0133")));
			values.put("Coolant_Tempture",String.valueOf(results.get("0105")));
			values.put("latitude", String.valueOf(lat));
			values.put("longitude", String.valueOf(Lon));
			values.put("Intake_Manifold_Press", String.valueOf(results.get("010B")));
			values.put("Vehicle_Speed", String.valueOf(results.get("010D")));
			values.put("DeviceTime", String.valueOf(DeviceTime));
//			values.put("VIN", String.valueOf("1FTEX1EM0DFD13619s")));
//			VehicalIN="1FTEX1EM0DFD13619";
//			MALBB51RLCM430690	1FTEX1EM0DFD13619	
			if(String.valueOf(results.get("0902")).length()!=17){
				values.put("VIN", String.valueOf(results.get("0902")));
				VehicalIN=String.valueOf(results.get("0902"));
				W_VIN = MyService.mediaPrefs.getString(MyService.devString, "");
			}else{
				values.put("VIN", String.valueOf(results.get("0902")));
				W_VIN = String.valueOf(results.get("0902"));
				VehicalIN=String.valueOf(results.get("0902"));
			}
			
			values.put("TotalMiles", String.valueOf(results.get("0131")));
//			nameValuePairs.add(new BasicNameValuePair("AccountId", String.valueOf(MainActivity.mediaPrefs.getString(MainActivity.AccountID, ""))));
			if(String.valueOf(results.get("03"))==null||String.valueOf(results.get("03")).equalsIgnoreCase("") || String.valueOf(results.get("03")).equalsIgnoreCase("null")){
				fault="";
				w_tblcd = "";
			}else{
				fault = String.valueOf(results.get("03"));
				w_tblcd= String.valueOf(results.get("03"));
			}
			values.put("FaultCodes",  String.valueOf(fault));
			//if(MainActivity.mediaPrefs.getString(MainActivity.AccountID, "")==null ||MainActivity.mediaPrefs.getString(MainActivity.AccountID, "").equalsIgnoreCase("")){
				values.put("AccountId", String.valueOf("0"));
				w_Account_id = String.valueOf("0");
			//}
			//else {
//				nameValuePairs.add(new BasicNameValuePair("AccountId", String.valueOf(MainActivity.mediaPrefs.getString(MainActivity.AccountID, ""))));
//				w_Account_id = String.valueOf(MyService.mediaPrefs.getString(MainActivity.AccountID, ""));
//			}
			if(data.size() != 0){
				values.put("Year", String.valueOf(data.get(0).MFYear));
				values.put("Make", String.valueOf(data.get(0).Make));
				values.put("Model", String.valueOf(data.get(0).Model));
				values.put("Mileage", String.valueOf(data.get(0).Mileage));
			}
			else{
				values.put("Year", String.valueOf("0"));
				values.put("Make", String.valueOf(""));
				values.put("Model", String.valueOf(""));
				values.put("Mileage", String.valueOf(""));
			}
			
			values.put("Fuel_Press", String.valueOf(results.get("010A")));
			values.put("GPS_Speed", String.valueOf(Gpsspeed));
			values.put("GPS_Time", String.valueOf(GPStime));
			values.put("Obs_Time", Long.toString(System.currentTimeMillis()/1000));
			values.put("Ambient_Air_Temp", String.valueOf(results.get("0146")));
			values.put("Air_Intake_Temp", String.valueOf(results.get("010F")));
			
//			if(MainActivity.mediaPrefs.getString(MainActivity.MobileUserProfileId, "")==null ||MainActivity.mediaPrefs.getString(MainActivity.MobileUserProfileId, "").equalsIgnoreCase("")){
				values.put("MobileUserProfileId", String.valueOf("0"));
				w_mobile_pro_id =  String.valueOf("0");
//			}
//			else{
				values.put("MobileUserProfileId", String.valueOf(LivePhoneReadings.getMobileUserProfileId()));
			//	w_mobile_pro_id = String.valueOf(MainActivity.mediaPrefs.getString(MainActivity.MobileUserProfileId, ""));
			//}
			
			//Log.e("", "Last time is :" + DeviceTime);
			if(btId.trim() == null){
				values.put("BtId", String.valueOf(""));
			}else{
				values.put("BtId", String.valueOf(btId.trim()));
			}
			
	//------------------------------------------New Commands---------------------------------------------------
			values.put("Fuel_Level_Input", String.valueOf(results.get("012F")));
			values.put("Fuel_status", String.valueOf(results.get("0103")));
			values.put("Timing_Advance", String.valueOf(results.get("0103")));
			values.put("Air_Flow_Rate", String.valueOf(results.get("0110")));
			values.put("OBD_Standard", String.valueOf(results.get("011C")));
			values.put("Oxygen_sensors_present", String.valueOf(results.get("011D")));
			values.put("Run_time", String.valueOf(results.get("011F")));
			values.put("Fuel_Rail_Pressure", String.valueOf(results.get("0123")));
			values.put("Commanded_evaporative_purge", String.valueOf(results.get("012E")));
			values.put("System_Vapor_Pressure", String.valueOf(results.get("0132")));
			values.put("Control_module_voltage", String.valueOf(results.get("0142")));
			values.put("Absolute_Load_Value", String.valueOf(results.get("0143")));
			values.put("Equivalence_ratio", String.valueOf(results.get("0144")));
			values.put("Fuel_Type", String.valueOf(results.get("0151")));
			values.put("Ethanol_fuel", String.valueOf(results.get("0152")));
			
			try {
				values.put("License_Num", String.valueOf(data.get(0).License_Num));
				values.put("License_State", String.valueOf(data.get(0).License_State));
			} catch (Exception e) {
				values.put("License_Num", String.valueOf(""));
				values.put("License_State", String.valueOf(""));
			}
			
			values.put("Monitor_status", String.valueOf(results.get("0101")));
			values.put("Short_term_fuel_B1", String.valueOf(results.get("0106")));
			values.put("Long_term_fuel_B1", String.valueOf(results.get("0107")));
			values.put("Short_term_fuel_B2", String.valueOf(results.get("0108")));
			values.put("Long_term_fuel_B2", String.valueOf(results.get("0109")));
			values.put("Oxy_sensor_present", String.valueOf(results.get("0113")));
			values.put("Oxy_sensor_voltage_B1S1", String.valueOf(results.get("0114")));
			values.put("Oxy_sensor_voltage_B1S2", String.valueOf(results.get("0115")));
			values.put("Oxy_sensor_voltage_B1S3", String.valueOf(results.get("0116")));
			values.put("Oxy_sensor_voltage_B1S4", String.valueOf(results.get("0117")));
			values.put("Oxy_sensor_voltage_B2S1", String.valueOf(results.get("0118")));
			values.put("Oxy_sensor_voltage_B2S2", String.valueOf(results.get("0119")));
			values.put("Oxy_sensor_voltage_B2S3", String.valueOf(results.get("011A")));
			values.put("Oxy_sensor_voltage_B2S4", String.valueOf(results.get("011B")));
			values.put("Auxiliary_input_status", String.valueOf(results.get("011E")));
			values.put("PID_supported", String.valueOf(results.get("0120")));
			values.put("Equivalence_Ratio_O2S1_V", String.valueOf(results.get("0124")));
			values.put("Equivalence_Ratio_O2S2_V", String.valueOf(results.get("0125")));
			values.put("Equivalence_Ratio_O2S3_V", String.valueOf(results.get("0126")));
			values.put("Equivalence_Ratio_O2S4_V", String.valueOf(results.get("0127")));
			values.put("Equivalence_Ratio_O2S5_V", String.valueOf(results.get("0128")));
			values.put("Equivalence_Ratio_O2S6_V", String.valueOf(results.get("0129")));
			values.put("Equivalence_Ratio_O2S7_V", String.valueOf(results.get("012A")));
			values.put("Equivalence_Ratio_O2S8_V", String.valueOf(results.get("012B")));
			values.put("Commanded_EGR", String.valueOf(results.get("012C")));
			values.put("EGR_Error", String.valueOf(results.get("012D")));
			values.put("No_WarmUps", String.valueOf(results.get("0130")));
			values.put("Dist_Traveled", String.valueOf(results.get("0131")));
			values.put("Equivalence_Ratio_O2S1_C", String.valueOf(results.get("0134")));
			values.put("Equivalence_Ratio_O2S2_C", String.valueOf(results.get("0135")));
			values.put("Equivalence_Ratio_O2S3_C", String.valueOf(results.get("0136")));
			values.put("Equivalence_Ratio_O2S4_C", String.valueOf(results.get("0137")));
			values.put("Equivalence_Ratio_O2S5_C", String.valueOf(results.get("0138")));
			values.put("Equivalence_Ratio_O2S6_C", String.valueOf(results.get("0139")));
			values.put("Equivalence_Ratio_O2S7_C", String.valueOf(results.get("013A")));
			values.put("Equivalence_Ratio_O2S8_C", String.valueOf(results.get("013B")));
			values.put("Catalyst_Temp_B1S1", String.valueOf(results.get("013C")));
			values.put("Catalyst_Temp_B2S1", String.valueOf(results.get("013D")));
			values.put("Catalyst_Temp_B1S2", String.valueOf(results.get("013E")));
			values.put("Catalyst_Temp_B2S2", String.valueOf(results.get("013F")));
			values.put("PID_supported_41", String.valueOf(results.get("0140")));
			values.put("Monitor_status_Cycle", String.valueOf(results.get("0141")));
			values.put("Rela_Throttle_position", String.valueOf(results.get("0145")));
			values.put("Absolute_Throttle_position_B", String.valueOf(results.get("0147")));
			values.put("Absolute_Throttle_position_C", String.valueOf(results.get("0148")));
			values.put("Accelerator_pedal_position_D", String.valueOf(results.get("0149")));
			values.put("Accelerator_pedal_position_E", String.valueOf(results.get("014A")));
			values.put("Accelerator_pedal_position_F", String.valueOf(results.get("014B")));
			values.put("Commanded_throttle_actuator", String.valueOf(results.get("014C")));
			values.put("Time_MIL", String.valueOf(results.get("014D")));
			values.put("Time_trouble_code", String.valueOf(results.get("014E")));
			values.put("Evap_system_Vapour_Pressure", String.valueOf(results.get("0153")));
			values.put("Freeze_frame_trouble_code", String.valueOf(results.get("0202")));
			values.put("Clear_trouble_code", String.valueOf(results.get("04")));
			values.put("OBD_Monitor_ID_Supported", String.valueOf(results.get("050100")));
			values.put("O2_sensor_Monitor_B1S1_Learn", String.valueOf(results.get("050101")));
			values.put("O2_sensor_Monitor_B1S2_Learn", String.valueOf(results.get("050102")));
			values.put("O2_sensor_Monitor_B1S3_Learn", String.valueOf(results.get("050103")));
			values.put("O2_sensor_Monitor_B1S4_Learn", String.valueOf(results.get("050104")));
			values.put("O2_sensor_Monitor_B2S1_Learn", String.valueOf(results.get("050105")));
			values.put("O2_sensor_Monitor_B2S2_Learn", String.valueOf(results.get("050106")));
			values.put("O2_sensor_Monitor_B2S3_Learn", String.valueOf(results.get("050107")));
			values.put("O2_sensor_Monitor_B2S4_Learn", String.valueOf(results.get("050108")));
			values.put("O2_sensor_Monitor_B3S1_Learn", String.valueOf(results.get("050109")));
			values.put("O2_sensor_Monitor_B3S2_Learn", String.valueOf(results.get("05010A")));
			values.put("O2_sensor_Monitor_B3S3_Learn", String.valueOf(results.get("05010B")));
			values.put("O2_sensor_Monitor_B3S4_Learn", String.valueOf(results.get("05010C")));
			values.put("O2_sensor_Monitor_B4S1_Learn", String.valueOf(results.get("05010D")));
			values.put("O2_sensor_Monitor_B4S2_Learn", String.valueOf(results.get("05010E")));
			values.put("O2_sensor_Monitor_B4S3_Learn", String.valueOf(results.get("05010F")));
			values.put("O2_sensor_Monitor_B4S4_Learn", String.valueOf(results.get("050110")));
			values.put("O2_sensor_Monitor_B1S1_Rich", String.valueOf(results.get("050201")));
			values.put("O2_sensor_Monitor_B1S2_Rich", String.valueOf(results.get("050202")));
			values.put("O2_sensor_Monitor_B1S3_Rich", String.valueOf(results.get("050203")));
			values.put("O2_sensor_Monitor_B1S4_Rich", String.valueOf(results.get("050204")));
			values.put("O2_sensor_Monitor_B2S1_Rich", String.valueOf(results.get("050205")));
			values.put("O2_sensor_Monitor_B2S2_Rich", String.valueOf(results.get("050206")));
			values.put("O2_sensor_Monitor_B2S3_Rich", String.valueOf(results.get("050207")));
			values.put("O2_sensor_Monitor_B2S4_Rich", String.valueOf(results.get("050208")));
			values.put("O2_sensor_Monitor_B3S1_Rich", String.valueOf(results.get("050209")));
			values.put("O2_sensor_Monitor_B3S2_Rich", String.valueOf(results.get("05020A")));
			values.put("O2_sensor_Monitor_B3S3_Rich", String.valueOf(results.get("05020B")));
			values.put("O2_sensor_Monitor_B3S4_Rich", String.valueOf(results.get("05020C")));
			values.put("O2_sensor_Monitor_B4S1_Rich", String.valueOf(results.get("05020D")));
			values.put("O2_sensor_Monitor_B4S2_Rich", String.valueOf(results.get("05020E")));
			values.put("O2_sensor_Monitor_B4S3_Rich", String.valueOf(results.get("05020F")));
			values.put("O2_sensor_Monitor_B4S4_Rich", String.valueOf(results.get("050210")));
			values.put("Mode_9_supported", String.valueOf(results.get("0900")));
			values.put("Fuel_Rail_Pressure_D", String.valueOf(results.get("0122")));
			
//			result = Utility.postRequestonlyforupload(getServerPath, nameValuePairs,mContext);
		} catch (Exception e) {
			e.printStackTrace();
			//Log.e("upload err", ""+e.getMessage());
			isfirst=true;
		}
		return null;
	}
	
		@SuppressLint("NewApi")
		private void uploadFile(String sourceFileUri,String Name,int status) {
			// TODO Auto-generated method stub

	    	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); // solution for Networkonmainthread exception
	    	StrictMode.setThreadPolicy(policy); // solution for Networkonmainthread exception
		        String fileName = sourceFileUri;
		        HttpURLConnection conn = null;
		        DataOutputStream dos = null;  
		        int bytesRead, bytesAvailable, bufferSize;
		        byte[] buffer;
		        int maxBufferSize = 1 * 1024 * 1024; 
		        File sourceFile = new File(sourceFileUri); 
		        if (!sourceFile.isFile()) {
		             return ;
		        }
		        else
		        {
		             try { 
           	  			String[] name = fileName.split("/");
           	  			FileInputStream fileInputStream = new FileInputStream(sourceFile);
           	  			URL url = new URL("http://www.jet-matics.com/JetComService/JetCom.svc/UploadDocument?fname="+Name);
		                
		                
//		              	URL url = new URL("http://192.168.1.21:9991/JetCom.svc/UploadDocument?fname="+Name);
           	  			//Log.e("", "post is : http://www.jet-matics.com/JetComService/JetCom.svc/UploadDocument?fname="+Name);
           	  			// Open a HTTP  connection to  the URL
           	  			conn = (HttpURLConnection) url.openConnection(); 
           	  			conn.setDoInput(true); // Allow Inputs
           	  			conn.setDoOutput(true); // Allow Outputs
           	  			conn.setUseCaches(false); // Don't use a Cached Copy
           	  			conn.setRequestMethod("POST");
           	  			conn.setRequestProperty("Connection", "Keep-Alive");
           	  			conn.setRequestProperty("ENCTYPE", "multipart/form-data");
           	  			conn.setRequestProperty("uploaded_file", Name); 
           	  			dos = new DataOutputStream(conn.getOutputStream());
		                 // create a buffer of  maximum size
           	  			bytesAvailable = fileInputStream.available(); 
           	  			bufferSize = Math.min(bytesAvailable, maxBufferSize);
           	  			buffer = new byte[bufferSize];
           	  			bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
           	  			
           	  			while (bytesRead > 0) {
		                   dos.write(buffer, 0, bufferSize);
		                   bytesAvailable = fileInputStream.available();
		                   bufferSize = Math.min(bytesAvailable, maxBufferSize);
		                   bytesRead = fileInputStream.read(buffer, 0, bufferSize);   
           	  			}
		                 
           	  			serverResponseCode = conn.getResponseCode();
           	  			String serverResponseMessage = conn.getResponseMessage();
           	  			//Log.e("uploadFile", "HTTP Response is : Server"+ serverResponseMessage + ": " + serverResponseCode);
	                        
           	  			if(serverResponseCode == 200){
		                	 
		                	 	InputStream in = conn.getInputStream();
		           			    byte data[] = new byte[1024];
		           			    int counter = -1;
		           			    String jsonString = "";
		           			    while( (counter = in.read(data)) != -1){
		           			         jsonString += new String(data, 0, counter);
		           			    }
		           			    //Log.dMainActivity.TAG, "HTTP Response is JSON String: " + jsonString);
		           			     
			           			JSONObject json=new JSONObject(jsonString);
			          			FragmentConnection.vehicalInfo=json.getString("VehicleHealth");
			          			//Log.e("Vehical Health", "HTTP Response Vehical Health is:"+FragmentConnection.vehicalInfo);
		          			
			          			// Vehical Response write in file
			          			writefile("Vehical Health is:"+FragmentConnection.vehicalInfo);
			          			
			                	if(status==2){
			                		 //-----------Delete Offline file after uploading
			                		 //----------if Response will get then file will deleted from sdcard
			                		  try{
			                			
		    	                    	  	String path11 = Environment.getExternalStorageDirectory().getPath()+"";
		    	                			File file = new File(path11, "/" + "matics.txt");
		    	                			file.delete();
		    	        				} 
			                		  catch (Exception e) {
			    							e.printStackTrace();
			    							//Log.e("", "Error in Deleting File");
			    						}
		    	                     
			                	}
			                	else if (status==1) {
			                		try{
			                			 	String path11 = Environment.getExternalStorageDirectory().getPath()+"";
		    	                			File file = new File(path11, "/" + "online.txt");
		    	                			file.delete();
		    						} 
			                		catch (Exception e) {
		    							e.printStackTrace();
		    							//Log.e("", "Error in Deleting File");
		    						}
								}
		                 }   
		                 fileInputStream.close();
		                 dos.flush();
		                 dos.close();
		            } 
		             catch (MalformedURLException ex) {
		                ex.printStackTrace();
		                //Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
		            } 
		             catch (Exception e) {
		                e.printStackTrace();
		                //Log.e("Upload file to server Exception", "Exception : "+ e.getMessage(), e);
		            }
		           
		         }  // End else block 
		       
		}

	// Vehical Response write in file
	 private void writefile(String data) {
			// TODO Auto-generated method stub
	    	try {
	    		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss EEEE");
	    		String currentDateandTime = sdf.format(new Date());
				File f1 = new File(Environment.getExternalStorageDirectory().getPath()+ "/Matics/VehicalInfo.txt");
				BufferedWriter out = new BufferedWriter(new FileWriter(f1, true));
				out.write("\n\n"+"["+currentDateandTime+"]  \n"+data + "\n\n");
				out.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	 
	
}
