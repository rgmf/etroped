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

package es.rgmf.etroped;

import android.content.Context;
import android.location.Location;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import es.rgmf.etroped.workout.ElevationTask;
import es.rgmf.etroped.workout.ElevationWorkout;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("es.rgmf.etroped", appContext.getPackageName());
    }

    @Test
    public void testElevationGain1() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();

        ElevationTask eleWorkout = new ElevationTask(appContext);

        eleWorkout.refresh(10);
        eleWorkout.refresh(11);
        eleWorkout.refresh(12);
        eleWorkout.refresh(13);
        eleWorkout.refresh(14);
        eleWorkout.refresh(15);
        eleWorkout.refresh(16);
        eleWorkout.refresh(17);
        eleWorkout.refresh(18);
        eleWorkout.refresh(19);
        eleWorkout.refresh(20);
        eleWorkout.refresh(21);
        eleWorkout.refresh(21);
        eleWorkout.refresh(21);
        eleWorkout.refresh(22);
        eleWorkout.refresh(22);
        eleWorkout.refresh(21);
        eleWorkout.refresh(22);
        eleWorkout.refresh(23);

        assertEquals(14, (int) eleWorkout.getGain());
        assertEquals(0, (int) eleWorkout.getLost());
        assertEquals(23, (int) eleWorkout.getMax());
        assertEquals(10, (int) eleWorkout.getMin());
    }

    @Test
    public void testElevationGain2() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();

        ElevationTask eleWorkout = new ElevationTask(appContext);

        eleWorkout.refresh(10);
        eleWorkout.refresh(11);
        eleWorkout.refresh(12);
        eleWorkout.refresh(13);
        eleWorkout.refresh(14);
        eleWorkout.refresh(15);
        eleWorkout.refresh(16);
        eleWorkout.refresh(17);
        eleWorkout.refresh(18);
        eleWorkout.refresh(19);
        eleWorkout.refresh(20);
        eleWorkout.refresh(21);
        eleWorkout.refresh(21);
        eleWorkout.refresh(21);
        eleWorkout.refresh(21);
        eleWorkout.refresh(21);
        eleWorkout.refresh(21);
        eleWorkout.refresh(22);
        eleWorkout.refresh(22);
        eleWorkout.refresh(21);
        eleWorkout.refresh(22);
        eleWorkout.refresh(23);

        assertEquals(11, (int) eleWorkout.getGain());
        assertEquals(0, (int) eleWorkout.getLost());
        assertEquals(23, (int) eleWorkout.getMax());
        assertEquals(10, (int) eleWorkout.getMin());
    }

    @Test
    public void testElevationGain3() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();

        ElevationTask eleWorkout = new ElevationTask(appContext);

        eleWorkout.refresh(10);
        eleWorkout.refresh(11);
        eleWorkout.refresh(12);
        eleWorkout.refresh(13);
        eleWorkout.refresh(14);
        eleWorkout.refresh(15);
        eleWorkout.refresh(16);
        eleWorkout.refresh(17);
        eleWorkout.refresh(18);
        eleWorkout.refresh(19);
        eleWorkout.refresh(20);
        eleWorkout.refresh(21);
        eleWorkout.refresh(21);
        eleWorkout.refresh(21);
        eleWorkout.refresh(21);
        eleWorkout.refresh(21);
        eleWorkout.refresh(21);
        eleWorkout.refresh(22);
        eleWorkout.refresh(22);
        eleWorkout.refresh(21);
        eleWorkout.refresh(22);
        eleWorkout.refresh(23);
        eleWorkout.refresh(24);
        eleWorkout.refresh(25);
        eleWorkout.refresh(26);
        eleWorkout.refresh(27);
        eleWorkout.refresh(27);
        eleWorkout.refresh(27);

        assertEquals(17, (int) eleWorkout.getGain());
        assertEquals(0, (int) eleWorkout.getLost());
        assertEquals(27, (int) eleWorkout.getMax());
        assertEquals(10, (int) eleWorkout.getMin());

    }

    @Test
    public void testElevationLost1() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();

        ElevationTask eleWorkout = new ElevationTask(appContext);

        eleWorkout.refresh(27);
        eleWorkout.refresh(27);
        eleWorkout.refresh(27);
        eleWorkout.refresh(25);
        eleWorkout.refresh(24);
        eleWorkout.refresh(23);
        eleWorkout.refresh(22);
        eleWorkout.refresh(21);
        eleWorkout.refresh(21);
        eleWorkout.refresh(21);
        eleWorkout.refresh(20);
        eleWorkout.refresh(19);
        eleWorkout.refresh(19);
        eleWorkout.refresh(19);
        eleWorkout.refresh(19);
        eleWorkout.refresh(19);
        eleWorkout.refresh(19);
        eleWorkout.refresh(19);
        eleWorkout.refresh(20);
        eleWorkout.refresh(21);
        eleWorkout.refresh(22);
        eleWorkout.refresh(22);
        eleWorkout.refresh(21);
        eleWorkout.refresh(20);
        eleWorkout.refresh(19);
        eleWorkout.refresh(18);
        eleWorkout.refresh(17);
        eleWorkout.refresh(16);

        assertEquals(0, (int) eleWorkout.getGain());
        assertEquals(14, (int) eleWorkout.getLost());
        assertEquals(27, (int) eleWorkout.getMax());
        assertEquals(16, (int) eleWorkout.getMin());
    }

    @Test
    public void testElevationGainAndLost() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();

        ElevationTask eleWorkout = new ElevationTask(appContext);

        eleWorkout.refresh(10);
        eleWorkout.refresh(11);
        eleWorkout.refresh(12);
        eleWorkout.refresh(12);
        eleWorkout.refresh(13);
        eleWorkout.refresh(14);
        eleWorkout.refresh(15);
        eleWorkout.refresh(16);
        eleWorkout.refresh(17);
        eleWorkout.refresh(18);
        eleWorkout.refresh(19);
        eleWorkout.refresh(19);
        eleWorkout.refresh(19);
        eleWorkout.refresh(19);
        eleWorkout.refresh(19);
        eleWorkout.refresh(19);
        eleWorkout.refresh(19);
        eleWorkout.refresh(20);
        eleWorkout.refresh(21);
        eleWorkout.refresh(22);
        eleWorkout.refresh(23);
        eleWorkout.refresh(24);
        eleWorkout.refresh(25);
        eleWorkout.refresh(26);
        eleWorkout.refresh(24);
        eleWorkout.refresh(23);
        eleWorkout.refresh(22);
        eleWorkout.refresh(21);
        eleWorkout.refresh(20);
        eleWorkout.refresh(19);
        eleWorkout.refresh(18);
        eleWorkout.refresh(17);

        assertEquals(16, (int) eleWorkout.getGain());
        assertEquals(9, (int) eleWorkout.getLost());
        assertEquals(26, (int) eleWorkout.getMax());
        assertEquals(10, (int) eleWorkout.getMin());

    }
}
