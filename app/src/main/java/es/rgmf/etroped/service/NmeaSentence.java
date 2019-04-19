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

package es.rgmf.etroped.service;

/**
 * All information about nmea sentence can be found here:
 * http://www.gpsinformation.org/dale/nmea.htm
 *
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class NmeaSentence {

    private String[] mNmeaParts;

    public NmeaSentence(String nmeaSentence) {

        if(nmeaSentence != null && !nmeaSentence.isEmpty()) {
            mNmeaParts = nmeaSentence.split(",");
        }
        else {
            mNmeaParts = new String[]{""};
        }
    }

    public String getGeoIdHeight() {
        if (mNmeaParts[0].equalsIgnoreCase("$GPGGA")) {
            if (mNmeaParts.length > 11 && mNmeaParts[11] != null && !mNmeaParts[11].isEmpty()) {
                return mNmeaParts[11];
            }
        }

        return null;
    }

    public String get3dfix() {
        if (mNmeaParts[0].equalsIgnoreCase("$GPGSA")) {
            if (mNmeaParts.length > 2 && mNmeaParts[2] != null && !mNmeaParts[2].isEmpty()) {
                return mNmeaParts[2];
            }
        }

        return null;
    }

    public String getPdop() {
        if (mNmeaParts[0].equalsIgnoreCase("$GPGSA")) {
            if (mNmeaParts.length > 15 && mNmeaParts[15] != null && !mNmeaParts[15].isEmpty()) {
                return mNmeaParts[15];
            }
        }

        return null;
    }

    public String getVdop(){
        if (mNmeaParts[0].equalsIgnoreCase("$GPGSA")) {
            if (mNmeaParts.length > 17 && mNmeaParts[17] != null && !mNmeaParts[17].isEmpty() && !mNmeaParts[17].startsWith("*")) {
                return mNmeaParts[17].split("\\*")[0];
            }
        }

        return null;
    }

    public String getHdop(){
        if (mNmeaParts[0].equalsIgnoreCase("$GPGGA")) {
            if (mNmeaParts.length > 8 && mNmeaParts[8] != null && !mNmeaParts[8].isEmpty()) {
                return mNmeaParts[8];
            }
        }
        else if (mNmeaParts[0].equalsIgnoreCase("$GPGSA")) {
            if (mNmeaParts.length > 16 && mNmeaParts[16] != null && !mNmeaParts[16].isEmpty()) {
                return mNmeaParts[16];
            }
        }

        return null;
    }
}
