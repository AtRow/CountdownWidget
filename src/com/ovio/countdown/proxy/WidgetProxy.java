package com.ovio.countdown.proxy;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.text.format.Time;
import android.widget.RemoteViews;
import com.ovio.countdown.R;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.preferences.WidgetOptions;
import com.ovio.countdown.util.Util;

import java.util.TimeZone;

/**
 * Countdown
 * com.ovio.countdown
 */
public abstract class WidgetProxy {

    public long nextUpdateTimestamp;

    public boolean isCountingSeconds = false;

    public boolean isAlive = true;


    private WidgetOptions options;

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

        long now = System.currentTimeMillis();

        String time = getTimeString(options.timestamp);

        //String seconds = formatSeconds(options.timestamp - now);
        Logger.d(TAG, "Px[%s]: Set remaining to: %s", options.widgetId, time);

        views.setTextViewText(R.id.counterTextView, time);

        appWidgetManager.updateAppWidget(options.widgetId, views);

        calculateNextUpdateTime();
    }

    protected String getTimeString(long targetMills) {

        // TODO cache time, update only seconds

        long nowMills = System.currentTimeMillis();

        TimeZone tz = TimeZone.getTimeZone(Time.getCurrentTimezone());

        /*DateTime now = DateTime.now(tz);
        DateTime target = DateTime.forInstant(targetMills, tz);

        now.minus()

        int result = 0;
        if(now.isSameDayAs(target)){
            // do nothing
        }
        else if (now.lt(target)){
            result = now.numDaysFrom(target);
        }
        else if (now.gt(target)){
            DateTime christmasNextYear = DateTime.forDateOnly(now.getYear() + 1, 12, 25);
            result = now.numDaysFrom(christmasNextYear);
        }

        StringBuilder sb = new StringBuilder();

        sb.append(years).append(" Y ")
                .append(months).append(" M ")
                .append(days).append(" D ")
                .append(hours).append(":")
                .append(mins).append(":")
                .append(secs);*/

        return null;
    }

    public synchronized void updateWidgetSecondsOnly(long second) {
        Logger.d(TAG, "Px[%s]: Updating widget Seconds only", options.widgetId);

        //long before = System.currentTimeMillis();

        // I don't know why, but instantiating new RemoteViews works A LOT FASTER then reusing existing!
//        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
//        RemoteViews views = new RemoteViews(context.getPackageName(), layout);
//
//        views.setTextViewText(R.id.counterTextView, formatSeconds(options.timestamp - second));
//        appWidgetManager.updateAppWidget(options.widgetId, views);

        //Log.w(TAG, "U: " + options.widgetId + " : " + (System.currentTimeMillis() - before));
    }

    public WidgetOptions getOptions() {
        return options;
    }

    public void setOptions(WidgetOptions options) {
        this.options = options;
        updateWidget();
    }

    private String formatSeconds(long mills) {
        if (mills <= 0) {
            return "0";
        }
        long seconds = mills / 1000;
        return Long.toString(seconds);
    }

    private void calculateNextUpdateTime() {

        nextUpdateTimestamp = System.currentTimeMillis();

        // TODO
        nextUpdateTimestamp += 1000 * 60; // 1 min

        //TODO
        if (options.timestamp > System.currentTimeMillis()) {
            isAlive = true;
            Logger.d(TAG, "Px[%s]: Target time is not yet reached, widget is alive", options.widgetId);
        } else {
            isAlive = false;
            Logger.d(TAG, "Px[%s]: Target time is already reached, widget is dead", options.widgetId);
        }

        if (options.enableSeconds) {
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
