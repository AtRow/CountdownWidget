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


public class CalendarActivity extends Activity {


    public static final String YEAR = "Y";
    public static final String MONTH = "M";
    public static final String DAY = "D";
    public static final int INVALID_VALUE = -1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SwitcherCalendarView switcherCalendarView = new SwitcherCalendarView(getApplicationContext());

        Time date = getDateFromIntent(getIntent());
        if (date != null) {
            switcherCalendarView.setDate(date);
        }

        switcherCalendarView.setOnDateSelectedListener(listener);
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


}
