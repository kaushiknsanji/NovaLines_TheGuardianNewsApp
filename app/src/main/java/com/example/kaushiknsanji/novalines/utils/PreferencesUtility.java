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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.example.kaushiknsanji.novalines.R;

/**
 * Utility class that manages access to the Settings' SharedPreferences and
 * other preferences which are not part of the
 * {@link com.example.kaushiknsanji.novalines.settings.SettingsFragment}.
 * <p>
 * Provides methods for accessing the Keys, their Default values
 * and also for updating their value in SharedPreferences.
 * </p>
 *
 * @author Kaushik N Sanji
 */
public class PreferencesUtility {

    /**
     * Method that returns the Key of the 'page' (Page To Display) setting
     *
     * @param context is the Context of the Fragment/Activity
     * @return String representing the Key of the 'page' (Page To Display) setting
     */
    @NonNull
    public static String getStartPageIndexKey(Context context) {
        return context.getString(R.string.pref_page_index_key);
    }

    /**
     * Method that returns the Default value of the 'page' (Page To Display) setting
     *
     * @param context is the Context of the Fragment/Activity
     * @return Integer value representing the Default value of the 'page' (Page To Display) setting
     */
    public static int getDefaultStartPageIndex(Context context) {
        return context.getResources().getInteger(R.integer.pref_page_index_default_value);
    }

    /**
     * Method that returns the Key of the 'endIndex' setting
     *
     * @param context is the Context of the Fragment/Activity
     * @return String representing the Key of the 'endIndex' setting
     */
    @NonNull
    public static String getLastPageIndexKey(Context context) {
        return context.getString(R.string.pref_page_max_value_key);
    }

    /**
     * Method that returns the Key of 'Sort by' ListPreference
     *
     * @param context is the Context of the Fragment/Activity
     * @return String representing the Key of 'Sort by' ListPreference
     */
    @NonNull
    public static String getSortByKey(Context context) {
        return context.getString(R.string.pref_sort_by_key);
    }

    /**
     * Method that returns the Default value of the 'Sort by' ListPreference
     *
     * @param context is the Context of the Fragment/Activity
     * @return String representing the Default value of the 'Sort by' ListPreference
     */
    public static String getDefaultSortByValue(Context context) {
        return context.getString(R.string.pref_sort_by_default);
    }

    /**
     * Method that returns the Key of 'Sort based on' ListPreference
     *
     * @param context is the Context of the Fragment/Activity
     * @return String representing the Key of 'Sort based on' ListPreference
     */
    @NonNull
    public static String getSortBasedOnKey(Context context) {
        return context.getString(R.string.pref_sort_on_key);
    }

    /**
     * Method that returns the Default value of the 'Sort based on' ListPreference
     *
     * @param context is the Context of the Fragment/Activity
     * @return String representing the Default value of the 'Sort based on' ListPreference
     */
    public static String getDefaultSortBasedOnValue(Context context) {
        return context.getString(R.string.pref_sort_on_default);
    }

    /**
     * Method that returns the Key of 'News items per page' NumberPickerPreference
     *
     * @param context is the Context of the Fragment/Activity
     * @return String representing the Key of 'News items per page' NumberPickerPreference
     */
    @NonNull
    public static String getItemsPerPageKey(Context context) {
        return context.getString(R.string.pref_items_per_page_key);
    }

    /**
     * Method that returns the Default value of the 'News items per page' NumberPickerPreference
     *
     * @param context is the Context of the Fragment/Activity
     * @return Integer value representing the Default value of the 'News items per page' NumberPickerPreference
     */
    public static int getDefaultItemsPerPage(Context context) {
        return context.getResources().getInteger(R.integer.pref_items_per_page_default_value);
    }

    /**
     * Method that returns the Key of 'Start Period Preset/Manual' CheckBoxPreference
     *
     * @param context is the Context of the Fragment/Activity
     * @return String representing the Key of 'Start Period Preset/Manual' CheckBoxPreference
     */
    @NonNull
    public static String getStartPeriodOverrideKey(Context context) {
        return context.getString(R.string.pref_start_period_manual_override_key);
    }

