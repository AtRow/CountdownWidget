/*
* Copyright 2011 Lauri Nevala.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.ovio.countdown.calendar;

import android.content.Context;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.ovio.countdown.R;

import java.util.Map;


public class CalendarView extends FrameLayout {

    //static final int FIRST_DAY_OF_WEEK = Time.SUNDAY;
    static final int FIRST_DAY_OF_WEEK = Time.MONDAY;

    private static final int ROWS = 6;
    private static final int WEEK = 7;

    private Time month;
    private Time selected;

    private Map<Integer, DayInfo> items; // container to store some random calendar items
    private int offset;
    private DayTile[] dayTiles;
    private DayTile selectedDayTile;
    private LinearLayout container;


    public CalendarView(Context context) {
        super(context);
        init();
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        li.inflate(R.layout.calendar_table, this, true);

        container = (LinearLayout) findViewById(R.id.container);
    }


    public Time getMonth() {
        return month;
    }

    public Time getDate() {
        return selected;
    }

    public void setMonth(Time time) {
        month = new Time(time);
        month.hour = 0;
        month.minute = 0;
        month.second = 0;
        month.monthDay = 1;

        String day = month.format("%Y %m");
        Log.d("SCV", "Updated calendar to: " + day);

        update();
    }

    public void setDate(Time time) {
        selected = new Time(time);

        if (month == null) {
            setMonth(time);
        }

        String day = selected.format("%Y %m %d");
        Log.d("SCV", "Set selected day to: " + day);

        update();
    }

    private void update() {
        if (month != null) {
            refreshDays();
            fillTable();
        }
    }


    public void offsetMonth(int offset) {

        if (month != null) {
            month.month += offset;
            month.normalize(true);
            setMonth(month);
        }
    }

    private void fillTable() {
        int i = 0;

        for (int r = 0; r < ROWS; r++) {

            LinearLayout row = (LinearLayout) container.getChildAt(r);

            for (int c = 0; c < WEEK; c++) {

                ViewGroup day = (ViewGroup) row.getChildAt(c);
                renderDayTileView(day, i);

                i++;
            }
        }
    }

    public void setItems(Map<Integer, DayInfo> items) {

        for (int day : items.keySet()) {
            int i = day - 1;
            if ((i >=0) && (dayTiles != null) && (i < dayTiles.length)) {

                dayTiles[i].dayInfo = items.get(day);
                dayTiles[i].render();
            }
        }
    }


    private DayTile.OnClickListener onTileClickListener = new DayTile.OnClickListener() {
        @Override
        public void onClick(DayTile dayTile) {

            selectTile(dayTile);

            selected = new Time(month);
            selected.monthDay = dayTile.getMonthDay();

//            String msg = "Selected: " + selected.format("%Y %m %d");
//            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }
    };

    private void selectTile(DayTile dayTile) {
        if (selectedDayTile != null) {
            selectedDayTile.setSelected(false);
        }
        dayTile.setSelected(true);
        selectedDayTile = dayTile;
    }


    public View renderDayTileView(ViewGroup tileView, int position) {

        if ((position >= offset) && (position - offset) < dayTiles.length) {

            DayTile tile = dayTiles[position - offset];

            tile.setOnClickListener(onTileClickListener);
            tile.renderTo(tileView);

            // don't forget to show view
            tileView.setVisibility(View.VISIBLE);

        } else {
            // disable empty days from the beginning
            tileView.setVisibility(View.INVISIBLE);
        }

        return tileView;
    }

    public void refreshDays() {

        Time counter = new Time(month);

        counter.monthDay = 1;
        counter.normalize(true);

        int weekDayOn1st = counter.weekDay;
        int daysInMonth = counter.getActualMaximum(Time.MONTH_DAY);

        offset = weekDayOn1st - FIRST_DAY_OF_WEEK;
        if (offset < 0) {
            offset += 7;
        }

        dayTiles = new DayTile[daysInMonth];

        for (int i = 0; i < daysInMonth; i++) {
            dayTiles[i] = new DayTile(counter);

            if ((selected != null) &&
                    (counter.year == selected.year) &&
                    (counter.month == selected.month) &&
                    (counter.monthDay == selected.monthDay)) {
                dayTiles[i].isCurrentDay = true;
            } else {
                dayTiles[i].isCurrentDay = false;
            }
            counter.monthDay++;
        }
    }
}
