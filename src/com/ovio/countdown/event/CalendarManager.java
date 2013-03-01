package com.ovio.countdown.event;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.format.DateUtils;
import android.text.format.Time;
import com.ovio.countdown.R;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.util.Util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Countdown
 * com.ovio.countdown.event
 */
public final class CalendarManager {

    private static final String TAG = Logger.PREFIX + "CalendarM";

    private static final String CALENDARS = "calendars";

    private static final String INSTANCES = "instances/when";

    private static final long[] QUERY_TIMESTAMP_ITERATIONS = {
            DateUtils.DAY_IN_MILLIS,
            DateUtils.DAY_IN_MILLIS * 30,
            DateUtils.YEAR_IN_MILLIS,
            DateUtils.YEAR_IN_MILLIS * 10
    };

    private static CalendarManager instance;

    private final Context context;


    private Uri baseUri;

    public static final int ALL_CALENDARS = -1;

    public static final int NONE_CALENDARS = -2;

    public static final long NEAREST_EVENT = -1;


    private CalendarManager(Context context) {
        Logger.d(TAG, "Instantiated CalendarManager");
        this.context = context;

        obtainBaseUri();
    }

    public static synchronized CalendarManager getInstance(Context context) {
        if (instance == null) {
            instance = new CalendarManager(context);
        }
        Logger.d(TAG, "Returning CalendarManager instance");
        return instance;
    }

    public boolean isCompatible() {

        boolean compatible = (baseUri != null);

        if (compatible) {
            Logger.i(TAG, "Device is CalendarData-compatible");
        } else {
            Logger.w(TAG, "Device is NOT CalendarData-compatible");
        }
        return (compatible);
    }

    public List<CalendarData> getCalendars() {

        ContentResolver contentResolver = context.getContentResolver();

        List<CalendarData> list = new ArrayList<CalendarData>();

        CalendarData noneCalendars = new CalendarData();
        noneCalendars.id = NONE_CALENDARS;
        noneCalendars.name = context.getString(R.string.calendar_none_name);
        list.add(noneCalendars);

        CalendarData allCalendars = new CalendarData();
        allCalendars.id = ALL_CALENDARS;
        allCalendars.name = context.getString(R.string.calendar_all_name);
        list.add(allCalendars);

        Uri calendarUri = Uri.withAppendedPath(baseUri, CALENDARS);

        String[] projection = getIdAndNameColumns();

        Cursor cursor = contentResolver.query(calendarUri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            int nameColumn = cursor.getColumnIndex(projection[1]);
            int idColumn = cursor.getColumnIndex(projection[0]);
            do {
                CalendarData calendarData = new CalendarData();
                calendarData.name = cursor.getString(nameColumn);
                calendarData.id = cursor.getInt(idColumn);
                calendarData.strId = cursor.getString(idColumn);
                list.add(calendarData);

            } while (cursor.moveToNext());

            cursor.close();
        }

        return list;
    }

    private String[] getIdAndNameColumns() {
        String name = "displayname";

        try {
            Class<?> calendarClass = Class.forName("android.provider.CalendarContract.CalendarColumns");

            // TODO
            name = "calendar_displayName";
        } catch (ClassNotFoundException e) {
            Logger.w(TAG, "Device uses API earlier then 14");
        }

        return new String[] { "_id", name };
    }

    public List<EventData> getEvents(long startMills, long endMills) {
        if (Logger.DEBUG) {
            Time time1 = new Time();
            time1.set(startMills);
            Time time2 = new Time();
            time2.set(endMills);
            Logger.i(TAG, "Getting events between %s and %s", time1.format(Util.TF), time2.format(Util.TF));
        }

        Uri eventUri = getUriBetween(startMills, endMills);

        String[] projection = EventData.COLUMNS;
        String selection = null;

        return queryEvents(eventUri, projection, selection);
    }

