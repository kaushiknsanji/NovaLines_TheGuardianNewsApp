package com.example.kaushiknsanji.novalines.settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.View;
import android.widget.DatePicker;

import com.example.kaushiknsanji.novalines.R;

import java.util.Calendar;

/**
 * {@link PreferenceDialogFragmentCompat} class that displays and manages the DatePicker dialog
 * shown for the custom {@link DatePickerPreference} class
 *
 * @author Kaushik N Sanji
 */
public class DatePickerPreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat
        implements DatePicker.OnDateChangedListener {

    //Constant used for logs
    private static final String LOG_TAG = DatePickerPreferenceDialogFragmentCompat.class.getSimpleName();

    //Bundle Key constants
    private static final String DATE_PICKER_YEAR_INT_KEY = "datePicker.year";
    private static final String DATE_PICKER_MONTH_INT_KEY = "datePicker.month";
    private static final String DATE_PICKER_DAY_INT_KEY = "datePicker.day";

    //Stores the reference to the DatePicker in the Dialog
    private DatePicker mDatePicker;

    //Saves the values selected by the user for restoring the intermediate state
    private int mYear = -1;
    private int mMonth = -1;
    private int mDay = -1;

    /**
     * Static Constructor of the PreferenceDialogFragmentCompat {@link DatePickerPreferenceDialogFragmentCompat}
     *
     * @param key              is the Preference key for the {@link DatePickerPreference}
     * @param dateTimeInMillis is the value set for the DatePicker in milliseconds since UNIX epoch
     * @return instance of {@link DatePickerPreferenceDialogFragmentCompat}
     */
    public static DatePickerPreferenceDialogFragmentCompat
    newInstance(String key, long dateTimeInMillis) {
        final DatePickerPreferenceDialogFragmentCompat dialogFragmentCompat
                = new DatePickerPreferenceDialogFragmentCompat();

        //Preparing the Calendar for the default date passed
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTimeInMillis(dateTimeInMillis);

        //Saving the arguments passed, in a Bundle: START
        final Bundle bundle = new Bundle(4);
        bundle.putString(ARG_KEY, key);
        bundle.putInt(DATE_PICKER_YEAR_INT_KEY, dateCal.get(Calendar.YEAR));
        bundle.putInt(DATE_PICKER_MONTH_INT_KEY, dateCal.get(Calendar.MONTH));
        bundle.putInt(DATE_PICKER_DAY_INT_KEY, dateCal.get(Calendar.DAY_OF_MONTH));
        dialogFragmentCompat.setArguments(bundle);
        //Saving the arguments passed, in a Bundle: END

        //Returning the instance
        return dialogFragmentCompat;
    }

    //Used to restore the Preference's state from the Bundle
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            //Restoring the previous values of DatePicker
            mYear = savedInstanceState.getInt(DATE_PICKER_YEAR_INT_KEY);
            mMonth = savedInstanceState.getInt(DATE_PICKER_MONTH_INT_KEY);
            mDay = savedInstanceState.getInt(DATE_PICKER_DAY_INT_KEY);
        }
    }

    //Saves the Preference's state in the bundle
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        //Saving the intermediate values of the DatePicker
        outState.putInt(DATE_PICKER_YEAR_INT_KEY, mDatePicker.getYear());
        outState.putInt(DATE_PICKER_MONTH_INT_KEY, mDatePicker.getMonth());
        outState.putInt(DATE_PICKER_DAY_INT_KEY, mDatePicker.getDayOfMonth());
        super.onSaveInstanceState(outState);
    }

    /**
     * Binds views in the content View of the dialog to data.
     *
     * @param view The content View of the dialog, if it is custom.
     */
    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        //Retrieving the DatePicker from the view
        mDatePicker = view.findViewById(R.id.pref_date_picker_id);

        //Throwing an exception when no DatePicker is found
        if (mDatePicker == null) {
            throw new IllegalStateException("Dialog view must contain a DatePicker" +
                    " with the id as 'pref_date_picker_id'");
        }

        //Initializing the DatePicker
        if (mYear == -1 && mMonth == -1 && mDay == -1) {
            //When Year, Month and Day are NOT initialized
            //Retrieving the values from the Bundle arguments
            Bundle bundleArgs = getArguments();
            mYear = bundleArgs.getInt(DATE_PICKER_YEAR_INT_KEY);
            mMonth = bundleArgs.getInt(DATE_PICKER_MONTH_INT_KEY);
            mDay = bundleArgs.getInt(DATE_PICKER_DAY_INT_KEY);
        }
        mDatePicker.init(mYear, mMonth, mDay, this);

        //Setting the bounds of the DatePicker: START
        Calendar minCalendar = Calendar.getInstance();
        minCalendar.set(2013, 0, 1, 0, 0, 0);
        //Setting the Minimum Date to 01st January, 2013
        mDatePicker.setMinDate(minCalendar.getTimeInMillis());
        //Setting the Maximum Date to Current locale date
        mDatePicker.setMaxDate(Calendar.getInstance().getTimeInMillis());
        //Setting the bounds of the DatePicker: END

    }

    /**
     * Invoked when the user submits a response through the dialog
     *
     * @param positiveResult boolean value indicated as True when the user clicks the positive button
     */
    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            //Saving the selected value on click of positive button

            //Retrieving the value selected to save
            Calendar dateSelectedCal = Calendar.getInstance();
            dateSelectedCal.set(mDatePicker.getYear(), mDatePicker.getMonth(),
                    mDatePicker.getDayOfMonth(), 0, 0, 0);
            long selectedDateTimeInMillis = dateSelectedCal.getTimeInMillis();

            //Retrieving the reference to DatePickerPreference
            DatePickerPreference datePickerPreference = getDatePickerPreference();
            //Notifying the onPreferenceChangeListeners and persisting the value if true
            if (datePickerPreference.callChangeListener(selectedDateTimeInMillis)) {
                //Saving the value
                datePickerPreference.setDateTimeInMillis(selectedDateTimeInMillis);
            }
        }
    }

    /**
     * Method that returns the reference to {@link DatePickerPreference}
     *
     * @return Reference to {@link DatePickerPreference}
     */
    private DatePickerPreference getDatePickerPreference() {
        //Retrieving and returning the DatePickerPreference
        return (DatePickerPreference) getPreference();
    }

    /**
     * Called upon a date change.
     *
     * @param view        The view associated with this listener.
     * @param year        The year that was set.
     * @param monthOfYear The month that was set (0-11) for compatibility
     *                    with {@link Calendar}.
     * @param dayOfMonth  The day of the month that was set.
     */
    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        //Storing the values changed
        mYear = year;
        mMonth = monthOfYear;
        mDay = dayOfMonth;
    }

}
