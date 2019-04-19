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

package es.rgmf.etroped.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import es.rgmf.etroped.R;
import es.rgmf.etroped.util.EtropedDateTimeUtils;

/**
 * This code is based on StreetComplete:
 * https://github.com/westnordost/StreetComplete
 *
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class MonthYearPickerDialog extends AlertDialog implements DialogInterface
        .OnClickListener, MonthYearPickerDelegate.OnDateChangedListener {

    private static final String YEAR = "year";
    private static final String MONTH = "month";

    private MonthYearPickerDelegate mSimpleDatePickerDelegate;
    private OnDateSetListener mDateSetListener;

    /**
     * @param context The context the dialog is to run in.
     */
    public MonthYearPickerDialog(Context context, OnDateSetListener listener, int year,
                                  int monthOfYear) {
        this(context, 0, listener, year, monthOfYear);
    }

    /**
     * @param context The context the dialog is to run in.
     * @param theme   the theme to apply to this dialog
     */
    @SuppressLint("InflateParams")
    public MonthYearPickerDialog(Context context, int theme, OnDateSetListener listener, int year,
                                  int monthOfYear) {
        super(context, theme);

        mDateSetListener = listener;

        this.setTitle(EtropedDateTimeUtils.getMonthName(context, monthOfYear) + " - " + year);

        Context themeContext = getContext();
        LayoutInflater inflater = LayoutInflater.from(themeContext);
        View view = inflater.inflate(R.layout.dialog_month_year_picker, null);
        setView(view);
        setButton(BUTTON_POSITIVE, themeContext.getString(android.R.string.ok), this);
        setButton(BUTTON_NEGATIVE, themeContext.getString(android.R.string.cancel), this);

        mSimpleDatePickerDelegate = new MonthYearPickerDelegate(view);
        mSimpleDatePickerDelegate.init(year, monthOfYear, this);
    }

    @Override
    public void onDateChanged(int year, int month) {
        // Stub - do nothing
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_POSITIVE:
                if (mDateSetListener != null) {
                    mDateSetListener.onDateSet(
                            mSimpleDatePickerDelegate.getYear(),
                            mSimpleDatePickerDelegate.getMonth());
                }
                break;
            case BUTTON_NEGATIVE:
                cancel();
                break;
        }
    }

    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(YEAR, mSimpleDatePickerDelegate.getYear());
        state.putInt(MONTH, mSimpleDatePickerDelegate.getMonth());
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int year = savedInstanceState.getInt(YEAR);
        int month = savedInstanceState.getInt(MONTH);
        mSimpleDatePickerDelegate.init(year, month, this);
    }

    public void setMinDate(long minDate) {
        mSimpleDatePickerDelegate.setMinDate(minDate);
    }

    public void setMaxDate(long maxDate) {
        mSimpleDatePickerDelegate.setMaxDate(maxDate);
    }

    /**
     * The callback used to indicate the user is done filling in the date.
     */
    public interface OnDateSetListener {

        /**
         * @param year        The year that was set.
         * @param monthOfYear The month that was set (0-11) for compatibility with {@link
         *                    java.util.Calendar}.
         */
        void onDateSet(int year, int monthOfYear);
    }
}