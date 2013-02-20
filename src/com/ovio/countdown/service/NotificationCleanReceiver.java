package com.ovio.countdown.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.ovio.countdown.log.Logger;

/**
 * Countdown
 * com.ovio.countdown.service
 */
public class NotificationCleanReceiver extends BroadcastReceiver {

    private static final String TAG = Logger.PREFIX + "NotifCleaner";

    public static final String CLEAN = "com.ovio.countdown.service.NotificationCleanReceiver.CLEAN";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Logger.i(TAG, "Received new Intent %s", action);

        if (action.equals(CLEAN)) {

            Bundle extras = intent.getExtras();
            if (extras != null) {
                int id = extras.getInt("ID");

                NotifyManager manager = NotifyManager.getInstance(context);
                manager.hide(id);
            }

        }

        Logger.d(TAG, "Finished receiver");
    }
}
