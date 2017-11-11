package com.matics.command;

import android.util.Log;

import com.matics.command.ObdCommand;

public class EngineRPMObdCommandDecode extends ObdCommand{

	public EngineRPMObdCommandDecode(String cmd, String desc, String resType) {
		super(cmd, desc, resType);
	}
	public EngineRPMObdCommandDecode(EngineRPMObdCommandDecode other) {
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
			String byteStrOne = res.substring(4,6);
			String byteStrTwo = res.substring(6,8);
			int a = Integer.parseInt(byteStrOne,16);
			int b = Integer.parseInt(byteStrTwo,16);
			return Integer.toString(transform(a,b)) + " " + resType;
		} catch (Exception ex) {
			// TODO: handle exception
			//Log.e("", "Error" + ex.getMessage());
			return res;
		}
		
	}
	protected int transform(int a, int b) {
		return (int)((double)(a*256+b)/4.0);
	}
}
