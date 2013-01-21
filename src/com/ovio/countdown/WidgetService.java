package com.ovio.countdown;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.preferences.PreferencesManager;
import com.ovio.countdown.preferences.WidgetOptions;
import com.ovio.countdown.preferences.WidgetPreferencesManager;
import com.ovio.countdown.proxy.WidgetProxy;
import com.ovio.countdown.proxy.WidgetProxyFactory;
import com.ovio.countdown.util.Util;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * Countdown
 * com.ovio.countdown
 */
public class WidgetService extends Service {

    public static final String START = "com.ovio.countdown.WidgetService.START";
    public static final String UPDATED = "com.ovio.countdown.WidgetService.WIDGET_UPDATED";
    public static final String DELETED = "com.ovio.countdown.WidgetService.WIDGET_DELETED";
    public static final String ALARM = "com.ovio.countdown.WidgetService.ALARM";

    public static final String OPTIONS = "options";
    public static final String WIDGET_IDS = "widget_ids";

    private static final String TAG = Logger.PREFIX + "Service";
    private static final long MAX_ACTIVE_WAIT_MILLS = 1000 * 60 * 10; // 10 min

    private Scheduler scheduler;

    private Time time = new Time();

    private Thread secondCounterThread;

    private SecondCounterRunnable secondCounterRunnable;

    //private int[] appWidgetIds;

    private Map<Integer, WidgetProxy> widgetProxies;

    private boolean started = false;

    private PreferencesManager preferencesManager;

    private WidgetPreferencesManager widgetPreferencesManager;

    private WidgetProxyFactory widgetProxyFactory;

    public WidgetService() {
        super();
        Logger.d(TAG, "Instantiated WidgetService");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Logger.i(TAG, "Launching WidgetService");

        Context context = getApplicationContext();

        widgetPreferencesManager = WidgetPreferencesManager.getInstance(context);
        widgetProxyFactory = WidgetProxyFactory.getInstance(context);
        preferencesManager = PreferencesManager.getInstance(context);
        scheduler = Scheduler.getInstance(context);

        // TODO: remove
        Toast.makeText(this, "Service created", Toast.LENGTH_LONG).show();

        int[] ids = preferencesManager.loadDefaultPrefs().savedWidgets;

        Logger.i(TAG, "Loaded valid widgets: %s", Util.getString(ids));

        widgetProxies = new TreeMap<Integer, WidgetProxy>();
        for (int i = 0; i < ids.length; i++) {

            WidgetOptions options = widgetPreferencesManager.load(ids[i]);
            if (options != null) {

                WidgetProxy proxy = widgetProxyFactory.getWidgetProxy(options);
                if (proxy != null) {
                    Logger.i(TAG, "Created [%s] Proxy with id: %s", i, ids[i]);
                    widgetProxies.put(ids[i], proxy);
                }
            } else {
                Logger.i(TAG, "Got null Options, probably the Widget is new");
            }
        }
        scheduleUpdate();

    }

    @Override
    public void onDestroy() {
        Logger.i(TAG, "Stopping WidgetService");

        stopSecondCounterThread();

        widgetPreferencesManager = null;
        widgetProxyFactory = null;
        preferencesManager = null;
        widgetProxies = null;

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        Logger.d(TAG, "Received new Intent %s", action);

        if (action.equals(START)) {
            onStartIntent(intent);

        } else if (action.equals(UPDATED)) {
            onUpdatedIntent(intent);

        } else if (action.equals(DELETED)) {
            onDeletedIntent(intent);

        } else if (action.equals(ALARM)) {
            onAlarmIntent(intent);

        } else {
            Logger.e(TAG, "Received UNEXPECTED Intent with Action: %s", action);
        }

        Logger.i(TAG, "Leaving Service in STICKY mode");
        return Service.START_STICKY;
    }

    private void onAlarmIntent(Intent intent) {
        Logger.i(TAG, "Received ALARM Intent");

        updateWidgets();
        scheduleUpdate();

    }

    private void onStartIntent(Intent intent) {
        // Do nothing
        Logger.i(TAG, "Received START Intent");
    }

