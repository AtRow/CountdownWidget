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

    private final static String TAG = "WidgetProxy";

    private final Context context;

    private final AppWidgetManager appWidgetManager;

    private final RemoteViews views;

    private WidgetOptions options; //TODO

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
    }

    public void updateWidget() {

        views.setCharSequence(R.id.titleTextView, "setText", options.title);
        views.setCharSequence(R.id.hiddenTextView, "setText", options.packSettings());

        appWidgetManager.updateAppWidget(options.widgetId, views);
    }

    public WidgetOptions getOptions() {
        return options;
    }

    public void setOptions(WidgetOptions options) {
        this.options = options;
    }
}
