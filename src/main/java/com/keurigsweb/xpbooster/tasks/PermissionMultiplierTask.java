package com.keurigsweb.xpbooster.tasks;

import com.keurigsweb.xpbooster.XPBoostPlugin;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

@AllArgsConstructor
public class PermissionMultiplierTask extends BukkitRunnable {

    private XPBoostPlugin plugin;

    @Override
    public void run() {
        if (plugin.config.getBoolean("permission-based-multiplier")) {
            Bukkit.getOnlinePlayers().forEach(PermissionMultiplierTask::checkPermissionMultiplier);
        }
    }

    public static void checkPermissionMultiplier(Player player) {

        List<String> permissions = player.getEffectivePermissions().stream()
                .filter(p -> p.getPermission().contains("xpbooster.multiplier"))
                .map(p -> p.getPermission().split("\\.")[2]) // Extracting the multiplier value
                .toList();

        double highestMultiplier = permissions.stream()
                .mapToDouble(Double::parseDouble)
                .max()
                .orElse(0); // Default value if no multiplier found

        if (player.hasPermission("xpbooster.multiplier." + highestMultiplier)) {
            XPBoostPlugin.permisionMultiplier.put(player.getUniqueId(), highestMultiplier);
        }
    }
}
