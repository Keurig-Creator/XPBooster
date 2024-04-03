package com.keurig.xpbooster.api;

import com.keurig.xpbooster.XPBoostPlugin;
import com.keurig.xpbooster.base.EXPBoost;
import com.keurig.xpbooster.base.InternalXPBoostHandler;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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
    public static void addBoost(UUID uuid, EXPBoost expBoost) {
        getBoostHandler().addBoost(uuid, expBoost);
    }

    /**
     * Remove boost from user
     */
    public static void removeBoost(UUID uuid) {
        getBoostHandler().removeBoost(uuid);
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
