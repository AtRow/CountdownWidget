package com.ovio.countdown.proxy.painter;

import android.content.Context;
import android.graphics.Typeface;
import com.ovio.countdown.R;

public abstract class AbstractWidgetPainter implements WidgetPainter {

    protected Typeface thinTf;
    protected Typeface lightTf;
    protected Typeface condensedTf;
    protected Context context;
    protected float density;

    AbstractWidgetPainter(Context context) {
        this.context = context;

        thinTf = Typeface.createFromAsset(context.getAssets(), "fonts/roboto-thin-reduced.ttf");
        lightTf = Typeface.createFromAsset(context.getAssets(), "fonts/roboto-light-reduced.ttf");
        condensedTf = Typeface.createFromAsset(context.getAssets(), "fonts/robotocondensed-regular-reduced.ttf");

        density = context.getResources().getDisplayMetrics().density;
    }

    protected int toPx(float dp) {
        return (int) ((dp * density) + 0.5);
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
}