package com.ovio.countdown;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import java.util.Map;
import java.util.TreeMap;

/**
 * Countdown
 * com.ovio.countdown
 */
public class WidgetService extends Service {

    public static final String START = "com.ovio.countdown.WidgetService.START";
    public static final String UPDATED = "com.ovio.countdown.WidgetService.APPWIDGET_UPDATED";
    public static final String DELETED = "com.ovio.countdown.WidgetService.APPWIDGET_DELETED";

    public static final String OPTIONS = "options";
    public static final String WIDGET_IDS = "widget_ids";

    private static final String TAG = Logger.PREFIX + "SERVICE";

    private Context self = this;

    private Time time = new Time();

    private Handler handler;

    private Thread counterThread;

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
                Logger.w(TAG, "Got null Options for previously saved Widget");
            }
        }

    }

    @Override
    public void onDestroy() {
        Logger.i(TAG, "Stopping WidgetService");

        handler = null;
        widgetPreferencesManager = null;
        widgetProxyFactory = null;
        preferencesManager = null;
        widgetProxies = null;

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(TAG, "Received new Intent");

        if (intent.getAction().equals(START)) {
            onStartIntent(intent);

        } else if (intent.getAction().equals(UPDATED)) {
            onUpdatedIntent(intent);

        } else if (intent.getAction().equals(DELETED)) {
            onDeletedIntent(intent);

        } else {
            Logger.e(TAG, "Received UNEXPECTED Intent with Action: %s", intent.getAction());
        }

        updateWidgets();

        Logger.i(TAG, "Leaving Service in STICKY mode");
        return Service.START_STICKY;
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
            WidgetProxy proxy = widgetProxyFactory.getWidgetProxy(options);
            widgetProxies.put(id, proxy);

            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Logger.d(TAG, "Current active Widgets in widgetProxies Map: %s",
                        Util.getString(widgetProxies.keySet()));
            }

            proxy.updateWidget();

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

    private void updateWidgets() {
        Logger.d(TAG, "Updating Widget Proxies");

        time.setToNow();
        Logger.d(TAG, "Updated Time to: %s", time.format2445());

        for (Integer id: widgetProxies.keySet()) {
            Logger.i(TAG, "Updating widget %s", id);

            WidgetProxy proxy = widgetProxies.get(id);
            Logger.i(TAG, "Widget title: '%s'", proxy.options.title);

            if (Log.isLoggable(TAG, Log.INFO)) {
                Time tt = new Time();
                tt.set(proxy.nextUpdateTimestamp);

                Logger.i(TAG, "Current time is [%s] and proxy.nextUpdate is [%s]", time.format2445(), tt.format2445());
            }

            if (time.toMillis(false) >= proxy.nextUpdateTimestamp) {
                Logger.d(TAG, "Widget will be updated now");
                proxy.updateWidget();
            }
        }
    }

}
