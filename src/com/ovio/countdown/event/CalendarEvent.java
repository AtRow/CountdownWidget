package com.ovio.countdown.event;

import android.content.Context;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.preferences.WidgetOptions;

/**
 * Countdown
 * com.ovio.countdown.preferences
 */
public class CalendarEvent implements Event {

    private final static String TAG = Logger.PREFIX + "CalendarEvent";

    private final CalendarManager manager;

    private EventData eventData;

    private final WidgetOptions options;

    private boolean isValid;

    public CalendarEvent(Context context, WidgetOptions options) {
        this.options = options;
        manager = CalendarManager.getInstance(context);

        if (options.eventId > 0) {
            eventData = manager.getEvent(options.timestamp, options.eventId);
            isValid = true;
        } else {
            eventData = new EventData();
            isValid = false;
        }
    }


    @Override
    public boolean isCountingUp() {
        return options.countUp;
    }

    @Override
    public long getTargetTimestamp() {
        return getFastForward();
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
        return ((eventData.duration != null) || (eventData.rdate != null) || (eventData.rrule != null));
    }

    @Override
    public boolean isAlive() {
        return isValid && (isCountingUp() || isRepeating() ||
                (!isCountingUp() && (getTargetTimestamp() > System.currentTimeMillis())));
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

    private long getFastForward() {

        long now = System.currentTimeMillis();

        if (!isRepeating() || (eventData.start > now)) {
            return eventData.start;
        }

        EventData newData = null;

        if (isCountingUp()) {
            newData = getPreviousEvent(options.eventId, now);
        } else {
            newData = getNextEvent(options.eventId, now);
        }

        if (newData != null) {
            eventData = newData;
        }

        return eventData.start;
    }

    private EventData getPreviousEvent(long eventId, long before) {
        return manager.getPreviousEvent(eventId, before);
    }

    private EventData getNextEvent(long eventId, long after) {
        return manager.getNextEvent(eventId, after);
    }

}
