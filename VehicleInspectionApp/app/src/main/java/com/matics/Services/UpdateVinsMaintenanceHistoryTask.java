package com.matics.Services;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ContentValues;
import android.os.AsyncTask;
import com.google.gson.Gson;
import com.matics.Bluetooth.BluetoothApp;
import com.matics.adapter.DataBaseHelper;
import com.matics.model.VehicleMaintenanceModel;
import com.matics.Utils.Utility;


public class UpdateVinsMaintenanceHistoryTask extends AsyncTask<String, Integer, String> {

	public String isSuccess="false";
	
	private final String serverUrl= "http://jet-matics.com/JetComService/JetCom.svc/VINVehicleMaintenanceList?";

	public UpdateVinsMaintenanceHistoryTask() {
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		
	}

	@Override
	protected String doInBackground(String... params) {
		getVINVehicleMaintenanceList(params[0]);
		return "";
	}

	protected void onPostExecute(String result) {
		
	}

	protected void onCancelled() {
		super.onCancelled();
		
	}
	private String getVINVehicleMaintenanceList(String Name) {
//		Utility.VehiclePofileData.clear();
		String result  = null;
		ArrayList<VINVehicleMaintenance> vinVehicleMaintenanceArrayList= new ArrayList<VINVehicleMaintenance>();
		
		try {
			ContentValues values = new ContentValues();
			values.put("username", Name);
			
			result = Utility.postRequest(serverUrl, values);
			//------------------------------------Parsing
			JSONObject jObject = new JSONObject(result.toString());
			JSONObject ProfileResult = jObject.getJSONObject("VINVehicleMaintenanceListResult");
			JSONArray j2 = ProfileResult.getJSONArray("VINVehicleMaintenance");
			Gson gson = new Gson();
			
			for(int i=0;i<j2.length();i++){
			VINVehicleMaintenance vinVehicleMaintenance = gson.fromJson(j2.get(i).toString(), VINVehicleMaintenance.class);
			vinVehicleMaintenanceArrayList.add(vinVehicleMaintenance);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
            return "";
		}
		DataBaseHelper dbHelper = new DataBaseHelper(BluetoothApp.context);
        dbHelper.clearMaintenanceHistoryTable();
		for(VINVehicleMaintenance vinVehicleMaintenance: vinVehicleMaintenanceArrayList){
			VehicleMaintenanceModel vehicleMaintenanceModel = new VehicleMaintenanceModel();
//			vehicleMaintenanceModel.setVin(vinVehicleMaintenance.getVin());
			for (VehicleMaintenance vehicleMaintenance: vinVehicleMaintenance.getVehicleMaintenance()){
				vehicleMaintenanceModel.setFacility(vehicleMaintenance.getFacility());
				vehicleMaintenanceModel.setInvoiceID(vehicleMaintenance.getInvoiceID());
				vehicleMaintenanceModel.setServiceDate(vehicleMaintenance.getServiceDate());
				dbHelper.addMaintenanceHistory(vehicleMaintenanceModel);
			}
		}
		dbHelper = null;
		
		
		return "";
	}
//	public static final void showMessageDialog(Context contex, String title,
//			String message) {
//		if (message != null && message.trim().length() > 0) {
//			Builder builder = new AlertDialog.Builder(contex);
//			builder.setTitle(title);
//			builder.setMessage(message);
//			builder.setPositiveButton("OK",
//					new DialogInterface.OnClickListener() {
//						public void onClick(DialogInterface dialog, int id) {
//							dialog.dismiss();
//						}
//					});
//			builder.show();
//		}
//	}
	
	
	class VINVehicleMaintenance{
		
		String VIN;
		VehicleMaintenance[] VehicleMaintenance;
		public String getVin() {
			return VIN;
		}
		public void setVin(String vin) {
			this.VIN = vin;
		}
		public VehicleMaintenance[] getVehicleMaintenance() {
			return VehicleMaintenance;
		}
		public void setVehicleMaintenance(VehicleMaintenance[] vehicleMaintenance) {
			VehicleMaintenance = vehicleMaintenance;
		}
		
		
	}
	
	class VehicleMaintenance{
		String Facility;
		String InvoiceID;
		String ServiceDate;
		public String getFacility() {
			return Facility;
		}
		public void setFacility(String facility) {
			Facility = facility;
		}
		public String getInvoiceID() {
			return InvoiceID;
		}
		public void setInvoiceID(String invoiceID) {
			InvoiceID = invoiceID;
		}
		public String getServiceDate() {
			return ServiceDate;
		}
		public void setServiceDate(String serviceDate) {
			ServiceDate = serviceDate;
		}
		
		
	}
	
}
