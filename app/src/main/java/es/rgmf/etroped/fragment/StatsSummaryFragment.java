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

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import es.rgmf.etroped.R;
import es.rgmf.etroped.StatsActivity;
import es.rgmf.etroped.data.EtropedContract;
import es.rgmf.etroped.util.EtropedDateTimeUtils;
import es.rgmf.etroped.util.EtropedNumberUtils;
import es.rgmf.etroped.util.EtropedUnitsUtils;
import es.rgmf.etroped.viewmodel.StatsViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StatsSummaryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StatsSummaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatsSummaryFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private StatsActivity mActivity;
    private StatsViewModel mViewModel;

    public StatsSummaryFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static StatsSummaryFragment newInstance() {
        StatsSummaryFragment fragment = new StatsSummaryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // The view to return.
        View view;

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_stats_summary, container, false);

        // The view model.
        mActivity = (StatsActivity) getActivity();
        mViewModel = mActivity.getViewModel();

        // Set views.
        // Bike...
        ((TextView) view.findViewById(R.id.tv_bike_workouts)).setText(
                EtropedUnitsUtils.formatWorkouts(
                        mActivity,
                        mViewModel.getTotalWorkouts(EtropedContract.SPORT_BIKE)
                )
        );

        ((TextView) view.findViewById(R.id.tv_bike_distance)).setText(
                EtropedUnitsUtils.formatDistance(
                        mActivity,
                        (long) mViewModel.getTotalDistance(EtropedContract.SPORT_BIKE)
                )
        );

        ((TextView) view.findViewById(R.id.tv_bike_time)).setText(
                EtropedDateTimeUtils.formatTime(
                        mActivity,
                        mViewModel.getTotalTime(EtropedContract.SPORT_BIKE),
                        EtropedContract.SPORT_RUNNING
                )
        );

        ((TextView) view.findViewById(R.id.tv_bike_max_altitude)).setText(
                EtropedUnitsUtils.formatAltitude(
                        mActivity,
                        (float) mViewModel.getMaxAltitude(EtropedContract.SPORT_BIKE)
                )
        );

        // Running...
        ((TextView) view.findViewById(R.id.tv_running_workouts)).setText(
                EtropedUnitsUtils.formatWorkouts(
                        mActivity,
                        mViewModel.getTotalWorkouts(EtropedContract.SPORT_RUNNING)
                )
        );

        ((TextView) view.findViewById(R.id.tv_running_distance)).setText(
                EtropedUnitsUtils.formatDistance(
                        mActivity,
                        (long) mViewModel.getTotalDistance(EtropedContract.SPORT_RUNNING)
                )
        );

        ((TextView) view.findViewById(R.id.tv_running_time)).setText(
                EtropedDateTimeUtils.formatTime(
                        mActivity,
                        mViewModel.getTotalTime(EtropedContract.SPORT_RUNNING),
                        EtropedContract.SPORT_RUNNING
                )
        );

        ((TextView) view.findViewById(R.id.tv_running_max_altitude)).setText(
                EtropedUnitsUtils.formatAltitude(
                        mActivity,
                        (float) mViewModel.getMaxAltitude(EtropedContract.SPORT_RUNNING)
                )
        );

        // Walking...
        ((TextView) view.findViewById(R.id.tv_walking_workouts)).setText(
                EtropedUnitsUtils.formatWorkouts(
                        mActivity,
                        mViewModel.getTotalWorkouts(EtropedContract.SPORT_WALKING)
                )
        );

        ((TextView) view.findViewById(R.id.tv_walking_distance)).setText(
                EtropedUnitsUtils.formatDistance(
                        mActivity,
                        (long) mViewModel.getTotalDistance(EtropedContract.SPORT_WALKING)
                )
        );

        ((TextView) view.findViewById(R.id.tv_walking_time)).setText(
                EtropedDateTimeUtils.formatTime(
                        mActivity,
                        mViewModel.getTotalTime(EtropedContract.SPORT_WALKING),
                        EtropedContract.SPORT_WALKING
                )
        );

        ((TextView) view.findViewById(R.id.tv_walking_max_altitude)).setText(
                EtropedUnitsUtils.formatAltitude(
                        mActivity,
                        (float) mViewModel.getMaxAltitude(EtropedContract.SPORT_WALKING)
                )
        );

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        */
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
