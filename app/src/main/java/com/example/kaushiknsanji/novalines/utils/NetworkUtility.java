package com.example.kaushiknsanji.novalines.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

/**
 * Utility class that deals with the Network related stuff
 *
 * @author Kaushik N Sanji
 */
public class NetworkUtility {

    //Constant used for Logs
    private static final String LOG_TAG = NetworkUtility.class.getSimpleName();

    /**
     * Method that evaluates the state of Internet Connectivity
     *
     * @param context is the Context of the Application
     * @return a Boolean representing the state of Internet Connectivity
     * <br/><b>TRUE</b> if the Internet Connectivity is established
     * <br/><b>FALSE</b> otherwise
     */
    public static boolean isNetworkConnected(Context context) {
        //Retrieving the Connectivity Manager from the Context
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //Retrieving the current active default data network
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        //Checking the connectivity status and returning its state
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Method that launches the Network Settings of the Device.
     *
     * @param context is the Context of the Application
     */
    public static void openNetworkSettings(Context context) {
        //Creating an Intent to launch the Network Settings
        Intent networkIntent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        //Verifying that the Intent will resolve to an Activity
        if (networkIntent.resolveActivity(context.getPackageManager()) != null) {
            //Launching the Activity if resolved
            context.startActivity(networkIntent);
        }
    }

}
