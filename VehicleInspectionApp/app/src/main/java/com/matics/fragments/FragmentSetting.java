package com.matics.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.matics.LivePhoneReadings;
import com.matics.MainActivity;
import com.matics.R;
import com.matics.Bluetooth.BluetoothApp;
import com.matics.CrashReport.CustomUncaughtExceptionHandler;
import com.matics.CrashReport.GMailSender;
import com.matics.CustomViews.Speedometer;
import com.matics.adapter.MainActivityListAdapter;
import com.matics.command.CommandsResponseParser;
import com.matics.Services.UploadDataTaskMail;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.Fragment;

public class FragmentSetting extends Fragment {

	private Button DetailData,button_log, btnUploadSErver, btnMailFile;
	public static ListView list_main;
	private Spinner Interval;
	private long interval_time=30000;
	public static Speedometer speedometer;
	List<String> results_Decode = new ArrayList<String>();
	private static Handler handler = null;
	static Context con;
	
	TextView tvData;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_setting, container,false);
		Thread.setDefaultUncaughtExceptionHandler(new CustomUncaughtExceptionHandler());
		
		con = getActivity();

		tvData = (TextView) view.findViewById(R.id.textView2);

		btnUploadSErver = (Button) view.findViewById(R.id.ButtonUploadLOg);
		btnMailFile = (Button) view.findViewById(R.id.ButtonMailLog);
		final File sourceFile = new File(Environment.getExternalStorageDirectory()+ "/Matics/matics.txt");
		
		if (sourceFile.exists()) {
			btnUploadSErver.setVisibility(View.VISIBLE);
			btnMailFile.setVisibility(View.VISIBLE);
		} else {
			btnUploadSErver.setVisibility(View.GONE);
			btnMailFile.setVisibility(View.GONE);
		}
		btnUploadSErver.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (sourceFile.exists()) {
					UploadDataTaskMail task = new UploadDataTaskMail(
							MainActivity.mContext, CommandsResponseParser.results_value);
					task.execute(String.valueOf(""), String.valueOf(""),
							String.valueOf(""), String.valueOf(""));
				} else {
					btnUploadSErver.setVisibility(View.GONE);
					btnMailFile.setVisibility(View.GONE);
				}

			}
		});

		btnMailFile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (sourceFile.exists()) {
					try {

						GMailSender sender = new GMailSender("dhruv.varde@theonetechnologies.com","Dhruv@1Login");
						sender.sendMail("Matics Report","Matics Report",sourceFile,"dhruv.varde@theonetechnologies.com",
								"dhruvvarde1@gmail.com,jaydeeptheonetech@gmail.com,divyesh.gohil@theonetechonologies.com");

						Toast.makeText(getActivity(), "Email sent!",Toast.LENGTH_LONG).show();
					} catch (Exception e) {
						Toast.makeText(getActivity(),"Some Error Occured While Sending Email\n"
										+ e.getMessage(), Toast.LENGTH_LONG).show();
					}
				} else {
					btnUploadSErver.setVisibility(View.GONE);
					btnMailFile.setVisibility(View.GONE);
				}

			}
		});

		DetailData = (Button) view.findViewById(R.id.buttonDetailData);
		list_main = (ListView) view.findViewById(R.id.listview_main);
		button_log = (Button) view.findViewById(R.id.ButtonLog);
		Interval = (Spinner) view.findViewById(R.id.spinnerInterval);
		Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/gill_sans.ttf");
		ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(),
				R.array.Interval, R.layout.customspinner);
		handler = new Handler();
		Interval.setAdapter(adapter);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Interval.setAdapter(adapter);
		Interval.setSelection(0);
		button_log.setTypeface(tf);
		DetailData.setTypeface(tf);

		Interval.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int Position, long arg3) {
				// TODO Auto-generated method stub

				//Log.e("", "Selected Position is :" + Position);
				interval_time = (Position + 1) * 30000;
				//Log.e("", "Interval time is :" + interval_time);

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		button_log.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					Intent emailIntent = new Intent(Intent.ACTION_SEND);
					emailIntent.setType("text/plain");
					emailIntent.putExtra(Intent.EXTRA_EMAIL,new String[] { "email@example.com" });
					emailIntent.putExtra(Intent.EXTRA_SUBJECT, "subject here");
					emailIntent.putExtra(Intent.EXTRA_TEXT, "body text");
					String pathToMyAttachedFile = Environment.getExternalStorageDirectory().getPath()+ "/android.txt";
					File file = new File(pathToMyAttachedFile);
					if (!file.exists() || !file.canRead()) {
						return;
					}
					Uri uri = Uri.fromFile(file);
					emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
					startActivity(Intent.createChooser(emailIntent,"Pick an Email provider"));
				} catch (Exception e) {
					Toast.makeText(getActivity(),"Some Error Occured While Sending Email",Toast.LENGTH_LONG).show();
				}
			}
		});

		new Thread() {
			public void run() {
				while (BluetoothApp.isConnectionEstablished) {
					try {
						final List<String> results_Decode = new ArrayList<String>();
						results_Decode.add(CommandsResponseParser.rpmValue);
						results_Decode.add(CommandsResponseParser.coolantTempValue);
						results_Decode.add(CommandsResponseParser.vehicleSpeedValue);
						results_Decode.add(CommandsResponseParser.fuelLevelValue);

						final String str = CommandsResponseParser.rpmValue + "\n"
								+ CommandsResponseParser.coolantTempValue + "\n"
								+ CommandsResponseParser.vehicleSpeedValue + "\n"
								+ LivePhoneReadings.vin + "\nVehical Health: "
								+ FragmentConnection.vehicalInfo + "%" + "\n"
								+ CommandsResponseParser.engineLoadValue + "\n"
								+ CommandsResponseParser.baromatricValue + "\n"
								+ CommandsResponseParser.airFlowValue + "\n"
								+ CommandsResponseParser.absoluteValue;
						handler.post(new Runnable() {
							public void run() {
								tvData.setText(str);
							}
						});

						sleep(500);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		}.start();
		
		return view;
	}
	

	public static void updateDataTable(final List<String> dataMap) {
		handler.post(new Runnable() {
			public void run() {
				MainActivityListAdapter mAdapter = new MainActivityListAdapter(
						con, dataMap);
				FragmentSetting.list_main.setAdapter(mAdapter);
			}
		});
	}	
	

}
