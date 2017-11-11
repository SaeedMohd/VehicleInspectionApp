package com.matics.fragments;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.matics.MainActivity;
import com.matics.R;
import com.matics.Bluetooth.BluetoothApp;
import com.matics.CrashReport.CustomUncaughtExceptionHandler;
import com.matics.CustomViews.SpeedometerView;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v4.app.Fragment;

public class FragmentConnection extends Fragment {
	
	public static ArrayList<Integer> vehicleInfoList=new ArrayList<Integer>();
	public static TextView Connect;
	Timer connectTextTimer;
	TextView Last_Updatetime;
	static SharedPreferences prefs;
	private SpeedometerView speedometer;
	public static String vehicalInfo="0";
	Typeface tf;
	
	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
		View view = inflater.inflate(R.layout.fragement_connection, container,false);

//		Initial all objects
		initalize(view);
		
//		Gauge Meter use for vehical health	
		
		speedometer.setMaxSpeed(100);
		speedometer.setMajorTickStep(20);
		speedometer.setMinorTicks(2);
		speedometer.addColoredRange(75, 100, Color.GREEN);
		speedometer.addColoredRange(50, 75, Color.YELLOW);
		speedometer.addColoredRange(0, 25, Color.RED);
		
		speedometer.setLabelConverter(new SpeedometerView.LabelConverter() {
			@Override
			public String getLabelFor(double progress, double maxProgress) {
				return String.valueOf((int) Math.round(progress));
			}
		});

		speedometer.setSpeed(Double.parseDouble("0"));
		
		//Log.dMainActivity.TAG,"starting timer for connected text view");
		
		startTimerForConnectedTextView();

		try {
//			if (MainActivity.mediaPrefs.getString(MainActivity.LastUpload, "").trim().length() > 0) {
//				Last_Updatetime.setText(MainActivity.mediaPrefs.getString(MainActivity.LastUpload, ""));
//				Last_Updatetime.setTypeface(tf);
//			}
//
//			//Log.e("","Last Date is :"+ MainActivity.mediaPrefs.getString(MainActivity.LastUpload, ""));
		} catch (Exception e) {
			// e.printStackTrace();
			//Log.e("", "in date exception");
		}
	
		return view;
	}

//	Inital method
	private void initalize(View view) {
		// TODO Auto-generated method stub
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		speedometer = (SpeedometerView) view.findViewById(R.id.speedometer);
		Connect = (TextView) view.findViewById(R.id.textViewConnect);
		Last_Updatetime = (TextView) view.findViewById(R.id.textViewlastTime);
		
		tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/gill_sans.ttf");
	}

//	Update connectivity every 3000 using Timer
	private void startTimerForConnectedTextView() {

		final Runnable updateListAfterDeviceIsConnectedToMaster = new Runnable() {
			public void run() {
				//Log.dMainActivity.TAG,
//						"Check connected or not to update the view");
				if (BluetoothApp.isConnectionEstablished) {
					Connect.setText("Connected");
					Connect.setTextColor(Color.GREEN);
					Connect.setTypeface(tf);
					speedometer.setSpeed(Double.parseDouble(FragmentConnection.vehicalInfo));
					
					try {
//						if (MainActivity.mediaPrefs.getString(MainActivity.LastUpload, "").trim().length() > 0) {
//							Last_Updatetime.setText(MainActivity.mediaPrefs.getString(MainActivity.LastUpload, ""));
//							Last_Updatetime.setTypeface(tf);
//						}
//
//						//Log.e("","Last Date is :"+ MainActivity.mediaPrefs.getString(MainActivity.LastUpload, ""));
					} catch (Exception e) {
						// e.printStackTrace();
						//Log.e("", "in date exception");
					}

				} else {
					Connect.setText("Not Connected");
					Connect.setTextColor(Color.BLACK);
					Connect.setTypeface(tf);
					vehicalInfo = "0";
					speedometer.setSpeed(Double.parseDouble("0"));
//					BluetoothApp.cancelNotification(getActivity());
				}

			}
		};

		TimerTask task = new TimerTask() {
			public void run() {
				getActivity().runOnUiThread(
						updateListAfterDeviceIsConnectedToMaster);
			}

		};

		connectTextTimer = new Timer();
		connectTextTimer.schedule(task, 0, 3 * 1000);
	}

}