    /**
     * Method that returns the Default value of 'Start Period Preset/Manual' CheckBoxPreference
     *
     * @param context is the Context of the Fragment/Activity
     * @return Boolean representing the Default value of 'Start Period Preset/Manual' CheckBoxPreference
     */
    public static boolean getDefaultStartPeriodOverrideValue(Context context) {
        return context.getResources().getBoolean(R.bool.pref_start_period_manual_override_default);
    }

    /**
     * Method that returns the Key of 'Preset Start Period' ListPreference
     *
     * @param context is the Context of the Fragment/Activity
     * @return String representing the Key of 'Preset Start Period' ListPreference
     */
    @NonNull
    public static String getPresetStartPeriodKey(Context context) {
        return context.getString(R.string.pref_start_period_preset_key);
    }

    /**
     * Method that returns the Default value of 'Preset Start Period' ListPreference
     *
     * @param context is the Context of the Fragment/Activity
     * @return String representing the Default value of 'Preset Start Period' ListPreference
     */
    public static String getDefaultPresetStartPeriodValue(Context context) {
        return context.getString(R.string.pref_start_period_preset_default);
    }

    /**
     * Method that returns the Possible values for 'Preset Start Period' ListPreference
     *
     * @param context is the Context of the Fragment/Activity
     * @return A String array representing the Possible values for 'Preset Start Period' ListPreference
     */
    public static String[] getPossiblePresetStartPeriodValues(Context context) {
        return context.getResources().getStringArray(R.array.pref_start_period_preset_entries);
    }

    /**
     * Method that returns the Key of 'Buffer to Start Period' NumberPickerPreference
     *
     * @param context is the Context of the Fragment/Activity
     * @return String representing the Key of 'Buffer to Start Period' NumberPickerPreference
     */
    @NonNull
    public static String getStartPeriodBufferKey(Context context) {
        return context.getString(R.string.pref_start_period_buffer_key);
    }

    /**
     * Method that returns the Default value of 'Buffer to Start Period' NumberPickerPreference
     *
     * @param context is the Context of the Fragment/Activity
     * @return Integer value representing the Default value of 'Buffer to Start Period' NumberPickerPreference
     */
    public static int getDefaultStartPeriodBufferValue(Context context) {
        return context.getResources().getInteger(R.integer.pref_start_period_buffer_default_value);
    }

    /**
     * Method that returns the Key(from-date) of 'Specify Start Period' DatePickerPreference
     *
     * @param context is the Context of the Fragment/Activity
     * @return String representing the Key(from-date) of 'Specify Start Period' DatePickerPreference
     */
    @NonNull
    public static String getStartPeriodKey(Context context) {
        return context.getString(R.string.pref_start_period_manual_key);
    }

    /**
     * Method that returns the Key of 'Reset Settings' ConfirmationPreference
     *
     * @param context is the Context of the Fragment/Activity
     * @return String representing the Key of 'Reset Settings' ConfirmationPreference
     */
    @NonNull
    public static String getResetSettingsKey(Context context) {
        return context.getString(R.string.pref_reset_settings_key);
    }

    /**
     * Method that returns the Key(date-today) of Current Day's Date setting
     *
     * @param context is the Context of the Fragment/Activity
     * @return String representing the Key(date-today) of Current Day's Date setting
     */
    @NonNull
    public static String getCurrentDayDateKey(Context context) {
        return context.getString(R.string.pref_today_date_key);
    }

    /**
     * Method that returns the current 'page' (Page to Display) setting value
     * from the SharedPreferences
     *
     * @param context is the Context of the Fragment/Activity
     * @return Integer value of the current 'page' (Page to Display) setting
     */
    public static int getStartPageIndex(Context context) {
        return getStartPageIndex(context, PreferenceManager.getDefaultSharedPreferences(context));
    }

