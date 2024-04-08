package com.keurig.xpbooster.event;

import com.keurig.xpbooster.XPBoostPlugin;
import com.keurig.xpbooster.api.XPBoostAPI;
import com.keurig.xpbooster.base.data.EXPBoost;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

@AllArgsConstructor
public class ExperienceChangeListener implements Listener {

    private XPBoostPlugin plugin;

    @EventHandler
    public void onExperienceChange(PlayerExpChangeEvent event) {

        Player player = event.getPlayer();

        EXPBoost boost = XPBoostAPI.getBoost(player.getUniqueId());
        if (boost == null) {
            return;
        }


        if (!boost.isExpired()) {
            event.setAmount(adjustExperience(event.getAmount(), boost.getMultiplier()));
        }
    }

    public int adjustExperience(int originalExperience, double multiplier) {
        // Calculate adjusted experience
        double adjustedExperience = originalExperience * multiplier;


        // Convert adjusted experience to an integer

        return (int) Math.round(adjustedExperience);
    }
}
