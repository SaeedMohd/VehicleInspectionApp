package com.matics.command;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.matics.Bluetooth.BluetoothApp;
import com.matics.LivePhoneReadings;
import com.matics.MainActivity;
import com.matics.Services.UpdateVehicleHealthStatusTask;
import com.matics.Utils.ApplicationPrefs;

import android.util.Log;

public class CommandsResponseParser {

    public static boolean recoveryModeOn = true;
    public static int noDataCounter = 0;
    public static String rpmValue = "";
    public static String EngineLight = "0";
    public static String coolantTempValue = "";
    public static String vehicleSpeedValue = "";
    public static String fuelLevelValue = "";
    public static String fuelLevel = "";
    public static String engineLoadValue = "", throttleValue = "", traveledValue = "", fuelStatus = "", troubleCodes = "", pendingFaultCodes = "", baromatricValue = "", airFlowValue = "", absoluteValue = "";
    public static String controlModuleVoltage = "";
    public static String bluetoothId = "";
    public static boolean isJustConnected = true;
    public static boolean troubleCodeExist = false;
    public static Map<String, String> results_value = new HashMap<String, String>();
    private static DecimalFormat df = new DecimalFormat("###.##");

    public static void parseResponse(ArrayList<String> responseArrayList) {
        ////Log.i("CommandsResponseParser::parseResponse",""+responseArrayList.toString());
        if (responseArrayList.get(0).equals("nodata") || responseArrayList.get(0).equals("NODATA")) {
//			//Log.i("no data no data","No data, adding one more to NnoDataCounter");
//			noDataCounter++;
//			if(noDataCounter>=25){
//				recoveryModeOn=true;
//			}
            return;
        }

        if (responseArrayList.get(0).contains("UNABLE")) {
            //Log.dMainActivity.TAG, "" + responseArrayList.get(0));
            recoveryModeOn = true;
            return;
        }
        String mode = "";
        if (responseArrayList.get(0).equals("41")) {
            mode = "01";
        } else if (responseArrayList.get(0).startsWith("43")) {
            mode = "03";
        } else if (responseArrayList.get(0).startsWith("44")) {
            mode = "04";
        } else if (responseArrayList.get(0).equals("49")) {
            mode = "09";
        }
        try {
            if (mode.equals("01")) {
                ////Log.i("running command mode one",responseArrayList.get(1));
                if (responseArrayList.get(1).equals("04")) {
                    throttleValue = "Throttle Position: " + df.format((((float) Integer.parseInt(responseArrayList.get(2), 16)) * 100 / 255)) + "%";
                    results_value.put("0104", "" + df.format((((float) Integer.parseInt(responseArrayList.get(2), 16)) * 100 / 255)) + "%");
                } else if (responseArrayList.get(1).equals("05")) {
                    coolantTempValue = "Coolant Temp: " + df.format(((((float) Integer.parseInt(responseArrayList.get(2), 16)) - 40) * 1.8 + 32)) + " F";
                    results_value.put("0105", "" + df.format(((((float) Integer.parseInt(responseArrayList.get(2), 16)) - 40) * 1.8 + 32)) + "F");
                } else if (responseArrayList.get(1).equals("0B")) {
                    results_value.put("010B", df.format(((float) Integer.parseInt(responseArrayList.get(2), 16))) + "kPa");
                } else if (responseArrayList.get(1).equals("0C")) {
                    rpmValue = "Engine RPM: " + ((((Integer.parseInt(responseArrayList.get(2), 16)) * 256) + (Integer.parseInt(responseArrayList.get(3), 16))) / 4) + " RPM";
                    //Log.dMainActivity.TAG, "rpm value = " + rpmValue);
                    results_value.put("010C", "" + ((((Integer.parseInt(responseArrayList.get(2), 16)) * 256) + (Integer.parseInt(responseArrayList.get(3), 16))) / 4) + " RPM");
                } else if (responseArrayList.get(1).equals("0D")) {
                    ////Log.i("speed is here","speed = "+df.format((((float)Integer.parseInt(responseArrayList.get(2),16)))*0.621371)+" km/h");
                    vehicleSpeedValue = "Speed: " + df.format((((float) Integer.parseInt(responseArrayList.get(2), 16))) * 0.621371) + " MPH";
                    results_value.put("010D", df.format(((float) Integer.parseInt(responseArrayList.get(2), 16))) + " MPH");
                } else if (responseArrayList.get(1).equals("0F")) {
                    results_value.put("010F", "" + df.format((((((float) Integer.parseInt(responseArrayList.get(2), 16)) - 40) * 1.8) + 32)) + " F");
                } else if (responseArrayList.get(1).equals("10")) {
                    results_value.put("0110", "" + df.format((((((float) Integer.parseInt(responseArrayList.get(2), 16)) * 256) + ((float) Integer.parseInt(responseArrayList.get(3), 16))) / 4)) + " g/s");
                } else if (responseArrayList.get(1).equals("11")) {
                    results_value.put("0111", "" + df.format((((float) Integer.parseInt(responseArrayList.get(2), 16)) * 100 / 255)) + " %");
                } else if (responseArrayList.get(1).equals("1F")) {
                    results_value.put("011F", "" + df.format((((float) Integer.parseInt(responseArrayList.get(2), 16)) * 256) + ((float) Integer.parseInt(responseArrayList.get(3), 16))) + " seconds");
                } else if (responseArrayList.get(1).equals("23")) {
                    results_value.put("0123", "" + df.format((((((float) Integer.parseInt(responseArrayList.get(2), 16)) * 256) + ((float) Integer.parseInt(responseArrayList.get(3), 16)))) * 10) + " kPa");
                } else if (responseArrayList.get(1).equals("2F")) {
                    results_value.put("012F", "" + df.format((((float) Integer.parseInt(responseArrayList.get(2), 16)) * 100 / 255)) + " %");
                    fuelLevelValue = df.format((((float) Integer.parseInt(responseArrayList.get(2), 16)) * 100 / 255));
                    fuelLevel = "Fuel Level: " + fuelLevelValue;
                } else if (responseArrayList.get(1).equals("31")) {
                    results_value.put("0131", "" + df.format((((float) Integer.parseInt(responseArrayList.get(2), 16)) * 256) + ((float) Integer.parseInt(responseArrayList.get(3), 16))) + " km");
                } else if (responseArrayList.get(1).equals("RV")) {
                    ////Log.i("Control Module Voltage","Contol Module Voltage= "+responseArrayList.get(2));
                    controlModuleVoltage = "Control Module Voltage: " + responseArrayList.get(2);
                    results_value.put("0142", "" + responseArrayList.get(2));
                } else if (responseArrayList.get(1).equals("46")) {
                    results_value.put("0146", "" + df.format((((((float) Integer.parseInt(responseArrayList.get(2), 16)) - 40) * 1.8) + 32)) + " F");
                }
            } else if (mode.equals("03")) {
                //Log.dMainActivity.TAG, "trouble code = " + responseArrayList.get(0));
                results_value.put("03", "" + responseArrayList.get(0));

                if (responseArrayList.get(0).startsWith("43") && responseArrayList.get(0).replace("43","").replace("00","").length()!=0) {
                    //Log.dMainActivity.TAG, "***************************************");
                    //Log.dMainActivity.TAG, "There is a trouble code");
                    //Log.dMainActivity.TAG, "trouble code exists? " + troubleCodeExist);
                    if (!troubleCodeExist || isJustConnected) {
                        isJustConnected = false;
                        //new UpdateVehicleHealthStatusTask().execute(LivePhoneReadings.getVin());
                    }
                    troubleCodeExist = true;
                    ApplicationPrefs.getInstance(BluetoothApp.context).setVehicleHealthValuePref(30);
                    LivePhoneReadings.setVehicleHealthValue(30);

                } else if (responseArrayList.get(0).startsWith("43") && responseArrayList.get(0).replace("43","").replace("00","").length()==0) {
                    if (troubleCodeExist || isJustConnected) {
                        isJustConnected = false;
                        new UpdateVehicleHealthStatusTask().execute(LivePhoneReadings.getVin());
                    }
                    troubleCodeExist = false;
                }

            } else if (mode.equals("04")) {
                //Log.dMainActivity.TAG, "Trouble code cleared. setting clear trouble code required to false");
                ApplicationPrefs.getInstance(BluetoothApp.context).setIsClearTroubleCodeRequired(false);
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    public static void parseMultiPidsCommandResponse(String multiPidsResponse) {
        //Log.dMainActivity.TAG, "command response is: " + multiPidsResponse);
        if (!multiPidsResponse.contains("NO") && !multiPidsResponse.contains("UNABLE") && !multiPidsResponse.contains("no") && !multiPidsResponse.contains("unable")) {
            ArrayList<String> responseArrayList = new ArrayList<String>();
            if (multiPidsResponse.startsWith("RV")) {
                responseArrayList.add(0, "41");
                String[] responseArray = multiPidsResponse.split(" ");
                responseArrayList.addAll(Arrays.asList(responseArray));
                ////Log.i("CommandsResponseParser::parseMultiPidsCommandResponse","*******responseArraList= "+responseArrayList.toString());
                parseResponse(responseArrayList);
                return;
            }

            if (multiPidsResponse.startsWith("43")) {
                responseArrayList.add(0, "43");
                String[] responseArray = multiPidsResponse.split(" ");
                responseArrayList.addAll(Arrays.asList(responseArray));
                ////Log.i("CommandsResponseParser::parseMultiPidsCommandResponse","*******responseArraList= "+responseArrayList.toString());
                parseResponse(responseArrayList);
                return;
            }

            if (multiPidsResponse.startsWith("47")) {
                responseArrayList.add(0, "47");
                String[] responseArray = multiPidsResponse.split(" ");
                responseArrayList.addAll(Arrays.asList(responseArray));
                ////Log.i("CommandsResponseParser::parseMultiPidsCommandResponse","*******responseArraList= "+responseArrayList.toString());
                parseResponse(responseArrayList);
                return;
            }

            String[] responseArray = multiPidsResponse.split(" ");
            String availableBytesString = responseArray[0];
            int availableBytesInteger = Integer.parseInt(availableBytesString, 16);
            ////Log.i("availableBytesInteger","availableBytesInteger = "+availableBytesInteger);
            for (int x = 1; x < responseArray.length; x++) {
                // //Log.i("looping on values",""+responseArray[x]);
                if (responseArrayList.size() < availableBytesInteger) {
                    if (!responseArray[x].contains(":")
                            //&& !responseArray[x].contains("41")
                            && !responseArray[x].contains(" ")) {
                        ////Log.i("adding in array", "" + responseArray[x]);
                        responseArrayList.add(responseArray[x]);
                    }
                }
            }


            String mode = responseArrayList.get(0);
            for (int x = 1; x < responseArrayList.size(); x++) {
                ArrayList<String> subResponseToParse = new ArrayList<String>();
                subResponseToParse.add(mode);
                if (x + 3 < responseArrayList.size()) {
                    subResponseToParse.addAll(responseArrayList.subList(x, x + 3));
                } else {
                    subResponseToParse.addAll(responseArrayList.subList(x, responseArrayList.size()));
                }
                parseResponse(subResponseToParse);

            }

        } else {
            BluetoothApp.isOBD2LockingPreventionRequired = true;
        }
    }

}
