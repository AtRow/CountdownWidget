package com.ovio.countdown.preferences;

import android.appwidget.AppWidgetManager;

import java.io.Serializable;

/**
 * Countdown
 * com.ovio.countdown.preferences
 */
public class WidgetOptions implements Serializable {

    public int widgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    public String title;

    public long timestamp;

    public boolean countUp;

    public boolean enableSeconds;

    //private boolean repeating;

    public long repeatingPeriod;

    public String calendarEventUrl;

    public boolean enableTime;

    public boolean isValid() {
        return widgetId != AppWidgetManager.INVALID_APPWIDGET_ID;
    }

    @Override
    public String toString() {
        return "WidgetOptions{" +
                "widgetId=" + widgetId +
                ", title='" + title + '\'' +
                ", timestamp=" + timestamp +
                ", countUp=" + countUp +
                ", enableSeconds=" + enableSeconds +
                ", repeatingPeriod=" + repeatingPeriod +
                ", calendarEventUrl='" + calendarEventUrl + '\'' +
                '}';
    }
}
