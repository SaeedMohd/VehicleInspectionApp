package com.matics.fragments;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.matics.MainActivity;
import com.matics.R;
import com.matics.SecondLogin;
import com.matics.imageloader.Utils;
import com.matics.model.UserAccountModel;
import com.matics.Utils.ApplicationPrefs;
import com.matics.Utils.GPSTracker;

import java.io.File;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.matics.MainActivity.fragmentRequestingPermission;

public class FragmentVehicleFacility extends Fragment implements OnClickListener,ActivityCompat.OnRequestPermissionsResultCallback {


    Bundle savedInstanceState;

    MapView mapView;
    ImageView Bgimage;
    GPSTracker gps;
    boolean mapIsCreated=false;
    double latitude = 0, longitude = 0;
    ImageView ivFacebook, ivTwitter, ivGooglePlus, ivEmail;
    TextView tvAddress;
    Button referAFriend, requestAnAppointMent, requestAQuote;
    private Button Connect;
    Typeface tf;
    public static Boolean DetailFlag = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.savedInstanceState=savedInstanceState;
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//		inflater.inflate(R.menu.menu_facility, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_facility:
                Fragment fragmentUser = new SecondLogin();
                FragmentManager fragmentManagerUser = getFragmentManager();
                FragmentTransaction ftUser = fragmentManagerUser.beginTransaction();
                ftUser.replace(R.id.fragment, fragmentUser);
                ftUser.commit();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_vehicle_facility, container, false);

        //------------initializing variables
        Bgimage = view.findViewById(R.id.imagebg);
        initalize(view, savedInstanceState);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Maintenance Facility");


//        if (ApplicationPrefs.getInstance(getActivity()).getUserProfilePref().isShop())
        if (ActivityCompat.checkSelfPermission(getActivity(),WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
//            fragmentRequestingPermission = "VehicleFacility_STORAGE";
//            ActivityCompat.requestPermissions(getActivity(), new String[]{WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            Utils.setShopImage(getActivity(), Bgimage);
        }



        return view;
    }

