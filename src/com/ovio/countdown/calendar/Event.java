package com.ovio.countdown.calendar;

import android.text.format.Time;
import com.ovio.countdown.util.Util;

/**
 * Countdown
 * com.ovio.countdown.preferences
 */
public class Event {

    public static final String ID = "_id";
    public static final String ALL_DAY = "allDay";
    public static final String CALENDAR_ID = "calendar_id";
    public static final String DTSTART = "dtstart";
    public static final String DTEND = "dtend";
    public static final String EVENT_TIMEZONE = "eventTimezone";
    public static final String TITLE = "title";

    public long id;

    public int calendarId;

    public String title;

    public int allDay;

    public long start;

    public long end;

    public String timezone;

    @Override
    public String toString() {

        Time time = new Time();

        time.set(start);
        time.timezone = timezone;
        time.normalize(false);

        String timeStr = time.format(Util.TF);

        return timeStr + " : " + title;
    }
}
