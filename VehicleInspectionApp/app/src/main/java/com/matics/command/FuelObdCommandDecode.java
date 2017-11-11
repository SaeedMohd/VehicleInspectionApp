package com.matics.command;

public class FuelObdCommandDecode extends IntObdCommandDecodeVin{

	public FuelObdCommandDecode(String cmd, String desc, String resType) {
		super(cmd, desc, resType);
	}
	public FuelObdCommandDecode(FuelObdCommandDecode other) {
		super(other);
	}
	protected int transform(int b) {
		return (100*b)/255;
	}
}
