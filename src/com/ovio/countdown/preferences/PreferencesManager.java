package com.ovio.countdown.preferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Countdown
 * com.ovio.countdown.preferences
 */
public class PreferencesManager {

    private Context context;

    public PreferencesManager(Context context) {
        this.context = context;
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

        WidgetOptions widgetOptions = new WidgetOptions();
        SharedPreferences prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE);

        widgetOptions.widgetId = prefs.getInt("widgetId", 0);
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

    public void saveDefaultPrefs(String key, DefaultPrefs defaultPrefs) {

        SharedPreferences.Editor editor = context.getSharedPreferences(key, Context.MODE_PRIVATE).edit();

        editor.putBoolean("upward", defaultPrefs.upward);
        editor.putBoolean("enableSeconds", defaultPrefs.enableSeconds);
        editor.putBoolean("enableTime", defaultPrefs.enableTime);
        editor.putBoolean("repeating", defaultPrefs.repeating);
        editor.putLong("repeatingPeriod", defaultPrefs.repeatingPeriod);

        editor.commit();
    }

    public DefaultPrefs loadDefaultPrefs(String key) {

        DefaultPrefs defaultPrefs = new DefaultPrefs();
        SharedPreferences prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE);

        defaultPrefs.upward = prefs.getBoolean("upward", true);
        defaultPrefs.enableSeconds = prefs.getBoolean("enableSeconds", true);
        defaultPrefs.enableTime = prefs.getBoolean("enableTime", false);
        defaultPrefs.repeating = prefs.getBoolean("repeating", false);
        defaultPrefs.repeatingPeriod = prefs.getLong("repeatingPeriod", 0L);

        return defaultPrefs;
    }

}
