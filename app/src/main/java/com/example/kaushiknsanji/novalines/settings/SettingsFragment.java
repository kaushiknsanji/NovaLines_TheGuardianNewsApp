package com.example.kaushiknsanji.novalines.settings;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;

import com.example.kaushiknsanji.novalines.R;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

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
        //Bind Preferences' summary to their value: START
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_sort_by_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_sort_on_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_items_per_page_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_start_period_manual_override_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_start_period_preset_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_start_period_buffer_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_start_period_manual_key)));
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

            if (preference.getKey().equals(getString(R.string.pref_start_period_buffer_key))) {
                //Adding custom text for the "Buffer to Start Period" Preference setting
                numberPickerPreference.setSummary(getString(R.string.buffer_days_summary_text, selectedValue));
            } else {
                //For others, just displaying the value AS-IS
                numberPickerPreference.setSummary(String.valueOf(selectedValue));
            }

        } else if (preference.getKey().equals(getString(R.string.pref_start_period_manual_override_key))) {
            //Setting Summary & defaults for the "Start Period Preset/Manual" CheckBoxPreference
            CheckBoxPreference checkBoxPreference = (CheckBoxPreference) preference;
            checkBoxPreference.setDefaultValue(Boolean.FALSE); //Setting for Auto/Preset Mode

            //Updating the summary on the "Start Period Preset/Manual" CheckBoxPreference
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
        CheckBoxPreference spOverridePreference = (CheckBoxPreference) findPreference(getString(R.string.pref_start_period_manual_override_key));

        //Declaring the date instance for retrieving the date to be shown
        Date fromDateSet = null;
        if (dateTimeInMillis == -1) {
            //Retrieving the DateTime value from the "from-date" key when DateTime is not passed
            DatePickerPreference datePickerPreference = (DatePickerPreference) findPreference(getString(R.string.pref_start_period_manual_key));
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
        CheckBoxPreference spOverridePreference = (CheckBoxPreference) findPreference(getString(R.string.pref_start_period_manual_override_key));
        //Preferences dependent on the "Start Period Preset/Manual" CheckBoxPreference
        ListPreference spPresetPreference = (ListPreference) findPreference(getString(R.string.pref_start_period_preset_key));
        NumberPickerPreference spBufferPreference = (NumberPickerPreference) findPreference(getString(R.string.pref_start_period_buffer_key));
        DatePickerPreference spManualPreference = (DatePickerPreference) findPreference(getString(R.string.pref_start_period_manual_key));

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
        DatePickerPreference spManualPreference = (DatePickerPreference) findPreference(getString(R.string.pref_start_period_manual_key));

        if (preference.getKey().equals(getString(R.string.pref_start_period_preset_key))
                || preference.getKey().equals(getString(R.string.pref_start_period_buffer_key))) {
            //When the "Preset Start Period" Preference or the "Buffer to Start Period" Preference is being changed

            //Updating the "from-date" key with the DateTime value calculated from the Preset Date setting
            dateTimeInMillis = getPresetStartPeriodValue();
            spManualPreference.setDateTimeInMillis(dateTimeInMillis);

        } else if (preference.getKey().equals(getString(R.string.pref_start_period_manual_override_key))) {
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
        Preference spPresetPreference = findPreference(getString(R.string.pref_start_period_preset_key));
        //Retrieving the "Preset Start Period" Preference summary
        String spPresetPreferenceSummary = spPresetPreference.getSummary().toString();
        //Retrieving the list of values used in the preference
        String[] availablePresets = getResources().getStringArray(R.array.pref_start_period_preset_entries);

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

        //Retrieving the reference to "Buffer to Start Period" Preference
        NumberPickerPreference spBufferPreference = (NumberPickerPreference) findPreference(getString(R.string.pref_start_period_buffer_key));
        //Retrieving the "Buffer to Start Period" Preference summary
        String spBufferPreferenceSummary = spBufferPreference.getSummary().toString();

        //Retrieving the value of the buffer value selected from the summary set in onPreferenceChange
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
            int selectedIndex = listPreference.findIndexOfValue(newValue.toString());
            listPreference.setSummary(listPreference.getEntries()[selectedIndex]);

            if (preference.getKey().equals(getString(R.string.pref_start_period_preset_key))) {
                //Retrieving the Start Period value and updating its summary when a Start Period Preset is selected
                //in the "Preset Start Period" Preference setting
                updateStartPeriodSummary(getStartPeriodValue(preference));
            }

        } else if (preference instanceof NumberPickerPreference) {
            //For Preferences of type NumberPickerPreference

            if (preference.getKey().equals(getString(R.string.pref_start_period_buffer_key))) {
                //Adding custom text for the "Buffer to Start Period" Preference setting
                preference.setSummary(getString(R.string.buffer_days_summary_text, (int) newValue));
                //Retrieving the Start Period value and updating its summary accordingly
                updateStartPeriodSummary(getStartPeriodValue(preference));
            } else {
                //For others, just displaying the value AS-IS
                preference.setSummary(String.valueOf(newValue));
            }

        } else if (preference.getKey().equals(getString(R.string.pref_start_period_manual_override_key))) {
            //For the "Start Period Preset/Manual" CheckBoxPreference
            CheckBoxPreference spOverridePreference = (CheckBoxPreference) preference;

            //Toggling the dependencies
            spOverridePreference.setChecked((boolean) newValue);

            //Updating the dependencies accordingly
            updateStartPeriodDependencies();

            //Retrieving the Start Period value and updating its summary accordingly
            updateStartPeriodSummary(getStartPeriodValue(preference));

            //Returning false as it has been handled already
            return false;

        } else if (preference.getKey().equals(getString(R.string.pref_start_period_manual_key))) {
            //For the "Specify Start Period" DatePickerPreference

            //Updating the Summary of Start Period
            updateStartPeriodSummary((long) newValue);
        }
        //Returning true for all the other preferences
        return true;
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
                if (preferenceKey.equals(getString(R.string.pref_items_per_page_key))) {
                    //Initializing the NumberPicker DialogFragment for "News items per page" Preference setting
                    dialogFragment = NumberPickerPreferenceDialogFragmentCompat.newInstance(
                            preferenceKey,
                            getResources().getInteger(R.integer.pref_items_per_page_min_value),
                            getResources().getInteger(R.integer.pref_items_per_page_max_value)
                    );
                } else if (preferenceKey.equals(getString(R.string.pref_start_period_buffer_key))) {
                    //Initializing the NumberPicker DialogFragment for "Buffer to Start Period" Preference setting
                    dialogFragment = NumberPickerPreferenceDialogFragmentCompat.newInstance(
                            preferenceKey,
                            getResources().getInteger(R.integer.pref_start_period_buffer_min_value),
                            getResources().getInteger(R.integer.pref_start_period_buffer_max_value)
                    );
                }
            }

            //Displaying the NumberPicker Dialog
            if (dialogFragment != null) {
                dialogFragment.setTargetFragment(this, 0);
                dialogFragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
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
                dialogFragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
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
        return (getFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG) != null);
    }
}
