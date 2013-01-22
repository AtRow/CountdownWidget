package com.ovio.countdown.proxy;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.text.format.Time;
import android.util.Log;
import android.widget.RemoteViews;
import com.ovio.countdown.R;
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


    private WidgetOptions options;

    private final static String TAG = Logger.PREFIX + "proxy";

    private final AppWidgetManager appWidgetManager;

    private final RemoteViews views;

    public WidgetProxy(Context context, AppWidgetManager appWidgetManager, RemoteViews views, WidgetOptions options) {
        Logger.d(TAG, "Instantiated WidgetProxy with options %s", options);

        this.appWidgetManager = appWidgetManager;
        this.views = views;
        this.options = options;

        updateWidget();
    }

    public synchronized void updateWidget() {
        Logger.i(TAG, "Px[%s]: Updating widget", options.widgetId);

        views.setCharSequence(R.id.titleTextView, "setText", options.title);

        long now = System.currentTimeMillis();

        String seconds = formatSeconds(options.timestamp - now);
        Logger.d(TAG, "Px[%s]: Set remaining to: %s", options.widgetId, seconds);

        views.setCharSequence(R.id.counterTextView, "setText", seconds);

        appWidgetManager.updateAppWidget(options.widgetId, views);

        calculateNextUpdateTime();
    }

    public synchronized void updateWidgetSecondsOnly(long second) {
        Logger.d(TAG, "Px[%s]: Updating widget Seconds only", options.widgetId);

        long before = System.currentTimeMillis();

        views.setTextViewText(R.id.counterTextView, formatSeconds(options.timestamp - second));
        appWidgetManager.updateAppWidget(options.widgetId, views);

        Log.w(TAG, "U: " + options.widgetId + " : " + (System.currentTimeMillis() - before));
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
