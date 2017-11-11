package com.matics.command;

import android.util.Log;

public class AirFlowRateObdCommand extends IntObdCommand{

	public AirFlowRateObdCommand(String cmd, String desc, String resType) {
		super(cmd, desc, resType);
	}
	public AirFlowRateObdCommand(AirFlowRateObdCommand other) {
		super(other);
	}
	protected int transform(int b) {
		return b-40;
	}
//	protected int transform(int b) {
//		return b;
		
//	}
}
