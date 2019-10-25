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

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Utility class that provides convenience methods
 * for working with the {@link java.util.Date} and {@link Calendar}
 *
 * @author Kaushik N Sanji
 */
public class DateUtility {

    /**
     * Method that removes/strips the time part from the calendar
     *
     * @param calendar is the Calendar instance on which the time part needs to be unset/cleared
     */
    public static void pruneTimePart(Calendar calendar) {
        if (calendar == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        //Clearing all hours, minutes, seconds and milliseconds
        calendar.clear(Calendar.HOUR_OF_DAY);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
    }

    /**
     * Method that removes/strips the time part from the date passed in milliseconds since unix epoch
     *
     * @param dateTimeInMillis is the date passed in milliseconds since unix epoch whose time part needs to be unset/cleared
     * @return The datetime in milliseconds since unix epoch whose time part is unset/cleared
     */
    public static long pruneTimePart(long dateTimeInMillis) {
        //Setting up the Calendar for the datetime passed
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(dateTimeInMillis);
        //Clearing the time part
        pruneTimePart(calendar);
        //Returning the result in milliseconds
        return calendar.getTimeInMillis();
    }

    /**
     * Method that calculates and returns the difference in Days, between the two datetimes passed in milliseconds
     *
     * @param dateTimeInMillisOne is the datetime in milliseconds since unix epoch
     * @param dateTimeInMillisTwo is the other datetime in milliseconds since unix epoch
     * @return long value representing the difference in Days between the two datetimes passed
     */
    public static long getDifferenceInDays(long dateTimeInMillisOne, long dateTimeInMillisTwo) {
        return TimeUnit.MILLISECONDS.toDays(Math.abs(dateTimeInMillisOne - dateTimeInMillisTwo));
    }

    /**
     * Method that calculates and returns the difference in Days, between the two {@link Calendar} dates passed
     *
     * @param dateTimeCalendarOne is a Calendar of some date
     * @param dateTimeCalendarTwo is another Calendar of some date
     * @return long value representing the difference in Days between the two {@link Calendar} dates passed
     */
    public static long getDifferenceInDays(Calendar dateTimeCalendarOne, Calendar dateTimeCalendarTwo) {
        if (dateTimeCalendarOne == null || dateTimeCalendarTwo == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return getDifferenceInDays(dateTimeCalendarOne.getTimeInMillis(), dateTimeCalendarTwo.getTimeInMillis());
    }

    /**
     * Methods that checks if two calendar objects are on the same day ignoring time.
     *
     * @param dateTimeCalendarOne is a Calendar of some date
     * @param dateTimeCalendarTwo is a Calendar of some date
     * @return <b>TRUE</b> if they represent the same day; <b>FALSE</b> otherwise.
     */
    public static boolean isSameDay(Calendar dateTimeCalendarOne, Calendar dateTimeCalendarTwo) {
        if (dateTimeCalendarOne == null || dateTimeCalendarTwo == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        return (dateTimeCalendarOne.get(Calendar.ERA) == dateTimeCalendarTwo.get(Calendar.ERA) &&
                dateTimeCalendarOne.get(Calendar.YEAR) == dateTimeCalendarTwo.get(Calendar.YEAR) &&
                dateTimeCalendarOne.get(Calendar.DAY_OF_YEAR) == dateTimeCalendarTwo.get(Calendar.DAY_OF_YEAR));
    }

    /**
     * Methods that checks if two datetimes passed in milliseconds are on the same day ignoring time.
     *
     * @param dateTimeInMillisOne is the datetime in milliseconds since unix epoch
     * @param dateTimeInMillisTwo is the other datetime in milliseconds since unix epoch
     * @return <b>TRUE</b> if they represent the same day; <b>FALSE</b> otherwise.
     */
    public static boolean isSameDay(long dateTimeInMillisOne, long dateTimeInMillisTwo) {
        //Setting up Calendar objects for the datetimes passed
        Calendar dateTimeCalendarOne = Calendar.getInstance();
        dateTimeCalendarOne.setTimeInMillis(dateTimeInMillisOne);
        Calendar dateTimeCalendarTwo = Calendar.getInstance();
        dateTimeCalendarTwo.setTimeInMillis(dateTimeInMillisTwo);
        //Returning the result of comparison
        return isSameDay(dateTimeCalendarOne, dateTimeCalendarTwo);
    }

}
