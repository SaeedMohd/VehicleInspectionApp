package com.matics.command;

import android.util.Log;

public class IntObdCommand2 extends ObdCommand {

	public IntObdCommand2(String cmd, String desc, String resType) {
		super(cmd, desc, resType);
	}
	public IntObdCommand2(IntObdCommand2 other) {
		super(other);
	}
	public String formatResult() {
		String res = super.formatResult();
		if(res.contains("NODATA") || res.contains("NO DATA")){
//			//Log.e("", "no data");
			return "NODATA";
		}
		if(res.contains("\n")){
			String[] ress = res.split("\n");
			res = ress[1];
		}
		res = res.replace(" ","");
		try {
//			String byteStr = res.substring(4,6);
//			int b = Integer.parseInt(byteStr,16);
//			//Log.e("TAG", "res b:" + b);
			return res;
		} catch (Exception ex) {
			// TODO: handle exception
//			//Log.e("", "Error in int" + ex.getMessage());
			return res;
		}
//		//Log.i("", "final res is :" +res );
		
	}
	protected int transform(int b) {
		return b;
	}
	
	
}