    /**
     * Method that returns the current 'page' (Page to Display) setting value
     * from the SharedPreferences
     *
     * @param context           is the Context of the Fragment/Activity
     * @param sharedPreferences is the instance of the {@link SharedPreferences}
     * @return Integer value of the current 'page' (Page to Display) setting
     */
    public static int getStartPageIndex(Context context, SharedPreferences sharedPreferences) {
        //Returning the current value of 'page' (Page to Display) setting
        return sharedPreferences.getInt(
                getStartPageIndexKey(context),
                getDefaultStartPageIndex(context)
        );
    }

    /**
     * Method that updates the current 'page' (Page to Display) setting value
     * with the given new value #newValue
     *
     * @param context  is the Context of the Fragment/Activity
     * @param newValue Integer value to be updated on the current 'page' (Page to Display) setting
     */
    public static void updateStartPageIndex(Context context, int newValue) {
        updateStartPageIndex(context, PreferenceManager.getDefaultSharedPreferences(context), newValue);
    }

    /**
     * Method that updates the current 'page' (Page to Display) setting value
     * with the given new value #newValue
     *
     * @param context           is the Context of the Fragment/Activity
     * @param sharedPreferences is the instance of the {@link SharedPreferences}
     * @param newValue          Integer value to be updated on the current 'page' (Page to Display) setting
     */
    public static void updateStartPageIndex(Context context, SharedPreferences sharedPreferences, int newValue) {
        //Opening the Editor to update the value
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        //Updating with the given new value
        prefEditor.putInt(
                getStartPageIndexKey(context),
                newValue
        );
        prefEditor.apply(); //applying the changes
    }

    /**
     * Method that returns the current 'endIndex' setting value which is the Maximum value of the 'Page to Display' setting
     *
     * @param context is the Context of the Fragment/Activity
     * @return Integer value of the current 'endIndex' setting which is the Maximum value of the 'Page to Display' setting
     * This method uses the default value of 'page' (Page to Display) setting while retrieving the value of current 'endIndex'.
     */
    public static int getLastPageIndex(Context context) {
        return getLastPageIndex(context, PreferenceManager.getDefaultSharedPreferences(context), getDefaultStartPageIndex(context));
    }

    /**
     * Method that returns the current 'endIndex' setting value which is the Maximum value of the 'Page to Display' setting
     *
     * @param context          is the Context of the Fragment/Activity
     * @param defaultPageIndex is the Default value to return when the 'endIndex' is not set
     * @return Integer value of the current 'endIndex' setting which is the Maximum value of the 'Page to Display' setting
     */
    public static int getLastPageIndex(Context context, int defaultPageIndex) {
        return getLastPageIndex(context, PreferenceManager.getDefaultSharedPreferences(context), defaultPageIndex);
    }

    /**
     * Method that returns the current 'endIndex' setting value which is the Maximum value of the 'Page to Display' setting
     *
     * @param context           is the Context of the Fragment/Activity
     * @param sharedPreferences is the instance of the {@link SharedPreferences}
     * @param defaultPageIndex  is the Default value to return when the 'endIndex' is not set
     * @return Integer value of the current 'endIndex' setting which is the Maximum value of the 'Page to Display' setting
     */
    public static int getLastPageIndex(Context context, SharedPreferences sharedPreferences, int defaultPageIndex) {
        return sharedPreferences.getInt(
                getLastPageIndexKey(context),
                defaultPageIndex
        );
    }

    /**
     * Method that updates the current 'endIndex' setting which is the Maximum value of the 'Page to Display' setting
     * with the given new value #newValue
     *
     * @param context  is the Context of the Fragment/Activity
     * @param newValue Integer value to be updated on the current 'endIndex' setting
     */
    public static void updateLastPageIndex(Context context, int newValue) {
        updateLastPageIndex(context, PreferenceManager.getDefaultSharedPreferences(context), newValue);
    }

