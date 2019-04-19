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
import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CombinedChart;

import es.rgmf.etroped.R;
import es.rgmf.etroped.adapter.viewlist.ActivityViewList;
import es.rgmf.etroped.adapter.viewlist.BarChartViewList;
import es.rgmf.etroped.adapter.viewlist.ViewTypeAbstract;
import es.rgmf.etroped.adapter.viewlist.ViewTypeActivity;
import es.rgmf.etroped.adapter.viewlist.ViewTypeBarChart;
import es.rgmf.etroped.adapter.viewlist.ViewTypeWeekSummary;
import es.rgmf.etroped.data.entity.ActivityEntity;
import es.rgmf.etroped.util.EtropedDateTimeUtils;
import es.rgmf.etroped.util.EtropedPreferences;
import es.rgmf.etroped.util.EtropedUnitsUtils;

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class BarChartAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = BarChartAdapter.class.getSimpleName();

    private Context mContext;
    private BarChartViewList mViewList;

    /**
     * Interface to listen click on items.
     *
     * This interface contain a method that receive the id of the activity that is clicked.
     */
    public interface ListItemClickListener {
        void onListItemClick(int itemIdClicked);
    }

    public BarChartAdapter(Context context) {
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View view;
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType) {
            case ViewTypeAbstract.TYPE_BAR_CHART:
                view = inflater.inflate(R.layout.bar_chart_layout, parent, false);
                viewHolder = new BarChartAdapter.BarChartViewHolder(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case ViewTypeAbstract.TYPE_BAR_CHART:
                BarChartViewHolder barChartViewHolder = (BarChartViewHolder) holder;
                ViewTypeBarChart viewTypeBarChart = (ViewTypeBarChart) mViewList.get(position);

                // Sport icon.
                barChartViewHolder.ivSport.setImageResource(
                        EtropedPreferences.getSportDrawableById(
                                viewTypeBarChart.getSportId()
                        )
                );
                ImageViewCompat.setImageTintList(
                        barChartViewHolder.ivSport,
                        ColorStateList.valueOf(
                                ContextCompat.getColor(
                                        mContext,
                                        EtropedPreferences.getSportColor(
                                                viewTypeBarChart.getSportId()
                                        )
                                )
                        )
                );

                // Description.
                barChartViewHolder.tvDescription.setText(viewTypeBarChart.getDescription());

                // Set the chart.
                viewTypeBarChart.setChart(barChartViewHolder.chart);

                break;
        }
    }

    @Override
    public int getItemCount() {

        if (mViewList == null)
            return 0;
        else
            return mViewList.getCount();
    }

    @Override
    public int getItemViewType(int position) {
        return mViewList.get(position).getViewType();
    }

    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor cursor) that is passed in.
     *
     * @param data The activity view list
     */
    public BarChartViewList swapData(BarChartViewList data) {

        if (mViewList == data)
            return null;

        BarChartViewList temp = mViewList;
        this.mViewList = data;

        if (data != null)
            this.notifyDataSetChanged();

        return temp;
    }

    /**
     * Inner class for creating ActivityViewHolder.
     */
    public class BarChartViewHolder extends RecyclerView.ViewHolder {

        ImageView ivSport;
        TextView tvDescription;
        CombinedChart chart;


        public BarChartViewHolder(View itemView) {
            super(itemView);

            ivSport = itemView.findViewById(R.id.iv_sport);
            tvDescription = itemView.findViewById(R.id.tv_description);
            chart = itemView.findViewById(R.id.chart);
        }
    }
}
