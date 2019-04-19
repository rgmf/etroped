/*
 * Copyright (c) 2017-2019 Román Ginés Martínez Ferrández (rgmf@riseup.net)
 *
 * This file is part of Etroped (http://etroped.rgmf.es).
 *
 * Etroped is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Etroped is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Etroped.  If not, see <https://www.gnu.org/licenses/>.
 */

package es.rgmf.etroped.util;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.TimeZone;

import es.rgmf.etroped.R;
import es.rgmf.etroped.data.EtropedContract;

/**
 * This class contains useful utilities for a Etroped dates and times.
 */
public final class EtropedDateTimeUtils {

    public static final long SECOND_IN_MILLIS = 1000;
    public static final long MINUTE_IN_MILLIS = SECOND_IN_MILLIS * 60;
    public static final long HOUR_IN_MILLIS = MINUTE_IN_MILLIS * 60;
    public static final long DAY_IN_MILLIS = HOUR_IN_MILLIS * 24;

    /**
     * Return the hours, minutes and/or seconds from a time in milliseconds depend on the sport.
     *
     * @param context Android context to access resources.
     * @param milliseconds The number of milliseconds.
     * @param sport The sport id. See {@link es.rgmf.etroped.data.EtropedContract}.
     * @return
     */
    public static String formatTime(Context context, long milliseconds, int sport) {
        int seconds = (int) (milliseconds / 1000) % 60 ;
        int minutes = (int) ((milliseconds / (1000*60)) % 60);
        int hours   = (int) ((milliseconds / (1000*60*60)) % 24);

        if (sport == EtropedContract.SPORT_BIKE) {
            if (hours > 0)
                return String.format(context.getString(R.string.hours_minutes_format),
                        hours, minutes);
            else if (minutes > 0)
                return String.format(context.getString(R.string.minutes_format), minutes);
            else
                return String.format(context.getString(R.string.minutes_seconds_format),
                        minutes, seconds);
        }
        else if (sport == EtropedContract.SPORT_RUNNING || sport == EtropedContract.SPORT_WALKING) {
            if (hours > 0)
                return String.format(context.getString(R.string.hours_minutes_seconds_format),
                        hours, minutes, seconds);
            else
                return String.format(context.getString(R.string.minutes_seconds_format),
                        minutes, seconds);
        }
        else
            throw new UnsupportedOperationException("Unknown sport id: " + sport);
    }

    /**
     * Return the hours, minutes and/or seconds from a time in milliseconds to show on live.
     *
     * @param context Android context to access resources.
     * @param milliseconds The number of milliseconds.
     * @return
     */
    public static String formatTimeOnLive(Context context, long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60 ;
        int minutes = (int) ((milliseconds / (1000*60)) % 60);
        int hours   = (int) ((milliseconds / (1000*60*60)) % 24);

        return String.format(context.getString(R.string.hours_minutes_seconds_format_onlive),
                hours, minutes, seconds);
    }

    /**
     * Return the hours (if any) and minutes from a time in milliseconds.
     *
     * @param context Android context to access resources.
     * @param milliseconds The number of milliseconds.
     * @return For example 12h 30min or simply 47min if empty hours.
     */
    public static String formatTimeHoursMinutes(Context context, long milliseconds) {
        int minutes = (int) ((milliseconds / (1000*60)) % 60);
        int hours   = (int) ((milliseconds / (1000*60*60)) % 24);

        if (hours > 0)
            return String.format(context.getString(R.string.hours_minutes_v2_format),
                    hours, minutes);
        else
            return String.format(context.getString(R.string.minutes_v2_format), minutes);
    }

    /**
     * Helper method to convert the database representation of the date into something to display
     * to users.  As classy and polished a user experience as "20140102" is, we can do better.
     * <p/>
     * The day string for forecast uses the following logic:
     * For today: "Today, June 8"
     * For tomorrow:  "Tomorrow"
     * For the next 5 days: "Wednesday" (just the day name)
     * For all days after that: "Mon, Jun 8" (Mon, 8 Jun in UK, for example)
     *
     * @param context      Context to use for resource localization
     * @param dateInMillis The date in milliseconds (UTC)
     *
     * @return A user-friendly representation of the date in a compress format like 20170503 163040
     */
    public static String getFriendlyDateCompressString(Context context, long dateInMillis) {

        int flags = DateUtils.FORMAT_SHOW_TIME
                | DateUtils.FORMAT_SHOW_DATE
                | DateUtils.FORMAT_NUMERIC_DATE
                | DateUtils.FORMAT_SHOW_YEAR;

        return DateUtils.formatDateTime(context, dateInMillis, flags);
    }

