package com.ovio.countdown.proxy.painter;

/**
 * Countdown
 * com.ovio.countdown.proxy.painter
 */
public class WordFormHelper {

    public static final int SINGLE = 0;
    public static final int SEMI = 1;
    public static final int PLURAL = 2;

    public static int getWordForm(int value) {

        if (value == 1) {
            return SINGLE;

        } else if (value >= 2 && value <= 4) {
            return SEMI;

        } else if (value >= 21) {
            int mod = value % 10;
            return getWordForm(mod);

        } else {
            return PLURAL;
        }
    }
}
