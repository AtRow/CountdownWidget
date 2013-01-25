package com.ovio.countdown.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.service.WidgetService;
import com.ovio.countdown.task.CleanupWidgetTask;
import com.ovio.countdown.task.DeleteWidgetsTask;
import com.ovio.countdown.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Countdown
 * com.ovio.countdown
 */
public abstract class CountdownWidgetProvider extends AppWidgetProvider {

    private static final String TAG = Logger.PREFIX + "Provider";

    private Context context;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        this.context = context;

        startWidgetService();

//        Logger.w(TAG, "Cleanup disabled because of a BUG");


        Logger.e(TAG, "Performing Update for widgets: %s", Util.getString(appWidgetIds));

        CleanupWidgetTask task = new CleanupWidgetTask(context);
        int[] installedAppWidgetIds = getInstalledWidgets(context, appWidgetManager);

        Logger.i(TAG, "Current valid widgets: %s", Util.getString(installedAppWidgetIds));

        task.execute(Util.toIntegerList(installedAppWidgetIds));


        Logger.d(TAG, "Finished onUpdate");
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        this.context = context;

        Logger.i(TAG, "Performing Delete for widgets: %s", Util.getString(appWidgetIds));

        DeleteWidgetsTask task = new DeleteWidgetsTask(context);
        List<Integer> list = new ArrayList<Integer>(appWidgetIds.length);
        for (int id: appWidgetIds) {
            list.add(id);
        }
        task.execute(list);

        Logger.d(TAG, "Finished onDeleted");
        super.onDeleted(context, appWidgetIds);
    }

    private int[] getInstalledWidgets(Context context, AppWidgetManager appWidgetManager) {
        Logger.d(TAG, "Getting all valid widgets");

        ComponentName smallWidget = new ComponentName(context, CountdownWidgetProviderSmall.class);
        int[] smalls = appWidgetManager.getAppWidgetIds(smallWidget);
        Logger.d(TAG, "Got [%s] small", smalls.length);

        ComponentName mediumWidget = new ComponentName(context, CountdownWidgetProviderMedium.class);
        int[] mediums = appWidgetManager.getAppWidgetIds(mediumWidget);
        Logger.d(TAG, "Got [%s] medium", mediums.length);

        ComponentName largeWidget = new ComponentName(context, CountdownWidgetProviderLarge.class);
        int[] larges = appWidgetManager.getAppWidgetIds(largeWidget);
        Logger.d(TAG, "Got [%s] large", larges.length);

        int[] all = Util.concatArrays(smalls, mediums, larges);

        return all;
    }


    private void startWidgetService() {
        Logger.i(TAG, "Sending START intent to Service");

        Intent widgetServiceIntent = new Intent(WidgetService.START);
        context.startService(widgetServiceIntent);
    }

}
