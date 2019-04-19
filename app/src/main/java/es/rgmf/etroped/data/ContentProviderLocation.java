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

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import es.rgmf.etroped.R;

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */

public class ContentProviderLocation extends ContentProvider {

    // Define final integer constants for the directory of locations and a single item.
    // It's convention to use 100, 200, 300, etc for directories,
    // and related ints (101, 102, ..) for items in that directory.
    public static final int LOCATIONS = 200;
    public static final int LOCATION_WITH_ID = 201;

    // Member variable for an EtropedDbHelper that's initialized in the onCreate() method.
    private EtropedDbHelper mDbHelper;

    // Declare a static variable for the Uri matcher that you construct
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    /**
     * Initialize a new matcher object without any matches, then use
     * .addURI(String authority, String path, int match) to add matches.
     */
    public static UriMatcher buildUriMatcher() {

        // Initialize a UriMatcher with no matches by passing in NO_MATCH to the constructor
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // All paths added to the UriMatcher have a corresponding int.
        // For each kind of uri you may want to access, add the corresponding match with addURI.
        // The two calls below add matches for the location directory and a single item by ID.
        uriMatcher.addURI(EtropedContract.LocationEntry.AUTHORITY,
                EtropedContract.LocationEntry.PATH,
                LOCATIONS);

        uriMatcher.addURI(EtropedContract.LocationEntry.AUTHORITY,
                EtropedContract.LocationEntry.PATH + "/#",
                LOCATION_WITH_ID);

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new EtropedDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection,  @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {

        final SQLiteDatabase sqLiteDatabase = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);

        switch (match) {
            case LOCATIONS:
                cursor = sqLiteDatabase.query(EtropedContract.LocationEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.err_unknown_uri, uri.toString()));
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        Uri resultUri;
        final SQLiteDatabase sqLiteDatabase = mDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);

        switch (match) {
            case LOCATIONS:
                long id = sqLiteDatabase.insert(EtropedContract.LocationEntry.TABLE_NAME,
                        null, contentValues);
                if (id > 0)
                    resultUri = ContentUris.withAppendedId(
                            EtropedContract.LocationEntry.CONTENT_URI, id);
                else
                    throw new SQLException(
                            getContext().getString(R.string.err_fail_insert_uri, uri));
                break;
            default:
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.err_unknown_uri, uri.toString()));
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return resultUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s,
                      @Nullable String[] strings) {
        return 0;
    }
}
