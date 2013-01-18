package com.ovio.countdown.proxy;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.widget.RemoteViews;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.preferences.WidgetOptions;

/**
 * Countdown
 * com.ovio.countdown
 */
public class LargeWidgetProxy extends WidgetProxy {

    private static final String TAG = Logger.PREFIX + "LWidgetPx";

    public LargeWidgetProxy(Context context, AppWidgetManager appWidgetManager, RemoteViews views, WidgetOptions options) {
        super(context, appWidgetManager, views, options);
        Logger.d(TAG, "Instantiated LargeWidgetProxy");
    }
}
