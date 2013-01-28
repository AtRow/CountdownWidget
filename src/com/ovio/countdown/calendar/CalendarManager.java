package com.ovio.countdown.calendar;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import com.ovio.countdown.R;
import com.ovio.countdown.log.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Countdown
 * com.ovio.countdown.calendar
 */
public final class CalendarManager {

    private static final String TAG = Logger.PREFIX + "CalendarM";

    private static final String CALENDARS = "calendars";

    private static final String EVENTS = "events";

    private static CalendarManager instance;

    private final Activity activity;

    private Uri baseUri;

    public static final int ALL_CALENDARS = -1;

    public static final long NEAREST_EVENT = -1;


    private CalendarManager(Activity activity) {
        Logger.d(TAG, "Instantiated CalendarManager");
        this.activity = activity;

        getBaseUri();
    }

    public static synchronized CalendarManager getInstance(Activity activity) {
        if (instance == null) {
            instance = new CalendarManager(activity);
        }
        Logger.d(TAG, "Returning CalendarManager instance");
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
        List<Calendar> list = new ArrayList<Calendar>();
        Calendar allCalendars = new Calendar();
        allCalendars.id = ALL_CALENDARS;
        allCalendars.name = activity.getString(R.string.calendar_all_name);
        list.add(allCalendars);

        Uri calendarUri = Uri.withAppendedPath(baseUri, CALENDARS);

        String[] projection = getIdAndNameColumns();

        Cursor managedCursor = activity.managedQuery(calendarUri, projection, null, null, null);
        if (managedCursor != null && managedCursor.moveToFirst()) {

            int nameColumn = managedCursor.getColumnIndex(projection[1]);
            int idColumn = managedCursor.getColumnIndex(projection[0]);
            do {
                Calendar calendar = new Calendar();
                calendar.name = managedCursor.getString(nameColumn);
                calendar.id = managedCursor.getInt(idColumn);
                calendar.strId = managedCursor.getString(idColumn);
                list.add(calendar);

            } while (managedCursor.moveToNext());

            managedCursor.close();
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

    public List<Event> getEvents(int calendarId) {
        List<Event> list = new ArrayList<Event>();

        Event nearestEvent = new Event();
        nearestEvent.id = NEAREST_EVENT;
        nearestEvent.title = activity.getString(R.string.event_nearest_title);
        list.add(nearestEvent);

        Uri eventUri = Uri.withAppendedPath(baseUri, EVENTS);

        String[] projection = getEventColumns();
        String selection = null;

        if (calendarId != ALL_CALENDARS) {
            selection = Event.CALENDAR_ID + "=" + calendarId;
        }

        Cursor managedCursor = activity.managedQuery(eventUri, projection, selection, null, null);
        if (managedCursor != null && managedCursor.moveToFirst()) {

            do {
                Event event = new Event();

                event.id = managedCursor.getInt(managedCursor.getColumnIndex(Event.ID));
                event.title = managedCursor.getString(managedCursor.getColumnIndex(Event.TITLE));
                event.calendarId = managedCursor.getInt(managedCursor.getColumnIndex(Event.CALENDAR_ID));
                event.allDay = managedCursor.getInt(managedCursor.getColumnIndex(Event.ALL_DAY));
                event.start = managedCursor.getLong(managedCursor.getColumnIndex(Event.DTSTART));
                //event.end = managedCursor.getLong(managedCursor.getColumnIndex(Event.DTEND));
                event.timezone = managedCursor.getString(managedCursor.getColumnIndex(Event.EVENT_TIMEZONE));

                list.add(event);

            } while (managedCursor.moveToNext());

            managedCursor.close();
        }

        return list;
    }

    private String[] getEventColumns() {
        return new String[] { "_id", "allDay", "calendar_id", "dtstart", "eventTimezone", "title" };
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