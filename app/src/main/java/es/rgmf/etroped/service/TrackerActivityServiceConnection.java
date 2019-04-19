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

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class TrackerActivityServiceConnection implements ServiceConnection {

    private static final String TAG = TrackerActivityServiceConnection.class.getSimpleName();

    private ITrackerActivityClient mClient;

    public TrackerActivityServiceConnection(ITrackerActivityClient client) {

        mClient = client;
        TrackerActivityService.addServiceClient(mClient);
    }

    /**
     *  This is called when the connection with the service has been established, giving us the
     *  object we can use to interact with the service.
     *
     * @param componentName
     * @param service
     */
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder service) {

        TrackerActivityService.TrackerActivityServiceBinder binder;

        binder = (TrackerActivityService.TrackerActivityServiceBinder) service;

        mClient.setService(binder.getService());
    }

    /**
     * This method is called when the connection with the service has been unexpectedly
     * disconnected, that is, its process crashed.
     *
     * @param componentName
     */
    @Override
    public void onServiceDisconnected(ComponentName componentName) {

        mClient.setService(null);
        TrackerActivityService.removeServiceClient(mClient);
    }
}
