package com.ovio.countdown.proxy;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.widget.RemoteViews;
import com.ovio.countdown.R;
import com.ovio.countdown.WidgetPreferencesActivity;
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

    protected int nextIncrement;

    protected final static int SECOND = 1000;
    protected final static int MINUTE = (1000 * 60);
    protected final static int HOUR = (1000 * 60 * 60);
    protected final static int DAY = (1000 * 60 * 60 * 24);


    private final static String TAG = Logger.PREFIX + "proxy";

    private Context context;

    private int layout;

    private final PendingIntent pendingIntent;

    public WidgetProxy(Context context, int layout, WidgetOptions options) {
        Logger.d(TAG, "Instantiated WidgetProxy with options %s", options);

        this.context = context;
        this.layout = layout;

        this.options = options;

        pendingIntent = getPendingIntent(context, options.widgetId);

        updateWidget();
    }

    public synchronized void updateWidget() {
        Logger.i(TAG, "Px[%s]: Updating widget", options.widgetId);

        // I don't know why, but instantiating new RemoteViews works A LOT FASTER then reusing existing!
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), layout);

        views.setTextViewText(R.id.titleTextView, options.title);
        views.setOnClickPendingIntent(R.id.widgetLayout, pendingIntent);

        TimeDifference diff = TimeDifference.between(System.currentTimeMillis(), options.timestamp);
        updateCounters(diff);
        String text = getTimeText(diff);
        views.setTextViewText(R.id.counterTextView, text);

        appWidgetManager.updateAppWidget(options.widgetId, views);

        calculateNextUpdateTime();
    }

    public synchronized void updateWidgetTimeOnly() {
        Logger.d(TAG, "Px[%s]: Updating widget Time only", options.widgetId);

        // I don't know why, but instantiating new RemoteViews works A LOT FASTER then reusing existing!
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), layout);

        views.setTextViewText(R.id.titleTextView, options.title);
        views.setOnClickPendingIntent(R.id.widgetLayout, pendingIntent);

        TimeDifference diff = TimeDifference.between(System.currentTimeMillis(), options.timestamp);
        updateCounters(diff);
        String text = getTimeText(diff);
        views.setTextViewText(R.id.counterTextView, text);

        appWidgetManager.updateAppWidget(options.widgetId, views);

    }

    public WidgetOptions getOptions() {
        return options;
    }

    public void setOptions(WidgetOptions options) {
        this.options = options;
        updateWidget();
    }


    protected abstract void updateCounters(TimeDifference diff);

    protected abstract String getTimeText(TimeDifference diff);

    void setCountSeconds(boolean doCount) {
        if (options.enableSeconds && doCount) {
            isCountingSeconds = true;
        } else {
            isCountingSeconds = false;
        }
    }

    private PendingIntent getPendingIntent(Context context, int id) {
        Logger.d(TAG, "Px[%s]: Creating PendingIntent", id);

        Bundle extras = new Bundle();
        extras.putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, id);

        Intent prefIntent = new Intent(context, WidgetPreferencesActivity.class);
        prefIntent.putExtras(extras);

        return PendingIntent.getActivity(context, id, prefIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    private void calculateNextUpdateTime() {

        nextUpdateTimestamp = (System.currentTimeMillis() / (1000 * 60)) * (1000 * 60); // rounded to minute

        nextUpdateTimestamp += nextIncrement;

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

        if (Logger.DEBUG) {
            Time time = new Time();
            time.set(nextUpdateTimestamp);
            Logger.i(TAG, "Px[%s]: Updated nextUpdateTime to %s", options.widgetId, time.format(Util.TF));
        }

    }

}
