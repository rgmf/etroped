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

import java.util.ArrayList;

public class ActivityViewList {
    private Context mContext;
    ArrayList<ViewTypeAbstract> mList = new ArrayList<>();

    public ActivityViewList(Context c) {
        mContext = c;
    }

    public int getCount() {
        if (mList != null)
            return mList.size();
        else
            return 0;
    }

    public void add(ViewTypeAbstract v) {
        mList.add(v);
    }

    public ViewTypeAbstract get(int position) {
        return mList.get(position);
    }
}
