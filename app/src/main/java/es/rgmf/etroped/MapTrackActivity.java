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

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.Loader;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewTreeObserver;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.util.List;

import es.rgmf.etroped.data.ActivityProvider;

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class MapTrackActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<GeoPoint>> {

    private static final String TAG = MapTrackActivity.class.getSimpleName();

    public static final String EXTRA_ACTIVITY_ID = "activity_id";

    private static final int GEOPOINTS_LOADER_ID = 1;

    private MapView mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_track);

        mMap = (MapView) findViewById(R.id.map);

        Intent intent = getIntent();
        int activityId = intent.getIntExtra(EXTRA_ACTIVITY_ID, -1);
        if (activityId >= 0) {
            // Loads all geo points from the activity.
            Bundle bundle = new Bundle();
            bundle.putInt(EXTRA_ACTIVITY_ID, activityId);
            getLoaderManager().initLoader(GEOPOINTS_LOADER_ID, bundle, this);
        }
    }

    @Override
    public Loader<List<GeoPoint>> onCreateLoader(int i, final Bundle args) {

        return new AsyncTaskLoader<List<GeoPoint>>(this) {

            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Override
            public List<GeoPoint> loadInBackground() {
                int id = args.getInt(EXTRA_ACTIVITY_ID);
                return ActivityProvider.getLocationsActivity(getContext(), id);
            }

            @Override
            public void deliverResult(List<GeoPoint> data) {
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<GeoPoint>> loader, List<GeoPoint> geoPoints) {
        mMap.setTileSource(TileSourceFactory.MAPNIK);

        mMap.setBuiltInZoomControls(true);
        mMap.setMultiTouchControls(true);
        mMap.setTilesScaledToDpi(true);
        mMap.getController().setCenter(geoPoints.get(0));
        mMap.getController().setZoom(14);

        int count = 0;
        double lat, lon, north = 0, est = 0, sud = 0, west = 0;
        for (GeoPoint gp : geoPoints) {
            lat = gp.getLatitude();
            lon = gp.getLongitude();

            if ((count == 0) || (lat > north)) north = lat;
            if ((count == 0) || (lat < sud)) sud = lat;
            if ((count == 0) || (lon < west)) west = lon;
            if ((count == 0) || (lon > est)) est = lon;
            count++;
        }

        zoomToBounds(new BoundingBox(north, est, sud, west));

        Polyline line = new Polyline();
        line.setColor(getResources().getColor(android.R.color.holo_red_dark));
        line.setPoints(geoPoints);

        mMap.getOverlayManager().add(line);
    }

    @Override
    public void onLoaderReset(Loader<List<GeoPoint>> loader) {

    }

    public void zoomToBounds(final BoundingBox box) {
        if (mMap.getHeight() > 0) {
            mMap.zoomToBoundingBox(box, true);

        } else {
            ViewTreeObserver vto = mMap.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    mMap.zoomToBoundingBox(box, true);
                    ViewTreeObserver vto2 = mMap.getViewTreeObserver();
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        vto2.removeGlobalOnLayoutListener(this);
                    } else {
                        vto2.removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }
    }
}
