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
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.util.AttributeSet;

/**
 * This code is based on StreetComplete:
 * https://github.com/westnordost/StreetComplete
 *
 * @author Created by Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public abstract class DialogPreferenceCompat extends DialogPreference
{
	public DialogPreferenceCompat(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public DialogPreferenceCompat(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}

	public DialogPreferenceCompat(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public DialogPreferenceCompat(Context context)
	{
		super(context);
	}

	public abstract PreferenceDialogFragmentCompat createDialog();
}
