package com.matics.command;

public class TempObdCommandDecode extends IntObdCommandDecode{

	public TempObdCommandDecode(String cmd, String desc, String resType) {
		super(cmd, desc, resType);
	}
	public TempObdCommandDecode(TempObdCommandDecode other) {
		super(other);
	}
	protected int transform(int b) {
		return b-40;
	}
}
