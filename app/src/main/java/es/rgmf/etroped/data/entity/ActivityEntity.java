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

import android.content.Context;
import android.database.Cursor;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import es.rgmf.etroped.data.EtropedContract;

/**
 * This class represent an Activity entity.
 *
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class ActivityEntity implements Serializable {
    private int id;
    private long startTime;
    private long endTime;
    private long time;
    private double distance;
    private String name;
    private String comment;

    private int fkSport;

    private LinkedList<LocationEntity> mLocationsList;

    public ActivityEntity() {
        mLocationsList = new LinkedList<>();
    }

    public ActivityEntity(Cursor cursor) {

        int idxId = cursor.getColumnIndex(EtropedContract.ActivityEntry._ID);
        int idxStartTime = cursor.getColumnIndex(EtropedContract.ActivityEntry.COLUMN_START_TIME);
        int idxEndTime = cursor.getColumnIndex(EtropedContract.ActivityEntry.COLUMN_END_TIME);
        int idxTime = cursor.getColumnIndex(EtropedContract.ActivityEntry.COLUMN_TIME);
        int idxDistance = cursor.getColumnIndex(EtropedContract.ActivityEntry.COLUMN_DISTANCE);
        int idxName = cursor.getColumnIndex(EtropedContract.ActivityEntry.COLUMN_NAME);
        int idxComment = cursor.getColumnIndex(EtropedContract.ActivityEntry.COLUMN_COMMENT);
        int idxFkSport = cursor.getColumnIndex(EtropedContract.ActivityEntry.COLUMN_SPORT);

        id = cursor.getInt(idxId);
        startTime = cursor.getLong(idxStartTime);
        endTime = cursor.getLong(idxEndTime);
        time = cursor.getLong(idxTime);
        distance = cursor.getDouble(idxDistance);
        name = cursor.getString(idxName);
        comment = cursor.getString(idxComment);
        fkSport = cursor.getInt(idxFkSport);

        mLocationsList = new LinkedList<>();
    }

    public void addLocation(LocationEntity locationEntity) {
        mLocationsList.add(locationEntity);
    }

    public List<LocationEntity> getLocations() {
        return mLocationsList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getFkSport() {
        return fkSport;
    }

    public void setFkSport(int fkSport) {
        this.fkSport = fkSport;
    }
}
