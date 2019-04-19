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

package es.rgmf.etroped.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import es.rgmf.etroped.R;
import es.rgmf.etroped.TrackerActivity;
import es.rgmf.etroped.service.GpsStatusService.ILocationManagerForStatus;
import es.rgmf.etroped.util.EtropedPreferences;
import es.rgmf.etroped.workout.Workout;

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class TrackerActivityService extends Service implements
        LocationListener, ILocationManagerForStatus {

    public static final String TAG = TrackerActivityService.class.getSimpleName();

    private static final int NOTIFICATION_ONGOING_ID = 100;

    // TODO Get GPS information every second. It should be configurable on preferences by the user.
    private static final long NOTIFY_INTERVAL = 1000;

    // Flag to know if gps is enabled.
    private boolean mIsGpsEnabled = false;

    // Flag to know if it is waiting for better signal.
    private boolean mWaitingGps = false;

    // Flag to know if activity is active (user clicked on play button). Activities must be
    // inform about this.
    private boolean mPlay = false;

    // Location manager and location object.
    private LocationManager mLocationManager;
    private Location mLocation;

    // The handler and the timer to get location every time fragment.
    private Handler mHandler;
    private Timer mTimer = null;

    // Minimum accuracy to save a point
    private int mMinimumGpsAccuracy;

    // Gps status service object.
    private GpsStatusService mGpsService;

    // The workout currently activity information.
    private Workout mWorkout;

    // The binder used to Android Activities to communicate with this service.
    private IBinder mBinder;

    // List of clients bound to this service.
    private static List<ITrackerActivityClient> sClients;

    /**
     * Default constructor.
     */
    public TrackerActivityService() {

    }

    @Override
    public LocationManager getLocationManager() {
        return mLocationManager;
    }

    /**
     * Class used for the client Binder. Because it knows this service always runs in the same
     * process as its clients, it does not need to deal with IPC.
     */
    public class TrackerActivityServiceBinder extends Binder {

        /**
         * @return This TrackerActivityService instance so clients can call public methods.
         */
        public TrackerActivityService getService() {
            return TrackerActivityService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return mBinder;
    }

    @Override
    public void onCreate() {

        super.onCreate();

        mMinimumGpsAccuracy = Integer.valueOf(
                EtropedPreferences.getDefaultAccuracy(getApplicationContext()));

        ServiceCreateTask task = new ServiceCreateTask();
        task.execute();

        mLocationManager = (LocationManager) getApplicationContext()
                .getSystemService(LOCATION_SERVICE);

        mBinder = new TrackerActivityServiceBinder();

        mGpsService = new GpsStatusService(this);
        try {
            mLocationManager.addNmeaListener(mGpsService);
            mLocationManager.addGpsStatusListener(mGpsService);
        } catch (java.lang.SecurityException e) {
            Log.i(TAG, "fail to adding gps service like nmea/status listener, ignore", e);
        }
    }

    /**
     * This async task class is used to execute code that can delay some seconds to no block the
     * main thread.
     */
    private class ServiceCreateTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {

            // Send all clients that this heavy task has begun.
            sendServiceLoadingToClients();
        }

        /**
         * Get Workout instance.
         *
         * This task can be heavy because:
         * - Sometimes need to recovery all data from database.
         * - If it has barometer then use web service to get information.
         * - Etc.
         *
         * @param voids
         * @return
         */
        @Override
        protected Void doInBackground(Void... voids) {

            mWorkout = Workout.newInstance(getApplicationContext());

            return null;
        }

        /**
         * When Workout instance is ready it can starts to get locations.
         *
         * @param aVoid
         */
        @Override
        protected void onPostExecute(Void aVoid) {

            // Send all clients that service is ready.
            sendServiceReadyToClients();

            // Begin the timer to get locations every fragment of time (NOTIFY_INTERVAL).
            mHandler = new Handler();
            mTimer = new Timer();
            mTimer.schedule(new TimerTaskToGetLocation(), 5, NOTIFY_INTERVAL);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

       return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        removeNotification();

        if (mWorkout != null)
            mWorkout.destroy();

        if (mTimer != null)
            mTimer.cancel();

        mLocationManager.removeNmeaListener(mGpsService);
        mLocationManager.removeGpsStatusListener(mGpsService);
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {}

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) {}

    @Override
    public void onProviderDisabled(String s) {}

    /**
     * A timer task that call getLocation method.
     */
    private class TimerTaskToGetLocation extends TimerTask {
        @Override
        public void run() {

            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    getLocation();
                }
            });
        }
    }

    /**
     * Get location and update data.
     */
    private void getLocation() {

        //Log.e(TAG, "getLocation");

        mIsGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!mIsGpsEnabled || !mPlay) {

            mWorkout.pauseWorkoutNotify();
        }
        else {

            mLocation = null;
            try {
                mLocationManager.requestLocationUpdates(mLocationManager.GPS_PROVIDER,
                        NOTIFY_INTERVAL, 0, this);
            } catch (java.lang.SecurityException ex) {
                Log.i(TAG, "fail to request location update, ignore", ex);
            } catch (IllegalArgumentException ex) {
                Log.d(TAG, "network provider does not exist, " + ex.getMessage());
            }

            if (mLocationManager != null) {
                try {
                    mLocation = mLocationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                } catch (java.lang.SecurityException ex) {
                    Log.i(TAG, "fail to request location update, ignore", ex);
                } catch (IllegalArgumentException ex) {
                    Log.d(TAG, "network provider does not exist, " + ex.getMessage());
                }

                // If there is location and the accuracy is less than mMinimumGpsAccuracy then
                // update and save the point. Otherwise it waits to better gps accuracy or location.
                if (mLocation != null && mLocation.getAccuracy() < mMinimumGpsAccuracy) {

                    //Log.e(TAG,"Good accuracy: " + mLocation.getAccuracy());

                    // Is waiting flag is true then change the status and inform all clients.
                    if (mWaitingGps) {

                        sendWaitingGpsToClients(false);
                        mWaitingGps = false;
                    }

                    // Add extra information in Location object through GpsStatusService.
                    mLocation = mGpsService.addStatusInformation(mLocation);

                    // Update.
                    update(mLocation);
                }
                else {

                    //Log.e(TAG, "Bad accuracy: " + mLocation.getAccuracy());

                    // It has waiting for better gps signal.
                    mWaitingGps = true;
                    mWorkout.pauseWorkoutNotify();
                    sendWaitingGpsToClients(true);
                }
            }
            else {

                //Log.e(TAG, "No Location Manager");

                // It has waiting for better gps signal.
                mWaitingGps = true;
                mWorkout.pauseWorkoutNotify();
                sendWaitingGpsToClients(true);
            }
        }
    }

    /**
     * Updates workout and creates an IntentService to updates database and send a broadcast so
     * activities can update their views location.
     *
     * @param location The new location.
     */
    private void update(Location location) {

        boolean isThereMovement = mWorkout.newLocation(location);

        //Log.e(TAG, "update, is there movement? " + isThereMovement);

        // If there is movement then save location into database.
        if (isThereMovement) {

            // Creates an IntentService to update database with new Location by other thread.
            Intent intentPersistence = new Intent(this, TrackerActivityPersistentService.class);
            intentPersistence.setAction(TrackerActivityPersistentService.ACTION_NEW_LOCATION);
            intentPersistence.putExtra(
                    TrackerActivityPersistentService.EXTRA_SPEED,
                    mWorkout.getSpeed()
            );
            intentPersistence.putExtra(
                    TrackerActivityPersistentService.EXTRA_ELAPSED_TIME,
                    mWorkout.getElapsedTime()
            );
            intentPersistence.putExtra(
                    TrackerActivityPersistentService.EXTRA_DISTANCE,
                    mWorkout.getDistance()
            );
            intentPersistence.putExtra(
                    TrackerActivityPersistentService.EXTRA_ELEVATION,
                    mWorkout.getAltitude()
            );

            intentPersistence.putExtra(
                    TrackerActivityPersistentService.EXTRA_PRESSURE_ELEVATION,
                    mWorkout.getPressureAltitude()
            );
            intentPersistence.putExtra(TrackerActivityPersistentService.EXTRA_LOCATION, location);
            startService(intentPersistence);
        }

        sendWorkoutUpdateToClients(mWorkout);
    }

    private void sendWorkoutUpdateToClients(Workout w) {

        if (sClients != null)
            for (ITrackerActivityClient c : sClients)
                c.onWorkoutUpdate(w);
    }

    private void sendWaitingGpsToClients(boolean waiting) {

        if (sClients != null)
            for (ITrackerActivityClient c : sClients)
                c.onWaitingGps(waiting);
    }

    private void sendServiceLoadingToClients() {

        if (sClients != null)
            for (ITrackerActivityClient c : sClients)
                c.onServiceLoading();
    }

    private void sendServiceReadyToClients() {

        if (sClients != null)
            for (ITrackerActivityClient c : sClients)
                c.onServiceReady();
    }

    public void playNotify() {
        mPlay = true;
        buildNotification();
    }

    public void pauseNotify() {
        mPlay = false;
    }

    public static void addServiceClient(ITrackerActivityClient client) {
        if (sClients == null)
            sClients = new ArrayList<>();

        sClients.add(client);
    }

    public static void removeServiceClient(ITrackerActivityClient client) {

        if (sClients != null) {
            sClients.remove(client);
        }
    }

    public Workout getWorkout() {
        return mWorkout;
    }

    /**
     * Remove the notification.
     */
    private void removeNotification() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ONGOING_ID);
    }

    /**
     * Builds a notification and start service in foreground.
     */
    private void buildNotification() {

        // The id of the channel.
        String CHANNEL_ID = "my_channel_01";
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_notification)
                        .setOnlyAlertOnce(true)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(getString(R.string.gps_service_message));
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, TrackerActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your app to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(TrackerActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // mNotificationId is a unique integer your app uses to identify the
        // notification. For example, to cancel the notification, you can pass its ID
        // number to NotificationManager.cancel().
        mNotificationManager.notify(NOTIFICATION_ONGOING_ID, mBuilder.build());

        final Notification notification = mBuilder.build();

        startForeground(NOTIFICATION_ONGOING_ID, notification);
    }
}
