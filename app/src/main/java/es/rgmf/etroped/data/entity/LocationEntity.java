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

import android.database.Cursor;
import android.util.Log;

import java.io.Serializable;

import es.rgmf.etroped.data.EtropedContract;

/**
 * This class represent a location entity (a database entity representing).
 *
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class LocationEntity implements Serializable {
    private int id;
    private int lap;
    private long time;
    private double longitude;
    private double latitude;
    private double temperature;
    private double pressure;
    private long elapsed;
    private double distance;
    private double altitude;
    private double pressureAltitude;
    private float accuracy;
    private double speed;
    private double bearing;
    private int satellites;

    private int fkActivity;

    public LocationEntity(Cursor cursor) {
        int idxId = cursor.getColumnIndex(EtropedContract.LocationEntry._ID);
        int idxLap = cursor.getColumnIndex(EtropedContract.LocationEntry.COLUMN_LAP);
        int idxTime = cursor.getColumnIndex(EtropedContract.LocationEntry.COLUMN_TIME);
        int idxLongitude = cursor.getColumnIndex(EtropedContract.LocationEntry.COLUMN_LONGITUDE);
        int idxLatitude = cursor.getColumnIndex(EtropedContract.LocationEntry.COLUMN_LATITUDE);
        int idxTemperature = cursor.getColumnIndex(EtropedContract.LocationEntry.COLUMN_TEMPERATURE);
        int idxPressure = cursor.getColumnIndex(EtropedContract.LocationEntry.COLUMN_PRESSURE);
        int idxElapsed = cursor.getColumnIndex(EtropedContract.LocationEntry.COLUMN_ELAPSED);
        int idxDistance = cursor.getColumnIndex(EtropedContract.LocationEntry.COLUMN_DISTANCE);
        int idxAltitude = cursor.getColumnIndex(EtropedContract.LocationEntry.COLUMN_GPS_ALTITUDE);
        int idxPressureAltitude = cursor.getColumnIndex(EtropedContract.LocationEntry.COLUMN_PRESSURE_ALTITUDE);
        int idxAccuracy = cursor.getColumnIndex(EtropedContract.LocationEntry.COLUMN_ACCURACY);
        int idxSpeed = cursor.getColumnIndex(EtropedContract.LocationEntry.COLUMN_SPEED);
        int idxBearing = cursor.getColumnIndex(EtropedContract.LocationEntry.COLUMN_BEARING);
        int idxSatellites = cursor.getColumnIndex(EtropedContract.LocationEntry.COLUMN_SATELLITES);
        int idxFkActivity = cursor.getColumnIndex(EtropedContract.LocationEntry.COLUMN_ACTIVITY);

        id = cursor.getInt(idxId);
        lap = cursor.getInt(idxLap);
        time = cursor.getInt(idxTime);
        longitude = cursor.getFloat(idxLongitude);
        latitude = cursor.getFloat(idxLatitude);
        temperature = cursor.getFloat(idxTemperature);
        pressure = cursor.getFloat(idxPressure);
        elapsed = cursor.getInt(idxElapsed);
        distance = cursor.getFloat(idxDistance);
        altitude = cursor.getFloat(idxAltitude);

        if (!cursor.isNull(idxPressureAltitude))
            pressureAltitude = cursor.getFloat(idxPressureAltitude);
        else {
            pressureAltitude = Double.NaN;
        }


        accuracy = cursor.getFloat(idxAccuracy);
        speed = cursor.getFloat(idxSpeed);
        bearing = cursor.getFloat(idxBearing);
        satellites = cursor.getInt(idxSatellites);
        fkActivity = cursor.getInt(idxFkActivity);
    }

    public LocationEntity() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLap() {
        return lap;
    }

    public void setLap(int lap) {
        this.lap = lap;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public long getElapsed() {
        return elapsed;
    }

    public void setElapsed(long elapsed) {
        this.elapsed = elapsed;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public double getPressureAltitude() {
        return pressureAltitude;
    }

    public void setPressureAltitude(double pressureAltitude) {
        this.pressureAltitude = pressureAltitude;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getBearing() {
        return bearing;
    }

    public void setBearing(double bearing) {
        this.bearing = bearing;
    }

    public int getSatellites() {
        return satellites;
    }

    public void setSatellites(int satellites) {
        this.satellites = satellites;
    }

    public int getFkActivity() {
        return fkActivity;
    }

    public void setFkActivity(int fkActivity) {
        this.fkActivity = fkActivity;
    }
}
