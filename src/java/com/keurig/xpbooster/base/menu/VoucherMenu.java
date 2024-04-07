package com.keurig.xpbooster.base.menu;

import com.keurig.xpbooster.XPBoostPlugin;
import com.keurig.xpbooster.api.XPBoostAPI;
import com.keurig.xpbooster.base.Booster;
import com.keurig.xpbooster.base.BoosterSound;
import com.keurig.xpbooster.base.Voucher;
import com.keurig.xpbooster.base.menu.data.Menu;
import com.keurig.xpbooster.base.menu.item.ActionItem;
import com.keurig.xpbooster.base.menu.item.ItemClickEvent;
import com.keurig.xpbooster.language.Language;
import com.keurig.xpbooster.util.Chat;
import com.keurig.xpbooster.util.InventoryUtil;
import com.keurig.xpbooster.util.ItemBuilder;
import com.keurig.xpbooster.util.NumUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class VoucherMenu extends Menu {
    @Override
    public int getSize() {
        return 27;
    }

    @Override
    public String getTitle() {
        return "&fVoucher Confirmation";
    }

    @Override
    public void onClick(ItemClickEvent e) {

    }

    @Override
    public void setup(Inventory inventory) {
        InventoryUtil.fillInventory(inventory, XPBoostPlugin.getInstance().getShopManager().getShop().fillItem());

        Booster booster = playerMenu.getData("booster", Booster.class);

        if (booster == null) {
            throw new RuntimeException("Booster was null");
        }

        Voucher voucher = booster.getVoucher();


        double currentPlayerMultiplier = XPBoostAPI.getBoost(getPlayer().getUniqueId()).getMultiplier();
        double boosterMultiplier = booster.getMultiplier();

        ItemBuilder acceptItem = ItemBuilder.item(Material.LIME_STAINED_GLASS_PANE);
        acceptItem.setName("&a&lACCEPT");

        ItemBuilder denyItem = ItemBuilder.item(Material.RED_STAINED_GLASS_PANE);
        denyItem.setName("&c&lDENY");

        ItemBuilder infoItem = ItemBuilder.item(Material.BOOK);
        infoItem.setName("&f&lINFO");

        if (currentPlayerMultiplier > boosterMultiplier) {
            // Warn the user more aggressively
            acceptItem.setLore(Chat.color("", "&7downgrading"));
            infoItem.setLore(
                    "",
                    "&7Multiplier: " + NumUtil.formatMultiplier(boosterMultiplier),
                    "&7Time: " + booster.getTime(),
                    "&7",
                    "&4Warning: Your current multiplier is less than the new multiplier!",
                    "&cConflicting multiplier: &6" + NumUtil.formatMultiplier(currentPlayerMultiplier),
                    "&cOverride current time for new multiplier of &6" + NumUtil.formatMultiplier(boosterMultiplier) + "&c?"
            );
        } else {
            acceptItem.setLore(Chat.color("", "&7upgrading"));
            // Provide regular information
            infoItem.setLore(
                    "",
                    "&7Multiplier: " + NumUtil.formatMultiplier(boosterMultiplier),
                    "&7Time: " + booster.getTime(),
                    "&7",
                    "&cConflicting multiplier: &6" + NumUtil.formatMultiplier(currentPlayerMultiplier),
                    "&cOverride current time for new multiplier of &6" + NumUtil.formatMultiplier(boosterMultiplier) + "&c?"
            );
        }

        addAction(new ActionItem(acceptItem.toItemStack()).addAction(e -> {
            Player player = e.getPlayer();

            ItemStack item = player.getInventory().getItemInMainHand();
            item.setAmount(item.getAmount() - 1);

            BoosterSound sound = XPBoostPlugin.getInstance().getBoosterManager().getSound();
            if (sound.sound != null) {
                player.playSound(player, sound.sound, sound.volume, sound.pitch);
            }

            XPBoostAPI.setBoost(player, booster);
            player.sendMessage(booster.getVoucher().getReplace(Language.BOOSTER_ACTIVATE_MESSAGE.toString()));
            player.closeInventory();
        }), 10);

        addAction(new ActionItem(infoItem.toItemStack()), 13);

        addAction(new ActionItem(denyItem.toItemStack()).addAction(e -> {
            e.getPlayer().closeInventory();
        }), 15);
    }

    @Override
    public boolean cancelClicks() {
        return true;
    }

    public Player getPlayer() {
        return playerMenu.getPlayer();
    }
}
