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
public class LargeWidgetProxy extends WidgetProxy {

    private static final String TAG = Logger.PREFIX + "proxy";

    private static final int LAYOUT = R.layout.countdown_widget_layout_4x1;

    public LargeWidgetProxy(Context context, WidgetOptions options) {
        super(context, LAYOUT, options);
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

    protected int getMaxCountingVal(TimeDifference diff) {

        if (diff.years > 0) {
            return Time.YEAR;

        } else if (diff.months > 0) {
            return Time.MONTH;

        } else if (diff.days > 0) {
            return Time.MONTH_DAY;

        } else {
            if (options.enableSeconds) {
                return Time.HOUR;
            } else {
                return Time.MONTH_DAY;
            }
        }
    }

    protected int getMinCountingVal(int maxCountingVal) {

        if (maxCountingVal == Time.YEAR) {
            return Time.MONTH_DAY;

        } else if (maxCountingVal == Time.MONTH) {
            return Time.HOUR;

        } else if (maxCountingVal == Time.MONTH_DAY) {
            return Time.MINUTE;

        } else {
            if (options.enableSeconds) {
                return Time.SECOND;
            } else {
                return Time.MINUTE;
            }
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
