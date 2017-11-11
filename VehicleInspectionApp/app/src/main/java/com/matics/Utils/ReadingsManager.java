package com.matics.Utils;

import android.content.Context;
import android.util.Log;

import com.matics.Bluetooth.BluetoothApp;
import com.matics.Bluetooth.CommsThread;
import com.matics.LivePhoneReadings;
import com.matics.MainActivity;
import com.matics.R;
import com.matics.adapter.DataBaseHelper;

import java.util.ArrayList;
import java.util.Map;

public class ReadingsManager {

    public static DataBaseHelper dbHelper;
    public static String[] commandsArray;
    public static ArrayList<String> commandsDescriptionList = new ArrayList<String>();
    public static ArrayList<String> commandsCodeList = new ArrayList<String>();
    public static ArrayList<String> commandsUnitList = new ArrayList<String>();
    public static ArrayList<String> commandsResultValuesList;

    public static Context context;

    public static void initializeReadings(final Context context) {
        //Log.dMainActivity.TAG,
//                "Initialize Readings started");
        ReadingsManager.context = context;
        if (commandsCodeList.size() == 0) {
            commandsArray = context.getResources().getStringArray(
                    R.array.OBD_Commands);
            for (String command : commandsArray) {
                String[] commandDetails = command.split("\\|");
                commandsCodeList.add(commandDetails[0]);
                commandsDescriptionList.add(commandDetails[1]);
            }
        }
    }

    public static void setReadingsValuesAndSaveToDB(
            Map<String, String> resultsMap, Context context) {
        if (commandsDescriptionList.isEmpty()) {
            initializeReadings(BluetoothApp.context);
        }
        commandsResultValuesList = new ArrayList<String>(
                commandsCodeList.size());

        //Filling the array with dummy value to make it ready to set values in specific positions
        for (String commandCode : commandsCodeList) {
            commandsResultValuesList.add("0");

        }

        for (Map.Entry<String, String> entry : resultsMap.entrySet()) {
            //This condition is here to get the real commands only and skip the OBD2 initialization commands
            if (commandsCodeList.contains(entry.getKey())) {
                commandsResultValuesList.set(
                        commandsCodeList.indexOf(entry.getKey()), entry.getValue());
            }

            //This condition to get VIN value and prepare the app to get the Vehicle profile from the server and add it to local DB if it is not exists
            if (commandsDescriptionList.indexOf("VIN") == commandsCodeList.indexOf(entry.getKey())) {
                commandsResultValuesList.set(commandsCodeList.indexOf(entry.getKey()), LivePhoneReadings.vin);
            }
        }

        addPhoneReadings();

        if (!LivePhoneReadings.getMake().isEmpty() || LivePhoneReadings.getVin().length() > 0) {
            dbHelper = new DataBaseHelper(context);
            dbHelper.addReadings(commandsDescriptionList, commandsResultValuesList);
        }


        //Log.dMainActivity.TAG, "completed");
    }

    private static void addPhoneReadings() {
        commandsResultValuesList.set(
                commandsDescriptionList.indexOf("Android_Phone_ID"), LivePhoneReadings.getPhoneId(context));
        commandsResultValuesList.set(commandsDescriptionList.indexOf("Latitude"),
                LivePhoneReadings.getLatitude());
        commandsResultValuesList.set(commandsDescriptionList.indexOf("Longitude"),
                LivePhoneReadings.getLongitude());
        commandsResultValuesList.set(commandsDescriptionList.indexOf("GPS_Speed"),
                LivePhoneReadings.getGps_speed());
        commandsResultValuesList.set(commandsDescriptionList.indexOf("GPS_Time"),
                LivePhoneReadings.getGps_time());
        commandsResultValuesList.set(commandsDescriptionList.indexOf("AccountId"),
                LivePhoneReadings.getAccountId());
        commandsResultValuesList.set(commandsDescriptionList.indexOf("Year"),
                LivePhoneReadings.getYear());
        commandsResultValuesList.set(commandsDescriptionList.indexOf("Make"),
                LivePhoneReadings.getMake());
        commandsResultValuesList.set(commandsDescriptionList.indexOf("Model"),
                LivePhoneReadings.getModel());
        commandsResultValuesList.set(commandsDescriptionList.indexOf("Mileage"),
                LivePhoneReadings.getMileage());
        commandsResultValuesList.set(commandsDescriptionList.indexOf("MobileUserProfileId"),
                LivePhoneReadings.getMobileUserProfileId());
        commandsResultValuesList.set(commandsDescriptionList.indexOf("BtId"),
                LivePhoneReadings.getBluetoothId());
        commandsResultValuesList.set(commandsDescriptionList.indexOf("DeviceTime"),
                LivePhoneReadings.getDeviceTime());

        commandsResultValuesList.set(commandsDescriptionList.indexOf("VinRetrievable"),
                "" + LivePhoneReadings.getVinRetrievable());

        if (CommsThread.isNormalProtocolUsed) {
            commandsResultValuesList.set(commandsDescriptionList.indexOf("Protocol"),
                    "Normal");
        } else if (CommsThread.isMultipePidsProtocolUsed) {
            commandsResultValuesList.set(commandsDescriptionList.indexOf("Protocol"),
                    "MultiPids");
        }
    }

    public static ArrayList<String> getReadingsForServerSynch(Context context) {
        //Log.dMainActivity.TAG, "Starting getReadingsForServerSynch");
        dbHelper = new DataBaseHelper(context);
        ArrayList<String> readingsStrings = dbHelper.getReadings();
        //Log.dMainActivity.TAG, "getReadingsForServerSynch completed");
        return readingsStrings;
    }


}
