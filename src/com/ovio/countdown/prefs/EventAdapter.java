package com.ovio.countdown.prefs;

import android.content.Context;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.ovio.countdown.R;
import com.ovio.countdown.event.Event;
import com.ovio.countdown.util.Util;

/**
 * Countdown
 * com.ovio.countdown.calendar
 */
public class EventAdapter extends BaseAdapter {

    private final Event[] events;
    private final Context context;

    public EventAdapter(Context context, Event[] events) {
        this.context = context;
        this.events = events;
    }

    @Override
    public int getCount() {
        return events.length;
    }

    @Override
    public Object getItem(int i) {
        return events[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewGroup view = (ViewGroup) convertView;

        if (convertView == null) { // if it's not recycled, initialize some attributes
            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = (ViewGroup) li.inflate(R.layout.event_item, null);
        }

        renderEvent(events[position], view);

        return view;
    }

    private void renderEvent(Event item, ViewGroup view) {

        TextView desc = (TextView) view.getChildAt(0);
        TextView date = (TextView) view.getChildAt(1);

        Time time = new Time();
        time.set(item.start);

        desc.setText(item.title);
        date.setText(time.format(Util.TF));

    }
}
