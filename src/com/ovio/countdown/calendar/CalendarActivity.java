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


    private static final String YEAR = "Y";
    private static final String MONTH = "M";
    private static final String DAY = "D";
    private static final int INVALID_VALUE = -1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Time date = getDateFromIntent(getIntent());

        SwitcherCalendarView switcherCalendarView = new SwitcherCalendarView(getApplicationContext());
        switcherCalendarView.setDate(date);
        switcherCalendarView.setOnDateSelectedListener(listener);

	    setContentView(switcherCalendarView);
    }

    private Time getDateFromIntent(Intent intent) {
        Time date = new Time();
        date.setToNow();
        date.hour = 0;
        date.minute = 0;
        date.second = 0;

        Bundle extras = intent.getExtras();
        if (extras != null) {
            int year = extras.getInt(YEAR, INVALID_VALUE);
            int month = extras.getInt(MONTH, INVALID_VALUE);
            int day = extras.getInt(DAY, INVALID_VALUE);

            if ((year != INVALID_VALUE) &&
                    (month != INVALID_VALUE) &&
                    (day != INVALID_VALUE)) {

                date.year = year;
                date.month = month;
                date.monthDay = day;
            }
        }

        return date;
    }

    private SwitcherCalendarView.OnDateSelectedListener listener = new SwitcherCalendarView.OnDateSelectedListener() {
        @Override
        public void onDateSelected(Time date) {
            returnDate(date);
        }
    };

    private void returnDate(Time date) {
        Intent intent = new Intent();

        Bundle extras = new Bundle();
        extras.putInt(YEAR, date.year);
        extras.putInt(MONTH, date.month);
        extras.putInt(DAY, date.monthDay);

        intent.putExtras(extras);
        setResult(RESULT_OK, intent);
        finish();
    }
}
