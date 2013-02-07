package com.ovio.countdown.calendar;

import java.util.Map;

/**
* Countdown
* com.ovio.countdown.calendar
*/
public interface DayInfoFetcher {
    Map<Integer, DayInfo> fetchForMonth(int year, int month);
}
