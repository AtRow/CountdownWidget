package com.ovio.countdown.date;

import android.test.AndroidTestCase;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * Countdown
 * com.ovio.countdown.date
 */
public class DiffTest extends AndroidTestCase{

    private List<Long> fromList = new ArrayList<Long>();
    private List<Long> tillList = new ArrayList<Long>();

    public void generateTimes() {

        long offset = Timestamp.valueOf("2012-01-01 00:00:00").getTime();
        long end = Timestamp.valueOf("2014-01-01 00:00:00").getTime();

        int i = 0;
        while (i < 1000) {

            long diff = end - offset + 1;

            long fromRand = offset + (long)(Math.random() * diff);
            long tillRand = offset + (long)(Math.random() * diff);

            if (fromRand < tillRand) {
                fromList.add((fromRand / 1000) * 1000);
                tillList.add((tillRand / 1000) * 1000);
                i++;
            }
        }

    }

    public void testDiff() {

        generateTimes();

        DateTimeZone tz = DateTimeZone.forTimeZone(TimeZone.getDefault());

        for (int i = 0; i < fromList.size(); i++) {

            long from = fromList.get(i);
            long till = tillList.get(i);

            DateTime fromDt = new DateTime(from, tz);
            DateTime tillDt = new DateTime(till, tz);

            TimeDifference diff = TimeDifference.between(from, till);

            Period period = new Period(fromDt, tillDt);

            period = period.normalizedStandard(PeriodType.yearMonthDayTime());

            String datesCompared = "From: " + fromDt.toString() + " Till: " + tillDt.toString() + " Period: " + period.toString(fmt) + "\n My: " + diff.toString();


            assertEquals("Years doesn't match in compare " + datesCompared, period.getYears(), diff.years);

            assertEquals("Months doesn't match in compare " + datesCompared, period.getMonths(), diff.months);

            assertEquals("Days doesn't match in compare " + datesCompared, period.getDays(), diff.days);

            assertEquals("Hours doesn't match in compare " + datesCompared, period.getHours(), diff.hours);

            assertEquals("Mins doesn't match in compare " + datesCompared, period.getMinutes(), diff.mins);

            assertEquals("Secs doesn't match in compare " + datesCompared, period.getSeconds(), diff.secs);

            System.out.println("Match: " + i);
        }

    }

    private PeriodFormatter fmt = new PeriodFormatterBuilder()
            .appendYears()
            .appendSuffix("year ", "years ")
            .appendMonths()
            .appendSuffix("month ", "months ")
            .appendDays()
            .appendSuffix("day ", "days ")
            .appendSeparator(" Time ")
            .appendHours()
            .appendSeparator(":")
            .appendMinutes()
            .appendSeparator(":")
            .appendSeconds()
            .toFormatter();

}
