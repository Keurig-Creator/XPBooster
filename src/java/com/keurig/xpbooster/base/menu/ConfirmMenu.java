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
import com.keurig.xpbooster.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

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
        ShopProfile shop = getPlayerMenu().getData("shop", ShopProfile.class);
        ShopBooster shopBooster = getPlayerMenu().getData("shopBooster", ShopBooster.class);
        ActionItem deny = new ActionItem(ItemBuilder.item(Material.RED_STAINED_GLASS).setName("&cDeny").toItemStack()).addAction(e -> {
            MenuManager.openMenu(ShopMenu.class, e.getPlayer());
        });

        ActionItem item = new ActionItem(ItemBuilder.item(shopBooster.getItem()).toItemStack());
        ActionItem accept = new ActionItem(ItemBuilder.item(Material.GREEN_STAINED_GLASS).setName("&aAccept").toItemStack()).addAction(e -> {
            XPBoostAPI.addBoost(playerMenu.getPlayer().getUniqueId(), shopBooster.getBooster());
            Chat.message(playerMenu.getPlayer(), shopBooster.getReplace(Language.VOUCHER_RECEIVE_XPBOOST_MESSAGE.toString()));
            playerMenu.getPlayer().closeInventory();
        });

        addAction(accept, 2);
        addAction(item, 4);
        addAction(deny, 6);
    }

    @Override
    public boolean cancelClicks() {
        return true;
    }
}
