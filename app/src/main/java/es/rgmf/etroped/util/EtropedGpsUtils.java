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

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public final class EtropedGpsUtils {

    /**
     * Uses the Haversine formula to calculate the distance between to latitude and longitude coordinates.
     *
     * Haversine formula:
     * A = sin²(Δlat/2) + cos(lat1) . cos(lat2) . sin²(Δlong/2)
     * C = 2 . atan2(√a, √(1−a))
     * D = R . c
     * R = radius of earth, 6371 km
     *
     * All angles are in radians.
     *
     * @param latitude1  The first point's latitude.
     * @param longitude1 The first point's longitude.
     * @param latitude2  The second point's latitude.
     * @param longitude2 The second point's longitude.
     * @return The distance between the two points in meters.
     */
    public static double calculateDistance(double latitude1, double longitude1,
                                           double latitude2, double longitude2) {

        double deltaLatitude = Math.toRadians(Math.abs(latitude1 - latitude2));
        double deltaLongitude = Math.toRadians(Math.abs(longitude1 - longitude2));
        double latitude1Rad = Math.toRadians(latitude1);
        double latitude2Rad = Math.toRadians(latitude2);

        double a = Math.pow(Math.sin(deltaLatitude / 2), 2) +
                (Math.cos(latitude1Rad) * Math.cos(latitude2Rad) *
                        Math.pow(Math.sin(deltaLongitude / 2), 2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return 6371 * c * 1000;
    }
}
