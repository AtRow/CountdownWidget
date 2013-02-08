package com.ovio.countdown.preferences;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.SharedPreferences;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.util.Util;

/**
 * Countdown
 * com.ovio.countdown.preferences
 */
public final class PreferencesManager {

    private static final String GLOBAL = "preferences";

    private static final String TAG = Logger.PREFIX + "PrefM";

    private static PreferencesManager instance;

    private final Context context;

    private PreferencesManager(Context context) {
        Logger.d(TAG, "Instantiated PreferencesManager");
        this.context = context;
    }

    public static synchronized PreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferencesManager(context);
        }
        Logger.d(TAG, "Returning PreferencesManager instance");
        return instance;
    }

    public void saveWidgetPrefs(String key, WidgetOptions widgetOptions) {

        Logger.i(TAG, "Saving WidgetOptions with key '%s'", key);
        Logger.d(TAG, "WidgetOptions: %s", key, widgetOptions);

        SharedPreferences.Editor editor = context.getSharedPreferences(key, Context.MODE_PRIVATE).edit();

        editor.putInt("widgetId", widgetOptions.widgetId);
        editor.putString("title", widgetOptions.title);
        editor.putLong("timestamp", widgetOptions.timestamp);
        editor.putBoolean("countUp", widgetOptions.countUp);
        editor.putBoolean("enableSeconds", widgetOptions.enableSeconds);
        editor.putLong("repeatingPeriod", widgetOptions.repeatingPeriod);
        editor.putString("calendarEventUrl", widgetOptions.calendarEventUrl);

        editor.commit();
        Logger.d(TAG, "Finished saving WidgetOptions");
    }

    public WidgetOptions loadWidgetPrefs(String key) {

        Logger.i(TAG, "Loading WidgetOptions with key '%s'", key);

        SharedPreferences prefs = context.getSharedPreferences(key, Context.MODE_PRIVATE);

        int id = prefs.getInt("widgetId", AppWidgetManager.INVALID_APPWIDGET_ID);
        if (id == AppWidgetManager.INVALID_APPWIDGET_ID) {
            return null;
        }

        WidgetOptions widgetOptions = new WidgetOptions();

        widgetOptions.widgetId = id;
        widgetOptions.title = prefs.getString("title", null);
        widgetOptions.timestamp = prefs.getLong("timestamp", 0L);
        widgetOptions.countUp = prefs.getBoolean("countUp", true);
        widgetOptions.enableSeconds = prefs.getBoolean("enableSeconds", false);
        widgetOptions.repeatingPeriod = prefs.getLong("repeatingPeriod", 0L);
        widgetOptions.calendarEventUrl = prefs.getString("calendarEventUrl", null);

        Logger.d(TAG, "Finished loading WidgetOptions with key '%s': %s", key, widgetOptions);
        return widgetOptions;
    }

    public void deleteWidgetPrefs(String key) {

        Logger.i(TAG, "Deleting WidgetOptions with key '%s'", key);

        SharedPreferences.Editor editor = context.getSharedPreferences(key, Context.MODE_PRIVATE).edit();

        editor.clear();
        editor.commit();

        Logger.d(TAG, "Finished deleting WidgetOptions with key '%s'", key);
    }

    public void saveDefaultPrefs(DefaultOptions defaultOptions) {

        Logger.i(TAG, "Updating Global options");
        Logger.d(TAG, "New Global options: %s", defaultOptions);

        SharedPreferences.Editor editor = context.getSharedPreferences(GLOBAL, Context.MODE_PRIVATE).edit();

        editor.putBoolean("countUp", defaultOptions.upward);
        editor.putBoolean("enableSeconds", defaultOptions.enableSeconds);
        editor.putBoolean("enableTime", defaultOptions.enableTime);
        editor.putBoolean("repeating", defaultOptions.repeating);
        editor.putLong("repeatingPeriod", defaultOptions.repeatingPeriod);

        editor.putString("savedWidgetsString", Util.packIntArray(defaultOptions.savedWidgets));

        editor.commit();

        Logger.d(TAG, "Finished updating Global options");
    }

    public DefaultOptions loadDefaultPrefs() {

        Logger.i(TAG, "Loading Global options");

        DefaultOptions defaultOptions = new DefaultOptions();
        SharedPreferences prefs = context.getSharedPreferences(GLOBAL, Context.MODE_PRIVATE);

        defaultOptions.upward = prefs.getBoolean("countUp", true);
        defaultOptions.enableSeconds = prefs.getBoolean("enableSeconds", true);
        defaultOptions.enableTime = prefs.getBoolean("enableTime", false);
        defaultOptions.repeating = prefs.getBoolean("repeating", false);
        defaultOptions.repeatingPeriod = prefs.getLong("repeatingPeriod", 0L);

        defaultOptions.savedWidgets = Util.unpackIntArray(prefs.getString("savedWidgetsString", null));

        Logger.d(TAG, "Finished loading Global options: %s", defaultOptions);
        return defaultOptions;
    }

}
