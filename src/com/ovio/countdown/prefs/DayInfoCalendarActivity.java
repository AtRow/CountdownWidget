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

package com.ovio.countdown.prefs;

import android.os.Bundle;
import android.text.format.Time;
import com.ovio.countdown.calendar.DayInfo;
import com.ovio.countdown.calendar.DayInfoFetcher;
import com.ovio.countdown.event.CalendarManager;
import com.ovio.countdown.event.EventData;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class DayInfoCalendarActivity extends CalendarActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Time date = new Time();
        date.setToNow();

        switcherCalendarView.setDayInfoFetcher(getDayInfoFetcher());
        switcherCalendarView.setDate(date);
    }

    private DayInfoFetcher getDayInfoFetcher() {
        return  new DayInfoFetcher() {
            CalendarManager manager = CalendarManager.getInstance(getApplicationContext());

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

                List<EventData> calendarEvents = manager.getEvents(start, end);

                for (EventData data : calendarEvents) {

                    Time eventStart = new Time();
                    eventStart.set(data.start);
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
