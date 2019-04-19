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

import es.rgmf.etroped.workout.Workout;

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public interface ITrackerActivityClient {

    public void onWorkoutUpdate(Workout workout);

    public void onWaitingGps(boolean waiting);

    public void onServiceLoading();

    public void onServiceReady();

    /**
     * Clients receive the service instance through this method when service is connected. If
     * service is disconnected then service parameter is null.
     *
     * @param service An instance if is connected and null otherwise.
     */
    public void setService(TrackerActivityService service);

}
