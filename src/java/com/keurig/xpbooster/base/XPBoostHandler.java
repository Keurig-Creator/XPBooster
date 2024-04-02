package com.keurig.xpbooster.base;

import com.keurig.xpbooster.util.JsonConfig;
import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class XPBoostHandler {

    private JsonConfig dataConfig;


    public void addBoost(UUID uuid, EXPBoost expBoost) {
        dataConfig.getExpBoosts().put(uuid, expBoost);
        dataConfig.save();
    }

    public void removeBoost(UUID uuid) {
        dataConfig.getExpBoosts().remove(uuid);
        dataConfig.save();
    }

    public boolean hasBoost(UUID uuid) {
        return getBoost(uuid) != null;
    }

    public EXPBoost getBoost(UUID uuid) {
        EXPBoost expBoost = dataConfig.getExpBoosts().get(uuid);

        if (expBoost == null) {
            return null;
        }

        if (expBoost.isExpired()) {
            removeBoost(uuid);
        }

        return expBoost;
    }

}
