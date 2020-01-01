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

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import es.rgmf.etroped.data.EtropedContract;
import es.rgmf.etroped.fragment.StatsFragmentFactory;
import es.rgmf.etroped.fragment.StatsMonthsFragment;
import es.rgmf.etroped.fragment.StatsSummaryFragment;
import es.rgmf.etroped.fragment.StatsYearsFragment;
import es.rgmf.etroped.viewmodel.StatsViewModel;

public class StatsActivity extends AppCompatActivity {
    private static final String TAG = StatsActivity.class.getSimpleName();

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    // The ViewModel.
    private StatsViewModel mViewModel;

    // Toolbar.
    private Toolbar mToolbar;

    // The progress bar.
    private FrameLayout mProgressBarHolder;

    // The tabs.
    private TabLayout mTabs;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // The tabs.
        mTabs = (TabLayout) findViewById(R.id.tabs);

        // The progress bar holder.
        mProgressBarHolder = (FrameLayout) findViewById(R.id.fl_progressbar_holder);

        // Load view model data.
        loadViewModelData(false,null);
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_stats, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

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
            switch (position) {
                case 0:
                    return StatsSummaryFragment.newInstance();
                case 1:
                    return StatsMonthsFragment.newInstance();
                case 2:
                    return StatsYearsFragment.newInstance();
                default:
                    return StatsSummaryFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }

    public StatsViewModel getViewModel() {
        return mViewModel;
    }

    /**
     * Load view model data and set fragment fragment like active if any.
     *
     * @param reload True for reloading mode (load once again the view model data).
     * @param fragment Fragment to be selected. By default (if is null) set the first one.
     */
    public void loadViewModelData(boolean reload, final Fragment fragment) {

        // Loading...
        AlphaAnimation inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        mProgressBarHolder.setAnimation(inAnimation);
        mProgressBarHolder.setVisibility(View.VISIBLE);
        if (mViewPager != null)
            mViewPager.setVisibility(View.GONE);
        mTabs.setVisibility(View.GONE);

        // Get all Stats data from ViewModel and when data is loaded then hide the progress bar.
        mViewModel = ViewModelProviders.of(this).get(StatsViewModel.class);
        // Observes the data to be loaded.
        mViewModel.loading(reload).observe(this, new Observer<Boolean>() {

            /**
             * This method is called when Stats change. This mean that it has the
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

                    // Create the adapter that will return a fragment for each of the three
                    // primary sections of the activity.
                    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

                    // Set up the ViewPager with the sections adapter.
                    mViewPager = (ViewPager) findViewById(R.id.container);
                    mViewPager.setVisibility(View.VISIBLE);
                    mViewPager.setAdapter(mSectionsPagerAdapter);

                    // The tab layout.
                    TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
                    mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
                    tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));


                    if (fragment != null) {
                        if (fragment instanceof StatsSummaryFragment) {
                            mViewPager.setCurrentItem(0);
                        } else if (fragment instanceof StatsMonthsFragment) {
                            mViewPager.setCurrentItem(1);
                        } else if (fragment instanceof StatsYearsFragment) {
                            mViewPager.setCurrentItem(2);
                        } else {
                            mViewPager.setCurrentItem(0);
                        }
                    }
                }
            }
        });
    }
}
