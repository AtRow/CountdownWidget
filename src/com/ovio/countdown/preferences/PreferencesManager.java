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

        Logger.d(TAG, "Finished updating Global options");
    }

    public GeneralOptions loadDefaultPrefs() {

        Logger.i(TAG, "Loading Global options");

        GeneralOptions generalOptions = new GeneralOptions();
        SharedPreferences prefs = context.getSharedPreferences(GLOBAL, Context.MODE_PRIVATE);
        generalOptions.readFromPrefs(prefs);


        Logger.d(TAG, "Finished loading Global options: %s", generalOptions);
        return generalOptions;
    }

}
