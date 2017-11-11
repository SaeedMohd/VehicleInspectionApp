package com.matics;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.observation.region.Region;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.plus.Plus;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.matics.Bluetooth.BluetoothApp;
import com.matics.GCM.GcmBroadcastReceiver;
import com.matics.GCM.GcmRegistration;
import com.matics.Services.ServiceCallbacks;
import com.matics.Services.VideoCallingService;
import com.matics.adapter.DataBaseHelper;
import com.matics.adapter.MainActivityListAdapter;
import com.matics.fragments.FragmentAppointments;
import com.matics.fragments.FragmentConnection;
import com.matics.fragments.FragmentConnectionMain;
import com.matics.fragments.FragmentSafetyCheckInitial;
import com.matics.fragments.FragmentSafetyCheckItems;
import com.matics.fragments.FragmentSafetyCheckReports;
import com.matics.fragments.FragmentSetting;
import com.matics.fragments.FragmentUserVehicles;
import com.matics.fragments.FragmentVehicleAbout;
import com.matics.fragments.FragmentVehicleFacility;
import com.matics.fragments.FragmentVehicleHealth;
import com.matics.fragments.FragmentVehicleSettings;
import com.matics.fragments.FragmentVehicleUser;

import com.matics.fragments.VideoCallFragments.FragmentVideoCall;
import com.matics.fragments.GetOBD2DeviceFragment;
import com.matics.fragments.VideoCallFragments.IncomingCallFragment;
import com.matics.imageloader.ImageLoader;
import com.matics.model.CompetitorModel;
import com.matics.model.UserAccountModel;
import com.matics.model.UserProfileModel;
import com.matics.serverTasks.GenericServerTask;
import com.matics.serverTasks.GetAccountDetailByEmailAndPhoneIDTask;
import com.matics.serverTasks.GetBeaconMessageTask;
import com.matics.serverTasks.GetCompetitorsListTask;
import com.matics.serverTasks.GetShopUserProfileDetails;
import com.matics.serverTasks.GetVehicleDetailsResolvedFromVinTask;
import com.matics.serverTasks.UpdateCompetitorGeoFenceTask;
import com.matics.serverTasks.SendPhoneInfoTask;
import com.matics.Utils.AccelerometerTracker;
import com.matics.Utils.ApplicationPrefs;
import com.matics.Utils.GPSTracker;
import com.matics.Utils.ReadingsManager;
import com.matics.Utils.Utility;
import com.matics.serverTasks.SendPushNotificationTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.R.attr.bitmap;


public class MainActivity extends AppCompatActivity implements LocationListener, ServiceCallbacks {

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
    public static GPSTracker gps;
    public static AccelerometerTracker accelerometerTracker;
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
    public static GPSTracker gpsTracker;

    private AlertDialog changePasswordDialog;

    private ImageView repairShopImage;


    static Bitmap icon;

    public static DrawerLayout mDrawerLayout;
    public DrawerNavigationListAdapter drawerNavigationListAdapter;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;
    private ListView mDrawerList;

    private BeaconManager beaconManager;

    Button btnRepairFacility, btnVehicleHealth, btnRepairHistory;
    ImageView signOutButton;
    ImageView ivUser, ivSetting, ivSearch, ivAbout, ivVehicles;
    public static boolean isBluetoothEnableDenied = false;
    boolean isExitFlagReady = false;
    boolean bound = false;

    ProgressDialog loadingCustomerProgressDialog;

    private VideoCallingService videoCallingService;

    public static String customerNameBeingCalled = "";
    public static int customerMobileUserProfileIDbeingCalled = 0;
    public static String customerEmailToCall = "";

    private boolean isDataSynchedToday = false;

    public static boolean isAppInForeground = false;

    public static boolean isCallRequested = false;

    private String welcomeMessage = "";

