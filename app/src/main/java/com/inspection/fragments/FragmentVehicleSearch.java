package com.inspection.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.inspection.CrashReport.CustomUncaughtExceptionHandler;
import com.inspection.inspection.R;

public class FragmentVehicleSearch extends Fragment implements OnClickListener {

	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	//------------initializing variables
		Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
		View view = inflater.inflate(R.layout.fragment_vehicle_search, container, false);
		
	
		return view;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
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
