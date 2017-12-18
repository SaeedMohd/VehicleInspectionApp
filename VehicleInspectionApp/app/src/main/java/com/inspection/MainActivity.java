package com.inspection;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;

import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.google.android.gms.plus.Plus;
import com.inspection.GCM.GcmBroadcastReceiver;
import com.inspection.GCM.GcmRegistration;

import com.inspection.fragments.FragmentForms;
import com.inspection.fragments.FragmentSafetyCheckInitial;
import com.inspection.fragments.FragmentSafetyCheckItems;
import com.inspection.fragments.FragmentSafetyCheckReports;

import com.inspection.imageloader.ImageLoader;
import com.inspection.R;
import com.inspection.model.UserAccountModel;
import com.inspection.serverTasks.GetAccountDetailByEmailAndPhoneIDTask;
import com.inspection.serverTasks.GetShopUserProfileDetails;
import com.inspection.Utils.ApplicationPrefs;
import com.inspection.Utils.Utility;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class MainActivity extends AppCompatActivity implements LocationListener {

    public static final int MY_BLUETOOTH_ENABLE_REQUEST_ID = 6;
    public static final int PHOTO_CAPTURE_ACTIVITY_REQUEST_ID = 345;
    public static final String ANSWER_VIDEO_CALL = "AnswerVideoCall";
    public static final String CANCEL_VIDEO_CALL = "CancelVideoCall";
    public static final String INCOMING_CALL = "incomingCall";
    public static final String TAG = "VehicleHealthMonitor";
    public static BluetoothAdapter btAdapter;
    private static final String[] CONTENT = new String[]{"Connect", "Bluetooth", "Vehicle Profile", "Setting", "Profile"};
    public static String devString;
    public static String fragmentRequestingPermission="";
    //    public FragmentVehicleFacility FragmentRequestingPermission;
    public String FacilityName="";
    public String FacilityNumber="";

    static String Upload_period;
    public static Boolean Enable = false, uploadtask = false;
    public static String Upload_Url = "http://www.jet-matics.com/JetComService/JetCom.svc/BluetoothDetailGet?";
    Window wind;
    public static BluetoothDevice bluetoothDevice;
    public static InputStream in = null;
    public static OutputStream out = null;
    Timer timer;
    int period = 10000;
    static final int SETTINGS = 3;
    static final int STOP_SERVICE = 4;
    public static BluetoothSocket sock = null;
    public static double finalthrottle = 0;
    SharedPreferences mPref;
    protected static final String MY_BEACON_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    SimpleDateFormat sdf;
    public static boolean isFirsttime = true, isDialog = true, spinnerstatus = true;
    public static String PHONE_ID = "";
    public static Boolean Connected = false;
    long time3 = 10000;
    public static Location currentLocation = null;
    public static LocationManager locationManager = null;
    private static ImageView Bgimage;
    static double latitude, longitude, Gpsspeed, Gpstime;
    private TabHost mTabHost;
    //	public PagerAdapter mPagerAdapter;
    public static ImageLoader imgLoader;
    int serverResponseCode = 0;
    public static Activity mContext;
    public static String GCM_DeviceID = "GCM_DeviceID";
    public static String GCM_ID = "GCM_DeviceID";
    //
    public static int TabPosition = 0;
    public static boolean boolThread = true;
    public static boolean boolService = true;
    public static Intent serviceIntent = null;
    static Handler handler;
    static Context con;

    private AlertDialog changePasswordDialog;

    private ImageView repairShopImage;


    static Bitmap icon;

    public static DrawerLayout mDrawerLayout;
    public DrawerNavigationListAdapter drawerNavigationListAdapter;
    private ActionBarDrawerToggle drawerToggle;
    public Toolbar toolbar;
    private ListView mDrawerList;

    Button btnRepairFacility, btnVehicleHealth, btnRepairHistory;
    ImageView signOutButton;
    ImageView ivUser, ivSetting, ivSearch, ivAbout, ivVehicles;
    public static boolean isBluetoothEnableDenied = false;
    boolean isExitFlagReady = false;
    boolean bound = false;

    ProgressDialog loadingCustomerProgressDialog;

    public static String customerNameBeingCalled = "";
    public static int customerMobileUserProfileIDbeingCalled = 0;
    public static String customerEmailToCall = "";

    private boolean isDataSynchedToday = false;

    public static boolean isAppInForeground = false;

    public static boolean isCallRequested = false;

    private String welcomeMessage = "";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main1);

        mContext = this;

        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }catch (Exception exp){
            exp.printStackTrace();
        }

        initView();

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    selectItem(position);
                }
            });
            toolbar.setTitleTextColor(Color.WHITE);
            //ActionBar bar = getActionBar();
            //bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f6f7fa")));
            drawerToggle = new ActionBarDrawerToggle(mContext, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                }

                @Override

                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);

                }
            };
            mDrawerLayout.setDrawerListener(drawerToggle);
        }

        ApplicationPrefs.getInstance(mContext).clearVehicleProfilePrefs();

        repairShopImage = (ImageView) findViewById(R.id.imagebg);

        handler = new Handler();

        initalize();

