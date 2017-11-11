package com.matics.command;

import android.util.Log;

public class OBDStarndardObdCommand extends IntObdCommand{

	public OBDStarndardObdCommand(String cmd, String desc, String resType) {
		super(cmd, desc, resType);
	}
	public OBDStarndardObdCommand(OBDStarndardObdCommand other) {
		super(other);
	}
//	protected int transform(int b) {
//		return b-40;
//	}
	protected int transform(int b) {
		return b;
		
		
		
		
	}
}
