package com.ovio.countdown.preferences;

import android.content.Context;
import com.ovio.countdown.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Countdown
 * com.ovio.countdown
 */
public class WidgetPreferencesManager {

    private static final String TAG = "W_PREFS";

    private static final String PREFIX = "widget_";

    private static WidgetPreferencesManager instance;

    private final PreferencesManager prefManager;

    private WidgetPreferencesManager(Context context) {
        prefManager = PreferencesManager.getInstance(context);
    }

    public static synchronized WidgetPreferencesManager getInstance(Context context) {
        if(instance == null) {
            instance = new WidgetPreferencesManager(context);
        }
        return instance;
    }

    public List<Integer> cleanup(List<Integer> validWidgetIds) {
        DefaultOptions defaultOptions = prefManager.loadDefaultPrefs();

        List<Integer> deletedIds = new ArrayList<Integer>(validWidgetIds.size());

        for (int id: defaultOptions.savedWidgets) {

            if (!validWidgetIds.contains(id)) {
                delete(id);
                deletedIds.add(id);
            }
        }

        defaultOptions.savedWidgets = Util.toIntArray(validWidgetIds);
        prefManager.saveDefaultPrefs(defaultOptions);

        return deletedIds;
    }

    public WidgetOptions load(int widgetId) {
        return prefManager.loadWidgetPrefs(PREFIX + widgetId);
    }

    public void add(WidgetOptions widgetOptions) {
        prefManager.saveWidgetPrefs(PREFIX + widgetOptions.widgetId, widgetOptions);
    }

    public void delete(int widgetId) {
        prefManager.deleteWidgetPrefs(PREFIX + widgetId);
    }

    public void deleteAll(List<Integer> widgetIds) {
        for (int id: widgetIds) {
            delete(id);
        }

        DefaultOptions defaultOptions = prefManager.loadDefaultPrefs();
        List<Integer> list = Util.toIntegerList(defaultOptions.savedWidgets);
        list.removeAll(widgetIds);
        defaultOptions.savedWidgets = Util.toIntArray(list);
    }
}
