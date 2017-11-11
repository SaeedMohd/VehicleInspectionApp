package com.matics.command;

import android.util.Log;

public class AbsoluteLoadValueObdCommand extends IntObdCommand{

	public AbsoluteLoadValueObdCommand(String cmd, String desc, String resType) {
		super(cmd, desc, resType);
	}
	public AbsoluteLoadValueObdCommand(AbsoluteLoadValueObdCommand other) {
		super(other);
	}
//	protected int transform(int b) {
//		return b-40;
//	}
	protected int transform(int b) {
		return b;
		
		
		
		
	}
}
