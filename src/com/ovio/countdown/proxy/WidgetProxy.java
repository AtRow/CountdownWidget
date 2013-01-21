package com.ovio.countdown.proxy;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.text.format.Time;
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

    public boolean isAlive = false;


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
        Logger.d(TAG, "Px[%s]: Set remaining to: %s", seconds);

        views.setCharSequence(R.id.counterTextView, "setText", seconds);

        appWidgetManager.updateAppWidget(options.widgetId, views);

        calculateNextUpdateTime();
    }

    public synchronized void updateWidgetSecondsOnly() {
        Logger.d(TAG, "Px[%s]: Updating widget Seconds only", options.widgetId);

        views.setCharSequence(R.id.counterTextView, "setText", formatSeconds(options.timestamp - System.currentTimeMillis()));
        appWidgetManager.updateAppWidget(options.widgetId, views);
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
        if (true) {
            isAlive = true;
            isCountingSeconds = true;
        }

        if (Logger.DEBUG) {
            Time time = new Time();
            time.set(nextUpdateTimestamp);
            Logger.i(TAG, "Px[%s]: Updated nextUpdateTime to %s", options.widgetId, time.format(Util.TF));
        }

    }

}
