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

import android.content.Context;

import java.util.LinkedList;

import es.rgmf.etroped.data.entity.LapEntity;
import es.rgmf.etroped.data.entity.LocationEntity;
import es.rgmf.etroped.util.EtropedUnitsUtils;

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class LapByDistanceTask {

    // Android Context.
    private Context mContext;

    // Distance from every lap.
    private int mDistance;

    // All laps.
    private LinkedList<LapEntity> mLapList;

    // The last time.
    private long mLastTime;

    // The last distance.
    private double mLastDistance;

    // Elevation task object to calculate elevation gain and lost.
    private ElevationTask mElevationTask;

    /**
     * Create a this class instance.
     *
     * @param distance Meters distance.
     */
    public LapByDistanceTask(Context context, int distance) {
        mContext = context;
        mDistance = distance;
        mLapList = new LinkedList<>();
    }

    /**
     * A new location mean that it needed to update the last lap or create another one.
     *
     * @param location The new location.
     */
    public void newLocation(LocationEntity location) {

        // If there is a lap it works on it. Otherwise it creates the first lap.
        if (mLapList.size() > 0) {

            // Get the last lap.
            LapEntity lap = mLapList.getLast();

            // Distance from last location in meters or yards.
            /*
            double distanceBetweenPoints = EtropedUnitsUtils.distance(mContext,
                    EtropedGpsUtils.calculateDistance(
                            mLastLatitude, mLastLongitude,
                            location.getLatitude(), location.getLongitude()
                    )
            );
            */

            // Speed in meters/seconds or yards/seconds.
            double speed = EtropedUnitsUtils.speed(mContext, location.getSpeed());

            // Add distance.
            double totalDistance = EtropedUnitsUtils.distance(mContext, location.getDistance());
            double accDistance = totalDistance - (mDistance * (mLapList.size() - 1));
            lap.setDistance(accDistance);

            // Max speed.
            if (lap.getMaxSpeed() < speed)
                lap.setMaxSpeed(speed);

            // Add average speed.
            lap.setAvgSpeed(location.getDistance() / lap.getElapsedTime());

            // Add time from last location.
            long accTime = lap.getElapsedTime() + (location.getTime() - mLastTime);
            lap.setElapsedTime(accTime);

            // Accumulates altitude gain and lost.
            mElevationTask.refresh(
                    location.getAltitude(),
                    location.getPressureAltitude()
            );

            // Add the altitude average.
            lap.setAltitude(mElevationTask.getGain() - mElevationTask.getLost());

            // If the distance has been achieved then creates a new lap.
            if (accDistance >= mDistance) {

                // Before it saves the corrected distance...
                lap.setDistance(mDistance);

                // ... and the corrected average speed.
                lap.setAvgSpeed(mDistance / lap.getElapsedTime());

                // It now can creates the new lap.
                /*
                LapEntity newLap = new LapEntity();
                newLap.setDistance(accDistance); // adds "el pico anterior".
                newLap.setElapsedTime(0);
                newLap.setAltitude(location.getAltitude());
                newLap.setAvgSpeed(speed);
                newLap.setMaxSpeed(speed);
                */
                LapEntity newLap = new LapEntity();
                newLap.setDistance(0);
                newLap.setElapsedTime(0);
                newLap.setAltitude(0);
                newLap.setAvgSpeed(0);
                newLap.setMaxSpeed(0);

                mLapList.add(newLap);

                mElevationTask = new ElevationTask(mContext);

                mLastDistance = EtropedUnitsUtils.distance(mContext, location.getDistance());
            }
        }
        else {
            // Speed in m/s or yd/s.
            double speed = EtropedUnitsUtils.speed(mContext, location.getSpeed());

            /*
            LapEntity lap = new LapEntity();
            lap.setDistance(0);
            lap.setElapsedTime(0);
            lap.setAltitude(location.getAltitude());
            lap.setAvgSpeed(speed);
            lap.setMaxSpeed(speed);
            */
            LapEntity lap = new LapEntity();
            lap.setDistance(0);
            lap.setElapsedTime(0);
            lap.setAltitude(0);
            lap.setAvgSpeed(0);
            lap.setMaxSpeed(0);

            mLapList.add(lap);

            mLastTime = 0;
            mLastDistance = 0;

            mElevationTask = new ElevationTask(mContext);
        }

        mLastTime = location.getTime();
    }

    public LinkedList<LapEntity> getLapList() {
        return mLapList;
    }
}
