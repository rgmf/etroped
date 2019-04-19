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

package es.rgmf.etroped.data.entity;

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class LapEntity {
    private long mElapsedTime;
    private double mDistance;
    private double mAltitude;
    private double mAvgSpeed;
    private double mMaxSpeed;

    public long getElapsedTime() {
        return mElapsedTime;
    }

    public void setElapsedTime(long mElapsedTime) {
        this.mElapsedTime = mElapsedTime;
    }

    public double getDistance() {
        return mDistance;
    }

    public void setDistance(double mElapsedDistance) {
        this.mDistance = mElapsedDistance;
    }

    public double getAltitude() {
        return mAltitude;
    }

    public void setAltitude(double mAltitude) {
        this.mAltitude = mAltitude;
    }

    public double getAvgSpeed() {
        return mAvgSpeed;
    }

    public void setAvgSpeed(double mAvgSpeed) {
        this.mAvgSpeed = mAvgSpeed;
    }

    public double getMaxSpeed() {
        return mMaxSpeed;
    }

    public void setMaxSpeed(double mMaxSpeed) {
        this.mMaxSpeed = mMaxSpeed;
    }
}
