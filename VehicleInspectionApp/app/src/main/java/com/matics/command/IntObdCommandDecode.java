package com.matics.command;

import java.io.IOException;

import com.matics.Utils.MyService;

import android.util.Log;

public class IntObdCommandDecode extends ObdCommand {

	public IntObdCommandDecode(String cmd, String desc, String resType) {
		super(cmd, desc, resType);
	}
	public IntObdCommandDecode(IntObdCommandDecode other) {
		super(other);
	}
	public String formatResult() {
		String res = super.formatResult();
		if(res.contains("NODATA") || res.contains("NO DATA")){
			//Log.e("", "no data");
			return "NODATA";
			
		}
		if(res.contains("\n")){
			String[] ress = res.split("\n");
			res = ress[1];
		}
		res = res.replace(" ","");
		try {
			//Log.e("TAG", "res b1:" +res);
			String byteStr = res.substring(4,6);
			//Log.e("TAG", "res b2:" + byteStr);
			int b = Integer.parseInt(byteStr,16);
			//Log.e("TAG", "res b3:" + b);
			return Integer.toString(transform(b)) + " " + resType;
		} catch (Exception ex) {
			// TODO: handle exception
			//Log.e("", "Error in int" + ex.getMessage());
			try {
				MyService.sock.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return res;
		}
	}
	protected int transform(int b) {
		return b;
	}
	
	
}
