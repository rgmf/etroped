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
import android.location.Location;
import android.util.Log;

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class ElevationWorkout {

    private static final String TAG = ElevationWorkout.class.getSimpleName();

    private static final int MIN_ELEVATION_FOR_ACCUMULATE_IN_M = 10;

    private static final int NUM_TIMES_FOR_CHANGE = 2;

    protected static final Double NONE = Double.NaN;

    protected Context mContext;

    private boolean initialized;

    protected double mElevation;
    protected double mElevationForGainAndLost;
    protected double mLastElevation;
    private double mMax;
    private double mMin;
    private double mReferenceForGain;
    private double mReferenceForLost;
    private double mGain;
    private double mLost;

    private boolean mClimbing;
    private int mClimbingChange;

    private boolean mDescending;
    private int mDescendingChange;

    private static final int ITEMS = 10;
    private double[] mLatestElevations;
    private int mCurrentItem;

    public ElevationWorkout(Context context) {
        mContext = context;
        mElevation = NONE;
        mElevationForGainAndLost = NONE;
        mLastElevation = NONE;
        mMax = Double.MIN_VALUE;
        mMin = Double.MAX_VALUE;
        mReferenceForGain = mMax;
        mReferenceForLost = mMin;
        mGain = 0d;
        mLost = 0d;

        mClimbing =false;
        mDescending = false;

        mClimbingChange = 0;
        mDescendingChange = 0;

        initialized = false;

        mLatestElevations = new double[ITEMS];
        for (int i = 0; i < ITEMS; i++) {
            mLatestElevations[i] = NONE;
        }

        mCurrentItem = 0;
    }

    public void refresh(Location location) {
        mElevation = avgElevation(location.getAltitude());
        mElevationForGainAndLost = getElevationForGainAndLost();

        if (!ElevationWorkout.isNone(mElevation) || !ElevationWorkout.isNone(mElevationForGainAndLost)) {

            if (initialized) {

                // Maximum and minimum altitude.
                if (mElevation > mMax)
                    mMax = mElevation;

                if (mElevation < mMin)
                    mMin = mElevation;

                // Two possibilities:
                // 1.- Is climbing? If flag mClimbing is true then it is climbing so accumulates
                //     any meter.
                // 2.- Otherwise, if it accumulates more than MIN_ELEVATION_FOR_ACCUMULATE_IN_M
                //     meters then begins climbing setting mClimbing flag to true and add the
                //     accumulate climbing.
                if (mClimbing) {
                    if (mElevationForGainAndLost > mLastElevation + 5) {
                        mGain += (mElevationForGainAndLost - mLastElevation);
                        mReferenceForGain = mElevationForGainAndLost;
                        mClimbingChange = NUM_TIMES_FOR_CHANGE;
                        Log.d(TAG, "Gain: " + mGain + " (mElevationForGainAndLost: " + mElevationForGainAndLost + " - mLastElevation: " + mLastElevation + ")");
                    }
                    else {
                        mClimbingChange--;
                        if (mClimbingChange == 0) {
                            Log.d(TAG, "Se acabó el ascenso");
                            mClimbing = false;
                        }
                    }
                }
                else if (mElevationForGainAndLost > mReferenceForGain + MIN_ELEVATION_FOR_ACCUMULATE_IN_M) {
                    mGain += (mElevationForGainAndLost - mReferenceForGain);
                    mReferenceForGain = mElevationForGainAndLost;
                    mClimbing = true;
                    mClimbingChange = NUM_TIMES_FOR_CHANGE;
                    Log.d(TAG, "Comienza el ascenso (elevation for gain and lost: " + mElevationForGainAndLost + " - reference for gain: " + mReferenceForGain + " - gain: " + mGain + ")");
                }

                // Updates reference for gain if necessary.
                if (mElevationForGainAndLost < mReferenceForGain)
                    mReferenceForGain = mElevationForGainAndLost;

                // Two possibilities:
                // 1.- Is descending? If flag mDescending is true then it is descending so
                //     accumulates any meter.
                // 2.- Otherwise, if it accumulates more than MIN_ELEVATION_FOR_ACCUMULATE_IN_M
                //     meters then begins descending setting mDescending flag to true and add the
                //     accumulate descending.
                if (mDescending) {
                    if (mElevationForGainAndLost < mLastElevation - 5) {
                        mLost += (mLastElevation - mElevationForGainAndLost);
                        mReferenceForLost = mElevationForGainAndLost;
                        mDescendingChange = NUM_TIMES_FOR_CHANGE;
                        Log.d(TAG, "Lost: " + mLost + " (mLastElevation: " + mLastElevation + " - mElevationForGainAndLost: " + mElevationForGainAndLost + ")");
                    }
                    else {
                        mDescendingChange--;
                        if (mDescendingChange == 0) {
                            mDescending = false;
                            Log.d(TAG, "Se acabo el descenso");
                        }
                    }
                }
                else if (mElevationForGainAndLost < mReferenceForLost - MIN_ELEVATION_FOR_ACCUMULATE_IN_M) {
                    mLost += (mReferenceForLost - mElevationForGainAndLost);
                    mReferenceForLost = mElevationForGainAndLost;
                    mDescending = true;
                    mDescendingChange = NUM_TIMES_FOR_CHANGE;

                    Log.d(TAG, "Comienza el descenso (elevation for gain and lost: " + mElevationForGainAndLost + " - reference for lost: " + mReferenceForLost + " - lost: " + mLost + ")");
                }

                // Updates reference for lost if necessary.
                if (mElevationForGainAndLost > mReferenceForLost)
                    mReferenceForLost = mElevationForGainAndLost;

                // Updates last elevation.
                mLastElevation = mElevationForGainAndLost;

            } else {

                initialized = true;
                mMax = mElevation;
                mMin = mElevation;
                mReferenceForGain = mElevationForGainAndLost;
                mReferenceForLost = mElevationForGainAndLost;
                mLastElevation = mElevationForGainAndLost;
            }
        }
    }

    public void destroy() {}

    public double getElevationForGainAndLost() {
        return mElevation;
    }

    public double getPressureElevation() {
        return NONE;
    }

    public double getElevation() {
        return mElevation;
    }

    public double getMax() {
        return mMax;
    }

    public double getMin() {
        return mMin;
    }

    public double getGain() {
        return mGain;
    }

    public double getLost() {
        return mLost;
    }

    public static boolean isNone(Double e) {
        return Double.isNaN(e);
    }

    /**
     * It does an average with last ITEMS altitudes read from gps to correct.
     *
     * @param newAltitude The new altitude that come from gps altitude.
     * @return The average of last ITEMS altitudes.
     */
    private double avgElevation(double newAltitude) {
        double sum = 0;

        mLatestElevations[mCurrentItem] = newAltitude;
        mCurrentItem = mCurrentItem + 1 < ITEMS ? mCurrentItem + 1 : 0;

        for (double e : mLatestElevations) {

            if (e == NONE) {
                return NONE;
            }

            sum += e;
        }

        return sum / ITEMS;
    }
}
