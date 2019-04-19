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

package es.rgmf.etroped.workout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import es.rgmf.etroped.R;
import es.rgmf.etroped.data.EtropedContract;
import es.rgmf.etroped.service.GpsStatusService;
import es.rgmf.etroped.service.TrackerActivityService;
import es.rgmf.etroped.util.EtropedGpsUtils;
import es.rgmf.etroped.util.EtropedPreferences;

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class Workout {

    private static final String TAG = Workout.class.getSimpleName();

    public static final int LOCATION_SOURCE = 1;
    public static final int PRESSURE_SOURCE = 2;

    private Context mContext;

    // Elapsed time in milliseconds.
    private long mElapsedTime = 0L;
    // Distance in meters.
    private double mDistance = 0f;
    // Speed in m/s.
    private double mSpeed = 0f;
    // Average speed in m/s.
    private double mSpeedAvg = 0f;
    // Maximum speed in m/s.
    private double mSpeedMax = 0f;
    // Pace in seconds per meters.
    private double mPace = 0f;
    // Altitude in meters.
    private double mAltitude = ElevationWorkout.NONE;
    // Pressure altitude in meteres.
    private double mPressureAltitude = ElevationWorkout.NONE;
    // Altitude gain in meters.
    private double mAltitudeGain = 0f;
    // Altitude loss in meters.
    private double mAltitudeLost = 0f;
    // Last time.
    private long mLastTime = 0L;
    // Las known location object.
    private Location mLastKnownLocation = null;

    // A class to get distance.
    private DistanceWorkout mDistanceWorkout;
    // A class to improve elevation.
    private ElevationWorkout mElevationWorkout;
    // A class to get speed.
    private SpeedWorkout mSpeedWorkout;

    /**
     * Static method to creates an instance of this class.
     *
     * @param context Android context.
     * @return A Workout object.
     */
    public static Workout newInstance(Context context) {
        Workout workout;

        int activityId = EtropedPreferences.getRecordingActivityId(context);
        if (activityId == -1)
            workout = new Workout();
        else
            workout = new Workout(context, activityId);

        workout.mContext = context;
        workout.mDistanceWorkout = DistanceWorkout.newInstance();
        workout.mElevationWorkout = ElevationFactory.newInstance(context);
        workout.mSpeedWorkout = SpeedWorkout.newInstance();

        return workout;
    }

    private Workout() {

    }

    /**
     * Constructs a WorkoutTask from the last location from the activity identified by activityId.
     *
     * @param context Android Context.
     * @param activityId The id of the ActivityEntity.
     */
    private Workout(Context context, int activityId) {

        try {

            Cursor cursor = context.getContentResolver().query(
                    EtropedContract.LocationEntry.CONTENT_URI,
                    null,
                    EtropedContract.LocationEntry.COLUMN_ACTIVITY + "=?",
                    new String[]{Integer.toString(activityId)},
                    EtropedContract.LocationEntry.COLUMN_TIME + " ASC");

            if (cursor != null && cursor.moveToFirst()) {

                ElevationTask elevationTask = new ElevationTask(context);

                int idxTime = -1, idxElapsedTime, idxDist, idxAlt, idxPressureAlt, idxSpeed;

                do {

                    // The last time is getting from the penultimo time.
                    if (idxTime != -1)
                        this.mLastTime = cursor.getLong(idxTime);

                    idxTime = cursor.getColumnIndex(EtropedContract.LocationEntry.COLUMN_TIME);
                    idxElapsedTime = cursor.getColumnIndex(
                            EtropedContract.LocationEntry.COLUMN_ELAPSED);
                    idxDist = cursor.getColumnIndex(
                            EtropedContract.LocationEntry.COLUMN_DISTANCE);
                    idxAlt = cursor.getColumnIndex(
                            EtropedContract.LocationEntry.COLUMN_GPS_ALTITUDE);
                    idxPressureAlt = cursor.getColumnIndex(
                            EtropedContract.LocationEntry.COLUMN_PRESSURE_ALTITUDE);
                    idxSpeed = cursor.getColumnIndex(
                            EtropedContract.LocationEntry.COLUMN_SPEED);

                    this.mLastKnownLocation = null;
                    this.mElapsedTime = cursor.getLong(idxElapsedTime);
                    this.mDistance = cursor.getLong(idxDist);
                    this.mSpeed = cursor.getDouble(idxSpeed);
                    this.mAltitude = cursor.getDouble(idxAlt);
                    this.mPressureAltitude = cursor.getDouble(idxPressureAlt);

                    elevationTask.refresh(this.mAltitude, this.mPressureAltitude);

                } while (cursor.moveToNext());

                this.mAltitudeGain = elevationTask.getGain();
                this.mAltitudeLost = elevationTask.getLost();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        mElevationWorkout.destroy();
    }

    /**
     * Receives a location and updates all values.
     *
     * @param location The new Location object.
     * @return True if updates data or False otherwise (ther is not movement).
     */
    public boolean newLocation(Location location) {

        // If there is not known location return with false.
        if (mLastKnownLocation == null) {
            mLastKnownLocation = location;
            mLastTime = location.getTime();
            return false;
        }

        // Calculates distance between new and last location.
        double distance = EtropedGpsUtils.calculateDistance(
                mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(),
                location.getLatitude(), location.getLongitude()
        );

        // Use DistanceWorkout for getting improved distance between the two las points.
        mDistanceWorkout.refresh(location, distance);
        distance = mDistanceWorkout.getDistance();

        // Get current altitude.
        mElevationWorkout.refresh(location);
        mAltitude = mElevationWorkout.getElevation();
        mPressureAltitude = mElevationWorkout.getPressureElevation();
        mAltitudeGain = mElevationWorkout.getGain();
        mAltitudeLost = mElevationWorkout.getLost();

        // Current speed, average speed and maximum speed.
        mSpeedWorkout.refresh(location, distance);
        mSpeed = mSpeedWorkout.getCurrentSpeed();
        mSpeedMax = mSpeedWorkout.getMaxSpeed();
        mSpeedAvg = mSpeedWorkout.getAvgSpeed();
        mPace = mSpeedWorkout.getPace();

        // Elapsed time.
        mElapsedTime += location.getTime() - mLastTime;

        // Updates last time.
        mLastTime = location.getTime();

        // If there is no movement return false.
        if (distance <= 0) {
            return false;
        }
        else {
            // Elapsed distance.
            mDistance += distance;
            mLastKnownLocation = location;
            return true;
        }
    }

    /**
     * When workout is paused (GPS is disabled or user paused the activity, for instance) it should
     * call this method so when workout starts again it does it from the new point.
     *
     * If you do not call this method then the time from workout was paused to the time workout
     * restart is sum up.
     */
    public void pauseWorkoutNotify() {
        mLastKnownLocation = null;
        mSpeedWorkout.pauseWorkoutNotify();
    }

    public double getLatitude() {
        if (mLastKnownLocation != null)
            return mLastKnownLocation.getLatitude();

        return 0d;
    }

    public double getLongitude() {
        if (mLastKnownLocation != null)
            return mLastKnownLocation.getLongitude();

        return 0d;
    }

    public int getAccuracy() {
        if (mLastKnownLocation != null)
            return (int) mLastKnownLocation.getAccuracy();

        return 0;
    }

    public int getGpsAltitude() {
        if (mLastKnownLocation != null)
            return (int) mLastKnownLocation.getAltitude();

        return 0;
    }

    public double getElapsedTime() {
        return mElapsedTime;
    }

    public double getDistance() {
        return mDistance;
    }

    public double getSpeed() {
        return mSpeed;
    }

    public double getSpeedAvg() {
        return mSpeedAvg;
    }

    public double getSpeedMax() {
        return mSpeedMax;
    }

    public double getPace() {
        return mPace;
    }

    public double getAltitude() {
        return mAltitude;
    }

    public double getPressureAltitude() {
        return mPressureAltitude;
    }

    public double getAltitudeGain() {
        return mAltitudeGain;
    }

    public double getAltitudeLost() {
        return mAltitudeLost;
    }
}
