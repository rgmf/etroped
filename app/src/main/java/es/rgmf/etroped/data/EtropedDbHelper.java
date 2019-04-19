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

package es.rgmf.etroped.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import es.rgmf.etroped.R;
import es.rgmf.etroped.SettingsActivity;

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class EtropedDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "etroped";
    public static final int DATABSE_VERSION = 2;

    private Context mContext;

    public EtropedDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABSE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_SPORT_TABLE = "CREATE TABLE " +
                EtropedContract.SportEntry.TABLE_NAME + " (" +
                EtropedContract.SportEntry._ID + " INTEGER PRIMARY KEY, " +
                EtropedContract.SportEntry.COLUMN_NAME + " TEXT NOT NULL);";


        final String SQL_CREATE_ACTIVITY_TABLE = "CREATE TABLE " +
                EtropedContract.ActivityEntry.TABLE_NAME + " (" +
                EtropedContract.ActivityEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                EtropedContract.ActivityEntry.COLUMN_START_TIME + " INTEGER, " +
                EtropedContract.ActivityEntry.COLUMN_END_TIME + " INTEGER, " +
                EtropedContract.ActivityEntry.COLUMN_TIME + " INTEGER, " +
                EtropedContract.ActivityEntry.COLUMN_DISTANCE + " REAL, " +
                EtropedContract.ActivityEntry.COLUMN_NAME + " TEXT, " +
                EtropedContract.ActivityEntry.COLUMN_COMMENT + " TEXT, " +
                EtropedContract.ActivityEntry.COLUMN_SPORT + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + EtropedContract.ActivityEntry.COLUMN_SPORT + ") REFERENCES " +
                EtropedContract.SportEntry.TABLE_NAME + " (" + EtropedContract.SportEntry._ID +
                "));";

        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " +
                EtropedContract.LocationEntry.TABLE_NAME + " (" +
                EtropedContract.LocationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                EtropedContract.LocationEntry.COLUMN_ACTIVITY + " INTEGER NOT NULL, " +
                EtropedContract.LocationEntry.COLUMN_LAP + " INTEGER NOT NULL, " +
                EtropedContract.LocationEntry.COLUMN_TIME + " INTEGER NOT NULL, " +
                EtropedContract.LocationEntry.COLUMN_LONGITUDE + " REAL NOT NULL, " +
                EtropedContract.LocationEntry.COLUMN_LATITUDE + " REAL NOT NULL, " +
                EtropedContract.LocationEntry.COLUMN_TEMPERATURE + " REAL, " +
                EtropedContract.LocationEntry.COLUMN_PRESSURE + " REAL, " +
                EtropedContract.LocationEntry.COLUMN_ELAPSED + " INTEGER, " +
                EtropedContract.LocationEntry.COLUMN_DISTANCE + " REAL, " +
                EtropedContract.LocationEntry.COLUMN_GPS_ALTITUDE + " REAL, " +
                EtropedContract.LocationEntry.COLUMN_PRESSURE_ALTITUDE + " REAL, " +
                EtropedContract.LocationEntry.COLUMN_ACCURACY + " REAL, " +
                EtropedContract.LocationEntry.COLUMN_SPEED + " REAL, " +
                EtropedContract.LocationEntry.COLUMN_BEARING + " REAL, " +
                EtropedContract.LocationEntry.COLUMN_SATELLITES + " INTEGER , " +
                "FOREIGN KEY (" + EtropedContract.LocationEntry.COLUMN_ACTIVITY
                + ") REFERENCES " +
                EtropedContract.ActivityEntry.TABLE_NAME + " (" +
                EtropedContract.ActivityEntry._ID + "));";

        // Creates tables defined above.
        sqLiteDatabase.execSQL(SQL_CREATE_SPORT_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ACTIVITY_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_LOCATION_TABLE);

        // Insert sports inside sport table (preload data).
        sqLiteDatabase.execSQL("INSERT INTO " + EtropedContract.SportEntry.TABLE_NAME + " (" +
                EtropedContract.SportEntry._ID + ", " +
                EtropedContract.SportEntry.COLUMN_NAME +

                ") VALUES (" +
                EtropedContract.SPORT_BIKE + ", '" +
                mContext.getString(R.string.sport_bike) +
                "'), (" +

                EtropedContract.SPORT_BIKE_ROAD + ", '" +
                mContext.getString(R.string.sport_bike_road) +
                "'), (" +

                EtropedContract.SPORT_BIKE_MTB + ", '" +
                mContext.getString(R.string.sport_bike_mtb) +
                "'), (" +

                EtropedContract.SPORT_RUNNING + ", '" +
                mContext.getString(R.string.sport_running) +
                "'), (" +

                EtropedContract.SPORT_WALKING + ", '" +
                mContext.getString(R.string.sport_walking) +
                "');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " +
                    EtropedContract.LocationEntry.TABLE_NAME + " (" +
                    EtropedContract.LocationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    EtropedContract.LocationEntry.COLUMN_ACTIVITY + " INTEGER NOT NULL, " +
                    EtropedContract.LocationEntry.COLUMN_LAP + " INTEGER NOT NULL, " +
                    EtropedContract.LocationEntry.COLUMN_TIME + " INTEGER NOT NULL, " +
                    EtropedContract.LocationEntry.COLUMN_LONGITUDE + " REAL NOT NULL, " +
                    EtropedContract.LocationEntry.COLUMN_LATITUDE + " REAL NOT NULL, " +
                    EtropedContract.LocationEntry.COLUMN_TEMPERATURE + " REAL, " +
                    EtropedContract.LocationEntry.COLUMN_PRESSURE + " REAL, " +
                    EtropedContract.LocationEntry.COLUMN_ELAPSED + " INTEGER, " +
                    EtropedContract.LocationEntry.COLUMN_DISTANCE + " REAL, " +
                    EtropedContract.LocationEntry.COLUMN_GPS_ALTITUDE + " REAL, " +
                    EtropedContract.LocationEntry.COLUMN_PRESSURE_ALTITUDE + " REAL DEFAULT NULL, " +
                    EtropedContract.LocationEntry.COLUMN_ACCURACY + " REAL, " +
                    EtropedContract.LocationEntry.COLUMN_SPEED + " REAL, " +
                    EtropedContract.LocationEntry.COLUMN_BEARING + " REAL, " +
                    EtropedContract.LocationEntry.COLUMN_SATELLITES + " INTEGER , " +
                    "FOREIGN KEY (" + EtropedContract.LocationEntry.COLUMN_ACTIVITY
                    + ") REFERENCES " +
                    EtropedContract.ActivityEntry.TABLE_NAME + " (" +
                    EtropedContract.ActivityEntry._ID + "));";

            final String SQL_INSERT = "INSERT INTO " + EtropedContract.LocationEntry.TABLE_NAME + "(" +
                    EtropedContract.LocationEntry._ID + ", " +
                    EtropedContract.LocationEntry.COLUMN_ACTIVITY + ", " +
                    EtropedContract.LocationEntry.COLUMN_LAP + ", " +
                    EtropedContract.LocationEntry.COLUMN_TIME + ", " +
                    EtropedContract.LocationEntry.COLUMN_LONGITUDE + ", " +
                    EtropedContract.LocationEntry.COLUMN_LATITUDE + ", " +
                    EtropedContract.LocationEntry.COLUMN_TEMPERATURE + ", " +
                    EtropedContract.LocationEntry.COLUMN_PRESSURE + ", " +
                    EtropedContract.LocationEntry.COLUMN_ELAPSED + ", " +
                    EtropedContract.LocationEntry.COLUMN_DISTANCE + ", " +
                    EtropedContract.LocationEntry.COLUMN_GPS_ALTITUDE + ", " +
                    EtropedContract.LocationEntry.COLUMN_PRESSURE_ALTITUDE + ", " +
                    EtropedContract.LocationEntry.COLUMN_ACCURACY + ", " +
                    EtropedContract.LocationEntry.COLUMN_SPEED + ", " +
                    EtropedContract.LocationEntry.COLUMN_BEARING + ", " +
                    EtropedContract.LocationEntry.COLUMN_SATELLITES + ") " +

                    "SELECT " +
                    EtropedContract.LocationEntry._ID + ", " +
                    EtropedContract.LocationEntry.COLUMN_ACTIVITY + ", " +
                    EtropedContract.LocationEntry.COLUMN_LAP + ", " +
                    EtropedContract.LocationEntry.COLUMN_TIME + ", " +
                    EtropedContract.LocationEntry.COLUMN_LONGITUDE + ", " +
                    EtropedContract.LocationEntry.COLUMN_LATITUDE + ", " +
                    EtropedContract.LocationEntry.COLUMN_TEMPERATURE + ", " +
                    EtropedContract.LocationEntry.COLUMN_PRESSURE + ", " +
                    EtropedContract.LocationEntry.COLUMN_ELAPSED + ", " +
                    EtropedContract.LocationEntry.COLUMN_DISTANCE + ", " +
                    EtropedContract.LocationEntry.COLUMN_GPS_ALTITUDE + ", " +
                    EtropedContract.LocationEntry.COLUMN_PRESSURE_ALTITUDE + ", " +
                    EtropedContract.LocationEntry.COLUMN_ACCURACY + ", " +
                    EtropedContract.LocationEntry.COLUMN_SPEED + ", " +
                    EtropedContract.LocationEntry.COLUMN_BEARING + ", " +
                    EtropedContract.LocationEntry.COLUMN_SATELLITES +

                    " FROM " + EtropedContract.LocationEntry.COLUMN_PRESSURE_ALTITUDE + "_old;";

            sqLiteDatabase.execSQL("ALTER TABLE " +
                    EtropedContract.LocationEntry.TABLE_NAME +
                    " RENAME TO " + EtropedContract.LocationEntry.COLUMN_PRESSURE_ALTITUDE + "_old;");
            sqLiteDatabase.execSQL(SQL_CREATE_LOCATION_TABLE);
            sqLiteDatabase.execSQL(SQL_INSERT);

        }
    }
}
