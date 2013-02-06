package com.ovio.countdown.calendar;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Countdown
 * com.ovio.countdown.calendar
 */
public class DayTileAdapter extends BaseAdapter {

    private final DayTile[] items;
    private final Context context;

    public DayTileAdapter(Context context, DayTile[] items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int i) {
        return items[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        View view = convertView;
//
//        if (convertView == null) { // if it's not recycled, initialize some attributes
//            LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            view = li.inflate(R.layout.calendar_item, null);
//        }

        return items[position];
    }
}
