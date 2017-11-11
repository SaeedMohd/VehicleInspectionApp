package com.matics.command;

import android.util.Log;

public class EthanolFuelObdCommand extends IntObdCommand{

	public EthanolFuelObdCommand(String cmd, String desc, String resType) {
		super(cmd, desc, resType);
	}
	public EthanolFuelObdCommand(EthanolFuelObdCommand other) {
		super(other);
	}
//	protected int transform(int b) {
//		return b-40;
//	}
	protected int transform(int b) {
		return b;
		
		
		
		
	}
}
