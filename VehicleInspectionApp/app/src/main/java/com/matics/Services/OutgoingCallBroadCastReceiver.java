package com.matics.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.matics.Utils.ApplicationPrefs;
import com.matics.model.CompetitorModel;
import com.matics.serverTasks.UpdateCompetitorGeoFenceTask;
import com.matics.serverTasks.UpdateCompetitorPhoneTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OutgoingCallBroadCastReceiver extends BroadcastReceiver {

    Context context;
    TelephonyManager telephony;
    static long startTime = -1, endTime = -1;
    static int phoneIncidentId = -1;
    static long totalTime = -1;
    static int competitorPhoneId = -1;

    @Override
    public void onReceive(final Context context, Intent intent) {
        this.context = context;
//        Toast.makeText(context, "something received", Toast.LENGTH_SHORT).show();
        String action = intent.getAction();
        Log.v("CAAAAAAAAAL", "hellloooo  r" + action);
        if (action.equalsIgnoreCase("android.intent.action.PHONE_STATE")) {
            if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
                    TelephonyManager.EXTRA_STATE_IDLE)) {
                if (startTime > -1) {
                    endTime = System.currentTimeMillis();
                    totalTime = endTime - startTime;
                    startTime = endTime = -1;

                    new UpdateCompetitorPhoneTask(context, competitorPhoneId, (int) totalTime / 1000 / 60, ApplicationPrefs.getInstance(context).getUserProfilePref().getMobileUserProfileId(), phoneIncidentId) {
                        @Override
                        public void onTaskCompleted(String result) {
//                            Toast.makeText(context, "Competitor Phone incident updated after: "+totalTime/1000/60+" minutes", Toast.LENGTH_SHORT).show();
                            totalTime = 0;
                            startTime = endTime = phoneIncidentId = -1;
                        }
                    }.execute();
                }
            }
        } else if (action.equalsIgnoreCase("android.intent.action.NEW_OUTGOING_CALL")) {
            String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            PhoneCallStateListenerService phoneListener = new PhoneCallStateListenerService();
            telephony = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

            updateCompetitorLogicWithPhoneNumber(phoneNumber);
        }
    }


    public void updateCompetitorLogicWithPhoneNumber(String phoneNumber) {
//        Toast.makeText(context, "Calling phone number: "+phoneNumber, Toast.LENGTH_SHORT).show();
        Log.v("received competitorssss", ApplicationPrefs.getInstance(context).getCompetitorModelString());
        JSONObject object = null;
        JSONArray arr = null;
        try {
            object = new JSONObject(ApplicationPrefs.getInstance(context).getCompetitorModelString());
            arr = object.getJSONArray("GetCustLossPreventionCompListByMobileUserProfileIDResult");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList<CompetitorModel> competitorModels;
        try {
            competitorModels = new Gson().fromJson(arr.toString(), new TypeToken<ArrayList<CompetitorModel>>() {
            }.getType());
        } catch (NullPointerException nullExp) {
            return;
        }
        if (competitorModels != null) {
            outerLoop:
            for (CompetitorModel competitorModel : competitorModels) {
                ArrayList<CompetitorModel.CompetitorPhone> competitorPhones = competitorModel.getCompetitorPhone();
                for (CompetitorModel.CompetitorPhone competitorPhone : competitorPhones) {
                    if (phoneNumber.replace("(", "").replace(")", "").replace("-", "").replace(" ", "").contains(competitorPhone.getPhoneNum())) {
                        startTime = System.currentTimeMillis();
//                        Toast.makeText(context, "Calling a Comp. number started", Toast.LENGTH_SHORT).show();
                        new UpdateCompetitorPhoneTask(context, competitorPhone.getPhoneID(), 0, ApplicationPrefs.getInstance(context).getUserProfilePref().getMobileUserProfileId(), phoneIncidentId) {
                            @Override
                            public void onTaskCompleted(String result) {
                                if (result != null && !result.contains("BAD")) {
                                    phoneIncidentId = Integer.parseInt(result.replace("{\"UpdateCompetitorPhoneResult\":", "").replace("}", ""));
//                                    Toast.makeText(context, "Competitor Phone Campaign submitted successfully", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }.execute();
                    }
                }
            }

        }
    }
}