    /**
     * Method that updates the current 'endIndex' setting which is the Maximum value of the 'Page to Display' setting
     * with the given new value #newValue
     *
     * @param context           is the Context of the Fragment/Activity
     * @param sharedPreferences is the instance of the {@link SharedPreferences}
     * @param newValue          Integer value to be updated on the current 'endIndex' setting
     */
    public static void updateLastPageIndex(Context context, SharedPreferences sharedPreferences, int newValue) {
        //Opening the Editor to update the value
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        //Updating with the given new value
        prefEditor.putInt(
                getLastPageIndexKey(context),
                newValue
        );
        prefEditor.apply(); //applying the changes
    }

    /**
     * Method that returns the value of "Start Period Preset/Manual" CheckBoxPreference
     *
     * @param context is the Context of the Fragment/Activity
     * @return Boolean representing the state of "Start Period Preset/Manual" CheckBoxPreference
     */
    public static boolean getStartPeriodOverrideValue(Context context) {
        return getStartPeriodOverrideValue(context, PreferenceManager.getDefaultSharedPreferences(context));
    }

    /**
     * Method that returns the value of "Start Period Preset/Manual" CheckBoxPreference
     *
     * @param context           is the Context of the Fragment/Activity
     * @param sharedPreferences is the instance of the {@link SharedPreferences}
     * @return Boolean representing the state of "Start Period Preset/Manual" CheckBoxPreference
     */
    public static boolean getStartPeriodOverrideValue(Context context, SharedPreferences sharedPreferences) {
        return sharedPreferences.getBoolean(
                getStartPeriodOverrideKey(context),
                getDefaultStartPeriodOverrideValue(context)
        );
    }

    /**
     * Method that returns the value of Current Day's Date stored in preferences
     *
     * @param context             is the Context of the Fragment/Activity
     * @param defaultDateInMillis is the default date value to return when the value is not set
     * @return Long value representing the Current Day's Date in milliseconds
     */
    public static long getCurrentDayDateValue(Context context, long defaultDateInMillis) {
        return getCurrentDayDateValue(context, PreferenceManager.getDefaultSharedPreferences(context), defaultDateInMillis);
    }

    /**
     * Method that returns the value of Current Day's Date stored in preferences
     *
     * @param context             is the Context of the Fragment/Activity
     * @param sharedPreferences   is the instance of the {@link SharedPreferences}
     * @param defaultDateInMillis is the default date value to return when the value is not set
     * @return Long value representing the Current Day's Date in milliseconds
     */
    public static long getCurrentDayDateValue(Context context, SharedPreferences sharedPreferences, long defaultDateInMillis) {
        return sharedPreferences.getLong(
                getCurrentDayDateKey(context),
                defaultDateInMillis
        );
    }

    /**
     * Method that updates the Current Day's Date to the new value #newDateInMillis
     *
     * @param context         is the Context of the Fragment/Activity
     * @param newDateInMillis is the New Date in Millis that needs to be updated on the Current Day's Date preference
     */
    public static void updateCurrentDayDateValue(Context context, long newDateInMillis) {
        updateCurrentDayDateValue(context, PreferenceManager.getDefaultSharedPreferences(context), newDateInMillis);
    }

    /**
     * Method that updates the Current Day's Date to the new value #newDateInMillis
     *
     * @param context           is the Context of the Fragment/Activity
     * @param sharedPreferences is the instance of the {@link SharedPreferences}
     * @param newDateInMillis   is the New Date in Millis that needs to be updated on the Current Day's Date preference
     */
    public static void updateCurrentDayDateValue(Context context, SharedPreferences sharedPreferences, long newDateInMillis) {
        //Opening the Editor to update the value
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        //Updating with the given new value
        prefEditor.putLong(
                getCurrentDayDateKey(context),
                newDateInMillis
        );
        prefEditor.apply(); //applying the changes
    }

    /**
     * Method that returns the value of "Preset Start Period" ListPreference
     *
     * @param context is the Context of the Fragment/Activity
     * @return String value of the Preset selected on the "Preset Start Period" ListPreference
     */
    public static String getPresetStartPeriodValue(Context context) {
        return getPresetStartPeriodValue(context, PreferenceManager.getDefaultSharedPreferences(context));
    }

