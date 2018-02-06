package com.example.kaushiknsanji.novalines.utils;

import android.content.Context;
import android.text.TextUtils;

import com.example.kaushiknsanji.novalines.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility Class that manages the Preference Keys to exclude
 * from or include in the Preference Listener
 *
 * @author Kaushik N Sanji
 */
public class PreferencesObserverUtility {

    /**
     * Method that returns the List of Preference Keys
     * that are to be excluded for the Preference Listener
     *
     * @param context is the Context of the Activity/Fragment or App
     * @return List of Strings that contain the Preference Keys to be excluded
     */
    public static List<String> getPreferenceKeysToExclude(Context context) {
        //Initializing an ArrayList of Strings for the Keys to be excluded
        ArrayList<String> keysToExclude = new ArrayList<>();

        //Adding the Preference Keys to be excluded : START
        //Excluding the maximum value of "Page To Display" preference setting by default
        keysToExclude.add(context.getString(R.string.pref_page_max_value_key));
        //Excluding the keys pertaining to the calculation of Preset Start Date of the News by default
        keysToExclude.add(context.getString(R.string.pref_start_period_manual_override_key));
        keysToExclude.add(context.getString(R.string.pref_start_period_preset_key));
        keysToExclude.add(context.getString(R.string.pref_start_period_buffer_key));
        //Excluding the preference key that saves current day's date
        keysToExclude.add(context.getString(R.string.pref_today_date_key));
        //Adding the Preference Keys to be excluded : END

        //Returning the exclusion list
        return keysToExclude;
    }

    /**
     * Method to add another key to an existing exclusion list
     *
     * @param keysToExclude is the List of Strings that contain the Preference Keys to be excluded
     * @param key           is an additional Preference Key string to be excluded
     */
    public static void addKeyToExclude(List<String> keysToExclude, String key) {
        //Checking initially if the Key is not empty and the list does NOT contain the Key yet
        if (!TextUtils.isEmpty(key) && !keysToExclude.contains(key)) {
            //Adding to the exclusion list
            keysToExclude.add(key);
        }
    }

    /**
     * Method to remove a key from an existing exclusion list
     *
     * @param keysToExclude is the List of Strings that contain the Preference Keys to be excluded
     * @param key           is the Preference Key string to be removed from exclusion
     */
    public static void removeKeyToInclude(List<String> keysToExclude, String key) {
        //Checking initially if the Key is not empty and the list does contain the Key
        if (!TextUtils.isEmpty(key) && keysToExclude.contains(key)) {
            //Removing from the exclusion list
            keysToExclude.remove(key);
        }
    }

}