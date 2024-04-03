package com.keurig.xpbooster.api;

import com.keurig.xpbooster.base.EXPBoost;

import java.util.UUID;

public interface XPBoostInterface {

    void addBoost(UUID uuid, EXPBoost expBoost);

    void removeBoost(UUID uuid);

    boolean hasBoost(UUID uuid);

    EXPBoost getBoost(UUID uuid);

}
