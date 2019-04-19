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

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

public class MonthYearPickerDialogFragment extends DialogFragment {
    public static final int NULL_INT = -1;
    private static final String ARG_MONTH = "month";
    private static final String ARG_YEAR = "year";
    private static final String ARG_MIN_DATE = "min_date";
    private static final String ARG_MAX_DATE = "max_date";

    private MonthYearPickerDialog.OnDateSetListener mOnDateSetListener;

    public MonthYearPickerDialogFragment() {
        // Do nothing
    }

    /**
     * Create a new instance of the DialogFragment
     *
     * @param year  the initial year
     * @param month the initial month
     * @return the fragment instance
     */
    public static MonthYearPickerDialogFragment getInstance(int year, int month) {
        return getInstance(year, month, NULL_INT, NULL_INT);
    }

    /**
     * Create a new instance of the DialogFragment
     *
     * @param year    the initial year
     * @param month   the initial month
     * @param minDate set the min date in milliseconds which should be less then initial date set.
     * @param maxDate set the max date in milliseconds which should not be less then current date.
     * @return
     */
    public static MonthYearPickerDialogFragment getInstance(int year, int month, long minDate,
                                                             long maxDate) {
        MonthYearPickerDialogFragment datePickerDialogFragment = new
                MonthYearPickerDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_MONTH, month);
        bundle.putInt(ARG_YEAR, year);
        bundle.putLong(ARG_MIN_DATE, minDate);
        bundle.putLong(ARG_MAX_DATE, maxDate);
        datePickerDialogFragment.setArguments(bundle);
        return datePickerDialogFragment;
    }

    /**
     * Get callback of the year and month selected.
     *
     * @param onDateSetListener
     */
    public void setOnDateSetListener(MonthYearPickerDialog.OnDateSetListener onDateSetListener) {
        mOnDateSetListener = onDateSetListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle dataBundle = getArguments();
        int year = dataBundle.getInt(ARG_YEAR);
        int month = dataBundle.getInt(ARG_MONTH);
        long minDate = dataBundle.getLong(ARG_MIN_DATE);
        long maxDate = dataBundle.getLong(ARG_MAX_DATE);
        checkForValidMinDate(year, month, minDate);
        checkForValidMaxDate(year, month, maxDate);
        MonthYearPickerDialog simpleDatePickerDialog = new MonthYearPickerDialog(
                getActivity(), mOnDateSetListener, year, month);
        if (minDate != NULL_INT) {
            simpleDatePickerDialog.setMinDate(minDate);
        }
        if (maxDate != NULL_INT) {
            simpleDatePickerDialog.setMaxDate(maxDate);
        }
        return simpleDatePickerDialog;
    }

    private void checkForValidMinDate(int year, int month, long minDate) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, year);
        c.add(Calendar.MONTH, month);
        if (c.getTimeInMillis() < minDate) {
            throw new IllegalArgumentException("The min date should be less than initial date set");
        }
    }

    private void checkForValidMaxDate(int year, int month, long maxDate) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.YEAR, year);
        c.add(Calendar.MONTH, month);
        if (c.getTimeInMillis() < maxDate) {
            throw new IllegalArgumentException(
                    "The max date should not be less than current date.");
        }
    }
}