    /**
     * Method that returns the value of "Preset Start Period" ListPreference
     *
     * @param context           is the Context of the Fragment/Activity
     * @param sharedPreferences is the instance of the {@link SharedPreferences}
     * @return String value of the Preset selected on the "Preset Start Period" ListPreference
     */
    public static String getPresetStartPeriodValue(Context context, SharedPreferences sharedPreferences) {
        return sharedPreferences.getString(
                getPresetStartPeriodKey(context),
                getDefaultPresetStartPeriodValue(context)
        );
    }

    /**
     * Method that returns the value of "Buffer to Start Period" NumberPickerPreference
     *
     * @param context is the Context of the Fragment/Activity
     * @return Integer value of Buffer selected on the "Buffer to Start Period" NumberPickerPreference
     */
    public static int getStartPeriodBufferValue(Context context) {
        return getStartPeriodBufferValue(context, PreferenceManager.getDefaultSharedPreferences(context));
    }

    /**
     * Method that returns the value of "Buffer to Start Period" NumberPickerPreference
     *
     * @param context           is the Context of the Fragment/Activity
     * @param sharedPreferences is the instance of the {@link SharedPreferences}
     * @return Integer value of Buffer selected on the "Buffer to Start Period" NumberPickerPreference
     */
    public static int getStartPeriodBufferValue(Context context, SharedPreferences sharedPreferences) {
        return sharedPreferences.getInt(
                getStartPeriodBufferKey(context),
                getDefaultStartPeriodBufferValue(context)
        );
    }

    /**
     * Method that returns the value of 'from-date' setting ('Specify Start Period' DatePickerPreference)
     *
     * @param context             is the Context of the Fragment/Activity
     * @param defaultDateInMillis is the default date value to return when the value is not set
     * @return Long value representing the Start Period 'from-date' in milliseconds
     */
    public static long getStartPeriodValue(Context context, long defaultDateInMillis) {
        return getStartPeriodValue(context, PreferenceManager.getDefaultSharedPreferences(context), defaultDateInMillis);
    }

    /**
     * Method that returns the value of 'from-date' setting ('Specify Start Period' DatePickerPreference)
     *
     * @param context             is the Context of the Fragment/Activity
     * @param sharedPreferences   is the instance of the {@link SharedPreferences}
     * @param defaultDateInMillis is the default date value to return when the value is not set
     * @return Long value representing the Start Period 'from-date' in milliseconds
     */
    public static long getStartPeriodValue(Context context, SharedPreferences sharedPreferences, long defaultDateInMillis) {
        return sharedPreferences.getLong(
                getStartPeriodKey(context),
                defaultDateInMillis
        );
    }

    /**
     * Method that updates the value of 'from-date' setting ('Specify Start Period' DatePickerPreference)
     * to the new value #newDateInMillis
     *
     * @param context         is the Context of the Fragment/Activity
     * @param newDateInMillis is the New Date in Millis that needs to be updated on the 'from-date' setting
     */
    public static void updateStartPeriodValue(Context context, long newDateInMillis) {
        updateStartPeriodValue(context, PreferenceManager.getDefaultSharedPreferences(context), newDateInMillis);
    }

    /**
     * Method that updates the value of 'from-date' setting ('Specify Start Period' DatePickerPreference)
     * to the new value #newDateInMillis
     *
     * @param context           is the Context of the Fragment/Activity
     * @param sharedPreferences is the instance of the {@link SharedPreferences}
     * @param newDateInMillis   is the New Date in Millis that needs to be updated on the 'from-date' setting
     */
    public static void updateStartPeriodValue(Context context, SharedPreferences sharedPreferences, long newDateInMillis) {
        //Opening the Editor to update the value
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        //Updating with the given new value
        prefEditor.putLong(
                getStartPeriodKey(context),
                newDateInMillis
        );
        prefEditor.apply(); //applying the changes
    }

    /**
     * Method that returns the current selected 'Sort by' ListPreference value
     *
     * @param context is the Context of the Fragment/Activity
     * @return String containing the value of the selected 'Sort by' ListPreference value
     */
    public static String getSortByValue(Context context) {
        return getSortByValue(context, PreferenceManager.getDefaultSharedPreferences(context));
    }

