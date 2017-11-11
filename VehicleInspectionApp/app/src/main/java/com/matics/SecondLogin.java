package com.matics;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.matics.MapView.LocationHelper;
import com.matics.adapter.MyObject;
import com.matics.fragments.FragmentVehicleFacility;
import com.matics.model.UserAccountModel;
import com.matics.serverTasks.EditProfileInsideRequestTask;
import com.matics.serverTasks.GetAccountDetailTask;
import com.matics.serverTasks.GetAllAccountDataTask;
import com.matics.Utils.ApplicationPrefs;
import com.matics.Utils.Utility;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class SecondLogin extends Fragment {

	private EditText facilityNameEditText, cityEditText, zipEditText, facility5DigitsEditText;
	public static String[] facilityArray;
	LocationHelper locationHelper;
	GoogleMap googleMap;
	Location myLocation;
	private Button imageView_go;
	// adapter for auto-complete
	ArrayAdapter<MyObject> myAdapter;
	MapView mapView;

	public static ArrayAdapter<String> adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		((MainActivity) getActivity()).getSupportActionBar().setTitle("Select Facility");

		View view = inflater.inflate(R.layout.secondlogin, container, false);
		// DigitCode = (EditText) findViewById(R.id.editTextCode);
		facilityNameEditText = (EditText) view
				.findViewById(R.id.editTextFacilityName);
		cityEditText = (EditText) view
				.findViewById(R.id.editTextCity);

		zipEditText= (EditText) view
				.findViewById(R.id.editTextZip);

		facility5DigitsEditText = (EditText) view
				.findViewById(R.id.editTextFacility5Digits);

		mapView = (MapView) view.findViewById(R.id.mapViewOtherDetail);
		mapView.onCreate(savedInstanceState);
		try {
			MapsInitializer.initialize(getActivity());
			if (GooglePlayServicesUtil
					.isGooglePlayServicesAvailable(getActivity()) == ConnectionResult.SUCCESS) {
				mapView.getMapAsync(new OnMapReadyCallback() {
					@Override
					public void onMapReady(GoogleMap googleMap) {
						googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
						googleMap.setMyLocationEnabled(true);
						googleMap.setTrafficEnabled(true);
						googleMap.setIndoorEnabled(true);
						googleMap.setBuildingsEnabled(true);
						googleMap.getUiSettings().setZoomControlsEnabled(true);
					}
				});
				// locationHelper = new LocationHelper(getActivity());
				myLocation = locationHelper.getLocationManaging();
				locationHelper.setUpMapOptions(googleMap);
			} else {
				System.out.println("Not able to load map!!!");
			}
		} catch (Exception e) {
			System.out.println("Error in setUpMap");
			e.printStackTrace();
		}

		imageView_go = (Button) view.findViewById(R.id.imageView_go);
		imageView_go.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Boolean isnet = Utility.checkInternetConnection(getActivity());
				if (isnet) {
					// -------There is only one choice from 5 Digit code or use
					// facility.
					// -------if any text is there in digit code edittext then
					// call webservice otherwise
					// -------Enter facility name which is autocomplete
					// Edittext.select facility name and calling webservice
					if (cityEditText.getText().toString().trim().length() > 0 || facilityNameEditText.getText().toString().trim().length() > 0 || zipEditText.getText().toString().trim().length() >0 || facility5DigitsEditText.getText().toString().trim().length()>0) {
						
						GetAccountDetailTask task = new GetAccountDetailTask(
								getActivity());
						if(facilityNameEditText.getText().toString().length() > 0 ) {
							task.setSearchParam(GetAccountDetailTask.SearchParam.NAME);
							task.execute(facilityNameEditText.getText().toString());
						}else if (cityEditText.getText().toString().trim().length() > 0) {
							task.setSearchParam(GetAccountDetailTask.SearchParam.CITY);
							task.execute(cityEditText.getText().toString());
						}else if (zipEditText.getText().toString().trim().length() > 0){
							task.setSearchParam(GetAccountDetailTask.SearchParam.ZIP);
							task.execute(zipEditText.getText().toString());
						}else if (facility5DigitsEditText.getText().toString().trim().length() > 0){
							task.setSearchParam(GetAccountDetailTask.SearchParam.FIVE_DIGITS);
							task.execute(facility5DigitsEditText.getText().toString());
						}
						try {
							ArrayList<UserAccountModel> userAccountArrayList = task
									.get();
							checkRetrievedUserAccountsArrayList(userAccountArrayList);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else{
						Toast.makeText(getActivity(), "Please enter Facility Name or Facility City/Zip to search for facilities.", Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(getActivity(),
							"Internet Connection is not Available",
							Toast.LENGTH_LONG).show();
				}
			}
		});


		return view;
	}

	private void checkRetrievedUserAccountsArrayList(
			final ArrayList<UserAccountModel> userAccountArrayList) {
		
		if(userAccountArrayList.size()>0){
			String[] facilityNames = new String[userAccountArrayList.size()];
			for(int x =0; x<userAccountArrayList.size(); x++){
				facilityNames[x] = userAccountArrayList.get(x).getAccountFullName();

			}
			
			Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle("Select Facility Name");
			builder.setItems(facilityNames, new android.content.DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					ApplicationPrefs.getInstance(getActivity()).setLoggedInPrefs(true);
					UserAccountModel userAccountModel = userAccountArrayList.get(which);
					ApplicationPrefs.getInstance(getActivity()).setUserAccountPrefs(userAccountModel);
					
					EditProfileInsideRequestTask task = new EditProfileInsideRequestTask(
							getActivity(), "");
					task.execute("", "", "", "", "", ""+userAccountModel.getAccountId(), ApplicationPrefs.getInstance(getActivity()).getUserProfilePref().getUserName(), "", "", "", "", "", "", "", "","", "", "", "");
					
					Fragment fragment = new FragmentVehicleFacility();
					FragmentManager fragmentManager = getFragmentManager();
					FragmentTransaction ft = fragmentManager.beginTransaction();
					ft.replace(R.id.fragment, fragment);
					ft.commit();
				}
			});
			builder.show();
		}else{
			Builder builder = new AlertDialog.Builder(getActivity());
//			builder.setTitle("Select Facility Name");
			builder.setMessage("No facilities found");
			builder.setPositiveButton("OK", null);
			builder.show();
		}
		
	}

	public static final void showMessageDialog(Context contex, String title,
			String message) {
		if (message != null && message.trim().length() > 0) {
			Builder builder = new AlertDialog.Builder(contex);
			builder.setTitle(title);
			builder.setMessage(message);
			builder.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.dismiss();
						}
					});
			builder.show();
		}
	}

	public void setCurrentMarker() {
		// TODO Auto-generated method stub
		try {
			googleMap.clear();
			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(new LatLng(Double.parseDouble("33.127115"), Double
							.parseDouble("-117.101726"))).zoom(16).build();
			googleMap.animateCamera(CameraUpdateFactory
					.newCameraPosition(cameraPosition));
			BitmapDescriptor icon = BitmapDescriptorFactory
					.fromResource(R.drawable.map_icon);
			googleMap.addMarker(new MarkerOptions()
					.icon(icon)
					.position(
							new LatLng(Double.parseDouble("33.127115"), Double
									.parseDouble("-117.101726"))).title(""));
			googleMap.getUiSettings().setZoomControlsEnabled(false);
			googleMap.getUiSettings().setMyLocationButtonEnabled(false);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	// ------------Getting All shop Data from webservice
	private void GetAllData() {
		Boolean isnet = Utility.checkInternetConnection(getActivity());
		if (isnet) {
			GetAllAccountDataTask task = new GetAllAccountDataTask(
					getActivity(), facilityNameEditText, cityEditText, mapView,
					googleMap);
			task.execute();
		} else {
			Toast.makeText(getActivity(),
					"Internet Connection is not Available", Toast.LENGTH_LONG)
					.show();
		}
	}

	// -----------Disabling back button click event
	public void onBackPressed() {

	}

	@Override
	public void onDestroy() {
		mapView.onDestroy();
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		mapView.onResume();
	}

}
