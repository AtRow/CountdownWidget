package com.ovio.countdown.service;

import android.content.Context;
import android.os.PowerManager;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.proxy.WidgetProxy;

import java.util.ArrayList;

/**
 * Countdown
 * com.ovio.countdown.service
 */
public class Blinker {

    private static final String TAG = Logger.PREFIX + "Blinker";

    private static final int SHOW = 400;
    private static final int HIDE = 400;

    private Thread blinkerThread;

    private BlinkerRunnable blinkerRunnable;

    private final ArrayList<WidgetProxy> widgetProxies;

    private final PowerManager powerManager;


    public Blinker(Context context) {

        powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        widgetProxies = new ArrayList<WidgetProxy>();
    }

    public void start() {
        if (powerManager.isScreenOn() && (blinkerThread == null || !blinkerThread.isAlive())) {
            blinkerRunnable = new BlinkerRunnable();

            blinkerThread = new Thread(blinkerRunnable);
            blinkerThread.start();
        }
    }

    public void stop() {
        if (blinkerThread != null && blinkerThread.isAlive()) {
            blinkerRunnable.prepareToStop();
            blinkerThread.interrupt();
        }
    }

    protected class BlinkerRunnable implements Runnable {

        private boolean stopRequested = false;

        @Override
        public void run() {
            Logger.i(TAG, "Started BlinkerRunnable thread");

            while (!stopRequested && powerManager.isScreenOn()) {
                try {

                    blink(true);
                    Thread.sleep(SHOW);

                    blink(false);
                    Thread.sleep(HIDE);

                } catch (InterruptedException e) {
                    Logger.i(TAG, "Interrupted BlinkerRunnable thread");
                }
            }
            unBlink();
            Logger.i(TAG, "Finished BlinkerRunnable thread");
        }

        public void prepareToStop() {
            stopRequested = true;
        }

        private void blink(boolean show) {
            for (WidgetProxy proxy: widgetProxies) {
                //if (proxy.isBlinking()) {
                    proxy.onBlink(show);
                //}
            }
        }

        private void unBlink() {
            for (WidgetProxy proxy: widgetProxies) {
                proxy.onBlink(true);
            }
        }

    }
}
