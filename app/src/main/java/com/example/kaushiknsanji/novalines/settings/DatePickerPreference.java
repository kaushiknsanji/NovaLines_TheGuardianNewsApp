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

package com.example.kaushiknsanji.novalines.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;

import com.example.kaushiknsanji.novalines.R;

import java.util.Calendar;

/**
 * Custom {@link DialogPreference} class for {@link android.widget.DatePicker}
 *
 * @author Kaushik N Sanji
 */
public class DatePickerPreference extends DialogPreference {

    //Constant used as the default fallback value when the preference value is not available
    private static final long FALLBACK_VALUE = Calendar.getInstance().getTimeInMillis();
    //Stores the reference to the Date Picker Layout Resource
    private final int mDialogLayoutResourceId = R.layout.pref_date_picker_dialog;
    //Stores the value of the Calendar date picked by the User, in milliseconds
    private long mDateTimeInMillis;

    public DatePickerPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        //Propagate call to super class
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public DatePickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        //Delegate to other constructor
        this(context, attrs, defStyleAttr, 0);
    }

    public DatePickerPreference(Context context, AttributeSet attrs) {
        //Delegate to other constructor
        //Using the dialogPreferenceStyle as the default style
        this(context, attrs, R.attr.dialogPreferenceStyle);
    }

    public DatePickerPreference(Context context) {
        //Delegate to other constructor
        this(context, null);
    }

    /**
     * Getter Method for retrieving the current Date Picked value
     *
     * @return Date value of the preference in milliseconds
     */
    public long getDateTimeInMillis() {
        return mDateTimeInMillis;
    }

    /**
     * Setter Method for saving the Date Picked by the User
     * to the {@link android.content.SharedPreferences} and to the member {@link #mDateTimeInMillis}
     *
     * @param dateTimeInMillis is the Calendar date picked by the User, mentioned in milliseconds since UNIX epoch
     */
    public void setDateTimeInMillis(long dateTimeInMillis) {
        this.mDateTimeInMillis = dateTimeInMillis;

        //Saving the value in the SharedPreferences
        persistLong(mDateTimeInMillis);
    }

    /**
     * Called when a Preference is being inflated and the default value
     * attribute needs to be read.
     *
     * @param a     The set of attributes.
     * @param index The index of the default value attribute.
     * @return The default value of this preference type.
     */
    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        //Returning the current date as the default value in milliseconds
        return FALLBACK_VALUE;
    }

    /**
     * Method to set the initial value of the Preference.
     *
     * @param restorePersistedValue True to restore the persisted value;
     *                              false to use the given <var>defaultValue</var>.
     * @param defaultValue          The default value for this Preference. Only use this
     *                              if <var>restorePersistedValue</var> is false.
     */
    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setDateTimeInMillis(restorePersistedValue ? getPersistedLong(FALLBACK_VALUE) : (long) defaultValue);
    }

    /**
     * Returns the layout resource that is used as the content View for
     * subsequent dialogs.
     *
     * @return The layout resource of the dialog
     */
    @Override
    public int getDialogLayoutResource() {
        return mDialogLayoutResourceId;
    }

    /**
     * Hook allowing a Preference to generate a representation of its internal
     * state that can later be used to create a new instance with that same
     * state. This state should only contain information that is not persistent
     * or can be reconstructed later.
     *
     * @return A Parcelable object containing the current dynamic state of
     * this Preference, or null if there is nothing interesting to save.
     * The default implementation returns null.
     * @see #onRestoreInstanceState
     * @see #saveHierarchyState
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        //Checking if the Preference state is persisted
        if (isPersistent()) {
            //No need to save the state as it is persistent. Hence returning the superState
            return superState;
        }

        //Creating an instance of the custom BaseSavedState
        SavedState savedState = new SavedState(superState);
        //Saving the state with the current preference value
        savedState.stateValue = getDateTimeInMillis();

        //Returning the custom BaseSavedState
        return savedState;
    }

    /**
     * Hook allowing a Preference to re-apply a representation of its internal
     * state that had previously been generated by {@link #onSaveInstanceState}.
     * This function will never be called with a null state.
     *
     * @param state The saved state that had previously been returned by
     *              {@link #onSaveInstanceState}.
     * @see #onSaveInstanceState
     * @see #restoreHierarchyState
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        //Checking whether the state was saved in the onSaveInstanceState
        if (state == null || !state.getClass().equals(SavedState.class)) {
            //Calling to the superclass when the state was never saved
            super.onRestoreInstanceState(state);
            return;
        }

        //Casting the saved state to the custom BaseSavedState
        SavedState savedState = (SavedState) state;
        //Passing the super state to the Super class
        super.onRestoreInstanceState(savedState.getSuperState());

        //Restoring the state to the member to reflect the current state
        setDateTimeInMillis(savedState.stateValue);
    }

    /**
     * Inner class that defines the state of the Preference
     */
    private static class SavedState extends BaseSavedState {

        //Standard Parcelable CREATOR object using an instance of this class
        public static final Parcelable.Creator<SavedState> CREATOR
                = new Creator<SavedState>() {
            /**
             * Creates a new instance of the Parcelable class, instantiating it
             * from the given Parcel whose data had previously been written by
             * {@link Parcelable#writeToParcel Parcelable.writeToParcel()}.
             *
             * @param source The Parcel to read the object's data from.
             * @return Returns a new instance of the Parcelable class.
             */
            @Override
            public SavedState createFromParcel(Parcel source) {
                //Returning an instance of this class
                return new SavedState(source);
            }

            /**
             * Creates a new array of the Parcelable class.
             *
             * @param size Size of the array.
             * @return Returns an array of the Parcelable class, with every entry
             * initialized to null.
             */
            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        //Holds the setting's value for persisting the Preference value
        long stateValue;

        public SavedState(Parcel source) {
            super(source);
            //Get the current Preference value
            stateValue = source.readLong();
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            //Write the Preference value
            dest.writeLong(stateValue);
        }

    }

}
