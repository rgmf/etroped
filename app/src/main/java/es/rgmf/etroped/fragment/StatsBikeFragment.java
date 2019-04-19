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
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import es.rgmf.etroped.ActivityDetailsActivity;
import es.rgmf.etroped.R;
import es.rgmf.etroped.adapter.LapsListAdapter;
import es.rgmf.etroped.data.EtropedContract;
import es.rgmf.etroped.data.entity.ActivityEntity;
import es.rgmf.etroped.data.entity.LapEntity;
import es.rgmf.etroped.util.EtropedDateTimeUtils;
import es.rgmf.etroped.util.EtropedUnitsUtils;
import es.rgmf.etroped.viewmodel.ActivityEntityViewModel;

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
// TODO At this moment the stats that it shows are the same than running one. I do not know what
// to show for bike.
public class StatsBikeFragment extends Fragment {

    private static final String TAG = StatsRunningFragment.class.getSimpleName();

    private View mRootView;
    private ActivityDetailsActivity mActivity;
    private ActivityEntityViewModel mViewModel;
    private ActivityEntity mActivityEntity;

    private RecyclerView mRecyclerView;

    private LapsListAdapter mAdapter;

    public StatsBikeFragment() {
        // Required empty public constructor
    }

    public static StatsBikeFragment newInstance() {
        StatsBikeFragment fragment = new StatsBikeFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // TODO It inflates fragmen_stats_running. When I will think about bike options I will
        // change it.
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_stats_running, container, false);

        // Get the activity entity.
        mActivity = (ActivityDetailsActivity) getActivity();
        mViewModel = mActivity.getViewModel();
        mActivityEntity = mViewModel.getActivityEntity();

        // Set the RecyclerView to its corresponding view
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view_list);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new LapsListAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);

        // Set adapter with list of laps.
        mAdapter.swapLaps(mViewModel.getKmPartials());

        return mRootView;
    }
}
