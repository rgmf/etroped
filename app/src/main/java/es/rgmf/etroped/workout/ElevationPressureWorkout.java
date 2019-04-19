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
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.util.Log;

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class ElevationPressureWorkout extends ElevationWorkout implements SensorEventListener {

    private static final String TAG = ElevationPressureWorkout.class.getSimpleName();

    private SensorManager mSensorManager;
    private Sensor mPressure;

    private float mCurrentPressure;
    private double[] mLatestPressures;
    private int mCurrentItem;
    private static final int ITEMS = 10;

    private double mPressureElevation;

    public ElevationPressureWorkout(Context context) {
        super(context);

        // Get an instance of the sensor service, and use that to get an instance of
        // a particular sensor.
        mSensorManager = (SensorManager) context.getApplicationContext()
                .getSystemService(Context.SENSOR_SERVICE);
        mPressure = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        //mSensorManager.registerListener(this, mPressure, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mPressure, SensorManager.SENSOR_DELAY_UI);

        mPressureElevation = NONE;

        mLatestPressures = new double[ITEMS];
        for (int i = 0; i < ITEMS; i++)
            mLatestPressures[i] = NONE;
        mCurrentItem = 0;
    }

    @Override
    public double getElevationForGainAndLost() {
        Log.d(TAG, "getElevationForGainAndLost - PressureElevation: " + mPressureElevation);
        return mPressureElevation;
    }

    @Override
    public double getPressureElevation() {
        return mPressureElevation;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double sum = 0d;
        int sumItems = 0;
        mLatestPressures[mCurrentItem] = sensorEvent.values[0];
        mCurrentItem = mCurrentItem + 1 < ITEMS ? mCurrentItem + 1 : 0;

        //Log.d(TAG, "Pressures----------------------------------");
        for (double p : mLatestPressures) {
            if (p == NONE) {
                break;
            }

            //Log.d(TAG, p + "");

            sum += p;
            sumItems++;
        }
        //Log.d(TAG, "End Pressures----------------------------------");

        if (sumItems == ITEMS) {
            mCurrentPressure = (float) (sum / ITEMS);

            mPressureElevation = mSensorManager.getAltitude(
                    SensorManager.PRESSURE_STANDARD_ATMOSPHERE,
                    mCurrentPressure
            );
        }



        /*
        mCurrentPressure = sensorEvent.values[0];

        mPressureElevation = mSensorManager.getAltitude(
                SensorManager.PRESSURE_STANDARD_ATMOSPHERE,
                mCurrentPressure
        );
        */
        Log.d(TAG, "Current pressure: " + mCurrentPressure + " - Pressure elevation: " + mPressureElevation);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void destroy() {

        mSensorManager.unregisterListener(this);

    }

    public float getCurrentPressure() {
        return mCurrentPressure;
    }
}
