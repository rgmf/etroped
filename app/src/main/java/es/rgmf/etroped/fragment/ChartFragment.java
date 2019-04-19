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

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import es.rgmf.etroped.ActivityDetailsActivity;
import es.rgmf.etroped.R;
import es.rgmf.etroped.data.entity.ActivityEntity;
import es.rgmf.etroped.data.entity.LocationEntity;
import es.rgmf.etroped.helper.ChartsHelper;
import es.rgmf.etroped.util.EtropedPreferences;
import es.rgmf.etroped.util.EtropedUnitsUtils;
import es.rgmf.etroped.viewmodel.ActivityEntityViewModel;

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class ChartFragment extends Fragment {

    private static final String TAG = ChartFragment.class.getSimpleName();

    private View mRootView;
    private ActivityDetailsActivity mActivity;
    private ActivityEntityViewModel mViewModel;
    private ActivityEntity mActivityEntity;

    public ChartFragment() {
        // Required empty public constructor
    }

    public static ChartFragment newInstance() {

        ChartFragment fragment = new ChartFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_activity_detail_chart,
                container, false);

        // Get the activity entity.
        mActivity = (ActivityDetailsActivity) getActivity();
        mViewModel = mActivity.getViewModel();
        mActivityEntity = mViewModel.getActivityEntity();

        // The line charts.
        LineChart chartSpeed = (LineChart) mRootView.findViewById(R.id.chart_speed);
        LineChart chartPace = (LineChart) mRootView.findViewById(R.id.chart_pace);
        LineChart chartAltitude = (LineChart) mRootView.findViewById(R.id.chart_altitude);

        // Add data to the line chart.
        List<Entry> entriesSpeed = new ArrayList<>();
        List<Entry> entriesPace = new ArrayList<>();
        List<Entry> entriesAltitude = new ArrayList<>();

        float maxEle = 0, minEle = 0, elevation;
        float maxSpeed = 0, minSpeed = 0, speedAux;
        float maxPace = 0, minPace = 0, paceAux;

        List<Float> speed = mViewModel.getNormalizedSpeed();
        List<Float> pace = mViewModel.getNormalizedPace();
        List<Float> distance = mViewModel.getNormalizedDistance();
        List<Float> altitude = mViewModel.getNormalizedAltitude();
        for (int i = 0; i < distance.size(); i++) {

            entriesSpeed.add(
                    new Entry(
                            distance.get(i),
                            (float) EtropedUnitsUtils.formatSpeedNumber(
                                    getActivity(), (double) speed.get(i))
                    )
            );

            entriesPace.add(
                    new Entry(
                            distance.get(i),
                            (float) EtropedUnitsUtils.formatPaceNumber(
                                    getActivity(), (double) pace.get(i))
                    )
            );

            entriesAltitude.add(
                    new Entry(
                            distance.get(i),
                            (float) EtropedUnitsUtils.formatAltitudeNumber(
                                    getActivity(), (double) altitude.get(i))
                    )
            );

            speedAux = (float) EtropedUnitsUtils.formatSpeedNumber(
                    getActivity(), (double) speed.get(i));
            if (i == 0 || speedAux > maxSpeed) maxSpeed = speedAux;
            if (i == 0 || speedAux < minSpeed) minSpeed = speedAux;

            paceAux = (float) EtropedUnitsUtils.formatPaceNumber(
                    getActivity(), (double) pace.get(i));
            if (i == 0 || paceAux > maxPace) maxPace = paceAux;
            if (i == 0 || paceAux < minPace) minPace = paceAux;

            elevation = (float) EtropedUnitsUtils.formatAltitudeNumber(
                    getActivity(), (double) altitude.get(i));
            if (i == 0 || elevation > maxEle) maxEle = elevation;
            if (i == 0 || elevation < minEle) minEle = elevation;

        }

        // Creates data set and format it.
        LineDataSet dataSetSpeed = new LineDataSet(entriesSpeed, getString(R.string.speed_caption_label));
        LineDataSet dataSetPace = new LineDataSet(entriesPace, getString(R.string.pace_caption_label));
        LineDataSet dataSetAltitude = new LineDataSet(entriesAltitude, getString(R.string.elevation_caption_label));

        // Config the lines data set.
        ChartsHelper.lineDataSetConfig(getActivity(), dataSetSpeed);
        ChartsHelper.lineDataSetConfig(getActivity(), dataSetPace);
        ChartsHelper.lineDataSetConfig(getActivity(), dataSetAltitude);

        // Add data set to the line data.
        LineData lineDataSpeed = new LineData(dataSetSpeed);
        LineData lineDataPace = new LineData(dataSetPace);
        LineData lineDataAltitude = new LineData(dataSetAltitude);

        // Insert line data to the chart.
        chartSpeed.setData(lineDataSpeed);
        chartPace.setData(lineDataPace);
        chartAltitude.setData(lineDataAltitude);

        // Properties of the charts.
        chartSpeed.getDescription().setText("");
        chartPace.getDescription().setText("");
        chartAltitude.getDescription().setText("");

        // Refresh chart.
        chartSpeed.invalidate();
        chartPace.invalidate();
        chartAltitude.invalidate();

        // Configurates charts.
        ChartsHelper.speedChartConfig(chartSpeed, minSpeed, maxSpeed);
        ChartsHelper.paceChartConfig(chartPace, minPace, maxPace);
        ChartsHelper.altitudeChartConfig(chartAltitude, minEle, maxEle);

        return mRootView;
    }
}
