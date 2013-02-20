package com.ovio.countdown.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.Time;
import android.widget.Toast;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.preferences.PreferencesManager;
import com.ovio.countdown.preferences.WidgetOptions;
import com.ovio.countdown.preferences.WidgetPreferencesManager;
import com.ovio.countdown.proxy.WidgetProxy;
import com.ovio.countdown.proxy.WidgetProxyFactory;
import com.ovio.countdown.util.Util;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * Countdown
 * com.ovio.countdown
 */
public class WidgetService extends Service {

    public static final String START = "com.ovio.countdown.service.WidgetService.START";
    public static final String ALARM = "com.ovio.countdown.service.WidgetService.ALARM";
    public static final String NOTIFY = "com.ovio.countdown.service.WidgetService.NOTIFY";
    public static final String UPDATED = "com.ovio.countdown.service.WidgetService.WIDGET_UPDATED";
    public static final String DELETED = "com.ovio.countdown.service.WidgetService.WIDGET_DELETED";

    public static final String PERMISSION = "com.ovio.countdown.service.WidgetService.PERMISSION";

    public static final String OPTIONS = "options";
    public static final String WIDGET_IDS = "widget_ids";

    private static final String TAG = Logger.PREFIX + "Service";
    private static final long MAX_ACTIVE_WAIT_MILLS = 1000 * 60 * 2; // 2 min

    private Map<Integer, WidgetProxy> widgetProxies = new TreeMap<Integer, WidgetProxy>();

    private Scheduler scheduler;

    private SecondCounter secondCounter;
    private Blinker blinker;

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
        secondCounter = new SecondCounter(context, Collections.unmodifiableCollection(widgetProxies.values()));
        blinker = new Blinker(context, Collections.unmodifiableCollection(widgetProxies.values()));

        // TODO: remove
        Toast.makeText(this, "Service created", Toast.LENGTH_LONG).show();

        int[] ids = preferencesManager.loadDefaultPrefs().savedWidgets;

        Logger.i(TAG, "Loaded valid widgets: %s", Util.getString(ids));

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
    }

    @Override
    public void onDestroy() {
        Logger.i(TAG, "Stopping WidgetService");

        secondCounter.stop();
        blinker.stop();

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

        } else if (action.equals(NOTIFY)) {
            onNotifyIntent(intent);

        } else {
            Logger.e(TAG, "Received UNEXPECTED Intent with Action: %s", action);
        }

        Logger.i(TAG, "Leaving Service in START_REDELIVER_INTENT mode");
        return Service.START_REDELIVER_INTENT;
    }

    private void onStartIntent(Intent intent) {
        Logger.i(TAG, "Received START Intent");

        scheduleUpdate();
    }

    private void onUpdatedIntent(Intent intent) {
        Logger.i(TAG, "Received UPDATED Intent");

        WidgetOptions options = getUpdateWidgetOptions(intent);

        if (options != null) {
            int id = options.widgetId;

            WidgetProxy proxy;
//            if (widgetProxies.containsKey(id)) {
//                Logger.d(TAG, "Updating existing Proxy with id %s", id);
//
//                proxy = widgetProxies.get(id);
//                proxy.setOptions(options);
//            } else {
                Logger.d(TAG, "Creating new Proxy with id %s", id);

                proxy = widgetProxyFactory.getWidgetProxy(options);
                if (proxy != null) {
                    widgetProxies.put(id, proxy);
                }
//            }

            if (Logger.DEBUG) {
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

    private void onAlarmIntent(Intent intent) {
        Logger.i(TAG, "Received ALARM Intent");

        updateWidgets();
        scheduleUpdate();
    }

    private void onNotifyIntent(Intent intent) {
        Logger.i(TAG, "Received NOTIFY Intent");

        notifyWidgets();
        scheduleUpdate();
    }

    private WidgetOptions getUpdateWidgetOptions(Intent updateIntent) {
        Logger.d(TAG, "Parsing update Intent");

        Bundle extras = updateIntent.getExtras();
        if (extras == null) {
            Logger.e(TAG, "Extras not found in UPDATE intent");
            return null;
        }

        WidgetOptions options = new WidgetOptions();
        options.setBundle(extras);

        Logger.d(TAG, "Got Options: %s", options);

        return options;
    }

    private void scheduleUpdate() {
        Collection<WidgetProxy> proxies = widgetProxies.values();

        long nextUpdate = scheduler.scheduleUpdate(proxies);
        scheduler.scheduleNotify(proxies);

        // If no update needed or too long to wait for update
        if ((nextUpdate - System.currentTimeMillis()) > MAX_ACTIVE_WAIT_MILLS ) {
            Logger.i(TAG, "To long to wait: %s ms, Forcing Service shutdown", nextUpdate - System.currentTimeMillis());
            shutdownService();
        } else {
            startCountingSeconds();
            startBlinking();
        }
    }

    private void shutdownService() {
        stopSelf();
    }

    private void startBlinking() {

        for (WidgetProxy proxy: widgetProxies.values()) {
            if (proxy.isBlinking()) {
                Logger.i(TAG, "Proxy %s is blinking, starting Blinker", proxy.getWidgetId());
                blinker.start();
                break;
            }
            blinker.stop();
        }
    }

    private void startCountingSeconds() {

        for (WidgetProxy proxy: widgetProxies.values()) {
            if (proxy.isCountingSeconds()) {
                Logger.i(TAG, "Proxy %s is counting seconds, starting Second counter", proxy.getWidgetId());
                secondCounter.start();
                break;
            }
            secondCounter.stop();
        }
    }

    private void updateWidgets() {
        Logger.i(TAG, "Updating Widget Proxies");

        for (WidgetProxy proxy: widgetProxies.values()) {
            Logger.i(TAG, "Updating widget %s", proxy.getWidgetId());

            if (proxy.isAlive()) {
                Logger.i(TAG, "Widget %s is alive", proxy.getWidgetId());

                long next = proxy.getNextUpdateTimestamp();

                if (Logger.DEBUG) {
                    Time time = new Time();
                    time.setToNow();
                    Time updTime = new Time();
                    updTime.set(next);

                    Logger.i(TAG, "Current time is [%s] and proxy.nextUpdate is [%s]", time.format(Util.TF), updTime.format(Util.TF));
                }

                if (System.currentTimeMillis() >= next) {
                    Logger.i(TAG, "Widget will be updated now");
                    proxy.updateWidget();
                }
            }
        }
    }

    private void notifyWidgets() {
        Logger.i(TAG, "Updating Widget Notifications");

        for (WidgetProxy proxy: widgetProxies.values()) {
            Logger.i(TAG, "Updating widget %s", proxy.getWidgetId());

            if (proxy.isAlive()) {
                Logger.i(TAG, "Widget %s is alive", proxy.getWidgetId());

                long next = proxy.getNextNotifyTimestamp();

                if (Logger.DEBUG) {
                    Time time = new Time();
                    time.setToNow();
                    Time updTime = new Time();
                    updTime.set(next);

                    Logger.i(TAG, "Current time is [%s] and proxy.nextNotify is [%s]", time.format(Util.TF), updTime.format(Util.TF));
                }

                if (System.currentTimeMillis() >= next) {
                    Logger.i(TAG, "Widget will be notified now");
                    proxy.updateWidget();
                }
            }
        }
    }

}
