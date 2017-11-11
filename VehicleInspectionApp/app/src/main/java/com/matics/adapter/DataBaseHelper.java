package com.matics.adapter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.plus.Plus;
import com.matics.LivePhoneReadings;
import com.matics.LoginActivity;
import com.matics.MainActivity;
import com.matics.R;
import com.matics.model.AccelerationModel;
import com.matics.model.VehicleMaintenanceModel;
import com.matics.model.VehicleProfileModel;
import com.matics.Utils.ApplicationPrefs;
import com.matics.Utils.ReadingsManager;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper {

    // The Android's default system path of your application database.
//	private static String DB_PATH = "";

    private static String DB_NAME = "dbVehicleHealth";
    private SQLiteDatabase myDataBase;
    private final Context myContext;
    private static String VEHICLE_PROFILE_TABLE = "vehicleprofiletable";
    private static String ACCELERATION_TABLE = "acceleration";
    private static String HEALTH_TABLE = "healthtable";
    private static String READINGS_TABLE = "readings";
    private static String MAINTENANCE_HISTORY_TABLE = "MAINTENANCE_HISTORY";

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");


    private final String HEALTH_TABLE_CREATION = "CREATE TABLE healthtable (VIN	TEXT,health_array TEXT)";
    private final String ACCELERATION_TABLE_CREATION = "CREATE TABLE ACCELERATION (USER_ID NUMERIC, VIN TEXT, ACCELERATION_X NUMERIC, ACCELERATION_Y NUMERIC, ACCELERATION_Z, NUMERIC, LATITUDE NUMERIC, LONGITUDE NUMERIC, TIME_STAMP TEXT)";
    private final String PROFILE_TABLE_CREATION = "CREATE TABLE vehicleprofiletable (BtID	 TEXT,License_State TEXT,	License_Num TEXT,VinRetrievable Numeric,Device_Id	TEXT,VIN TEXT,last_update	TEXT,year TEXT,make TEXT,model TEXT,mileage TEXT, vinid text, reason TEXT, VehicleHealth Numberic)";
    private final String MAINTENANCE_HISTORY_TABLE_CREATION = "CREATE TABLE MAINTENANCE_HISTORY (VIN TEXT, FACILITY TEXT, INVOICE_ID TEXT, SERVICE_DATE TEXT)";


    private static final int DATABASE_VERSION = 12;

    /**
     * Constructor Takes and keeps a reference of the passed context in order to
     * access to the application assets and resources.
     *
     * @param context
     */
    public DataBaseHelper(Context context) {

        super(context, DB_NAME, null, DATABASE_VERSION);
        this.myContext = context;

//		DB_PATH = "/data/data/" + myContext.getPackageName() + "/databases/";
        //DB_PATH = Environment.getExternalStorageDirectory().getPath()+"/Matics/";
    }
    /**
     * Creates a empty database on the system and rewrites it with your own
     * database.
     * */
//	public void createDataBase() throws IOException {
//		boolean dbExist = checkDataBase();
//		if (dbExist) {
//			// do nothing - database already exist
//			//Log.i("log_tag", "Db is already ecxist");
//		} else {
//			// By calling this method and empty database will be created into
    // the default system path
    // of your application so we are gonna be able to overwrite that
    // database with our database.
//			this.getReadableDatabase();
//			this.getWritableDatabase();

//			try {
//				copyDataBase();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}

    /**
     * Check if the database already exist to avoid re-copying the file each
     * time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase() {

        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            // database does't exist yet.
            e.printStackTrace();
        }

        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created
     * empty database in the system folder, from where it can be accessed and
     * handled. This is done by transfering bytestream.
     */
    private void copyDataBase() throws IOException {

        // Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_NAME;

        // Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

        openDataBase();

    }

    public void openDataBase() throws SQLException {

        // Open the database
        String myPath = DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if (myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Log.dMainActivity.TAG, "On Creaaaaaaaate databaaaaase*********");
        ReadingsManager.initializeReadings(myContext);

        StringBuilder readingsTableCreation = new StringBuilder();
        readingsTableCreation.append("CREATE TABLE " + READINGS_TABLE + " (");
        for (int x = 0; x < ReadingsManager.commandsDescriptionList.size(); x++) {
            if (x != ReadingsManager.commandsDescriptionList.size() - 1) {
                readingsTableCreation.append(ReadingsManager.commandsDescriptionList.get(x) + " text,");
            } else {
                readingsTableCreation.append(ReadingsManager.commandsDescriptionList.get(x) + " text");
            }
        }

        readingsTableCreation.append(");");
        //Log.dMainActivity.TAG, readingsTableCreation.toString());
        db.execSQL(readingsTableCreation.toString());

        db.execSQL(PROFILE_TABLE_CREATION);
        db.execSQL(ACCELERATION_TABLE_CREATION);
        db.execSQL(HEALTH_TABLE_CREATION);
        db.execSQL(MAINTENANCE_HISTORY_TABLE_CREATION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (DATABASE_VERSION > 8) {
            ApplicationPrefs.getInstance(myContext).setLoggedInPrefs(false);

            ApplicationPrefs.getInstance(myContext).setAccountDetailPref(null);
            ApplicationPrefs.getInstance(myContext).setVehicleHealthMessagePref("No TroubleCodes");
            ApplicationPrefs.getInstance(myContext).setVehicleHealthValuePref(100);

            ApplicationPrefs.getInstance(myContext).setBooleanPref(myContext.getString(R.string.is_user_logged_in_with_email), false);


            if (ApplicationPrefs.getInstance(myContext).getBooleanPref(myContext.getString(R.string.is_user_logged_in_with_google_plus))) {
                if (LoginActivity.Companion.getMGoogleApiClient().isConnected()) {
                    Plus.AccountApi.clearDefaultAccount(LoginActivity.Companion.getMGoogleApiClient());
                    Plus.AccountApi.revokeAccessAndDisconnect(LoginActivity.Companion.getMGoogleApiClient());
                    LoginActivity.Companion.getMGoogleApiClient().disconnect();
                }

                ApplicationPrefs.getInstance(myContext).setBooleanPref(myContext.getString(R.string.is_user_logged_in_with_google_plus), false);
            }

            if (ApplicationPrefs.getInstance(myContext).getBooleanPref(myContext.getString(R.string.is_user_logged_in_with_facebook))) {
                FacebookSdk.sdkInitialize(myContext);
                LoginManager.getInstance().logOut();

                ApplicationPrefs.getInstance(myContext).setBooleanPref(myContext.getString(R.string.is_user_logged_in_with_facebook), false);
            }

            //Log.dMainActivity.TAG, "upgrading databaaaaase*********");
            db.execSQL("DROP TABLE IF EXISTS " + VEHICLE_PROFILE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + ACCELERATION_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + HEALTH_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + MAINTENANCE_HISTORY_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + READINGS_TABLE);
            onCreate(db);

            Intent intent = new Intent(myContext, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            myContext.startActivity(intent);
        }

    }

    //(USER_ID NUMERIC, ACCELERATION_X NUMERIC, ACCELERATION_Y NUMERIC, ACCELERATION_Z, NUMERIC, LATITUDE NUMERIC, LONGITUDE NUMERIC, TIME_STAMP DATE)";
    public void addAcceleration(AccelerationModel accelerationModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = null;
        values = new ContentValues();
        values.put("USER_ID", accelerationModel.getMobileUserProfileID());
        values.put("VIN", accelerationModel.getVin());
        values.put("ACCELERATION_X", accelerationModel.getAccelerationX());
        values.put("ACCELERATION_Y", accelerationModel.getAccelerationY());
        values.put("ACCELERATION_Z", accelerationModel.getAccelerationZ());
        values.put("LATITUDE", accelerationModel.getLatitude());
        values.put("LONGITUDE", accelerationModel.getLongitude());
        values.put("TIME_STAMP", simpleDateFormat.format(new Date()));
        //Log.dMainActivity.TAG, "" + simpleDateFormat.format(new Date()));
        db.insert(ACCELERATION_TABLE, null, values);
        db.close(); // Closing database connection
    }

    public ArrayList<AccelerationModel> getAccelerations() {
        ArrayList<AccelerationModel> accelerationModelArrayList = new ArrayList<AccelerationModel>();
        String selectQuery = "SELECT  * FROM " + ACCELERATION_TABLE + " order by time_stamp desc";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            AccelerationModel accelerationModel;
            while (cursor.moveToNext()) {
                accelerationModel = new AccelerationModel();
                accelerationModel.setMobileUserProfileID(cursor.getInt(cursor.getColumnIndex("USER_ID")));
                accelerationModel.setVin(cursor.getString(cursor.getColumnIndex("VIN")));
                accelerationModel.setAccelerationX(cursor.getFloat(cursor.getColumnIndex("ACCELERATION_X")));
                accelerationModel.setAccelerationY(cursor.getFloat(cursor.getColumnIndex("ACCELERATION_Y")));
                accelerationModel.setAccelerationZ(cursor.getFloat(cursor.getColumnIndex("ACCELERATION_Z")));
                accelerationModel.setLatitude(cursor.getString(cursor.getColumnIndex("LATITUDE")));
                accelerationModel.setLongitude(cursor.getString(cursor.getColumnIndex("LONGITUDE")));
                accelerationModel.setSendDate(cursor.getString(cursor.getColumnIndex("TIME_STAMP")));
                accelerationModelArrayList.add(accelerationModel);
                //Log.dMainActivity.TAG, "user_id: " + accelerationModel.getMobileUserProfileID());
                //Log.dMainActivity.TAG, "accelerationX: " + accelerationModel.getAccelerationX());
                //Log.dMainActivity.TAG, "accelerationY:" + accelerationModel.getAccelerationY());
                //Log.dMainActivity.TAG, "accelerationZ:" + accelerationModel.getAccelerationZ());
                //Log.dMainActivity.TAG, "latitude:" + accelerationModel.getLatitude());
                //Log.dMainActivity.TAG, "longitude:" + accelerationModel.getLongitude());
                //Log.dMainActivity.TAG, "time stamp:" + accelerationModel.getSendDate());
                break;
            }
            accelerationModel = null;
        }

        return accelerationModelArrayList;
    }


    //----------------------Add Profile------------------------

    public void addProfile(VehicleProfileModel profile) {

        //Log.dMainActivity.TAG, "with vehicle VIN = " + profile.getVIN());
        //Log.dMainActivity.TAG, "with vehicle BtID = " + profile.getBtID());
        //Log.dMainActivity.TAG, "with vehicle BtID = " + profile.getYear());
        //Log.dMainActivity.TAG, "with vehicle Health = " + profile.getVehicleHealth());
        //Log.dMainActivity.TAG, "with vehicle Reason = " + profile.getReason());


        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = null;
        values = new ContentValues();
        values.put("Device_Id", LivePhoneReadings.getPhoneId());
        values.put("VIN", profile.getVIN());
        values.put("last_update", profile.getLastupdate());
        values.put("year", profile.getYear());
        values.put("make", profile.getMake());
        values.put("model", profile.getModel());
        values.put("mileage", profile.getMileage());
        values.put("VinRetrievable", profile.getVinRetrievable());
        values.put("License_Num", profile.getLicense_Num());
        values.put("License_State", profile.getLicense_State());
        values.put("BtID", profile.getBtID());
        values.put("vinid", profile.getVINID());
        values.put("Reason", profile.getReason());
        values.put("VehicleHealth", profile.getVehicleHealth());

        db.insert(VEHICLE_PROFILE_TABLE, null, values);
        db.close(); // Closing database connection
    }


    public void deleteVehicle(String vinID) {
        //String deleteMaintenanceHistoryValuesForVin = "DELETE FROM " + MAINTENANCE_HISTORY_TABLE + " where VINID='"+ VIN +"'";
        String deleteVehicleProfileForVin = "DELETE FROM " + VEHICLE_PROFILE_TABLE + " where VINID='" + vinID + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        //db.execSQL(deleteMaintenanceHistoryValuesForVin, null);
        db.execSQL(deleteVehicleProfileForVin);
        //Log.dMainActivity.TAG, "delete VIN done");
        db.close();
    }
    //-----------------------get Profile------------------


//	void deletetable1(){
//		SQLiteDatabase db = this.getWritableDatabase();
//		db.execSQL("delete from "+ TABLE_CONTACTS);
//	}

//	void deleteiddata(String ID){
//		SQLiteDatabase db = this.getWritableDatabase();
//		String args = String.valueOf(ID);
//		String deleteQuery = "DELETE FROM "+TABLE2+" where Account_id='"+ args +"'";
//		//Log.e("", "deletequery is :" + deleteQuery);
//		db.execSQL(deleteQuery);
//	}


    public void addMaintenanceHistory(VehicleMaintenanceModel vehicleMaintenanceModel) {
//        //Log.i("Maintenance history", "Vin: " + vehicleMaintenanceModel.getVin());
        //Log.dMainActivity.TAG, "Facility: " + vehicleMaintenanceModel.getFacility());
        //Log.dMainActivity.TAG, "Invoice_ID:" + vehicleMaintenanceModel.getInvoiceID());
        //Log.dMainActivity.TAG, "Service_Date:" + vehicleMaintenanceModel.getServiceDate());
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
        }catch(NullPointerException exp){
            return;
        }
        ContentValues values = null;
        values = new ContentValues();
//        values.put("VIN", vehicleMaintenanceModel.getVin());
        values.put("FACILITY", vehicleMaintenanceModel.getFacility());
        values.put("INVOICE_ID", vehicleMaintenanceModel.getInvoiceID());
        values.put("SERVICE_DATE", vehicleMaintenanceModel.getServiceDate());
        db.insert(MAINTENANCE_HISTORY_TABLE, null, values);
        db.close(); // Closing database connection
    }

    public ArrayList<VehicleMaintenanceModel> getVehicleMaintenanceHistoryForVin(String vin) {
        ArrayList<VehicleMaintenanceModel> vehicleMaintenanceModelArrayList = new ArrayList<VehicleMaintenanceModel>();
        String selectQuery = "SELECT  * FROM " + MAINTENANCE_HISTORY_TABLE + " where VIN='" + vin + "' order by service_date desc";
        ////Log.i("select query to get all profile data",""+selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.getCount() > 0) {
            VehicleMaintenanceModel vehicleMaintenanceModel;
            while (cursor.moveToNext()) {
                vehicleMaintenanceModel = new VehicleMaintenanceModel();
//                vehicleMaintenanceModel.setVin(vin);
                vehicleMaintenanceModel.setFacility(cursor.getString(cursor.getColumnIndex("FACILITY")));
                vehicleMaintenanceModel.setInvoiceID(cursor.getString(cursor.getColumnIndex("INVOICE_ID")));
                vehicleMaintenanceModel.setServiceDate(cursor.getString(cursor.getColumnIndex("SERVICE_DATE")));
                vehicleMaintenanceModelArrayList.add(vehicleMaintenanceModel);
//                //Log.i("Maintenance history", "Vin: " + vehicleMaintenanceModel.getVin());
                //Log.dMainActivity.TAG, "Facility: " + vehicleMaintenanceModel.getFacility());
                //Log.dMainActivity.TAG, "Invoice_ID:" + vehicleMaintenanceModel.getInvoiceID());
                //Log.dMainActivity.TAG, "Service_Date:" + vehicleMaintenanceModel.getServiceDate());
            }
            vehicleMaintenanceModel = null;
        }

        return vehicleMaintenanceModelArrayList;
    }

    public ArrayList<VehicleProfileModel> getVehicleProfilesWhichDoesntRetrieveVin() {
        String selectQuery = "SELECT  * FROM " + VEHICLE_PROFILE_TABLE + " where VinRetrievable=0";
        ////Log.i("select query to get all profile data",""+selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<VehicleProfileModel> vehicleProfileModels = new ArrayList<>();
        VehicleProfileModel profile;
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                profile = new VehicleProfileModel();
                if (cursor.getInt(3) == 0) {
                    profile.setVinRetrievable(false);
                } else {
                    profile.setVinRetrievable(true);
                }

                profile.setDevice_Id(cursor.getString(4));
                profile.setVIN(cursor.getString(5));
                profile.setLastupdate(cursor.getString(6));
                profile.setYear(cursor.getString(7));
                if (cursor.getString(8) == null || cursor.getString(8).equalsIgnoreCase("") || cursor.getString(8).equalsIgnoreCase(null) || cursor.getString(8).equalsIgnoreCase("null")) {
                    profile.setMake("");
                } else {
                    profile.setMake(cursor.getString(8));
                }
                if (cursor.getString(9) == null || cursor.getString(9).equalsIgnoreCase("") || cursor.getString(9).equalsIgnoreCase(null) || cursor.getString(9).equalsIgnoreCase("null")) {
                    profile.setModel("");
                } else {
                    profile.setModel(cursor.getString(9));
                }
                if (cursor.getString(10) == null || cursor.getString(10).equalsIgnoreCase("") || cursor.getString(10).equalsIgnoreCase(null) || cursor.getString(10).equalsIgnoreCase("null")) {
                    profile.setMileage("");
                } else {
                    profile.setMileage(cursor.getString(10));
                }
                if (cursor.getString(2) == null || cursor.getString(2).equalsIgnoreCase("") || cursor.getString(2).equalsIgnoreCase(null) || cursor.getString(2).equalsIgnoreCase("null")) {
                    profile.setLicense_Num("");
                } else {
                    profile.setLicense_Num(cursor.getString(2));
                }
                if (cursor.getString(1) == null || cursor.getString(1).equalsIgnoreCase("") || cursor.getString(1).equalsIgnoreCase(null) || cursor.getString(1).equalsIgnoreCase("null")) {
                    profile.setLicense_State("");
                } else {
                    profile.setLicense_State(cursor.getString(1));
                }
                if (cursor.getString(0) == null || cursor.getString(0).equalsIgnoreCase("") || cursor.getString(0).equalsIgnoreCase(null) || cursor.getString(0).equalsIgnoreCase("null")) {
                    profile.setBtID("");
                } else {
                    profile.setBtID(cursor.getString(0));
                }

                if (cursor.getString(12) == null || cursor.getString(12).equalsIgnoreCase("") || cursor.getString(12).equalsIgnoreCase(null) || cursor.getString(12).equalsIgnoreCase("null")) {
                    profile.setReason("No Trouble Codes reported. There is no record of this vehicle having service. It is simple for any repair facility to join. All they have to do is goto VehicleHealth.org");
                } else {
                    profile.setReason(cursor.getString(12));
                }

                if (cursor.getString(13) == null || cursor.getString(13).equalsIgnoreCase("") || cursor.getString(13).equalsIgnoreCase(null) || cursor.getString(13).equalsIgnoreCase("null")) {
                    profile.setVehicleHealth(70);
                } else {
                    profile.setVehicleHealth(cursor.getInt(13));
                }

                profile.setVINID(cursor.getString(11));
                vehicleProfileModels.add(profile);
            }
        }
        cursor.close();

        return vehicleProfileModels;
    }

    public VehicleProfileModel getVehicleProfileDataWithObd2AndPhoneIds(String obd2Id, String phoneId) {

        String selectQuery = "SELECT  * FROM " + VEHICLE_PROFILE_TABLE + " where btid='" + obd2Id + "' and Device_Id = '"+ phoneId+"' ";
        ////Log.i("select query to get all profile data",""+selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        VehicleProfileModel profile = new VehicleProfileModel();
        ;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            if (cursor.getInt(3) == 0) {
                profile.setVinRetrievable(false);
            } else {
                profile.setVinRetrievable(true);
            }

            profile.setDevice_Id(cursor.getString(4));
            profile.setVIN(cursor.getString(5));
            profile.setLastupdate(cursor.getString(6));
            profile.setYear(cursor.getString(7));
            if (cursor.getString(8) == null || cursor.getString(8).equalsIgnoreCase("") || cursor.getString(8).equalsIgnoreCase(null) || cursor.getString(8).equalsIgnoreCase("null")) {
                profile.setMake("");
            } else {
                profile.setMake(cursor.getString(8));
            }
            if (cursor.getString(9) == null || cursor.getString(9).equalsIgnoreCase("") || cursor.getString(9).equalsIgnoreCase(null) || cursor.getString(9).equalsIgnoreCase("null")) {
                profile.setModel("");
            } else {
                profile.setModel(cursor.getString(9));
            }
            if (cursor.getString(10) == null || cursor.getString(10).equalsIgnoreCase("") || cursor.getString(10).equalsIgnoreCase(null) || cursor.getString(10).equalsIgnoreCase("null")) {
                profile.setMileage("");
            } else {
                profile.setMileage(cursor.getString(10));
            }
            if (cursor.getString(2) == null || cursor.getString(2).equalsIgnoreCase("") || cursor.getString(2).equalsIgnoreCase(null) || cursor.getString(2).equalsIgnoreCase("null")) {
                profile.setLicense_Num("");
            } else {
                profile.setLicense_Num(cursor.getString(2));
            }
            if (cursor.getString(1) == null || cursor.getString(1).equalsIgnoreCase("") || cursor.getString(1).equalsIgnoreCase(null) || cursor.getString(1).equalsIgnoreCase("null")) {
                profile.setLicense_State("");
            } else {
                profile.setLicense_State(cursor.getString(1));
            }
            if (cursor.getString(0) == null || cursor.getString(0).equalsIgnoreCase("") || cursor.getString(0).equalsIgnoreCase(null) || cursor.getString(0).equalsIgnoreCase("null")) {
                profile.setBtID("");
            } else {
                profile.setBtID(cursor.getString(0));
            }

            if (cursor.getString(12) == null || cursor.getString(12).equalsIgnoreCase("") || cursor.getString(12).equalsIgnoreCase(null) || cursor.getString(12).equalsIgnoreCase("null")) {
                profile.setReason("No Trouble Codes reported. There is no record of this vehicle having service. It is simple for any repair facility to join. All they have to do is goto VehicleHealth.org");
            } else {
                profile.setReason(cursor.getString(12));
            }

            if (cursor.getString(13) == null || cursor.getString(13).equalsIgnoreCase("") || cursor.getString(13).equalsIgnoreCase(null) || cursor.getString(13).equalsIgnoreCase("null")) {
                profile.setVehicleHealth(70);
            } else {
                profile.setVehicleHealth(cursor.getInt(13));
            }

            profile.setVINID(cursor.getString(11));

        }

        cursor.close();
        return profile;
    }

    public VehicleProfileModel getVehicleProfileDataWithObd2AndPhoneIdsAndVinRetrievableIsFalse(String obd2Id, String phoneId) {

        String selectQuery = "SELECT  * FROM " + VEHICLE_PROFILE_TABLE + " where btid='" + obd2Id + "' and Device_Id = '"+ phoneId+"' and VinRetrievable = 0";
        ////Log.i("select query to get all profile data",""+selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        VehicleProfileModel profile = new VehicleProfileModel();
        ;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            if (cursor.getInt(3) == 0) {
                profile.setVinRetrievable(false);
            } else {
                profile.setVinRetrievable(true);
            }

            profile.setDevice_Id(cursor.getString(4));
            profile.setVIN(cursor.getString(5));
            profile.setLastupdate(cursor.getString(6));
            profile.setYear(cursor.getString(7));
            if (cursor.getString(8) == null || cursor.getString(8).equalsIgnoreCase("") || cursor.getString(8).equalsIgnoreCase(null) || cursor.getString(8).equalsIgnoreCase("null")) {
                profile.setMake("");
            } else {
                profile.setMake(cursor.getString(8));
            }
            if (cursor.getString(9) == null || cursor.getString(9).equalsIgnoreCase("") || cursor.getString(9).equalsIgnoreCase(null) || cursor.getString(9).equalsIgnoreCase("null")) {
                profile.setModel("");
            } else {
                profile.setModel(cursor.getString(9));
            }
            if (cursor.getString(10) == null || cursor.getString(10).equalsIgnoreCase("") || cursor.getString(10).equalsIgnoreCase(null) || cursor.getString(10).equalsIgnoreCase("null")) {
                profile.setMileage("");
            } else {
                profile.setMileage(cursor.getString(10));
            }
            if (cursor.getString(2) == null || cursor.getString(2).equalsIgnoreCase("") || cursor.getString(2).equalsIgnoreCase(null) || cursor.getString(2).equalsIgnoreCase("null")) {
                profile.setLicense_Num("");
            } else {
                profile.setLicense_Num(cursor.getString(2));
            }
            if (cursor.getString(1) == null || cursor.getString(1).equalsIgnoreCase("") || cursor.getString(1).equalsIgnoreCase(null) || cursor.getString(1).equalsIgnoreCase("null")) {
                profile.setLicense_State("");
            } else {
                profile.setLicense_State(cursor.getString(1));
            }
            if (cursor.getString(0) == null || cursor.getString(0).equalsIgnoreCase("") || cursor.getString(0).equalsIgnoreCase(null) || cursor.getString(0).equalsIgnoreCase("null")) {
                profile.setBtID("");
            } else {
                profile.setBtID(cursor.getString(0));
            }

            if (cursor.getString(12) == null || cursor.getString(12).equalsIgnoreCase("") || cursor.getString(12).equalsIgnoreCase(null) || cursor.getString(12).equalsIgnoreCase("null")) {
                profile.setReason("No Trouble Codes reported. There is no record of this vehicle having service. It is simple for any repair facility to join. All they have to do is goto VehicleHealth.org");
            } else {
                profile.setReason(cursor.getString(12));
            }

            if (cursor.getString(13) == null || cursor.getString(13).equalsIgnoreCase("") || cursor.getString(13).equalsIgnoreCase(null) || cursor.getString(13).equalsIgnoreCase("null")) {
                profile.setVehicleHealth(70);
            } else {
                profile.setVehicleHealth(cursor.getInt(13));
            }

            profile.setVINID(cursor.getString(11));

        }

        cursor.close();
        return profile;
    }


    public VehicleProfileModel getVehicleProfileDataWithVin(String VIN) {

        String selectQuery = "SELECT  * FROM " + VEHICLE_PROFILE_TABLE + " where VIN='" + VIN + "'";
        ////Log.i("select query to get all profile data",""+selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        VehicleProfileModel profile = new VehicleProfileModel();
        ;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            if (cursor.getInt(3) == 0) {
                profile.setVinRetrievable(false);
            } else {
                profile.setVinRetrievable(true);
            }

            profile.setDevice_Id(cursor.getString(4));
            profile.setVIN(cursor.getString(5));
            profile.setLastupdate(cursor.getString(6));
            profile.setYear(cursor.getString(7));
            if (cursor.getString(8) == null || cursor.getString(8).equalsIgnoreCase("") || cursor.getString(8).equalsIgnoreCase(null) || cursor.getString(8).equalsIgnoreCase("null")) {
                profile.setMake("");
            } else {
                profile.setMake(cursor.getString(8));
            }
            if (cursor.getString(9) == null || cursor.getString(9).equalsIgnoreCase("") || cursor.getString(9).equalsIgnoreCase(null) || cursor.getString(9).equalsIgnoreCase("null")) {
                profile.setModel("");
            } else {
                profile.setModel(cursor.getString(9));
            }
            if (cursor.getString(10) == null || cursor.getString(10).equalsIgnoreCase("") || cursor.getString(10).equalsIgnoreCase(null) || cursor.getString(10).equalsIgnoreCase("null")) {
                profile.setMileage("");
            } else {
                profile.setMileage(cursor.getString(10));
            }
            if (cursor.getString(2) == null || cursor.getString(2).equalsIgnoreCase("") || cursor.getString(2).equalsIgnoreCase(null) || cursor.getString(2).equalsIgnoreCase("null")) {
                profile.setLicense_Num("");
            } else {
                profile.setLicense_Num(cursor.getString(2));
            }
            if (cursor.getString(1) == null || cursor.getString(1).equalsIgnoreCase("") || cursor.getString(1).equalsIgnoreCase(null) || cursor.getString(1).equalsIgnoreCase("null")) {
                profile.setLicense_State("");
            } else {
                profile.setLicense_State(cursor.getString(1));
            }
            if (cursor.getString(0) == null || cursor.getString(0).equalsIgnoreCase("") || cursor.getString(0).equalsIgnoreCase(null) || cursor.getString(0).equalsIgnoreCase("null")) {
                profile.setBtID("");
            } else {
                profile.setBtID(cursor.getString(0));
            }

            if (cursor.getString(12) == null || cursor.getString(12).equalsIgnoreCase("") || cursor.getString(12).equalsIgnoreCase(null) || cursor.getString(12).equalsIgnoreCase("null")) {
                profile.setReason("No Trouble Codes reported. There is no record of this vehicle having service. It is simple for any repair facility to join. All they have to do is goto VehicleHealth.org");
            } else {
                profile.setReason(cursor.getString(12));
            }

            if (cursor.getString(13) == null || cursor.getString(13).equalsIgnoreCase("") || cursor.getString(13).equalsIgnoreCase(null) || cursor.getString(13).equalsIgnoreCase("null")) {
                profile.setVehicleHealth(70);
            } else {
                profile.setVehicleHealth(cursor.getInt(13));
            }

            profile.setVINID(cursor.getString(11));

        }
        cursor.close();
        return profile;
    }

    public VehicleProfileModel getVehicleProfileDataWithVinId(int vinId) {

        String selectQuery = "SELECT  * FROM " + VEHICLE_PROFILE_TABLE + " where VINid='" + vinId + "'";
        ////Log.i("select query to get all profile data",""+selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        VehicleProfileModel profile = new VehicleProfileModel();
        ;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            if (cursor.getInt(3) == 0) {
                profile.setVinRetrievable(false);
            } else {
                profile.setVinRetrievable(true);
            }

            profile.setDevice_Id(cursor.getString(4));
            profile.setVIN(cursor.getString(5));
            profile.setLastupdate(cursor.getString(6));
            profile.setYear(cursor.getString(7));
            if (cursor.getString(8) == null || cursor.getString(8).equalsIgnoreCase("") || cursor.getString(8).equalsIgnoreCase(null) || cursor.getString(8).equalsIgnoreCase("null")) {
                profile.setMake("");
            } else {
                profile.setMake(cursor.getString(8));
            }
            if (cursor.getString(9) == null || cursor.getString(9).equalsIgnoreCase("") || cursor.getString(9).equalsIgnoreCase(null) || cursor.getString(9).equalsIgnoreCase("null")) {
                profile.setModel("");
            } else {
                profile.setModel(cursor.getString(9));
            }
            if (cursor.getString(10) == null || cursor.getString(10).equalsIgnoreCase("") || cursor.getString(10).equalsIgnoreCase(null) || cursor.getString(10).equalsIgnoreCase("null")) {
                profile.setMileage("");
            } else {
                profile.setMileage(cursor.getString(10));
            }
            if (cursor.getString(2) == null || cursor.getString(2).equalsIgnoreCase("") || cursor.getString(2).equalsIgnoreCase(null) || cursor.getString(2).equalsIgnoreCase("null")) {
                profile.setLicense_Num("");
            } else {
                profile.setLicense_Num(cursor.getString(2));
            }
            if (cursor.getString(1) == null || cursor.getString(1).equalsIgnoreCase("") || cursor.getString(1).equalsIgnoreCase(null) || cursor.getString(1).equalsIgnoreCase("null")) {
                profile.setLicense_State("");
            } else {
                profile.setLicense_State(cursor.getString(1));
            }
            if (cursor.getString(0) == null || cursor.getString(0).equalsIgnoreCase("") || cursor.getString(0).equalsIgnoreCase(null) || cursor.getString(0).equalsIgnoreCase("null")) {
                profile.setBtID("");
            } else {
                profile.setBtID(cursor.getString(0));
            }

            if (cursor.getString(12) == null || cursor.getString(12).equalsIgnoreCase("") || cursor.getString(12).equalsIgnoreCase(null) || cursor.getString(12).equalsIgnoreCase("null")) {
                profile.setReason("No Trouble Codes reported. There is no record of this vehicle having service. It is simple for any repair facility to join. All they have to do is goto VehicleHealth.org");
            } else {
                profile.setReason(cursor.getString(12));
            }

            if (cursor.getString(13) == null || cursor.getString(13).equalsIgnoreCase("") || cursor.getString(13).equalsIgnoreCase(null) || cursor.getString(13).equalsIgnoreCase("null")) {
                profile.setVehicleHealth(70);
            } else {
                profile.setVehicleHealth(cursor.getInt(13));
            }

            profile.setVINID(cursor.getString(11));

        }
        cursor.close();
        return profile;
    }

    public List<VehicleProfileModel> getprofiledata() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<VehicleProfileModel> data = new ArrayList<VehicleProfileModel>();
        String selectQuery = "SELECT  * FROM " + VEHICLE_PROFILE_TABLE;
        //Log.e("", "select is " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                VehicleProfileModel profile = new VehicleProfileModel();
                profile.setBtID(cursor.getString(0));
                profile.setLicense_Num(cursor.getString(2));
                profile.setLicense_State(cursor.getString(1));
                if (cursor.getInt(3) == 0) {
                    profile.setVinRetrievable(false);
                } else {
                    profile.setVinRetrievable(true);
                }
                profile.setDevice_Id(cursor.getString(4));
                profile.setVIN(cursor.getString(5));
                profile.setLastupdate(cursor.getString(6));
                profile.setYear(cursor.getString(7));
                profile.setMake(cursor.getString(8));
                profile.setModel(cursor.getString(9));
                profile.setMileage(cursor.getString(10));
                profile.setBtID(cursor.getString(0));
                profile.setVINID(cursor.getString(11));
                profile.setReason(cursor.getString(12));
                profile.setVehicleHealth(cursor.getInt(13));
                data.add(profile);
                //Log.dMainActivity.TAG, "" + profile.getBtID());
                //Log.dMainActivity.TAG, "" + profile.getYear());
                //Log.dMainActivity.TAG, "" + profile.getMake());
                //Log.dMainActivity.TAG, "" + profile.getModel());
                //Log.dMainActivity.TAG, "" + profile.getVinRetrievable());
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return data;
    }

    public VehicleProfileModel getprofiledataUsingBluetoothId(String bluetoothId) {
        SQLiteDatabase db = this.getWritableDatabase();
        VehicleProfileModel profile = new VehicleProfileModel();
        String selectQuery = "SELECT  * FROM " + VEHICLE_PROFILE_TABLE + " where BtID = '" + bluetoothId + "'";
        //Log.e("", "select is " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        //Log.dMainActivity.TAG, "" + cursor.getCount());
        // looping through all rows and adding to list
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            profile.setBtID(cursor.getString(0));
            profile.setLicense_Num(cursor.getString(2));
            profile.setLicense_State(cursor.getString(1));
            if (cursor.getInt(3) == 0) {
                profile.setVinRetrievable(false);
            } else {
                profile.setVinRetrievable(true);
            }
            profile.setDevice_Id(cursor.getString(4));
            profile.setVIN(cursor.getString(5));
            profile.setLastupdate(cursor.getString(6));
            profile.setYear(cursor.getString(7));
            profile.setMake(cursor.getString(8));
            profile.setModel(cursor.getString(9));
            profile.setMileage(cursor.getString(10));
            profile.setBtID(cursor.getString(0));
            profile.setVINID(cursor.getString(11));
            profile.setReason(cursor.getString(12));
            profile.setVehicleHealth(cursor.getInt(13));
            //Log.dMainActivity.TAG, "" + profile.getBtID());
            //Log.dMainActivity.TAG, "" + profile.getYear());
            //Log.dMainActivity.TAG, "" + profile.getMake());
            //Log.dMainActivity.TAG, "" + profile.getModel());
        }
        cursor.close();
        db.close();
        return profile;
    }

    public ArrayList<VehicleProfileModel> getprofiledataforlist() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<VehicleProfileModel> data = new ArrayList<VehicleProfileModel>();
        String selectQuery = "SELECT  * FROM " + VEHICLE_PROFILE_TABLE;
        //Log.e("", "select is " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        while (cursor.moveToNext()) {
            VehicleProfileModel profile = new VehicleProfileModel();
            profile.setBtID(cursor.getString(0));
            profile.setLicense_Num(cursor.getString(2));
            profile.setLicense_State(cursor.getString(1));
            if (cursor.getInt(3) == 0) {
                profile.setVinRetrievable(false);
            } else {
                profile.setVinRetrievable(true);
            }
            profile.setDevice_Id(cursor.getString(4));
            profile.setVIN(cursor.getString(5));
            profile.setLastupdate(cursor.getString(6));
            profile.setYear(cursor.getString(7));
            profile.setMake(cursor.getString(8));
            profile.setModel(cursor.getString(9));
            profile.setMileage(cursor.getString(10));
            profile.setBtID(cursor.getString(0));
            profile.setVINID(cursor.getString(11));
            profile.setReason(cursor.getString(12));
            profile.setVehicleHealth(cursor.getInt(13));
            data.add(profile);
        }
        cursor.close();
        db.close();
        return data;
    }

    public int updateprofile(VehicleProfileModel profile) {
        SQLiteDatabase db = this.getWritableDatabase();
//		values.put("Sys_Contact_ID", "");
        ContentValues values = null;
        values = new ContentValues();
        //Log.dMainActivity.TAG, "" + profile.getVIN());
        //Log.dMainActivity.TAG, "" + profile.getYear());
        //Log.dMainActivity.TAG, "" + profile.getMake());
        //Log.dMainActivity.TAG, "" + profile.getModel());
        //Log.dMainActivity.TAG, "" + profile.getMileage());
        //Log.dMainActivity.TAG, "" + profile.getBtID());
        values.put("VIN", profile.getVIN());
        values.put("BtID", profile.getBtID());
        values.put("last_update", profile.getLastupdate());
        values.put("year", profile.getYear());
        values.put("make", profile.getMake());
        values.put("model", profile.getModel());
        values.put("mileage", profile.getMileage());
        values.put("License_Num", profile.getLicense_Num());
        values.put("License_State", profile.getLicense_State());
        values.put("vinid", profile.getVINID());
        values.put("vehiclehealth", profile.getVehicleHealth());
        values.put("reason", profile.getReason());
        values.put("VinRetrievable", profile.getVinRetrievable());
        values.put("Device_Id", profile.getDevice_Id());
        // updating row
        int result = 5;
        //Log.dMainActivity.TAG, "VIN = " + profile.getVIN());
        //Log.dMainActivity.TAG, "BtID = " + profile.getBtID());

        if (profile.getVINID().length() > 0) {
            result = db.update(VEHICLE_PROFILE_TABLE, values, "vinid" + " = ?", new String[]{profile.getVINID()});
        } else if (profile.getVIN().length() > 0) {
            result = db.update(VEHICLE_PROFILE_TABLE, values, "VIN" + " = ?", new String[]{profile.getVIN()});
        } else {
            result = db.update(VEHICLE_PROFILE_TABLE, values, "BtID" + " = ?", new String[]{profile.getBtID()});
        }
        //Log.dMainActivity.TAG, "database updated, " + result);
        db.close();
        return result;
    }

    public void updateVehicleMileage(VehicleProfileModel vehicleProfileModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = null;
        values = new ContentValues();
        values.put("mileage", vehicleProfileModel.getMileage());


        int result = db.update(VEHICLE_PROFILE_TABLE, values, "VINID" + " = ?",
                new String[]{vehicleProfileModel.getVINID()});

        //Log.dMainActivity.TAG, "Mileage = " + vehicleProfileModel.getMileage());
        //Log.dMainActivity.TAG, "Update query status = " + result);

        db.close();
    }

    //reason TEXT, VehicleHealth TEXT)";
    public int updatehealth(String vinid, String reason, int vehicleHealth) {
        SQLiteDatabase db = this.getWritableDatabase();
//		values.put("Sys_Contact_ID", "");
        ContentValues values = null;
        values = new ContentValues();
        values.put("reason", reason);
        values.put("vehicleHealth", vehicleHealth);
        // updating row
        int result = db.update(VEHICLE_PROFILE_TABLE, values, "VINID" + " = ?",
                new String[]{vinid});
        db.close();
        return result;
    }

    public int updatelasttime(String time, String VIN) {
        SQLiteDatabase db = this.getWritableDatabase();
//		values.put("Sys_Contact_ID", "");
        ContentValues values = null;
        values = new ContentValues();
        values.put("last_update", time);
        int result = db.update(VEHICLE_PROFILE_TABLE, values, "VIN" + " = ?",
                new String[]{VIN});
        db.close();
        return result;
    }

    public void deletetable1() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + VEHICLE_PROFILE_TABLE);
        db.close();
    }

    // Adding vehicle Health Array

    public void addhealth(String VIN, ArrayList<Integer> health) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = null;
        values = new ContentValues();
        values.put("VIN", VIN);
        values.put("health_array", health.toString());
        db.insert(HEALTH_TABLE, null, values);
        db.close(); // Closing database connection
    }

    // Update Vehicle Health Array
    public int updatehealthArray(String VIN, ArrayList<Integer> health) {
        SQLiteDatabase db = this.getWritableDatabase();
//		values.put("Sys_Contact_ID", "");
        ContentValues values = new ContentValues();
        values.put("VIN", VIN);
        values.put("health_array", health.toString());
        // updating row
        int result = db.update(HEALTH_TABLE, values, "VIN" + " = ?", new String[]{LivePhoneReadings.vin, ""});
        db.close();
        return result;

    }

    public Boolean CheckVin(String VIN) {
        Boolean flag = false;
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + VEHICLE_PROFILE_TABLE + " WHERE VIN =" + "'" + VIN + "'";
        //Log.e("", "select is " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            flag = true;
        } else {
            flag = false;
        }
        cursor.close();
        db.close();
        return flag;
    }

    public Boolean CheckHealthVin(String VIN) {
        Boolean flag = false;
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + HEALTH_TABLE + " WHERE VIN =" + "'" + VIN + "'";
        //Log.e("", "select is " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            flag = true;
        } else {
            flag = false;
        }
        cursor.close();
        db.close();
        return flag;
    }

    public String Gethealtharray(String VIN) {
        String givenVIn = "";
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + HEALTH_TABLE + " WHERE VIN =" + "'" + VIN + "'";
        //Log.e("", "select is " + selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    givenVIn = cursor.getString(1);
                } while (cursor.moveToNext());
            }
        } else {
            //Log.e("", "null cursor found");
        }
        cursor.close();
        db.close();
        return givenVIn;

    }

    public void addReadings(ArrayList<String> commandsDescriptionList,
                            ArrayList<String> commandsResultValuesList) {
        //Log.dMainActivity.TAG, "Preparing to insert data in table");
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = null;
        values = new ContentValues();
        for (int m = 0; m < commandsDescriptionList.size(); m++) {
            values.put(commandsDescriptionList.get(m), commandsResultValuesList.get(m));
        }
        db.insert(READINGS_TABLE, null, values);
        db.close(); // Closing database connection

    }

    public ArrayList<String> getReadings() {
        //Log.dMainActivity.TAG, "Preparing to get readings from table");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + READINGS_TABLE, null);
        ArrayList<String> readingsStrings = new ArrayList<String>();
        while (cursor.moveToNext()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int x = 0; x < cursor.getColumnCount(); x++) {
                stringBuilder.append(cursor.getColumnName(x) + "="
                        + cursor.getString(x) + "&");
            }
            readingsStrings.add(stringBuilder.toString());
            stringBuilder = null;
        }
        cursor.close();
        db.close();
        //Log.dMainActivity.TAG, "Readings retreived, and returning cursor now");
        return readingsStrings;
    }

    public int getReadingsCount() {
        //Log.dMainActivity.TAG, "Preparing to get readings count from table");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + READINGS_TABLE, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        //Log.dMainActivity.TAG, "Readings count = " + count);
        //Log.dMainActivity.TAG, "Readings count retreived, and returning value now");

        return count;
    }

    public int getAccelerationReadingsCount() {
        //Log.dMainActivity.TAG, "Preparing to get acceleration readings count from acceleration table");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + ACCELERATION_TABLE, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        //Log.dMainActivity.TAG, "Readings count = " + count);
        //Log.dMainActivity.TAG, "Readings count retreived, and returning value now");

        return count;
    }

    public void clearReadingsTable() {
        //Log.dMainActivity.TAG, "Preparing to clear the table");
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + READINGS_TABLE);
        db.close();
        //Log.dMainActivity.TAG, "Readings table cleared");
    }

    public void clearProfileTable() {
        //Log.dMainActivity.TAG, "Vehicle to clear the table");
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + VEHICLE_PROFILE_TABLE);
        db.close();
        //Log.dMainActivity.TAG, "Vehicle table cleared");
    }

    public void clearMaintenanceHistoryTable() {
        //Log.dMainActivity.TAG, "clear Maintenance History table");
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("delete from " + MAINTENANCE_HISTORY_TABLE);
        db.close();
        //Log.dMainActivity.TAG, "maintenance table cleared");
    }

    public void clearAccelerationTable() {
        //Log.dMainActivity.TAG, "clear acceleration table");
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + ACCELERATION_TABLE);
        db.close();
        //Log.dMainActivity.TAG, "Acceleration table cleared");
    }

}



