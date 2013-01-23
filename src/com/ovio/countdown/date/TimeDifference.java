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


        years = tillTime.year - fromTime.year;
        months = tillTime.month - fromTime.month;
        days = tillTime.monthDay - fromTime.monthDay;
        hours = tillTime.hour - fromTime.hour;
        mins = tillTime.minute - fromTime.minute;
        secs = tillTime.second - fromTime.second;

        // Leap year workaround
        if (tillTime.year != fromTime.year) {
            for (int y = fromTime.year; y <= tillTime.year; y++) {
                if (isLeapYear(y)) {
                    days++;
                }
            }
        }

        if (secs < 0) {
            mins--;
            secs += 60;
        }

        if (mins < 0) {
            hours--;
            mins += 60;
        }

        if (hours < 0) {
            days--;
            hours += 24;
        }

        if (days < 0) {
            months--;
            int mon = (tillTime.month == 0) ? 11 : tillTime.month - 1;
            days += getDaysInMonth(tillTime.year, mon);
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





























