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
import com.ovio.countdown.event.Event;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.prefs.WidgetPreferencesActivity;
import com.ovio.countdown.service.NotifyManager;
import com.ovio.countdown.util.Util;

/**
 * Countdown
 * com.ovio.countdown
 */
public abstract class WidgetProxy {

    private boolean doCountSeconds = false;

    private boolean showText = true;

    protected final Event event;

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

    private final int widgetId;

    private long currentNotifyTimestamp;

    public WidgetProxy(Context context, int layout, int widgetId, Event event) {
        Logger.d(TAG, "Instantiated WidgetProxy for widget %s, event '%s'", widgetId, event.getTitle());

        this.context = context;
        this.layout = layout;
        this.widgetId = widgetId;
        this.event = event;

        pendingIntent = getPendingIntent(context, widgetId);

        updateWidget();
    }

    public synchronized void updateWidget() {
        Logger.i(TAG, "Px[%s]: Updating widget", widgetId);

        long now = System.currentTimeMillis();
        long target = event.getTargetTimestamp();

        if (event.isPaused()) {
            now = target;
        }

        if (event.isNotifying()) {
            Logger.i(TAG, "Px[%s]: Event has notification", widgetId);

            long timestamp = event.getNotificationTimestamp();

            if ((currentNotifyTimestamp != timestamp) && (timestamp > System.currentTimeMillis())) {
                currentNotifyTimestamp = timestamp;
            }

            if ((currentNotifyTimestamp != 0) && (currentNotifyTimestamp <= System.currentTimeMillis())) {

                NotifyManager manager = NotifyManager.getInstance(context);
                manager.show(widgetId, currentNotifyTimestamp, event.getTitle());
                currentNotifyTimestamp = 0;
            }
        }

        updateWidgetTime(now, target);
    }

    private synchronized void updateWidgetTime(long now, long target) {
        Logger.i(TAG, "Px[%s]: Updating widget Time only", widgetId);

        // I don't know why, but instantiating new RemoteViews works A LOT FASTER then reusing existing!
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), layout);

        views.setTextViewText(R.id.titleTextView, event.getTitle());
        views.setOnClickPendingIntent(R.id.widgetLayout, pendingIntent);

        TimeDifference diff = TimeDifference.between(now, target);
        updateCounters(diff);
        String text = getTimeText(diff);

        Logger.i(TAG, "Px[%s]: Setting text: '%s'", widgetId, text);

        views.setTextViewText(R.id.counterTextView, text);

        if (showText) {
            views.setInt(R.id.counterTextView, "setVisibility", View.VISIBLE);
        } else {
            views.setInt(R.id.counterTextView, "setVisibility", View.INVISIBLE);
        }

        appWidgetManager.updateAppWidget(widgetId, views);

    }

    protected abstract void updateCounters(TimeDifference diff);

    protected abstract String getTimeText(TimeDifference diff);

    void setCountSeconds(boolean doCount) {
        this.doCountSeconds = doCount;
    }

    public boolean isCountingSeconds() {
        return event.isCountingSeconds() && doCountSeconds && event.isAlive();
    }

    public boolean isBlinking() {

        if (event.isPaused()) {
            Logger.i(TAG, "Px[%s]: Blinking", widgetId);
            return true;
        } else {
            Logger.i(TAG, "Px[%s]: No blinking", widgetId);
            return false;
        }
    }

    public void blink(boolean show) {
        this.showText = show;
        updateWidget();
    }

    public boolean isAlive() {
        return event.isAlive();
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
            nextUpdateTimestamp = event.getPausedTill();
        } else {
            nextUpdateTimestamp += nextIncrement;
        }

        if (Logger.DEBUG) {
            Time time = new Time();
            time.set(nextUpdateTimestamp);
            Logger.i(TAG, "Px[%s]: Updated nextUpdateTime to %s", widgetId, time.format(Util.TF));
        }

        return nextUpdateTimestamp;
    }

    public int getWidgetId() {
        return widgetId;
    }

    public long getNextNotifyTimestamp() {
        return event.getNotificationTimestamp();
    }

    public boolean isNotifying() {
        return event.isNotifying() && (event.getNotificationTimestamp() > System.currentTimeMillis());
    }
}
