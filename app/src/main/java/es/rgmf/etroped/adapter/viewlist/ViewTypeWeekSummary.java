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

package es.rgmf.etroped.adapter.viewlist;

import android.content.Context;
import android.util.Log;

import es.rgmf.etroped.R;
import es.rgmf.etroped.data.EtropedContract;
import es.rgmf.etroped.data.entity.ActivityEntity;

public class ViewTypeWeekSummary extends ViewTypeAbstract {
    private Context mContext;
    private String mCaption;
    private long mStartDate;
    private long mEndDate;
    private Stat mWalking = new Stat(0L, 0f, 0);
    private Stat mRunning = new Stat(0L, 0f, 0);
    private Stat mCycling = new Stat(0L, 0f, 0);

    public static ViewTypeWeekSummary newInstance(Context context) {
        ViewTypeWeekSummary i = new ViewTypeWeekSummary();
        i.mContext = context;
        i.mCaption = i.mContext.getString(R.string.week_caption);
        return i;
    }

    public void add(ActivityEntity a) {
        switch (a.getFkSport()) {
            case EtropedContract.SPORT_WALKING:
                mWalking.time += a.getTime();
                mWalking.distance += a.getDistance();
                break;
            case EtropedContract.SPORT_RUNNING:
                mRunning.time += a.getTime();
                mRunning.distance += a.getDistance();
                break;
            case EtropedContract.SPORT_BIKE:
                mCycling.time += a.getTime();
                mCycling.distance += a.getDistance();
                break;
        }
    }

    public void setCaption(String c) {
        mCaption = c;
    }

    public String getCaption() {
        return mCaption;
    }

    @Override
    public int getViewType() {
        return TYPE_WEEK_SUMMARY;
    }

    public long getStartDate() {
        return mStartDate;
    }

    public void setStartDate(long mStartDate) {
        this.mStartDate = mStartDate;
    }

    public long getEndDate() {
        return mEndDate;
    }

    public void setEndDate(long mEndDate) {
        this.mEndDate = mEndDate;
    }

    public Stat getWalking() {
        return mWalking;
    }

    public Stat getRunning() {
        return mRunning;
    }

    public Stat getCycling() {
        return mCycling;
    }

    public class Stat {
        public long time;
        public float distance;
        public int elevationGain;

        public Stat(long t, float d, int e) {
            time = t;
            distance = d;
            elevationGain = e;
        }
    }
}
