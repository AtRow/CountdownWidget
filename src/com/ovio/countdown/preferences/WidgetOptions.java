package com.ovio.countdown.preferences;

import android.appwidget.AppWidgetManager;
import com.ovio.countdown.prefs.Recurring;

import java.io.Serializable;

/**
 * Countdown
 * com.ovio.countdown.preferences
 */
public class WidgetOptions implements Serializable {

    public int widgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    public long eventId;

    public long calendarId;

    public String title;

    public long timestamp;

    public boolean countUp;

    public boolean enableSeconds;

    //private boolean repeating;

    public Recurring recurring;

    public String calendarEventUrl;

    public boolean enableTime;

    public boolean isReloading;

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
                ", repeatingPeriod=" + recurring.toString() +
                ", calendarEventUrl='" + calendarEventUrl + '\'' +
                '}';
    }
}
