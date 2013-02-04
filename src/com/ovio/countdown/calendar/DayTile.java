package com.ovio.countdown.calendar;

import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.ovio.countdown.R;

/**
 * CalendarViewSample
 * com.examples.android.calendar
 */
public class DayTile {

    public DayInfo dayInfo;

    public boolean containsDay;

    public boolean isCurrentDay;

    private final Time time;

    private OnClickListener clickListener;

    private DayTile self = this;

    private ViewGroup tileView;

    public void setSelected(boolean selected) {
        if (selected) {
            tileView.setBackgroundResource(R.drawable.item_bg_selected);
        } else {
            tileView.setBackgroundResource(R.drawable.item_bg_default);
        }
    }

    public static interface OnClickListener {
        void onClick(DayTile dayTile);
    }

    public DayTile() {
        time = new Time();
        containsDay = false;
    }

    public DayTile(Time time) {
        this.time = new Time(time);
        this.time.normalize(true);
        containsDay = true;
    }

    public int getMonthDay() {
        return time.monthDay;
    }

    public boolean isHoliday() {
        return (time.weekDay == Time.SATURDAY ||
                time.weekDay == Time.SUNDAY);
    }

    public void setOnClickListener(OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void renderTo(ViewGroup tileView) {

        this.tileView = tileView;
        render();
    }

    public void render() {

        if (tileView == null) {
            Log.w("DayTile", "Trying to render before assigning TileView, aborting");
            return;
        }

        TextView hint = (TextView) tileView.getChildAt(0);
        TextView dayView = (TextView)tileView.getChildAt(1);

        dayView.setText(Integer.toString(time.monthDay));

        if(isCurrentDay) {
            tileView.setBackgroundResource(R.drawable.item_bg_selected);

        } else if (isHoliday()) {
            tileView.setBackgroundResource(R.drawable.item_bg_holiday);

        } else {
            tileView.setBackgroundResource(R.drawable.item_bg_default);
        }

        // show icon if date is not empty and it exists in the items array
        //ImageView hint = (ImageView)v.findViewById(R.id.date_icon);


        if(dayInfo != null) {
            hint.setVisibility(View.VISIBLE);
        } else {
            hint.setVisibility(View.INVISIBLE);
        }

        tileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener != null) {
                    clickListener.onClick(self);
                }
            }
        });
    }
}
