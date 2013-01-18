package com.ovio.countdown.proxy;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.text.format.Time;
import android.util.Log;
import android.widget.RemoteViews;
import com.ovio.countdown.R;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.preferences.WidgetOptions;

/**
 * Countdown
 * com.ovio.countdown
 */
public abstract class WidgetProxy {

    public Time nextUpdateTime = new Time();


    private WidgetOptions options; //TODO

    private final static String TAG = Logger.PREFIX + "proxy";

    private final Context context;

    private final AppWidgetManager appWidgetManager;

    private final RemoteViews views;

    private Time time = new Time();

    private boolean needsUpdate;

    private int highestMeasure;

    private int lowestMeasure;

    private boolean isSubscribed;

    public WidgetProxy(Context context, AppWidgetManager appWidgetManager, RemoteViews views, WidgetOptions options) {
        Logger.d(TAG, "Instantiated WidgetProxy with options %s", options);

        this.context = context;
        this.appWidgetManager = appWidgetManager;
        this.views = views;
        this.options = options;

        calculateNextUpdateTime();
    }

    public void updateWidget() {
        Logger.i(TAG, "Px[%s]: Updating widget", options.widgetId);

        time.setToNow();

        Logger.d(TAG, "Px[%s]: Updated Time to: %s", options.widgetId, time.format3339(false));

        views.setCharSequence(R.id.titleTextView, "setText", options.title);
        views.setCharSequence(R.id.counterTextView, "setText", formatSeconds(options.timestamp - time.toMillis(false)));

        String status = "T: " + time.hour + ":" + time.minute + ":" + time.second + ":"
                + " Uts: " + formatSeconds(nextUpdateTime.toMillis(false) - time.toMillis(false));
        views.setCharSequence(R.id.statusText, "setText", status);

        appWidgetManager.updateAppWidget(options.widgetId, views);

        calculateNextUpdateTime();
    }

    public WidgetOptions getOptions() {
        return options;
    }

    public void setOptions(WidgetOptions options) {
        this.options = options;
        calculateNextUpdateTime();
    }

    private String formatSeconds(long mills) {
        long seconds = mills / 1000;
        return Long.toString(seconds);
    }

    private void calculateNextUpdateTime() {
        nextUpdateTime.setToNow();

        // TODO
        nextUpdateTime.minute += 1;
        nextUpdateTime.normalize(false);

        if (Log.isLoggable(TAG, Log.INFO)) {
            Logger.i(TAG, "Px[%s]: Updated nextUpdateTime to %s, time: %s",
                    options.widgetId, nextUpdateTime.format3339(false), time.format3339(false));
        }

    }

}
