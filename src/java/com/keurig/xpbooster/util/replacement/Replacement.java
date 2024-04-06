package com.keurig.xpbooster.util.replacement;

import com.keurig.xpbooster.XPBoostPlugin;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
public class Replacement {

    private final Map<String, String> replacements = new HashMap<>();
    private String input;

    public static String COMMAND_REGEX = "(?i)\\$command";
    public static String PLAYER_REGEX = "(?i)\\$player";
    public static String TARGET_REGEX = "(?i)\\$target";
    public static String DURATION_REGEX = "(?i)\\$duration";
    public static String MULTIPLIER_REGEX = "(?i)\\$multiplier";
    public static String VOUCHER_REGEX = "(?i)\\$multiplier";
    public static String NAME_REGEX = "(?i)\\$name";
    public static String PRICE_REGEX = "(?i)\\$price";
    public static String TIME_REGEX = "(?i)\\$time";
    public static String BOOSTER_REGEX = "(?i)\\$booster";

    public String getReplacement(String input) {
        input = input.replaceAll("(?i)\\$(min|minimum)(Multiplier)", String.valueOf(XPBoostPlugin.getInstance().config.getInt("minimum-multiplier") + .5));
        input = input.replaceAll("(?i)\\$(max|maximum)(Multiplier)", String.valueOf(XPBoostPlugin.getInstance().config.getInt("maximum-multiplier")));

        for (Map.Entry<String, String> replace : replacements.entrySet()) {
            input = input.replaceAll(replace.getKey(), replace.getValue());
        }

        return input;
    }

    public String getReplacement() {
        replaceMessage("(?i)\\$(min|minimum)(Multiplier)", String.valueOf(XPBoostPlugin.getInstance().config.getInt("minimum-multiplier") + .5));
        replaceMessage("(?i)\\$(max|maximum)(Multiplier)", String.valueOf(XPBoostPlugin.getInstance().config.getInt("maximum-multiplier")));

        for (Map.Entry<String, String> replace : replacements.entrySet()) {
            replaceMessage(replace.getKey(), replace.getValue());
        }

        return input;
    }
    
    @Builder(builderMethodName = "builder", buildMethodName = "buildInternal")
    public static Replacement createReplacement(String command, String player, String target) {
        Replacement replacement = new Replacement();
        if (command != null) replacement.addReplacement(COMMAND_REGEX, command);
        if (player != null) replacement.addReplacement(PLAYER_REGEX, player);
        if (target != null) replacement.addReplacement(TARGET_REGEX, target);
        return replacement;
    }

    @Builder(builderMethodName = "builder", buildMethodName = "buildInternal")
    public static Replacement createReplacement(String command, String player) {
        Replacement replacement = new Replacement();
        if (command != null) replacement.addReplacement(COMMAND_REGEX, command);
        if (player != null) replacement.addReplacement(PLAYER_REGEX, player);
        return replacement;
    }

    @Builder(builderMethodName = "builder", buildMethodName = "buildInternal")
    public static Replacement createReplacement(String command) {
        Replacement replacement = new Replacement();
        if (command != null) replacement.addReplacement(COMMAND_REGEX, command);
        return replacement;
    }


    private void replaceMessage(String regex, String replace) {
        if (replace != null & replace != null) {
            input = input.replaceAll(regex, replace);
        }
    }

    public Replacement addReplacement(String regex, String replace) {
        replacements.put(regex, replace);

        return this;
    }
}
