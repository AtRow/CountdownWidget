package com.ovio.countdown.preferences;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.SharedPreferences;
import com.ovio.countdown.log.Logger;

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

        SharedPreferences preferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);

        widgetOptions.writeToPrefs(preferences);

//        editor.putInt("widgetId", widgetOptions.widgetId);
//        editor.putString("title", widgetOptions.title);
//        editor.putLong("timestamp", widgetOptions.timestamp);
//        editor.putBoolean("countUp", widgetOptions.countUp);
//        editor.putBoolean("enableTime", widgetOptions.enableTime);
//        editor.putBoolean("enableSeconds", widgetOptions.enableSeconds);
//        editor.putLong("recurringInterval", widgetOptions.recurringInterval.getMillis()) ;
//        editor.putString("calendarEventUrl", widgetOptions.calendarEventUrl);
//
//        editor.commit();
        Logger.d(TAG, "Finished saving WidgetOptions");
    }

    public WidgetOptions loadWidgetPrefs(String key) {

        Logger.i(TAG, "Loading WidgetOptions with key '%s'", key);

        SharedPreferences preferences = context.getSharedPreferences(key, Context.MODE_PRIVATE);

        int id = preferences.getInt("widgetId", AppWidgetManager.INVALID_APPWIDGET_ID);
        if (id == AppWidgetManager.INVALID_APPWIDGET_ID) {
            return null;
        }

        WidgetOptions widgetOptions = new WidgetOptions();

        widgetOptions.readFromPrefs(preferences);

//        widgetOptions.widgetId = id;
//        widgetOptions.title = prefs.getString("title", null);
//        widgetOptions.timestamp = prefs.getLong("timestamp", 0L);
//        widgetOptions.countUp = prefs.getBoolean("countUp", true);
//        widgetOptions.enableTime = prefs.getBoolean("enableTime", false);
//        widgetOptions.enableSeconds = prefs.getBoolean("enableSeconds", false);
//        widgetOptions.recurringInterval = Recurring.getRecurringFor(prefs.getLong("recurringInterval", 0L));
//        widgetOptions.calendarEventUrl = prefs.getString("calendarEventUrl", null);

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

    public void saveDefaultPrefs(GeneralOptions generalOptions) {

        Logger.i(TAG, "Updating Global options");
        Logger.d(TAG, "New Global options: %s", generalOptions);

        SharedPreferences prefs = context.getSharedPreferences(GLOBAL, Context.MODE_PRIVATE);

        generalOptions.writeToPrefs(prefs);

//        editor.putBoolean("countUp", generalOptions.countUp);
//        editor.putBoolean("enableSeconds", generalOptions.enableSeconds);
//        editor.putBoolean("enableTime", generalOptions.enableTime);
//        editor.putBoolean("recurring", generalOptions.recurring);
//        editor.putLong("recurringInterval", generalOptions.recurringInterval);
//
//        editor.putString("savedWidgetsString", Util.packIntArray(generalOptions.savedWidgets));
//
//        editor.commit();

        Logger.d(TAG, "Finished updating Global options");
    }

    public GeneralOptions loadDefaultPrefs() {

        Logger.i(TAG, "Loading Global options");

        GeneralOptions generalOptions = new GeneralOptions();
        SharedPreferences prefs = context.getSharedPreferences(GLOBAL, Context.MODE_PRIVATE);

        generalOptions.readFromPrefs(prefs);

//        generalOptions.countUp = prefs.getBoolean("countUp", true);
//        generalOptions.enableSeconds = prefs.getBoolean("enableSeconds", true);
//        generalOptions.enableTime = prefs.getBoolean("enableTime", false);
//        generalOptions.recurring = prefs.getBoolean("recurring", false);
//        generalOptions.recurringInterval = prefs.getLong("recurringInterval", 0L);
//
//        generalOptions.savedWidgets = Util.unpackIntArray(prefs.getString("savedWidgetsString", null));

        Logger.d(TAG, "Finished loading Global options: %s", generalOptions);
        return generalOptions;
    }

}
