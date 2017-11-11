package com.matics.fragments.VideoCallFragments;

import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.matics.MainActivity;
import com.matics.R;
import com.matics.Utils.ApplicationPrefs;
import com.matics.Utils.CameraUtils;
import com.matics.Utils.RingtonePlayer;
import com.matics.Services.VideoCallingService;
import com.matics.imageloader.Utils;
import com.matics.serverTasks.SendPushNotificationTask;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBMediaStreamManager;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientVideoTracksCallbacks;
import com.quickblox.videochat.webrtc.callbacks.QBRTCSessionConnectionCallbacks;
import com.quickblox.videochat.webrtc.exception.QBRTCException;
import com.quickblox.videochat.webrtc.view.QBRTCVideoTrack;
import com.quickblox.videochat.webrtc.view.RTCGLVideoView;


import org.webrtc.VideoRenderer;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FragmentVideoCall extends Fragment implements QBRTCSessionConnectionCallbacks, QBRTCClientVideoTracksCallbacks, VideoCallingService.QBRTCSessionUserCallback {

    private View view;


    private QBMediaStreamManager mediaStreamManager;
    private QBChatService chatService;
    private IntentFilter intentFilter;
//    private AudioStreamReceiver audioStreamReceiver;

    private boolean isCallStarted = false;


    private boolean isRemoteVideoEnabled = false;
    private boolean isLocalVideoEnabled = false;

    QBRTCSession currentSession;

    private boolean isSpeakerOff = true;

    static final String APP_ID = "37950";
    static final String AUTH_KEY = "wA7By6LqUzWFnrE";
    static final String AUTH_SECRET = "5-cV7pRd-t73fEJ";
    static final String ACCOUNT_KEY = "bk42gqT5SCqr6y1Tuzxp";

    private VideoCallingService.QBRTCSessionUserCallback sessionUserCallback;

    private RingtonePlayer ringtonePlayer, busyTonePlayer;

    private RTCGLVideoView opponentVideo, localVideo;
    private Button hangupCallButton, switchCameraButton, videoToggleButton, audioToggleButton, initiateCallButton, initialHangupCallButton, audioSourceSwitchButton;
    private ImageView profilePhotoImageView;

    private TextView callingStatusTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_video_call, container, false);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle(ApplicationPrefs.getInstance(getActivity()).getUserAccountPref().getAccountFullName());

        VideoCallingService.getInstance(getActivity()).initializeVideoCallFragmentVariable(FragmentVideoCall.this);

        intentFilter = new IntentFilter();
        intentFilter.addAction(AudioManager.ACTION_HEADSET_PLUG);
        intentFilter.addAction(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED);
