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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import es.rgmf.etroped.R;
import es.rgmf.etroped.workout.Workout;
import es.rgmf.etroped.util.EtropedDateTimeUtils;
import es.rgmf.etroped.util.EtropedUnitsUtils;

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class TrackerRunningFragment extends AbstractTrackerFragment {

    public static final String TAG = TrackerRunningFragment.class.getSimpleName();

    public TextView mTime;
    public TextView mPace;
    public TextView mSpeed;
    public TextView mDistance;
    public TextView mDistanceUnit;

    //private ScrollView mScrollView;

    public TrackerRunningFragment() {
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
        View view = inflater.inflate(R.layout.fragment_tracker_running, container, false);

        // Get views from layout.
        mTime = (TextView) view.findViewById(R.id.tv_walking_time);
        mPace = (TextView) view.findViewById(R.id.tv_pace);
        mSpeed = (TextView) view.findViewById(R.id.tv_speed);
        mDistance = (TextView) view.findViewById(R.id.tv_distance);
        mDistanceUnit = (TextView) view.findViewById(R.id.tv_distance_unit);

        // Set units.
        ((TextView) view.findViewById(R.id.tv_pace_unit)).setText(
                EtropedUnitsUtils.paceUnit(getContext()));

        return view;
    }

    @Override
    public void onWorkoutUpdate(@Nullable Workout workout) {

        if (workout == null || mContext == null)
            return;

        mTime.setText(EtropedDateTimeUtils
                .formatTimeOnLive(mContext, (long) workout.getElapsedTime()));
        mPace.setText(EtropedUnitsUtils.formatPace(mContext, (float) workout.getPace()));
        mSpeed.setText(EtropedUnitsUtils.formatSpeed(mContext, (float) workout.getSpeed()));
        mDistance.setText(EtropedUnitsUtils.formatDistance(mContext, (long) workout.getDistance()));
        mDistanceUnit.setText(EtropedUnitsUtils.distanceUnit(
                mContext, (float) workout.getDistance()));
    }
}
