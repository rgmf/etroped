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

import es.rgmf.etroped.workout.ElevationWorkout;

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class ElevationTask extends ElevationWorkout {

    Location mLocation;
    private double mPressureAltitude;

    public ElevationTask(Context context) {
        super(context);

        mLocation = new Location("");
    }

    public void refresh(double altitude, double pressureAltitude) {
        mPressureAltitude = pressureAltitude;
        refresh(altitude);
    }

    public void refresh(double altitude) {
        mElevation = altitude;
        mLocation.setAltitude(altitude);

        super.refresh(mLocation);
    }

    @Override
    public double getElevation() {
        return mElevation;
    }

    @Override
    public double getElevationForGainAndLost() {
        if (!isNone(mPressureAltitude)) {
            Log.d("ElevationTask", "Mierda, no debería estar aquí: " + mPressureAltitude);
            return mPressureAltitude;
        }
        else {
            Log.d("ElevationTask", "Bien, debería estar aquí: " + mElevation);
            return mElevation;
        }
    }

    @Override
    public void destroy() {

    }
}
