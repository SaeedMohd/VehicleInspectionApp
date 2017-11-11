package com.matics.Bluetooth;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import com.matics.GCM.GcmRegistration;
import com.matics.LivePhoneReadings;
import com.matics.MainActivity;
import com.matics.adapter.DataBaseHelper;
import com.matics.command.CommandsResource;
import com.matics.command.CommandsResponseParser;
import com.matics.command.MultiplePidObdCommand;
import com.matics.command.ObdCommand;
import com.matics.command.VinObdCommand;
import com.matics.model.VehicleProfileModel;
import com.matics.serverTasks.GetProfileTask;
import com.matics.Utils.ApplicationPrefs;
import com.matics.Utils.ReadingsManager;

public class CommsThread extends Thread {

    final BluetoothSocket bluetoothSocket;
    public final BufferedInputStream inputStream;
    public final OutputStream outputStream;
    final StringBuilder fullMessage;
    int deviceIndexInSlavesArray;
    UUID uuid;

    public Handler mHandler;
    List<String> results_Decode = new ArrayList<String>();
    String result_Decode;
    private ArrayList<ObdCommand> DecodeCommands = new ArrayList<ObdCommand>();
    int serverResponseCode = 0;
    public static Boolean DetailFlag = true;
    public static String connectedbluetooth = null;
    private int recoveryModeCounter = 0;
    private HashMap<String, Boolean> availablePIDs;
    public static boolean isNormalProtocolUsed = false;
    public static boolean isMultipePidsProtocolUsed = false;
    // private GPSTracker gpsTracker;
    private boolean isRpmReturnsNoData = false;
    private boolean isSpeedReturnsNoData = false;
    private boolean isCoolantTempReturnsNoData = false;

    public static boolean isVinCalled = false;

    public CommsThread(BluetoothSocket socket, UUID uuid, String btId) {
        bluetoothSocket = socket;
        BufferedInputStream tmpIn = null;
        OutputStream tmpOut = null;
        fullMessage = new StringBuilder();
        this.uuid = uuid;

        try {
            tmpIn = new BufferedInputStream(socket.getInputStream());
            tmpOut = socket.getOutputStream();

        } catch (IOException e) {
            // isConnected = false;
            e.printStackTrace();
        }
        inputStream = tmpIn;
        outputStream = tmpOut;

        availablePIDs = new HashMap<String, Boolean>();

        initializeReadings();
        BluetoothApp.showNotification(BluetoothApp.context);

        LivePhoneReadings.bluetoothId = bluetoothSocket.getRemoteDevice()
                .getAddress();


        if (ApplicationPrefs.getInstance(BluetoothApp.context).getLastConnectedVinId() >= 0) {
            //Log.dMainActivity.TAG, "in last vin");
            DataBaseHelper dbHelper = new DataBaseHelper(
                    BluetoothApp.context);
            VehicleProfileModel vehicleProfileModel = dbHelper
                    .getVehicleProfileDataWithVinId(ApplicationPrefs.getInstance(BluetoothApp.context).getLastConnectedVinId());
            if (!vehicleProfileModel.getBtID().equals("")) {
                ApplicationPrefs.getInstance(
                        BluetoothApp.context)
                        .setVehicleProfilePrefs(
                                vehicleProfileModel);

            }
            ApplicationPrefs.getInstance(BluetoothApp.context).setVehicleHealthValuePref(vehicleProfileModel.getVehicleHealth());
            ApplicationPrefs.getInstance(BluetoothApp.context).setVehicleHealthMessagePref(vehicleProfileModel.getReason());

        } else {

            DataBaseHelper dbHelper = new DataBaseHelper(
                    BluetoothApp.context);
            VehicleProfileModel vehicleProfileModel = dbHelper
                    .getprofiledataUsingBluetoothId(LivePhoneReadings.bluetoothId);
            if (!vehicleProfileModel.getBtID().equals("")) {
                ApplicationPrefs.getInstance(
                        BluetoothApp.context)
                        .setVehicleProfilePrefs(
                                vehicleProfileModel);

            }
            ApplicationPrefs.getInstance(BluetoothApp.context).setVehicleHealthValuePref(vehicleProfileModel.getVehicleHealth());
            ApplicationPrefs.getInstance(BluetoothApp.context).setVehicleHealthMessagePref(vehicleProfileModel.getReason());
        }


    }

    private void initializeReadings() {
        CommandsResponseParser.rpmValue = "NO DATA";
        CommandsResponseParser.controlModuleVoltage = "NO DATA";
        CommandsResponseParser.coolantTempValue = "NO DATA";
        CommandsResponseParser.vehicleSpeedValue = "NO DATA";
        // CommandsResponseParser.VIN= LivePhoneReadings.VIN;

    }

    public synchronized String runVINCommand(VinObdCommand cmd)
            throws InterruptedException, IOException {
        ////Log.i("running command", "command description: " + cmd.getDesc());
        cmd.setInputStream(inputStream);
        cmd.setOutputStream(outputStream);
        cmd.start();
        int counter = 0;
        while (BluetoothApp.isConnectionEstablished) {
            cmd.join(20);
            if (!cmd.isAlive()) {
                break;
            }
            if (counter > 400) {
                cancel();
            }
            counter++;

        }

        return cmd.formatResult();
    }

