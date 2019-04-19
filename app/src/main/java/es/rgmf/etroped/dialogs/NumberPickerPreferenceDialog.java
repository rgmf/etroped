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

import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.View;
import android.widget.NumberPicker;

import es.rgmf.etroped.R;

/**
 * This code is based on StreetComplete:
 * https://github.com/westnordost/StreetComplete
 *
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class NumberPickerPreferenceDialog extends PreferenceDialogFragmentCompat
{
	private NumberPicker picker;

	@Override protected void onBindDialogView(View view) {
		super.onBindDialogView(view);
		picker = view.findViewById(R.id.number_picker);
		picker.setMinValue(getNumberPickerPreference().getMinValue());
		picker.setMaxValue(getNumberPickerPreference().getMaxValue());
		picker.setValue(getNumberPickerPreference().getValue());
		picker.setWrapSelectorWheel(false);
	}

	@Override public void onDialogClosed(boolean positiveResult)
	{
		// hackfix: The Android number picker accepts input via soft keyboard (which makes sense
		// from a UX viewpoint) but is not designed for that. By default, it does not apply the
		// input there. See http://stackoverflow.com/questions/18944997/numberpicker-doesnt-work-with-keyboard
		// A workaround is to clear the focus before saving.
		picker.clearFocus();

		if(positiveResult)
		{
			getNumberPickerPreference().setValue(picker.getValue());
		}
	}

	private NumberPickerPreference getNumberPickerPreference()
	{
		return (NumberPickerPreference) getPreference();
	}
}
