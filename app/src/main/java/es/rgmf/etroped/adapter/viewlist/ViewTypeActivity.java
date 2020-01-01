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
import android.database.Cursor;

import java.util.ArrayList;

import es.rgmf.etroped.data.entity.ActivityEntity;

public class ViewTypeActivity extends ViewTypeAbstract {
    private Context mContext;
    private ActivityEntity mActivity;

    public static ViewTypeActivity newInstance(Context context) {
        ViewTypeActivity i = new ViewTypeActivity();
        i.mContext = context;
        return i;
    }

    @Override
    public int getViewType() {
        return TYPE_ACTIVITY;
    }

    public ActivityEntity getActivity() {
        return mActivity;
    }

    public void setActivity(ActivityEntity a) {
        mActivity = a;
    }
}
