package com.ovio.countdown.task;

import android.content.Context;
import android.os.AsyncTask;
import com.ovio.countdown.WidgetPreferencesManager;

/**
 * Countdown
 * com.ovio.countdown.task
 */
public class DeleteWidgetsTask extends AsyncTask<Integer, Void, Void> {

    private Context context;

    public DeleteWidgetsTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Integer[] widgetIds) {

        if (widgetIds.length != 1) {
            throw new RuntimeException("Exactly 1 widget can be deleted at once, got: " + widgetIds.length);
        }

        WidgetPreferencesManager manager = new WidgetPreferencesManager(context);

        manager.delete(widgetIds[0]);

        return null;
    }
}
