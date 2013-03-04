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

        years = tillTime.year - fromTime.year;
        months = tillTime.month - fromTime.month;
        days = tillTime.monthDay - fromTime.monthDay;
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

        int d = 0;
        if (hours < 0) {
            days--;
            d--;
            hours += 24;
        }

        //TODO
        if (days < 0) {
            months--;


            int m = tillTime.month;
            int y = tillTime.year;

            m--;
            if (m < 0) {
                y--;
                m = 11;
            }

            int fromAdd = getDaysInMonth(y, m) - fromTime.monthDay;
            if (fromAdd < 0) {
                fromAdd += fromTime.monthDay - getDaysInMonth(y, m);
            }

            int tillAdd = tillTime.monthDay;

            days = fromAdd + tillAdd + d;
        }

        int dim = getDaysInMonth(tillTime.year, tillTime.month);
        if (days == dim && tillTime.monthDay == dim && d == 0) {
            days = 0;
            months++;
        }

        if (months < 0) {
            years--;
            months += 12;
        }

    }

    /*
Months doesn't match in compare From: 2013-07-30T16:05:38.000Z Till: 2013-09-30T15:35:58.000Z Period: 1month 30days  Time 23:30:20
My: TimeDifference{years=0, months=2, days=0, hours=23, mins=30, secs=20} expected:<1> but was:<2>
     */

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
