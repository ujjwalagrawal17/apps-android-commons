package fr.free.nrw.commons;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static fr.free.nrw.commons.utils.NetworkUtils.isInternetConnectionEstablished;


public class NetworkConnectivityChangeReceiver extends BroadcastReceiver {

    public boolean isConnected = false;

    @Override
    public void onReceive(Context context, Intent intent) {
    }
}
