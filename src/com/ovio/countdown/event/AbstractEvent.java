package com.ovio.countdown.event;

import com.ovio.countdown.preferences.WidgetOptions;

/**
 * Countdown
 * com.ovio.countdown.event
 */
public abstract class AbstractEvent implements Event {

    protected final WidgetOptions options;

    public AbstractEvent(WidgetOptions options) {
        this.options = options;
    }

    @Override
    public long getNotificationTimestamp() {
        // No notifications
        return 0;
    }

    @Override
    public boolean isNotifying() {
        // No notifications
        return false;
    }

    @Override
    public void reload() {
        // No reloading capabilities by default
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
    public int getIcon() {
        return options.icon;
    }

    @Override
    public int getStyle() {
        return options.style;
    }

}
