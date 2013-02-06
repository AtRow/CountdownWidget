package com.ovio.countdown.calendar;

import android.content.Context;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ovio.countdown.R;

/**
 * CalendarViewSample
 * com.examples.android.calendar
 */
public class DayTile extends RelativeLayout {

    public static final int INVALID = -1;

    public DayInfo dayInfo;

    public boolean containsDay;

    public boolean isCurrentDay;

    private Time tileDate;

    private Time now;

    private Time currentMonth;

    private OnClickListener clickListener;

    private DayTile self = this;


    public DayTile(Context context) {
        super(context);
    }

    public DayTile(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DayTile(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setSelected(boolean selected) {
        if (selected) {
            setBackgroundResource(R.drawable.item_bg_selected);
        } else {
            setBackgroundResource(R.drawable.item_bg_default);
        }
    }

    public void setTileDate(Time tileDate) {
        this.tileDate = new Time(tileDate);
    }

    public void setCurrentMonth(Time currentMonth) {
        this.currentMonth = new Time(currentMonth);
    }

    public Time getDate() {
        return tileDate;
    }

    public static interface OnClickListener {
        void onClick(DayTile dayTile);
    }

    public int getMonthDay() {
        if (tileDate != null) {
            return tileDate.monthDay;
        } else {
            return INVALID;
        }
    }

    private boolean isHoliday() {
        return (tileDate != null) &&
                (tileDate.weekDay == Time.SATURDAY || tileDate.weekDay == Time.SUNDAY);
    }

    public void setOnClickListener(OnClickListener clickListener) {
        this.clickListener = clickListener;
    }


    public void update() {

        TextView hint = (TextView) getChildAt(0);
        TextView day = (TextView) getChildAt(1);

        day.setText(getMonthDayString());

        if (!isDayEnabled()) {
            setBackgroundResource(R.drawable.item_bg_selected);
        } else {

            if(isCurrentDay()) {
                setBackgroundResource(R.drawable.item_bg_selected);

            } else if (isHoliday()) {
                setBackgroundResource(R.drawable.item_bg_holiday);

            } else {
                setBackgroundResource(R.drawable.item_bg_default);
            }

        }
        // show icon if date is not empty and it exists in the items array
        //ImageView hint = (ImageView)v.findViewById(R.id.date_icon);


        if(dayInfo != null) {
            hint.setVisibility(View.VISIBLE);
        } else {
            hint.setVisibility(View.INVISIBLE);
        }

        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickListener != null) {
                    clickListener.onClick(self);
                }
            }
        });
    }

    private boolean isDayEnabled() {
        return (tileDate != null) &&
               (currentMonth != null) &&
               (tileDate.year == currentMonth.year) &&
               (tileDate.month == currentMonth.month);
    }

    private boolean isCurrentDay() {
        if (now == null) {
            now = new Time();
            now.setToNow();
        }

        return (tileDate != null) &&
               (now != null) &&
               (tileDate.year == now.year) &&
               (tileDate.month == now.month) &&
               (tileDate.monthDay == now.monthDay);
    }

    private String getMonthDayString() {
        return Integer.toString(getMonthDay());
    }

}
