package com.ovio.countdown.preferences;

/**
 * Countdown
 * com.ovio.countdown.preferences
 */
public class DefaultOptions {

    public boolean upward;

    public boolean enableSeconds;

    public boolean enableTime;

    public boolean repeating;

    public long repeatingPeriod;

    public int[] savedWidgets;

    @Override
    public String toString() {
        return "DefaultOptions{" +
                "upward=" + upward +
                ", enableSeconds=" + enableSeconds +
                ", enableTime=" + enableTime +
                ", repeating=" + repeating +
                ", repeatingPeriod=" + repeatingPeriod +
                ", savedWidgets=" + savedWidgets +
                '}';
    }
}
