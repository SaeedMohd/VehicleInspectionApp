package com.matics.command;

import com.matics.Bluetooth.BluetoothApp;
import com.matics.Utils.ApplicationPrefs;

import java.util.ArrayList;

public class CommandsResource {


    public static ArrayList<ObdCommand> getCommands() {
        ArrayList<ObdCommand> cmds = new ArrayList<ObdCommand>();

        cmds.add(new ObdCommand("0110", "Mass Airflow", "g/s"));
        cmds.add(new ObdCommand("010F", "Air Intake Temp", "C"));
        cmds.add(new ObdCommand("010B", "Intake Manifold Press", "kPa"));
//  		cmds.add(new IntObdCommand("0133", "Barometric Press", "kPa"));
        cmds.add(new ObdCommand("0146", "Ambient Air Temp", "C"));
        cmds.add(new ObdCommand("010D", "Vehicle Speed", "km/h"));
        cmds.add(new ObdCommand("0111", "Throttle Position", "%"));
        cmds.add(new ObdCommand("010C", "Engine RPM", "RPM"));
//  		cmds.add(new FuelPressureObdCommand("010A", "Fuel Press", "kPa"));
        cmds.add(new ObdCommand("0105", "Coolant Temp", "C"));
        cmds.add(new ObdCommand("0104", "Engine Load", "%"));
//  		cmds.add(new TotalDistanceObdCommand("0121", "Total miles", ""));// Distance traveled since codes cleared
        //cmds.add(new ObdCommand("0902", "VIN", "code"));
        cmds.add(new ObdCommand("03", "Trouble Codes", ""));
        cmds.add(new ObdCommand("07", "MultiPidsCommand", ""));
        ////cmds.add(new TroubleCodesObdCommand7("03", "Trouble Codes", ""));

        //-----------------------------------New Without DeCode-----------------------
        cmds.add(new ObdCommand("012F", "Fuel Level Input", "%"));
//  		cmds.add(new FuelObdCommand("0103", "Fuel status", ""));
        cmds.add(new ObdCommand("0103", "Fuel Status", ""));
//  		cmds.add(new OBDStarndardObdCommand("011C", "OBD Standard", ""));
//  		cmds.add(new OxySenserPresentObdCommand("011D", "Oxygen sensors present", ""));
        cmds.add(new ObdCommand("011F", "Run time since engine start", "seconds"));
        cmds.add(new ObdCommand("0123", "Fuel Rail Pressure", "kPa"));
//  		cmds.add(new CommandedPurgeObdCommand("012E", "Commanded evaporative purge", "%"));
//  		cmds.add(new VaporPressureObdCommand("0132", "Evap. System Vapor Pressure", "Pa"));
        cmds.add(new ObdCommand("ATRV", "Control module voltage", "V"));

        if (ApplicationPrefs.getInstance(BluetoothApp.context).isClearTroubleCodeRequired()) {
            cmds.add(new ObdCommand("04", "Clear_trouble_code 04", ""));
        }

//  		cmds.add(new AbsoluteLoadValueObdCommand("0143", "Absolute load value", "%"));
//  		cmds.add(new EquivalenceRatioObdCommand("0144", "Command equivalence ratio", ""));
//  		cmds.add(new FuelTypeObdCommand("0151", "Fuel Type", "V"));
//  		cmds.add(new EthanolFuelObdCommand("0152", "Ethanol fuel", "%"));
//  		
//  		cmds.add(new EthanolFuelObdCommand("0101", "Monitor_status", ""));
//  		cmds.add(new EthanolFuelObdCommand("0106", "Short_term_fuel_B1", ""));
//  		cmds.add(new EthanolFuelObdCommand("0107", "Long_term_fuel_B1", ""));
//  		cmds.add(new EthanolFuelObdCommand("0108", "Short_term_fuel_B2", ""));
//  		cmds.add(new EthanolFuelObdCommand("0109", "Long_term_fuel_B2", ""));
//  		cmds.add(new EthanolFuelObdCommand("0113", "Oxy_sensor_present", ""));
//  		cmds.add(new EthanolFuelObdCommand("0114", "Oxy_sensor_voltage_B1S1", ""));
//  		cmds.add(new EthanolFuelObdCommand("0115", "Oxy_sensor_voltage_B1S2", ""));
//  		cmds.add(new EthanolFuelObdCommand("0116", "Oxy_sensor_voltage_B1S3", ""));
//  		cmds.add(new EthanolFuelObdCommand("0117", "Oxy_sensor_voltage_B1S4", ""));
//  		cmds.add(new EthanolFuelObdCommand("0118", "Oxy_sensor_voltage_B2S1", ""));
//  		cmds.add(new EthanolFuelObdCommand("0119", "Oxy_sensor_voltage_B2S2", ""));
//  		cmds.add(new EthanolFuelObdCommand("011A", "Oxy_sensor_voltage_B2S3", ""));
//  		cmds.add(new EthanolFuelObdCommand("011B", "Oxy_sensor_voltage_B2S4", ""));
//  		cmds.add(new EthanolFuelObdCommand("011E", "Auxiliary_input_status", ""));
        //cmds.add(new ObdCommand("0120", "PID_supported", ""));
//  		cmds.add(new EthanolFuelObdCommand("0124", "Equivalence_Ratio_O2S1_V", ""));
//  		cmds.add(new EthanolFuelObdCommand("0125", "Equivalence_Ratio_O2S2_V", ""));
//  		cmds.add(new EthanolFuelObdCommand("0126", "Equivalence_Ratio_O2S3_V", ""));
//  		cmds.add(new EthanolFuelObdCommand("0127", "Equivalence_Ratio_O2S4_V", ""));
//  		cmds.add(new EthanolFuelObdCommand("0128", "Equivalence_Ratio_O2S5_V", ""));
//  		cmds.add(new EthanolFuelObdCommand("0129", "Equivalence_Ratio_O2S6_V", ""));
//  		cmds.add(new EthanolFuelObdCommand("012A", "Equivalence_Ratio_O2S7_V", ""));
//  		cmds.add(new EthanolFuelObdCommand("012B", "Equivalence_Ratio_O2S8_V", ""));
//  		cmds.add(new EthanolFuelObdCommand("012C", "Commanded_EGR", ""));
//  		cmds.add(new EthanolFuelObdCommand("012D", "EGR_Error", ""));
//  		cmds.add(new EthanolFuelObdCommand("0130", "No_WarmUps", ""));
        cmds.add(new ObdCommand("0131", "Dist_Traveled", ""));
//  		cmds.add(new EthanolFuelObdCommand("0134", "Equivalence_Ratio_O2S1_C", ""));
//  		cmds.add(new EthanolFuelObdCommand("0135", "Equivalence_Ratio_O2S2_C", ""));
//  		cmds.add(new EthanolFuelObdCommand("0136", "Equivalence_Ratio_O2S3_C", ""));
//  		cmds.add(new EthanolFuelObdCommand("0137", "Equivalence_Ratio_O2S4_C", ""));
//  		cmds.add(new EthanolFuelObdCommand("0138", "Equivalence_Ratio_O2S5_C", ""));
//  		cmds.add(new EthanolFuelObdCommand("0139", "Equivalence_Ratio_O2S6_C", ""));
//  		cmds.add(new EthanolFuelObdCommand("013A", "Equivalence_Ratio_O2S7_C", ""));
//  		cmds.add(new EthanolFuelObdCommand("013B", "Equivalence_Ratio_O2S8_C", ""));
//  		cmds.add(new EthanolFuelObdCommand("013C", "Catalyst_Temp_B1S1", ""));
//  		cmds.add(new EthanolFuelObdCommand("013D", "Catalyst_Temp_B2S1", ""));
//  		cmds.add(new EthanolFuelObdCommand("013E", "Catalyst_Temp_B1S2", ""));
//  		cmds.add(new EthanolFuelObdCommand("013F", "Catalyst_Temp_B2S2", ""));
//  		cmds.add(new EthanolFuelObdCommand("0140", "PID_supported_41", ""));
//  		cmds.add(new EthanolFuelObdCommand("0141", "Monitor_status_Cycle", ""));
//  		cmds.add(new EthanolFuelObdCommand("0145", "Real_Throttle_position", ""));
//  		cmds.add(new EthanolFuelObdCommand("0147", "Absolute_Throttle_position_B", ""));
//  		cmds.add(new EthanolFuelObdCommand("0148", "Absolute_Throttle_position_C", ""));
//  		cmds.add(new EthanolFuelObdCommand("0149", "Accelerator_pedal_position_D", ""));
//  		cmds.add(new EthanolFuelObdCommand("014A", "Accelerator_pedal_position_E", ""));
//  		cmds.add(new EthanolFuelObdCommand("014B", "Accelerator_pedal_position_F", ""));
//  		cmds.add(new EthanolFuelObdCommand("014C", "Commanded_throttle_actuator", ""));
//  		
//  		cmds.add(new EthanolFuelObdCommand("014D", "Time_MIL", ""));
//  		cmds.add(new EthanolFuelObdCommand("014E", "Time_trouble_code", ""));
//  		cmds.add(new EthanolFuelObdCommand("0153", "Evap_system_Vapour_Pressure", ""));
//  		cmds.add(new EthanolFuelObdCommand("0202", "Freeze_frame_trouble_code", ""));

//  		cmds.add(new EthanolFuelObdCommand("050100", "OBD_Monitor_ID_Supported", ""));
//  		cmds.add(new EthanolFuelObdCommand("050101", "O2_sensor_Monitor_B1S1_Learn", ""));
//  		cmds.add(new EthanolFuelObdCommand("050102", "O2_sensor_Monitor_B1S2_Learn", ""));
//  		cmds.add(new EthanolFuelObdCommand("050103", "O2_sensor_Monitor_B1S3_Learn", ""));
//  		cmds.add(new EthanolFuelObdCommand("050104", "O2_sensor_Monitor_B1S4_Learn", ""));
//  		
//  		cmds.add(new EthanolFuelObdCommand("050105", "O2_sensor_Monitor_B2S1_Learn", ""));
//  		cmds.add(new EthanolFuelObdCommand("050106", "O2_sensor_Monitor_B2S2_Learn", ""));
//  		cmds.add(new EthanolFuelObdCommand("050107", "O2_sensor_Monitor_B2S3_Learn", ""));
//  		cmds.add(new EthanolFuelObdCommand("050108", "O2_sensor_Monitor_B2S4_Learn", ""));
//  		cmds.add(new EthanolFuelObdCommand("050109", "O2_sensor_Monitor_B3S1_Learn", ""));
//  		cmds.add(new EthanolFuelObdCommand("05010A", "O2_sensor_Monitor_B3S2_Learn", ""));
//  		cmds.add(new EthanolFuelObdCommand("05010B", "O2_sensor_Monitor_B3S3_Learn", ""));
//  		cmds.add(new EthanolFuelObdCommand("05010C", "O2_sensor_Monitor_B3S4_Learn", ""));
//  		cmds.add(new EthanolFuelObdCommand("05010D", "O2_sensor_Monitor_B4S1_Learn", ""));
//  		cmds.add(new EthanolFuelObdCommand("05010E", "O2_sensor_Monitor_B4S2_Learn", ""));
//  		cmds.add(new EthanolFuelObdCommand("05010F", "O2_sensor_Monitor_B4S3_Learn", ""));
//  		cmds.add(new EthanolFuelObdCommand("050110", "O2_sensor_Monitor_B4S4_Learn", ""));
//  		cmds.add(new EthanolFuelObdCommand("050201", "O2_sensor_Monitor_B1S1_Rich", ""));
//  		cmds.add(new EthanolFuelObdCommand("050202", "O2_sensor_Monitor_B1S2_Rich", ""));
//  		cmds.add(new EthanolFuelObdCommand("050203", "O2_sensor_Monitor_B1S3_Rich", ""));
//  		cmds.add(new EthanolFuelObdCommand("050204", "O2_sensor_Monitor_B1S4_Rich", ""));
//  		cmds.add(new EthanolFuelObdCommand("050205", "O2_sensor_Monitor_B2S1_Rich", ""));
//  		cmds.add(new EthanolFuelObdCommand("050206", "O2_sensor_Monitor_B2S2_Rich", ""));
//  		cmds.add(new EthanolFuelObdCommand("050207", "O2_sensor_Monitor_B2S3_Rich", ""));
//  		cmds.add(new EthanolFuelObdCommand("050208", "O2_sensor_Monitor_B2S4_Rich", ""));
//  		cmds.add(new EthanolFuelObdCommand("050209", "O2_sensor_Monitor_B3S1_Rich", ""));
//  		cmds.add(new EthanolFuelObdCommand("05020A", "O2_sensor_Monitor_B3S2_Rich", ""));
//  		cmds.add(new EthanolFuelObdCommand("05020B", "O2_sensor_Monitor_B3S3_Rich", ""));
//  		cmds.add(new EthanolFuelObdCommand("05020C", "O2_sensor_Monitor_B3S4_Rich", ""));
//  		cmds.add(new EthanolFuelObdCommand("05020D", "O2_sensor_Monitor_B4S1_Rich", ""));
//  		cmds.add(new EthanolFuelObdCommand("05020E", "O2_sensor_Monitor_B4S2_Rich", ""));
//  		cmds.add(new EthanolFuelObdCommand("05020F", "O2_sensor_Monitor_B4S3_Rich", ""));
//  		cmds.add(new EthanolFuelObdCommand("050210", "O2_sensor_Monitor_B4S4_Rich", ""));
//  		cmds.add(new EthanolFuelObdCommand("0900", "Mode_9_supported", ""));
//  		cmds.add(new EthanolFuelObdCommand("0122", "Fuel_Rail_Pressure_D", ""));


        return cmds;
    }

