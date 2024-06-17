package com.keurigsweb.xpbooster.base.handler;

import com.keurigsweb.xpbooster.api.XPBoostInterface;
import com.keurigsweb.xpbooster.base.data.EXPBoost;
import com.keurigsweb.xpbooster.base.data.booster.global.GlobalBoost;
import com.keurigsweb.xpbooster.util.Chat;
import com.keurigsweb.xpbooster.util.JsonConfig;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class InternalXPBoostHandler implements XPBoostInterface {

    private JsonConfig dataConfig;

    @Override
    public void addBoost(UUID uuid, EXPBoost expBoost) {
        dataConfig.getExpBoosts().put(String.valueOf(uuid), expBoost);
        dataConfig.save();
    }

    @Override
    public void removeBoost(UUID uuid) {
        dataConfig.getExpBoosts().remove(uuid.toString());
        dataConfig.save();
    }

    public void addGlobalBoost(GlobalBoost globalBoost) {
        if (getGlobalBoost(globalBoost.getMultiplier()) != null) {
            globalBoost.addTime(getGlobalBoost(globalBoost.getMultiplier()).getTime());
        }
        dataConfig.getGlobalBoosts().put(globalBoost.getMultiplier(), globalBoost);
        dataConfig.save();
    }

    public void removeGlobalBoost(Double multiplier) {
        dataConfig.getGlobalBoosts().remove(multiplier);
        dataConfig.save();
    }

    public GlobalBoost getGlobalBoost(Double multiplier) {
        return dataConfig.getGlobalBoosts().get(multiplier);
    }

    public List<GlobalBoost> getGlobalBoosts() {
        return dataConfig.getGlobalBoosts().values().stream().toList();
    }

    @Override
    public boolean hasBoost(UUID uuid) {
        return getBoost(uuid) != null;
    }

    @Override
    public EXPBoost getBoost(UUID uuid) {
        EXPBoost expBoost = dataConfig.getExpBoosts().get(uuid.toString());

        if (expBoost == null) {
            return null;
        }

        if (expBoost.isExpired()) {
            removeBoost(uuid);
        }

        return expBoost;
    }

}
