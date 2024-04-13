package com.keurigsweb.xpbooster.event;

import com.keurigsweb.xpbooster.XPBoostPlugin;
import com.keurigsweb.xpbooster.api.XPBoostAPI;
import com.keurigsweb.xpbooster.base.data.EXPBoost;
import com.keurigsweb.xpbooster.base.data.booster.Booster;
import com.keurigsweb.xpbooster.base.data.ui.VoucherMenu;
import com.keurigsweb.xpbooster.base.menu.data.MenuManager;
import com.keurigsweb.xpbooster.base.menu.data.PlayerMenu;
import com.keurigsweb.xpbooster.language.Language;
import com.keurigsweb.xpbooster.util.NumUtil;
import com.keurigsweb.xpbooster.util.TimeUtil;
import com.keurigsweb.xpbooster.util.replacement.Replacement;
import de.tr7zw.changeme.nbtapi.NBT;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
public class VoucherClickEvent {

    private XPBoostPlugin plugin;
    private Player player;
    private ItemStack item;

    public boolean onClick(PlayerInteractEvent e) {
        StringBuilder boosterId = new StringBuilder();
        NBT.get(e.getItem(), c -> {
            boosterId.append(c.getString("boosterId"));
        });

        Booster booster = plugin.getBoosterManager().getBooster(boosterId.toString());

        if (booster == null)
            return false;

        if (XPBoostAPI.hasBoost(player.getUniqueId()) && !booster.isGlobal()) {
            if (XPBoostAPI.getBoost(player.getUniqueId()).getMultiplier() != booster.getMultiplier()) {

                MenuManager.openMenu(VoucherMenu.class, new PlayerMenu(player).setData("booster", booster).setData("voucherClickEvent", this));
                return true;
            }
        }

        // Apply booster to the player
        claim(booster);
        return true;
    }

    // Claim the item and send message
    public void claim(Booster booster) {
        if (booster.isGlobal()) {
            Replacement replacement = booster.getReplacement(Language.GLOBAL_MESSAGE.toString());
            replacement.addReplacement(Replacement.DURATION_REGEX, booster.getTime());
            replacement.addReplacement(Replacement.TARGET_REGEX, player.getName());
            
            player.sendMessage(replacement.getReplacement());
            XPBoostAPI.addGlobalBoost(player, booster.getMultiplier(), booster.getTime());
        } else {
            EXPBoost expBoost = XPBoostAPI.addBoost(player, booster);
            Replacement replacement = booster.getReplacement(Language.BOOSTER_ACTIVATE_MESSAGE.toString());
            replacement.addReplacement(Replacement.TIME_REGEX, expBoost.getRemainingTime());
            player.sendMessage(replacement.getReplacement());
            XPBoostAPI.addBoost(player, booster);
        }
        item.setAmount(item.getAmount() - 1);
        player.getInventory().setItemInHand(item);
        player.closeInventory();
    }
}
