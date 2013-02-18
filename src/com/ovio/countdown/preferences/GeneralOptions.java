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
    public static final Field SAVED_WIDGETS = Field.get("savedWidgets", Array.class);

    private final static Field[] fields = {
            COUNT_UP,
            ENABLE_SECONDS,
            ENABLE_TIME,
            SAVED_WIDGETS
    };

    public boolean countUp;

    public boolean enableSeconds;

    public boolean enableTime;

    public int[] savedWidgets;


    public GeneralOptions() {
        super(fields);
    }

    @Override
    protected void updateFields() {

        countUp = bundle.getBoolean(COUNT_UP.name);

        enableSeconds = bundle.getBoolean(ENABLE_SECONDS.name);

        enableTime = bundle.getBoolean(ENABLE_TIME.name);

        savedWidgets = bundle.getIntArray(SAVED_WIDGETS.name);
    }

    @Override
    protected void updateBundle() {

        bundle.putBoolean(COUNT_UP.name, countUp);

        bundle.putBoolean(ENABLE_SECONDS.name, enableSeconds);

        bundle.putBoolean(ENABLE_TIME.name, enableTime);

        bundle.putIntArray(SAVED_WIDGETS.name, savedWidgets);
    }
}
