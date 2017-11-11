package com.matics.command;

import java.util.ArrayList;

import android.util.Log;

import com.matics.MainActivity;

public class ModeSupportedPIDsCommand extends ObdCommand {

	public ModeSupportedPIDsCommand(ObdCommand other) {
		super(other);
	}
	
	public ModeSupportedPIDsCommand(String cmd, String desc, String resType) {
		super(cmd,desc,resType);
	}
	

	public String formatResult() {
		String res = super.formatResult();
		
		if(res.contains("NODATA") || res.contains("NO DATA")){
			return "NODATA";
		}else if (res.contains("UNABLE")){
			return "UNABLE_TO_CONNECT";
		}
		
		res = res.replace(" ", "");
		res = res.substring(4, res.length());
		//Log.dMainActivity.TAG, ""+res);
		return res;
	}
}
