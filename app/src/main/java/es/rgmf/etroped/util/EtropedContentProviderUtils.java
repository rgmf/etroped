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

package es.rgmf.etroped.util;

import android.content.Context;
import android.database.Cursor;

import es.rgmf.etroped.data.EtropedContract;

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public final class EtropedContentProviderUtils {

    /**
     * Query activity for getting sport field value.
     *
     * @param context The Android context.
     * @param activityId The activiy id to query.
     * @return The sport id from activity or null if any or something was wrong.
     */
    public static Integer getSportFromActivityId(Context context, long activityId) {
        String selection = EtropedContract.ActivityEntry._ID + "=?";
        String[] selectionArgs = {Long.toString(activityId)};

        Cursor cursor = context.getContentResolver().query(
                EtropedContract.ActivityEntry.CONTENT_URI,
                null, selection, selectionArgs, null);

        if (cursor != null && cursor.getCount() == 1 && cursor.moveToFirst()) {
            int index = cursor.getColumnIndex(EtropedContract.ActivityEntry.COLUMN_SPORT);
            return cursor.getInt(index);
        }

        return null;
    }
}
