package com.keurig.xpbooster.util.replacement;

public class ReplacementUtil {
    public static String getReplacement(Replacable replacable, String input) {
        return replacable.getReplace(input);
    }
}