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

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class DistanceWorkout {

    private static final int MIN_DISTANCE_IN_METERS = 2;

    private double mDistance;

    public static DistanceWorkout newInstance() {
        DistanceWorkout dw = new DistanceWorkout();
        return dw;
    }

    private DistanceWorkout() {
        mDistance = 0d;
    }

    public void refresh(Location location, double distance) {

        if (location.getSpeed() > 0) {
            mDistance = distance;
        }
        else {
            mDistance = 0d;
        }
    }

    public double getDistance() {
        return mDistance;
    }
}
