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
import android.text.TextUtils;

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
        keysToExclude.add(PreferencesUtility.getLastPageIndexKey(context));
        //Excluding the keys pertaining to the calculation of Preset Start Date of the News by default
        keysToExclude.add(PreferencesUtility.getStartPeriodOverrideKey(context));
        keysToExclude.add(PreferencesUtility.getPresetStartPeriodKey(context));
        keysToExclude.add(PreferencesUtility.getStartPeriodBufferKey(context));
        //Excluding the preference key that saves current day's date
        keysToExclude.add(PreferencesUtility.getCurrentDayDateKey(context));
        //Excluding the preference key that does the reset of all Settings
        keysToExclude.add(PreferencesUtility.getResetSettingsKey(context));
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