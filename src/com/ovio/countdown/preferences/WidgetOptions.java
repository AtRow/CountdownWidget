package com.ovio.countdown.preferences;

import java.io.Serializable;

/**
 * Countdown
 * com.ovio.countdown.preferences
 */
public class WidgetOptions implements Serializable {

    public int widgetId;

    public String title;

    public long timestamp;

    public boolean upward;

    public boolean enableSeconds;

    //private boolean repeating;

    public long repeatingPeriod;

    public String calendarEventUrl;

}
