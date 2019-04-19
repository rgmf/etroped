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

package es.rgmf.etroped.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import es.rgmf.etroped.R;

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public final class DialogsHelper {

    public static final int SETTINGS_LOCATION = 1;
    public static final int SETTINGS_INTERNET_FOR_SEA_LEVEL_PRESURE = 2;

    /**
     * Function to show settings alert dialog.
     */
    public static void showSettingsAlert(final Context context, int setting) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        switch (setting) {

            case SETTINGS_LOCATION:

                // Setting Dialog Message.
                alertDialog.setMessage(
                        context.getString(R.string.alert_gps_enable_location_message));

                // On pressing Settings button
                alertDialog.setPositiveButton(context.getString(R.string.button_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int which) {
                                Intent intent = new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                context.startActivity(intent);
                            }
                        });

                break;

            case SETTINGS_INTERNET_FOR_SEA_LEVEL_PRESURE:

                // Setting Dialog Message.
                alertDialog.setMessage(
                        context.getString(R.string.alert_internet_enable_for_pressure_message));

                // On pressing Settings button
                alertDialog.setPositiveButton(context.getString(R.string.button_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int which) {
                                Intent intent = new Intent(
                                        Settings.ACTION_NETWORK_OPERATOR_SETTINGS);
                                context.startActivity(intent);
                            }
                        });

                break;
        }

        // On pressing cancel button.
        alertDialog.setNegativeButton(context.getString(R.string.button_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // Showing Alert Message
        alertDialog.show();
    }
}
