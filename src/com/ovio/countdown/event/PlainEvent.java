package com.ovio.countdown.event;

import android.text.format.Time;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.preferences.WidgetOptions;
import com.ovio.countdown.util.Util;

/**
 * Countdown
 * com.ovio.countdown.event
 */
public class PlainEvent implements Event {

    private final static String TAG = Logger.PREFIX + "plainEvent";
    private final static int PAUSE = 10000; // 10 sec

    private final WidgetOptions options;

    private long targetTimestamp;
    private long pause;

    public PlainEvent(WidgetOptions options) {
        this.options = options;
        this.targetTimestamp = getFastForward(options.timestamp);
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
        return (options.recurringInterval != 0L);
    }

    @Override
    public boolean isAlive() {
        return isCountingUp() || isRepeating() || isPaused() ||
                (!isCountingUp() && (getTargetTimestamp() > System.currentTimeMillis()));
    }

    @Override
    public boolean isPaused() {
        return System.currentTimeMillis() < pause;
    }

    @Override
    public long getPausedTill() {
        if (Logger.DEBUG) {
            Logger.i(TAG, "Px[%s]: Seconds till unPause: %s", options.widgetId, (System.currentTimeMillis() - pause)/1000);
        }
        return pause;
    }

    @Override
    public boolean isNotifying() {
        return options.notificationInterval > 0;
    }

    @Override
    public long getNotificationTimestamp() {
        return targetTimestamp - options.notificationInterval;
    }

    private void pause() {
        pause = System.currentTimeMillis() + PAUSE;

        if (Logger.DEBUG) {
            Time time = new Time();
            time.set(pause);
            Logger.i(TAG, "Px[%s]: Widget will be paused till: %s", options.widgetId, time.format(Util.TF));
        }
    }

    private long getFastForward(long timestamp) {

        if (!isRepeating()) {
            return timestamp;
        }

        long now = System.currentTimeMillis();

        if (timestamp > now) {
            return timestamp;
        }

        if (Logger.DEBUG) {
            Time time = new Time();
            time.set(now);
            Logger.i(TAG, "Px[%s]: Now is: [%s]", options.widgetId, time.format(Util.TF));
        }
        long incrementMills = options.recurringInterval;
        long periodsCount = (now - timestamp) / incrementMills;

        if (!isCountingUp()) {
            periodsCount++;
        }

        long delta = periodsCount * incrementMills;

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
        return targetTimestamp + options.recurringInterval;
    }

    private void getNextEvent() {
        targetTimestamp = getFastForward(targetTimestamp);

        if (Logger.DEBUG) {
            Time time = new Time();
            time.set(targetTimestamp);
            Logger.i(TAG, "Px[%s]: New Target timestamp: [%s]", options.widgetId, time.format(Util.TF));
        }
    }

    private boolean isExpired() {
        // Non-recurring events never expires
        if (!isRepeating()) {
            return false;
        }

        if (isCountingUp()) {
            return (System.currentTimeMillis() >= (getNextTimestamp()));
        } else {
            return (System.currentTimeMillis() >= (targetTimestamp));
        }
    }

}
