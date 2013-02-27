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
