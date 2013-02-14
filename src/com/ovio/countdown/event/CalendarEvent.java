package com.ovio.countdown.event;

import android.content.Context;
import android.text.format.DateUtils;
import com.ovio.countdown.preferences.WidgetOptions;

/**
 * Countdown
 * com.ovio.countdown.preferences
 */
public class CalendarEvent implements Event {

    private final CalendarManager manager;

    private EventData eventData;
    private long nextTimestamp;
    private final WidgetOptions options;

    public CalendarEvent(Context context, WidgetOptions options) {
        this.options = options;
        manager = CalendarManager.getInstance(context);

        if (options.eventId > 0) {
            eventData = manager.getEvent(options.timestamp, options.eventId);
        } else {
            eventData = new EventData();
        }
    }


    @Override
    public boolean isCountingUp() {
        return options.countUp;
    }

    @Override
    public long getTargetTimestamp() {
        return eventData.start;
    }

    @Override
    public String getTitle() {
        return eventData.title;
    }

    @Override
    public boolean isCountingSeconds() {
        return options.enableSeconds;
    }

    @Override
    public boolean isRepeating() {
        return (eventData != null) || isReloading();
    }

    @Override
    public boolean isAlive() {
        if (isCountingUp() || isRepeating() ||
           (!isCountingUp() && (getTargetTimestamp() > System.currentTimeMillis()))) {

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isPaused() {
        return false;
    }

    @Override
    public long getPausedTill() {
        return System.currentTimeMillis() + DateUtils.SECOND_IN_MILLIS;
    }

    private boolean isReloading() {
        return options.isReloading;
    }

    private long getNextTimestamp() {
        if (nextTimestamp == 0) {
            EventData data = manager.getNextEvent(options.timestamp, options.eventId);
            nextTimestamp = data.start;
        }
        return nextTimestamp;
    }

    private void getNextEvent() {
        eventData = manager.getNextEvent(options.timestamp, options.eventId);
    }


}
