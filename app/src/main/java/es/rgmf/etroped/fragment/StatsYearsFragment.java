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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;

import es.rgmf.etroped.R;
import es.rgmf.etroped.StatsActivity;
import es.rgmf.etroped.adapter.BarChartAdapter;
import es.rgmf.etroped.adapter.viewlist.BarChartViewList;
import es.rgmf.etroped.adapter.viewlist.ViewTypeBarChart;
import es.rgmf.etroped.data.EtropedContract;
import es.rgmf.etroped.util.EtropedDateTimeUtils;
import es.rgmf.etroped.viewmodel.StatsViewModel;

/**
 * This fragment loads a view with year charts representing monthly activities data.
 */
public class StatsYearsFragment extends Fragment {

    private StatsViewModel mViewModel;
    private StatsActivity mActivity;

    private int mYear, mMonth;
    private int mCurrentYear;
    private String[] mMonthsNames;

    private BarChartAdapter mAdapter;
    private RecyclerView mRecyclerView;
    BarChartViewList mViewList;

    private TextView mTvYear;

    public StatsYearsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment StatsYearsFragment.
     */
    public static StatsYearsFragment newInstance() {
        StatsYearsFragment fragment = new StatsYearsFragment();
        fragment.mCurrentYear = Calendar.getInstance().get(Calendar.YEAR);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stats_years, container, false);

        mActivity = (StatsActivity) getActivity();
        mViewModel = mActivity.getViewModel();

        // Recycler view.
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new BarChartAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);

        final Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        if (mYear == mCurrentYear) {
            mMonth = calendar.get(Calendar.MONTH);
        }
        else {
            mMonth = 11;
        }
        mMonthsNames = EtropedDateTimeUtils.getListMonthsNames();

        mTvYear = view.findViewById(R.id.tv_year);
        mTvYear.setText(String.valueOf(mYear));

        mViewList = new BarChartViewList(mActivity);
        setBikeData();
        setRunningData();
        setWalkingData();

        final ImageView ivLeft = view.findViewById(R.id.iv_left);
        final ImageView ivRight = view.findViewById(R.id.iv_right);
        ivRight.setVisibility(View.INVISIBLE);

        // When click on left arrow text view go to previous month.
        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mYear -= 1;

                if (mYear < mCurrentYear)
                    mMonth = 11;
                else
                    mMonth = calendar.get(Calendar.MONTH);

                ivRight.setVisibility(View.VISIBLE);

                mTvYear.setText(String.valueOf(mYear));
                mViewList.clear();
                mViewList = new BarChartViewList(mActivity);
                mAdapter.swapData(mViewList);
                setBikeData();
                setRunningData();
                setWalkingData();
            }
        });

        // When click on right arrow text view go to next month.
        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mYear++;

                if (mYear == mCurrentYear) {
                    ivRight.setVisibility(View.INVISIBLE);
                    mMonth = calendar.get(Calendar.MONTH);
                }
                else {
                    ivRight.setVisibility(View.VISIBLE);
                    mMonth = 11;
                }

                mTvYear.setText(String.valueOf(mYear));
                mViewList.clear();
                mViewList = new BarChartViewList(mActivity);
                mAdapter.swapData(mViewList);
                setBikeData();
                setRunningData();
                setWalkingData();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setBikeData() {
        int sportId = EtropedContract.SPORT_BIKE;
        String desc = mActivity.getString(R.string.distance);
        String[] labels = new String[mMonth + 1];
        Float[] values = new Float[mMonth + 1];
        Float[] averages = new Float[mMonth +1];
        float acc = 0f;
        float dist;
        for (int i = 0; i < mMonth + 1; i++) {
            dist = (float) mViewModel.getTotalDistance(sportId, String.valueOf(mYear), String.valueOf(i)) / 1000f;
            values[i] = dist;
            acc += dist;
            averages[i] = acc / (i + 1);
            labels[i] = mMonthsNames[i];
        }
        ViewTypeBarChart viewTypeBarChart = ViewTypeBarChart.newInstance(
                sportId,
                desc,
                labels,
                values,
                averages,
                ContextCompat.getColor(getContext(), R.color.color_bike_chart)
        );

        mViewList.add(viewTypeBarChart);
        mAdapter.swapData(mViewList);
    }

    private void setRunningData() {
        int sportId = EtropedContract.SPORT_RUNNING;
        String desc = mActivity.getString(R.string.distance);
        String[] labels = new String[mMonth + 1];
        Float[] values = new Float[mMonth + 1];
        Float[] averages = new Float[mMonth +1];
        float acc = 0f;
        float dist;
        for (int i = 0; i < mMonth + 1; i++) {
            dist = (float) mViewModel.getTotalDistance(sportId, String.valueOf(mYear), String.valueOf(i)) / 1000f;
            values[i] = dist;
            acc += dist;
            averages[i] = acc / (i + 1);
            labels[i] = mMonthsNames[i];
        }
        ViewTypeBarChart viewTypeBarChart = ViewTypeBarChart.newInstance(
                sportId,
                desc,
                labels,
                values,
                averages,
                ContextCompat.getColor(getContext(), R.color.color_running_chart)
        );

        mViewList.add(viewTypeBarChart);
        mAdapter.swapData(mViewList);
    }

    private void setWalkingData() {
        int sportId = EtropedContract.SPORT_WALKING;
        String desc = mActivity.getString(R.string.distance);
        String[] labels = new String[mMonth + 1];
        Float[] values = new Float[mMonth + 1];
        Float[] averages = new Float[mMonth +1];
        float acc = 0f;
        float dist;
        for (int i = 0; i < mMonth + 1; i++) {
            dist = (float) mViewModel.getTotalDistance(sportId, String.valueOf(mYear), String.valueOf(i)) / 1000f;
            values[i] = dist;
            acc += dist;
            averages[i] = acc / (i + 1);
            labels[i] = mMonthsNames[i];
        }
        ViewTypeBarChart viewTypeBarChart = ViewTypeBarChart.newInstance(
                sportId,
                desc,
                labels,
                values,
                averages,
                ContextCompat.getColor(getContext(), R.color.color_walking_chart)
        );

        mViewList.add(viewTypeBarChart);
        mAdapter.swapData(mViewList);
    }
}
