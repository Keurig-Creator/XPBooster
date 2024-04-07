package com.keurig.xpbooster.language;

import com.keurig.xpbooster.XPBoostPlugin;
import com.keurig.xpbooster.util.Chat;
import com.keurig.xpbooster.util.replacement.Replacement;
import lombok.Getter;

public enum Language {

    BOOST_END_MESSAGE("&7Your XP Boost has &eexpired&7."),
    SET_BOOST_MESSAGE("&7Set XP boost to &e$target&7 with a multiplier of &c$multiplier&7 for &e$duration&7."),
    GIVE_VOUCHER_MESSAGE("&7Gave XP boost voucher &e$booster &7to &e$target&7"),
    VOUCHER_RECEIVE_MESSAGE("&7You were given the &e$name &7XP boost voucher!"),
    BOOSTER_ACTIVATE_MESSAGE("&7Your XP multiplier is now &e$multiplier &7Booster time is now &e$time"),
    ADD_BOOST_MESSAGE("&7Added &e$duration&7 to &e$target&7's &7XP boost."),
    REMOVE_BOOST_MESSAGE("&7Removed XP boost from &e$target&7."),
    NO_ACTIVE_BOOSTER("&cYou have no active boosters"),
    TARGET_NO_ACTIVE_BOOSTER("&cTarget has no active boosters"),
    INVALID_DATE_FORMAT("&cInvalid time/date format. Use formats like 1d, 1day, or 10s."),
    INVALID_VOUCHER("&cInvalid voucher. &7[&f$vouchers&7]"),
    NO_MULTIPLIER("&cUsage: $command <player> [multiplier] [time]"),
    INVALID_NUMBER("&cPlease provide a valid number."),
    MINIMUM_MULTIPLIER("&cError minimum multiplier &f$minMultiplier&c."),
    MAXIMUM_MULTIPLIER("&cError maximum multiplier &f$maxMultiplier&c."),
    FULL_RANGE_MULTIPLIER("&cFull range multiplier is disabled. You can enable it in the configuration."),
    RELOAD_COMMAND("&eConfiguration has been successfully reloaded."),
    ;


    @Getter
    final
    public String value;

    private final XPBoostPlugin plugin = XPBoostPlugin.getInstance();

    Language(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        Replacement replacement = new Replacement();
        replacement.addReplacement("(?i)\\$(min|minimum)(Multiplier)", String.valueOf(XPBoostPlugin.getInstance().config.getInt("minimum-multiplier")));
        replacement.addReplacement("(?i)\\$(max|maximum)(Multiplier)", String.valueOf(XPBoostPlugin.getInstance().config.getInt("maximum-multiplier")));
        replacement.setInput(Chat.color(plugin.lang.getString(name().toLowerCase())));
        return replacement.getReplacement();
    }

    public String toString(Replacement replacement) {
        replacement.setInput(toString());
        return replacement.getReplacement();
    }
}
