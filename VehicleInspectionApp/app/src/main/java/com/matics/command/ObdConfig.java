package com.matics.command;

import java.util.ArrayList;

public class ObdConfig
{

	public static ArrayList<ObdCommand> getCommands()
	{
		ArrayList<ObdCommand> cmds = new ArrayList<ObdCommand>();
	/*	
		cmds.add(new ObdCommand("010D", "Vehicle Speed", "km/h"));
		cmds.add(new ObdCommand("010C", "Engine RPM", "RPM"));
		
		cmds.add(new ObdCommand("0110", "Mass Airflow", "g/s"));
		cmds.add(new ObdCommand("010F", "Air Intake Temp", "C"));
		cmds.add(new ObdCommand("010B", "Intake Manifold Press", "kPa"));
		cmds.add(new ObdCommand("0133", "Barometric Press", "kPa"));
		cmds.add(new ObdCommand("0146", "Ambient Air Temp", "C"));
		cmds.add(new ObdCommand("010D", "Vehicle Speed", "km/h"));		
		cmds.add(new ObdCommand("0111", "Throttle Position", "%"));
		cmds.add(new ObdCommand("010C", "Engine RPM", "RPM"));
		cmds.add(new ObdCommand("010A", "Fuel Press", "kPa"));
		cmds.add(new ObdCommand("0105", "Coolant Temp", "C"));
		cmds.add(new ObdCommand("0104", "Engine Load", "%"));
		cmds.add(new ObdCommand("0131", "Total miles", ""));
		cmds.add(new ObdCommand("0902", "VIN", "code"));
		*/
		
		cmds.add(new MassAirflowObdCommand("0110", "Mass Airflow", "g/s"));
		cmds.add(new TempObdCommand("010F", "Air Intake Temp", "C"));
		cmds.add(new IntObdCommand("010B", "Intake Manifold Press", "kPa"));
		cmds.add(new IntObdCommand("0133", "Barometric Press", "kPa"));
		cmds.add(new TempObdCommand("0146", "Ambient Air Temp", "C"));
		cmds.add(new IntObdCommand("010D", "Vehicle Speed", "km/h"));		
		cmds.add(new ThrottleObdCommand("0111", "Throttle Position", "%"));
		cmds.add(new EngineRPMObdCommand("010C", "Engine RPM", "RPM"));
		cmds.add(new FuelPressureObdCommand("010A", "Fuel Press", "kPa"));
		cmds.add(new TempObdCommand("0105", "Coolant Temp", "C"));
		cmds.add(new ThrottleObdCommand("0104", "Engine Load", "%"));
		cmds.add(new TotalDistanceObdCommand("0121", "Total miles", ""));// Distance traveled since codes cleared
		cmds.add(new VinObdCommand("0902", "VIN", "code"));
		//cmds.add(new DtcNumberObdCommand("0101", "Trouble Code Status", ""));
		cmds.add(new TroubleCodesObdCommand7("03", "Trouble Codes", ""));
		//cmds.add(new ObdCommand("03", "Protocol search order)", "C"));
//		AT E0
//		for(int i=0;i<10;i++)
//		{
//			cmds.add(new TroubleCodesObdCommand("P000"+i, "Trouble Codes:"+i+"::", ""));
//		}
		//cmds.add(new DtcNumberObdCommand("0101", "Diagnostic trouble code (DTC) in another control module.", ""));/////
		
		return cmds;
	}

	public static ArrayList<ObdCommand> getStaticCommands()
	{
		ArrayList<ObdCommand> cmds = new ArrayList<ObdCommand>();
		cmds.add(new ObdCommand("ATZ", "Initialize", ""));
		cmds.add(new ObdCommand("ATSP0", "Protocol Initialize", "C"));
		cmds.add(new ObdCommand("ate0", "Protocol search order)", "C"));
		return cmds;
	}
	public static ArrayList<ObdCommand> getAllCommands()
	{
		ArrayList<ObdCommand> cmds = new ArrayList<ObdCommand>();
		cmds.addAll(getStaticCommands());
		cmds.addAll(getCommands());
		return cmds;
	}
}