//        audioStreamReceiver = new AudioStreamReceiver();
//        getActivity().registerReceiver(audioStreamReceiver, intentFilter);


        ringtonePlayer = new RingtonePlayer(getActivity(), R.raw.beep);
        busyTonePlayer = new RingtonePlayer(getActivity(), R.raw.busy);

        opponentVideo = (RTCGLVideoView) view.findViewById(R.id.opponentView);
        localVideo = (RTCGLVideoView) view.findViewById(R.id.localView);

        hangupCallButton = (Button) view.findViewById(R.id.hangupCallButton);

        hangupCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveVideoCall();
            }
        });


        initiateCallButton = (Button) view.findViewById(R.id.initiateCallButton);
        initiateCallButton.setEnabled(false);


        initiateCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.customerNameBeingCalled.trim().length()>0) {
                    callingStatusTextView.setText("Calling " + MainActivity.customerNameBeingCalled);
                }else{
                    callingStatusTextView.setText("Calling " + MainActivity.customerEmailToCall);
                }
                HashMap<String, String> userInfoMap = new HashMap<String, String>();
                if(ApplicationPrefs.getInstance(getActivity()).getUserProfilePref().isShop()){
                    userInfoMap.put("callerName", ApplicationPrefs.getInstance(getActivity()).getUserAccountPref().getAccountFullName());
                }else{
                    userInfoMap.put("callerName", ApplicationPrefs.getInstance(getActivity()).getUserProfilePref().getFirstName() + " " + ApplicationPrefs.getInstance(getActivity()).getUserProfilePref().getLastName());
                }
                userInfoMap.put("callerMobileID", "" + ApplicationPrefs.getInstance(getActivity()).getUserProfilePref().getMobileUserProfileId());
                userInfoMap.put("calledMobileID", "" + MainActivity.customerMobileUserProfileIDbeingCalled);
                hangupCallButton.setVisibility(View.VISIBLE);
                initiateCallButton.setVisibility(View.INVISIBLE);
                initialHangupCallButton.setVisibility(View.INVISIBLE);
                currentSession.startCall(userInfoMap);
                ringtonePlayer.play(true);
            }
        });

        initialHangupCallButton = (Button) view.findViewById(R.id.initialHangupButton);
        initialHangupCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveVideoCall();
            }
        });

        switchCameraButton = (Button) view.findViewById(R.id.cameraSwitchButton);

        switchCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCamera();
            }
        });


        videoToggleButton = (Button) view.findViewById(R.id.videoToggleButton);
        videoToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCamera();
            }
        });

        audioToggleButton = (Button) view.findViewById(R.id.audioToggleButton);
        audioToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleAudio();
            }
        });


        audioSourceSwitchButton = (Button) view.findViewById(R.id.switchAudioSourceButton);
        audioSourceSwitchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switchAudio();
            }
        });

        profilePhotoImageView = (ImageView) view.findViewById(R.id.profilePhotoImageView);

        Utils.setShopImageBackground(getActivity(), profilePhotoImageView);

        callingStatusTextView = (TextView) view.findViewById(R.id.callingStatusTextView);

        currentSession = VideoCallingService.getInstance(MainActivity.mContext).getCurrentSession();

        //Log.dMainActivity.TAG, "current session = " + currentSession);

        if (VideoCallingService.getInstance(getActivity()).isIncomingCall) {
            callingStatusTextView.setText("Connecting");
            if (currentSession != null) {
                currentSession.acceptCall(currentSession.getUserInfo());
                initSession();
                VideoCallingService.getInstance(getActivity()).initAudioManager();
            } else {
                callEnded("Caller Hanged Up");
            }

        } else {
            prepareCall();
        }
        return view;
    }

    private void initSession() {
//        VideoCallingService.getInstance(getActivity()).addTCClientConnectionCallback(this);
//        VideoCallingService.getInstance(getActivity()).addVideoTrackCallbacksListener(this);

        currentSession.addSessionCallbacksListener(this);
        currentSession.addVideoTrackCallbacksListener(this);

    }

    @Override
    public void onUserNotAnswer(QBRTCSession qbrtcSession, Integer integer) {
        callEnded("No Answer");

        if (ApplicationPrefs.getInstance(getActivity()).getUserProfilePref().isShop()) {
            new SendPushNotificationTask(""+MainActivity.customerMobileUserProfileIDbeingCalled, "You missed a call from your shop", ApplicationPrefs.getInstance(getActivity()).getUserAccountPref().getPhotoURL(), "Notification", "", ""){

                @Override
                public void onTaskCompleted(String result) {

                }
            }.execute();
        }else{
            new SendPushNotificationTask(""+MainActivity.customerMobileUserProfileIDbeingCalled, "You missed a call from "+ApplicationPrefs.getInstance(getActivity()).getUserProfilePref().getFirstName() + "", ApplicationPrefs.getInstance(getActivity()).getUserAccountPref().getPhotoURL(), "Notification", "", ""){

                @Override
                public void onTaskCompleted(String result) {

                }
            }.execute();
        }

        busyTonePlayer.play(false);
        //Log.dMainActivity.TAG, "user didn't answer");

    }

    @Override
    public void onCallRejectByUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {
        callEnded("Busy");
        busyTonePlayer.play(false);
    }

    @Override
    public void onCallAcceptByUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {
        ringtonePlayer.stop();

    }

    @Override
    public void onReceiveHangUpFromUser(QBRTCSession qbrtcSession, Integer integer) {
        callEnded("Call Ended");
        busyTonePlayer.play(false);
    }


    @Override
    public void onStartConnectToUser(QBRTCSession qbrtcSession, Integer integer) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initiateCallButton.setVisibility(View.INVISIBLE);
                callingStatusTextView.setText("Connecting");
            }
        });
    }

    @Override
    public void onConnectedToUser(QBRTCSession qbrtcSession, Integer integer) {
        //Log.dMainActivity.TAG, "onconnectedToUser");
        ringtonePlayer.stop();
        if (!isCallStarted) {
            isCallStarted = true;
            showCallUI();
        }

    }

    @Override
    public void onConnectionClosedForUser(QBRTCSession qbrtcSession, Integer integer) {
        //Log.dMainActivity.TAG, "onConnectionClosedForUser");
        callEnded("Call Ended");
    }

    @Override
    public void onDisconnectedFromUser(QBRTCSession qbrtcSession, Integer integer) {
        //Log.dMainActivity.TAG, "onDisconnectedFromUser");
    }

    @Override
    public void onDisconnectedTimeoutFromUser(QBRTCSession qbrtcSession, Integer integer) {
        //callEnded();
    }

    @Override
    public void onConnectionFailedWithUser(QBRTCSession qbrtcSession, Integer integer) {
        //Log.dMainActivity.TAG, "onConnectionFailedWithUser");
        callEnded("Call Dropped");
    }

    @Override
    public void onError(QBRTCSession qbrtcSession, QBRTCException e) {
        //Log.dMainActivity.TAG, "onError");
        //callEnded();
    }

    @Override
    public void onLocalVideoTrackReceive(QBRTCSession qbrtcSession, QBRTCVideoTrack qbrtcVideoTrack) {
        fillVideoView(localVideo, qbrtcVideoTrack, false);
        qbrtcSession.getMediaStreamManager().setVideoEnabled(false);
    }


    @Override
    public void onRemoteVideoTrackReceive(QBRTCSession qbrtcSession, QBRTCVideoTrack qbrtcVideoTrack, Integer integer) {
        fillVideoView(opponentVideo, qbrtcVideoTrack, true);
        qbrtcSession.getMediaStreamManager().setVideoEnabled(false);
    }

    private void fillVideoView(RTCGLVideoView videoView, QBRTCVideoTrack videoTrack, boolean remoteRenderer) {
        videoTrack.addRenderer(new VideoRenderer(remoteRenderer ?
                videoView.obtainVideoRenderer(RTCGLVideoView.RendererSurface.MAIN) :
                videoView.obtainVideoRenderer(RTCGLVideoView.RendererSurface.SECOND)));
    }

    public void callEnded(final String reason) {
        ringtonePlayer.stop();
        busyTonePlayer.stop();
        try {
            currentSession.hangUp(new HashMap<String, String>());
        } catch (NullPointerException exp) {
            exp.printStackTrace();
        }
        VideoCallingService.getInstance(getActivity()).removeTCClientConnectionCallback(this);
        VideoCallingService.getInstance(getActivity()).removeVideoTrackCallbacksListener(this);
        VideoCallingService.getInstance(getActivity()).releaseCurrentSession();

        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
//                        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Maintenance Facility");
                        callingStatusTextView.setText(reason);
                        localVideo.setVisibility(View.INVISIBLE);
                        opponentVideo.setVisibility(View.INVISIBLE);
                        audioSourceSwitchButton.setVisibility(View.INVISIBLE);
                        videoToggleButton.setVisibility(View.INVISIBLE);
                        audioToggleButton.setVisibility(View.INVISIBLE);
                        switchCameraButton.setVisibility(View.INVISIBLE);

                    } catch (NullPointerException exp) {

                    }
                }
            });
        } catch (NullPointerException exp) {
            exp.printStackTrace();
        }

        try {
//            ((MainActivity) getActivity()).onBackPressed();
        } catch (NullPointerException exp) {
            exp.printStackTrace();
        }

    }


    public void leaveVideoCall() {
        ringtonePlayer.stop();
        try {
            currentSession.hangUp(new HashMap<String, String>());
        } catch (NullPointerException exp) {
            exp.printStackTrace();
        }
        VideoCallingService.getInstance(getActivity()).removeTCClientConnectionCallback(this);
        VideoCallingService.getInstance(getActivity()).removeVideoTrackCallbacksListener(this);
        VideoCallingService.getInstance(getActivity()).releaseCurrentSession();

        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Maintenance Facility");
                        callingStatusTextView.setText("Call Ended");
                        hideRemoteVideo();
                        hideLocalVideo();

                    } catch (NullPointerException exp) {

                    }
                }
            });
        } catch (NullPointerException exp) {
            exp.printStackTrace();
        }

        try {
            ((MainActivity) getActivity()).onBackPressed();
        } catch (NullPointerException exp) {
            exp.printStackTrace();
        }
    }

    private void switchCamera() {
        mediaStreamManager = currentSession.getMediaStreamManager();
        if (mediaStreamManager != null) {
            if (CameraUtils.isCameraFront(mediaStreamManager.getCurrentCameraId())) {
                switchCameraButton.setBackgroundResource(R.drawable.switch_to_front_camera);
            } else {
                switchCameraButton.setBackgroundResource(R.drawable.switch_to_back_camera);
            }
            mediaStreamManager.switchCameraInput(null);

            int currentCameraId = mediaStreamManager.getCurrentCameraId();


        }
    }

    public void showRemoteVideo() {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    profilePhotoImageView.setVisibility(View.GONE);
                    callingStatusTextView.setVisibility(View.GONE);

                    opponentVideo.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    public void hideRemoteVideo() {
        //Log.dMainActivity.TAG, "Hiding remote video");
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Log.dMainActivity.TAG, "Hiding it now");
                    profilePhotoImageView.setVisibility(View.VISIBLE);
                    callingStatusTextView.setVisibility(View.VISIBLE);
                    opponentVideo.setVisibility(View.INVISIBLE);
                }
            });
        }
    }


    private void toggleCamera() {

        mediaStreamManager = currentSession.getMediaStreamManager();
        if (mediaStreamManager != null) {
            if (isLocalVideoEnabled) {
                new SendPushNotificationTask(VideoCallingService.getInstance(getActivity()).isIncomingCall ? currentSession.getUserInfo().get("callerMobileID") : currentSession.getUserInfo().get("calledMobileID"), "", "", "DisableVideoRequest", "", "") {
                    @Override
                    public void onTaskCompleted(String result) {

                    }
                }.execute();
                isLocalVideoEnabled = false;
                videoToggleButton.setBackgroundResource(R.drawable.video_off);
                mediaStreamManager.setVideoEnabled(false);
                hideLocalVideo();
                //Log.dMainActivity.TAG, "disabeling video");
            } else {
                isLocalVideoEnabled = true;
                new SendPushNotificationTask(VideoCallingService.getInstance(getActivity()).isIncomingCall ? currentSession.getUserInfo().get("callerMobileID") : currentSession.getUserInfo().get("calledMobileID"), "", "", "EnableVideoRequest", "", "") {

                    @Override
                    public void onTaskCompleted(String result) {

                    }
                }.execute();
                showLocalVideo();
                videoToggleButton.setBackgroundResource(R.drawable.video_on);
                mediaStreamManager.setVideoEnabled(true);
            }
            //Log.dMainActivity.TAG, "enabeling video");
        }

    }

    private void switchAudio() {
        VideoCallingService.getInstance(getContext()).onSwitchAudio();
        if (isSpeakerOff) {
            isSpeakerOff = false;
            audioSourceSwitchButton.setBackgroundResource(R.drawable.speaker_on);
        }else{
            isSpeakerOff = true;
            audioSourceSwitchButton.setBackgroundResource(R.drawable.speaker_off);
        }
    }

    private void showLocalVideo() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                currentSession.getMediaStreamManager().setVideoEnabled(isLocalVideoEnabled);
                switchCameraButton.setVisibility(View.VISIBLE);
                localVideo.setVisibility(View.VISIBLE);
