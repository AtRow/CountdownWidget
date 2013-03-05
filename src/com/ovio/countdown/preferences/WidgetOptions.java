package com.ovio.countdown.preferences;

import android.appwidget.AppWidgetManager;

/**
 * Countdown
 * com.ovio.countdown.preferences
 */
public class WidgetOptions extends AbstractOptions {

    public static final Field WIDGET_ID = Field.get("widgetId", Integer.class);
    public static final Field EVENT_ID = Field.get("eventId", Long.class);
    public static final Field CALENDAR_ID = Field.get("calendarId", Long.class);
    public static final Field INSTANCE_ID = Field.get("instanceId", Long.class);
    public static final Field CONCRETE_EVENT = Field.get("concreteEvent", Boolean.class);
    public static final Field TITLE = Field.get("title", String.class);
    public static final Field TIMESTAMP = Field.get("timestamp", Long.class);
    public static final Field RECURRING_INTERVAL = Field.get("recurringInterval", Long.class);
    public static final Field NOTIFICATION_INTERVAL = Field.get("notificationInterval", Long.class);
    public static final Field ICON = Field.get("icon", Integer.class);


    private final static Field[] fields = {
            WIDGET_ID,
            EVENT_ID,
            CALENDAR_ID,
            INSTANCE_ID,
            CONCRETE_EVENT,
            TITLE,
            TIMESTAMP,
            GeneralOptions.COUNT_UP,
            GeneralOptions.ENABLE_TIME,
            GeneralOptions.ENABLE_SECONDS,
            RECURRING_INTERVAL,
            NOTIFICATION_INTERVAL,
            ICON,
            GeneralOptions.STYLE
    };

    public int widgetId;

    public long eventId;

    public long calendarId;

    public long instanceId;

    public boolean concreteEvent;

    public String title;

    public long timestamp;

    public boolean countUp;

    public boolean enableSeconds;

    public long recurringInterval;

    public long notificationInterval;

    public boolean enableTime;

    public int icon;

    public int style;

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

        instanceId = bundle.getLong(INSTANCE_ID.name);

        concreteEvent = bundle.getBoolean(CONCRETE_EVENT.name);

        title = bundle.getString(TITLE.name);

        timestamp = bundle.getLong(TIMESTAMP.name);

        countUp = bundle.getBoolean(GeneralOptions.COUNT_UP.name);

        enableTime = bundle.getBoolean(GeneralOptions.ENABLE_TIME.name);

        enableSeconds = bundle.getBoolean(GeneralOptions.ENABLE_SECONDS.name);

        recurringInterval = bundle.getLong(RECURRING_INTERVAL.name);

        notificationInterval = bundle.getLong(NOTIFICATION_INTERVAL.name);

        icon = bundle.getInt(ICON.name);

        style = bundle.getInt(GeneralOptions.STYLE.name);

    }

    @Override
    protected void updateBundle() {

        bundle.putInt(WIDGET_ID.name, widgetId);

        bundle.putLong(EVENT_ID.name, eventId);

        bundle.putLong(CALENDAR_ID.name, calendarId);

        bundle.putLong(INSTANCE_ID.name, instanceId);

        bundle.putBoolean(CONCRETE_EVENT.name, concreteEvent);

        bundle.putString(TITLE.name, title);

        bundle.putLong(TIMESTAMP.name, timestamp);

        bundle.putBoolean(GeneralOptions.COUNT_UP.name, countUp);

        bundle.putBoolean(GeneralOptions.ENABLE_TIME.name, enableTime);

        bundle.putBoolean(GeneralOptions.ENABLE_SECONDS.name, enableSeconds);

        bundle.putLong(RECURRING_INTERVAL.name, recurringInterval);

        bundle.putLong(NOTIFICATION_INTERVAL.name, notificationInterval);

        bundle.putInt(ICON.name, icon);

        bundle.putInt(GeneralOptions.STYLE.name, style);

    }
}
