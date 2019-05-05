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
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import es.rgmf.etroped.ActivityDetailsActivity;
import es.rgmf.etroped.R;
import es.rgmf.etroped.StatsActivity;
import es.rgmf.etroped.adapter.ActivitiesAdapter;
import es.rgmf.etroped.adapter.viewlist.ActivityViewList;
import es.rgmf.etroped.adapter.viewlist.ViewTypeActivity;
import es.rgmf.etroped.charts.XAxisValueFormatter;
import es.rgmf.etroped.data.EtropedContract;
import es.rgmf.etroped.data.entity.ActivityEntity;
import es.rgmf.etroped.dialogs.MonthYearPickerDialog;
import es.rgmf.etroped.dialogs.MonthYearPickerDialogFragment;
import es.rgmf.etroped.util.EtropedDateTimeUtils;
import es.rgmf.etroped.util.EtropedPreferences;
import es.rgmf.etroped.viewmodel.StatsViewModel;

/**
 * This fragment load a view with activities in a selected year/month with chart included.
 */
public class StatsMonthsFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<ActivityViewList>,
        ActivitiesAdapter.ListItemClickListener{

    private StatsViewModel mViewModel;
    private StatsActivity mActivity;

    private int mYear, mMonth; // month: from 0 (January) to 11 (December) like in Calendar class.
    private int mCurrentYear, mCurrentMonth;
    private TextView mTvMonthYear;
    private BarChart mBcDistanceSports;

    private static final int ACTIVITIES_LOADER_ID = 10;

    private ActivitiesAdapter mAdapter;
    RecyclerView mRecyclerView;

    public StatsMonthsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment StatsMonthsFragment.
     */
    public static StatsMonthsFragment newInstance() {
        StatsMonthsFragment fragment = new StatsMonthsFragment();
        fragment.mCurrentYear = Calendar.getInstance().get(Calendar.YEAR);
        fragment.mCurrentMonth = Calendar.getInstance().get(Calendar.MONTH);
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
        final View view = inflater.inflate(R.layout.fragment_stats_months, container, false);

        mActivity = (StatsActivity) getActivity();
        mViewModel = mActivity.getViewModel();

        Calendar calendar = Calendar.getInstance();
        this.mYear = calendar.get(Calendar.YEAR);
        this.mMonth = calendar.get(Calendar.MONTH);

        mTvMonthYear = view.findViewById(R.id.tv_year);
        updateCaption();

        final ImageView ivLeft = view.findViewById(R.id.iv_left);
        final ImageView ivRight = view.findViewById(R.id.iv_right);
        ivRight.setVisibility(View.INVISIBLE);

        setDistanceChart(view);

        // When click on month/year text view show a dialog to select month/year.
        mTvMonthYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MonthYearPickerDialogFragment dialogFragment =
                        MonthYearPickerDialogFragment.getInstance(mYear, mMonth);
                dialogFragment.show(getFragmentManager(), null);

                dialogFragment.setOnDateSetListener(new MonthYearPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(int year, int monthOfYear) {
                        if (year > mCurrentYear || (year == mCurrentYear && monthOfYear > mCurrentMonth)) {
                            Toast.makeText(mActivity, getString(R.string.toast_date_invalid), Toast.LENGTH_LONG).show();
                        }
                        else {
                            mYear = year;
                            mMonth = monthOfYear;
                            if (mMonth == mCurrentMonth && mYear >= mCurrentYear)
                                ivRight.setVisibility(View.INVISIBLE);
                            else
                                ivRight.setVisibility(View.VISIBLE);
                            updateCaption();
                            setDistanceChartData();
                            getLoaderManager().restartLoader(ACTIVITIES_LOADER_ID, null, StatsMonthsFragment.this);
                        }
                    }
                });
            }
        });

        // When click on left arrow text view go to previous month.
        ivLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMonth == 0) {
                    mMonth = 11;
                    mYear -= 1;
                }
                else {
                    mMonth--;
                }

                if (mMonth == mCurrentMonth && mYear >= mCurrentYear)
                    ivRight.setVisibility(View.INVISIBLE);
                else
                    ivRight.setVisibility(View.VISIBLE);

                updateCaption();
                setDistanceChartData();

                getLoaderManager().restartLoader(ACTIVITIES_LOADER_ID, null, StatsMonthsFragment.this);
            }
        });

        // When click on right arrow text view go to next month.
        ivRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMonth == 11) {
                    mMonth = 0;
                    mYear++;
                }
                else {
                    mMonth++;
                }


                if (mMonth == mCurrentMonth && mYear >= mCurrentYear)
                    ivRight.setVisibility(View.INVISIBLE);
                else
                    ivRight.setVisibility(View.VISIBLE);

                updateCaption();
                setDistanceChartData();

                getLoaderManager().restartLoader(ACTIVITIES_LOADER_ID, null, StatsMonthsFragment.this);
            }
        });

        // Recycler view with activities.
        mRecyclerView = view.findViewById(R.id.recycler_view_activities);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ActivitiesAdapter(getContext(), this);
        mRecyclerView.setAdapter(mAdapter);

        /**
         * Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                // WeekSummaryViewHolder cannot be deleted.
                if (viewHolder instanceof ActivitiesAdapter.WeekSummaryViewHolder) return 0;
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {

                // Builds an alert to ask the user if he/she is sure with remove action.
                AlertDialog.Builder yesOrNotAlert = new AlertDialog.Builder(getContext());
                yesOrNotAlert.setMessage(getString(R.string.alert_ask_for_remove_activity));

                // No removes.
                yesOrNotAlert.setNegativeButton(getString(R.string.button_cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                getLoaderManager().restartLoader(
                                        ACTIVITIES_LOADER_ID, null, StatsMonthsFragment.this);
                                dialog.cancel();
                            }
                        });

                // Yes removes.
                yesOrNotAlert.setPositiveButton(getString(R.string.button_ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                int id = (int) viewHolder.itemView.getTag();
                                String stringId = Integer.toString(id);
                                Uri uri = EtropedContract.ActivityEntry.CONTENT_URI
                                        .buildUpon().appendPath(stringId).build();

                                getContext().getContentResolver().delete(uri, null, null);

                                // Load the view model data for refresh the data.
                                mActivity.loadViewModelData(true,StatsMonthsFragment.this);
                                setDistanceChart(view);

                                // Load the list of activities.
                                getLoaderManager().restartLoader(
                                        ACTIVITIES_LOADER_ID, null, StatsMonthsFragment.this);

                                if (id == EtropedPreferences.getRecordingActivityId(getContext())) {
                                    EtropedPreferences.removeRecordingActivityId(getContext());
                                }
                            }
                        });
                yesOrNotAlert.show();
            }
        }).attachToRecyclerView(mRecyclerView);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(ACTIVITIES_LOADER_ID, null, this);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void updateCaption() {
        mTvMonthYear.setText(EtropedDateTimeUtils.getMonthName(mActivity, mMonth) + " - " + mYear);
    }

    private void setDistanceChart(View view) {
        mBcDistanceSports = view.findViewById(R.id.bc_distance_sports);

        mBcDistanceSports.setDrawBarShadow(false);

        // Description.
        Description description = new Description();
        description.setText(EtropedPreferences.getDescriptionDistance(mActivity));
        mBcDistanceSports.setDescription(description);

        // No legend.
        mBcDistanceSports.getLegend().setEnabled(false);

        // Disable zoom.
        mBcDistanceSports.setPinchZoom(false);
        mBcDistanceSports.setScaleEnabled(false);

        // Text above bar instance of on bar (with false value is on bar).
        mBcDistanceSports.setDrawValueAboveBar(true);

        // Display the axis on the left (contains the sports labels).
        XAxis xAxis = mBcDistanceSports.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setEnabled(true);
        xAxis.setDrawAxisLine(false);

        // Left axis.
        YAxis yLeft = mBcDistanceSports.getAxisLeft();
        yLeft.setEnabled(false);
        yLeft.setAxisMinimum(0f);

        // Set label count to 3 as we are displaying 3 sports.
        xAxis.setLabelCount(3);

        // Add the labels to be added on the vertical axis.
        String[] values = {
                getString(R.string.sport_bike),
                getString(R.string.sport_running),
                getString(R.string.sport_walking)
        };
        xAxis.setValueFormatter(new XAxisValueFormatter(values));

        // Right axis.
        YAxis yRight = mBcDistanceSports.getAxisRight();
        yRight.setDrawAxisLine(true);
        yRight.setDrawGridLines(false);
        yRight.setEnabled(false);
        yRight.setAxisMinimum(0f);

        mBcDistanceSports.setFitBars(true);

        // Set bar entries and add necessary formatting.
        setDistanceChartData();
    }

    private void setDistanceChartData() {
        List<BarEntry> entries = new ArrayList<>();

        double distBike = mViewModel.getTotalDistance(
                                EtropedContract.SPORT_BIKE,
                                String.valueOf(mYear),
                                String.valueOf(mMonth)) / 1000d;
        double distRun = mViewModel.getTotalDistance(
                                EtropedContract.SPORT_RUNNING,
                                String.valueOf(mYear),
                                String.valueOf(mMonth)) / 1000d;
        double distWalk = mViewModel.getTotalDistance(
                                EtropedContract.SPORT_WALKING,
                                String.valueOf(mYear),
                                String.valueOf(mMonth)) / 1000d;

        entries.add(new BarEntry(0f, (float) distBike));
        entries.add(new BarEntry(1f, (float) distRun));
        entries.add(new BarEntry(2f, (float) distWalk));

        BarDataSet barDataSet = new BarDataSet(entries, "BarDataSet");

        // Set colors.
        barDataSet.setColors(
                ContextCompat.getColor(mBcDistanceSports.getContext(), R.color.color_bike_chart),
                ContextCompat.getColor(mBcDistanceSports.getContext(), R.color.color_running_chart),
                ContextCompat.getColor(mBcDistanceSports.getContext(), R.color.color_walking_chart));

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.9f); // set custom bar width
        mBcDistanceSports.setData(barData);
        mBcDistanceSports.setFitBars(true); // make the x-axis fit exactly all bars
        mBcDistanceSports.invalidate(); // refresh

        // Set animation to the graph.
        mBcDistanceSports.animateY(2000);
    }

    @Override
    public void onListItemClick(int clickedIdActivity) {
        Intent intent = new Intent(getActivity(), ActivityDetailsActivity.class);
        intent.putExtra(ActivityDetailsActivity.EXTRA_ACTIVITY_ID, clickedIdActivity);
        startActivity(intent);
    }

    @Override
    public Loader<ActivityViewList> onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<ActivityViewList>(getContext()) {

            // Initialize a Cursor, this will hold all the activities data.
            ActivityViewList mActivitiesData = null;

            @Override
            protected void onStartLoading() {
                if (mActivitiesData != null)
                    deliverResult(mActivitiesData);
                else
                    forceLoad();
            }

            @Override
            public ActivityViewList loadInBackground() {
                try {
                    ActivityViewList viewList = new ActivityViewList(getActivity());

                    // Get all activities from first day to last day of mMonth/mYear date.
                    String firstDate = Long.toString(EtropedDateTimeUtils.getFirstDayInMillis(mYear, mMonth));
                    String lastDate = Long.toString(EtropedDateTimeUtils.getLastDayInMillis(mYear, mMonth));
                    Cursor cursor = getContext().getContentResolver().query(
                            EtropedContract.ActivityEntry.CONTENT_URI,
                            null,
                            EtropedContract.ActivityEntry.COLUMN_START_TIME + ">=? AND " + EtropedContract.ActivityEntry.COLUMN_START_TIME + "<=?",
                            new String[]{firstDate, lastDate},
                            EtropedContract.ActivityEntry.COLUMN_START_TIME + " DESC");

                    if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                        do {
                            ActivityEntity activityEntity = new ActivityEntity(cursor);
                            ViewTypeActivity viewTypeActivity = ViewTypeActivity.newInstance(getActivity());
                            viewTypeActivity.setActivity(activityEntity);
                            viewList.add(viewTypeActivity);
                        } while (cursor.moveToNext());
                    }

                    return viewList;
                } catch (Exception e) {
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener.
            @Override
            public void deliverResult(ActivityViewList data) {
                mActivitiesData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ActivityViewList> loader, ActivityViewList data) {
        /*if (data.getCount() == 0)
            mViewEmpty.setVisibility(View.VISIBLE);
        else
            mViewEmpty.setVisibility(View.INVISIBLE);*/

        mAdapter.swapData(data);
    }

    @Override
    public void onLoaderReset(Loader<ActivityViewList> loader) {

    }
}
