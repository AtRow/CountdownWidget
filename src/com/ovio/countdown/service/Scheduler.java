package com.ovio.countdown.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.format.Time;
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

    private Scheduler(Context context) {
        Logger.d(TAG, "Instantiated Scheduler");

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

        // Get minimum
        long min = Long.MAX_VALUE;
        for (WidgetProxy proxy: widgetProxies) {
            if (proxy.isAlive) {
                if (proxy.nextUpdateTimestamp < min) {
                    min = proxy.nextUpdateTimestamp;
                }
            }
        }

        if (min < Long.MAX_VALUE) {

            nearestUpdateTimestamp = min;

            if (Logger.DEBUG) {
                Time time = new Time();
                time.set(nearestUpdateTimestamp);
                Logger.i(TAG, "Next update at: %s", time.format(Util.TF));
            }

            alarmManager.set(AlarmManager.RTC, nearestUpdateTimestamp, updatingPendingIntent);

        } else {
            alarmManager.cancel(updatingPendingIntent);
            Logger.i(TAG, "Unscheduled all update alarms");
        }

        Logger.d(TAG, "Finished Scheduling");

        return nearestUpdateTimestamp;
    }

}
