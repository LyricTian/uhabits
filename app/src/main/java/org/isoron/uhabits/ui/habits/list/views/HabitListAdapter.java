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
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.isoron.uhabits.models.Habit;
import org.isoron.uhabits.ui.habits.list.HabitListLoader;

import java.util.List;

class HabitListAdapter extends BaseAdapter
{
    private final Context context;
    private HabitListLoader loader;
    private List selectedPositions;
    private HabitCardView.Listener listener;

    public HabitListAdapter(Context context, HabitListLoader loader)
    {
        this.loader = loader;
        this.context = context;
    }

    @Override
    public int getCount()
    {
        return loader.habits.size();
    }

    @Override
    public Habit getItem(int position)
    {
        return loader.habitsList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return (getItem(position)).getId();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        final Habit habit = loader.habitsList.get(position);
        boolean selected = selectedPositions.contains(position);
        HabitCardView cardView = (HabitCardView) view;

        // TODO: Update when day changes at midnight
        if (cardView == null)
        {
            cardView = new HabitCardView(context);
            cardView.setListener(listener);
        }

        int score = loader.scores.get(habit.getId());
        int checkmarks[] = loader.checkmarks.get(habit.getId());

        cardView.setHabit(habit);
        cardView.setSelected(selected);
        cardView.setCheckmarkValues(checkmarks);
        cardView.setScore(score);
        return cardView;
    }

    public void setSelectedPositions(List selectedPositions)
    {
        this.selectedPositions = selectedPositions;
    }

    public void setHabitCardListener(HabitCardView.Listener listener)
    {
        this.listener = listener;
    }
}
