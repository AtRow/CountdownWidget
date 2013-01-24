package com.ovio.countdown.proxy;

import android.content.Context;
import android.text.format.Time;
import com.ovio.countdown.R;
import com.ovio.countdown.date.TimeDifference;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.preferences.WidgetOptions;

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

    public SmallWidgetProxy(Context context, WidgetOptions options) {
        super(context, LAYOUT, options);
    }

    @Override
    protected String getTimeText(TimeDifference diff) {

        String text;

        switch (maxCountingVal) {
            case Time.YEAR:
                text = getTextY(diff);
                break;

            case Time.MONTH:
                text = getTextMon(diff);
                break;

            case Time.MONTH_DAY:
                text = getTextD(diff);
                break;

            case Time.HOUR:
                text = getTextH(diff);
                break;

            case Time.MINUTE:
                text = getTextMin(diff);
                break;

            case Time.SECOND:
                text = getTextS(diff);
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
            if (options.enableSeconds) {
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

    private String getTextY(TimeDifference diff) {
        StringBuilder sb = new StringBuilder();

        sb.append(diff.years);
        if (diff.months >= 6) sb.append(",5");
        sb.append("y");

        return sb.toString();
    }

    private String getTextMon(TimeDifference diff) {
        StringBuilder sb = new StringBuilder();

        sb.append(diff.months);
        if (diff.days >= 15) sb.append(",5");
        sb.append("m");

        return sb.toString();
    }

    private String getTextD(TimeDifference diff) {
        StringBuilder sb = new StringBuilder();

        sb.append(diff.days);
        if (diff.hours >= 12) sb.append(",5");
        sb.append("d");

        return sb.toString();
    }

    private String getTextH(TimeDifference diff) {
        StringBuilder sb = new StringBuilder();

        sb.append(diff.hours);
        if (diff.mins >= 30) sb.append(",5");
        sb.append("h");

        return sb.toString();
    }

    private String getTextMin(TimeDifference diff) {
        StringBuilder sb = new StringBuilder();

        sb.append(diff.mins);
        if (diff.secs >= 30) sb.append(",5");
        sb.append("m");

        return sb.toString();
    }

    private String getTextS(TimeDifference diff) {
        StringBuilder sb = new StringBuilder();

        sb.append(diff.secs).append("s");

        return sb.toString();
    }

}
