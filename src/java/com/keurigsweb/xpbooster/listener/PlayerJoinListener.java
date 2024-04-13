package com.keurigsweb.xpbooster.listener;

import com.keurigsweb.xpbooster.XPBoostPlugin;
import com.keurigsweb.xpbooster.base.data.booster.global.HolidayBoost;
import com.keurigsweb.xpbooster.util.Chat;
import com.keurigsweb.xpbooster.util.Tasks;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@AllArgsConstructor
public class PlayerJoinListener implements Listener {

    private XPBoostPlugin plugin;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String notify = HolidayBoost.GLOBAL_NOTIFY;
        if (notify != null) {
            Tasks.runTaskLater(plugin, () -> Chat.message(player, notify), 5);
        }
    }

}
