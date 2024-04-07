package com.keurig.xpbooster.api;

import com.keurig.xpbooster.XPBoostPlugin;
import com.keurig.xpbooster.base.Booster;
import com.keurig.xpbooster.base.EXPBoost;
import com.keurig.xpbooster.base.InternalXPBoostHandler;
import com.keurig.xpbooster.util.CalenderUtil;
import com.keurig.xpbooster.util.Chat;
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

    public static void addBoost(Player player, Booster booster) {
        UUID playerId = player.getUniqueId();
        if (hasBoost(playerId)) {
            EXPBoost existingBoost = getBoost(playerId);
            if (existingBoost.getMultiplier() == booster.getMultiplier()) {
                Chat.log("Adding time");
                long newExpirationTime = CalenderUtil.getTime(existingBoost.getDate(), booster.getTime());
                existingBoost.setDate(newExpirationTime);
                setBoost(playerId, existingBoost);
                return;
            }
        }

        Chat.log("Setting new booster");
        setBoost(playerId, new EXPBoost(playerId, booster.getMultiplier(), CalenderUtil.getTime(booster.getTime()), booster.getName()));
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
