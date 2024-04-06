package com.keurig.xpbooster.util.replacement;

public interface Replacable {
    String getReplace(String input);

    Replacement getReplacement(String input);
}