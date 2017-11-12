package com.inspection.CrashReport;

import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;

public class CustomUncaughtExceptionHandler implements UncaughtExceptionHandler {
	private Thread.UncaughtExceptionHandler defaultUEH;

	private Activity app = null;

	public CustomUncaughtExceptionHandler(Activity app) {
		this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
		this.app = app;

	}

	public CustomUncaughtExceptionHandler() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {

		StackTraceElement[] arr = e.getStackTrace();
		String report = e.toString() + "\n\n";
		report += "--------- Stack trace ---------\n\n";
		for (int i = 0; i < arr.length; i++) {
			report += "    " + arr[i].toString() + "\n";
		}
		report += "-------------------------------\n\n";

		// If the exception was thrown in a background thread inside
		// AsyncTask, then the actual exception can be found with getCause
		report += "--------- Cause ---------\n\n";
		Throwable cause = e.getCause();
		if (cause != null) {
			report += cause.toString() + "\n\n";
			arr = cause.getStackTrace();
			for (int i = 0; i < arr.length; i++) {
				report += "    " + arr[i].toString() + "\n";
			}
		}
		report += "-------------------------------\n\n";

		//Log.dMainActivity.TAG, report);

		//Log.dMainActivity.TAG, "start");

//		Offiline crash report
//		try {
//			SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss EEEE");
//			String currentDateandTime = sdf.format(new Date());
//			File f1 = new File(Environment.getExternalStorageDirectory().getPath() + "/Matics/CrashReport_"+currentDateandTime+".txt");
//			BufferedWriter out = new BufferedWriter(new FileWriter(f1, true));
//			out.write("\n\n" + "[" + currentDateandTime + "]  \n" + report+ "\n\n");
//			out.close();
//		} catch (Exception ioe) {
//			ioe.printStackTrace();
//		}
		
		// sendEmail();

		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss EEEE");
			String currentDateandTime = sdf.format(new Date());
			GMailSender sender = new GMailSender("dhruv.varde@theonetechnologies.com", "Dhruv@1Login");
			//sender.sendMail("Matics Application Crash Report - "+currentDateandTime, report, "dhruv.varde@theonetechnologies.com", "dhruvvarde1@gmail.com,jaydeeptheonetech@gmail.com");
		} catch (Exception e1) {
			//Log.e("SendMail", e1.getMessage(), e1);
		}

		//Log.dMainActivity.TAG, "stop");

		try {
			FileOutputStream trace = app.openFileOutput("stack.trace", Context.MODE_PRIVATE);
			trace.write(report.getBytes());
			trace.close();
		} catch (IOException ioe) {
			// ...
		}

		defaultUEH.uncaughtException(t, e);

	}

	@Override
	protected void finalize() throws Throwable {
		if (Thread.getDefaultUncaughtExceptionHandler().equals(this))
			Thread.setDefaultUncaughtExceptionHandler(defaultUEH);
		super.finalize();
	}
}