//                final Animation fadeInAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_animation);
//
//                final Animation translateVideoToggleAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.video_toggle_button_animation_translate_right);
//
//                final Animation translateRightAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in_animation);
//
//
//                final Animation hangUpButtonAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.hangup_button_animation_translate_right);
//                hangUpButtonAnimation.setAnimationListener(new Animation.AnimationListener() {
//                    @Override
//                    public void onAnimationStart(Animation animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animation animation) {
//                        RelativeLayout.LayoutParams audioSwitchButtonLayoutParams = (RelativeLayout.LayoutParams) audioSourceSwitchButton.getLayoutParams();
//                        audioSwitchButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//                        audioSwitchButtonLayoutParams.setMarginEnd(60);
//                        audioSwitchButtonLayoutParams.removeRule(RelativeLayout.RIGHT_OF);
//                        audioSourceSwitchButton.setLayoutParams(audioSwitchButtonLayoutParams);
//
//                        RelativeLayout.LayoutParams hangupCallButtonLayoutParams = (RelativeLayout.LayoutParams) hangupCallButton.getLayoutParams();
//                        hangupCallButtonLayoutParams.addRule(RelativeLayout.LEFT_OF, R.id.switchAudioSourceButton);
//                        hangupCallButtonLayoutParams.setMarginEnd(60);
//                        hangupCallButtonLayoutParams.removeRule(RelativeLayout.CENTER_HORIZONTAL);
//                        hangupCallButton.setLayoutParams(hangupCallButtonLayoutParams);
//
//                        RelativeLayout.LayoutParams switchCameraButtonLayoutParam = (RelativeLayout.LayoutParams) switchCameraButton.getLayoutParams();
//                        switchCameraButtonLayoutParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//                        switchCameraButtonLayoutParam.setMarginEnd(60);
//                        switchCameraButtonLayoutParam.removeRule(RelativeLayout.RIGHT_OF);
//                        switchCameraButton.setLayoutParams(switchCameraButtonLayoutParam);
//
//                        switchCameraButton.setVisibility(View.VISIBLE);
//
//                        RelativeLayout.LayoutParams videoToggleLayoutParams = (RelativeLayout.LayoutParams) videoToggleButton.getLayoutParams();
//                        videoToggleLayoutParams.addRule(RelativeLayout.LEFT_OF, R.id.cameraSwitchButton);
//                        videoToggleLayoutParams.setMarginEnd(50);
//                        videoToggleButton.setLayoutParams(videoToggleLayoutParams);
//
//                        RelativeLayout.LayoutParams audioToggleLayoutParams = (RelativeLayout.LayoutParams) audioToggleButton.getLayoutParams();
//                        audioToggleLayoutParams.addRule(RelativeLayout.LEFT_OF, R.id.hangupCallButton);
//
//                        audioToggleButton.setLayoutParams(audioToggleLayoutParams);
//
//                        RelativeLayout.LayoutParams switchAudioSourceLayoutParams = (RelativeLayout.LayoutParams) audioSourceSwitchButton.getLayoutParams();
//                        switchAudioSourceLayoutParams.addRule(RelativeLayout.LEFT_OF, R.id.cameraSwitchButton);
//                        switchAudioSourceLayoutParams.setMarginEnd(50);
//                        audioSourceSwitchButton.setLayoutParams(switchAudioSourceLayoutParams);
//
//                        switchCameraButton.startAnimation(translateRightAnimation);
//

