package com.ovio.countdown.date;

import android.test.AndroidTestCase;
import android.text.format.Time;
import android.util.Log;
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

    private void generateTimes() {

        long offset = Timestamp.valueOf("2012-01-01 00:00:00").getTime();
        long end = Timestamp.valueOf("2014-01-01 00:00:00").getTime();

        int i = 0;
        while (i < 5000) {

            long diff = end - offset + 1;

            long fromRand = offset + (long)(Math.random() * diff);
            long tillRand = offset + (long)(Math.random() * diff);

            if (fromRand < tillRand) {

                Time startTime = new Time();
                startTime.set((fromRand / 1000) * 1000);

                Time endTime = new Time();
                endTime.set((tillRand / 1000) * 1000);

                if (!(startTime.month == 1 && startTime.monthDay >= 28) && !(endTime.month == 1 && endTime.monthDay >= 28)) {

                    fromList.add((fromRand / 1000) * 1000);
                    tillList.add((tillRand / 1000) * 1000);
                    i++;
                }
            }
        }

    }

    private void generateTimesStraight() {

        long start = Timestamp.valueOf("2012-01-01 00:00:00").getTime();
        long end = Timestamp.valueOf("2012-06-01 00:00:00").getTime();

        Time startTime = new Time();
        startTime.set(start);

        Time endTime = new Time();
        endTime.set(end);

        while (startTime.before(endTime)) {
            Time mediumTime = new Time(startTime);
            while (mediumTime.before(endTime)) {
                mediumTime.monthDay += 1;
                mediumTime.normalize(false);

                //if (!(mediumTime.month == 1 && mediumTime.monthDay > 28)) {
                    fromList.add(startTime.toMillis(false));
                    tillList.add(mediumTime.toMillis(false));
                //}
            }
            startTime.monthDay += 1;
            startTime.normalize(false);
        }
    }

    private void generateTimesSingle() {
        //2013-07-30T16:05:38.000Z Till: 2013-09-30T15:35:58.000Z
        long start = Timestamp.valueOf("2013-07-30 16:00:00").getTime();
        long end = Timestamp.valueOf("2013-09-30 15:00:00").getTime();

        fromList.add(start);
        tillList.add(end);

    }

    public void testNegativeDiff() {

        //generateTimesStraight();

        DateTimeZone tz = DateTimeZone.forTimeZone(TimeZone.getDefault());

        tz = DateTimeZone.forID("Europe/Kiev");

        for (int i = 0; i < fromList.size(); i++) {

            long from = fromList.get(i);
            long till = tillList.get(i);

            DateTime fromDt = new DateTime(from, tz);
            DateTime tillDt = new DateTime(till, tz);

            TimeDifference diff = TimeDifference.between(from, till);

            String datesCompared = "From: " + fromDt.toString() + " Till: " + tillDt.toString() + "\nMy: " + diff.toString();

            assertFalse("Negative Years count" + datesCompared, diff.years < 0);
            assertFalse("Negative Month count" + datesCompared, diff.months < 0);
            assertFalse("Negative Days  count" + datesCompared, diff.days < 0);
            assertFalse("Negative Hours count" + datesCompared, diff.hours < 0);
            assertFalse("Negative Mins  count" + datesCompared, diff.mins < 0);
            assertFalse("Negative Secs  count" + datesCompared, diff.secs < 0);

            System.out.println("Match: " + i);
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

            Log.w("TEST", "From: " + fromDt.toString() + " Till: " + tillDt.toString() + " Period: " + period.toString(fmt));

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
