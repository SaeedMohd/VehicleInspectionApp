package com.matics.command;

import android.util.Log;

public class VaporPressureObdCommand extends IntObdCommand{

	public VaporPressureObdCommand(String cmd, String desc, String resType) {
		super(cmd, desc, resType);
	}
	public VaporPressureObdCommand(VaporPressureObdCommand other) {
		super(other);
	}
//	protected int transform(int b) {
//		return b-40;
//	}
	protected int transform(int b) {
		return b;
		
		
		
		
	}
}
