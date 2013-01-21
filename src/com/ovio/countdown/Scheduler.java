package com.ovio.countdown;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.format.Time;
import android.util.Log;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.proxy.WidgetProxy;
import com.ovio.countdown.util.Util;

import java.util.Collection;

/**
 * Countdown
 * com.ovio.countdown
 */
public final class Scheduler {

    private static final String TAG = Logger.PREFIX + "Scheduler";

    private static Scheduler instance;

    private AlarmManager alarmManager;

    private PendingIntent updatingPendingIntent;

    private Time time = new Time();

    private Context context;

    private Scheduler(Context context) {
        Logger.d(TAG, "Instantiated Scheduler");

        this.context = context;
        alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(WidgetService.ALARM);
        updatingPendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    public static synchronized Scheduler getInstance(Context context) {
        if (instance == null) {
            instance = new Scheduler(context);
        }
        Logger.d(TAG, "Returning Scheduler instance");
        return instance;
    }

    public long scheduleUpdate(Collection<WidgetProxy> widgetProxies) {
        Logger.d(TAG, "Scheduling next update alarm for one of %s proxies", widgetProxies.size());

        long nearestUpdateTimestamp = -1;

        alarmManager.cancel(updatingPendingIntent);

        // Get minimum
        long min = Long.MAX_VALUE;
        for (WidgetProxy proxy: widgetProxies) {
            if (proxy.isAlive) {
                long mills = proxy.nextUpdateTime.toMillis(false);
                if (mills < min) {
                    min = mills;
                }
            }
        }

        if (min < Long.MAX_VALUE) {

            nearestUpdateTimestamp = min;

            if (Log.isLoggable(TAG, Log.INFO)) {
                time.set(nearestUpdateTimestamp);
                Logger.i(TAG, "Next update at: %s", time.format(Util.TF));
            }

            alarmManager.set(AlarmManager.RTC, nearestUpdateTimestamp, updatingPendingIntent);
        }

        Logger.d(TAG, "Finished Scheduling");

        return nearestUpdateTimestamp;
    }

}
