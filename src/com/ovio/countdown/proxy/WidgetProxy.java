package com.ovio.countdown.proxy;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.RemoteViews;
import com.ovio.countdown.R;
import com.ovio.countdown.date.TimeDifference;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.preferences.WidgetOptions;
import com.ovio.countdown.prefs.WidgetPreferencesActivity;
import com.ovio.countdown.util.Util;

/**
 * Countdown
 * com.ovio.countdown
 */
public abstract class WidgetProxy {

    private boolean doCountSeconds = false;

    private boolean showText = true;

    protected WidgetOptions options;

    protected int maxCountingVal;

    protected int minCountingVal;

    protected int nextIncrement;

    protected final static int SECOND = 1000;
    protected final static int MINUTE = (SECOND * 60);
    protected final static int HOUR = (MINUTE * 60);
    protected final static int DAY = (HOUR * 24);

    private static final long BLINKING_MILLS = 15 * SECOND;

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

        long now = System.currentTimeMillis();

        if (isRepeating() && (now >= (options.timestamp + MINUTE))) {
            loadNextEvent();
        }

        if (isAlive()) {
            updateWidgetTime(now);
        } else {
            updateWidgetTime(options.timestamp);
        }
    }

    private synchronized void updateWidgetTime(long now) {
        Logger.i(TAG, "Px[%s]: Updating widget Time only", options.widgetId);

        // I don't know why, but instantiating new RemoteViews works A LOT FASTER then reusing existing!
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), layout);

        views.setTextViewText(R.id.titleTextView, options.title);
        views.setOnClickPendingIntent(R.id.widgetLayout, pendingIntent);

        TimeDifference diff = TimeDifference.between(now, options.timestamp);
        updateCounters(diff);
        String text = getTimeText(diff);

        Logger.i(TAG, "Px[%s]: Setting text: '%s'", options.widgetId, text);

        views.setTextViewText(R.id.counterTextView, text);

        if (showText) {
            views.setInt(R.id.counterTextView, "setVisibility", View.VISIBLE);
        } else {
            views.setInt(R.id.counterTextView, "setVisibility", View.INVISIBLE);
        }

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
        this.doCountSeconds = doCount;
    }

    public boolean isCountingSeconds() {
        return options.enableSeconds && doCountSeconds && isAlive();
    }

    public boolean isBlinking() {

        long now = System.currentTimeMillis();

        if ((!options.countUp) && (now > options.timestamp) && ((now - options.timestamp) <= BLINKING_MILLS)) {
            Logger.i(TAG, "Px[%s]: Minute or less since finishing, blinking", options.widgetId);
            return true;
        } else {
            Logger.i(TAG, "Px[%s]: No blinking", options.widgetId);
            return false;
        }
    }

    public void blink(boolean show) {
        this.showText = show;
        updateWidget();
    }

    public boolean isAlive() {
        if (options.countUp ||
                isRepeating() ||
                (!options.countUp && (options.timestamp > System.currentTimeMillis()))) {

            Logger.i(TAG, "Px[%s]: Target time is not yet reached, widget is alive", options.widgetId);
            return true;
        } else {
            Logger.i(TAG, "Px[%s]: Target time is already reached, widget is finished", options.widgetId);
            return false;
        }
    }

    private boolean isRepeating() {
        return (options.repeatingPeriod > 0) || (options.isRepeating);
    }

    private PendingIntent getPendingIntent(Context context, int id) {
        Logger.d(TAG, "Px[%s]: Creating PendingIntent", id);

        Bundle extras = new Bundle();
        extras.putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, id);

        Intent prefIntent = new Intent(context, WidgetPreferencesActivity.class);
        prefIntent.putExtras(extras);

        return PendingIntent.getActivity(context, id, prefIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    public long getNextUpdateTimestamp() {

        // TODO: updating methods in Service and Scheduler rely on Widget's isAlive state,
        // TODO: but it could be better to return something like -1 for nextUpdateTimestamp is widget ain't going to update

        long nextUpdateTimestamp = (System.currentTimeMillis() / MINUTE) * MINUTE; // rounded to minute

        if (isBlinking()) {
            nextUpdateTimestamp += BLINKING_MILLS;
        } else {
            nextUpdateTimestamp += nextIncrement;
        }

        if (Logger.DEBUG) {
            Time time = new Time();
            time.set(nextUpdateTimestamp);
            Logger.i(TAG, "Px[%s]: Updated nextUpdateTime to %s", options.widgetId, time.format(Util.TF));
        }

        return nextUpdateTimestamp;
    }

}
