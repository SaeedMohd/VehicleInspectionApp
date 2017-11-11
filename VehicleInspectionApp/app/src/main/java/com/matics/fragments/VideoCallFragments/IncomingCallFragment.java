package com.matics.fragments.VideoCallFragments;

import android.app.Fragment;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.matics.MainActivity;
import com.matics.R;
import com.matics.Utils.RingtonePlayer;
import com.matics.Services.VideoCallingService;
import com.quickblox.videochat.webrtc.QBRTCSession;

import java.util.HashMap;

public class IncomingCallFragment extends Fragment{

    private View view;
    private IntentFilter intentFilter;

    QBRTCSession currentSession;


    private RingtonePlayer ringtonePlayer;

    private Button acceptCallButton, rejectCallButton;
    private TextView callerInfoTextView;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_incoming_video_call, container, false);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Incoming Call");

        intentFilter = new IntentFilter();
        intentFilter.addAction(AudioManager.ACTION_HEADSET_PLUG);
        intentFilter.addAction(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED);

        ringtonePlayer = new RingtonePlayer(getActivity(), R.raw.beep);
        ringtonePlayer.play(true);

        VideoCallingService.getInstance(getActivity()).initializeIncomingCallFragment(this);

        currentSession = VideoCallingService.getInstance(getActivity()).getCurrentSession();

        callerInfoTextView = (TextView) view.findViewById(R.id.callerTextView);

        try {
            callerInfoTextView.setText(currentSession.getUserInfo().get("callerName"));
        }catch(NullPointerException exp){
            exp.printStackTrace();
        }


        acceptCallButton= (Button) view.findViewById(R.id.acceptCallButton);
        acceptCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ringtonePlayer.stop();
                VideoCallingService.getInstance(getActivity()).callAcceptedGoToVideoCall();

            }
        });

        rejectCallButton= (Button) view.findViewById(R.id.rejectCallButton);
        rejectCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hangupCall();
            }
        });





        return view;
    }


    public void hangupCall(){
        try {
            ringtonePlayer.stop();
            currentSession.rejectCall(new HashMap<String, String>());
            ((MainActivity) getActivity()).onBackPressed();
        }catch (NullPointerException exp){
            exp.printStackTrace();
        }
    }


}
