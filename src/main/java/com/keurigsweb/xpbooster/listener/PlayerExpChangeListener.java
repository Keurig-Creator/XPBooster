package com.keurigsweb.xpbooster.listener;

import com.keurigsweb.xpbooster.XPBoostPlugin;
import com.keurigsweb.xpbooster.api.XPBoostAPI;
import com.keurigsweb.xpbooster.base.data.EXPBoost;
import com.keurigsweb.xpbooster.base.data.booster.global.HolidayBoost;
import com.keurigsweb.xpbooster.util.Chat;
import com.keurigsweb.xpbooster.util.ConfigValue;
import lombok.AllArgsConstructor;
import org.bukkit.entity.ExperienceOrb;
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

        try {
            if(event.getSource() instanceof ExperienceOrb orb) {
                if (ConfigValue.IGNOREXPBOTTLES && orb.getSpawnReason() == ExperienceOrb.SpawnReason.EXP_BOTTLE) {
                    return;
                }
            }
        } catch (Exception ignored) {}

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

        if (XPBoostPlugin.permisionMultiplier.get(player.getUniqueId()) != null) {
            if (ConfigValue.GLOBAL_STACKING) {
                multiplier += roundMultiplier(XPBoostPlugin.permisionMultiplier.get(player.getUniqueId()));
            } else if (XPBoostPlugin.permisionMultiplier.get(player.getUniqueId()) > multiplier){
                multiplier = roundMultiplier(XPBoostPlugin.permisionMultiplier.get(player.getUniqueId()));
            }
        }

        if (multiplier > 0) {
            event.setAmount(adjustExperience(event.getAmount(), multiplier));
        }
    }
    /**
     * Custom rounding method from config
     */
    public int roundMultiplier(double multiplier) {
        switch (ConfigValue.ROUND) {
            case CEIL:
                return (int) Math.ceil(multiplier);
            case FLOOR:
                return (int) Math.floor(multiplier);
            case ROUND:
                double roundDecimal = ConfigValue.ROUND_DECIMAL;
                double decimalPart = multiplier % 1;

                // Adjust the comparison to check if the decimal part is greater than ROUND_DECIMAL
                if (decimalPart >= roundDecimal || Math.abs(decimalPart - roundDecimal) < 0.000001) {
                    return (int) Math.ceil(multiplier); // Round up if equal or greater than ROUND_DECIMAL
                } else {
                    return (int) Math.floor(multiplier); // Round down otherwise
                }
            default:
                throw new IllegalArgumentException("Unknown rounding method");
        }
    }




    public int adjustExperience(int originalExperience, double multiplier) {
        // Calculate adjusted experience
        double adjustedExperience = originalExperience * multiplier;


        // Convert adjusted experience to an integer

        return (int) Math.round(adjustedExperience);
    }
}
