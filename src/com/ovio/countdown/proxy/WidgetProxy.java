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
import com.ovio.countdown.service.*;
import com.ovio.countdown.util.Util;

/**
 * Countdown
 * com.ovio.countdown
 */
public abstract class WidgetProxy implements Blinking, Notifying, SecondsCounting, Updating {

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

    private final static String TAG = Logger.PREFIX + "proxy";

    private Context context;

    private int layout;

    private final PendingIntent preferencesIntent;

    private final int widgetId;

    private long currentNotifyTimestamp;

    private final Scheduler scheduler;

    private final NotifyScheduler notifyScheduler;

    private final SecondCounter secondCounter;

    private final Blinker blinker;

    public WidgetProxy(Context context, int layout, int widgetId, Event event) {
        Logger.d(TAG, "Instantiated WidgetProxy for widget %s, event '%s'", widgetId, event.getTitle());

        this.context = context;
        this.layout = layout;
        this.widgetId = widgetId;
        this.event = event;

        preferencesIntent = getPreferencesIntent(context, widgetId);

        currentNotifyTimestamp = getNextNotifyTimestamp();

        scheduler = Scheduler.getInstance(context);
        notifyScheduler = NotifyScheduler.getInstance(context);
        secondCounter = new SecondCounter(context);
        blinker = new Blinker(context);
    }

    public void onCreate() {
        updateWidget();

        if (isAlive()) {
            scheduler.register(widgetId, this);
        }

        if (isNotifying()) {
            notifyScheduler.register(widgetId, this);
        }

    }

    private void updateWidget() {
        updateWidget(System.currentTimeMillis());
    }

    public void onDelete() {
        scheduler.unRegister(widgetId);
        notifyScheduler.unRegister(widgetId);

    }

    @Override
    public void onUpdate(long timestamp) {
        updateWidget(timestamp);

        if (!isAlive()) {
            scheduler.unRegister(widgetId);
        }
    }

    @Override
    public void onNotify() {
        notifyWidget();

        if (!isNotifying()) {
            notifyScheduler.unRegister(widgetId);
        }
    }

    @Override
    public void onNextSecond() {
        // TODO
    }

    private void notifyWidget() {

        if (event.isNotifying()) {
            Logger.i(TAG, "Px[%s]: Event has notification", widgetId);

            NotifyManager manager = NotifyManager.getInstance(context);
            manager.show(widgetId, currentNotifyTimestamp, event.getTitle());
            currentNotifyTimestamp = getNextNotifyTimestamp();
        }
    }

    public synchronized void updateWidget(long now) {
        Logger.i(TAG, "Px[%s]: Updating widget", widgetId);

        //long now = System.currentTimeMillis();
        long target = event.getTargetTimestamp();

        if (event.isPaused()) {
            now = target;
        }

        updateWidgetTime(now, target);
    }

    private synchronized void updateWidgetTime(long now, long target) {
        Logger.i(TAG, "Px[%s]: Updating widget Time only", widgetId);

        // I don't know why, but instantiating new RemoteViews works A LOT FASTER then reusing existing!
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), layout);

        views.setTextViewText(R.id.titleTextView, event.getTitle());
        views.setOnClickPendingIntent(R.id.widgetLayout, preferencesIntent);

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

    @Override
    public void onBlink(boolean show) {
        this.showText = show;
        updateWidget();
    }

    @Override
    public long getNextUpdateTimestamp() {

        // TODO: Return time in past if expired

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

    @Override
    public long getNextNotifyTimestamp() {
        return event.getNotificationTimestamp();
    }

    private boolean isCountingSeconds() {
        return event.isCountingSeconds() && doCountSeconds && event.isAlive();
    }

    private boolean isNotifying() {
        return event.isNotifying() && (event.getNotificationTimestamp() > System.currentTimeMillis());
    }


    private boolean isAlive() {
        return event.isAlive();
    }

    private boolean isBlinking() {

        if (event.isPaused()) {
            Logger.i(TAG, "Px[%s]: Blinking", widgetId);
            return true;
        } else {
            Logger.i(TAG, "Px[%s]: No blinking", widgetId);
            return false;
        }
    }


    private PendingIntent getPreferencesIntent(Context context, int id) {
        Logger.d(TAG, "Px[%s]: Creating PendingIntent", id);

        Bundle extras = new Bundle();
        extras.putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, id);

        Intent prefIntent = new Intent(context, WidgetPreferencesActivity.class);
        prefIntent.putExtras(extras);

        return PendingIntent.getActivity(context, id, prefIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
    }

}
