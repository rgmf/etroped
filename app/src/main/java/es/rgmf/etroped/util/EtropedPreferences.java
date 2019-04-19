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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import es.rgmf.etroped.R;
import es.rgmf.etroped.data.EtropedContract;

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class EtropedPreferences {

    /**
     * Returns true if the user has selected metric distance display.
     *
     * @param context Context used to get the SharedPreferences
     *
     * @return true If metric display should be used
     */
    public static boolean isMetric(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        String keyForUnits = context.getString(R.string.pref_units_key);
        String defaultValue = context.getString(R.string.pref_units_metric);
        String metric = sharedPreferences.getString(keyForUnits, defaultValue);

        if (metric.equals(context.getString(R.string.pref_units_metric)))
            return true;
        else
            return false;
    }

    /**
     * This function search in preferences the default sport selected and return its pref names.
     *
     * @param context Context used to get the SharedPreferences.
     * @return The sport's string name.
     */
    public static String getDefaultSport(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        String keyForDefaultSport = context.getString(R.string.pref_default_sport_key);
        String defaultSportValue = context.getString(R.string.pref_bike);
        String defaultSport = sharedPreferences.getString(keyForDefaultSport, defaultSportValue);

        return defaultSport;
    }

    /**
     * Return the id of the sport selected.
     *
     * @param context Context used to get the SharedPreferences.
     * @return the sport's default id.
     */
    public static int getDefaultSportId(Context context) {
        String sport = getDefaultSport(context);

        if (sport.equals(context.getString(R.string.pref_running))) {
            return EtropedContract.SPORT_RUNNING;
        } else if (sport.equals(context.getString(R.string.pref_walking))) {
            return EtropedContract.SPORT_WALKING;
        } else {
            return EtropedContract.SPORT_BIKE;
        }
    }

    /**
     * The sport's id int preferences are string. This method return the associated integer id from
     * sport string id in preferences.
     *
     * @param context Context used to get the SharedPreferences.
     * @param sport the sport's default id.
     * @return
     */
    public static int getSportIdFromNameInPreferences(Context context, String sport) {
        if (sport.equals(context.getString(R.string.sport_running))) {
            return EtropedContract.SPORT_RUNNING;
        } else if (sport.equals(context.getString(R.string.sport_walking))) {
            return EtropedContract.SPORT_WALKING;
        } else {
            return EtropedContract.SPORT_BIKE;
        }
    }

    /**
     * Return the name of the sport by the sport identify.
     *
     * @param context The Android Context.
     * @param sport The sport identify.
     * @return
     */
    public static String getSportNameById(Context context, int sport) {
        switch(sport) {
            case EtropedContract.SPORT_BIKE:
                return context.getString(R.string.sport_bike_road);

            case EtropedContract.SPORT_BIKE_MTB:
                return context.getString(R.string.sport_bike_mtb);

            case EtropedContract.SPORT_BIKE_ROAD:
                return context.getString(R.string.sport_bike);

            case EtropedContract.SPORT_RUNNING:
                return context.getString(R.string.sport_running);

            case EtropedContract.SPORT_WALKING:
                return context.getString(R.string.sport_walking);

            default:
                return context.getString(R.string.sport_bike);

        }
    }

    /**
     * Return a sport drawable from sport id.
     *
     * @param sport The sport identify.
     * @return
     */
    public static int getSportDrawableById(int sport) {
        switch(sport) {
            case EtropedContract.SPORT_BIKE:
            case EtropedContract.SPORT_BIKE_MTB:
            case EtropedContract.SPORT_BIKE_ROAD:
                return R.drawable.ic_directions_bike_black_48dp;
            case EtropedContract.SPORT_RUNNING:
                return R.drawable.ic_directions_run_black_48dp;
            case EtropedContract.SPORT_WALKING:
                return R.drawable.ic_directions_walk_black_48dp;
            default:
                return R.drawable.ic_directions_walk_black_48dp;
        }
    }

    public static int getSportColor(int sport) {
        switch(sport) {
            case EtropedContract.SPORT_BIKE:
            case EtropedContract.SPORT_BIKE_MTB:
            case EtropedContract.SPORT_BIKE_ROAD:
                return R.color.color_bike_icon;
            case EtropedContract.SPORT_RUNNING:
                return R.color.color_running_icon;
            case EtropedContract.SPORT_WALKING:
                return R.color.color_walking_icon;
            default:
                return R.color.color_walking_icon;
        }
    }

    /**
     * This function edit preference to put the id of the sport.
     *
     * @param context Context used to get the SharedPreferences and strings.
     * @param sportId The id of the sport selected.
     */
    /*public static void setSportSelectedId(Context context, int sportId) {
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context).edit();
        editor.putInt(context.getString(R.string.pref_selected_sport_id), sportId);
        editor.commit();
    }*/

    /**
     * Remove the activity recording preference from SharedPreferences.
     *
     * @param context Context to get the SharedPreferences and strings.
     */
    /*public static void removeSportSelectedId(Context context) {
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context).edit();
        editor.remove(context.getString(R.string.pref_selected_sport_id));
        editor.commit();
    }*/

    /**
     * Return the id of the sport that is selected that is in the preference.
     *
     * @param context Context to get the SharedPreferences and strings.
     * @return The id of the sport that is selected or -1 if any is recording now.
     */
    /*public static int getSportSelectedId(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        return sharedPreferences.getInt(
                context.getString(R.string.pref_selected_sport_id), -1);
    }*/

    /**
     * This function edit preference to put the id of the activity that is recording.
     *
     * @param context Context used to get the SharedPreferences and strings.
     * @param activityId The id of the activity that is recording.
     */
    public static void setRecordingActivityId(Context context, int activityId) {
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context).edit();
        editor.putInt(context.getString(R.string.pref_recording_activity_id), activityId);
        editor.commit();
    }

    /**
     * Remove the activity recording preference from SharedPreferences.
     *
     * @param context Context to get the SharedPreferences and strings.
     */
    public static void removeRecordingActivityId(Context context) {
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context).edit();
        editor.remove(context.getString(R.string.pref_recording_activity_id));
        editor.commit();
    }

    /**
     * Return the id of the activity that is recording in the preference.
     *
     * @param context Context to get the SharedPreferences and strings.
     * @return The id of the recording that is recording or -1 if any is recording now.
     */
    public static int getRecordingActivityId(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        return sharedPreferences.getInt(
                context.getString(R.string.pref_recording_activity_id), -1);
    }

    /**
     * This function search in preferences the default accuracy and return it.
     *
     * @param context Context used to get the SharedPreferences.
     * @return The sport's string name.
     */
    public static String getDefaultAccuracy(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        String keyForAccuracy = context.getString(R.string.pref_accuracy_key);
        String defaultAccuracy = context.getString(R.string.pref_accuracy_default);
        String defaultSport = sharedPreferences.getString(keyForAccuracy, defaultAccuracy);

        return defaultSport;
    }

    public static boolean isPebbleChecked(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        String keyForPebble = context.getString(R.string.pref_pebble_key);

        return sharedPreferences.getBoolean(keyForPebble, false);
    }

    public static String getDescriptionDistance(Context context) {
        if (isMetric(context)) {
            return context.getString(R.string.hint_distance_km);
        }
        else {
            return context.getString(R.string.hint_distance_mi);
        }
    }
}
