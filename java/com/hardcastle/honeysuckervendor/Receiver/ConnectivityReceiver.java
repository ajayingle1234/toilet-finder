package com.hardcastle.honeysuckervendor.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.hardcastle.honeysuckervendor.Utils.GLOBAL;

/**
 * Created by abhijeet on 12/15/2017.
 */

public class ConnectivityReceiver extends BroadcastReceiver {

    public static ConnectivityReceiverListener connectivityReceiverListener;

    public ConnectivityReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent arg1) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null
                    && activeNetwork.isConnectedOrConnecting();

            if (connectivityReceiverListener != null) {
                connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isConnected() {
        NetworkInfo activeNetwork = null;
        try {
            ConnectivityManager
                    cm = (ConnectivityManager) GLOBAL.getInstance().getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            activeNetwork = cm.getActiveNetworkInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }


    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }
}