    private void onUpdatedIntent(Intent intent) {
        Logger.i(TAG, "Received UPDATED Intent");

        WidgetOptions options = getUpdateWidgetOptions(intent);

        if (options != null) {
            int id = options.widgetId;

            WidgetProxy proxy;
            if (widgetProxies.containsKey(id)) {
                Logger.d(TAG, "Updating existing Proxy with id %s", id);

                proxy = widgetProxies.get(id);
                proxy.setOptions(options);
            } else {
                Logger.d(TAG, "Creating new Proxy with id %s", id);

                proxy = widgetProxyFactory.getWidgetProxy(options);
                widgetProxies.put(id, proxy);
            }

            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Logger.d(TAG, "Current active Widgets in widgetProxies Map: %s",
                        Util.getString(widgetProxies.keySet()));
            }

            scheduleUpdate();

        } else {
            Logger.e(TAG, "Got null Options for UPDATE intent, no WidgetProxies will be created");
        }
    }

    private void onDeletedIntent(Intent intent) {
        Logger.i(TAG, "Received DELETED Intent");

        int[] ids = intent.getIntArrayExtra(WIDGET_IDS);

        if (ids != null) {
            for (int i = 0; i < ids.length; i++) {
                widgetProxies.remove(ids[i]);
            }
            scheduleUpdate();

        } else {
            Logger.e(TAG, "Got null Ids for DELETED intent, no WidgetProxies will be deleted");
        }
    }

    private WidgetOptions getUpdateWidgetOptions(Intent updateIntent) {
        Logger.d(TAG, "Parsing update Intent");

        WidgetOptions options;

        Bundle extras = updateIntent.getExtras();
        if (extras == null) {
            Logger.e(TAG, "Extras not found in UPDATE intent");
            return null;
        }

        Object o = extras.getSerializable(OPTIONS);
        if (o == null) {
            Logger.e(TAG, "Got empty Options for UPDATE intent");
            return null;
        }

        try {
            options = (WidgetOptions)o;
            Logger.d(TAG, "Got Options: %s", options);

        } catch (Exception e) {
            Logger.e(TAG, e, "Failed to cast UPDATE intent's Extras to WidgetOptions");
            return null;
        }

        return options;
    }

    private void scheduleUpdate() {
        Collection<WidgetProxy> proxies = widgetProxies.values();

        long nextUpdate = scheduler.scheduleUpdate(proxies);

        for (WidgetProxy proxy: proxies) {
            if (proxy.isCountingSeconds) {
                Logger.d(TAG, "Proxy %s is counting seconds, starting Second counter", proxy.getOptions().widgetId);
                startSecondCounterThread();
                break;
            }
            stopSecondCounterThread();
        }

        // If no update needed or too long to wait for update
        if (nextUpdate == -1) {
            Logger.i(TAG, "No update needed, Forcing Service shutdown");
            shutdownService();
        } else if ((nextUpdate - System.currentTimeMillis()) > MAX_ACTIVE_WAIT_MILLS ) {
            Logger.i(TAG, "To long to wait: %s ms, Forcing Service shutdown", System.currentTimeMillis() - nextUpdate);
            shutdownService();
        }

        return;
    }


    private void startSecondCounterThread() {
        if (secondCounterThread == null) {
            secondCounterRunnable = new SecondCounterRunnable();
            secondCounterThread = new Thread(secondCounterRunnable);
        }
        if (!secondCounterThread.isAlive()) {
            secondCounterThread.start();
        }
    }

    private void stopSecondCounterThread() {
        if (secondCounterThread != null && secondCounterThread.isAlive()) {
            secondCounterRunnable.stop();
            secondCounterThread.interrupt();
        }
    }

    private void shutdownService() {
        stopSelf();
    }

    private void updateWidgets() {
        Logger.d(TAG, "Updating Widget Proxies");

        time.setToNow();
        Logger.d(TAG, "Updated Time to: %s", time.format(Util.TF));

        for (WidgetProxy proxy: widgetProxies.values()) {
            WidgetOptions options = proxy.getOptions();
            Logger.i(TAG, "Updating widget %s", options.widgetId);

            //TODO: Log
            if (proxy.isAlive) {

                Logger.i(TAG, "Widget title: '%s'", options.title);

                if (Log.isLoggable(TAG, Log.INFO)) {
                    Logger.i(TAG, "Current time is [%s] and proxy.nextUpdate is [%s]", time.format(Util.TF), proxy.nextUpdateTime.format(Util.TF));
                }

                if (Time.compare(time, proxy.nextUpdateTime) >= 0) {
                    Logger.i(TAG, "Widget will be updated now");
                    proxy.updateWidget();
                }
            }
        }
    }

    protected class SecondCounterRunnable implements Runnable {

        private boolean stopRequested = false;

        @Override
        public void run() {
            Logger.i(TAG, "Started SecondCounterRunnable thread");

            while (!stopRequested) {
                try {

                    updateWidgetSeconds();

                    if (Log.isLoggable(TAG, Log.DEBUG)) {
                        long now = System.currentTimeMillis();
                        long nextSecond = (now / 1000 + 1) * 1000;
                        long timeToSleep = nextSecond - now;

                        Logger.d(TAG, "Now    is: %s", now);
                        Logger.d(TAG, "Next S is: %s", nextSecond);
                        Logger.d(TAG, "To  Sleep: %s", timeToSleep);

                        Thread.sleep(timeToSleep);

                    } else {
                        Thread.sleep(((System.currentTimeMillis() / 1000 + 1) * 1000) - System.currentTimeMillis());
                    }

                } catch (InterruptedException e) {
                    Logger.i(TAG, "Interrupted SecondCounterRunnable thread, stopping");
                    stopRequested = true;
                }
            }
            Logger.i(TAG, "Finished SecondCounterRunnable thread");
        }

        private void updateWidgetSeconds() {
            for (WidgetProxy proxy: widgetProxies.values()) {

                if (Log.isLoggable(TAG, Log.DEBUG)) {
                    Logger.d(TAG, "Walking trough widget %s to update seconds only", proxy.getOptions().widgetId);
                }

                if (proxy.isAlive && proxy.isCountingSeconds) {
                    proxy.updateWidgetSecondsOnly();
                }
            }
        }

        public void stop() {
            stopRequested = true;
        }
    }

}