    private ArrayList<EventData> queryEvents(Uri eventUri, String[] projection, String selection) {

        Logger.i(TAG, "Querying events for selection: %s", selection);

        ArrayList<EventData> list = new ArrayList<EventData>();
        ContentResolver contentResolver = context.getContentResolver();

        String orderBy = EventData.DTSTART + " asc";

        Cursor cursor = contentResolver.query(eventUri, projection, selection, null, orderBy);
        if (cursor != null && cursor.moveToFirst()) {

            do {
                EventData data = new EventData();

                data.id = cursor.getInt(cursor.getColumnIndex(EventData.ID));
                data.eventId = cursor.getInt(cursor.getColumnIndex(EventData.EVENT_ID));
                data.title = cursor.getString(cursor.getColumnIndex(EventData.TITLE));
                data.calendarId = cursor.getInt(cursor.getColumnIndex(EventData.CALENDAR_ID));
                data.allDay = cursor.getInt(cursor.getColumnIndex(EventData.ALL_DAY));
                data.start = cursor.getLong(cursor.getColumnIndex(EventData.DTSTART));
                data.end = cursor.getLong(cursor.getColumnIndex(EventData.DTEND));
                data.rdate = cursor.getString(cursor.getColumnIndex(EventData.RDATE));
                data.duration = cursor.getString(cursor.getColumnIndex(EventData.DURATION));
                data.timezone = cursor.getString(cursor.getColumnIndex(EventData.EVENT_TIMEZONE));

                Logger.i(TAG, "Got EventData: %s", data.toDebugString());

                list.add(data);

            } while (cursor.moveToNext());

            cursor.close();
        }
        Logger.i(TAG, "Got %s entries", list.size());

        return list;
    }

    private Uri obtainBaseUri() {
        Logger.i(TAG, "Getting CalendarData Uri");

        if (baseUri == null) {
            Class<?> calendarProviderClass = null;
            try {
                calendarProviderClass = Class.forName("android.provider.CalendarData");
                Field uriField = calendarProviderClass.getField("CONTENT_URI");
                baseUri = (Uri) uriField.get(null);

            } catch (ClassNotFoundException e) {
                Logger.e(TAG, "android.provider.CalendarData class not available on device", e);

            } catch (NoSuchFieldException e) {
                Logger.e(TAG, "android.provider.CalendarData doesn't provide CONTENT_URI", e);

            } catch (IllegalAccessException e) {
                Logger.e(TAG, "Can't find baseUri", e);
            }
        }

        if (baseUri == null) {

            Uri calendarsUri = Uri.parse("content://calendar/calendars");
            Cursor managedCursor = null;

            try {
                managedCursor = context.getContentResolver().query(calendarsUri, null, null, null, null);
            } catch (Exception e) {
                Logger.e(TAG, "URI 'content://calendar/calendars' is not valid for this device", e);

            }
            if (managedCursor != null) {
                baseUri = Uri.parse("content://calendar/");
                managedCursor.close();

            } else {
                calendarsUri = Uri.parse("content://com.android.calendar/calendars");

                try {
                    managedCursor = context.getContentResolver().query(calendarsUri, null, null, null, null);
                } catch (Exception e) {
                    Logger.e(TAG, "URI 'content://com.android.calendar/calendars' is not valid for this device", e);

                }
                if (managedCursor != null) {
                    baseUri = Uri.parse("content://com.android.calendar/");
                }
            }
        }

        if (baseUri == null) {
            // Give up
            Logger.e(TAG, "No calendar support found on this device");
        } else {
            Logger.i(TAG, "CalendarData Uri: %s", baseUri.toString());
        }

        return baseUri;
    }

    public Uri getBaseUri() {
        return baseUri;
    }

    public EventData getEvent(long timestamp, long eventId) {

        if (Logger.DEBUG) {
            Time time = new Time();
            time.set(timestamp);
            Logger.i(TAG, "Getting exact event with id %s and TS %s", eventId, time.format(Util.TF));
        }

        Uri eventUri = getUriBetween(timestamp, timestamp);

        String[] projection = EventData.COLUMNS;
        String selection = EventData.EVENT_ID + " = " + eventId;

        ArrayList<EventData> events = queryEvents(eventUri, projection, selection);
        Logger.i(TAG, "Got %s events", events.size());

        if (events.size() == 1) {
            return events.get(0);
        }

        Logger.w(TAG, "Exact event not found");
        return null;
    }


