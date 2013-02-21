package com.ovio.countdown.service;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.proxy.WidgetProxy;

import java.util.ArrayList;

/**
 * Countdown
 * com.ovio.countdown.service
 */
public class SecondCounter {

    private static final String TAG = Logger.PREFIX + "SecCounter";

    private static final long SEC = 1000L;

    private Thread secondCounterThread;

    private SecondCounterRunnable secondCounterRunnable;

    private final ArrayList<WidgetProxy> widgetProxies;

    private final PowerManager powerManager;


    public SecondCounter(Context context) {

        powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        widgetProxies = new ArrayList<WidgetProxy>();
    }

    public void start() {
        if (powerManager.isScreenOn() && (secondCounterThread == null || !secondCounterThread.isAlive())) {
            secondCounterRunnable = new SecondCounterRunnable();

            secondCounterThread = new Thread(secondCounterRunnable);
            secondCounterThread.start();
        }
    }

    public void stop() {
        if (secondCounterThread != null && secondCounterThread.isAlive()) {
            secondCounterRunnable.prepareToStop();
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

            while (!stopRequested && powerManager.isScreenOn()) {
                try {

                    updateWidgetSeconds();

                    if ((System.currentTimeMillis() - now) > 2 * SEC) {
                        Log.w(TAG, "Lagging, sleeping 1 sec");
                        Thread.sleep(SEC);
                    }

                    now = System.currentTimeMillis();
                    nextSecond = (now / SEC + 1) * SEC + 10;
                    toSleep = nextSecond - now;

                    if (Logger.DEBUG) {
                        Log.w(TAG, "n: " + now + " ns: " + nextSecond + " ts: " + toSleep);
                    }

                    if (toSleep > 0) {
                        Thread.sleep(toSleep);
                    }

                } catch (InterruptedException e) {
                    Logger.i(TAG, "Interrupted SecondCounterRunnable thread");
                }
            }
            Logger.i(TAG, "Finished SecondCounterRunnable thread");
        }

        public void prepareToStop() {
            stopRequested = true;
        }

        private void updateWidgetSeconds() {
            if (Logger.DEBUG) {
                Logger.i(TAG, "Starting updating widget Seconds");
            }

            for (WidgetProxy proxy: widgetProxies) {

                //if (proxy.isCountingSeconds()) {
                    proxy.updateWidget(System.currentTimeMillis());
                //}
            }
        }
    }
}
