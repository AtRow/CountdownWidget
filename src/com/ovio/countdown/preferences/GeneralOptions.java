package com.ovio.countdown.preferences;

import java.lang.reflect.Array;

/**
 * Countdown
 * com.ovio.countdown.preferences
 */
public class GeneralOptions extends AbstractOptions {

    public static final Field COUNT_UP = Field.get("countUp", Boolean.class);
    public static final Field ENABLE_SECONDS = Field.get("enableSeconds", Boolean.class);
    public static final Field ENABLE_TIME = Field.get("enableTime", Boolean.class);
    public static final Field RECURRING = Field.get("recurring", Boolean.class);
    public static final Field RECURRING_INTERVAL = Field.get("recurringInterval", Long.class);
    public static final Field SAVED_WIDGETS = Field.get("savedWidgets", Array.class);

    private final static Field[] fields = {
            COUNT_UP,
            ENABLE_SECONDS,
            ENABLE_TIME,
            RECURRING,
            RECURRING_INTERVAL,
            SAVED_WIDGETS
    };

    public boolean countUp;

    public boolean enableSeconds;

    public boolean enableTime;

    public boolean recurring;

    public long recurringInterval;

    public int[] savedWidgets;


    public GeneralOptions() {
        super(fields);
    }

    @Override
    protected void updateFields() {

        countUp = bundle.getBoolean(COUNT_UP.name);

        enableSeconds = bundle.getBoolean(ENABLE_SECONDS.name);

        enableTime = bundle.getBoolean(ENABLE_TIME.name);

        recurring = bundle.getBoolean(RECURRING.name);

        recurringInterval = bundle.getLong(RECURRING_INTERVAL.name);

        savedWidgets = bundle.getIntArray(SAVED_WIDGETS.name);
    }

    @Override
    protected void updateBundle() {

        bundle.putBoolean(COUNT_UP.name, countUp);

        bundle.putBoolean(ENABLE_SECONDS.name, enableSeconds);

        bundle.putBoolean(ENABLE_TIME.name, enableTime);

        bundle.putBoolean(RECURRING.name, recurring);

        bundle.putLong(RECURRING_INTERVAL.name, recurringInterval);

        bundle.putIntArray(SAVED_WIDGETS.name, savedWidgets);
    }
}