    public synchronized String runProtocolSelectionCommand(ObdCommand cmd)
            throws InterruptedException, IOException {
        //Log.dMainActivity.TAG, "command description: " + cmd.getDesc());
        cmd.setInputStream(inputStream);
        cmd.setOutputStream(outputStream);
        cmd.start();
        int counter = 0;
        while (BluetoothApp.isConnectionEstablished) {
            cmd.join(20);
            if (!cmd.isAlive()) {
                break;
            }
            if (counter > 400) {
                cancel();
            }
            counter++;
        }

        String response = cmd.getResult().replace("SEARCHING...", "");
        response = response.replace("\n", "");
        response = response.replace("\r", "");
        //Log.dMainActivity.TAG,
//                "protocol selection command response= " + response + " 000000");
        return response;
    }

    public synchronized void runCommand(ObdCommand cmd)
            throws InterruptedException, IOException {

        //Log.dMainActivity.TAG, "command description: " + cmd.getDesc());
        cmd.setInputStream(inputStream);
        cmd.setOutputStream(outputStream);
        cmd.start();
        int counter = 0;
        while (BluetoothApp.isConnectionEstablished) {
            ////Log.i("Command sent", "command sent , we still waiting");
            cmd.join(20);
            if (!cmd.isAlive()) {
                break;
            }
            if (counter > 400) {
                cancel();
            }
            counter++;
        }

        //Log.dMainActivity.TAG,
//                "reponse from command: " + cmd.formatResult());
        String response = cmd.formatResult();


        if (response.equals("NO DATA")) {
            response = response.replace(" ", "");
        }
        if (cmd.getCmd().contains("0C") && (response.contains("DATA") || response.contains("UNABLE"))) {
            isRpmReturnsNoData = true;
        }
        if (cmd.getCmd().contains("0D") && (response.contains("DATA") || response.contains("UNABLE"))) {
            isSpeedReturnsNoData = true;
        }
        if (cmd.getCmd().contains("05") && (response.contains("DATA") || response.contains("UNABLE"))) {
            isCoolantTempReturnsNoData = true;
        }

        if (cmd.getCmd().equals("ATRV")) {
            response = "41 RV " + response;
        }

        if (response.startsWith("43")) {
            //Log.dMainActivity.TAG, "trouble code before formatting: " + response);
            response = response.replace("43", " ").replace(" ", "");
            response = "43" + response;
            //Log.dMainActivity.TAG,
//                    "trouble code in commsthread= " + response);
        }

        if (isRpmReturnsNoData && isSpeedReturnsNoData
                && isCoolantTempReturnsNoData) {
            isRpmReturnsNoData = false;
            isSpeedReturnsNoData = false;
            isCoolantTempReturnsNoData = false;
            // CommandsResponseParser.recoveryModeOn=true;
            BluetoothApp.isOBD2LockingPreventionRequired = true;
        }

        CommandsResponseParser.parseResponse(new ArrayList<String>(Arrays
                .asList(response.split(" "))));
    }

    public synchronized String runMultiplePIDsCommand(MultiplePidObdCommand cmd)
            throws InterruptedException, IOException {
        //Log.dMainActivity.TAG, "command description: " + cmd.getDesc());
        cmd.setInputStream(inputStream);
        cmd.setOutputStream(outputStream);
        cmd.start();
        int counter = 0;
        while (BluetoothApp.isConnectionEstablished) {
            cmd.join(20);
            if (!cmd.isAlive()) {
                break;
            }
            if (counter > 400) {
                cancel();
            }
            counter++;
        }

        String response = cmd.formatMultipleResults();
        if (response.startsWith("43")) {
            response = response.replace(" ", "");
            //Log.dMainActivity.TAG, "trouble code command with response:" + response);
        }

        if (response.startsWith("47")) {
            response = response.replace(" ", "");
            //Log.dMainActivity.TAG, "trouble code command with response:" + response);
        }

        if (cmd.getCmd().equals("ATRV")) {
            response = "RV " + cmd.formatMultipleResults();
        }

        return response;
    }

