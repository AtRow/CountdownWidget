package com.ovio.countdown.prefs;

import android.text.format.DateUtils;
import android.util.Pair;
import com.ovio.countdown.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Countdown
 * com.ovio.countdown.prefs
 */
public class NotificationSpinnerData extends SpinnerData<Long> {

    @Override
    protected Pair<Integer, Long> getDefault() {
        return getPair(R.string.notification_none, 0L);
    }

    @Override
    protected List<Pair<Integer, Long>> getList() {
        return  new ArrayList<Pair<Integer, Long>>() {
            {
                add(getDefault());
                add(getPair(R.string.notification_1_min, DateUtils.MINUTE_IN_MILLIS));
                add(getPair(R.string.notification_5_min, DateUtils.MINUTE_IN_MILLIS * 5));
                add(getPair(R.string.notification_10_min, DateUtils.MINUTE_IN_MILLIS * 10));
                add(getPair(R.string.notification_15_min, DateUtils.MINUTE_IN_MILLIS * 15));
                add(getPair(R.string.notification_20_min, DateUtils.MINUTE_IN_MILLIS * 20));
                add(getPair(R.string.notification_25_min, DateUtils.MINUTE_IN_MILLIS * 25));
                add(getPair(R.string.notification_30_min, DateUtils.MINUTE_IN_MILLIS * 30));
                add(getPair(R.string.notification_45_min, DateUtils.MINUTE_IN_MILLIS * 45));
                add(getPair(R.string.notification_1_hour, DateUtils.HOUR_IN_MILLIS));
                add(getPair(R.string.notification_2_hour, DateUtils.HOUR_IN_MILLIS * 2));
                add(getPair(R.string.notification_3_hour, DateUtils.HOUR_IN_MILLIS * 3));
                add(getPair(R.string.notification_12_hour, DateUtils.HOUR_IN_MILLIS * 12));
                add(getPair(R.string.notification_24_hour, DateUtils.HOUR_IN_MILLIS * 24));
                add(getPair(R.string.notification_2_day, DateUtils.DAY_IN_MILLIS));
                add(getPair(R.string.notification_1_week, DateUtils.WEEK_IN_MILLIS));
            }
        };
    }

}
