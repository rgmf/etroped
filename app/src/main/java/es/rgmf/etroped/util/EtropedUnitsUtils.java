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
import android.util.Log;

import es.rgmf.etroped.R;

/**
 * This class contains useful utilities for a Etroped units.
 *
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public final class EtropedUnitsUtils {

    private static final double YARDS_IN_A_METER = 1.0936133d;
    private static final double YARDS_IN_A_MILE = 1760.0d;
    private static final double METERS_IN_A_KILOMETER = 1000.0d;
    private static final double FEETS_IN_A_METER = 3.28084d;

    public static double fromMetersToYards(double meters) {
        return meters * YARDS_IN_A_METER;
    }

    public static double fromMetersToFeets(double meters) { return meters * FEETS_IN_A_METER; }

    public static double fromYardsToMiles(double yards) {
        return yards / YARDS_IN_A_MILE;
    }

    public static double fromMetersToKilometers(double meters) {
        return meters / METERS_IN_A_KILOMETER;
    }

    /**
     * This method return a String containing a number of workouts.
     *
     * @param context Android context to access preferences and resources.
     * @param workouts Number of workouts.
     * @return The number or workouts. For instance, in English: "24 workouts".
     */
    public static String formatWorkouts(Context context, int workouts) {
        return String.format(context.getString(R.string.workouts_unit), workouts);
    }

    /**
     * This method return a String containing a distance in a human format in metric or imperial
     * format, depending on preferences.
     *
     * @param context Android context to access preferences and resources.
     * @param distance A distance in meters.
     * @return The distance string.
     */
    public static String formatDistance(Context context, long distance) {
        int distanceFormatId;
        String result;

        if(!EtropedPreferences.isMetric(context)) {
            double imperialDistance = fromMetersToYards(distance);

            if (imperialDistance >= YARDS_IN_A_MILE) {
                imperialDistance = fromYardsToMiles(imperialDistance);
                distanceFormatId = R.string.distance_miles_format;
            }
            else
                distanceFormatId = R.string.distance_yards_format;

            result = String.format(context.getString(distanceFormatId), imperialDistance);
        }
        else {
            double metricDistance = (double)distance;

            if (distance >= METERS_IN_A_KILOMETER) {
                metricDistance = fromMetersToKilometers(metricDistance);
                distanceFormatId = R.string.distance_kilometers_format;
            }
            else
                distanceFormatId = R.string.distance_meters_format;

            result = String.format(context.getString(distanceFormatId), metricDistance);
        }

        return result;
    }

    public static String formatDistancePebble(Context context, long distance) {
        int distanceFormatId;
        String result;

        if(!EtropedPreferences.isMetric(context)) {
            double imperialDistance = fromMetersToYards(distance);
            imperialDistance = fromYardsToMiles(imperialDistance);
            distanceFormatId = R.string.distance_no_unit;
            result = String.format(context.getString(distanceFormatId), imperialDistance);
        }
        else {
            double metricDistance = (double)distance;
            metricDistance = fromMetersToKilometers(metricDistance);
            distanceFormatId = R.string.distance_no_unit;
            result = String.format(context.getString(distanceFormatId), metricDistance);
        }

        return result;
    }

    /**
     * This method return a String containing a speed in a human format in metric or imperial
     * format, depending on preferences.
     *
     * This method not add the
     *
     * @param context Android Context.
     * @param distance Distance in meters.
     * @param milliseconds Time in milliseconds.
     * @return The speed string.
     */
    public static String formatSpeed(Context context, double distance, long milliseconds) {

        if (EtropedPreferences.isMetric(context)) {
            double km = distance / 1000;
            double hours = ((float) milliseconds) / (1000d * 60d * 60d);
            double speed;
            if (km > 0)
                speed = km / hours;
            else
                speed = 0;

            return String.format(context.getString(R.string.speed_format), speed);
        }
        else {
            double mi = fromYardsToMiles(fromMetersToYards(distance));
            double hours = ((double) milliseconds) / (1000d * 60d * 60d);
            double speed;
            if (mi > 0)
                speed = (double) (mi / hours);
            else
                speed = 0;

            return String.format(context.getString(R.string.speed_format), speed);
        }
    }

    /**
     * This method return a String containing a speed in a human format in metric or imperial
     * format, depending on preferences. This adds the unit at the end.
     *
     * This method not add the
     *
     * @param context Android Context.
     * @param distance Distance in meters.
     * @param milliseconds Time in milliseconds.
     * @return The speed string.
     */
    public static String formatSpeedWithUnit(Context context, double distance, long milliseconds) {

        return formatSpeed(context, distance, milliseconds) + " " + speedUnit(context);
    }

    /**
     * This method receive the speed in m/s and returns speed formatted in km/h or mi/ho depending
     * on preferences system metric.
     *
     * @param context Android Context.
     * @param speed Speed in m/s.
     * @return
     */
    public static String formatSpeed(Context context, float speed) {
        if (EtropedPreferences.isMetric(context)) {
            return String.format(context.getString(R.string.speed_format), speed * 3.6);
        }
        else {
            return String.format(context.getString(R.string.speed_format), speed * 2.236936);
        }
    }

    /**
     * This method receive the speed in m/s and returns speed formatted in km/h or mi/ho depending
     * on preferences system metric. This adds the unit.
     *
     * @param context Android Context.
     * @param speed Speed in m/s.
     * @return
     */
    public static String formatSpeedWithUnit(Context context, float speed) {

        return formatSpeed(context, speed) + " " + speedUnit(context);
    }

    /**
     * Return a number of meteres o feets of altitude depending on preferences (imperial or metric).
     *
     * @param context Android Context.
     * @param altitude Altitude in meters.
     * @return
     */
    public static String formatAltitude(Context context, float altitude) {

        if (EtropedPreferences.isMetric(context)) {
            return String.format(context.getString(R.string.altitude_format), (int) altitude);
        }
        else {
            return String.format(context.getString(R.string.altitude_format),
                    (int) fromMetersToFeets(altitude));
        }
    }

    /**
     * Return the pace depending on preferences (imperial or metric).
     *
     * @param context Android Context.
     * @param pace Seconds per meters.
     * @return
     */
    public static String formatPace(Context context, float pace) {

        if (EtropedPreferences.isMetric(context)) {
            float paceMinKm = pace / 60 * 1000;
            int minutes = (int) paceMinKm;
            int seconds = (int) ((paceMinKm - minutes) * 60);

            return String.format(context.getString(R.string.pace_format), minutes, seconds);
        }
        else {
            float paceMinMi = pace / 60 * 1609.34f;
            int minutes = (int) paceMinMi;
            int seconds = (int) ((paceMinMi - minutes) * 60);

            return String.format(context.getString(R.string.pace_format), minutes, seconds);
        }
    }

    /**
     * Return the pace depending on preferences (imperial or metric).
     *
     * @param context Android Context.
     * @param time Elapsed time.
     * @param distance Distance.
     * @return
     */
    public static String formatPace(Context context, float time, float distance) {

        float pace = (time / distance) / 1000;

        return formatPace(context, pace);
    }

    /**
     * Return the pace depending on preferences (imperial or metric).
     *
     * @param context Android Context.
     * @param time Elapsed time.
     * @param distance Distance.
     * @return
     */
    public static String formatPaceWithUnit(Context context, float time, float distance) {

        float pace = (time / distance) / 1000;

        return formatPaceWithUnit(context, pace);
    }

    /**
     * Return the pace depending on preferences (imperial or metric).
     *
     * @param context Android Context.
     * @param pace Seconds per meters.
     * @return
     */
    public static String formatPaceWithUnit(Context context, float pace) {

        return formatPace(context, pace) + " " + paceUnit(context);
    }

    /**
     * Return the speed unit depending on preferences.
     *
     * @param context Android Context.
     * @return
     */
    public static String speedUnit(Context context) {

        if (EtropedPreferences.isMetric(context))
            return context.getString(R.string.speed_unit_metric);
        else
            return context.getString(R.string.speed_unit_imperial);
    }

    /**
     * Return the distance unit depending on preferences.
     *
     * @param context Android Context.
     * @return
     */
    public static String distanceCaptionUnit(Context context) {

        if (EtropedPreferences.isMetric(context))
            return context.getString(R.string.km_caption_label);
        else
            return context.getString(R.string.mi_caption_label);
    }

    /**
     * Return the distance unit depending on preferences.
     *
     * @param context Android Context.
     * @param distance The distance in meters.
     * @return
     */
    public static String distanceUnit(Context context, float distance) {

        if (EtropedPreferences.isMetric(context)) {
            if (distance > 1000)
                return context.getString(R.string.distance_unit_km);
            else
                return context.getString(R.string.distance_unit_meters);
        }
        else {
            if (fromYardsToMiles(fromMetersToYards(distance)) > 1)
                return context.getString(R.string.distance_unit_mi);
            else
                return context.getString(R.string.distance_unit_yards);
        }
    }

    /**
     * Return the altitude unit depending on preferences.
     *
     * @param context Android Context.
     * @return
     */
    public static String altitudeUnit(Context context) {

        if (EtropedPreferences.isMetric(context))
            return context.getString(R.string.altitude_unit_metric);
        else
            return context.getString(R.string.altitude_unit_imperial);
    }

    /**
     * Return the pace unit depending on preferences.
     *
     * @param context Android Context.
     * @return
     */
    public static String paceUnit(Context context) {

        if (EtropedPreferences.isMetric(context))
            return context.getString(R.string.pace_unit_metric);
        else
            return context.getString(R.string.pace_unit_imperial);
    }

    /**
     * Return the distance in unit selected by the user in preferences.
     *
     * @param context Android context.
     * @param distance The distance in meters.
     * @return
     */
    public static double distance(Context context, double distance) {
        if (!EtropedPreferences.isMetric(context))
            return fromMetersToYards(distance);

        return distance;
    }

    /**
     * Return the speed in the unit specified by the preferences (m/s or mi/s).
     *
     * @param context Android Context.
     * @param speed Speed in m/s.
     * @return
     */
    public static double speed(Context context, double speed) {
        if (!EtropedPreferences.isMetric(context))
            return fromMetersToYards(speed);

        return speed;
    }

    /**
     * Return the speed in km/h or mi/ho depending on preferences.
     *
     * @param context Android context.
     * @param speed The speed in m/s.
     * @return
     */
    public static double formatSpeedNumber(Context context, double speed) {
        if (!EtropedPreferences.isMetric(context))
            return fromMetersToYards(speed) * 2.236936;

        return speed * 3.6;
    }

    /**
     * Return the pace in min/km or min/mi depending on preferences.
     *
     * @param context Android context.
     * @param pace The pace in s/m.
     * @return
     */
    public static double formatPaceNumber(Context context, double pace) {

        if (EtropedPreferences.isMetric(context))
            return pace / 60 * 1000;
        else
            return pace / 60 * 1609.34f;
    }

    /**
     * Return a number of meteres o feets of altitude depending on preferences (imperial or metric).
     *
     * @param context Android Context.
     * @param altitude Altitude in meters.
     * @return
     */
    public static double formatAltitudeNumber(Context context, double altitude) {

        if (EtropedPreferences.isMetric(context)) {
            return altitude;
        }
        else {
            return fromMetersToFeets(altitude);
        }
    }

    /**
     * Return the base unit. A km in meters or a mile in yards depending on preferences.
     *
     * @param context Android Context.
     * @return
     */
    public static double baseUnit(Context context) {
        if (EtropedPreferences.isMetric(context))
            return METERS_IN_A_KILOMETER;
        else
            return YARDS_IN_A_MILE;
    }

    /**
     * Convert from density (dp) to pixels. For example 15dp to pixels.
     *
     * @param context Android Context.
     * @param dp The value of density.
     * @return
     */
    public static int fromDpToPx(Context context, int dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        int pixels = (int) (15 * scale + 0.5f);

        return pixels;
    }
}
