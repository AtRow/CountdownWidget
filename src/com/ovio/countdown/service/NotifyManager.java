package com.ovio.countdown.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.ovio.countdown.R;
import com.ovio.countdown.log.Logger;

/**
 * Countdown
 * com.ovio.countdown
 */
public class NotifyManager {

    private static final String TAG = Logger.PREFIX + "Notif";

    private static NotifyManager instance;
    private final Context context;

    private NotifyManager(Context context) {
        Logger.d(TAG, "Instantiated NotifyManager");
        this.context = context;
    }

    public static synchronized NotifyManager getInstance(Context context) {
        if(instance == null) {
            instance = new NotifyManager(context);
        }
        Logger.d(TAG, "Returning NotifyManager instance");
        return instance;
    }


    public void show(int id, long timestamp, String title) {
        Notification notification = new Notification(R.drawable.icon, title, timestamp);

        notification.defaults = Notification.DEFAULT_ALL;

        Intent intent = getIntent(id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        notification.setLatestEventInfo(context, title, title, pendingIntent);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(id, notification);
    }

    private Intent getIntent(int id) {
        Intent intent = new Intent(NotificationCleanReceiver.CLEAN);
        Bundle extras = new Bundle(1);
        extras.putInt("ID", id);
        intent.putExtras(extras);

        return intent;
    }

    public void hide(int id) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(id);

    }
}
