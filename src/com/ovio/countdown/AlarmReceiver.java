package com.ovio.countdown;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.ovio.countdown.log.Logger;

/**
 * Countdown
 * com.ovio.countdown
 */
public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = Logger.PREFIX + "Alarm";

    @Override
    public void onReceive(Context context, Intent intent) {
        Logger.d(TAG, "Received %s Intent", intent.getAction());

        Logger.d(TAG, "Sending ALARM intent to Service");

        Intent widgetServiceIntent = new Intent(WidgetService.ALARM);
        context.startService(widgetServiceIntent);

        Logger.d(TAG, "Finished receiver");
    }
}
