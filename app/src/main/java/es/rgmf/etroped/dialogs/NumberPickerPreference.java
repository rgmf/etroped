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

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.util.AttributeSet;

import es.rgmf.etroped.R;

/**
 * This code is based on StreetComplete:
 * https://github.com/westnordost/StreetComplete
 *
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class NumberPickerPreference extends DialogPreferenceCompat
{
	private static final int DEFAULT_MIN_VALUE = 1;
	private static final int DEFAULT_MAX_VALUE = 100;

	private static final int DEFAULT_VALUE = 1;

	private int value;
	private int minValue;
	private int maxValue;

	private Context mContext;

	public NumberPickerPreference(Context context, AttributeSet attrs, int defStyleAttr,
                                  int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context, attrs);
	}

	public NumberPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	public NumberPickerPreference(Context context) {
		super(context);
		init(context, null);
	}

	@Override
    public PreferenceDialogFragmentCompat createDialog() {
		return new NumberPickerPreferenceDialog();
	}

	public NumberPickerPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {

	    mContext = context;

		setDialogLayoutResource(R.layout.dialog_number_picker_preference);

		final TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.NumberPickerPreference);

		minValue = a.getInt(R.styleable.NumberPickerPreference_minValue, DEFAULT_MIN_VALUE);
		maxValue = a.getInt(R.styleable.NumberPickerPreference_maxValue, DEFAULT_MAX_VALUE);

		a.recycle();
	}

	@Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {

		if (restorePersistedValue)
		    value = Integer.parseInt(
		            getPersistedString(mContext.getString(R.string.pref_accuracy_default))
            );
		else
		    value = (Integer) defaultValue;
	}

	@Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
		return a.getInteger(index, DEFAULT_VALUE);
	}

	@Override
    public CharSequence getSummary() {
		return String.format(super.getSummary().toString(), value);
	}

	public int getMinValue() {
		return minValue;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
		persistString(String.valueOf(value));
		notifyChanged();
	}
}
