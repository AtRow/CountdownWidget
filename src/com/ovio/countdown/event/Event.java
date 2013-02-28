package com.ovio.countdown.event;

/**
 * Countdown
 * com.ovio.countdown.event
 */
public interface Event {

    long getTargetTimestamp();

    String getTitle();

    boolean isCountingSeconds();

    boolean isCountingUp();

    boolean isRepeating();

    boolean isAlive();

    long getNotificationTimestamp();

    boolean isNotifying();

    int getIcon();

    int getStyle();

    void reload();
}
