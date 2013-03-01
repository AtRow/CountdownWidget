package com.ovio.countdown.event;

import android.content.Context;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.preferences.WidgetOptions;
import com.ovio.countdown.preferences.WidgetPreferencesManager;

/**
 * Countdown
 * com.ovio.countdown.preferences
 */
public class CalendarEvent extends AbstractEvent {

    private final static String TAG = Logger.PREFIX + "CalendarEvent";

    private final CalendarManager manager;

    private EventData eventData;

    private boolean isValid;

    private final Context context;

    public CalendarEvent(Context context, WidgetOptions options) {
        super(options);

        this.context = context;

        manager = CalendarManager.getInstance(context);

        if (options.eventId > 0) {
            eventData = manager.getEvent(options.timestamp, options.eventId);
        }

        isValid = (eventData != null);
        if (!isValid) {
            eventData = new EventData();
        }
    }

    @Override
    public long getTargetTimestamp() {
        return getFastForward();
    }

    @Override
    public String getTitle() {
        return (eventData.title == null) ? "" : eventData.title;
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
    public synchronized void reload() {

        EventData newData = manager.getEventInstance(options.timestamp, options.eventId, options.instanceId);
        if (newData != null) {
            eventData = newData;

            options.instanceId = eventData.id;
            options.timestamp = eventData.start;
            WidgetPreferencesManager.getInstance(context).save(options);
        }

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
