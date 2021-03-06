package com.ovio.countdown.proxy;

import android.content.Context;
import android.text.format.Time;
import com.ovio.countdown.R;
import com.ovio.countdown.date.TimeDifference;
import com.ovio.countdown.event.Event;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.proxy.painter.MediumWidgetPainter;
import com.ovio.countdown.proxy.painter.WidgetPainter;

/**
 * Countdown
 * com.ovio.countdown
 */
public class MediumWidgetProxy extends WidgetProxy {

    private static final String TAG = Logger.PREFIX + "proxy";

    private static final int LAYOUT = R.layout.countdown_widget_layout_2x1;

    public MediumWidgetProxy(Context context, int widgetId, Event event) {
        super(context, LAYOUT, widgetId, event);
    }

    @Override
    protected WidgetPainter getWidgetPainter() {
        return MediumWidgetPainter.getInstance(context);
    }

    @Override
    protected void updateCounters(TimeDifference diff) {

        // Year-Month
        if (diff.years > 0) {
            maxCountingVal = Time.YEAR;
            minCountingVal = Time.MONTH;
            nextIncrement = DAY;
            setCountSeconds(false);

        // Month-Day
        } else if (diff.months > 0) {
            maxCountingVal = Time.MONTH;
            minCountingVal = Time.MONTH_DAY;

            if (diff.months == 1 && diff.days == 0) {
                nextIncrement = HOUR;
            } else {
                nextIncrement = DAY;
            }
            setCountSeconds(false);

        // Day-Hour
        } else if (diff.days > 0) {
            maxCountingVal = Time.MONTH_DAY;
            minCountingVal = Time.HOUR;

            if (diff.days == 1 && diff.hours == 0) {
                nextIncrement = MINUTE;
            } else {
                nextIncrement = HOUR;
            }
            setCountSeconds(false);

        // Hour-Minute
        } else if (diff.hours > 0) {
            maxCountingVal = Time.HOUR;
            minCountingVal = Time.MINUTE;
            nextIncrement = MINUTE;

            if (diff.hours == 1 && diff.mins == 0) {
                setCountSeconds(true);
            } else {
                setCountSeconds(false);
            }

        // Minute-Second
        } else {
            if (event.isCountingSeconds()) {
                maxCountingVal = Time.MINUTE;
                minCountingVal = Time.SECOND;
            } else {
                maxCountingVal = Time.HOUR;
                minCountingVal = Time.MINUTE;
            }

            nextIncrement = MINUTE;
            setCountSeconds(true);
        }
    }
}
