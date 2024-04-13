package com.keurigsweb.xpbooster.base.data.ui;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.keurigsweb.xpbooster.XPBoostPlugin;
import com.keurigsweb.xpbooster.api.XPBoostAPI;
import com.keurigsweb.xpbooster.base.data.booster.Booster;
import com.keurigsweb.xpbooster.base.data.booster.BoosterSound;
import com.keurigsweb.xpbooster.base.data.booster.voucher.Voucher;
import com.keurigsweb.xpbooster.base.menu.data.Menu;
import com.keurigsweb.xpbooster.base.menu.item.ActionItem;
import com.keurigsweb.xpbooster.base.menu.item.ItemClickEvent;
import com.keurigsweb.xpbooster.event.VoucherClickEvent;
import com.keurigsweb.xpbooster.language.Language;
import com.keurigsweb.xpbooster.util.Chat;
import com.keurigsweb.xpbooster.util.InventoryUtil;
import com.keurigsweb.xpbooster.util.ItemBuilder;
import com.keurigsweb.xpbooster.util.NumUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
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

        ItemBuilder acceptItem = ItemBuilder.item(XMaterial.LIME_STAINED_GLASS_PANE.parseItem());
        acceptItem.setName("&a&lACCEPT");

        ItemBuilder denyItem = ItemBuilder.item(XMaterial.RED_STAINED_GLASS_PANE.parseItem());
        denyItem.setName("&c&lDENY");

        ItemBuilder infoItem = ItemBuilder.item(XMaterial.BOOK.parseItem());
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
            VoucherClickEvent voucherClickEvent = playerMenu.getData("voucherClickEvent", VoucherClickEvent.class);
            BoosterSound sound = XPBoostPlugin.getInstance().getBoosterManager().getSound();
            if (sound.sound != null) {
                player.playSound(player.getLocation(), sound.sound, sound.volume, sound.pitch);
            }
            voucherClickEvent.claim(booster);
        }), 11);

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
