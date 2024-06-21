package com.keurigsweb.xpbooster.util;

import com.keurigsweb.xpbooster.XPBoostPlugin;
import com.keurigsweb.xpbooster.base.data.booster.global.Holiday;
import com.keurigsweb.xpbooster.util.replacement.Replacement;
import org.bukkit.configuration.ConfigurationSection;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigValue {

    public static boolean GLOBAL_ENABLED;
    public static double GLOBAL_DEFAULT_MULTIPLIER;
    public static int GLOBAL_MAX_DURATION;
    public static int GLOBAL_COOLDOWN;
    public static boolean GLOBAL_STACKING;
    public static String GLOBAL_TIMEZONE;
    public static List<Holiday> GLOBAL_HOLIDAYS = new ArrayList<>();
    public static boolean IGNOREXPBOTTLES;
    public static Map<DayOfWeek, Double> GLOBAL_DAYS_OF_WEEK = new HashMap<>();

    static {
        loadValues();
    }

    public static void loadValues() {
        XPBoostPlugin instance = XPBoostPlugin.getInstance();
        GLOBAL_ENABLED = instance.config.getBoolean("global-boost.enabled");
        GLOBAL_DEFAULT_MULTIPLIER = instance.config.getDouble("global-boost.default-multiplier");
        GLOBAL_MAX_DURATION = instance.config.getInt("global-boost.max-duration-hours");
        GLOBAL_COOLDOWN = instance.config.getInt("global-boost.cooldown-hours");
        GLOBAL_STACKING = instance.config.getBoolean("global-boost.allow-stacking");
        GLOBAL_TIMEZONE = instance.config.getString("global-boost.schedule.timezone");
        IGNOREXPBOTTLES = instance.config.getBoolean("ignore-xp-bottles");
        ConfigurationSection holidays = instance.config.getConfigurationSection("global-boost.schedule.holidays");
        if (holidays != null) {
            GLOBAL_HOLIDAYS.clear();
            for (String key : holidays.getKeys(false)) {
                ConfigurationSection holiday = holidays.getConfigurationSection(key);

                if (holiday == null)
                    continue;

                Replacement replace = new Replacement();
                replace.addReplacement(Replacement.MULTIPLIER_REGEX, NumUtil.formatMultiplier(holiday.getDouble("multiplier")));


                GLOBAL_HOLIDAYS.add(Holiday.builder()
                        .name(key)
                        .startDate(getDate(holiday.getString("start-date")))
                        .endDate((getDate(holiday.getString("end-date"))))
                        .multiplier(holiday.getDouble("multiplier"))
                        .notifyHoliday(replace.getReplacement(holiday.getString("notify", null)))
                        .build());
            }
        }
        ConfigurationSection schedule = instance.config.getConfigurationSection("global-boost.schedule.days-of-week");

        if (schedule != null) {
            for (String key : schedule.getKeys(false)) {
                GLOBAL_DAYS_OF_WEEK.put(DayOfWeek.valueOf(key.toUpperCase()), schedule.getDouble(key));
            }
        }
    }

    private static LocalDate getDate(String dateString) {
        // Parse and complete the date string

        return parseAndCompleteDate(dateString);
    }

    // Method to parse and complete date string
    private static LocalDate parseAndCompleteDate(String dateString) {
        // Split the date string by '-'
        String[] parts = dateString.split("-");

        // Extract month and day from the parts
        int month = Integer.parseInt(parts[0]);
        int day = Integer.parseInt(parts[1]);

        // Create a LocalDate with current year and provided month and day
        return LocalDate.of(Year.now(ZoneId.of(GLOBAL_TIMEZONE)).getValue(), month, day);
    }
}
