package com.av.ajouuniv.avproject2;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class Network_check {

//			********************************************************************************************
//	 										IF NETWORK IS ACTIVE OR NOT
//			********************************************************************************************

    public static boolean isNetworkAvailable(Context context)
    {
        boolean networkavailable=false;


        try {
            ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

            if (connectivityManager != null && activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting()) {
                networkavailable = true;
            } else {
                networkavailable = false;
                Toast.makeText((Activity)context,"Connection Failed", Toast.LENGTH_LONG).show();

            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return networkavailable;
    }
}