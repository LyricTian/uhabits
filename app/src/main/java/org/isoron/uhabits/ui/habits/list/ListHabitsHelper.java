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

package org.isoron.uhabits.ui.habits.list;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import org.isoron.uhabits.R;
import org.isoron.uhabits.models.Habit;
import org.isoron.uhabits.utils.ColorUtils;
import org.isoron.uhabits.utils.DateUtils;
import org.isoron.uhabits.utils.InterfaceUtils;

public class ListHabitsHelper
{
    private static final int CHECKMARK_LEFT_TO_RIGHT = 0;
    private static final int CHECKMARK_RIGHT_TO_LEFT = 1;

    private final int lowContrastColor;
    private final int mediumContrastColor;

    private final Context context;
    private final HabitListLoader loader;

    public ListHabitsHelper(Context context, HabitListLoader loader)
    {
        this.context = context;
        this.loader = loader;

        lowContrastColor = InterfaceUtils.getStyledColor(context, R.attr.lowContrastTextColor);
        mediumContrastColor = InterfaceUtils.getStyledColor(context, R.attr.mediumContrastTextColor);
    }

    public int getButtonCount()
    {
        float screenWidth = InterfaceUtils.getScreenWidth(context);
        float labelWidth = context.getResources().getDimension(R.dimen.habitNameWidth);
        float buttonWidth = context.getResources().getDimension(R.dimen.checkmarkWidth);
        return Math.max(0, (int) ((screenWidth - labelWidth) / buttonWidth));
    }

    public void updateCheckmark(int activeColor, TextView tvCheck, int check)
    {
        switch (check)
        {
            case 2:
                tvCheck.setText(R.string.fa_check);
                tvCheck.setTextColor(activeColor);
                tvCheck.setTag(R.string.toggle_key, 2);
                break;

            case 1:
                tvCheck.setText(R.string.fa_check);
                tvCheck.setTextColor(lowContrastColor);
                tvCheck.setTag(R.string.toggle_key, 1);
                break;

            case 0:
                tvCheck.setText(R.string.fa_times);
                tvCheck.setTextColor(lowContrastColor);
                tvCheck.setTag(R.string.toggle_key, 0);
                break;
        }
    }

    public void updateEmptyMessage(View view)
    {
        if (loader.getLastLoadTimestamp() == null) view.setVisibility(View.GONE);
        else view.setVisibility(loader.habits.size() > 0 ? View.GONE : View.VISIBLE);
    }

    public void toggleCheckmarkView(View v, Habit habit)
    {
        int androidColor = ColorUtils.getColor(context, habit.color);

        if (v.getTag(R.string.toggle_key).equals(2))
            updateCheckmark(androidColor, (TextView) v, 0);
        else
            updateCheckmark(androidColor, (TextView) v, 2);
    }

    public Long getHabitIdFromCheckmarkView(View v)
    {
        return (Long) v.getTag(R.string.habit_key);
    }

    public long getTimestampFromCheckmarkView(View v)
    {
        Integer offset = (Integer) v.getTag(R.string.offset_key);
        return DateUtils.getStartOfDay(DateUtils.getLocalTime() -
                offset * DateUtils.millisecondsInOneDay);
    }

    public void triggerRipple(View v, final float x, final float y)
    {
        final Drawable background = v.getBackground();
        if (android.os.Build.VERSION.SDK_INT >= 21)
            background.setHotspot(x, y);

        background.setState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled});

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                background.setState(new int[]{});
            }
        }, 25);
    }

}
