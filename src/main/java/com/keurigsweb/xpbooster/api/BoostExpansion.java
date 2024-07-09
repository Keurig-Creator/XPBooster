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
            return player == null ? null : NumUtil.formatMultiplier(XPBoostAPI.getMultiplier(player.getPlayer()));
        } else if(params.equalsIgnoreCase("global")) {
            return player == null ? null : NumUtil.formatMultiplier(XPBoostAPI.getTotalBoost(player.getPlayer()));
        } else if (params.equalsIgnoreCase("time")) {
            return player == null ? null : XPBoostAPI.getRemainingTime(player.getPlayer());
        }

        return null;
    }
}
