/*
 * Copyright (C) 2016 Álinson Santos Xavier <isoron@gmail.com>
 *
 * This file is part of Loop Habit Tracker.
 *
 * Loop Habit Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Loop Habit Tracker is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.isoron.uhabits.unit.ui.habits.list.view;

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;

import org.isoron.uhabits.models.Checkmark;
import org.isoron.uhabits.ui.habits.list.views.CheckmarkPanelView;
import org.isoron.uhabits.unit.views.ViewTest;
import org.isoron.uhabits.utils.ColorUtils;
import org.isoron.uhabits.utils.DateUtils;
import org.isoron.uhabits.utils.Preferences;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class CheckmarkPanelViewTest extends ViewTest
{
    public static final String PATH = "ui/habits/list/CheckmarkPanelView/";

    private CountDownLatch latch;
    private CheckmarkPanelView view;
    private int checkmarks[];

    @Before
    public void setup()
    {
        super.setup();
        setSimilarityCutoff(0.03f);
        Preferences.getInstance().setShouldReverseCheckmarks(false);

        latch = new CountDownLatch(1);
        checkmarks = new int[]{Checkmark.CHECKED_EXPLICITLY, Checkmark.UNCHECKED,
                Checkmark.CHECKED_IMPLICITLY, Checkmark.CHECKED_EXPLICITLY};

        view = new CheckmarkPanelView(targetContext);
        view.setCheckmarkValues(checkmarks);
        view.setColor(ColorUtils.CSV_PALETTE[7]);

        measureView(dpToPixels(200), dpToPixels(200), view);
    }

    protected void waitForLatch() throws InterruptedException
    {
        assertTrue("Latch timeout", latch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void testRender() throws Exception
    {
        assertRenders(view, PATH + "render.png");
    }

    @Test
    public void testToggleCheckmark_withLeftToRight() throws Exception
    {
        setToggleListener();
        view.getButton(1).performToggle();
        waitForLatch();
    }

    @Test
    public void testToggleCheckmark_withReverseCheckmarks() throws Exception
    {
        Preferences.getInstance().setShouldReverseCheckmarks(true);
        view.setCheckmarkValues(checkmarks); // refresh after preference change

        setToggleListener();
        view.getButton(2).performToggle();
        waitForLatch();
    }

    @Test
    public void testInvalidToggle() throws Exception
    {
        setInvalidToggleListener();
        view.getButton(0).performInvalidToggle();
        waitForLatch();
    }

    protected void setToggleListener()
    {
        view.setListener(new CheckmarkPanelView.Listener()
        {
            @Override
            public void onToggleCheckmark(CheckmarkPanelView v, long timestamp)
            {
                long day = DateUtils.millisecondsInOneDay;
                long yesterday = DateUtils.getStartOfToday() - day;

                assertThat(v, equalTo(view));
                assertThat(timestamp, equalTo(yesterday));
                latch.countDown();
            }

            @Override
            public void onInvalidToggle(CheckmarkPanelView view)
            {
                fail();
            }
        });
    }

    protected void setInvalidToggleListener()
    {
        view.setListener(new CheckmarkPanelView.Listener()
        {
            @Override
            public void onToggleCheckmark(CheckmarkPanelView v, long timestamp)
            {
                fail();
            }

            @Override
            public void onInvalidToggle(CheckmarkPanelView v)
            {
                assertThat(v, equalTo(view));
                latch.countDown();
            }
        });
    }
}