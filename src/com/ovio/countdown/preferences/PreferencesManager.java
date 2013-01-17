package com.ovio.countdown.preferences;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.SharedPreferences;
import com.ovio.countdown.util.Util;

/**
 * Countdown
 * com.ovio.countdown.preferences
 */
public final class PreferencesManager {

    private static final String GLOBAL = "global";

    private static PreferencesManager instance;

    private Context context;

    private PreferencesManager(Context context) {
        this.context = context;
    }

    public static synchronized PreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferencesManager(context);
        }
        return instance;
    }

    public void saveWidgetPrefs(String key, WidgetOptions widgetOptions) {

        SharedPreferences.Editor editor = context.getSharedPreferences(key, Context.MODE_PRIVATE).edit();

        editor.putInt("widgetId", widgetOptions.widgetId);
        editor.putString("title", widgetOptions.title);
        editor.putLong("timestamp", widgetOptions.timestamp);
        editor.putBoolean("upward", widgetOptions.upward);
        editor.putBoolean("enableSeconds", widgetOptions.enableSeconds);
        editor.putLong("repeatingPeriod", widgetOptions.repeatingPeriod);
        editor.putString("calendarEventUrl", widgetOptions.calendarEventUrl);

        editor.commit();
    }

    public WidgetOptions loadWidgetPrefs(String key) {

        SharedPreferences prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE);

        int id = prefs.getInt("widgetId", AppWidgetManager.INVALID_APPWIDGET_ID);
        if (id == AppWidgetManager.INVALID_APPWIDGET_ID) {
            return null;
        }

        WidgetOptions widgetOptions = new WidgetOptions();

        widgetOptions.widgetId = id;
        widgetOptions.title = prefs.getString("title", null);
        widgetOptions.timestamp = prefs.getLong("timestamp", 0L);
        widgetOptions.upward = prefs.getBoolean("upward", true);
        widgetOptions.enableSeconds = prefs.getBoolean("enableSeconds", false);
        widgetOptions.repeatingPeriod = prefs.getLong("repeatingPeriod", 0L);
        widgetOptions.calendarEventUrl = prefs.getString("calendarEventUrl", null);

        return widgetOptions;
    }

    public void deleteWidgetPrefs(String key) {

        SharedPreferences.Editor editor = context.getSharedPreferences(key, Context.MODE_PRIVATE).edit();

        editor.clear();
        editor.commit();
    }

    public void saveDefaultPrefs(DefaultOptions defaultOptions) {

        SharedPreferences.Editor editor = context.getSharedPreferences(GLOBAL, Context.MODE_PRIVATE).edit();

        editor.putBoolean("upward", defaultOptions.upward);
        editor.putBoolean("enableSeconds", defaultOptions.enableSeconds);
        editor.putBoolean("enableTime", defaultOptions.enableTime);
        editor.putBoolean("repeating", defaultOptions.repeating);
        editor.putLong("repeatingPeriod", defaultOptions.repeatingPeriod);

        editor.putString("savedWidgetsString", Util.packIntArray(defaultOptions.savedWidgets));

        editor.commit();
    }

    public DefaultOptions loadDefaultPrefs() {

        DefaultOptions defaultOptions = new DefaultOptions();
        SharedPreferences prefs = context.getSharedPreferences(GLOBAL, Context.MODE_PRIVATE);

        defaultOptions.upward = prefs.getBoolean("upward", true);
        defaultOptions.enableSeconds = prefs.getBoolean("enableSeconds", true);
        defaultOptions.enableTime = prefs.getBoolean("enableTime", false);
        defaultOptions.repeating = prefs.getBoolean("repeating", false);
        defaultOptions.repeatingPeriod = prefs.getLong("repeatingPeriod", 0L);

        defaultOptions.savedWidgets = Util.unpackIntArray(prefs.getString("savedWidgetsString", null));

        return defaultOptions;
    }

}
