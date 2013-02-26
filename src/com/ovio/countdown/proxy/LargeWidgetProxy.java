package com.ovio.countdown.proxy;

import android.content.Context;
import android.text.format.Time;
import com.ovio.countdown.R;
import com.ovio.countdown.date.TimeDifference;
import com.ovio.countdown.event.Event;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.proxy.painter.LargeWidgetPainter;
import com.ovio.countdown.proxy.painter.WidgetPainter;

/**
 * Countdown
 * com.ovio.countdown
 */
public class LargeWidgetProxy extends WidgetProxy {

    private static final String TAG = Logger.PREFIX + "proxy";

    private static final int LAYOUT = R.layout.countdown_widget_layout_4x1;

    public LargeWidgetProxy(Context context, int widgetId, Event event) {
        super(context, LAYOUT, widgetId, event);
    }

    @Override
    protected WidgetPainter getWidgetPainter() {
        return LargeWidgetPainter.getInstance(context);
    }

    @Override
    protected void updateCounters(TimeDifference diff) {

        // Year-Month-Day
        if (diff.years > 0) {
            maxCountingVal = Time.YEAR;
            minCountingVal = Time.MONTH_DAY;
            nextIncrement = DAY;
            setCountSeconds(false);

        // Month-Day-Hour
        } else if (diff.months > 0) {
            maxCountingVal = Time.MONTH;
            minCountingVal = Time.HOUR;
            if (diff.months == 1 && diff.days == 0 && diff.hours == 0) {
                nextIncrement = MINUTE;
            } else {
                nextIncrement = HOUR;
            }
            setCountSeconds(false);

        // Day-Hour-Minute
        } else if (diff.days > 0) {
            maxCountingVal = Time.MONTH_DAY;
            minCountingVal = Time.MINUTE;
            nextIncrement = MINUTE;

            setCountSeconds(false);
            if (diff.days == 1 && diff.hours == 0 && diff.mins == 0) {
                setCountSeconds(true);
            } else {
                setCountSeconds(false);
            }

        // Hour-Minute-Second
        } else {
            if (event.isCountingSeconds()) {
                maxCountingVal = Time.HOUR;
                minCountingVal = Time.SECOND;
            } else {
                maxCountingVal = Time.MONTH_DAY;
                minCountingVal = Time.MINUTE;
            }

            nextIncrement = MINUTE;
            setCountSeconds(true);
        }
    }
}
