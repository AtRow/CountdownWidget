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
public final class EventManager {

    private static final String TAG = Logger.PREFIX + "CalendarM";

    private static final String CALENDARS = "calendars";

    private static final String EVENTS = "instances/when";

    private static EventManager instance;

    private final Context context;

    private Uri baseUri;

    public static final int ALL_CALENDARS = -1;

    public static final long NEAREST_EVENT = -1;


    private EventManager(Context context) {
        Logger.d(TAG, "Instantiated EventManager");
        this.context = context;

        getBaseUri();
    }

    public static synchronized EventManager getInstance(Context context) {
        if (instance == null) {
            instance = new EventManager(context);
        }
        Logger.d(TAG, "Returning EventManager instance");
        return instance;
    }

    public boolean isCompatible() {

        boolean compatible = (baseUri != null);

        if (compatible) {
            Logger.i(TAG, "Device is Calendar-compatible");
        } else {
            Logger.w(TAG, "Device is NOT Calendar-compatible");
        }
        return (compatible);
    }

    public List<Calendar> getCalendars() {

        ContentResolver contentResolver = context.getContentResolver();

        List<Calendar> list = new ArrayList<Calendar>();
        Calendar allCalendars = new Calendar();
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
                Calendar calendar = new Calendar();
                calendar.name = cursor.getString(nameColumn);
                calendar.id = cursor.getInt(idColumn);
                calendar.strId = cursor.getString(idColumn);
                list.add(calendar);

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

    public List<Event> getEvents(long startMills, long endMills) {

        ContentResolver contentResolver = context.getContentResolver();

        List<Event> list = new ArrayList<Event>();

        Event nearestEvent = new Event();
        nearestEvent.id = NEAREST_EVENT;
        nearestEvent.title = context.getString(R.string.event_nearest_title);
        // TODO list.add(nearestEvent);

        Uri.Builder builder = Uri.withAppendedPath(baseUri, EVENTS).buildUpon();
        ContentUris.appendId(builder, startMills);
        ContentUris.appendId(builder, endMills);

        Uri eventUri = builder.build();

        String[] projection = Event.COLUMNS;
        String selection = null;

        Cursor cursor = contentResolver.query(eventUri, projection, selection, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            do {
                Event event = new Event();

                event.id = cursor.getInt(cursor.getColumnIndex(Event.ID));
                event.title = cursor.getString(cursor.getColumnIndex(Event.TITLE));
                event.calendarId = cursor.getInt(cursor.getColumnIndex(Event.CALENDAR_ID));
                event.allDay = cursor.getInt(cursor.getColumnIndex(Event.ALL_DAY));
                event.start = cursor.getLong(cursor.getColumnIndex(Event.DTSTART));
                event.end = cursor.getLong(cursor.getColumnIndex(Event.DTEND));
                event.rdate = cursor.getString(cursor.getColumnIndex(Event.RDATE));

                event.duration = cursor.getString(cursor.getColumnIndex(Event.DURATION));

                event.timezone = cursor.getString(cursor.getColumnIndex(Event.EVENT_TIMEZONE));

                Logger.i(TAG, "Got Event: %s", event.toDebugString());

                list.add(event);

            } while (cursor.moveToNext());

            cursor.close();
        }

        return list;
    }

    private Uri getBaseUri() {
        Logger.i(TAG, "Getting Calendar Uri");

        if (baseUri == null) {
            Class<?> calendarProviderClass = null;
            try {
                calendarProviderClass = Class.forName("android.provider.Calendar");
                Field uriField = calendarProviderClass.getField("CONTENT_URI");
                baseUri = (Uri) uriField.get(null);

            } catch (ClassNotFoundException e) {
                Logger.e(TAG, "android.provider.Calendar class not available on device", e);

            } catch (NoSuchFieldException e) {
                Logger.e(TAG, "android.provider.Calendar doesn't provide CONTENT_URI", e);

            } catch (IllegalAccessException e) {
                Logger.e(TAG, "Can't find baseUri", e);
            }
        }

        Logger.i(TAG, "Calendar Uri: %s", baseUri.toString());

        return baseUri;
    }

}