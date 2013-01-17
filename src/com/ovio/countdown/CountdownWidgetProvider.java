package com.ovio.countdown;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;
import com.ovio.countdown.task.DeleteWidgetsTask;
import com.ovio.countdown.task.UpdateWidgetTask;

import java.util.ArrayList;
import java.util.List;

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

        UpdateWidgetTask task = new UpdateWidgetTask(context);
        List<Integer> list = new ArrayList<Integer>(appWidgetIds.length);
        for (int id: appWidgetIds) {
            list.add(id);
        }
        task.execute(list);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {

        Log.i(TAG, "onDelete()");

        DeleteWidgetsTask task = new DeleteWidgetsTask(context);
        List<Integer> list = new ArrayList<Integer>(appWidgetIds.length);
        for (int id: appWidgetIds) {
            list.add(id);
        }
        task.execute(list);

        super.onDeleted(context, appWidgetIds);
    }

    /*  TODO: onReceive is out of scope for now
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        Log.i(TAG, "onReceive()");

        if (widgetsInstalled(context) != 0 && intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
            startCountdownService(context, null);
        }
    }
*/

/*
    private void startCountdownService(Context context, int[] appWidgetIds) {
        Intent updatingIntent = new Intent(context, CountdownService.class);

        if (appWidgetIds != null) {
            updatingIntent.putExtra(CountdownService.WIDGET_IDS, appWidgetIds);
        }

        context.startService(updatingIntent);
    }
*/

/*
    private int widgetsInstalled(Context context) {
        ComponentName thisWidget = new ComponentName(context, CountdownWidgetProvider.class);
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        return mgr.getAppWidgetIds(thisWidget).length;
    }
*/
}
