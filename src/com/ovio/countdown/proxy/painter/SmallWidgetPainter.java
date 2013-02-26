package com.ovio.countdown.proxy.painter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.format.Time;
import com.ovio.countdown.date.TimeDifference;
import com.ovio.countdown.log.Logger;

/**
 * Countdown
 * com.ovio.countdown
 */
public class SmallWidgetPainter extends AbstractWidgetPainter {

    private static final String TAG = Logger.PREFIX + "SmallPainter";

    // TODO

    private static final float PLUS_SIZE = 33;
    private static final float DIGIT_SIZE = 50;

    private static final float PLUS_V_OFFSET = 55;
    private static final float DIGIT_V_OFFSET = 61;

    private static final String PLUS_SYM = "+";
    private static final String OFFSET_DIGIT = "0";

    private static final float SUB_SIZE = 11f;

    private static final float PLUS_H_OFFSET = 6;
    private static final float FIRST_H_OFFSET = 76;
    private static final float SECOND_H_OFFSET = 160;
    private static final float THIRD_H_OFFSET = 245;

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
    public Bitmap getNewBitmap() {
        return Bitmap.createBitmap(toPx(BITMAP_WIDTH), toPx(BITMAP_HEIGHT), Bitmap.Config.ARGB_8888);
    }

    @Override
    public Bitmap drawTime(Bitmap bitmap, TimeDifference diff, int maxCountingVal) {

        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setSubpixelText(true);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setLinearText(true);

        switch (maxCountingVal) {
            case Time.YEAR:
                drawPlus(canvas, paint, diff.positive, diff.years);

                drawYear(canvas, paint, diff.years, FIRST_H_OFFSET);
                drawMonth(canvas, paint, diff.months, true, SECOND_H_OFFSET);
                drawDay(canvas, paint, diff.days, true, THIRD_H_OFFSET);
                break;

            case Time.MONTH:
                drawPlus(canvas, paint, diff.positive, diff.months);

                drawMonth(canvas, paint, diff.months, false, FIRST_H_OFFSET);
                drawDay(canvas, paint, diff.days, true, SECOND_H_OFFSET);
                drawHour(canvas, paint, diff.hours, true, THIRD_H_OFFSET);
                break;

            case Time.MONTH_DAY:
                drawPlus(canvas, paint, diff.positive, diff.days);

                drawDay(canvas, paint, diff.days, false, FIRST_H_OFFSET);
                drawHour(canvas, paint, diff.hours, true, SECOND_H_OFFSET);
                drawMinute(canvas, paint, diff.mins, THIRD_H_OFFSET);
                break;

            case Time.HOUR:
                drawPlus(canvas, paint, diff.positive, diff.hours);

                drawHour(canvas, paint, diff.hours, false, FIRST_H_OFFSET);
                drawMinute(canvas, paint, diff.mins, SECOND_H_OFFSET);
                //drawSecond(canvas, paint, diff.secs, THIRD_H_OFFSET);
                break;
        }

        return bitmap;
    }

    @Override
    public Bitmap drawSeconds(Bitmap bitmap, int seconds) {

        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setSubpixelText(true);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setLinearText(true);

        drawSecond(canvas, paint, seconds, THIRD_H_OFFSET);

        return bitmap;
    }

    private void drawYear(Canvas canvas, Paint paint, int value, float xOffset) {

        drawDigit(canvas, paint, value, false, xOffset);
        drawSub(canvas, paint, getYearSub(value), xOffset);
    }

    private void drawMonth(Canvas canvas, Paint paint, int value, boolean drawZero, float xOffset) {

        drawDigit(canvas, paint, value, drawZero, xOffset);
        drawSub(canvas, paint, getMonthSub(value), xOffset);
    }

    private void drawDay(Canvas canvas, Paint paint, int value, boolean drawZero, float xOffset) {

        drawDigit(canvas, paint, value, drawZero, xOffset);
        drawSub(canvas, paint, getDaySub(value), xOffset);
    }

    private void drawHour(Canvas canvas, Paint paint, int value, boolean drawZero, float xOffset) {

        drawDigit(canvas, paint, value, drawZero, xOffset);
        drawSub(canvas, paint, getHourSub(value), xOffset);
    }

    private void drawMinute(Canvas canvas, Paint paint, int value, float xOffset) {

        drawDigit(canvas, paint, value, true, xOffset);
        drawSub(canvas, paint, getMinuteSub(value), xOffset);
    }

    private void drawSecond(Canvas canvas, Paint paint, int value, float xOffset) {

        drawDigit(canvas, paint, value, true, xOffset);
        drawSub(canvas, paint, getSecondSub(value), xOffset);
    }

    private void drawPlus(Canvas canvas, Paint paint, boolean isPositive, int firstVal) {
        if (isPositive) {
            return;
        }

        paint.setTextSize(toPx(PLUS_SIZE));
        paint.setTypeface(thinTf);

        float offset = toPx(PLUS_H_OFFSET);
        if (firstVal < 10) {
            offset += paint.measureText(OFFSET_DIGIT);
        }

        canvas.drawText(PLUS_SYM, offset, toPx(PLUS_V_OFFSET), paint);
    }

    private void drawDigit(Canvas canvas, Paint paint, int value, boolean drawZero, float xOffset) {
        paint.setTextSize(toPx(DIGIT_SIZE));
        paint.setTypeface(thinTf);
        paint.setTextAlign(Paint.Align.RIGHT);

        String text = Integer.toString(value);
        if (drawZero && (value < 10)) {
            text = "0" + text;
        }

        canvas.drawText(text, toPx(xOffset), toPx(DIGIT_V_OFFSET), paint);
    }

    private void drawSub(Canvas canvas, Paint paint, String text, float xOffset) {
        paint.setTextSize(toPx(SUB_SIZE));
        paint.setTypeface(lightTf);
        paint.setTextAlign(Paint.Align.LEFT);

        canvas.drawText(text, toPx(xOffset), toPx(DIGIT_V_OFFSET), paint);
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