//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animation animation) {
//
//                    }
//                });
//
//
//                translateRightAnimation.setAnimationListener(new Animation.AnimationListener() {
//                    @Override
//                    public void onAnimationStart(Animation animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animation animation) {
//                        RelativeLayout.LayoutParams switchCameraButtonLayoutParams = (RelativeLayout.LayoutParams) switchCameraButton.getLayoutParams();
//                        switchCameraButtonLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//                        switchCameraButtonLayoutParams.removeRule(RelativeLayout.RIGHT_OF);
//                        switchCameraButtonLayoutParams.removeRule(RelativeLayout.CENTER_HORIZONTAL);
//                        switchCameraButtonLayoutParams.setMarginStart(120);
//                        switchCameraButton.setLayoutParams(switchCameraButtonLayoutParams);
//                        switchCameraButton.setVisibility(View.VISIBLE);
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animation animation) {
//
//                    }
//                });
//                hangupCallButton.startAnimation(hangUpButtonAnimation);
//                videoToggleButton.startAnimation(translateVideoToggleAnimation);
//                switchCameraButton.startAnimation(hangUpButtonAnimation);
//                audioToggleButton.startAnimation(hangUpButtonAnimation);
            }
        });
    }

    private void hideLocalVideo() {
        currentSession.getMediaStreamManager().setVideoEnabled(false);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Log.dMainActivity.TAG, "hiding local video");
                localVideo.setVisibility(View.INVISIBLE);
                switchCameraButton.setVisibility(View.INVISIBLE);

