package com.matics.command;

public class FuelPressureObdCommand extends IntObdCommand{

	public FuelPressureObdCommand(String cmd, String desc, String resType) {
		super(cmd, desc, resType);
	}
	public FuelPressureObdCommand(FuelPressureObdCommand other) {
		super(other);
	}
//	public int transform(int b) {
//		return b*3;
//	}
	public int transform(int b) {
		return b;
	}
}
