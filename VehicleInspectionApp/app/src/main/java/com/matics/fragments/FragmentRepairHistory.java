package com.matics.fragments;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.matics.Bluetooth.BluetoothApp;
import com.matics.LivePhoneReadings;
import com.matics.MainActivity;
import com.matics.R;
import com.matics.adapter.DataBaseHelper;
import com.matics.adapter.GetApplicableServicesResultData;
import com.matics.imageloader.Utils;
import com.matics.model.VehicleMaintenanceDetailModel;
import com.matics.model.VehicleMaintenanceModel;
import com.matics.model.VehicleProfileModel;
import com.matics.Utils.ApplicationPrefs;
import com.matics.Utils.Utility;

public class FragmentRepairHistory extends Fragment {

	private Spinner spinner_vehicle;
	private View List;
	private LinearLayout Linear_Layout, linearVin;
	// private LinearLayout linear_graph;
	private ImageView ivConnection;
	private ImageButton saveButton, deleteVehicleButton, vehicleHealthInfoButton;
	// private ImageView imageView_connected;
	private TextView textview_serviceList, vehicleHealthValueTextView;
	// private TextView textView_VehicleInfo;
	public static ArrayList<Integer> vehicleHelthList = new ArrayList<Integer>();
	private static int currentspinner = 0;
	ProgressDialog mProgressDialog;
	private EditText modelEditText, makeEditText, yearEditText,
			vinEditText, currentMileageEditText;
	public static ArrayList<GetApplicableServicesResultData> ServiceArray;
	public ArrayList<VehicleProfileModel> vehicleProfileModelArrayList;
	public ArrayList<VehicleMaintenanceModel> vehicleMaintenanceModelArrayList;
	private VehicleMaintenanceDetailModel vehicleMaintenanceDetailModel;
	private ScrollView scrollView;

	private ListView maintenanceHistoryListView;
	ArrayList<String> vinsArray;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// ------------initializing variables
		// Thread.setDefaultUncaughtExceptionHandler(new
		// CustomUncaughtExceptionHandler());
		View view = inflater.inflate(R.layout.fragment_repair_history,
				container, false);

		((MainActivity) getActivity()).getSupportActionBar().setTitle("Repair History");

		ImageView Bgimage = (ImageView) view.findViewById(R.id.imagebg);

		Utils.setShopImage(getActivity(), Bgimage);


		scrollView = (ScrollView) view.findViewById(R.id.scrollView1);
		scrollView.post(new Runnable() {

	        @Override
	        public void run() {
	        	scrollView.fullScroll(ScrollView.FOCUS_UP);
	        }
	    });
		spinner_vehicle = (Spinner) view.findViewById(R.id.spinner_vehicle);
		// textView_VehicleInfo =
		// (TextView)view.findViewById(R.id.textView_VehicleInfo);
		linearVin = (LinearLayout) view.findViewById(R.id.linear_layoutVin);
		Linear_Layout = (LinearLayout) view.findViewById(R.id.linear_layout);
		// linear_graph = (LinearLayout)view.findViewById(R.id.linear_graph);
		// imageView_connected =
		// (ImageView)view.findViewById(R.id.imageView_connected);
		ivConnection = (ImageView) view.findViewById(R.id.ivConnection);
		
		vehicleHealthValueTextView = (TextView) view.findViewById(R.id.vehicleHealthValueTextView);
		
