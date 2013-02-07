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
import android.widget.TextView;
import android.widget.Toast;
import com.ovio.countdown.R;


public class SwitcherCalendarView extends FrameLayout implements CalendarView.OnDateSelectedListener {

    private static final int LEFT = 0;
    private static final int CENTER = 1;
    private static final int RIGHT = 2;

    private HorizontalPager pager;

    private FrameLayout leftScreen;
    private FrameLayout centerScreen;
    private FrameLayout rightScreen;

    private CalendarView prevCalendar;
    private CalendarView currCalendar;
    private CalendarView nextCalendar;

    private FrameLayout container;
    private TextView title;

    private Time selectedDate;

    private OnDateSelectedListener onDateSelectedListener;
    private DayInfoFetcher dayInfoFetcher;


    public SwitcherCalendarView(Context context) {
        super(context);
        init();
    }

    public SwitcherCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    public void onDateSelected(Time date) {
        selectedDate = new Time(date);

        if (onDateSelectedListener != null) {
            onDateSelectedListener.onDateSelected(selectedDate);
        }
    }

    public void setDayInfoFetcher(DayInfoFetcher dayInfoFetcher) {
        this.dayInfoFetcher = dayInfoFetcher;

        prevCalendar.setDayInfoFetcher(dayInfoFetcher);
        currCalendar.setDayInfoFetcher(dayInfoFetcher);
        nextCalendar.setDayInfoFetcher(dayInfoFetcher);
    }

    public static interface OnDateSelectedListener {
        void onDateSelected(Time date);
    }

    public void setOnDateSelectedListener(OnDateSelectedListener onDateSelectedListener) {
        this.onDateSelectedListener = onDateSelectedListener;
    }

    private void init() {

        inflate();

        pager = new HorizontalPager(getContext());
        pager.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

        leftScreen = new FrameLayout(getContext());
        centerScreen = new FrameLayout(getContext());
        rightScreen = new FrameLayout(getContext());

        prevCalendar = new CalendarView(getContext());
        currCalendar = new CalendarView(getContext());
        nextCalendar = new CalendarView(getContext());

        prevCalendar.setOnDateSelectedListener(this);
        currCalendar.setOnDateSelectedListener(this);
        nextCalendar.setOnDateSelectedListener(this);

        leftScreen.addView(prevCalendar);
        centerScreen.addView(currCalendar);
        rightScreen.addView(nextCalendar);

        pager.addView(leftScreen);
        pager.addView(centerScreen);
        pager.addView(rightScreen);

        pager.setCurrentScreen(CENTER, false);

        pager.setOnScreenSwitchListener(onScreenSwitchListener);


//        final GestureDetector tapGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
//            @Override
//            public boolean onSingleTapConfirmed(MotionEvent e) {
//                onSingleClick();
//                return true;
//            }
//        });
//
//        pager.setOnTouchListener(new OnTouchListener() {
//            public boolean onTouch(View v, MotionEvent event) {
//                tapGestureDetector.onTouchEvent(event);
//                return false;
//            }
//        });

        container.addView(pager);
    }

    private void inflate() {
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        li.inflate(R.layout.calendar_switcher, this, true);

        container = (FrameLayout) findViewById(R.id.switcherContainer);
        title = (TextView) findViewById(R.id.title);

        TextView previous = (TextView) findViewById(R.id.previous);
        previous.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentScreen(LEFT, true);
            }
        });

        TextView next = (TextView) findViewById(R.id.next);
        next.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentScreen(RIGHT, true);
            }
        });
    }

    public void setDate(Time time) {
        Time date = new Time(time);
        date.monthDay = 1;
        date.hour = 0;
        date.minute = 0;
        date.second = 0;
        date.month--;

        CalendarView[] calendarViews = new CalendarView[] {prevCalendar, currCalendar, nextCalendar};

        for (int i = 0; i < 3; i++) {
            date.normalize(true);
            calendarViews[i].setMonth(date);
            date.month++;
        }

        currCalendar.setDate(time);

        updateCurrentMonth();
        selectedDate = new Time(time);
    }

    public Time getSelectedDate() {
        return selectedDate;
    }

    private void onSingleClick() {

        int i = pager.getCurrentScreen();

        ViewGroup viewGroup = (ViewGroup) pager.getChildAt(i);
        CalendarView selectedCalendar = (CalendarView) viewGroup.getChildAt(0);

        selectedDate = selectedCalendar.getDate();

        if (onDateSelectedListener != null) {
            onDateSelectedListener.onDateSelected(selectedDate);
        }

        String day = selectedDate.format("%Y.%m.%d %H:%M:%S");
        String msg = "Selected: " + day;
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void updateCurrentMonth() {
        Time currentMonth = currCalendar.getMonth();
        title.setText(currentMonth.format("%OB %Y"));
        Log.w("SCV", "Set current month to: " + currentMonth.format("%Y %m"));
    }

    private final HorizontalPager.OnScreenSwitchListener onScreenSwitchListener = new HorizontalPager.OnScreenSwitchListener() {
        public void onScreenSwitched(final int screen) {
            /*
            * this method is executed if a screen has been activated, i.e. the screen is
			* completely visible and the animation has stopped (might be useful for
			* removing / adding new views)
			*/

            switch (screen) {

                case LEFT:
                    // release currCalendar
                    centerScreen.removeAllViews();
                    // release prevCalendar
                    leftScreen.removeAllViews();
                    centerScreen.addView(prevCalendar);

                    pager.setCurrentScreen(CENTER, false);

                    // release nextCalendar
                    rightScreen.removeAllViews();

                    rightScreen.addView(currCalendar);
                    leftScreen.addView(nextCalendar);

                    nextCalendar.offsetMonth(-3);

                    prevCalendar = (CalendarView) leftScreen.getChildAt(0);
                    currCalendar = (CalendarView) centerScreen.getChildAt(0);
                    nextCalendar = (CalendarView) rightScreen.getChildAt(0);

                    Log.d("SCV", "LEFT   [" + prevCalendar.getMonth().format("%Y %m") + "]");
                    Log.d("SCV", "CENTER [" + currCalendar.getMonth().format("%Y %m") + "]");
                    Log.d("SCV", "RIGHT  [" + nextCalendar.getMonth().format("%Y %m") + "]");

                    break;

                case RIGHT:
                    // release currCalendar
                    centerScreen.removeAllViews();
                    // release nextCalendar
                    rightScreen.removeAllViews();
                    centerScreen.addView(nextCalendar);
                    pager.setCurrentScreen(CENTER, false);

                    // release prevCalendar
                    leftScreen.removeAllViews();

                    rightScreen.addView(prevCalendar);
                    leftScreen.addView(currCalendar);

                    prevCalendar.offsetMonth(+3);

                    prevCalendar = (CalendarView) leftScreen.getChildAt(0);
                    currCalendar = (CalendarView) centerScreen.getChildAt(0);
                    nextCalendar = (CalendarView) rightScreen.getChildAt(0);

                    Log.d("SCV", "LEFT   [" + prevCalendar.getMonth().format("%Y %m") + "]");
                    Log.d("SCV", "CENTER [" + currCalendar.getMonth().format("%Y %m") + "]");
                    Log.d("SCV", "RIGHT  [" + nextCalendar.getMonth().format("%Y %m") + "]");

                    break;

            }
            updateCurrentMonth();
        }
    };

}
