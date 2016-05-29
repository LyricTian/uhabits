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
import android.graphics.Canvas;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.isoron.uhabits.R;
import org.isoron.uhabits.models.Checkmark;
import org.isoron.uhabits.utils.InterfaceUtils;
import org.isoron.uhabits.utils.Preferences;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CheckmarkButtonView extends FrameLayout implements View.OnLongClickListener,
        View.OnClickListener
{
    private Listener listener;

        private long timestamp;
    private int value;

    @BindView(R.id.tvCheck) TextView tvCheck;
    private int color;

    public CheckmarkButtonView(Context context)
    {
        super(context);
        init();
    }

    private void init()
    {
        addView(inflate(getContext(), R.layout.list_habits_item_check, null));
        ButterKnife.bind(this);

        setOnLongClickListener(this);
        setOnClickListener(this);
        setWillNotDraw(false);
        setHapticFeedbackEnabled(false);

        tvCheck.setTypeface(InterfaceUtils.getFontAwesome(getContext()));
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        int lowContrastColor = InterfaceUtils.getStyledColor(getContext(),
                R.attr.lowContrastTextColor);

        if (value == Checkmark.CHECKED_EXPLICITLY)
        {
            tvCheck.setText(R.string.fa_check);
            tvCheck.setTextColor(color);
        }

        if (value == Checkmark.CHECKED_IMPLICITLY)
        {
            tvCheck.setText(R.string.fa_check);
            tvCheck.setTextColor(lowContrastColor);
        }

        if (value == Checkmark.UNCHECKED)
        {
            tvCheck.setText(R.string.fa_times);
            tvCheck.setTextColor(lowContrastColor);
        }

        super.onDraw(canvas);
    }

    public void setListener(Listener listener)
    {
        this.listener = listener;
    }

    public void setColor(int color)
    {
        this.color = color;
        postInvalidate();
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    public void setValue(int value)
    {
        this.value = value;
        postInvalidate();
    }

    public void performToggle()
    {
        if (value == Checkmark.CHECKED_EXPLICITLY) setValue(Checkmark.UNCHECKED);
        else setValue(Checkmark.CHECKED_EXPLICITLY);

        if(listener != null) listener.onToggleCheckmark(this, timestamp);
    }

    public void performInvalidToggle()
    {
        if(listener != null) listener.onInvalidToggle(this);
    }

    @Override
    public boolean onLongClick(View v)
    {
        performToggle();
        return true;
    }

    @Override
    public void onClick(View v)
    {
        if (Preferences.getInstance().isShortToggleEnabled()) performToggle();
        else performInvalidToggle();
    }

    public interface Listener
    {
        void onToggleCheckmark(CheckmarkButtonView view, long timestamp);

        void onInvalidToggle(CheckmarkButtonView view);
    }
}
