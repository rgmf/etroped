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

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import es.rgmf.etroped.R;
import es.rgmf.etroped.data.entity.LapEntity;
import es.rgmf.etroped.util.EtropedNumberUtils;
import es.rgmf.etroped.util.EtropedUnitsUtils;

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class LapsListAdapter extends RecyclerView.Adapter<LapsListAdapter.LapViewHolder> {

    private Context mContext;
    private List<LapEntity> mLaps;
    private int mLapDistance;
    private double mBestLap;
    private double mWorstLap;

    public LapsListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public LapViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.lap_layout, parent, false);

        return new LapViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LapViewHolder holder, int position) {

        if (position == 0) {
            holder.tvNum.setText(EtropedUnitsUtils.distanceCaptionUnit(mContext));
            holder.tvPace.setText(mContext.getString(R.string.pace_caption_label));
            holder.tvElevation.setText(mContext.getString(R.string.elevation_caption_label));

            // TODO Deprecated functions used. Is should change it.
            holder.itemView.setBackgroundColor(
                    mContext.getResources().getColor(R.color.colorPrimaryLight));

            holder.tvNum.setTextAppearance(mContext, R.style.TextAppearance_AppCompat_Caption);
            holder.tvPace.setTextAppearance(mContext, R.style.TextAppearance_AppCompat_Caption);
            holder.tvElevation.setTextAppearance(mContext,
                    R.style.TextAppearance_AppCompat_Caption);

            holder.vBarLeft.setVisibility(View.INVISIBLE);
        }
        else {
            holder.itemView.setBackgroundColor(
                    mContext.getResources().getColor(android.R.color.white));

            LapEntity lap = mLaps.get(position - 1);

            // If the last lap is less than 10 meters not show it.
            if (lap.getDistance() > 10) {
                if (lap.getDistance() >= mLapDistance)
                    holder.tvNum.setText(Integer.toString(position));
                else
                    holder.tvNum.setText(
                            EtropedNumberUtils.formatReal(lap.getDistance() / mLapDistance));

                double pace = (lap.getElapsedTime() / 1000d) / lap.getDistance();
                holder.tvPace.setText(EtropedUnitsUtils.formatPace(mContext, (float) (pace)));
                holder.tvElevation.setText(Integer.toString((int) lap.getAltitude()));

                // The weight of the bar.
                float weightLeft = (float) (pace / mBestLap);
                holder.vBarLeft.setVisibility(View.VISIBLE);

                // Set layout_weight of the bar view programmatically.
                int pixels = EtropedUnitsUtils.fromDpToPx(mContext, 16);
                LinearLayout.LayoutParams pLeft = new LinearLayout.LayoutParams(0, pixels);
                pLeft.weight = weightLeft;
                holder.vBarLeft.setLayoutParams(pLeft);
            }
            else {
                holder.vBarLeft.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * Return the number of items in the recycler view adapter. It adds 1 to add a header first.
     *
     * @return
     */
    @Override
    public int getItemCount() {
        if (mLaps == null)
            return 1;
        else
            return mLaps.size() + 1;
    }

    public List<LapEntity> swapLaps(List<LapEntity> laps) {
        if (mLaps == laps)
            return null;

        List<LapEntity> temp = mLaps;
        mLaps = laps;

        if (laps.size() > 0) {
            LapEntity firstLap = laps.get(0);
            // TODO I have to create a preference with lap distance for running. At this
            // moment I use EtropedUnitsUtils.baseUnit to get it.
            mLapDistance = (int) EtropedUnitsUtils.baseUnit(mContext);

            mBestLap = (firstLap.getElapsedTime() / 1000d) / firstLap.getDistance();
            mWorstLap = mBestLap;
            for (LapEntity lap : laps) {
                double current = (lap.getElapsedTime() / 1000d) / lap.getDistance();
                if (mBestLap > current)
                    mBestLap = current;
                if (mWorstLap < current)
                    mWorstLap = current;
            }

            if (laps != null)
                this.notifyDataSetChanged();
        }

        return temp;
    }

    /**
     * Inner class for creating ViewHolders.
     */
    class LapViewHolder extends RecyclerView.ViewHolder {

        TextView tvNum;
        TextView tvPace;
        TextView tvElevation;
        View vBarLeft;
        View vBarRight;

        public LapViewHolder(View itemView) {
            super(itemView);

            tvNum = (TextView) itemView.findViewById(R.id.tv_num);
            tvPace = (TextView) itemView.findViewById(R.id.tv_pace);
            tvElevation = (TextView) itemView.findViewById(R.id.tv_elevation);
            vBarLeft = itemView.findViewById(R.id.v_bar_left);
            vBarRight = itemView.findViewById(R.id.v_bar_right);
        }
    }
}
