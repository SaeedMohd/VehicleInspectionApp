package com.matics.command;

import android.util.Log;

public class CommandedPurgeObdCommand extends IntObdCommand{

	public CommandedPurgeObdCommand(String cmd, String desc, String resType) {
		super(cmd, desc, resType);
	}
	public CommandedPurgeObdCommand(CommandedPurgeObdCommand other) {
		super(other);
	}
//	protected int transform(int b) {
//		return b-40;
//	}
	protected int transform(int b) {
		return b;
		
		
		
		
	}
}
