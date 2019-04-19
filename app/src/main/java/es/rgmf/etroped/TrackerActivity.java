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

package es.rgmf.etroped;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import es.rgmf.etroped.adapter.SportsIconsListAdapter;
import es.rgmf.etroped.data.ActivityProvider;
import es.rgmf.etroped.data.EtropedContract;
import es.rgmf.etroped.data.entity.ActivityEntity;
import es.rgmf.etroped.fragment.AbstractTrackerFragment;
import es.rgmf.etroped.fragment.TrackerBikeFragment;
import es.rgmf.etroped.fragment.TrackerRunningFragment;
import es.rgmf.etroped.fragment.TrackerSplashFragment;
import es.rgmf.etroped.helper.DialogsHelper;
import es.rgmf.etroped.service.ITrackerActivityClient;
import es.rgmf.etroped.service.TrackerActivityService;
import es.rgmf.etroped.service.TrackerActivityServiceConnection;
import es.rgmf.etroped.util.EtropedPreferences;
import es.rgmf.etroped.workout.Workout;

/**
 * The activity for record sport activities.
 *
 * All UI logic for recording a sport activity is here.
 *
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class TrackerActivity extends AppCompatActivity implements
        ITrackerActivityClient, LoaderManager.LoaderCallbacks<ActivityEntity> {

    private static final String TAG = TrackerActivity.class.getSimpleName();

    private static final String EXTRA_ACTIVITY_ID = "extra_activity_id";

    private static final int ACTIVITY_RECORDING_LOADER_ID = 1;

    private static final int REQUEST_ACCESS_FINE_LOCATION_PERMISSIONS = 1;

    private static final int STATUS_NONE = 1;
    private static final int STATUS_PLAY = 2;
    private static final int STATUS_PAUSE = 3;

    // The service and flag to know if this activity is bound to the service.
    TrackerActivityService mService;
    boolean mBound = false;

    // The selected sport id.
    protected Integer mSelectedSportId;

    // Last status (played, paused, etc).
    private int mLastStatus = STATUS_NONE;

    // The fragment loaded.
    private AbstractTrackerFragment mFragment;

    // The root view.
    private View mRootView;

    // Images buttons.
    private FloatingActionButton mButtonRecord;
    private FloatingActionButton mButtonPlay;
    private FloatingActionButton mButtonPause;
    private ImageView mButtonFinish;
    private ImageView mSport;
    private ImageView mGpsStatus;

    private FrameLayout mProgressBarHolder;

    // Flag to know if location permission is granted.
    private boolean mPermissionGranted = false;

    // Snackbar for waiting gps signal message.
    private Snackbar mWaitingGpsSnackbar = null;

    // The current workout.
    protected Workout mCurrentWorkout = null;

    // The connection.
    private TrackerActivityServiceConnection mConnection = new TrackerActivityServiceConnection(
            this);

    /**
     * This receiver receive providers (gps and network location) changes.
     *
     * If GPS is enabled it shows through a snackbar component information for a while.
     *
     * If GPS is disabled it shows indefinite snackbar with action to enable GPS.
     */
    private BroadcastReceiver mGpsStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()) {

                case LocationManager.PROVIDERS_CHANGED_ACTION:

                    if (isGpsProvierEnabled()) {

                        showGpsEnableSnackbar();

                        // If last status was STATUS_PLAY then the current status is pause so
                        // resume with pause.
                        if (mLastStatus == STATUS_PLAY) {
                            pause();
                        }
                        // If last status was STATUS_PAUSE then the current status is play so
                        // resume with play.
                        else if (mLastStatus == STATUS_PAUSE) {
                            play();
                        }
                        // If last status is STATUS_NONE (neither play and pause button was
                        // clicked) then show record button.
                        else if (mLastStatus == STATUS_NONE) {
                            mButtonRecord.setVisibility(View.VISIBLE);
                        }
                    }
                    else {

                        showGpsDisableSnackbar();

                        mButtonRecord.setVisibility(View.VISIBLE);
                        mButtonPlay.setVisibility(View.INVISIBLE);
                        mButtonPause.setVisibility(View.INVISIBLE);
                        mButtonFinish.setVisibility(View.INVISIBLE);
                        mSport.setVisibility(View.INVISIBLE);
                        mGpsStatus.setVisibility(View.INVISIBLE);

                        if (mService != null)
                            mService.pauseNotify();
                    }

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        // The progress bar holder.
        mProgressBarHolder = (FrameLayout) findViewById(R.id.fl_progressbar_holder);

        // Get objects from layout.
        mSport = (ImageView) findViewById(R.id.iv_sport);
        mGpsStatus = (ImageView) findViewById(R.id.iv_gps_status);
        mButtonRecord = (FloatingActionButton) findViewById(R.id.b_record);
        mButtonPlay = (FloatingActionButton) findViewById(R.id.b_play);
        mButtonPause = (FloatingActionButton) findViewById(R.id.b_pause);
        mButtonFinish = (ImageView) findViewById(R.id.b_finish);
        mRootView = findViewById(R.id.root_view);

        // Before go on creating this Activity it loads activity saved if any.
        int activityRecordingId = EtropedPreferences.getRecordingActivityId(
                getApplicationContext());
        if (activityRecordingId != -1) {
            Bundle bundle = new Bundle();
            bundle.putInt(EXTRA_ACTIVITY_ID, activityRecordingId);
            getLoaderManager().initLoader(ACTIVITY_RECORDING_LOADER_ID, bundle, this);
        }
        else {
            mSelectedSportId = EtropedPreferences.getDefaultSportId(getApplicationContext());
            // Set the drawable sport.
            setSportDrawableBySportId(mSelectedSportId);
            onCreateGoOn();
        }
    }

    private void onCreateGoOn() {

        // Initialize sport image button.
        initializeSportButton();

        // Initialize gps status button
        initilizeGpsStatus();

        // Initialize record button.
        initializeRecordButton();

        // Initialize play button.
        initializePlayButton();

        // Initialize pause button.
        initializePauseButton();

        // Initialize finish button.
        initializeFinishButton();

        // Add action to receive broadcast from LocationManager.
        IntentFilter gpsStatusFilter = new IntentFilter();
        gpsStatusFilter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        registerReceiver(mGpsStatusReceiver, gpsStatusFilter);

        // Check if gps is disabled then show alert to ask to the user that enable it.
        if (!isGpsProvierEnabled()) {
            DialogsHelper.showSettingsAlert(this, DialogsHelper.SETTINGS_LOCATION);
        }

        // 2 scenarios: the service is off or is on.
        // If is off then initialize buttons and loads splash fragment. Otherwise bind the Activity
        // to the service that is already running.
        if (!isMyServiceRunning(TrackerActivityService.class)) {
            mButtonRecord.setVisibility(View.VISIBLE);
            mButtonPlay.setVisibility(View.INVISIBLE);
            mButtonPause.setVisibility(View.INVISIBLE);
            mButtonFinish.setVisibility(View.INVISIBLE);
            mSport.setVisibility(View.INVISIBLE);
            mGpsStatus.setVisibility(View.INVISIBLE);
            // Loads splash fragment and set visible button.
            loadFragmentByTag(TrackerSplashFragment.TAG);
        }
        else {
            loadFragment();
            getApplicationContext().bindService(
                    new Intent(getApplicationContext(), TrackerActivityService.class),
                    mConnection, getApplicationContext().BIND_AUTO_CREATE
            );
        }
    }

    @Override
    protected void onResume() {

        super.onResume();

        // If GPS is disable shows snackbar information with action to enable it.
        if (!isGpsProvierEnabled())
            showGpsDisableSnackbar();

        // Request needed permission in this activity (location).
        requestPermission();

        // Loads workout if any.
        /*
        if (mCurrentWorkout != null) {
            if (mFragment instanceof AbstractTrackerFragment) {
                mFragment.onWorkoutUpdate(mCurrentWorkout);
            }
        }
        else if (mService != null && mService.getWorkout() != null) {
            mCurrentWorkout = mService.getWorkout();
            if (mFragment instanceof AbstractTrackerFragment &&
                    mCurrentWorkout.getElapsedTime() > 0)
                mFragment.onWorkoutUpdate(mCurrentWorkout);
        }
        */
        loadWorkoutIfAny();
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onStop() {

        super.onStop();

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        unregisterReceiver(mGpsStatusReceiver);

        if (mBound) {
            getApplicationContext().unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    public void onWorkoutUpdate(Workout workout) {

        if (mFragment instanceof AbstractTrackerFragment) {
            mCurrentWorkout = workout;
            mFragment.onWorkoutUpdate(workout);
        }
    }

    @Override
    public void onWaitingGps(boolean waiting) {

        if (waiting)
            showGpsWaitingSnackbar();
        else
            dismissGpsWaitingSnackbar();
    }

    @Override
    public void onServiceLoading() {
        loading();
    }

    @Override
    public void onServiceReady() {

        // Loads the current workout from service.
        /*
        if (mCurrentWorkout == null && mService != null && mService.getWorkout() != null) {
            mCurrentWorkout = mService.getWorkout();
            if (mFragment instanceof AbstractTrackerFragment &&
                    mCurrentWorkout.getElapsedTime() > 0)
                mFragment.onWorkoutUpdate(mCurrentWorkout);
        }
        */
        loadWorkoutIfAny();

        ready();
    }

    private void loading() {
        findViewById(R.id.cl_buttons).setVisibility(View.INVISIBLE);
        findViewById(R.id.fragment_view).setVisibility(View.INVISIBLE);

        AlphaAnimation inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        mProgressBarHolder.setAnimation(inAnimation);
        mProgressBarHolder.setVisibility(View.VISIBLE);
    }

    private void ready() {
        findViewById(R.id.cl_buttons).setVisibility(View.VISIBLE);
        findViewById(R.id.fragment_view).setVisibility(View.VISIBLE);

        AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
        outAnimation.setDuration(200);
        mProgressBarHolder.setAnimation(outAnimation);
        mProgressBarHolder.setVisibility(View.INVISIBLE);
    }

    @Override
    public void setService(TrackerActivityService service) {

        mService = service;

        if (service != null) {
            mBound = true;

            // If there is an activity already running then "press" play button to resume only if
            // gps is enabled.
            if (EtropedPreferences.getRecordingActivityId(getApplicationContext()) != -1) {
                if (isGpsProvierEnabled())
                    play();
                else {
                    mButtonRecord.setVisibility(View.VISIBLE);
                    mButtonPlay.setVisibility(View.INVISIBLE);
                    mButtonPause.setVisibility(View.INVISIBLE);
                    mButtonFinish.setVisibility(View.INVISIBLE);
                    mSport.setVisibility(View.INVISIBLE);
                    mGpsStatus.setVisibility(View.INVISIBLE);
                }
            }
        }
        else {
            mBound = false;
        }

        // Loads the current workout from service.
        /*
        if (mCurrentWorkout == null && mService != null && mService.getWorkout() != null) {
            mCurrentWorkout = mService.getWorkout();
            if (mFragment instanceof AbstractTrackerFragment &&
                    mCurrentWorkout.getElapsedTime() > 0)
                mFragment.onWorkoutUpdate(mCurrentWorkout);
        }
        */
        loadWorkoutIfAny();
    }

    /**
     * This method request access fine location permission to the user.
     */
    private void requestPermission() {

        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            }
            else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_ACCESS_FINE_LOCATION_PERMISSIONS);

                // REQUEST_ACCESS_FINE_LOCATION_PERMISSIONS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else {
            mPermissionGranted = true;
        }
    }

    /**
     * This method is call every time requestPermissions is called.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {

        switch (requestCode) {
            case REQUEST_ACCESS_FINE_LOCATION_PERMISSIONS:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mPermissionGranted = true;
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    //startServiceIfNotRunning();

                }
                else {
                    Toast.makeText(this, getString(R.string.toast_location_permission_needed),
                            Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_ACCESS_FINE_LOCATION_PERMISSIONS);
                }
                return;
        }
    }

    /**
     * Loads fragment depending on sport selected.
     */
    private void loadFragment() {

        if (mSelectedSportId != null && (mSelectedSportId == EtropedContract.SPORT_RUNNING ||
                mSelectedSportId == EtropedContract.SPORT_WALKING)) {

            loadFragmentByTag(TrackerRunningFragment.TAG);
        } else {

            loadFragmentByTag(TrackerBikeFragment.TAG);
        }
    }

    /**
     * Loads a fragment by tag.
     *
     * @param tag The tag of the fragment to load.
     */
    private void loadFragmentByTag(String tag) {

        if (tag.equals(TrackerSplashFragment.TAG)) {
            mFragment = new TrackerSplashFragment();
        } else if (tag.equals(TrackerBikeFragment.TAG)) {
            getSupportActionBar().hide();
            mFragment = new TrackerBikeFragment();
        } else if (tag.equals(TrackerRunningFragment.TAG)) {
            getSupportActionBar().hide();
            mFragment = new TrackerRunningFragment();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_view, mFragment, tag)
                .commit();
        getSupportFragmentManager().executePendingTransactions();

        loadWorkoutIfAny();
    }

    /**
     * Check if GPS provider is enabled.
     *
     * @return True if GPS provider is enabled and false otherwise.
     */
    private boolean isGpsProvierEnabled() {

        if (((LocationManager) getSystemService(LOCATION_SERVICE))
                .isProviderEnabled(LocationManager.GPS_PROVIDER))
            return true;
        else
            return false;
    }

    /**
     * Initialize the sport button image depending on the preferences.
     *
     * Also, prepare onclick listener on sport button. From this the user can change the sport that
     * want to recording.
     */
    private void initializeSportButton() {

        // When user clicks on sport icon it shows an Alert with a list options to choose sport to
        // recording.
        mSport.setOnClickListener(new View.OnClickListener() {

            // The data and adapter.
            final Integer[] nameIds = new Integer[]{
                    R.string.sport_bike,
                    R.string.sport_running,
                    R.string.sport_walking
            };
            final Integer[] drawableIds = new Integer[] {
                    R.drawable.ic_directions_bike_black_48dp,
                    R.drawable.ic_directions_run_black_48dp,
                    R.drawable.ic_directions_walk_black_48dp
            };
            SportsIconsListAdapter adapter = new SportsIconsListAdapter(
                    TrackerActivity.this, nameIds, drawableIds);

            @Override
            public void onClick(View view) {

                // The custom title layout.
                LayoutInflater inflater = TrackerActivity.this.getLayoutInflater();
                View titleLayout = inflater.inflate(R.layout.custom_title_dialog_layout, null);

                // Builds and shows alert dialog.
                // When it selects a sport it changes the selected sport.
                AlertDialog.Builder builder = new AlertDialog.Builder(TrackerActivity.this);
                builder.setCustomTitle(titleLayout)
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                changeSportTo(
                                        EtropedPreferences.getSportIdFromNameInPreferences(
                                                getApplicationContext(),
                                                getApplicationContext().getString(nameIds[i])
                                        )
                                );
                                mSport.setImageResource(drawableIds[i]);
                            }
                        });
                builder.show();
            }
        });
    }

    /**
     * Initialize the gps status button.
     */
    private void initilizeGpsStatus() {
        mGpsStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GpsStatusActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Initialize record button.
     *
     * This record button listen click event. When user click on it then it starts location service
     * through GpsTrackerService only if there is permission granted.
     */
    private void initializeRecordButton() {

        final Activity activity = this;

        mButtonRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Checks if gps provider is enabled. Otherwise show the alert and return.
                if (!isGpsProvierEnabled()) {
                    DialogsHelper.showSettingsAlert(
                            activity, DialogsHelper.SETTINGS_LOCATION);
                    return;
                }

                if (mPermissionGranted) {

                    startServiceIfNotRunning();

                    mButtonRecord.setVisibility(View.INVISIBLE);
                    mButtonPlay.setVisibility(View.VISIBLE);
                    mButtonPause.setVisibility(View.INVISIBLE);
                    mButtonFinish.setVisibility(View.VISIBLE);
                    mSport.setVisibility(View.VISIBLE);
                    mGpsStatus.setVisibility(View.INVISIBLE);

                    loadFragment();
                }
                else {
                    requestPermission();
                }
            }
        });
    }

    /**
     * This play button listen click event to start the activity.
     */
    private void initializePlayButton() {

        mButtonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get the activityId already recording.
                int activityId = EtropedPreferences.getRecordingActivityId(
                        getApplicationContext()
                );

                // If there is not an activity already running the it creates one.
                if (activityId == -1) {

                    new AsyncTask<Void, Void, Integer>() {

                        @Override
                        protected void onPreExecute() {

                            loading();
                        }

                        @Override
                        protected Integer doInBackground(Void... voids) {

                            return ActivityProvider.createActivity(
                                    getApplicationContext(),
                                    mSelectedSportId
                            );
                        }

                        @Override
                        protected void onPostExecute(Integer activityId) {

                            // Exit from the activity because is not possible starts an activity.
                            if (activityId == -1)
                                exitFromActivity();

                            // Things to do when play.
                            ready();
                            play();
                        }
                    }.execute();
                }
                else {

                    // There is an activity already running so play only.
                    play();
                }
            }
        });
    }

    /**
     * This play button listen click event to pause the activity.
     */
    private void initializePauseButton() {

        mButtonPause.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                pause();
            }
        });
    }

    /**
     * This finish button listen click event to finish the activity.
     *
     * When user click on this button calls to the method to ask the user what to do.
     */
    private void initializeFinishButton() {

        mButtonFinish.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                askForFinishAlert();
            }
        });
    }

    /**
     * This function is called when user click on finish button and ask to the user what to do.
     *
     * If user accept then:
     * - Stop TrackerActivityService if is running.
     * - Unbind the activity from the service if is bound.
     * - Deletes activity preference because it is finished.
     * - Finishes this activity.
     */
    private void askForFinishAlert() {

        AlertDialog.Builder yesOrNotAlert = new AlertDialog.Builder(this);
        yesOrNotAlert.setMessage(getString(R.string.alert_ask_for_finish_activity));
        yesOrNotAlert.setNegativeButton(getString(R.string.button_resume),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.cancel();
                    }
                });
        yesOrNotAlert.setPositiveButton(getString(R.string.button_finish),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (mCurrentWorkout.getDistance() == 0) {
                            int recordingActivityId = EtropedPreferences.getRecordingActivityId(
                                            getApplicationContext());
                            if (recordingActivityId >= 0)
                                ActivityProvider.deleteActivity(getApplicationContext(), recordingActivityId);
                        }

                        EtropedPreferences.removeRecordingActivityId(getApplicationContext());
                        exitFromActivity();
                    }
                });
        yesOrNotAlert.show();
    }

    /**
     * This function return true if the service passed by argument is running and false otherwise.
     *
     * @param serviceClass
     * @return
     */
    private boolean isMyServiceRunning(Class<?> serviceClass) {

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets the sport image in the button sport depending on sport id. By default it sets with
     * the bike icon.
     *
     * @param sportId The sport id.
     */
    private void setSportDrawableBySportId(long sportId) {

        if (sportId == EtropedContract.SPORT_RUNNING) {
            mSport.setImageResource(R.drawable.ic_directions_run_black_48dp);
        } else if (sportId == EtropedContract.SPORT_WALKING) {
            mSport.setImageResource(R.drawable.ic_directions_walk_black_48dp);
        } else {
            mSport.setImageResource(R.drawable.ic_directions_bike_black_48dp);
        }
    }

    /**
     * Changes the sport identify selected. If activity is running it changes in database too.
     *
     * @param sportId The sport identify.
     */
    private void changeSportTo(final int sportId) {

        mSelectedSportId = sportId;

        final int activityId = EtropedPreferences.getRecordingActivityId(getApplicationContext());
        if (activityId != -1) {

            // Updates sport's activity in the database.
            new AsyncTask<Void, Void, Integer>() {

                @Override
                protected Integer doInBackground(Void... voids) {

                    return ActivityProvider.updateActivitySport(
                            getApplicationContext(), activityId, sportId);
                }

                @Override
                protected void onPostExecute(Integer rows) {

                    // If error updating then show a Toast message and change sport to default sport.
                    if (rows != 1) {
                        Toast.makeText(
                                getApplicationContext(),
                                getString(R.string.err_no_sport_updated),
                                Toast.LENGTH_LONG
                        ).show();

                        mSelectedSportId = EtropedPreferences.getDefaultSportId(getApplicationContext());
                    }

                    loadFragment();
                }
            }.execute();
        }
        else
            loadFragment();
    }

    /**
     * Things to do when activity is played.
     */
    protected void play() {
        mLastStatus = STATUS_PAUSE;
        mButtonPlay.setVisibility(View.INVISIBLE);
        mButtonPause.setVisibility(View.VISIBLE);
        mButtonFinish.setVisibility(View.VISIBLE);
        mSport.setVisibility(View.VISIBLE);
        mGpsStatus.setVisibility(View.VISIBLE);

        if (mFragment instanceof TrackerSplashFragment)
            loadFragment();

        mService.playNotify();
    }

    /**
     * Things to do when activity is paused (because GPS is off or user click on pause button, for
     * example).
     */
    protected void pause() {
        mLastStatus = STATUS_PLAY;
        mButtonPause.setVisibility(View.INVISIBLE);
        mButtonPlay.setVisibility(View.VISIBLE);
        mButtonFinish.setVisibility(View.VISIBLE);
        mSport.setVisibility(View.VISIBLE);
        mGpsStatus.setVisibility(View.INVISIBLE);

        mService.pauseNotify();
    }

    /**
     * Show gps enable snackbar.
     */
    private void showGpsEnableSnackbar() {
        Snackbar snackbar = Snackbar.make(
                mRootView,
                getString(R.string.alert_gps_enabled_title),
                Snackbar.LENGTH_LONG)
                .setActionTextColor(getResources().getColor(R.color.colorTextOnAccent));

        View sbView = snackbar.getView();
        sbView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) sbView.getLayoutParams();
        params.gravity = Gravity.TOP;
        sbView.setLayoutParams(params);

        snackbar.show();
    }

    /**
     * Show gps disable snackbar.
     */
    private void showGpsDisableSnackbar() {
        Snackbar snackbar = Snackbar.make(
                mRootView,
                getString(R.string.alert_gps_disabled_title),
                Snackbar.LENGTH_INDEFINITE)
                .setActionTextColor(getResources().getColor(R.color.colorTextSnackbarError))
                .setAction(R.string.enable, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });

        View sbView = snackbar.getView();
        sbView.setBackgroundColor(getResources().getColor(R.color.colorBackgroundSnackbarError));
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) sbView.getLayoutParams();
        params.gravity = Gravity.TOP;
        sbView.setLayoutParams(params);

        snackbar.show();
    }

    /**
     * Show waiting gps snackbar if it is not currently showed.
     */
    private void showGpsWaitingSnackbar() {

        if (mWaitingGpsSnackbar == null) {
            mWaitingGpsSnackbar = Snackbar.make(
                    mRootView,
                    getString(R.string.alert_gps_waiting_title),
                    Snackbar.LENGTH_INDEFINITE)
                    .setActionTextColor(getResources().getColor(R.color.colorTextSnackbarInfo));

            View sbView = mWaitingGpsSnackbar.getView();
            sbView.setBackgroundColor(getResources().getColor(R.color.colorBackgroundSnackbarInfo));
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) sbView.getLayoutParams();
            params.gravity = Gravity.TOP;
            sbView.setLayoutParams(params);

            mWaitingGpsSnackbar.show();
        }
    }

    /**
     * Dismiss waiting gps snackbar if it is showing.
     */
    private void dismissGpsWaitingSnackbar() {

        if (mWaitingGpsSnackbar != null) {
            mWaitingGpsSnackbar.dismiss();
            mWaitingGpsSnackbar = null;
        }
    }

    /**
     * Start the service needed if not running.
     */
    private void startServiceIfNotRunning() {

        if (!isMyServiceRunning(TrackerActivityService.class)) {

            // Creates an intent to start service.
            Intent intent = new Intent(getApplicationContext(),
                    TrackerActivityService.class);
            startService(intent);

            // Binds to the service.
            getApplicationContext().bindService(
                    new Intent(getApplicationContext(), TrackerActivityService.class),
                    mConnection, getApplicationContext().BIND_AUTO_CREATE
            );
        }
    }

    /**
     * Stop running service.
     */
    private void stopRunningService() {

        if (isMyServiceRunning(TrackerActivityService.class)) {

            stopService(new Intent(getApplicationContext(),
                    TrackerActivityService.class));

            if (mBound) {

                getApplicationContext().unbindService(mConnection);
                mBound = false;
            }
        }
    }

    /**
     * Stop running service and exit from this activity.
     */
    private void exitFromActivity() {

        stopRunningService();
        TrackerActivity.this.finish();
    }

    /**
     * This loader loads the selected sport from an activity that is recording if any or loads
     * default sport.
     *
     * When application is destroying it can recovery because in SharedPreferences it saves the
     * activity id already recording. This Loader get from Bundle args the id of the activity
     * already running to loads the sport id in the member mSelectedSportId.
     *
     * @param i
     * @param args
     * @return
     */
    @Override
    public Loader<ActivityEntity> onCreateLoader(int i, final Bundle args) {

        return new AsyncTaskLoader<ActivityEntity>(this) {

            @Override
            protected void onStartLoading() {

                loading();
                forceLoad();
            }

            @Override
            public ActivityEntity loadInBackground() {

                int activityRecordingId = args.getInt(EXTRA_ACTIVITY_ID);

                ActivityEntity activityEntity = ActivityProvider.getActivity(
                        getApplicationContext(), activityRecordingId);

                return activityEntity;
            }

            @Override
            public void deliverResult(ActivityEntity data) {
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ActivityEntity> loader, ActivityEntity activityEntity) {

        ready();

        if (activityEntity != null)
            mSelectedSportId = activityEntity.getFkSport();
        else {
            // Any issue was triggered. Maybe the sport saved in preferences does already not
            // exists so it removes it.
            mSelectedSportId = EtropedPreferences.getDefaultSportId(getApplicationContext());
            EtropedPreferences.removeRecordingActivityId(getApplicationContext());
        }

        // Set the drawable sport.
        setSportDrawableBySportId(mSelectedSportId);

        // Go on with onCreate.
        onCreateGoOn();
    }

    private void loadWorkoutIfAny() {

        if (mCurrentWorkout != null) {
            if (mFragment instanceof AbstractTrackerFragment)
                mFragment.onWorkoutUpdate(mCurrentWorkout);
        }
        else if (mService != null && mService.getWorkout() != null) {
            mCurrentWorkout = mService.getWorkout();
            if (mFragment instanceof AbstractTrackerFragment &&
                    mCurrentWorkout.getElapsedTime() > 0)
                mFragment.onWorkoutUpdate(mCurrentWorkout);
        }
    }

    @Override
    public void onLoaderReset(Loader<ActivityEntity> loader) {}
}
