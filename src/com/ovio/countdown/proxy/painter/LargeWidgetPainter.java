package com.ovio.countdown.proxy.painter;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.NinePatchDrawable;
import android.text.format.Time;
import com.ovio.countdown.date.TimeDifference;
import com.ovio.countdown.log.Logger;

/**
 * Countdown
 * com.ovio.countdown
 */
public class LargeWidgetPainter extends AbstractWidgetPainter {

    private static final String TAG = Logger.PREFIX + "LargePainter";

    private static final int PLUS_SIZE = 33;
    private static final int DIGIT_SIZE = 50;
    private static final int TITLE_SIZE = 14;
    private static final int SUB_SIZE = 11;

    private static final int PLUS_V_OFFSET = 59;
    private static final int DIGIT_V_OFFSET = 65;

    private static final int TITLE_H_OFFSET = 10;
    private static final int TITLE_V_OFFSET = 18;
    private static final int TITLE_WIDTH = 276;

    private static final int ICON_H_OFFSET = 253;
    private static final int ICON_V_OFFSET = 9;
    private static final int ICON_SIZE = 28;

    private static final String PLUS_SYM = "+";
    private static final String OFFSET_DIGIT = "0";

    private static final int PLUS_H_OFFSET = 10;
    private static final int FIRST_H_OFFSET = 80;
    private static final int SECOND_H_OFFSET = 164;
    private static final int THIRD_H_OFFSET = 249;

    private static final int BITMAP_WIDTH = 290;
    private static final int BITMAP_HEIGHT = 72;


    private static LargeWidgetPainter instance;


    private LargeWidgetPainter(Context context) {
        super(context);
        Logger.i(TAG, "Instantiated LargeWidgetPainter");
    }

    public static synchronized LargeWidgetPainter getInstance(Context context) {
        if (instance == null) {
            instance = new LargeWidgetPainter(context);
        }
        Logger.i(TAG, "Returning LargeWidgetPainter instance");
        return instance;
    }


    @Override
    public Bitmap getNewBitmap(Context context, int resource) {

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resource);

        byte[] chunk = bitmap.getNinePatchChunk();
        NinePatchDrawable npDrawable = new NinePatchDrawable(bitmap, chunk, new Rect(), null);
        npDrawable.setBounds(0, 0, toPx(BITMAP_WIDTH), toPx(BITMAP_HEIGHT));

        Bitmap outputBitmap = Bitmap.createBitmap(toPx(BITMAP_WIDTH), toPx(BITMAP_HEIGHT), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outputBitmap);
        npDrawable.draw(canvas);

        return outputBitmap;
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
                drawMinute(canvas, paint, diff.mins, true, THIRD_H_OFFSET);
                break;

            case Time.HOUR:
                drawPlus(canvas, paint, diff.positive, diff.hours);

                drawHour(canvas, paint, diff.hours, false, FIRST_H_OFFSET);
                drawMinute(canvas, paint, diff.mins, true, SECOND_H_OFFSET);
                drawSecond(canvas, paint, diff.secs, true, THIRD_H_OFFSET);
                break;
        }

        return bitmap;
    }

    @Override
    public Bitmap drawHeader(Bitmap bitmap, String title, Bitmap icon) {

        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setSubpixelText(true);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setLinearText(true);

        paint.setTextSize(toPx(TITLE_SIZE));
        paint.setTypeface(condensedTf);

        int titleWidth = toPx(TITLE_WIDTH);

        if (icon != null) {
            titleWidth -= toPx(ICON_SIZE + 5);
            Bitmap scaledIcon = Bitmap.createScaledBitmap(icon, toPx(ICON_SIZE), toPx(ICON_SIZE), false);
            canvas.drawBitmap(scaledIcon, toPx(ICON_H_OFFSET), toPx(ICON_V_OFFSET), paint);
        }

        String text = truncateText(title, paint, titleWidth, true);
        
        canvas.drawText(text, toPx(TITLE_H_OFFSET), toPx(TITLE_V_OFFSET), paint);

        return bitmap;
    }

    private void drawYear(Canvas canvas, Paint paint, int value, int xOffset) {

        drawDigit(canvas, paint, value, false, xOffset);
        drawSub(canvas, paint, getYearSub(value), xOffset);
    }

    private void drawMonth(Canvas canvas, Paint paint, int value, boolean drawZero, int xOffset) {

        drawDigit(canvas, paint, value, drawZero, xOffset);
        drawSub(canvas, paint, getMonthSub(value), xOffset);
    }

    private void drawDay(Canvas canvas, Paint paint, int value, boolean drawZero, int xOffset) {

        drawDigit(canvas, paint, value, drawZero, xOffset);
        drawSub(canvas, paint, getDaySub(value), xOffset);
    }

    private void drawHour(Canvas canvas, Paint paint, int value, boolean drawZero, int xOffset) {

        drawDigit(canvas, paint, value, drawZero, xOffset);
        drawSub(canvas, paint, getHourSub(value), xOffset);
    }

    private void drawMinute(Canvas canvas, Paint paint, int value, boolean drawZero, int xOffset) {

        drawDigit(canvas, paint, value, drawZero, xOffset);
        drawSub(canvas, paint, getMinuteSub(value), xOffset);
    }

    private void drawSecond(Canvas canvas, Paint paint, int value, boolean drawZero, int xOffset) {

        drawDigit(canvas, paint, value, drawZero, xOffset);
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

    private void drawDigit(Canvas canvas, Paint paint, int value, boolean drawZero, int xOffset) {
        paint.setTextSize(toPx(DIGIT_SIZE));
        paint.setTypeface(thinTf);
        paint.setTextAlign(Paint.Align.RIGHT);

        String text = Integer.toString(value);
        if (drawZero && (value < 10)) {
            text = "0" + text;
        }

        canvas.drawText(text, toPx(xOffset), toPx(DIGIT_V_OFFSET), paint);
    }

    private void drawSub(Canvas canvas, Paint paint, String text, int xOffset) {
        paint.setTextSize(toPx(SUB_SIZE));
        paint.setTypeface(lightTf);
        paint.setTextAlign(Paint.Align.LEFT);

        canvas.drawText(text, toPx(xOffset), toPx(DIGIT_V_OFFSET), paint);
    }

}
