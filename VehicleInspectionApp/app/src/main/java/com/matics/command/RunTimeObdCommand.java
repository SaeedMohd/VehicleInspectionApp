package com.matics.command;

import android.util.Log;

public class RunTimeObdCommand extends IntObdCommand{

	public RunTimeObdCommand(String cmd, String desc, String resType) {
		super(cmd, desc, resType);
	}
	public RunTimeObdCommand(RunTimeObdCommand other) {
		super(other);
	}
//	protected int transform(int b) {
//		return b-40;
//	}
	protected int transform(int b) {
		return b;
		
		
		
		
	}
}
