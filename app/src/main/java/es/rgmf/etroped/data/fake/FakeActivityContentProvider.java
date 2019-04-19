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

package es.rgmf.etroped.data.fake;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import es.rgmf.etroped.data.EtropedContract;

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class FakeActivityContentProvider extends ContentProvider {

    // Define final integer constants for the directory of activities and a single item.
    // It's convention to use 100, 200, 300, etc for directories,
    // and related ints (101, 102, ..) for items in that directory.
    public static final int ACTIVITIES = 100;
    public static final int ACTIVITY_WITH_ID = 101;

    // Declare a static variable for the Uri matcher that you construct
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    // Member variable for a MatrixCursor that's contain fake activity datas for testing.
    private MatrixCursor mCursor;

    /**
     * Initialize a new matcher object without any matches, then use
     * .addURI(String authority, String path, int match) to add matches.
     */
    public static UriMatcher buildUriMatcher() {

        // Initialize a UriMatcher with no matches by passing in NO_MATCH to the constructor
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // All paths added to the UriMatcher have a corresponding int.
        // For each kind of uri you may want to access, add the corresponding match with addURI.
        // The two calls below add matches for the activity directory and a single item by ID.
        uriMatcher.addURI(EtropedContract.ActivityEntry.AUTHORITY,
                EtropedContract.ActivityEntry.PATH,
                ACTIVITIES);

        uriMatcher.addURI(EtropedContract.ActivityEntry.AUTHORITY,
                EtropedContract.ActivityEntry.PATH + "/#",
                ACTIVITY_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        String[] columns = new String[] {
                EtropedContract.ActivityEntry._ID,
                EtropedContract.ActivityEntry.COLUMN_START_TIME,
                EtropedContract.ActivityEntry.COLUMN_END_TIME,
                EtropedContract.ActivityEntry.COLUMN_TIME,
                EtropedContract.ActivityEntry.COLUMN_DISTANCE,
                EtropedContract.ActivityEntry.COLUMN_NAME,
                EtropedContract.ActivityEntry.COLUMN_COMMENT,
                EtropedContract.ActivityEntry.COLUMN_SPORT
        };
        mCursor = new MatrixCursor(columns);

        mCursor.addRow(new Object[] {1, 1504278000000L, 1504285200000L, 7200000L, 58000L,
                "Bici dos horas", "Buena ruta en bicicleta de carretera",
                EtropedContract.SPORT_BIKE});

        mCursor.addRow(new Object[] {2, 1504452600000L, 1504457100000L, 4140000L, 30000L,
                "Bici una hora y cuarto", "Buena ruta en bicicleta de carretera",
                EtropedContract.SPORT_BIKE});

        mCursor.addRow(new Object[] {3, 1505066400000L, 1505068200000L, 1800000L, 5000L,
                "Media hora de running", "Salida tranquila a correr",
                EtropedContract.SPORT_RUNNING});

        mCursor.addRow(new Object[] {4, 1505152800000L, 1505156400000L, 3600000L, 6000L,
                "Caminata una hora", "Paseo con Otto",
                EtropedContract.SPORT_WALKING});

        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s,
                        @Nullable String[] strings1, @Nullable String s1) {
        int matchingCode = sUriMatcher.match(uri);
        switch (matchingCode) {
            case ACTIVITIES:
                return mCursor;
            case ACTIVITY_WITH_ID:
                throw new UnsupportedOperationException("Not yet implemented");
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Unknown uri " + uri);
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        throw new UnsupportedOperationException("Unknown uri " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        throw new UnsupportedOperationException("Unknown uri " + uri);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s,
                      @Nullable String[] strings) {
        throw new UnsupportedOperationException("Unknown uri " + uri);
    }
}
