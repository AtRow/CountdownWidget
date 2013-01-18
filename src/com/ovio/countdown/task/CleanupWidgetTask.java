package com.ovio.countdown.task;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import com.ovio.countdown.WidgetService;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.preferences.WidgetPreferencesManager;
import com.ovio.countdown.util.Util;

import java.util.List;

/**
 * Countdown
 * com.ovio.countdown.task
 */
public class CleanupWidgetTask extends AsyncTask<List<Integer>, Void, Void> {

    private static final String TAG = Logger.PREFIX + "task";

    private Context context;

    public CleanupWidgetTask(Context context) {
        Logger.d(TAG, "Instantiated CleanupWidgetTask");
        this.context = context;
    }

    @Override
    protected Void doInBackground(List<Integer>... validWidgetIds) {
        Logger.i(TAG, "Running CleanupWidgetTask");

        if (validWidgetIds.length != 1) {
            Exception e = new RuntimeException("Exactly 1 array of widgets can be handled: " + validWidgetIds.length);
            Logger.e(TAG, e, "Wrong task argument");
        }

        WidgetPreferencesManager manager = WidgetPreferencesManager.getInstance(context);

        List<Integer> deletedIds = manager.cleanup(validWidgetIds[0]);

        if (!deletedIds.isEmpty()) {
            Logger.i(TAG, "Sending DELETED intent to Service");

            Intent widgetServiceIntent = new Intent(WidgetService.DELETED);
            Bundle extras = new Bundle();
            extras.putIntArray(WidgetService.WIDGET_IDS, Util.toIntArray(deletedIds));

            widgetServiceIntent.putExtras(extras);
            context.startService(widgetServiceIntent);
        }

        Logger.d(TAG, "Finished CleanupWidgetTask");
        return null;
    }
}
