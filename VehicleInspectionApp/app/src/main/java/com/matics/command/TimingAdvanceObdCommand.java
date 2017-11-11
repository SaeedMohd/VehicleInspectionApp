package com.matics.command;

import android.util.Log;

public class TimingAdvanceObdCommand extends IntObdCommand{

	public TimingAdvanceObdCommand(String cmd, String desc, String resType) {
		super(cmd, desc, resType);
	}
	public TimingAdvanceObdCommand(TimingAdvanceObdCommand other) {
		super(other);
	}
//	protected int transform(int b) {
//		return b-40;
//	}
	protected int transform(int b) {
		return b;
		
		
		
		
	}
}
