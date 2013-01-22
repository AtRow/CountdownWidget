package com.ovio.countdown.service;

import android.util.Log;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.proxy.WidgetProxy;

import java.util.Collection;

/**
 * Countdown
 * com.ovio.countdown.service
 */
public class SecondCounter {

    private static final String TAG = Logger.PREFIX + "SecCounter";

    private static final long SEC = 1000L;

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

            long nextSecond;
            long toSleep;
            long now = System.currentTimeMillis();

            while (!stopRequested) {
                try {

                    updateWidgetSeconds();

                    if ((System.currentTimeMillis() - now) > 2 * SEC) {
                        Log.e(TAG, "Lagging, sleeping 5 secs");
                        Thread.currentThread().sleep(5 * SEC);
                        System.gc();
                    }

                    now = System.currentTimeMillis();
                    nextSecond = (now / SEC + 1) * SEC + 10;

                    toSleep = nextSecond - now;

                    Log.w(TAG, "n: " + now + " ns: " + nextSecond + " ts: " + toSleep);

                    if (toSleep > 0) {
                        Thread.currentThread().sleep(toSleep);
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

            long second = (System.currentTimeMillis() / SEC) * SEC;

            for (WidgetProxy proxy: widgetProxies) {

                if (Logger.DEBUG) {
                    Logger.d(TAG, "Walking trough widget %s to update seconds only", proxy.getOptions().widgetId);
                }

                if (proxy.isAlive && proxy.isCountingSeconds) {
                    proxy.updateWidgetSecondsOnly(second);
                }
            }
        }
    }
}
