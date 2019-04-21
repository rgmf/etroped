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

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Calendar;

import es.rgmf.etroped.ActivityDetailsActivity;
import es.rgmf.etroped.R;
import es.rgmf.etroped.TrackerActivity;
import es.rgmf.etroped.TrackerPebbleActivity;
import es.rgmf.etroped.adapter.ActivitiesAdapter;
import es.rgmf.etroped.adapter.viewlist.ActivityViewList;
import es.rgmf.etroped.adapter.viewlist.ViewTypeActivity;
import es.rgmf.etroped.adapter.viewlist.ViewTypeWeekSummary;
import es.rgmf.etroped.data.EtropedContract;
import es.rgmf.etroped.data.entity.ActivityEntity;
import es.rgmf.etroped.util.EtropedDateTimeUtils;
import es.rgmf.etroped.util.EtropedPreferences;

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class ActivitiesFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<ActivityViewList>,
        SharedPreferences.OnSharedPreferenceChangeListener,
        ActivitiesAdapter.ListItemClickListener {

    public static final String TAG = ActivitiesFragment.class.getSimpleName();

    private static final int ACTIVITIES_LOADER_ID = 0;

    private ActivitiesAdapter mAdapter;
    RecyclerView mRecyclerView;
    View mViewEmpty;

    public ActivitiesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_activities, container, false);

        // Set the RecyclerView to its corresponding view
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_activities);

        // The image to show when not items.
        mViewEmpty = view.findViewById(R.id.v_empty);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the adapter and attach it to the RecyclerView
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
                                            ACTIVITIES_LOADER_ID, null, ActivitiesFragment.this);
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

                                    getLoaderManager().restartLoader(
                                            ACTIVITIES_LOADER_ID, null, ActivitiesFragment.this);

                                    if (id == EtropedPreferences.getRecordingActivityId(getContext())) {
                                        EtropedPreferences.removeRecordingActivityId(getContext());
                                    }
                                }
                            });
                    yesOrNotAlert.show();
            }
        }).attachToRecyclerView(mRecyclerView);

        // The float button.
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (EtropedPreferences.isPebbleChecked(getActivity()))
                    startActivity(new Intent(getActivity(), TrackerPebbleActivity.class));
                else
                    startActivity(new Intent(getActivity(), TrackerActivity.class));
            }
        });

        // TODO Comprobar bien que funciona quitando esta línea de aquí que ahora dejo comentada.
        // Loads all activities by using a background task.
        //getLoaderManager().initLoader(ACTIVITIES_LOADER_ID, null, this);

        // Register the SharedPreferences listener because if preferences changes then information
        // about activities must change too.
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .registerOnSharedPreferenceChangeListener(this);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        getLoaderManager().restartLoader(ACTIVITIES_LOADER_ID, null, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Unregister the SharedPreferences listener.
        PreferenceManager.getDefaultSharedPreferences(getContext())
                .unregisterOnSharedPreferenceChangeListener(this);
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

            // loadInBackground() performs asynchronous loading of data.
            @Override
            public ActivityViewList loadInBackground() {
                try {
                    ActivityViewList viewList = new ActivityViewList(getActivity());

                    // seven days in milliseconds.
                    long sevenDaysMillis = 7L * 24L * 60L * 60L * 1000L;

                    // It shows all activities for last 5 weeks.
                    // ... monday 5 weeks ago.
                    long[] firstDays = EtropedDateTimeUtils.getLastDays(Calendar.MONDAY, 5, EtropedDateTimeUtils.getFirstDayOfThisWeek());
                    long[] lastDays = EtropedDateTimeUtils.getLastDays(Calendar.SUNDAY, 5, EtropedDateTimeUtils.getFirstDayOfThisWeek() + sevenDaysMillis);
                    /*
                    long[] lastDays = new long[5];
                    // ... and sundays 5 weeks ago.
                    for (int i = 0; i < 5; i++) {
                        lastDays[i] = firstDays[i] + (sevenDaysMillis - 1L);
                    }
                    */

                    // Get all activities from 5 weeks ago.
                    Cursor cursor = getContext().getContentResolver().query(
                            EtropedContract.ActivityEntry.CONTENT_URI,
                            null,
                            EtropedContract.ActivityEntry.COLUMN_START_TIME + ">=?",
                            new String[]{String.valueOf(firstDays[4])},
                            EtropedContract.ActivityEntry.COLUMN_START_TIME + " DESC");

                    if (cursor.getCount() > 0 && cursor.moveToFirst()) {

                        int index = 0;

                        // Builds and initializes week stats.
                        ViewTypeWeekSummary weekSummary = ViewTypeWeekSummary.newInstance(getActivity());
                        weekSummary.setStartDate(firstDays[index]);
                        weekSummary.setEndDate(lastDays[index]);
                        weekSummary.setCaption(getString(R.string.this_week));
                        viewList.add(weekSummary);

                        do {

                            ActivityEntity activityEntity = new ActivityEntity(cursor);
                            ViewTypeActivity viewTypeActivity = ViewTypeActivity.newInstance(getActivity());
                            viewTypeActivity.setActivity(activityEntity);

                            if (EtropedDateTimeUtils.getDatePart(activityEntity.getStartTime()) >= firstDays[index] &&
                                    EtropedDateTimeUtils.getDatePart(activityEntity.getEndTime()) <= lastDays[index]) {
                                weekSummary.add(activityEntity);
                            }
                            else {
                                index++;

                                // Builds and initializes week stats.
                                weekSummary = ViewTypeWeekSummary.newInstance(getActivity());
                                weekSummary.setStartDate(firstDays[index]);
                                weekSummary.setEndDate(lastDays[index]);
                                viewList.add(weekSummary);

                                if (EtropedDateTimeUtils.getDatePart(activityEntity.getStartTime()) >= firstDays[index] &&
                                        EtropedDateTimeUtils.getDatePart(activityEntity.getEndTime()) <= lastDays[index]) {
                                    weekSummary.add(activityEntity);
                                }
                            }

                            viewList.add(viewTypeActivity);
                        } while (cursor.moveToNext());
                    }

                    return viewList;
                } catch(Exception e) {
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
        if (data.getCount() == 0)
            mViewEmpty.setVisibility(View.VISIBLE);
        else
            mViewEmpty.setVisibility(View.INVISIBLE);

        mAdapter.swapData(data);
    }

    @Override
    public void onLoaderReset(Loader<ActivityViewList> loader) {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_units_key)))
            // Loads all activities by using a background task.
            getLoaderManager().restartLoader(ACTIVITIES_LOADER_ID, null, this);
    }

    /**
     * This method is necessary when ActivitiesAdapter.ListItemClickListener interface is
     * implemented.
     *
     * Every time a item from a recycler view is clicked this method is called.
     *
     * @param clickedIdActivity The activiy identify.
     */
    @Override
    public void onListItemClick(int clickedIdActivity) {
        Intent intent = new Intent(getActivity(), ActivityDetailsActivity.class);
        intent.putExtra(ActivityDetailsActivity.EXTRA_ACTIVITY_ID, clickedIdActivity);
        startActivity(intent);

    }
}
