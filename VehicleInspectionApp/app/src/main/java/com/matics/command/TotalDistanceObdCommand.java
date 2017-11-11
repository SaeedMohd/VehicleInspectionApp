package com.matics.command;

import android.content.Context;
import android.util.Log;


public class TotalDistanceObdCommand extends ObdCommand
{
	Context con;
	
	public TotalDistanceObdCommand(String cmd, String desc, String resType) {
		super(cmd, desc, resType);
	}
	public TotalDistanceObdCommand(TotalDistanceObdCommand other) {
		super(other);
	}
	public String formatResult() {
		String res = super.formatResult();
		if(res.contains("NODATA") || res.contains("NO DATA")){
			return "NODATA";
		}

		if(res.contains("\n")){
			String[] ress = res.split("\n");
			res = ress[1];
		}
		res = res.replace(" ","");
		try {
			String byteStr = res.substring(4,6);
			int b = Integer.parseInt(byteStr,16);
			//Log.e("TAG", "res b:" + b);
			return Float.toString(transform(b)) + " " + resType;
		} catch (Exception ex) {
			// TODO: handle exception
			//Log.e("", "Error" + ex.getMessage());
			return res;
		}
		
	}
	public float transform(int b) {
		 return new Double(b * 0.621371192).floatValue();
		//return b;
		
	}
}
