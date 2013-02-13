package com.ovio.countdown.event;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import com.ovio.countdown.R;
import com.ovio.countdown.log.Logger;

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

    private static final String EVENTS = "instances/when";

    private static CalendarManager instance;

    private final Context context;

    private Uri baseUri;

    public static final int ALL_CALENDARS = -1;

    public static final long NEAREST_EVENT = -1;


    private CalendarManager(Context context) {
        Logger.d(TAG, "Instantiated CalendarManager");
        this.context = context;

        getBaseUri();
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

        ContentResolver contentResolver = context.getContentResolver();

        List<EventData> list = new ArrayList<EventData>();

//        CalendarEvent nearestCalendarEvent = new CalendarEvent();
//        nearestCalendarEvent.id = NEAREST_EVENT;
//        nearestCalendarEvent.title = context.getString(R.string.event_nearest_title);
        // TODO list.save(nearestCalendarEvent);

        Uri.Builder builder = Uri.withAppendedPath(baseUri, EVENTS).buildUpon();
        ContentUris.appendId(builder, startMills);
        ContentUris.appendId(builder, endMills);

        Uri eventUri = builder.build();

        String[] projection = EventData.COLUMNS;
        String selection = null;

        Cursor cursor = contentResolver.query(eventUri, projection, selection, null, null);
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

        return list;
    }

    private Uri getBaseUri() {
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

    public EventData getEvent(long timestamp, long eventId) {
        return null;
    }

    public EventData getNextEvent(long timestamp, long eventId) {
        return null;
    }
}