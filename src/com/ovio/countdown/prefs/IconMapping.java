package com.ovio.countdown.prefs;

import com.ovio.countdown.R;

/**
 * Countdown
 * com.ovio.countdown.prefs
 */
public class IconMapping extends ResourceMapping {

    public static final int NONE = 0;

    private static IconMapping instance;

    public static synchronized IconMapping getInstance() {
        if (instance == null) {
            instance = new IconMapping();
        }
        return instance;
    }

    private IconMapping() {
        put(0, 0);
        put(1, R.drawable.icon_1);
        put(2, R.drawable.icon_2);
        put(3, R.drawable.icon_3);
        put(4, R.drawable.icon_4);
        put(5, R.drawable.icon_5);
        put(6, R.drawable.icon_6);
    }

}
