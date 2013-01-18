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
public class DeleteWidgetsTask extends AsyncTask<List<Integer>, Void, Void> {

    private static final String TAG = Logger.PREFIX + "DelTsk";

    private Context context;

    public DeleteWidgetsTask(Context context) {
        Logger.d(TAG, "Instantiated DeleteWidgetsTask");
        this.context = context;
    }

    @Override
    protected Void doInBackground(List<Integer>... widgetIds) {
        Logger.i(TAG, "Running DeleteWidgetsTask");

        if (widgetIds.length != 1) {
            Exception e = new RuntimeException("Exactly 1 array of widgets can be handled: " + widgetIds.length);
            Logger.e(TAG, e, "Wrong task argument");
        }

        WidgetPreferencesManager manager = WidgetPreferencesManager.getInstance(context);

        manager.deleteAll(widgetIds[0]);

        Logger.i(TAG, "Sending DELETED intent to Service");

        Intent widgetServiceIntent = new Intent(WidgetService.DELETED);
        Bundle extras = new Bundle();
        extras.putIntArray(WidgetService.WIDGET_IDS, Util.toIntArray(widgetIds[0]));

        widgetServiceIntent.putExtras(extras);
        context.startService(widgetServiceIntent);

        Logger.d(TAG, "Finished DeleteWidgetsTask");
        return null;
    }
}
