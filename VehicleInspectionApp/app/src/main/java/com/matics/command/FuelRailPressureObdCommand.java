package com.matics.command;

import android.util.Log;

public class FuelRailPressureObdCommand extends IntObdCommand{

	public FuelRailPressureObdCommand(String cmd, String desc, String resType) {
		super(cmd, desc, resType);
	}
	public FuelRailPressureObdCommand(FuelRailPressureObdCommand other) {
		super(other);
	}
//	protected int transform(int b) {
//		return b-40;
//	}
	protected int transform(int b) {
		return b;
		
		
		
		
	}
}