    private BeaconRegion region = new BeaconRegion("monitored region", UUID.fromString(MY_BEACON_UUID), 61862, 25906);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main1);

        mContext = this;

        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        BluetoothApp.setApplicationContext(this);

        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }catch (Exception exp){
            exp.printStackTrace();
        }

        if (ApplicationPrefs.getInstance(mContext).getUserProfilePref()!=null) {
            if (ApplicationPrefs.getInstance(mContext).getIsLoggedInPref() && !ApplicationPrefs.getInstance(mContext).getUserProfilePref().isShop()) {
                beaconManager = new BeaconManager(getApplicationContext());

                // add this below:
                beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                    @Override
                    public void onServiceReady() {
                        beaconManager.startMonitoring(region);
                    }
                });

                beaconManager.setMonitoringListener(new BeaconManager.BeaconMonitoringListener() {
                    @Override
                    public void onEnteredRegion(BeaconRegion beaconRegion, List<Beacon> list) {
                        try {
                            new GetBeaconMessageTask("" + ApplicationPrefs.getInstance(mContext).getUserProfilePref().getMobileUserProfileId(), beaconRegion.getProximityUUID() + ":" + beaconRegion.getMajor() + ":" + beaconRegion.getMinor()) {
                                @Override
                                public void onTaskCompleted(String result) {
                                    showNotification("", result);
                                }
                            }.execute();


                            if (ApplicationPrefs.getInstance(mContext).getShopUserProfile() != null && ApplicationPrefs.getInstance(mContext).getUserProfilePref() != null) {
                                new SendPushNotificationTask("" + ApplicationPrefs.getInstance(mContext).getShopUserProfile().getMobileUserProfileId(),
                                        ApplicationPrefs.getInstance(mContext).getUserProfilePref().getFirstName() + " " + ApplicationPrefs.getInstance(mContext).getUserProfilePref().getLastName() + " has arrived", "", "Notification", "", "") {

                                    @Override
                                    public void onTaskCompleted(String result) {

                                    }
                                }.execute();
                            }

                        } catch (Exception exp) {

                        }
                    }

                    @Override
                    public void onExitedRegion(BeaconRegion beaconRegion) {
                        // could add an "exit" notification too if you want (-:
                    }
                });
            }
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
        //SAEED
        new GenericServerTask(mContext, mContext.getString(R.string.GetSafetyCheckProgramName), new String[]{"accountId"}, new String[]{""+ApplicationPrefs.getInstance(mContext).getUserProfilePref().getAccountID()}) {
            @Override
            public void onTaskCompleted(String result) {
                if (!result.contains("BAD") && result.replace("{\"GetSafetyCheckProgramNameResult\":\"", "").replace("\"}", "").length()>0) {
                    ApplicationPrefs.getInstance(mContext).setSafetyCheckProgramName(result.replace("{\"GetSafetyCheckProgramNameResult\":\"", "").replace("\"}", ""));
                }else{
                    ApplicationPrefs.getInstance(mContext).setSafetyCheckProgramName("Safety Check");
                }
                    updateDrawer();

            }
        }.execute();


        ApplicationPrefs.getInstance(mContext).clearVehicleProfilePrefs();

        repairShopImage = (ImageView) findViewById(R.id.imagebg);

        gpsTracker = new GPSTracker(mContext);
        Intent intent = new Intent(this, AccelerometerTracker.class);
        startService(intent);

        handler = new Handler();

        initalize();


        new SendPhoneInfoTask().execute();


        if (new Date().getTime() - ApplicationPrefs.getInstance(mContext).getLastSynchDate() > (1000 * 60 * 60 * 24)) {
            new GetAccountDetailByEmailAndPhoneIDTask(mContext, false, ApplicationPrefs.getInstance(mContext).getUserProfilePref().getEmail(), "") {

                @Override
                public void onTaskCompleted(String result) {
                    //Log.dMainActivity.TAG, "synching user data completed");
                    new GetShopUserProfileDetails(mContext, false, ApplicationPrefs.getInstance(mContext).getUserAccountPref().getEmail(), "") {
                        @Override
                        public void onTaskCompleted(String result) {
                            //Log.dMainActivity.TAG, "Synching shop profile completed");
                            ApplicationPrefs.getInstance(con).setLastSynchDate(new Date().getTime());
                            this.checkAccountDetailsRetrievedFromCloud(result);
                        }
                    }.execute();
                }
            }.execute();

        }

        File myFolder = new File(Environment.getExternalStorageDirectory().getPath() + "/Matics");
        if (!myFolder.exists()) {
            myFolder.mkdir();
        }

        icon = BitmapFactory.decodeResource(getResources(), R.drawable.banner);

        ApplicationPrefs.getInstance(this).updateProfiles();

        PHONE_ID = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
        LivePhoneReadings.phoneId = PHONE_ID;

        if (!GcmBroadcastReceiver.isStarted) {
            new GcmRegistration(this);
        }

        if (ApplicationPrefs.getInstance(this).IsFirstRun()) {
            ApplicationPrefs.getInstance(this).setVehicleHealthMessagePref("No Trouble Codes reported.");
            ApplicationPrefs.getInstance(this).setVehicleHealthValuePref(100);

            final AlertDialog termsAndConditionsDialog = new AlertDialog.Builder(this).create();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            final View dialogView = inflater.inflate(R.layout.dialog_terms_and_conditions,
                    null);
            termsAndConditionsDialog.setView(dialogView);
            termsAndConditionsDialog.setTitle("Terms & Conditions");

            Button dialogAgreeButton = (Button) dialogView.findViewById(R.id.dialogAgreeButton);

            dialogAgreeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ApplicationPrefs.getInstance(mContext).setFirstRun(false);
                    termsAndConditionsDialog.dismiss();
                    if (ApplicationPrefs.getInstance(MainActivity.this).getBooleanPref(ApplicationPrefs.IS_PASSWORD_RESET) && ApplicationPrefs.getInstance(MainActivity.this).getEmailUserBeenReset().equals(ApplicationPrefs.getInstance(MainActivity.this).getUserProfilePref().getEmail())) {
                        Log.v(TAG, "email reset: " + ApplicationPrefs.getInstance(con).getEmailUserBeenReset());
                        Log.v(TAG, "user email: " + ApplicationPrefs.getInstance(con).getUserProfilePref().getEmail());
                        showChangePasswordDialog();
                        ApplicationPrefs.getInstance(MainActivity.this).setBooleanPref(ApplicationPrefs.IS_PASSWORD_RESET, false);
                        ApplicationPrefs.getInstance(MainActivity.this).setEmailUserBeenReset("");
                    }
                }
            });

            Button dialogDisAgreeButton = (Button) dialogView.findViewById(R.id.dialogDisAgreeButton);

            dialogDisAgreeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            termsAndConditionsDialog.show();
        } else {
            if (ApplicationPrefs.getInstance(this).getBooleanPref(ApplicationPrefs.IS_PASSWORD_RESET)) {
                showChangePasswordDialog();
                ApplicationPrefs.getInstance(this).setBooleanPref(ApplicationPrefs.IS_PASSWORD_RESET, false);
            }
        }


        ReadingsManager.initializeReadings(mContext);

