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
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import es.rgmf.etroped.data.EtropedContract;
import es.rgmf.etroped.data.entity.ActivityEntity;
import es.rgmf.etroped.fragment.ChartFragment;
import es.rgmf.etroped.fragment.InfoFragment;
import es.rgmf.etroped.fragment.StatsFragmentFactory;
import es.rgmf.etroped.util.EtropedDateTimeUtils;
import es.rgmf.etroped.util.EtropedPreferences;
import es.rgmf.etroped.viewmodel.ActivityEntityViewModel;

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class ActivityDetailsActivity extends AppCompatActivity {

    private static final String TAG = ActivityDetailsActivity.class.getSimpleName();

    // The tabs identifies.
    private static final int NUMBER_OF_TABS = 3;
    private static final int TAB_DETAILS = 0;
    private static final int TAB_REVIEW = 1;
    private static final int TAB_CHART = 2;

    public static final String EXTRA_ACTIVITY_ID = "extra_activity_id";

    public static final int REQUEST_CODE_FOR_RESULT_ACTIVITY = 1;

    // The id of the activity and activity entity.
    private int mActivityId;
    private ActivityEntity mActivityEntity;

    // Views.
    private FrameLayout mProgressBarHolder;
    private TabLayout mTabs;

    // Toolbar.
    Toolbar mToolbar;

    // The ViewModel.
    private ActivityEntityViewModel mViewModel;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // The toolbar.
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.loading));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // The tabs.
        mTabs = (TabLayout) findViewById(R.id.tabs);

        // The progress bar holder.
        mProgressBarHolder = (FrameLayout) findViewById(R.id.fl_progressbar_holder);

        // This is the activity id receive from component that start this activity.
        Intent intent = getIntent();
        mActivityId = intent.getIntExtra(EXTRA_ACTIVITY_ID, -1);


        // Loading...
        AlphaAnimation inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        mProgressBarHolder.setAnimation(inAnimation);
        mProgressBarHolder.setVisibility(View.VISIBLE);
        mTabs.setVisibility(View.GONE);

        // Get all Activity data from ViewModel and when data is loaded then hide the progress bar
        // and finish the tabs and view pager configuration.
        mViewModel = ViewModelProviders.of(this)
                .get(ActivityEntityViewModel.class);
        // Observes the data to be load.
        mViewModel.loading(mActivityId).observe(this, new Observer<Boolean>() {

            /**
             * This method is called when ActivityEntity is changed. This mean that it has the
             * data needed to load Fragments with the views.
             *
             * @param load
             */
            @Override
            public void onChanged(@Nullable Boolean load) {
                if (load != null) {
                    AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
                    outAnimation.setDuration(200);
                    mProgressBarHolder.setAnimation(outAnimation);
                    mProgressBarHolder.setVisibility(View.GONE);
                    mTabs.setVisibility(View.VISIBLE);

                    mActivityEntity = mViewModel.getActivityEntity();

                    // Create the adapter that will return a fragment for each of the three
                    // primary sections of the activity.
                    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

                    // Set up the ViewPager with the sections adapter.
                    mViewPager = (ViewPager) findViewById(R.id.container);
                    mViewPager.setAdapter(mSectionsPagerAdapter);

                    // Set toolbar title.
                    mToolbar.setTitle(EtropedDateTimeUtils.getFriendlyDateString(
                            getApplicationContext(), mActivityEntity.getStartTime(), true));

                    // The tab layout.
                    TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
                    tabLayout.setupWithViewPager(mViewPager);

                    // Icons for the three tabs.
                    tabLayout.getTabAt(0).setIcon(R.drawable.ic_map_white_48dp);
                    tabLayout.getTabAt(1).setIcon(R.drawable.ic_description_white_48dp);
                    tabLayout.getTabAt(2).setIcon(R.drawable.ic_pie_chart_white_48dp);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_remove) {
            // Builds an alert to ask the user if he/she is sure with remove action.
            AlertDialog.Builder yesOrNotAlert = new AlertDialog.Builder(this);
            yesOrNotAlert.setMessage(getString(R.string.alert_ask_for_remove_activity));

            // No removes.
            yesOrNotAlert.setNegativeButton(getString(R.string.button_cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.cancel();
                        }
                    });

            // Yes removes.
            yesOrNotAlert.setPositiveButton(getString(R.string.button_ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String stringId = Integer.toString(mActivityId);
                            Uri uri = EtropedContract.ActivityEntry.CONTENT_URI
                                    .buildUpon().appendPath(stringId).build();

                            getContentResolver().delete(uri, null, null);

                            if (mActivityId == EtropedPreferences.getRecordingActivityId(
                                    getApplicationContext())) {
                                EtropedPreferences.removeRecordingActivityId(
                                        getApplicationContext());
                            }

                            ActivityDetailsActivity.this.finish();
                        }
                    });
            yesOrNotAlert.show();

            return true;
        }
        else if (id == R.id.action_edit) {
            Intent intent = new Intent(this, EditActivity.class);
            intent.putExtra(EditActivity.EXTRA_ACTIVITY_ID, mActivityId);
            startActivityForResult(intent, REQUEST_CODE_FOR_RESULT_ACTIVITY);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_FOR_RESULT_ACTIVITY:
                if (resultCode == Activity.RESULT_OK) {
                    mViewModel.reloading(mActivityId);
                }
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment f = null;

            switch (position) {
                case TAB_DETAILS:
                    f = InfoFragment.newInstance();
                    break;

                case TAB_REVIEW:
                    ActivityEntity ae = mViewModel.getActivityEntity();
                    if (ae != null)
                        f = StatsFragmentFactory.newInstance(ae);
                    break;

                case TAB_CHART:
                    f = ChartFragment.newInstance();
                    break;
            }

            return f;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return NUMBER_OF_TABS;
        }

        /*
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case TAB_DETAILS:
                    return "DETAIL";
                case TAB_REVIEW:
                    return "STATS";
                case TAB_CHART:
                    return "GRAPH";
            }
            return null;
        }
        */
    }

    public ActivityEntityViewModel getViewModel() {
        return mViewModel;
    }
}
