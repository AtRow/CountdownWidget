package com.ovio.countdown.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ovio.countdown.R;

import java.util.TimeZone;

/**
 * Countdown
 * com.ovio.countdown.view
 */
@Deprecated
public class CompoundCounterView extends LinearLayout {

    private TextView counter;

    private TextView hiddenView;

    private Time time;

    private boolean needsUpdate;

    private long targetTimestamp;

    private boolean countUpward;

    private boolean countSeconds;

    private int highestMeasure;

    private int lowestMeasure;

    private boolean isSubscribed;

    private final Handler handler = new Handler();

    private final CounterBroadcastReceiver broadcastReceiver = new CounterBroadcastReceiver();


    public CompoundCounterView(Context context) {
        super(context);
    }

    public CompoundCounterView(Context context, AttributeSet attrs) {
        super(context, attrs);

        time = new Time();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        counter = (TextView) findViewById(R.id.counterTextView);
        //hiddenView = (TextView) findViewById(R.id.hiddenTextView);

        hiddenView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateSettings(editable.toString());
            }
        });

        if (!isSubscribed) {

            IntentFilter filter = new IntentFilter();

            filter.addAction(Intent.ACTION_TIME_TICK);
            filter.addAction(Intent.ACTION_TIME_CHANGED);
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);

            getContext().registerReceiver(broadcastReceiver, filter, null, handler);

            isSubscribed = true;
        }

        time.setToNow();
        onTick();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (isSubscribed) {
            getContext().unregisterReceiver(broadcastReceiver);
            isSubscribed = false;
        }
    }

    protected int getHighestMeasure() {
        if (time.year > 0) {
            return Time.YEAR;
        } else if (time.month > 0) {
            return Time.MONTH;
        } else if (time.monthDay > 0) {
            return Time.MONTH_DAY;
        } else {
            if (countSeconds) {
                return Time.MONTH_DAY;
            } else {
                return Time.HOUR;
            }
        }
/*
        } else if (time.hour > 0) {
            return Time.HOUR;
        } else if (time.minute > 0) {
            return Time.MINUTE;
        } else {
            return Time.SECOND;
        }
*/
    }

    protected int getLowestMeasure(int highestMeasure) {
        switch (highestMeasure) {
            case Time.YEAR:
                return Time.MONTH_DAY;
            case Time.MONTH:
                return Time.HOUR;
            case Time.MONTH_DAY:
                return Time.MINUTE;
            default:
                if (countSeconds) {
                    return Time.SECOND;
                } else {
                    return Time.MINUTE;
                }
        }
    }

    protected void updateCounter(int highestMeasure, int lowestMeasure) {
        StringBuilder sb = new StringBuilder();

        switch (highestMeasure) {
            case Time.YEAR:
                sb.append(time.year).append("y ").
                        append(time.month).append("m ").
                        append(time.monthDay).append("d");
                break;
            case Time.MONTH:
                sb.append(time.month).append("m ").
                        append(time.monthDay).append("d ").
                        append(time.hour).append("h");
                break;
            case Time.MONTH_DAY:
                sb.append(time.monthDay).append("d ").
                        append(time.hour).append("h ").
                        append(time.minute).append("m");
                break;
            default:
                if (countSeconds) {
                    sb.append(time.hour).append("h ").
                            append(time.minute).append("m ").
                            append(time.second).append("s");
                } else {
                    sb.append(time.monthDay).append("d ").
                            append(time.hour).append("h ").
                            append(time.minute).append("m");
                }
        }

        counter.setText(sb.toString());
    }



    private void onTick() {
        time.setToNow();
        time.normalize(false);

        highestMeasure = getHighestMeasure();
        lowestMeasure = getLowestMeasure(highestMeasure);

        updateCounter(highestMeasure, lowestMeasure);

/*      TODO: For now, will always listen to ACTION_TIME_TICK
        if (lowestMeasure == Time.MINUTE || lowestMeasure == Time.SECOND) {
            subscribeMinuteTicks();
        } else {
            unSubscribeMinuteTicks();
            scheduleAlarmAt(getNextUpdate());
        }
*/
        if (lowestMeasure == Time.SECOND) {
            startSecondsUpdater();
        }
    }

    private void updateSettings(String settingsData) {
/*
        countSeconds = WidgetOptions.parseCountSeconds(settingsData);
        countUpward = WidgetOptions.parseUpward(settingsData);
        targetTimestamp = WidgetOptions.parseTimestamp(settingsData);
*/

        onTick();
    }


    private void startSecondsUpdater() {
        //TODO

    }


    protected class CounterBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                String tz = intent.getStringExtra("time-zone");
                time = new Time(TimeZone.getTimeZone(tz).getID());
            }

            onTick();
        }
    }

}
