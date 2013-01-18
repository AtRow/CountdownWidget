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

    public long nextUpdateTimestamp;

    public WidgetOptions options; //TODO



    private final static String TAG = Logger.PREFIX + "proxy";

    private final Context context;

    private final AppWidgetManager appWidgetManager;

    private final RemoteViews views;

    private Time time;

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

        time = new Time();
        Logger.d(TAG, "Updated Time to: %s", time.format3339(false));

        calculateNextUpdateTimestamp();
    }

    public void updateWidget() {
        Logger.i(TAG, "Px[%s]: Updating widget", options.widgetId);

        time.setToNow();
        Logger.d(TAG, "Px[%s]: Updated Time to: %s", options.widgetId, time.format3339(false));

        views.setCharSequence(R.id.titleTextView, "setText", options.title);
        views.setCharSequence(R.id.counterTextView, "setText", Long.toString(options.timestamp - time.toMillis(false)));
        views.setCharSequence(R.id.nextTsTextView, "setText", Long.toString(nextUpdateTimestamp - options.timestamp));

        appWidgetManager.updateAppWidget(options.widgetId, views);

        calculateNextUpdateTimestamp();
    }

    private void calculateNextUpdateTimestamp() {
        time.setToNow();
        Logger.d(TAG, "Px[%s]: Updated Time to: %s", options.widgetId, time.format3339(false));

        // 5 mins
        nextUpdateTimestamp = time.toMillis(false) + (1000 * 60 * 5);

        if (Log.isLoggable(TAG, Log.INFO)) {
            Time tt = new Time();
            tt.set(nextUpdateTimestamp);

            Logger.i(TAG, "Px[%s]: Updated nextUpdateTimestamp to %s, time: %s",
                    options.widgetId, nextUpdateTimestamp, tt.format3339(false));
        }

    }

}
