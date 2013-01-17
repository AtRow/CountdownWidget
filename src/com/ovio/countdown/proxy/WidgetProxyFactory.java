package com.ovio.countdown.proxy;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.widget.RemoteViews;
import com.ovio.countdown.R;
import com.ovio.countdown.preferences.WidgetOptions;

/**
 * Countdown
 * com.ovio.countdown
 */
public class WidgetProxyFactory {

    private static final String TAG = "WidgetProxyFactory";

    private static WidgetProxyFactory instance;

    private Context context;

    private final AppWidgetManager appWidgetManager;

    private WidgetProxyFactory(Context context) {
        this.context = context;
        appWidgetManager = AppWidgetManager.getInstance(context);
    }

    public static synchronized WidgetProxyFactory getInstance(Context context) {
        if (instance == null) {
            instance = new WidgetProxyFactory(context);
        }
        return instance;
    }

    public WidgetProxy getWidgetProxy(WidgetOptions options) {
        if (options == null) {
            return null;
        }
        AppWidgetProviderInfo info = appWidgetManager.getAppWidgetInfo(options.widgetId);

        if (info == null || info.initialLayout == 0) {
            return null;
        }

        switch (info.initialLayout) {
            case R.layout.countdown_widget_layout_4x1 :
                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.countdown_widget_layout_4x1);
                if (views == null) {
                    return null;
                } else {
                    return getLargeWidgetProxy(views, options);
                }

            default:
                return null;
        }
    }

    private WidgetProxy getLargeWidgetProxy(RemoteViews views, WidgetOptions options) {
        return new LargeWidgetProxy(context, appWidgetManager, views, options);
    }

}
