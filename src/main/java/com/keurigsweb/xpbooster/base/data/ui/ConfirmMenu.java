package com.keurigsweb.xpbooster.base.data.ui;

import com.cryptomorin.xseries.XMaterial;
import com.keurigsweb.xpbooster.XPBoostPlugin;
import com.keurigsweb.xpbooster.api.XPBoostAPI;
import com.keurigsweb.xpbooster.base.data.EXPBoost;
import com.keurigsweb.xpbooster.base.data.booster.Booster;
import com.keurigsweb.xpbooster.base.data.booster.BoosterSound;
import com.keurigsweb.xpbooster.base.data.booster.voucher.Voucher;
import com.keurigsweb.xpbooster.base.menu.data.Menu;
import com.keurigsweb.xpbooster.base.menu.data.MenuManager;
import com.keurigsweb.xpbooster.base.menu.item.ActionItem;
import com.keurigsweb.xpbooster.base.menu.item.ItemClickEvent;
import com.keurigsweb.xpbooster.language.Language;
import com.keurigsweb.xpbooster.util.Chat;
import com.keurigsweb.xpbooster.util.InventoryUtil;
import com.keurigsweb.xpbooster.util.ItemBuilder;
import com.keurigsweb.xpbooster.util.NumUtil;
import com.keurigsweb.xpbooster.util.replacement.Replacement;
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
        ItemBuilder acceptItem = ItemBuilder.item(XMaterial.LIME_STAINED_GLASS_PANE.parseItem()).setName("&a&lACCEPT");
        ItemBuilder denyItem = ItemBuilder.item(XMaterial.RED_STAINED_GLASS_PANE.parseItem()).setName("&c&lDENY");
        ItemBuilder infoItem = ItemBuilder.item(XMaterial.BOOK.parseItem()).setName("&f&lINFO");

        // Prepare lore for info item based on the multiplier comparison
        String boosterType = currentPlayerMultiplier > boosterMultiplier ? "&7downgrading multiplier" : "&7upgrading multiplier";
        boosterType = currentPlayerMultiplier == boosterMultiplier ? "" : boosterType;
        List<String> infoLore = new ArrayList<>(Arrays.asList(
                "",
                "",
                "",
                "&7Booster: &e" + XPBoostPlugin.capitalizeFirstLetter(booster.getId()),
                "&7Multiplier: &e" + NumUtil.formatMultiplier(boosterMultiplier),
                "&7Time: &e" + booster.getTime()
        ));

        if (instantClaim) {
            infoLore.set(0, "&cYou are purchasing this for $" + booster.getPrice());
            infoLore.set(1, "&cActivation immediately");
            if (!booster.isGlobal()) {
                // Add warning if current player's multiplier is less than booster multiplier
                if (currentPlayerMultiplier != boosterMultiplier && currentPlayerMultiplier != 0) {
                    infoLore.add("");
                    infoLore.add("&cConflicting multiplier: &6" + NumUtil.formatMultiplier(currentPlayerMultiplier));
                    infoLore.add("&cOverride current time for new multiplier of &6" + NumUtil.formatMultiplier(boosterMultiplier) + "&c?");
                }

                if (currentPlayerMultiplier > boosterMultiplier) {
                    infoLore.add(7, "&4Warning: Your current multiplier is less than the new multiplier!");
                }

                // Set lore for accept and info items
                if (!boosterType.isBlank() && !boosterType.isEmpty())
                    acceptItem.setLore(Chat.color("", "&7" + boosterType));
            }
        } else {
            infoLore.set(0, "&cYou are purchasing this voucher for $" + booster.getPrice());
            infoLore.add("&fYou receive a physical voucher");
        }

        infoItem.setLore(infoLore.stream().map(Chat::color).collect(Collectors.toList()));

        // Add action items to the menu
        addAction(new ActionItem(acceptItem.toItemStack()).addAction(e -> {
            Player player = e.getPlayer();

            boolean withdraw = XPBoostPlugin.getInstance().getEconomyAdapter().withdraw(player, booster.getPrice());

            if (!withdraw) {
                player.sendMessage(Language.NOT_ENOUGH_FUNDS.toString());
                player.closeInventory();
                return;
            }

            if (currentPlayerMultiplier != boosterMultiplier) {
                BoosterSound sound = XPBoostPlugin.getInstance().getBoosterManager().getSound();
                if (sound.sound != null) {
                    player.playSound(player.getLocation(), sound.sound, sound.volume, sound.pitch);
                }

            }

            if (!instantClaim) {
                player.getInventory().addItem(voucher.getItem());
                player.sendMessage(booster.getReplace(Language.VOUCHER_RECEIVE_MESSAGE.toString()));
            } else {

                if (booster.isGlobal()) {
                    Replacement replacement = booster.getReplacement(Language.GLOBAL_MESSAGE.toString());
                    replacement.addReplacement(Replacement.DURATION_REGEX, booster.getTime());
                    replacement.addReplacement(Replacement.TARGET_REGEX, player.getName());

                    player.sendMessage(replacement.getReplacement());
                    XPBoostAPI.addGlobalBoost(player, booster.getMultiplier(), booster.getTime());
                } else {
                    Replacement replacement = booster.getReplacement(Language.BOOSTER_ACTIVATE_MESSAGE.toString());


                    // Apply booster to the player
                    EXPBoost expBoost = XPBoostAPI.addBoost(player, booster);
                    replacement.addReplacement(Replacement.TIME_REGEX, expBoost.getRemainingTimeFormat());
                    player.sendMessage(replacement.getReplacement());
                }
            }

            player.closeInventory();
        }), 11);

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
