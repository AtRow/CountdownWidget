package com.ovio.countdown;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Countdown
 * com.ovio.countdown
 */
@Deprecated
public class CountdownService extends Service {

    public static final String WIDGET_IDS = "widget_ids";
    private static final String TAG = "SERVICE";

    private Context self = this;

    private Handler handler;

    private Thread counterThread;

    //private int[] appWidgetIds;

    private List<Integer> appWidgetIds;

    private boolean stopThread = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        counterThread = new Thread(new CounterRunnable());

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //TODO
            }
        };
    }

    @Override
    public void onDestroy() {
        handler = null;

        stopThread = true;
        counterThread.interrupt();

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        int[] ids = intent.getIntArrayExtra(WIDGET_IDS);

        if (ids != null) {
            List list = Arrays.asList(ids);
            appWidgetIds = (List<Integer>)list;
        }

        if (!counterThread.isAlive()) {
            counterThread.start();
        }
        counterThread.interrupt();

        return Service.START_STICKY;
    }

    private void updateWidgets() {

        List<Integer> invalidWidgets = new ArrayList<Integer>();

        for(Integer id : appWidgetIds) {
            AppWidgetManager manager = AppWidgetManager.getInstance(self);

            RemoteViews views = null;

            AppWidgetProviderInfo info = manager.getAppWidgetInfo(id);

            if (info == null) {
                Log.w(TAG, "Widget [" + id + "] is invalid, skipping");
                invalidWidgets.add(id);
                continue;
            }

            int layout = info.initialLayout;
            Log.d(TAG, "Updating widget [" + id + "] with layout [" + layout + "]");

            switch (layout) {
                case R.layout.countdown_widget_layout_4x1 :
                    views = new RemoteViews(self.getPackageName(), R.layout.countdown_widget_layout_4x1);
                    break;

                default:
                    // TODO: Exceptions
                    Log.e(TAG, "Layout not found");
            }

            //views.setCharSequence(R.id.widgetTimeTextView, "setText", instancePreferences.getString(TEXT, ""));

            manager.updateAppWidget(id, views);

        }

    }

    private class CounterRunnable implements Runnable {

        @Override
        public void run() {
            while (!stopThread) {
                try {
                    updateWidgets();
                    wait(Long.MAX_VALUE);
                } catch (InterruptedException e) {
                    // Do nothing. Just continue loop.
                }
            }
        }
    }
}
