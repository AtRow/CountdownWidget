package com.ovio.countdown.proxy.painter;

import android.content.Context;
import android.graphics.Bitmap;
import com.ovio.countdown.date.TimeDifference;

/**
 * Countdown
 * com.ovio.countdown.proxy
 */
public interface WidgetPainter {

    Bitmap getNewBitmap(Context context, int resource);

    Bitmap drawTime(Bitmap bitmap, TimeDifference diff, int maxCountingVal);

    Bitmap drawHeader(Bitmap bitmap, String title, Bitmap icon);
}
