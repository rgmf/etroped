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

package es.rgmf.etroped.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import es.rgmf.etroped.data.EtropedContract;
import es.rgmf.etroped.util.EtropedPreferences;
import es.rgmf.etroped.workout.ElevationWorkout;

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class TrackerActivityPersistentService extends IntentService {

    private static final String TAG = TrackerActivityPersistentService.class.getSimpleName();

    // The actions this service does.
    public static final String ACTION_NEW_LOCATION = "action_new_location";

    // Extra data names.
    public static final String EXTRA_LOCATION = "extra_location";
    public static final String EXTRA_SPEED = "extra_speed";
    public static final String EXTRA_ELAPSED_TIME = "extra_elapsed_time";
    public static final String EXTRA_DISTANCE = "extra_distance";
    public static final String EXTRA_ELEVATION = "extra_elevation";
    public static final String EXTRA_PRESSURE_ELEVATION = "extra_pressure_elevation";


    public TrackerActivityPersistentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        switch (intent.getAction()) {

            case ACTION_NEW_LOCATION:

                int activityId = EtropedPreferences.getRecordingActivityId(getApplicationContext());

                if (activityId != -1) {

                    Location location = intent.getParcelableExtra(EXTRA_LOCATION);
                    double speed = intent.getDoubleExtra(EXTRA_SPEED, 0d);
                    long elapsedTime = (long) intent.getDoubleExtra(EXTRA_ELAPSED_TIME, 0d);
                    double distance = intent.getDoubleExtra(EXTRA_DISTANCE, 0d);
                    double elevation = intent.getDoubleExtra(EXTRA_ELEVATION, 0d);
                    double pressureElevation = intent.getDoubleExtra(EXTRA_PRESSURE_ELEVATION, 0d);
                    ContentValues locationValues = new ContentValues();
                    ContentValues activityValues = new ContentValues();

                    // Saves location.
                    locationValues.put(
                            EtropedContract.LocationEntry.COLUMN_LATITUDE,
                            location.getLatitude()
                    );

                    locationValues.put(
                            EtropedContract.LocationEntry.COLUMN_LONGITUDE,
                            location.getLongitude()
                    );

                    locationValues.put(
                            EtropedContract.LocationEntry.COLUMN_ACCURACY,
                            location.getAccuracy()
                    );

                    // Saves elevation only if exists.
                    if (!ElevationWorkout.isNone(elevation))
                        locationValues.put(
                                EtropedContract.LocationEntry.COLUMN_GPS_ALTITUDE,
                                elevation
                        );

                    // Saves pressure elevation only if exists.
                    if (!ElevationWorkout.isNone(pressureElevation))
                        locationValues.put(
                                EtropedContract.LocationEntry.COLUMN_PRESSURE_ALTITUDE,
                                pressureElevation
                        );

                    locationValues.put(
                            EtropedContract.LocationEntry.COLUMN_TIME,
                            location.getTime()
                    );

                    locationValues.put(
                            EtropedContract.LocationEntry.COLUMN_SPEED,
                            speed
                    );

                    locationValues.put(
                            EtropedContract.LocationEntry.COLUMN_ELAPSED,
                            elapsedTime
                    );

                    locationValues.put(
                            EtropedContract.LocationEntry.COLUMN_DISTANCE,
                            distance
                    );

                    // TODO At this moment there only is a lap per activity.
                    locationValues.put(
                            EtropedContract.LocationEntry.COLUMN_LAP,
                            1
                    );

                    locationValues.put(
                            EtropedContract.LocationEntry.COLUMN_ACTIVITY,
                            activityId
                    );

                    Uri uriLocation = getContentResolver().insert(
                            EtropedContract.LocationEntry.CONTENT_URI,
                            locationValues);

                    // TODO Saves activity each time it has a new location to recover activity later
                    // if necessary. Maybe there is a best option to do that.
                    // Updates activity.
                    activityValues.put(
                            EtropedContract.ActivityEntry.COLUMN_END_TIME,
                            location.getTime()
                    );

                    activityValues.put(
                            EtropedContract.ActivityEntry.COLUMN_TIME,
                            elapsedTime
                    );

                    activityValues.put(
                            EtropedContract.ActivityEntry.COLUMN_DISTANCE,
                            distance
                    );

                    Uri uriActivity = EtropedContract.ActivityEntry.CONTENT_URI
                            .buildUpon().appendPath(Integer.toString(activityId)).build();

                    getContentResolver().update(uriActivity, activityValues, null, null);
                }

                break;
        }
    }
}