//                Animation hideLocalVideoAnimationForVideoToggleButton = AnimationUtils.loadAnimation(getActivity(), R.anim.video_toggle_button_animation_translate_left);
//                videoToggleButton.startAnimation(hideLocalVideoAnimationForVideoToggleButton);
//
//                Animation hideLocalVideoAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.hangup_button_animation_translate_left);
//                hideLocalVideoAnimation.setAnimationListener(new Animation.AnimationListener() {
//                    @Override
//                    public void onAnimationStart(Animation animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animation animation) {
//                        RelativeLayout.LayoutParams videoToggleLayoutParams = (RelativeLayout.LayoutParams) videoToggleButton.getLayoutParams();
//                        videoToggleLayoutParams.removeRule(RelativeLayout.LEFT_OF);
//                        videoToggleLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
//                        videoToggleLayoutParams.setMarginEnd(0);
//                        videoToggleButton.setLayoutParams(videoToggleLayoutParams);
//
//                        RelativeLayout.LayoutParams audioToogleLayoutParam = (RelativeLayout.LayoutParams) audioToggleButton.getLayoutParams();
//                        audioToogleLayoutParam.addRule(RelativeLayout.LEFT_OF, R.id.buttonsSeparatorView);
//                        audioToogleLayoutParam.removeRule(RelativeLayout.CENTER_HORIZONTAL);
//                        audioToogleLayoutParam.setMarginEnd(50);
//                        audioToggleButton.setLayoutParams(audioToogleLayoutParam);
//
//                        RelativeLayout.LayoutParams hangupToggleButtonLayoutParams = (RelativeLayout.LayoutParams) hangupCallButton.getLayoutParams();
//                        hangupToggleButtonLayoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.buttonsSeparatorView);
//                        hangupToggleButtonLayoutParams.removeRule(RelativeLayout.CENTER_HORIZONTAL);
//                        hangupToggleButtonLayoutParams.removeRule(RelativeLayout.RIGHT_OF);
//                        hangupToggleButtonLayoutParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//                        hangupToggleButtonLayoutParams.setMarginStart(50);
//                        hangupCallButton.setLayoutParams(hangupToggleButtonLayoutParams);
//
//
//                        RelativeLayout.LayoutParams audioSourceSwitchButtonLayoutParams = (RelativeLayout.LayoutParams) audioSourceSwitchButton.getLayoutParams();
//                        audioSourceSwitchButtonLayoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.hangupCallButton);
//                        audioSourceSwitchButtonLayoutParams.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//                        audioSourceSwitchButtonLayoutParams.setMarginStart(50);
//                        audioSourceSwitchButton.setLayoutParams(audioSourceSwitchButtonLayoutParams);
//
//
//                        switchCameraButton.setVisibility(View.INVISIBLE);
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animation animation) {
//
//                    }
//                });
//
//                hangupCallButton.startAnimation(hideLocalVideoAnimation);
//                audioToggleButton.startAnimation(hideLocalVideoAnimation);
//                audioSourceSwitchButton.startAnimation(hideLocalVideoAnimation);


            }
        });
    }


    private void showCallUI() {

//        final Animation fadeInAnimationForAudioSwitcherButton = AnimationUtils.loadAnimation(getActivity(), R.anim.hangup_button_animation_translate_right);
//
//        final Animation fadeInAnimationForAudioToggleButton = AnimationUtils.loadAnimation(getActivity(), R.anim.audio_toggle_button_audiocall_animation);
//        fadeInAnimationForAudioToggleButton.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        callingStatusTextView.setText("Call In Progress");
//                    }
//                });
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                RelativeLayout.LayoutParams audioToggleButtonLayoutParams = (RelativeLayout.LayoutParams) audioToggleButton.getLayoutParams();
//                audioToggleButtonLayoutParams.addRule(RelativeLayout.LEFT_OF, R.id.hangupCallButton);
//                audioToggleButtonLayoutParams.removeRule(RelativeLayout.CENTER_HORIZONTAL);
//                audioToggleButtonLayoutParams.setMarginEnd(50);
//                audioToggleButton.setLayoutParams(audioToggleButtonLayoutParams);
//
//
//
//                RelativeLayout.LayoutParams audioSourceSwitchButtonLayoutParams = (RelativeLayout.LayoutParams) audioSourceSwitchButton.getLayoutParams();
//                audioSourceSwitchButtonLayoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.hangupCallButton);
//                audioSourceSwitchButtonLayoutParams.removeRule(RelativeLayout.CENTER_HORIZONTAL);
//                audioSourceSwitchButtonLayoutParams.setMarginStart(50);
//                audioSourceSwitchButton.setLayoutParams(audioSourceSwitchButtonLayoutParams);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                videoToggleButton.setVisibility(View.VISIBLE);
                audioToggleButton.setVisibility(View.VISIBLE);
                audioSourceSwitchButton.setVisibility(View.VISIBLE);
                callingStatusTextView.setText("Call In Progress with "+MainActivity.customerNameBeingCalled);
                RelativeLayout.LayoutParams hangupButtonLayoutParams = (RelativeLayout.LayoutParams) hangupCallButton.getLayoutParams();
                hangupButtonLayoutParams.addRule(RelativeLayout.LEFT_OF, R.id.audioToggleButton);
                hangupButtonLayoutParams.removeRule(RelativeLayout.CENTER_HORIZONTAL);
                hangupButtonLayoutParams.setMarginEnd((int) getActivity().getResources().getDisplayMetrics().density * 20);
                hangupCallButton.setLayoutParams(hangupButtonLayoutParams);
//                audioToggleButton.startAnimation(fadeInAnimationForAudioToggleButton);
//                audioSourceSwitchButton.startAnimation(fadeInAnimationForAudioSwitcherButton);
            }
        });

