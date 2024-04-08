package com.keurigsweb.xpbooster.api;

import com.keurigsweb.xpbooster.XPBoostPlugin;
import com.keurigsweb.xpbooster.base.data.EXPBoost;
import com.keurigsweb.xpbooster.base.data.booster.Booster;
import com.keurigsweb.xpbooster.base.handler.InternalXPBoostHandler;
import com.keurigsweb.xpbooster.util.CalenderUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class XPBoostAPI {

    // Singleton instance of XPBoostHandler
    private static volatile InternalXPBoostHandler boostHandler;

    /**
     * Get a users boost
     */
    public static EXPBoost getBoost(UUID uuid) {
        return getBoostHandler().getBoost(uuid);
    }

    public static String getRemainingTime(Player player) {
        if (hasBoost(player.getUniqueId())) {
            return getBoost(player.getUniqueId()).getRemainingTime();
        }

        return null;
    }

    public static double getMultiplier(Player player) {
        if (hasBoost(player.getUniqueId())) {
            return getBoost(player.getUniqueId()).getMultiplier();
        }

        return -1;
    }

    /**
     * Add boost to user
     */
    public static void setBoost(UUID uuid, EXPBoost expBoost) {
        getBoostHandler().addBoost(uuid, expBoost);
    }

    /**
     * Remove boost from user
     */
    public static void removeBoost(UUID uuid) {
        getBoostHandler().removeBoost(uuid);
    }

    public static void setBoost(Player player, Booster booster) {
        setBoost(player.getUniqueId(), new EXPBoost(player.getUniqueId(), booster.getMultiplier(), CalenderUtil.getTime(booster.getTime()), booster.getName()));
    }

    public static EXPBoost addBoost(Player player, Booster booster) {
        UUID playerId = player.getUniqueId();
        if (hasBoost(playerId)) {
            EXPBoost existingBoost = getBoost(playerId);
            if (existingBoost.getMultiplier() == booster.getMultiplier()) {
                long newExpirationTime = CalenderUtil.getTime(existingBoost.getDate(), booster.getTime());
                existingBoost.setDate(newExpirationTime);
                setBoost(playerId, existingBoost);
                return existingBoost;
            }
        }

        EXPBoost expBoost = new EXPBoost(playerId, booster.getMultiplier(), CalenderUtil.getTime(booster.getTime()), booster.getName());
        setBoost(playerId, expBoost);
        return expBoost;
    }

    /**
     * Check if a player has a boost
     */
    public static boolean hasBoost(UUID uuid) {
        return getBoostHandler().hasBoost(uuid);
    }

    // Method to get the instance of XPBoostHandler
    public static InternalXPBoostHandler getBoostHandler() {
        // Double-checked locking for thread safety during initialization
        if (boostHandler == null) {
            synchronized (XPBoostAPI.class) {
                if (boostHandler == null) {
                    // Initialize boostHandler if it's null
                    boostHandler = XPBoostPlugin.getInstance().getBoostHandler();
                    if (boostHandler == null) {
                        // Throw exception if initialization fails
                        throw new IllegalStateException("XPBoostHandler is null");
                        // Or handle null case in some other appropriate way
                    }
                }
            }
        }
        // Return the initialized boostHandler
        return boostHandler;
    }

}