    public EventData getEventInstance(long timestamp, long eventId, long instanceId) {

        Logger.i(TAG, "Getting exact Event Instance by eventId %s and instanceId %s", eventId, instanceId);

        String[] projection = EventData.COLUMNS;
        String selection = EventData.EVENT_ID + " = " + eventId + " AND Instances._id = " + instanceId;

        for (int i = 0; i < QUERY_TIMESTAMP_ITERATIONS.length; i++) {

            Logger.i(TAG, "Running %s search query iteration", i);
            if (Logger.DEBUG) {
                Time time1 = new Time();
                time1.set(timestamp - QUERY_TIMESTAMP_ITERATIONS[i]);
                Time time2 = new Time();
                time2.set(timestamp + QUERY_TIMESTAMP_ITERATIONS[i]);
                Logger.i(TAG, "Looking between %s and %s", time1.format(Util.TF), time2.format(Util.TF));
            }

            Uri eventUri = getUriBetween(timestamp - QUERY_TIMESTAMP_ITERATIONS[i], timestamp + QUERY_TIMESTAMP_ITERATIONS[i]);
            ArrayList<EventData> events = queryEvents(eventUri, projection, selection);
            Logger.i(TAG, "Got %s events", events.size());

            if (events.size() == 1) {
                return events.get(0);
            }
        }

        Logger.w(TAG, "Exact Event Instance not found");
        Logger.w(TAG, "Trying to find nearest Event Instance only by eventId %s", eventId);

        selection = EventData.EVENT_ID + " = " + eventId;

        for (int i = 0; i < QUERY_TIMESTAMP_ITERATIONS.length; i++) {

            Logger.i(TAG, "Running %s search query iteration", i);
            if (Logger.DEBUG) {
                Time time1 = new Time();
                time1.set(timestamp - QUERY_TIMESTAMP_ITERATIONS[i]);
                Time time2 = new Time();
                time2.set(timestamp + QUERY_TIMESTAMP_ITERATIONS[i]);
                Logger.i(TAG, "Looking between %s and %s", time1.format(Util.TF), time2.format(Util.TF));
            }

            Uri eventUri = getUriBetween(timestamp - QUERY_TIMESTAMP_ITERATIONS[i], timestamp + QUERY_TIMESTAMP_ITERATIONS[i]);
            ArrayList<EventData> events = queryEvents(eventUri, projection, selection);
            Logger.i(TAG, "Got %s events", events.size());

            if (!events.isEmpty()) {

                int index = 0;
                long diff = Math.abs(timestamp - events.get(0).start);
                for (int j = 1; j < events.size(); j++) {
                    long nextDiff = Math.abs(timestamp - events.get(j).start);
                    if (nextDiff < diff) {
                        diff = nextDiff;
                        index = j;
                    }
                }
                return events.get(index);
            }
        }

        Logger.w(TAG, "Nearby Event not found");
        return null;
    }


    public EventData getNextEvent(long eventId, long base) {

        if (Logger.DEBUG) {
            Time time = new Time();
            time.set(base);
            Logger.i(TAG, "Getting next event with id %s after %s", eventId, time.format(Util.TF));
        }

        String[] projection = EventData.COLUMNS;
        String selection = EventData.EVENT_ID + " = " + eventId + " AND " + EventData.DTSTART + " > " + base;

        for (int i = 0; i < QUERY_TIMESTAMP_ITERATIONS.length; i++) {

            Logger.i(TAG, "Running %s search query iteration", i);
            if (Logger.DEBUG) {
                Time time1 = new Time();
                time1.set(base - QUERY_TIMESTAMP_ITERATIONS[i]);
                Time time2 = new Time();
                time2.set(base + QUERY_TIMESTAMP_ITERATIONS[i]);
                Logger.i(TAG, "Looking between %s and %s", time1.format(Util.TF), time2.format(Util.TF));
            }

            Uri eventUri = getUriBetween(base - QUERY_TIMESTAMP_ITERATIONS[i], base + QUERY_TIMESTAMP_ITERATIONS[i]);
            ArrayList<EventData> events = queryEvents(eventUri, projection, selection);
            Logger.i(TAG, "Got %s events", events.size());

            if (!events.isEmpty()) {
                return events.get(0);
            }
        }

        Logger.w(TAG, "Next Event not found");
        return null;
    }


