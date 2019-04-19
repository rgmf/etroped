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

import android.Manifest;
import android.app.Service;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.Iterator;

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class GpsStatusService implements GpsStatus.Listener, GpsStatus.NmeaListener {

    private static final String TAG = GpsStatusService.class.getSimpleName();

    public static final String GPS_GEO_ID_HEIGHT = "geo_id_height";
    public static final String GPS_PDOP = "gps_pdop";
    public static final String GPS_VDOP = "gps_vdop";
    public static final String GPS_HDOP = "gps_hdop";
    public static final String GPS_SATELLITES_VISIBLE = "max_satellites_visible";
    public static final String GPS_SATELLITES_USED_IN_FIX = "satellites_used";

    private String mGeoIdHeight = null;
    private String m3dfixValue = null;
    private String mPdop = null;
    private String mVdop = null;
    private String mHdop = null;

    private int mNumberOfReads;

    private ILocationManagerForStatus mService;

    private int mSatellitesUsedInFix;
    private int mSatellitesVisible;

    public interface ILocationManagerForStatus {
        LocationManager getLocationManager();
    }

    public GpsStatusService(ILocationManagerForStatus service) {
        mNumberOfReads = 0;
        mService = service;
    }

    @Override
    public void onGpsStatusChanged(int event) {

        switch (event) {
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                break;

            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:

                try {
                    GpsStatus status = mService.getLocationManager().getGpsStatus(null);

                    int maxSatellites = status.getMaxSatellites();

                    Iterator<GpsSatellite> it = status.getSatellites().iterator();
                    mSatellitesVisible = 0;
                    mSatellitesUsedInFix=0;

                    while (it.hasNext() && mSatellitesVisible <= maxSatellites) {
                        GpsSatellite sat = it.next();
                        if(sat.usedInFix()){
                            mSatellitesUsedInFix++;
                        }
                        mSatellitesVisible++;
                    }

                } catch (java.lang.SecurityException ex) {
                    Log.i(TAG, "fail to request location update, ignore", ex);
                }

                break;

            case GpsStatus.GPS_EVENT_STARTED:
                break;

            case GpsStatus.GPS_EVENT_STOPPED:
                break;

        }
    }

    @Override
    public void onNmeaReceived(long tiemstamp, String nmeaSentence) {

        if(nmeaSentence != null && !nmeaSentence.isEmpty()) {
            NmeaSentence nmea = new NmeaSentence(nmeaSentence);

            mGeoIdHeight = nmea.getGeoIdHeight();
            m3dfixValue = nmea.get3dfix() != null ? nmea.get3dfix() : m3dfixValue;
            mPdop = nmea.getPdop() != null ? nmea.getPdop() : mPdop;
            mVdop = nmea.getVdop() != null ? nmea.getVdop() : mVdop;
            mHdop = nmea.getHdop() != null ? nmea.getHdop() : mHdop;
        }
    }

    public Location addStatusInformation(Location location) {
        Bundle bundle = new Bundle();
        bundle.putString(GPS_GEO_ID_HEIGHT, mGeoIdHeight);
        bundle.putString(GPS_PDOP, mPdop);
        bundle.putString(GPS_VDOP, mVdop);
        bundle.putString(GPS_HDOP, mHdop);
        bundle.putString(GPS_SATELLITES_USED_IN_FIX, Integer.toString(mSatellitesUsedInFix));
        bundle.putString(GPS_SATELLITES_VISIBLE, Integer.toString(mSatellitesVisible));
        location.setExtras(bundle);

        return location;
    }

    /**
     * @return True if GPS has a good quality.
     */
    public boolean isReady() {

        mNumberOfReads++;
        if (mNumberOfReads >= 5)
            return true;
        else
            return false;

        /*

        // From Wikipedia:
        // https://en.wikipedia.org/wiki/Dilution_of_precision_(navigation)#Meaning_of_DOP_Values
        Log.e(TAG, "isReady?");

        if (m3dfixValue != null && mPdop != null) {

            Log.e(TAG, "3D Fix: " + m3dfixValue);
            Log.e(TAG, "PDOP: " + mPdop);

            if (Float.valueOf(m3dfixValue) >= 3 && Float.valueOf(mPdop) < 5)
                return true;
        }

        return true;
        */
    }
}
