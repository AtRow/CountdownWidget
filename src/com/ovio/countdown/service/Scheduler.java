package com.ovio.countdown.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.proxy.Updating;
import com.ovio.countdown.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

/**
 * Countdown
 * com.ovio.countdown
 */
public final class Scheduler extends BroadcastReceiver {

    public static final String UPDATE = "com.ovio.countdown.service.Scheduler.UPDATE";

    private static final String TAG = Logger.PREFIX + "Scheduler";

    private static Scheduler instance;

    private static final String IDS = "IDS";

    private final TreeMap<Integer, Updating> updatingMap = new TreeMap<Integer, Updating>();

    private Context context;


    public Scheduler() {
        Logger.d(TAG, "Instantiated Scheduler");
    }

    public static synchronized Scheduler getInstance(Context context) {
        if (instance == null) {
            instance = new Scheduler();
        }
        Logger.d(TAG, "Returning Scheduler instance");

        instance.context = context;
        return instance;
    }

    public synchronized void register(int id, Updating updating) {
        Logger.i(TAG, "Registering new Updating widget with id: %s", id);

        updatingMap.put(id, updating);
        scheduleUpdate();
    }

    public synchronized void unRegister(int id) {
        Logger.i(TAG, "UnRegistering new Updating widget with id: %s", id);

        updatingMap.remove(id);
        scheduleUpdate();
    }

    public synchronized void onUpdate(Intent intent) {
        Logger.i(TAG, "Starting onUpdate");

        int[] ids = getIds(intent);
        Logger.i(TAG, "Will update widgets: %s", Arrays.toString(ids));
        long now = System.currentTimeMillis();

        if (ids != null) {

            for (int i = 0; i < ids.length; i++) {
                Updating updating = updatingMap.get(ids[i]);
                if (updating != null) {
                    Logger.i(TAG, "Updating widget [%s]", ids[i]);
                    updating.onUpdate(now);
                }
            }

            scheduleUpdate();
        }
        Logger.i(TAG, "Finished onUpdate");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Logger.i(TAG, "Received %s Intent", action);

        if (action.equals(UPDATE)) {
            Logger.i(TAG, "Sending UPDATE intent to Service");

            Intent serviceIntent = new Intent(UPDATE);
            serviceIntent.putExtras(intent.getExtras());
            context.startService(serviceIntent);
        }

        Logger.i(TAG, "Finished receiver");
    }

    private void scheduleUpdate() {
        Logger.i(TAG, "Scheduling next update alarm for one of %s proxies", updatingMap.size());

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        long now = System.currentTimeMillis();

        TreeMap<Long, List<Integer>> timestampMap = new TreeMap<Long, List<Integer>>();

        for (Integer id: updatingMap.keySet()) {
            Updating updating = updatingMap.get(id);

            long timestamp = updating.getNextUpdateTimestamp();

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
                Logger.i(TAG, "Next update at: %s", time.format(Util.TF));
            }
            Logger.i(TAG, "Ids to update: %s", Arrays.toString(ids));

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

        Intent alarmIntent = new Intent(UPDATE);

        if (ids != null) {
            Bundle extras = new Bundle();
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
