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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import com.ovio.countdown.calendar.SwitcherCalendarView;


public class CalendarActivity extends Activity {


    protected SwitcherCalendarView switcherCalendarView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        switcherCalendarView = new SwitcherCalendarView(getApplicationContext());

        switcherCalendarView.setOnDateSelectedListener(listener);

        Time date = DateHelper.getDateFromIntent(getIntent());
        if (date != null) {
            switcherCalendarView.setDate(date);
        }

	    setContentView(switcherCalendarView);
    }


    private SwitcherCalendarView.OnDateSelectedListener listener = new SwitcherCalendarView.OnDateSelectedListener() {
        @Override
        public void onDateSelected(Time date) {
            Intent intent = new Intent();
            DateHelper.putDateToIntent(date, intent);
            setResult(RESULT_OK, intent);
            finish();
        }
    };

}
