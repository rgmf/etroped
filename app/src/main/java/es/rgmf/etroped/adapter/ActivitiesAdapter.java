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

import es.rgmf.etroped.R;
import es.rgmf.etroped.adapter.viewlist.ActivityViewList;
import es.rgmf.etroped.adapter.viewlist.ViewTypeAbstract;
import es.rgmf.etroped.adapter.viewlist.ViewTypeActivity;
import es.rgmf.etroped.adapter.viewlist.ViewTypeWeekSummary;
import es.rgmf.etroped.data.entity.ActivityEntity;
import es.rgmf.etroped.util.EtropedDateTimeUtils;
import es.rgmf.etroped.util.EtropedPreferences;
import es.rgmf.etroped.util.EtropedUnitsUtils;

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class ActivitiesAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = ActivitiesAdapter.class.getSimpleName();

    private Context mContext;
    private ActivityViewList mViewList;
    final private ListItemClickListener mOnclickListener;

    /**
     * Interface to listen click on items.
     *
     * This interface contain a method that receive the id of the activity that is clicked.
     */
    public interface ListItemClickListener {
        void onListItemClick(int itemIdClicked);
    }

    public ActivitiesAdapter(Context context, ListItemClickListener listener) {
        mContext = context;
        mOnclickListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        View view;
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType) {
            case ViewTypeAbstract.TYPE_ACTIVITY:
                view = inflater.inflate(R.layout.activity_layout, parent, false);
                viewHolder = new ActivitiesAdapter.ActivityViewHolder(view);
                break;
            case ViewTypeAbstract.TYPE_WEEK_SUMMARY:
                view = inflater.inflate(R.layout.week_summary_layout, parent, false);
                viewHolder = new ActivitiesAdapter.WeekSummaryViewHolder(view);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case ViewTypeAbstract.TYPE_WEEK_SUMMARY:
                WeekSummaryViewHolder weekSummaryViewHolder = (WeekSummaryViewHolder) holder;
                ViewTypeWeekSummary viewTypeWeekSummary = (ViewTypeWeekSummary) mViewList.get(position);

                weekSummaryViewHolder.tvText.setText(viewTypeWeekSummary.getCaption());
                weekSummaryViewHolder.tvStartDate.setText(
                        EtropedDateTimeUtils.getCompressDay(
                                mContext,
                                viewTypeWeekSummary.getStartDate()
                        )
                );
                weekSummaryViewHolder.tvEndDate.setText(
                        EtropedDateTimeUtils.getCompressDay(
                                mContext,
                                viewTypeWeekSummary.getEndDate()
                        )
                );
                weekSummaryViewHolder.tvWalkingTime.setText(
                        EtropedDateTimeUtils.formatTimeHoursMinutes(
                                mContext,
                                viewTypeWeekSummary.getWalking().time
                        )
                );
                weekSummaryViewHolder.tvWalkingDistance.setText(
                        EtropedUnitsUtils.formatDistance(
                                mContext,
                                (long) viewTypeWeekSummary.getWalking().distance
                        )
                );
                weekSummaryViewHolder.tvRunningTime.setText(
                        EtropedDateTimeUtils.formatTimeHoursMinutes(
                                mContext,
                                viewTypeWeekSummary.getRunning().time
                        )
                );
                weekSummaryViewHolder.tvRunningDistance.setText(
                        EtropedUnitsUtils.formatDistance(
                                mContext,
                                (long) viewTypeWeekSummary.getRunning().distance
                        )
                );
                weekSummaryViewHolder.tvCyclingTime.setText(
                        EtropedDateTimeUtils.formatTimeHoursMinutes(
                                mContext,
                                viewTypeWeekSummary.getCycling().time
                        )
                );
                weekSummaryViewHolder.tvCyclingDistance.setText(
                        EtropedUnitsUtils.formatDistance(
                                mContext,
                                (long) viewTypeWeekSummary.getCycling().distance
                        )
                );
                break;
            case ViewTypeAbstract.TYPE_ACTIVITY:
                ActivityViewHolder activityViewHolder = (ActivityViewHolder) holder;
                ViewTypeActivity viewType = (ViewTypeActivity) mViewList.get(position);
                ActivityEntity activityEntity = viewType.getActivity();

                // Set values.
                activityViewHolder.itemView.setTag(activityEntity.getId());
                activityViewHolder.tvName.setText(activityEntity.getName());
                activityViewHolder.tvTime.setText(
                        EtropedDateTimeUtils.formatTime(
                                mContext,
                                activityEntity.getTime(),
                                activityEntity.getFkSport()
                        )
                );
                activityViewHolder.tvDistance.setText(
                        EtropedUnitsUtils.formatDistance(
                                mContext,
                                (long) activityEntity.getDistance()
                        )
                );
                activityViewHolder.ivSportIcon.setImageResource(
                        EtropedPreferences.getSportDrawableById(
                                activityEntity.getFkSport()
                        )
                );
                ImageViewCompat.setImageTintList(
                        activityViewHolder.ivSportIcon,
                        ColorStateList.valueOf(
                                ContextCompat.getColor(
                                        mContext,
                                        EtropedPreferences.getSportColor(
                                                activityEntity.getFkSport()
                                        )
                                )
                        )
                );
                activityViewHolder.tvDate.setText(
                        EtropedDateTimeUtils.getFriendlyDateTimeString(
                                mContext,
                                (long) activityEntity.getStartTime()
                        )
                );
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
    public ActivityViewList swapData(ActivityViewList data) {

        if (mViewList == data)
            return null;

        //ActivityViewList temp = mViewList;
        this.mViewList = data;

        if (data != null)
            this.notifyDataSetChanged();

        return data;
    }

    /**
     * Inner class for creating ActivityViewHolder.
     */
    public class ActivityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivSportIcon;
        TextView tvName;
        TextView tvDistance;
        TextView tvTime;
        TextView tvDate;

        public ActivityViewHolder(View itemView) {
            super(itemView);

            ivSportIcon = (ImageView) itemView.findViewById(R.id.iv_sport_icon);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvDistance = (TextView) itemView.findViewById(R.id.tv_distance);
            tvTime = (TextView) itemView.findViewById(R.id.tv_walking_time);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);

            // Set on click listener to this item view.
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int itemIdClicked = (int) view.getTag();
            mOnclickListener.onListItemClick(itemIdClicked);
        }
    }

    /**
     * Inner class for creating ActivityViewHolder.
     */
    public class WeekSummaryViewHolder extends RecyclerView.ViewHolder {
        TextView tvText;
        TextView tvStartDate;
        TextView tvEndDate;
        TextView tvWalkingTime;
        TextView tvWalkingDistance;
        TextView tvRunningTime;
        TextView tvRunningDistance;
        TextView tvCyclingTime;
        TextView tvCyclingDistance;

        public WeekSummaryViewHolder(View itemView) {
            super(itemView);

            tvText = (TextView) itemView.findViewById(R.id.tv_text);
            tvStartDate = (TextView) itemView.findViewById(R.id.tv_start_date);
            tvEndDate = (TextView) itemView.findViewById(R.id.tv_end_date);
            tvWalkingTime = (TextView) itemView.findViewById(R.id.tv_walking_time);
            tvWalkingDistance = (TextView) itemView.findViewById(R.id.tv_walking_distance);
            tvRunningTime = (TextView) itemView.findViewById(R.id.tv_running_time);
            tvRunningDistance = (TextView) itemView.findViewById(R.id.tv_running_distance);
            tvCyclingTime = (TextView) itemView.findViewById(R.id.tv_cycling_time);
            tvCyclingDistance = (TextView) itemView.findViewById(R.id.tv_cycling_distance);
        }
    }
}
