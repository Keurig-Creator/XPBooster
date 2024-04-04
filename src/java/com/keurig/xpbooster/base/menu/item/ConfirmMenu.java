package com.keurig.xpbooster.base.menu.item;

import com.keurig.xpbooster.base.menu.ShopMenu;
import com.keurig.xpbooster.base.menu.data.Menu;
import com.keurig.xpbooster.base.menu.data.MenuManager;
import com.keurig.xpbooster.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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

        ActionItem deny = new ActionItem(ItemBuilder.item(Material.RED_STAINED_GLASS).setName("&cDeny").toItemStack()).addAction(e -> {
            MenuManager.openMenu(ShopMenu.class, e.getPlayer());
        });

        ActionItem item = new ActionItem(ItemBuilder.item(getPlayerMenu().getData("shopItem", ItemStack.class)).toItemStack());
        ActionItem accept = new ActionItem(ItemBuilder.item(Material.GREEN_STAINED_GLASS).setName("&aAccept").toItemStack()).addAction(e -> {
            e.getPlayer().getInventory().addItem(item.getItem());
            e.getPlayer().closeInventory();
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
