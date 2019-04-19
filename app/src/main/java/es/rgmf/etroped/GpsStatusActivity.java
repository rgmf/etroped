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

package es.rgmf.etroped;

import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import es.rgmf.etroped.service.GpsStatusService;
import es.rgmf.etroped.workout.ElevationFactory;
import es.rgmf.etroped.workout.ElevationPressureWorkout;
import es.rgmf.etroped.workout.ElevationWorkout;

public class GpsStatusActivity extends AppCompatActivity implements
        LocationListener, GpsStatusService.ILocationManagerForStatus {

    private static final String TAG = GpsStatusActivity.class.getSimpleName();

    private TextView mTvLat;
    private TextView mTvLon;
    private TextView mTvSatellites;
    private TextView mTvAccuracy;
    private TextView mTvGpsAltitude;
    private TextView mTvPressure;
    private TextView mTvBarAltitude;
    private TextView mTvGeoidheight;
    private TextView mTvPdop;
    private TextView mTvVdop;
    private TextView mTvHdop;

    private LocationManager mLocationManager;

    private GpsStatusService mGpsService;

    private ElevationWorkout mElevationWorkout;

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_status);

        mTvLat = (TextView) findViewById(R.id.tv_lat);
        mTvLon = (TextView) findViewById(R.id.tv_lon);
        mTvSatellites = (TextView) findViewById(R.id.tv_satellites);
        mTvAccuracy = (TextView) findViewById(R.id.tv_accuracy);
        mTvGpsAltitude = (TextView) findViewById(R.id.tv_altitude);
        mTvPressure = (TextView) findViewById(R.id.tv_pressure);
        mTvBarAltitude = (TextView) findViewById(R.id.tv_barometer_altitude);
        mTvGeoidheight = (TextView) findViewById(R.id.tv_geoidheight);
        mTvPdop = (TextView) findViewById(R.id.tv_pdop);
        mTvHdop = (TextView) findViewById(R.id.tv_hdop);
        mTvVdop = (TextView) findViewById(R.id.tv_vdop);

        mLocationManager = (LocationManager) getApplicationContext()
                .getSystemService(LOCATION_SERVICE);
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch(SecurityException e) {
            Log.i(TAG, "fail to request location update, ignore", e);
        } catch(IllegalArgumentException e) {
            Log.i(TAG, "network provider does not exist, " + e.getMessage());
        }

        mGpsService = new GpsStatusService(this);
        try {
            mLocationManager.addNmeaListener(mGpsService);
            mLocationManager.addGpsStatusListener(mGpsService);
        } catch (java.lang.SecurityException e) {
            Log.i(TAG, "fail to adding gps service like nmea/status listener, ignore", e);
        }

        mElevationWorkout = ElevationFactory.newInstance(this);
        if (!(mElevationWorkout instanceof ElevationPressureWorkout)) {
            mElevationWorkout.destroy();
            mElevationWorkout = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mLocationManager.removeNmeaListener(mGpsService);
        mLocationManager.removeGpsStatusListener(mGpsService);
        mLocationManager.removeUpdates(this);
    }

    private void updateViews(Location location) {

        // Data from location directly.
        mTvLat.setText(Double.toString(location.getLatitude()));
        mTvLon.setText(Double.toString(location.getLongitude()));
        mTvAccuracy.setText(Integer.toString((int) location.getAccuracy()));
        mTvGpsAltitude.setText(Integer.toString((int) location.getAltitude()));

        // If there is information about barometer pressure altitude set data about that.
        if (mElevationWorkout != null) {
            mTvPressure.setText(
                    Integer.toString((int) ((ElevationPressureWorkout) mElevationWorkout).getCurrentPressure()));
            mTvBarAltitude.setText(Integer.toString((int) mElevationWorkout.getElevation()));
        }

        // If there are extra information set them.
        if (location.getExtras() != null) {
            mTvSatellites.setText(
                    strOrNone(
                            location.getExtras().getString(
                                    GpsStatusService.GPS_SATELLITES_USED_IN_FIX)) +
                     " / " +
                    strOrNone(
                            location.getExtras().getString(
                                    GpsStatusService.GPS_SATELLITES_VISIBLE)
                    )
            );
            mTvGeoidheight.setText(
                    strOrNone(
                            location.getExtras().getString(GpsStatusService.GPS_GEO_ID_HEIGHT)
                    )
            );
            mTvPdop.setText(strOrNone(location.getExtras().getString(GpsStatusService.GPS_PDOP)));
            mTvVdop.setText(strOrNone(location.getExtras().getString(GpsStatusService.GPS_VDOP)));
            mTvHdop.setText(strOrNone(location.getExtras().getString(GpsStatusService.GPS_HDOP)));
        }
        else {
            mTvSatellites.setText("--/--");
            mTvGeoidheight.setText("--");
            mTvPdop.setText("--");
            mTvVdop.setText("--");
            mTvHdop.setText("--");
        }
    }

    private String strOrNone(String s) {
        if (s == null)
            return "--";

        return s;
    }

    @Override
    public void onLocationChanged(Location location) {

        // Add extra information on location.
        Location l = mGpsService.addStatusInformation(location);

        // Refresh information about elevation obtained from pressure barometer if exists.
        if (mElevationWorkout != null)
            mElevationWorkout.refresh(location);

        updateViews(l);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public LocationManager getLocationManager() {
        return mLocationManager;
    }
}
