package com.keurig.xpbooster.base.menu;

import com.keurig.xpbooster.api.XPBoostAPI;
import com.keurig.xpbooster.base.ShopBooster;
import com.keurig.xpbooster.base.menu.data.Menu;
import com.keurig.xpbooster.base.menu.data.MenuManager;
import com.keurig.xpbooster.base.menu.item.ActionItem;
import com.keurig.xpbooster.base.menu.item.ItemClickEvent;
import com.keurig.xpbooster.base.shop.ShopProfile;
import com.keurig.xpbooster.language.Language;
import com.keurig.xpbooster.util.Chat;
import com.keurig.xpbooster.util.InventoryUtil;
import com.keurig.xpbooster.util.ItemBuilder;
import com.keurig.xpbooster.util.NumUtil;
import com.keurig.xpbooster.util.replacement.Replacement;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConfirmMenu extends Menu {
    @Override
    public int getSize() {
        return 9;
    }

    @Override
    public String getTitle() {
        return "&8&lConfirm";
    }

    @Override
    public void onClick(ItemClickEvent e) {

    }

    @Override
    public void setup(Inventory inventory) {
        boolean instantClaim = playerMenu.getData("instantClaim", Boolean.class);

        ShopProfile shop = getPlayerMenu().getData("shop", ShopProfile.class);
        ShopBooster shopBooster = getPlayerMenu().getData("shopBooster", ShopBooster.class);
        ActionItem deny = new ActionItem(ItemBuilder.item(Material.RED_STAINED_GLASS_PANE).setName("&cDeny").toItemStack()).addAction(e -> {
            MenuManager.openMenu(ShopMenu.class, e.getPlayer());
        });

        List<String> lore = new ArrayList<>(Objects.requireNonNull(Objects.requireNonNull(shopBooster.getItem().getItemMeta()).getLore()));

        ItemBuilder itemBuilder = ItemBuilder.item(Material.BOOK);
        itemBuilder.setGlow(true);
        itemBuilder.setName("&fINFO");

        if (instantClaim) {
            lore.add("");
            lore.add("&fYou are purchasing this booster!");
            if (XPBoostAPI.getBoost(getPlayerMenu().getPlayer().getUniqueId()) != null && XPBoostAPI.getBoost(getPlayerMenu().getPlayer().getUniqueId()).getMultiplier() > shopBooster.getBooster().getMultiplier()) {
                lore.add("&4Warning by buying this voucher");
                lore.add("&4will reset your current booster time");
            }

            lore = lore.stream().map(Chat::color).collect(Collectors.toList());
            itemBuilder.setLore(lore);
        } else {
//            itemBuilder = ItemBuilder.item(shopBooster.getItem());
        }

        Replacement replacement = new Replacement();
        Chat.log(XPBoostAPI.getBoost(getPlayerMenu().getPlayer().getUniqueId()).getRemainingTime());

        String remainTime = XPBoostAPI.getBoost(getPlayerMenu().getPlayer().getUniqueId()).getRemainingTime();
        if (remainTime == null) {
            replacement.addReplacement(Replacement.TIME_REGEX, XPBoostAPI.getBoost(playerMenu.getPlayer().getUniqueId()).getRemainingTime());
        } else {
//            replacement.addReplacement(Replacement.TIME_REGEX, addTime(shopBooster.getBooster().getTime(), remainTime));
        }
        replacement.addReplacement(Replacement.NAME_REGEX, shopBooster.getTitle());
        replacement.addReplacement(Replacement.MULTIPLIER_REGEX, NumUtil.formatMultiplier(shopBooster.getBooster().getMultiplier()));

        ActionItem item = new ActionItem(itemBuilder.toItemStack());
        ActionItem accept = new ActionItem(ItemBuilder.item(Material.LIME_STAINED_GLASS_PANE).setName("&aAccept").toItemStack()).addAction(e -> {
            if (instantClaim) {
                Chat.log("here");
                XPBoostAPI.addBoost(playerMenu.getPlayer(), shopBooster.getBooster());
                Chat.message(playerMenu.getPlayer(), Language.BOOSTER_ACTIVATE_MESSAGE.toString(replacement));
            } else {

                Chat.message(playerMenu.getPlayer(), Language.VOUCHER_RECEIVE_MESSAGE.toString(replacement));
            }

            playerMenu.getPlayer().closeInventory();
        });

        addAction(accept, 2);
        addAction(item, 4);
        addAction(deny, 6);

        InventoryUtil.fillInventory(inventory, shop.fillItem());
    }

    @Override
    public boolean cancelClicks() {
        return true;
    }
}
