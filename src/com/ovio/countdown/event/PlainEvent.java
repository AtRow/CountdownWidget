package com.ovio.countdown.event;

import android.text.format.Time;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.preferences.WidgetOptions;
import com.ovio.countdown.util.Util;

/**
 * Countdown
 * com.ovio.countdown.event
 */
public class PlainEvent extends AbstractEvent {

    private final static String TAG = Logger.PREFIX + "plainEvent";

    public PlainEvent(WidgetOptions options) {
        super(options);
    }

    @Override
    public long getTargetTimestamp() {
        long targetTimestamp = getFastForward(options.timestamp);

        if (Logger.DEBUG) {
            Time time = new Time();
            time.set(targetTimestamp);
            Logger.i(TAG, "Pe[%s]: Target timestamp for widget: [%s]", options.widgetId, time.format(Util.TF));
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
        return options.countUp;
    }

    @Override
    public boolean isRepeating() {
        return (options.recurringInterval != 0L);
    }

    @Override
    public boolean isAlive() {
        return isCountingUp() || isRepeating() ||
                (!isCountingUp() && (getTargetTimestamp() > System.currentTimeMillis()));
    }

    @Override
    public boolean isNotifying() {
        return options.notificationInterval > 0;
    }

    @Override
    public long getNotificationTimestamp() {
        getTargetTimestamp();

        long now = System.currentTimeMillis();
        long targetTimestamp = getFastForward(options.timestamp);

        if (now > (targetTimestamp - options.notificationInterval)) {
            return targetTimestamp + options.recurringInterval - options.notificationInterval;
        } else {
            return targetTimestamp - options.notificationInterval;
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
}
