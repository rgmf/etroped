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

package es.rgmf.etroped.helper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.Utils;

import es.rgmf.etroped.R;

/**
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public final class ChartsHelper {

    public static void paceChartConfig(LineChart chart, float min, float max) {

        XAxis xaxis = chart.getXAxis();
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xaxis.setDrawAxisLine(false);

        YAxis yleft = chart.getAxisLeft();
        yleft.setDrawAxisLine(false);

        if (max < 5)
            yleft.setAxisMaximum(10);
        else if (max < 10)
            yleft.setAxisMaximum(20);
        else if (max < 15)
            yleft.setAxisMaximum(30);
        else if (max < 20)
            yleft.setAxisMaximum(50);
        else
            yleft.setAxisMaximum(max + 20);

        if (min < 10)
            yleft.setAxisMinimum(-10);
        else
            yleft.setAxisMinimum(0);

        YAxis yright = chart.getAxisRight();
        yright.setDrawLabels(false);
        yright.setDrawAxisLine(false);
    }

    public static void speedChartConfig(LineChart chart, float min, float max) {

        XAxis xaxis = chart.getXAxis();
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xaxis.setDrawAxisLine(false);

        YAxis yleft = chart.getAxisLeft();
        yleft.setDrawAxisLine(false);

        if (max < 10)
            yleft.setAxisMaximum(30);
        else if (max < 20)
            yleft.setAxisMaximum(40);
        else if (max < 40)
            yleft.setAxisMaximum(60);
        else if (max < 60)
            yleft.setAxisMaximum(80);
        else
            yleft.setAxisMaximum(max + 100);

        if (min < 10)
            yleft.setAxisMinimum(-10);
        else
            yleft.setAxisMinimum(0);

        YAxis yright = chart.getAxisRight();
        yright.setDrawLabels(false);
        yright.setDrawAxisLine(false);
    }

    public static void altitudeChartConfig(LineChart chart, float min, float max) {

        XAxis xaxis = chart.getXAxis();
        xaxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xaxis.setDrawAxisLine(false);

        YAxis yleft = chart.getAxisLeft();
        yleft.setDrawAxisLine(false);

        if (max < 100)
            yleft.setAxisMaximum(300);
        else if (max < 500)
            yleft.setAxisMaximum(700);
        else if (max < 800)
            yleft.setAxisMaximum(1000);
        else if (max < 1000)
            yleft.setAxisMaximum(1200);
        else
            yleft.setAxisMaximum(max + 100);

        if (min > 1000)
            yleft.setAxisMinimum(800);
        else if (min > 500)
            yleft.setAxisMinimum(300);
        else if (min > 100)
            yleft.setAxisMinimum(0);
        else if (min > 0)
            yleft.setAxisMinimum(-50);
        else if (min > -10)
            yleft.setAxisMinimum(-100);
        else if (min > -50)
            yleft.setAxisMinimum(-200);
        else
            yleft.setAxisMinimum(-400);

        YAxis yright = chart.getAxisRight();
        yright.setDrawLabels(false);
        yright.setDrawAxisLine(false);
    }

    public static void lineDataSetConfig(Context context, LineDataSet lds) {
        lds.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        lds.setDrawFilled(true);
        if (Utils.getSDKInt() >= 18) {
            // fill drawable only supported on api level 18 and above
            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.fade_red);
            lds.setFillDrawable(drawable);
        } else {
            lds.setFillColor(android.R.color.black);
        }

        lds.setLineWidth(1.75f);
        lds.setColor(context.getResources().getColor(R.color.colorAccent));
        lds.setHighLightColor(context.getResources().getColor(R.color.colorAccent));
        lds.setDrawValues(false);
        lds.setDrawCircles(false);
    }
}
