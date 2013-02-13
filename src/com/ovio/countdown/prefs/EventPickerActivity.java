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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.ovio.countdown.R;
import com.ovio.countdown.event.CalendarManager;
import com.ovio.countdown.event.EventData;


public class EventPickerActivity extends Activity {

    public static final String EVENT = "event";

    private EventPickerActivity self = this;

    private EventAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Time date = DateHelper.getDateFromIntent(getIntent());
        if (date == null) {
            Toast.makeText(getApplicationContext(), "No date info!", Toast.LENGTH_LONG);
            finish();
        }

        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ListView listView = (ListView) li.inflate(R.layout.events_list, null);

        CalendarManager manager = CalendarManager.getInstance(getApplicationContext());

        Time time = new Time(date);
        time.hour = 0;
        time.minute = 0;
        time.second = 0;
        long start = time.toMillis(true);

        time.hour = 23;
        time.minute = 59;
        time.second = 59;
        long end = time.toMillis(true);

        EventData[] events = manager.getEvents(start, end).toArray(new EventData[0]);

        if (events.length == 0) {
            Toast.makeText(getApplicationContext(), R.string.event_picker_no_events, Toast.LENGTH_LONG).show();
            finish();
        }

        adapter = new EventAdapter(getApplicationContext(), events);

        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        listView.setOnItemClickListener(getOnItemClickListener());

        setContentView(listView);
    }

    private AdapterView.OnItemClickListener getOnItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                returnSelectedEvent((EventData) adapter.getItem(position));
            }
        };
    }

    private void returnSelectedEvent(EventData eventData) {
        Intent intent = new Intent();

        if (eventData != null) {
            Bundle extras = new Bundle();
            extras.putSerializable(EVENT, eventData);
            intent.putExtras(extras);

            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED, intent);
        }

        finish();
    }

}
