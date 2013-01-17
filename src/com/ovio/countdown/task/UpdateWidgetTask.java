package com.ovio.countdown.task;

import android.content.Context;
import android.os.AsyncTask;
import com.ovio.countdown.WidgetPreferencesManager;

import java.util.List;

/**
 * Countdown
 * com.ovio.countdown.task
 */
public class UpdateWidgetTask extends AsyncTask<List<Integer>, Void, Void> {

    private Context context;

    public UpdateWidgetTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(List<Integer>... widgetIds) {

        if (widgetIds.length != 1) {
            throw new RuntimeException("Exactly 1 array of widgets can be handled: " + widgetIds.length);
        }

        WidgetPreferencesManager manager = new WidgetPreferencesManager(context);

        List<Integer> validWidgetIds = manager.validate(widgetIds[0]);

        for (int id: validWidgetIds) {
            manager.update(id);
        }

        return null;
    }
}
