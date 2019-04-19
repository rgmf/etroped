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

package es.rgmf.etroped;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.getpebble.android.kit.Constants;
import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;
import com.getpebble.android.kit.util.SportsState;

import es.rgmf.etroped.data.EtropedContract;
import es.rgmf.etroped.util.EtropedPebbleUtils;
import es.rgmf.etroped.util.EtropedPreferences;
import es.rgmf.etroped.workout.Workout;

public class TrackerPebbleActivity extends TrackerActivity {
    private static final String TAG = TrackerPebbleActivity.class.getSimpleName();

    private PebbleKit.PebbleDataReceiver mReceiver = null;
    private SportsState mCurrentState;
    private byte mSportUnitValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentState = new SportsState();
        mSportUnitValue = (byte) (EtropedPreferences.isMetric(getApplicationContext()) ?
                Constants.SPORTS_UNITS_METRIC : Constants.SPORTS_UNITS_IMPERIAL);
        PebbleKit.startAppOnPebble(getApplicationContext(), Constants.SPORTS_UUID);
    }

    @Override
    public void onWorkoutUpdate(Workout workout) {
        super.onWorkoutUpdate(workout);

        PebbleDictionary dict = new PebbleDictionary();
        dict.addUint8(Constants.SPORTS_UNITS_KEY, mSportUnitValue);
        PebbleKit.sendDataToPebble(getApplicationContext(), Constants.SPORTS_UUID, dict);

        mCurrentState.setTimeInSec((int) (workout.getElapsedTime() / 1000));
        mCurrentState.setDistance(
                EtropedPebbleUtils.getDistance(
                        getApplicationContext(),
                        (float) workout.getDistance()
                )
        );
        if (mSelectedSportId == EtropedContract.SPORT_RUNNING ||
                mSelectedSportId == EtropedContract.SPORT_WALKING) {
            mCurrentState.setPaceInSec(
                    EtropedPebbleUtils.getPace(
                            getApplicationContext(),
                            (float) workout.getPace()
                    )
            );
        } else {
            mCurrentState.setSpeed(
                    EtropedPebbleUtils.getSpeed(
                            getApplicationContext(),
                            (float) workout.getSpeed()
                    )
            );
        }

        mCurrentState.synchronize(getApplicationContext());
    }

    /*
    @Override
    public void onWorkoutUpdate(Workout workout) {
        super.onWorkoutUpdate(workout);

        // Creates a dictionary.
        PebbleDictionary dict = new PebbleDictionary();

        // Set unit (imperial or metric).
        //if (!mUnit) {
        byte value = (byte) (EtropedPreferences.isMetric(getApplicationContext()) ?
                Constants.SPORTS_UNITS_METRIC : Constants.SPORTS_UNITS_IMPERIAL);
        dict.addUint8(Constants.SPORTS_UNITS_KEY, value);
        //    mUnit = true;
        //}

        // Time and distance.
        dict.addString(
                Constants.SPORTS_TIME_KEY,
                EtropedDateTimeUtils.formatTimeOnLive(getApplicationContext(), (long) workout.getElapsedTime())
        );
        dict.addString(
                Constants.SPORTS_DISTANCE_KEY,
                EtropedUnitsUtils.formatDistancePebble(getApplicationContext(), (long) workout.getDistance())
        );

        // Pace or speed for data.
        if (mSelectedSportId == EtropedContract.SPORT_RUNNING ||
                mSelectedSportId == EtropedContract.SPORT_WALKING) {
            dict.addUint8(Constants.SPORTS_LABEL_KEY, (byte) Constants.SPORTS_DATA_PACE);
            dict.addString(
                    Constants.SPORTS_DATA_KEY,
                    EtropedUnitsUtils.formatPace(getApplicationContext(), (float) workout.getPace())
            );
        }
        else {
            dict.addUint8(Constants.SPORTS_LABEL_KEY, (byte) Constants.SPORTS_DATA_SPEED);
            dict.addString(
                    Constants.SPORTS_DATA_KEY,
                    EtropedUnitsUtils.formatSpeed(getApplicationContext(), (float) workout.getSpeed())
            );
        }

        PebbleKit.sendDataToPebble(getApplicationContext(), Constants.SPORTS_UUID, dict);
    }
    */

    @Override
    protected void onResume() {
        super.onResume();

        // Listen for button events
        if(mReceiver == null) {
            mReceiver = new PebbleKit.PebbleDataReceiver(Constants.SPORTS_UUID) {

                @Override
                public void receiveData(Context context, int id, PebbleDictionary data) {
                    // Always ACKnowledge the last message to prevent timeouts
                    PebbleKit.sendAckToPebble(getApplicationContext(), id);

                    // Get action and display as Toast
                    int state = data.getUnsignedIntegerAsLong(Constants.SPORTS_STATE_KEY).intValue();
                    Log.d(TAG, "Action: " + state + " (" + (state == Constants.SPORTS_STATE_PAUSED ? "Running" : "Paused") + ")");
                    if (state == Constants.SPORTS_STATE_PAUSED) {
                        play();
                    }
                    else {
                        pause();
                    }
                }

            };
            PebbleKit.registerReceivedDataHandler(this, mReceiver);
        }
    }

    /*
    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();

        try {
            unregisterReceiver(mReceiver);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            mReceiver = null;
        }
    }
    */

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            unregisterReceiver(mReceiver);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            mReceiver = null;
        }

        PebbleKit.closeAppOnPebble(getApplicationContext(), Constants.SPORTS_UUID);
    }

    @Override
    protected void play() {
        super.play();

        PebbleDictionary out = new PebbleDictionary();
        out.addUint8(Constants.SPORTS_STATE_KEY, (byte) Constants.SPORTS_STATE_PAUSED);
        PebbleKit.sendDataToPebble(getApplicationContext(), Constants.SPORTS_UUID, out);
    }

    @Override
    protected void pause() {
        super.pause();

        PebbleDictionary out = new PebbleDictionary();
        out.addUint8(Constants.SPORTS_STATE_KEY, (byte) Constants.SPORTS_STATE_RUNNING);
        PebbleKit.sendDataToPebble(getApplicationContext(), Constants.SPORTS_UUID, out);
    }
}