//    private void setCurrentMarker() {
//        // TODO Auto-generated method stub
//        try {
//            mapView.clear();
//            final double shoplongitude = Double.parseDouble(ApplicationPrefs.getInstance(getActivity()).getUserAccountPref().getLon());
//            final double shoplatitude = Double.parseDouble(ApplicationPrefs.getInstance(getActivity()).getUserAccountPref().getLat());
//            //Log.dMainActivity.TAG, "" + ApplicationPrefs.getInstance(getActivity()).getUserAccountPref().getLon());
//            //Log.dMainActivity.TAG, "" + ApplicationPrefs.getInstance(getActivity()).getUserAccountPref().getLat());
//            //longitude = -117.102;
//            //latitude = 33.1271;
//            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(shoplatitude, shoplongitude)).zoom(16).build();
//            mapView.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.map_icon);
//
//            mapView.addMarker(new MarkerOptions()
//                    .icon(icon)
//                    .position(new LatLng(shoplatitude, shoplongitude)).title(""));
//            mapView.getUiSettings().setZoomControlsEnabled(false);
//            mapView.getUiSettings().setMyLocationButtonEnabled(false);
//            gps = new GPSTracker(MainActivity.mContext);
//            if (gps.canGetLocation()) {
//                latitude = gps.getLatitude();
//                longitude = gps.getLongitude();
//            } else {
//                gps.showSettingsAlert();
//            }
//            mapView.setOnMarkerClickListener(new OnMarkerClickListener() {
//
//                @Override
//                public boolean onMarkerClick(Marker arg0) {
//                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
//                            Uri.parse("http://maps.google.com/maps?saddr="
//                                    + latitude + ","
//                                    + longitude
////                                    + "&daddr=" + ApplicationPrefs.getInstance(getActivity()).getUserAccountPref().getLat() + "," + ApplicationPrefs.getInstance(getActivity()).getUserAccountPref().getLon()));
//                                    + "&daddr=" + shoplatitude+ "," + shoplongitude));
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
//                    intent.setClassName("com.google.android.apps.maps",
//                            "com.google.android.maps.MapsActivity");
//                    startActivity(intent);
//
//                    return false;
//                    // TODO Auto-generated method stub
//                }
//            });
//
//            mapView.setOnMapClickListener(new OnMapClickListener() {
//                @Override
//                public void onMapClick(LatLng arg0) {
//                    // TODO Auto-generated method stub
//                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
//                            Uri.parse("http://maps.google.com/maps?saddr="
//                                    + latitude + "," + longitude));
//
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
//                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
//                    startActivity(intent);
//                }
//            });
//        } catch (Exception e) {
//            // TODO: handle exception
//            e.printStackTrace();
//        }
//    }

    private void initalize(View view, Bundle savedInstanceState) {
        tf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/gill_sans.ttf");
//		facebook = new Facebook(getString(R.string.Facebook_Key));
        Connect = (Button) view.findViewById(R.id.connectToShopButton);
        ivFacebook = (ImageView) view.findViewById(R.id.facebookIcon);
        ivTwitter = (ImageView) view.findViewById(R.id.twitterIcon);
        ivGooglePlus = (ImageView) view.findViewById(R.id.googleIcon);
        ivEmail = (ImageView) view.findViewById(R.id.mailIcon);

        tvAddress = (TextView) view.findViewById(R.id.addressTextView);
        referAFriend = (Button) view.findViewById(R.id.referFriendButton);
        requestAnAppointMent = (Button) view.findViewById(R.id.bookAnAppointmentButton);
        requestAQuote = (Button) view.findViewById(R.id.requestQuoteButton);

        ivFacebook.setOnClickListener(this);
        ivTwitter.setOnClickListener(this);
        ivGooglePlus.setOnClickListener(this);
        ivEmail.setOnClickListener(this);
        referAFriend.setOnClickListener(this);
        requestAnAppointMent.setOnClickListener(this);
        requestAQuote.setOnClickListener(this);
//        Connect.setOnClickListener(this);
        mapView = (MapView) view.findViewById(R.id.mapView);

        // Saeed Mostafa - 03092017 - Check and get permission first [Start]
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){ //&& ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            // Check Permissions Now
            Log.v("PERMISSION REQUEST: ", "START");
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                fragmentRequestingPermission = "VehicleFacility_ALL";
                ActivityCompat.requestPermissions(getActivity(), new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 1);
            } else if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                fragmentRequestingPermission = "VehicleFacility_LOCATION";
                ActivityCompat.requestPermissions(getActivity(), new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, 1);
                handlebanner();
            } else if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                fragmentRequestingPermission = "VehicleFacility_STORAGE";
                ActivityCompat.requestPermissions(getActivity(), new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }, 1);
                handleMap();
            }
        } else {
            handleMap();
            handlebanner();
        }
        // Saeed Mostafa - 03092017 - Check and get permission first [End]
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
//		CALL FACEBOOK
            case R.id.facebookIcon:
                try {
                    Intent fb_intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ApplicationPrefs.getInstance(getActivity()).getUserAccountPref().getFacebookUrl()));
                    startActivity(fb_intent);
                } catch (Exception e) {
                    // TODO: handle exception
                    Toast ta = Toast.makeText(getActivity(), "There is no Facebook Account in Database", Toast.LENGTH_LONG);
                    ta.show();
                }

//			if(facebook.isSessionValid()){
//				postToWall();
//			}
//			else{
//				loginToFacebook();
//			}
                break;
//		CALL TWITTER
            case R.id.twitterIcon:
                try {
                    Intent tw_intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ApplicationPrefs.getInstance(getActivity()).getUserAccountPref().getTwitterURL()));
                    startActivity(tw_intent);
                } catch (Exception e) {
                    Toast ta = Toast.makeText(getActivity(), "There is no Twitter Account in Database", Toast.LENGTH_LONG);
                    ta.show();
                }
                break;
//		CALL GOOGLE PLUS
            case R.id.googleIcon:

                break;
