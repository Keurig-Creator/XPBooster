package com.keurigsweb.xpbooster.base.data.booster.global;

import com.keurigsweb.xpbooster.language.Language;
import com.keurigsweb.xpbooster.util.Chat;
import com.keurigsweb.xpbooster.util.ConfigValue;
import com.keurigsweb.xpbooster.util.NumUtil;
import com.keurigsweb.xpbooster.util.replacement.Replacement;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;

public class HolidayBoost {

    public static double GLOBAL_MULTIPLIER;
    public static String GLOBAL_NOTIFY;

    public HolidayBoost() {
        load();
    }

    public void load() {
        GLOBAL_NOTIFY = null;
        GLOBAL_MULTIPLIER = 0;

        if (ConfigValue.GLOBAL_ENABLED) {
            loadHolidays();
            loadDaysOfWeek();
        }


        Chat.log(GLOBAL_MULTIPLIER);
    }

    private void loadDaysOfWeek() {
        Chat.log("loading days");

        for (Map.Entry<DayOfWeek, Double> entry : ConfigValue.GLOBAL_DAYS_OF_WEEK.entrySet()) {
            DayOfWeek day = entry.getKey();
            Double dayMultiplier = entry.getValue();
            DayOfWeek currentDay = LocalDate.now(ZoneId.of(ConfigValue.GLOBAL_TIMEZONE)).getDayOfWeek();

            Replacement replacement = new Replacement();
            replacement.addReplacement(Replacement.MULTIPLIER_REGEX, NumUtil.formatMultiplier(dayMultiplier));

            if (dayMultiplier <= 1 || currentDay != day)
                continue;

            if (!ConfigValue.GLOBAL_STACKING && dayMultiplier > GLOBAL_MULTIPLIER) {
                GLOBAL_MULTIPLIER = dayMultiplier;
            } else {
                GLOBAL_MULTIPLIER += dayMultiplier;
            }

            if (GLOBAL_NOTIFY != null)
                continue;

            try {
                GLOBAL_NOTIFY = Language.valueOf("DAY_OF_" + currentDay).toString(replacement);
            } catch (IllegalArgumentException e) {
                Chat.error(e.getMessage());
            }
        }
    }

    private void loadHolidays() {
        double multiplier = 0;
        for (Holiday holiday : ConfigValue.GLOBAL_HOLIDAYS) {
            if (holiday.isHoliday()) {
                if (ConfigValue.GLOBAL_STACKING) {
                    multiplier += holiday.getMultiplier();
                } else {
                    if (holiday.getMultiplier() > multiplier) {
                        multiplier = holiday.getMultiplier();
                    }
                }

                GLOBAL_NOTIFY = holiday.getNotifyHoliday();
                GLOBAL_MULTIPLIER = multiplier;
            }
        }
    }

}
