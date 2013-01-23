package com.ovio.countdown.proxy;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.text.format.Time;
import android.widget.RemoteViews;
import com.ovio.countdown.R;
import com.ovio.countdown.date.TimeDifference;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.preferences.WidgetOptions;
import com.ovio.countdown.util.Util;

/**
 * Countdown
 * com.ovio.countdown
 */
public abstract class WidgetProxy {

    public long nextUpdateTimestamp;

    public boolean isCountingSeconds = false;

    public boolean isAlive = true;


    protected WidgetOptions options;

    protected int maxCountingVal;

    protected int minCountingVal;


    private final static String TAG = Logger.PREFIX + "proxy";

    private Context context;

    private int layout;

    public WidgetProxy(Context context, int layout, WidgetOptions options) {
        Logger.d(TAG, "Instantiated WidgetProxy with options %s", options);

        this.context = context;
        this.layout = layout;

        this.options = options;
        updateWidget();
    }

    public synchronized void updateWidget() {
        Logger.i(TAG, "Px[%s]: Updating widget", options.widgetId);

        // I don't know why, but instantiating new RemoteViews works A LOT FASTER then reusing existing!
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), layout);

        views.setTextViewText(R.id.titleTextView, options.title);
        updateTimeViews(views, options.timestamp);

        appWidgetManager.updateAppWidget(options.widgetId, views);

        calculateNextUpdateTime();
    }

    public synchronized void updateWidgetTimeOnly() {
        Logger.d(TAG, "Px[%s]: Updating widget Time only", options.widgetId);

        // I don't know why, but instantiating new RemoteViews works A LOT FASTER then reusing existing!
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), layout);

        updateTimeViews(views, options.timestamp);
        appWidgetManager.updateAppWidget(options.widgetId, views);

    }

    public WidgetOptions getOptions() {
        return options;
    }

    public void setOptions(WidgetOptions options) {
        this.options = options;
        updateWidget();
    }


    private void updateTimeViews(RemoteViews views, long timestamp) {
        TimeDifference diff = TimeDifference.between(System.currentTimeMillis(), timestamp);

        maxCountingVal = getMaxCountingVal(diff);
        minCountingVal = getMinCountingVal(maxCountingVal);

        String text = getTimeText(diff);

        views.setTextViewText(R.id.counterTextView, text);
    }

    protected abstract String getTimeText(TimeDifference diff);

    protected abstract int getMaxCountingVal(TimeDifference diff);

    protected abstract int getMinCountingVal(int maxCountingVal);


    private void calculateNextUpdateTime() {

        nextUpdateTimestamp = System.currentTimeMillis();

        switch (minCountingVal) {

            case Time.SECOND:
            case Time.MINUTE:
                nextUpdateTimestamp = (nextUpdateTimestamp / (1000 * 60) + 1) * (1000 * 60); // next minute, rounded
                break;

            case Time.HOUR:
                nextUpdateTimestamp = (nextUpdateTimestamp / (1000 * 60 * 60) + 1) * (1000 * 60 * 60); // next hour, rounded
                break;

            default:
                nextUpdateTimestamp = (nextUpdateTimestamp / (1000 * 60 * 60 * 24) + 1) * (1000 * 60 * 60 * 24); // next day, rounded
        }

        //TODO
/*
        if (options.timestamp > System.currentTimeMillis()) {
            isAlive = true;
            Logger.d(TAG, "Px[%s]: Target time is not yet reached, widget is alive", options.widgetId);
        } else {
            isAlive = false;
            Logger.d(TAG, "Px[%s]: Target time is already reached, widget is dead", options.widgetId);
        }
*/

        if (minCountingVal == Time.SECOND) {
            isCountingSeconds = true;
        } else {
            isCountingSeconds = false;
        }

        if (Logger.DEBUG) {
            Time time = new Time();
            time.set(nextUpdateTimestamp);
            Logger.i(TAG, "Px[%s]: Updated nextUpdateTime to %s", options.widgetId, time.format(Util.TF));
        }

    }

}
