package com.ovio.countdown.event;

import android.text.format.Time;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.util.Util;

import java.io.Serializable;

/**
 * Countdown
 * com.ovio.countdown.preferences
 */
public class EventData implements Serializable {

    public static final String ID = "_id";
    public static final String EVENT_ID = "event_id";
    public static final String ALL_DAY = "allDay";
    public static final String CALENDAR_ID = "calendar_id";
    public static final String DTSTART = "begin";//"dtstart";
    public static final String DTEND = "dtend";
    public static final String RDATE = "rdate";
    public static final String RRULE = "rrule";
    public static final String DURATION = "duration";
    public static final String EVENT_TIMEZONE = "eventTimezone";
    public static final String TITLE = "title";

    public static final String[] COLUMNS = new String[] {ID, EVENT_ID, ALL_DAY, CALENDAR_ID, DTSTART, DTEND, RDATE, RRULE, DURATION, EVENT_TIMEZONE, TITLE};

    public long id;

    public long eventId;

    public int calendarId;

    public String title;

    public int allDay;

    public long start;

    public long end;

    public String timezone;

    public String rdate;

    public String rrule;

    public String duration;

    public String toDebugString() {
        String ts = Long.toString(start);
        if (Logger.DEBUG) {
            Time time = new Time();
            time.set(start);
            ts = time.format(Util.TF);
        }
        return "CalendarEvent{" +
                "id=" + id +
                ", eventId=" + eventId +
                ", calendarId=" + calendarId +
                ", title='" + title + '\'' +
                ", allDay=" + allDay +
                ", start=" + ts +
                ", end=" + end +
                ", timezone='" + timezone + '\'' +
                ", rdate='" + rdate + '\'' +
                ", rrule='" + rrule + '\'' +
                ", duration='" + duration + '\'' +
                '}';
    }

    @Override
    public String toString() {

        Time time = new Time();

        time.set(start);
        time.switchTimezone(timezone);
        time.normalize(false);

        String timeStr = time.format(Util.TF);

        return "" + id + ":" + timeStr + " : " + title;
    }
}
