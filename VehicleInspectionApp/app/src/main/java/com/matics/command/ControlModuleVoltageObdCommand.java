package com.matics.command;

import android.util.Log;

public class ControlModuleVoltageObdCommand extends IntObdCommand{

	public ControlModuleVoltageObdCommand(String cmd, String desc, String resType) {
		super(cmd, desc, resType);
	}
	public ControlModuleVoltageObdCommand(ControlModuleVoltageObdCommand other) {
		super(other);
	}
//	protected int transform(int b) {
//		return b-40;
//	}
	protected int transform(int b) {
		return b;
		
		
		
		
	}
}