    public EventData getPreviousEvent(long eventId, long base) {

        if (Logger.DEBUG) {
            Time time = new Time();
            time.set(base);
            Logger.i(TAG, "Getting previous event with id %s before %s", eventId, time.format(Util.TF));
        }

        String[] projection = EventData.COLUMNS;
        String selection = EventData.EVENT_ID + " = " + eventId + " AND " + EventData.DTSTART + " < " + base;

        for (int i = 0; i < QUERY_TIMESTAMP_ITERATIONS.length; i++) {

            Logger.i(TAG, "Running %s search query iteration", i);
            if (Logger.DEBUG) {
                Time time1 = new Time();
                time1.set(base - QUERY_TIMESTAMP_ITERATIONS[i]);
                Time time2 = new Time();
                time2.set(base + QUERY_TIMESTAMP_ITERATIONS[i]);
                Logger.i(TAG, "Looking between %s and %s", time1.format(Util.TF), time2.format(Util.TF));
            }

            Uri eventUri = getUriBetween(base - QUERY_TIMESTAMP_ITERATIONS[i], base + QUERY_TIMESTAMP_ITERATIONS[i]);
            ArrayList<EventData> events = queryEvents(eventUri, projection, selection);
            Logger.i(TAG, "Got %s events", events.size());

            if (!events.isEmpty()) {
                return events.get(events.size() - 1);
            }
        }

        Logger.w(TAG, "Previous Event not found");
        return null;
    }

    public EventData getPreviousCalendarEvent(long calendarId, long base) {

        if (Logger.DEBUG) {
            Time time = new Time();
            time.set(base);
            Logger.i(TAG, "Getting previous event for Calendar id %s before %s", calendarId, time.format(Util.TF));
        }

        String[] projection = EventData.COLUMNS;

        String selection = EventData.DTSTART + " < " + base;
        if (calendarId != ALL_CALENDARS) {
            selection += " AND " + EventData.CALENDAR_ID + " = " + calendarId;
        }

        for (int i = 0; i < QUERY_TIMESTAMP_ITERATIONS.length; i++) {

            Logger.i(TAG, "Running %s search query iteration", i);
            if (Logger.DEBUG) {
                Time time1 = new Time();
                time1.set(base - QUERY_TIMESTAMP_ITERATIONS[i]);
                Time time2 = new Time();
                time2.set(base + QUERY_TIMESTAMP_ITERATIONS[i]);
                Logger.i(TAG, "Looking between %s and %s", time1.format(Util.TF), time2.format(Util.TF));
            }

            Uri eventUri = getUriBetween(base - QUERY_TIMESTAMP_ITERATIONS[i] - QUERY_TIMESTAMP_ITERATIONS[i], base + QUERY_TIMESTAMP_ITERATIONS[i]);
            ArrayList<EventData> events = queryEvents(eventUri, projection, selection);
            Logger.i(TAG, "Got %s events", events.size());

            if (!events.isEmpty()) {
                return events.get(events.size() - 1);
            }
        }

        Logger.w(TAG, "Previous Calendar Event not found");
        return null;
    }

    public EventData getNextCalendarEvent(long calendarId, long base) {

        if (Logger.DEBUG) {
            Time time = new Time();
            time.set(base);
            Logger.i(TAG, "Getting next event for Calendar id %s after %s", calendarId, time.format(Util.TF));
        }

        String[] projection = EventData.COLUMNS;

        String selection = EventData.DTSTART + " > " + base;
        if (calendarId != ALL_CALENDARS) {
            selection += " AND " + EventData.CALENDAR_ID + " = " + calendarId;
        }

        for (int i = 0; i < QUERY_TIMESTAMP_ITERATIONS.length; i++) {

            Logger.i(TAG, "Running %s search query iteration", i);
            if (Logger.DEBUG) {
                Time time1 = new Time();
                time1.set(base - QUERY_TIMESTAMP_ITERATIONS[i]);
                Time time2 = new Time();
                time2.set(base + QUERY_TIMESTAMP_ITERATIONS[i]);
                Logger.i(TAG, "Looking between %s and %s", time1.format(Util.TF), time2.format(Util.TF));
            }

            Uri eventUri = getUriBetween(base - QUERY_TIMESTAMP_ITERATIONS[i], base + QUERY_TIMESTAMP_ITERATIONS[i]);
            ArrayList<EventData> events = queryEvents(eventUri, projection, selection);
            Logger.i(TAG, "Got %s events", events.size());

            if (!events.isEmpty()) {
                return events.get(0);
            }
        }

        Logger.w(TAG, "Next Calendar Event not found");
        return null;
    }

    private Uri getUriBetween(long startMills, long endMills) {
        Uri.Builder builder = Uri.withAppendedPath(baseUri, INSTANCES).buildUpon();
        ContentUris.appendId(builder, startMills);
        ContentUris.appendId(builder, endMills);

        return builder.build();
    }

}