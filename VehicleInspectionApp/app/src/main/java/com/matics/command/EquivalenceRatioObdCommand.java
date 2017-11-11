package com.matics.command;

import android.util.Log;

public class EquivalenceRatioObdCommand extends IntObdCommand{

	public EquivalenceRatioObdCommand(String cmd, String desc, String resType) {
		super(cmd, desc, resType);
	}
	public EquivalenceRatioObdCommand(EquivalenceRatioObdCommand other) {
		super(other);
	}
//	protected int transform(int b) {
//		return b-40;
//	}
	protected int transform(int b) {
		return b;
		
		
		
		
	}
}
