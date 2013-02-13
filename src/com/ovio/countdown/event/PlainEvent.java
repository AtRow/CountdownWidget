package com.ovio.countdown.event;

import android.content.Context;
import com.ovio.countdown.preferences.WidgetOptions;
import com.ovio.countdown.preferences.WidgetPreferencesManager;

/**
 * Countdown
 * com.ovio.countdown.event
 */
public class PlainEvent implements Event {
    protected final WidgetOptions options;
    private final WidgetPreferencesManager widgetPreferencesManager;

    protected final Context context;

    private final static int SECOND = 1000;
    private final static int MINUTE = (SECOND * 60);
    private final static int HOUR = (MINUTE * 60);
    private final static int DAY = (HOUR * 24);

    public PlainEvent(Context context, WidgetOptions options) {
        this.options = options;
        this.context = context;

        widgetPreferencesManager = WidgetPreferencesManager.getInstance(context);
    }

    @Override
    public long getTargetTimestamp() {
        if (isExpired()) {
            getNextEvent();
        }
        return options.timestamp;
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
        return options.isRepeating;
    }

    private long getNextTimestamp() {
        return options.timestamp + options.repeatingPeriod;
    }

    private void getNextEvent() {
        options.timestamp = getNextTimestamp();
        //TODO async
        widgetPreferencesManager.save(options);
    }

    private boolean isExpired() {
        // Non-repeating events never expires
        if (!isRepeating()) {
            return false;
        }

        if (isCountingUp()) {
            return (System.currentTimeMillis() >= getNextTimestamp());
        } else {
            return (System.currentTimeMillis() >= (options.timestamp + MINUTE));
        }
    }

}
