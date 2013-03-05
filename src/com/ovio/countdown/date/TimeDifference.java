package com.ovio.countdown.date;

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

        int shift = (int) (fromTime.gmtoff - tillTime.gmtoff) / (60 * 60);

        years = tillTime.year - fromTime.year;
        months = tillTime.month - fromTime.month;
        days = tillTime.monthDay - fromTime.monthDay;
        hours = tillTime.hour - fromTime.hour + shift;
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
        while (hours < 0) {
            days--;
            d--;
            hours += 24;
        }
        while (hours >= 24) {
            d++;
            days++;
            hours -=24;
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
}
