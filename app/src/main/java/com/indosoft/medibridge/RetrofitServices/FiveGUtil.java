package com.indosoft.medibridge.RetrofitServices;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.widget.Toast;

public class FiveGUtil {
    public static boolean is5GAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network network = connectivityManager.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities capabilities =
                        connectivityManager.getNetworkCapabilities(network);
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        if (capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
                        }
                    }
                }
            }
        }
        return false;
    }

    public static void display5GStatus(Context context) {
        if (is5GAvailable(context)) {
            Toast.makeText(context, "5G is available!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "5G is not available", Toast.LENGTH_LONG).show();
        }
    }
}