//        openSafetyCheckFragment();

        android.support.v4.app.Fragment fragment;
        fragment =  new FragmentForms();
        FragmentManager fragmentManagerSC = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ftSC= fragmentManagerSC.beginTransaction();
        ftSC.replace(R.id.fragment, fragment);
        ftSC.addToBackStack("");
        ftSC.commit();



//        if (new Date().getTime() - ApplicationPrefs.getInstance(mContext).getLastSynchDate() > (1000 * 60 * 60 * 24)) {
//            new GetAccountDetailByEmailAndPhoneIDTask(mContext, false, ApplicationPrefs.getInstance(mContext).getUserProfilePref().getEmail(), "") {
//
//                @Override
//                public void onTaskCompleted(String result) {
//                    //Log.dMainActivity.TAG, "synching user data completed");
//                    new GetShopUserProfileDetails(mContext, false, ApplicationPrefs.getInstance(mContext).getUserAccountPref().getEmail(), "") {
//                        @Override
//                        public void onTaskCompleted(String result) {
//                            //Log.dMainActivity.TAG, "Synching shop profile completed");
//                            ApplicationPrefs.getInstance(con).setLastSynchDate(new Date().getTime());
//                            this.checkAccountDetailsRetrievedFromCloud(result);
//                        }
//                    }.execute();
//                }
//            }.execute();
//
//        }

        File myFolder = new File(Environment.getExternalStorageDirectory().getPath() + "/Matics");
        if (!myFolder.exists()) {
            myFolder.mkdir();
        }

        icon = BitmapFactory.decodeResource(getResources(), R.drawable.banner);

