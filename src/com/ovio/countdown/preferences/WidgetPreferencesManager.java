package com.ovio.countdown.preferences;

import android.content.Context;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Countdown
 * com.ovio.countdown
 */
public class WidgetPreferencesManager {

    private static final String TAG = Logger.PREFIX + "preferences";

    private static final String PREFIX = "widget_";

    private static WidgetPreferencesManager instance;

    private final PreferencesManager prefManager;

    private WidgetPreferencesManager(Context context) {
        Logger.d(TAG, "Instantiated WidgetPreferencesManager");
        prefManager = PreferencesManager.getInstance(context);
    }

    public static synchronized WidgetPreferencesManager getInstance(Context context) {
        if(instance == null) {
            instance = new WidgetPreferencesManager(context);
        }
        Logger.d(TAG, "Returning WidgetPreferencesManager instance");
        return instance;
    }

    public List<Integer> cleanup(List<Integer> validWidgetIds) {

        Logger.i(TAG, "Starting cleanup");
        Logger.i(TAG, "Valid widget ids are: %s", Util.getString(validWidgetIds));

        GeneralOptions generalOptions = prefManager.loadDefaultPrefs();

        List<Integer> deletedIds = new ArrayList<Integer>(validWidgetIds.size());

        for (int id: generalOptions.savedWidgets) {

            if (!validWidgetIds.contains(id)) {
                delete(id);
                deletedIds.add(id);
            }
        }

        Logger.d(TAG, "Updating savedWidgets; Current content is: %s", Util.getString(generalOptions.savedWidgets));

        generalOptions.savedWidgets = Util.toIntArray(validWidgetIds);
        prefManager.saveDefaultPrefs(generalOptions);

        Logger.d(TAG, "New content is: %s", Util.getString(generalOptions.savedWidgets));

        Logger.d(TAG, "Finished cleanup");
        return deletedIds;
    }

    public WidgetOptions load(int widgetId) {
        Logger.i(TAG, "Loading widget %s", widgetId);
        return prefManager.loadWidgetPrefs(PREFIX + widgetId);
    }

    public void save(WidgetOptions widgetOptions) {
        Logger.i(TAG, "Adding widget %s", widgetOptions.widgetId);
        prefManager.saveWidgetPrefs(PREFIX + widgetOptions.widgetId, widgetOptions);
    }

    public void delete(int widgetId) {
        Logger.i(TAG, "Deleting widget %s", widgetId);
        prefManager.deleteWidgetPrefs(PREFIX + widgetId);
    }

    public void deleteAll(List<Integer> widgetIds) {
        Logger.d(TAG, "Deleting a list of widgets");
        for (int id: widgetIds) {
            delete(id);
        }

        GeneralOptions generalOptions = prefManager.loadDefaultPrefs();

        Logger.d(TAG, "Updating savedWidgets; Current content is: %s", Util.getString(generalOptions.savedWidgets));

        List<Integer> list = Util.toIntegerList(generalOptions.savedWidgets);
        list.removeAll(widgetIds);
        generalOptions.savedWidgets = Util.toIntArray(list);

        Logger.d(TAG, "Updated content is: %s", Util.getString(generalOptions.savedWidgets));

        prefManager.saveDefaultPrefs(generalOptions);
    }

    public List<Integer> getAllIds() {
        Logger.d(TAG, "Getting a list of all widget Ids");

        GeneralOptions generalOptions = prefManager.loadDefaultPrefs();
        int[] alIds = generalOptions.savedWidgets;

        return Util.toIntegerList(alIds);
    }
}
