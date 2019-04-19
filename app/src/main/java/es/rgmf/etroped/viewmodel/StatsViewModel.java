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

package es.rgmf.etroped.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.rgmf.etroped.data.EtropedContract;
import es.rgmf.etroped.data.entity.ActivityEntity;
import es.rgmf.etroped.data.entity.LocationEntity;
import es.rgmf.etroped.util.EtropedPreferences;

/**
 * Is a ViewModel class. I extend from AndroidViewModel instance of ViewModel because
 * AndroidViewModel include an Application reference to use system services.
 */
public class StatsViewModel extends AndroidViewModel {
    private static final String TAG = StatsViewModel.class.getSimpleName();

    // Android context.
    private Context mContext;
    // Here it is save the stats information.
    // For key of this map it use this a string created through this method:
    // <year>-<month>
    private HashMap<Integer, HashMap<String, Stats>> mStats;
    // This LiveData is used to notify out that data is loaded.
    private MutableLiveData<Boolean> mLoadedData;

    // When first activity and lasta activity was.
    private long mFirstDate;
    private long mLastDate;

    public StatsViewModel(Application application) {
        super(application);

        mContext = application.getApplicationContext();

        mStats = null;
        mLoadedData = new MutableLiveData<>();

        mFirstDate = Long.MIN_VALUE;
        mLastDate = Long.MAX_VALUE;
    }

    /**
     * This is the method that from outside it must be called to load data in mActivityEntity.
     *
     * @return
     */
    public MutableLiveData<Boolean> loading(boolean reload) {
        if (mStats == null)
            loadStats();
        else if (reload)
            loadStats();

        return mLoadedData;
    }

    public MutableLiveData<Boolean> reloading() {
        mLoadedData.setValue(false);
        loadStats();
        return mLoadedData;
    }

    private void loadStats() {
        new LoadStatsAsyncTask().execute();
    }

