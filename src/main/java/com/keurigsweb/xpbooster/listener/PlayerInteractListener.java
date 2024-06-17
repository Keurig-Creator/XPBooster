package com.keurigsweb.xpbooster.listener;

import com.keurigsweb.xpbooster.XPBoostPlugin;
import com.keurigsweb.xpbooster.api.XPBoostAPI;
import com.keurigsweb.xpbooster.base.data.EXPBoost;
import com.keurigsweb.xpbooster.base.data.booster.Booster;
import com.keurigsweb.xpbooster.base.data.ui.VoucherMenu;
import com.keurigsweb.xpbooster.base.menu.data.MenuManager;
import com.keurigsweb.xpbooster.base.menu.data.PlayerMenu;
import com.keurigsweb.xpbooster.event.VoucherClickEvent;
import com.keurigsweb.xpbooster.language.Language;
import com.keurigsweb.xpbooster.util.replacement.Replacement;
import de.tr7zw.changeme.nbtapi.NBT;
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
public class PlayerInteractListener implements Listener {

    private XPBoostPlugin plugin;

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        ItemStack item = player.getInventory().getItemInHand();

        // Blocks attempts other than right click
        if (item.getType() == Material.AIR || e.getItem() == null)
            return;

        if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK)
            return;

        VoucherClickEvent voucherClickEvent = new VoucherClickEvent(plugin, player, item);
        if (voucherClickEvent.onClick(e)) {
            e.setUseItemInHand(Event.Result.DENY);
            e.setUseInteractedBlock(Event.Result.DENY);
            e.setCancelled(true);
        }

//        StringBuilder boosterId = new StringBuilder();
//        NBT.get(e.getItem(), c -> {
//            boosterId.append(c.getString("boosterId"));
//        });
//
//        Booster booster = plugin.getBoosterManager().getBooster(boosterId.toString());
//        if (booster == null)
//            return;
//
//        if (XPBoostAPI.hasBoost(player.getUniqueId())) {
//            if (XPBoostAPI.getBoost(player.getUniqueId()).getMultiplier() != booster.getMultiplier()) {
//
//                MenuManager.openMenu(VoucherMenu.class, new PlayerMenu(player).setData("booster", booster));
//                e.setUseItemInHand(Event.Result.DENY);
//                e.setUseInteractedBlock(Event.Result.DENY);
//                e.setCancelled(true);
//                return;
//            }
//        }
//
//        // Apply booster to the player
//        EXPBoost expBoost = XPBoostAPI.addBoost(player, booster);
//        Replacement replacement = booster.getReplacement(Language.BOOSTER_ACTIVATE_MESSAGE.toString());
//        replacement.addReplacement(Replacement.TIME_REGEX, expBoost.getRemainingTime());
//        player.sendMessage(replacement.getReplacement());
//        XPBoostAPI.addBoost(player, booster);
//        item.setAmount(item.getAmount() - 1);
//        player.getInventory().setItemInHand(item);

    }
}