    /**
     * Helper method to convert the database representation of the date into something to display
     * to users.  As classy and polished a user experience as "20140102" is, we can do better.
     * <p/>
     * The day string for forecast uses the following logic:
     * For today: "Today, June 8"
     * For yesterday:  "Yesterday"
     * For the next 5 days: "Wednesday" (just the day name)
     * For all days after that: "Mon, Jun 8" (Mon, 8 Jun in UK, for example)
     *
     * @param context      Context to use for resource localization
     * @param dateInMillis The date in milliseconds (UTC)
     * @param showFullDate Used to show a fuller-version of the date, which always contains either
     *                     the day of the week, today, or tomorrow, in addition to the date.
     *
     * @return A user-friendly representation of the date such as "Today, June 8", "Tomorrow",
     * or "Friday"
     */
    public static String getFriendlyDateString(Context context, long dateInMillis, boolean showFullDate) {

        long localDate = getLocalDateFromUTC(dateInMillis);
        long dayNumber = getDayNumber(localDate);
        long currentDayNumber = getDayNumber(System.currentTimeMillis());

        if (showFullDate) {
            /*
             * If the date we're building the String for is today's date, the format
             * is "Today, June 24"
             */

            String dayName = getDayName(context, localDate);
            String readableDate = getReadableDateString(context, localDate, false);

            // Today or yesterday. Otherwise, is "Tuesday, Jun 24" for example.
            if (currentDayNumber - dayNumber < 2) {
                /*
                 * Since there is no localized format that returns "Today" or "Yesterday" in the API
                 * levels we have to support, we take the name of the day (from SimpleDateFormat)
                 * and use it to replace the date from DateUtils. This isn't guaranteed to work,
                 * but our testing so far has been conclusively positive.
                 *
                 * For information on a simpler API to use (on API > 18), please check out the
                 * documentation on DateFormat#getBestDateTimePattern(Locale, String)
                 * https://developer.android.com/reference/android/text/format/DateFormat.html#getBestDateTimePattern
                 */
                String localizedDayName = new SimpleDateFormat("EEEE").format(localDate);
                return readableDate.replace(localizedDayName, dayName);
            } else {
                return readableDate;
            }
        } else if (currentDayNumber - dayNumber < 7) {
            return getDayName(context, localDate);
        } else {
            int flags = DateUtils.FORMAT_SHOW_DATE
                    | DateUtils.FORMAT_NO_YEAR
                    | DateUtils.FORMAT_ABBREV_ALL
                    | DateUtils.FORMAT_SHOW_WEEKDAY;

            return DateUtils.formatDateTime(context, localDate, flags);
        }
    }

    /**
     * Return an String representing a date (without year).
     *
     * @param context The Android Context.
     * @param dateTimeInMillis Date time in milliseconds.
     * @return A date like mon., dec 3 (spanish: lun., 3 dic).
     */
    public static String getCompressDay(Context context, long dateTimeInMillis) {
        int flags = DateUtils.FORMAT_SHOW_DATE
                    | DateUtils.FORMAT_NO_YEAR
                    | DateUtils.FORMAT_ABBREV_ALL
                    | DateUtils.FORMAT_SHOW_WEEKDAY;

        return DateUtils.formatDateTime(context, dateTimeInMillis, flags);
    }

    /**
     * Helper method to convert the date into something to display to users.
     *
     * @param context Android Context.
     * @param dateInMillis Date in milliseconds.
     * @return A string with a friendly user date time format.
     */
    public static String getFriendlyDateTimeString(Context context, long dateInMillis) {

        long dayNumber = getDayNumber(dateInMillis);
        long currentDayNumber = getDayNumber(System.currentTimeMillis());
        String dayName = getDayName(context, dateInMillis);
        String readableDate = getReadableDateTimeString(context, dateInMillis);

        if (currentDayNumber - dayNumber < 2) {
            String localizedDayName = new SimpleDateFormat("EEEE").format(dateInMillis);
            return readableDate.replace(localizedDayName, dayName);
        } else {
            return readableDate;
        }
    }

    /**
     * Since all dates from the database are in UTC, we must convert the given date
     * (in UTC timezone) to the date in the local timezone. Ths function performs that conversion
     * using the TimeZone offset.
     *
     * @param utcDate The UTC datetime to convert to a local datetime, in milliseconds.
     * @return The local date (the UTC datetime - the TimeZone offset) in milliseconds.
     */
    public static long getLocalDateFromUTC(long utcDate) {
        TimeZone tz = TimeZone.getDefault();
        long gmtOffset = tz.getOffset(utcDate);
        return utcDate - gmtOffset;
    }

