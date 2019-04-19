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

package es.rgmf.etroped.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import es.rgmf.etroped.ActivityDetailsActivity;
import es.rgmf.etroped.MapTrackActivity;
import es.rgmf.etroped.R;
import es.rgmf.etroped.data.EtropedContract;
import es.rgmf.etroped.data.entity.ActivityEntity;
import es.rgmf.etroped.util.EtropedDateTimeUtils;
import es.rgmf.etroped.util.EtropedUnitsUtils;
import es.rgmf.etroped.viewmodel.ActivityEntityViewModel;

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class InfoFragment extends Fragment {

    private static final String TAG = InfoFragment.class.getSimpleName();

    private View mRootView;
    private ActivityDetailsActivity mActivity;
    private ActivityEntityViewModel mViewModel;
    private ActivityEntity mActivityEntity;

    private MapView map;

    public InfoFragment() {
        // Required empty public constructor
    }

    public static InfoFragment newInstance() {

        InfoFragment fragment = new InfoFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // The view to return.
        View view;

        // Get the activity entity.
        mActivity = (ActivityDetailsActivity) getActivity();
        mViewModel = mActivity.getViewModel();
        mActivityEntity = mViewModel.getActivityEntity();

        // Build a view.
        switch (mActivityEntity.getFkSport()) {
            case EtropedContract.SPORT_RUNNING:
            case EtropedContract.SPORT_WALKING:
                view = buildRunningView(inflater, container, mActivityEntity.getFkSport());
                break;

            default:
                view = buildBikeView(inflater, container);
                break;
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Add the map and all its information.
        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().load(
                getActivity().getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(
                        getActivity().getApplicationContext()));

        map = (MapView) mRootView.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        List<GeoPoint> geoPoints = mViewModel.getGeoPoints();
        if (geoPoints.size() > 0) {
            map.getController().setCenter(geoPoints.get(0));
            map.getController().setZoom(14);
            zoomToBounds(mViewModel.getBoundingBox());

            Polyline line = new Polyline();
            line.setColor(getResources().getColor(android.R.color.holo_red_dark));
            line.setPoints(geoPoints);
        /*
        line.setOnClickListener(new Polyline.OnClickListener() {

            @Override
            public boolean onClick(Polyline polyline, MapView mapView, GeoPoint eventPos) {
                Toast.makeText(mapView.getContext(),
                        "polyline with " + polyline.getPoints().size() + "pts was tapped",
                        Toast.LENGTH_LONG).show();
                return false;
            }
        });
        */

            map.getOverlayManager().add(line);

            // Disables scrolling map (this map is only for previewing the track) handling the touch
            // events.
            map.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.e(TAG, "onTouch");
                    Intent intent = new Intent(getContext(), MapTrackActivity.class);
                    intent.putExtra(MapTrackActivity.EXTRA_ACTIVITY_ID, mActivityEntity.getId());
                    startActivity(intent);
                    return true;
                }
            });
        }


        /*
        // When user click on map then it starts an activity with the map detailed.
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onClick");
                Intent intent = new Intent(getContext(), MapTrackActivity.class);
                intent.putExtra(MapTrackActivity.EXTRA_ACTIVITY_ID, mActivityEntity.getId());
                startActivity(intent);
            }
        });
        */

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        map.onDetach();
    }

    /**
     * This method build the information bike view.
     *
     * @param inflater
     * @param container
     * @return
     */
    private View buildBikeView(LayoutInflater inflater, ViewGroup container) {

        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_activity_details_info_bike,
                container, false);

        TextView tvName = (TextView) mRootView.findViewById(R.id.tv_name);
        TextView tvDistance = (TextView) mRootView.findViewById(R.id.tv_distance);
        TextView tvElapsed = (TextView) mRootView.findViewById(R.id.tv_elapsed_time);
        TextView tvAvgSpeed = (TextView) mRootView.findViewById(R.id.tv_avg_speed);
        TextView tvAvgSpeedUnit = (TextView) mRootView.findViewById(R.id.tv_avg_speed_unit);
        TextView tvMaxSpeed = (TextView) mRootView.findViewById(R.id.tv_max_speed);
        TextView tvMaxSpeedUnit = (TextView) mRootView.findViewById(R.id.tv_max_speed_unit);
        TextView tvElevationGain = (TextView) mRootView.findViewById(R.id.tv_elevation_gain);
        TextView tvElevationGainUnit = (TextView) mRootView
                .findViewById(R.id.tv_elevation_gain_unit);

        double maxSpeed = mViewModel.getMaxSpeed();
        double elevationGain = mViewModel.getElevationGain();
        String speedUnit = EtropedUnitsUtils.speedUnit(mActivity);

        tvName.setText(mActivityEntity.getName());
        tvDistance.setText(
                EtropedUnitsUtils.formatDistance(
                        mActivity,
                        (long) mActivityEntity.getDistance()
                )
        );
        tvElapsed.setText(
                EtropedDateTimeUtils.formatTimeOnLive(
                        mActivity,
                        mActivityEntity.getTime()
                )
        );
        tvAvgSpeed.setText(
                EtropedUnitsUtils.formatSpeed(
                        mActivity,
                        mActivityEntity.getDistance(),
                        mActivityEntity.getTime()
                )
        );
        tvAvgSpeedUnit.setText(speedUnit);
        tvMaxSpeed.setText(
                EtropedUnitsUtils.formatSpeed(
                        mActivity,
                        (float) maxSpeed
                )
        );
        tvMaxSpeedUnit.setText(speedUnit);
        tvElevationGain.setText(Integer.toString((int) elevationGain));
        tvElevationGainUnit.setText(EtropedUnitsUtils.altitudeUnit(mActivity));

        return mRootView;
    }

    /**
     * This method build the information running view.
     *
     * @param inflater
     * @param container
     * @return
     */
    private View buildRunningView(LayoutInflater inflater, ViewGroup container, int sport) {

        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_activity_details_info_running,
                container, false);

        TextView tvName = (TextView) mRootView.findViewById(R.id.tv_name);
        TextView tvDistance = (TextView) mRootView.findViewById(R.id.tv_distance);
        TextView tvElapsed = (TextView) mRootView.findViewById(R.id.tv_elapsed_time);
        TextView tvPace = (TextView) mRootView.findViewById(R.id.tv_pace);
        TextView tvPaceUnit = (TextView) mRootView.findViewById(R.id.tv_pace_unit);
        TextView tvElevationGain = (TextView) mRootView.findViewById(R.id.tv_elevation_gain);
        TextView tvElevationGainUnit = (TextView) mRootView
                .findViewById(R.id.tv_elevation_gain_unit);
        TextView tvElevationLost = (TextView) mRootView.findViewById(R.id.tv_elevation_lost);
        TextView tvElevationLostUnit = (TextView) mRootView
                .findViewById(R.id.tv_elevation_lost_unit);
        ImageView ivSportIcon = (ImageView) mRootView.findViewById(R.id.iv_sport_icon);

        double elevationGain = mViewModel.getElevationGain();
        double elevationLost = mViewModel.getElevationLost();
        String elevationUnit = EtropedUnitsUtils.altitudeUnit(mActivity);

        tvName.setText(mActivityEntity.getName());
        tvDistance.setText(
                EtropedUnitsUtils.formatDistance(
                        mActivity,
                        (long) mActivityEntity.getDistance()
                )
        );
        tvElapsed.setText(
                EtropedDateTimeUtils.formatTimeOnLive(
                        mActivity,
                        mActivityEntity.getTime()
                )
        );
        tvPace.setText(
                EtropedUnitsUtils.formatPace(
                        getActivity(),
                        mActivityEntity.getTime(),
                        (float) mActivityEntity.getDistance()
                )
        );
        tvPaceUnit.setText(EtropedUnitsUtils.paceUnit(mActivity));
        tvElevationGain.setText(Integer.toString((int) elevationGain));
        tvElevationGainUnit.setText(elevationUnit);
        tvElevationLost.setText(Integer.toString((int) elevationLost));
        tvElevationLostUnit.setText(elevationUnit);

        if (sport == EtropedContract.SPORT_WALKING) {
            ivSportIcon.setImageResource(R.drawable.ic_directions_walk_black_48dp);
        }
        else {
            ivSportIcon.setImageResource(R.drawable.ic_directions_run_black_48dp);
        }

        return mRootView;
    }

    /*
    public BoundingBox computeArea(List<GeoPoint> points) {
        double nord = 0, sud = 0, ovest = 0, est = 0;

        for (int i = 0; i < points.size(); i++) {
            if (points.get(i) == null) continue;

            double lat = points.get(i).getLatitude();
            double lon = points.get(i).getLongitude();

            if ((i == 0) || (lat > nord)) nord = lat;
            if ((i == 0) || (lat < sud)) sud = lat;
            if ((i == 0) || (lon < ovest)) ovest = lon;
            if ((i == 0) || (lon > est)) est = lon;

        }

        return new BoundingBox(nord, est, sud, ovest);
    }
    */

    public void zoomToBounds(final BoundingBox box) {
        if (map.getHeight() > 0) {
            map.zoomToBoundingBox(box, true);

        } else {
            ViewTreeObserver vto = map.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    map.zoomToBoundingBox(box, true);
                    ViewTreeObserver vto2 = map.getViewTreeObserver();
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
