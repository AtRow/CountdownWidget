package com.ovio.countdown.preferences;

/**
 * Countdown
 * com.ovio.countdown.preferences
 */
public class DefaultPrefs {

    public int widgetId;

    public String title;

    public long timestamp;

    public boolean upward;

    public boolean countSeconds;

    //private boolean repeating;

    public long repeatingPeriod;

    public String calendarEventUrl;

    /**
     * long timestamp, boolean upward, boolean countSeconds
     * @return
     */
    public String packSettings() {
        StringBuilder sb = new StringBuilder();
        sb.append(timestamp).append(';').append(upward).append(';').append(countSeconds);
        return sb.toString();
    }

    public static long parseTimestamp(String packedString) {
        String[] chunks = packedString.split(";");
        return Long.getLong(chunks[0]);
    }

    public static boolean parseUpward(String packedString) {
        String[] chunks = packedString.split(";");
        return Boolean.getBoolean(chunks[1]);
    }

    public static boolean parseCountSeconds(String packedString) {
        String[] chunks = packedString.split(";");
        return Boolean.getBoolean(chunks[2]);
    }

    @Deprecated
    public interface RepeatingPeriod {
        int HOURLY = 0;
        int DAILY = 1;
        int WEEKLY = 2;

    }

}
