package com.ovio.countdown.proxy;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.text.format.Time;
import android.util.Log;
import android.widget.RemoteViews;
import com.ovio.countdown.R;
import com.ovio.countdown.preferences.WidgetOptions;

/**
 * Countdown
 * com.ovio.countdown
 */
public abstract class WidgetProxy {

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

    public WidgetProxy(Context context) {
        this.context = context;

        appWidgetManager = AppWidgetManager.getInstance(context);
        views = getRemoteViews();
    }

    public void updateWidget() {

        views.setCharSequence(R.id.titleTextView, "setText", options.title);
        views.setCharSequence(R.id.hiddenTextView, "setText", options.packSettings());

        appWidgetManager.updateAppWidget(options.widgetId, views);
    }

    private RemoteViews getRemoteViews() {

        int layout = appWidgetManager.getAppWidgetInfo(options.widgetId).initialLayout;

        Log.d(TAG, "Loading remote views for widget [" + options.widgetId + "] with layout [" + layout + "]");

        switch (layout) {
            case R.layout.countdown_widget_layout_4x1 :
                return new RemoteViews(context.getPackageName(), R.layout.countdown_widget_layout_4x1);

            default:
                Log.e(TAG, "Layout not found");
                return null;
        }

    }

    public WidgetOptions getOptions() {
        return options;
    }

    public void setOptions(WidgetOptions options) {
        this.options = options;
    }
}
