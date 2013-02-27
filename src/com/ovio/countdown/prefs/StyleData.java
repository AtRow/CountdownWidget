package com.ovio.countdown.prefs;

import com.ovio.countdown.R;

/**
 * Countdown
 * com.ovio.countdown.prefs
 */
public class StyleData extends ResourceData {

    public static final int DEFAULT = 0;

    private static StyleData instance;

    public static synchronized StyleData getInstance() {
        if (instance == null) {
            instance = new StyleData();
        }
        return instance;
    }

    public StyleData() {
        put(0, R.drawable.style_0);
        put(1, R.drawable.style_1);
        put(2, R.drawable.style_2);
    }

}
