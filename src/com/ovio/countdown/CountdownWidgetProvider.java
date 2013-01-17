package com.ovio.countdown;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Countdown
 * com.ovio.countdown
 */
public class CountdownWidgetProvider extends AppWidgetProvider {

    private static final String TAG = "WIDGET";

    private String text;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        Log.i(TAG, "onUpdate()");

        startCountdownService(context, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        Log.i(TAG, "onReceive()");

        if (widgetsInstalled(context) != 0 && intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
            startCountdownService(context, null);
        }
    }

    private void startCountdownService(Context context, int[] appWidgetIds) {
        Intent updatingIntent = new Intent(context, CountdownService.class);

        if (appWidgetIds != null) {
            updatingIntent.putExtra(CountdownService.WIDGET_IDS, appWidgetIds);
        }

        context.startService(updatingIntent);
    }

    private int widgetsInstalled(Context context) {
        ComponentName thisWidget = new ComponentName(context, CountdownWidgetProvider.class);
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        return mgr.getAppWidgetIds(thisWidget).length;
    }
}
