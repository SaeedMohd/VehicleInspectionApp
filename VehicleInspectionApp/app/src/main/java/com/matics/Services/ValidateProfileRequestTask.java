package com.matics.Services;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.matics.fragments.FragmentVehicleFacility;
import com.matics.R;
import com.matics.adapter.DataBaseHelper;
import com.matics.model.UserProfileModel;
import com.matics.model.VehicleProfileModel;
import com.matics.Utils.ApplicationPrefs;
import com.matics.Utils.MyService;
import com.matics.Utils.Utility;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

public class ValidateProfileRequestTask extends AsyncTask<String, Integer, Object> {

	private final Context mContext;
	private final String webUrl;
	private ProgressDialog mProgressDialog;
	private String ErrorDetail,status;
	// private final LoginData loginData = new LoginData();

	public ValidateProfileRequestTask(final Context mContext, String url) {
		this.mContext = mContext;
		this.webUrl = url;
		mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setMessage("Please Wait...");
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setCancelable(false);
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (mProgressDialog != null && !mProgressDialog.isShowing()) {
			mProgressDialog.show();
		}
//		try {
			DataBaseHelper Mydb = new DataBaseHelper(mContext);
//			Mydb.createDataBase();
//		} catch (IOException ioe) {
//			throw new Error("Unable to create database");
//		}
	}

	@Override
	protected Object doInBackground(String... params) {
		return EditProfile(params[0]);
	}

	@Override
	protected void onPostExecute(Object result) {
		super.onPostExecute(result);
		
		if (mProgressDialog != null && mProgressDialog.isShowing()) {
			mProgressDialog.dismiss();
		}
		Utility.showMessageDialog(mContext, "", ErrorDetail);
		if (status.equalsIgnoreCase("true")) {
//			LoginActivity.storeStringUserDetails(LoginActivity.First_Name, value);
//			Intent intent = new Intent(mContext, MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//            mContext.startActivity(intent);
//            ((Activity)mContext).finish();
			
			//MainActivity.mediaPrefs.edit().putBoolean("login", true).commit();
		  	Fragment fragmentUser= new FragmentVehicleFacility();
			FragmentManager fragmentManagerUser = ((Activity) mContext).getFragmentManager();
			FragmentTransaction ftUser = fragmentManagerUser.beginTransaction();
			ftUser.replace(R.id.tab_frames, fragmentUser);
			ftUser.commit();
			
			
//            LoginActivity.storeBooleanUserDetails("profile", true);
		}

	}

	/**
	 * routine use to get-authentication for request credential
	 */

	private Object EditProfile(String PhoneNumber) {
			
		String result = null;
        String getServerPath = "http://www.jet-matics.com/JetComService/JetCom.svc/ValidateUserProfile?";
        Utility.VehiclePofileData = new ArrayList<VehicleProfileModel>();
        try {
			ContentValues values = new ContentValues();
			values.put("PhoneNumber", String.valueOf(PhoneNumber));
			//nameValuePairs.add(new BasicNameValuePair("AccountId", String.valueOf(MainActivity.mediaPrefs.getString(MainActivity.AccountID, null))));
			//nameValuePairs.add(new BasicNameValuePair("DeviceId", String.valueOf(MainActivity.mediaPrefs.getString(MainActivity.GCM_DeviceID, null))));
			////Log.e("device id", String.valueOf(MainActivity.mediaPrefs.getString(MainActivity.GCM_DeviceID, null)));
			if(!Utility.isMyServiceRunning(mContext)){
				values.put("BtId", String.valueOf(""));
			}else{
				values.put("BtId", String.valueOf(MyService.devString));
			}
			result = Utility.postRequest(getServerPath, values);
			JSONObject jObject = new JSONObject(result.toString());
			JSONObject Loginresult = jObject.getJSONObject("ValidateUserProfileResult");
			JSONObject ObjError = Loginresult.getJSONObject("Error");
			status = ObjError.getString("IsSuccess");
			DataBaseHelper Mydb = new DataBaseHelper(mContext);
			JSONObject UserProfileOBJ = Loginresult.getJSONObject("UserProfile");
			if (status.equalsIgnoreCase("true")) {
				UserProfileModel userProfileModel = ApplicationPrefs.getInstance(mContext).getUserProfilePref();
				userProfileModel.setFirstName(UserProfileOBJ.getString("FirstName"));
				userProfileModel.setLastName(UserProfileOBJ.getString("LastName"));
				userProfileModel.setEmail(UserProfileOBJ.getString("Email"));
				userProfileModel.setPhoneNumber(UserProfileOBJ.getString("PhoneNumber"));
				userProfileModel.setMobileUserProfileId(Integer.parseInt(UserProfileOBJ.getString("MobileUserProfileId")));
				ApplicationPrefs.getInstance(mContext).setUserProfilePref(userProfileModel);
				userProfileModel=null;

//				Mydb.deletetable1();
				JSONArray VehicleArray = Loginresult.getJSONArray("VehicleProfiles");
				for(int i=0;i<VehicleArray.length();i++){
					JSONObject Jobj = VehicleArray.getJSONObject(i);
					VehicleProfileModel prm = new VehicleProfileModel();
					prm.setYear(Jobj.getString("MFYear"));
					prm.setMake(Jobj.getString("Make"));
					prm.setModel(Jobj.getString("Model"));
					prm.setVIN(Jobj.getString("VIN"));
					prm.setMileage(Jobj.getString("Mileage"));
					prm.setLicense_Num(Jobj.getString("License_Num"));
					prm.setLicense_State(Jobj.getString("License_State"));
					Utility.VehiclePofileData.add(prm);
					if(!Mydb.CheckVin(Jobj.getString("VIN"))){
						//Mydb.addProfile(new VehicleProfileModel(Jobj.getString("VIN"), "", Jobj.getInt("MFYear"), Jobj.getString("Make"), Jobj.getString("Model"),Jobj.getString("Mileage"), "","",Jobj.getString("License_Num"),Jobj.getString("License_State"),Jobj.getString("BtID"),0,Jobj.getString("VINID")));
					}
					
				}
			}
//			{
//				"ValidateUserProfileResult":{
//				"Error":{},
//				"UserProfile":{},
//				"VehicleProfiles":[
//				{
//				"MFYear":1999,
//				"Make":"bmw",
//				"Mileage":"2345",
//				"Model":"cd123",
//				"VIN":"1GNFK13598R223864"
//				}
//				]
//				}
//				}
        }
        catch (Exception e) {
            e.printStackTrace();
        }
		return null;
	}
	@Override
	protected void onCancelled() {
		super.onCancelled();
		// if(mProgressDialog != null && mProgressDialog.isShowing())
		// mProgressDialog.dismiss();
	}
}
