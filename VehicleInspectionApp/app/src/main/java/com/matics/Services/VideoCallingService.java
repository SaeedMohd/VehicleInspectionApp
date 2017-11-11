package com.matics.Services;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.matics.Bluetooth.BluetoothApp;
import com.matics.MainActivity;
import com.matics.R;
import com.matics.Utils.ApplicationPrefs;
import com.matics.Utils.Toaster;
import com.matics.Utils.Utility;
import com.matics.fragments.VideoCallFragments.FragmentVideoCall;
import com.matics.fragments.VideoCallFragments.IncomingCallFragment;
import com.matics.model.UserProfileModel;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBSignaling;
import com.quickblox.chat.QBWebRTCSignaling;
import com.quickblox.chat.listeners.QBVideoChatSignalingManagerListener;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBSettings;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.AppRTCAudioManager;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCConfig;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;
import com.quickblox.videochat.webrtc.QBSignalingSpec;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientSessionCallbacks;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientVideoTracksCallbacks;
import com.quickblox.videochat.webrtc.callbacks.QBRTCSessionConnectionCallbacks;
import com.quickblox.videochat.webrtc.callbacks.QBRTCSignalingCallback;
import com.quickblox.videochat.webrtc.exception.QBRTCException;
import com.quickblox.videochat.webrtc.exception.QBRTCSignalException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.CameraVideoCapturer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.matics.MainActivity.TAG;


public class VideoCallingService extends Service implements QBRTCClientSessionCallbacks, QBRTCSessionConnectionCallbacks, QBRTCSignalingCallback {

    public static final String APP_ID = "37950";
    public static final String AUTH_KEY = "wA7By6LqUzWFnrE";
    public static final String AUTH_SECRET = "5-cV7pRd-t73fEJ";
    public static final String ACCOUNT_KEY = "bk42gqT5SCqr6y1Tuzxp";

    FragmentVideoCall fragmentVideoCall;
    IncomingCallFragment incomingCallFragment;

    public boolean isIncomingCall = false;
    private boolean isQuickbloxConnected = false;


    private static Context context;
    private static VideoCallingService videoCallingService;

    private OnChangeDynamicToggle onChangeDynamicCallback;


    private AppRTCAudioManager audioManager;

    private boolean isReadyForCalls = false;
    static QBChatService chatService;
    private boolean isSignalingAdded = false;
    public static List<Integer> opponents = new ArrayList<>();

    private boolean headsetPlugged;
    private boolean previousDeviceEarPiece;
    private boolean showToastAfterHeadsetPlugged = true;

    public static QBRTCTypes.QBConferenceType qbConferenceType = QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO;

    public QBRTCClient rtcClient;
    private QBRTCSession currentSession;
    private boolean callStarted;

    public static VideoCallingService getInstance(Context _context) {
        context = _context;
        if (videoCallingService != null) {
            return videoCallingService;
        }

        videoCallingService = new VideoCallingService();
        return videoCallingService;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public boolean isConnected() {
        if (chatService != null) {

            return chatService.isLoggedIn();
        } else {
            return false;
        }
    }


    public void loginToVideoCallService() {
        if (ApplicationPrefs.getInstance(MainActivity.mContext).getUserProfilePref().getEmail() != null) {
            processLoginlogin(ApplicationPrefs.getInstance(MainActivity.mContext).getUserProfilePref().getEmail());
        }
    }

    public void initializeVideoCallFragmentVariable(FragmentVideoCall fragmentVideoCall){
        this.fragmentVideoCall = fragmentVideoCall;
    }

    public void initializeIncomingCallFragment(IncomingCallFragment incomingCallFragment){
        this.incomingCallFragment= incomingCallFragment;
    }

    public void opponentVideoEnabled(){
        fragmentVideoCall.showRemoteVideo();
    }

    public void opponentVideoDisabled(){
        fragmentVideoCall.hideRemoteVideo();
    }


    private void processLoginlogin(String login) {
        QBSettings.getInstance().init(BluetoothApp.context, APP_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);

        final QBUser user = new QBUser(login, "VHM" + login);


        QBAuth.createSessionByEmail(login, "VHM" + login).performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession session, Bundle bundle) {

                user.setId(session.getUserId());

                chatService = QBChatService.getInstance();


                chatService.login(user, new QBEntityCallback<QBUser>() {

                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        //Log.dTAG, "Login successful");
                        initQBRTCClient();

                    }

                    @Override
                    public void onError(QBResponseException errors) {

                        if (errors.getMessage().contains("Connection failed") || errors.getMessage().contains("No response received within reply timeout")) {
                            loginToVideoCallService();
                        }

                        if (errors.getMessage().contains("You have already logged in chat")) {
                            initQBRTCClient();
                        }

                        MainActivity.isCallRequested = false;
                        if (context != null) {
                            Toast.makeText(context, "Cannot prepare call, please make sure internet connection is active and try again", Toast.LENGTH_LONG).show();
                        }
                        if(fragmentVideoCall!=null) {
                            fragmentVideoCall.callEnded("Couldn't Connect");
                        }
                        //Log.dTAG, "login error: " + errors.getMessage());
                    }
                });
            }

