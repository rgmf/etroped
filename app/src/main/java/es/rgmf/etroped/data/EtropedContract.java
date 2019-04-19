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

import android.net.Uri;
import android.provider.BaseColumns;

import es.rgmf.etroped.R;

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class EtropedContract {

    // Sports identifiers.
    // It gets identifier from preferences ids strings.
    public static final int SPORT_BIKE = 100;
    public static final int SPORT_BIKE_ROAD = 101;
    public static final int SPORT_BIKE_MTB = 102;
    public static final int SPORT_RUNNING = 200;
    public static final int SPORT_WALKING = 300;

    /**
     * Inner class that defines the sport table contents.
     */
    public static final class SportEntry implements BaseColumns {

        // The authority, which is how your code knows which Content Provider to access
        public static final String AUTHORITY = "es.rgmf.etroped.sport";

        // The base content URI = "content://" + <authority>
        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

        // Define the possible paths for accessing data in this contract.
        // This is the path for the "activities" directory.
        public static final String PATH = "sports";

        // Sport content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();

        // Table name and columns name.
        public static final String TABLE_NAME = "sport";

        public static final String COLUMN_NAME = "name";
    }

    /**
     * Inner class that defines the activity table contents.
     */
    public static final class ActivityEntry implements BaseColumns {

        // The authority, which is how your code knows which Content Provider to access
        public static final String AUTHORITY = "es.rgmf.etroped.activity";

        // The base content URI = "content://" + <authority>
        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

        // Define the possible paths for accessing data in this contract.
        // This is the path for the "activities" directory.
        public static final String PATH = "activities";

        // ActivityEntity content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();

        // Table name and columns name.
        public static final String TABLE_NAME = "activity";

        public static final String COLUMN_START_TIME = "start_time";
        public static final String COLUMN_END_TIME = "end_time";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_DISTANCE = "distance";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_COMMENT = "comment";
        public static final String COLUMN_SPORT = "sport";
    }

    /**
     * Inner class that defines the location table contents.
     */
    public static final class LocationEntry implements BaseColumns {

        // The authority, which is how your code knows which Content Provider to access
        public static final String AUTHORITY = "es.rgmf.etroped.location";

        // The base content URI = "content://" + <authority>
        public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

        // This is the path for the "locations" directory.
        public static final String PATH = "locations";

        // LocationEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH).build();

        // Table name and columns name.
        public static final String TABLE_NAME = "location";

        public static final String COLUMN_ACTIVITY = "activity";
        public static final String COLUMN_LAP = "lap";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_TEMPERATURE = "temperature";
        public static final String COLUMN_PRESSURE = "pressure";
        public static final String COLUMN_ELAPSED = "elapsed";
        public static final String COLUMN_DISTANCE = "distance";
        public static final String COLUMN_GPS_ALTITUDE = "gps_altitude";
        public static final String COLUMN_PRESSURE_ALTITUDE = "pressure_altitude";
        public static final String COLUMN_ACCURACY = "accuracy";
        public static final String COLUMN_SPEED = "speed";
        public static final String COLUMN_BEARING = "bearing";
        public static final String COLUMN_SATELLITES = "satellites";
    }
}