//		SynchDataWithServerThread.getInstance(mContext).startSynchingThread(mContext);

        Bgimage = (ImageView) findViewById(R.id.imagebg);
//        updateBigImage();

//		serviceIntent = new Intent(MainActivity.this, autoMaticService.class);
//		startService(serviceIntent);

        //BluetoothApp.startConnectionManager();


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        findViewById(R.id.tab_frames).setAlpha((float) 1.0);


        //Log.dMainActivity.TAG, "" + ApplicationPrefs.getInstance(mContext).getIsLoggedInPref());


        if (ApplicationPrefs.getInstance(mContext).getUserAccountPref() != null && ApplicationPrefs.getInstance(mContext).getUserAccountPref().getAccountId() > 0) {
            Fragment fragment = new FragmentVehicleFacility();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.fragment, fragment);
            ft.commit();
        } else {
            Fragment fragment = new FragmentConnectionMain();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.fragment, fragment);
            ft.commit();
        }

        VideoCallingService.getInstance(MainActivity.mContext).loginToVideoCallService();
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

//        btnRepairFacility.setOnClickListener(this);
//        btnRepairHistory.setOnClickListener(this);
//        btnVehicleHealth.setOnClickListener(this);
//        signOutButton.setOnClickListener(this);
//        ivUser.setOnClickListener(this);
//        ivAbout.setOnClickListener(this);

