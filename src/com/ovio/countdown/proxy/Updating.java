package com.ovio.countdown.proxy;

/**
 * Countdown
 * com.ovio.countdown.proxy
 */
public interface Updating {

    void onUpdate(long timestamp);

    long getNextUpdateTimestamp();

}
