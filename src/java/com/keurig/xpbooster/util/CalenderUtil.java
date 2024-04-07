package com.keurig.xpbooster.util;

import java.util.Calendar;

public class CalenderUtil {

    public static long getTime(long beginningTime, String time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(beginningTime);

        int extractNumber = NumUtil.extractNumber(time);
        int calendarUnit = NumUtil.convertToCalendar(time);

        calendar.add(calendarUnit, extractNumber);
        return calendar.getTime().getTime();
    }

    public static long getTime(String time) {
        return getTime(System.currentTimeMillis(), time); // Call the other getTime method with current time
    }

    public static long removeTime(String time) {
        Calendar calendar = Calendar.getInstance();

        int extractNumber = NumUtil.extractNumber(time);
        int calenderN = NumUtil.convertToCalendar(time);

        calendar.roll(calenderN, extractNumber);
        return calendar.getTime().getTime();
    }

}
