package com.ovio.countdown;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;
import com.ovio.countdown.preferences.PreferencesManager;
import com.ovio.countdown.preferences.WidgetOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Countdown
 * com.ovio.countdown
 */
public class WidgetPreferencesManager {

    private static final String TAG = "W_PREFS";

    private static final String PREFIX = "widget_";

    private static final String TEXT = "text";

    private final Context context;

    private final PreferencesManager prefManager;


    public WidgetPreferencesManager(Context context) {
        this.context = context;

        prefManager = new PreferencesManager(context);
    }

    public List<Integer> validate(List<Integer> widgetIds) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);

        List<Integer> validIds = new ArrayList<Integer>(widgetIds.size());
        for (int id: widgetIds) {
            if (manager.getAppWidgetInfo(id) != null) {
                validIds.add(id);
            } else {
                delete(id);
            }
        }

        return validIds;
    }


    public void update(int widgetId) {
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);

        RemoteViews views = null;
        int layout = widgetManager.getAppWidgetInfo(widgetId).initialLayout;

        Log.d(TAG, "Updating widget [" + widgetId + "] with layout [" + layout + "]");

        switch (layout) {
            case R.layout.countdown_widget_layout_4x1 :
                views = new RemoteViews(context.getPackageName(), R.layout.countdown_widget_layout_4x1);
                break;

            default:
                // TODO: Exceptions
                Log.e(TAG, "Layout not found");
        }

        WidgetOptions prefs = prefManager.loadWidgetPrefs(PREFIX + widgetId);

        if (prefs == null) {
            throw new RuntimeException("Can't find preferences for widget: " + widgetId);
        }

        views.setCharSequence(R.id.titleTextView, "setText", prefs.title);
        views.setCharSequence(R.id.hiddenTextView, "setText", prefs.packSettings());

        widgetManager.updateAppWidget(widgetId, views);

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
    }
}