            @Override
            public void onError(QBResponseException errors) {
                //error
                if(fragmentVideoCall!=null) {
                    fragmentVideoCall.callEnded("Couldn't Connect");
                };
                if (context!= null) {
                // Saeed Mostafa - Replaced mContext with Context to resolve crash "com.matics.Services.VideoCallingService$1.onError"
                    Toast.makeText(context, "Cannot prepare call, please make sure internet connection is active and try again", Toast.LENGTH_LONG).show();
                }
                MainActivity.isCallRequested = false;
                //Log.dTAG, "session error: " + errors.getMessage());
            }
        });
    }

    public void initQBRTCClient() {
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("key", "value");

        rtcClient = QBRTCClient.getInstance(MainActivity.mContext);
        // Add signalling manager
        QBChatService.getInstance().getVideoChatWebRTCSignalingManager().addSignalingManagerListener(new QBVideoChatSignalingManagerListener() {
            @Override
            public void signalingCreated(QBSignaling qbSignaling, boolean createdLocally) {
                if (!createdLocally) {
                    rtcClient.addSignaling((QBWebRTCSignaling) qbSignaling);
                    //Log.dTAG, "Signalling Added, ehstaa");
                    isQuickbloxConnected = true;
                    isSignalingAdded = true;
                } else {
                    //Log.dTAG, "signalling isn't added");
                }
            }
        });


        rtcClient.setCameraErrorHendler(new CameraVideoCapturer.CameraEventsHandler() {
            @Override
            public void onCameraError(String s) {

            }

            @Override
            public void onCameraFreezed(String s) {

            }

            @Override
            public void onCameraOpening(int i) {

            }

            @Override
            public void onFirstFrameAvailable() {

            }

            @Override
            public void onCameraClosed() {

            }
        });


        chatService.startAutoSendPresence(30);



        QBRTCConfig.setMaxOpponentsCount(6);
        QBRTCConfig.setAnswerTimeInterval(60l);
        QBRTCConfig.setDebugEnabled(true);


        // Add activity as callback to RTCClient
        rtcClient.addSessionCallbacksListener(VideoCallingService.this);

        // Start mange QBRTCSessions according to VideoCall parser's callbacks
        rtcClient.prepareToProcessCalls();


        isReadyForCalls = true;

        if (MainActivity.isCallRequested){
            //Log.dTAG,"let's do it ya reggala");
            MainActivity.mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (ApplicationPrefs.getInstance(MainActivity.mContext).getUserProfilePref().isShop()) {
                        ((MainActivity) MainActivity.mContext).callRequested();
                    }else{
                        fragmentVideoCall.prepareCall();
                    }
                }
            });
        }
    }


    public void initCurrentSession(QBRTCSession session) {
        //Log.dTAG, "Initializing Session .....");
        currentSession = session;
        currentSession.addSessionCallbacksListener(this);
        currentSession.addSignalingCallback(this);
    }


    public QBRTCSession getCurrentSession() {
        return currentSession;
    }

    @Override
    public void onReceiveNewSession(QBRTCSession qbrtcSession) {
        if (getCurrentSession() == null) {

            initCurrentSession(qbrtcSession);

            isIncomingCall = true;
            if (MainActivity.isAppInForeground) {


                try {
                    incomingCallFragment = new IncomingCallFragment();
                    FragmentTransaction incomingCall = MainActivity.mContext.getFragmentManager().beginTransaction();
                    incomingCall.add(R.id.fragment, incomingCallFragment);
                    incomingCall.commit();
                } catch (IllegalStateException exp) {
                    exp.printStackTrace();
                    qbrtcSession.rejectCall(null);
                }

            } else {

//                Intent i = new Intent();
//                i.setClass(MainActivity.mContext, MainActivity.class);
//                i.putExtra(MainActivity.INCOMING_CALL, true);
//                MainActivity.mContext.startActivity(i);
//
//                Fragment fragmentVideoCall = new IncomingCallFragment();
//                FragmentTransaction incomingCall = MainActivity.mContext.getFragmentManager().beginTransaction();
//                incomingCall.add(R.id.fragment, fragmentVideoCall).addToBackStack("");
//                incomingCall.commit();

                NotificationManager mNotificationManager = (NotificationManager)
                        MainActivity.mContext.getSystemService(Context.NOTIFICATION_SERVICE);

                Intent resultIntent = new Intent(MainActivity.mContext, NotificationIntentService.class);
                resultIntent.putExtra(MainActivity.INCOMING_CALL, true);
                PendingIntent mainIntent = PendingIntent.getService(MainActivity.mContext, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//
                Intent intent = new Intent(MainActivity.mContext, NotificationIntentService.class);
                intent.putExtra(MainActivity.ANSWER_VIDEO_CALL, true);
                PendingIntent pIntent = PendingIntent.getService(MainActivity.mContext, 19, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                Intent intent2 = new Intent(MainActivity.mContext, NotificationIntentService.class);
                intent2.putExtra(MainActivity.CANCEL_VIDEO_CALL, true);
                PendingIntent pIntent2 = PendingIntent.getService(MainActivity.mContext, 22, intent2, PendingIntent.FLAG_UPDATE_CURRENT);

                Notification myNotification = new NotificationCompat.Builder(MainActivity.mContext)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentIntent(mainIntent)
                        .setContentTitle("Vehicle Health Monitor")
                        .setFullScreenIntent(mainIntent, true)
                        .setPriority(Notification.PRIORITY_HIGH)
                        .setVibrate(new long[20])
                        .setCategory(Notification.CATEGORY_CALL)
                        .setSound(Uri.parse("android.resource://com.matics/raw/ring_ring"))
                        .setContentText(getCurrentSession().getUserInfo().get("callerName") + " is calling")
                        .addAction(R.drawable.notification_answer_call, "Answer", pIntent)
                        .addAction(R.drawable.notification_reject_call, "Decline", pIntent2)
                        .build();
                mNotificationManager.notify(1, myNotification);

            }

        } else {

            qbrtcSession.acceptCall(null);
        }


    }

    @Override
    public void onUserNotAnswer(QBRTCSession qbrtcSession, Integer integer) {
        fragmentVideoCall.callEnded("No Answer");

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.mContext);
        alert.setTitle("Didn't Answer");

        if (ApplicationPrefs.getInstance(MainActivity.mContext).getUserProfilePref().isShop()) {
            alert.setMessage(MainActivity.customerNameBeingCalled +" is not available via the app right now. Would you like to try and call them on their Phone ?");
        } else {
            alert.setMessage(ApplicationPrefs.getInstance(MainActivity.mContext).getUserAccountPref().getAccountFullName() + " is not available via the app right now. Would you like to try and call him on Phone ?");
        }
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.mContext.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel: " + ApplicationPrefs.getInstance(MainActivity.mContext).getUserAccountPref().getPhone())));
            }
        });

        alert.setNegativeButton("No", null);

        alert.show();

    }

    @Override
    public void onCallRejectByUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {
//        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.mContext);
//        alert.setTitle("Customer Busy");
//
//        if (ApplicationPrefs.getInstance(MainActivity.mContext).getUserProfilePref().isShop()) {
//            alert.setMessage("Customer is busy, please call him later");
//        } else {
//            alert.setMessage("Shop is busy, please call him later");
//        }
//        alert.setPositiveButton("OK", null);
//
//        alert.show();
    }

    @Override
    public void onCallAcceptByUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {

    }

    @Override
    public void onReceiveHangUpFromUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {
        if (incomingCallFragment!=null){
            incomingCallFragment.hangupCall();
        }
    }

    @Override
    public void onUserNoActions(QBRTCSession qbrtcSession, Integer integer) {

    }

    @Override
    public void onSessionClosed(QBRTCSession qbrtcSession) {
        //Log.dTAG, "OnSessionClosed");
        releaseCurrentSession();
    }

    @Override
    public void onSessionStartClose(QBRTCSession qbrtcSession) {
        //Log.dTAG, "onSessionStartClose");
        qbrtcSession.removeSessionCallbacksListener(this);
    }

    @Override
    public void onStartConnectToUser(QBRTCSession qbrtcSession, Integer integer) {
    }

    @Override
    public void onConnectedToUser(QBRTCSession qbrtcSession, Integer integer) {
        callStarted = true;

    }

    @Override
    public void onConnectionClosedForUser(QBRTCSession qbrtcSession, Integer integer) {
        //Log.dTAG, "on connection closed");
        callStarted = false;
    }

    @Override
    public void onDisconnectedFromUser(QBRTCSession qbrtcSession, Integer integer) {
        callStarted = false;
    }

    @Override
    public void onDisconnectedTimeoutFromUser(QBRTCSession qbrtcSession, Integer integer) {
        callStarted = false;
    }

    @Override
    public void onConnectionFailedWithUser(QBRTCSession qbrtcSession, Integer integer) {

    }

    @Override
    public void onError(QBRTCSession qbrtcSession, QBRTCException e) {

    }

    @Override
    public void onSuccessSendingPacket(QBSignalingSpec.QBSignalCMD qbSignalCMD, Integer integer) {

    }

    @Override
    public void onErrorSendingPacket(QBSignalingSpec.QBSignalCMD qbSignalCMD, Integer integer, QBRTCSignalException e) {

    }


    public void callAcceptedGoToVideoCall() {
//        MainActivity.mContext.getFragmentManager().popBackStack();
        fragmentVideoCall = new FragmentVideoCall();
        FragmentTransaction ftVideo = MainActivity.mContext.getFragmentManager().beginTransaction();
        ftVideo.add(R.id.fragment, fragmentVideoCall);
        ftVideo.commit();
    }

    public void addVideoTrackCallbacksListener(QBRTCClientVideoTracksCallbacks videoTracksCallbacks) {
        if (currentSession != null) {
            currentSession.addVideoTrackCallbacksListener(videoTracksCallbacks);
        }
    }

    public void addTCClientConnectionCallback(QBRTCSessionConnectionCallbacks clientConnectionCallbacks) {
        if (currentSession != null) {
            currentSession.addSessionCallbacksListener(clientConnectionCallbacks);
        }
    }

    public void removeVideoTrackCallbacksListener(QBRTCClientVideoTracksCallbacks videoTracksCallbacks) {
        if (currentSession != null) {
            currentSession.removeVideoTrackCallbacksListener(videoTracksCallbacks);
        }
    }

    public void removeTCClientConnectionCallback(QBRTCSessionConnectionCallbacks clientConnectionCallbacks) {
        if (currentSession != null) {
            currentSession.removeSessionCallbacksListener(clientConnectionCallbacks);
        }
    }


    public void releaseCurrentSession() {
        if (currentSession != null) {
            this.currentSession.removeSessionCallbacksListener(this);
            this.currentSession.removeSignalingCallback(this);
            this.currentSession = null;
        }
    }

    public interface QBRTCSessionUserCallback {
        void onUserNotAnswer(QBRTCSession session, Integer userId);

        void onCallRejectByUser(QBRTCSession session, Integer userId, Map<String, String> userInfo);

        void onCallAcceptByUser(QBRTCSession session, Integer userId, Map<String, String> userInfo);

        void onReceiveHangUpFromUser(QBRTCSession session, Integer userId);
    }


    private class LoadShopUsersTask extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.mContext);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Loading Customers");
            progressDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            ContentValues values = new ContentValues();
            values.put("AccountID", ApplicationPrefs.getInstance(MainActivity.mContext).getUserProfilePref().getAccountID());

            //Log.dTAG, "calling url now");
            return Utility.postRequest(getString(R.string.getShopCustomersURL), values);

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();

            //Log.dTAG, s);

            if (s.contains("IsSuccess\":true")) {
                try {
                    JSONObject jObject = new JSONObject(s.toString());
                    JSONObject profileResult = jObject
                            .getJSONObject("ShopUsersByAccountIDResult");

                    JSONArray usersOfShopResult = profileResult
                            .getJSONArray("ShopUserProfiles");

                    //Log.dTAG, usersOfShopResult.toString());

                    final ArrayList<UserProfileModel> userProfileModels = new Gson().fromJson(usersOfShopResult.toString(), new TypeToken<ArrayList<UserProfileModel>>() {
                    }.getType());
                    //Log.dTAG, "We are good here");
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.mContext);
                    alert.setTitle("Select User to Call");
                    String[] emails = new String[userProfileModels.size()];
                    String[] customerNames = new String[userProfileModels.size()];
                    for (int x = 0; x < userProfileModels.size(); x++) {
                        emails[x] = userProfileModels.get(x).getEmail();
                        customerNames[x] = userProfileModels.get(x).getFirstName() +" "+ userProfileModels.get(x).getLastName();
                    }
                    final String[] finalEmails = emails;

                    alert.setItems(customerNames, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.customerNameBeingCalled = userProfileModels.get(which).getFirstName() + " " + userProfileModels.get(which).getLastName();
                            MainActivity.customerMobileUserProfileIDbeingCalled = userProfileModels.get(which).getMobileUserProfileId();
                            MainActivity.customerEmailToCall = finalEmails[which];

                            Fragment fragmentVideoCall = new FragmentVideoCall();
                            FragmentManager fragmentVideoCallManager = MainActivity.mContext.getFragmentManager();
                            FragmentTransaction ftVideo = fragmentVideoCallManager.beginTransaction();
                            ftVideo.replace(R.id.fragment, fragmentVideoCall);
                            ftVideo.addToBackStack("");
                            ftVideo.commit();

                        }
                    });
                    alert.create().show();

                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                    //Log.dTAG, jsonException.getMessage());
                }
            } else {
                Toast.makeText(MainActivity.mContext, "Error retrieving Customers List, please check internet connection and try again", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void callRequested(){
        if (VideoCallingService.getInstance(MainActivity.mContext).isConnected()) {
            //Log.dTAG, "Let's video call");
            //isCallRequested = false;
            VideoCallingService.getInstance(MainActivity.mContext).isIncomingCall = false;

            if (ApplicationPrefs.getInstance(MainActivity.mContext).getUserProfilePref().isShop()) {
                //Log.dTAG, "Let's load customers");
                new LoadShopUsersTask().execute();
            } else {
                if (ApplicationPrefs.getInstance(MainActivity.mContext).getUserProfilePref().getAccountID() > 0) {

                    MainActivity.customerMobileUserProfileIDbeingCalled = ApplicationPrefs.getInstance(MainActivity.mContext).getShopUserProfile().getMobileUserProfileId();
                    MainActivity.customerNameBeingCalled = ApplicationPrefs.getInstance(MainActivity.mContext).getUserProfilePref().getFirstName() + " " + ApplicationPrefs.getInstance(MainActivity.mContext).getUserProfilePref().getLastName();
                    MainActivity.customerEmailToCall = ApplicationPrefs.getInstance(MainActivity.mContext).getUserAccountPref().getEmail();

                    Fragment fragmentVideoCall = new FragmentVideoCall();
                    FragmentManager fragmentVideoCallManager = MainActivity.mContext.getFragmentManager();
                    FragmentTransaction ftVideo = fragmentVideoCallManager.beginTransaction();
                    ftVideo.replace(R.id.fragment, fragmentVideoCall);
                    ftVideo.addToBackStack("");
                    ftVideo.commit();

                } else {
                    Utility.showAlertDialog(MainActivity.mContext, "", "You should connect to a shop to be able to call it");
                }
            }
        } else {
            VideoCallingService.getInstance(MainActivity.mContext).loginToVideoCallService();
        }
    }

    public void initAudioManager() {
        audioManager = AppRTCAudioManager.create(context, new AppRTCAudioManager.OnAudioManagerStateListener() {
            @Override
            public void onAudioChangedState(AppRTCAudioManager.AudioDevice audioDevice) {
                if (callStarted) {
                    if (audioManager.getSelectedAudioDevice() == AppRTCAudioManager.AudioDevice.EARPIECE) {
                        previousDeviceEarPiece = true;
                    } else if (audioManager.getSelectedAudioDevice() == AppRTCAudioManager.AudioDevice.SPEAKER_PHONE) {
                        previousDeviceEarPiece = false;
                    }
                    if (showToastAfterHeadsetPlugged) {
                        Toaster.shortToast(MainActivity.mContext, "Audio device switched to  " + audioDevice);
                    }
                }
            }
        });


        if (QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO.equals(currentSession.getConferenceType())) {
            audioManager.setDefaultAudioDevice(AppRTCAudioManager.AudioDevice.SPEAKER_PHONE);
            //Log.dTAG, "AppRTCAudioManager.AudioDevice.SPEAKER_PHONE");
        } else {
            audioManager.setDefaultAudioDevice(AppRTCAudioManager.AudioDevice.EARPIECE);
            previousDeviceEarPiece = true;
            //Log.dTAG, "AppRTCAudioManager.AudioDevice.EARPIECE");
        }

        audioManager.setOnWiredHeadsetStateListener(new AppRTCAudioManager.OnWiredHeadsetStateListener() {
            @Override
            public void onWiredHeadsetStateChanged(boolean plugged, boolean hasMicrophone) {
                headsetPlugged = plugged;
                if (callStarted) {
                    Toaster.shortToast(MainActivity.mContext, "Headset " + (plugged ? "plugged" : "unplugged"));
                }
                if (onChangeDynamicCallback != null) {
                    if (!plugged) {
                        showToastAfterHeadsetPlugged = false;
                        if (previousDeviceEarPiece) {
                            setAudioDeviceDelayed(AppRTCAudioManager.AudioDevice.EARPIECE);
                        } else {
                            setAudioDeviceDelayed(AppRTCAudioManager.AudioDevice.SPEAKER_PHONE);
                        }
                    }
                    onChangeDynamicCallback.enableDynamicToggle(plugged, previousDeviceEarPiece);
                }
            }
        });
        audioManager.init();
    }

    private void setAudioDeviceDelayed(final AppRTCAudioManager.AudioDevice audioDevice) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showToastAfterHeadsetPlugged = true;
                audioManager.setAudioDevice(audioDevice);
            }
        }, 500);
    }

    public interface OnChangeDynamicToggle {
        void enableDynamicToggle(boolean plugged, boolean wasEarpiece);
    }

    public void onSwitchAudio() {
        if (audioManager.getSelectedAudioDevice() == AppRTCAudioManager.AudioDevice.WIRED_HEADSET
                || audioManager.getSelectedAudioDevice() == AppRTCAudioManager.AudioDevice.EARPIECE) {
            audioManager.setAudioDevice(AppRTCAudioManager.AudioDevice.SPEAKER_PHONE);
        } else {
            audioManager.setAudioDevice(AppRTCAudioManager.AudioDevice.EARPIECE);
        }
    }

}