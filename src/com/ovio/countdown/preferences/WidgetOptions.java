package com.ovio.countdown.preferences;

import android.appwidget.AppWidgetManager;
import com.ovio.countdown.prefs.Recurring;

/**
 * Countdown
 * com.ovio.countdown.preferences
 */
public class WidgetOptions extends AbstractOptions {

    public static final Field WIDGET_ID = Field.get("widgetId", Integer.class);
    public static final Field EVENT_ID = Field.get("eventId", Long.class);
    public static final Field CALENDAR_ID = Field.get("calendarId", Long.class);
    public static final Field TITLE = Field.get("title", String.class);
    public static final Field TIMESTAMP = Field.get("timestamp", Long.class);

    private final static Field[] fields = {
            WIDGET_ID,
            EVENT_ID,
            CALENDAR_ID,
            TITLE,
            TIMESTAMP,
            GeneralOptions.COUNT_UP,
            GeneralOptions.ENABLE_TIME,
            GeneralOptions.ENABLE_SECONDS,
            GeneralOptions.RECURRING_INTERVAL,
            GeneralOptions.RECURRING
    };

    public int widgetId;

    public long eventId;

    public long calendarId;

    public String title;

    public long timestamp;

    public boolean countUp;

    public boolean enableSeconds;

    public boolean recurring;

    public Recurring recurringInterval;

    public boolean enableTime;

    public WidgetOptions() {
        super(fields);
        bundle.putInt(WIDGET_ID.name, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    public boolean isValid() {
        return bundle.getInt(WIDGET_ID.name) != AppWidgetManager.INVALID_APPWIDGET_ID;
    }

    @Override
    protected void updateFields() {

        widgetId = bundle.getInt(WIDGET_ID.name);

        eventId = bundle.getLong(EVENT_ID.name);

        calendarId = bundle.getLong(CALENDAR_ID.name);

        title = bundle.getString(TITLE.name);

        timestamp = bundle.getLong(TIMESTAMP.name);

        countUp = bundle.getBoolean(GeneralOptions.COUNT_UP.name);

        enableTime = bundle.getBoolean(GeneralOptions.ENABLE_TIME.name);

        enableSeconds = bundle.getBoolean(GeneralOptions.ENABLE_SECONDS.name);

        recurring = bundle.getBoolean(GeneralOptions.RECURRING.name);

        long millis = bundle.getLong(GeneralOptions.RECURRING_INTERVAL.name);
        recurringInterval = Recurring.getRecurringFor(millis);

    }

    @Override
    protected void updateBundle() {

        bundle.putInt(WIDGET_ID.name, widgetId);

        bundle.putLong(EVENT_ID.name, eventId);

        bundle.putLong(CALENDAR_ID.name, calendarId);

        bundle.putString(TITLE.name, title);

        bundle.putLong(TIMESTAMP.name, timestamp);

        bundle.putBoolean(GeneralOptions.COUNT_UP.name, countUp);

        bundle.putBoolean(GeneralOptions.ENABLE_TIME.name, enableTime);

        bundle.putBoolean(GeneralOptions.ENABLE_SECONDS.name, enableSeconds);

        bundle.putBoolean(GeneralOptions.RECURRING.name, recurring);

        bundle.putLong(GeneralOptions.RECURRING_INTERVAL.name, recurringInterval.millis);

    }
}
