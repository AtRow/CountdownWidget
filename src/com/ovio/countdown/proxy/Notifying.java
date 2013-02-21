package com.ovio.countdown.proxy;

/**
 * Countdown
 * com.ovio.countdown.proxy
 */
public interface Notifying {

    void onNotify();

    long getNextNotifyTimestamp();

}
