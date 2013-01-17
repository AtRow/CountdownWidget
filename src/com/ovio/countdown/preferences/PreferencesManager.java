package com.ovio.countdown.preferences;

import java.util.Date;

/**
 * Countdown
 * com.ovio.countdown.preferences
 */
public class PreferencesManager {

    public void deleteForWidget(int widgetId) {

    }

    public void deleteForAllWidgets(int[] widgetIds) {

        update(0, "a", new Date());

    }

    public <T> void update(int widgetId, String key, T object) {

        String clazzName = object.getClass().getName();


    }


    public <T> T get(Class<T> clazz) {
        return (T)new Date();
    }
}
