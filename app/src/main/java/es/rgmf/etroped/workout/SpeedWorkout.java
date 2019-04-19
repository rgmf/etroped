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

import android.location.Location;
import android.util.Log;

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class SpeedWorkout {

    private double mCurrentSpeed;
    private double mMaxSpeed;
    private double mAvgSpeed;
    private double mPace;

    private long mCurrentTimeInMillis;
    private long mLastTime;
    private double mTotalDistance;
    private long mElapsedTime;

    public static SpeedWorkout newInstance() {
        SpeedWorkout sw = new SpeedWorkout();

        return sw;
    }

    private SpeedWorkout() {
        mCurrentSpeed = 0d;
        mMaxSpeed = 0d;
        mAvgSpeed = 0d;

        mCurrentTimeInMillis = 0L;
        mLastTime = 0L;
        mTotalDistance = 0d;
        mElapsedTime = 0L;
    }

    public void refresh(Location location, double distance) {

        if (mCurrentTimeInMillis == 0)
            mCurrentTimeInMillis = location.getTime();
        else {
            mLastTime = mCurrentTimeInMillis;
            mCurrentTimeInMillis = location.getTime();
            mElapsedTime += (mCurrentTimeInMillis - mLastTime);

            if (mElapsedTime > 0) {
                mAvgSpeed = (mTotalDistance / mElapsedTime) * 1000;
                if (mTotalDistance > 0)
                    mPace = (mElapsedTime / mTotalDistance) / 1000;
            }
        }

        mCurrentSpeed = location.getSpeed();

        mTotalDistance += distance;
    }

    public double getCurrentSpeed() {

        return mCurrentSpeed;
    }

    public double getMaxSpeed() {
        return mMaxSpeed;
    }

    public double getAvgSpeed() {
        return mAvgSpeed;
    }

    public double getPace() {
        return mPace;
    }

    public void pauseWorkoutNotify() {
        mCurrentTimeInMillis = 0;
    }
}
