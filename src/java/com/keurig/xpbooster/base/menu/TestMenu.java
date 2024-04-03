package com.keurig.xpbooster.base.menu;

import com.keurig.xpbooster.XPBoostPlugin;
import com.keurig.xpbooster.util.Chat;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class TestMenu extends Menu {


    public TestMenu(Player player) {
        super(player);
    }

    @Override
    public void handle(InventoryClickEvent e) {
        Chat.log("handled");
    }

    @Override
    public int getSlots() {
        return 18;
    }

    @Override
    public void setup(Inventory inventory) {
        this.inventory = XPBoostPlugin.getInstance().getShopManager().getShop().getMenu();
    }

    @Override
    public boolean cancelClicks() {
        return true;
    }
}
