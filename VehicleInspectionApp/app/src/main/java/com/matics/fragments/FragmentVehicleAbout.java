package com.matics.fragments;

import com.matics.MainActivity;
import com.matics.R;
import com.matics.CrashReport.CustomUncaughtExceptionHandler;
import com.matics.imageloader.Utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Fragment;
import android.content.Intent;

public class FragmentVehicleAbout extends Fragment implements OnClickListener {

	TextView appInfoTextView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		((MainActivity) getActivity()).getSupportActionBar().setTitle("Info");

		Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
		View view = inflater.inflate(R.layout.fragment_vehicle_about, container, false);
		appInfoTextView = (TextView) view.findViewById(R.id.appInfoTextView);
        PackageManager manager = getActivity().getPackageManager();
        PackageInfo info = null;
        int version=1;
        try {
            info = manager.getPackageInfo(getActivity().getApplicationContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            version = 1;
        }

		ImageView Bgimage = (ImageView) view.findViewById(R.id.imagebg);
		Utils.setShopImage(getActivity(), Bgimage);

        version = info.versionCode;
		appInfoTextView.setText(Html.fromHtml("Vehicle Health App Version "+version+"<br/><br/>To share suggestions, please email us at <a href=\"Suggestions@VehicleHealth.org\">Suggestions@VehicleHealth.org</a><br/><br/>"));
		appInfoTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent email = new Intent(Intent.ACTION_SEND);
				email.putExtra(Intent.EXTRA_EMAIL, new String[]{"Suggestions@VehicleHealth.org"});
				email.putExtra(Intent.EXTRA_SUBJECT, "Suggestion");
				email.putExtra(Intent.EXTRA_TEXT, "");

				email.setType("message/rfc822");

				startActivityForResult(Intent.createChooser(email, "Choose an Email client:"),
						1);
			}
		});
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
