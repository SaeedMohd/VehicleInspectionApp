package com.matics.fragments

import java.text.DecimalFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Timer

import android.app.AlertDialog
import android.app.Fragment
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Typeface
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

import com.google.gson.Gson
import com.matics.Bluetooth.BluetoothApp
import com.matics.LivePhoneReadings
import com.matics.MainActivity
import com.matics.R
import com.matics.CustomViews.SpeedometerView
import com.matics.adapter.DataBaseHelper
import com.matics.command.CommandsResponseParser
import com.matics.imageloader.Utils
import com.matics.model.VehicleProfileModel
import com.matics.model.VinResolvedObject
import com.matics.Utils.ApplicationPrefs
import com.matics.Utils.Utility
import com.matics.serverTasks.GetVehicleDetailsResolvedFromVinTask
import kotlinx.android.synthetic.main.dialog_set_vin.*
import kotlinx.android.synthetic.main.fragment_vehicle_health.*

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class FragmentVehicleHealth : Fragment(), OnClickListener {


    internal lateinit var tf: Typeface
    internal var connectTimer: Timer? = null
    internal var handler: Handler? = null
    internal var vehicle_speed = 0.0
    internal var engine_rpm = 0.0
    internal var engine_temp = 0.0
    internal var throttle_position = 0.0
    private val df = DecimalFormat("###.##")
    private var vehicleHealthInfoDialog: AlertDialog.Builder? = null
    private var setVehicleDialog: AlertDialog.Builder? = null
    internal lateinit var setVinDialog: AlertDialog
    internal var m = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // ------------initializing variables
        val view = inflater.inflate(R.layout.fragment_vehicle_health,
                container, false)
        (activity as MainActivity).supportActionBar!!.setTitle("Dashboard")
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initalize(view)

        setVinButton.setOnClickListener {
            showSetVehicleDialog()
        }

//        setVinButton!!.setOnClickListener {
//            showSetVehicleDialog()
//            //showSetVinDialog();
//        }

        Utils.setShopImage(activity, imagebg)


        displayButton!!.setOnClickListener {
            if (tvData.visibility == View.VISIBLE) {
                displayButton!!.text = "Display On"
                tvData.visibility = View.INVISIBLE
            } else {
                displayButton!!.text = "Display Off"
                tvData.visibility = View.VISIBLE
            }
        }


        // imageView_connected =
        // (ImageView)view.findViewById(R.id.imageView_connected);
        // textView_vehicleInfo =
        // (TextView)view.findViewById(R.id.textView_vehicleInfo);
        val DB = DataBaseHelper(activity)
        val profiledata = DB.getprofiledata()
        val list = ArrayList<String>()
        for (i in profiledata.indices) {
            //Log.e("porofile", "Heath of vehicle is :" + profiledata[i].vehicleHealth)
            if (!Utility.validateString(profiledata[i].make)) {
                list.add("         " + profiledata[i].vin) // white
                // spacing
                // included
                // because
                // of
                // connection/disconnection
                // icon
                // before
                // this
                // name
            } else {
                list.add("         " + profiledata[i].make + "-"
                        + profiledata[i].model + "("
                        + profiledata[i].MFYear + ")") // white spacing
                // included because
                // of
                // connection/disconnection
                // icon before this
                // name
            }
        }
        val list_health = ArrayList<String>()
        // -----------getting Vehicle Health of All vehicle
        try {
            list_health.clear()
            for (i in profiledata.indices) {
                list_health.add("" + profiledata[i].vehicleHealth)
            }
        } catch (e: Exception) {
            // TODO: handle exception
            e.printStackTrace()
        }

        // ---------------get Connected VIN
        if (BluetoothApp.isConnectionEstablished) {
            for (i in list.indices) {
                if (list[i]
                        .trim { it <= ' ' }
                        .equals(
                                LivePhoneReadings.getVin(), ignoreCase = true)) {
                    currentspinner = i
                } else {
                    currentspinner = 0
                }
            }
        }
        // --------------setting Connection/Disconnection status image on top
        // right corner
        if (BluetoothApp.isConnectionEstablished) {
            // textView_vehicleInfo.setText(""+ApplicationPrefs.getInstance(getActivity()).getVehicleProfilePref().getMake()+" ,"+ApplicationPrefs.getInstance(getActivity()).getVehicleProfilePref().getModel());
            // imageView_connected.setImageResource(R.drawable.green);
        } else {
            // textView_vehicleInfo.setText("");
            // imageView_connected.setImageResource(R.drawable.red);
        }
        // ------setting Spinner of vehicle list which is coming from database

        val dataAdapter = ArrayAdapter(
                activity, android.R.layout.simple_spinner_item, list)
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        vehicle_spinner!!.adapter = dataAdapter
        vehicle_spinner!!.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View,
                                        position: Int, id: Long) {
                // TODO Auto-generated method stub
                //Log.e("", "speed is :" + list_health[position])
                val value = list_health[position]
                val value_double = java.lang.Float.parseFloat(value)
                // speedometer.setSpeed(value_double);
                val text1 = view as TextView
                text1.setTextColor(Color.WHITE)
                view.setPadding(0, 5, 0, 5)
                if (BluetoothApp.isConnectionEstablished) {
                    if (list[position]
                            .trim { it <= ' ' }
                            .equals(
                                    LivePhoneReadings.getVin(), ignoreCase = true)) {
                        view.setBackgroundResource(R.drawable.connected_strip)
                    } else {
                        view.setBackgroundResource(R.drawable.disconnected_strip)
                    }
                } else {
                    view.setBackgroundResource(R.drawable.disconnected_strip)
                }
                // view.setBackgroundColor(Color.GREEN);
                // speedometer.setSpeed(Double.parseDouble(string)profiledata.get(position).Health);
                // Toast.makeText(getActivity(),
                // profiledata.get(position).Health, Toast.LENGTH_LONG).show();
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // TODO Auto-generated method stub

            }
        }
        // startTimerForConnectedTextView();

        if (BluetoothApp.isConnectionEstablished) {
            if (BluetoothApp.isOBD2LockingPreventionRequired) {
                ivConnection!!.setImageResource(R.drawable.yellow)
            } else {
                ivConnection!!.setImageResource(R.drawable.green)
            }
        } else {
            ivConnection!!.setImageResource(R.drawable.red)
        }

        object : Thread() {
            override fun run() {
                var isbroke = false
                loop@ while (true) {
                    try {
                        // cancel service
                        if (BluetoothApp.isConnectionEstablished) {
                            // --------stop service if "Myservice" is running
                            // try {
                            // if(isMyServiceRunning(MyService.class)){
                            // getActivity().stopService(new
                            // Intent(getActivity(), MyService.class));
                            // }
                            // } catch (Exception e) {
                            // // TODO: handle exception
                            // e.printStackTrace();
                            // }
                            val str = CommandsResponseParser.rpmValue + "\n" + CommandsResponseParser.controlModuleVoltage + "\n" + CommandsResponseParser.coolantTempValue + "\n" +
                                    CommandsResponseParser.vehicleSpeedValue + "\n" + LivePhoneReadings.vin + "\n" + "Vehical Health: " + FragmentConnection.vehicalInfo + "%" + "\n" +
                                    "GPS SPEED: " + LivePhoneReadings.getGps_speed() + "\n" + "Accelerometer X: " + LivePhoneReadings.accelerometerX + "\n" + "Accelerometer Y: " +
                                    LivePhoneReadings.accelerometerY + "\n" + "Accelerometer Z: " + LivePhoneReadings.accelerometerZ + "\n"
                            handler!!.post {
                                // imageView_connected.setImageResource(R.drawable.green);
                                ivConnection!!
                                        .setImageResource(R.drawable.green)
                                // speedometer.setSpeed(Float.parseFloat(FragmentConnection.vehicalInfo));
                                if (BluetoothApp.isConnectionEstablished) {

                                    if (LivePhoneReadings.getVinRetrievable()) {
                                        setVinButton!!.visibility = View.INVISIBLE
                                    } else {
                                        setVinButton!!.visibility = View.VISIBLE
                                    }

                                    if (!LivePhoneReadings.getMake().isEmpty()) {
                                        textViewConnect.text = LivePhoneReadings.getMake() + " " + LivePhoneReadings.getModel() + " " + LivePhoneReadings.getYear()
                                        //setVinButton.setVisibility(View.INVISIBLE);
                                    } else if (LivePhoneReadings.getVin().length > 0) {
                                        textViewConnect.text = LivePhoneReadings.getVin()
                                        //setVinButton.setVisibility(View.INVISIBLE);
                                    } else {
                                        textViewConnect.text = "Unknown Vehicle"
                                        //setVinButton.setVisibility(View.VISIBLE);
                                    }

                                    if (BluetoothApp.isOBD2LockingPreventionRequired) {
                                        ivConnection!!.setImageResource(R.drawable.yellow)
                                    } else {
                                        ivConnection!!.setImageResource(R.drawable.green)
                                    }

                                } else {
                                    textViewConnect.text = "Not Connected"
                                    ivConnection!!.setImageResource(R.drawable.red)
                                    //										setVinButton.setVisibility(View.INVISIBLE);
                                }


                                if (!CommandsResponseParser.troubleCodeExist) {
                                    ivEngineLight!!.visibility = View.INVISIBLE
                                    ivEngineLight!!
                                            .setImageResource(R.drawable.deactive)
                                    speedometerHealth!!.speed = LivePhoneReadings.vehicleHealthValue.toDouble()
                                } else {
                                    ivEngineLight!!
                                            .setImageResource(R.drawable.active)
                                    ivEngineLight!!.visibility = View.VISIBLE
                                    speedometerHealth!!.speed = LivePhoneReadings.vehicleHealthValue.toDouble()
                                }
                                // //Log.e("VIN is : "+CommsThread.VehicleId.toString().trim().length(),
                                // CommsThread.VehicleId.toString().trim());
                                if (CommandsResponseParser.recoveryModeOn == true) {
                                    ivConnection!!.setImageResource(R.drawable.yellow)
                                } else {
                                    ivConnection!!.setImageResource(R.drawable.green)
                                }
                                textViewConnect.typeface = tf
                                tvData.text = str

                                try {

                                    if (CommandsResponseParser.vehicleSpeedValue.length > 0 && !CommandsResponseParser.vehicleSpeedValue.contains("DATA"))
                                        speedometerSpeed!!.speed = java.lang.Float
                                                .parseFloat(CommandsResponseParser.vehicleSpeedValue
                                                        .replace(" MPH".toRegex(), "")
                                                        .replace(
                                                                "Speed: ",
                                                                "")).toDouble()

                                    if (CommandsResponseParser.fuelLevelValue.length > 0 && !CommandsResponseParser.fuelLevelValue.contains("DATA"))
                                        speedometerFuelLevel!!.speed = java.lang.Float
                                                .parseFloat(CommandsResponseParser.fuelLevelValue).toDouble()

                                    if (CommandsResponseParser.controlModuleVoltage.length > 0 && !CommandsResponseParser.controlModuleVoltage.contains("DATA")) {
                                        if (java.lang.Float
                                                .parseFloat(CommandsResponseParser.controlModuleVoltage
                                                        .replace(
                                                                "Control Module Voltage: ",
                                                                "")
                                                        .replace(
                                                                "V", "")) > 0) {
                                            speedometerVoltage!!.speed = java.lang.Float
                                                    .parseFloat(CommandsResponseParser.controlModuleVoltage
                                                            .replace("Control Module Voltage: ".toRegex(), "")
                                                            .replace(
                                                                    "V",
                                                                    "")).toDouble()
                                        }
                                    }

                                    if (CommandsResponseParser.rpmValue.length > 0 && !CommandsResponseParser.rpmValue.contains("DATA")) {
                                        speedometerRPM!!.speed = (java.lang.Float
                                                .parseFloat(CommandsResponseParser.rpmValue
                                                        .replace(" RPM".toRegex(), "")
                                                        .replace(
                                                                "Engine: ",
                                                                "")) / 1000).toDouble()
                                    }


                                    if (CommandsResponseParser.coolantTempValue.length > 0 && !CommandsResponseParser.coolantTempValue.contains("DATA")) {
                                        val str = CommandsResponseParser.coolantTempValue
                                                .replace("F".toRegex(), "")
                                                .replace("Coolant Temp: ".toRegex(), "").trim { it <= ' ' }

                                        if (java.lang.Float.parseFloat(str) > 0) {
                                            speedometerTemp!!.speed = java.lang.Float
                                                    .parseFloat(str).toDouble()
                                        } else {
                                            speedometerTemp!!.speed = java.lang.Float
                                                    .parseFloat("0").toDouble()
                                        }
                                    }

                                    //											 if(BluetoothApp.isOBD2LockingPreventionRequired){
                                    //											Connect.setText("Connected");
                                    //											Connect.setTextColor(Color.YELLOW);
                                    //											Connect.setTypeface(tf);
                                    //											// imageView_connected.setImageResource(R.drawable.red);
                                    //											ivConnection
                                    //													.setImageResource(R.drawable.yellow);
                                    //											// ivConnection.setImageResource(R.drawable.red);
                                    //											vehicalInfo = "0";
                                    //											tvData.setText("No Data");
                                    //											// speedometer.setSpeed(Double.parseDouble("0"));
                                    //											smSpeed.setSpeed(Float.parseFloat("0"));
                                    //											smVoltage.setSpeed(Float.parseFloat("0"));
                                    //											smRPM.setSpeed(Float.parseFloat("0"));
                                    //											smTemp.setSpeed(Float.parseFloat("0"));
                                    //											// BluetoothApp.cancelNotification(getActivity());
                                    //											ivEngineLight
                                    //													.setImageResource(R.drawable.deactive);
                                    //										}
                                } catch (e: Exception) {
                                    // TODO: handle exception
                                    e.printStackTrace()
                                }
                            }
                        } else {
                                handler!!.post {
                                    try {
                                        textViewConnect.text = "Not Connected"
                                        textViewConnect.setTextColor(Color.BLACK)
                                        textViewConnect.typeface = tf
                                        // imageView_connected.setImageResource(R.drawable.red);
                                        ivConnection!!
                                                .setImageResource(R.drawable.red)
                                        // ivConnection.setImageResource(R.drawable.red);
                                        vehicalInfo = "0"
                                        tvData.text = ""
                                        // speedometer.setSpeed(Double.parseDouble("0"));
                                        speedometerSpeed!!.speed = java.lang.Float.parseFloat("0").toDouble()
                                        speedometerVoltage!!.speed = java.lang.Float.parseFloat("0").toDouble()
                                        speedometerRPM!!.speed = java.lang.Float.parseFloat("0").toDouble()
                                        speedometerTemp!!.speed = java.lang.Float.parseFloat("0").toDouble()
                                        // BluetoothApp.cancelNotification(getActivity());
                                        ivEngineLight!!.visibility = View.INVISIBLE
                                        ivEngineLight!!
                                                .setImageResource(R.drawable.deactive)
                                    }catch (e: Exception){
                                        e.printStackTrace()
                                        isbroke = true
                                    }
                                }
                            if (isbroke) {
                                break
                            }
                            // if(!isMyServiceRunning(MyService.class)){
                            // getActivity().startService(new
                            // Intent(getActivity(), MyService.class));
                            // }
                        }
                        Thread.sleep(500)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        break
                    }

                }
            }
        }.start()

    }


    private fun showSetVehicleDialog() {
        val dbHelper = DataBaseHelper(activity)
        val vehicleProfileModels = dbHelper.vehicleProfilesWhichDoesntRetrieveVin
        //Log.dMainActivity.TAG, "" + vehicleProfileModels.size)
        val vehiclesNames = arrayOfNulls<String>(vehicleProfileModels.size + 1)
        vehiclesNames[0] = "Set as new vehicle"
        for (x in vehicleProfileModels.indices) {
            vehiclesNames[x + 1] = vehicleProfileModels[x].make + " " + vehicleProfileModels[x].model + " " + vehicleProfileModels[x].year
        }
        setVehicleDialog = AlertDialog.Builder(activity)
        setVehicleDialog!!.setTitle("Set Connected Vehicle")
        setVehicleDialog!!.setItems(vehiclesNames) { dialog, which ->
            if (which == 0) {
                showSetVinDialog()
            } else {
                //Log.dMainActivity.TAG, "" + vehicleProfileModels[which - 1].make + "" + vehicleProfileModels[which - 1].model + " " + vehicleProfileModels[which - 1].year)
                ApplicationPrefs.getInstance(BluetoothApp.context).setVehicleProfilePrefs(vehicleProfileModels[which - 1])
            }
        }

        setVehicleDialog!!.show()

    }

    private fun showSetVinDialog() {
        setVinDialog = AlertDialog.Builder(activity).create()
        setVinDialog.setTitle("Set Vehicle info")
        val inflater = LayoutInflater.from(activity)
        val dialogView = inflater.inflate(R.layout.dialog_set_vin, null)
        setVinDialog.setView(dialogView)
//        yearEditText
//        val yearEditText = dialogView.findViewById(R.id.yearEditText) as EditText
//        val makeEditText = dialogView.findViewById(R.id.makeEditText) as EditText
//        val modelEditText = dialogView.findViewById(R.id.modelEditText) as EditText
//        val vinEditText = dialogView.findViewById(R.id.vinEditText) as EditText
//        val mileageEditText = dialogView.findViewById(R.id.mileageEditText) as EditText
//        val saveButton = dialogView.findViewById(R.id.setVinOkButton) as Button

        setVinOkButton.setOnClickListener {
            if (vinEditText.text.toString().trim { it <= ' ' }.length > 0) {
                //Log.dMainActivity.TAG, "starting decode VIN")
                //                    new DecodeVinTask(BluetoothApp.context).execute(vinEditText.getText().toString(), yearEditText.getText().toString(), makeEditText.getText().toString(), modelEditText.getText().toString());
                object : GetVehicleDetailsResolvedFromVinTask(context, vinEditText.text.toString()) {
                    override fun onTaskCompleted(result: String) {
                        if (result.contains("failed")) {

                        } else {
                            val tempYear = result.split("!-!")[0]
                            val tempMake = result.split("!-!")[1]
                            val tempModel = result.split("!-!")[2]
                            val tempVin = result.split("!-!")[3]
                            val vehicleProfileModel: VehicleProfileModel
                            if (ApplicationPrefs.getInstance(BluetoothApp.context).vehicleProfilePref != null) {
                                vehicleProfileModel = ApplicationPrefs.getInstance(BluetoothApp.context).vehicleProfilePref
                                vehicleProfileModel.make = tempMake
                                vehicleProfileModel.model = tempModel
                                vehicleProfileModel.year = tempYear
                                vehicleProfileModel.vinRetrievable = false
                                vehicleProfileModel.vin = tempVin
                                vehicleProfileModel.mileage = "0"
                                vehicleProfileModel.vehicleHealth = 70
                                vehicleProfileModel.reason = getString(R.string.vehicle_health_default_reason)
                                //ApplicationPrefs.getInstance(BluetoothApp.context).setVehicleProfilePrefs(vehicleProfileModel);
                                //vehicleProfileModel = null;
                            } else {
                                vehicleProfileModel = VehicleProfileModel()
                                vehicleProfileModel.vin = tempVin
                                vehicleProfileModel.btID = LivePhoneReadings.bluetoothId
                                vehicleProfileModel.make = tempMake
                                vehicleProfileModel.model = tempModel
                                vehicleProfileModel.vinRetrievable = false
                                vehicleProfileModel.year = tempYear
                                vehicleProfileModel.mileage = "0"
                                vehicleProfileModel.vehicleHealth = 70
                                vehicleProfileModel.reason = getString(R.string.vehicle_health_default_reason)
                                //                            ApplicationPrefs.getInstance(BluetoothApp.context).setVehicleProfilePrefs(vehicleProfileModel);
                                //                          DataBaseHelper dbHelper = new DataBaseHelper(BluetoothApp.context);
                                //                            dbHelper.addProfile(vehicleProfileModel);
                                //dbHelper = null;
                                //vehicleProfileModel = null;
                            }
                            AddVehicleTask().execute(vehicleProfileModel.vin, vehicleProfileModel.year, vehicleProfileModel.make, vehicleProfileModel.model, vehicleProfileModel.mileage)

                        }
                    }
                }.execute()

            } else if (yearEditText.text.toString().trim { it <= ' ' }.length > 0 && makeEditText.text.toString().trim { it <= ' ' }.length > 0 && modelEditText.text.toString().trim { it <= ' ' }.length > 0) {
                val vehicleProfileModel = VehicleProfileModel()
                vehicleProfileModel.btID = LivePhoneReadings.getBluetoothId()
                vehicleProfileModel.year = "" + yearEditText.text.toString().trim { it <= ' ' }
                vehicleProfileModel.make = "" + makeEditText.text.toString().trim { it <= ' ' }
                vehicleProfileModel.model = "" + modelEditText.text.toString().trim { it <= ' ' }
                vehicleProfileModel.vin = ""
                if (mileageEditText.text.toString().trim { it <= ' ' }.length > 0) {
                    vehicleProfileModel.mileage = mileageEditText.text.toString().trim { it <= ' ' }
                } else {
                    vehicleProfileModel.mileage = "0"
                }
                vehicleProfileModel.vehicleHealth = 70
                vehicleProfileModel.reason = getString(R.string.vehicle_health_default_reason)
                vehicleProfileModel.vinRetrievable = false
                //ApplicationPrefs.getInstance(getActivity()).setVehicleProfilePrefs(vehicleProfileModel);
                //DataBaseHelper dbHelper = new DataBaseHelper(getActivity());
                //dbHelper.addProfile(vehicleProfileModel);
                //Connect.setText(vehicleProfileModel.getMake() + " " + vehicleProfileModel.getModel() + " " + vehicleProfileModel.getYear());
                AddVehicleTask().execute(vehicleProfileModel.vin, vehicleProfileModel.year, vehicleProfileModel.make, vehicleProfileModel.model, vehicleProfileModel.mileage)

                setVinDialog.dismiss()

            } else if (yearEditText.text.toString().trim { it <= ' ' }.length <= 0 || makeEditText.text.toString().trim { it <= ' ' }.length <= 0 || modelEditText.text.toString().trim { it <= ' ' }.length <= 0) {
                Toast.makeText(activity, "If VIN is not set, please make sure to enter Year, Make, and Model to proceed", Toast.LENGTH_LONG).show()
            }
        }
        setVinDialog.show()


    }

    private fun showVehicleHealthInfoDialog() {
        vehicleHealthInfoDialog = AlertDialog.Builder(activity)
        vehicleHealthInfoDialog!!.setTitle("Vehicle Health Status")
        vehicleHealthInfoDialog!!.setMessage(LivePhoneReadings.getVehicleHealthMessage())
        vehicleHealthInfoDialog!!.setPositiveButton("OK", null)
        vehicleHealthInfoDialog!!.show()
    }

    private fun initalize(view: View?) {
        handler = Handler()
        tf = Typeface.createFromAsset(activity.assets,
                "fonts/gill_sans.ttf")


        // hidden for now
        // speedometer.setVisibility(View.GONE);
        // smTemp.setVisibility(View.GONE);
        // smVoltage.setVisibility(View.GONE);
        // smSpeed.setVisibility(View.GONE);
        // smRPM.setVisibility(View.GONE);

        speedometerFuelLevel!!.maxSpeed = 100.0
        speedometerFuelLevel!!.majorTickStep = 20.0
        speedometerFuelLevel!!.minorTicks = 1
        speedometerFuelLevel!!.addColoredRange(0.0, 100.0, Color.BLACK)
        speedometerFuelLevel!!.setSpeedUnit("%")
        // speedometer.addColoredRange(50, 75, Color.YELLOW);
        // speedometer.addColoredRange(0, 25, Color.RED);
        // speedometer.setSpeed(Double.parseDouble("100"));
        speedometerFuelLevel!!.labelConverter = SpeedometerView.LabelConverter { progress, maxProgress -> Math.round(progress).toInt().toString() }

        speedometerHealth!!.maxSpeed = 100.0
        speedometerHealth!!.majorTickStep = 20.0
        speedometerHealth!!.minorTicks = 1
        speedometerHealth!!.addColoredRange(0.0, 100.0, Color.BLACK)
        speedometerHealth!!.setSpeedUnit("%")
        speedometerHealth!!.setShowIcon(true)
        // speedometer.addColoredRange(50, 75, Color.YELLOW);
        // speedometer.addColoredRange(0, 25, Color.RED);
        // speedometer.setSpeed(Double.parseDouble("100"));
        speedometerHealth!!.labelConverter = SpeedometerView.LabelConverter { progress, maxProgress -> Math.round(progress).toInt().toString() }
        speedometerHealth!!.setOnClickListener {
            //showVehicleHealthInfoDialog();
            GetVehicleHealthTask().execute()
        }

        speedometerTemp!!.maxSpeed = 250.0
        speedometerTemp!!.majorTickStep = 50.0
        speedometerTemp!!.minorTicks = 1
        speedometerTemp!!.addColoredRange(0.0, 250.0, Color.BLACK)
        //smTemp.setSpeedUnit("F");
        // smTemp.addColoredRange(200, 250, Color.GREEN);
        // smTemp.addColoredRange(0, 200, Color.YELLOW);
        // smTemp.addColoredRange(-40, 0, Color.RED);
        speedometerTemp!!.labelConverter = SpeedometerView.LabelConverter { progress, maxProgress -> Math.round(progress).toInt().toString() }
        speedometerTemp!!.speed = java.lang.Float.parseFloat("0").toDouble()

        speedometerVoltage!!.maxSpeed = 20.0
        speedometerVoltage!!.majorTickStep = 5.0
        speedometerVoltage!!.minorTicks = 1
        speedometerVoltage!!.setDigitalTextFormat("##.##")
        //smVoltage.setSpeedUnit("Volts");
        speedometerVoltage!!.addColoredRange(0.0, 100.0, Color.BLACK)
        // smThrottle.addColoredRange(0, 100, Color.GREEN);
        // smThrottle.addColoredRange(50, 75, Color.YELLOW);
        // smThrottle.addColoredRange(0, 25, Color.RED);
        speedometerVoltage!!.labelConverter = SpeedometerView.LabelConverter { progress, maxProgress -> Math.round(progress).toInt().toString() }
        speedometerVoltage!!.speed = java.lang.Float.parseFloat("0").toDouble()

        speedometerSpeed!!.maxSpeed = 120.0
        speedometerSpeed!!.majorTickStep = 20.0
        speedometerSpeed!!.minorTicks = 1
        //smSpeed.setSpeedUnit("MPH");
        speedometerSpeed!!.addColoredRange(0.0, 300.0, Color.BLACK)
        // smSpeed.addColoredRange(200, 300, Color.RED);
        // smSpeed.addColoredRange(100, 200, Color.GREEN);
        // smSpeed.addColoredRange(0, 100, Color.YELLOW);
        speedometerSpeed!!.labelConverter = SpeedometerView.LabelConverter { progress, maxProgress -> Math.round(progress).toInt().toString() }
        speedometerSpeed!!.speed = java.lang.Float.parseFloat("0").toDouble()

        speedometerRPM!!.maxSpeed = 6.0
        speedometerRPM!!.setDigitalSpeedRatio(1000)
        speedometerRPM!!.majorTickStep = 1.0
        speedometerRPM!!.minorTicks = 1
        //smRPM.setSpeedUnit("RPM");
        speedometerRPM!!.addColoredRange(0.0, 10.0, Color.BLACK)
        // smRPM.addColoredRange(7, 10, Color.RED);
        // smRPM.addColoredRange(0, 7, Color.YELLOW);

        speedometerRPM!!.labelConverter = SpeedometerView.LabelConverter { progress, maxProgress -> Math.round(progress).toInt().toString() }
        speedometerRPM!!.speed = java.lang.Float.parseFloat("0").toDouble()
    }

    override fun onClick(v: View) {
        // TODO Auto-generated method stub

    }

    override fun onDestroy() {
        //Log.e("", "On Distroy")
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()

    }

    fun checkcurentvehicle() {
        Handler().postDelayed({ vehicle_spinner!!.setSelection(currentspinner) }, 10000)
    }

    private inner class GetVehicleHealthTask : AsyncTask<Void, Void, String>() {

        internal lateinit var progressDialog: ProgressDialog

        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog = ProgressDialog(activity)
            progressDialog.setMessage("Please Wait...")
            progressDialog.setCancelable(false)
            progressDialog.show()
        }


        override fun doInBackground(vararg params: Void): String {
            var result = ""
            if (BluetoothApp.isConnectionEstablished && !LivePhoneReadings.vin.isEmpty()) {
                result = updateVehicleHealthDataWithVinId(LivePhoneReadings.vinId)
            } else {
                if (LivePhoneReadings.getVehicleHealthMessage().isEmpty()) {
                    LivePhoneReadings.setVehicleHealthMessage("No Trouble Codes Found.")
                }
            }
            return result
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            progressDialog.dismiss()
            if (result.contains("ConnectionError")) {
                vehicleHealthInfoDialog = AlertDialog.Builder(activity)
                vehicleHealthInfoDialog!!.setTitle("Error")
                vehicleHealthInfoDialog!!.setMessage("Couldn't retrieve Vehicle Health status. Please make sure that your internet connect is active, then try again")
                vehicleHealthInfoDialog!!.setPositiveButton("OK", null)
                vehicleHealthInfoDialog!!.show()
            } else {
                showVehicleHealthInfoDialog()
            }
        }
    }

    private fun updateVehicleHealthDataWithVinId(vinid: Int): String {
        var result: String? = null
        val getServerPath = "http://jet-matics.com/JetComService/JetCom.svc/GetVehicleHealthData?"
        try {

            val values = ContentValues()
            values.put("VINID", "" + vinid)

            result = Utility.postRequest(getServerPath, values)
            val jObject = JSONObject(result!!.toString())
            val j1 = jObject.getJSONObject("GetVehicleHealthDataResult")
            val vehicleHealthDataResult = Gson().fromJson(j1.toString(), VehicleHealthDataResult::class.java)
            ApplicationPrefs.getInstance(BluetoothApp.context).vehicleHealthValuePref = vehicleHealthDataResult.vehicleHealth
            ApplicationPrefs.getInstance(BluetoothApp.context).vehicleHealthMessagePref = vehicleHealthDataResult.reason
            ApplicationPrefs.getInstance(BluetoothApp.context).lastUpdatePref = Calendar.DAY_OF_MONTH

            val dbHelper = DataBaseHelper(BluetoothApp.context)
            dbHelper.updatehealth("" + vinid, vehicleHealthDataResult.reason, vehicleHealthDataResult.vehicleHealth)
            dbHelper.close()


        } catch (e: JSONException) {
            return "ConnectionError"
        }

        return result
    }


    internal inner class VehicleHealthDataResult {
        var reason: String = ""
        var vehicleHealth: Int = 0

    }