//        ivSetting.setOnClickListener(this);
//        ivVehicles.setOnClickListener(this);
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

    public static void resetConnection() {
        if (in != null) {
            try {
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            in = null;
        }
        if (out != null) {
            try {
                out.close();
            } catch (Exception e) {
            }
            out = null;
        }
        if (sock != null) {
            try {
                sock.close();
            } catch (Exception e) {
            }
            sock = null;
        }
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


        if (gpsTracker == null) {
            gpsTracker = new GPSTracker(mContext);
        }

        try {
            if (!Utility.isAirplaneModeOn(this) && (BluetoothAdapter.getDefaultAdapter() == null && !BluetoothAdapter.getDefaultAdapter().isEnabled()) && !isBluetoothEnableDenied && ApplicationPrefs.getInstance(this).isBluetoothNotificationOn() && !ApplicationPrefs.getInstance(BluetoothApp.context).isBluetoothSnoozeRequired()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, MY_BLUETOOTH_ENABLE_REQUEST_ID);
            }
        } catch (Exception exp) {

        }

        if (ApplicationPrefs.getInstance(BluetoothApp.context).isBluetoothSnoozeRequired()) {
            Fragment fragment = new FragmentVehicleFacility();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.fragment, fragment);
            ft.commit();
            ApplicationPrefs.getInstance(BluetoothApp.context).setBluetoothSnoozeRequired(false);

            AlertDialog.Builder snoozeDialog = new AlertDialog.Builder(this);
            snoozeDialog.setTitle("Snooze Bluetooth Notification");
            //snoozeDialog.setMessage("Disable notification if bluetooth is off:");
            snoozeDialog.setItems(R.array.BT_Snooze, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Calendar now = Calendar.getInstance();
                    switch (which) {
                        case 0:
                            now.add(Calendar.HOUR_OF_DAY, 1);
                            //Log.dMainActivity.TAG, "" + now.getTime());
                            ApplicationPrefs.getInstance(mContext).setEnableNotificationTime(now.getTime().getTime());
                            break;
                        case 1:
                            now.add(Calendar.HOUR_OF_DAY, 2);
                            //Log.dMainActivity.TAG, "" + now.getTime());
                            ApplicationPrefs.getInstance(mContext).setEnableNotificationTime(now.getTime().getTime());
                            break;
                        case 2:
                            now.add(Calendar.HOUR_OF_DAY, 6);
                            //Log.dMainActivity.TAG, "" + now.getTime());
                            ApplicationPrefs.getInstance(mContext).setEnableNotificationTime(now.getTime().getTime());
                            break;
                        case 3:
                            now.add(Calendar.HOUR_OF_DAY, 12);
                            //Log.dMainActivity.TAG, "" + now.getTime());
                            ApplicationPrefs.getInstance(mContext).setEnableNotificationTime(now.getTime().getTime());
                            break;
                        case 4:
                            now.add(Calendar.DAY_OF_MONTH, 1);
                            //Log.dMainActivity.TAG, "" + now.getTime());
                            ApplicationPrefs.getInstance(mContext).setEnableNotificationTime(now.getTime().getTime());
                            break;
                        case 5:
                            now.add(Calendar.DAY_OF_MONTH, 7);
                            //Log.dMainActivity.TAG, "" + now.getTime());
                            ApplicationPrefs.getInstance(mContext).setEnableNotificationTime(now.getTime().getTime());
                            break;
                        case 6:
                            now.add(Calendar.MONTH, 1);
                            //Log.dMainActivity.TAG, "" + now.getTime());
                            ApplicationPrefs.getInstance(mContext).setEnableNotificationTime(now.getTime().getTime());
                            break;
                        case 7:
                            ApplicationPrefs.getInstance(mContext).setBluetoothNotification(false);
                            //Log.dMainActivity.TAG, "Disable Notification");
                            break;
                    }
                }
            });
            AlertDialog alert = snoozeDialog.create();
            alert.show();

        }

        super.onResume();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