//        final Animation fadeInAnimationForHangupButton = AnimationUtils.loadAnimation(getActivity(), R.anim.hangup_button_audiocall_animation);
//        fadeInAnimationForHangupButton.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                RelativeLayout.LayoutParams hangupButtonLayoutParams = (RelativeLayout.LayoutParams) hangupCallButton.getLayoutParams();
//                hangupButtonLayoutParams.addRule(RelativeLayout.RIGHT_OF, R.id.buttonsSeparatorView);
//                hangupButtonLayoutParams.removeRule(RelativeLayout.CENTER_HORIZONTAL);
//                hangupButtonLayoutParams.setMarginStart(50);
//                hangupCallButton.setLayoutParams(hangupButtonLayoutParams);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                hangupCallButton.startAnimation(fadeInAnimationForHangupButton);
//            }
//        });

    }

    public void opponentVideoEnabled() {
        showLocalVideo();
    }

    private void toggleAudio() {
        mediaStreamManager = currentSession.getMediaStreamManager();
        if (mediaStreamManager != null) {
            if (mediaStreamManager.isAudioEnabled()) {
                audioToggleButton.setBackgroundResource(R.drawable.unmute);
            } else {
                audioToggleButton.setBackgroundResource(R.drawable.mute);
            }
            mediaStreamManager.setAudioEnabled(!mediaStreamManager.isAudioEnabled());
        }

    }