//		CALL EMAIL
            case R.id.mailIcon:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
                i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
                i.putExtra(Intent.EXTRA_TEXT, "body of email");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.referFriendButton:
                try {
                    //Log.dMainActivity.TAG, ApplicationPrefs.getInstance(getActivity()).getUserAccountPref().getReferFriendURL());
                    Intent referAFriend_intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ApplicationPrefs.getInstance(getActivity()).getUserAccountPref().getReferFriendURL()));
                    startActivity(referAFriend_intent);
                } catch (Exception e) {
                    Toast ta = Toast.makeText(getActivity(), "No referral URL", Toast.LENGTH_LONG);
                    ta.show();
                }
                break;

            case R.id.bookAnAppointmentButton:
                try {
                    Intent requestAnAppointment_intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ApplicationPrefs.getInstance(getActivity()).getUserAccountPref().getRequestAnAppointmentURL()));
                    startActivity(requestAnAppointment_intent);
                } catch (Exception e) {
                    Toast ta = Toast.makeText(getActivity(), "No request appointment URL", Toast.LENGTH_LONG);
                    ta.show();
                }
                break;

            case R.id.requestQuoteButton:
                try {
                    //Log.dMainActivity.TAG, ApplicationPrefs.getInstance(getActivity()).getUserAccountPref().getRequestAQuote());
                    Intent requestAQuote_intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ApplicationPrefs.getInstance(getActivity()).getUserAccountPref().getRequestAQuote()));
                    startActivity(requestAQuote_intent);
                } catch (Exception e) {
                    Toast ta = Toast.makeText(getActivity(), "No request a quote URL", Toast.LENGTH_LONG);
                    ta.show();
                }
                break;
//		USE FOR LOGIN INTO STORE
            //TODO
//            case R.id.ButtonConect:
//                Intent intent = new Intent(getActivity(), SecondLogin.class);
//                startActivity(intent);
//                break;

            default:
                break;
        }
    }


    /**
     * Function to login into facebook
     */
//	public void loginToFacebook() {
//
//		mPrefs = getPreferences(MODE_PRIVATE);
//		String access_token = mPrefs.getString("access_token", null);
//		long expires = mPrefs.getLong("access_expires", 0);
//
//		if (access_token != null) {
//			facebook.setAccessToken(access_token);
//			
//			btnFbLogin.setVisibility(View.INVISIBLE);
//			
//			// Making get profile button visible
//			btnFbGetProfile.setVisibility(View.VISIBLE);
//
//			// Making post to wall visible
//			btnPostToWall.setVisibility(View.VISIBLE);
//
//			// Making show access tokens button visible
//			btnShowAccessTokens.setVisibility(View.VISIBLE);
//
//			//Log.d"FB Sessions", "" + facebook.isSessionValid());
//		}
//
//		if (expires != 0) {
//			facebook.setAccessExpires(expires);
//		}
//
//		if (!facebook.isSessionValid()) {
//			facebook.authorize(this,
//					new String[] { "email", "publish_stream" },
//					new DialogListener() {
//
//						@Override
//						public void onCancel() {
//							// Function to handle cancel event
//						}
//
//						@Override
//						public void onComplete(Bundle values) {
//							// Function to handle complete event
//							// Edit Preferences and update facebook acess_token
//							SharedPreferences.Editor editor = mPrefs.edit();
//							editor.putString("access_token",
//									facebook.getAccessToken());
//							editor.putLong("access_expires",
//									facebook.getAccessExpires());
//							editor.commit();
//
//							// Making Login button invisible
//							btnFbLogin.setVisibility(View.INVISIBLE);
//
//							// Making logout Button visible
//							btnFbGetProfile.setVisibility(View.VISIBLE);
//
//							// Making post to wall visible
//							btnPostToWall.setVisibility(View.VISIBLE);
//
//							// Making show access tokens button visible
//							btnShowAccessTokens.setVisibility(View.VISIBLE);
//						}
//
//						@Override
//						public void onError(DialogError error) {
//							// Function to handle error
//
//						}
//
//						@Override
//						public void onFacebookError(FacebookError fberror) {
//							// Function to handle Facebook errors
//
//						}
//
//					});
//		}
//	}
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//		facebook.authorizeCallback(requestCode, resultCode, data);
    }


    /**
     * Get Profile information by making request to Facebook Graph API
     * */
