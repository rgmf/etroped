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

import android.graphics.Color;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;


import java.util.ArrayList;

import es.rgmf.etroped.charts.ValueFormatter;
import es.rgmf.etroped.charts.XAxisValueFormatter;

public class ViewTypeBarChart extends ViewTypeAbstract {
    // The sport id for the barchar.
    private int mSportId;

    // Description.
    private String mDescription;

    // Labels to be added on the horizontal axis.
    private String[] mLabels;

    // Data values.
    private Float[] mValues;

    // Average data for linechart.
    private Float[] mAverages;

    // Color for bars.
    private int mBarsColor;

    public static ViewTypeBarChart newInstance(
            int sportId,
            String desc,
            String[] labels,
            Float[] values,
            Float[] averages,
            int barColor
    ) {
        ViewTypeBarChart i = new ViewTypeBarChart();
        i.mSportId = sportId;
        i.mDescription = desc;
        i.mLabels = labels;
        i.mValues = values;
        i.mAverages = averages;
        i.mBarsColor = barColor;
        return i;
    }

    @Override
    public int getViewType() {
        return TYPE_BAR_CHART;
    }

    public int getSportId() {
        return mSportId;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setChart(CombinedChart chart) {

        // Description empty.
        Description description = new Description();
        description.setText("");
        chart.setDescription(description);

        // No legend.
        chart.getLegend().setEnabled(false);

        // Disable zoom.
        chart.setPinchZoom(false);
        chart.setScaleEnabled(false);

        // Other options.
        chart.setBackgroundColor(Color.WHITE);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setHighlightFullBarEnabled(false);

        // Draw bars behind lines.
        chart.setDrawOrder(new CombinedChart.DrawOrder[] {
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE
        });

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);
        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setEnabled(false);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)


        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(false); // the horizontal line marking the x axis
        xAxis.setDrawGridLines(false); // vertical lines from each label
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        //xAxis.setValueFormatter(new XAxisValueFormatter(mLabels));
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return mLabels[(int) value % mLabels.length];
            }
        });

        CombinedData data = new CombinedData();
        data.setData(generateLineData());
        data.setData(generateBarData());

        xAxis.setAxisMaximum(data.getXMax() + 0.25f);

        chart.setData(data);
        chart.invalidate();

        chart.animateY(2000);
    }

    private LineData generateLineData() {
        LineData d = new LineData();
        ArrayList<Entry> entries = new ArrayList<>();

        for (int i = 0; i < mAverages.length; i++)
            entries.add(new Entry(i, mAverages[i]));

        LineDataSet set = new LineDataSet(entries, "Line DataSet");
        set.setColor(Color.rgb(0, 0, 0));
        //set.setLineWidth(2.5f);
        set.setCircleColor(Color.rgb(0, 0, 0));
        //set.setCircleRadius(5f);
        set.setFillColor(Color.rgb(0, 0, 0));
        //set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawValues(true);
        //set.setValueTextSize(10f);
        set.setValueTextColor(Color.rgb(0, 0, 0));

        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        d.addDataSet(set);

        return d;
    }

    private BarData generateBarData() {
        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < mValues.length; i++)
            entries.add(new BarEntry(i, mValues[i]));

        BarDataSet set = new BarDataSet(entries, "Bar");
        set.setColor(mBarsColor);
        //set.setValueTextColor(Color.rgb(60, 220, 78));
        //set.setValueTextSize(15f);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        BarData d = new BarData(set);
        d.setBarWidth(0.5f);

        return d;
    }
}
