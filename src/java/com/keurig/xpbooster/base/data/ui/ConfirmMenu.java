package com.keurig.xpbooster.base.data.ui;

import com.keurig.xpbooster.XPBoostPlugin;
import com.keurig.xpbooster.api.XPBoostAPI;
import com.keurig.xpbooster.base.data.EXPBoost;
import com.keurig.xpbooster.base.data.booster.Booster;
import com.keurig.xpbooster.base.data.booster.BoosterSound;
import com.keurig.xpbooster.base.data.booster.voucher.Voucher;
import com.keurig.xpbooster.base.menu.data.Menu;
import com.keurig.xpbooster.base.menu.data.MenuManager;
import com.keurig.xpbooster.base.menu.item.ActionItem;
import com.keurig.xpbooster.base.menu.item.ItemClickEvent;
import com.keurig.xpbooster.language.Language;
import com.keurig.xpbooster.util.Chat;
import com.keurig.xpbooster.util.InventoryUtil;
import com.keurig.xpbooster.util.ItemBuilder;
import com.keurig.xpbooster.util.NumUtil;
import com.keurig.xpbooster.util.replacement.Replacement;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConfirmMenu extends Menu {
    @Override
    public int getSize() {
        return 27;
    }

    @Override
    public String getTitle() {
        return "&fShop Confirmation";
    }

    @Override
    public void onClick(ItemClickEvent e) {

    }

    @Override
    public void setup(Inventory inventory) {
        // Fill the inventory with shop items
        InventoryUtil.fillInventory(inventory, XPBoostPlugin.getInstance().getShopManager().getShop().fillItem());

        // Retrieve data from the player menu
        Booster booster = playerMenu.getData("booster", Booster.class);

        boolean instantClaim = playerMenu.getData("instantClaim", Boolean.class);
        if (booster == null) {
            throw new RuntimeException("Booster was null");
        }
        Voucher voucher = booster.getVoucher();

        // Retrieve current player's multiplier and booster multiplier
        double currentPlayerMultiplier = XPBoostAPI.hasBoost(playerMenu.getPlayer().getUniqueId()) ? XPBoostAPI.getBoost(playerMenu.getPlayer().getUniqueId()).getMultiplier() : 0;
        double boosterMultiplier = booster.getMultiplier();

        // Create item builders for accept, deny, and info items
        ItemBuilder acceptItem = ItemBuilder.item(Material.LIME_STAINED_GLASS_PANE).setName("&a&lACCEPT");
        ItemBuilder denyItem = ItemBuilder.item(Material.RED_STAINED_GLASS_PANE).setName("&c&lDENY");
        ItemBuilder infoItem = ItemBuilder.item(Material.BOOK).setName("&f&lINFO");

        // Prepare lore for info item based on the multiplier comparison
        String boosterType = currentPlayerMultiplier > boosterMultiplier ? "&7downgrading multiplier" : "&7upgrading multiplier";
        boosterType = currentPlayerMultiplier == boosterMultiplier ? "" : boosterType;
        List<String> infoLore = new ArrayList<>(Arrays.asList(
                "",
                "&7Multiplier: " + NumUtil.formatMultiplier(boosterMultiplier),
                "&7Time: " + booster.getTime(),
                "&7"
        ));

        if (instantClaim) {
            infoLore.add("&fActivation immediately");
            // Add warning if current player's multiplier is less than booster multiplier
            if (currentPlayerMultiplier > boosterMultiplier) {
                infoLore.add("&cConflicting multiplier: &6" + NumUtil.formatMultiplier(currentPlayerMultiplier));
                infoLore.add("&cOverride current time for new multiplier of &6" + NumUtil.formatMultiplier(boosterMultiplier) + "&c?");
                infoLore.add(5, "&4Warning: Your current multiplier is less than the new multiplier!");
            }

            // Set lore for accept and info items
            if (!boosterType.isBlank() && !boosterType.isEmpty())
                acceptItem.setLore(Chat.color("", "&7" + boosterType));
        } else {
            infoLore.add("&fYou will receive a voucher");
        }

        infoItem.setLore(infoLore.stream().map(Chat::color).collect(Collectors.toList()));

        // Add action items to the menu
        addAction(new ActionItem(acceptItem.toItemStack()).addAction(e -> {
            Player player = e.getPlayer();

            if (!instantClaim) {
                // Decrease the amount of the item in hand by 1
                player.getInventory().addItem(voucher.getItem());
                player.sendMessage(booster.getReplace(Language.VOUCHER_RECEIVE_MESSAGE.toString()));
            } else {
                Replacement replacement = booster.getReplacement(Language.BOOSTER_ACTIVATE_MESSAGE.toString());
                if (currentPlayerMultiplier != boosterMultiplier) {
                    BoosterSound sound = XPBoostPlugin.getInstance().getBoosterManager().getSound();
                    if (sound.sound != null) {
                        player.playSound(player.getLocation(), sound.sound, sound.volume, sound.pitch);
                    }

                }

                // Apply booster to the player
                EXPBoost expBoost = XPBoostAPI.addBoost(player, booster);
                replacement.addReplacement(Replacement.TIME_REGEX, expBoost.getRemainingTime());
                player.sendMessage(replacement.getReplacement());
            }

            player.closeInventory();
        }), 10);

        addAction(new ActionItem(infoItem.toItemStack()), 13);

        addAction(new ActionItem(denyItem.toItemStack()).addAction(e -> {
            MenuManager.openMenu(ShopMenu.class, playerMenu);
        }), 15);
    }


    @Override
    public boolean cancelClicks() {
        return true;
    }
}
