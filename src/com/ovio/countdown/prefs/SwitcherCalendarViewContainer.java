package com.ovio.countdown.prefs;

import android.content.Context;
import android.view.ViewGroup;
import android.view.ViewParent;
import com.ovio.countdown.calendar.SwitcherCalendarView;

/**
 * Countdown
 * com.ovio.countdown.prefs
 */
public class SwitcherCalendarViewContainer {

    private static SwitcherCalendarViewContainer instance;

    private final Context context;

    private SwitcherCalendarView switcherCalendarView;

    private SwitcherCalendarViewContainer(Context context) {
        this.context = context;

        new Thread(runnable).start();
    }

    public static synchronized SwitcherCalendarViewContainer getInstance(Context context) {
        if (instance == null) {
            instance = new SwitcherCalendarViewContainer(context);
        }
        return instance;
    }

    public SwitcherCalendarView getSwitcherCalendarView() {
        ViewParent parent = switcherCalendarView.getParent();

        if (parent instanceof ViewGroup) {
            ((ViewGroup)parent).removeView(switcherCalendarView);
        }

        return switcherCalendarView;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            switcherCalendarView = new SwitcherCalendarView(context);
        }
    };
}
