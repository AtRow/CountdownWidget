package com.ovio.countdown.proxy;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.widget.RemoteViews;
import com.ovio.countdown.R;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.preferences.WidgetOptions;

/**
 * Countdown
 * com.ovio.countdown
 */
public class WidgetProxyFactory {

    private static final String TAG = Logger.PREFIX + "WidgetPxFact";

    private static WidgetProxyFactory instance;

    private Context context;

    private final AppWidgetManager appWidgetManager;

    private WidgetProxyFactory(Context context) {
        Logger.d(TAG, "Instantiated WidgetProxyFactory");

        this.context = context;
        appWidgetManager = AppWidgetManager.getInstance(context);
    }

    public static synchronized WidgetProxyFactory getInstance(Context context) {
        if (instance == null) {
            instance = new WidgetProxyFactory(context);
        }

        Logger.d(TAG, "Returning WidgetProxyFactory instance");
        return instance;
    }

    public WidgetProxy getWidgetProxy(WidgetOptions options) {
        Logger.i(TAG, "Creating WidgetProxy with options: %s", options);

        if (options == null) {
            Logger.e(TAG, "Trying to create WidgetProxy with null options!");
            return null;
        }

        AppWidgetProviderInfo info = appWidgetManager.getAppWidgetInfo(options.widgetId);

        if (info == null || info.initialLayout == 0) {
            Logger.e(TAG, "Couldn't find AppWidgetProviderInfo by widget id %s", options.widgetId);
            return null;
        }

        switch (info.initialLayout) {
            case R.layout.countdown_widget_layout_4x1 :
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.countdown_widget_layout_4x1);
                if (views == null) {
                    Logger.e(TAG, "Couldn't get RemoteViews for layout R.layout.countdown_widget_layout_4x1");
                    return null;
                } else {
                    Logger.d(TAG, "Widget is Large");
                    return getLargeWidgetProxy(views, options);
                }

            default:
                Logger.e(TAG, "Couldn't find layout resource for widget's initialLayout: %s", info.initialLayout);
                return null;
        }
    }

    private WidgetProxy getLargeWidgetProxy(RemoteViews views, WidgetOptions options) {
        Logger.d(TAG, "Creating Large widget");
        return new LargeWidgetProxy(context, appWidgetManager, views, options);
    }

}
