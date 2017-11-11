package com.matics.command;

import android.util.Log;

public class OxySenserPresentObdCommand extends IntObdCommand{

	public OxySenserPresentObdCommand(String cmd, String desc, String resType) {
		super(cmd, desc, resType);
	}
	public OxySenserPresentObdCommand(OxySenserPresentObdCommand other) {
		super(other);
	}
//	protected int transform(int b) {
//		return b-40;
//	}
	protected int transform(int b) {
		return b;
		
		
		
		
	}
}
