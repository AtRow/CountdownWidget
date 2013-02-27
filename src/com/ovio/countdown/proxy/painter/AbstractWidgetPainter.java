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
        // TODO
        return context.getString(R.string.sub_year);
    }

    protected String getMonthSub(int value) {
        // TODO
        return context.getString(R.string.sub_month);
    }

    protected String getDaySub(int value) {
        // TODO
        return context.getString(R.string.sub_day);
    }

    protected String getHourSub(int value) {
        // TODO
        return context.getString(R.string.sub_hour);
    }

    protected String getMinuteSub(int value) {
        // TODO
        return context.getString(R.string.sub_minute);
    }

    protected String getSecondSub(int value) {
        // TODO
        return context.getString(R.string.sub_second);
    }

    protected String truncateText(String text, Paint paint, int width) {

        if (paint.measureText(text) <= width) {
            return text;
        }

        if (truncatedMap.containsKey(text) && truncatedMap.get(text).second.equals(width)) {
            return truncatedMap.get(text).first;
        }

        int pos = text.length() - 1;
        float ellipsis = paint.measureText("...");

        do {
            pos--;
        } while (paint.measureText(text, 0, pos) + ellipsis >= width);

        String truncatedText = text.substring(0, pos) + "...";

        if (truncatedMap.size() > 25) {
            truncatedMap.clear();
        }

        truncatedMap.put(text, new Pair<String, Integer>(truncatedText, width));

        return truncatedText;
    }
}