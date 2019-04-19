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

package es.rgmf.etroped.fragment;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import es.rgmf.etroped.data.EtropedContract;
import es.rgmf.etroped.data.entity.ActivityEntity;

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class StatsFragmentFactory {

    public static Fragment newInstance(ActivityEntity activityEntity) {

        switch (activityEntity.getFkSport()) {
            case EtropedContract.SPORT_BIKE:
            case EtropedContract.SPORT_BIKE_MTB:
            case EtropedContract.SPORT_BIKE_ROAD:
                return StatsBikeFragment.newInstance();

            case EtropedContract.SPORT_RUNNING:
            case EtropedContract.SPORT_WALKING:
                return StatsRunningFragment.newInstance();

            default:
                return null;
        }
    }
}
