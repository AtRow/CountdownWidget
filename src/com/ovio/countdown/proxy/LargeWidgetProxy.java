package com.ovio.countdown.proxy;

import android.content.Context;
import android.text.format.Time;
import com.ovio.countdown.R;
import com.ovio.countdown.date.TimeDifference;
import com.ovio.countdown.event.Event;
import com.ovio.countdown.log.Logger;

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
    protected String getTimeText(TimeDifference diff) {

        String text;

        switch (maxCountingVal) {
            case Time.YEAR:
                text = getTextY2D(diff);
                break;

            case Time.MONTH:
                text = getTextM2H(diff);
                break;

            case Time.MONTH_DAY:
                text = getTextD2M(diff);
                break;

            case Time.HOUR:
                text = getTextH2S(diff);
                break;

            default:
                text = "Error";
        }

        if (!diff.positive) {
            text = "+ " + text;
        }

        return text;
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



    private String getTextY2D(TimeDifference diff) {
        StringBuilder sb = new StringBuilder();

        sb.append(diff.years).append("y ")
                .append(diff.months).append("m ")
                .append(diff.days).append("d");

        return sb.toString();
    }

    private String getTextM2H(TimeDifference diff) {
        StringBuilder sb = new StringBuilder();

        sb.append(diff.months).append("m ")
                .append(diff.days).append("d :");
        if (diff.hours < 10) sb.append(0);
        sb.append(diff.hours);

        return sb.toString();
    }

    private String getTextD2M(TimeDifference diff) {
        StringBuilder sb = new StringBuilder();

        sb.append(diff.days).append("d ");
        if (diff.hours < 10) sb.append(0);
        sb.append(diff.hours).append(":");
        if (diff.mins < 10) sb.append(0);
        sb.append(diff.mins);

        return sb.toString();
    }

    private String getTextH2S(TimeDifference diff) {
        StringBuilder sb = new StringBuilder();

        if (diff.hours < 10) sb.append(0);
        sb.append(diff.hours).append(":");
        if (diff.mins < 10) sb.append(0);
        sb.append(diff.mins).append(":");
        if (diff.secs < 10) sb.append(0);
        sb.append(diff.secs);

        return sb.toString();
    }

}