    public void run() {
        try {
            CommandsResponseParser.isJustConnected = true;
            CommandsResponseParser.recoveryModeOn = true;
            isNormalProtocolUsed = false;
            isMultipePidsProtocolUsed = false;
            //Log.dMainActivity.TAG, "Start executing OBD2 commands");
            mainLoop:
            while (BluetoothApp.isConnectionEstablished) {
                //Log.dMainActivity.TAG, "Main Loop is running");
                if (recoveryModeCounter >= 3) {
                    // CommandsResponseParser.recoveryModeOn=true;
                    BluetoothApp.isOBD2LockingPreventionRequired = true;
                    recoveryModeCounter = 0;
                    // cancel();
                }

                // if(gpsTracker.isGPSEnabled() &&
                // !gpsTracker.isGetLocationStarted){
                // gpsTracker.isGetLocationStarted=true;
                // gpsTracker.getLocation();
                // }

                if (BluetoothApp.isOBD2LockingPreventionRequired) {
                    //Log.dMainActivity.TAG, "isOBD2Locking prevention is true");
                    initializeReadings();
                    //Log.dMainActivity.TAG,
//                            "Will wait now until car moves");
                    sleep(20 * 1000);
                    // isOBD2LockingPreventionRequired=false;
                    runCommand(new ObdCommand("ATSP0", "", ""));
                    CommandsResponseParser.recoveryModeOn = true;
                    //Log.dMainActivity.TAG, "logic resumed, let's initiate the communication logic again");
                    continue;
                }

                if (CommandsResponseParser.recoveryModeOn) {
                    //Log.dMainActivity.TAG, "recovery mode on");
                    //Log.dMainActivity.TAG,
//                            "Recovery mode is on, let's recover the communicataion with OBD2");
                    CommandsResponseParser.recoveryModeOn = false;
                    executeStaticCommands();

                    // getAndInitializeSupportedPIDs();
                }

                // maybe when the app tried to get supported pids, the bluetooth
                // device may
                // return unable to connect for any reason, so we need to loop
                // an run recovery mode again
                // if(recoveryModeOn){
                // //Log.i("CommsThread::run::2","Recovery mode is on, let's recover the communicataion with OBD2");
                // continue;
                // }


                if (isNormalProtocolUsed || isMultipePidsProtocolUsed) {
                    //Log.dMainActivity.TAG, "checking if normal or multiple protocols is true");

                    if (!isVinCalled) {
                        //Log.dMainActivity.TAG, "isVin called is false");
                        isVinCalled = true;
                        String vehicleId = runVINCommand(new VinObdCommand("0902", "VIN", "code"));
                        //TODO Simulating
//                        vehicleId = "1GNFK13598R223864";
                        if (vehicleId.equals("NA")
                                || vehicleId.equals("NODATA")
                                || vehicleId.equals("NO DATA") || vehicleId.contains("UNABLE")) {
                            //Log.dMainActivity.TAG, "NO VINNNNNNNNNNN");
                            LivePhoneReadings.setBluetoothId(bluetoothSocket
                                    .getRemoteDevice().getAddress());
                            // CommandsResponseParser.VehicleId =
                            // LivePhoneReadings.phoneId+"-"+LivePhoneReadings.getBluetoothId();
                            LivePhoneReadings.bluetoothId = LivePhoneReadings
                                    .getBluetoothId();

                            DataBaseHelper dbHelper = new DataBaseHelper(BluetoothApp.context);
                            VehicleProfileModel vehicleProfileModel = dbHelper.getVehicleProfileDataWithObd2AndPhoneIdsAndVinRetrievableIsFalse(LivePhoneReadings.getBluetoothId(), LivePhoneReadings.getPhoneId());

                            ApplicationPrefs.getInstance(BluetoothApp.context).setVehicleProfilePrefs(vehicleProfileModel);


//							if(ApplicationPrefs.getInstance(BluetoothApp.context).getLastConnectedVinId()>=0){
//								//Log.i("I am here", "in last vin");
//								DataBaseHelper dbHelper = new DataBaseHelper(
//										BluetoothApp.context);
//								VehicleProfileModel vehicleProfileModel = dbHelper
//										.getVehicleProfileDataWithVinId(ApplicationPrefs.getInstance(BluetoothApp.context).getLastConnectedVinId());
//								if (!vehicleProfileModel.getBtID().equals("")) {
//									ApplicationPrefs.getInstance(
//											BluetoothApp.context)
//											.setVehicleProfilePrefs(
//													vehiclePro fileModel);
//
//								}
//								ApplicationPrefs.getInstance(BluetoothApp.context).setVehicleHealthValuePref(vehicleProfileModel.getVehicleHealth());
//								ApplicationPrefs.getInstance(BluetoothApp.context).setVehicleHealthMessagePref(vehicleProfileModel.getReason());
//							}else {
//
//								DataBaseHelper dbHelper = new DataBaseHelper(
//										BluetoothApp.context);
//								VehicleProfileModel vehicleProfileModel = dbHelper
//										.getprofiledataUsingBluetoothId(LivePhoneReadings.bluetoothId);
//								if (!vehicleProfileModel.getBtID().equals("")) {
//									ApplicationPrefs.getInstance(
//											BluetoothApp.context)
//											.setVehicleProfilePrefs(
//													vehicleProfileModel);
//
//								}
//								ApplicationPrefs.getInstance(BluetoothApp.context).setVehicleHealthValuePref(vehicleProfileModel.getVehicleHealth());
//								ApplicationPrefs.getInstance(BluetoothApp.context).setVehicleHealthMessagePref(vehicleProfileModel.getReason());
//							}


                        } else {
                            // CommandsResponseParser.VehicleId=vehicleId;
                            //if(!LivePhoneReadings.VIN.equals(vehicleId)){
                            ApplicationPrefs.getInstance(BluetoothApp.context).setLastUpdatePref(0);
                            LivePhoneReadings.setVin(vehicleId);
                            //new BackgroundDailyServerCallsThread(BluetoothApp.context).start();
                            //Log.dMainActivity.TAG, "1");

                            DataBaseHelper db = new DataBaseHelper(BluetoothApp.context);
                            VehicleProfileModel vehicleProfileModel = db.getVehicleProfileDataWithVin(vehicleId);
                            if (vehicleProfileModel.getMake().length() > 0 && vehicleProfileModel.getModel().length() > 0 && vehicleProfileModel.getYear().length() > 0) {
                                ApplicationPrefs.getInstance(BluetoothApp.context).setVehicleProfilePrefs(vehicleProfileModel);
                                //Log.dMainActivity.TAG, "2");
                            } else {
                                vehicleProfileModel.setVIN(vehicleId);
                                vehicleProfileModel.setBtID(LivePhoneReadings.bluetoothId);
                                vehicleProfileModel.setAndroidPhoneId(LivePhoneReadings.phoneId);
                                vehicleProfileModel.setMileage("");
                                vehicleProfileModel.setMake("");
                                vehicleProfileModel.setYear("");
                                vehicleProfileModel.setModel("");
                                vehicleProfileModel.setVinRetrievable(true);
                                db.addProfile(vehicleProfileModel);

                                //Log.dMainActivity.TAG, "3");
                                new GetProfileTask(BluetoothApp.context).execute(vehicleId);
                            }
                            ApplicationPrefs.getInstance(BluetoothApp.context).setVehicleHealthValuePref(vehicleProfileModel.getVehicleHealth());
                            ApplicationPrefs.getInstance(BluetoothApp.context).setVehicleHealthMessagePref(vehicleProfileModel.getReason());


                        }


                        results_Decode.clear();


                        CommandsResponseParser.results_value.put("0902", ""
                                + LivePhoneReadings.vin);
                        if (LivePhoneReadings.getVinId() > 0) {
                            new GcmRegistration(BluetoothApp.context);
                        }
                        Thread.sleep(100);
                    }
                }

                if (isNormalProtocolUsed) {
                    //Log.dMainActivity.TAG, "running on normal mode");
                    DecodeCommands = CommandsResource.getAllCommands();
                    int i = 0;
                    for (int decode = 0; decode < DecodeCommands.size(); decode++) {
                        //Log.dMainActivity.TAG, "Looping on commands");
                        runCommand(DecodeCommands.get(decode));
                        i++;
                        if (i == 1) {
                            i = 0;
                            // Fire Main 4 Commands
                            for (int j = 0; j < CommandsResource
                                    .getDecodeCommands().size(); j++) {
                                runCommand(CommandsResource.getDecodeCommands()
                                        .get(j));
                                Thread.sleep(200);
                            }
                            Thread.sleep(200);
                        }
                        if (CommandsResponseParser.recoveryModeOn) {
                            recoveryModeCounter++;
                            //Log.dMainActivity.TAG,
//                                    "Recovery mode is on, let's recover the communicataion with OBD2");
                            continue mainLoop;
                        }
                    }
                    //Log.dMainActivity.TAG,
//                            "executing OBD2 commands completed");

                    Thread.sleep(1000);

                    if (!CommandsResponseParser.recoveryModeOn) {
                        recoveryModeCounter = 0;
                        ReadingsManager.setReadingsValuesAndSaveToDB(
                                CommandsResponseParser.results_value,
                                BluetoothApp.context);
                    }
                    CommandsResponseParser.noDataCounter = 0;

                    Thread.sleep(1000);
                } else if (isMultipePidsProtocolUsed) {
                    //Log.dMainActivity.TAG, "running on multiple commands mode");
                    DecodeCommands = CommandsResource.getAllCommands();
                    results_Decode.clear();

                    for (int x = 0; x < CommandsResource.getMultiPIDCommands()
                            .size(); x++) {
                        Thread.sleep(1000);
                        executeMultiplePIDsCommand(CommandsResource
                                .getMultiPIDCommands().get(x));
                    }
                    runCommand(new ObdCommand("03", "Trouble Codes", ""));

                    if (CommandsResponseParser.recoveryModeOn) {
                        recoveryModeCounter++;
                        continue mainLoop;
                    }

                    if (!CommandsResponseParser.recoveryModeOn) {
                        recoveryModeCounter = 0;
                        ReadingsManager.setReadingsValuesAndSaveToDB(
                                CommandsResponseParser.results_value,
                                BluetoothApp.context);
                    }
                    CommandsResponseParser.noDataCounter = 0;

                } else if (!isNormalProtocolUsed && !isMultipePidsProtocolUsed) {
                    String multiCommandResponse = runProtocolSelectionCommand(CommandsResource
                            .getMultiPIDCommands().get(0));
                    //Log.dMainActivity.TAG, ""
//                            + multiCommandResponse);

                    if (multiCommandResponse.contains("0C")
                            && multiCommandResponse.contains("0D")
                            && multiCommandResponse.contains("05")) {
                        //Log.dMainActivity.TAG,
//                                "Multiple PIDs Protocol Selected");
                        isMultipePidsProtocolUsed = true;
                    } else if (multiCommandResponse.toLowerCase(
                            Locale.getDefault()).contains("unable")) {
                        //Log.dMainActivity.TAG,
//                                "Unable to connect");
                        CommandsResponseParser.recoveryModeOn = true;
                        recoveryModeCounter++;
                        isNormalProtocolUsed = true;
                    } else {
                        //Log.dMainActivity.TAG,
//                                "Normal Protocol Selected");
                        isNormalProtocolUsed = true;
                    }
                }

            }
            isVinCalled = false;
        } catch (IOException exp) {
            //Log.dMainActivity.TAG, exp.getMessage());
            //Log.dMainActivity.TAG,
//                    "will cancel the connection now after this exception.");
            isVinCalled = false;
            cancel();
        } catch (InterruptedException ecp) {
            //Log.dMainActivity.TAG, ecp.getMessage());
            //Log.dMainActivity.TAG,
//                    "will cancel the connection now after this exception.");
            isVinCalled = false;
            cancel();
        } catch (Exception e) {
            e.printStackTrace();
            isVinCalled = false;
            cancel();
        }

    }

