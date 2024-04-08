package com.keurigsweb.xpbooster.base.handler;

import com.keurigsweb.xpbooster.api.XPBoostInterface;
import com.keurigsweb.xpbooster.base.data.EXPBoost;
import com.keurigsweb.xpbooster.util.JsonConfig;
import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class InternalXPBoostHandler implements XPBoostInterface {

    private JsonConfig dataConfig;

    @Override
    public void addBoost(UUID uuid, EXPBoost expBoost) {
        dataConfig.getExpBoosts().put(uuid, expBoost);
        dataConfig.save();
    }

    @Override
    public void removeBoost(UUID uuid) {
        dataConfig.getExpBoosts().remove(uuid);
        dataConfig.save();
    }

    @Override
    public boolean hasBoost(UUID uuid) {
        return getBoost(uuid) != null;
    }

    @Override
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