//        menu.add(0, SETTINGS, 0, "Settings");
//        menu.add(0, STOP_SERVICE, 0, "Stop Background Service");
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case SETTINGS:
//                updateConfig();
//                //Log.e("", "setting is clicked");
//                return true;
//        }
        return false;
    }

    private void updateConfig() {
//		Intent configIntent c new Intent(this, SettingActivity.class);
//		startActivity(configIntent);

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


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        Log.v("activity result", "Activityyyyyyyyyyyyyyyyyy resultttttttt " + requestCode);

        if (requestCode == MY_BLUETOOTH_ENABLE_REQUEST_ID) {
            isBluetoothEnableDenied = true;
        }

        if (resultCode == 0) {
            showSnoozeDialog();
        }
    }


    public static Handler getHandler() {
        // TODO Auto-generated method stub
        return handler;
    }

    public static Context getActivity() {
        // TODO Auto-generated method stub
        return con;
    }

    public static void setData(List<String> results_Decode) {
        // TODO Auto-generated method stub
        MainActivityListAdapter mAdapter = new MainActivityListAdapter(con,
                results_Decode);
        FragmentSetting.list_main.setAdapter(mAdapter);
        WeakReference<TextView> text = new WeakReference<TextView>(
                FragmentConnection.Connect);
        text.get().setText("Data : " + results_Decode.toString());

    }

    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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

                    new changePasswordTask().execute(LivePhoneReadings.getMobileUserProfileId(), oldPasswordEditText.getText().toString(), newPasswordEditText.getText().toString());


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

                if (getFragmentManager().findFragmentById(R.id.fragment) instanceof IncomingCallFragment || getFragmentManager().findFragmentById(R.id.fragment) instanceof FragmentVideoCall) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            selectItem(0);
                        }
                    });
                } else {
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
                }
            }
        }
    }

    private void selectItem(int position) {
        // Create a new fragment and specify the planet to show based on position
        mDrawerLayout.closeDrawer(mDrawerList);
        final FragmentManager fragmentManager = getFragmentManager();
        Fragment f = fragmentManager.findFragmentById(R.id.fragment);
        switch (position) {

            case 0:
//                repairShopImage.setVisibility(View.VISIBLE);
                if (ApplicationPrefs.getInstance(mContext).getUserAccountPref() != null && ApplicationPrefs.getInstance(mContext).getUserAccountPref().getAccountId() > 0) {
                    Fragment fragment = new FragmentVehicleFacility();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.replace(R.id.fragment, fragment);
                    ft.commit();
                } else {
                    Fragment fragment = new FragmentConnectionMain();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.replace(R.id.fragment, fragment);
                    ft.commit();
                }
                break;

            case 1:

//                repairShopImage.setVisibility(View.VISIBLE);
                Fragment fragmentVehicleHealth = new FragmentVehicleHealth();
                FragmentManager fragmentManagerVehicleHealth = getFragmentManager();
                FragmentTransaction ftVehicleHealth = fragmentManagerVehicleHealth.beginTransaction();
                ftVehicleHealth.replace(R.id.fragment, fragmentVehicleHealth);
                ftVehicleHealth.commit();
                break;


//            case 2:
////                repairShopImage.setVisibility(View.VISIBLE);
//                Fragment fragmentHistory = new FragmentRepairHistory();
//                FragmentManager fragmentManagerHistory = getFragmentManager();
//                FragmentTransaction ftHistory = fragmentManagerHistory.beginTransaction();
//                ftHistory.replace(R.id.fragment, fragmentHistory);
//                ftHistory.commit();
//                break;

            case 2:
//                repairShopImage.setVisibility(View.GONE);
                Fragment fragmentUserVehicles = new FragmentUserVehicles();
                getFragmentManager().beginTransaction().replace(R.id.fragment, fragmentUserVehicles).commit();
                break;

            case 3:
//                repairShopImage.setVisibility(View.GONE);
                Fragment fragmentUser = new FragmentVehicleUser();
                FragmentManager fragmentManagerUser = getFragmentManager();
                FragmentTransaction ftUser = fragmentManagerUser.beginTransaction();
                ftUser.replace(R.id.fragment, fragmentUser);
                ftUser.commit();
                break;

            case 4:
                Fragment fragmentSetting = new FragmentVehicleSettings();
                FragmentManager fragmentManagerSetting = getFragmentManager();
                FragmentTransaction ftSetting = fragmentManagerSetting.beginTransaction();
                ftSetting.replace(R.id.fragment, fragmentSetting);
                ftSetting.commit();
                break;

            case 5:

                Fragment getOBD2DeviceFragment = new GetOBD2DeviceFragment();
                FragmentManager getOBD2DeviceFragmentManager = getFragmentManager();
                FragmentTransaction ftGetObd2Transaction = getOBD2DeviceFragmentManager.beginTransaction();
                ftGetObd2Transaction.replace(R.id.fragment, getOBD2DeviceFragment);
                ftGetObd2Transaction.commit();
                break;


            case 6:
//                repairShopImage.setVisibility(View.GONE);
                Fragment fragmentAbout = new FragmentVehicleAbout();
                FragmentManager fragmentManagerAbout = getFragmentManager();
                FragmentTransaction ftAbout = fragmentManagerAbout.beginTransaction();
                ftAbout.replace(R.id.fragment, fragmentAbout);
                ftAbout.commit();
                break;

            case 7:
//                repairShopImage.setVisibility(View.GONE);
                Fragment fragmentAppointments= new FragmentAppointments();
                FragmentManager fragmentManagerAppointments = getFragmentManager();
                FragmentTransaction ftAppointments = fragmentManagerAppointments.beginTransaction();
                ftAppointments.replace(R.id.fragment, fragmentAppointments);
                ftAppointments.commit();
                break;

            case 8:
                isCallRequested = true;
                callRequested();

                break;

            case 9:
                // Saeed Mostafa - For noth Shop/User Mode will need Accessing Storage permission - move to new method after checking the permission
                if (ActivityCompat.checkSelfPermission(this,WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    fragmentRequestingPermission= "MainActivitySafetyCheckMenuItem";
                    ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openSafetyCheckFragment();
                }
//                Fragment fragment;
//                if (ApplicationPrefs.getInstance(this).getUserProfilePref().isShop()){
//                    fragment = new FragmentSafetyCheckInitial();
//                }else{
//                    fragment =  new FragmentSafetyCheckReports();
//                }
//                FragmentManager fragmentManagerSC = getFragmentManager();
//                FragmentTransaction ftSC= fragmentManagerSC.beginTransaction();
//                ftSC.replace(R.id.fragment, fragment);
//                ftSC.addToBackStack("");
//                ftSC.commit();
                break;

            case 10:

                AlertDialog.Builder logoutConfirmationDialog = new AlertDialog.Builder(mContext);
                logoutConfirmationDialog.setTitle("Please Confirm");
                logoutConfirmationDialog.setMessage("Are you sure you want to logout?");
                logoutConfirmationDialog.setNegativeButton("Cancel", null);
                logoutConfirmationDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ApplicationPrefs.getInstance(mContext).setAccountDetailPref(null);
                        ApplicationPrefs.getInstance(mContext).setVehicleHealthMessagePref("No TroubleCodes");
                        ApplicationPrefs.getInstance(mContext).setVehicleHealthValuePref(100);
                        ApplicationPrefs.getInstance(mContext).setLastCompetitorUpdate(new Date().getTime() + 50*60*60*1000);

                        ApplicationPrefs.getInstance(mContext).setBooleanPref(getString(R.string.is_user_logged_in_with_email), false);

                        DataBaseHelper db = new DataBaseHelper(mContext);
                        db.clearAccelerationTable();
                        db.clearMaintenanceHistoryTable();
                        db.clearProfileTable();
                        //db.clearReadingsTable();

                        db = null;


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
//                        VideoCallingService.getInstance(mContext).

                        if (beaconManager != null) {
                            beaconManager.stopRanging(region);
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
        // Highlight the selected item, update the title, and close the drawer
        //mDrawerList.setItemChecked(position, true);
        //setTitle(mPlanetTitles[position]);

    }

    class DrawerNavigationListAdapter extends BaseAdapter {
        final LayoutInflater inflater;

        DrawerNavigationListAdapter() {
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return 11;
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
                    drawerIconImage.setImageResource(R.drawable.tool);
                    drawerTextView.setText("Maintenance Facility");
                    break;

                case 1:
                    drawerIconImage.setImageResource(R.drawable.dash);
                    drawerTextView.setText("Dashboard");
                    break;

//                case 2:
//                    drawerIconImage.setImageResource(R.drawable.repair_hisotry_icon);
//                    drawerTextView.setText("Repair History");
//                    break;

                case 2:
                    drawerIconImage.setImageResource(R.drawable.car);
                    drawerTextView.setText("Vehicles");
                    break;

                case 3:
                    drawerIconImage.setImageResource(R.drawable.user);
                    drawerTextView.setText("User Profile");
                    break;

                case 4:
                    drawerIconImage.setImageResource(R.drawable.settings);
                    drawerTextView.setText("Settings");
                    break;

                case 5:
                    drawerIconImage.setImageResource(R.drawable.cart);
                    drawerTextView.setText("Get OBD2 Device");
                    break;

                case 6:
                    drawerIconImage.setImageResource(R.drawable.info);
                    drawerTextView.setText("Info");
                    break;

                case 7:
                    drawerIconImage.setImageResource(R.drawable.appoint);
                    drawerTextView.setText("Appointments");
                    break;

                case 8:
                    drawerIconImage.setImageResource(R.drawable.video);
                    drawerTextView.setText("Video Call");
                    break;

                case 9:
                    drawerIconImage.setImageResource(R.drawable.safety);
                    if (ApplicationPrefs.getInstance(mContext).getSafetyCheckProgramName().length()>0){
                        drawerTextView.setText(ApplicationPrefs.getInstance(mContext).getSafetyCheckProgramName());
                    }else {
                        drawerTextView.setText("Safety Check");
                    }
                    break;

                case 10:
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

    private void showSnoozeDialog() {
        AlertDialog.Builder snoozeDialog = new AlertDialog.Builder(this);
        snoozeDialog.setTitle("Snooze Bluetooth Notification");
        //snoozeDialog.setMessage("Disable notification if bluetooth is off:");
        snoozeDialog.setItems(R.array.BT_Snooze, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Calendar now = Calendar.getInstance();
                switch (which) {
                    case 0:
                        now.add(Calendar.HOUR_OF_DAY, 1);
                        //Log.dMainActivity.TAG, "" + now.getTime());
                        ApplicationPrefs.getInstance(mContext).setEnableNotificationTime(now.getTime().getTime());
                        break;
                    case 1:
                        now.add(Calendar.HOUR_OF_DAY, 2);
                        //Log.dMainActivity.TAG, "" + now.getTime());
                        ApplicationPrefs.getInstance(mContext).setEnableNotificationTime(now.getTime().getTime());
                        break;
                    case 2:
                        now.add(Calendar.HOUR_OF_DAY, 6);
                        //Log.dMainActivity.TAG, "" + now.getTime());
                        ApplicationPrefs.getInstance(mContext).setEnableNotificationTime(now.getTime().getTime());
                        break;
                    case 3:
                        now.add(Calendar.HOUR_OF_DAY, 12);
                        //Log.dMainActivity.TAG, "" + now.getTime());
                        ApplicationPrefs.getInstance(mContext).setEnableNotificationTime(now.getTime().getTime());
                        break;
                    case 4:
                        now.add(Calendar.DAY_OF_MONTH, 1);
                        //Log.dMainActivity.TAG, "" + now.getTime());
                        ApplicationPrefs.getInstance(mContext).setEnableNotificationTime(now.getTime().getTime());
                        break;
                    case 5:
                        now.add(Calendar.DAY_OF_MONTH, 7);
                        //Log.dMainActivity.TAG, "" + now.getTime());
                        ApplicationPrefs.getInstance(mContext).setEnableNotificationTime(now.getTime().getTime());
                        break;
                    case 6:
                        now.add(Calendar.MONTH, 1);
                        //Log.dMainActivity.TAG, "" + now.getTime());
                        ApplicationPrefs.getInstance(mContext).setEnableNotificationTime(now.getTime().getTime());
                        break;
                    case 7:
                        ApplicationPrefs.getInstance(mContext).setBluetoothNotification(false);
                        //Log.dMainActivity.TAG, "Disable Notification");
                        break;
                }
            }
        });
        AlertDialog alert = snoozeDialog.create();
        alert.show();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    private class LoadShopUsersTask extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (loadingCustomerProgressDialog != null) {
                if (!loadingCustomerProgressDialog.isShowing()) {
                    loadingCustomerProgressDialog = new ProgressDialog(MainActivity.mContext);
                    loadingCustomerProgressDialog.setIndeterminate(true);
                    loadingCustomerProgressDialog.setMessage("Loading Customers");
                    loadingCustomerProgressDialog.show();
                } else {
                    loadingCustomerProgressDialog.show();
                }
            }

        }

        @Override
        protected String doInBackground(String... params) {
            ContentValues values = new ContentValues();
            values.put("AccountID", ApplicationPrefs.getInstance(getActivity()).getUserProfilePref().getAccountID());

            //Log.dMainActivity.TAG, "calling url now");
            return Utility.postRequest(getString(R.string.getShopCustomersURL), values);

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (loadingCustomerProgressDialog != null) {
                loadingCustomerProgressDialog.dismiss();
            }

            //Log.dMainActivity.TAG, s);

            if (s.contains("IsSuccess\":true")) {
                try {
                    JSONObject jObject = new JSONObject(s.toString());
                    JSONObject profileResult = jObject
                            .getJSONObject("ShopUsersByAccountIDResult");

                    JSONArray usersOfShopResult = profileResult
                            .getJSONArray("ShopUserProfiles");

                    //Log.dMainActivity.TAG, usersOfShopResult.toString());

                    final ArrayList<UserProfileModel> userProfileModels = new Gson().fromJson(usersOfShopResult.toString(), new TypeToken<ArrayList<UserProfileModel>>() {
                    }.getType());
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.mContext);
                    alert.setTitle("Select User to Call");
                    String[] emails = new String[userProfileModels.size()];
                    String[] customerNames = new String[userProfileModels.size()];
                    for (int x = 0; x < userProfileModels.size(); x++) {
                        emails[x] = userProfileModels.get(x).getEmail();
                        customerNames[x] = userProfileModels.get(x).getFirstName() + " " + userProfileModels.get(x).getLastName();
                    }
                    final String[] finalEmails = emails;
                    alert.setItems(customerNames, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.customerNameBeingCalled = userProfileModels.get(which).getFirstName() + " " + userProfileModels.get(which).getLastName();
                            MainActivity.customerMobileUserProfileIDbeingCalled = userProfileModels.get(which).getMobileUserProfileId();
                            MainActivity.customerEmailToCall = finalEmails[which];

                            Fragment fragmentVideoCall = new FragmentVideoCall();
                            FragmentManager fragmentVideoCallManager = getFragmentManager();
                            FragmentTransaction ftVideo = fragmentVideoCallManager.beginTransaction();
                            ftVideo.replace(R.id.fragment, fragmentVideoCall);
                            ftVideo.addToBackStack("");
                            ftVideo.commit();

                        }
                    });
                    alert.create().show();

                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                    //Log.dMainActivity.TAG, jsonException.getMessage());
                }
            } else {
                Toast.makeText(MainActivity.mContext, "Error retrieving Customers List, please check internet connection and try again", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void callRequested() {
        if (VideoCallingService.getInstance(MainActivity.mContext).isConnected()) {
            //Log.dMainActivity.TAG, "Let's video call");
            isCallRequested = false;
            VideoCallingService.getInstance(mContext).isIncomingCall = false;

            if (ApplicationPrefs.getInstance(MainActivity.mContext).getUserProfilePref().isShop()) {
                //Log.dMainActivity.TAG, "Let's load customers");
                new LoadShopUsersTask().execute();
            } else {
                if (ApplicationPrefs.getInstance(MainActivity.mContext).getUserProfilePref().getAccountID() > 0) {

                    MainActivity.customerMobileUserProfileIDbeingCalled = ApplicationPrefs.getInstance(MainActivity.mContext).getShopUserProfile().getMobileUserProfileId();
                    MainActivity.customerNameBeingCalled = ApplicationPrefs.getInstance(MainActivity.mContext).getShopUserProfile().getFirstName() + " " + ApplicationPrefs.getInstance(MainActivity.mContext).getShopUserProfile().getLastName();
                    MainActivity.customerEmailToCall = ApplicationPrefs.getInstance(MainActivity.mContext).getShopUserProfile().getEmail();

                    Fragment fragmentVideoCall = new FragmentVideoCall();
                    FragmentManager fragmentVideoCallManager = getFragmentManager();
                    FragmentTransaction ftVideo = fragmentVideoCallManager.beginTransaction();
                    ftVideo.replace(R.id.fragment, fragmentVideoCall);
                    ftVideo.addToBackStack("");
                    ftVideo.commit();
                } else {
                    Utility.showAlertDialog(MainActivity.mContext, "", "You should connect to a shop to be able to call it");
                }
            }
        } else {
            if (ApplicationPrefs.getInstance(MainActivity.mContext).getUserProfilePref().isShop()) {
                loadingCustomerProgressDialog = new ProgressDialog(MainActivity.mContext);
                loadingCustomerProgressDialog.setIndeterminate(true);
                loadingCustomerProgressDialog.setMessage("Loading Customers");
                loadingCustomerProgressDialog.show();
                VideoCallingService.getInstance(MainActivity.mContext).loginToVideoCallService();
            } else {
                Fragment fragmentVideoCall = new FragmentVideoCall();
                FragmentManager fragmentVideoCallManager = getFragmentManager();
                FragmentTransaction ftVideo = fragmentVideoCallManager.beginTransaction();
                ftVideo.replace(R.id.fragment, fragmentVideoCall);
                ftVideo.addToBackStack("");
                ftVideo.commit();
            }
        }
    }


    public void showNotification(String title, String message) {
        Intent notifyIntent = new Intent(this, MainActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[]{notifyIntent}, PendingIntent.FLAG_UPDATE_CURRENT);
//        NotificationCompat notification = new Notification.Builder(this)
//                .setSmallIcon(android.R.drawable.ic_dialog_info)
//                .setContentTitle(title)
//                .setContentText(message)
//                .setAutoCancel(false)
//                .setContentIntent(pendingIntent)
//                .build();

        NotificationCompat.Builder myNotification = new NotificationCompat.Builder(this)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentIntent(pendingIntent)
                .setContentTitle("title")
                .setContentText(message)
                .setAutoCancel(false)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message));

//        notification. |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, myNotification.build());
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
                if (fragmentRequestingPermission.equals("VehicleFacility_ALL")) { //Location Permission
                    Log.v("RequetPermissionResult:","  VehicleFacility_ALL");
                    FragmentVehicleFacility fragment= (FragmentVehicleFacility) getFragmentManager().findFragmentById(R.id.fragment);
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
//                        fragment.handleMap();
                    if (grantResults[1] == PackageManager.PERMISSION_GRANTED)
                    fragment.handlebanner();
                } else if (fragmentRequestingPermission.equals("VehicleFacility_LOCATION") && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //Location Permission
                    Log.v("RequetPermissionResult:","  VehicleFacility_LOCATION");
                    FragmentVehicleFacility fragment= (FragmentVehicleFacility) getFragmentManager().findFragmentById(R.id.fragment);
//                    fragment.handleMap();
                } else if (fragmentRequestingPermission.equals("VehicleFacility_STORAGE") && grantResults[0] == PackageManager.PERMISSION_GRANTED) { //Location Permission
                    Log.v("RequetPermissionResult:","  VehicleFacility_STORAGE");
                    FragmentVehicleFacility fragment= (FragmentVehicleFacility) getFragmentManager().findFragmentById(R.id.fragment);
                    fragment.handlebanner();
                } else if (fragmentRequestingPermission.equals("FragmentSafetyCheckItems") && grantResults[0] == PackageManager.PERMISSION_GRANTED ) { //Storage & Camera Permission
                    Log.v("RequetPermissionResult:","  FragmentSafetyCheckItems");
                    FragmentSafetyCheckItems fragment= (FragmentSafetyCheckItems) getFragmentManager().findFragmentById(R.id.fragment);
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
        FragmentManager fragmentManagerSC = getFragmentManager();
        FragmentTransaction ftSC= fragmentManagerSC.beginTransaction();
        ftSC.replace(R.id.fragment, fragment);
        ftSC.addToBackStack("");
        ftSC.commit();
    }
}