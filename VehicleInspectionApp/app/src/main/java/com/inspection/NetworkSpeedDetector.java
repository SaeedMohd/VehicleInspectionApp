package com.inspection;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;

public class NetworkSpeedDetector {
    private final Context context;
    private NetworkSpeedListener listener;
    private boolean isConnected = true;
    private boolean isSlowConnection = false;

    public NetworkSpeedDetector(Context context) {
        this.context = context;
    }

    // Set a listener to handle network speed changes
    public void setNetworkSpeedListener(NetworkSpeedListener listener) {
        this.listener = listener;
    }

    public void startMonitoring() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest networkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            connectivityManager.registerNetworkCallback(networkRequest, new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    // Connection is available
                    if (!isConnected) {
                        isConnected = true;
                        listener.onNetworkSpeedRestored(); // Notify that connection is restored
                    }
                    checkNetworkSpeed(connectivityManager);
                }

                @Override
                public void onLost(Network network) {
                    // Connection is lost
                    isConnected = false;
                    isSlowConnection = false; // Reset slow connection flag
                    if (listener != null) {
                        listener.onSlowNetworkDetected("No Internet Connection");
                    }
                }

                @Override
                public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
                    if (isConnected) {
                        checkNetworkSpeed(connectivityManager);
                    }
                }
            });
        }
    }

    private void checkNetworkSpeed(ConnectivityManager connectivityManager) {
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        if (networkCapabilities != null) {
            int downSpeedKbps = networkCapabilities.getLinkDownstreamBandwidthKbps();
            int uploadSpeedKbps = networkCapabilities.getLinkUpstreamBandwidthKbps();
            boolean isCurrentlySlow = (downSpeedKbps < 400 || uploadSpeedKbps < 400);

            if (isCurrentlySlow) {
                if (!isSlowConnection) {
                    // Connection has just become slow
                    isSlowConnection = true;
                    if (listener != null) {
                        listener.onSlowNetworkDetected("Slow Internet Connection \nDownload Speed: " + downSpeedKbps + " Kbps \nUpload Speed: " + uploadSpeedKbps + " Kbps");
                    }
                }
            } else {
                if (isSlowConnection) {
                    // Connection speed is back to normal
                    isSlowConnection = false;
                    if (listener != null) {
                        listener.onNetworkSpeedRestored();
                    }
                } else if (!isSlowConnection && isConnected) {
                    // Connection is good and stable after restoration
                    if (listener != null) {
                        listener.onNetworkSpeedRestored();
                    }
                }
            }
        }
    }

    // Define the listener interface
    public interface NetworkSpeedListener {
        void onSlowNetworkDetected(String message);
        void onNetworkSpeedRestored();
    }
}