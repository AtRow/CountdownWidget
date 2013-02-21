package com.ovio.countdown.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.proxy.Notifying;
import com.ovio.countdown.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

/**
 * Countdown
 * com.ovio.countdown
 */
public final class NotifyScheduler extends BroadcastReceiver {

    public static final String NOTIFY = "com.ovio.countdown.service.NotifyScheduler.NOTIFY";

    private static final String TAG = Logger.PREFIX + "NotifyScheduler";

    private static NotifyScheduler instance;

    private static final String IDS = "IDS";

    private final TreeMap<Integer, Notifying> updatingMap = new TreeMap<Integer, Notifying>();

    private Context context;


    public NotifyScheduler() {
        Logger.d(TAG, "Instantiated NotifyScheduler");
    }

    public static synchronized NotifyScheduler getInstance(Context context) {
        if (instance == null) {
            instance = new NotifyScheduler();
        }
        Logger.d(TAG, "Returning NotifyScheduler instance");

        instance.context = context;
        return instance;
    }

    public synchronized void register(int id, Notifying notifying) {
        Logger.i(TAG, "Registering new Notifying widget with id: %s", id);

        updatingMap.put(id, notifying);
        scheduleUpdate();
    }

    public synchronized void unRegister(int id) {
        Logger.i(TAG, "UnRegistering new Notifying widget with id: %s", id);

        updatingMap.remove(id);
        scheduleUpdate();
    }

    public synchronized void onNotify(Intent intent) {
        Logger.i(TAG, "Starting onNotify");

        int[] ids = getIds(intent);
        Logger.i(TAG, "Will notify widgets: %s", Arrays.toString(ids));

        if (ids != null) {

            for (int i = 0; i < ids.length; i++) {
                Notifying notifying = updatingMap.get(ids[i]);
                if (notifying != null) {
                    Logger.i(TAG, "Notifying widget [%s]", ids[i]);
                    notifying.onNotify();
                }
            }

            scheduleUpdate();
        }
        Logger.i(TAG, "Finished onNotify");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Logger.i(TAG, "Received %s Intent", action);

        if (action.equals(NOTIFY)) {
            Logger.i(TAG, "Sending NOTIFY intent to Service");

            Intent serviceIntent = new Intent(NOTIFY);
            serviceIntent.putExtras(intent.getExtras());
            context.startService(serviceIntent);
        }

        Logger.i(TAG, "Finished receiver");
    }

    private void scheduleUpdate() {
        Logger.i(TAG, "Scheduling next notify alarm for one of %s proxies", updatingMap.size());

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        long now = System.currentTimeMillis();

        TreeMap<Long, List<Integer>> timestampMap = new TreeMap<Long, List<Integer>>();

        for (Integer id: updatingMap.keySet()) {
            Notifying notifying = updatingMap.get(id);

            long timestamp = notifying.getNextNotifyTimestamp();

            // Event expired
            if (timestamp <= now) {
                continue;
            }

            if (!timestampMap.containsKey(timestamp)) {
                timestampMap.put(timestamp, new ArrayList<Integer>());
            }
            timestampMap.get(timestamp).add(id);
        }

        if (!timestampMap.isEmpty()) {
            long nearestUpdateTimestamp = timestampMap.firstKey();
            int[] ids = Util.toIntArray(timestampMap.get(nearestUpdateTimestamp));

            if (Logger.DEBUG) {
                Time time = new Time();
                time.set(nearestUpdateTimestamp);
                Logger.i(TAG, "Next notify at: %s", time.format(Util.TF));
            }
            Logger.i(TAG, "Ids to notify: %s", Arrays.toString(ids));

            PendingIntent pendingIntent = getPendingIntent(ids);
            alarmManager.set(AlarmManager.RTC, nearestUpdateTimestamp, pendingIntent);

        } else {

            alarmManager.cancel(getPendingIntent());
            Logger.i(TAG, "Unscheduled all update alarms");
        }

        Logger.d(TAG, "Finished Scheduling");
    }

    private PendingIntent getPendingIntent() {
        return getPendingIntent(null);
    }

    private PendingIntent getPendingIntent(int[] ids) {

        Intent alarmIntent = new Intent(NOTIFY);

        if (ids != null) {
            Bundle extras = new Bundle(1);
            extras.putIntArray(IDS, ids);
            alarmIntent.putExtras(extras);
        }

        return PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private int[] getIds(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            return extras.getIntArray(IDS);
        }
        return null;
    }

/*    public long scheduleNotify(Collection<WidgetProxy> widgetProxies) {
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
    }*/

}
