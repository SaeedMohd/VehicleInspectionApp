package com.matics.command;

import android.util.Log;

public class FuelObdCommand extends IntObdCommand{

	public FuelObdCommand(String cmd, String desc, String resType) {
		super(cmd, desc, resType);
	}
	public FuelObdCommand(FuelObdCommand other) {
		super(other);
	}
//	protected int transform(int b) {
//		return b-40;
//	}
	protected int transform(int b) {
		return b;
		
		
		
		
	}
}
