package com.matics.command;

import android.util.Log;

public class FuelTypeObdCommand extends IntObdCommand{

	public FuelTypeObdCommand(String cmd, String desc, String resType) {
		super(cmd, desc, resType);
	}
	public FuelTypeObdCommand(FuelTypeObdCommand other) {
		super(other);
	}
//	protected int transform(int b) {
//		return b-40;
//	}
	protected int transform(int b) {
		return b;
		
		
		
		
	}
}
