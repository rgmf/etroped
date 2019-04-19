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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import es.rgmf.etroped.fragment.ActivitiesFragment;
import es.rgmf.etroped.util.EtropedPreferences;

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static String TAG = MainActivity.class.getSimpleName();

    private static final int PERMISSION_WRITE = 1;

    NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // The activity action bar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer layout slide left menu in the toolbar.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        // When activity is created it loads activities fragment.
        mNavigationView.setCheckedItem(R.id.nav_activities_list);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_view, new ActivitiesFragment(), ActivitiesFragment.TAG)
                .commit();

        // Check and ask for permission needed.
        checkPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE);



        // TODO Eliminar: si descomento esto se guardan los logs en el fichero.
        /********************** ELIMINAR *************************
        String filename = this.getExternalFilesDir(null).getPath() + File.separator + "etroped.log";
        String command = "logcat -f "+ filename + " -v time *:V";

        Log.d(TAG, filename);

        Log.d(TAG, "command: " + command);

        try{
            Runtime.getRuntime().exec(command);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        ***********************************************************/

        //Log.d(TAG, "Comenzamos");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNavigationView.setCheckedItem(R.id.nav_activities_list);
    }

    private void loadFragmentByName(String name) {

        Fragment activitiesFragment = new ActivitiesFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (name.equals(ActivitiesFragment.TAG)) {
            transaction.replace(R.id.fragment_view, activitiesFragment, ActivitiesFragment.TAG);
        }

        // If the fragment to be load is not already load then add to back stack it.
        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(name);
        if (!(currentFragment != null && currentFragment.isVisible())) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_new_activity) {
            if (EtropedPreferences.isPebbleChecked(this))
                startActivity(new Intent(this, TrackerPebbleActivity.class));
            else
                startActivity(new Intent(this, TrackerActivity.class));
        } else if (id == R.id.nav_activities_list) {
            loadFragmentByName(ActivitiesFragment.TAG);
        } else if (id == R.id.nav_stats) {
            startActivity(new Intent(this, StatsActivity.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(this, AboutActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void checkPermissions(String permission) {
        int permissionCheck = ContextCompat.checkSelfPermission(this, permission);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_WRITE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_WRITE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!

                } else {

                    Toast.makeText(
                            this,
                            R.string.permission_storage_explanation,
                            Toast.LENGTH_LONG
                    ).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }
}