    //---------------------Decoded Commands which will be displayed in listview in setting Screen 
    public static ArrayList<ObdCommand> getDecodeCommands() {
        ArrayList<ObdCommand> cmds = new ArrayList<ObdCommand>();
        //cmds.add(new EngineRPMObdCommandDecode("010C", "Engine RPM", " RPM"));
        cmds.add(new ObdCommand("010C", "Engine RPM", " RPM"));
        cmds.add(new ObdCommand("0142", "Control module voltage", "V"));
//		cmds.add(new TempObdCommandDecode("0105", "Coolant Temp", " C"));
        cmds.add(new ObdCommand("010D", "Vehicle Speed", ""));
//		cmds.add(new FuelObdCommandDecode("012F", "Fuel Level Input", " %"));

//		cmds.add(new VinObdCommand("0902", "VIN", "code"));
//		cmds.add(new ThrottleObdCommand("0104", "Engine Load", "%"));
//		cmds.add(new ThrottleObdCommand("0111", "Throttle Position", "%"));
//		cmds.add(new IntObdCommand2("0131", "Dist_Traveled", ""));
//		cmds.add(new IntObdCommand2("0103", "Fuel status", ""));
//		cmds.add(new IntObdCommand2("03", "Trouble Codes", ""));
//		cmds.add(new IntObdCommand2("07", "Clear_trouble_code", ""));
//		cmds.add(new MassAirflowObdCommand("0110", "Air Flow Rate", "g/s"));
//		cmds.add(new IntObdCommand2("0133", "Barometric Press", "kPa"));  //7F0112
//		cmds.add(new IntObdCommand1("0143", "Absolute load value", "%"));  //7F0112
//		cmds.add(new IntObdCommand1("0101", "EngineLight", ""));
        return cmds;
    }


