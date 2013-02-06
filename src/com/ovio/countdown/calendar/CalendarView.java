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
import android.graphics.Rect;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.Toast;
import com.ovio.countdown.R;

import java.util.Map;


public class CalendarView extends FrameLayout {

    //static final int FIRST_DAY_OF_WEEK = Time.SUNDAY;
    static final int FIRST_DAY_OF_WEEK = Time.MONDAY;

    private static final int ROWS = 6;
    private static final int WEEK = 7;
    private static final int CELLS = WEEK * ROWS;

    private Time month;
    private Time selected;

    private Map<Integer, DayInfo> items; // gridView to store some random calendar items
    private DayTile[] dayTiles;

    private DayTile selectedDayTile;
    private GridView gridView;
    private FrameLayout rootLayout;
    private DayTileAdapter dayTileArrayAdapter;

    private OnDateSelectedListener onDateSelectedListener;


    public CalendarView(Context context) {
        super(context);
        init();
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setOnDateSelectedListener(OnDateSelectedListener onDateSelectedListener) {
        this.onDateSelectedListener = onDateSelectedListener;
    }

    public interface OnDateSelectedListener {
        void onDateSelected(Time date);
    }

    private void init() {
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        li.inflate(R.layout.calendar_table, this, true);

        gridView = (GridView) findViewById(R.id.gridView);
        rootLayout = (FrameLayout) findViewById(R.id.rootLayout);

        dayTiles = new DayTile[CELLS];
        for (int i = 0; i < CELLS; i++) {
            dayTiles[i] = (DayTile) li.inflate(R.layout.calendar_item, null);
        }

        dayTileArrayAdapter = new DayTileAdapter(getContext(), dayTiles);
        gridView.setAdapter(dayTileArrayAdapter);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int tileHeight = getTileHeight();

        for (int i = 0; i < CELLS; i++) {
            dayTiles[i].setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.WRAP_CONTENT, tileHeight));
        }
    }

    private void updateTiles(Time currentMonth) {

        int offset = getOffset(currentMonth);

        Time now = new Time();
        now.setToNow();

        Time counter = new Time(currentMonth);
        counter.monthDay = 1;
        counter.monthDay -= offset;
        counter.normalize(true);

        for (int i = 0; i < CELLS; i++) {
            dayTiles[i].setCurrentMonth(month);
            dayTiles[i].setOnClickListener(onTileClickListener);

            counter.normalize(true);
            dayTiles[i].setTileDate(counter);
            counter.monthDay++;

            dayTiles[i].update();
        }
    }

    private int getTileHeight() {
        Rect rect = new Rect();
        rootLayout.getDrawingRect(rect);

        return rootLayout.getMeasuredHeight() / 7;

        //return rect.height() / 7;

//        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
//        Display display = wm.getDefaultDisplay();
//        int height = display.getHeight();  // deprecated
//
//        return height / 7;
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
        selectCurrentTile();
    }

    private void selectCurrentTile() {

        for (int i = 0; i < CELLS; i++) {
            Time date = dayTiles[i].getDate();

            if ((selected != null) &&
                    (date.year == selected.year) &&
                    (date.month == selected.month) &&
                    (date.monthDay == selected.monthDay)) {
                selectTile(dayTiles[i]);
            }
        }

    }

    private void update() {
        if (month != null) {
            updateTiles(month);
        }
    }


    public void offsetMonth(int offset) {

        if (month != null) {
            month.month += offset;
            month.normalize(true);
            setMonth(month);
        }
    }

    public void setItems(Map<Integer, DayInfo> items) {

        for (int day : items.keySet()) {
            int i = day - 1;
            if ((i >=0) && (dayTiles != null) && (i < dayTiles.length)) {

                dayTiles[i].dayInfo = items.get(day);
                // TODO
                //dayTiles[i].render();
            }
        }
    }


    private DayTile.OnClickListener onTileClickListener = new DayTile.OnClickListener() {
        @Override
        public void onClick(DayTile dayTile) {

            selectTile(dayTile);

            selected = new Time(month);
            selected.monthDay = dayTile.getMonthDay();

            String msg = "Selected: " + selected.format("%Y %m %d");
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();

            if (onDateSelectedListener != null) {
                onDateSelectedListener.onDateSelected(selected);
            }
        }
    };

    private void selectTile(DayTile dayTile) {
        if (selectedDayTile != null) {
            selectedDayTile.setSelected(false);
        }
        dayTile.setSelected(true);
        selectedDayTile = dayTile;
    }

    private int getOffset(Time currentMonth) {

        Time counter = new Time(currentMonth);

        counter.monthDay = 1;
        counter.normalize(true);

        int weekDayOn1st = counter.weekDay;

        int offset = weekDayOn1st - FIRST_DAY_OF_WEEK;
        if (offset < 0) {
            offset += 7;
        }

        return offset;
    }
}
