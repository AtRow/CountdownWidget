package com.ovio.countdown.task;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import com.ovio.countdown.WidgetService;
import com.ovio.countdown.preferences.WidgetPreferencesManager;
import com.ovio.countdown.util.Util;

import java.util.List;

/**
 * Countdown
 * com.ovio.countdown.task
 */
public class DeleteWidgetsTask extends AsyncTask<List<Integer>, Void, Void> {

    private Context context;

    public DeleteWidgetsTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(List<Integer>... widgetIds) {

        if (widgetIds.length != 1) {
            throw new RuntimeException("Exactly 1 array of widgets can be handled: " + widgetIds.length);
        }

        WidgetPreferencesManager manager = WidgetPreferencesManager.getInstance(context);

        manager.deleteAll(widgetIds[0]);

        Intent widgetServiceIntent = new Intent(WidgetService.DELETED);

        Bundle extras = new Bundle();
        extras.putIntArray(WidgetService.WIDGET_IDS, Util.toIntArray(widgetIds[0]));

        widgetServiceIntent.putExtras(extras);
        context.startService(widgetServiceIntent);

        return null;
    }
}