		saveButton = (ImageButton) view.findViewById(R.id.updateVehicleProfileButton);
		deleteVehicleButton = (ImageButton) view.findViewById(R.id.deleteVehicleProfileButton);
		vehicleHealthInfoButton = (ImageButton) view.findViewById(R.id.vehicleHealthInfoButton);
		vehicleHealthInfoButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder vehicleHealthInfoDialog = new AlertDialog.Builder(getActivity());
				vehicleHealthInfoDialog.setTitle("Vehicle Health Info");
				vehicleHealthInfoDialog.setMessage(vehicleProfileModelArrayList.get(spinner_vehicle.getSelectedItemPosition()).getReason());
				vehicleHealthInfoDialog.setPositiveButton("OK", null);
				vehicleHealthInfoDialog.show();
			}
		});
		
		
		
		
		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(modelEditText.getText().toString().trim().length()>0 && makeEditText.getText().toString().trim().length()>0 && yearEditText.getText().toString().trim().length()>0
						&& currentMileageEditText.getText().toString().trim().length()>0){
					VehicleProfileModel vehicleProfileModel = vehicleProfileModelArrayList.get(spinner_vehicle.getSelectedItemPosition());
					vehicleProfileModel.setBtID(vehicleProfileModel.getBtID());
					vehicleProfileModel.setYear(yearEditText.getText().toString().trim());
					vehicleProfileModel.setMake(""+makeEditText.getText().toString().trim());
					vehicleProfileModel.setModel(""+modelEditText.getText().toString().trim());
					vehicleProfileModel.setVIN(""+vinEditText.getText().toString().trim());
					vehicleProfileModel.setMileage(""+currentMileageEditText.getText().toString().trim());
					//ApplicationPrefs.getInstance(getActivity()).setVehicleProfilePrefs(vehicleProfileModel);
					DataBaseHelper dbHelper = new DataBaseHelper(getActivity());
					dbHelper.updateprofile(vehicleProfileModel);
					vehicleProfileModel = null;
					dbHelper = null;
					Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(getActivity(), "Please enter Year, Make, Model, and Mileage to save", Toast.LENGTH_SHORT).show();
				}
			}
		});

		
		deleteVehicleButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder deleteVehicleDialog = new AlertDialog.Builder(getActivity());
				deleteVehicleDialog.setTitle("Please Confirm");
				deleteVehicleDialog.setMessage("Are you sure you want to delete this vehicle?");
				deleteVehicleDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						new DeleteVehicleTask().execute(vehicleProfileModelArrayList.get(spinner_vehicle.getSelectedItemPosition()).getVINID(), LivePhoneReadings.getMobileUserProfileId());
					}
				});
				deleteVehicleDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_LONG).show();
					}
				});
				deleteVehicleDialog.show();
			}
		});
		
		modelEditText = (EditText) view
				.findViewById(R.id.modelValueEditText);
		makeEditText = (EditText) view
				.findViewById(R.id.makeValueEditText);
		yearEditText = (EditText) view
				.findViewById(R.id.yearValueEditText);
		vinEditText = (EditText) view.findViewById(R.id.vinValueEditText);
		currentMileageEditText = (EditText) view
				.findViewById(R.id.currentMileageValueEditText);

		vehicleProfileModelArrayList = new ArrayList<VehicleProfileModel>();
		vehicleMaintenanceModelArrayList = new ArrayList<VehicleMaintenanceModel>();

		maintenanceHistoryListView = (ListView) view
				.findViewById(R.id.maintenanceHistoryListView);
		maintenanceHistoryListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//Log.dMainActivity.TAG, "item selected");
				
				new CallGetVehicleMaintenanceDetails()
				.execute(vehicleMaintenanceModelArrayList.get(
						position).getInvoiceID());
				
			}
		});

		
		DataBaseHelper dbHelper = new DataBaseHelper(getActivity());
		vehicleProfileModelArrayList = dbHelper.getprofiledataforlist();
		//Log.dMainActivity.TAG,"it has an array count = "+vehicleProfileModelArrayList.size());
		vinsArray = new ArrayList<String>();
		for(VehicleProfileModel vehicleProfileModel : vehicleProfileModelArrayList){
		if (vehicleProfileModel.getMake() != null
				&& !vehicleProfileModel.getMake().trim().equals("")
				&& !vehicleProfileModel.getMake().equals("0")) {
			vinsArray.add(vehicleProfileModel.getMake() + "-"
					+ vehicleProfileModel.getModel() + "-"
					+ vehicleProfileModel.getYear());
		} else {
			vinsArray.add(vehicleProfileModel.getVIN());
		}
		}
		
		updateVinsSpinner(vinsArray);
