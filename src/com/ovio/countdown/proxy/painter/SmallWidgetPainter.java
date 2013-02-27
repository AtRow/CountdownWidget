package com.ovio.countdown.proxy.painter;

import android.content.Context;
import android.graphics.Bitmap;
import com.ovio.countdown.date.TimeDifference;
import com.ovio.countdown.log.Logger;

/**
 * Countdown
 * com.ovio.countdown
 */
public class SmallWidgetPainter extends AbstractWidgetPainter {

    private static final String TAG = Logger.PREFIX + "SmallPainter";

    // TODO

    private static final int BITMAP_WIDTH = 283;
    private static final int BITMAP_HEIGHT = 64;


    private static SmallWidgetPainter instance;

    private SmallWidgetPainter(Context context) {
        super(context);
        Logger.i(TAG, "Instantiated LargeWidgetPainter");
    }

    public static synchronized SmallWidgetPainter getInstance(Context context) {
        if (instance == null) {
            instance = new SmallWidgetPainter(context);
        }
        Logger.i(TAG, "Returning LargeWidgetPainter instance");
        return instance;
    }


    @Override
    public Bitmap getNewBitmap(Context context, int resource) {
        return null;
    }

    @Override
    public Bitmap drawTime(Bitmap bitmap, TimeDifference diff, int maxCountingVal) {
        return null;
    }

    @Override
    public Bitmap drawHeader(Bitmap bitmap, String title, Bitmap icon) {
        return null;
    }

    // TODO:

/*
    @Override
    protected Bitmap getGeneralBitmap(TimeDifference diff) {

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

        return null;
    }
*/

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