//    private class AudioStreamReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//            if (intent.getAction().equals(AudioManager.ACTION_HEADSET_PLUG)) {
//
//            } else if (intent.getAction().equals(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED)) {
//
//            }
//        }
//    }


    public void prepareCall() {

        hangupCallButton.setVisibility(View.INVISIBLE);
        if (VideoCallingService.getInstance(getActivity()).isConnected()) {
            callingStatusTextView.setText("Preparing Call with "+ MainActivity.customerNameBeingCalled);
            loadAndCallUserFromQuickBloxWithEmail(MainActivity.customerEmailToCall);
        } else {
            callingStatusTextView.setText("Contacting "+ MainActivity.customerNameBeingCalled);
            VideoCallingService.getInstance(getActivity()).loginToVideoCallService();
        }
    }


    public void loadAndCallUserFromQuickBloxWithEmail(String email) {


        QBUsers.getUserByEmail(email).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                VideoCallingService.getInstance(MainActivity.mContext).opponents.clear();
                VideoCallingService.getInstance(MainActivity.mContext).opponents.add(qbUser.getId());
                //Log.dMainActivity.TAG, "time difference = " + qbUser.getLastRequestAt().compareTo(new Date()));
                //TODO
                if (new Date().getTime() - qbUser.getLastRequestAt().getTime() > 60 * 1000) {
                    new SendPushNotificationTask("" + MainActivity.customerMobileUserProfileIDbeingCalled, MainActivity.customerNameBeingCalled + " is Calling you", "", SendPushNotificationTask.Companion.getCallRequestedType(), "", "") {
                        @Override
                        public void onTaskCompleted(String result) {
                            try {
                                //Log.dMainActivity.TAG, "push notification Task Completed");
                                initiateCallButton.setEnabled(true);

                                QBRTCSession newSessionWithOpponents = VideoCallingService.getInstance(MainActivity.mContext).rtcClient.createNewSessionWithOpponents(
                                        VideoCallingService.getInstance(MainActivity.mContext).opponents, VideoCallingService.getInstance(MainActivity.mContext).qbConferenceType);
                                VideoCallingService.getInstance(getActivity()).isIncomingCall = false;
                                VideoCallingService.getInstance(MainActivity.mContext).initCurrentSession(newSessionWithOpponents);
                                currentSession = VideoCallingService.getInstance(MainActivity.mContext).getCurrentSession();
                                currentSession.addSessionCallbacksListener(FragmentVideoCall.this);
                                initSession();
                                VideoCallingService.getInstance(getActivity()).initAudioManager();
                                showCallAndHangupButton();
                                callingStatusTextView.setText("Press Green Button to Initiate a Call");

                            } catch (NullPointerException exp) {
                                VideoCallingService.getInstance(MainActivity.mContext).initQBRTCClient();
                            }
                        }
                    }.execute();
                } else {

                    try {
                        QBRTCSession newSessionWithOpponents = VideoCallingService.getInstance(MainActivity.mContext).rtcClient.createNewSessionWithOpponents(
                                VideoCallingService.getInstance(MainActivity.mContext).opponents, VideoCallingService.getInstance(MainActivity.mContext).qbConferenceType);
                        VideoCallingService.getInstance(getActivity()).isIncomingCall = false;
                        VideoCallingService.getInstance(MainActivity.mContext).initCurrentSession(newSessionWithOpponents);
                        currentSession = VideoCallingService.getInstance(MainActivity.mContext).getCurrentSession();
                        currentSession.addSessionCallbacksListener(FragmentVideoCall.this);
                        initiateCallButton.setEnabled(true);
                        initSession();
                        VideoCallingService.getInstance(getActivity()).initAudioManager();
                        showCallAndHangupButton();
                        callingStatusTextView.setText("Press Green Button to Initiate a Call");

                    } catch (NullPointerException exp) {
                        VideoCallingService.getInstance(MainActivity.mContext).initQBRTCClient();
                        return;
                    }
                }
            }

            @Override
            public void onError(QBResponseException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.mContext, "Error while preparing the call, please make sure internet connection is active and try again", Toast.LENGTH_LONG).show();
//                callEnded();
                e.printStackTrace();
            }
        });
    }

    private void showCallAndHangupButton() {
        final Animation fadeInForCallButton = AnimationUtils.loadAnimation(MainActivity.mContext, R.anim.fade_in_animation);
        fadeInForCallButton.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                initiateCallButton.setVisibility(View.VISIBLE);
                initialHangupCallButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        initiateCallButton.startAnimation(fadeInForCallButton);
        initialHangupCallButton.startAnimation(fadeInForCallButton);
    }


}
