package com.inspection.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.inspection.NetworkSpeedDetector;

public class BaseFragment extends Fragment implements NetworkSpeedDetector.NetworkSpeedListener {

    private NetworkSpeedDetector networkSpeedDetector;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        networkSpeedDetector = new NetworkSpeedDetector(requireContext());
        networkSpeedDetector.setNetworkSpeedListener(this);
        networkSpeedDetector.startMonitoring();
    }

    @Override
    public void onSlowNetworkDetected(String message) {
        // Show a toast or Snackbar to notify user
//        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        Snackbar sb = Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG);
//        sb.getView().setBackgroundColor(Color.parseColor("#4572ce"));
        sb.setBackgroundTint(Color.parseColor("#4572ce"));
        sb.setTextColor(Color.WHITE);
        sb.show();
    }

    @Override
    public void onNetworkSpeedRestored() {
        Snackbar sb = Snackbar.make(requireView(), "Connection speed is back to normal", Snackbar.LENGTH_LONG);
        sb.setBackgroundTint(Color.parseColor("#4572ce"));
        sb.setTextColor(Color.WHITE);
        sb.show();
    }
//
//    @Override
//    public void onNetworkSpeedNormal() {
//        // Hide the notification if network speed is normal
//    }


}
