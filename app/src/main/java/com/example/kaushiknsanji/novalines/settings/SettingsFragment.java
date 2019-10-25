package com.example.kaushiknsanji.novalines.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;

import com.example.kaushiknsanji.novalines.R;
import com.example.kaushiknsanji.novalines.utils.PreferencesUtility;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

/**
 * {@link PreferenceFragmentCompat} class that loads the Preferences for
 * the app's Settings
 *
 * @author Kaushik N Sanji
 */
public class SettingsFragment extends PreferenceFragmentCompat
        implements Preference.OnPreferenceChangeListener {

    //OOB Constant used as an identifier for the DialogFragments
    private static final String DIALOG_FRAGMENT_TAG = "android.support.v7.preference.PreferenceFragment.DIALOG";
    //Context of the Fragment
    private Context mContext;


    /**
     * Called during {@link #onCreate(Bundle)} to supply the preferences for this fragment.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     * @param rootKey            If non-null, this preference fragment should be rooted at the
     *                           {@link PreferenceScreen} with this key.
     */
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        //Load Preferences from XML Resource
        addPreferencesFromResource(R.xml.preferences);
        //Get Context
        mContext = requireContext();
        //Bind Preferences' summary to their value: START
        bindPreferenceSummaryToValue(findPreference(PreferencesUtility.getSortByKey(mContext)));
        bindPreferenceSummaryToValue(findPreference(PreferencesUtility.getSortBasedOnKey(mContext)));
        bindPreferenceSummaryToValue(findPreference(PreferencesUtility.getItemsPerPageKey(mContext)));
        bindPreferenceSummaryToValue(findPreference(PreferencesUtility.getStartPeriodOverrideKey(mContext)));
        bindPreferenceSummaryToValue(findPreference(PreferencesUtility.getPresetStartPeriodKey(mContext)));
        bindPreferenceSummaryToValue(findPreference(PreferencesUtility.getStartPeriodBufferKey(mContext)));
        bindPreferenceSummaryToValue(findPreference(PreferencesUtility.getStartPeriodKey(mContext)));
        bindPreferenceSummaryToValue(findPreference(PreferencesUtility.getResetSettingsKey(mContext)));
        //Bind Preferences' summary to their value: END

        //Enabling the preferences dependent on "Start Period Preset/Manual" CheckBoxPreference
        updateStartPeriodDependencies();

    }

    /**
     * Method that binds the Preferences' summary to its current value
     *
     * @param preference is the {@link Preference} whose summary is to be set to its value
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        if (preference instanceof ListPreference) {
            //Setting Summary for ListPreferences
            ListPreference listPreference = (ListPreference) preference;
            listPreference.setSummary(listPreference.getEntry());
        } else if (preference instanceof NumberPickerPreference) {
            //Setting Summary for NumberPickerPreferences
            NumberPickerPreference numberPickerPreference = (NumberPickerPreference) preference;
            int selectedValue = numberPickerPreference.getValue();

            if (preference.getKey().equals(PreferencesUtility.getStartPeriodBufferKey(mContext))) {
                //Adding custom text for the "Buffer to Start Period" Preference setting
                numberPickerPreference.setSummary(getString(R.string.buffer_days_summary_text, selectedValue));
            } else {
                //For others, just displaying the value AS-IS
                numberPickerPreference.setSummary(String.valueOf(selectedValue));
            }

        } else if (preference.getKey().equals(PreferencesUtility.getStartPeriodOverrideKey(mContext))) {
            //Setting Summary for the "Start Period Preset/Manual" CheckBoxPreference

            //Updating the summary
            updateStartPeriodSummary(-1);

        }
        //Attaching the OnPreferenceChangeListener on the Preference
        preference.setOnPreferenceChangeListener(this);
    }

    /**
     * Method that updates the summary for the Start Period
     * in the "Start Period Preset/Manual" CheckBoxPreference
     *
     * @param dateTimeInMillis is the DateTime in Millis, that when passed
     *                         will be used for updating the summary.
     *                         <br/> When -1, the DateTime value will be read from the "from-date" key
     */
    private void updateStartPeriodSummary(long dateTimeInMillis) {
        //"Start Period Preset/Manual" CheckBoxPreference
        CheckBoxPreference spOverridePreference = (CheckBoxPreference) findPreference(PreferencesUtility.getStartPeriodOverrideKey(mContext));

        //Declaring the date instance for retrieving the date to be shown
        Date fromDateSet;
        if (dateTimeInMillis == -1) {
            //Retrieving the DateTime value from the "from-date" key when DateTime is not passed
            DatePickerPreference datePickerPreference = (DatePickerPreference) findPreference(PreferencesUtility.getStartPeriodKey(mContext));
            fromDateSet = new Date(datePickerPreference.getDateTimeInMillis());
        } else {
            //Honouring the DateTime when passed
            fromDateSet = new Date(dateTimeInMillis);
        }

        //Converting the Date to a readable format like 'January 01, 2017'
        String fromDateSetStr = DateFormat.getDateInstance(DateFormat.LONG).format(fromDateSet);

        //Setting Summary based on the DateTime value
        //and the current setting of "Start Period Preset/Manual" CheckBoxPreference
        if (spOverridePreference.isChecked()) {
            //When checked, Start Period will be set Manually
            //by the "Specify Start Period" Preference
            spOverridePreference.setSummary(getString(R.string.manual_date_summary_text, fromDateSetStr));
        } else {
            //When un-checked, Start Period will be determined automatically
            //based on the preset preferences
            spOverridePreference.setSummary(getString(R.string.predetermined_date_summary_text, fromDateSetStr));
        }
    }

    /**
     * Method that enables the required preferences based on the current setting
     * of the "Start Period Preset/Manual" CheckBoxPreference
     */
    private void updateStartPeriodDependencies() {
        //"Start Period Preset/Manual" CheckBoxPreference
        CheckBoxPreference spOverridePreference = (CheckBoxPreference) findPreference(PreferencesUtility.getStartPeriodOverrideKey(mContext));
        //Preferences dependent on the "Start Period Preset/Manual" CheckBoxPreference
        ListPreference spPresetPreference = (ListPreference) findPreference(PreferencesUtility.getPresetStartPeriodKey(mContext));
        NumberPickerPreference spBufferPreference = (NumberPickerPreference) findPreference(PreferencesUtility.getStartPeriodBufferKey(mContext));
        DatePickerPreference spManualPreference = (DatePickerPreference) findPreference(PreferencesUtility.getStartPeriodKey(mContext));

        //Enabling the preferences dependent on "Start Period Preset/Manual" CheckBoxPreference
        //based on the current setting of this CheckBox
        if (spOverridePreference.isChecked()) {
            //When checked, Start Period will be set Manually
            //by the "Specify Start Period" Preference
            spManualPreference.setEnabled(true);
            spPresetPreference.setEnabled(false);
            spBufferPreference.setEnabled(false);
        } else {
            //When un-checked, Start Period will be determined automatically
            //based on the preset preferences
            spPresetPreference.setEnabled(true);
            spBufferPreference.setEnabled(true);
            spManualPreference.setEnabled(false);
        }
    }

    /**
     * Method that calculates the Start Period value based on the preference
     * being changed and updates the value to the "Specify Start Period" DatePickerPreference
     *
     * @param preference The preference whose value is being changed. Invoked by the method {@link #onPreferenceChange(Preference, Object)}
     * @return long value containing the DateTime in Millis calculated for the preference being changed
     */
    private long getStartPeriodValue(Preference preference) {
        long dateTimeInMillis = -1; //Defaulting the DateTime value to be calculated, to -1

        //Retrieving the "Specify Start Period" DatePickerPreference
        DatePickerPreference spManualPreference = (DatePickerPreference) findPreference(PreferencesUtility.getStartPeriodKey(mContext));

        if (preference.getKey().equals(PreferencesUtility.getPresetStartPeriodKey(mContext))
                || preference.getKey().equals(PreferencesUtility.getStartPeriodBufferKey(mContext))) {
            //When the "Preset Start Period" Preference or the "Buffer to Start Period" Preference is being changed

            //Updating the "from-date" key with the DateTime value calculated from the Preset Date setting
            dateTimeInMillis = getPresetStartPeriodValue();
            spManualPreference.setDateTimeInMillis(dateTimeInMillis);

        } else if (preference.getKey().equals(PreferencesUtility.getStartPeriodOverrideKey(mContext))) {
            //When the "Start Period Preset/Manual" CheckBoxPreference is being changed

            //Retrieving the reference to the CheckBoxPreference
            CheckBoxPreference spOverridePreference = (CheckBoxPreference) preference;

            if (spOverridePreference.isChecked()) {
                //When checked, Start Period will be set Manually
                //by the "Specify Start Period" Preference
                dateTimeInMillis = spManualPreference.getDateTimeInMillis();
            } else {
                //When un-checked, Start Period will be determined automatically
                //based on the preset preferences

                //Updating the "from-date" key with the DateTime value calculated from the Preset Date setting
                dateTimeInMillis = getPresetStartPeriodValue();
                spManualPreference.setDateTimeInMillis(dateTimeInMillis);
            }
        }

        //Returning the calculated DateTime in Millis
        return dateTimeInMillis;
    }

    /**
     * Method that calculates and returns the Start Period value based on the setting of
     * "Preset Start Period" Preference and the linked "Buffer to Start Period" Preference
     *
     * @return long value containing the DateTime in Millis calculated for the Preset Date setting
     */
    private long getPresetStartPeriodValue() {
        //Retrieving the reference to "Preset Start Period" Preference
        Preference spPresetPreference = findPreference(PreferencesUtility.getPresetStartPeriodKey(mContext));
        //Retrieving the "Preset Start Period" Preference summary
        String spPresetPreferenceSummary = spPresetPreference.getSummary().toString();
        //Retrieving the list of values used in the preference
        String[] availablePresets = PreferencesUtility.getPossiblePresetStartPeriodValues(mContext);

        //Getting the current calendar instance
        Calendar dateCalendar = Calendar.getInstance();

        if (spPresetPreferenceSummary.equals(availablePresets[0])) {
            //When the option selected was "Start of the Week"

            //Setting the calendar to the locale's week's start day
            dateCalendar.set(Calendar.DAY_OF_WEEK, dateCalendar.getFirstDayOfWeek());
        } else if (spPresetPreferenceSummary.equals(availablePresets[1])) {
            //When the option selected was "Start of the Month"

            //Setting the calendar to the start of the Month
            dateCalendar.set(Calendar.DAY_OF_MONTH, 1);
        }
        //For the option "Start of Today", we are using the current day date AS-IS

        //Retrieving the reference to "Buffer to Start Period" Preference
        NumberPickerPreference spBufferPreference = (NumberPickerPreference) findPreference(PreferencesUtility.getStartPeriodBufferKey(mContext));
        //Retrieving the "Buffer to Start Period" Preference summary
        String spBufferPreferenceSummary = spBufferPreference.getSummary().toString();

        //Retrieving the buffer value selected from the summary set in onPreferenceChange
        int selectedValue = Integer.parseInt(TextUtils.substring(
                spBufferPreferenceSummary,
                0,
                spBufferPreferenceSummary.indexOf("days"))
                .trim()
        );

        //Subtracting the calendar date by the Buffer value selected
        dateCalendar.add(Calendar.DAY_OF_YEAR, -selectedValue);

        //Returning the calculated DateTime in Millis
        return dateCalendar.getTimeInMillis();
    }

    /**
     * Called when a Preference has been changed by the user. This is
     * called before the state of the Preference is about to be updated and
     * before the state is persisted.
     *
     * @param preference The changed Preference.
     * @param newValue   The new value of the Preference.
     * @return True to update the state of the Preference with the new value.
     */
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference instanceof ListPreference) {
            //For Preferences of type ListPreference
            ListPreference listPreference = (ListPreference) preference;
            //Finding the index of the value selected
            int selectedIndex = listPreference.findIndexOfValue(newValue.toString());
            //Updating the Summary
            listPreference.setSummary(listPreference.getEntries()[selectedIndex]);

            if (preference.getKey().equals(PreferencesUtility.getPresetStartPeriodKey(mContext))) {
                //Retrieving the Start Period value and updating its summary when a Start Period Preset is selected
                //in the "Preset Start Period" Preference setting
                updateStartPeriodSummary(getStartPeriodValue(preference));
            }

        } else if (preference instanceof NumberPickerPreference) {
            //For Preferences of type NumberPickerPreference

            if (preference.getKey().equals(PreferencesUtility.getStartPeriodBufferKey(mContext))) {
                //Adding custom text for the "Buffer to Start Period" Preference setting
                preference.setSummary(getString(R.string.buffer_days_summary_text, (int) newValue));
                //Retrieving the Start Period value and updating its summary accordingly
                updateStartPeriodSummary(getStartPeriodValue(preference));
            } else {
                //For others, just displaying the value AS-IS
                preference.setSummary(String.valueOf(newValue));
            }

        } else if (preference.getKey().equals(PreferencesUtility.getStartPeriodOverrideKey(mContext))) {
            //For the "Start Period Preset/Manual" CheckBoxPreference
            CheckBoxPreference spOverridePreference = (CheckBoxPreference) preference;

            //Updating the value
            spOverridePreference.setChecked((boolean) newValue);

            //Updating the dependencies accordingly
            updateStartPeriodDependencies();

            //Retrieving the Start Period value and updating its summary accordingly
            updateStartPeriodSummary(getStartPeriodValue(preference));

            //Returning false as it has been already updated
            return false;

        } else if (preference.getKey().equals(PreferencesUtility.getStartPeriodKey(mContext))) {
            //For the "Specify Start Period" DatePickerPreference

            //Updating the Summary of Start Period
            updateStartPeriodSummary((long) newValue);

        } else if (preference instanceof ConfirmationPreference) {
            //For Preferences of type ConfirmationPreference
            boolean confirmationResult = (boolean) newValue;
            if (confirmationResult) {
                //If result is True, then reset all the Preferences to their defaults
                resetAllToDefaults();
            }
            //Returning false to prevent updating the state of this Preference
            return false;
        }
        //Returning true for all the other preferences
        return true;
    }

    /**
     * Method invoked on positive confirmation to "Reset Settings" preference
     * to reset all the Preferences used by the app
     */
    private void resetAllToDefaults() {
        //Retrieving the Preferences
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();

        //Iterating over the Preference Keys to reset them to their defaults: START
        Set<String> prefKeySet = sharedPreferences.getAll().keySet();
        for (String prefKeyStr : prefKeySet) {
            //Switching based on the preference key and setting to their defaults
            if (prefKeyStr.equals(PreferencesUtility.getSortByKey(mContext))) {
                bindPreferenceToDefaultValue(findPreference(prefKeyStr),
                        PreferencesUtility.getDefaultSortByValue(mContext));
            } else if (prefKeyStr.equals(PreferencesUtility.getSortBasedOnKey(mContext))) {
                bindPreferenceToDefaultValue(findPreference(prefKeyStr),
                        PreferencesUtility.getDefaultSortBasedOnValue(mContext));
            } else if (prefKeyStr.equals(PreferencesUtility.getItemsPerPageKey(mContext))) {
                bindPreferenceToDefaultValue(findPreference(prefKeyStr),
                        PreferencesUtility.getDefaultItemsPerPage(mContext));
            } else if (prefKeyStr.equals(PreferencesUtility.getStartPeriodOverrideKey(mContext))) {
                bindPreferenceToDefaultValue(findPreference(prefKeyStr),
                        PreferencesUtility.getDefaultStartPeriodOverrideValue(mContext));
            } else if (prefKeyStr.equals(PreferencesUtility.getPresetStartPeriodKey(mContext))) {
                bindPreferenceToDefaultValue(findPreference(prefKeyStr),
                        PreferencesUtility.getDefaultPresetStartPeriodValue(mContext));
            } else if (prefKeyStr.equals(PreferencesUtility.getStartPeriodBufferKey(mContext))) {
                bindPreferenceToDefaultValue(findPreference(prefKeyStr),
                        PreferencesUtility.getDefaultStartPeriodBufferValue(mContext));
            } else if (prefKeyStr.equals(PreferencesUtility.getStartPeriodKey(mContext))) {
                bindPreferenceToDefaultValue(findPreference(prefKeyStr), Calendar.getInstance().getTimeInMillis());
            }
        }
        //Iterating over the Preference Keys to reset them to their defaults: END

        //Forcibly marking all preferences to their defaults using PreferenceManager
        PreferenceManager.setDefaultValues(mContext, R.xml.preferences, true);
    }

    /**
     * Method to force bind the Preferences to their default values,
     * invoked on positive confirmation to "Reset Settings" preference
     *
     * @param preference   The Preference to be modified to reflect its Default value
     * @param defaultValue The Default Value of the Preference
     */
    private void bindPreferenceToDefaultValue(Preference preference, Object defaultValue) {
        if (preference instanceof ListPreference) {
            //For Preferences of type ListPreference

            ListPreference listPreference = (ListPreference) preference;
            //Finding the index of the default value
            int selectedIndex = listPreference.findIndexOfValue(defaultValue.toString());
            //Updating the Summary
            listPreference.setSummary(listPreference.getEntries()[selectedIndex]);
            //Setting the Default value
            listPreference.setValue(defaultValue.toString());

        } else if (preference instanceof NumberPickerPreference) {
            //For Preferences of type NumberPickerPreference

            //Setting the Default value
            ((NumberPickerPreference) preference).setValue((int) defaultValue);

            if (preference.getKey().equals(PreferencesUtility.getStartPeriodBufferKey(mContext))) {
                //Adding custom text for the "Buffer to Start Period" Preference setting
                preference.setSummary(getString(R.string.buffer_days_summary_text, (int) defaultValue));
            } else {
                //For others, just displaying the value AS-IS
                preference.setSummary(String.valueOf(defaultValue));
            }

        } else if (preference.getKey().equals(PreferencesUtility.getStartPeriodOverrideKey(mContext))) {
            //For the "Start Period Preset/Manual" CheckBoxPreference
            CheckBoxPreference spOverridePreference = (CheckBoxPreference) preference;

            //Setting the Default value
            spOverridePreference.setChecked((boolean) defaultValue);

            //Updating the dependencies accordingly
            updateStartPeriodDependencies();

        } else if (preference.getKey().equals(PreferencesUtility.getStartPeriodKey(mContext))) {
            //For the "Specify Start Period" DatePickerPreference

            //Setting the Default value
            ((DatePickerPreference) preference).setDateTimeInMillis((long) defaultValue);

            //Updating the Summary of Start Period
            updateStartPeriodSummary((long) defaultValue);
        }
    }

    /**
     * Called when a custom preference in the tree requests to display a dialog.
     *
     * @param preference The Custom Preference object requesting the dialog.
     */
    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        //Evaluating based on the kind of Preference
        if (preference instanceof NumberPickerPreference) {
            //If the preference is a NumberPickerPreference then display the
            //NumberPicker Dialog

            //Retrieving the Preference's Key
            String preferenceKey = preference.getKey();

            //Declaring the NumberPicker's DialogFragment
            DialogFragment dialogFragment = null;

            if (!anyDialogActive()) {
                //Initializing the NumberPicker DialogFragment based on the Preference Key
                //when no Dialog is found to be active
                if (preferenceKey.equals(PreferencesUtility.getItemsPerPageKey(mContext))) {
                    //Initializing the NumberPicker DialogFragment for "News items per page" Preference setting
                    dialogFragment = NumberPickerPreferenceDialogFragmentCompat.newInstance(
                            preferenceKey,
                            PreferencesUtility.getItemsPerPageMinValue(mContext),
                            PreferencesUtility.getItemsPerPageMaxValue(mContext)
                    );
                } else if (preferenceKey.equals(PreferencesUtility.getStartPeriodBufferKey(mContext))) {
                    //Initializing the NumberPicker DialogFragment for "Buffer to Start Period" Preference setting
                    dialogFragment = NumberPickerPreferenceDialogFragmentCompat.newInstance(
                            preferenceKey,
                            PreferencesUtility.getStartPeriodBufferMinValue(mContext),
                            PreferencesUtility.getStartPeriodBufferMaxValue(mContext)
                    );
                }
            }

            //Displaying the NumberPicker Dialog
            if (dialogFragment != null) {
                dialogFragment.setTargetFragment(this, 0);
                dialogFragment.show(requireFragmentManager(), DIALOG_FRAGMENT_TAG);
            }

        } else if (preference instanceof DatePickerPreference) {
            //If the preference is a DatePickerPreference then display the
            //DatePicker Dialog

            DatePickerPreference datePickerPreference = (DatePickerPreference) preference;

            //Declaring the DatePicker's DialogFragment
            DialogFragment dialogFragment = null;

            if (!anyDialogActive()) {
                //Initializing the DatePicker DialogFragment for the "Specify Start Period" Preference setting
                //when no Dialog is found to be active
                dialogFragment = DatePickerPreferenceDialogFragmentCompat.newInstance(
                        datePickerPreference.getKey(),
                        datePickerPreference.getDateTimeInMillis()
                );
            }

            //Displaying the DatePicker Dialog
            if (dialogFragment != null) {
                dialogFragment.setTargetFragment(this, 0);
                dialogFragment.show(requireFragmentManager(), DIALOG_FRAGMENT_TAG);
            }

        } else if (preference instanceof ConfirmationPreference) {
            //If the preference is a ConfirmationPreference
            //then display the Confirmation Dialog

            //Declaring the DialogFragment for Confirmation
            DialogFragment dialogFragment = null;

            if (!anyDialogActive()) {
                //Initializing the ConfirmationPreference DialogFragment when no Dialog is found to be active
                dialogFragment = ConfirmationPreferenceDialogFragmentCompat.newInstance(preference.getKey());
            }

            //Displaying the Confirmation Dialog
            if (dialogFragment != null) {
                dialogFragment.setTargetFragment(this, 0);
                dialogFragment.show(requireFragmentManager(), DIALOG_FRAGMENT_TAG);
            }

        } else {
            //For all else, calling to super
            super.onDisplayPreferenceDialog(preference);
        }
    }

    /**
     * Method that checks if any DialogFragment is still displayed/active
     *
     * @return <b>TRUE</b> when there is a Dialog active and shown; <b>FALSE</b> otherwise
     */
    private boolean anyDialogActive() {
        return (requireFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG) != null);
    }
}
