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

package es.rgmf.etroped.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.Editable;
import android.util.Log;
import android.widget.Toast;

import org.osmdroid.util.GeoPoint;

import java.util.LinkedList;
import java.util.List;

import es.rgmf.etroped.EditActivity;
import es.rgmf.etroped.R;
import es.rgmf.etroped.data.entity.ActivityEntity;
import es.rgmf.etroped.data.entity.LocationEntity;
import es.rgmf.etroped.util.EtropedDateTimeUtils;
import es.rgmf.etroped.util.EtropedPreferences;

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class ActivityProvider {

    /**
     * Creates a new activity and adds to the SharedPreferences the id of the activity that is
     * recording.
     *
     * @param context Antroid Context.
     * @param sportId The id of the activity sport.
     * @return The id of the new activity inserted in the database.
     */
    public static int createActivity(Context context, int sportId) {

        long millis = System.currentTimeMillis();
        ContentValues values = new ContentValues();
        values.put(EtropedContract.ActivityEntry.COLUMN_SPORT, sportId);
        values.put(EtropedContract.ActivityEntry.COLUMN_START_TIME, millis);
        values.put(EtropedContract.ActivityEntry.COLUMN_NAME,
                EtropedDateTimeUtils.getFriendlyDateCompressString(context,
                        millis));

        try {

            Uri uri = context.getContentResolver()
                    .insert(EtropedContract.ActivityEntry.CONTENT_URI, values);
            int id = Integer.valueOf(uri.getLastPathSegment());
            EtropedPreferences.setRecordingActivityId(context, id);

            return id;

        } catch (Exception e) {
            Toast.makeText(
                    context,
                    context.getString(R.string.err_fail_inserting_data),
                    Toast.LENGTH_LONG
            ).show();
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Delete activity identified by activityId.
     *
     * @param context Android Context.
     * @param activityId The activity to be deletes.
     * @return Number of activities delete.
     */
    public static int deleteActivity(Context context, int activityId) {

        Uri uriActivity = EtropedContract.ActivityEntry.CONTENT_URI
                .buildUpon().appendPath(Integer.toString(activityId)).build();

        int num = context.getContentResolver().delete(
                uriActivity,null, null);

        return num;
    }

    /**
     * Get the activity identified by activityRecordingId.
     *
     * @param context Android Context.
     * @param activityRecordingId The activity id it wants.
     * @return The Entity.ActivityEntity
     */
    public static ActivityEntity getActivity(Context context, int activityRecordingId) {

        ActivityEntity activityEntity = null;

        Cursor cursor = context.getContentResolver().query(
                EtropedContract.ActivityEntry.CONTENT_URI,
                null,
                EtropedContract.ActivityEntry._ID + "=?",
                new String[] {Integer.toString(activityRecordingId)},
                null
        );

        if (cursor != null && cursor.getCount() == 1 && cursor.moveToFirst()) {
            activityEntity = new ActivityEntity(cursor);
        }

        return activityEntity;
    }

    /**
     * Get and return all GeoPoints from activity.
     *
     * @param context Android Context.
     * @param activityId The activity identify.
     * @return
     */
    public static List<GeoPoint> getLocationsActivity(Context context, int activityId) {

        LinkedList<GeoPoint> points = new LinkedList<>();

        Cursor cursorLocations = context.getContentResolver().query(
                EtropedContract.LocationEntry.CONTENT_URI,
                null,
                EtropedContract.LocationEntry.COLUMN_ACTIVITY + "=?",
                new String[]{Integer.toString(activityId)},
                null
        );

        //Log.e("get", "antes del if");
        if (cursorLocations.moveToFirst()) {
            double lat, lon;
            do {
                LocationEntity locationEntity = new LocationEntity(cursorLocations);

                lat = locationEntity.getLatitude();
                lon = locationEntity.getLongitude();
                //Log.e("get", lat + " - " + lon);
                points.add(new GeoPoint(lat, lon));

            } while (cursorLocations.moveToNext());
        }

        return points;
    }

    /**
     * Updates the sport of the activity.
     *
     * @param applicationContext Android Context.
     * @param activityId The activity id to change.
     * @param sportId The new sport id.
     * @return The number of rows updated (it should be one).
     */
    public static int updateActivitySport(Context applicationContext,
                                           int activityId, int sportId) {

        // Creates values to update.
        ContentValues values = new ContentValues();
        values.put(EtropedContract.ActivityEntry.COLUMN_SPORT, sportId);

        // Build the uri to update.
        String idStr = Integer.toString(activityId);
        Uri uri = EtropedContract.ActivityEntry.CONTENT_URI.buildUpon().appendPath(idStr).build();

        // Update and return the number of rows updated.
        return applicationContext.getContentResolver().update(uri, values, null, null);
    }

    /**
     * Updates the values from activity.
     *
     * @param context Android Context.
     * @param id Activity id.
     * @param name Activity name.
     * @param comment Activity comment.
     * @param sportId Activity sport id.
     * @return number of rows updated.
     */
    public static int updateActivitySport(Context context, int id, String name, String comment,
                                           int sportId) {

        ContentValues values = new ContentValues();
        values.put(EtropedContract.ActivityEntry.COLUMN_NAME, name);
        values.put(EtropedContract.ActivityEntry.COLUMN_COMMENT, comment);
        values.put(EtropedContract.ActivityEntry.COLUMN_SPORT, sportId);

        String idStr = Integer.toString(id);
        Uri uri = EtropedContract.ActivityEntry.CONTENT_URI.buildUpon().appendPath(idStr).build();

        return context.getContentResolver().update(uri, values, null, null);
    }
}
