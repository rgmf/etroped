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
import android.util.Log;

import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;

import java.util.LinkedList;
import java.util.List;

import es.rgmf.etroped.data.EtropedContract;
import es.rgmf.etroped.data.entity.ActivityEntity;
import es.rgmf.etroped.data.entity.LapEntity;
import es.rgmf.etroped.data.entity.LocationEntity;
import es.rgmf.etroped.workout.ElevationTask;
import es.rgmf.etroped.workout.LapByDistanceTask;
import es.rgmf.etroped.util.EtropedUnitsUtils;

/**
 * Is a ViewModel class. I extend from AndroidViewModel instance of ViewModel because
 * AndroidViewModel include an Application reference to use system services.
 *
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class ActivityEntityViewModel extends AndroidViewModel {
    private static final String TAG = ActivityEntity.class.getSimpleName();

    // Android context.
    Context mContext;
    // Here is save the ActivityEntity data.
    private MutableLiveData<ActivityEntity> mActivityEntity;
    // This LiveData is used to notify out that data is loaded.
    private MutableLiveData<Boolean> mLoadedData;
    // Max speed value.
    private double mMaxSpeed;
    // Elevation gain.
    private double mElevationGain;
    // Elevation lost.
    private double mElevationLost;
    // Define a lap to partial stats.
    private LinkedList<LapEntity> mKmPartials;
    // Geopoints in this activity.
    private LinkedList<GeoPoint> mGeoPoints;
    // Bounding box.
    private BoundingBox mBoundingBox;
    // These are datas for charts and graphs.
    private List<Float> mNormalizedSpeed;
    private List<Float> mNormalizedPace;
    private List<Float> mNormalizedDistance;
    private List<Float> mNormalizedAltitude;

    /**
     * AndroidViewModel default constructor.
     *
     * @param application The application.
     */
    public ActivityEntityViewModel(Application application) {
        super(application);

        mContext = application.getApplicationContext();

        mActivityEntity = null;
        mLoadedData = new MutableLiveData<>();

        mMaxSpeed = 0f;

        mElevationGain = 0f;
        mElevationLost = 0f;

        mKmPartials = new LinkedList<>();

        mGeoPoints = new LinkedList<>();

        // Initialize all charts and graphs data and creates a point because if an activity does not
        // have any point may crash application. For example, an activity with 0 seconds.
        mNormalizedSpeed = new LinkedList<>();
        mNormalizedSpeed.add(0f);
        mNormalizedPace = new LinkedList<>();
        mNormalizedPace.add(0f);
        mNormalizedDistance = new LinkedList<>();
        mNormalizedDistance.add(0f);
        mNormalizedAltitude = new LinkedList<>();
        mNormalizedAltitude.add(0f);
    }

    /**
     * This is the method that from outside it must be called to load data in mActivityEntity.
     *
     * @param id The ActivityEntity identify.
     * @return
     */
    public MutableLiveData<Boolean> loading(int id) {
        if (mActivityEntity == null)
            loadActivityEntity(id);

        return mLoadedData;
    }

    public MutableLiveData<Boolean> reloading(int id) {
        loadActivityEntity(id);
        return mLoadedData;
    }

    /**
     * Return the ActivityEntity that is inside the LiveData loaded. To get data loaded it must be
     * called loading method before and observe when data is loaded.
     *
     * @return The ActivityEntity object or null if LiveData is null.
     */
    public ActivityEntity getActivityEntity() {
        if (mActivityEntity == null) {
            return null;
        }

        return mActivityEntity.getValue();
    }

    /**
     * Loads data in the background.
     *
     * @param id The Activity entity identify.
     */
    private void loadActivityEntity(int id) {
        new LoadActivityEntityAsyncTask().execute(id);
    }

    /**
     * Async task to load ActivityEntity data in background.
     */
    private class LoadActivityEntityAsyncTask extends AsyncTask<Integer, Void, Void> {

        @Override
        protected Void doInBackground(Integer... integers) {
            int id = integers[0];

            // It retrieves the ActivityEntity.
            Cursor cursor = getApplication().getContentResolver().query(
                    EtropedContract.ActivityEntry.CONTENT_URI,
                    null,
                    EtropedContract.ActivityEntry._ID + "=?",
                    new String[]{Integer.toString(id)},
                    null
            );

            // Now that it has the fresh data, it makes it available to outside observers of the
            // mActivityEntity MutableLiveData object.
            mActivityEntity = new MutableLiveData<>();
            if (cursor != null && cursor.moveToFirst() && cursor.getCount() == 1) {

                // Creates the activity entity.
                ActivityEntity activityEntity = new ActivityEntity(cursor);

                // It retrieve all locations of the activity and add them to the activity entity.
                Cursor cursorLocations = getApplication().getContentResolver().query(
                        EtropedContract.LocationEntry.CONTENT_URI,
                        null,
                        EtropedContract.LocationEntry.COLUMN_ACTIVITY + "=?",
                        new String[]{Integer.toString(id)},
                        null
                );


                // The stats.
                if (cursorLocations.moveToFirst()) {

                    // This task calculates step by step the elevation gain and lost.
                    ElevationTask elevationTask = new ElevationTask(mContext);

                    // This task calculates 1km laps step by step.
                    // TODO I have to create a preference with lap distance for running. At this
                    // moment I use EtropedUnitsUtils.baseUnit to get it.
                    LapByDistanceTask lapTask = new LapByDistanceTask(
                            mContext,
                            (int) EtropedUnitsUtils.baseUnit(mContext)
                    );

                    double north = 0, sud = 0, west = 0, est = 0, lat, lon, count = 0;

                    do {
                        LocationEntity locationEntity = new LocationEntity(cursorLocations);
                        activityEntity.addLocation(locationEntity);

                        Log.d(TAG, locationEntity.getAltitude() + " - " + locationEntity.getPressureAltitude());

                        if (locationEntity.getSpeed() > mMaxSpeed)
                            mMaxSpeed = locationEntity.getSpeed();

                        elevationTask.refresh(
                                locationEntity.getAltitude(),
                                locationEntity.getPressureAltitude()
                        );

                        lapTask.newLocation(locationEntity);

                        lat = locationEntity.getLatitude();
                        lon = locationEntity.getLongitude();
                        mGeoPoints.add(new GeoPoint(lat, lon));

                        if ((count == 0) || (lat > north)) north = lat;
                        if ((count == 0) || (lat < sud)) sud = lat;
                        if ((count == 0) || (lon < west)) west = lon;
                        if ((count == 0) || (lon > est)) est = lon;
                        count++;

                    } while (cursorLocations.moveToNext());

                    mElevationGain = elevationTask.getGain();
                    mElevationLost = elevationTask.getLost();

                    mKmPartials = lapTask.getLapList();

                    mBoundingBox = new BoundingBox(north, est, sud, west);

                    // Normalize datas activity for graphs and charts.
                    normalize(activityEntity);
                }

                // Add the activity entity to mutable live data object.
                mActivityEntity.postValue(activityEntity);
            }
            else {
                mActivityEntity.postValue(new ActivityEntity());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            mLoadedData.postValue(true);
        }
    }

    /**
     * In an activity is records a lot of points. This method units several points to get a number
     * of points to show in a graph.
     *
     * @param activityEntity
     */
    private void normalize(ActivityEntity activityEntity) {
        List<LocationEntity> locations = activityEntity.getLocations();
        float avgSpeed = 0;
        float avgPace = 0;
        int total = locations.size();
        int split = howManySplits(total);
        int div = total / split;
        int count = 0;

        mNormalizedSpeed = new LinkedList<>();
        mNormalizedPace = new LinkedList<>();
        mNormalizedDistance = new LinkedList<>();
        mNormalizedAltitude = new LinkedList<>();

        for (int i = 0; i < total; i++) {
            avgSpeed += locations.get(i).getSpeed();
            avgPace += ((locations.get(i).getElapsed() / 1000d) / locations.get(i).getDistance());
            count++;

            if (i == div) {
                avgSpeed = (avgSpeed / (float) count);
                avgPace = (avgPace / (float) count);

                mNormalizedSpeed.add(avgSpeed);
                mNormalizedPace.add(avgPace);
                mNormalizedDistance.add((float) locations.get(i).getDistance());
                mNormalizedAltitude.add((float) locations.get(i).getAltitude());

                div += split;
                avgSpeed = 0;
                avgPace = 0;
                count = 0;
            }
        }
    }

    /**
     * totalPoints are a lot of points to build a graph, so this method return the divisor to be
     * used to reduce the number of points that will be use in a graph.
     *
     * @param totalPoints
     * @return
     */
    private int howManySplits(int totalPoints) {
        if (totalPoints < 60) {
            return totalPoints;
        }
        else if (totalPoints < 1000) {
            return 20;
        }
        else if (totalPoints < 2000) {
            return 50;
        }
        else if (totalPoints < 5000) {
            return 100;
        }
        else {
            return 200;
        }
    }

    public List<Float> getNormalizedSpeed() {
        return mNormalizedSpeed;
    }
    public List<Float> getNormalizedPace() {
        return mNormalizedPace;
    }
    public List<Float> getNormalizedDistance() {
        return mNormalizedDistance;
    }
    public List<Float> getNormalizedAltitude() {
        return mNormalizedAltitude;
    }

    public double getMaxSpeed() {
        return mMaxSpeed;
    }

    public double getElevationGain() {
        return mElevationGain;
    }

    public double getElevationLost() {
        return mElevationLost;
    }

    public LinkedList<LapEntity> getKmPartials() {
        return mKmPartials;
    }

    public LinkedList<GeoPoint> getGeoPoints() {
        return mGeoPoints;
    }

    public BoundingBox getBoundingBox() {
        return mBoundingBox;
    }
}
