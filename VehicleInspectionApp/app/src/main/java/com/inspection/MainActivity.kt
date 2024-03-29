package com.inspection

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import java.text.SimpleDateFormat

import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.*
import android.app.job.JobInfo
import android.app.job.JobScheduler

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory

import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.provider.Settings.Secure
import androidx.drawerlayout.widget.DrawerLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager

import com.google.android.gms.plus.Plus
import com.inspection.GCM.GcmBroadcastReceiver
import com.inspection.GCM.GcmRegistration

import com.inspection.imageloader.ImageLoader
import com.inspection.model.AAAFacilityComplete

import androidx.viewpager.widget.ViewPager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.inspection.Utils.*
import com.inspection.adapter.MultipartRequest
import com.inspection.fragments.*
import com.inspection.interfaces.*
import com.inspection.model.AnnualVisitationInspectionFormData
import com.inspection.model.FacilityDataModel
import kotlinx.android.synthetic.main.activity_main1.*
import kotlinx.android.synthetic.main.app_bar_forms.*
import kotlinx.android.synthetic.main.fragment_visitation_form.*
import okio.Utf8
import java.io.*
import java.util.*


class MainActivity : AppCompatActivity(), LocationListener {
    var FacilityName = ""
    var FacilityNumber = ""
    var isLoadNewDetailsRequired = false
    var viewPager: ViewPager? = null
    var visitationID : String? = ""
    lateinit var facilitySelected: AAAFacilityComplete
    internal var sdf: SimpleDateFormat? = null
    var fragment: FragmentForms? = null

    private var changePasswordDialog: AlertDialog? = null

    internal lateinit var drawerNavigationListAdapter: DrawerNavigationListAdapter
    lateinit var saveBtn: Button
    private var mDrawerList: ListView? = null

    internal var isExitFlagReady = false
    private var welcomeMessage = ""



//    private var mLocationRequest: LocationRequest? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main1)

        mContext = this


//        val locationUpdatesIntent = Intent(this,LocationUpdatesService::class.java)
//        startService(locationUpdatesIntent)
//        scheduleAlarm()

//        Utility.scheduleJob(applicationContext)

        var urlString = ApplicationPrefs.getInstance(activity).sessionID
        urlString += "&userId=" + ApplicationPrefs.getInstance(activity).loggedInUserID
        urlString += "&deviceId=" + ApplicationPrefs.getInstance(activity).deviceID

        var bundle = PersistableBundle()
        bundle.putString("urlString", urlString);

        if (Constants.enableLocationTracking) {
            val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

            val jobInfo = JobInfo.Builder(12, ComponentName(this@MainActivity, LocationLogService::class.java))
                    // only add if network access is required
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setMinimumLatency(1000 * 60)
                    .setOverrideDeadline(1000 * 60 * 2)
                    .setPersisted(true)
                    .setExtras(bundle)
                    .build()

            jobScheduler.schedule(jobInfo)
        }
    }

    public fun scheduleAlarm() {
        val intent = Intent(getApplicationContext(), LocationAlarmManager::class.java);
        val pIntent = PendingIntent.getBroadcast(this, LocationAlarmManager.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        var firstMillis = System.currentTimeMillis()
        var alarm = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager;
        var d=Calendar.getInstance()
        alarm?.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis()+5000,
                pIntent)
    }


    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // Sync the toggle state after onRestoreInstanceState has occurred.
        try {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        } catch (exp: Exception) {
            exp.printStackTrace()
        }

        ActivityCompat.requestPermissions(mContext,
                arrayOf("android.permission.CAMERA", "android.permission.READ_EXTERNAL_STORAGE","android.permission.WRITE_EXTERNAL_STORAGE","android.permission.ACCESS_COARSE_LOCATION","android.permission.ACCESS_FINE_LOCATION"),
                123)
        initView()
        if (Constants.enableLocationTracking) enableLocation()

        if (toolbar != null) {
            setSupportActionBar(toolbar)
            toolbar!!.setTitleTextColor(Color.WHITE)

        }

        ApplicationPrefs.getInstance(mContext).clearVehicleProfilePrefs()

        handler = Handler()

        //        openSafetyCheckFragment();

        val fragment= FragmentForms()
        val fragmentManagerSC = supportFragmentManager
        val ftSC = fragmentManagerSC.beginTransaction()
        ftSC.replace(R.id.fragment, fragment)
        ftSC.addToBackStack("")
        ftSC.commit()



