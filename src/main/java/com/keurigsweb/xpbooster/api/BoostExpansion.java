package com.keurigsweb.xpbooster.api;

import com.keurigsweb.xpbooster.util.NumUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BoostExpansion extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "xpbooster";
    }

    @Override
    public @NotNull String getAuthor() {
        return "keurig";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("boost")) {
            return player == null ? null : XPBoostAPI.getMultiplier(player.getPlayer()) + "";
        } else if(params.equalsIgnoreCase("boostformatted")) {
            return player == null ? null : NumUtil.formatMultiplier(XPBoostAPI.getMultiplier(player.getPlayer()));
        } else if(params.equalsIgnoreCase("total")) {
            return player == null ? null : XPBoostAPI.getTotalBoost(player.getPlayer()) + "";
        } else if(params.equalsIgnoreCase("totalformatted")) {
            return player == null ? null : NumUtil.formatMultiplier(XPBoostAPI.getTotalBoost(player.getPlayer()));
        } else if (params.equalsIgnoreCase("time")) {
            return player == null ? null : XPBoostAPI.getBoost(player.getUniqueId()).getRemainingTime();
        } else if (params.equalsIgnoreCase("timeformatted")) {
            return player == null ? null : XPBoostAPI.getBoost(player.getUniqueId()).getRemainingTimeFormat();
        } else if (params.equalsIgnoreCase("global")) {
            return XPBoostAPI.getGlobalMultiplier() + "";
        } else if (params.equalsIgnoreCase("globalformatted")) {
            return NumUtil.formatMultiplier(XPBoostAPI.getGlobalMultiplier());
        } else if (params.equalsIgnoreCase("globaltime")) {
            return XPBoostAPI.getGlobalMultiplierTime();
        } else if (params.equalsIgnoreCase("globaltimeformatted")) {
            return XPBoostAPI.getGlobalMultiplierTimeFormat();
        }

        return null;
    }
}
