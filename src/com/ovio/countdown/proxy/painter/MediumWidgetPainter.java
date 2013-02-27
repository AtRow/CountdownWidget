package com.ovio.countdown.proxy.painter;

import android.content.Context;
import android.graphics.Bitmap;
import com.ovio.countdown.date.TimeDifference;
import com.ovio.countdown.log.Logger;

/**
 * Countdown
 * com.ovio.countdown
 */
public class MediumWidgetPainter extends AbstractWidgetPainter {

    private static final String TAG = Logger.PREFIX + "MediumPainter";

    // TODO

    private static final int BITMAP_WIDTH = 290;
    private static final int BITMAP_HEIGHT = 72;


    private static MediumWidgetPainter instance;

    private MediumWidgetPainter(Context context) {
        super(context);
        Logger.i(TAG, "Instantiated LargeWidgetPainter");
    }

    public static synchronized MediumWidgetPainter getInstance(Context context) {
        if (instance == null) {
            instance = new MediumWidgetPainter(context);
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


    // TODO

/*
    protected Bitmap getGeneralBitmap(TimeDifference diff) {

        String text;

        switch (maxCountingVal) {
            case Time.YEAR:
                text = getTextY2M(diff);
                break;

            case Time.MONTH:
                text = getTextM2D(diff);
                break;

            case Time.MONTH_DAY:
                text = getTextD2H(diff);
                break;

            case Time.HOUR:
                text = getTextH2M(diff);
                break;

            case Time.MINUTE:
                text = getTextM2S(diff);
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

    private String getTextY2M(TimeDifference diff) {
        StringBuilder sb = new StringBuilder();

        sb.append(diff.years).append("y ")
                .append(diff.months).append("m ");

        return sb.toString();
    }

    private String getTextM2D(TimeDifference diff) {
        StringBuilder sb = new StringBuilder();

        sb.append(diff.months).append("m ")
                .append(diff.days).append("d");

        return sb.toString();
    }

    private String getTextD2H(TimeDifference diff) {
        StringBuilder sb = new StringBuilder();

        sb.append(diff.days).append("d ");
        if (diff.hours < 10) sb.append(0);
        sb.append(diff.hours).append("h");

        return sb.toString();
    }

    private String getTextH2M(TimeDifference diff) {
        StringBuilder sb = new StringBuilder();

        if (diff.hours < 10) sb.append(0);
        sb.append(diff.hours).append(":");
        if (diff.mins < 10) sb.append(0);
        sb.append(diff.mins);

        return sb.toString();
    }

    private String getTextM2S(TimeDifference diff) {
        StringBuilder sb = new StringBuilder();

        if (diff.mins < 10) sb.append(0);
        sb.append(diff.mins).append(":");
        if (diff.secs < 10) sb.append(0);
        sb.append(diff.secs);

        return sb.toString();
    }

}
