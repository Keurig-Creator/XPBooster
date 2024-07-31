package com.keurigsweb.xpbooster.listener;

import com.keurigsweb.xpbooster.PluginUpdater;
import com.keurigsweb.xpbooster.XPBoostPlugin;
import com.keurigsweb.xpbooster.base.data.booster.global.GlobalBoost;
import com.keurigsweb.xpbooster.base.data.booster.global.HolidayBoost;
import com.keurigsweb.xpbooster.event.VoucherClickEvent;
import com.keurigsweb.xpbooster.tasks.PermissionMultiplierTask;
import com.keurigsweb.xpbooster.util.Chat;
import com.keurigsweb.xpbooster.util.ConfigValue;
import com.keurigsweb.xpbooster.util.Tasks;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class PlayerJoinListener implements Listener {

    private XPBoostPlugin plugin;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String notify = HolidayBoost.GLOBAL_NOTIFY;
       // Use Permission based multiplier
        if (plugin.config.getBoolean("permission-based-multiplier")) {
            PermissionMultiplierTask.checkPermissionMultiplier(player);
        }
        if (notify != null) {
            Tasks.runTaskLater(plugin, () -> {
                Chat.message(player, notify);
                if (ConfigValue.NOTIFY_UPDATES && PluginUpdater.isUpdate() && (player.hasPermission("xpbooster.update") || player.isOp())) {
                    Chat.message(player, "&aXPBooster &7is out of date latest version is &c" + PluginUpdater.LATEST_VERSION + " &7You are currently on &a" + PluginUpdater.CURRENT_VERSION);
                }
            }, 5);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        XPBoostPlugin.permisionMultiplier.remove(player.getUniqueId());
    }
}
