package com.ovio.countdown.service;

import com.ovio.countdown.log.Logger;
import com.ovio.countdown.proxy.WidgetProxy;

import java.util.Collection;

/**
 * Countdown
 * com.ovio.countdown.service
 */
public class SecondCounter {

    private static final String TAG = Logger.PREFIX + "SecCounter";

    private Thread secondCounterThread;

    private final Collection<WidgetProxy> widgetProxies;


    public SecondCounter(Collection<WidgetProxy> widgetProxies) {
        this.widgetProxies = widgetProxies;
    }

    public void start() {
        if (secondCounterThread == null) {
            secondCounterThread = new Thread(new SecondCounterRunnable());
        }
        if (!secondCounterThread.isAlive()) {
            secondCounterThread.start();
        }
    }

    public void stop() {
        if (secondCounterThread != null && secondCounterThread.isAlive()) {
            secondCounterThread.interrupt();
        }
    }

    protected class SecondCounterRunnable implements Runnable {

        private boolean stopRequested = false;

        @Override
        public void run() {
            Logger.i(TAG, "Started SecondCounterRunnable thread");

            while (!stopRequested) {
                try {

                    updateWidgetSeconds();

                    if (Logger.DEBUG) {
                        long now = System.currentTimeMillis();
                        long nextSecond = (now / 1000 + 1) * 1000;
                        long timeToSleep = nextSecond - now;

                        Logger.d(TAG, "Now    is: %s", now);
                        Logger.d(TAG, "Next S is: %s", nextSecond);
                        Logger.d(TAG, "To  Sleep: %s", timeToSleep);

                        Thread.sleep(timeToSleep);

                    } else {
                        Thread.sleep(((System.currentTimeMillis() / 1000 + 1) * 1000) - System.currentTimeMillis());
                    }

                } catch (InterruptedException e) {
                    Logger.i(TAG, "Interrupted SecondCounterRunnable thread, stopping");
                    stopRequested = true;
                }
            }
            Logger.i(TAG, "Finished SecondCounterRunnable thread");
        }

        private void updateWidgetSeconds() {
            if (Logger.DEBUG) {
                Logger.d(TAG, "Starting updating widget Seconds");
            }

            for (WidgetProxy proxy: widgetProxies) {

                if (Logger.DEBUG) {
                    Logger.d(TAG, "Walking trough widget %s to update seconds only", proxy.getOptions().widgetId);
                }

                if (proxy.isAlive && proxy.isCountingSeconds) {
                    proxy.updateWidgetSecondsOnly();
                }
            }
        }
    }
}
