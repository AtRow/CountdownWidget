package com.ovio.countdown.proxy;

import android.content.Context;
import com.ovio.countdown.R;
import com.ovio.countdown.log.Logger;
import com.ovio.countdown.preferences.WidgetOptions;

/**
 * Countdown
 * com.ovio.countdown
 */
public class LargeWidgetProxy extends WidgetProxy {

    private static final String TAG = Logger.PREFIX + "proxy";

    private static final int LAYOUT = R.layout.countdown_widget_layout_4x1;

    public LargeWidgetProxy(Context context, WidgetOptions options) {
        super(context, LAYOUT, options);
    }
}
