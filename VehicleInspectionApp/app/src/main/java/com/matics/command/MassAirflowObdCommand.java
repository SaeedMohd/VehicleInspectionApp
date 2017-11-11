package com.matics.command;

import android.util.Log;

public class MassAirflowObdCommand extends ObdCommand {

	public MassAirflowObdCommand(String cmd, String desc, String resType) {
		super(cmd, desc, resType);
	}
	public MassAirflowObdCommand(MassAirflowObdCommand other) {
		super(other);
	}
	public String formatResult() {
		String res = super.formatResult();
//		//Log.e("", "DIrect mass air flow is :>>" + res);
		if(res.contains("NODATA") || res.contains("NO DATA")){
			return "NODATA";
		}
//		
		if(res.contains("\n")){
			String[] ress = res.split("\n");
			res = ress[1];
		}
		res = res.replace(" ","");
		try {
			String byteStrOne = res.substring(4,6);
			String byteStrTwo = res.substring(6,8);
			int a = Integer.parseInt(byteStrOne,16);
			int b = Integer.parseInt(byteStrTwo,16);
			// maf = (byteStrOne * 256 + byteStrTwo) / 100.0f;
			//Log.e("TAG", "res b:" + b);
			return Float.toString(transform(a,b)) + " " + resType;
		} catch (Exception ex) {
			// TODO: handle exception
			//Log.e("", "Error" + ex.getMessage());
			return res;
		}
//		return res;
		
		
		
	}
//	protected float transform(int a,int b) {
//		
//		return ((a * 256 + b) / 100.0f);
//	}
	protected float transform(int a,int b) {
		
		return ((a * 256 + b) / 100.0f);
	}
}