//    internal inner class DecodeVinTask(private val mContext: Context) : AsyncTask<String, Int, Any>() {
//        private var VIn = ""
//        private val make = ""
//        private val model = ""
//        private val year = ""
//        private var IsSuccess = ""
//        var progressBar: ProgressDialog
//        var isnet1: Boolean? = null
//        var Mydb: DataBaseHelper
//
//        init {
//            isnet1 = Utility.checkInternetConnection(mContext)
//            Mydb = DataBaseHelper(mContext)
//            progressBar = ProgressDialog(activity)
//        }
//
//        override fun onPreExecute() {
//            super.onPreExecute()
//            progressBar.setMessage("Checking VIN....")
//            progressBar.show()
//        }
//
//        override fun doInBackground(vararg params: String): Any? {
//            //Log.dMainActivity.TAG, "calling execute commands")
//            ExecuteCommands(params[0], params[1], params[2], params[3])
//
//            return null
//        }
//
//        override fun onPostExecute(result: Any) {
//            super.onPostExecute(result)
//            progressBar.dismiss()
//            // DataBaseHelper DB = new DataBaseHelper(mContext);
//            if (IsSuccess == "badVin") {
//                Toast.makeText(BluetoothApp.context, "VIN is not correct. Please enter a correct VIN or enter Year, Make, and Model manually and leave VIN blank", Toast.LENGTH_LONG).show()
//            }
//
//        }
//
//        override fun onCancelled() {
//            super.onCancelled()
//        }
//
//        private fun ExecuteCommands(VIN: String?, year: String, make: String, model: String): Any? {
//            var year = year
//            var make = make
//            var model = model
//            Utility.VehiclePofileData = ArrayList<VehicleProfileModel>()
//            var result: String? = null
////            this.VIn = VIN
//            //Log.dMainActivity.TAG, "with VIN value = " + VIN!!)
//
//            if (isnet1!!) {
//
//                val getServerPath = "http://api.edmunds.com/v1/api/toolsrepository/vindecoder?"
//                try {
//                    val values = ContentValues()
//
//                    if (VIN == null || VIN.equals("null", ignoreCase = true)
//                            || VIN.equals("", ignoreCase = true)) {
//                        values.put("vin", "NODATA".toString())
//                    } else {
//                        values.put("vin", VIN.toString())
//                    }
//                    values.put("fmt", "json".toString())
//
//                    values.put("api_key", "e7yfyg8y7w9yr7rs3a2qr4ba".toString())
//                    result = Utility.postRequest(getServerPath, values)
//                    // ------------------------------------Parsing
//                    if (result!!.toString().contains("styleHolder")) {
//                        val jObject = JSONObject(result.toString())
//                        //Log.dMainActivity.TAG, "server repoinse is: " + result)
//                        val j1 = jObject.getJSONArray("styleHolder")
//                        val j2 = j1.getJSONObject(0)
//                        year = j2.getString("year")
//                        make = j2.getString("makeName")
//                        model = j2.getString("modelName")
//                        IsSuccess = "true"
//                        //Log.e("GetProfileTask", "year is :" + year)
//                        //Log.e("GetProfileTask", "make is :" + make)
//                        //Log.e("GetProfileTask", "make is :" + model)
//
//                        val vehicleProfileModel: VehicleProfileModel
//                        if (ApplicationPrefs.getInstance(BluetoothApp.context).vehicleProfilePref != null) {
//                            vehicleProfileModel = ApplicationPrefs.getInstance(BluetoothApp.context).vehicleProfilePref
//                            vehicleProfileModel.make = make
//                            vehicleProfileModel.model = model
//                            vehicleProfileModel.year = year
//                            vehicleProfileModel.vinRetrievable = false
//                            vehicleProfileModel.vin = VIN
//                            vehicleProfileModel.mileage = "0"
//                            vehicleProfileModel.vehicleHealth = 70
//                            vehicleProfileModel.reason = getString(R.string.vehicle_health_default_reason)
//                            //ApplicationPrefs.getInstance(BluetoothApp.context).setVehicleProfilePrefs(vehicleProfileModel);
//                            //vehicleProfileModel = null;
//                        } else {
//                            vehicleProfileModel = VehicleProfileModel()
//                            vehicleProfileModel.vin = VIN
//                            vehicleProfileModel.btID = LivePhoneReadings.bluetoothId
//                            vehicleProfileModel.make = make
//                            vehicleProfileModel.model = model
//                            vehicleProfileModel.vinRetrievable = false
//                            vehicleProfileModel.year = year
//                            vehicleProfileModel.mileage = "0"
//                            vehicleProfileModel.vehicleHealth = 70
//                            vehicleProfileModel.reason = getString(R.string.vehicle_health_default_reason)
//                            //                            ApplicationPrefs.getInstance(BluetoothApp.context).setVehicleProfilePrefs(vehicleProfileModel);
//                            //                          DataBaseHelper dbHelper = new DataBaseHelper(BluetoothApp.context);
//                            //                            dbHelper.addProfile(vehicleProfileModel);
//                            //dbHelper = null;
//                            //vehicleProfileModel = null;
//                        }
//                        AddVehicleTask().execute(vehicleProfileModel.vin, vehicleProfileModel.year, vehicleProfileModel.make, vehicleProfileModel.model, vehicleProfileModel.mileage)
//                        setVinDialog.dismiss()
//                    } else {
//                        IsSuccess = "badVin"
//                    }
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//
//            }
//
//            return null
//        }
//
//        fun existVIn(): Boolean? {
//            var Flag: Boolean? = true
//            try {
//
//                val data = Mydb.getprofiledata()
//                for (i in data.indices) {
//                    if (VIn.equals(data[i].VIN, ignoreCase = true)) {
//                        Flag = false
//                        break
//                    } else {
//                        Flag = true
//                    }
//                }
//                return Flag
//            } catch (e: Exception) {
//                // TODO: handle exception
//                e.printStackTrace()
//                return Flag
//            }
//
//        }
//
//    }


    private inner class AddVehicleTask : AsyncTask<String, Void, String>() {

        internal var progressDialog: ProgressDialog? = null
        internal lateinit var vinResolvedObject: VinResolvedObject
        internal lateinit var mileage: String
        internal var isVinEntered = false

        override fun onPreExecute() {
            super.onPreExecute()

            //            progressDialog = new ProgressDialog(getActivity());
            //            progressDialog.setMessage("Adding Vehicle ...");
            //            progressDialog.show();

        }

        override fun doInBackground(vararg params: String): String {
            mileage = params[4]
            if (params[0].length > 0) {
                isVinEntered = true
                vinResolvedObject = VinResolvedObject(params[0], params[1], params[2], params[3], params[4].toFloat())
                return addVehicleRequest(params[0], params[1], params[2], params[3], params[4])
            } else {
                vinResolvedObject = VinResolvedObject(params[0], params[1], params[2], params[3], params[4].toFloat())
                return addVehicleRequest(params[0], params[1], params[2], params[3], params[4])
            }
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            //Log.dMainActivity.TAG, result)
            val profileResult: JSONObject
            if (result.contains("IsSuccess\":true")) {
                try {
                    val jObject = JSONObject(result)

                    profileResult = jObject
                            .getJSONObject("AddVehicleInformationResult")

                    val vinID = profileResult.getString("VINID")


                    val vehicleProfileModel = VehicleProfileModel()
                    vehicleProfileModel.vin = vinResolvedObject.vin
                    vehicleProfileModel.vinid = vinID
                    vehicleProfileModel.make = vinResolvedObject.make
                    vehicleProfileModel.model = vinResolvedObject.model
                    vehicleProfileModel.year = vinResolvedObject.year
                    vehicleProfileModel.androidPhoneId = LivePhoneReadings.phoneId
                    vehicleProfileModel.btID = LivePhoneReadings.bluetoothId
                    vehicleProfileModel.vehicleHealth = 70
                    vehicleProfileModel.reason = getString(R.string.vehicle_health_default_reason)
                    if (isVinEntered) {
                        vehicleProfileModel.vinRetrievable = true
                    } else {
                        vehicleProfileModel.vinRetrievable = false
                    }
                    vehicleProfileModel.mileage = mileage

                    val dbHelper = DataBaseHelper(activity)
                    dbHelper.addProfile(vehicleProfileModel)


                    ApplicationPrefs.getInstance(activity).setVehicleProfilePrefs(vehicleProfileModel)
                    textViewConnect.text = vehicleProfileModel.make + " " + vehicleProfileModel.model + " " + vehicleProfileModel.year

                    Toast.makeText(activity, "Vehicle added successfully", Toast.LENGTH_SHORT).show()


                } catch (jsonExp: JSONException) {
                    jsonExp.printStackTrace()
                    Toast.makeText(activity, "Failed to add vehicle, please try again", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(activity, "Failed to add vehicle, please try again", Toast.LENGTH_SHORT).show()
            }

            //            progressDialog.dismiss();
        }


        private fun addVehicleRequest(vin: String, year: String, make: String, model: String, mileage: String): String {
            //Log.dMainActivity.TAG, "Calling Web Service")
            var result = ""
            try {

                val values = ContentValues()
                values.put("VIN", vin)
                values.put("MFYear", year)
                values.put("Make", make + "sherifVehicleHealth")
                values.put("Model", model)
                values.put("Mileage", mileage)
                values.put("MobileUserProfileId", LivePhoneReadings.getMobileUserProfileId())
                values.put("MacId", "Vehicle Health")


                result = Utility.postRequest(getString(R.string.addVehicleURL), values)

                //Log.dMainActivity.TAG, "Result response= " + result)

            } catch (e: Exception) {
                e.printStackTrace()
                result = "failed"
            }

            return result
        }


    }

    companion object {
        var vehicalInfo = "0"
        private var currentspinner = 0
        var connection_count = 0
    }

}
