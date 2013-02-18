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
    YEARLY(R.string.recurring_yearly, DateUtils.YEAR_IN_MILLIS);
    //CUSTOM(R.string.recurring_custom);

    public final int textId;
    public long millis;

    Recurring(int textId, long millis) {
        this.textId = textId;
        this.millis = millis;
    }

    Recurring(int textId) {
        this.textId = textId;
    }

    public static Recurring getRecurringFor(long millis) {
        if (millis == HOURLY.millis) {
            return HOURLY;

        } else if (millis == DAILY.millis) {
            return DAILY;

        } else if (millis == WEEKLY.millis) {
            return WEEKLY;

        } else if (millis == YEARLY.millis) {
            return YEARLY;

        } else {
            return NONE;
        }
    }
}

