package com.ovio.countdown.proxy;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;
import com.ovio.countdown.R;
import com.ovio.countdown.preferences.WidgetOptions;

/**
 * Countdown
 * com.ovio.countdown
 */
public class WidgetProxyFactory {

    private static final String TAG = "WidgetProxyFactory";

    private Context context;

    private final AppWidgetManager appWidgetManager;

    public WidgetProxyFactory(Context context) {
        this.context = context;
        appWidgetManager = AppWidgetManager.getInstance(context);
    }


    public WidgetProxy getWidgetProxy(WidgetOptions options) {
        AppWidgetProviderInfo info = appWidgetManager.getAppWidgetInfo(options.widgetId);

        if (info == null || info.initialLayout == 0) {
            return null;
        }

        switch (info.initialLayout) {
            case R.layout.countdown_widget_layout_4x1 :
                return getLargeWidgetProxy();
            default:
                return null;
        }
    }




    private RemoteViews getRemoteViews() {

        int layout = appWidgetManager.getAppWidgetInfo(widgetId).initialLayout;

        Log.d(TAG, "Loading remote views for widget [" + widgetId + "] with layout [" + layout + "]");

        switch (layout) {
            case R.layout.countdown_widget_layout_4x1 :
                return new RemoteViews(context.getPackageName(), R.layout.countdown_widget_layout_4x1);

            default:
                Log.e(TAG, "Layout not found");
                return null;
        }

    }
}
