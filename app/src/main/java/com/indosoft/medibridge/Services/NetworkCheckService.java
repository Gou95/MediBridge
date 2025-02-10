package com.indosoft.medibridge.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;

public class NetworkCheckService extends Service {

    private ConnectivityManager connectivityManager;

    @Override
    public void onCreate() {
        super.onCreate();
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Check the network status every 5 seconds (or as needed)
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    checkNetworkConnection();
                    try {
                        Thread.sleep(5000); // Wait for 5 seconds before checking again
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        return START_STICKY; // Keeps the service running until explicitly stopped
    }

    private void checkNetworkConnection() {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Log.d("NetworkCheckService", "Network is connected: " + networkInfo.getTypeName());
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                if (networkInfo.getSubtype() == ConnectivityManager.TYPE_MOBILE_HIPRI) {
                    Log.d("NetworkCheckService", "Currently on 5G");
                } else {
                    Log.d("NetworkCheckService", "Mobile network is active");
                }
            }
        } else {
            Log.d("NetworkCheckService", "No network connection");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("NetworkCheckService", "Service destroyed");
    }
}
