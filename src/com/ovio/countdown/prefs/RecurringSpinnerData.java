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
public class RecurringSpinnerData extends SpinnerData<Long> {

    @Override
    protected Pair<Integer, Long> getDefault() {
        return getPair(R.string.recurring_none, 0L);
    }

    @Override
    protected List<Pair<Integer, Long>> getList() {
        return  new ArrayList<Pair<Integer, Long>>() {
            {
                add(getDefault());
                add(getPair(R.string.recurring_hourly, DateUtils.HOUR_IN_MILLIS));
                add(getPair(R.string.recurring_daily, DateUtils.DAY_IN_MILLIS));
                add(getPair(R.string.recurring_weekly, DateUtils.WEEK_IN_MILLIS));
                add(getPair(R.string.recurring_yearly, DateUtils.YEAR_IN_MILLIS));
            }
        };
    }

}

