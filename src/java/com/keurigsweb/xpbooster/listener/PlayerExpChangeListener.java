package com.keurigsweb.xpbooster.listener;

import com.keurigsweb.xpbooster.XPBoostPlugin;
import com.keurigsweb.xpbooster.api.XPBoostAPI;
import com.keurigsweb.xpbooster.base.data.EXPBoost;
import com.keurigsweb.xpbooster.base.data.booster.global.HolidayBoost;
import com.keurigsweb.xpbooster.util.Chat;
import com.keurigsweb.xpbooster.util.ConfigValue;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

@AllArgsConstructor
public class PlayerExpChangeListener implements Listener {

    private XPBoostPlugin plugin;

    @EventHandler
    public void onExperienceChange(PlayerExpChangeEvent event) {

        Player player = event.getPlayer();

        int multiplier = 0;

        EXPBoost boost = XPBoostAPI.getBoost(player.getUniqueId());
        if (boost != null) {
            if (!boost.isExpired()) {
                multiplier = roundMultiplier(boost.getMultiplier());
            }
        }

        if (ConfigValue.GLOBAL_STACKING) {
            multiplier += roundMultiplier(XPBoostAPI.getGlobalMultiplier());
        } else if (XPBoostAPI.getGlobalMultiplier() > multiplier) {
            multiplier = roundMultiplier(XPBoostAPI.getGlobalMultiplier());
        }

        if (ConfigValue.GLOBAL_STACKING) {
            multiplier += roundMultiplier(HolidayBoost.GLOBAL_MULTIPLIER);
        } else if (HolidayBoost.GLOBAL_MULTIPLIER > multiplier) {
            multiplier = roundMultiplier(HolidayBoost.GLOBAL_MULTIPLIER);
        }

        if (ConfigValue.GLOBAL_STACKING) {
            multiplier += roundMultiplier(XPBoostPlugin.permisionMultiplier.get(player.getUniqueId()));
        } else if (XPBoostPlugin.permisionMultiplier.get(player.getUniqueId()) > multiplier){
            multiplier = roundMultiplier(XPBoostPlugin.permisionMultiplier.get(player.getUniqueId()));
        }

        if (multiplier > 0) {
            event.setAmount(adjustExperience(event.getAmount(), multiplier));
        }
    }

    public int roundMultiplier(double multiplier) {
        return (int) Math.round(multiplier);
    }

    public int adjustExperience(int originalExperience, double multiplier) {
        // Calculate adjusted experience
        double adjustedExperience = originalExperience * multiplier;


        // Convert adjusted experience to an integer

        return (int) Math.round(adjustedExperience);
    }
}
