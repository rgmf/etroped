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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import es.rgmf.etroped.dialogs.DialogPreferenceCompat;
import es.rgmf.etroped.dialogs.NumberPickerPreference;

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class SettingsFragmet extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = SettingsFragmet.class.getSimpleName();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_etroped);

        // Set summary of all preferences.
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        int count = preferenceScreen.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            Preference p = preferenceScreen.getPreference(i);
            if (p instanceof PreferenceCategory) {
                int countGroup = ((PreferenceCategory) p).getPreferenceCount();
                for (int j = 0; j < countGroup; j++) {
                    Preference pInGrp = ((PreferenceCategory) p).getPreference(j);
                    if (pInGrp instanceof NumberPickerPreference) {
                        String value = String.valueOf(sharedPreferences.getString(p.getKey(), ""));
                        setPreferenceSummary(pInGrp, value);
                    }
                    else if (pInGrp instanceof ListPreference) {
                        String value = sharedPreferences.getString(p.getKey(), "");
                        setPreferenceSummary(pInGrp, value);
                    }
                }
            }
            if (p instanceof NumberPickerPreference) {
                String value = String.valueOf(sharedPreferences.getString(p.getKey(), ""));
                setPreferenceSummary(p, value);
            }
            else if (p instanceof ListPreference) {
                String value = sharedPreferences.getString(p.getKey(), "");
                setPreferenceSummary(p, value);
            }
        }
        /*
        for (int i = 0; i < count; i++) {
            Preference p = preferenceScreen.getPreference(i);
            // CheckBoxPreference does not need set its summary. You can set a summary from XML
            // attributes.
            if (p instanceof NumberPickerPreference) {
                String value = String.valueOf(sharedPreferences.getString(p.getKey(), ""));
                setPreferenceSummary(p, value);
            }
            //else if (!(p instanceof CheckBoxPreference)) {
            //    String value = sharedPreferences.getString(p.getKey(), "");
            //    setPreferenceSummary(p, value);
            //}
        }
        */
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * The purpose of this method is to set the correct preference summary.
     *
     * @param preference The preference to set the summary.
     * @param value The value of the summary.
     */
    private void setPreferenceSummary(Preference preference, String value) {
        if (preference instanceof ListPreference) {
            // Cast the preference to ListPreference.
            ListPreference listPreference = (ListPreference) preference;
            // Find the index of the preference.
            int prefIndex = listPreference.findIndexOfValue(value);
            if (prefIndex >= 0) {
                // Get label associated to the value.
                listPreference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        }
        else if (preference instanceof EditTextPreference) {
            preference.setSummary(value);
        }
        else if (preference instanceof NumberPickerPreference) {
            preference.setSummary(value);
        }
    }

    /**
     * When a preference changes then it calls this method that change the preference summary.
     *
     * @param sharedPreferences
     * @param key
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (preference != null) {
            if (preference instanceof NumberPickerPreference) {
                String value = sharedPreferences.getString(preference.getKey(), "");
                setPreferenceSummary(preference, value);
            }
            else if (preference instanceof ListPreference) {
                String value = sharedPreferences.getString(preference.getKey(), "");
                setPreferenceSummary(preference, value);
            }
        }
    }

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        DialogFragment fragment;
        if (preference instanceof DialogPreferenceCompat) {
            fragment = ((DialogPreferenceCompat)preference).createDialog();
            Bundle bundle = new Bundle(1);
            bundle.putString("key", preference.getKey());
            fragment.setArguments(bundle);
            fragment.setTargetFragment(this, 0);
            fragment.show(getFragmentManager(),
                    "android.support.v7.preference.PreferenceFragment.DIALOG");
        } else super.onDisplayPreferenceDialog(preference);
    }
}