//        val myFolder = File(Environment.getExternalStorageDirectory().path + "/Matics")
//        if (!myFolder.exists()) {
//            myFolder.mkdir()
//        }
        icon = BitmapFactory.decodeResource(resources, R.drawable.banner)

//        PHONE_ID = Secure.getString(applicationContext.contentResolver, Secure.ANDROID_ID)

        if (!GcmBroadcastReceiver.isStarted) {
            GcmRegistration(this)
        }


//        if (ApplicationPrefs.getInstance(this).getBooleanPref(ApplicationPrefs.IS_PASSWORD_RESET)) {
//            showChangePasswordDialog()
//            ApplicationPrefs.getInstance(this).setBooleanPref(ApplicationPrefs.IS_PASSWORD_RESET, false)
//        }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    fun enableLocation() {
        var service = getSystemService(LOCATION_SERVICE) as LocationManager
        var enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {
            var alertBuilder = AlertDialog.Builder(this);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle("GPS Location is required")
            alertBuilder.setMessage("GPS location is required within this app. If you disagree the app will be closed");
            alertBuilder.setPositiveButton("Agree") { dialog, which ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
            alertBuilder.setNegativeButton("Disagree") { dialog, which ->
                this.finish()
            }
            val alert = alertBuilder.create();
            alert.show();
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Pass any configuration change to the drawer toggls
//        drawerToggle!!.onConfigurationChanged(newConfig)
    }

    private fun initView() {
//        mDrawerList = findViewById<View>(R.id.left_drawer) as ListView
//        //mDrawerList.setOnItemClickListener(activity);
//        toolbar = findViewById<View>(R.id.toolbar) as Toolbar
//        saveBtn = findViewById<View>(R.id.saveBtn) as Button
//        mDrawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
//        drawerNavigationListAdapter = DrawerNavigationListAdapter()
//        mDrawerList!!.adapter = drawerNavigationListAdapter

        //pageAdapter = new MyPagerAdapter(getSupportFragmentManager());
        //ViewPager pager=(ViewPager)findViewById(R.id.pager);
        //pager.setAdapter(pageAdapter);
    }



    @SuppressLint("NewApi")
    private fun Generate_Files() {

        // -------------------Generating Log file
        try {
            val path11 = Environment.getExternalStorageDirectory().path + ""
            val file = File(path11, "/" + "android.txt")
            file.setWritable(true)
            file.createNewFile()
        } catch (e1: IOException) {
            // TODO Auto-generated catch block
            e1.printStackTrace()
        }

        // --------------Generating offline file
        // ---------------whenever Internet connection not available then url
        // will be store in matics file
        // ---------------when Internet connection will be available this file
        // automatically send to server and fill it to database
        try {
            val path11 = Environment.getExternalStorageDirectory().path + ""
            val file = File(path11, "/" + "matics.txt")
            file.setWritable(true)
            file.createNewFile()
        } catch (e1: IOException) {
            // TODO Auto-generated catch block
            e1.printStackTrace()
        }

        // -----------------Generating online file
        try {
            val path11 = Environment.getExternalStorageDirectory().path + ""
            val file = File(path11, "/" + "online.txt")
            file.setWritable(true)
            file.createNewFile()
        } catch (e1: IOException) {
            // TODO Auto-generated catch block
            e1.printStackTrace()
        }

    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()


    }


    override fun onPause() {
        // TODO Auto-generated method stub
        //Log.e("", "onPause");

        isAppInForeground = false

        super.onPause()
    }


    override fun onResume() {
        //Log.e("", "onResume");

        isAppInForeground = true

        if (welcomeMessage.length > 0) {
            val builder = AlertDialog.Builder(mContext)
            builder.setMessage(welcomeMessage)
            builder.setNegativeButton("OK") { dialog, which -> welcomeMessage = "" }

            builder.show()
            welcomeMessage = ""

        }

        val mNotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        mNotificationManager.cancel(110)



        super.onResume()
    }


    override fun onDestroy() {
        super.onDestroy()
    }


    override fun onLocationChanged(location: Location) {
        currentLocation = location
    }

    override fun onProviderDisabled(provider: String) {

    }

    override fun onProviderEnabled(provider: String) {

    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        // TODO Auto-generated method stub

    }


    private fun showChangePasswordDialog() {
        changePasswordDialog = AlertDialog.Builder(this).create()
        val inflater = LayoutInflater.from(this)
        val dialogView = inflater.inflate(
                R.layout.dialog_change_password, null)
        val linearLayout = dialogView
                .findViewById<View>(R.id.changePasswordProgressLayout) as LinearLayout

        val oldPasswordEditText = dialogView.findViewById<View>(R.id.changePasswordOldPasswordEditText) as EditText
        val newPasswordEditText = dialogView.findViewById<View>(R.id.changePasswordNewPasswordEditText) as EditText
        val confirmNewPasswordEditText = dialogView.findViewById<View>(R.id.changePasswordConfirmNewPasswordEditText) as EditText

        changePasswordDialog!!.setView(dialogView)
        //        changePasswordDialog.setCancelable(false);
        changePasswordDialog!!.setTitle("Change Password")
        val dialogOkButton = dialogView
                .findViewById<View>(R.id.changePasswordDialogChangePasswordButton) as Button

        dialogOkButton.setOnClickListener {
            if (oldPasswordEditText.text.toString().trim { it <= ' ' }.length > 0 && newPasswordEditText.text.toString().trim { it <= ' ' }.length > 0 && confirmNewPasswordEditText.text.toString().trim { it <= ' ' }.length > 0) {
//                if (confirmNewPasswordEditText.text.toString().trim { it <= ' ' }.equals(newPasswordEditText.text.toString().trim { it <= ' ' })) {
                    dialogOkButton.isEnabled = false
                    oldPasswordEditText.isEnabled = true
                    newPasswordEditText.isEnabled = false
                    confirmNewPasswordEditText.isEnabled = false
//                } else {
//                    Utility.showValidationAlertDialog(this,"Password & Confirm password are not matching")
//                }
                linearLayout.visibility = View.VISIBLE
                changePasswordTask().execute("" + ApplicationPrefs.getInstance(mContext).userProfilePref.mobileUserProfileId, oldPasswordEditText.text.toString(), newPasswordEditText.text.toString())
            }
        }
        changePasswordDialog!!.show()
    }

    internal inner class changePasswordTask : AsyncTask<String, Void, String>() {

        var changePasswordUrl = "http://jet-matics.com/JetComService/JetCom.svc/ChangePassword?"

        override fun doInBackground(vararg params: String): String {
            var result: String? = ""
            try {
                val values = ContentValues()
                values.put("mobileuserprofileid", params[0])
                values.put("oldpassword", params[1])
                values.put("newpassword", params[2])

                //Log.dMainActivity.TAG, "calling url now");
                result = Utility.postRequest(changePasswordUrl, values)

                //Log.dMainActivity.TAG, "Result response= "
                //                        + result);

            } catch (e: Exception) {
                e.printStackTrace()
            }

            return result!!
        }


        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result != null) {
                if (result.contains("Successfully")) {
                    changePasswordDialog!!.dismiss()
                    val confirmationDialog = AlertDialog.Builder(this@MainActivity)
                    confirmationDialog.setMessage("Password has been changed successfully.")
                    confirmationDialog.setPositiveButton("OK", null)
                    confirmationDialog.show()
                } else {
                    val confirmationDialog = AlertDialog.Builder(this@MainActivity)
                    confirmationDialog.setMessage("Old password is not correct.")
                    confirmationDialog.setPositiveButton("OK", null)
                    confirmationDialog.show()
                    changePasswordDialog!!.dismiss()

                }
            } else {
                val confirmationDialog = AlertDialog.Builder(this@MainActivity)
                confirmationDialog.setMessage("Unable to connect. Please make sure that internet connection is active")
                confirmationDialog.setPositiveButton("OK", null)
                confirmationDialog.show()
                changePasswordDialog!!.dismiss()
            }
        }
    }

    override fun onBackPressed() {
        //Log.dMainActivity.TAG, "" + getFragmentManager().getBackStackEntryCount());
        if (fragmentManager.backStackEntryCount == 1){
            viewPager?.adapter = null
        }else if (fragmentManager.backStackEntryCount != 0) {
            fragmentManager.popBackStack()
        } else {
            if (isExitFlagReady) {
                super.onBackPressed()
            } else {

                //                if (getFragmentManager().findFragmentById(R.id.fragment) instanceof IncomingCallFragment || getFragmentManager().findFragmentById(R.id.fragment) instanceof FragmentVideoCall) {
                //                    runOnUiThread(new Runnable() {
                //                        @Override
                //                        public void run() {
                //                            selectItem(0);
                //                        }
                //                    });
                //                } else {
                toast("Press back button again to return to the main menu")
                object : Thread() {
                    override fun run() {
                        try {
                            isExitFlagReady = true
                            Thread.sleep(3500)
                            isExitFlagReady = false
                            //Log.dMainActivity.TAG, "interrupted");
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }.start()
                //                }
            }
        }
    }

    private fun selectItem(position: Int) {
        // Create a new fragment and specify the planet to show based on position
        mDrawerLayout.closeDrawer(mDrawerList!!)
        val fragmentManager = supportFragmentManager
        //        androidx.appcompat.app.Fragment f = fragmentManager.findFragmentById(R.id.fragment);
        when (position) {

            0 -> {
                // Saeed Mostafa - For noth Shop/User Mode will need Accessing Storage permission - move to new method after checking the permission
                //                if (ActivityCompat.checkSelfPermission(this,WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //                    // TODO: Consider calling
                //                    fragmentRequestingPermission= "MainActivitySafetyCheckMenuItem";
                //                    ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, 1);
                //                } else {
                //                    openSafetyCheckFragment();
                //                }
                val fragment = FragmentForms()
                val fragmentManagerSC = fragmentManager
                val ftSC = fragmentManagerSC.beginTransaction()
                ftSC.replace(R.id.fragment, fragment as Fragment)
                ftSC.addToBackStack("")
                ftSC.commit()
            }

            1 -> {

                val logoutConfirmationDialog = AlertDialog.Builder(mContext)
                logoutConfirmationDialog.setTitle("Please Confirm")
                logoutConfirmationDialog.setMessage("Are you sure you want to logout?")
                logoutConfirmationDialog.setNegativeButton("Cancel", null)
                logoutConfirmationDialog.setPositiveButton("YES") { dialog, which ->
                    ApplicationPrefs.getInstance(mContext).accountDetailPref = null
                    //                        ApplicationPrefs.getInstance(mContext).setVehicleHealthMessagePref("No TroubleCodes");
                    //                        ApplicationPrefs.getInstance(mContext).setVehicleHealthValuePref(100);
                    ApplicationPrefs.getInstance(mContext).setLastCompetitorUpdate(Date().time + 50 * 60 * 60 * 1000)

                    ApplicationPrefs.getInstance(mContext).setBooleanPref(getString(R.string.is_user_logged_in_with_email), false)



                    if (ApplicationPrefs.getInstance(mContext).getBooleanPref(getString(R.string.is_user_logged_in_with_google_plus))) {
                        if (LoginActivity.mGoogleApiClient != null) {
                            if (LoginActivity.mGoogleApiClient!!.isConnected) {
                                Plus.AccountApi.clearDefaultAccount(LoginActivity.mGoogleApiClient)
                                Plus.AccountApi.revokeAccessAndDisconnect(LoginActivity.mGoogleApiClient)
                                LoginActivity.mGoogleApiClient!!.disconnect()
                            }
                        }

                        ApplicationPrefs.getInstance(mContext).setBooleanPref(getString(R.string.is_user_logged_in_with_google_plus), false)
                    }

                    if (ApplicationPrefs.getInstance(mContext).getBooleanPref(getString(R.string.is_user_logged_in_with_facebook))) {

                        ApplicationPrefs.getInstance(mContext).setBooleanPref(getString(R.string.is_user_logged_in_with_facebook), false)
                    }

                    val intent = Intent(mContext, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                logoutConfirmationDialog.setCancelable(false)
                logoutConfirmationDialog.show()
            }
        }

    }

    internal inner class DrawerNavigationListAdapter : BaseAdapter() {
        val inflater: LayoutInflater

        init {
            inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        }

        override fun getCount(): Int {
            return 2
        }

        override fun getItem(position: Int): Any? {
            return null
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

            var view: View? = convertView

            if (view == null) {
                view = inflater.inflate(R.layout.drawer_list_item, null)
            }
            val drawerIconImage = view!!.findViewById<View>(R.id.item_icon) as ImageView
            val drawerTextView = view.findViewById<View>(R.id.item_text) as TextView

            when (position) {
                0 -> {
                    drawerIconImage.setImageResource(R.drawable.safety)
                    if (ApplicationPrefs.getInstance(mContext).safetyCheckProgramName.length > 0) {
                        drawerTextView.text = ApplicationPrefs.getInstance(mContext).safetyCheckProgramName
                    } else {
                        drawerTextView.text = "Forms"
                    }
                }

                1 -> {
                    drawerIconImage.setImageResource(R.drawable.logout)
                    if (ApplicationPrefs.getInstance(applicationContext).isLoggedInPref) {
                        drawerTextView.text = "Logout"
                    } else {
                        drawerTextView.text = "Login"
                    }
                }
            }


            return view
        }
    }

    fun updateDrawer() {
        //Log.dMainActivity.TAG, "updateDrawer is being updated");
        drawerNavigationListAdapter.notifyDataSetChanged()
        // OR
        //mDrawerList.setAdapter(new DrawerNavigationListAdapter());
    }

    fun checkPermission() : Boolean {
        return ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
    }

    fun requestPermissionAndContinue() {
        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)) {
                var alertBuilder = AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("Permission Required")
                alertBuilder.setMessage("Storage permission is required to create generate the completed visitation PDF, ");
                alertBuilder.setPositiveButton("YES") { dialog, which ->
                    ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE), 350);
                }
                val alert = alertBuilder.create();
                alert.show();
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE), 350);
            }
        } else {
            generateAndOpenPDF()
        }
    }

    fun generateAndOpenPDF(){
        createPDF(this)
//        val target = Intent(Intent.ACTION_VIEW)
//        val file = File(Environment.getExternalStorageDirectory().path+"/"+FacilityDataModel.getInstance().tblFacilities[0].FACNo+"_VisitationDetails_ForSpecialist.pdf")
//        val fileShop = File(Environment.getExternalStorageDirectory().path+"/"+FacilityDataModel.getInstance().tblFacilities[0].FACNo+"_VisitationDetails_ForShop.pdf")
//        val file = File(Environment.getExternalStorageDirectory().path+"/"+Constants.visitationIDForPDF+"_VisitationDetails_ForSpecialist.pdf")
//        val fileShop = File(Environment.getExternalStorageDirectory().path+"/"+Constants.visitationIDForPDF+"_VisitationDetails_ForShop.pdf")

//        uploadPDF(file,"Specialist")
//        uploadPDF(fileShop,"Shop")
//        val target = Intent(Intent.ACTION_VIEW)
//        target.setDataAndType(FileProvider.getUriForFile(this,"com.inspection.android.fileprovider",file), "application/pdf")
//        target.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
//        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        val intent = Intent.createChooser(target, "Open File")
//        try {
//            startActivity(intent)
//        } catch (e: ActivityNotFoundException) {
//            // Instruct the user to install a PDF reader here, or something
//        }
    }

    fun uploadPDF(file: File,type: String) {
//        val multipartRequest = MultipartRequest(Constants.uploadFile+ApplicationPrefs.getInstance(activity).loggedInUserEmail, null, file, Response.Listener { response ->
        val multipartRequest = MultipartRequest(Constants.uploadFile+"saeed@pacificresearchgroup.com&type=${type}", null, file, Response.Listener { response ->
            try {
//                println("Networkonse " + String(response.data, Utf8)
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }

            //                Toast.makeText(context, "Upload successfully!", Toast.LENGTH_SHORT).show();
        }, Response.ErrorListener {
            //                Toast.makeText(context, "Upload failed!\r\n" + error.toString(), Toast.LENGTH_SHORT).show();
        })
        val socketTimeout = 30000//30 seconds - change to what you want
        val policy = DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        multipartRequest.retryPolicy = policy
        Volley.newRequestQueue(applicationContext).add(multipartRequest)
    }




    // Saeed Mostafa - 02092017 - CallBack to check the permissions [START]
    override fun  onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.size > 0) {
                // We can now safely use the API we requested access to
                //                if (fragmentRequestingPermission.equals("VehicleFacility_ALL")) { //Location Permission
                //                    Log.v("RequetPermissionResult:","  VehicleFacility_ALL");
                //                    FragmentVehicleFacility fragment= (FragmentVehicleFacility) getFragmentManager().findFragmentById(R.id.fragment);
                //                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ////                        fragment.handleMap();
                //                    if (grantResults[1] == PackageManager.PERMISSION_GRANTED)
                //                    fragment.handlebanner();
                //                } else if (fragmentRequestingPermission.equals("VehicleFacility_LOCATION") && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //Location Permission
                //                    Log.v("RequetPermissionResult:","  VehicleFacility_LOCATION");
                //                    FragmentVehicleFacility fragment= (FragmentVehicleFacility) getFragmentManager().findFragmentById(R.id.fragment);
                ////                    fragment.handleMap();
                //                } else if (fragmentRequestingPermission.equals("VehicleFacility_STORAGE") && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //Location Permission
                //                    Log.v("RequetPermissionResult:","  VehicleFacility_STORAGE");
                //                    FragmentVehicleFacility fragment= (FragmentVehicleFacility) getFragmentManager().findFragmentById(R.id.fragment);
                //                    fragment.handlebanner();
                if (fragmentRequestingPermission == "FragmentSafetyCheckItems" && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //Storage & Camera Permission
                    Log.v("RequetPermissionResult:", "  FragmentSafetyCheckItems")
                    val fragment = supportFragmentManager.findFragmentById(R.id.fragment) as FragmentSafetyCheckItems
                    fragment.dispatchTakePictureIntent()
                } else if (fragmentRequestingPermission == "MainActivitySafetyCheckMenuItem" && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //Storage Permission
                    Log.v("RequetPermissionResult:", "  MainActivitySafetyCheckMenuItem")
                    openSafetyCheckFragment()
                }
                return
            }
            else if (requestCode ==100){
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "Permission has been denied by user")
                } else {
                    Log.i(TAG, "Permission has been granted by user")
                }
            }
            else if (requestCode ==350){
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "Permission has been denied by user")
                } else {
                    generateAndOpenPDF()
                }
            }
        }

    }
    // Saeed Mostafa - 02092017 - CallBack to check the permissions [END]

    fun openSafetyCheckFragment() {
        val folder = File(Environment.getExternalStorageDirectory().toString() +
                File.separator + "Matics")
        if (!folder.exists()) {
            folder.mkdirs()
        }
        val fragment: Fragment
        if (ApplicationPrefs.getInstance(this).userProfilePref.isShop) {
            fragment = FragmentSafetyCheckInitial()
        } else {
            fragment = FragmentSafetyCheckReports()
        }
        val fragmentManagerSC = supportFragmentManager
        val ftSC = fragmentManagerSC.beginTransaction()
        ftSC.replace(R.id.fragment, fragment)
        ftSC.addToBackStack("")
        ftSC.commit()
    }


    companion object {

        val MY_BLUETOOTH_ENABLE_REQUEST_ID = 6
        val PHOTO_CAPTURE_ACTIVITY_REQUEST_ID = 345
        val ANSWER_VIDEO_CALL = "AnswerVideoCall"
        val CANCEL_VIDEO_CALL = "CancelVideoCall"
        val INCOMING_CALL = "incomingCall"
        val TAG = "VehicleHealthMonitor"
        var btAdapter: BluetoothAdapter? = null
        private val CONTENT = arrayOf("Connect", "Bluetooth", "Vehicle Profile", "Setting", "Profile")
        var devString: String? = null
        var fragmentRequestingPermission = ""

        internal var Upload_period: String? = null
        var Enable: Boolean? = false
        var uploadtask: Boolean? = false
        var Upload_Url = "http://www.jet-matics.com/JetComService/JetCom.svc/BluetoothDetailGet?"
        var bluetoothDevice: BluetoothDevice? = null
        var `in`: InputStream? = null
        var out: OutputStream? = null
        internal val SETTINGS = 3
        internal val STOP_SERVICE = 4
        var sock: BluetoothSocket? = null
        var finalthrottle = 0.0
        protected val MY_BEACON_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D"
        var isFirsttime = true
        var isDialog = true
        var spinnerstatus = true
        var PHONE_ID = ""
        var Connected: Boolean? = false
        var currentLocation: Location? = null

        private var Bgimage: ImageView? = null
        internal var latitude: Double = 0.toDouble()
        internal var longitude: Double = 0.toDouble()
        internal var Gpsspeed: Double = 0.toDouble()
        internal var Gpstime: Double = 0.toDouble()
        //	public PagerAdapter mPagerAdapter;
        var imgLoader: ImageLoader? = null
        lateinit var mContext: Activity
        var GCM_DeviceID = "GCM_DeviceID"
        var GCM_ID = "GCM_DeviceID"
        //
        var TabPosition = 0
        var boolThread = true
        var boolService = true
        var serviceIntent: Intent? = null
        // TODO Auto-generated method stub
        lateinit var handler: Handler
            internal set
        // TODO Auto-generated method stub
        var activity: Context? = null
            internal set


        internal lateinit var icon: Bitmap

        lateinit var mDrawerLayout: DrawerLayout
        var isBluetoothEnableDenied = false

        var customerNameBeingCalled = ""
        var customerMobileUserProfileIDbeingCalled = 0
        var customerEmailToCall = ""

        var isAppInForeground = false

        var isCallRequested = false

        fun isNetworkAvailable(context: Context): Boolean {
            val connectivity = context
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivity != null) {
                val info = connectivity.allNetworkInfo
                if (info != null)
                    for (i in info.indices)
                        if (info[i].state == NetworkInfo.State.CONNECTED) {
                            return true
                        }
            }
            return false
        }

        fun updateBigImage() {
            val userAccountModel = ApplicationPrefs.getInstance(activity).userAccountPref

            val Bgimage = mContext.findViewById<View>(R.id.imagebg) as ImageView
            try {
                ImageLoader(mContext, Bgimage, userAccountModel.photoUrl).execute()
            } catch (e: Exception) {
                val icon = BitmapFactory.decodeResource(mContext.resources, R.drawable.banner)
                Bgimage.setImageBitmap(icon)
            }

        }

        fun checkInternetConnection(context: Context): Boolean {
            val cm = context
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return if (cm.activeNetworkInfo != null
                    && cm.activeNetworkInfo!!.isAvailable
                    && cm.activeNetworkInfo!!.isConnected) {
                true
            } else {
                //Log.dMainActivity.TAG, "Internet Connection Not Present");
                false
            }
        }
    }



}