package com.keurigsweb.xpbooster.listener;

import com.keurigsweb.xpbooster.XPBoostPlugin;
import com.keurigsweb.xpbooster.base.data.booster.global.GlobalBoost;
import com.keurigsweb.xpbooster.base.data.booster.global.HolidayBoost;
import com.keurigsweb.xpbooster.event.VoucherClickEvent;
import com.keurigsweb.xpbooster.util.Chat;
import com.keurigsweb.xpbooster.util.Tasks;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class PlayerJoinListener implements Listener {

    private XPBoostPlugin plugin;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String notify = HolidayBoost.GLOBAL_NOTIFY;

        if (plugin.config.getBoolean("permission-based-multiplier")) {
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
        if (notify != null) {
            Tasks.runTaskLater(plugin, () -> Chat.message(player, notify), 5);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        XPBoostPlugin.permisionMultiplier.remove(player.getUniqueId());
    }

}