    private class LoadStatsAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Cursor cursor = getApplication().getContentResolver().query(
                    EtropedContract.ActivityEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                mStats = new HashMap<>();

                do {
                    ActivityEntity activityEntity = new ActivityEntity(cursor);
                    long startTime = activityEntity.getStartTime();

                    if (startTime < mFirstDate)
                        mFirstDate = startTime;

                    if (startTime > mLastDate)
                        mLastDate = startTime;

                    Calendar calForYear = Calendar.getInstance();
                    calForYear.setTimeInMillis(startTime);
                    String year = String.valueOf(calForYear.get(Calendar.YEAR));

                    Calendar calForMonth = Calendar.getInstance();
                    calForMonth.setTimeInMillis(startTime);
                    String month = String.valueOf(calForMonth.get(Calendar.MONTH));

                    int sportId = activityEntity.getFkSport();

                    String key = year + "-" + month;
                    Stats stats;
                    HashMap<String, Stats> map;
                    if (mStats.containsKey(sportId)) {
                        map = mStats.get(sportId);
                        if (map.containsKey(key)) {
                            stats = map.get(key);
                        }
                        else {
                            stats = new Stats();
                            map.put(key, stats);
                        }
                    }
                    else {
                        stats = new Stats();
                        map = new HashMap<>();
                        map.put(key, stats);
                        mStats.put(sportId, map);
                    }

                    stats.update(activityEntity);

                } while (cursor.moveToNext());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mLoadedData.postValue(true);
        }
    }

    public long getFirstDate() {
        return mFirstDate;
    }

    public long getLastDate() {
        return mLastDate;
    }

    public int getTotalWorkouts(int sportId) {
        if (mStats != null && mStats.containsKey(sportId)) {
            HashMap<String, Stats> map = mStats.get(sportId);
            int total = 0;

            for (Map.Entry mapItem : map.entrySet()) {
                total += ((Stats) mapItem.getValue()).getTotalWorkouts();
            }

            return total;
        }

        return 0;
    }

    public int getTotalWorkouts(int sportId, String year) {
        if (mStats != null && mStats.containsKey(sportId)) {
            HashMap<String, Stats> map = mStats.get(sportId);
            int total = 0;

            for (Map.Entry mapItem : map.entrySet()) {
                if (((String) mapItem.getKey()).startsWith(year + "-")) {
                    total += ((Stats) mapItem.getValue()).getTotalWorkouts();
                }
            }

            return total;
        }

        return 0;
    }

    public int getTotalWorkouts(int sportId, String year, String month) {
        if (mStats != null && mStats.containsKey(sportId)) {
            HashMap<String, Stats> map = mStats.get(sportId);
            return map.get(year + "-" + month).getTotalWorkouts();
        }

        return 0;
    }

    public double getTotalDistance(int sportId) {
        if (mStats != null && mStats.containsKey(sportId)) {
            HashMap<String, Stats> map = mStats.get(sportId);
            double totalDistance = 0d;

            for (Map.Entry mapItem : map.entrySet()) {
                totalDistance += ((Stats) mapItem.getValue()).getTotalDistance();
            }

            return totalDistance;
        }

        return 0d;
    }

    public double getTotalDistance(int sportId, String year) {
        if (mStats != null && mStats.containsKey(sportId)) {
            HashMap<String, Stats> map = mStats.get(sportId);
            double totalDistance = 0d;

            for (Map.Entry mapItem : map.entrySet()) {
                if (((String) mapItem.getKey()).startsWith(year + "-")) {
                    totalDistance += ((Stats) mapItem.getValue()).getTotalDistance();
                }
            }

            return totalDistance;
        }

        return 0d;
    }

    public double getTotalDistance(int sportId, String year, String month) {
        if (mStats != null && mStats.containsKey(sportId)) {
            HashMap<String, Stats> map = mStats.get(sportId);
            if (map.containsKey(year + "-" + month)) {
                return map.get(year + "-" + month).getTotalDistance();
            }
        }

        return 0d;
    }

    public long getTotalTime(int sportId) {
        if (mStats != null && mStats.containsKey(sportId)) {
            HashMap<String, Stats> map = mStats.get(sportId);
            long totalTime = 0L;

            for (Map.Entry mapItem : map.entrySet()) {
                totalTime += ((Stats) mapItem.getValue()).getTotalTime();
            }

            return totalTime;
        }

        return 0L;
    }

    public long getTotalTime(int sportId, String year) {
        if (mStats != null && mStats.containsKey(sportId)) {
            HashMap<String, Stats> map = mStats.get(sportId);
            long totalTime = 0L;

            for (Map.Entry mapItem : map.entrySet()) {
                if (((String) mapItem.getKey()).startsWith(year + "-")) {
                    totalTime += ((Stats) mapItem.getValue()).getTotalTime();
                }
            }

            return totalTime;
        }

        return 0L;
    }

    public long getTotalTime(int sportId, String year, String month) {
        if (mStats != null && mStats.containsKey(sportId)) {
            HashMap<String, Stats> map = mStats.get(sportId);
            if (map.containsKey(year + "-" + month)) {
                return map.get(year + "-" + month).getTotalTime();
            }
        }

        return 0L;
    }

    public double getMaxAltitude(int sportId) {
        double res = 0d;

        if (mStats != null && mStats.containsKey(sportId)) {
            HashMap<String, Stats> map = mStats.get(sportId);
            double maxAlt;
            for (Map.Entry mapItem : map.entrySet()) {
                maxAlt = ((Stats) mapItem.getValue()).getMaxAltitude();
                if (maxAlt > res) {
                    res = maxAlt;
                }
            }
        }

        return res;
    }

    public double getMaxAltitude(int sportId, String year) {
        double res = 0d;

        if (mStats != null && mStats.containsKey(sportId)) {
            HashMap<String, Stats> map = mStats.get(sportId);
            double maxAlt;
            for (Map.Entry mapItem : map.entrySet()) {
                if (((String) mapItem.getKey()).startsWith(year + "-")) {
                    maxAlt = ((Stats) mapItem.getValue()).getMaxAltitude();
                    if (maxAlt > res) {
                        res = maxAlt;
                    }
                }
            }
        }

        return res;
    }

    public double getMaxAltitude(int sportId, String year, String month) {
        if (mStats != null && mStats.containsKey(sportId)) {
            HashMap<String, Stats> map = mStats.get(sportId);
            if (map.containsKey(year + "-" + month)) {
                return map.get(year + "-" + month).getMaxAltitude();
            }
        }

        return 0d;
    }

    /**
     * Inner class for Stats.
     */
    private class Stats {
        private int mTotalWorkouts;
        private double mTotalDistance;
        private long mTotalTime;
        private double mTotalGain;
        private double mMaxAltitude;

        public Stats() {
            mTotalWorkouts = 0;
            mTotalDistance = 0d;
            mTotalTime = 0L;
            mTotalGain = 0d;
            mMaxAltitude = 0d;
        }

        public int getTotalWorkouts() {
            return mTotalWorkouts;
        }

        public double getTotalDistance() {
            return mTotalDistance;
        }

        public long getTotalTime() {
            return mTotalTime;
        }

        public double getTotalGain() {
            return mTotalGain;
        }

        public double getMaxAltitude() {
            return mMaxAltitude;
        }

        public void update(ActivityEntity activityEntity) {
            mTotalWorkouts++;
            mTotalDistance += activityEntity.getDistance();
            mTotalTime += activityEntity.getTime();

            if (activityEntity.getLocations().isEmpty()) {
                updateAltitude(activityEntity);
            }
        }

        private void updateAltitude(ActivityEntity activityEntity) {
            Cursor cursor = getApplication().getContentResolver().query(
                    EtropedContract.LocationEntry.CONTENT_URI,
                    null,
                    EtropedContract.LocationEntry.COLUMN_ACTIVITY + "=?",
                    new String[]{Integer.toString(activityEntity.getId())},
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    LocationEntity locationEntity = new LocationEntity(cursor);

                    if (mMaxAltitude < locationEntity.getAltitude()) {
                        mMaxAltitude = locationEntity.getAltitude();
                    }
                } while (cursor.moveToNext());
            }
        }
    }
}
