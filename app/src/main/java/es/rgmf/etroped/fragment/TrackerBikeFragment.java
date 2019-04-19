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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import es.rgmf.etroped.R;
import es.rgmf.etroped.workout.ElevationWorkout;
import es.rgmf.etroped.workout.Workout;
import es.rgmf.etroped.util.EtropedDateTimeUtils;
import es.rgmf.etroped.util.EtropedUnitsUtils;

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class TrackerBikeFragment extends AbstractTrackerFragment {

    public static final String TAG = TrackerBikeFragment.class.getSimpleName();

    public TextView mTime;
    public TextView mSpeed;
    public TextView mSpeedAvg;
    public TextView mDistance;
    public TextView mAltitude;
    public TextView mGain;
    public TextView mLost;
    public TextView mDistanceUnit;

    public TrackerBikeFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tracker_bike, container, false);

        // Get views from layout.
        mTime = (TextView) view.findViewById(R.id.tv_walking_time);
        mSpeed = (TextView) view.findViewById(R.id.tv_speed);
        mSpeedAvg = (TextView) view.findViewById(R.id.tv_avg_speed);
        mDistance = (TextView) view.findViewById(R.id.tv_distance);
        mAltitude = (TextView) view.findViewById(R.id.tv_elevation);
        mGain = (TextView) view.findViewById(R.id.tv_elevation_gain);
        mLost = (TextView) view.findViewById(R.id.tv_elevation_lost);
        mDistanceUnit = (TextView) view.findViewById(R.id.tv_distance_unit);

        // Set units.
        ((TextView) view.findViewById(R.id.tv_speed_unit)).setText(
                EtropedUnitsUtils.speedUnit(getContext()));
        ((TextView) view.findViewById(R.id.tv_elevation_unit)).setText(
                EtropedUnitsUtils.altitudeUnit(getContext()));

        return view;
    }

    @Override
    public void onWorkoutUpdate(@Nullable Workout workout) {

        Log.e(TAG, "onWorkoutUpdate: " + workout + " : " + mContext);

        if (workout == null || mContext == null)
            return;

        mTime.setText(EtropedDateTimeUtils
                .formatTimeOnLive(mContext, (long) workout.getElapsedTime()));

        mSpeed.setText(EtropedUnitsUtils.formatSpeed(mContext, (float) workout.getSpeed()));

        mSpeedAvg.setText(EtropedUnitsUtils.formatSpeed(mContext, (float) workout.getSpeedAvg()));

        mDistance.setText(EtropedUnitsUtils.formatDistance(mContext, (long) workout.getDistance()));

        mDistanceUnit.setText(EtropedUnitsUtils.distanceUnit(
                mContext, (float) workout.getDistance()));

        if (!ElevationWorkout.isNone(workout.getAltitude()))
            mAltitude.setText(EtropedUnitsUtils
                    .formatAltitude(mContext, (float) workout.getAltitude()));
        else
            mAltitude.setText("--");

        mGain.setText(EtropedUnitsUtils.formatAltitude(mContext, (float) workout.getAltitudeGain()));

        mLost.setText(EtropedUnitsUtils.formatAltitude(mContext, (float) workout.getAltitudeLost()));
    }
}
