package com.ovio.countdown.proxy.painter;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Pair;
import com.ovio.countdown.R;
import com.ovio.countdown.util.Util;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractWidgetPainter implements WidgetPainter {

    protected Typeface thinTf;
    protected Typeface lightTf;
    protected Typeface condensedTf;
    protected Context context;

    private Map<String, Pair<String, Integer>> truncatedMap = new HashMap<String, Pair<String, Integer>>();

    AbstractWidgetPainter(Context context) {
        this.context = context;

        thinTf = Typeface.createFromAsset(context.getAssets(), "fonts/roboto-thin-reduced.ttf");
        lightTf = Typeface.createFromAsset(context.getAssets(), "fonts/roboto-light-reduced.ttf");
        condensedTf = Typeface.createFromAsset(context.getAssets(), "fonts/robotocondensed-regular-reduced.ttf");
    }

    protected int toPx(int dp) {
        return Util.toPx(context, dp);
    }

    protected String getYearSub(int value) {
        /*
        1 год
        2-4 года
        5-n лет
        21-n l.d.
        1 year
        n years
        */
        int form = WordFormHelper.getWordForm(value);

        switch (form) {
            case WordFormHelper.SINGLE:
                return context.getString(R.string.sub_year_sin);

            case WordFormHelper.SEMI:
                return context.getString(R.string.sub_year_sem);

            default:
                return context.getString(R.string.sub_year_plu);
        }
    }

    protected String getMonthSub(int value) {
        /*
        n мес.
        n mon.
         */
        return context.getString(R.string.sub_month);
    }

    protected String getDaySub(int value) {
        /*
        1 день
        2-4 дня
        5-20 дней
        21-n l.d.
        1 day
        n days
         */
        int form = WordFormHelper.getWordForm(value);

        switch (form) {
            case WordFormHelper.SINGLE:
                return context.getString(R.string.sub_day_sin);

            case WordFormHelper.SEMI:
                return context.getString(R.string.sub_day_sem);

            default:
                return context.getString(R.string.sub_day_plu);
        }
    }

    protected String getHourSub(int value) {
        /*
        1 час
        n час.
        1 hour
        n hrs.
         */
        return (value == 1) ? context.getString(R.string.sub_hour_sin) : context.getString(R.string.sub_hour_plu);
    }

    protected String getMinuteSub(int value) {
        /*
        n мин.
        n min.
         */
        return context.getString(R.string.sub_minute);
    }

    protected String getSecondSub(int value) {
        /*
        n сек.
        n sec.
         */
        return context.getString(R.string.sub_second);
    }

    protected String truncateText(String text, Paint paint, int width, boolean enableEllipsis) {

        if (paint.measureText(text) <= width) {
            return text;
        }

        if (truncatedMap.containsKey(text) && truncatedMap.get(text).second.equals(width)) {
            return truncatedMap.get(text).first;
        }

        int pos = text.length() - 1;
        float ellipsis = (enableEllipsis) ? paint.measureText("...") : 0;

        do {
            pos--;
        } while (paint.measureText(text, 0, pos) + ellipsis >= width);

        String truncatedText = text.substring(0, pos);
        if (enableEllipsis) {
            truncatedText += "...";
        }

        if (truncatedMap.size() > 25) {
            truncatedMap.clear();
        }

        truncatedMap.put(text, new Pair<String, Integer>(truncatedText, width));

        return truncatedText;
    }
}