package com.keurigsweb.xpbooster.api;

import com.keurigsweb.xpbooster.XPBoostPlugin;
import com.keurigsweb.xpbooster.base.data.EXPBoost;
import com.keurigsweb.xpbooster.base.data.booster.Booster;
import com.keurigsweb.xpbooster.base.data.booster.global.GlobalBoost;
import com.keurigsweb.xpbooster.base.data.booster.global.HolidayBoost;
import com.keurigsweb.xpbooster.base.handler.InternalXPBoostHandler;
import com.keurigsweb.xpbooster.util.ConfigValue;
import com.keurigsweb.xpbooster.util.TimeUtil;
import com.keurigsweb.xpbooster.util.Chat;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
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

    public static double getTotalBoost(Player player) {
        double multiplier = 0;
        double globalMultiplier = 0;
        double holidayMultiplier = HolidayBoost.GLOBAL_MULTIPLIER;
        double permissionMultiplier = XPBoostPlugin.permisionMultiplier.getOrDefault(player.getUniqueId(), 0.0);

        if (hasBoost(player.getUniqueId())) {
            multiplier = getBoost(player.getUniqueId()).getMultiplier();
        }

        if (!getBoostHandler().getGlobalBoosts().isEmpty()) {
            for (GlobalBoost globalBoost : getBoostHandler().getGlobalBoosts()) {
                if (ConfigValue.GLOBAL_STACKING) {
                    globalMultiplier += globalBoost.getMultiplier();
                } else if (globalBoost.getMultiplier() > globalMultiplier) {
                    globalMultiplier = globalBoost.getMultiplier();
                }
            }
        }

        if (ConfigValue.GLOBAL_STACKING) {
            return Math.abs(multiplier + globalMultiplier + holidayMultiplier + permissionMultiplier);
        } else {
            return Math.max(Math.max(Math.max(permissionMultiplier, multiplier), globalMultiplier), holidayMultiplier);
        }
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

    public static void addGlobalBoost(CommandSender commandSender, double multiplier, String time) {


        if (commandSender instanceof Player player) {
            getBoostHandler().addGlobalBoost(new GlobalBoost(player.getUniqueId().toString(), multiplier, TimeUtil.getTime(time)));
        } else if (commandSender instanceof ConsoleCommandSender) {
            getBoostHandler().addGlobalBoost(new GlobalBoost("Console", multiplier, TimeUtil.getTime(time)));
        }
    }

    public static void removeGlobalBoost(Double multiplier) {
        getBoostHandler().removeGlobalBoost(multiplier);
    }

    public static GlobalBoost getGlobalBoost(Double multiplier) {
        return getBoostHandler().getGlobalBoost(multiplier);
    }

    public static double getGlobalMultiplier() {
        Set<Double> uniqueMultipliers = new HashSet<>();

        for (GlobalBoost globalBoost : getBoostHandler().getGlobalBoosts()) {
            if (!globalBoost.isExpired()) {
                uniqueMultipliers.add(globalBoost.getMultiplier());
            }
        }

        double totalMultiplier = 0;
        for (double multiplier : uniqueMultipliers) {
            if (ConfigValue.GLOBAL_STACKING) {
                totalMultiplier += multiplier;
            } else if (totalMultiplier < multiplier) {
                totalMultiplier = multiplier;
            }
        }

        return totalMultiplier;
    }

    public static void setBoost(Player player, Booster booster) {
        setBoost(player.getUniqueId(), new EXPBoost(player.getUniqueId(), booster.getMultiplier(), TimeUtil.getTime(booster.getTime()), booster.getName()));
    }

    public static EXPBoost addBoost(UUID uuid, double multiplier, String time) {
        if (hasBoost(uuid)) {
            Chat.log("Boost already exists!");
            EXPBoost existingBoost = getBoost(uuid);
            long newExpirationTime = TimeUtil.getTime(existingBoost.getTime(), time);
            existingBoost.setTime(newExpirationTime);
            setBoost(uuid, existingBoost);
            return existingBoost;
        }

        EXPBoost expBoost = new EXPBoost(uuid, multiplier, TimeUtil.getTime(time), null);
        setBoost(uuid, expBoost);
        return expBoost;
    }

    public static EXPBoost addBoost(Player player, Booster booster) {
        UUID playerId = player.getUniqueId();
        if (hasBoost(playerId)) {
            EXPBoost existingBoost = getBoost(playerId);
            if (existingBoost.getMultiplier() == booster.getMultiplier()) {
                long newExpirationTime = TimeUtil.getTime(existingBoost.getTime(), booster.getTime());
                existingBoost.setTime(newExpirationTime);
                setBoost(playerId, existingBoost);
                return existingBoost;
            }
        }

        EXPBoost expBoost = new EXPBoost(playerId, booster.getMultiplier(), TimeUtil.getTime(booster.getTime()), booster.getName());
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
