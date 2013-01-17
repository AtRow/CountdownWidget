package com.ovio.countdown.task;

import android.os.AsyncTask;
import com.ovio.countdown.WidgetManager;

/**
 * Countdown
 * com.ovio.countdown.task
 */
public class UpdateWidgetTask extends AsyncTask<Integer, Void, Void> {

    @Override
    protected Void doInBackground(Integer[] widgetIds) {

        WidgetManager manager = new WidgetManager();

        int[] validWidgetIds = manager.validate(widgetIds);

        for (int id: validWidgetIds) {
            manager.update(id);
        }

        return null;
    }
}
