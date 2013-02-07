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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import com.ovio.countdown.event.Event;
import com.ovio.countdown.event.EventManager;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class CalendarActivity extends Activity {


    public static final String YEAR = "Y";
    public static final String MONTH = "M";
    public static final String DAY = "D";
    public static final int INVALID_VALUE = -1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SwitcherCalendarView switcherCalendarView = new SwitcherCalendarView(getApplicationContext());

        switcherCalendarView.setOnDateSelectedListener(listener);
        switcherCalendarView.setDayInfoFetcher(getDayInfoFetcher());

        Time date = getDateFromIntent(getIntent());
        if (date != null) {
            switcherCalendarView.setDate(date);
        }

	    setContentView(switcherCalendarView);
    }

    public static Time getDateFromIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            int year = extras.getInt(YEAR, INVALID_VALUE);
            int month = extras.getInt(MONTH, INVALID_VALUE);
            int day = extras.getInt(DAY, INVALID_VALUE);

            if ((year != INVALID_VALUE) &&
                    (month != INVALID_VALUE) &&
                    (day != INVALID_VALUE)) {

                Time date = new Time();
                date.hour = 0;
                date.minute = 0;
                date.second = 0;

                date.year = year;
                date.month = month;
                date.monthDay = day;

                return date;
            }
        }

        return null;
    }

    public static void putDateToIntent(Time date, Intent intent) {
        Bundle extras = new Bundle();
        extras.putInt(YEAR, date.year);
        extras.putInt(MONTH, date.month);
        extras.putInt(DAY, date.monthDay);

        intent.putExtras(extras);
    }


    private SwitcherCalendarView.OnDateSelectedListener listener = new SwitcherCalendarView.OnDateSelectedListener() {
        @Override
        public void onDateSelected(Time date) {
            Intent intent = new Intent();
            putDateToIntent(date, intent);
            setResult(RESULT_OK, intent);
            finish();
        }
    };

    private DayInfoFetcher getDayInfoFetcher() {
        return  new DayInfoFetcher() {
            EventManager manager = EventManager.getInstance(getApplicationContext());

            @Override
            public Map<Integer, DayInfo> fetchForMonth(int year, int month) {

                Map<Integer, DayInfo> map = new TreeMap<Integer, DayInfo>();

                Time time = new Time();
                time.year = year;
                time.month = month;

                long start = time.toMillis(true);

                time.monthDay = time.getActualMaximum(Time.MONTH_DAY);
                time.hour = 23;
                time.minute = 59;
                time.second = 59;

                long end = time.toMillis(true);

                List<Event> events = manager.getEvents(start, end);

                for (Event event: events) {

                    Time eventStart = new Time();
                    eventStart.set(event.start);
                    int day = eventStart.monthDay;

                    if (!map.containsKey(day)) {
                        DayInfo info = new DayInfo();
                        info.eventCount = 1;
                        map.put(day, info);

                    } else {
                        DayInfo info = map.get(day);
                        info.eventCount++;
                    }
                }

                return map;
            }
        };
    }

}