//		if(Utility.checkInternetConnection(getActivity())){
//			modelEditText.setEnabled(true);
//			makeEditText.setEnabled(true);
//			yearEditText.setEnabled(true);
//			vinEditText.setEnabled(true);
//			currentMileageEditText.setEnabled(true);
//			
//		new callGetVINsByUserTask().execute(ApplicationPrefs
//				.getInstance(getActivity()).getUserProfilePref().getUserName());
//		}else{
//			Toast.makeText(getActivity(), "No Internet Connection. Please Connect", Toast.LENGTH_LONG).show();
//			modelEditText.setEnabled(false);
//			makeEditText.setEnabled(false);
//			yearEditText.setEnabled(false);
//			vinEditText.setEnabled(false);
//			currentMileageEditText.setEnabled(false);
//		}

		spinner_vehicle.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				
				setSelectedVehicleValues(vehicleProfileModelArrayList
						.get(position));
				new CallGetVehicleMaintenanceByVIN()
						.execute(vehicleProfileModelArrayList.get(position)
								.getVINID());
				DataBaseHelper dbHelper = new DataBaseHelper(BluetoothApp.context);
				vehicleMaintenanceModelArrayList = dbHelper.getVehicleMaintenanceHistoryForVin(vehicleProfileModelArrayList.get(position).getVIN());
				updateMaintenanceHistoryListView(vehicleMaintenanceModelArrayList);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		
		if(ApplicationPrefs.getInstance(getActivity()).getVehicleProfilePref()!=null){
			//Log.dMainActivity.TAG, "heeeeeereeeeee");
			if((ApplicationPrefs.getInstance(getActivity()).getVehicleProfilePref().getVIN().length()>0) && vinsArray.contains(ApplicationPrefs.getInstance(getActivity()).getVehicleProfilePref().getVIN())){
				spinner_vehicle.setSelection(vinsArray.indexOf(ApplicationPrefs.getInstance(getActivity()).getVehicleProfilePref().getVIN()));
				//Log.dMainActivity.TAG, "set selection as per Vin");
			}else if(vinsArray.contains(ApplicationPrefs.getInstance(getActivity()).getVehicleProfilePref().getMake() + "-"
					+ ApplicationPrefs.getInstance(getActivity()).getVehicleProfilePref().getModel() + "-"
					+ ApplicationPrefs.getInstance(getActivity()).getVehicleProfilePref().getYear())){
				spinner_vehicle.setSelection(vinsArray.indexOf(ApplicationPrefs.getInstance(getActivity()).getVehicleProfilePref().getMake() + "-"
						+ ApplicationPrefs.getInstance(getActivity()).getVehicleProfilePref().getModel() + "-"
						+ ApplicationPrefs.getInstance(getActivity()).getVehicleProfilePref().getYear()));
				//Log.dMainActivity.TAG, "set selection as per bt id");
			}
			}
		
		return view;
	}

	private void setSelectedVehicleValues(
			VehicleProfileModel vehicleProfileModel) {
		makeEditText.setText("" + vehicleProfileModel.getMake());
		modelEditText.setText("" + vehicleProfileModel.getModel());
		yearEditText.setText("" + vehicleProfileModel.getYear());
		vinEditText.setText("" + vehicleProfileModel.getVIN());
		vehicleHealthValueTextView.setText(vehicleProfileModel.getVehicleHealth()+" %");
		if(vehicleProfileModel.getVinRetrievable()){
			vinEditText.setEnabled(false);
            makeEditText.setEnabled(false);
            yearEditText.setEnabled(false);
            modelEditText.setEnabled(false);
		}else{
			vinEditText.setEnabled(true);
			makeEditText.setEnabled(true);
			yearEditText.setEnabled(true);
			modelEditText.setEnabled(true);
		}
		currentMileageEditText.setText(""
				+ vehicleProfileModel.getMileage());
	}

	private void updateVinsSpinner(ArrayList<String> vinsArray) {
		MySpinnerAdapter dataAdapter = new MySpinnerAdapter(
				getActivity(), android.R.layout.simple_spinner_item, vinsArray);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_vehicle.setAdapter(dataAdapter);
		if (BluetoothApp.isConnectionEstablished) {
			// imageView_connected.setImageResource(R.drawable.green);
		} else {
			// imageView_connected.setImageResource(R.drawable.red);
		}
		
		if(ApplicationPrefs.getInstance(getActivity()).getVehicleProfilePref()!=null){
		if((ApplicationPrefs.getInstance(getActivity()).getVehicleProfilePref().getVIN().length()>0) && vinsArray.contains(ApplicationPrefs.getInstance(getActivity()).getVehicleProfilePref().getVIN())){
			spinner_vehicle.setSelection(vinsArray.indexOf(ApplicationPrefs.getInstance(getActivity()).getVehicleProfilePref().getVIN()));
			//Log.dMainActivity.TAG, "set selection as per Vin");
		}else if(vinsArray.contains(ApplicationPrefs.getInstance(getActivity()).getVehicleProfilePref().getBtID())){
			spinner_vehicle.setSelection(vinsArray.indexOf(ApplicationPrefs.getInstance(getActivity()).getVehicleProfilePref().getBtID()));
			//Log.dMainActivity.TAG, "set selection as per bt id");
		}
		}
	}

	private void updateMaintenanceHistoryListView(
			ArrayList<VehicleMaintenanceModel> vehicleMaintenanceModelArrayList) {
		
		ArrayList<String> maintenanceHistoryList = new ArrayList<String>();
		for (int x = 0; x < vehicleMaintenanceModelArrayList.size(); x++) {
			maintenanceHistoryList.add(vehicleMaintenanceModelArrayList.get(x).getServiceDate()+" at "
					+ vehicleMaintenanceModelArrayList.get(x).getFacility());
		}
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_list_item_1,
				maintenanceHistoryList);
		maintenanceHistoryListView.setAdapter(dataAdapter);
		maintenanceHistoryListView
				.setLayoutParams(new LinearLayout.LayoutParams(
						LayoutParams.FILL_PARENT,
						getListViewHeight(maintenanceHistoryListView)));
	}

	private int getListViewHeight(ListView list) {
		ListAdapter adapter = list.getAdapter();
		int listviewHeight = 0;
		list.measure(MeasureSpec.makeMeasureSpec(MeasureSpec.UNSPECIFIED,
				MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0,
				MeasureSpec.UNSPECIFIED));

		listviewHeight = list.getMeasuredHeight() * adapter.getCount()
				+ (adapter.getCount() * list.getDividerHeight());

		return listviewHeight;
	}

	class callGetVINsByUserTask extends
			AsyncTask<String, Void, ArrayList<String>> {

		private ProgressDialog mProgressDialog;

		protected void onPreExecute() {
			mProgressDialog = new ProgressDialog(getActivity());
			mProgressDialog.setMessage("Please Wait...");
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
		};

		@Override
		protected ArrayList<String> doInBackground(String... params) {
			try {
				String result = null;
				String getServerPath = "http://jet-matics.com/JetComService/JetCom.svc/GetVinsByUser?";

				ContentValues values = new ContentValues();
				values.put("UserName", params[0]);

				result = Utility.postRequest(getServerPath, values);
				//Log.dMainActivity.TAG, "" + result);
				// String result = new
				// GetVINsByUserTask(getActivity()).execute(ApplicationPrefs.getInstance(getActivity()).getUserProfilePref().getUserName()).get();
				JSONObject aObject = new JSONObject(result.toString());
				JSONObject jObject = aObject
						.getJSONObject("GetVinsByUserResult");
				JSONArray vehicleProfilesArray = jObject
						.getJSONArray("VehicleProfiles");

				VehicleProfileModel vehicleProfileModel;
				for (int x = 0; x < vehicleProfilesArray.length(); x++) {
					vehicleProfileModel = new Gson().fromJson(
							vehicleProfilesArray.get(x).toString(),
							VehicleProfileModel.class);
					vehicleProfileModelArrayList.add(vehicleProfileModel);
					if (vehicleProfileModel.getMake() != null
							&& !vehicleProfileModel.getMake().trim().equals("")
							&& !vehicleProfileModel.getMake().equals("0")) {
						vinsArray.add(vehicleProfileModel.getMake() + "-"
								+ vehicleProfileModel.getModel() + "-"
								+ vehicleProfileModel.getYear());
					} else {
						vinsArray.add(vehicleProfileModel.getVIN());
					}
					//Log.dMainActivity.TAG,
//							"adding vehicle profile with VIN value = "
//									+ vehicleProfileModel.getVIN());
					vehicleProfileModel = null;
				}
			} catch (JSONException e1) {
				// Toast.makeText(getActivity(), "Error parcing Server data",
				// Toast.LENGTH_SHORT).show();
				e1.printStackTrace();
			}
			return vinsArray;
		}

		protected void onPostExecute(ArrayList<String> vinsArray) {
			if (mProgressDialog != null && mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
			updateVinsSpinner(vinsArray);
		}

	}

	class CallGetVehicleMaintenanceByVIN extends
			AsyncTask<String, Void, ArrayList<String>> {

		private ProgressDialog mProgressDialog;

		protected void onPreExecute() {
			mProgressDialog = new ProgressDialog(getActivity());
			mProgressDialog.setMessage("Please Wait...");
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
		};

		@Override
		protected ArrayList<String> doInBackground(String... params) {
			vehicleMaintenanceModelArrayList.clear();
			try {
				String result = null;
				String getServerPath = "http://www.jet-matics.com/JetComService/JetCom.svc/GetVehicleMaintenance?";

				ContentValues values = new ContentValues();
				values.put("VINID", params[0]);

				result = Utility.postRequest(getServerPath, values);
				//Log.dMainActivity.TAG, "" + result);

				JSONObject aObject = new JSONObject(result.toString());
				JSONObject jObject = aObject
						.getJSONObject("GetVehicleMaintenanceResult");
				JSONArray vehicleMaintenanceJsonArray = jObject
						.getJSONArray("VehicleMaintenance");

				VehicleMaintenanceModel vehicleMaintenanceModel;
				for (int x = 0; x < vehicleMaintenanceJsonArray.length(); x++) {
					vehicleMaintenanceModel = new Gson().fromJson(
							vehicleMaintenanceJsonArray.get(x).toString(),
							VehicleMaintenanceModel.class);
					
					vehicleMaintenanceModelArrayList
							.add(vehicleMaintenanceModel);
					vehicleMaintenanceModel = null;
				}
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(ArrayList<String> vinsArray) {
			if (mProgressDialog != null && mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
			updateMaintenanceHistoryListView(vehicleMaintenanceModelArrayList);


			DataBaseHelper db = new DataBaseHelper(getActivity());
			for(VehicleMaintenanceModel vehicleMaintenanceModel : vehicleMaintenanceModelArrayList) {
				db.addMaintenanceHistory(vehicleMaintenanceModel);
			}
		}

	}

	class CallGetVehicleMaintenanceDetails extends
			AsyncTask<String, Void, ArrayList<String>> {

		private ProgressDialog mProgressDialog;

		protected void onPreExecute() {
			mProgressDialog = new ProgressDialog(getActivity());
			mProgressDialog.setMessage("Please Wait...");
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
		};

		@Override
		protected ArrayList<String> doInBackground(String... params) {
			try {
				String result = null;
				String getServerPath = "http://www.jet-matics.com/JetComService/JetCom.svc/GetVehicleMaintenanceDetails?";

				ContentValues values = new ContentValues();
				values.put("InvoiceID", params[0]);

				result = Utility.postRequest(getServerPath, values);
				//Log.dMainActivity.TAG, "" + result);

				JSONObject aObject = new JSONObject(result.toString());
				JSONObject jObject = aObject
						.getJSONObject("GetVehicleMaintenanceDetailsResult");
				JSONArray vehicleMaintenanceDetailJsonArray = jObject
						.getJSONArray("VehicleMaintenanceDetail");

				vehicleMaintenanceDetailModel = new Gson().fromJson(
						vehicleMaintenanceDetailJsonArray.get(0).toString(),
						VehicleMaintenanceDetailModel.class);
				//Log.dMainActivity.TAG,
//						"model array:"
//								+ vehicleMaintenanceDetailModel
//										.getServicePerformedList().toString());



			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(ArrayList<String> vinsArray) {
			if (mProgressDialog != null && mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
			showVehicleMaintenanceDetailDialog();
		}

	}

	private void showVehicleMaintenanceDetailDialog() {
		StringBuilder servicesStringBuilder = new StringBuilder();
		for(String service: vehicleMaintenanceDetailModel.getServicePerformedList()){
		servicesStringBuilder.append(service+", ");
		}
		String servicesString = servicesStringBuilder.substring(0, servicesStringBuilder.length()-2);
		AlertDialog.Builder vehicleMaintenanceDetailDialog = new AlertDialog.Builder(
				getActivity());
		vehicleMaintenanceDetailDialog.setTitle("Maintenance Details");
		vehicleMaintenanceDetailDialog.setMessage("Mileage was: "
				+ vehicleMaintenanceDetailModel.getMileage()
				+ "\nService Date: "
				+ vehicleMaintenanceDetailModel.getServiceDate()
				+ "\nServices Performed:"
				+ servicesString);
		vehicleMaintenanceDetailDialog.setPositiveButton("OK", null);
		vehicleMaintenanceDetailDialog.show();
	}
	
	
	class DeleteVehicleTask extends AsyncTask<String, Integer, String> {

		String vinID;
		
		private final String serverUrl= "http://jet-matics.com/JetComService/JetCom.svc/RemoveUserVehicleRelation?";

		public DeleteVehicleTask() {
			mProgressDialog = new ProgressDialog(getActivity());
			mProgressDialog.setMessage("Please wait...");
			mProgressDialog.setCanceledOnTouchOutside(false);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (mProgressDialog != null && !mProgressDialog.isShowing()) {
				mProgressDialog.show();
			}
		}

		@Override
		protected String  doInBackground(String... params) {
			vinID = params[0];
			return deleteVehicleRequest(params[0], params[1]);
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (mProgressDialog != null && mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
			
			if(result.contains("IsSuccess\":true,")){
				int x = spinner_vehicle.getSelectedItemPosition();
				vehicleProfileModelArrayList.remove(x);
				vinsArray.remove(x);
				updateVinsSpinner(vinsArray);
				
				DataBaseHelper dbHelper = new DataBaseHelper(getActivity());
				dbHelper.deleteVehicle(vinID);
				dbHelper = null;
			}else{
				Toast.makeText(getActivity(), "Deletion error. Please try again later.", Toast.LENGTH_SHORT).show();
			}
			return ;
		}

		protected void onCancelled() {
			super.onCancelled();
			 if(mProgressDialog != null && mProgressDialog.isShowing())
			 mProgressDialog.dismiss();
		}
		private String deleteVehicleRequest(String vinID, String mobileUserProfileID) {
//			Utility.VehiclePofileData.clear();
			String result  = null;
			
			try {
				ContentValues values = new ContentValues();
				values.put("VIN", vinID);
				values.put("MobileUserProfileId", mobileUserProfileID);

				
				result = Utility.postRequest(serverUrl, values);
//				//------------------------------------Parsing
//				JSONObject jObject = new JSONObject(result.toString());
//				JSONObject ProfileResult = jObject.getJSONObject("GetAccountDetailResult");
//				JSONArray j2 = ProfileResult.getJSONArray("AccountUsers");
//				Gson gson = new Gson();
//				
//				for(int i=0;i<j2.length();i++){
//				UserAccountModel vm = gson.fromJson(j2.get(i).toString(), UserAccountModel.class);
//				userAccountArrayList.add(vm);
//				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			return result;
		}
//		public static final void showMessageDialog(Context contex, String title,
//				String message) {
//			if (message != null && message.trim().length() > 0) {
//				Builder builder = new AlertDialog.Builder(contex);
//				builder.setTitle(title);
//				builder.setMessage(message);
//				builder.setPositiveButton("OK",
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog, int id) {
//								dialog.dismiss();
//							}
//						});
//				builder.show();
//			}
//		}
		
	}

	private static class MySpinnerAdapter extends ArrayAdapter<String> {

		private MySpinnerAdapter(Context context, int resource, List<String> items) {
	        super(context, resource, items);
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        TextView view = (TextView) super.getView(position, convertView, parent);
	        view.setTextAppearance(getContext(), android.R.style.TextAppearance_Large);
	        view.setGravity(Gravity.CENTER);
	        return view;
	    }

	    // Affects opened state of the spinner
	    @Override
	    public View getDropDownView(int position, View convertView, ViewGroup parent) {
	        TextView view = (TextView) super.getDropDownView(position, convertView, parent);
	        view.setTextAppearance(getContext(), android.R.style.TextAppearance_Large);
	        view.setGravity(Gravity.CENTER);
	        return view;
	    }
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
	}
}
