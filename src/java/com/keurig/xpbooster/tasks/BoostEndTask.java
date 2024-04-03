package com.keurig.xpbooster.tasks;

import com.keurig.xpbooster.XPBoostPlugin;
import com.keurig.xpbooster.api.XPBoostAPI;
import com.keurig.xpbooster.base.EXPBoost;
import com.keurig.xpbooster.language.Language;
import com.keurig.xpbooster.util.Chat;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public class BoostEndTask extends BukkitRunnable {

    private XPBoostPlugin plugin;

    @Override
    public void run() {
        Iterator<Map.Entry<UUID, EXPBoost>> iterator = plugin.getDataConfig().getExpBoosts().entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, EXPBoost> next = iterator.next();

            EXPBoost expBoost = next.getValue();

            if (expBoost.isExpired()) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    Player player = Bukkit.getPlayer(expBoost.getUuid());

                    if (player == null) {
                        return;
                    }

                    try {
                        player.playSound(player, Sound.valueOf(plugin.config.getString("boost_end_sound")), 1, 1);
                    } catch (IllegalArgumentException ignored) {

                    }

                    Chat.message(player, Language.BOOST_END_MESSAGE.toString());
                }, 0);

                iterator.remove();
                XPBoostAPI.removeBoost(expBoost.getUuid());
            }
        }
    }
}
