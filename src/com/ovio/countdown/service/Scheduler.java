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

    private final AlarmManager alarmManager;

    private final PendingIntent updatingPendingIntent;

    private final PendingIntent notifyingPendingIntent;

    private Scheduler(Context context) {
        Logger.d(TAG, "Instantiated Scheduler");

        alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(WidgetService.ALARM);
        updatingPendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

        Intent notifyIntent = new Intent(WidgetService.NOTIFY);
        notifyingPendingIntent = PendingIntent.getBroadcast(context, 0, notifyIntent, 0);
    }

    public static synchronized Scheduler getInstance(Context context) {
        if (instance == null) {
            instance = new Scheduler(context);
        }
        Logger.d(TAG, "Returning Scheduler instance");
        return instance;
    }

    public long scheduleUpdate(Collection<WidgetProxy> widgetProxies) {
        Logger.i(TAG, "Scheduling next update alarm for one of %s proxies", widgetProxies.size());

        // Get minimum
        long nearestUpdateTimestamp = Long.MAX_VALUE;

        for (WidgetProxy proxy: widgetProxies) {
            if (proxy.isAlive()) {
                long next = proxy.getNextUpdateTimestamp();
                if (next < nearestUpdateTimestamp) {
                    nearestUpdateTimestamp = next;
                }
            }
        }

        if (nearestUpdateTimestamp < Long.MAX_VALUE) {

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

    public long scheduleNotify(Collection<WidgetProxy> widgetProxies) {
        Logger.i(TAG, "Scheduling next Notify alarm for one of %s proxies", widgetProxies.size());

        // Get minimum
        long nearestNotifyTimestamp = Long.MAX_VALUE;
        for (WidgetProxy proxy: widgetProxies) {
            if (proxy.isAlive() && proxy.isNotifying()) {
                long next = proxy.getNextUpdateTimestamp();
                if (next < nearestNotifyTimestamp) {
                    nearestNotifyTimestamp = next;
                }
            }
        }

        if (nearestNotifyTimestamp < Long.MAX_VALUE) {

            if (Logger.DEBUG) {
                Time time = new Time();
                time.set(nearestNotifyTimestamp);
                Logger.i(TAG, "Next Notify at: %s", time.format(Util.TF));
            }

            alarmManager.set(AlarmManager.RTC_WAKEUP, nearestNotifyTimestamp, notifyingPendingIntent);

        } else {
            alarmManager.cancel(notifyingPendingIntent);
            Logger.i(TAG, "Unscheduled all Notify alarms");
        }

        Logger.d(TAG, "Finished Scheduling");

        return nearestNotifyTimestamp;
    }

}
