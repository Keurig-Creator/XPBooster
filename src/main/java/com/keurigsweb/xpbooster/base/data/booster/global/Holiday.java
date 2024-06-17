package com.keurigsweb.xpbooster.base.data.booster.global;

import com.keurigsweb.xpbooster.util.Chat;
import com.keurigsweb.xpbooster.util.ConfigValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@AllArgsConstructor
@Builder
@Getter
public class Holiday {

    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private double multiplier;
    private String notifyHoliday;

    public boolean isHoliday() {
        LocalDate now = LocalDate.now(ZoneId.of(ConfigValue.GLOBAL_TIMEZONE));
        // Check if today's date is within the holiday range
        if (now.isEqual(startDate) || now.isEqual(endDate)) {
            return true; // Today is the start or end date of the holiday
        } else {
            return now.isAfter(startDate) && now.isBefore(endDate);
        }
    }
}
