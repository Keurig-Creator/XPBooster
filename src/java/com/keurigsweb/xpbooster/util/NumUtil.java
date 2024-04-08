package com.keurigsweb.xpbooster.util;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumUtil {

    public static String DATE_FORMAT_REGEX = "^(\\d+(?:\\.\\d{1,2})?)(day|d|year|y|minute|min|m|second|s)$";

    public static boolean isNumber(String string) {
        return isNumber(string, true);
    }

    public static boolean isNumber(String string, boolean negative) {
        // Regular expression to match numbers (including negative numbers and decimal numbers)
        String numberRegex = "-?\\d+(\\.\\d+)?";
        if (!negative) {
            numberRegex = numberRegex.substring(2);
        }

        // Check if the string matches the number pattern
        return Pattern.matches(numberRegex, string);
    }

    // Function to check if a double represents either a whole number or half
    public static boolean isWholeOrHalf(double number) {
        // Check if the fractional part is either 0 or 0.5
        double fractionalPart = number - Math.floor(number);

        return fractionalPart == 0 || fractionalPart == 0.5;
    }

    public static int convertToCalendar(String input) {
        // Define regular expression pattern to match the input formats
        Pattern r = Pattern.compile(DATE_FORMAT_REGEX);

        Matcher m = r.matcher(input);

        if (m.find()) {
            String unit = m.group(2);
            unit = unit.substring(0, 1);

            // Based on the unit (d/day, m/month, y/year), add the specified amount to the calendar
            switch (unit) {
                case "d":
                    return Calendar.DATE;
                case "m":
                    return Calendar.MINUTE;
                case "y":
                    return Calendar.YEAR;
                case "s":
                    return Calendar.SECOND;
            }
        }

        return -1; // Indicate failure if input doesn't match the pattern
    }

    public static String formatMultiplier(double multiplier) {
        if (multiplier == (int) multiplier) {
            return String.format("%.0fx", multiplier);
        } else {
            return multiplier + "x";
        }
    }

    public static String timeFormat(long millis, boolean round) {
        long currentTimeMillis = System.currentTimeMillis();
        long durationInMillis = millis - currentTimeMillis;

        long second = (durationInMillis / 1000) % 60;
        long minute = (durationInMillis / (1000 * 60)) % 60;
        long hour = (durationInMillis / (1000 * 60 * 60)) % 24;
        long day = durationInMillis / (1000 * 60 * 60 * 24);
        long year = day / 365;

        // Adjust minute if seconds are greater than or equal to 30
        if (round && second >= 59) {
            minute++;
            second = 0;
            if (minute == 60) { // Adjust hour if minute overflows
                hour++;
                minute = 0;
            }
        }

        StringBuilder formattedTime = new StringBuilder();
        if (year > 0) {
            formattedTime.append(year).append("y ");
        }
        if (day > 0) {
            formattedTime.append(day).append("d ");
        }
        if (hour > 0) {
            formattedTime.append(hour).append("h ");
        }
        if (minute > 0) {
            formattedTime.append(minute).append("m ");
        }
        if (second > 0 || formattedTime.length() == 0) {
            formattedTime.append(second).append("s");
        }

        return formattedTime.toString().trim();
    }

    public static String convertDateToStr(String input) {
        // Define regular expression pattern to match the input formats
        Pattern r = Pattern.compile(DATE_FORMAT_REGEX);

        Matcher m = r.matcher(input);

        if (m.find()) {
            String unit = m.group(2);
            double num = Double.parseDouble(m.group(1));
            unit = unit.substring(0, 1);

            String pluralize = pluralize(num);
            // Based on the unit (d/day, m/month, y/year), add the specified amount to the calendar
            switch (unit) {
                case "d":
                    return num + " day" + pluralize;
                case "m":
                    return num + " minute" + pluralize;
                case "y":
                    return num + " year" + pluralize;
                case "s":
                    return num + " second" + pluralize;
            }
        }

        return null; // Indicate failure if input doesn't match the pattern
    }

    public static int extractNumber(String input) {
        String pattern = "(\\d+)";
        Pattern r = Pattern.compile(pattern);

        Matcher matcher = r.matcher(input);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return -1;
    }

    public static String pluralize(double count) {
        return count == 0 || count > 1 ? "s" : "";
    }
}
