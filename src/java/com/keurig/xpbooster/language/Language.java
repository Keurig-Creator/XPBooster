package com.keurig.xpbooster.language;

import com.keurig.xpbooster.XPBooster;
import com.keurig.xpbooster.util.Chat;
import com.keurig.xpbooster.util.Replacement;
import lombok.Getter;

public enum Language {

    BOOST_END_MESSAGE("&7Your XP Boost has &eexpired&7."),
    SET_XPBOOST_MESSAGE("&7Set XP boost to &e$target&7 with a multiplier of &c$multiplier&7 for &e$duration&7."),
    REMOVE_XPBOOST_MESSAGE("&7Removed XP boost from &e$target&7."),
    PLAYER_NO_BOOST("&cPlayer does not have any boosts."),
    INVALID_DATE("&cInvalid time/date format. Use formats like 1d, 1day, or 10s."),
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

    private final XPBooster plugin = XPBooster.getInstance();

    Language(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        Replacement replacement = new Replacement();
        replacement.addReplacement("(?i)\\$(min|minimum)(Multiplier)", String.valueOf(XPBooster.getInstance().config.getInt("minimum_multiplier")));
        replacement.addReplacement("(?i)\\$(max|maximum)(Multiplier)", String.valueOf(XPBooster.getInstance().config.getInt("maximum_multiplier")));
        replacement.setInput(Chat.color(plugin.lang.getString(name().toLowerCase())));
        return replacement.getReplacement();
    }

    public String toString(Replacement replacement) {
        replacement.setInput(toString());
        return replacement.getReplacement();
    }
}
