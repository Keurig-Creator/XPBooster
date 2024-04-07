package com.keurig.xpbooster.event;

import com.keurig.xpbooster.XPBoostPlugin;
import com.keurig.xpbooster.api.XPBoostAPI;
import com.keurig.xpbooster.base.Booster;
import com.keurig.xpbooster.base.BoosterSound;
import com.keurig.xpbooster.base.menu.VoucherMenu;
import com.keurig.xpbooster.base.menu.data.MenuManager;
import com.keurig.xpbooster.base.menu.data.PlayerMenu;
import com.keurig.xpbooster.language.Language;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class PlayerClickEvent implements Listener {

    private XPBoostPlugin plugin;

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        // Blocks attempts other than right click
        if (item.getType() == Material.AIR || e.getItem() == null)
            return;

        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK)
            return;

        String localizedName = e.getItem().getItemMeta().getLocalizedName();
        if (localizedName == null || localizedName.isBlank()) {
            return;
        }

        Booster booster = plugin.getBoosterManager().getBooster(localizedName);
        if (booster == null)
            return;

        if (XPBoostAPI.hasBoost(player.getUniqueId())) {
            if (XPBoostAPI.getBoost(player.getUniqueId()).getMultiplier() != booster.getMultiplier()) {

                MenuManager.openMenu(VoucherMenu.class, new PlayerMenu(player).setData("booster", booster));

                player.sendMessage(booster.getVoucher().getReplace(Language.BOOSTER_ACTIVATE_MESSAGE.toString()));
                e.setUseItemInHand(Event.Result.DENY);
                e.setUseInteractedBlock(Event.Result.DENY);
                e.setCancelled(true);
                return;
            }
        } else {
            BoosterSound sound = XPBoostPlugin.getInstance().getBoosterManager().getSound();
            if (sound.sound != null) {
                player.playSound(player, sound.sound, sound.volume, sound.pitch);
            }

        }

        item.setAmount(item.getAmount() - 1);
        XPBoostAPI.addBoost(player, booster);

        player.sendMessage(booster.getVoucher().getReplace(Language.BOOSTER_ACTIVATE_MESSAGE.toString()));
        e.setUseItemInHand(Event.Result.DENY);
        e.setUseInteractedBlock(Event.Result.DENY);
        e.setCancelled(true);
    }
}
