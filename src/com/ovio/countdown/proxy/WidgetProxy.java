package com.ovio.countdown.proxy;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.widget.RemoteViews;
import com.ovio.countdown.R;
import com.ovio.countdown.date.TimeDifference;
import com.ovio.countdown.event.Event;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.prefs.IconData;
import com.ovio.countdown.prefs.StyleData;
import com.ovio.countdown.prefs.WidgetPreferencesActivity;
import com.ovio.countdown.proxy.painter.WidgetPainter;
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

    protected long nextIncrement;

    protected Context context;

    protected final static int SECOND = 1000;
    protected final static int MINUTE = (SECOND * 60);
    protected final static int HOUR = (MINUTE * 60);
    protected final static int DAY = (HOUR * 24);

    private static final long PAUSE = DateUtils.SECOND_IN_MILLIS * 10;

    private final static String TAG = Logger.PREFIX + "proxy";

    private int layout;

    private final PendingIntent preferencesIntent;

    private final int widgetId;

    private long currentNotifyTimestamp;

    private long currentTargetTimestamp;

    private long pauseTimestamp;

    private final Scheduler scheduler;

    private final NotifyScheduler notifyScheduler;

    private final SecondCounter secondCounter;

    private final Blinker blinker;

    private boolean reachedTarget = false;

    private Bitmap currentBitmap;

    private Bitmap icon;

    private int styleRes;


    public WidgetProxy(Context context, int layout, int widgetId, Event event) {
        Logger.d(TAG, "Instantiated WidgetProxy for widget %s, event '%s'", widgetId, event.getTitle());

        this.context = context;
        this.layout = layout;
        this.widgetId = widgetId;
        this.event = event;

        preferencesIntent = getPreferencesIntent(context, widgetId);

        currentNotifyTimestamp = getNextNotifyTimestamp();
        currentTargetTimestamp = event.getTargetTimestamp();

        int iconRes = IconData.getInstance().getResource(event.getIcon());
        if (iconRes != IconData.NONE) {
            icon = BitmapFactory.decodeResource(context.getResources(), iconRes);
        }

        styleRes = StyleData.getInstance().getResource(event.getStyle());

        isTargetReached();

        scheduler = Scheduler.getInstance(context);
        notifyScheduler = NotifyScheduler.getInstance(context);
        secondCounter = SecondCounter.getInstance(context);
        blinker = Blinker.getInstance(context);
    }

    public void onCreate() {
        updateWidget();

        if (isAlive()) {
            scheduler.register(widgetId, this);
        }

        if (isNotifying()) {
            notifyScheduler.register(widgetId, this);
        }

        if (isCountingSeconds()) {
            secondCounter.register(widgetId, this);
        }

        if (isBlinking()) {
            blinker.register(widgetId, this);
        }
    }

    public void onDelete() {
        scheduler.unRegister(widgetId);
        notifyScheduler.unRegister(widgetId);
        secondCounter.unRegister(widgetId);
        blinker.unRegister(widgetId);
    }

    @Override
    public void onUpdate(long timestamp) {

        if (isTargetReached()) {
            pause();
        }

        updateWidget(timestamp);

        if (isCountingSeconds() && !isBlinking()) {
            secondCounter.register(widgetId, this);
        } else {
            secondCounter.unRegister(widgetId);
        }

        if (isBlinking()) {
            blinker.register(widgetId, this);
        } else {
            blinker.unRegister(widgetId);
        }

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
    public void onNextSecond(long timestamp) {

        if (isTargetReached()) {
            pause();
        }

        updateWidgetSeconds(timestamp);
    }

    @Override
    public void onBlink(boolean show) {
        this.showText = show;
        updateWidget();

        if (!isBlinking()) {
            blinker.unRegister(widgetId);
        }
    }

    @Override
    public long getNextUpdateTimestamp() {

        // TODO: Return time in past if expired

        long nextUpdateTimestamp = (System.currentTimeMillis() / MINUTE) * MINUTE; // rounded to minute

        if (isBlinking()) {
            nextUpdateTimestamp = pauseTimestamp;
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

    private void updateWidgetSeconds(long now) {

        long target = currentTargetTimestamp;

        if (isBlinking() || !isAlive()) {
            now = target;
        }

        drawWidget(now, target, true);
    }

    private synchronized void updateWidget(long now) {
        Logger.i(TAG, "Px[%s]: Updating widget", widgetId);

        //long now = System.currentTimeMillis();
        long target = event.getTargetTimestamp();

        if (isBlinking() || !isAlive()) {
            now = target;
        }

        drawWidget(now, target, false);
    }

    private void updateWidget() {
        updateWidget(System.currentTimeMillis());
    }

    private synchronized void drawWidget(long now, long target, boolean timeOnly) {
        Logger.i(TAG, "Px[%s]: Updating widget", widgetId);
        if (timeOnly) {
            Logger.i(TAG, "Px[%s]: Time only", widgetId);
        }

        // I don't know why, but instantiating new RemoteViews works A LOT FASTER then reusing existing!
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), layout);

        views.setOnClickPendingIntent(R.id.widgetLayout, preferencesIntent);

        TimeDifference diff = TimeDifference.between(now, target);
        updateCounters(diff);

        WidgetPainter widgetPainter = getWidgetPainter();

        if ((currentBitmap == null) || !timeOnly) {
            currentBitmap = widgetPainter.getNewBitmap(context, styleRes);
            widgetPainter.drawHeader(currentBitmap, event.getTitle(), icon);
        }

        Bitmap bitmap = currentBitmap.copy(currentBitmap.getConfig(), true);

        if (showText) {
            widgetPainter.drawTime(bitmap, diff, maxCountingVal);
        }

        views.setImageViewBitmap(R.id.imageView, bitmap);
        appWidgetManager.updateAppWidget(widgetId, views);
    }

    private void notifyWidget() {

        if (event.isNotifying()) {
            Logger.i(TAG, "Px[%s]: Event has notification", widgetId);

            NotifyManager manager = NotifyManager.getInstance(context);
            manager.show(widgetId, currentNotifyTimestamp, event.getTitle());
            currentNotifyTimestamp = getNextNotifyTimestamp();
        }
    }

    protected abstract void updateCounters(TimeDifference diff);

    protected abstract WidgetPainter getWidgetPainter();

    void setCountSeconds(boolean doCount) {
        this.doCountSeconds = doCount;
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

    private boolean isTargetReached() {

        if (!reachedTarget && (System.currentTimeMillis() >= currentTargetTimestamp)) {
            long newTimestamp = event.getTargetTimestamp();
            if (newTimestamp > currentTargetTimestamp) {
                currentTargetTimestamp = newTimestamp;
                reachedTarget = false;
                return true;

            } else {
                reachedTarget = true;
                return true;
            }
        }
        return false;
    }

    private boolean isBlinking() {
        return System.currentTimeMillis() < pauseTimestamp;
    }

    private void pause() {
        pauseTimestamp = System.currentTimeMillis() + PAUSE;

        if (Logger.DEBUG) {
            Time time = new Time();
            time.set(pauseTimestamp);
            Logger.i(TAG, "Px[%s]: Widget will be paused till: %s", widgetId, time.format(Util.TF));
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
