package com.ovio.countdown.proxy;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.text.format.Time;
import android.widget.RemoteViews;
import com.ovio.countdown.R;
import com.ovio.countdown.preferences.WidgetOptions;

/**
 * Countdown
 * com.ovio.countdown
 */
public abstract class WidgetProxy {

    public long nextUpdateTimestamp;

    public WidgetOptions options; //TODO



    private final static String TAG = "WidgetProxy";

    private final Context context;

    private final AppWidgetManager appWidgetManager;

    private final RemoteViews views;

    private Time time;

    private boolean needsUpdate;

    private int highestMeasure;

    private int lowestMeasure;

    private boolean isSubscribed;

    public WidgetProxy(Context context, AppWidgetManager appWidgetManager, RemoteViews views, WidgetOptions options) {
        this.context = context;
        this.appWidgetManager = appWidgetManager;
        this.views = views;
        this.options = options;

        time = new Time();
        calculateNextUpdateTimestamp();
    }

    public void updateWidget() {
        time.setToNow();

        views.setCharSequence(R.id.titleTextView, "setText", options.title);
        views.setCharSequence(R.id.counterTextView, "setText", Long.toString(options.timestamp - time.toMillis(false)));
        views.setCharSequence(R.id.nextTsTextView, "setText", Long.toString(nextUpdateTimestamp - options.timestamp));

        appWidgetManager.updateAppWidget(options.widgetId, views);

        calculateNextUpdateTimestamp();
    }

    private void calculateNextUpdateTimestamp() {
        time.setToNow();
        // 5 mins
        nextUpdateTimestamp = time.toMillis(false) + (1000 * 60 * 5);
    }

}
