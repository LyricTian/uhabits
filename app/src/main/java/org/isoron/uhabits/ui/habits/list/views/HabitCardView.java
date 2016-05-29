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

package org.isoron.uhabits.ui.habits.list.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.isoron.uhabits.R;
import org.isoron.uhabits.models.Habit;
import org.isoron.uhabits.models.Score;
import org.isoron.uhabits.utils.ColorUtils;
import org.isoron.uhabits.utils.InterfaceUtils;
import org.isoron.uhabits.views.RingView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.isoron.uhabits.utils.InterfaceUtils.getStyledDrawable;

public class HabitCardView extends LinearLayout implements CheckmarkPanelView.Listener
{
    @BindView(R.id.checkmarkPanel) CheckmarkPanelView checkmarkPanel;
    @BindView(R.id.innerFrame) LinearLayout innerFrame;
    @BindView(R.id.label) TextView label;
    @BindView(R.id.scoreRing) RingView scoreRing;

    private Listener listener;
    private Habit habit;
    private int activeColor;

    public HabitCardView(Context context)
    {
        super(context);
        init();
    }

    public HabitCardView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public HabitCardView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.list_habits_item, this);
        ButterKnife.bind(this);

        checkmarkPanel.setListener(this);
        innerFrame.setOnTouchListener(new HotspotTouchListener());

        if(isInEditMode()) initEditMode();
    }

    private void initEditMode()
    {
        int color = ColorUtils.CSV_PALETTE[0];
        int[] values = { 2, 0, 1, 2 };

        label.setText("Wake up early");
        label.setTextColor(color);
        scoreRing.setColor(color);
        scoreRing.setPercentage(0.60f);
        checkmarkPanel.setColor(color);
        checkmarkPanel.setCheckmarkValues(values);
    }

    @Override
    public void setSelected(boolean isSelected)
    {
        updateBackground(isSelected);
    }

    private void updateBackground(boolean isSelected)
    {
        if (android.os.Build.VERSION.SDK_INT >= 21)
        {
            if (isSelected) innerFrame.setBackgroundResource(R.drawable.selected_box);
            else innerFrame.setBackgroundResource(R.drawable.ripple);
        }
        else
        {
            Drawable background;

            if (isSelected) background = getStyledDrawable(getContext(), R.attr.selectedBackground);
            else background = getStyledDrawable(getContext(), R.attr.cardBackground);

            innerFrame.setBackgroundDrawable(background);
        }
    }

    public void setHabit(Habit habit)
    {
        this.habit = habit;
        this.activeColor = getActiveColor(habit);

        label.setText(habit.name);
        label.setTextColor(activeColor);
        scoreRing.setColor(activeColor);
        checkmarkPanel.setColor(activeColor);

        postInvalidate();
    }

    public void setCheckmarkValues(int checkmarks[])
    {
        checkmarkPanel.setCheckmarkValues(checkmarks);
        postInvalidate();
    }

    public void setScore(int score)
    {
        float percentage = (float) score / Score.MAX_VALUE;
        scoreRing.setPercentage(percentage);
        scoreRing.setPrecision(1.0f / 16);
        postInvalidate();
    }

    private int getActiveColor(Habit habit)
    {
        int mediumContrastColor = InterfaceUtils.getStyledColor(getContext(),
                R.attr.mediumContrastTextColor);
        int activeColor = ColorUtils.getColor(getContext(), habit.color);
        if(habit.isArchived()) activeColor = mediumContrastColor;

        return activeColor;
    }

    public void setListener(Listener listener)
    {
        this.listener = listener;
    }

    @Override
    public void onToggleCheckmark(CheckmarkPanelView view, long timestamp)
    {
        triggerRipple(getWidth() / 2, getHeight() / 2);
        if(listener != null) listener.onToggleCheckmark(this, habit, timestamp);
    }

    private void triggerRipple(final float x, final float y)
    {
        final Drawable background = innerFrame.getBackground();
        if (android.os.Build.VERSION.SDK_INT >= 21) background.setHotspot(x, y);
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

    @Override
    public void onInvalidToggle(CheckmarkPanelView view)
    {
        if(listener != null) listener.onInvalidToggle(this);
    }

    private static class HotspotTouchListener implements View.OnTouchListener
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            if (android.os.Build.VERSION.SDK_INT >= 21)
                v.getBackground().setHotspot(event.getX(), event.getY());
            return false;
        }
    }

    interface Listener
    {
        void onToggleCheckmark(HabitCardView view, Habit habit, long timestamp);

        void onInvalidToggle(HabitCardView view);
    }
}
