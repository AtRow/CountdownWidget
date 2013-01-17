package com.ovio.countdown;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;
import com.ovio.countdown.preferences.PreferencesManager;
import com.ovio.countdown.preferences.WidgetOptions;
import com.ovio.countdown.preferences.WidgetPreferencesManager;
import com.ovio.countdown.proxy.WidgetProxy;
import com.ovio.countdown.proxy.WidgetProxyFactory;

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

    private static final String TAG = "SERVICE";

    private Context self = this;

    private Handler handler;

    private Thread counterThread;

    //private int[] appWidgetIds;

    private Map<Integer, WidgetProxy> widgetProxies;

    private boolean started = false;

    private PreferencesManager preferencesManager;

    private WidgetPreferencesManager widgetPreferencesManager;

    private WidgetProxyFactory widgetProxyFactory;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Context context = getApplicationContext();

        widgetPreferencesManager = WidgetPreferencesManager.getInstance(context);
        widgetProxyFactory = WidgetProxyFactory.getInstance(context);
        preferencesManager = PreferencesManager.getInstance(context);

        Toast.makeText(this, "Service created", Toast.LENGTH_LONG).show();

        int[] ids = preferencesManager.loadDefaultPrefs().savedWidgets;

        widgetProxies = new TreeMap<Integer, WidgetProxy>();
        for (int i = 0; i < ids.length; i++) {
            WidgetOptions options = widgetPreferencesManager.load(ids[i]);
            WidgetProxy proxy = widgetProxyFactory.getWidgetProxy(options);
            if (proxy != null) {
                widgetProxies.put(ids[i], proxy);
            }
        }

    }

    @Override
    public void onDestroy() {
        handler = null;
        widgetPreferencesManager = null;
        widgetProxyFactory = null;
        preferencesManager = null;
        widgetProxies = null;

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction().equals(START)) {
            // Do nothing
        } else if (intent.getAction().equals(UPDATED)) {

            WidgetOptions options = (WidgetOptions) intent.getExtras().getSerializable(OPTIONS);
            int id = options.widgetId;
            WidgetProxy proxy = widgetProxyFactory.getWidgetProxy(options);
            widgetProxies.put(id, proxy);
            proxy.updateWidget();

        } else if (intent.getAction().equals(DELETED)) {

            int[] ids = intent.getIntArrayExtra(WIDGET_IDS);
            for (int i = 0; i < ids.length; i++) {
                widgetProxies.remove(ids[i]);
            }
        }

        updateWidgets();

        return Service.START_STICKY;
    }

    private void updateWidgets() {
        Time time = new Time();

        for (Integer id: widgetProxies.keySet()) {
            Log.d(TAG, "Updating widget" + id);

            WidgetProxy proxy = widgetProxies.get(id);

            if (time.toMillis(false) >= proxy.nextUpdateTimestamp) {
                proxy.updateWidget();
            }
        }
    }

}
