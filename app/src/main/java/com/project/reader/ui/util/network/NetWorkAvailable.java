package com.project.reader.ui.util.network;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public  class NetWorkAvailable {
    public  boolean isNetwork(Activity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkINfo =connectivityManager.getActiveNetworkInfo();
        if (networkINfo != null && networkINfo.isAvailable()) {
            return true;
        }
        return false;
    }
}
