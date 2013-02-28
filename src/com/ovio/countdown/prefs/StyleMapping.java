package com.ovio.countdown.prefs;

import com.ovio.countdown.R;

/**
 * Countdown
 * com.ovio.countdown.prefs
 */
public class StyleMapping extends ResourceMapping {

    public static final int DEFAULT = 0;

    private static StyleMapping instance;

    public static synchronized StyleMapping getInstance() {
        if (instance == null) {
            instance = new StyleMapping();
        }
        return instance;
    }

    public StyleMapping() {
        put(0, R.drawable.style_0);
        put(1, R.drawable.style_1);
        put(2, R.drawable.style_2);
    }

}
