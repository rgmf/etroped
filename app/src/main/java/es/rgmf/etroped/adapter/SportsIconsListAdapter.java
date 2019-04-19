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

package es.rgmf.etroped.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.database.DataSetObserver;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import es.rgmf.etroped.R;
import es.rgmf.etroped.util.EtropedPreferences;

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class SportsIconsListAdapter extends ArrayAdapter<Integer> {

    private final Context mContext;
    private final Integer[] mNamesIds;
    private final Integer[] mDrawablesIds;


    public SportsIconsListAdapter(@NonNull Context context, @NonNull Integer[] namesIds,
                                  @NonNull Integer[] drawablesIds) {
        super(context, R.layout.list_icons_sports, drawablesIds);

        mContext = context;
        mNamesIds = namesIds;
        mDrawablesIds = drawablesIds;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        View view = inflater.inflate(R.layout.list_icons_sports, null, true);

        String sportName = mContext.getString(mNamesIds[position]);

        ImageView iv = (ImageView) view.findViewById(R.id.iv);
        TextView tv = (TextView) view.findViewById(R.id.tv);
        iv.setImageResource(mDrawablesIds[position]);
        tv.setText(sportName);
        ImageViewCompat.setImageTintList(
                iv,
                ColorStateList.valueOf(
                        ContextCompat.getColor(
                                mContext,
                                EtropedPreferences.getSportColor(
                                        EtropedPreferences.getSportIdFromNameInPreferences(
                                                mContext,
                                                sportName
                                        )
                                )
                        )
                )
        );

        return view;
    }
}
