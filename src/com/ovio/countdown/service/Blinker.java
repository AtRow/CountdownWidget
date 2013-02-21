package com.ovio.countdown.service;

import android.content.Context;
import android.os.PowerManager;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.proxy.Blinking;

import java.util.TreeMap;

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

    private final TreeMap<Integer, Blinking> blinkingMap = new TreeMap<Integer, Blinking>();

    private final PowerManager powerManager;

    private static Blinker instance;

    private Blinker(Context context) {
        powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    }

    public static synchronized Blinker getInstance(Context context) {
        if (instance == null) {
            instance = new Blinker(context);
        }
        Logger.d(TAG, "Returning Blinker instance");
        return instance;
    }

    public synchronized void register(int id, Blinking blinking) {
        Logger.i(TAG, "Registering new Blinking widget with id: %s", id);

        if (!blinkingMap.containsKey(id)) {
            blinkingMap.put(id, blinking);
            start();
        }
    }

    public synchronized void unRegister(int id) {
        Logger.i(TAG, "UnRegistering Blinking widget with id: %s", id);

        blinkingMap.remove(id);
        if (blinkingMap.isEmpty()) {
            stop();
        }
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
            for (Blinking blinking: blinkingMap.values()) {
                blinking.onBlink(show);
            }
        }

        private void unBlink() {
            for (Blinking blinking: blinkingMap.values()) {
                blinking.onBlink(true);
            }
        }

    }
}
