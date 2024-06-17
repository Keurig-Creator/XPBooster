package com.keurigsweb.xpbooster.util;

import java.util.Calendar;

public class TimeUtil {

    public static long getTime(long beginningTime, String time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(beginningTime);

        int extractNumber = NumUtil.extractNumber(time);
        int calendarUnit = NumUtil.convertToCalendar(time);

        calendar.add(calendarUnit, extractNumber);
        return calendar.getTimeInMillis(); // Return the end time in milliseconds
    }

    public static long getTime(String time) {
        return getTime(System.currentTimeMillis(), time); // Call the other getTime method with current time
    }
}
