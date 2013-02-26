package com.ovio.countdown.proxy;

import android.content.Context;
import android.text.format.Time;
import com.ovio.countdown.R;
import com.ovio.countdown.date.TimeDifference;
import com.ovio.countdown.event.Event;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.proxy.painter.SmallWidgetPainter;
import com.ovio.countdown.proxy.painter.WidgetPainter;

/**
 * Countdown
 * com.ovio.countdown
 */
public class SmallWidgetProxy extends WidgetProxy {

//TODO:
/*
    public static final int HALF_MINUTE = -5;
    public static final int HALF_HOUR = -6;
    public static final int HALF_DAY = -7;
*/

    private static final String TAG = Logger.PREFIX + "proxy";

    private static final int LAYOUT = R.layout.countdown_widget_layout_1x1;

    public SmallWidgetProxy(Context context, int widgetId, Event event) {
        super(context, LAYOUT, widgetId, event);
    }


    @Override
    protected WidgetPainter getWidgetPainter() {
        return SmallWidgetPainter.getInstance(context);
    }

    @Override
    protected void updateCounters(TimeDifference diff) {

        // Year
        if (diff.years > 0) {
            maxCountingVal = Time.YEAR;
            minCountingVal = maxCountingVal;
            nextIncrement = DAY;
            setCountSeconds(false);

        // Month
        } else if (diff.months > 0) {
            maxCountingVal = Time.MONTH;
            minCountingVal = maxCountingVal;
            nextIncrement = DAY;
            setCountSeconds(false);

        // Day
        } else if (diff.days > 0) {
            maxCountingVal = Time.MONTH_DAY;
            minCountingVal = maxCountingVal;

            if (diff.days == 1) {
                nextIncrement = HOUR;
            } else {
                nextIncrement = DAY;
            }
            setCountSeconds(false);

        // Hour
        } else if (diff.hours > 0) {
            maxCountingVal = Time.HOUR;
            minCountingVal = maxCountingVal;

            if (diff.hours == 1) {
                nextIncrement = MINUTE;
            } else {
                nextIncrement = HOUR;
            }
            setCountSeconds(false);

        // Minute
        } else if (diff.mins > 0) {
            maxCountingVal = Time.MINUTE;
            minCountingVal = maxCountingVal;
            nextIncrement = MINUTE;

            if (diff.mins == 1) {
                setCountSeconds(true);
            } else {
                setCountSeconds(false);
            }

        // Second
        } else {
            if (event.isCountingSeconds()) {
                maxCountingVal = Time.SECOND;
                minCountingVal = maxCountingVal;
            } else {
                maxCountingVal = Time.MINUTE;
                minCountingVal = maxCountingVal;
            }

            nextIncrement = MINUTE;
            setCountSeconds(true);
        }
    }

}