    /**
     * This method returns the number of days since the epoch (January 01, 1970, 12:00 Midnight UTC)
     * in UTC time from the current date.
     *
     * @param date A date in milliseconds in local time.
     *
     * @return The number of days in UTC time from the epoch.
     */
    public static long getDayNumber(long date) {
        TimeZone tz = TimeZone.getDefault();
        long gmtOffset = tz.getOffset(date);
        return (date + gmtOffset) / DAY_IN_MILLIS;
    }

    /**
     * @return The first day of current week at 00:00:00 in milliseconds.
     */
    public static long getFirstDayOfThisWeek() {
        // get today and clear time of day
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        // get start of this week in milliseconds
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());

        return cal.getTimeInMillis();


        /*
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, Calendar.MONDAY - calendar.get(Calendar.DAY_OF_WEEK));
        calendar.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        return calendar.getTimeInMillis();
        */
    }

    public static long getFirstDayInMillis(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        return cal.getTimeInMillis();
    }

    public static long getLastDayInMillis(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);

        return cal.getTimeInMillis();
    }

    /**
     * Given a day, returns just the name to use for that day.
     *   E.g "today", "yesterday", "Wednesday".
     *
     * @param context      Context to use for resource localization
     * @param dateInMillis The date in milliseconds (local time)
     *
     * @return the string day of the week
     */
    private static String getDayName(Context context, long dateInMillis) {

        long dayNumber = getDayNumber(dateInMillis);
        long currentDayNumber = getDayNumber(System.currentTimeMillis());
        if (dayNumber == currentDayNumber) {
            return context.getString(R.string.today);
        } else if (dayNumber == currentDayNumber - 1) {
            return context.getString(R.string.yesterday);
        } else {
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
            return dayFormat.format(dateInMillis);
        }
    }

    /**
     * Give a month number (from 0 - January - to 11 - December -) return a month name.
     *
     * @param context
     * @param month
     * @return
     */
    public static String getMonthName(Context context, int month) {

        if (month >= 0 || month <= 11) {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
            cal.set(Calendar.MONTH, month);
            String monthName = month_date.format(cal.getTime());

            return monthName;
        }
        else {
            throw new InvalidParameterException("Month " + month + " does not exists... It needs from 0 to 11");
        }
    }

    /**
     * Returns a date string in the format specified, which shows a date, without a year,
     * abbreviated, showing the full weekday.
     *
     * @param context      Used by DateUtils to formate the date in the current locale
     * @param timeInMillis Time in milliseconds since the epoch (local time)
     * @param year         True if year will add or false otherwise.
     *
     * @return The formatted date string
     */
    private static String getReadableDateString(Context context, long timeInMillis, boolean year) {
        int flags = DateUtils.FORMAT_SHOW_DATE
                | DateUtils.FORMAT_SHOW_WEEKDAY;

        if (year)
            flags |= DateUtils.FORMAT_NO_YEAR;

        return DateUtils.formatDateTime(context, timeInMillis, flags);
    }

    /**
     * Returns a date time string in a readable user format.
     *
     * @param context       Android contexto for DateUtils.
     * @param timeInMillis  Time in milliseconds.
     *
     * @return              The formatted date time string.
     */
    private static String getReadableDateTimeString(Context context, long timeInMillis) {
        int flags = DateUtils.FORMAT_SHOW_DATE
                | DateUtils.FORMAT_SHOW_TIME
                | DateUtils.FORMAT_SHOW_YEAR
                | DateUtils.FORMAT_SHOW_WEEKDAY;

        return DateUtils.formatDateTime(context, timeInMillis, flags);
    }

    /**
     * Create a string array with the name's abbreviation of the months.
     *
     * @return An array with all 12 names months of the year.
     */
    public static String[] getListMonthsNames() {
        int[] months = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        String[] xVals = new String[12];

        for (int i = 0; i < months.length; i++) {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat monthDate = new SimpleDateFormat("MMM");
            cal.set(Calendar.MONTH, months[i]);
            String monthName = monthDate.format(cal.getTime());

            xVals[i] = monthName;
        }

        return xVals;
    }

    /**
     * Return the n last mondays from from date.
     * @param n Number of mondays to get.
     * @param from Datetime in milliseconds to begin.
     * @return the last n mondays.
     */
    public static long[] getLastMondays(int n, long from) {
        long[] res = new long[n];
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(from);

        for (int i = 0; i < n; i++) {
            while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                cal.add(Calendar.DATE, -1);
            }
            res[i] = cal.getTimeInMillis();
            cal.add(Calendar.DATE, -1);
        }

        return res;
    }
}