    // private void checkIfRecoveryNeeded(){
    // //This logic is added to fix the issue when the is not able to get
    // readings or unable to connect for any reason, protocol , reading protocol
    // will
    // // //get restarted to be able to read.
    // if(CommandsResponseParser.rpmValue.contains("DATA") &&
    // CommandsResponseParser.coolantTempValue.contains("DATA") &&
    // CommandsResponseParser.vehicleSpeedValue.contains("DATA") &&
    // CommandsResponseParser.engineLoadValue.contains("DATA") &&
    // CommandsResponseParser.throttleValue.contains("DATA")){
    // //Log.i("NODATA issue occured","NODATA issue occured, let's fix it");
    // CommandsResponseParser.recoveryModeOn=true;
    // //executeStaticCommands();
    // }

    // if(rpmValue.contains("UNABLETOCONNECT") ||
    // coolantTempValue.contains("UNABLETOCONNECT") ||
    // vehicleSpeedValue.contains("UNABLETOCONNECT")){
    // // engineLoadValue.contains("UNABLETOCONNECT") ||
    // throttleValue.contains("UNABLETOCONNECT")){
    // rpmValue="";
    // coolantTempValue="";
    // vehicleSpeedValue="";
    // engineLoadValue="";
    // throttleValue="";
    // //Log.i("UNABLETOCONNECT issue occured","UNABLETOCONNECT issue occured, let's fix it");
    // sleep(3000);
    // executeStaticCommands();
    // }
    // }

