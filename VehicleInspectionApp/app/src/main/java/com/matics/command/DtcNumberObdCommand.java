package com.matics.command;

import android.util.Log;

import com.matics.command.ObdCommand;

public class DtcNumberObdCommand extends ObdCommand {

	private int codeCount = -1;
	private boolean milOn = false;
	public DtcNumberObdCommand() {
		super("0101","DTC Status","");
	}
	public DtcNumberObdCommand(String cmd, String desc, String resType) {
		super(cmd, desc, resType);
	}
	public DtcNumberObdCommand(DtcNumberObdCommand other) {
		super(other);
	}
	public String formatResult() {
		String res = super.formatResult();
		if(res.contains("\n")){
			String[] ress = res.split("\n");
			res = ress[1];
		}		
		res = res.replace(" ","");
		try {
			String byte1 = res.substring(4,6);
			int mil = Integer.parseInt(byte1,16);
			String result = "MIL is off, ";
			if ((mil & 0x80) == 1) {
				milOn = true;
				result = "MIL is on, ";
			}
			codeCount = mil & 0x7f;
			result += codeCount + " codes";
			return result;
		} catch (Exception ex) {
			// TODO: handle exception
			//Log.e("", "Error" + ex.getMessage());
			return res;
		}
		
	}
	public int getCodeCount() {
		return codeCount;
	}
	public boolean getMilOn() {
		return milOn;
	}
}