//        ApplicationPrefs.getInstance(this).updateProfiles();

        PHONE_ID = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);

        if (!GcmBroadcastReceiver.isStarted) {
            new GcmRegistration(this);
        }


        if (ApplicationPrefs.getInstance(this).getBooleanPref(ApplicationPrefs.IS_PASSWORD_RESET)) {
            showChangePasswordDialog();
            ApplicationPrefs.getInstance(this).setBooleanPref(ApplicationPrefs.IS_PASSWORD_RESET, false);
        }


        Bgimage = (ImageView) findViewById(R.id.imagebg);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void initView() {
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        //mDrawerList.setOnItemClickListener(activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerNavigationListAdapter = new DrawerNavigationListAdapter();
        mDrawerList.setAdapter(drawerNavigationListAdapter);

        //pageAdapter = new MyPagerAdapter(getSupportFragmentManager());
        //ViewPager pager=(ViewPager)findViewById(R.id.pager);
        //pager.setAdapter(pageAdapter);
    }

    private void initalize() {
        // TODO Auto-generated method stub
        btnRepairFacility = (Button) findViewById(R.id.btnRepairFacility);
        btnRepairHistory = (Button) findViewById(R.id.btnRepairHistory);
        btnVehicleHealth = (Button) findViewById(R.id.btnVehicleHealth);
        signOutButton = (ImageView) findViewById(R.id.signoutButton);
        //final LoginButton button = (LoginButton) findViewById(R.id.signoutButton);
//		button.setBackgroundResource(R.drawable.black_bg);


        ivUser = (ImageView) findViewById(R.id.ivUser);
        ivAbout = (ImageView) findViewById(R.id.ivAbout);
        //ivSearch=(ImageView) findViewById(R.id.ivSearch);
        ivSetting = (ImageView) findViewById(R.id.ivSetting);
        ivVehicles = (ImageView) findViewById(R.id.ivVehicles);


    }


    @SuppressLint("NewApi")
    private void Generate_Files() {

        // -------------------Generating Log file
        try {
            String path11 = Environment.getExternalStorageDirectory().getPath()
                    + "";
            File file = new File(path11, "/" + "android.txt");
            file.setWritable(true);
            file.createNewFile();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // --------------Generating offline file
        // ---------------whenever Internet connection not available then url
        // will be store in matics file
        // ---------------when Internet connection will be available this file
        // automatically send to server and fill it to database
        try {
            String path11 = Environment.getExternalStorageDirectory().getPath()
                    + "";
            File file = new File(path11, "/" + "matics.txt");
            file.setWritable(true);
            file.createNewFile();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        // -----------------Generating online file
        try {
            String path11 = Environment.getExternalStorageDirectory().getPath()
                    + "";
            File file = new File(path11, "/" + "online.txt");
            file.setWritable(true);
            file.createNewFile();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();


    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        //Log.e("", "onPause");

        isAppInForeground = false;

        //Facebook track app closed or paused
        AppEventsLogger.deactivateApp(this);


        super.onPause();
    }


    @Override
    protected void onResume() {
        //Log.e("", "onResume");

        isAppInForeground = true;

        //Facebook tracking app installs and opens
        AppEventsLogger.activateApp(this);


        if (welcomeMessage.length() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(welcomeMessage);
            builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    welcomeMessage = "";
                }
            });

            builder.show();
            welcomeMessage = "";

        }

        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.cancel(110);



        super.onResume();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    public static void updateBigImage() {
        UserAccountModel userAccountModel = ApplicationPrefs.getInstance(getActivity()).getUserAccountPref();

        ImageView Bgimage = (ImageView) mContext.findViewById(R.id.imagebg);
        try {
            new ImageLoader(mContext, Bgimage, userAccountModel.getPhotoUrl()).execute();
        } catch (Exception e) {
            Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.banner);
            Bgimage.setImageBitmap(icon);
        }
    }

    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        } else {
            //Log.dMainActivity.TAG, "Internet Connection Not Present");
            return false;
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }




    public static Handler getHandler() {
        // TODO Auto-generated method stub
        return handler;
    }

    public static Context getActivity() {
        // TODO Auto-generated method stub
        return con;
    }



    private void showChangePasswordDialog() {
        changePasswordDialog = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = LayoutInflater.from(this);
        final View dialogView = inflater.inflate(
                R.layout.dialog_change_password, null);
        final LinearLayout linearLayout = (LinearLayout) dialogView
                .findViewById(R.id.changePasswordProgressLayout);

        final EditText oldPasswordEditText = (EditText) dialogView.findViewById(R.id.changePasswordOldPasswordEditText);
        final EditText newPasswordEditText = (EditText) dialogView.findViewById(R.id.changePasswordNewPasswordEditText);
        final EditText confirmNewPasswordEditText = (EditText) dialogView.findViewById(R.id.changePasswordConfirmNewPasswordEditText);

        changePasswordDialog.setView(dialogView);
//        changePasswordDialog.setCancelable(false);
        changePasswordDialog.setTitle("Change Password");
        final Button dialogOkButton = (Button) dialogView
                .findViewById(R.id.changePasswordDialogChangePasswordButton);

        dialogOkButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (oldPasswordEditText.getText().toString().trim().length() > 0 && newPasswordEditText.getText().toString().trim().length() > 0 && confirmNewPasswordEditText.getText().toString().trim().length() > 0) {
                    dialogOkButton.setEnabled(false);
                    oldPasswordEditText.setEnabled(true);
                    newPasswordEditText.setEnabled(false);
                    confirmNewPasswordEditText.setEnabled(false);

                    linearLayout.setVisibility(View.VISIBLE);

                    new changePasswordTask().execute(""+ApplicationPrefs.getInstance(mContext).getUserProfilePref().getMobileUserProfileId(), oldPasswordEditText.getText().toString(), newPasswordEditText.getText().toString());


                }
            }
        });
        changePasswordDialog.show();
    }

    class changePasswordTask extends AsyncTask<String, Void, String> {

        String changePasswordUrl = "http://jet-matics.com/JetComService/JetCom.svc/ChangePassword?";

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                ContentValues values = new ContentValues();
                values.put("mobileuserprofileid", params[0]);
                values.put("oldpassword", params[1]);
                values.put("newpassword", params[2]);

                //Log.dMainActivity.TAG, "calling url now");
                result = Utility.postRequest(changePasswordUrl, values);

                //Log.dMainActivity.TAG, "Result response= "
//                        + result);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                if (result.contains("Successfully")) {
                    changePasswordDialog.dismiss();
                    AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(MainActivity.this);
                    confirmationDialog.setMessage("Password has been changed successfully.");
                    confirmationDialog.setPositiveButton("OK", null);
                    confirmationDialog.show();
                } else {
                    AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(MainActivity.this);
                    confirmationDialog.setMessage("Old password is not correct.");
                    confirmationDialog.setPositiveButton("OK", null);
                    confirmationDialog.show();
                    changePasswordDialog.dismiss();

                }
            } else {
                AlertDialog.Builder confirmationDialog = new AlertDialog.Builder(MainActivity.this);
                confirmationDialog.setMessage("Unable to connect. Please make sure that internet connection is active");
                confirmationDialog.setPositiveButton("OK", null);
                confirmationDialog.show();
                changePasswordDialog.dismiss();
            }
        }
    }

    @Override
    public void onBackPressed() {
        //Log.dMainActivity.TAG, "" + getFragmentManager().getBackStackEntryCount());
        if (getFragmentManager().getBackStackEntryCount() != 0) {
            getFragmentManager().popBackStack();
        } else {
            if (isExitFlagReady) {
                super.onBackPressed();
            } else {

//                if (getFragmentManager().findFragmentById(R.id.fragment) instanceof IncomingCallFragment || getFragmentManager().findFragmentById(R.id.fragment) instanceof FragmentVideoCall) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            selectItem(0);
//                        }
//                    });
//                } else {
                Toast.makeText(this, "Press back button again to leave the app", Toast.LENGTH_LONG).show();
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            isExitFlagReady = true;
                            Thread.sleep(3500);
                            isExitFlagReady = false;
                            //Log.dMainActivity.TAG, "interrupted");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
//                }
            }
        }
    }

    private void selectItem(int position) {
        // Create a new fragment and specify the planet to show based on position
        mDrawerLayout.closeDrawer(mDrawerList);
        final FragmentManager fragmentManager = getSupportFragmentManager();
//        android.support.v4.app.Fragment f = fragmentManager.findFragmentById(R.id.fragment);
        switch (position) {

            case 0:
                // Saeed Mostafa - For noth Shop/User Mode will need Accessing Storage permission - move to new method after checking the permission
//                if (ActivityCompat.checkSelfPermission(this,WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                    // TODO: Consider calling
//                    fragmentRequestingPermission= "MainActivitySafetyCheckMenuItem";
//                    ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, 1);
//                } else {
//                    openSafetyCheckFragment();
//                }
                android.support.v4.app.Fragment fragment;
                fragment =  new FragmentForms();
                android.support.v4.app.FragmentManager fragmentManagerSC = getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction ftSC= fragmentManagerSC.beginTransaction();
                ftSC.replace(R.id.fragment, fragment);
                ftSC.addToBackStack("");
                ftSC.commit();

                break;

            case 1:

                AlertDialog.Builder logoutConfirmationDialog = new AlertDialog.Builder(mContext);
                logoutConfirmationDialog.setTitle("Please Confirm");
                logoutConfirmationDialog.setMessage("Are you sure you want to logout?");
                logoutConfirmationDialog.setNegativeButton("Cancel", null);
                logoutConfirmationDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ApplicationPrefs.getInstance(mContext).setAccountDetailPref(null);
//                        ApplicationPrefs.getInstance(mContext).setVehicleHealthMessagePref("No TroubleCodes");
//                        ApplicationPrefs.getInstance(mContext).setVehicleHealthValuePref(100);
                        ApplicationPrefs.getInstance(mContext).setLastCompetitorUpdate(new Date().getTime() + 50*60*60*1000);

                        ApplicationPrefs.getInstance(mContext).setBooleanPref(getString(R.string.is_user_logged_in_with_email), false);



                        if (ApplicationPrefs.getInstance(mContext).getBooleanPref(getString(R.string.is_user_logged_in_with_google_plus))) {
                            if (LoginActivity.Companion.getMGoogleApiClient()!= null) {
                                if (LoginActivity.Companion.getMGoogleApiClient().isConnected()) {
                                    Plus.AccountApi.clearDefaultAccount(LoginActivity.Companion.getMGoogleApiClient());
                                    Plus.AccountApi.revokeAccessAndDisconnect(LoginActivity.Companion.getMGoogleApiClient());
                                    LoginActivity.Companion.getMGoogleApiClient().disconnect();
                                }
                            }

                            ApplicationPrefs.getInstance(mContext).setBooleanPref(getString(R.string.is_user_logged_in_with_google_plus), false);
                        }

                        if (ApplicationPrefs.getInstance(mContext).getBooleanPref(getString(R.string.is_user_logged_in_with_facebook))) {
                            FacebookSdk.sdkInitialize(getApplicationContext());
                            LoginManager.getInstance().logOut();

                            ApplicationPrefs.getInstance(mContext).setBooleanPref(getString(R.string.is_user_logged_in_with_facebook), false);
                        }

                        Intent intent = new Intent(mContext, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);


                    }
                });
                logoutConfirmationDialog.setCancelable(false);
                logoutConfirmationDialog.show();

                break;
        }

    }

    class DrawerNavigationListAdapter extends BaseAdapter {
        final LayoutInflater inflater;

        DrawerNavigationListAdapter() {
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = convertView;

            if (view == null) {
                view = inflater.inflate(R.layout.drawer_list_item, null);
            }
            ImageView drawerIconImage = (ImageView) view.findViewById(R.id.item_icon);
            TextView drawerTextView = (TextView) view.findViewById(R.id.item_text);

            switch (position) {
                case 0:
                    drawerIconImage.setImageResource(R.drawable.safety);
                    if (ApplicationPrefs.getInstance(mContext).getSafetyCheckProgramName().length()>0){
                        drawerTextView.setText(ApplicationPrefs.getInstance(mContext).getSafetyCheckProgramName());
                    }else {
                        drawerTextView.setText("Forms");
                    }
                    break;

                case 1:
                    drawerIconImage.setImageResource(R.drawable.logout);
                    if (ApplicationPrefs.getInstance(getApplicationContext()).getIsLoggedInPref()) {
                        drawerTextView.setText("Logout");
                    } else {
                        drawerTextView.setText("Login");
                    }
                    break;
            }


            return view;
        }
    }

    public void updateDrawer() {
        //Log.dMainActivity.TAG, "updateDrawer is being updated");
        drawerNavigationListAdapter.notifyDataSetChanged();
        // OR
        //mDrawerList.setAdapter(new DrawerNavigationListAdapter());
    }


    // Saeed Mostafa - 02092017 - CallBack to check the permissions [START]
    @Override
    public void  onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode ==  1) {
            if (grantResults.length > 0
//                    && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    ) {
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
                if (fragmentRequestingPermission.equals("FragmentSafetyCheckItems") && grantResults[0] == PackageManager.PERMISSION_GRANTED ) { //Storage & Camera Permission
                    Log.v("RequetPermissionResult:","  FragmentSafetyCheckItems");
                    FragmentSafetyCheckItems fragment= (FragmentSafetyCheckItems) getSupportFragmentManager().findFragmentById(R.id.fragment);
                    fragment.dispatchTakePictureIntent();
                } else if (fragmentRequestingPermission.equals("MainActivitySafetyCheckMenuItem") && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //Storage Permission
                    Log.v("RequetPermissionResult:","  MainActivitySafetyCheckMenuItem");
                    openSafetyCheckFragment();
                }
                return;

            }
        }

    }
    // Saeed Mostafa - 02092017 - CallBack to check the permissions [END]

    public void openSafetyCheckFragment (){
        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "Matics");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        Fragment fragment;
        if (ApplicationPrefs.getInstance(this).getUserProfilePref().isShop()){
            fragment = new FragmentSafetyCheckInitial();
        }else{
            fragment =  new FragmentSafetyCheckReports();
        }
        FragmentManager fragmentManagerSC = getSupportFragmentManager();
        FragmentTransaction ftSC= fragmentManagerSC.beginTransaction();
        ftSC.replace(R.id.fragment, fragment);
        ftSC.addToBackStack("");
        ftSC.commit();
    }
}