//	public void getProfileInformation() {
//		mAsyncRunner.request("me", new RequestListener() {
//			@Override
//			public void onComplete(String response, Object state) {
//				//Log.d"Profile", response);
//				String json = response;
//				try {
//					// Facebook Profile JSON data
//					JSONObject profile = new JSONObject(json);
//					
//					// getting name of the user
//					final String name = profile.getString("name");
//					
//					// getting email of the user
//					final String email = profile.getString("email");
//					
//					runOnUiThread(new Runnable() {
//
//						@Override
//						public void run() {
//							Toast.makeText(getApplicationContext(), "Name: " + name + "\nEmail: " + email, Toast.LENGTH_LONG).show();
//						}
//
//					});
//
//					
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//			}
//
//			@Override
//			public void onIOException(IOException e, Object state) {
//			}
//
//			@Override
//			public void onFileNotFoundException(FileNotFoundException e,
//					Object state) {
//			}
//
//			@Override
//			public void onMalformedURLException(MalformedURLException e,
//					Object state) {
//			}
//
//			@Override
//			public void onFacebookError(FacebookError e, Object state) {
//			}
//		});
//	}

    /**
     * Function to post to facebook wall
     */
//	public void postToWall() {
//		// post on user's wall.
//		facebook.dialog(this, "feed", new DialogListener() {
//
//			@Override
//			public void onFacebookError(FacebookError e) {
//			}
//
//			@Override
//			public void onError(DialogError e) {
//			}
//
//			@Override
//			public void onComplete(Bundle values) {
//			}
//
//			@Override
//			public void onCancel() {
//			}
//		});
//
//	}
    @Override
    public void onDestroy() {
        if (mapView!=null && mapIsCreated) {
            mapView.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        //Log.dMainActivity.TAG, "opening onResume");
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Maintenance Facility");
        super.onResume();

        if (mapView!=null && mapIsCreated ) {
            mapView.onResume();
        }

        if (ApplicationPrefs.getInstance(getActivity().getApplicationContext()).getUserAccountPref() != null && ApplicationPrefs.getInstance(getActivity().getApplicationContext()).getUserAccountPref().getAccountId() > 0) {
            UserAccountModel userAccountModel = ApplicationPrefs.getInstance(getActivity().getApplicationContext()).getUserAccountPref();
//            Connect.setText("Connected");
//            Connect.setEnabled(false);
//            Connect.setVisibility(View.GONE);
//            tvAccountName.setText(userAccountModel.getAccountFullName());
            tvAddress.setText(userAccountModel.getAddress().trim() + ", " + userAccountModel.getCity().trim() + ", " + userAccountModel.getState().trim() + " " + userAccountModel.getZip().trim());
            ivFacebook.setEnabled(true);
            ivTwitter.setEnabled(true);
            ivGooglePlus.setEnabled(true);
            ivEmail.setEnabled(true);
        } else {
            Connect.setText("Connect");
            Connect.setVisibility(View.VISIBLE);
//			tvAccountName.setText("About Service Automotive Repair Center");
//			tvAddress.setText("854 Metcalf St, \nEscondido, CA 92025\n\n760 738-8718");
            ivFacebook.setEnabled(false);
            ivTwitter.setEnabled(false);
            ivGooglePlus.setEnabled(false);
            ivEmail.setEnabled(false);
        }
    }

    // Saeed Mostafa - 02092017 - Move all Map & location code to new method [Start]

    public void handleMap(){
        Log.v("Method ------------- ","Reached");
        try {
            mapView.onCreate (savedInstanceState);
            mapIsCreated=true;
            MapsInitializer.initialize(getActivity());
            if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity()) == ConnectionResult.SUCCESS) {
                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        LatLng shopLocation = new LatLng(Double.parseDouble(ApplicationPrefs.getInstance(MainActivity.mContext).getUserAccountPref().getLat()), Double.parseDouble(ApplicationPrefs.getInstance(MainActivity.mContext).getUserAccountPref().getLon()));
                        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//                        googleMap.setMyLocationEnabled(true);
                        googleMap.getUiSettings().setZoomControlsEnabled(true);
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(shopLocation, 18));
                        googleMap.addMarker(new MarkerOptions().position(shopLocation).title("Shop"));
//                        fragmentGoogleMap=googleMap;
                    }
                });
            } else {
                System.out.println("Not able to load map!!!");
            }
        } catch (Exception e) {
            System.out.println("Error in setUpMap");
            e.printStackTrace();
        }
//        mapView.onResume();

    }

    public void handlebanner(){
        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "Matics");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        Utils.setShopImage(getActivity(), Bgimage);
    }

    // Saeed Mostafa - 02092017 - Move all Map & location code to new method [End]


}
