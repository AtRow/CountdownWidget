package com.ovio.countdown.event;

import android.content.Context;
import android.text.format.Time;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.preferences.WidgetOptions;
import com.ovio.countdown.preferences.WidgetPreferencesManager;
import com.ovio.countdown.prefs.Recurring;
import com.ovio.countdown.util.Util;

/**
 * Countdown
 * com.ovio.countdown.event
 */
public class PlainEvent implements Event {
    protected final WidgetOptions options;
    private final WidgetPreferencesManager widgetPreferencesManager;

    protected final Context context;

    private final static String TAG = Logger.PREFIX + "plainEvent";
    private final static int PAUSE = 1000 * 10; // 10 sec

    private long pause;

    private long targetTimestamp;

    public PlainEvent(Context context, WidgetOptions options) {
        this.options = options;
        this.context = context;

        this.targetTimestamp = getRoundTimestampRecurring(options.timestamp);

        widgetPreferencesManager = WidgetPreferencesManager.getInstance(context);
    }

    @Override
    public long getTargetTimestamp() {
        Logger.i(TAG, "Pe[%s]: Getting target timestamp for widget", options.widgetId);
        if (isExpired()) {
            getNextEvent();
            pause();
        }

        if (Logger.DEBUG) {
            Time time = new Time();
            time.set(targetTimestamp);
            Logger.i(TAG, "Px[%s]: Target timestamp for widget: [%s]", options.widgetId, time.format(Util.TF));
        }

        return targetTimestamp;
    }

    @Override
    public String getTitle() {
        return options.title;
    }

    @Override
    public boolean isCountingSeconds() {
        return options.enableSeconds;
    }

    @Override
    public boolean isCountingUp() {
        if (Logger.DEBUG) {
            Logger.i(TAG, "Px[%s]: Counting up: %s", options.widgetId, options.countUp);
        }

        return options.countUp;
    }

    @Override
    public boolean isRepeating() {
        return (options.recurring != Recurring.NONE);
    }

    @Override
    public boolean isAlive() {
        return isCountingUp() || isRepeating() || isPaused() ||
                (!isCountingUp() && (getTargetTimestamp() > System.currentTimeMillis()));
    }

    @Override
    public boolean isPaused() {
        if (Logger.DEBUG) {
            Logger.i(TAG, "Px[%s]: Seconds till unPause: %s", options.widgetId, (System.currentTimeMillis() - pause)/1000);
        }

        return System.currentTimeMillis() < pause;
    }

    @Override
    public long getPausedTill() {
        return pause;
    }

    private void pause() {
        pause = System.currentTimeMillis() + PAUSE;

        if (Logger.DEBUG) {
            Time time = new Time();
            time.set(pause);
            Logger.i(TAG, "Px[%s]: Widget will be paused till: %s", options.widgetId, time.format(Util.TF));
        }
    }

    private long getRoundTimestampRecurring(long timestamp) {

        if (!isRepeating()) {
            return timestamp;
        }

        long now = System.currentTimeMillis();
        if (Logger.DEBUG) {
            Time time = new Time();
            time.set(now);
            Logger.i(TAG, "Px[%s]: Now is: [%s]", options.widgetId, time.format(Util.TF));
        }
        long inc = options.recurring.getMillis();

        long delta = ((now - timestamp) / inc) * inc;
        if (delta < 0) {
            delta -= inc;
        }
        if (!isCountingUp()) {
            delta += inc;
        }

        if (Logger.DEBUG) {
            Logger.i(TAG, "Px[%s]: Delta: %s seconds", options.widgetId, delta / 1000);
        }

        if (Logger.DEBUG) {
            Time time = new Time();
            time.set(timestamp + delta);
            Logger.i(TAG, "Px[%s]: Rounded Timestamp [%s]", options.widgetId, time.format(Util.TF));
        }

        return timestamp + delta;
    }

    private long getNextTimestamp() {
        return targetTimestamp + options.recurring.getMillis();
    }

    private void getNextEvent() {
        //targetTimestamp = getNextTimestamp();
        targetTimestamp = getRoundTimestampRecurring(targetTimestamp);

        if (Logger.DEBUG) {
            Time time = new Time();
            time.set(targetTimestamp);
            Logger.i(TAG, "Px[%s]: New Target timestamp: [%s]", options.widgetId, time.format(Util.TF));
        }
    }

    private boolean isExpired() {
        // Non-repeating events never expires
        if (!isRepeating()) {
            return false;
        }

        boolean isExpired;

        if (isCountingUp()) {
            isExpired = (System.currentTimeMillis() >= (getNextTimestamp()));
        } else {
            isExpired = (System.currentTimeMillis() >= (targetTimestamp));
        }
        Logger.i(TAG, "Px[%s]: Is Expired: %s", options.widgetId, isExpired);
        return isExpired;
    }

}
