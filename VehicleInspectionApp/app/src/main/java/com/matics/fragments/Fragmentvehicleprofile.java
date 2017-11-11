package com.matics.fragments;

import java.util.List;
import com.matics.R;
import com.matics.CrashReport.CustomUncaughtExceptionHandler;
import com.matics.adapter.DataBaseHelper;
import com.matics.adapter.VehicleListAdapter;
import com.matics.model.VehicleProfileModel;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.support.v4.app.Fragment;

public class Fragmentvehicleprofile extends Fragment {

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_vehicalprofile, container, false);
		
		//Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
		
		DataBaseHelper DB = new DataBaseHelper(getActivity());
		
		List<VehicleProfileModel> profiledata = DB.getprofiledata();
		
		ListView list = (ListView) view.findViewById(R.id.listView_vehicle);
		VehicleListAdapter adpter = new VehicleListAdapter(getActivity(),profiledata);
		list.setAdapter(adpter);

		return view;
	}
}
