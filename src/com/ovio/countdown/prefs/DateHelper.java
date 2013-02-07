package com.ovio.countdown.prefs;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;

public class DateHelper {
    public static final String YEAR = "Y";
    public static final String MONTH = "M";
    public static final String DAY = "D";
    public static final int INVALID_VALUE = -1;

    public static Time getDateFromIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            int year = extras.getInt(YEAR, INVALID_VALUE);
            int month = extras.getInt(MONTH, INVALID_VALUE);
            int day = extras.getInt(DAY, INVALID_VALUE);

            if ((year != INVALID_VALUE) &&
                    (month != INVALID_VALUE) &&
                    (day != INVALID_VALUE)) {

                Time date = new Time();
                date.hour = 0;
                date.minute = 0;
                date.second = 0;

                date.year = year;
                date.month = month;
                date.monthDay = day;

                return date;
            }
        }

        return null;
    }

    public static void putDateToIntent(Time date, Intent intent) {
        Bundle extras = new Bundle();
        extras.putInt(YEAR, date.year);
        extras.putInt(MONTH, date.month);
        extras.putInt(DAY, date.monthDay);

        intent.putExtras(extras);
    }
}