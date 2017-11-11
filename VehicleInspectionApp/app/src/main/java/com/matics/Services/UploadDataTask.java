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

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.util.Log;
import android.widget.ListView;

import com.matics.MainActivity;
import com.matics.fragments.FragmentConnection;
import com.matics.Bluetooth.ResponceStatus;
import com.matics.adapter.DataBaseHelper;
import com.matics.command.ObdCommand;
import com.matics.model.VehicleProfileModel;
import com.matics.serverTasks.GetProfileTask;
import com.matics.Utils.GPSTracker;
import com.matics.Utils.MyService;
import com.matics.Utils.Utility;


public class UploadDataTask extends AsyncTask<String, Integer, Object> {

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
	String PHONE_ID;
	Boolean isnet1;
	ResponceStatus resStatus=new ResponceStatus();
	public UploadDataTask(Context mContext,Map<String, String> result) {
		this.mContext = mContext;
		myDbHelper = new DataBaseHelper(mContext);
		MyService.mediaPrefs= mContext.getSharedPreferences("matics", 1);
		this.results = result;
		isnet1 = Utility.checkInternetConnection(mContext);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
		PHONE_ID = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);
		
		//data = myDbHelper.getVehicleProfileDataWithVin(results.get("0902"));
		
		if(String.valueOf(results.get("0902")).length()==17){
			
			
			try {
				
					if(data.size() != 0){
						if(String.valueOf(data.get(0).Make).toString().trim().length() != 0 && String.valueOf(data.get(0).Model).toString().trim().length() != 0 && String.valueOf(data.get(0).MFYear).toString().trim().length() == 0){
							VehicalIN=String.valueOf(results.get("0902"));
//							MainActivity.storeStringUserDetails(MainActivity.VIN, VehicalIN);
							GetProfileTask task = new GetProfileTask(mContext);
							task.execute(VehicalIN);
						}
					}
					else{
						VehicalIN=String.valueOf(results.get("0902"));
//						MainActivity.storeStringUserDetails(MainActivity.VIN, VehicalIN);
						GetProfileTask task = new GetProfileTask(mContext);
						task.execute(VehicalIN);
					}
			} catch (Exception e) {
				e.printStackTrace();
			}	
			
			 //------------------Adding/Updating  Vehicle health Array in database from which graph will be draw			
  			try {
  				DataBaseHelper db  = new DataBaseHelper(mContext);
  				if(!db.CheckHealthVin(VehicalIN)){
  					db.addhealth(VehicalIN, FragmentConnection.vehicleInfoList);
  				}
  				else{
  					db.updatehealthArray(VehicalIN, FragmentConnection.vehicleInfoList);
  				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
//			
//			
		}
		
		
	}

	@Override
	protected Object doInBackground(String... params) {
//		ExecuteCommands(params[0], params[1], params[2],params[3], params[4]);
//		
	    try {
			if(isnet1){
				//Log.dMainActivity.TAG, "available");
				
//				Upload file in server			
				
//				File fileMatics = new File(Environment.getExternalStorageDirectory()+"/Matics/matics.txt");
//				if(fileMatics.exists()){
//					System.out.println(fileMatics.length());
//					
//					if(fileMatics.length() != 0){
//						uploadFile(Environment.getExternalStorageDirectory()+"/Matics/matics.txt","matics.txt",2);
//					}
//				}
				return uploadFile(Environment.getExternalStorageDirectory()+"/Matics/online.txt","online.txt",1);
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
		//Log.e("", "Denish vikani in post execute");
		if (result != null) {
			ResponceStatus mStatus = (ResponceStatus) result;
			JSONObject json;
			try {
				json = new JSONObject(mStatus.getResultJson());
				// -------------Getting Single Vehicle Health which is displayed
				// in speedometer
				FragmentConnection.vehicalInfo = json.getString("OneVehicleHealth_");
				//Log.e("Vehical Health", "HTTP Response Vehical Health is:"+ FragmentConnection.vehicalInfo);
				// ---------------Getting Array of Vehicle Health array from which graph will be draw
				JSONArray jArray = json.getJSONArray("VehicleHealth");
				//Log.e("Vehical jArray " + jArray.length(),"" + jArray.toString());
				FragmentConnection.vehicleInfoList.clear();
				for (int i = 0; i < jArray.length(); i++) {
					if (jArray.get(i).toString().equals("0"))
						break;
					else
						FragmentConnection.vehicleInfoList.add(Integer.parseInt((jArray.get(i).toString())));
				}

				// ---------------- Updating Health Vehical Response write in file.
				//LivePhoneReadings.VehicleHealthValue() = Integer.parseInt(FragmentConnection.vehicalInfo);
				DataBaseHelper Mydb = new DataBaseHelper(mContext);
				//Mydb.updatehealth(MainActivity.finalvehicle_health);
				writefile("Upload data response: " + json.toString());
			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				String path11 = Environment.getExternalStorageDirectory()
						.getPath() + "";
				File file = new File(path11, "/" + "online.txt");
				file.delete();
			} catch (Exception e) {
				e.printStackTrace();
				//Log.e("", "Error in Deleting File");
			}
		}

		try {
			//MainActivity.finalvehicle_health = Integer.parseInt(FragmentConnection.vehicalInfo);
			//DataBaseHelper Mydb = new DataBaseHelper(mContext);
			//Mydb.updatehealth(MainActivity.finalvehicle_health);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		
//		if(apiFlag){
//			Boolean isnet1 = Utility.checkInternetConnection(MainActivity.mContext);
//			if(isnet1){
//				try {
//					myDbHelper = new DataBaseHelper(mContext);
//					if(VehicalIN.length()==17){
//						GetProfileTask task = new GetProfileTask(mContext);
//						task.execute(VehicalIN);
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//					//Log.e("", "Error : " + e.getMessage());
//				}
//				apiFlag=false;
//			}
//			
//		}
		
	}
	@Override
	protected void onCancelled() {
		super.onCancelled();
	}
//	private Object ExecuteCommands(String lat,String Lon,String Gpsspeed,String GPStime, String btId) {
//		String result = null;
//		String getServerPath = MainActivity.Upload_Url;
//		
//		String fault = null;
//		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
//		try {
//			sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");// date formate
//			String currentDateandTime = sdf.format(new Date());
//			DeviceTime = currentDateandTime.toString();
//			//Log.e("", "VIN is:" + results.get("0902"));
//	
//			MainActivity.storeStringUserDetails(MainActivity.LastUpload,DeviceTime);
//			
//			nameValuePairs.add(new BasicNameValuePair("Android_Phone_ID", PHONE_ID));
////			nameValuePairs.add(new BasicNameValuePair("Android_Phone_ID", String.valueOf("ce6acb5414f4be96")));
//			nameValuePairs.add(new BasicNameValuePair("Throttle_Positions", String.valueOf(results.get("0111"))));
//			nameValuePairs.add(new BasicNameValuePair("Engine_RPM", String.valueOf(results.get("010C"))));
//			nameValuePairs.add(new BasicNameValuePair("Engine_Load",  String.valueOf(results.get("0104"))));
//			nameValuePairs.add(new BasicNameValuePair("Barometric_Press",  String.valueOf(results.get("0133"))));
//			nameValuePairs.add(new BasicNameValuePair("Coolant_Tempture",String.valueOf(results.get("0105"))));
//			nameValuePairs.add(new BasicNameValuePair("latitude", String.valueOf(lat)));
//			nameValuePairs.add(new BasicNameValuePair("longitude", String.valueOf(Lon)));
//			nameValuePairs.add(new BasicNameValuePair("Intake_Manifold_Press", String.valueOf(results.get("010B"))));
//			nameValuePairs.add(new BasicNameValuePair("Vehicle_Speed", String.valueOf(results.get("010D"))));
//			nameValuePairs.add(new BasicNameValuePair("DeviceTime", String.valueOf(DeviceTime)));			
////			nameValuePairs.add(new BasicNameValuePair("VIN", String.valueOf("1FTEX1EM0DFD13619")));
////			nameValuePairs.add(new BasicNameValuePair("VIN", String.valueOf("MALBB51RLCM430690")));
////			VehicalIN="MALBB51RLCM430690";
////			MainActivity.storeStringUserDetails(MainActivity.VIN, VehicalIN);
////			MALBB51RLCM430690	1FTEX1EM0DFD13619	
//			if(String.valueOf(results.get("0902")).length()!=17){
//				nameValuePairs.add(new BasicNameValuePair("VIN", String.valueOf(results.get("0902"))));
//				VehicalIN=String.valueOf(results.get("0902"));
//				MainActivity.storeStringUserDetails(MainActivity.VIN, VehicalIN);
//				W_VIN = MyService.mediaPrefs.getString(MyService.devString, "");
//			}else{
//				nameValuePairs.add(new BasicNameValuePair("VIN", String.valueOf(results.get("0902"))));
//				W_VIN = String.valueOf(results.get("0902"));
//				MainActivity.storeStringUserDetails(MainActivity.VIN, W_VIN);
//				VehicalIN=String.valueOf(results.get("0902"));
//			}
//			nameValuePairs.add(new BasicNameValuePair("TotalMiles", String.valueOf(results.get("0131"))));
////			nameValuePairs.add(new BasicNameValuePair("AccountId", String.valueOf(MainActivity.mediaPrefs.getString(MainActivity.AccountID, ""))));
//			if(String.valueOf(results.get("03"))==null||String.valueOf(results.get("03")).equalsIgnoreCase("") || String.valueOf(results.get("03")).equalsIgnoreCase("null")){
//				fault="";
//				w_tblcd = "";
//			}else{
//				fault = String.valueOf(results.get("03"));
//				w_tblcd= String.valueOf(results.get("03"));
//			}
//			nameValuePairs.add(new BasicNameValuePair("FaultCodes",  String.valueOf(fault)));
//			if(MainActivity.mediaPrefs.getString(MainActivity.AccountID, "")==null ||MainActivity.mediaPrefs.getString(MainActivity.AccountID, "").equalsIgnoreCase("")){
//				nameValuePairs.add(new BasicNameValuePair("AccountId", String.valueOf("0")));
//				w_Account_id = String.valueOf("0");
//			}
//			else {
//				nameValuePairs.add(new BasicNameValuePair("AccountId", String.valueOf(MainActivity.mediaPrefs.getString(MainActivity.AccountID, ""))));
//				w_Account_id = String.valueOf(MyService.mediaPrefs.getString(MainActivity.AccountID, ""));
//			}
//			if(data.size() != 0){
//				nameValuePairs.add(new BasicNameValuePair("Year", String.valueOf(data.get(0).MFYear)));
//				nameValuePairs.add(new BasicNameValuePair("Make", String.valueOf(data.get(0).Make)));
//				nameValuePairs.add(new BasicNameValuePair("Model", String.valueOf(data.get(0).Model)));
//				nameValuePairs.add(new BasicNameValuePair("Mileage", String.valueOf(data.get(0).Mileage)));
//			}
//			else{
//				nameValuePairs.add(new BasicNameValuePair("Year", String.valueOf("0")));
//				nameValuePairs.add(new BasicNameValuePair("Make", String.valueOf("")));
//				nameValuePairs.add(new BasicNameValuePair("Model", String.valueOf("")));
//				nameValuePairs.add(new BasicNameValuePair("Mileage", String.valueOf("")));
//			}
//			
//			nameValuePairs.add(new BasicNameValuePair("Fuel_Press", String.valueOf(results.get("010A"))));
//			nameValuePairs.add(new BasicNameValuePair("GPS_Speed", String.valueOf(Gpsspeed)));
//			nameValuePairs.add(new BasicNameValuePair("GPS_Time", String.valueOf(GPStime)));
//			nameValuePairs.add(new BasicNameValuePair("Obs_Time", Long.toString(System.currentTimeMillis()/1000)));
//			nameValuePairs.add(new BasicNameValuePair("Ambient_Air_Temp", String.valueOf(results.get("0146"))));
//			nameValuePairs.add(new BasicNameValuePair("Air_Intake_Temp", String.valueOf(results.get("010F"))));
//			
//			if(MainActivity.mediaPrefs.getString(MainActivity.MobileUserProfileId, "")==null ||MainActivity.mediaPrefs.getString(MainActivity.MobileUserProfileId, "").equalsIgnoreCase("")){
//				nameValuePairs.add(new BasicNameValuePair("MobileUserProfileId", String.valueOf("0")));
//				w_mobile_pro_id =  String.valueOf("0");
//			}
//			else{
//				nameValuePairs.add(new BasicNameValuePair("MobileUserProfileId", String.valueOf(MainActivity.mediaPrefs.getString(MyService.MobileUserProfileId, ""))));
//				w_mobile_pro_id = String.valueOf(MainActivity.mediaPrefs.getString(MainActivity.MobileUserProfileId, ""));
//			}
//			
//			//Log.e("", "Last time is :" + DeviceTime);
//			if(btId.trim() == null){
//				nameValuePairs.add(new BasicNameValuePair("BtId", String.valueOf("")));
//			}
//			else{
//				nameValuePairs.add(new BasicNameValuePair("BtId", String.valueOf(btId.trim())));
//			}
//			
//	//------------------------------------------New Commands---------------------------------------------------
//			nameValuePairs.add(new BasicNameValuePair("Fuel_Level_Input", String.valueOf(results.get("012F"))));
//			nameValuePairs.add(new BasicNameValuePair("Fuel_status", String.valueOf(results.get("0103"))));
//			nameValuePairs.add(new BasicNameValuePair("Timing_Advance", String.valueOf(results.get("0103"))));
//			nameValuePairs.add(new BasicNameValuePair("Air_Flow_Rate", String.valueOf(results.get("0110"))));
//			nameValuePairs.add(new BasicNameValuePair("OBD_Standard", String.valueOf(results.get("011C"))));
//			nameValuePairs.add(new BasicNameValuePair("Oxygen_sensors_present", String.valueOf(results.get("011D"))));
//			nameValuePairs.add(new BasicNameValuePair("Run_time", String.valueOf(results.get("011F"))));
//			nameValuePairs.add(new BasicNameValuePair("Fuel_Rail_Pressure", String.valueOf(results.get("0123"))));
//			nameValuePairs.add(new BasicNameValuePair("Commanded_evaporative_purge", String.valueOf(results.get("012E"))));
//			nameValuePairs.add(new BasicNameValuePair("System_Vapor_Pressure", String.valueOf(results.get("0132"))));
//			nameValuePairs.add(new BasicNameValuePair("Control_module_voltage", String.valueOf(results.get("0142"))));
//			nameValuePairs.add(new BasicNameValuePair("Absolute_Load_Value", String.valueOf(results.get("0143"))));
//			nameValuePairs.add(new BasicNameValuePair("Equivalence_ratio", String.valueOf(results.get("0144"))));
//			nameValuePairs.add(new BasicNameValuePair("Fuel_Type", String.valueOf(results.get("0151"))));
//			nameValuePairs.add(new BasicNameValuePair("Ethanol_fuel", String.valueOf(results.get("0152"))));
//			
//			try {
//				nameValuePairs.add(new BasicNameValuePair("License_Num", String.valueOf(data.get(0).License_Num)));
//				nameValuePairs.add(new BasicNameValuePair("License_State", String.valueOf(data.get(0).License_State)));
//			} catch (Exception e) {
//				nameValuePairs.add(new BasicNameValuePair("License_Num", String.valueOf("")));
//				nameValuePairs.add(new BasicNameValuePair("License_State", String.valueOf("")));
//			}
//			
//			nameValuePairs.add(new BasicNameValuePair("Monitor_status", String.valueOf(results.get("0101"))));
//			nameValuePairs.add(new BasicNameValuePair("Short_term_fuel_B1", String.valueOf(results.get("0106"))));
//			nameValuePairs.add(new BasicNameValuePair("Long_term_fuel_B1", String.valueOf(results.get("0107"))));
//			nameValuePairs.add(new BasicNameValuePair("Short_term_fuel_B2", String.valueOf(results.get("0108"))));
//			nameValuePairs.add(new BasicNameValuePair("Long_term_fuel_B2", String.valueOf(results.get("0109"))));
//			nameValuePairs.add(new BasicNameValuePair("Oxy_sensor_present", String.valueOf(results.get("0113"))));
//			nameValuePairs.add(new BasicNameValuePair("Oxy_sensor_voltage_B1S1", String.valueOf(results.get("0114"))));
//			nameValuePairs.add(new BasicNameValuePair("Oxy_sensor_voltage_B1S2", String.valueOf(results.get("0115"))));
//			nameValuePairs.add(new BasicNameValuePair("Oxy_sensor_voltage_B1S3", String.valueOf(results.get("0116"))));
//			nameValuePairs.add(new BasicNameValuePair("Oxy_sensor_voltage_B1S4", String.valueOf(results.get("0117"))));
//			nameValuePairs.add(new BasicNameValuePair("Oxy_sensor_voltage_B2S1", String.valueOf(results.get("0118"))));
//			nameValuePairs.add(new BasicNameValuePair("Oxy_sensor_voltage_B2S2", String.valueOf(results.get("0119"))));
//			nameValuePairs.add(new BasicNameValuePair("Oxy_sensor_voltage_B2S3", String.valueOf(results.get("011A"))));
//			nameValuePairs.add(new BasicNameValuePair("Oxy_sensor_voltage_B2S4", String.valueOf(results.get("011B"))));
//			nameValuePairs.add(new BasicNameValuePair("Auxiliary_input_status", String.valueOf(results.get("011E"))));
//			nameValuePairs.add(new BasicNameValuePair("PID_supported", String.valueOf(results.get("0120"))));
//			nameValuePairs.add(new BasicNameValuePair("Equivalence_Ratio_O2S1_V", String.valueOf(results.get("0124"))));
//			nameValuePairs.add(new BasicNameValuePair("Equivalence_Ratio_O2S2_V", String.valueOf(results.get("0125"))));
//			nameValuePairs.add(new BasicNameValuePair("Equivalence_Ratio_O2S3_V", String.valueOf(results.get("0126"))));
//			nameValuePairs.add(new BasicNameValuePair("Equivalence_Ratio_O2S4_V", String.valueOf(results.get("0127"))));
//			nameValuePairs.add(new BasicNameValuePair("Equivalence_Ratio_O2S5_V", String.valueOf(results.get("0128"))));			
//			nameValuePairs.add(new BasicNameValuePair("Equivalence_Ratio_O2S6_V", String.valueOf(results.get("0129"))));
//			nameValuePairs.add(new BasicNameValuePair("Equivalence_Ratio_O2S7_V", String.valueOf(results.get("012A"))));
//			nameValuePairs.add(new BasicNameValuePair("Equivalence_Ratio_O2S8_V", String.valueOf(results.get("012B"))));
//			nameValuePairs.add(new BasicNameValuePair("Commanded_EGR", String.valueOf(results.get("012C"))));
//			nameValuePairs.add(new BasicNameValuePair("EGR_Error", String.valueOf(results.get("012D"))));
//			nameValuePairs.add(new BasicNameValuePair("No_WarmUps", String.valueOf(results.get("0130"))));
//			nameValuePairs.add(new BasicNameValuePair("Dist_Traveled", String.valueOf(results.get("0131"))));
//			nameValuePairs.add(new BasicNameValuePair("Equivalence_Ratio_O2S1_C", String.valueOf(results.get("0134"))));
//			nameValuePairs.add(new BasicNameValuePair("Equivalence_Ratio_O2S2_C", String.valueOf(results.get("0135"))));
//			nameValuePairs.add(new BasicNameValuePair("Equivalence_Ratio_O2S3_C", String.valueOf(results.get("0136"))));
//			nameValuePairs.add(new BasicNameValuePair("Equivalence_Ratio_O2S4_C", String.valueOf(results.get("0137"))));
//			nameValuePairs.add(new BasicNameValuePair("Equivalence_Ratio_O2S5_C", String.valueOf(results.get("0138"))));
//			nameValuePairs.add(new BasicNameValuePair("Equivalence_Ratio_O2S6_C", String.valueOf(results.get("0139"))));
//			nameValuePairs.add(new BasicNameValuePair("Equivalence_Ratio_O2S7_C", String.valueOf(results.get("013A"))));
//			nameValuePairs.add(new BasicNameValuePair("Equivalence_Ratio_O2S8_C", String.valueOf(results.get("013B"))));
//			nameValuePairs.add(new BasicNameValuePair("Catalyst_Temp_B1S1", String.valueOf(results.get("013C"))));
//			nameValuePairs.add(new BasicNameValuePair("Catalyst_Temp_B2S1", String.valueOf(results.get("013D"))));
//			nameValuePairs.add(new BasicNameValuePair("Catalyst_Temp_B1S2", String.valueOf(results.get("013E"))));
//			nameValuePairs.add(new BasicNameValuePair("Catalyst_Temp_B2S2", String.valueOf(results.get("013F"))));
//			nameValuePairs.add(new BasicNameValuePair("PID_supported_41", String.valueOf(results.get("0140"))));
//			nameValuePairs.add(new BasicNameValuePair("Monitor_status_Cycle", String.valueOf(results.get("0141"))));
//			nameValuePairs.add(new BasicNameValuePair("Rela_Throttle_position", String.valueOf(results.get("0145"))));
//			nameValuePairs.add(new BasicNameValuePair("Absolute_Throttle_position_B", String.valueOf(results.get("0147"))));
//			nameValuePairs.add(new BasicNameValuePair("Absolute_Throttle_position_C", String.valueOf(results.get("0148"))));
//			nameValuePairs.add(new BasicNameValuePair("Accelerator_pedal_position_D", String.valueOf(results.get("0149"))));
//			nameValuePairs.add(new BasicNameValuePair("Accelerator_pedal_position_E", String.valueOf(results.get("014A"))));
//			nameValuePairs.add(new BasicNameValuePair("Accelerator_pedal_position_F", String.valueOf(results.get("014B"))));
//			nameValuePairs.add(new BasicNameValuePair("Commanded_throttle_actuator", String.valueOf(results.get("014C"))));
//			nameValuePairs.add(new BasicNameValuePair("Time_MIL", String.valueOf(results.get("014D"))));
//			nameValuePairs.add(new BasicNameValuePair("Time_trouble_code", String.valueOf(results.get("014E"))));
//			nameValuePairs.add(new BasicNameValuePair("Evap_system_Vapour_Pressure", String.valueOf(results.get("0153"))));
//			nameValuePairs.add(new BasicNameValuePair("Freeze_frame_trouble_code", String.valueOf(results.get("0202"))));
//			nameValuePairs.add(new BasicNameValuePair("Clear_trouble_code", String.valueOf(results.get("04"))));
//			nameValuePairs.add(new BasicNameValuePair("OBD_Monitor_ID_Supported", String.valueOf(results.get("050100"))));
//			nameValuePairs.add(new BasicNameValuePair("O2_sensor_Monitor_B1S1_Learn", String.valueOf(results.get("050101"))));
//			nameValuePairs.add(new BasicNameValuePair("O2_sensor_Monitor_B1S2_Learn", String.valueOf(results.get("050102"))));
//			nameValuePairs.add(new BasicNameValuePair("O2_sensor_Monitor_B1S3_Learn", String.valueOf(results.get("050103"))));
//			nameValuePairs.add(new BasicNameValuePair("O2_sensor_Monitor_B1S4_Learn", String.valueOf(results.get("050104"))));
//			nameValuePairs.add(new BasicNameValuePair("O2_sensor_Monitor_B2S1_Learn", String.valueOf(results.get("050105"))));
//			nameValuePairs.add(new BasicNameValuePair("O2_sensor_Monitor_B2S2_Learn", String.valueOf(results.get("050106"))));
//			nameValuePairs.add(new BasicNameValuePair("O2_sensor_Monitor_B2S3_Learn", String.valueOf(results.get("050107"))));
//			nameValuePairs.add(new BasicNameValuePair("O2_sensor_Monitor_B2S4_Learn", String.valueOf(results.get("050108"))));
//			nameValuePairs.add(new BasicNameValuePair("O2_sensor_Monitor_B3S1_Learn", String.valueOf(results.get("050109"))));
//			nameValuePairs.add(new BasicNameValuePair("O2_sensor_Monitor_B3S2_Learn", String.valueOf(results.get("05010A"))));
//			nameValuePairs.add(new BasicNameValuePair("O2_sensor_Monitor_B3S3_Learn", String.valueOf(results.get("05010B"))));
//			nameValuePairs.add(new BasicNameValuePair("O2_sensor_Monitor_B3S4_Learn", String.valueOf(results.get("05010C"))));
//			nameValuePairs.add(new BasicNameValuePair("O2_sensor_Monitor_B4S1_Learn", String.valueOf(results.get("05010D"))));
//			nameValuePairs.add(new BasicNameValuePair("O2_sensor_Monitor_B4S2_Learn", String.valueOf(results.get("05010E"))));
//			nameValuePairs.add(new BasicNameValuePair("O2_sensor_Monitor_B4S3_Learn", String.valueOf(results.get("05010F"))));
//			nameValuePairs.add(new BasicNameValuePair("O2_sensor_Monitor_B4S4_Learn", String.valueOf(results.get("050110"))));
//			nameValuePairs.add(new BasicNameValuePair("O2_sensor_Monitor_B1S1_Rich", String.valueOf(results.get("050201"))));
//			nameValuePairs.add(new BasicNameValuePair("O2_sensor_Monitor_B1S2_Rich", String.valueOf(results.get("050202"))));
//			nameValuePairs.add(new BasicNameValuePair("O2_sensor_Monitor_B1S3_Rich", String.valueOf(results.get("050203"))));
//			nameValuePairs.add(new BasicNameValuePair("O2_sensor_Monitor_B1S4_Rich", String.valueOf(results.get("050204"))));
//			nameValuePairs.add(new BasicNameValuePair("O2_sensor_Monitor_B2S1_Rich", String.valueOf(results.get("050205"))));
//			nameValuePairs.add(new BasicNameValuePair("O2_sensor_Monitor_B2S2_Rich", String.valueOf(results.get("050206"))));
//			nameValuePairs.add(new BasicNameValuePair("O2_sensor_Monitor_B2S3_Rich", String.valueOf(results.get("050207"))));
//			nameValuePairs.add(new BasicNameValuePair("O2_sensor_Monitor_B2S4_Rich", String.valueOf(results.get("050208"))));
//			nameValuePairs.add(new BasicNameValuePair("O2_sensor_Monitor_B3S1_Rich", String.valueOf(results.get("050209"))));
//			nameValuePairs.add(new BasicNameValuePair("O2_sensor_Monitor_B3S2_Rich", String.valueOf(results.get("05020A"))));
//			nameValuePairs.add(new BasicNameValuePair("O2_sensor_Monitor_B3S3_Rich", String.valueOf(results.get("05020B"))));
//			nameValuePairs.add(new BasicNameValuePair("O2_sensor_Monitor_B3S4_Rich", String.valueOf(results.get("05020C"))));
//			nameValuePairs.add(new BasicNameValuePair("O2_sensor_Monitor_B4S1_Rich", String.valueOf(results.get("05020D"))));
//			nameValuePairs.add(new BasicNameValuePair("O2_sensor_Monitor_B4S2_Rich", String.valueOf(results.get("05020E"))));
//			nameValuePairs.add(new BasicNameValuePair("O2_sensor_Monitor_B4S3_Rich", String.valueOf(results.get("05020F"))));
//			nameValuePairs.add(new BasicNameValuePair("O2_sensor_Monitor_B4S4_Rich", String.valueOf(results.get("050210"))));
//			nameValuePairs.add(new BasicNameValuePair("Mode_9_supported", String.valueOf(results.get("0900"))));
//			nameValuePairs.add(new BasicNameValuePair("Fuel_Rail_Pressure_D", String.valueOf(results.get("0122"))));
//			result = Utility.postRequestonlyforupload(getServerPath, nameValuePairs);
//		} catch (Exception e) {
//			e.printStackTrace();
//			//Log.e("upload err", ""+e.getMessage());
//			isfirst=true;
//		}
//		return null;
//	}
	
		@SuppressLint("NewApi")
		private Object uploadFile(String sourceFileUri,String Name,int status) {
			// TODO Auto-generated method stub
//	    	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); // solution for Networkonmainthread exception
//	    	StrictMode.setThreadPolicy(policy); // solution for Networkonmainthread exception
		        String fileName = sourceFileUri;
		        HttpURLConnection conn = null;
		        DataOutputStream dos = null;  
		        int bytesRead, bytesAvailable, bufferSize;
		        byte[] buffer;
		        int maxBufferSize = 1 * 1024 * 1024; 
		        File sourceFile = new File(sourceFileUri); 
		        if (!sourceFile.isFile()) {
		             return "";
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
           	  			resStatus.setResCode(serverResponseCode);
           	  			resStatus.setResponseStatus(conn.getResponseMessage());
           	  		 
           	  			////Log.e("uploadFile", "HTTP Response is : Server"+ serverResponseMessage + ": " + serverResponseCode);
           	  			if(serverResponseCode == 200){
		                	 
		                	 	InputStream in = conn.getInputStream();
		           			    byte data[] = new byte[1024];
		           			    int counter = -1;
		           			    String jsonString = "";
		           			    while( (counter = in.read(data)) != -1){
		           			         jsonString += new String(data, 0, counter);
		           			    }
		           			    //Log.dMainActivity.TAG, "HTTP Response is JSON String: " + jsonString);
		           			
		           			 resStatus.setResultJson(jsonString);
		           			 
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
				return resStatus;
		       
		}

	// Vehical Response write in file 
	private void writefile(String data) {
		// TODO Auto-generated method stub
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss EEEE");
			String currentDateandTime = sdf.format(new Date());
			File f1 = new File(Environment.getExternalStorageDirectory().getPath() + "/Matics/VehicalInfo.txt");
			BufferedWriter out = new BufferedWriter(new FileWriter(f1, true));
			out.write("\n\n" + "[" + currentDateandTime + "]  \n" + data+ "\n\n");
			out.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	 
	
}
