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

package es.rgmf.etroped.helper;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public final class StatusHelper {

    public static boolean hasBarometer(Context context) {

        SensorManager sm = (SensorManager) context.getApplicationContext()
                .getSystemService(Context.SENSOR_SERVICE);

        return sm.getDefaultSensor(Sensor.TYPE_PRESSURE) != null;
    }
}
