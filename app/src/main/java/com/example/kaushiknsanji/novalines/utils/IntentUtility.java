/*
 * Copyright 2018 Kaushik N. Sanji
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.kaushiknsanji.novalines.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ShareCompat;

import com.example.kaushiknsanji.novalines.settings.SettingsActivity;

/**
 * Utility class that provides convenience methods for the common Intents
 * used in the app.
 *
 * @author Kaushik N Sanji
 */
public class IntentUtility {

    /**
     * Method that loads the App's Settings
     * by sending an Intent to {@link SettingsActivity}
     *
     * @param context is the Context of the Calling Activity/Fragment
     */
    public static void openAppSettings(Context context) {
        //Creating an explicit intent to SettingsActivity
        Intent settingsIntent = new Intent(context, SettingsActivity.class);
        //Launching the Activity
        context.startActivity(settingsIntent);
    }

    /**
     * Method that launches the Network Settings of the Device.
     *
     * @param context is the Context of the Calling Activity/Fragment
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

    /**
     * Method that opens a webpage for the URL passed
     *
     * @param context is the Context of the Calling Activity/Fragment
     * @param webUrl  is the String containing the URL of the Web Page to be launched
     */
    public static void openLink(Context context, String webUrl) {
        //Parsing the URL
        Uri webPageUri = Uri.parse(webUrl);
        //Creating an ACTION_VIEW Intent with the URI
        Intent webIntent = new Intent(Intent.ACTION_VIEW, webPageUri);
        //Checking if there is an Activity that accepts the Intent
        if (webIntent.resolveActivity(context.getPackageManager()) != null) {
            //Launching the corresponding Activity and passing it the Intent
            context.startActivity(webIntent);
        }
    }

    /**
     * Method that opens up an app chooser for sharing the text content passed.
     *
     * @param launchingActivity is the {@link FragmentActivity} initiating this share action
     * @param textToShare       is the text String to be shared
     * @param chooserTitle      is the Title text to be shown for the chooser dialog
     */
    public static void shareText(FragmentActivity launchingActivity, String textToShare, String chooserTitle) {
        //Building and launching the share intent, to share the text content
        ShareCompat.IntentBuilder
                .from(launchingActivity)
                .setType("text/plain")
                .setText(textToShare)
                .setChooserTitle(chooserTitle)
                .startChooser();
    }

}