    /**
     * Method that returns the current selected 'Sort by' ListPreference value
     *
     * @param context           is the Context of the Fragment/Activity
     * @param sharedPreferences is the instance of the {@link SharedPreferences}
     * @return String containing the value of the selected 'Sort by' ListPreference value
     */
    public static String getSortByValue(Context context, SharedPreferences sharedPreferences) {
        return sharedPreferences.getString(
                getSortByKey(context),
                getDefaultSortByValue(context)
        );
    }

    /**
     * Method that returns the current selected 'Sort based on' ListPreference value
     *
     * @param context is the Context of the Fragment/Activity
     * @return String containing the value of the selected 'Sort based on' ListPreference value
     */
    public static String getSortBasedOnValue(Context context) {
        return getSortBasedOnValue(context, PreferenceManager.getDefaultSharedPreferences(context));
    }

    /**
     * Method that returns the current selected 'Sort based on' ListPreference value
     *
     * @param context           is the Context of the Fragment/Activity
     * @param sharedPreferences is the instance of the {@link SharedPreferences}
     * @return String containing the value of the selected 'Sort based on' ListPreference value
     */
    public static String getSortBasedOnValue(Context context, SharedPreferences sharedPreferences) {
        return sharedPreferences.getString(
                getSortBasedOnKey(context),
                getDefaultSortBasedOnValue(context)
        );
    }

    /**
     * Method that returns the current selected 'News items per page' NumberPickerPreference value
     *
     * @param context is the Context of the Fragment/Activity
     * @return Integer containing the value of the selected 'News items per page' NumberPickerPreference value
     */
    public static int getItemsPerPageValue(Context context) {
        return getItemsPerPageValue(context, PreferenceManager.getDefaultSharedPreferences(context));
    }

    /**
     * Method that returns the current selected 'News items per page' NumberPickerPreference value
     *
     * @param context           is the Context of the Fragment/Activity
     * @param sharedPreferences is the instance of the {@link SharedPreferences}
     * @return Integer containing the value of the selected 'News items per page' NumberPickerPreference value
     */
    public static int getItemsPerPageValue(Context context, SharedPreferences sharedPreferences) {
        return sharedPreferences.getInt(
                getItemsPerPageKey(context),
                getDefaultItemsPerPage(context)
        );
    }

    /**
     * Method that returns the Minimum value supported by the 'News items per page' NumberPickerPreference
     *
     * @param context is the Context of the Fragment/Activity
     * @return Integer representing the Minimum value supported by the 'News items per page' NumberPickerPreference
     */
    public static int getItemsPerPageMinValue(Context context) {
        return context.getResources().getInteger(R.integer.pref_items_per_page_min_value);
    }

    /**
     * Method that returns the Maximum value supported by the 'News items per page' NumberPickerPreference
     *
     * @param context is the Context of the Fragment/Activity
     * @return Integer representing the Maximum value supported by the 'News items per page' NumberPickerPreference
     */
    public static int getItemsPerPageMaxValue(Context context) {
        return context.getResources().getInteger(R.integer.pref_items_per_page_max_value);
    }

    /**
     * Method that returns the Minimum value supported by the 'Buffer to Start Period' NumberPickerPreference
     *
     * @param context is the Context of the Fragment/Activity
     * @return Integer representing the Minimum value supported by the 'Buffer to Start Period' NumberPickerPreference
     */
    public static int getStartPeriodBufferMinValue(Context context) {
        return context.getResources().getInteger(R.integer.pref_start_period_buffer_min_value);
    }

    /**
     * Method that returns the Maximum value supported by the 'Buffer to Start Period' NumberPickerPreference
     *
     * @param context is the Context of the Fragment/Activity
     * @return Integer representing the Maximum value supported by the 'Buffer to Start Period' NumberPickerPreference
     */
    public static int getStartPeriodBufferMaxValue(Context context) {
        return context.getResources().getInteger(R.integer.pref_start_period_buffer_max_value);
    }

}