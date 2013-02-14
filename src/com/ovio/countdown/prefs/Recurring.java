package com.ovio.countdown.prefs;

import android.text.format.DateUtils;
import com.ovio.countdown.R;

/**
 * Countdown
 * com.ovio.countdown.prefs
 */
public enum Recurring {

    NONE(R.string.recurring_none, 0),
    HOURLY(R.string.recurring_hourly, DateUtils.HOUR_IN_MILLIS),
    DAILY(R.string.recurring_daily, DateUtils.DAY_IN_MILLIS),
    WEEKLY(R.string.recurring_weekly, DateUtils.WEEK_IN_MILLIS),
    YEARLY(R.string.recurring_yearly, DateUtils.YEAR_IN_MILLIS),
    CUSTOM(R.string.recurring_custom);

    private final int textId;
    private long millis;

    Recurring(int textId, long millis) {
        this.textId = textId;
        this.millis = millis;
    }

    Recurring(int textId) {
        this.textId = textId;
    }

    public int getTextResId() {
        return textId;
    }

    public long getMillis() {
        return millis;
    }

    public static Recurring getRecurringFor(long millis) {
        if (millis == NONE.getMillis()) {
            return NONE;

        } else if (millis == HOURLY.getMillis()) {
            return HOURLY;

        } else if (millis == DAILY.getMillis()) {
            return DAILY;

        } else if (millis == WEEKLY.getMillis()) {
            return WEEKLY;

        } else if (millis == YEARLY.getMillis()) {
            return YEARLY;

        } else {
            return CUSTOM;
        }
    }
}