    //--------------------Static commands for initialization. which will be called only once whicle running Application
    public static ArrayList<ObdCommand> getStaticCommands() {
        ArrayList<ObdCommand> cmds = new ArrayList<ObdCommand>();
        cmds.add(new ObdCommand("ATZ", "Initialize", ""));
//  		cmds.add(new ObdCommand("ATD", "Initialize", ""));
//  		cmds.add(new ObdCommand("ATWS", "Initialize", ""));
//  		cmds.add(new ObdCommand("ATD", "Initialize", ""));
//  		cmds.add(new ObdCommand("AT PP FF OFF", "Initialize", ""));
//  		cmds.add(new ObdCommand("AT CS", "Initialize", ""));
//  		cmds.add(new ObdCommand("ATZ", "Initialize", ""));
        cmds.add(new ObdCommand("ATSP0", "Protocol Initialize", "C"));
        //cmds.add(new ObdCommand("ATV0", "Initialize", ""));
        //cmds.add(new ObdCommand("ATSF0", "forget event",""));

        //cmds.add(new ObdCommand("AT PP 2A b00", "pp2A", "C"));
//  		cmds.add(new ObdCommand("AT @1", "@1", "C"));
//  		cmds.add(new ObdCommand("AT @2", "@2", "C"));
//  		cmds.add(new ObdCommand("AT @3", "@3", "C"));
        cmds.add(new ObdCommand("ATE0", "Echo Off", "C"));
        cmds.add(new ObdCommand("ATH0", "Headers OFF", "C"));
        cmds.add(new ObdCommand("ATRV", "voltage)", "C"));
        return cmds;
    }

//  	public static ArrayList<ObdCommand> getModeSupportedPIDsCommands()
//  	{
//  		ArrayList<ObdCommand> cmds = new ArrayList<ObdCommand>();
//  		cmds.add(new ModeSupportedPIDsCommand("0900", "Protocol search order)", "C"));
//  		return cmds;
//  	}