    private void executeStaticCommands() {
        try {
            for (ObdCommand command : CommandsResource.getStaticCommands()) {
                runCommand(command);
                // if(result_Decode==null ||
                // result_Decode.equalsIgnoreCase("")){
                // result_Decode="NO DATA";
                // results_Decode.add(command.getDesc() + " : "+ result_Decode);
                // }
                // else{
                // if(command.getDesc().equals("EngineLight")){
                // EngineLight=result_Decode;
                // //Log.e("Intial Commands: "+command.getCmd(),
                // "EngineLight: "+result_Decode);
                // }
                // results_Decode.add(command.getDesc() + " : "+ result_Decode);
                // }
                // //Log.i("Intial Commands: "+command.getDesc(),
                // ""+result_Decode);
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    // private void getAndInitializeSupportedPIDs(){
    // availablePIDs.clear();
    // getAndInitializeMode1SupportedPIDs();
    // if(recoveryModeOn){
    // return;
    // }
    // getAndInitializeMode5SupportedPIDs();
    // if(recoveryModeOn){
    // return;
    // }
    // getAndInitializeMode9SupportedPIDs();
    // if(recoveryModeOn){
    // return;
    // }
    //
    // //Adding fault code manually as true for now
    // availablePIDs.put("03", true);
    // availablePIDs.put("04", true);
    // availablePIDs.put("07", true);
    // availablePIDs.put("0202", true);
    // availablePIDs.put("050100", true);
    // availablePIDs.put("0100", true);
    // availablePIDs.put("0900", true);
    //
    // }

    // private void getAndInitializeMode1SupportedPIDs(){
    // try {
    // String availablePIDsHexString = runCommand(new
    // ModeSupportedPIDsCommand("0100", "Protocol search order)", "C"));
    // //Log.i("CommsThread::getAndInitializeMode1SupportedPIDs::0100","returned string is: "+availablePIDsHexString);
    // if(availablePIDsHexString.contains("UNABLE")){
    // //Log.i("CommsThread::getAndInitializeMode1SupportedPIDs::0100","Recovery mode is on, let's recover the communicataion with OBD2");
    // recoveryModeOn=true;
    // return;
    // }
    // String big ="";
    // if(availablePIDsHexString.contains("NODATA")){
    // big = "00000000000000000000000000000000";
    // }else{
    // big = new BigInteger(availablePIDsHexString, 16).toString(2);
    // }
    // int subStringIndex = 0;
    // availablePIDs.put("0101", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0102", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0103", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0104", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0105", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0106", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0107", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0108", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0109", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("010A", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("010B", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("010C", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("010D", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("010E", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("010F", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0110", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0111", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0112", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0113", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0114", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0115", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0116", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0117", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0118", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0119", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("011A", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("011B", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("011C", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("011D", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("011E", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("011F", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0120", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    //
    // availablePIDsHexString = runCommand(new ModeSupportedPIDsCommand("0120",
    // "Protocol search order)", "C"));
    // if(availablePIDsHexString.contains("UNABLE")){
    // //Log.i("CommsThread::getAndInitializeMode1SupportedPIDs::0120","Recovery mode is on, let's recover the communicataion with OBD2");
    // recoveryModeOn=true;
    // return;
    // }
    //
    // if(availablePIDsHexString.contains("NODATA")){
    // big = "00000000000000000000000000000000";
    // }else{
    // big = new BigInteger(availablePIDsHexString, 16).toString(2);
    // }
    // subStringIndex = 0;
    // availablePIDs.put("0121", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0122", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0123", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0124", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0125", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0126", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0127", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0128", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0129", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("012A", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("012B", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("012C", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("012D", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("012E", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("012F", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0130", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0131", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0132", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0133", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0134", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0135", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0136", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0137", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0138", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0139", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("013A", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("013B", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("013C", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("013D", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("013E", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("013F", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0140", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    //
    //
    //
    // availablePIDsHexString = runCommand(new ModeSupportedPIDsCommand("0140",
    // "Protocol search order)", "C"));
    // if(availablePIDsHexString.contains("UNABLE")){
    // //Log.i("CommsThread::getAndInitializeMode1SupportedPIDs::0140","Recovery mode is on, let's recover the communicataion with OBD2");
    // recoveryModeOn=true;
    // return;
    // }
    //
    // if(availablePIDsHexString.contains("NODATA")){
    // big = "00000000000000000000000000000000";
    // }else{
    // big = new BigInteger(availablePIDsHexString, 16).toString(2);
    // }
    // subStringIndex = 0;
    // availablePIDs.put("0141", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0142", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0143", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0144", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0145", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0146", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0147", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0148", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0149", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("014A", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("014B", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("014C", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("014D", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("014E", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("014F", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0150", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0151", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0152", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0153", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0154", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0155", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0156", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0157", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0158", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0159", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("015A", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("015B", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("015C", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("015D", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("015E", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("015F", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0160", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    //
    //
    // // if(availablePIDs.get("0160")){
    // // availablePIDsHexString = runCommand(new
    // ModeSupportedPIDsCommand("0160", "Protocol search order)", "C"));
    // // if(availablePIDsHexString.contains("DATA")||
    // availablePIDsHexString.contains("UNABLE") ){
    // // big = new BigInteger(availablePIDsHexString, 16).toString(2);
    // // subStringIndex = 0;
    // // availablePIDs.put("0161", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("0162", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("0163", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("0164", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("0165", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("0166", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("0167", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("0168", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("0169", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("016A", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("016B", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("016C", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("016D", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("016E", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("016F", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("0170", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("0171", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("0172", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("0173", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("0174", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("0175", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("0176", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("0177", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("0178", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("0179", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("017A", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("017B", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("017C", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("017D", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("017E", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("017F", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("0180", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // }
    // // }
    // // if(availablePIDs.get("0180")){
    // // availablePIDsHexString = runCommand(new
    // ModeSupportedPIDsCommand("0180", "Protocol search order)", "C"));
    // // if(!availablePIDsHexString.contains("DATA")&&
    // !availablePIDsHexString.contains("UNABLE") ){
    // // big = new BigInteger(availablePIDsHexString, 16).toString(2);
    // // subStringIndex = 0;
    // // availablePIDs.put("0181", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("0182", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("0183", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("0184", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("0185", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("0186", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // }
    // // }
    //
    // } catch (InterruptedException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }
    //
    //
    // private void getAndInitializeMode5SupportedPIDs(){
    // try {
    // String availablePIDsHexString = runCommand(new
    // ModeSupportedPIDsCommand("050100", "Protocol search order)", "C"));
    // if(availablePIDsHexString.contains("UNABLE")){
    // //Log.i("CommsThread::getAndInitializeMode1SupportedPIDs::050100","Recovery mode is on, let's recover the communicataion with OBD2");
    // recoveryModeOn=true;
    // return;
    // }
    // String big ="";
    // if(availablePIDsHexString.contains("NODATA")){
    // big = "00000000000000000000000000000000";
    // }else{
    // big = new BigInteger(availablePIDsHexString, 16).toString(2);
    // //Log.i("returned from mode 5 supported pids",big);
    // }
    // //Log.i("returned from mode 5 supported pids",big);
    // int subStringIndex = 0;
    // availablePIDs.put("050101", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("050102", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("050103", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("050104", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("050105", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("050106", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("050107", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("050108", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("050109", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("05010A", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("05010B", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("05010C", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("05010D", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("05010E", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("05010F", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("050110", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("050201", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("050202", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("050203", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("050204", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("050205", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("050206", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("050207", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("050208", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("050209", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("05020A", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("05020B", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("05020C", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("05020D", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("05020E", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("05020F", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("050210", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    //
    // } catch (InterruptedException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }
    //
    // private void getAndInitializeMode9SupportedPIDs(){
    // try {
    // String availablePIDsHexString = runCommand(new
    // ModeSupportedPIDsCommand("0900", "Protocol search order)", "C"));
    // if(availablePIDsHexString.contains("UNABLE")){
    // //Log.i("CommsThread::getAndInitializeMode1SupportedPIDs::0900","Recovery mode is on, let's recover the communicataion with OBD2");
    // recoveryModeOn=true;
    // return;
    // }
    // String big ="";
    // if(availablePIDsHexString.contains("NODATA")){
    // big = "00000000000000000000000000000000";
    // }else{
    // big = new BigInteger(availablePIDsHexString, 16).toString(2);
    // }
    // int subStringIndex = 0;
    // availablePIDs.put("0901", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0902", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0903", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0904", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0905", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0906", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0907", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0908", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("0909", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("090A", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("090B", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("090C", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("090D", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("090E", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // availablePIDs.put("090F", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("0110", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("0111", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("0112", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("0113", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("0114", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("0115", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("0116", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("0117", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("0118", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("0119", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("011A", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("011B", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("011C", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("011D", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("011E", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("011F", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    // // availablePIDs.put("0120", big.substring(subStringIndex++,
    // subStringIndex).equals("1")? true:false);
    //
    // } catch (InterruptedException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // }
    //
    private synchronized void executeMultiplePIDsCommand(
            MultiplePidObdCommand command) throws InterruptedException,
            IOException {
        CommandsResponseParser
                .parseMultiPidsCommandResponse(runMultiplePIDsCommand(command));

    }

    // Decode obd commands
    // private synchronized Object ExecuteCommands(ObdCommand command, int pos)
    // {
    // try {
    //
    // //Log.i("executeCommands","will execute command: "+command.getCmd());
    // // if(availablePIDs.get(command.getCmd())){
    // result_Decode = runCommand(command).replace(" ", "");
    // //Log.i("executeCommands","returned value is: "+result_Decode);
    // if(result_Decode==null || result_Decode.equalsIgnoreCase("")){
    // result_Decode="NO DATA";
    // results_Decode.add(DecodeCommands.get(pos).getDesc() + " : "+
    // result_Decode);
    // results_value.put(DecodeCommands.get(pos).getCmd(), result_Decode);
    // }
    // else{
    // results_value.put(DecodeCommands.get(pos).getCmd(), result_Decode);
    // }
    //
    // // if(command.getDesc().equals("Engine RPM"))
    // // rpmValue=command.getDesc() + " : "+ result_Decode;
    // if(command.getDesc().equals("Coolant Temp"))
    // coolantTempValue=command.getDesc() + " : "+ result_Decode;
    // else if(command.getDesc().equals("Vehicle Speed"))
    // vehicleSpeedValue=command.getDesc() + " : "+ result_Decode;
    // else if(command.getDesc().equals("Fuel Level Input")){
    // fuelLevelValue=command.getDesc() + " : "+ result_Decode;
    // fuelLevel=command.getDesc();
    // }
    // else if(command.getDesc().equals("VIN")){
    // if(result_Decode.equals("NA") || result_Decode.equals("NODATA") ||
    // result_Decode.equals("NO DATA")){
    // LivePhoneReadings.setBluetoothId(bluetoothSocket.getRemoteDevice().getAddress());
    // VehicleId =
    // LivePhoneReadings.phoneId+"-"+LivePhoneReadings.getBluetoothId();
    // VIN= "VIN: "+VehicleId;
    //
    // }else{
    // VehicleId=result_Decode;
    // VIN=command.getDesc() + " : "+ result_Decode;
    // }
    // results_value.put(DecodeCommands.get(pos).getCmd(), VehicleId);
    // }
    // else if(command.getDesc().equals("Engine Load"))
    // engineLoadValue=command.getDesc() + " : "+ result_Decode;
    // else if(command.getDesc().equals("Throttle Position"))
    // throttleValue=command.getDesc() + " : "+ result_Decode;
    // else if(command.getDesc().equals("Dist_Traveled"))
    // traveledValue=command.getDesc() + " : "+ result_Decode;
    // else if(command.getDesc().equals("Fuel status"))
    // fuelStatus=command.getDesc() + " : "+ result_Decode;
    // else if(command.getDesc().equals("Trouble Codes")){
    // if(result_Decode.startsWith("4300") ||
    // result_Decode.startsWith("43004300")){
    // result_Decode="No Fault Found: "+result_Decode;
    // }
    // else{
    // result_Decode="Fault Found: "+result_Decode;
    // }
    // troubleCodes=command.getDesc() + " : "+ result_Decode;
    // }
    // else if(command.getDesc().equals("Clear_trouble_code"))
    // pendingFaultCodes=command.getDesc() + " : "+ result_Decode;
    // else if(command.getDesc().equals("Barometric Press"))
    // baromatricValue=command.getDesc() + " : "+ result_Decode;
    // else if(command.getDesc().equals("Air Flow Rate"))
    // airFlowValue=command.getDesc() + " : "+ result_Decode;
    // else if(command.getDesc().equals("Absolute load value"))
    // absoluteValue=command.getDesc() + " : "+ result_Decode;
    // else if(command.getDesc().equals("EngineLight"))
    // EngineLight=result_Decode;
    // else if(command.getDesc().equals("Control module voltage"))
    // controlModuleVoltage = result_Decode;
    // //}else{
    // // if(command.getDesc().equals("VIN")){
    // // results_value.put(DecodeCommands.get(pos).getCmd(),
    // LivePhoneReadings.phoneId+"-"+bluetoothSocket.getRemoteDevice().getAddress());
    // // }else{
    // //
    // //Log.i("Command is not available","Command: "+command.getCmd()+" is not available");
    // // results_value.put(DecodeCommands.get(pos).getCmd(), "NA");
    // // }
    // // break;
    // //}
    //
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // return result_Decode;
    // }

    // Decode main 4 commands
    // private synchronized Object ExecuteCommands1(ObdCommand command, int pos)
    // {
    // try {
    // while (BluetoothApp.isConnectionEstablished) {
    // runCommand(command);
    // if(result_Decode==null || result_Decode.equalsIgnoreCase("")){
    // result_Decode="NO DATA";
    // results_Decode.add(CommandsResource.getDecodeCommands().get(pos).getDesc()
    // + " : "+ result_Decode);
    // break;
    // }
    // else{
    // // if(!availablePIDs.get(command.getCmd())){
    // // result_Decode ="NA";
    // // }
    // results_Decode.add(CommandsResource.getDecodeCommands().get(pos).getDesc()
    // + " : "+ result_Decode);
    // if(CommandsResource.getDecodeCommands().get(pos).getDesc().equals("Engine RPM"))
    // rpmValue=CommandsResource.getDecodeCommands().get(pos).getDesc() + " : "+
    // result_Decode;
    // else
    // if(CommandsResource.getDecodeCommands().get(pos).getDesc().equals("Coolant Temp"))
    // coolantTempValue=CommandsResource.getDecodeCommands().get(pos).getDesc()
    // + " : "+ result_Decode;
    // else
    // if(CommandsResource.getDecodeCommands().get(pos).getDesc().equals("Vehicle Speed"))
    // vehicleSpeedValue=CommandsResource.getDecodeCommands().get(pos).getDesc()
    // + " : "+ result_Decode;
    // else
    // if(CommandsResource.getDecodeCommands().get(pos).getDesc().equals("Fuel Level Input")){
    // fuelLevelValue=CommandsResource.getDecodeCommands().get(pos).getDesc() +
    // " : "+ result_Decode;
    // fuelLevel=CommandsResource.getDecodeCommands().get(pos).getDesc();
    // }
    // else
    // if(CommandsResource.getDecodeCommands().get(pos).getDesc().equals("VIN")){
    // if(result_Decode.equals("NA") || result_Decode.equals("NODATA") ||
    // result_Decode.equals("NO DATA")){
    // LivePhoneReadings.setBluetoothId(bluetoothSocket.getRemoteDevice().getAddress());
    // VehicleId =
    // LivePhoneReadings.phoneId+"-"+LivePhoneReadings.getBluetoothId();
    // VIN= "VIN: "+VehicleId;
    // }else{
    // VehicleId=result_Decode;
    // VIN=CommandsResource.getDecodeCommands().get(pos).getDesc() + " : "+
    // result_Decode;
    // }
    // }
    // else
    // if(CommandsResource.getDecodeCommands().get(pos).getDesc().equals("Engine Load"))
    // engineLoadValue=CommandsResource.getDecodeCommands().get(pos).getDesc() +
    // " : "+ result_Decode;
    // else
    // if(CommandsResource.getDecodeCommands().get(pos).getDesc().equals("Throttle Position"))
    // throttleValue=CommandsResource.getDecodeCommands().get(pos).getDesc() +
    // " : "+ result_Decode;
    // else
    // if(CommandsResource.getDecodeCommands().get(pos).getDesc().equals("Dist_Traveled"))
    // traveledValue=CommandsResource.getDecodeCommands().get(pos).getDesc() +
    // " : "+ result_Decode;
    // else
    // if(CommandsResource.getDecodeCommands().get(pos).getDesc().equals("Fuel status"))
    // fuelStatus=CommandsResource.getDecodeCommands().get(pos).getDesc() +
    // " : "+ result_Decode;
    // else
    // if(CommandsResource.getDecodeCommands().get(pos).getDesc().equals("Trouble Codes")){
    // if(result_Decode.startsWith("4300") ||
    // result_Decode.startsWith("43004300")){
    // result_Decode="No Fault Found: "+result_Decode;
    // }
    // else{
    // result_Decode="Fault Found: "+result_Decode;
    // }
    // troubleCodes=CommandsResource.getDecodeCommands().get(pos).getDesc() +
    // " : "+ result_Decode;
    // }
    // else
    // if(CommandsResource.getDecodeCommands().get(pos).getDesc().equals("Clear_trouble_code"))
    // pendingFaultCodes=CommandsResource.getDecodeCommands().get(pos).getDesc()
    // + " : "+ result_Decode;
    // else
    // if(CommandsResource.getDecodeCommands().get(pos).getDesc().equals("Barometric Press"))
    // baromatricValue=CommandsResource.getDecodeCommands().get(pos).getDesc() +
    // " : "+ result_Decode;
    // else
    // if(CommandsResource.getDecodeCommands().get(pos).getDesc().equals("Air Flow Rate"))
    // airFlowValue=CommandsResource.getDecodeCommands().get(pos).getDesc() +
    // " : "+ result_Decode;
    // else
    // if(CommandsResource.getDecodeCommands().get(pos).getDesc().equals("Absolute load value"))
    // absoluteValue=CommandsResource.getDecodeCommands().get(pos).getDesc() +
    // " : "+ result_Decode;
    // else
    // if(CommandsResource.getDecodeCommands().get(pos).getDesc().equals("EngineLight"))
    // EngineLight=result_Decode;
    // else
    // if(CommandsResource.getDecodeCommands().get(pos).getDesc().equals("Control module voltage"))
    // controlModuleVoltage = result_Decode;
    //
    //
    //
    // if(recoveryModeOn){
    // recoveryModeOn = false;
    // //Log.i("recovery mode needed",
    // "recovery mode needed, let's sleep for 3 seconds and restart obd2 protocol");
    // sleep(3000);
    // executeStaticCommands();
    // }
    //
    // break;
    // }
    //
    // }
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // return result_Decode;
    // }

    public HashMap<String, Boolean> decodeAvailablePIDsString(String hexString) {

        return availablePIDs;
    }

    // ---call this from the main Activity to
    // send data to the remote device---
    public void write(String str) {
        try {
            outputStream.write(str.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ---call this from the main Activity to
    // shutdown the connection---
    public synchronized void cancel() {
        try {
            synchronized (this) {
                bluetoothSocket.close();
            }
            // isConnected = false;
            BluetoothApp.isConnectionEstablished = false;
            isVinCalled = false;
            BluetoothApp.isOBD2LockingPreventionRequired = true;
            LivePhoneReadings.setBluetoothId("");
            LivePhoneReadings.setMake("");
            LivePhoneReadings.setYear("");
            LivePhoneReadings.setModel("");
            LivePhoneReadings.setVin("");
            VehicleProfileModel vehicleProfileModel = new VehicleProfileModel();
            vehicleProfileModel.setMake("");
            vehicleProfileModel.setYear("");
            vehicleProfileModel.setVIN("");
            vehicleProfileModel.setVINID("");
            vehicleProfileModel.setModel("");
            vehicleProfileModel.setAndroidPhoneId("");
            vehicleProfileModel.setBtID("");
            vehicleProfileModel.setDevice_Id("");
            vehicleProfileModel.setVinRetrievable(false);
            vehicleProfileModel.setLastupdate("");
            vehicleProfileModel.setLicense_Num("");
            vehicleProfileModel.setLicense_State("");
            vehicleProfileModel.setMFYear("");
            vehicleProfileModel.setReason("No trouble codes");
            vehicleProfileModel.setVehicleHealth(70);
            ApplicationPrefs.getInstance(BluetoothApp.context).setVehicleProfilePrefs(vehicleProfileModel);
            vehicleProfileModel = null;

            // MainActivity.gps.stopUsingGPS();
            // if(gpsTracker!=null){
            // gpsTracker.stopUsingGPS();
            // gpsTracker = null;
            // }

            BluetoothApp.cancelNotification(BluetoothApp.context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
