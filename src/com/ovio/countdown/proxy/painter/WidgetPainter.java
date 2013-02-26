package com.ovio.countdown.proxy.painter;

import android.graphics.Bitmap;
import com.ovio.countdown.date.TimeDifference;

/**
 * Countdown
 * com.ovio.countdown.proxy
 */
public interface WidgetPainter {

    Bitmap getNewBitmap();

    Bitmap drawTime(Bitmap bitmap, TimeDifference diff, int maxCountingVal);

    Bitmap drawHeader(Bitmap bitmap, String title, Bitmap icon);
}