    //----------------Combining All command Array into one Array
    public static ArrayList<ObdCommand> getAllCommands() {
        ArrayList<ObdCommand> cmds = new ArrayList<ObdCommand>();
        //cmds.addAll(getStaticCommands());
        cmds.addAll(getCommands());
        return cmds;
    }


    public static ArrayList<MultiplePidObdCommand> getMultiPIDCommands() {
        ArrayList<MultiplePidObdCommand> cmds = new ArrayList<MultiplePidObdCommand>();
        //cmds.add(new MultiplePidObdCommand("0104050B42", "RPM And Cooland Temp", ""));
        cmds.add(new MultiplePidObdCommand("010C0D05", "010C0D05", ""));
        cmds.add(new MultiplePidObdCommand("010C0F04", "010C0F04", ""));
        cmds.add(new MultiplePidObdCommand("010C111F", "010C111F", ""));
        cmds.add(new MultiplePidObdCommand("010C2331", "010C2331", ""));
        cmds.add(new MultiplePidObdCommand("010C0B46", "010C0B46", ""));
        cmds.add(new MultiplePidObdCommand("010C2F10", "010C2F10", ""));
        cmds.add(new MultiplePidObdCommand("0101", "DTC", ""));
        //cmds.add(new MultiplePidObdCommand("0142", "MultiPidsCommand", ""));
        cmds.add(new MultiplePidObdCommand("ATRV", "ATRV", ""));
        cmds.add(new MultiplePidObdCommand("03", "03", ""));
        cmds.add(new MultiplePidObdCommand("07", "07", ""));

        if (ApplicationPrefs.getInstance(BluetoothApp.context).isClearTroubleCodeRequired()) {
            cmds.add(new MultiplePidObdCommand("04", "Clear_trouble_code 04", ""));
        }

        return cmds;
    }
}





