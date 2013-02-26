package com.ovio.countdown.proxy.painter;

import android.graphics.Bitmap;
import com.ovio.countdown.date.TimeDifference;

/**
 * Countdown
 * com.ovio.countdown.proxy
 */
public interface WidgetPainter {

    Bitmap getNewBitmap();

    Bitmap drawSeconds(Bitmap bitmap, int seconds);

    Bitmap drawTime(Bitmap bitmap, TimeDifference diff, int maxCountingVal);
}
