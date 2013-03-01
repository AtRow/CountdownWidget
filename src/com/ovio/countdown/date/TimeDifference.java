package com.ovio.countdown.date;

import android.text.format.DateUtils;
import android.text.format.Time;

/**
 * Countdown
 * com.ovio.countdown.date
 */
public final class TimeDifference {

    public boolean positive = true;

    public int years;

    public int months;

    public int days;

    public int hours;

    public int mins;

    public int secs;

    private static final int SEC = 1000;

    private static final int[] DAYS_PER_MONTH = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

    private TimeDifference() {

    }

    public static TimeDifference between(long from, long till) {

        from = (from / SEC) * SEC;
        till = (till / SEC) * SEC;

        TimeDifference diff = new TimeDifference();

        if (from == till) {
            return diff;

        } else if (from < till) {
            diff.calculate(from, till);

        } else {
            diff.calculate(till, from);
            diff.positive = false;
        }

        return diff;
    }

    @Override
    public String toString() {
        return "TimeDifference{" +
                "years=" + years +
                ", months=" + months +
                ", days=" + days +
                ", hours=" + hours +
                ", mins=" + mins +
                ", secs=" + secs +
                '}';
    }

    private void calculate(long from, long till) {

        Time fromTime = new Time();
        fromTime.set(from);

        Time tillTime = new Time();
        tillTime.set(till);

        hours = tillTime.hour - fromTime.hour;
        mins = tillTime.minute - fromTime.minute;
        secs = tillTime.second - fromTime.second;

        if (secs < 0) {
            mins--;
            secs += 60;
        }

        if (mins < 0) {
            hours--;
            mins += 60;
        }

        if (hours < 0) {
            //days--;
            hours += 24;
        }

        days = getDaysBetween(from, till);

        //int daysTillMonthEnd = getDaysInMonth(fromTime.year, fromTime.month) - fromTime.monthDay;

        int m = fromTime.month;
        int y = fromTime.year;

        months = 0;
        while (days - getDaysInMonth(y, m) >= 0) {
            days -= getDaysInMonth(y, m);
            m++;
            if (m == 12) {
                m = 0;
                y++;
            }
            months++;
        }

        years = months / 12;
        months -= years * 12;
    }

    // Copy-Pasted from android.text.format.Time to avoid new Time instances creation
    private boolean isLeapYear(int y) {
        return (y % 4) == 0 && ((y % 100) != 0 || (y % 400) == 0);
    }

    // Copy-Pasted from android.text.format.Time to avoid new Time instances creation
    private int getDaysInMonth(int year, int month) {

        int n = DAYS_PER_MONTH[month];

        if (n != 28) {
            return n;
        } else {
            return isLeapYear(year) ? 29 : 28;
        }
    }

    // Copy-Pasted from android.text.format.Time
    private int getDaysInYear(int y) {
        return ((y % 4) == 0 && ((y % 100) != 0 || (y % 400) == 0)) ? 366 : 365;
    }

    private int getDaysBetween(long from, long till) {
        return (int) ((till - from) / DateUtils.DAY_IN_MILLIS);
//
//        Calendar cursor = Calendar.getInstance();
//        cursor.setTimeInMillis(from);
//
//        cursor.add(Calendar.DAY_OF_YEAR, presumedDays);
//        long instant = cursor.getTimeInMillis();
//        if (instant == till) {
//            return presumedDays;
//        }
//        final int step = (instant < till) ? 1 : -1;
//        do {
//            cursor.add(Calendar.DAY_OF_MONTH, step);
//            presumedDays += step;
//        } while (cursor.getTimeInMillis() != till);
//
//        return presumedDays;
    }

    private int getDaysBetween2(long from, long till) {

        Time fromTime = new Time();
        fromTime.set(from);
        fromTime.normalize(false);

        Time tillTime = new Time();
        tillTime.set(till);
        tillTime.normalize(false);

        int daysBetween = 0;
        while (fromTime.before(tillTime)) {
            fromTime.monthDay++;
            fromTime.normalize(false);
            daysBetween++;
        }
        return daysBetween;
    }
}
