package com.ovio.countdown.event;

import android.content.Context;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.preferences.WidgetOptions;

/**
 * Countdown
 * com.ovio.countdown.preferences
 */
public class CalendarEvent extends AbstractEvent {

    private final static String TAG = Logger.PREFIX + "CalendarEvent";

    private final CalendarManager manager;

    private EventData eventData;

    private boolean isValid;

    public CalendarEvent(Context context, WidgetOptions options) {
        super(options);

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
    public long getTargetTimestamp() {
        return getFastForward();
    }

    @Override
    public String getTitle() {
        return eventData.title;
    }

    @Override
    public boolean isRepeating() {
        return (!options.concreteEvent || (eventData.duration != null) || (eventData.rdate != null) || (eventData.rrule != null));
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

        if (options.concreteEvent) {
            if (isCountingUp()) {
                newData = getPreviousEvent(options.eventId, now);
            } else {
                newData = getNextEvent(options.eventId, now);
            }
        } else {
            if (isCountingUp()) {
                eventData = manager.getPreviousCalendarEvent(options.calendarId, now);
            } else {
                eventData = manager.getNextCalendarEvent(options.calendarId, now);
            }
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
