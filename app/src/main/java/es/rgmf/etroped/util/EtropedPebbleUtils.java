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
import android.util.Log;

public final class EtropedPebbleUtils {
    /**
     * @param context
     * @param distance Distance in meters.
     * @return Distance in km or miles.
     */
    public static float getDistance(Context context, float distance) {
        if(EtropedPreferences.isMetric(context)) {
            return distance / 1000f;
        }
        else {
            return (float) EtropedUnitsUtils.fromMetersToYards(
                    EtropedUnitsUtils.fromYardsToMiles(distance));
        }
    }

    /**
     * @param context
     * @param speed Speed in seconds per meter.
     * @return Speed in km/h or mi/h.
     */
    public static float getSpeed(Context context, float speed) {
        if(EtropedPreferences.isMetric(context)) {
            return speed * 3.6f;
        }
        else {
            return speed * 2.236936f;
        }
    }

    /**
     * @param context
     * @param pace Pace in seconds per meter.
     * @return Pace in seconds per km or seconds per miles.
     */
    public static int getPace(Context context, float pace) {
        if(EtropedPreferences.isMetric(context)) {
            return (int) (pace * 1000f);
        }
        else {
            return (int) (pace * 1609.344f);
        }
    }
}
